package basic;
import processing.core.*;
import processing.opengl.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.DOF2Action;
import remixlab.dandelion.constraint.*;
import remixlab.proscene.*;
import remixlab.tersehandling.core.EventConstants;

@SuppressWarnings("serial")
public class ThirdPerson extends PApplet {
	Scene scene;
	InteractiveAvatarFrame avatar;
	
	public void setup()	{
		size(640, 360, P3D);
		scene = new Scene(this);
		scene.setRadius(400);
		scene.setGridIsDrawn(false);
		scene.setAxisIsDrawn(false);
		// press 'f' to display frame selection hints
		
		avatar = new InteractiveAvatarFrame(scene);
		avatar.setTrackingDistance(300);
		avatar.setAzimuth(PI/12);
		avatar.setInclination(PI/6);
		
		//scene.setAvatar(avatar);
		
		WorldConstraint baseConstraint = new WorldConstraint();
		baseConstraint.setTranslationConstraint(AxisPlaneConstraint.Type.PLANE, new Vec(0.0f,1.0f,0.0f));
		baseConstraint.setRotationConstraint(AxisPlaneConstraint.Type.AXIS, new Vec(0.0f,1.0f,0.0f));
		avatar.setConstraint(baseConstraint);
		
		//scene.setInteractiveFrame(avatar);
		//scene.registerCameraProfile( new ThirdPersonCameraProfile(scene, "THIRD_PERSON") );
		//scene.setCameraMode( Scene.CameraMode.THIRD_PERSON );
	}

	public void draw() {
		background(0);
		// Save the current model view matrix
		pushMatrix();
		// Multiply matrix to get in the frame coordinate system.
		// applyMatrix(scene.interactiveFrame().matrix()) is possible but inefficient 
		//scene.interactiveFrame().applyTransformation(this);//very efficient
		scene.applyTransformation(avatar);
		// Draw an axis using the Scene static function
		scene.drawAxis(20);
		if( scene.avatar() != null )
			fill(255,0,0);
		else
			fill(0,0,255);
		box(15, 20, 30);
		popMatrix();
		
		//draw the ground
		noStroke();
		fill(120, 120, 120);
		beginShape();
		vertex(-400, 10, -400);
		vertex(400, 10, -400);
		vertex(400, 10, 400);
		vertex(-400, 10, 400);
		endShape(CLOSE);
	}	
	
	public void keyPressed() { 
		if(key == 'x') {
			print("avatar up vector: ");
			scene.avatar().upVector().print();
			print("camara up vector b4: ");
			scene.camera().upVector().print();
			scene.camera().setUpVector(scene.avatar().upVector());
			print("camara up vector after: ");
			scene.camera().upVector().print();
		}
		if( key == ' ' )
		if( scene.avatar() == null ) {
			scene.setAvatar(avatar);
			scene.defaultMouseAgent().setAsThirdPerson();
			scene.defaultMouseAgent().setDefaultGrabber(avatar);
			scene.defaultMouseAgent().disableTracking();
		}
		else {
			scene.unsetAvatar(); //simply sets avatar as null
			scene.defaultMouseAgent().setAsArcball();
			scene.defaultMouseAgent().setDefaultGrabber(scene.viewPoint().frame());
			scene.defaultMouseAgent().enableTracking();
		}
	}
		
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "ThirdPerson" });
	}
}
