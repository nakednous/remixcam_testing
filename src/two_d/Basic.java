package two_d;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.dandelion.geom.*;

@SuppressWarnings("serial")
public class Basic extends PApplet {	
	Scene scene;
	PFont font;
	float angle;	

	public void setup() {
		size(640, 360, JAVA2D);
		//size(640, 360, P2D);
		//size(360, 640, P2D);
		// /**
		font = createFont("Arial", 16);
		textFont(font, 16);
		// */
		scene = new Scene(this);
		
		//scene.window().flip();
		//scene.camera().centerScene();
		//scene.showAll();	
		
		Quat q = new Quat();
		println("axis: " + q.axis()					
		          + " angle: " + q.angle() );		
		
		Vec from = new Vec(-1,1);
		Vec to = new Vec(-1,-1);
		
		//scene.viewpoint().frame().scaling().y(scene.viewpoint().frame().scaling().y() * 2);
	}	

	public void draw() {
		background(150);
		ellipse(0, 0, 40, 40);		
		
		// /**
		scene.beginScreenDrawing();
		text("Hello world", 5, 17);
		scene.endScreenDrawing();
		// */
		
		rect(50, 50, 30, 30);
		
		/**
		scene.beginScreenDrawing();
		pushStyle();
		stroke(255, 255, 255);
		strokeWeight(2);
		noFill();
		beginShape();
		vertex(30, 20);
		vertex(85, 20);
		vertex(85, 75);
		vertex(30, 75);
		endShape(CLOSE);
		popStyle();
		scene.endScreenDrawing();
		// */		
	}
	
	public void keyPressed() {
		if(key == 'u' || key == 'U') {
			//scene.window().interpolateToZoomOnPixel(new Point(mouseX, mouseY));			
			println("scale factor: " );
			scene.window().frame().scaling().print();
			float[] wh = scene.window().getBoundaryWidthHeight();
			println("halfWidth: " + wh[0]);
			println("halfHeight: " + wh[1]);
			println("screenWidth: " + scene.window().screenWidth() );
			println("screenHeight: " + scene.window().screenHeight() );			
		}
		if(key == 'x' || key == 'X') {
			Vec v = scene.eye().projectedCoordinatesOf(new Vec(0,0,0));
			println(v);
		}
		if(key == 'v' || key == 'V') {
			Vec v = scene.eye().unprojectedCoordinatesOf(new Vec(width/2,height/2,0.5f));
			println(v);
		}
		if(key == 'z' || key == 'Z') {
			//scene.window().fitCircle(new Vec(0,0), 20);
			scene.window().fitBall(new Vec(65,65), 15);
		}
		if(key == 'y' || key == 'Y') {
			scene.window().flip();			
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.Basic" });
	}
}
