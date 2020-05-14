package org.tube.scene.primitive;

/**
 * Represents the color of a material
 * 
 * @author Igor Khotin
 *
 */
public class Color {

  public double r, g, b;
  
  public Color (double r, double g, double b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }
  
  public void set(double r, double g, double b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }
  
  public Color (int argb) {
    r = (double) (argb >> 16 & 0xff) / 255;
    g = (double) (argb >> 8 & 0xff) / 255;
    b = (double) (argb >> 0 & 0xff) / 255;
  }
  
  public void scale (double scale) {
    r *= scale;
    g *= scale;
    b *= scale;
  }
  
  public void add (Color texel) {
    r += texel.r;
    g += texel.g;
    b += texel.b;
  }
  
  /**
   * @return the RGB color value in format 0xFFRRGGBB
   */
  public int getRGB () {
	if (this.r > 1) this.r = 1; if (this.r < 0) this.r = 0;
	if (this.g > 1) this.g = 1; if (this.g < 0) this.g = 0;
	if (this.b > 1) this.b = 1; if (this.b < 0) this.b = 0;			
	
    return 0xff000000 | (int) (r * 255.99) << 16 |
      (int) (g * 255.99) << 8 | (int) (b * 255.99) << 0;
  }
  
  public int getRed() {
      int ir = (int)(this.r * 256);
      if (ir > 255) ir = 255;
      return ir;
  }
  
  public int getGreen() {
      int ig = (int)(this.g * 256);
      if (ig > 255) ig = 255;
      return ig;
  }
  
  public int getBlue() {
      int ib = (int)(this.b * 256);
      if (ib > 255) ib = 255;
      return ib;
  }
}
