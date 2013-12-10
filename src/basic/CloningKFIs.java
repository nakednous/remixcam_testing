package basic;

import processing.core.*;
import processing.opengl.*;
import remixlab.dandelion.core.Constants.KeyboardAction;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

@SuppressWarnings("serial")
public class CloningKFIs extends PApplet {
	Scene mainScene, auxScene;
	PGraphics canvas, auxCanvas;
	
	boolean mainMouse, auxMouse;

	public void setup() {
		size(640, 720, P3D);

		canvas = createGraphics(640, 360, P3D);
		mainScene = new Scene(this, canvas);
		mainScene.setRadius(mainScene.radius()*1.6f);
		
		mainScene.defaultKeyboardAgent().profile().setShortcut('v', KeyboardAction.CAMERA_KIND);
		// enable computation of the frustum planes equations (disabled by default)
		mainScene.enableFrustumEquationsUpdate();
		mainScene.setGridIsDrawn(false);
		mainScene.addDrawHandler(this, "mainDrawing");
		
		auxCanvas = createGraphics(640, 360, P3D);
		// Note that we pass the upper left corner coordinates where the scene
		// is to be drawn (see drawing code below) to its constructor.
		//auxScene = new Scene(this, auxCanvas, 0, 360);
		auxScene = new Scene(this, auxCanvas, 0, 360);
		auxScene.camera().setType(Camera.Type.ORTHOGRAPHIC);
		auxScene.setAxisIsDrawn(false);
		auxScene.setGridIsDrawn(false);
		auxScene.setRadius(200);
		auxScene.showAll();
		auxScene.addDrawHandler(this, "auxiliarDrawing");

		unregisterMethod("mouseEvent", mainScene.defaultMouseAgent());
		unregisterMethod("mouseEvent", auxScene.defaultMouseAgent());
		unregisterMethod("keyEvent", mainScene.defaultKeyboardAgent());
		unregisterMethod("keyEvent", auxScene.defaultKeyboardAgent());
		handleMouse();
	}

	public void mainDrawing(Scene s) {
		PGraphicsOpenGL p = s.pggl();
		p.background(0);
		p.noStroke();
		// the main viewer camera is used to cull the sphere object against its frustum
		switch (mainScene.camera().sphereIsVisible(new Vec(0, 0, 0), 40)) {
		case VISIBLE:
			p.fill(0, 255, 0);
			p.sphere(40);
			break;
		case SEMIVISIBLE:
			p.fill(255, 0, 0);
			p.sphere(40);
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
		s.drawCamera(mainScene.camera());
		s.pg3d().popStyle();
	}

	public void draw() {
		handleMouse();
		canvas.beginDraw();
		mainScene.beginDraw();
		mainScene.endDraw();
		canvas.endDraw();
		image(canvas, 0, 0);

		auxCanvas.beginDraw();
		auxScene.beginDraw();
		auxScene.endDraw();
		auxCanvas.endDraw();
		// We retrieve the scene upper left coordinates defined above.
		image(auxCanvas, auxScene.upperLeftCorner.x, auxScene.upperLeftCorner.y);
	}
	
	//Agent should be available all the time:
	public void handleMouse() {
		if (mouseY < 360) {
			if(!mainMouse) {
			  registerMethod("mouseEvent", mainScene.defaultMouseAgent());
			  registerMethod("keyEvent", mainScene.defaultKeyboardAgent());			  
			  mainMouse = true;
			  auxMouse = false;
			}			
			unregisterMethod("mouseEvent", auxScene.defaultMouseAgent());
			unregisterMethod("keyEvent", auxScene.defaultKeyboardAgent());
			
		} else {
			if(!auxMouse) {
				registerMethod("mouseEvent", auxScene.defaultMouseAgent());
				registerMethod("keyEvent", auxScene.defaultKeyboardAgent());
				mainMouse = false;
				auxMouse = true;
			}
			unregisterMethod("mouseEvent", mainScene.defaultMouseAgent());
			unregisterMethod("keyEvent", mainScene.defaultKeyboardAgent());
		}
	}
	
	public void keyPressed() {
		if (key == 'x') {
		    KeyFrameInterpolator kfiOriginal = mainScene.camera().keyFrameInterpolator(1);	
		    if(kfiOriginal != null) {
		    	KeyFrameInterpolator kfiNew = kfiOriginal.get();
		    	mainScene.camera().setKeyFrameInterpolator(2, kfiNew);
		    }
		}
		if (key == 'y') {
		    /*
			KeyFrameInterpolator kfiNew = mainScene.camera().keyFrameInterpolator(1).get();		    
		    kfiNew.scene = auxScene;
		    
		    kfiNew.setFrame(auxScene.camera().frame());		    
		    for (int i = 0; i < kfiNew.numberOfKeyFrames(); ++i)
		    	  if(kfiNew.keyFrame(i) instanceof InteractiveFrame)
		    	    ((InteractiveFrame)kfiNew.keyFrame(i)).scene = auxScene;		    
		    auxScene.camera().setKeyFrameInterpolator(1, kfiNew);
		    //*/
			
			KeyFrameInterpolator kfiOrig = mainScene.camera().keyFrameInterpolator(1);
			if(kfiOrig != null)
				auxScene.camera().setKeyFrameInterpolator(1, kfiOrig.get());
			else
				auxScene.camera().deletePath(1);
		}
		if(key=='z') {
			KeyFrameInterpolator kfi;
			kfi = mainScene.camera().keyFrameInterpolator(1);
			if( kfi != null ) 
				println("main scene path 1 no.: " + kfi.numberOfKeyFrames());
			else
				println("main scene path 1 no.: " + 0);
			kfi = auxScene.camera().keyFrameInterpolator(1);
			if( kfi != null ) 
				println("aux scene path 1 no.: " + kfi.numberOfKeyFrames());
			else
				println("aux scene path 1 no.: " + 0);
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.StandardCamera" });
	}
}