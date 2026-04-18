package Messages;

import Spaces.Player;
import Spaces.Property;

/**
 * This Class will be a message sent from the model to the view to ask let the view 
 * know to propmt the user if they want to buy a property  
 * @author Jake 
 */
public class PurchasePromptMessage {
	private Player currentPlayer;
	private Property property;

	public PurchasePromptMessage(Player currentPlayer, Property property) {
		this.currentPlayer = currentPlayer;
		this.property = property;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	public Property getProperty() {
		return property;
	}
}
