package flock;

import java.util.ArrayList;

import processing.core.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.constraint.*;
import remixlab.proscene.*;

class Boid {
	PApplet parent;
	Scene scene;
	  InteractiveAvatarFrame frame;
	  Quat q;
	  int grabsMouseColor;//color
	  int avatarColor;

	  // fields
	  PVector pos, vel, acc, ali, coh, sep; // pos, velocity, and acceleration in
	  // a vector datatype
	  float neighborhoodRadius; // radius in which it looks for fellow boids
	  float maxSpeed = 4; // maximum magnitude for the velocity vector
	  float maxSteerForce = .1f; // maximum magnitude of the steering vector
	  float sc = 3; // scale factor for the render of the boid
	  float flap = 0;
	  float t = 0;

	  // constructors
	  Boid(Scene scn, PVector inPos) {
		  scene = scn;
		  parent = scene.pApplet();
	    grabsMouseColor = parent.color(0,0,255);
	    avatarColor = parent.color(255,0,0);		
	    pos = new PVector();
	    pos.set(inPos);
	    frame = new InteractiveAvatarFrame(scene);	
	    frame.setPosition(new Vec(pos.x, pos.y, pos.z));
	    frame.setAzimuth(-PApplet.HALF_PI);
	    frame.setInclination(PApplet.PI*(4/5));
	    frame.setTrackingDistance(scene.radius()/10);
	    vel = new PVector(parent.random(-1, 1), parent.random(-1, 1), parent.random(1, -1));
	    acc = new PVector(0, 0, 0);
	    neighborhoodRadius = 100;
	  }

	  Boid(Scene scn, PVector inPos, PVector inVel, float r) {
		  scene = scn;
		  parent = scene.pApplet();
	    grabsMouseColor = parent.color(0,0,255);
	    avatarColor = parent.color(255,0,0);
	    pos = new PVector();
	    pos.set(inPos);
	    frame = new InteractiveAvatarFrame(scene);
	    frame.setPosition(new Vec(pos.x, pos.y, pos.z));
	    frame.setAzimuth(-PApplet.HALF_PI);
	    frame.setTrackingDistance(scene.radius()/10);
	    vel = new PVector();
	    vel.set(inVel);
	    acc = new PVector(0, 0);
	    neighborhoodRadius = r;
	  }

	  void run(ArrayList bl) {
	    t += .1;
	    flap = 10 * PApplet.sin(t);
	    // acc.add(steer(new PVector(mouseX,mouseY,300),true));
	    // acc.add(new PVector(0,.05,0));
	    if (((Flock)parent).avoidWalls) {
	      acc.add(PVector.mult(avoid(new PVector(pos.x, ((Flock)parent).flockHeight, pos.z), true), 5));
	      acc.add(PVector.mult(avoid(new PVector(pos.x, 0, pos.z), true), 5));
	      acc.add(PVector.mult(avoid(new PVector(((Flock)parent).flockWidth, pos.y, pos.z),	true), 5));
	      acc.add(PVector.mult(avoid(new PVector(0, pos.y, pos.z), true), 5));
	      acc.add(PVector.mult(avoid(new PVector(pos.x, pos.y, 0), true), 5));
	      acc.add(PVector.mult(avoid(new PVector(pos.x, pos.y, ((Flock)parent).flockDepth), true), 5));
	    }
	    flock(bl);
	    move();
	    checkBounds();
	    //render();
	  }

	  // ///-----------behaviors---------------
	  void flock(ArrayList bl) {
	    ali = alignment(bl);
	    coh = cohesion(bl);
	    sep = seperation(bl);
	    acc.add(PVector.mult(ali, 1));
	    acc.add(PVector.mult(coh, 3));
	    acc.add(PVector.mult(sep, 1));
	  }

	  void scatter() {

	  }

	  // //------------------------------------

	  void move() {
	    vel.add(acc); // add acceleration to velocity
	    vel.limit(maxSpeed); // make sure the velocity vector magnitude does not
	    // exceed maxSpeed
	    pos.add(vel); // add velocity to position
	    frame.setPosition(new Vec(pos.x, pos.y, pos.z));
	    acc.mult(0); // reset acceleration
	  }

	  void checkBounds() {
	    if (pos.x > ((Flock)parent).flockWidth)
	      pos.x = 0;
	    if (pos.x < 0)
	      pos.x = ((Flock)parent).flockWidth;
	    if (pos.y > ((Flock)parent).flockHeight)
	      pos.y = 0;
	    if (pos.y < 0)
	      pos.y = ((Flock)parent).flockHeight;
	    if (pos.z > ((Flock)parent).flockDepth)
	      pos.z = 0;
	    if (pos.z < 0)
	      pos.z = ((Flock)parent).flockDepth;
	  }
	  
	  // check if this boid's frame is the avatar
	  boolean isAvatar() {
	    if ( scene.avatar() == null )
	      return false;
	    if ( scene.avatar().equals(frame) )
	      return true;
	    return false;
	  }

	  void render() {
		  parent.pushStyle();
		  parent.stroke(((Flock)parent).hue);
		  parent.noFill();
		  parent.noStroke();
	    parent.fill(((Flock)parent).hue);		

	    q = Quat.multiply(new Quat( new Vec(0,1,0),  PApplet.atan2(-vel.z, vel.x)), 
	                      new Quat( new Vec(0,0,1),  PApplet.asin(vel.y / vel.mag())) );		
	    frame.setRotation(q);

	    parent.pushMatrix();
	    // Multiply matrix to get in the frame coordinate system.
	    frame.applyTransformation();

	    // highlight boids under the mouse
	    if (frame.grabsAgent(scene.defaultMouseAgent())) {
	      parent.fill( grabsMouseColor);
	      // additionally, set the boid's frame as the avatar if the mouse is pressed
	      if (parent.mousePressed == true) 
	        scene.setAvatar(frame);			
	    }
	    
	    // highlight the boid if its frame is the avatar
	    if ( isAvatar() ) {
	      parent.fill( avatarColor );
	    }

	    //draw boid
	    parent.beginShape(PApplet.TRIANGLES);
	    parent.vertex(3 * sc, 0, 0);
	    parent.vertex(-3 * sc, 2 * sc, 0);
	    parent.vertex(-3 * sc, -2 * sc, 0);

	    parent.vertex(3 * sc, 0, 0);
	    parent.vertex(-3 * sc, 2 * sc, 0);
	    parent.vertex(-3 * sc, 0, 2 * sc);

	    parent.vertex(3 * sc, 0, 0);
	    parent.vertex(-3 * sc, 0, 2 * sc);
	    parent.vertex(-3 * sc, -2 * sc, 0);

	    parent.vertex(-3 * sc, 0, 2 * sc);
	    parent.vertex(-3 * sc, 2 * sc, 0);
	    parent.vertex(-3 * sc, -2 * sc, 0);
	    parent. endShape();		

	    parent.popMatrix();

	    parent.popStyle();		
	  }

	  // steering. If arrival==true, the boid slows to meet the target. Credit to
	  // Craig Reynolds
	  PVector steer(PVector target, boolean arrival) {
	    PVector steer = new PVector(); // creates vector for steering
	    if (!arrival) {
	      steer.set(PVector.sub(target, pos)); // steering vector points
	      // towards target (switch
	      // target and pos for
	      // avoiding)
	      steer.limit(maxSteerForce); // limits the steering force to
	      // maxSteerForce
	    } 
	    else {
	      PVector targetOffset = PVector.sub(target, pos);
	      float distance = targetOffset.mag();
	      float rampedSpeed = maxSpeed * (distance / 100);
	      float clippedSpeed = PApplet.min(rampedSpeed, maxSpeed);
	      PVector desiredVelocity = PVector.mult(targetOffset,
	      (clippedSpeed / distance));
	      steer.set(PVector.sub(desiredVelocity, vel));
	    }
	    return steer;
	  }

	  // avoid. If weight == true avoidance vector is larger the closer the boid
	  // is to the target
	  PVector avoid(PVector target, boolean weight) {
	    PVector steer = new PVector(); // creates vector for steering
	    steer.set(PVector.sub(pos, target)); // steering vector points away from
	    // target
	    if (weight)
	      steer.mult(1 / PApplet.sq(PVector.dist(pos, target)));
	    // steer.limit(maxSteerForce); //limits the steering force to
	    // maxSteerForce
	    return steer;
	  }

	  PVector seperation(ArrayList boids) {
	    PVector posSum = new PVector(0, 0, 0);
	    PVector repulse;
	    for (int i = 0; i < boids.size(); i++) {
	      Boid b = (Boid) boids.get(i);
	      float d = PVector.dist(pos, b.pos);
	      if (d > 0 && d <= neighborhoodRadius) {
	        repulse = PVector.sub(pos, b.pos);
	        repulse.normalize();
	        repulse.div(d);
	        posSum.add(repulse);
	      }
	    }
	    return posSum;
	  }

	  PVector alignment(ArrayList boids) {
	    PVector velSum = new PVector(0, 0, 0);
	    int count = 0;
	    for (int i = 0; i < boids.size(); i++) {
	      Boid b = (Boid) boids.get(i);
	      float d = PVector.dist(pos, b.pos);
	      if (d > 0 && d <= neighborhoodRadius) {
	        velSum.add(b.vel);
	        count++;
	      }
	    }
	    if (count > 0) {
	      velSum.div((float) count);
	      velSum.limit(maxSteerForce);
	    }
	    return velSum;
	  }

	  PVector cohesion(ArrayList boids) {
	    PVector posSum = new PVector(0, 0, 0);
	    PVector steer = new PVector(0, 0, 0);
	    int count = 0;
	    for (int i = 0; i < boids.size(); i++) {
	      Boid b = (Boid) boids.get(i);
	      float d = PApplet.dist(pos.x, pos.y, b.pos.x, b.pos.y);
	      if (d > 0 && d <= neighborhoodRadius) {
	        posSum.add(b.pos);
	        count++;
	      }
	    }
	    if (count > 0) {
	      posSum.div((float) count);
	    }
	    steer = PVector.sub(posSum, pos);
	    steer.limit(maxSteerForce);
	    return steer;
	  }
	}