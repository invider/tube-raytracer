package com.colliderlabs.stub.image;

import java.io.InputStream;
import java.io.OutputStream;

import com.colliderlabs.ColliderException;

/**
 * Interface to load and store images.
 * Different implementations provide loat/store capabilities
 * in different formats.
 * 
 * @author Igor Khotin
 * 
 */
public interface ImageAdapter {
	
	public GLImage loadImage(InputStream inputStream) throws ColliderException;
	
	public void storeImage(GLImage image, OutputStream outputStream) throws ColliderException;
	
	public void storeImage(GLImage image, OutputStream outputStream, int format) throws ColliderException;
	
}
