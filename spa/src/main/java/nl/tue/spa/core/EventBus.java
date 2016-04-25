package nl.tue.spa.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventBus {

	private Map<String, Set<String>> subscriptions; //variable name, set of parties subscribed to changes to the variable 
	
	public EventBus(){
		subscriptions = new HashMap<String, Set<String>>();
	}
	
	/**
	 * Subscribe party to changes to a given JavaScript variable.
	 * If the value of the JavaScript variable changes, the party is informed through a callback.
	 * Parties are identified by their file names, as they are known by the MainController.
	 * 
	 * @param party file name of a party as it is known by the MainController. 
	 * @param variable name of the JavaScript variable to notify changes of.
	 */
	public void subscribe(String party, String variable){
		Set<String> partiesForVariable = subscriptions.get(variable);
		if (partiesForVariable == null){
			partiesForVariable = new HashSet<String>();
		}
		partiesForVariable.add(party);
		subscriptions.put(variable, partiesForVariable);
	}
	
	/**
	 * Sends updates to all parties that are registered for this variable.
	 * Unsubscribe of parties for this variable will happen automatically, if the party cannot be reached.
	 * 
	 * @param variable the variable with the updated value.
	 * @param value the updated value.
	 */
	public void update(String variable, String value){
		Set<String> partiesForVariable = subscriptions.get(variable);
		if (partiesForVariable != null){
			for (String party: partiesForVariable){
				Environment.getMainController().executeJavaScriptOnParty(party, "update(\""+variable+"\","+value+")");
			}
		}		
	}

	/**
	 * Unsubscribes the specified party from all subscriptions of which it is a part.
	 * 
	 * @param party the party to unsubscribe.
	 */
	public void unsubscribe(String party) {
		for (Set<String> me: subscriptions.values()){
			me.remove(party);
		}
	}
}
