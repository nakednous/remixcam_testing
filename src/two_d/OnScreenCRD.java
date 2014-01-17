package two_d;

import processing.core.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.constraint.*;
import remixlab.proscene.*;
import controlP5.*;

@SuppressWarnings("serial")
public class OnScreenCRD extends PApplet {	
	// Define scene dimension here
	int w = 640;
	int h = 480;

	// define your navigator position using screen coordinates
	//int screenX = w * 3 / 4;
	//int screenY = h / 4;
	
	int screenX = w / 2;
	int screenY = h / 2;
	//this is a depth value ranging in [0..1] (near and far plane respectively).
	//play with it according to your needs
	float screenZ = 0.15f;

	// Define navigator size in pixels here:
	float boxLenght = 310;
	float boxLenghtRatio;

	boolean rotateRespectToWorld = false;
	boolean drawFrameSelectionHints = false;
	boolean applied = false;

	Scene scene;
	InteractiveFrame iFrame;
	InteractiveFrame planeIFrame;
	Vec slide = new Vec();
	Vec tranSlide = new Vec();
	ControlP5 controlP5;

	int sliderValue = 0;
	int oldSliderValue = 0;

	public void setup() {
		size(w, h, P2D);
		//size(w, h, JAVA2D);
		scene = new Scene(this);
		scene.showAll();
		scene.setAxisVisualHint(false);

		iFrame = new InteractiveFrame(scene);
		iFrame.setReferenceFrame(scene.window().frame());

		LocalConstraint constraint = new LocalConstraint();
		constraint.setTranslationConstraintType(AxisPlaneConstraint.Type.FORBIDDEN);
		iFrame.setConstraint(constraint);
		
		controlP5 = new ControlP5(this);
		controlP5.addSlider("sliderValue", -100, 100, sliderValue, 10, 50, 100, 10);
		controlP5.setAutoDraw(false);
		
		// Needs testing: disabling it gives better results in my setup. See:
		// 1. http://code.google.com/p/proscene/issues/detail?id=7
		// 2. http://processing.org/reference/hint_.html
		//hint(DISABLE_STROKE_PERSPECTIVE);

		// throws a null when running as an applet from within eclipse
		// this.frame.setResizable(true);
	}

	public void draw() {
		background(55);
		fill(204, 102, 0);
		
		//testing
		/*
		stroke(255,255,0);
		Vec p1 = scene.center();
		Vec p2 = Vec.add(scene.center(), Vec.mult(scene.window().upVector(), 230 * scene.window().pixelSceneRatio(scene.center())));	  
		line(p1.x(), p1.y(), p2.x(), p2.y());
		*/

		// first level: draw respect to the camera frame
		pushMatrix();
		scene.window().frame().applyTransformation();
		// second level: draw respect to the iFrame
		pushMatrix();
		iFrame.applyTransformation();
		//WARNING, new. need to cancel camera scaling:
		scene.scale(1/scene.window().frame().scaling().x(), 1/scene.window().frame().scaling().y());
		scene.drawAxis(boxLenghtRatio);
		popMatrix();
		popMatrix();				

		/*
		pushMatrix();
		planeIFrame.applyTransformation(scene);
		scene.drawAxis(40);
		rectMode(CENTER);
		rect(0, 0, 200, 200);
		popMatrix();
		*/			

		//the important bit here (!) is the order of the operations:
		//1. Draw the frame selection hints		
		//if(drawFrameSelectionHints) scene.drawSelectionHints();

		// /*
		//2. Disable the z-buffer (it's done in beginScreenDrawing)
		scene.beginScreenDrawing();
		//3. Then draw the gui: it should be drawn on top of the 3D scene
		//Think if the painter's algorithm
		controlP5.draw();
		scene.endScreenDrawing();
		// */			

		parameteriseNavigator();
	}

	/**
	 * This procedure defines the parameterization. Note that the navigator
	 * should *always* be visible since the third parameter passed to
	 * unprojectedCoordinatesOf is 0.5, i.e., just half way between zNear and
	 * zFar Please refer to camera.unprojectedCoordinatesOf.
	 */
	public void parameteriseNavigator() {
		iFrame.setPosition(scene.window().unprojectedCoordinatesOf(new Vec(screenX, screenY, screenZ)));
		this.boxLenghtRatio = boxLenght	* scene.window().pixelSceneRatio(iFrame.position());
	}	

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "pmpm.OnScreenCRD" });
	}
}