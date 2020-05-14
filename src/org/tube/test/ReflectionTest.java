package org.tube.test;

import org.tube.math.*;

public class ReflectionTest {
    
    public static void main(String[] args) {
        System.out.println("Testing reflection");
        
        Vector3 original = new Vector3(0, 1, 0);
        original.normalize();
        Vector3 normal = new Vector3(1, 1, 0);
        normal.normalize();
        Vector3 reflection = original.getReflected(normal);
        
        original.print();
        normal.print();
        reflection.print();
        reflection.normalize();
        reflection.print();
    }
}
