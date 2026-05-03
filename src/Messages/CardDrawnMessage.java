/**
 * The purpose of this is to notify the view to display
 * card info
 * 
 * @author Tyler Carpenter
 */
package Messages;

import Cards.Card;
import Spaces.Player;

public class CardDrawnMessage {
	private Player player;
	private Card card;

	/**
	 * Constructor: This builds a Card drawn message
	 * 
	 * @param player (Player): The player to act on
	 * @param card   (Card): The card drawn
	 */
	public CardDrawnMessage(Player player, Card card) {
		this.player = player;
		this.card = card;
	}

	/**
	 * Gets the player object that pulled the card
	 * 
	 * @return the player object
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Gets the card being pulled
	 * 
	 * @return the card object pulled
	 */
	public Card getCard() {
		return card;
	}
}
