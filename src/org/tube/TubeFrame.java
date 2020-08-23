package org.tube;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.tube.tracer.*;
import org.tube.scene.*;

import com.colliderlabs.ColliderException;
import com.colliderlabs.stub.image.GLImage;
import com.colliderlabs.stub.image.GLUnsignedByteImage;
import com.colliderlabs.stub.image.ImageAdapter;
import com.colliderlabs.stub.image.TargaImageAdapter;


/**
 * GUI main frame
 * 
 * @author Igor Khotin
 */
public class TubeFrame extends Frame implements WindowListener, MouseListener {
	// renders 
    int width = 600;
    int height = 600;
    int renderCount = 0;

    int borderX = 0;
    int borderY = 0;
    
    Image imgBackground;    
    
    Image imgRender;
    
    Scene scene;
    
    Tracer tracer;
    
    public TubeFrame(String title) {
        super(title);
        this.setSize(width, height);
        this.setResizable(false);
        this.setVisible(true);
        this.addWindowListener(this);
        this.addMouseListener(this);
        
        generateBackgroundImage();
        
        this.scene = new SceneMultiSphere();
        
        // create a render
        this.tracer = new Tracer(scene);
      
        this.tracer.createDisplay(600, 600);
        this.centerFrame();
    }

    private void centerFrame() {
            Dimension windowSize = getSize();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Point centerPoint = ge.getCenterPoint();

            int dx = centerPoint.x - windowSize.width / 2;
            int dy = centerPoint.y - windowSize.height / 2;    
            this.setLocation(dx, dy);
    }
    
    public void paint(Graphics g) {        
        g.drawImage(imgBackground, 0, 0, this);
        g.drawImage(imgRender, this.borderX, this.borderY, this);
    }
            
    @Override
	public void repaint() {    	
		//super.repaint();
		this.paint(this.getGraphics());
	}

	public void windowOpened(WindowEvent e) {}
    
    public void windowActivated(WindowEvent e) {}
    
    public void windowDeactivated(WindowEvent e) {}
    
    public void windowIconified(WindowEvent e) {}
    
    public void windowDeiconified(WindowEvent e) {}
    
    public void windowClosing(WindowEvent e) {
        windowClosed(e);
    }
    
    public void windowClosed(WindowEvent e) {
        this.setVisible(false);
        System.exit(0);
    }
    
    public void mouseClicked(MouseEvent e) {}
    
    public void mousePressed(MouseEvent e) {
        System.out.println("rendering #" + this.renderCount++);
        
        generateRender();
        this.repaint();
    }
    
    public void mouseReleased(MouseEvent e) {}
    
    public void mouseEntered(MouseEvent e) {}
    
    public void mouseExited(MouseEvent e) {}
    
    /**
     * generates rendered image
     */
    private void generateRender() {    	
    	int[] raw = this.tracer.render();
    	this.tracer.dlx += 100; // move the light source vector
    	    	
    	imgRender = this.createImage(new MemoryImageSource(
    	            	tracer.width, tracer.height, raw, 0, tracer.height));
    	
    	this.saveTarga(raw);
    }
    
    private int targaCounter = 0;
    private void saveTarga(int[] raw) {
    	try {
    		GLImage image = new GLUnsignedByteImage(true, tracer.width, tracer.height, raw);
    		image.flipVerticaly();
    		
    		OutputStream outputStream = new FileOutputStream(
    			"render_" + ++targaCounter + ".tga");
    	
    		ImageAdapter adapter = new TargaImageAdapter();
    		adapter.storeImage(image, outputStream,
    				TargaImageAdapter.TARGA_RAW_ENCODED);
    	} catch (IOException e) {
    		e.printStackTrace();
    	} catch (ColliderException e) {
    		e.printStackTrace();
    	}
    	
    }
    
    /**
     * generates background image
     */
    private void generateBackgroundImage() {
        int i = 0;
        int r, g, b;
        int pixels[] = new int[height * width];
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < height; x++) {
                r = (x^y)&0xFF;
                g = (x*2^y*2)&0xFF;
                b = (x*4^y*4)&0xFF;
                pixels[i++] = (255 << 24) | (r << 16) | (g << 8) | b;
            }
        }
        
        imgBackground = this.createImage(
            new MemoryImageSource(width, height, pixels, 0, height));
    }
}
