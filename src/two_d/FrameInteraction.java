package two_d;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.tersehandling.core.EventConstants;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.DOF2Action;
import remixlab.dandelion.geom.*;

public class FrameInteraction extends PApplet {
	Scene scene;
	InteractiveFrame iFrame;
	int w = 640;
	int h = 360;
	float r = 100;
	//String renderer = P2D;
	String renderer = JAVA2D;

	public void setup() {
		size(w,h,renderer);
		//scene = new Java2DScene(this);
		scene = new Scene(this);
		scene.setRadius(r);
		scene.showAll();
		iFrame = new InteractiveFrame(scene);
		// A Scene has a single InteractiveFrame (null by default). We set it
		// here.
		//scene.setInteractiveFrame(new InteractiveFrame(scene));
		//iFrame.translate(new Vec(30, 30));
		// press 'i' to switch the interaction between the camera frame and the
		// interactive frame
		//scene.setShortcut('i', DLKeyboardAction.FOCUS_INTERACTIVE_FRAME);
		// press 'f' to display frame selection hints
		//scene.setShortcut('f', DLKeyboardAction.DRAW_FRAME_SELECTION_HINT);
		scene.setFrameVisualHint(true);
		//scene.defaultMouseAgent().cameraProfile().setBinding(EventConstants.TH_LEFT, DOF2Action.CAD_ROTATE);
	}

	public void draw() {	
		//if (scene.renderer() instanceof RendererJava2D  && scene instanceof Java2DScene) bindM();

		background(0);
		fill(204, 102, 0);
		//rect(0, 0, 55, 55);
		ellipse(0, 0, 2*scene.radius(), 2*scene.radius());
		//ellipse(0, 0, 360, 360);
		// Save the current model view matrix
		pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(iFrame.matrix()) is handy but
		// inefficient
		iFrame.applyTransformation();// optimum
		// Draw an axis using the Scene static function
		scene.drawAxis(40);
		// Draw a second box attached to the interactive frame
		if (iFrame.grabsAgent(scene.defaultMouseAgent())) {
			fill(255, 0, 0);
			rect(0, 0, 35, 35);			
		} /** else if (scene.interactiveFrameIsDrawn()) {
			fill(0, 255, 255);
			rect(0, 0, 35, 35);
		} */ else {
			fill(0, 0, 255);
			rect(0, 0, 30, 30);
		}
		popMatrix();

		scene.beginScreenDrawing(); text("Hello world", 5, 17);
		scene.endScreenDrawing(); //		
	}

	/**
	public void bindM() {		
		scene.window().computeProjectionMatrix();
		scene.window().computeViewMatrix();
		scene.window().computeProjectionViewMatrix();
		
		Vec pos = scene.window().position();
		Orientable quat = scene.window().frame().orientation();

		translate(scene.width() / 2, scene.height() / 2);
		
		if(scene.isRightHanded()) scale(1,-1);
		
		scale(scene.window().frame().inverseMagnitude().x(), 
			  scene.window().frame().inverseMagnitude().y());
		
		rotate(-quat.angle());
		
		translate(-pos.x(), -pos.y());
		
		//if(scene.isRightHanded()) scale(1,-1);
	}
	*/

	/**
	public void keyPressed() {
		if (key == 'x')
			iFrame.scale(-1, 1);
		if (key == 'X')
			scene.window().frame().scale(-1, 1);
		if (key == 'y')
			iFrame.scale(1, -1);
		if (key == 'Y')
			scene.window().frame().scale(1, -1);
		if (key == 'v' || key == 'V') {
			scene.window().flip();
		}

		println("iFrame scaling: " + iFrame.scaling());
		println("iFrame magnitude: " + iFrame.magnitude());

		if (scene.isRightHanded())
			println("Scene is RIGHT handed");
		else
			println("Scene is LEFT handed");
		if (iFrame.isInverted())
			println("iFrame is inverted");
		else
			println("iFrame is NOT inverted");
		if (scene.window().frame().isInverted())
			println("scene.window().frame() is inverted");
		else
			println("scene.window().frame() is NOT inverted");
	}
	// */
	
	public void keyPressed() {
		if(key== '+' || key == '-' ) {
			if(key == '+') scene.setRadius(scene.radius()*1.1f);
			if(key == '-') scene.setRadius(scene.radius()*0.9f);
			println("new scene radius is: " + scene.radius());
			scene.showAll();
		}
		if(key == 'u')
			scene.eye().frame().scaling().y(scene.eye().frame().scaling().y() * 2);
		if(key == 'U')
			scene.eye().frame().scaling().y(scene.eye().frame().scaling().y() / 2);
		if(key == 'v')
			scene.eye().frame().scaling().x(scene.eye().frame().scaling().x() * 2);
		if(key == 'V')
			scene.eye().frame().scaling().x(scene.eye().frame().scaling().x() / 2);
		if(key == 'x') {
			//println(scene.window().frame().magnitude());
			println(scene.eye().frame().scaling().x() + " " + scene.eye().frame().scaling().y());
		}
	}
}
