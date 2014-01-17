package TUIO2dofDandelion;

import remixlab.dandelion.core.Constants.DOF2Action;
import remixlab.tersehandling.generic.event.GenericDOF2Event;
import remixlab.tersehandling.generic.profile.Actionable;

public class TUIOEvent extends GenericDOF2Event<DOF2Action> {
	public TUIOEvent(TUIOEvent prevEvent, float x, float y, DOF2Action action) {
		super(prevEvent, x, y, action);
	}

	public TUIOEvent(TUIOEvent prevEvent, float x, float y) {
		super(prevEvent, x, y);
	}
	
	protected TUIOEvent(TUIOEvent prevEvent) {
		super(prevEvent);
	}
	
	@Override
	public TUIOEvent get() {
		return new TUIOEvent(this);
	}
}
