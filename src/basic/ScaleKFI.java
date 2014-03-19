package basic;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.dandelion.geom.*;

public class ScaleKFI extends PApplet {
	Scene scene;
	float scl = 50;

	public void setup() {
	  size(800, 800, P3D);
	  scene = new Scene(this);
	  scene.camera().setPosition(new Vec(80, 0, 0));
	  scene.camera().addKeyFrameToPath(1);
	  scene.camera().setPosition(new Vec(30, 30, -80));
	  scene.camera().addKeyFrameToPath(1);
	  scene.camera().setPosition(new Vec(-30, -30, -80));
	  scene.camera().addKeyFrameToPath(1);
	  scene.camera().setPosition(new Vec(-80, 0, 0));
	  scene.camera().addKeyFrameToPath(1);

	  scene.camera().setPosition(new Vec(0, 0, 1));
	  scene.showAll();

	  //scene.setViewPointPathsAreDrawn(true); // draws camera paths
	  scene.setRadius(2000); // makes drawn camera paths huge
	}
	 
	public void draw() {
	  background(0);
	  scene.drawPath(scene.eye().keyFrameInterpolator(1), 3, 5, scl);
	  fill(204, 102, 0);
	  box(20, 30, 50);
	}
	
	public void keyPressed() {
		if(key == '+')	{scl = scl*1.2f; println("scl: " + scl);}
		if(key == '-')	{scl = scl*0.8f; println("scl: " + scl);}
	}
}
