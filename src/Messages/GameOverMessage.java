package Messages;

import Spaces.Player;

/**
 * Sent to the view when it detects player is the last remaining player in
 * the turn cycle.
 * @author: Jake
 */
public class GameOverMessage {

	private Player player;
	
	/**
	 * Constructor. Pass in the player who won.
	 * @param winner
	 */
	public GameOverMessage(Player winner) {
		this.player=winner;
	}
	
	/**
	 * Gets the name of the player who won
	 * @return String the player 
	 */
	public String getPlayerName() {
		return player.toString();
	}

}
