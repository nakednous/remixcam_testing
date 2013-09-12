package basic;

import processing.core.*;
import processing.opengl.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;
import remixlab.proscene.*;

public class FirstPerson extends PApplet {
	Scene scene;
	boolean focusIFrame;
	boolean firstPerson;
	InteractiveFrame iFrame;
	
	public void setup()	{
		size(640, 360, P3D);		
		scene = new Scene(this);	
		iFrame = new InteractiveFrame(scene);
		iFrame.translate(new Vec(30, 30, 0));
		scene.defaultMouseAgent().setAsFirstPerson();
		firstPerson = true;
	}

	public void draw() {
		background(0);
		fill(204, 102, 0);
		box(20, 30, 40);		
		
		// Save the current model view matrix
		pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(iFrame.matrix()) is possible but inefficient 
		iFrame.applyTransformation();//very efficient
		// Draw an axis using the Scene static function
		scene.drawAxis(20);
		
		// Draw a second box
		if (focusIFrame) {
			fill(0, 255, 255);
			box(12, 17, 22);
		}
		else if (iFrame.grabsAgent(scene.defaultMouseAgent())) {
			fill(255, 0, 0);
			box(12, 17, 22);
		}
		else {
			fill(0,0,255);
			box(10, 15, 20);
		}	
		
		popMatrix();
	}
	
	public void keyPressed() {
		if( key == 'i') {
			if( focusIFrame ) {
				scene.defaultMouseAgent().setDefaultGrabber(scene.viewport().frame());
				scene.defaultMouseAgent().enableTracking();
			} else {
				scene.defaultMouseAgent().setDefaultGrabber(iFrame);
				scene.defaultMouseAgent().disableTracking();
			}
			focusIFrame = !focusIFrame;
		}
		if( key == ' ') {
			firstPerson = !firstPerson;
			if ( firstPerson ) {
				scene.defaultMouseAgent().setAsFirstPerson();
			}
			else {
				scene.defaultMouseAgent().setAsArcball();			
			}		
		}
		if ((key == 'y') || (key == 'Y')) {
			scene.setDottedGrid(!scene.gridIsDotted());
		}
		if ((key == 'u') || (key == 'U')) {
			println("papplet's frame count: " + frameCount);
			println("scene's frame count: " + scene.timingHandler().frameCount());
		}
		if ((key == 'v') || (key == 'V')) {
			println("papplet's frame rate: " + frameRate);
			println("scene's frame rate: " + scene.timingHandler().frameRate());
		}
		if( key == 't' || key == 'T' )
			scene.switchTimers();
		if(key == '+')
			frameRate(frameRate + 10);
		if(key == '-')
			frameRate(frameRate - 10);	
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.FirstPerson" });
	}
}
