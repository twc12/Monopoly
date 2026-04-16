package monopoly;

/**
 * This class will be used for the controller who is operating the 
 * Ai decisions to send a message to the view for every ai decision 
 * 
 * Fields:
 * 	aiAction (String): The String of the ai action 
 */
public class AiActionMessage {

	private String aiAction;
	
	/**
	 * Constructor: Builds the AIActionMessage
	 * @param actionFromAi (String): The text brief describing the ai action
	 */
	public AiActionMessage(String actionFromAi) {
		aiAction = actionFromAi;
	}
	
	/**
	 * Getter: Get out the Ai action string to display it 
	 * @return String: the action from the ai in text 
	 */
	public String getAiAction() {
		return aiAction;
	}
}
