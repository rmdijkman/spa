package nl.tue.spa.core.guistate;

public interface GUIStateSerializable{

	public GUIState getState();
	public void restoreState(GUIState state);
	
}
