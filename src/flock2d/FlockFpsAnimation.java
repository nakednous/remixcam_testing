package flock2d;

/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/64701*@* */
/* !do not delete the line above, required for linking your tweak if you re-upload */
/*
 Flock
 Written by Jared "BlueThen" C. on July 4, 2011
 Revamped, optimized, and cleaned up on June 26, 2012

 bluethen.com
 twitter.com/bluethen
 openprocessing.org/portal/?userID=3044
 hawkee.com/profile/37047/

 Email me: bluethen (@) gmail . com

 Move mouse towards top left of applet for menu options
 */

import processing.core.*;
import processing.opengl.*;
import remixlab.fpstiming.*;

import java.util.*;

public class FlockFpsAnimation extends PApplet {
	TimingHandler handler;
	
	// List of birds
	List<GridObject> birds;

	// Grid for maintaining cell location of each bird
	Grid grid;

	// Quality settings
	// Renderer -> P2D, JAVA2D, or OPENGL (OPENGL and P2D may be the same?)
	// Best if used by P2D/OPENGL, but those tend to break as an applet.
	static String renderer = P2D;
	static int numBirds = 1000;
	static int birdTailLen = 4;
	static boolean smoothen = false;

	// Performance variables
	float updateTime = 0;
	float renderTime = 0;
	float guiTime = 0;
	int lastPrint = 0;

	public void setup() {
		size(640, 480, renderer);

		birds = new ArrayList<GridObject>();

		for (int i = 0; i < numBirds; i++) {
			Bird bird = new Bird(new PVector(random(width), random(height)), // position
					new PVector(random(-20, 20), random(-20, 20))); // velocity
			birds.add(bird);
		}

		// Create a new grid using number of birds and cell size
		// cell size = largest distance between two detectable items
		grid = new Grid(18, birds);

		// GUI stuff
		createGUI();

		// turn smooth() on or off based on the quality settings
		if (smoothen)
			smooth();
		else
			noSmooth();
		
		handler = new TimingHandler();
	}

	public void draw() {
		// Update birds, GUI, etc.
		long updateStart = millis();
		// we base our elapsed time off of the frameRate.
		// 1/frameRate = avg amount of ms elapsed for the previous several
		// frames
		// we limit it to between 16ms (~60fps) and 32ms (~30fps)
		// since nothing in the algorithm is time-sensitive, we can afford to
		// use a dynamic timestep
		// instead of using currentTime - previousTime (which is opt to change
		// wildly at any time), we use frameRate
		// frameRate is awesome for this because it keeps the update from being
		// jittering (because it's averaged over time)
		float elapsedTime = constrain(1f / frameRate, 16f / 1000f, 32f / 1000f);
		for (GridObject gOb : birds) {
			Bird bird = (Bird) gOb;
			bird.update(elapsedTime); // update
		}
		gui.update(elapsedTime);
		long updateEnd = millis();

		// Render
		long renderStart = millis();
		// clear screen
		background(0);
		// grid.drawGrid();
		beginShape(LINES);
		for (GridObject gOb : birds) {
			Bird bird = (Bird) gOb;
			bird.draw(); // render
		}
		endShape();
		gui.draw();
		long renderEnd = millis();

		// calculate performances of each part of the program
		// the times for this frame is factored in only as a small portion
		updateTime = (updateTime * 29 + (updateEnd - updateStart)) / 30;
		renderTime = (renderTime * 29 + (renderEnd - renderStart)) / 30;
		// Print performances every few seconds
		if (lastPrint != second() && second() % 4 == 0 && second() != 1) {
			lastPrint = second();
			float total = updateTime + renderTime;
			println("Update Time: " + (int) updateTime + "ms ("
					+ (int) (100 * updateTime / total) + "%)"
					+ " | Render Time: " + (int) renderTime + "ms ("
					+ (int) (100 * renderTime / total) + "%)");
			println("Total " + (int) total + "ms");
			println("Framerate: " + (int) frameRate);
		}
		
		handler.handle();
	}

	public interface GridObject {
		float getX();

		float getY();
	}

	// BIRD

	// Values used for different properties of bird behavior
	// Cohesion: tendency to move towards the surrounding birds' average
	// position
	static float cohesionMin = -0.1f;
	static float cohesionMax = 5f;

	// Alignment: tendency to move towards the surrounding birds' average
	// velocity
	static float alignmentMin = -0.01f;
	static float alignmentMax = 0.05f;

	// Separation: tendency to move away from the surrounding birds' average
	// position
	static float separationMin = 0.25f;
	static float separationMax = 5f;

	static float separationDistMinSq = 5 * 5;
	static float separationDistMaxSq = 15 * 15;

	class Bird extends AnimatedObject implements GridObject {
		PVector position;
		PVector velocity;

		PVector[] lastPos;

		// by chance, we choose between high and low factors
		float cohesion = cohesionMax;
		float alignment = alignmentMax;
		float separation = separationMax;
		float separationDistSq = separationDistMaxSq;

		// constructor
		Bird(PVector pos, PVector vel) {
			lastPos = new PVector[birdTailLen];
			for (int i = 0; i < lastPos.length; i++)
				lastPos[i] = pos.get();
			position = pos.get();
			velocity = vel.get();
		}

		// Update position and velocity
		void update(float elapsedTime) {
			// If outside boundary, teleport to opposite side
			if (position.x > width)
				position.x = 1;
			if (position.x < -1)
				position.x = width;
			if (position.y > height)
				position.y = 1;
			if (position.y < -1)
				position.y = height;

			// track last positions
			if (lastPos.length > 0) {
				for (int i = lastPos.length - 1; i > 0; i--)
					lastPos[i] = lastPos[i - 1].get();
				lastPos[0] = position.get();
			}

			// set up variables for flocking algorithm
			PVector cohesionSum = new PVector(0, 0);
			int cohesionCount = 0;

			PVector alignmentSum = new PVector(0, 0);
			int alignmentCount = 0;

			PVector separationSum = new PVector(0, 0);
			int separationCount = 0;

			// Use grid to find birds potentially close enough to interact with
			List<Bird> nearby = grid.nearByObjects(position.x, position.y);

			// loop through each nearby Bird
			for (Bird bird : nearby) {
				if (bird != this) {
					PVector other = bird.position;

					// find distance squared
					// using sqrt is very costly, so we avoid it when possible
					float xDiff = position.x - other.x;
					float yDiff = position.y - other.y;
					float distSq = xDiff * xDiff + yDiff * yDiff;

					// 15 is the minimum distance needed for two birds to
					// interact
					if (distSq < 324) {
						// added their pos to cohesion
						cohesionSum.add(other);
						cohesionCount++;

						// add their velocity to alignment
						alignmentSum.add(bird.velocity);
						alignmentCount++;

						// the separation distance will be smaller than cohere
						// distance
						// birds will try and maintain a particular distance
						// from each other
						if (distSq < separationDistSq) {
							separationSum.add(other);
							separationCount++;
							if (distSq < separationDistMinSq) { // avoid being
																// too close to
																// any
																// particular
																// bird
								separationSum.add(PVector.mult(other, 12));
								separationCount += 12;
							} // separation minimum
						} // separation dist check
					} // dist check
				} // bird != this check
			} // end of for loop

			// now average the separation, cohesion, and alignment, and factor
			// them into the bird's new direction

			PVector acceleration = new PVector(0, 0);

			// Cohesion
			if (cohesionCount > 0) {
				// one would normally normalize the desired vector
				// however, for the sake of speed (normalizing uses sqrt), we
				// just use the vector itself

				// average the cohesion to find the evarage position of all
				// surrounding bird
				cohesionSum.div(cohesionCount);

				// find the vector between the average position and this bird's
				// position
				PVector desired = PVector.sub(position, cohesionSum);

				// multiply it by our cohesion
				desired.mult(cohesion);

				if (cohesionCount > 10 && cohesion > 0) // avoid tightly packed
														// groups
					desired.mult(-0.25f);

				// subtract it from acceleration
				acceleration.sub(desired);
			}

			// Alignment
			if (alignmentCount > 0) {
				alignmentSum.div(alignmentCount); // average out all the
													// velocities
				alignmentSum.mult(alignment); // multiply it by our alignment
												// property
				acceleration.add(alignmentSum); // add it to the acceleration
			}

			// Separation
			// uses the exact same algorithm as Cohesion, except steer is
			// subracted from acceleration instead of added
			if (separationCount > 0) {
				separationSum.div(separationCount); // average out the
													// surrounding birds'
													// position

				PVector desired = PVector.sub(position, separationSum); // find
																		// vector
																		// between
																		// this
																		// bird
																		// and
																		// the
																		// avg
																		// position
				desired.mult(separation); // multiply that vector by our
											// separation property

				acceleration.add(desired); // add it to the acceleration
			}

			// Interaction with mouse
			if (mousePressed && mousePressedOnCanvas) {
				// first make sure the bird is close enough
				float mDiffX = mouseX - position.x;
				float mDiffY = mouseY - position.y;
				float mDistSq = mDiffX * mDiffX + mDiffY * mDiffY;
				if (mDistSq < 100 * 100) {
					float mDist = sqrt(mDistSq);
					// push it away by normalizing the vector from mouse to
					// position, and changing the vector length to 100
					acceleration.sub((mDiffX / mDist) * 100,
							(mDiffY / mDist) * 100, 0);
				}
			}
			// factor in the acceleration
			velocity.add(acceleration);

			// velocity.limit(); <- really slow. They calculate the sqrt twice
			// to check and limit the vector
			float velMagSq = velocity.x * velocity.x + velocity.y * velocity.y;
			if (velMagSq > 6400) { // 80 * 80
				float velMag = sqrt(velMagSq); // only sqrt the magnitude when
												// absolutely necessary
				// change velocity's length to 80
				velocity.x = (velocity.x / velMag) * 80;
				velocity.y = (velocity.y / velMag) * 80;
			}

			// add the velocity
			position.add(PVector.mult(velocity, elapsedTime));

			// update the grid with the new position
			grid.update(this);
		}

		// Render
		public void draw() {
			// each bird is colored based on their "personality"
			stroke(((cohesion - cohesionMin) / (cohesionMax - cohesionMin)) * 155 + 100,
					((alignment - alignmentMin) / (alignmentMax - alignmentMin)) * 155 + 100,
					((separation - separationMin) / (separationMax - separationMin)) * 155 + 100,
					255);

			if (lastPos.length > 0) { // if we keep track of the last
										// position(s)
				vertex((int) position.x, (int) position.y); // draw a line from
															// current to first
															// last position
				vertex((int) (lastPos[0].x + 1), (int) (lastPos[0].y + 1)); // we
																			// add
																			// 1
																			// to
																			// ensure
																			// that
																			// atleast
																			// something
																			// is
																			// drawn.

				for (int i = 0; i < lastPos.length - 1; i++) {
					float diffX = lastPos[i].x - lastPos[i + 1].x;
					float diffY = lastPos[i].y - lastPos[i + 1].y;
					float distSq = diffX * diffX + diffY * diffY;
					// check to see if the line is
					// long enough to even bother drawing
					// and not stretched across the entire screen because of
					// edge wrapping
					if (distSq > 1.5 && distSq < 2500) {
						vertex((int) lastPos[i].x, (int) lastPos[i].y);
						vertex((int) lastPos[i + 1].x, (int) lastPos[i + 1].y);
					}
				}
			} else { // we're not keeping track of the last position(s)
				// just draw a point
				vertex((int) position.x, (int) position.y);
				vertex((int) position.x + 1, (int) position.y + 1);
			}
		}

		// getX() and getY() is used by the Grid
		public float getX() {
			return position.x;
		}

		public float getY() {
			return position.y;
		}
	}

	// The GUI is separated into a sort of hierarchy
	// The very top level is the GUI class
	// next down is the GUISlideTab. we can have as many slide-tabs as we'd like
	// next are the RadioGroups. It makes sure only one radio in its group is
	// selected at a time
	// then there's the GUIText and RadioButtons.

	// The text and radio buttons can be added into the GUI or GUISlideTab (and
	// RadioGroups for just the radio buttons)

	// GUI - highest interface level
	public class GUI {
		// list of elements
		List<GUIElement> elements;

		// variable for activating mouse pressed event
		boolean mouseWasPressed;

		GUI() {
			elements = new ArrayList<GUIElement>();
		}

		public void draw() {
			// loop through each contained element and draw
			for (GUIElement element : elements)
				element.draw();
		}

		// the update function check for mouse press, and updates each element
		public void update(float elapsedTime) {
			if (mousePressed)
				mouseWasPressed = true;
			else {
				if (mouseWasPressed) {
					for (GUIElement element : elements)
						element.mouseReleased();
				}
			}
			for (GUIElement element : elements)
				element.update(elapsedTime);
		}

		public void add(GUIElement element) {
			elements.add(element);
		}
	}

	// Slide tab
	public class GUISlideTab implements GUIElement {
		List<GUIElement> elements;
		// width, height, x,y, and value (t) between 0 and 1 representing its
		// slide state
		float w, h, x, y, t;

		float minX, maxX, minY, maxY;

		int fillC, strokeC;

		GUISlideTab(float x, float y, float w, float h) {
			t = 1;

			elements = new ArrayList<GUIElement>();

			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;

			fillC = color(0, 175);
			strokeC = color(255);

			minX = x - w - 3;
			maxX = x;
			minY = y;
			maxY = y;
		}

		public void add(GUIElement element) {
			elements.add(element);
		}

		public void draw() {
			draw(0, 0);
		}

		public void draw(float transX, float transY) {

			// draw the tab background
			pushStyle();
			fill(fillC);
			stroke(strokeC);
			rect(getX() + transX, getY() + transY, w, h);
			rect(getX() + transX + w, getY() + transY + h / 2f - (h / 2f) / 2f,
					w * (1f / 13f), h / 2f);
			popStyle();

			// draw all contained elements
			for (GUIElement element : elements)
				element.draw(transX + t * (maxX - minX) - (maxX - minX), transY
						+ t * (maxY - minY) - (maxY - minY));
		}

		public float getX() {
			return minX + t * (maxX - minX);
		}

		public float getY() {
			return minY + t * (maxY - minY);
		}

		public void update(float elapsedTime) {
			// adjust t to reflect state
			// we use sine for its speed
			// sin(0) == 0
			// sin(PI/2) == 1
			// sin(PI) == 0
			// knowing that the speed will be highest at PI/2 and lowest at 0
			// and PI, we can multiply t by PI and use that as our main value
			// for the speed
			if ((mouseX > maxX && mouseX < maxX + w && mouseY > maxY && mouseY < maxY
					+ h)
					|| (mouseX > getX() + w
							&& mouseX < getX() + w + w * (1f / 13f)
							&& mouseY > getY() + h / 2f - (h / 2f) / 2f && mouseY < getY()
							+ h / 2f - (h / 2f) / 2f + h / 2f)) {
				if (t != 1)
					t += max(elapsedTime * 8 * sin(t * PI), 0.01f); // 8 = max
																	// speed,
																	// 0.01 =
																	// min speed
			} else if (t != 0)
				t -= max(elapsedTime * 8 * sin(t * PI), 0.01f);

			// make sure we don't over/under shoot it
			t = constrain(t, 0, 1);

			// update contained elements
			for (GUIElement element : elements)
				element.update(elapsedTime);
		}

		// mouse release event
		public void mouseReleased() {
			if (!mousePressedOnCanvas) { // only do it if mouse was initially
											// pressed inside tab
				// notify all contained elements
				for (GUIElement element : elements)
					element.mouseReleased();
			}
		}
	}

	// Radio Group
	// Maintains contained radios so that only one at a time is selected
	public class RadioGroup implements GUIElement {

		List<RadioButton> radios;

		public RadioGroup() {
			radios = new ArrayList<RadioButton>();
		}

		public void draw() {
			draw(0, 0);
		}

		public void draw(float transX, float transY) {
			for (RadioButton radio : radios)
				radio.draw(transX, transY);
		}

		public void update(float elapsedTime) {
			for (RadioButton radio : radios)
				radio.update(elapsedTime);
		}

		public void add(RadioButton radio) {
			radios.add(radio);
		}

		public void mouseReleased() {
			for (RadioButton radio : radios)
				if (!radio.selected && radio.active) { // If a radio is "active"
														// at the time of mouse
														// release, and isn't
														// selected, select it
					radio.selected();
					// loop through other radios and make sure they're
					// unselected
					for (RadioButton other : radios) {
						if (other != radio)
							other.selected = false;
					}
				}
		}

		// method for setting all the colors for all contained radios.
		public void setColors(int strokeC, int hoverStrokeC, int activeStrokeC,
				int fillC, int hoverFillC, int activeFillC,
				int selectedStrokeC, int selectedHoverStrokeC,
				int selectedActiveStrokeC, int selectedFillC,
				int selectedHoverFillC, int selectedActiveFillC) {

			for (RadioButton radio : radios) {
				radio.strokeC = strokeC;
				radio.hoverStrokeC = hoverStrokeC;
				radio.activeStrokeC = activeStrokeC;

				radio.fillC = fillC;
				radio.hoverFillC = hoverFillC;
				radio.activeFillC = activeFillC;

				radio.selectedStrokeC = selectedStrokeC;
				radio.selectedHoverStrokeC = selectedHoverStrokeC;
				radio.selectedActiveStrokeC = selectedActiveStrokeC;

				radio.selectedFillC = selectedFillC;
				radio.selectedHoverFillC = selectedHoverFillC;
				radio.selectedActiveFillC = selectedActiveFillC;
			}

		}
	}

	// Radio Button
	public class RadioButton implements GUIElement {
		float x, y;
		float d; // diameter

		int strokeC = color(255);
		int hoverStrokeC = color(255);
		int activeStrokeC = color(255);

		int fillC = color(100);
		int hoverFillC = color(150);
		int activeFillC = color(200);

		int selectedStrokeC = color(255);
		int selectedHoverStrokeC = color(255);
		int selectedActiveStrokeC = color(255);

		int selectedFillC = color(255, 0, 0);
		int selectedHoverFillC = color(255, 150, 150);
		int selectedActiveFillC = color(255, 200, 200);

		// States
		boolean hover = false;
		boolean active = false;
		boolean selected = false;

		// event handler
		GUIHandler guiH;

		// name for event handling
		String name;

		public RadioButton(String name, GUIHandler guiH, float x, float y,
				float d) {
			this.name = name;

			this.guiH = guiH;

			this.x = x;
			this.y = y;

			this.d = d;
		}

		public void draw() {
			draw(0, 0);
		}

		public void draw(float transX, float transY) {
			pushStyle();
			// we set colors in order of precedence
			// least important first: idle, hover, then active colors
			// then selected idle, hover, then active colors
			if (!selected) {
				fill(fillC);
				stroke(strokeC);

				if (hover) {
					fill(hoverFillC);
					stroke(hoverStrokeC);
				}
				if (active) {
					fill(activeFillC);
					stroke(activeStrokeC);
				}
			} else {
				fill(selectedFillC);
				stroke(selectedStrokeC);

				if (hover) {
					fill(selectedHoverFillC);
					stroke(selectedHoverStrokeC);
				}
				if (active) {
					fill(selectedActiveFillC);
					stroke(selectedActiveStrokeC);
				}
			}
			ellipse(x + transX, y + transY, d, d);
			popStyle();
		}

		public void update(float elapsedTime) {
			// here we check to see if the mouse is hovering this radio
			float diffX = mouseX - x;
			float diffY = mouseY - y;
			if (diffX * diffX + diffY * diffY < (d * d) / 4) {
				if (mousePressed) {
					active = true;
					hover = false;
				} else {
					active = false;
					hover = true;
				}
			} else {
				hover = false;
				active = false;
			}
		}

		// method for RadioGroup to use when this button gets selected
		public void selected() {
			selected = true;
			guiH.selected(name);
		}

		public void mouseReleased() {
		}
	}

	// GUIText
	// Object to make it convenient to slide on the GUISlideTab
	// It allows us to draw with a translation
	public class GUIText implements GUIElement {
		String msg;
		float x, y;
		int c;

		GUIText(String msg, float x, float y, int c) {
			this.x = x;
			this.y = y;
			this.msg = msg;
			this.c = c;
		}

		public void draw() {
			draw(0, 0);
		}

		public void draw(float transX, float transY) {
			pushStyle();
			fill(c);
			text(msg, x + transX, y + transY);
			popStyle();
		}

		public void update(float elapsedTime) {
		}

		public void mouseReleased() {
		}
	}

	// Interface for groups and the interface to list any variety of GUI
	// elements
	public interface GUIElement {
		public void draw();

		public void draw(float transX, float transY);

		public void update(float elapsedTime);

		public void mouseReleased();
	}

	// GUI class
	GUI gui;
	// Front-end handler for events
	GUIHandler guiH;
	// Slide-out tab
	GUISlideTab tab;

	// variable for deciding whether a mouse click was intended for the menu or
	// the birds
	boolean mousePressedOnCanvas = false;

	public void mousePressed() {
		// check to see if the mouse is within the slideout tab or its side-tab
		// thing
		if (mouseX > tab.getX() && mouseX < tab.getX() + tab.w
				&& mouseY > tab.getY() && mouseY < tab.getY() + tab.h)
			mousePressedOnCanvas = false;
		else
			mousePressedOnCanvas = true;
	}

	// Method for initializing the GUI
	public void createGUI() {
		// Font
		PFont font = createFont("TwCenMT-Bold-15.vlw", 15);
		textFont(font, 15);

		gui = new GUI();

		guiH = new GUIHandler();

		// Cohesive radio buttons
		RadioButton noneC = new RadioButton("noneC", guiH, 130, 55, 15); // "name",
																			// handler,
																			// x,
																			// y,
																			// diameter
		RadioButton randC = new RadioButton("randC", guiH, 185, 55, 15);
		RadioButton allC = new RadioButton("allC", guiH, 240, 55, 15);
		RadioGroup cohesionRads = new RadioGroup();
		cohesionRads.add(noneC);
		cohesionRads.add(randC);
		cohesionRads.add(allC);
		allC.selected = true;
		cohesionRads.setColors(color(255, 0, 0), color(255, 0, 0),
				color(255, 0, 0), color(100), color(200), color(255),
				color(255, 0, 0), color(255, 0, 0), color(255, 0, 0),
				color(255, 0, 0), color(255, 200, 200), color(255, 255, 255));

		// Alignment radio buttons
		RadioButton noneA = new RadioButton("noneA", guiH, 130, 100, 15);
		RadioButton randA = new RadioButton("randA", guiH, 185, 100, 15);
		RadioButton allA = new RadioButton("allA", guiH, 240, 100, 15);
		RadioGroup alignmentRads = new RadioGroup();
		alignmentRads.add(noneA);
		alignmentRads.add(randA);
		alignmentRads.add(allA);
		allA.selected = true;
		alignmentRads.setColors(color(0, 255, 0), color(0, 255, 0),
				color(0, 255, 0), color(100), color(200), color(255),
				color(0, 255, 0), color(0, 255, 0), color(0, 255, 0),
				color(0, 255, 0), color(200, 255, 200), color(255, 255, 255));

		// separation radio buttons
		RadioButton noneS = new RadioButton("noneS", guiH, 130, 145, 15);
		RadioButton randS = new RadioButton("randS", guiH, 185, 145, 15);
		RadioButton allS = new RadioButton("allS", guiH, 240, 145, 15);
		RadioGroup separationRads = new RadioGroup();
		separationRads.add(noneS);
		separationRads.add(randS);
		separationRads.add(allS);
		allS.selected = true;
		separationRads.setColors(color(100, 100, 255), color(100, 100, 255),
				color(100, 100, 255), color(100), color(200), color(255),
				color(100, 100, 255), color(100, 100, 255),
				color(100, 100, 255), color(100, 100, 255),
				color(200, 200, 255), color(255, 255, 255));

		// Slide-out tab
		tab = new GUISlideTab(0, 0, 272, 175);
		tab.add(cohesionRads);
		tab.add(alignmentRads);
		tab.add(separationRads);
		gui.add(tab);

		GUIText noneLab = new GUIText("NONE", 130 - allC.d / 2, 30, color(255,
				255, 255));
		GUIText randLab = new GUIText("RAND", 185 - allC.d / 2, 30, color(255,
				255, 255));
		GUIText allLab = new GUIText("ALL", 240 - allC.d / 2, 30, color(255,
				255, 255));

		float sepWidth = textWidth("SEPARATE");
		GUIText cohereLab = new GUIText("COHERE", 20 + sepWidth
				- textWidth("COHERE"), 55 + 15 / 2 - 2, color(255, 0, 0));
		GUIText alignLab = new GUIText("ALIGN", 20 + sepWidth
				- textWidth("ALIGN"), 100 + 15 / 2 - 2, color(0, 255, 0));
		GUIText separateLab = new GUIText("SEPARATE", 20, 145 + 15 / 2 - 2,
				color(100, 100, 255));

		tab.add(noneLab);
		tab.add(randLab);
		tab.add(allLab);

		tab.add(cohereLab);
		tab.add(alignLab);
		tab.add(separateLab);
	}

	// Object to be called when a Radio Button is selected
	class GUIHandler {
		void selected(String name) {
			if (name.equals("allC"))
				allCohere();
			if (name.equals("noneC"))
				noneCohere();
			if (name.equals("randC"))
				randCohere();
			if (name.equals("allA"))
				allAlign();
			if (name.equals("randA"))
				randAlign();
			if (name.equals("noneA"))
				noneAlign();
			if (name.equals("allS"))
				allSeparate();
			if (name.equals("noneS"))
				noneSeparate();
			if (name.equals("randS"))
				randSeparate();
		}
	}

	// Methods for radio-button events
	void allCohere() {
		for (GridObject go : birds) {
			Bird bird = (Bird) go;
			bird.cohesion = cohesionMax;
		}
	}

	void noneCohere() {
		for (GridObject go : birds) {
			Bird bird = (Bird) go;
			bird.cohesion = cohesionMin;
		}
	}

	void randCohere() {
		for (GridObject go : birds) {
			Bird bird = (Bird) go;
			bird.cohesion = random(1) < 0.5 ? cohesionMin : cohesionMax;
		}
	}

	void allAlign() {
		for (GridObject go : birds) {
			Bird bird = (Bird) go;
			bird.alignment = alignmentMax;
		}
	}

	void noneAlign() {
		for (GridObject go : birds) {
			Bird bird = (Bird) go;
			bird.alignment = alignmentMin;
		}
	}

	void randAlign() {
		for (GridObject go : birds) {
			Bird bird = (Bird) go;
			bird.alignment = random(1) < 0.5 ? alignmentMin : alignmentMax;
		}
	}

	void allSeparate() {
		for (GridObject go : birds) {
			Bird bird = (Bird) go;
			bird.separation = separationMax;
			bird.separationDistSq = separationDistMaxSq;
		}
	}

	void noneSeparate() {
		for (GridObject go : birds) {
			Bird bird = (Bird) go;
			bird.separation = separationMin;
			bird.separationDistSq = separationDistMinSq;
		}
	}

	void randSeparate() {
		for (GridObject go : birds) {
			Bird bird = (Bird) go;
			bird.separation = random(1) < 0.5 ? separationMin : separationMax;
			bird.separationDistSq = bird.separation == separationMin ? separationDistMinSq
					: separationDistMaxSq;
		}
	}

	// Grid is an algorithm I made about 2 years ago
	// I revamped it, optimized it, and cleaned it significantly
	// it takes objects, stores them in separate cells,
	// and when called on, will provide a list of objects in a cell and cells
	// adjacent to that cell
	// it allows to save resources by only having collision checks between
	// objects potentially close enough

	class Grid {
		// The cells map keeps track of where each object is
		// The grid list keeps track of which objects are in each cell

		Map cells = new HashMap(); // Key: Object object, Value: grid
									// position... (x/scale,y/scale)
									// holds the position of the object in the
									// grid. This makes sure that the object
									// gets removed and replaced each time
									// it changes position in the grid.

		List[][] grid; // grid[x/scale][y/scale] = List of objects
		int cellSize;

		Grid(int cellSize, List<GridObject> objects) {
			this.cellSize = cellSize;
			grid = new ArrayList[(int) (width / cellSize) + 1][(int) (height / cellSize) + 1];

			// construct a list for each cell
			for (int x = 0; x < grid.length; x++) {
				for (int y = 0; y < grid[x].length; y++) {
					grid[x][y] = new ArrayList(); // a list of objects for every
													// cell
				}
			}

			// add the objects to their cells
			// and keep track of which cell each object is in
			for (GridObject object : objects) {
				cells.put(object, new PVector((int) (object.getX() / cellSize),
						(int) (object.getY() / cellSize)));
				grid[(int) (object.getX() / cellSize)][(int) (object.getY() / cellSize)]
						.add(object);
			}
		}

		void drawGrid() {
			stroke(50);
			for (int x = 0; x < grid.length; x++) {
				for (int y = 0; y < grid[x].length; y++) {
					noFill();
					rect(x * cellSize, y * cellSize, cellSize, cellSize);
				}
			}
		}

		// Update needs to be called on by each object after they update their
		// position
		// this makes sure that the object is still in the correct cell
		void update(GridObject object) {
			// first check if it needs to be moved

			// Find cell we stored it in
			PVector oldCellPos = (PVector) cells.get(object);
			int oldCellX = (int) oldCellPos.x;
			int oldCellY = (int) oldCellPos.y;

			// find cell it is in currently
			int cellX = (int) (object.getX() / cellSize);
			int cellY = (int) (object.getY() / cellSize);

			// if they match, stop there
			if (cellX == oldCellX && cellY == oldCellY)
				return;

			// safety check
			if (cellX >= 0 && cellX < grid.length && cellY >= 0
					&& cellY < grid[cellX].length) {
				// if the grid the object is in now doesn't contain that
				// object...
				if (!grid[cellX][cellY].contains(object)) {
					// add the object and remove the object from its former cell
					grid[cellX][cellY].add(object);
					grid[oldCellX][oldCellY].remove(object);
					// memorize which cell the object is in
					cells.put(object, new PVector(cellX, cellY));
				}
			}
		}

		// Find a list of nearby objects
		// goes through each cell adjacent to the current cell, and the current
		// cell,
		// and generates a list of objects contained in those cells
		List nearByObjects(float x, float y) {
			List nearBy = new ArrayList();
			int cellX = (int) (x / cellSize);
			int cellY = (int) (y / cellSize);

			if (cellX >= 0 && cellX < grid.length) {
				// center column
				if (cellY >= 0 && cellY < grid[cellX].length) {
					// middle
					for (Object object : grid[cellX][cellY])
						nearBy.add(object);
					// top
					// cellY+1 >= 0 can be assumed since cellY >= 0 is checked
					// in this block
					if (cellY + 1 < grid[cellX].length) {
						for (Object object : grid[cellX][cellY + 1])
							nearBy.add(object);
					}
				}
				// bottom
				if (cellY - 1 >= 0 && cellY - 1 < grid[cellX].length) {
					for (Object object : grid[cellX][cellY - 1])
						nearBy.add(object);
				}

				// right column
				if (cellX + 1 < grid.length) {
					if (cellY >= 0 && cellY < grid[cellX + 1].length) {
						// middle right
						for (Object object : grid[cellX + 1][cellY])
							nearBy.add(object);
						if (cellY + 1 < grid[cellX + 1].length) {
							// top right
							for (Object object : grid[cellX + 1][cellY + 1])
								nearBy.add(object);
						}
					}
					if (cellY - 1 >= 0 && cellY - 1 < grid[cellX + 1].length) {
						// bottom right
						for (Object object : grid[cellX + 1][cellY - 1])
							nearBy.add(object);
					}
				}
			}

			// left column
			if (cellX - 1 >= 0 && cellX - 1 < grid.length) {
				if (cellY >= 0 && cellY < grid[cellX - 1].length) {
					// center left
					for (Object object : grid[cellX - 1][cellY])
						nearBy.add(object);
					// top left
					if (cellY + 1 < grid[cellX - 1].length) {
						for (Object object : grid[cellX - 1][cellY + 1])
							nearBy.add(object);
					}
				}
				if (cellY - 1 >= 0 && cellY - 1 < grid[cellX - 1].length) {
					// bottom left
					for (Object object : grid[cellX - 1][cellY - 1])
						nearBy.add(object);
				}
			}
			return nearBy;
		}
	}
}