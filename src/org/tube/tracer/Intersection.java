package org.tube.tracer;

import org.tube.math.Vector3;
import org.tube.scene.primitive.Primitive;
import org.tube.scene.primitive.Material;

/**
 * This class represents result of an intersection
 * between tracing ray and a primitive.
 * It contains all essential information about intersection -
 * normal vector, distance, link to the primitive.
 * 
 * @author Igor Khotin
 *
 */
public class Intersection {
	private boolean intersected;
	
	private double distance;
	
	private Vector3 point; // vector to the point of intersection
	
	private Vector3 normal;
	
	private Primitive primitive;
	
	public Intersection() {
		this.intersected = false;		
	}
	
	public Intersection(double distance, Vector3 point, Vector3 normal, Primitive primitive) {
		this.intersected = true;
		this.distance = distance;
		this.point = point;
		this.normal = normal;
		this.primitive = primitive;
	}
	
	public boolean isIntersected() {
		return this.intersected;
	}
	
	public double getDistance() {
		return this.distance;
	}
	
	public Vector3 getPoint() {
		return this.point;
	}
	
	public Vector3 getNormal() {
		return this.normal;
	}
	
	public Primitive getPrimitive() {
		return this.primitive;
	}
    
    public Material getMaterial() {
        return this.primitive.getMaterial();
    }
	
	
	// debug
	public void dump() {
		System.out.print("Intersection dump: \n");
		if (intersected) {
			System.out.println("    dist = " + this.distance);
			if (this.normal == null) System.out.println("    normal is null");
			if (this.primitive == null) System.out.println("    primitive is null");			
		} else {
			System.out.println("    no intersection");
		}
	}
}
