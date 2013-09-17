package fpsanimation;
import geom.Particle2D;
import processing.core.*;
import processing.opengl.*;
import remixlab.fpstiming.*;
//import remixlab.proscene.*;

public class Animation2D extends PApplet {	
	ParticleSystem system;
	TimingHandler handler;

	public void setup() {
		//size(640, 360, P2D);
		size(640, 360, JAVA2D);
		// /**
		system = new ParticleSystem(this);
		handler = new TimingHandler(system);
		system.startAnimation();
		//*/
		/**
		handler = new TimingHandler();
		system = new ParticleSystem(this, handler);
		//*/
		smooth();
	}
	
	public void draw() {
		background(0);
		pushStyle();
		pushMatrix();
		translate(width/2, height/2);
		strokeWeight(3); // Default
		beginShape(POINTS);
		for (int i = 0; i < system.particle.length; i++) {
			system.particle[i].draw();
		}
		endShape();
		popMatrix();
		popStyle();
		handler.handle();
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "Animation" });
	}
	
	public void keyPressed() {
		if((key == 'x') || (key == 'X'))
			system.setAnimationPeriod(system.animationPeriod()-2);
		if((key == 'y') || (key == 'Y'))
			system.setAnimationPeriod(system.animationPeriod()+2);
	}

	class ParticleSystem extends AnimatedObject {
		int nbPart;
		Particle2D[] particle;
		PApplet parent;

		// We need to call super(p) to instantiate the base class
		public ParticleSystem(PApplet p, TimingHandler handler) {
			super(handler);
			parent = p;
			nbPart = 2000;
			particle = new Particle2D[nbPart];
			for (int i = 0; i < particle.length; i++)
			    particle[i] = new Particle2D(parent);
			startAnimation();
		}
		
		public ParticleSystem(PApplet p) {
			parent = p;
			nbPart = 2000;
			particle = new Particle2D[nbPart];
			for (int i = 0; i < particle.length; i++)
			    particle[i] = new Particle2D(parent);
		}

		// Initialization stuff could have also been performed at
		// setup(), once after the Scene object have been instantiated

		// Define here your animation.
		@Override
		public void animate() {
			for (int i = 0; i < nbPart; i++)
				if(particle[i] != null)
					particle[i].animate();
		}
	}
}