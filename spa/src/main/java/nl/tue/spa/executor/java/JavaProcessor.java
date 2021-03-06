package nl.tue.spa.executor.java;

import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import nl.tue.spa.core.Environment;
import nl.tue.spa.executor.EvaluationResult;
import nl.tue.spa.executor.EvaluationResult.ResultType;

public class JavaProcessor {

	private static Map<String,String> variables = new HashMap<String,String>(); //Shared variable space to make the whole thing thread safe
	private static Map<String,String> functions = new HashMap<String,String>(); //Shared function space to make the whole thing thread safe
	
	private static final String[] reservedNames = {"eventbus","runner","console","update","run"}; 
	
    public static EvaluationResult evaluate(Context cx, Scriptable scope, String expression, String sourceName, int lineNumber){
    	String result = null;
    	try{
    		putVariablesAndFunctionsInScope(cx, scope);
    		cx.evaluateString(scope, "console = {log: function(msg){Packages.nl.tue.spa.core.Environment.getConsoleController().log(msg);}}", "", 0, null);
    		cx.evaluateString(scope, "eventbus = {subscribe: function(party,variable){Packages.nl.tue.spa.core.Environment.getEventBus().subscribe(party,variable);},"
    				+ "unsubscribe: function(party){Packages.nl.tue.spa.core.Environment.getEventBus().unsubscribe(party);}}", "", 0, null);
    		cx.evaluateString(scope,  "runner = {start: function(party){Packages.nl.tue.spa.core.Environment.getRunner().addPartyToThread(party);},"
    				+ "stop: function(party){Packages.nl.tue.spa.core.Environment.getRunner().removePartyToThread(party);}}", "", 0, null);
    		Object returnedValue = cx.evaluateString(scope, expression, sourceName, lineNumber, null);
    		if (returnedValue != Context.getUndefinedValue()){
    			result = Context.toString(returnedValue);
    		}
    		setVariablesAndFunctionsFromScope(cx, scope);
    	}catch (Exception e){
    		return new EvaluationResult(e.getLocalizedMessage(), ResultType.ERROR);
    	}
    	if (result == null){
    		return new EvaluationResult();
    	}else{
    		return new EvaluationResult(result, ResultType.RESULT);
    	}
    }
    
    public static Context initializeContext(){
		Context cx = Context.enter();
		return cx;
    }
    
    public static Scriptable initializeScope(Context cx){
		Scriptable scope = cx.initStandardObjects();
    	return scope;
    }
    
    public static EvaluationResult evaluateScript(Context cx, Scriptable scope, String script, String sourceName){
    	EvaluationResult er = evaluate(cx, scope, script, sourceName, 1);
    	if (er.getType() == ResultType.ERROR){
    		return er;
    	}else{
    		return new EvaluationResult();
    	}
    }
    
    public static synchronized String[][] getVariables(){
    	String result[][] = new String[variables.size()][2];
    	int i = 0;
    	for (Map.Entry<String, String> me: variables.entrySet()){
    		result[i][0] = me.getKey();
    		result[i][1] = me.getValue();
    		i++;
    	}
    	return result;
    }

    public static synchronized String[][] getFunctions(){
    	String result[][] = new String[functions.size()][2];
    	int i = 0;
    	for (Map.Entry<String, String> me: functions.entrySet()){
    		result[i][0] = me.getKey();
    		result[i][1] = me.getValue();
    		i++;
    	}
    	return result;
    }

	public static synchronized void setVariables(String[][] variablesToSet) {
		for (String[] variable: variablesToSet){
			String name = variable[0];
			String value = variable[1];
			if (!value.equals(variables.get(name))){ //If it is changed, put the variable/value on the event bus.
				Environment.getEventBus().update(name, value);
			}
			variables.put(name, value);
		}
	}

    /**
     * Puts the variables with their values as well as functions from the global address space in the given context and scope.
     * 
     * @param cx the context into which to put the variables.
     * @param scope the scope into which to put the variables.
     */
    private static synchronized void putVariablesAndFunctionsInScope(Context cx, Scriptable scope){
    	String scriptedVariables = getVariablesAndFunctionsAsScript();
		cx.evaluateString(scope, scriptedVariables, "VARIABLE INITIALIZATION", 1, null);
    }
    
    /**
     * Gets the variables with their values as well as functions from the given context and scope.
     * Puts them in the global address space. 
     * Also puts changed variable values on the event bus. 
     * 
     * @param cx the context from which to get the variables.
     * @param scope the scope from which to get the variables.
     */
    private static synchronized void setVariablesAndFunctionsFromScope(Context cx, Scriptable scope){    	
    	Object variableNames[] = scope.getIds();
    	for (int i = variableNames.length-1; i >= 0; i--){
    		String variable = variableNames[i].toString();
    		if (!isReserved(variable)){
    			String jsonValue = "";
    			if (Context.toString(cx.evaluateString(scope, "typeof(" + variable.toString() +")", "", 1, null)).equals("function")){
    				jsonValue = Context.toString(cx.evaluateString(scope, variable.toString() +".toString()", "", 1, null));    			
        			functions.put(variable, jsonValue);
    			}else{
    				jsonValue = Context.toString(cx.evaluateString(scope, "JSON.stringify(" + variable.toString() +")", "", 1, null));
    				if (!jsonValue.equals(variables.get(variable))){ //If it is changed, put the variable/value on the event bus.
    					Environment.getEventBus().update(variable, jsonValue);
    				}
        			variables.put(variable, jsonValue);
    			}
    		}
    	}
    }

    /**
     * Returns the 
     * 
     * @param includeScriptTags
     * @return
     */
    public static synchronized String getVariablesAndFunctionsAsScript(){
    	String result = "";
    	for (Map.Entry<String, String> me: variables.entrySet()){
   			result += "var " + me.getKey() + " = " + me.getValue() + ";\n";
    	}
    	for (Map.Entry<String, String> me: functions.entrySet()){
   			result += "var " + me.getKey() + " = " + me.getValue() + ";\n";
    	}
    	return result;
    }

    /**
     * Removes all variables from the global address space. 
     */
	public static synchronized void clearVariablesAndFunctions() {
		variables = new HashMap<String,String>();
		functions = new HashMap<String,String>();
	}

	/**
	 * Removes the specified variable from the global address space.
	 * 
	 * @param variableName the name of the variable to remove.
	 */
	public static synchronized void removeVariableOrFunction(String variableName) {
		variables.remove(variableName);
		functions.remove(variableName);
	}
	
	private static boolean isReserved(String variableName){
		for (String reservedVariableName: reservedNames){
			if (reservedVariableName.equals(variableName)){
				return true;
			}
		}
		return false;
	}
}
