package basic;
import processing.core.*;
import processing.opengl.*;
import remixlab.proscene.*;

@SuppressWarnings("serial")
public class AlternativeUse extends PApplet {
	MyScene scene;	
	
	public void setup()	{
		size(640, 360, OPENGL);
		//size(640, 360, P3D);
		scene = new MyScene(this);			  
	}

	// /**
	public void draw() {		
	}	
	// */
	
	class MyScene extends Scene {
		public MyScene(PApplet p) {
			super(p);
		}

		public void init() {			
			setGridVisualHint(true);
			setAxisVisualHint(true);
		}
		
		public void proscenium() {
			background(0);
			// http://stackoverflow.com/questions/2808501/calling-outer-class-function-from-inner-class
			//AlternativeUse.this.pushMatrix();// calls PApplet pushMatrix
			fill(204, 102, 0);
			box(20, 30, 50);
		}
	}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "AlternativeUse" });
	}
}
