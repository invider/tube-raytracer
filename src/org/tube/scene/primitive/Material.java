package org.tube.scene.primitive;

import org.tube.scene.primitive.Color;

/**
 * Material represents color and reflective properties of a surface. 
 * 
 * @author Igor Khotin
 *
 */
public class Material {
    
    // color properties
    private Color color;
    
    // illumination properties
    private double diffuse;
    
    private double specular;
    
    private double ambient;
    
    private int phong; // determines the size of specular highlight
    
    // optics
    private double reflection = 0;
    
    private double transparency = 0;
    
    
    public Material() {
        this.color = new Color(0.6, 0.6, 0.6);
        this.diffuse = 1;
        this.specular = 1;
        this.ambient = 0;
    }
    
    public Material(Color color,
                double diffuse, double specular, double ambient,
                double reflection, double transparency) {
        this.color = color;
        this.diffuse = diffuse;
        this.specular = specular;
        this.ambient = ambient;
        this.reflection = reflection;
        this.transparency = transparency;
        this.phong = 20;
    }

	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
        this.color = color;
	}
    
    public double getDiffuse() {
        return this.diffuse;
    }
    
    public double getSpecular() {
        return this.specular;
    }
    
    public double getAmbient() {
        return this.ambient;
    }
    
    public int getPhong() {
        return this.phong;
    }
}
