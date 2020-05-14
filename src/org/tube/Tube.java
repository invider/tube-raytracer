package org.tube;

/**
 * Application entry point
 * 
 * @author Igor Khotin
 *
 */
public class Tube {

    public Tube() {}

    public String getVersion() {
        return "0.1";
    }

    public static void main(String[] args) {
        System.out.println("Tube raycasting engine");
        
        // construct GUI
        TubeFrame tubeFrame = new TubeFrame("Tube Ray Tracer");
    }
}
