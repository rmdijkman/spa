package nl.tue.spa.core.guistate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GUIState implements Serializable{
	private static final long serialVersionUID = 1L;

	Map<String,Object> stateVariables = new HashMap<String,Object>();
	
	public void putStateVar(String var, Object val){
		stateVariables.put(var, val);
	}
	public Object getStateVar(String var){
		return stateVariables.get(var);
	}
}
