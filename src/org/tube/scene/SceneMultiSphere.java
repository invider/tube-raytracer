package org.tube.scene;

import org.tube.math.Vector3;
import org.tube.scene.primitive.Color;
import org.tube.scene.primitive.Material;
import org.tube.scene.primitive.Sphere;
import org.tube.scene.primitive.Triangle;

public class SceneMultiSphere extends Scene {

	public SceneMultiSphere() {
		// create materials
    	Material matBlue = new Material(new Color(0d, 0d, 1d), 1, 0.2, 0.1, 0, 0);
    	Material matRed = new Material(new Color(1d, 0d, 0d), 1, 0.2, 0.1, 0, 0);
    	
        // big sphere
        Sphere big = new Sphere(new Vector3(1, 1, 5), 2);
        big.setMaterial(matBlue);
        obj.add(big);
        
        // small sphere
        Sphere small = new Sphere(new Vector3(-3, -3, 7), 1);
        small.setMaterial(matRed);
        obj.add(small);
        
        
	}
}
