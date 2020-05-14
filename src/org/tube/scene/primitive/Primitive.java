package org.tube.scene.primitive;

import org.tube.math.Vector3;
import org.tube.tracer.Intersection;
import org.tube.tracer.Ray;

/**
 * Represents abstract surface on the scene.
 * All other bodies (planes, spheres, triangles etc)
 * must inherit all properties of this class.
 * 
 * @author Igor Khotin
 *
 */
public abstract class Primitive {
	
    public Material material;
    
    public boolean light = false;
    
    
    //public abstract Vector3 getNormal(Vector3 pos);
    
    public abstract Intersection intersect(Ray ray);
    
    
    public void setMaterial(Material material) {
        this.material = material;
    }
    
    public Material getMaterial() {
        return this.material;
    }
    
    public Color getColor() {
        return this.material.getColor();
    }
    
    public boolean isLight() {
        return light;
    }
}
