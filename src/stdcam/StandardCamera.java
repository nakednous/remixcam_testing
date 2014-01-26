package stdcam;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.proscene.Scene.ProsceneKeyboard;
import remixlab.proscene.Scene.ProsceneMouse;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

// TODO fix me, add draw handler
public class StandardCamera extends PApplet {
	Scene scene, auxScene;
	PGraphics canvas, auxCanvas;
	
	ProsceneMouse mouse, auxMouse;
	ProsceneKeyboard keyboard, auxKeyboard;

	public void setup() {
	  size(640, 720, P3D);

	  canvas = createGraphics(640, 360, P3D);
	  scene = new Scene(this, (PGraphicsOpenGL) canvas);
	  // enable computation of the frustum planes equations (disabled by default)
	  scene.enableBoundaryEquations();
	  scene.setGridVisualHint(false);
	  scene.addDrawHandler(this, "mainDrawing");

	  auxCanvas = createGraphics(640, 360, P3D);
	  // Note that we pass the upper left corner coordinates where the scene
	  // is to be drawn (see drawing code below) to its constructor.
	  auxScene = new Scene(this, (PGraphicsOpenGL) auxCanvas, 0, 360);
	  auxScene.camera().setType(Camera.Type.ORTHOGRAPHIC);
	  auxScene.setAxisVisualHint(false);
	  auxScene.setGridVisualHint(false);
	  auxScene.setRadius(200);
	  auxScene.showAll();
	  auxScene.addDrawHandler(this, "auxiliarDrawing");
	  
	  mouse = (ProsceneMouse)scene.terseHandler().agent("proscene_mouse");
	  keyboard = (ProsceneKeyboard)scene.terseHandler().agent("proscene_keyboard");
	  auxMouse = (ProsceneMouse)scene.terseHandler().agent("proscene_mouse");
	  auxKeyboard = (ProsceneKeyboard)scene.terseHandler().agent("proscene_keyboard");

	  handleMouse();
	}

	public void mainDrawing(Scene s) {		
	  PGraphicsOpenGL p = s.pggl();
	  p.background(0);
	  p.noStroke();
	  // the main viewer camera is used to cull the sphere object against its
	  // frustum
	  switch (scene.camera().ballIsVisible(new Vec(0, 0, 0), 40)) {
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
	  s.pg().pushStyle();
	  s.pg().fill(0,255,255);
	  s.pg().stroke(0,255,255);
	  s.drawEye(scene.camera());
	  s.pg().popStyle();
	}

	public void draw() {
		background(0);
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
			scene.terseHandler().registerAgent(mouse);
			scene.terseHandler().registerAgent(keyboard);			
			auxScene.terseHandler().unregisterAgent(auxMouse);
			auxScene.terseHandler().unregisterAgent(auxKeyboard);
		} else {
			scene.terseHandler().unregisterAgent(mouse);
			scene.terseHandler().unregisterAgent(keyboard);			
			auxScene.terseHandler().registerAgent(auxMouse);
			auxScene.terseHandler().registerAgent(auxKeyboard);
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "stdcam.StandardCamera" });
	}
}
