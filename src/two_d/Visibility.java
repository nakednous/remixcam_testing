package two_d;

import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;

public class Visibility extends PApplet {
	Scene scene;
	InteractiveFrame iFrame;
	Vec center = new Vec(30, 50);
	Vec corner = new Vec(70, 100);
	//PVector center = new PVector(0, 0);
	float radius = 50;

	public void setup() {
		size(640, 360, P2D);
		//size(640, 360, JAVA2D);
		scene = new Scene(this);
		scene.enableBoundaryEquations();
		//scene.setRightHanded();
		//scene.window().flip();
	}

	public void draw() {
		background(0);
		noStroke();
		
		// /*
		switch (scene.window().ballIsVisible(center, radius)) {
		case VISIBLE:
			fill(255);
			ellipse(center.x(), center.y(), 2*radius, 2*radius);
			break;
		case SEMIVISIBLE:
			fill(120,120,120);
			ellipse(center.x(), center.y(), 2*radius, 2*radius);
			break;
		case INVISIBLE:
			println("invisible");
			break;
		}
		//*/
		
		/*
		noStroke();
		rectMode(CORNERS);
		switch (scene.window().boxIsVisible(center, corner)) {		
		case VISIBLE:
			fill(255);
			rect(center.x(), center.y(), corner.x(), corner.y());
			break;
		case SEMIVISIBLE:
			fill(120,120,120);
			rect(center.x(), center.y(), corner.x(), corner.y());
			break;
		case INVISIBLE:
			println("invisible");
			break;
		}
		//*/
		
		/*
		if(scene.window().pointIsVisible(center))
			fill(255);
		else
			fill(120,120,120);
		ellipse(center.x(), center.y(), 2*radius, 2*radius);
		//*/
		
		/*
		this.strokeWeight(10);
		stroke(255,0,0);
		point(center.x(), center.y());
		*/
		
		/*
		stroke(0,255,0);
		point(center.x(), -center.y());
		stroke(0,0,255);
		point(-center.x(), center.y());
		stroke(255,255,0);
		point(-center.x(), -center.y());
		//*/
		
		/*
		rectMode(CORNERS);
		stroke(0,255,0);
		point(corner.x(), corner.y());
		//*/
	}
	
	public void keyPressed() {
		if(key == 'u')
			scene.eye().frame().scaling().y(scene.eye().frame().scaling().y() * 2);
		if(key == 'U')
			scene.eye().frame().scaling().y(scene.eye().frame().scaling().y() / 2);
		if(key == 'v')
			scene.eye().frame().scaling().x(scene.eye().frame().scaling().x() * 2);
		if(key == 'V')
			scene.eye().frame().scaling().x(scene.eye().frame().scaling().x() / 2);
		if(key == 'x')
			scene.window().flip();
		if(key == 'y') {
			println("distance to left: " + scene.window().distanceToBoundary(0, center));
			println("distance to right: " + scene.window().distanceToBoundary(1, center));
			println("distance to top: " + scene.window().distanceToBoundary(2, center));
			println("distance to bottom: " + scene.window().distanceToBoundary(3, center));
		}
		if(key == 'z') {
			scene.window().setUpVector(new Vec(0,1));
		}
		if(key == 'w') {
			scene.window().setUpVector(new Vec(0,-1));
		}
	}
}