package Messages;

/**
 * Sent to the view to create the Ai Log box in the bottom right
 * when its determined there are 1 or more AI players
 * @author Jake
 */
public class AiLogsEnabledMessage {

	/**
	 * Constructor
	 * No args required, the message existing is enough
	 */
	public AiLogsEnabledMessage() {	
	}
	
	/**
	 * Pulls a boolean representing if the logs are enabled
	 * @return a boolean if it's enabled
	 */
	public boolean getMessage() {
		return true;
	}
	
}
