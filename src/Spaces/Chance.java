/**
 * Chance: This space represents
 * a chance card space 
 * 
 * @author Tyler
 */

package Spaces;

import Cards.Card;
import Monopoly.Model;

public class Chance extends Space {
	
	/**
	 * Constructor: Initializes a Chance card pulling space 
	 * @param imageFile (Str): The string for this space image
	 */
	public Chance(String imageFile) {
		super("Chance");
		this.imageFile = imageFile;
	}

	/**
	 * processSpace(player, model): This will
	 * act on the player and pull a card and 
	 * act on that card using this player
	 * 
	 * @param Player: The player to act on 
	 * @param Model: uses to notify the view 
	 */
	@Override
	protected void processSpace(Player player, Model model) {
		Card card = model.getChanceCards().pop();
		System.out.println("[+] <Chance>processSpace: "+player.getPlayerName()+" landed on chance card -> "+card.getDescription());
		if(model.getChanceCards().isEmpty())
			model.regenerateDeck();
		if (!player.isAI()) model.notifyViewCardDrawn(player, card);
	}
	
}
