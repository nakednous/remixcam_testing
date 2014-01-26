package basic;

import processing.core.*;
import processing.opengl.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.DOF2Action;
import remixlab.dandelion.core.Constants.WheelAction;
import remixlab.proscene.*;
import remixlab.dandelion.geom.*;

@SuppressWarnings("serial")
public class StdCamera extends PApplet {
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;
	StandardCamera cam;

	public void setup() {
		size(640, 720, P3D);

		canvas = createGraphics(640, 360, P3D);
		scene = new Scene(this, canvas);
		
		cam = new StandardCamera(scene);
		scene.camera(cam);
		
		//scene.camera().setType(Camera.Type.ORTHOGRAPHIC);
		scene.setRadius(200);
		scene.showAll();
		
		// enable computation of the frustum planes equations (disabled by default)
		scene.enableBoundaryEquations();
		scene.setGridVisualHint(false);
		scene.addDrawHandler(this, "mainDrawing");
		
		auxCanvas = createGraphics(640, 360, P3D);
		// Note that we pass the upper left corner coordinates where the scene
		// is to be drawn (see drawing code below) to its constructor.
		auxScene = new Scene(this, auxCanvas, 0, 360);
		auxScene.camera().setType(Camera.Type.ORTHOGRAPHIC);
		auxScene.setAxisVisualHint(false);
		auxScene.setGridVisualHint(false);
		auxScene.setRadius(400);
		auxScene.showAll();
		auxScene.addDrawHandler(this, "auxiliarDrawing");

		handleMouse();
	}

	public void mainDrawing(Scene s) {
		PGraphicsOpenGL p = s.pggl();
		p.background(0);
		p.noStroke();
		// the main viewer camera is used to cull the sphere object against its frustum
		switch (scene.camera().ballIsVisible(new Vec(0, 0, 0), scene.radius()*0.6f)) {
		case VISIBLE:
			p.fill(0, 255, 0);
			p.sphere(scene.radius()*0.6f);
			break;
		case SEMIVISIBLE:
			p.fill(255, 0, 0);
			p.sphere(scene.radius()*0.6f);
			break;
		case INVISIBLE:
			break;
		}
	}

	public void auxiliarDrawing(Scene s) {
		mainDrawing(s);		
		s.pg3d().pushStyle();
		s.pg3d().stroke(255,255,0);
		s.pg3d().fill(255,255,0,160);
		s.drawEye(scene.camera());
		s.pg3d().popStyle();
	}

	public void draw() {
		handleMouse();
		canvas.beginDraw();
		scene.beginDraw();
		scene.endDraw();
		canvas.endDraw();
		image(canvas, 0, 0);

		auxCanvas.beginDraw();
		auxScene.beginDraw();
		auxScene.endDraw();
		auxCanvas.endDraw();
		// We retrieve the scene upper left coordinates defined above.
		image(auxCanvas, auxScene.upperLeftCorner.x(), auxScene.upperLeftCorner.y());
	}
	
	public void handleMouse() {
		if (mouseY < 360) {
			scene.enableDefaultMouseAgent();
			scene.enableDefaultKeyboardAgent();
			auxScene.disableDefaultMouseAgent();
			auxScene.disableDefaultKeyboardAgent();
		} else {
			scene.disableDefaultMouseAgent();
			scene.disableDefaultKeyboardAgent();
			auxScene.enableDefaultMouseAgent();
			auxScene.enableDefaultKeyboardAgent();
		}
	}
	
	public void keyPressed() {
		if(key == 't') {
			cam.toggleMode();
			this.redraw();
		}
		if( key == 'u' )
			scene.defaultMouseAgent().cameraWheelProfile().setBinding(WheelAction.ZOOM);
		if( key == 'v' )
			scene.defaultMouseAgent().cameraWheelProfile().setBinding(WheelAction.SCALE);
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.StdCamera" });
	}
	
	public class StandardCamera extends Camera {
		float orthoSize;
		boolean standard;
		
	    public StandardCamera(AbstractScene scn) {
	    	super(scn);
	    	standard = false;
	    	orthoSize = 1;
	    }
		
	    public void toggleMode() {
	    	standard = !standard;
	    }
	    
	    public boolean isStandard() {
	    	return standard;
	    }
	    
		@Override
		public float zNear() { 
		  if (standard) 
		    return 0.001f; 
		  else 
		    return super.zNear(); 
		}

		@Override
		public float zFar() {
		  if (standard) 
		    return 1000.0f; 
		  else 
		    return super.zFar();
		}
		
		@Override
		public float rescalingOrthoFactor() {
			if(isStandard())
				return 1.0f;
			return super.rescalingOrthoFactor();
		}
	}
}
