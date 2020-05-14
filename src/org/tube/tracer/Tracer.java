package org.tube.tracer;

import java.util.Iterator;

import org.tube.math.Vector3;
import org.tube.scene.Scene;
import org.tube.scene.primitive.Color;
import org.tube.scene.primitive.Material;
import org.tube.scene.primitive.Primitive;

/**
 * Renders the image of scene.
 * @todo we must have a camera to attach tracer too
 * 
 * @author Igor Khotin
 *
 */
public class Tracer {
    //>>>>>>>>>>>>>>>>>>>>>>
    //debug values
	public double dlx = -50;
    public int dflag;
    //>>>>>>>>>>>>>>>>>>>>>>
    
    public int width, height;
    int[] raw;
    
    private Scene scene;
    
    // render options
    private int maxReflectionDepth = 7;

    public Tracer(Scene scene) {
    	this.scene = scene;
    }
    
    public void createDisplay(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Renders image of the scene
     */
    private int dx, dy;
    public int[] render() {
    	/*
        int pos = 0;
        this.raw = new int[width * height];
        
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                raw[pos++] = 0xff6000 << 8 | x;
            	//raw[pos++] = 0xff000000;
            }
        */
        
        // debug
        dflag = 0;
    	
    	// create new canvas
    	this.raw = new int[width * height];
    	
       	// camera focus distance
    	double fov = 250;
    	Vector3 cameraOrigin = new Vector3(0, 0, 0);
    	
    	// fire single ray for each pixel of image
    	int iraw = 0;
    	for (int y = 0; y < height; y++) {
    		for (int x = 0; x < width; x++) {
                 //@debug
                 dx = x; dy = y;
    			 Vector3 rayDir = new Vector3(x - width/2, y - height/2, fov);
                 rayDir.normalize();
                 Ray ray = new Ray(cameraOrigin, rayDir);
                 Color color = traceRay(ray, 0);
                 
                 if (color != null) raw[iraw++] = color.getRGB();
                 else raw[iraw++] = 0xff202020;                 
    		}
    	}
    		
        return raw;
    }    
    
    
    /**
     * Traces a single ray
     *
     * @param ray 
     * @param depth current recursive trace depth
     * @return the color of intersected point if any; black color overwise
     */
    private Color traceRay(Ray ray, int depth)
    {    	
        if( depth > maxReflectionDepth ) return new Color(0,0,0);
        
        //Color ambientLightColor = new Color(.1f, .1f, .1f);
        Color dirLightColor = new Color(1d, 1d, 1d);
        Vector3 lightSource = new Vector3(dlx, 0, -20);
        //lightSource.normalize();
        
        // ********************************************************************
        // itterate over each primitive
        Intersection closestIntersection = null;
        Iterator prims = this.scene.getPrimitives();
        
        while (prims.hasNext()) {
        	Primitive primitive = (Primitive)prims.next();        	
        	Intersection intersection = primitive.intersect(ray);
        	
        	if (!intersection.isIntersected()) continue;
        	if (closestIntersection == null)
        		closestIntersection = intersection;
        	if (intersection.getDistance() < closestIntersection.getDistance())
        		closestIntersection = intersection;
        }
        
        // check if no intersection found
        if (closestIntersection == null || !closestIntersection.isIntersected())        	
        	return new Color(0, 0, 0); // return background color       
        
        Vector3 normal = closestIntersection.getNormal();
        Vector3 intersectionPoint = closestIntersection.getPoint();
        Material material = closestIntersection.getMaterial();
        Color color = material.getColor();
        
        // *************************************************************
        // compute the color of the surface at the point of intersection
        
        // cycle through all light sources
        
        	// found one ...
            // **************************************************************
            // diffuse
			// create a vector pointing to that source
			Vector3 lightVector = Vector3.sub(lightSource, intersectionPoint);
			// find the distance to the light before normalizing			
			double lightDist = lightVector.getLength();
			// normalize vector on light
			lightVector.normalize();
			// calculate coeficient
			double diffuseCoef = Vector3.dot(normal, lightVector);
            
			// check if vector on light and normal pointing opposite dirrections
			if (diffuseCoef > 0) {
				diffuseCoef *= material.getDiffuse();
			} else diffuseCoef = 0;
            
            // **************************************************************
            // specular
            
            Vector3 lightReflected = lightVector.getReflected(normal);
            lightReflected.normalize();
            
            double specularCoef = 0;
            double dot = Vector3.dot(ray.getDirection(), lightReflected);
            if (dot > 0) {
                specularCoef = Math.pow(dot, 200) * material.getSpecular();
            }
            
			//double specularCoef = Math.max(
            //    0, .getOpposite()));
            //specularCoef = Math.pow(specularCoef, material.getPhong())
            //    * material.getSpecular();
                
           //if (dx > 200 && dx < 220 && dy == 200) {
           //if (dflag < 10 && specularCoef > 0.1) {
           //    dflag++;
           //}
            
        /*    
        // calculate reflection
        Vector3 reflectDir = Vector3.sub(ray.getDirection(), Vector3.scale(normal, 2 * Vector3.dot(ray.getDirection(), normal)));
        Vector3 intersection = Vector3.add(ray.getOrigin(), Vector3.scale(ray.getDirection(), closestIntersection.getDistance()));
        
        // move away a little bit towards normal
        // so ray will not intersect the surface it reflected from
        float distFromSurfaceInDirOfNormal = .0001f;
        intersection = Vector3.add(intersection, Vector3.scale(normal, distFromSurfaceInDirOfNormal));
        Ray reflectedRay = new Ray(intersection, reflectDir);
        // invoke another ray
        //Color reflectedColor = traceRay(reflectedRay, depth + 1);
        Color reflectedColor = new Color(0d, 0d, 0d);
        
        Material material = closestIntersection.getPrimitive().getMaterial();
        
        /*
        r = material.getReflection() *  reflectedColor.getRed()
        			+ (1d - material.getReflection()) * (ambientLightColor.getRed()
        			* material.getAmbient().getRed() + dirLightColor.getRed()
        			* material.getDiffuse().getRed() * lightCoef);
        
        g = material.getReflection() * reflectedColor.getGreen()
        			+ (1d - material.getReflection()) * (ambientLightColor.getGreen()
        			* material.getAmbient().getGreen() + dirLightColor.getGreen()
        			* material.getDiffuse().getGreen() * lightCoef);
        
        b = material.getReflection() * reflectedColor.getBlue()
        			+ (1d - material.getReflection()) * (ambientLightColor.getBlue()
        			* material.getAmbient().getBlue() + dirLightColor.getBlue()
        			* material.getDiffuse().getBlue() * lightCoef);
        */
        /*
        r = material.getReflection() * reflectedColor.getRed() 
        	+ (1d - material.getDiffuse().getRed());
        g = material.getReflection() * reflectedColor.getGreen()
        	+ (1d - material.getDiffuse().getGreen());
        b = material.getReflection() * reflectedColor.getBlue()
        	+ (1d - material.getDiffuse().getBlue());
        	*/
            
        // the final color to return
        double r=0, g=0, b=0;
        
        r = diffuseCoef * color.r + specularCoef * 255;
        g = diffuseCoef * color.g + specularCoef * 255;
        b = diffuseCoef * color.b + specularCoef * 255;
        
        return new Color(r, g, b);
    }
}