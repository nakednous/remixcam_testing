package basic;

import processing.core.*;
import remixlab.dandelion.core.Constants.KeyboardAction;
import remixlab.dandelion.core.KeyFrameInterpolator;
import remixlab.proscene.*;
import remixlab.proscene.Scene.ProsceneKeyboard;

public class Basic extends PApplet {
	private static final long serialVersionUID = 1L;
	
	Scene scene;

	public void setup() {
	  size(640, 360, P3D);
	  scene = new Scene(this);
	  //scene.setJavaTimers();
	  scene.showAll();
	  scene.camera().frame().setDampingFriction(0);
	  frameRate(100);
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
