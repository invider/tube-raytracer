package org.tube.scene.primitive;

import org.tube.math.Vector3;

import org.tube.math.Vector3;
import org.tube.tracer.Intersection;
import org.tube.tracer.Ray;

public class Sphere extends Primitive {

    Vector3 center;
    
    double radius, rradius, sqradius;
    
    public Sphere(Vector3 center, double radius) {
        this.center = center;
        this.radius = radius;
        this.sqradius = radius * radius;
        this.rradius = 1.0d / radius;
    }
    
    public Vector3 getCenter() {
        return this.center;
    }
    
    public double getSqRadius() {
        return this.sqradius;
    }
    
    public Vector3 getNormal(Vector3 pos) {
        Vector3 vec = new Vector3(pos);
        vec.sub(this.center);
        vec.mul(this.rradius);
        return vec;
    }
    
    
    public Intersection intersect(Ray ray) {
        Vector3 rayToSphereCenter = Vector3.sub(center, ray.getOrigin());
        double lengthRTSC2 = Vector3.dot(rayToSphereCenter, rayToSphereCenter);
        
        double closestApproach = Vector3.dot(rayToSphereCenter, ray.getDirection());
        if (closestApproach < 0 ) // the intersection is behind the ray
            return new Intersection(); // return no intersection
            
        // halfCord2 = the distance squared from the closest approach of the ray
        // to a perpendicular to the ray through the center of the sphere to
        // the place where the ray actually intersects the sphere
        double halfCord2 = (radius * radius) - lengthRTSC2 + (closestApproach * closestApproach);
        
        if(halfCord2 < 0)
        	return new Intersection(); // ray missed the sphere, return no intersection        
            
        // the ray hits the sphere. calculate the normal
        double dist = closestApproach - (double)Math.sqrt(halfCord2);
        Vector3 intersectionLoc = Vector3.add(
        		ray.getOrigin(), Vector3.scale(ray.getDirection(), dist));        
        Vector3 normal = Vector3.sub(intersectionLoc, center);
        //Vector3.scale(normal, 1/ radius);
        return new Intersection(dist, intersectionLoc, normal, this);
    }
}
