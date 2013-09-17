package p5flock;

import processing.core.*;

public class Flocking extends PApplet {
	Flock flock;

	public void setup() {
	  size(640, 360);
	  flock = new Flock();
	  // Add an initial set of boids into the system
	  for (int i = 0; i < 150; i++) {
	    flock.addBoid(new Boid(this,width/2,height/2));
	  }
	}

	public void draw() {
	  background(50);
	  flock.run();
	}

	// Add a new boid into the System
	public void mousePressed() {
	  flock.addBoid(new Boid(this,mouseX,mouseY));
	}
}
