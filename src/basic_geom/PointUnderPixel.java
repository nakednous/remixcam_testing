package basic_geom;

import geom.Box;
import processing.core.*;
import processing.opengl.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.ClickAction;
import remixlab.dandelion.geom.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class PointUnderPixel extends PApplet {
	Scene scene;
	Box [] boxes;
	
	public void setup()	{
		size(640, 360, P3D);
		scene = new Scene(this);
		scene.setRadius(150);
		scene.showAll();	
		boxes = new Box[50];
		for (int i = 0; i < boxes.length; i++)
			boxes[i] = new Box(scene);
		
		scene.defaultKeyboardAgent().keyboardProfile().setShortcut('z', Scene.KeyboardAction.RESET_ARP);
	    
	    scene.defaultMouseAgent().cameraClickProfile().setClickBinding(Constants.TH_NOMODIFIER_MASK, Constants.TH_LEFT, 1, ClickAction.ZOOM_ON_PIXEL);
	    scene.defaultMouseAgent().cameraClickProfile().setClickBinding(Constants.TH_ALT, Constants.TH_CENTER, 1, ClickAction.SHOW_ALL);
	    scene.defaultMouseAgent().cameraClickProfile().setClickBinding(Constants.TH_META,  Constants.TH_RIGHT, 1, ClickAction.ARP_FROM_PIXEL);
	    //scene.defaultMouseAgent().cameraClickProfile().setClickBinding(Constants.TH_NOMODIFIER_MASK,  Constants.TH_RIGHT, 1, ClickAction.ARP_FROM_PIXEL);
	    scene.defaultMouseAgent().cameraClickProfile().setClickBinding(Constants.TH_SHIFT, Constants.TH_LEFT, 1, ClickAction.RESET_ARP);	    
	}

	public void draw() {	
		background(0);
		for (int i = 0; i < boxes.length; i++)			
			boxes[i].draw();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic_geom.PointUnderPixel" });
	}
}