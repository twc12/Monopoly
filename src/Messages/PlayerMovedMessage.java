package Messages;

import Spaces.Player;

/**
 * PlayerMovedMessage: This class is to send a message from the model.
 * The message will say how much a specific player moved.
 * 
 * It is assumed this message is only sent out for normal forward movements
 * 
 * Attributes:
 * 	currPlayer (Player): The player that moved
 * 	ammtMoved (int): The amount of spaces they moved
 * 
 * @author Alex Myers
 */
public class PlayerMovedMessage {
	private Player currPlayer;
	private int ammtMoved;
	
	/**
	 * Constructor: Builds a Player Moved Message with data given
	 * @param player (Player): The player that moved
	 * @param ammtMovedGiven (int): The amount of spaces they moved
	 */
	public PlayerMovedMessage(Player player, int ammtMovedGiven) {
		currPlayer = player;
		ammtMoved = ammtMovedGiven;
	}
	
	/**
	 * Getter: Returns the player that moved
	 * @return Player: the player that moved
	 */
	public Player getPlayer() {
		return currPlayer;
	}
	
	/**
	 * Getter: Returns the amount the player moved
	 * @return int:    the amount the player moved
	 */
	public int getAmmtMoved() {
		return ammtMoved;
	}
}
