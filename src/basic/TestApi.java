package basic;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.constraint.*;
import geom.Box;
import geom.Sphere;

public class TestApi extends PApplet {
	Scene scene;
	InteractiveFrame f1, f2, f3, f4, f5;
	Frame f6 = new Frame();
	Vec v, p;
	Vec res;

	Box box1, box5;
	Point screenPoint = new Point();
	Camera.WorldPoint wp;
	Vec orig = new Vec();
	Vec dir = new Vec();
	Vec end = new Vec();
	
	public void setup() {
		size(840, 360, P3D);
		
		//frameRate(20);
		scene = new Scene(this);
		
		//scene.setSingleThreadedTimers();
		//scene.switchTimers();
		
		scene.setRadius(500);
		
		scene.setFrameVisualHint(true);
		v = new Vec(20,30,40);
		p = new Vec(40,30,20);
		
		f1 = new InteractiveFrame(scene);
		//f1 = new TestIFrame(scene);
		f1.translate(20, 30, 60);
		//f1.rotate(new Quat(new Vec(1,0,0), HALF_PI));
		f1.scale(2, 1.7f, -2.3f);
		//f1.scale(1, 1, -1);
		//f1.removeFromMouseGrabberPool();
		
		box1 = new Box(scene, f1);
		
		f2 = new InteractiveFrame(scene);
		//f2 = new TestIFrame(scene);
		f2.setReferenceFrame(f1);
		f2.translate(30, 20, -30);
		//f2.rotate(new Quat(new Vec(0,1,0), -QUARTER_PI));
		f2.scale(-1.2f, 1.1f, 0.8f);
		//f2.scale(-1, 1, 1);
		//f2.removeFromMouseGrabberPool();
		
		f3 = new InteractiveFrame(scene);
		//f3 = new TestIFrame(scene);
		f3.setReferenceFrame(f1);
		f3.translate(15, 20, -30);
		f3.rotate(new Quat(new Vec(0,1,0), -HALF_PI));		
		f3.scale(1, -1.3f, 1.2f);
		//f3.scale(1, -1, 1);
		//f3.removeFromMouseGrabberPool();
		
		f4 = new InteractiveFrame(scene);
		//f4 = new TestIFrame(scene);
		f4.setReferenceFrame(f2);
		f4.translate(20, 15, 30);
		f4.rotate(new Quat(new Vec(0,1,0), QUARTER_PI));
		f4.scale(-1.3f, -0.9f, 0.8f);
		//f4.scale(-1, -1, 1);
		//f4.removeFromMouseGrabberPool();
		
		f5 = new InteractiveFrame(scene);
		//f5 = new TestIFrame(scene);
		f5.setReferenceFrame(f4);
		f5.translate(20, 15, 30);
		f5.rotate(new Quat(new Vec(0,1,0), QUARTER_PI));
		f5.scale(-1.3f, -0.9f, 0.8f);
		//f5.scale(-1, -1, 1);
		//f5.removeFromMouseGrabberPool();
		
		box5 = new Box(scene, f5);
		
		scene.setRadius(200);
		scene.showAll();
		
		// press 'f' to display frame selection hints
		//scene.setShortcut('f', remixlab.remixcam.core.AbstractScene.KeyboardAction.DRAW_FRAME_SELECTION_HINT);	
		
		//scene.camera().frame().setSpinningSensitivity(3);
		//scene.camera().frame().setSpinningFriction(0.5f);
		scene.camera().frame().setRotationSensitivity(1.3f);
		//scene.camera().frame().setSpinningFriction(0.5f);
		//scene.camera().frame().setSpinningFriction(0.5f);
		//scene.camera().frame().setTossingFriction(0.6f);
		
		frameRate(200);
	}
	
	public void draw() {
		background(0);
		
		//scene.camera().frame().updateFlyUpVector();		
		//drawPrimitives(color(255));
		
		drawLine();
		drawBoxes();
	    
		/**
		pushMatrix();		
		  scene.applyTransformation(f1);
		  scene.drawAxis(40);
		  drawPrimitives(color(255,0,0));
		  pushMatrix();
		    scene.applyTransformation(f2);
		    scene.drawAxis(40);
		    drawPrimitives(color(0,255,0));		        		    
		  popMatrix();
		popMatrix();
		// */
		
		// /**
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
		      pushMatrix();
		        scene.applyTransformation(f5);
		        scene.drawAxis(40);
		        drawPrimitives(color(255,0,255));
		      popMatrix();	      
		    popMatrix();    		    
		  popMatrix();
		popMatrix();
		// */
		
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
				
		/**
		// f4 -> f3
		res = f4.coordinatesOfIn(p, f3);
		drawArrow(f3, res);
		// */
		
		/**
		v = new Vec(50,50,50);		 
		drawArrow(v);
		res = f1.transformOfNoScl(v);
		//res.print();
		drawArrow(f1, res);
		// */
		
		/**
		res = f2.transformOf(v);
		drawArrow(f2, res);
		// */
		
		/**
		res = f2.transformOfFrom(res, f1);
		drawArrow(f2, res);
		// */
	}
	
	public void drawBoxes() {
		box1.draw();
		box5.draw();
	}
	
	public void drawLine() {		
		if(wp != null)
		if(wp.found) {
			pushStyle();
			strokeWeight(5);
			this.stroke(255, 0, 0);
			this.point(wp.point.x(), wp.point.y(), wp.point.z());
			
			this.stroke(0, 0, 255);		
			this.line(orig.x(), orig.y(), orig.z(), end.x(), end.y(), end.z());
			//this.line(orig.x(), orig.y(), orig.z(), orig.x(), orig.y(), orig.z());
			popStyle();
		}
	}
	
	public void drawPrimitives(int color) {
		pushStyle();
		stroke(255,255,0);
		//line(0,0,0, v.x(), v.y(), v.z());
		popStyle();
		pushStyle();
		stroke(color);
		strokeWeight(5);
		point(p.x(), p.y(), p.z());
		popStyle();
	}
	
	public void drawArrow(Vec vec) {
		drawArrow(null, vec);
	}
	
	public void drawArrow(Frame frame, Vec vec) {		
		if(frame != null) {
			pushMatrix();
			// Multiply matrix to get in the frame coordinate system.
			//scene.applyMatrix(frame.matrix());// local, is handy but inefficient
			//scene.applyMatrix(frame.worldMatrix());// world, is handy but inefficient 
			//scene.applyTransformation(frame);
			scene.applyWorldTransformation(frame);
			pushStyle();
			stroke(0,255,255);
			line(0,0,0, vec.x(), vec.y(), vec.z());
			popStyle();
			popMatrix();
		}
		else {
			pushStyle();
			stroke(0,255,255);
			line(0,0,0, vec.x(), vec.y(), vec.z());
			popStyle();
		}
	}
	
	public void mouseClicked() {		
		wp =  scene.camera().pointUnderPixel(new Point(mouseX, mouseY));
		if( wp.found ) {
			screenPoint.set(mouseX, mouseY);
			scene.camera().convertClickToLine(screenPoint, orig, dir);				
			end = Vec.add(orig, Vec.multiply(dir, 1000.0f));
			orig.print();
			dir.print();
			end.print();
		}
	}
	
	public float distanceToSC1() {		
		return Math.abs((scene.camera().frame().coordinatesOf(scene.camera().sceneCenter())).vec[2]);//before scln		
	}
	
	public float distanceToSC2() {				
		Vec zCam = scene.camera().frame().zAxis();		
		zCam.normalize();
		Vec cam2SceneCenter = Vec.subtract(scene.camera().position(), scene.camera().sceneCenter());
		return Math.abs(Vec.dot(cam2SceneCenter, zCam));
	}
	
	public float distanceToSC3() {	
		Vec zCam = scene.camera().frame().magnitude().z() > 0 ? scene.camera().frame().zAxis() : scene.camera().frame().zAxis(false);
		zCam.normalize();
		Vec cam2SceneCenter = Vec.subtract(scene.camera().position(), scene.camera().sceneCenter());
		return Math.abs(Vec.dot(cam2SceneCenter, zCam));
	}

	public float distanceToARP1() {
		return Math.abs(scene.camera().cameraCoordinatesOf(scene.camera().frame().arcballReferencePoint()).vec[2]);//before scln		
	}
	
	public float distanceToARP2() {
		Vec zCam = scene.camera().frame().zAxis();	
		zCam.normalize();
		Vec cam2arp = Vec.subtract(scene.camera().position(), scene.camera().arcballReferencePoint());
		return Math.abs(Vec.dot(cam2arp, zCam));
	}
	
	public float distanceToARP3() {
		Vec zCam = scene.camera().frame().magnitude().z() > 0 ? scene.camera().frame().zAxis() : scene.camera().frame().zAxis(false);
		zCam.normalize();
		Vec cam2arp = Vec.subtract(scene.camera().position(), scene.camera().arcballReferencePoint());
		return Math.abs(Vec.dot(cam2arp, zCam));
	}
	
	public void keyPressed() {
		if(key == 'u' || key == 'U') {
			//Vec v = f4.zAxis();
			//println("f4.zAxis(): " + v + " mag: " + v.mag());			
			Mat m = f5.matrix();
			println("f5 matrix is: ");
			m.print();
			f6.fromMatrix(m, f5.scaling());
			println("f6 matrix is: ");
			f6.matrix().print();
		}
		/*
		if(key == 'v' || key == 'V') {
			if(scene.isRightHanded())
				scene.setLeftHanded();			
			else
				scene.setRightHanded();			
		}
		*/
		//TODO hoe to settint scal
		if(key == 'u')
			scene.eye().frame().scaling().y(scene.eye().frame().scaling().y() * 2);
		if(key == 'v')
			scene.eye().frame().scaling().y(scene.eye().frame().scaling().y() / 2);
		if(key == 'x') {
			scene.camera().frame().setScaling(-scene.camera().frame().scaling().x(),
					                           scene.camera().frame().scaling().y(),
					                           scene.camera().frame().scaling().z());
            println("scaling by -x");
		}
		if(key == 'X') {
			scene.camera().frame().setScaling(2*scene.camera().frame().scaling().x(),
                                              scene.camera().frame().scaling().y(),
                                              scene.camera().frame().scaling().z());                                              
			println("scaling by 2x");
		}
		if(key == 'y') {
			scene.camera().frame().setScaling( scene.camera().frame().scaling().x(),
					                          -scene.camera().frame().scaling().y(),
					                           scene.camera().frame().scaling().z());
            println("scaling by -y");
		}
		if(key == 'Y') {
			scene.camera().frame().setScaling(scene.camera().frame().scaling().x(),
											  2*scene.camera().frame().scaling().y(),
                                              scene.camera().frame().scaling().z());                                              
			println("scaling by 2y");
		}
		if(key == 'z') {
			scene.camera().frame().setScaling( scene.camera().frame().scaling().x(),
					                           scene.camera().frame().scaling().y(),
					                          -scene.camera().frame().scaling().z());
            println("scaling by -z");
		}
		if(key == 'Z') {
			scene.camera().frame().setScaling(scene.camera().frame().scaling().x(),
                                              scene.camera().frame().scaling().y(),
                                              2*scene.camera().frame().scaling().z());                                              
			println("scaling by 2z");
		}		
		if(key == 'q' || key == 'Q') {
			/**
			f1.setScaling( f1.scaling().x(),
					-f1.scaling().y(),
					f1.scaling().z()
					);*/
			scene.camera().lookAt(scene.center());
		}
		
		if(key == 'm' || key == 'M') {
			println("distanceToSC1(): " + distanceToSC1());
			println("distanceToSC2(): " + distanceToSC2());
			println("distanceToSC3(): " + distanceToSC3());
		}
		
		if(scene.isRightHanded())
			println("Scene is RIGHT handed");
		else
			println("Scene is LEFT handed");
		println("zNear: " + scene.camera().zNear());
		println("zFar: " + scene.camera().zFar());
		println("cam mag: " + scene.camera().frame().magnitude());
	}
}
