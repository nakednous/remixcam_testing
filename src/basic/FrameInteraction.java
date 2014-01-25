package basic;
import processing.core.*;
import remixlab.proscene.*;
import remixlab.tersehandling.core.EventConstants;
import remixlab.tersehandling.core.Util;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.DOF2Action;
import remixlab.dandelion.core.Constants.WheelAction;
import remixlab.dandelion.geom.*;

@SuppressWarnings("serial")
public class FrameInteraction extends PApplet {
	Scene scene;
	boolean focusIFrame;
	InteractiveFrame iFrame;
	
	public void setup()	{
		//size(360, 640, P3D);
		size(640, 360, P3D);		
		scene = new Scene(this);
		scene.setVisualHints(Constants.AXIS | /*Constants.GRID |*/ Constants.PATHS | Constants.FRAME);
		//scene.setRadius(1000);
		iFrame = new InteractiveFrame(scene);
		iFrame.translate(new Vec(30, 30, 0));
		iFrame.setDampingFriction(0);
		scene.camera().frame().setDampingFriction(0);
		noLights();
		println(scene.camera().frame().scaling().x() + " " + scene.camera().frame().scaling().y());
		//scene.view().frame().scaling().y(scene.view().frame().scaling().y() * 2);
		//scene.defaultMotionAgent().frameProfile().setBinding(EventConstants.TH_LEFT, DOF2Action.CUSTOM);
	}

	public void draw() {
		background(0);
		fill(204, 102, 0);
		box(20, 30, 40);	
		//sphere(scene.radius());
		
		// Save the current model view matrix
		pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		applyMatrix(Scene.toPMatrix(iFrame.matrix()));// is possible but inefficient 
		//iFrame.applyTransformation();//very efficient
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
		if(key == '2')
			scene.camera().setFieldOfView((float) Math.PI / 2.0f);
		if(key == '3')
			scene.camera().setFieldOfView((float) Math.PI / 3.0f);
		if(key == '4')
			scene.camera().setFieldOfView((float) Math.PI / 4.0f);
		if( key == 'i') {
			if( focusIFrame ) {
				scene.defaultMouseAgent().setDefaultGrabber(scene.eye().frame());
				scene.defaultMouseAgent().enableTracking();
			} else {
				scene.defaultMouseAgent().setDefaultGrabber(iFrame);
				scene.defaultMouseAgent().disableTracking();
			}
			focusIFrame = !focusIFrame;
		}
		if(key == 'j')
			scene.defaultMouseAgent().cameraWheelProfile().setBinding(WheelAction.ZOOM);
		if(key == 'k')
			scene.defaultMouseAgent().cameraWheelProfile().setBinding(WheelAction.SCALE);
		if( key == 't') {			
			println(scene.camera().distanceToSceneCenter());
			//println(scene.camera().frame().scaling().y() + " " + scene.camera().orthoCoef);
		}
		//TODO weirdly enough this doesn't work:
		/*
		if(key == 'u')
			scene.view().frame().scaling().y(scene.view().frame().scaling().y() * 2);
		if(key == 'U')
			scene.view().frame().scaling().y(scene.view().frame().scaling().y() / 2);
		if(key == 'v')
			scene.view().frame().scaling().x(scene.view().frame().scaling().x() * 2);
		if(key == 'V')
			scene.view().frame().scaling().x(scene.view().frame().scaling().x() / 2);
		*/
		if(key == 'u')
			scene.eye().frame().setScaling(scene.eye().frame().scaling().x(),
					                             scene.eye().frame().scaling().y() * 2,
					                             scene.eye().frame().scaling().z());
		if(key == 'U')
			scene.eye().frame().setScaling(scene.eye().frame().scaling().x(),
                    							 scene.eye().frame().scaling().y() / 2,
                    							 scene.eye().frame().scaling().z());
		if(key == 'v')
			scene.eye().frame().setScaling(scene.eye().frame().scaling().x() * 2,
					 							 scene.eye().frame().scaling().y(),
					 							 scene.eye().frame().scaling().z());
		if(key == 'V')
			scene.eye().frame().setScaling(scene.eye().frame().scaling().x() / 2,
				                             	 scene.eye().frame().scaling().y(),
				                             	 scene.eye().frame().scaling().z());
		if(key== '+' || key == '-' ) {
			if(key == '+') scene.setRadius(scene.radius()*1.1f);
			if(key == '-') scene.setRadius(scene.radius()*0.9f);
			println("new scene radius is: " + scene.radius());
			scene.showAll();
		}
		if(key == 'y')
			scene.camera().setArcballReferencePoint(new Vec(50,50,50));
		if(key == 'Y')
			scene.camera().setArcballReferencePoint(new Vec(0,0,0));
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.FrameInteraction" });
	}
}
