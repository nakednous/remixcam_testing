package basic_geom;
import geom.Box;
import processing.core.*;
import processing.opengl.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class ScreenDrawing extends PApplet {
	Scene scene;
	Box [] boxes;
	Point[] points;
	int LINE_ITEMS = 500;
	int index;
	
	public void setup()	{
		size(640, 360, P3D);
		scene = new Scene(this);
		scene.setRadius(150);
		scene.showAll();
		index = 0;
		points = new Point [LINE_ITEMS];		
		boxes = new Box[50];
		for (int i = 0; i < boxes.length; i++)
			boxes[i] = new Box(scene);
	}

	public void draw() {	
		background(0);
		for (int i = 0; i < boxes.length; i++)			
			boxes[i].draw();
		
		//2D drawing
		scene.beginScreenDrawing();
		pushStyle();
		strokeWeight(8);
		stroke(183,67,158,127);
		noFill();
		beginShape();		
		for (int i=0; i<index; i++) {			
			vertex(points[i].x(), points[i].y(), 0);
		}
		endShape();
		popStyle();		
		scene.endScreenDrawing();
	}
	

	/**
	 //TODO pending but really easy :)
	public void mouseDragged() {
		if(!scene.mouseIsHandled()) {			
			if (index < LINE_ITEMS ) {
				points[index] = new Point (mouseX, mouseY);
				index++;
			} else
				index = 0;			
		}
	}
	*/
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "ScreenDrawing" });
	}
}
