package grabbers.simple_grabber;

import grabbers.button.Button2D;
import processing.core.PVector;
import remixlab.proscene.Scene;
import remixlab.tersehandling.event.*;
import remixlab.tersehandling.generic.event.GenericClickEvent;

public class ClickButton extends Button2D {
	boolean addBox;

	public ClickButton(Scene scn, PVector p, String t, boolean addB) {
		super(scn, p, t, 24);
		addBox = addB;
	}

	@Override
	public void performInteraction(TerseEvent event) {
		if (event instanceof ClickEvent)
			if (((ClickEvent) event).getClickCount() == 1) {
				if (addBox)
					((Grabbers)parent).addBox();
				else
					((Grabbers)parent).removeBox();
			}
	}
}
