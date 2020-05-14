package com.colliderlabs.stub.image;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.colliderlabs.ColliderException;
import com.colliderlabs.stub.image.*;


/**
 * <p>
 * Adapter for loading and saving images in TARGA format.
 * </p>
 * <p>
 * This is a low-level adapter which doesn't rely on AWT for image manipulation.
 * Use is when AWT is not available or not desirable to use.
 * </p> 
 * @author Igor Khotin
 *
 */
public class TargaImageAdapter implements ImageAdapter {
	
	public static final int TARGA_RAW_ENCODED = 0;
	public static final int TARGA_RLE_ENCODED = 1;
	
	private boolean alpha;

	@Override
	public GLImage loadImage(InputStream inputStream) throws ColliderException {
		Targa targa = new Targa(inputStream);
		GLImage glImage = new GLUnsignedByteImage(targa.hasAlpha(),
				targa.getWidth(), targa.getHeight(), targa.getRawData());
		
		if ((targa.getImageDescriptor() & 0x20) == 0) glImage.flipVerticaly();
		
		return glImage;
	}

	@Override
	public void storeImage(GLImage image, OutputStream outputStream) throws ColliderException {
		this.storeImage(image, outputStream, TARGA_RLE_ENCODED);
	}
	
	@Override
	public void storeImage(GLImage image, OutputStream outputStream, int format)
			throws ColliderException {
		if (format == TARGA_RAW_ENCODED) {
			Targa targa = new Targa(image, false);
			targa.store(outputStream);	
		} else if (format == TARGA_RLE_ENCODED) {
			Targa targa = new Targa(image, true);
			targa.store(outputStream);
		} else throw new ColliderException("Unknown targa format");
	}




	private class Targa {
		
		private DataInputStream in;
				
		private int idLength;
		
		private int colorMapType;
		
		private int imageTypeCode;
		
		private int colorMapOrigin, colorMapLength, colorMapEntrySize;
		
		private int xOrigin, yOrigin; 
		
		private int width = 2, height = 2;
		
		private int bpp, clen;
		
		private int imageDesc;
		
		byte[] raw;
		
		
		private boolean rle = false;
		
		private boolean alpha = true;
		
		public Targa(GLImage image, boolean rle) throws ColliderException {
			this.idLength = 0;
			this.colorMapType = 0;
			if (rle) {
				this.rle = true;
				this.imageTypeCode = 10; // RLE-encoded RGB(A) image
			} else {
				this.rle = false;
				this.imageTypeCode = 2; // Uncompressed RGB(A) true-color image
			}
			this.colorMapOrigin = this.colorMapLength = this.colorMapEntrySize = 0;
			this.xOrigin = 0;
			this.yOrigin = 0;
			this.width = image.getWidth();
			this.height = image.getHeight();
			if (!image.hasAlpha()) {
				this.bpp = 24;
				this.clen = 3;
				this.alpha = false;
			} else {
				this.bpp = 32;
				this.clen = 4;
				this.alpha = true;
			}
			this.raw = image.getRawData();
		}		
		
		public Targa(InputStream is) {
			try {		
				if (is == null) System.err.println("Can't locate the resource");
				this.in = new DataInputStream(is);
				
				// ************************************************************************
				// read header
				
				this.idLength = in.readUnsignedByte();
				this.colorMapType = in.readUnsignedByte();
				this.imageTypeCode = in.readUnsignedByte();
				if (this.imageTypeCode == 9 || this.imageTypeCode == 10) this.rle = true;
				// color map specification
				this.colorMapOrigin = in.readUnsignedByte() | (in.readUnsignedByte() << 8);
				this.colorMapLength = in.readUnsignedByte() | (in.readUnsignedByte() << 8);
				this.colorMapEntrySize = in.readUnsignedByte();
				// image specification
				this.xOrigin = in.readUnsignedByte() | (in.readUnsignedByte() << 8);
				this.yOrigin = in.readUnsignedByte() | (in.readUnsignedByte() << 8);
				this.width = in.readUnsignedByte() | (in.readUnsignedByte() << 8);
				this.height = in.readUnsignedByte() | (in.readUnsignedByte() << 8);
				this.bpp = in.readUnsignedByte();
				this.clen = bpp >> 3;
				this.imageDesc = in.readUnsignedByte();
				
				// skip image identification field
				in.skip(this.idLength);
				
				/*
				System.out.println("idLength: " + idLength);
				System.out.println("colorMapType: " + colorMapType);
				System.out.println("imageTypeCode: " + imageTypeCode);
				System.out.println("colorMap: " + this.colorMapOrigin + "->" + this.colorMapLength
						+ ":" + this.colorMapEntrySize);
				System.out.println("xyOrigin: " + xOrigin + "x" + yOrigin);
				System.out.println("width&height: " + width + "x" + height);
				System.out.println("bpp: " + bpp);
				System.out.println("imageDesc: " + imageDesc);
				*/
				
				// ************************************************************************
				// color map and data
				
				if (this.colorMapType != 0) {
					// skip till origin
					for (int i = 0; i < this.colorMapOrigin; i++) in.readByte();
					
					// read the table
										
					byte[] ctable = new byte[this.colorMapLength * (this.colorMapEntrySize >> 3)];
					for (int i = 0; i < ctable.length; i++) ctable[i] = in.readByte();
					TargaColorTable colorTable = new TargaColorTable(this.colorMapEntrySize, ctable);				
					
					// read color indexes and turn them into raw data
					int ri = 0;
					raw = new byte[this.width * this.height * this.colorMapEntrySize];
					for (int i = 0; i < this.width * this.height; i++) {
						int index = next();
						if (index < 0) index &= 0xFF;					
						raw[ri++] = colorTable.getRed(index);
						raw[ri++] = colorTable.getGreen(index);
						raw[ri++] = colorTable.getBlue(index);					
						raw[ri++] = colorTable.getAlpha(index);
					}
					
				} else {			
					// read raw data
					if (this.bpp == 24) this.alpha = false;
					
					raw = new byte[this.width * this.height * this.bpp >> 3];
					for (int i = 0; i < this.width * this.height; i++) {
						if (this.bpp == 24) {
							byte b = next();
							byte g = next();
							byte r = next();
							
							raw[i * 3] = r;
							raw[i * 3 + 1] = g;
							raw[i * 3 + 2] = b;						
						} else if (this.bpp == 32) {
							byte b = next();
							byte g = next();
							byte r = next();
							byte a = next();
							
							raw[i * 4] = r;
							raw[i * 4 + 1] = g;
							raw[i * 4 + 2] = b;
							raw[i * 4 + 3] = a;						
						}
					}
				}
				
				this.in.close();						
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public void store(OutputStream outputStream) throws ColliderException {
			try {
				DataOutputStream out = new DataOutputStream(outputStream);
				
				// ************************************************************************
				// write header				
				out.writeByte(this.idLength);
				out.writeByte(this.colorMapType);
				out.writeByte(this.imageTypeCode);
				// color map specification
				out.writeByte(this.colorMapOrigin & 0xFF); out.writeByte((this.colorMapOrigin >> 8) & 0xFF);
				out.writeByte(this.colorMapLength & 0xFF); out.writeByte((this.colorMapLength >> 8) & 0xFF);
				out.writeByte(this.colorMapEntrySize);
				// image specification
				out.writeByte(this.xOrigin & 0xFF); out.writeByte((this.xOrigin >> 8) & 0xFF);
				out.writeByte(this.yOrigin & 0xFF); out.writeByte((this.yOrigin >> 8) & 0xFF);
				out.writeByte(this.width & 0xFF); out.writeByte((this.width >> 8) & 0xFF);
				out.writeByte(this.height & 0xFF); out.writeByte((this.height >> 8) & 0xFF);
				out.writeByte(this.bpp);
				out.writeByte(this.imageDesc);
								
				// fill image identification field
				for (int i = 0; i < this.idLength; i++) out.writeByte(0x00);
				
				// write raw data
				if (rle) {
					int pos = 0;
					int rawLength = 0;
					int rawPos = 0;
					while (pos < this.raw.length) {
						int length = this.getNextRunLength(pos);
						
						if (length == 1) {
							if (rawLength == 0) rawPos = pos; // mark the start of raw packet
							pos += length * this.clen;							
							rawLength++;
							// check if the raw packet is ready to be stored
							if (rawLength == 128 || pos >= this.raw.length) {
								this.storeRawPacket(rawPos, rawLength, out);
								rawLength = 0;
							}							
						} else if (length > 1) {
							// check if there is raw data to store first
							if (rawLength > 0) {
								this.storeRawPacket(rawPos, rawLength, out);								
								rawLength = 0;
							}
							// write run-length packet
							this.storeRunLengthPacket(pos, length, out);
							pos += length * this.clen;
						}
					}
				} else {
					for (int i = 0; i < this.width * this.height; i++) {
						if (this.alpha) this.storeColor(i * 4, out);
						else this.storeColor(i * 3, out);
					}
				}
				
			} catch (IOException e) {
				throw new ColliderException("Adapter can't store targa file", e);				
			}
		}
		
		// RLE-decoding support data
		private boolean rawPacket = true;
		private int packetCounter = 0;
		private int colorCounter = 0;
		private byte bValue = 0, gValue = 0, rValue = 0, aValue = 0;
		
		private byte next() throws IOException {
			if (rle) {
				if (++colorCounter == clen) {
					packetCounter--;
					colorCounter = 0;		
				}
				
				if (packetCounter > 0) {
					// packet data
					if (rawPacket) return this.in.readByte();
					else {
						if (colorCounter == 0) return bValue;
						else if (colorCounter == 1) return gValue;
						else if (colorCounter == 2) return rValue;
						return aValue;
					}
				} else {
					byte p = this.in.readByte();
					if ((p & 0x80) == 0) {
						rawPacket = true;
						packetCounter = p + 1;
						colorCounter = 0;					
						return this.in.readByte();
					} else {
						rawPacket = false;
						packetCounter = (p & 0x7F) + 1;
						colorCounter = 0;
						bValue = this.in.readByte();
						gValue = this.in.readByte();
						rValue = this.in.readByte();
						if (clen == 4) aValue = this.in.readByte();					
						return bValue;
					}
				}
			} else {
				byte value = this.in.readByte();
				return (byte)value;
			}
		}
		
		private void storeRawPacket(int pos, int length, DataOutputStream out) throws IOException {
			out.writeByte(length - 1);
			for (int i = 0; i < length; i++) this.storeColor(pos, out);
		}
		
		private void storeRunLengthPacket(int pos, int length, DataOutputStream out) throws IOException {			
			out.writeByte(0x80 | (length - 1));
			this.storeColor(pos, out);			
		}
		
		private void storeColor(int pos, DataOutputStream out) throws IOException {
			byte r, g, b, a = 0;
			r = raw[pos++];
			g = raw[pos++];
			b = raw[pos++];
			if (alpha) a = raw[pos++];
			
			out.writeByte(b);
			out.writeByte(g);
			out.writeByte(r);
			if (alpha) out.writeByte(a);			
		}
		
		private int getNextRunLength(int position) {
			int length = 1;
			byte r, g, b, a = 0;
			r = raw[position++];
			g = raw[position++];
			b = raw[position++];
			if (this.alpha) a = raw[position++];
			
			while(position < raw.length) {
				if (this.alpha) {
					if (r == raw[position++]
				         && g == raw[position++]
				         && b == raw[position++]
				         && a == raw[position++]) length++;
					else break;
				} else {
					if (r == raw[position++]
						         && g == raw[position++]
						         && b == raw[position++]) length++;
					else break;					
				}
				if (length == 128) break;
			}
			
			return length;
		}

		public int getWidth() {
			return width;
		}
		
		public int getHeight() {
			return height;
		}
		
		public byte[] getRawData() {
			return this.raw;
		}

		public boolean hasAlpha() {
			return alpha;
		}
		
		public int getImageDescriptor() {
			return this.imageDesc;
		}

		private class TargaColorTable {
			
			private int colorMapEntrySize;
			
			private byte[] cdata;
			
			public TargaColorTable(int colorMapEntrySize, byte[] cdata) {
				// only 32bit color model are allowed and we can't handle other models
				// TODO throw an exception that tga color model is not supported if not 32
				this.colorMapEntrySize = (colorMapEntrySize >> 3);
				
				this.cdata = cdata;
			}
			
			public byte getRed(int index) {
				return cdata[index * colorMapEntrySize + 2];		
			}
			public byte getGreen(int index) {
				return cdata[index * colorMapEntrySize + 1];		
			}
			public byte getBlue(int index) {
				return cdata[index * colorMapEntrySize];
			}
			public byte getAlpha(int index) {
				return cdata[index * colorMapEntrySize + 3];		
			}	
		}
	}
}
