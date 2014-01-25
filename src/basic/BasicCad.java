package basic;

import processing.core.*;
import remixlab.dandelion.core.Constants.*;
import remixlab.proscene.*;
import remixlab.proscene.Scene.ProsceneMouse;
import remixlab.tersehandling.core.EventConstants;

public class BasicCad extends PApplet {
	private static final long serialVersionUID = 1L;
	
	Scene scene;

	public void setup() {
	  //size(640, 360, P3D);
		size(360, 640, P3D);
	  scene = new Scene(this);
	  //ProsceneMouse mouse = (ProsceneMouse)scene.defaultMotionAgent();
	  //mouse.cameraProfile().setBinding(EventConstants.TH_LEFT, DOF2Action.CAD_ROTATE);
	  scene.defaultMouseAgent().cameraProfile().setBinding(EventConstants.TH_LEFT, DOF2Action.CAD_ROTATE);
	}

	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 50);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
