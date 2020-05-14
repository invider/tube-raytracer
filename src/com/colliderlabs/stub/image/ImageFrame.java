package com.colliderlabs.stub.image;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;

public class ImageFrame extends Frame {
	
	private Image image;
	
	public ImageFrame() {}
	
	public void setImage(Image image) {
		this.image = image;
		this.setSize(new Dimension(image.getWidth(this), image.getHeight(this)));		
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(this.image, 0, 0, this);			
	}
}
