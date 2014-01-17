package basic;

import processing.core.*;
import remixlab.dandelion.core.Camera;
import remixlab.dandelion.core.Constants;
import remixlab.dandelion.core.Constants.KeyboardAction;
import remixlab.dandelion.core.KeyFrameInterpolator;
import remixlab.dandelion.geom.Vec;
import remixlab.proscene.*;
import remixlab.proscene.Scene.ProsceneKeyboard;

public class Basic extends PApplet {	
	private static final long serialVersionUID = 1L;
	
	Scene scene;

	public void setup() {
	  //size(360, 640, P3D);
      size(640, 360, P3D);
	  scene = new Scene(this);
	  //scene.camera().setType(Camera.Type.ORTHOGRAPHIC);
	  //scene.setJavaTimers();
	  scene.showAll();
	  scene.camera().frame().setDampingFriction(0);
	  frameRate(100);
	  //scene.setVisualHints(scene.visualHints() & ~Constants.AXIS);
	  scene.setVisualHints(Constants.AXIS | Constants.GRID | Constants.PATHS | Constants.FRAME);
	  //scene.defaultKeyboardAgent().profile().setShortcut('s', null);
	  //scene.setRadius(2000);
	  //scene.camera().setPosition(new Vec(0, 0, -1800));
	  //scene.camera().lookAt( new Vec(0, 0, 0));
	  Vec at = scene.camera().at();
	  print("at(): ");
	  at.print();
	  //print("at() at sketch: ");
	  //scene.camera().at().print();
	  //scene.viewpoint().frame().scaling().y(scene.viewpoint().frame().scaling().y() * 2);
	    
	}	
	
	public void draw() {
	  background(0);
	  fill(204, 102, 0);
	  box(20, 30, 50);
	}
	
	public void defaultKeyBindings() {
		scene.defaultKeyboardAgent().keyboardProfile().setShortcut('a', KeyboardAction.DRAW_AXIS);
		scene.defaultKeyboardAgent().keyboardProfile().setShortcut('f', KeyboardAction.DRAW_FRAME_SELECTION_HINT);
		scene.defaultKeyboardAgent().keyboardProfile().setShortcut('g', KeyboardAction.DRAW_GRID);
		scene.defaultKeyboardAgent().keyboardProfile().setShortcut('m', KeyboardAction.ANIMATION);
	}
	
	public void keyPressed() {
		if( key == 'd' || key == 'D' )
			defaultKeyBindings();
		if( key == 'x' || key == 'X' ) {
			float[] target = new float[2];
			scene.camera().getBoundaryWidthHeight(target);
			println("viewpoint aspect ratio: " + scene.camera().aspectRatio()
					+ " orthoX/orthoY; " + target[0]/target[1] + " orthoY/orthoX: " + target[1]/target[0]);	
		}
		if( key == 'T' || key == 't' ) {
			scene.camera().setFieldOfView((float) Math.PI / 3.0f);
		}
		if( key == 'U' || key == 'u' ) {
			scene.camera().setFieldOfView((float) Math.PI / 2.0f);
		}
		if(key=='v')
			scene.showAll();
	}
		
	/*
	public void keyPressed() {
		if( key == 't' || key == 'T' )
			scene.switchTimers();
		if(key == '+')
			frameRate(frameRate + 10);
		if(key == '-')
			frameRate(frameRate - 10);		
		if(key == 'u' || key == 'U') {
			if(scene.isRightHanded()) {
				scene.setLeftHanded();
				println("Left handed set");
			}
			else {
				scene.setRightHanded();
				println("Right handed set");
			}
		}
		println(frameRate);
	}
	//*/
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
