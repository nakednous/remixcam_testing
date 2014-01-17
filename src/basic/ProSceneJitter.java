package basic;

import processing.core.PApplet;
import processing.core.PGraphics;
import remixlab.dandelion.geom.Vec;
import remixlab.proscene.Scene;

@SuppressWarnings("serial")
public class ProSceneJitter extends PApplet {

	PGraphics pg;
	Scene scene;
	int numShapes = 500;
	int dim = 1500;

	@Override
	public void setup() {
		size(1280, 720, P3D);
		pg = createGraphics(width, height, P3D);
		scene = new Scene(this, pg);
		scene.setRadius(10000);
		scene.setGridVisualHint(false);
		scene.setAxisVisualHint(false);
		scene.camera().setPosition(new Vec(0, 0, dim + 250));
		scene.defaultKeyboardAgent().profile().setShortcut('s', null);
		//scene.disableDefaultKeyboardAgent();
	}

	@Override
	public void draw() {
		// without these the problem arises, but it takes longer
		// with these the problem arises almost immediately
		scene.enableDefaultMouseAgent();
		scene.enableDefaultKeyboardAgent();

		pg.beginDraw();
		scene.beginDraw();

		pg.background(0);
		pg.noFill();
		pg.stroke(255);
		pg.box(dim);
		pg.sphereDetail(1);
		pg.noFill();
		float fc = frameCount * 0.001f;
		for (int i = 0; i < numShapes; i++) {
			pg.pushMatrix();
			pg.stroke(i % 255, 255 - i % 255, i % 125);
			float x = (noise(i + 1 + fc) - 0.5f) * dim;
			float y = (noise(i + 4 + fc) - 0.5f) * dim;
			float z = (noise(i + 9 + fc) - 0.5f) * dim;
			pg.line(0, 0, 0, x, y, z);
			pg.translate(x, y, z);
			pg.sphere(25);
			pg.popMatrix();
		}

		scene.endDraw();
		pg.endDraw();

		image(pg, 0, 0);

		//frame.setTitle(round(frameRate) + " fps"); // frameRate is fine
	}
	
	public void keyPressed() {
		if(key == 'x') println(frameRate);
		if(key == 'y') scene.switchTimers();
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "ProSceneJitter" });
	}
}
