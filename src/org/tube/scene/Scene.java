package org.tube.scene;

import java.util.Iterator;
import java.util.Vector;

import org.tube.math.*;
import org.tube.scene.primitive.*;

/**
 * <p>
 * Represents scene with all objects.
 * </p>
 * <p>
 * Usually, scene contains Primitives (which could be organized
 * in more complicated structures), Light Sources and Cameras. 
 * </p> 
 *
 * @author Igor Khotin
 */
public abstract class Scene {

    public Vector<Primitive> obj = new Vector<Primitive>();
    
    public void addPrimitive(Primitive primitive) {
        this.obj.add(primitive);
    }
    
    public int getPrimitivesNum() {
        return this.obj.size();
    }
    
    /**
     * @return iterator with all available primitives on the scene
     */
    public Iterator getPrimitives() {
    	return this.obj.iterator();
    }
}
