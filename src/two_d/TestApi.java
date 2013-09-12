package two_d;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.constraint.*;

public class TestApi extends PApplet {
	Scene scene;
	InteractiveFrame f1, f2, f3, f4;
	Vec v, p;
	Vec res;
	
	public void setup() {
		size(640, 360, P2D);
		//size(640, 360, JAVA2D);		
		scene = new Scene(this);
		v = new Vec(20,30);
		p = new Vec(40,30);
		
		f1 = new InteractiveFrame(scene);
		f1.translate(20, 30);
		f1.rotate(new Rot(HALF_PI));
		//f1.scale(2f, 2.5f);
		f1.scale(1.2f, 2.5f);
		
		f2 = new InteractiveFrame(scene);
		f2.setReferenceFrame(f1);
		f2.translate(30, 20);
		f2.rotate(new Rot(-QUARTER_PI));
		//f2.scale(1.2f, 1.1f);
		f2.scale(-0.8f, 1.4f);
		
		f3 = new InteractiveFrame(scene);
		f3.setReferenceFrame(f1);
		f3.translate(15, 20);
		f3.rotate(new Rot(-HALF_PI));
		//f3.scale(1, 1.5f);
		f3.scale(-1.5f, -1f);
		
		f4 = new InteractiveFrame(scene);
		f4.setReferenceFrame(f2);
		f4.translate(20, 15);
		f4.rotate(new Rot(QUARTER_PI));
		//f4.scale(1.4f, 0.5f);
		f4.scale(1.4f, -0.5f);
		
		scene.setRadius(200);
		scene.showAll();
		
		// press 'f' to display frame selection hints
		//scene.setShortcut('f', DLKeyboardAction.DRAW_FRAME_SELECTION_HINT);
	}
	
	public void draw() {
		background(0);
		drawPrimitives(color(255));
		
		pushMatrix();		
		  scene.applyTransformation(f1);
		  scene.drawAxis(40);
		  drawPrimitives(color(255,0,0));		  
		  pushMatrix();		  
		    scene.applyTransformation(f3);
		    scene.drawAxis(40);
		    drawPrimitives(color(0,0,255));
		  popMatrix();		  
		  pushMatrix();
		    scene.applyTransformation(f2);
		    scene.drawAxis(40);
		    drawPrimitives(color(0,255,0));		    
		    pushMatrix();
		      scene.applyTransformation(f4);
		      scene.drawAxis(40);
		      drawPrimitives(color(125));
		    popMatrix();		    		    
		  popMatrix();		  
		popMatrix();
		
		//testing
		
		/**
		// f2 -> world
		res = f2.inverseCoordinatesOf(p);
		drawArrow(res);
		// */
		
		/**
		// f2 -> f1
		res = f1.coordinatesOfFrom(p, f2);
		drawArrow(f1, res);
		// */
		
		/**
		//same as the prev one
		// f2 -> f1
		res = f2.coordinatesOfIn(p, f1);
		drawArrow(f1, res);
		// */
		
		/**
		// Ref. Frame(f1) -> f2
		res = f2.localCoordinatesOf(p);
		drawArrow(f2, res);
		// */
		
	    /**
		// f3 -> f4
		res = f4.coordinatesOfFrom(p, f3);
		drawArrow(f4, res);
		// */		
		
		// /**
		// f4 -> f3
		res = f4.coordinatesOfIn(p, f3);
		drawArrow(f3, res);
		// */
	}	
	
	public void drawPrimitives(int color) {
		pushStyle();
		stroke(255,255,0);
		//line(0, 0, v.x(), v.y());
		popStyle();
		pushStyle();
		stroke(color);
		strokeWeight(5);
		point(p.x(), p.y());
		popStyle();
	}
	
	public void drawArrow(Vec vec) {
		drawArrow(null, vec);
	}
	
	public void drawArrow(RefFrame frame, Vec vec) {		
		if(frame != null) {
			pushMatrix();
			// Multiply matrix to get in the frame coordinate system.
			//scene.applyMatrix(frame.matrix());// local, is handy but inefficient
			//scene.applyMatrix(frame.worldMatrix());// world, is handy but inefficient 
			//scene.applyTransformation(frame);
			scene.applyWorldTransformation(frame);
			pushStyle();
			stroke(0,255,255);
			line(0, 0, vec.x(), vec.y());
			popStyle();
			popMatrix();
		}
		else {
			pushStyle();
			stroke(0,255,255);
			line(0, 0, vec.x(), vec.y());
			popStyle();
		}
	}
	
	public void keyPressed() { 
		if (key == 'x')
			f1.scale(-1, 1);
		if (key == 'X')
			scene.window().frame().scale(-1, 1);
		if (key == 'y')
			f1.scale(1, -1);
		if (key == 'Y')
			scene.window().frame().scale(1, -1);
		
		if (key == 'v' || key == 'V') {
			scene.window().flip();
		}
	}
}
