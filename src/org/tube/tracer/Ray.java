package org.tube.tracer;

import org.tube.math.Vector3;

/**
 * Tracing ray
 * 
 * @author Igor Khotin
 *
 */
public class Ray {
    public static final int MISS = 0;
    public static final int INPRIM = -1;
    public static final int HIT = 1;
    
    private Vector3 origin;
    
    private Vector3 direction;
    
    public Ray() {
        this.origin = new Vector3(0, 0, 0);
        this.direction = new Vector3(0, 0, 0);
    }
    
    public Ray(Vector3 origin, Vector3 direction) {
        this.origin = origin;
        this.direction = direction;
    }
    
    public void setOrigin(Vector3 origin) {
        this.origin = origin;
    }
    
    public Vector3 getOrigin() {
        return this.origin;
    }
    
    public void setDirection(Vector3 direction) {
        this.direction = direction;
    }
    
    public Vector3 getDirection() {
        return this.direction;
    }
}
