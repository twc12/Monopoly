package monopoly;

/**
 * This class is made to notify the view 
 * from the model that the next players turn 
 * is up. This class will hold the player object 
 * whos turn it is NOW
 */
public class NextPlayerMessage {
	
	private Player nextPlayer;
	/**
	 * Constructor: Build a message to the view of who 
	 * the next player is 
	 * @param theNextPlayer (Player): the next player object
	 */
	public NextPlayerMessage(Player theNextPlayer) {
		nextPlayer = theNextPlayer;
	}
	
	/**
	 * Getter: To get the next player object
	 * @return Player: The next player in line
	 */
	public Player getNextPlayer() {
		return nextPlayer;
	}
}
