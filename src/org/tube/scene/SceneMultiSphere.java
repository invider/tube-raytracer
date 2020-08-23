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
        Sphere big = new Sphere(new Vector3(1, 1, 7), 2);
        big.setMaterial(matBlue);
        obj.add(big);

        double sx = -25, sy = -25, sz = 40;
        double x = sx, y = sy, z = sz;
        double dx = 8, dy = 8;
        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < 8; i++) {
                // small sphere
                Sphere small = new Sphere(new Vector3(x, y, 40), 4);
                small.setMaterial(matRed);
                obj.add(small);
                x += dx;
            }
            x = sx;
            y += dy;
        }
        
        // triangles
        Triangle t = new Triangle(
            new Vector3(-1, 0, 4),
            new Vector3(1,  1, 4),
            new Vector3(0,  0, 5)
        );
        t.setMaterial(matBlue);
        obj.add(t);
	}
}
