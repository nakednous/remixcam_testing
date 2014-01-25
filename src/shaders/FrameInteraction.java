package shaders;
import processing.core.*;
import processing.opengl.PShader;
import remixlab.proscene.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.KeyboardAction;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.helper.MatrixStackHelper;

@SuppressWarnings("serial")
public class FrameInteraction extends PApplet {
	Scene scene;
	boolean focusIFrame;
	InteractiveFrame iFrame;
	PShader prosceneShader;
	Mat pmv;
	MatrixStackHelper helper;
	PMatrix3D pmatrix = new PMatrix3D( );
	boolean prosceneShaders = true;
	
	public void setup()	{
		size(640, 360, P3D);
		prosceneShader = loadShader("FrameFrag.glsl", "FrameVert_pmv.glsl");
		scene = new Scene(this);
		helper = new MatrixStackHelper(scene);
		if(prosceneShaders)
			scene.setMatrixHelper(new MatrixStackHelper(scene));
		iFrame = new InteractiveFrame(scene);
		iFrame.translate(new Vec(30, 30, 0));
		iFrame.setDampingFriction(0);
		scene.camera().frame().setDampingFriction(0);
		noLights();
	}

	public void draw() {
		background(0);
		
		if(prosceneShaders) {
			resetMatrix();
			setProsceneUniforms();
		}
		
		fill(204, 102, 0);
		box(20, 30, 40);
		
		// /**
		// Save the current model view matrix
		scene.pushModelView();
		// Multiply matrix to get in the frame coordinate system.
		scene.applyModelView(iFrame.matrix());// is possible but inefficient 
		//iFrame.applyTransformation();//very efficient		
		if(prosceneShaders) setProsceneUniforms();
		
		// Draw an axis using the Scene static function
		scene.drawAxis(20);
		
		// Draw a second box
		if (focusIFrame) {
			fill(0, 255, 255);
			box(12, 17, 22);
		}
		else if (iFrame.grabsAgent(scene.defaultMouseAgent())) {
			fill(255, 0, 0);
			box(12, 17, 22);
		}
		else {
			fill(0,0,255);
			box(10, 15, 20);
		}	
		
		scene.popModelView();
	}
	
	public void setProsceneUniforms() {
		resetShader();
		shader(prosceneShader);
		
		// /**
		//Funciona sin transponer las matrices:
		pmv = Mat.multiply(scene.projection(), scene.modelView());
		pmatrix.set(pmv.get(new float[16]));
		prosceneShader.set("proscene_transform", pmatrix);
		// */
		
		/**
		//las matrices de proscene son column major order, entonce intente:
		pmv = Mat.mult(scene.getProjection(), scene.getModelView());
		//traspone:
		pmatrix = Scene.toPMatrix(pmv);
		prosceneShader.set("proscene_transform", pmatrix);
		// */
	}
	
	public void keyPressed() {
		if( key == 'i') {
			if( focusIFrame ) {
				scene.defaultMouseAgent().setDefaultGrabber(scene.eye().frame());
				scene.defaultMouseAgent().enableTracking();
			} else {
				scene.defaultMouseAgent().setDefaultGrabber(iFrame);
				scene.defaultMouseAgent().disableTracking();
			}
			focusIFrame = !focusIFrame;
		}
		if( key == 't') {
			scene.switchTimers();
		}
		if( key == 'u' || key == 'U' ) {
			println("P5 modelview is");
			((PMatrix3D)scene.pg3d().getMatrix()).print();
			println("Proscene modelview is");
			helper.modelView().print();
			println("P5 projection is");
			scene.pg3d().projection.print();
			println("Proscene projection is");
			helper.projection().print();
		}
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "shaders.FrameInteraction" });
	}
}
