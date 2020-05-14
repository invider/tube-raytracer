package com.colliderlabs.stub.image;

import java.nio.Buffer;

import com.colliderlabs.ColliderException;

public abstract class GLImage {
	
	protected int width, height;
	
	protected boolean alpha;
	
	public abstract byte[] getRawData();
	
	public abstract int[] getRawIntData() throws ColliderException;
	
	/**
	 * Retrieves raw image data. 
	 *  	
	 * @return a Buffer instance with raw image data. MUST be compatible with specified data type. 
	 */
	public abstract Buffer getNormalizedData();
	
	public abstract void flipHorizontaly();
	
	public abstract void flipVerticaly();
	
	/**
	 * @return image width
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * @return image height
	 */
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * Returns normalized width - which is one of powers of 2
	 * 
	 * @return OpenGL texture compatible width
	 */
	public int getNormalizedWidth() {
		return this.getNormalizedValue(this.width);
	}
	
	/**
	 * Returns normalized height - which is one of powers of 2
	 * 
	 * @return OpenGL compatible height
	 */
	public int getNormalizedHeight() {
		return this.getNormalizedValue(this.height);
	}
	
	/**
	 * Get closest power of 2
	 */
	private int getNormalizedValue(int value) {
		int res = 2;
		while (res < value) res *= 2;
		return res;
	}
	
	/**
	 * Checks if source data uses alpha channel
	 * @return true if raw image data contains alpha values
	 */
	public boolean hasAlpha() {
		return this.alpha;
	}
	
	/**
	 * Calculates the number of values needed to store image
	 * based on image size and texel format.
	 * 
	 * @return
	 */
	public int storageDemands() {
		return width * height * texelSize();
	}
	
	/**
	 * Determines texel size by texel format
	 * @return
	 */
	public int texelSize() {
		if (this.alpha) return 4;
		else return 3;
	}
}