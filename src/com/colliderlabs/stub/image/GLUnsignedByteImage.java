package com.colliderlabs.stub.image;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.colliderlabs.ColliderException;
import com.colliderlabs.stub.image.GLImage;

public class GLUnsignedByteImage extends GLImage {
	
	protected ByteBuffer data;
	
	public GLUnsignedByteImage(boolean alpha, int width, int height) {
		this.alpha = alpha;
		this.width = width;
		this.height = height;
		this.data = ByteBuffer.allocateDirect(this.storageDemands());		
	}
	
	public GLUnsignedByteImage(boolean alpha, int width, int height, byte[] rawData) {
		this.alpha = alpha;
		this.width = width;
		this.height = height;
		this.data = ByteBuffer.allocateDirect(rawData.length);
		this.data.put(rawData);
	}
	
	public GLUnsignedByteImage(boolean alpha, int width, int height, ByteBuffer buffer) {
		this.alpha = alpha;
		this.width = width;
		this.height = height;
		this.data = buffer;
	}
	
	public GLUnsignedByteImage(boolean alpha, int width, int height, int[] rawData) {
		this.alpha = alpha;
		this.width = width;
		this.height = height;
		// translate int[] to byte[]
		byte[] buf = null;
		if (alpha) buf = new byte[rawData.length * 4];
		else buf = new byte[rawData.length * 3];
		
		int pos = 0;
		for (int i = 0; i < rawData.length; i++) {
			buf[pos++] = (byte)((rawData[i] & 0x00FF0000) >> 16);
			buf[pos++] = (byte)((rawData[i] & 0x0000FF00) >> 8);
			buf[pos++] = (byte)(rawData[i] & 0x000000FF);
			if (alpha) buf[pos++] = (byte)(rawData[i] >> 24);
		}
		
		this.data = ByteBuffer.allocateDirect(buf.length);
		this.data.put(buf);
	}
	
	@Override
	public byte[] getRawData() {
		this.data.clear();
		byte[] bytes = new byte[this.data.capacity()];
		this.data.get(bytes, 0, bytes.length);
		
		return bytes;
	}
	
	@Override
	public int[] getRawIntData() throws ColliderException {
		byte[] d = this.getRawData();
		
		if (this.alpha) {
			int[] res = new int[d.length / 4];
			for (int i = 0; i < res.length; i++) {
				// cast each byte to unsigned int value
				// and shift according to ARGB color position
				res[i] =
					((int)d[i * 4 + 3] & 0xFF) << 24 
					| ((int)d[i * 4] & 0xFF) << 16				
					| ((int)d[i * 4 + 1] & 0xFF) << 8
					| ((int)d[i * 4 + 2] & 0xFF);
			}
			return res;
		} else {
			int[] res = new int[d.length / 3];
			for (int i = 0; i < res.length; i++) {
				// cast each byte to unsigned int value
				// and shift according to RGB color position
				res[i] =
					0xFF << 24
					| ((int)d[i * 3] & 0xFF) << 16				
					| ((int)d[i * 3 + 1] & 0xFF) << 8
					| ((int)d[i * 3 + 2] & 0xFF);				
			}
			return res;
		}
	}

	@Override
	public Buffer getNormalizedData() {
		
		int normalWidth = this.getNormalizedWidth();
		int normalHeight = this.getNormalizedHeight();
		int texelSize = this.texelSize();
		
		if (width < normalWidth || height < normalHeight) {
			// normalize
			// TODO we are making array copy here... consider cheaper ways to do that
			ByteBuffer res = ByteBuffer.allocateDirect(normalWidth * normalHeight * texelSize());
			
			int dpos = 0, opos = 0;			
			for (int y = 0; y < this.height; y++) {
				for (int x = 0; x < this.width; x++) {
					for (int c = 0; c < texelSize; c++) {
						res.put(dpos++, data.get(opos++));						
					}
				}
				// fill unused space
				for (int x = this.width; x < normalWidth; x++) {
					for (int c = 0; c < texelSize; c++)
						res.put(dpos++, (byte)0x00);					
				}
			}
			
			// fill unused space
			for (int y = this.height; y < normalHeight; y++) {
				for (int x = 0; x < normalWidth; x++) {
					for (int c = 0; c < texelSize; c++)
						res.put(dpos++, (byte)0x00);					
				}
			}
			
			res.position(0);
			return res;			
		} else {
			this.data.position(0);
			System.out.println();
			for (int i = 0; i < data.capacity(); i++) {
				System.out.print(" ." + data.get(i));
			}
			return data;
		}				
		
	}
	
	@Override
	public void flipVerticaly() {
		int texelSize = this.texelSize();
		byte[] line = new byte[this.width * texelSize];
		
		for (int y = 0; y < this.height / 2; y++) {
			// copy top line to buffer
			for (int i = 0; i < line.length; i++) line[i] = data.get(y * this.width * texelSize + i);
			// copy last line to first line
			for (int i = 0; i < line.length; i++) data.put(y * this.width * texelSize + i,
					data.get((height - y - 1) * this.width * texelSize + i));
			// copy buffer to last line
			for (int i = 0; i < line.length; i++)
				data.put((height - y - 1) * this.width * texelSize + i, line[i]);			
		}
	}
	
	@Override
	public void flipHorizontaly() {
		int texelSize = this.texelSize();
		byte[] line = new byte[this.height * texelSize];
		
		for (int x = 0; x < this.width / 2; x++) {
			// copy left row to buffer
			for (int y = 0; y < this.height; y++)
				for (int c = 0; c < texelSize; c++)
					line[y * texelSize + c] = data.get(y * this.width * texelSize + x * texelSize + c);
			
			// copy right row to left row
			for (int y = 0; y < this.height; y++)
				for (int c = 0; c < texelSize; c++)
					data.put(y * this.width * texelSize + x * texelSize + c,
							data.get(y * this.width * texelSize + (this.width - x - 1) * texelSize + c));
			
			// copy buffer to right row
			for (int y = 0; y < this.height; y++)
				for (int c = 0; c < texelSize; c++)
					data.put(y * this.width * texelSize + (this.width - x - 1) * texelSize + c,
							line[y * texelSize + c]);
		}
	}
	
}