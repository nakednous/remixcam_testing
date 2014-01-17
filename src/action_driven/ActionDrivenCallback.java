package action_driven;

import processing.core.*;
import remixlab.tersehandling.core.*;
import remixlab.tersehandling.generic.agent.*;
import remixlab.tersehandling.generic.event.*;
import remixlab.tersehandling.generic.profile.*;
import remixlab.tersehandling.event.*;

public class ActionDrivenCallback extends PApplet {
	public class MouseAgent
			extends
			GenericMotionAgent<GenericMotionProfile<MotionAction>, GenericClickProfile<ClickAction>>
			implements EventConstants {
		GenericDOF2Event<MotionAction> event, prevEvent;

		public MouseAgent(TerseHandler scn, String n) {
			super(new GenericMotionProfile<MotionAction>(),
				  new GenericClickProfile<ClickAction>(), scn, n);
			// default bindings
			clickProfile().setClickBinding(TH_LEFT, 1, ClickAction.CHANGE_COLOR);
			clickProfile().setClickBinding(TH_META, TH_RIGHT, 1, ClickAction.CHANGE_STROKE_WEIGHT);
			clickProfile().setClickBinding((TH_META | TH_SHIFT), TH_RIGHT, 1, ClickAction.CHANGE_STROKE_WEIGHT);
			profile().setBinding(TH_LEFT, MotionAction.CHANGE_POSITION);
			profile().setBinding(TH_CENTER, MotionAction.CHANGE_SHAPE);
			profile().setBinding(TH_META, TH_RIGHT, MotionAction.CHANGE_SHAPE);
		}

		/**
		// 1st Choice: as it's done in the tutorial. Requires reflection
		public void mouseEvent(processing.event.MouseEvent e) {
			if (e.getAction() == processing.event.MouseEvent.MOVE) {
				event = new GenericDOF2Event<MotionAction>(prevEvent, e.getX(),	e.getY(), e.getModifiers(), e.getButton());
				updateGrabber(event);
				prevEvent = event.get();
			}
			if (e.getAction() == processing.event.MouseEvent.DRAG) {
				event = new GenericDOF2Event<MotionAction>(prevEvent, e.getX(),	e.getY(), e.getModifiers(), e.getButton());
				handle(event);
				prevEvent = event.get();
			}
			if (e.getAction() == processing.event.MouseEvent.CLICK) {
				handle(new GenericClickEvent<ClickAction>(e.getX(), e.getY(), e.getModifiers(), e.getButton(), e.getCount()));
			}
		}
		// */
		
		/**
		// 2nd Choice: not that simple
		@Override
		public GenericDOF2Event<MotionAction> feed() {
			return new GenericDOF2Event<MotionAction>(mouseX, mouseY, TH_NOMODIFIER_MASK, TH_NOBUTTON);
		}
		// */
		
	}

	public class GrabbableCircle extends AbstractGrabber {
		public float radiusX, radiusY;
		public PVector center;
		public int colour;
		public int contourColour;
		public int sWeight;

		public GrabbableCircle(Agent agent) {
			agent.addInPool(this);
			setColor();
			setPosition();
			sWeight = 4;
			contourColour = color(0, 0, 0);
		}

		public GrabbableCircle(Agent agent, PVector c, float r) {
			agent.addInPool(this);
			radiusX = r;
			radiusY = r;
			center = c;
			setColor();
			sWeight = 4;
		}

		public void setColor() {
			setColor(color(random(0, 255), random(0, 255), random(0, 255)));
		}

		public void setColor(int myC) {
			colour = myC;
		}

		public void setPosition(float x, float y) {
			setPositionAndRadii(new PVector(x, y), radiusX, radiusY);
		}

		public void setPositionAndRadii(PVector p, float rx, float ry) {
			center = p;
			radiusX = rx;
			radiusY = ry;
		}

		public void setPosition() {
			float maxRadius = 50;
			float low = maxRadius;
			float highX = w - maxRadius;
			float highY = h - maxRadius;
			float r = random(20, maxRadius);
			setPositionAndRadii(
					new PVector(random(low, highX), random(low, highY)), r, r);
		}

		public void draw() {
			draw(colour);
		}

		public void draw(int c) {
			pushStyle();
			stroke(contourColour);
			strokeWeight(sWeight);
			fill(c);
			ellipse(center.x, center.y, 2 * radiusX, 2 * radiusY);
			popStyle();
		}

		@Override
		public boolean checkIfGrabsInput(TerseEvent event) {
			if (event instanceof GenericDOF2Event) {
				float x = ((GenericDOF2Event<?>) event).x();
				float y = ((GenericDOF2Event<?>) event).y();
				return (pow((x - center.x), 2) / pow(radiusX, 2)
						+ pow((y - center.y), 2) / pow(radiusY, 2) <= 1);
			}
			return false;
		}

		@Override
		public void performInteraction(TerseEvent event) {
			if (event instanceof Duoable) {
				switch ((GlobalAction) ((Duoable<?>) event).action()
						.referenceAction()) {
				case CHANGE_COLOR:
					contourColour = color(random(100, 255), random(100, 255),
							random(100, 255));
					break;
				case CHANGE_STROKE_WEIGHT:
					if (event.isShiftDown()) {
						if (sWeight > 1)
							sWeight--;
					} else
						sWeight++;
					break;
				case CHANGE_POSITION:
					setPosition(((GenericDOF2Event<?>) event).x(),
							((GenericDOF2Event<?>) event).y());
					break;
				case CHANGE_SHAPE:
					radiusX += ((GenericDOF2Event<?>) event).x();
					radiusY += ((GenericDOF2Event<?>) event).y();
					break;
				}
			}
		}
	}

	int w = 600;
	int h = 600;
	MouseAgent agent;
	TerseHandler terseHandler;
	GrabbableCircle[] circles;
	
	GenericDOF2Event<MotionAction> event, prevEvent;

	public void setup() {
		size(w, h);
		terseHandler = new TerseHandler();
		agent = new MouseAgent(terseHandler, "my_mouse");
		//1st choice above
		//registerMethod("mouseEvent", agent);
		circles = new GrabbableCircle[50];
		for (int i = 0; i < circles.length; i++)
			circles[i] = new GrabbableCircle(agent);
	}

	public void draw() {
		background(255);
		for (int i = 0; i < circles.length; i++) {
			if (circles[i].grabsAgent(agent))
				circles[i].draw(color(255, 0, 0));
			else
				circles[i].draw();
		}
		terseHandler.handle();
	}
	
	//3rd choice from here
	@Override
	public void mouseMoved() {
		event = new GenericDOF2Event<MotionAction>(prevEvent, mouseX, mouseY, EventConstants.TH_NOMODIFIER_MASK, EventConstants.TH_NOBUTTON);
		agent.updateGrabber(event);
		prevEvent = event.get();	
	}
	
	@Override
	public void mouseDragged() {
		event = new GenericDOF2Event<MotionAction>(prevEvent, mouseX, mouseY, EventConstants.TH_NOMODIFIER_MASK, EventConstants.TH_LEFT);
		agent.handle(event);
		prevEvent = event.get();	
	}
	
	@Override
	public void mouseClicked() {
		agent.handle(new GenericClickEvent<ClickAction>(mouseX, mouseY, EventConstants.TH_NOMODIFIER_MASK, EventConstants.TH_RIGHT, 1));	
	}
}
