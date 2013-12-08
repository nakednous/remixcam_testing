package grabbers.camerainterpolation;

import grabbers.button.Button2D;

import java.util.ArrayList;

import processing.core.*;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.geom.Vec;

public class CameraInterpolation extends PApplet {
	Scene scene;
	ArrayList buttons;
	int h;
	int fSize = 16;

	public void setup() {
		size(640, 360, P3D);
		scene = new Scene(this);

		// create a camera path and add some key frames:
		// key frames can be added at runtime with keys [j..n]
		scene.viewPoint().setPosition(new Vec(80, 0, 0));
		scene.viewPoint().lookAt(scene.viewPoint().sceneCenter());
		scene.viewPoint().addKeyFrameToPath(1);

		scene.viewPoint().setPosition(new Vec(30, 30, -80));
		scene.viewPoint().lookAt(scene.viewPoint().sceneCenter());
		scene.viewPoint().addKeyFrameToPath(1);

		scene.viewPoint().setPosition(new Vec(-30, -30, -80));
		scene.viewPoint().lookAt(scene.viewPoint().sceneCenter());
		scene.viewPoint().addKeyFrameToPath(1);

		scene.viewPoint().setPosition(new Vec(-80, 0, 0));
		scene.viewPoint().lookAt(scene.viewPoint().sceneCenter());
		scene.viewPoint().addKeyFrameToPath(1);

		// re-position the camera:
		scene.viewPoint().setPosition(new Vec(0, 0, 1));
		scene.viewPoint().lookAt(scene.viewPoint().sceneCenter());
		scene.showAll();

		// drawing of camera paths are toggled with key 'r'.
		scene.setViewPointPathsAreDrawn(true);

		buttons = new ArrayList(6);
		for (int i = 0; i < 5; ++i)
			buttons.add(null);

		Button2D button = new ClickButton(scene, new PVector(10, 5), 0);
		h = button.myHeight;
		buttons.set(0, button);
	}

	public void draw() {
		background(0);
		fill(204, 102, 0);
		box(20, 30, 50);

		updateButtons();
		displayButtons();
	}

	void updateButtons() {
		for (int i = 1; i < buttons.size(); i++) {
			// Check if CameraPathPlayer is still valid
			if ((buttons.get(i) != null)
					&& (scene.viewPoint().keyFrameInterpolator(i) == null))
				buttons.set(i, null);
			// Or add it if needed
			if ((scene.viewPoint().keyFrameInterpolator(i) != null)
					&& (buttons.get(i) == null))
				buttons.set(i, new ClickButton(scene, new PVector(10, +(i)
						* (h + 7)), i));
		}
	}

	void displayButtons() {
		for (int i = 0; i < buttons.size(); i++) {
			Button2D button = (Button2D) buttons.get(i);
			if (button != null)
				button.display();
		}
	}
	
	public void keyPressed() {
		if( key == 't' || key == 'T' ) {
			scene.switchTimers();
		}
		if(key == '+')
			frameRate(frameRate + 10);
		if(key == '-')
			frameRate(frameRate - 10);		
		if(key == 'u' || key == 'U') {
			if(scene.isRightHanded()) {
				scene.setLeftHanded();
				println("Left handed set");
			}
			else {
				scene.setRightHanded();
				println("Right handed set");
			}
		}
		println(frameRate);
	}
}
