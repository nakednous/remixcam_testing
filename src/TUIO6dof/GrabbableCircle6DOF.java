package TUIO6dof;

import processing.core.*;
import remixlab.tersehandling.core.*;
import remixlab.tersehandling.event.TerseEvent;
import remixlab.tersehandling.generic.event.GenericDOF6Event;
import remixlab.tersehandling.generic.profile.Duoable;

public class GrabbableCircle6DOF extends AbstractGrabber {

	public float radiusX, radiusY;
	public PVector center;
	public PVector rotation = new PVector();

	public int colour;
	public int contourColour;
	public int sWeight;

	PApplet parent;
	PGraphics canvas;

	public GrabbableCircle6DOF(PApplet parent, PGraphics canvas, Agent agent) {
		agent.addInPool(this);
		sWeight = 4;
		this.parent = parent;
		this.canvas = canvas;
		contourColour = canvas.color(255, 255, 255);
		setColor();
		setPosition();
	}

	public GrabbableCircle6DOF(PApplet parent, PGraphics canvas, Agent agent,
			PVector c, float r) {
		agent.addInPool(this);
		radiusX = r;
		radiusY = r;
		this.parent = parent;
		this.canvas = canvas;
		center = c;
		setColor();
		sWeight = 4;
	}

	public void setColor() {
		setColor(canvas.color(parent.random(0, 255), parent.random(0, 255),
				parent.random(0, 255)));
	}

	public void setColor(int myC) {
		colour = myC;
	}

	public void setPosition(float x, float y, float z) {
		PApplet.println("z"+z);
		setPositionAndRadii(new PVector(x, y, z), radiusX, radiusY);
	}

	public void setRotation(float rx, float ry, float rz) {
		rotation.x = rx;
		rotation.y = ry;
		rotation.z = rz;
	}

	public void setPositionAndRadii(PVector p, float rx, float ry) {
		center = p;
		radiusX = rx;
		radiusY = ry;
	}

	public void setPosition() {
		float maxRadius = 50;
		float low = maxRadius;
		float highX = canvas.width - maxRadius;
		float highY = canvas.height - maxRadius;
		float r = parent.random(20, maxRadius);
		setPositionAndRadii(
				new PVector(parent.random(low, highX),
						parent.random(low, highY)), r, r);
	}

	public void draw(PGraphics canvas) {
		draw(colour, canvas);
	}

	public void draw(int c, PGraphics canvas) {
		canvas.pushStyle();
		canvas.stroke(contourColour);
		canvas.strokeWeight(sWeight);
		canvas.fill(c);
		// canvas.ellipse(center.x, center.y, 2 * radiusX, 2 * radiusY);
		canvas.pushMatrix();
		canvas.translate(center.x, center.y, center.z);
		canvas.rotateX(rotation.x);
		canvas.rotateY(rotation.y);
		canvas.rotateZ(rotation.z);
		canvas.box(2 * radiusX);

		canvas.popMatrix();
		canvas.popStyle();
	}

	@Override
	public boolean checkIfGrabsInput(TerseEvent event) {
		// if (event instanceof GenericDOF2Event) {
		float x = ((GenericDOF6Event<?>) event).x();
		float y = ((GenericDOF6Event<?>) event).y();
		return (PApplet.pow((x - center.x), 2) / PApplet.pow(radiusX, 2)
				+ PApplet.pow((y - center.y), 2) / PApplet.pow(radiusY, 2) <= 1);
		// }
		// return false;
	}

	@Override
	public void performInteraction(TerseEvent event) {
		if (event instanceof Duoable<?>) {
			switch ((GlobalAction) ((Duoable<?>) event).action()
					.referenceAction()) {
			case CHANGE_COLOR:
				contourColour = canvas.color(parent.random(100, 255),
						parent.random(100, 255), parent.random(100, 255));
				break;
			case CHANGE_STROKE_WEIGHT:
				if (event.isShiftDown()) {
					if (sWeight > 1)
						sWeight--;
				} else
					sWeight++;
				break;
			case CHANGE_POSITION:
				setPosition(((GenericDOF6Event<?>) event).x(),
						((GenericDOF6Event<?>) event).y(),
						((GenericDOF6Event<?>) event).z());
				break;

			case CHANGE_ROTATION:
				setRotation(((GenericDOF6Event<?>) event).rx(),
						((GenericDOF6Event<?>) event).ry(),
						((GenericDOF6Event<?>) event).rz());
				PApplet.println(rotation);
				break;
			case CHANGE_SHAPE:
				radiusX += ((GenericDOF6Event<?>) event).dx();
				radiusY += ((GenericDOF6Event<?>) event).dy();
				break;
			}
		}
	}
}
