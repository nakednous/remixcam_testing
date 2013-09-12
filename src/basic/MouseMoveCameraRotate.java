package basic;

import processing.core.*;
import processing.event.KeyEvent;
import processing.opengl.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.agent.KeyboardAgent;
import remixlab.dandelion.agent.MouseAgent;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.DOF2Action;
import remixlab.proscene.*;
import remixlab.proscene.Scene.ProsceneKeyboard;
import remixlab.tersehandling.generic.event.GenericDOF2Event;
import remixlab.tersehandling.generic.event.GenericKeyboardEvent;

public class MouseMoveCameraRotate extends PApplet {
	Scene scene;
	MouseMoveAgent agent;

	public void setup() {
		size(640, 360, P3D);
		scene = new Scene(this);
		scene.enableFrustumEquationsUpdate();
		scene.setRadius(150);
		scene.showAll();
		agent = new MouseMoveAgent(scene, "MyMouseAgent");
		scene.terseHandler().unregisterAgent(agent);
		//scene.camera().frame().setDampingFriction(0);
	}

	public void draw() {
		background(0);
		noStroke();
		if (scene.camera().sphereIsVisible(new Vec(0, 0, 0), 40) == Camera.Visibility.SEMIVISIBLE)
			fill(255, 0, 0);
		else
			fill(0, 255, 0);
		sphere(40);
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "basic.MouseMoveCameraRotate" });
	}

	public void keyPressed() {
		if (key != ' ')
			return;
		if (!scene.terseHandler().isAgentRegistered(agent)) {
			scene.terseHandler().registerAgent(agent);
			scene.parent.registerMethod("mouseEvent", agent);
			scene.disableDefaultMouseAgent();
		} else {
			scene.terseHandler().unregisterAgent(agent);
			scene.parent.unregisterMethod("mouseEvent", agent);
			scene.enableDefaultMouseAgent();
		}
	}

	public class MouseMoveAgent extends MouseAgent {
		GenericDOF2Event<DOF2Action> event, prevEvent;

		public MouseMoveAgent(AbstractScene scn, String n) {
			super(scn, n);
			cameraProfile().setBinding(DOF2Action.ROTATE); // -> MouseEvent.MOVE
			cameraProfile().setBinding(TH_LEFT, DOF2Action.TRANSLATE); // ->
																		// MouseEvent.DRAG
		}

		public void mouseEvent(processing.event.MouseEvent e) {
			// don't even necessary :P
			// if( e.getAction() == processing.event.MouseEvent.MOVE ||
			// e.getAction() == processing.event.MouseEvent.DRAG) {
			event = new GenericDOF2Event<DOF2Action>(prevEvent, e.getX()
					- scene.upperLeftCorner.getX(), e.getY()
					- scene.upperLeftCorner.getY(), e.getModifiers(),
					e.getButton());
			handle(event);
			prevEvent = event.get();
			// }
		}
	}

	public class CustomizedKeyboardAgent extends ProsceneKeyboard {

		public CustomizedKeyboardAgent(Scene scene, String n) {
			scene.super(scene, n);
			// TODO Auto-generated constructor stub
		}
	}
}
