package KinectProsceneSelection;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;
import remixlab.proscene.Scene.ProsceneKeyboard;
import remixlab.proscene.Scene.ProsceneMouse;
import remixlab.tersehandling.core.*;
import remixlab.tersehandling.generic.event.*;
import remixlab.dandelion.geom.*;
import remixlab.dandelion.agent.*;
import remixlab.dandelion.core.*;
import remixlab.dandelion.core.Constants.*;
import geom.Box;

public class KinectProsceneSelection extends PApplet {
	Scene scene;
	HIDAgent agent;
	Kinect kinect;
	PVector kinectPos, kinectRot;
	Box[] boxes;

	public void setup() {
		size(800, 600, P3D);
		scene = new Scene(this);
		kinect = new Kinect(this);

		scene.camera().setPosition(new Vec(250, 250, 250));
		scene.camera().lookAt(new Vec(0, 0, 0));

		agent = new HIDAgent(scene, "Kinect") {
			GenericDOF6Event<Constants.DOF6Action> currEvent;

			@Override
			public GenericDOF6Event<Constants.DOF6Action> feed() {
				currEvent = new GenericDOF6Event<Constants.DOF6Action>(
						kinectPos.x, kinectPos.y, kinectPos.z, kinectRot.x,
						kinectRot.y, kinectRot.z);
				if (kinect.isSelection()) {
					updateGrabber(new GenericDOF6Event<Constants.DOF6Action>(kinect.posit.x, kinect.posit.y, kinect.posit.z, kinectRot.x, kinectRot.y, kinectRot.z));
					//return null;
				}
				return currEvent;
			}
		};
		agent.enableTracking();
		agent.setSensitivities(0.2f, 0.2f, 0.2f, 0.0002f, 0.0002f, 0.0002f);

		boxes = new Box[30];
		for (int i = 0; i < boxes.length; i++) {
			boxes[i] = new Box(scene);
			agent.addInPool(boxes[i].iFrame);
		}
	}

	public void draw() {
		background(0);

		for (int i = 0; i < boxes.length; i++)
			boxes[i].draw();

		// Update the Kinect data
		kinect.update();

		kinect.draw();

		// Get the translation and rotation vectors from Kinect
		kinectPos = kinect.deltaPositionVector();
		kinectRot = kinect.rotationVector();

	}

	/****************************************************************************/
	/******************************* KINECT EVENTS ******************************/
	/****************************************************************************/
	// SimpleOpenNI events
	void onNewUser(SimpleOpenNI curContext, int userId) {
		kinect.onNewUser(curContext, userId);
	}
	
	public class Hand {
		  PVector point;
		  int col;
		  /****************************************************************************/
		  /******************************* CONSTRUCTORS *******************************/
		  /****************************************************************************/
		  public Hand(){
		    point=new PVector(0,0,0);
		  }
		  public Hand(int inColor){
		    point=new PVector(0,0,0);
		    col=inColor;
		  }
		  /****************************************************************************/
		  /**************************** GETTERS AND SETTERS ***************************/
		  /****************************************************************************/
		  /**
		   * Return the point
		   * @return PVector Current Hand position
		   * */
		  public PVector getPoint(){
		    return point;
		  }
		  /**
		   * Set the point
		   * @param PVector inPoint Position vector
		   * */
		  public void setPoint(PVector inPoint){
		    point=inPoint;
		  }
		  /****************************************************************************/
		  /********************************** METHODS *********************************/
		  /****************************************************************************/
		  /**
		   * Draw the hand point
		   * */
		   public void draw(){
		     fill(col);
		     float maxZAllowed=1800;
		     float minZAllowed=1000;
		     float maxRadius=15;
		     float minRadius=5;
		     float rad=abs((maxRadius-((point.z-minZAllowed)*((maxRadius-minRadius)/(maxZAllowed-minZAllowed)))));
		     ellipse(point.x, point.y,rad,rad);
		   }
		}
	
	public class Kinect {
		  SimpleOpenNI  context;    //Context for the Kinect handler
		  Hand left,right;          // Hands position
		  PVector posit;            // The vector with position values calculated with hands positions
		  PVector rotat;            // The vector with rotation values calculated with hands positions
		  PVector initial;          // Initial position, set when two hands are detected
		  boolean initialDefined;   // True if the initial vector was defined
		  float selThreshold=90;    // Threshold to select objects
		  boolean processSelGesture = false;
		  
		  /****************************************************************************/
		  /******************************* CONSTRUCTORS *******************************/
		  /****************************************************************************/
		  /**
		   * Kinect object constructor
		   * @param PApplet p PApplet object
		   * */
		  public Kinect(PApplet p) {
		    //Kinect init
		    context = new SimpleOpenNI(p);
		     if (context.isInit() == false){
		       println("ERROR PARSING KINECT: Check if the camera is connected.");
		     }else{
		       // disable mirror
		       context.setMirror(true);
		       
		       // enable depthMap generation 
		       context.enableDepth();
		         
		       // enable skeleton generation for all joints
		       context.enableUser();
		       
		       // Initialize the hands vectors
		       left = new Hand(color(0, 0, 255));
		       right = new Hand(color(0, 0, 255));
		       //Initialize the movement vectors
		       posit=rotat=new PVector(0,0,0);
		       
		       // Initialize the initial vector
		       initialDefined=false;
		       initial=new PVector(0,0,0);
		       
		       //Update for the first time the hands to define the starting vector
		       update();
		     }
		  }
		  /****************************************************************************/
		  /**************************** GETTERS AND SETTERS ***************************/
		  /****************************************************************************/
		  
		  /****************************************************************************/
		  /********************************** METHODS *********************************/
		  /****************************************************************************/
		  /**
		   * Update the Kinect context and the hand positions
		   * */
		   public void update(){
		     //update the camera context
		     context.update();
		     //Update the hands position
		     updateHands();
		     //If the hands are detected by the first time, set the initial vector positions
		     if(isDetectingHands()&&!initialDefined){
		       initial=positionVector();
		       initialDefined=true;
		     }
		   }
		  /**
		   * Update Draw the hands position in the screen
		   * */
		  public void draw(){
		    drawScreenElements();
		  }
		  
		  void drawScreenElements(){
		    scene.beginScreenDrawing();
		      pushStyle();
		        //Draw the depth image
		        image(context.userImage(),0,0,width/3,height/3);
		        left.draw();
		        right.draw();
		        // Draw the position vector
		        fill(color(255,140,0));
		        ellipse(posit.x,posit.y,10,10);
		      popStyle();
		    scene.endScreenDrawing(); 
		  }
		  
		  /**
		   * Return the vector of position calculated using the position of hands
		   * @param PVector Position vector
		   * */
		  public PVector positionVector(){
		    posit=new PVector(0,0,0);
		    if(isActiveUser()){
		      posit.x=((left.getPoint().x+right.getPoint().x)/2);
		      posit.y=((left.getPoint().y+right.getPoint().y)/2);
		      posit.z=((left.getPoint().z+right.getPoint().z)/2);
		    }
		    return posit;
		  }
		  /**
		   * Return the vector of diferences between the initial position and the actual position
		   * @param PVector Position vector
		   * */
		  public PVector deltaPositionVector(){
		    PVector delta=new PVector(0,0,0);
		    PVector posit=positionVector();
		    if(isActiveUser()){
		      delta.x=initial.x-posit.x;
		      delta.y=initial.y-posit.y;
		      delta.z=initial.z-posit.z;
		    }
		    return delta;
		  }
		  /**
		   * Return the vector of rotations calculated using the position of hands
		   * @param PVector Rotation vector
		   * */
		  public PVector rotationVector(){
		    rotat=new PVector(0,0,0);
		    if(isActiveUser()){
		      //TODO: Define a gesture to x-rotation rotation.x=(left.getPoint().x-right.getPoint().x);
		      rotat.x=0;
		      rotat.y=-(left.getPoint().z-right.getPoint().z);
		      rotat.z=-(left.getPoint().y-right.getPoint().y);
		    }
		    return rotat;
		  }
		    
		  /**
		   * Update the hands position with the screen values (projective coordinates)
		   * */
		  private void updateHands(){
		    if(isActiveUser()){
		      int[] userList = context.getUsers();
		      if(context.isTrackingSkeleton(userList[0])){
		        PVector leftPoint=new PVector();
		        PVector rightPoint=new PVector();
		        context.getJointPositionSkeleton(userList[0],SimpleOpenNI.SKEL_LEFT_HAND,leftPoint);
		        context.getJointPositionSkeleton(userList[0],SimpleOpenNI.SKEL_RIGHT_HAND,rightPoint);
		        left.setPoint(getScreen(leftPoint));
		        right.setPoint(getScreen(rightPoint));
		      }
		    }
		  }
		  /**
		   * Return the point with World coordinates in screen (projective) coordinates
		   * @param PVector point Point to convert
		   * @return PVector point converted to projective coordinated
		   * */
		  private PVector getScreen(PVector point) {
		    PVector screenPos = new PVector();
		    context.convertRealWorldToProjective(point, screenPos);
		    return screenPos;
		  }
		  /**
		   * Check if there is an active user in the openni context.
		   * This example only works with the first registered user.
		   * @return boolean True if is an active user, false otherwise
		   * */
		  private boolean isActiveUser() {
		    boolean active=false;
		    if (context.isInit() == true) {
		      int[] userList = context.getUsers();
		      if (userList.length>=1) {
		        active=true;
		      }
		    }
		    return active;
		  }
		  /**
		  * Check if the device is detecting both hands.
		  */
		  private boolean isDetectingHands() {
		    if(left.getPoint().x==0&&left.getPoint().y==0&&left.getPoint().z==0&&
		         right.getPoint().x==0&&right.getPoint().y==0&&right.getPoint().z==0)
		      return false;
		    else
		      return true;
		  }
		  
		  public boolean isSelection() {
		    //println(distance());
		    if(distance()<selThreshold) {
		      scene.setFrameVisualHint(true);
		      return true;
		    }else{
		      scene.setFrameVisualHint(false);
		      return false;
		    }
		  }
		  /**
		  * Return the distance between the hands
		  * @return float the distance
		  **/
		  private float distance(){
		    return sqrt(pow(left.getPoint().x-right.getPoint().x,2)+pow(left.getPoint().y-right.getPoint().y,2)+pow(left.getPoint().z-right.getPoint().z,2));
		  }
		  // SimpleOpenNI events
		  public void onNewUser(SimpleOpenNI curContext, int userId){
		    println("New user detected: " + userId);
		    curContext.startTrackingSkeleton(userId);
		  }
		}
}
