package org.tube.scene.primitive;

import org.tube.math.Vector3;
import org.tube.tracer.Ray;
import org.tube.tracer.Intersection;

public class Triangle extends Primitive
{
    private Vector3 v1, v2, v3;
    private Vector3 edge1, edge2, normal;
    
    public Triangle(Vector3 v1, Vector3 v2, Vector3 v3)
    {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.edge1 = Vector3.sub(v2, v1);
        this.edge2 = Vector3.sub(v3, v1);
        this.normal = Vector3.cross(edge1, edge2); 
        this.normal.normalize();
    }
    
    public Intersection intersect(Ray ray)
    {
        Vector3 pvec = Vector3.cross(ray.getDirection(), this.edge2);
        
        double det = Vector3.dot(this.edge1, pvec);
	        
        // no intersection
        if(det > -.000001 && det < .000001)
            return new Intersection();
            
        double invDet = 1/det;
        
        Vector3 tvec = Vector3.sub(ray.getOrigin(), this.v1);
        
        double u = Vector3.dot(tvec, pvec) * invDet;
        if(u < 0 || u > 1)
            return new Intersection();
        
        Vector3 qvec = Vector3.cross(tvec, this.edge1);
        
        double v = Vector3.dot(ray.getDirection(), qvec) * invDet;
        if(v < 0 || (u + v) > 1)
            return new Intersection();
            
        double dist = Vector3.dot(this.edge2, qvec) * invDet;
        if(dist < 0)
        	return new Intersection();
        
        Vector3 intersectionLocation = new Vector3();
        
        return new Intersection(dist, intersectionLocation, normal, this);
    }
}

