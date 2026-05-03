/**
 * This class holds the Community Chest space object, which when the player lands on it,
 * The space pulls from the models Community Chest card deck and applies that effect to the player. 
 * The view is then notified of this to show accordingly.
 * 
 * @author Tyler Carpenter
 */
package Spaces;

import Cards.Card;
import Monopoly.Model;

public class CommunityChest extends Space {

	/**
	 * Space constructor, that constructs the name and the string for the 
	 * spaces image file 
	 * 
	 * @param imageFile
	 */
	public CommunityChest(String imageFile) {
		super("Community Chest");
		this.imageFile = imageFile;
	}
	
	/**
	 * Processes the space by popping the card from the top of the Community Chest card stack, then 
	 * re-populating the deck if it is empty, then notifying the view of the cards effect.
	 * The controller applies the effect to the player.
	 */
	@Override
	protected void processSpace(Player player, Model model) {
		Card card = model.getCommunityChestCards().pop();
		if(model.getCommunityChestCards().isEmpty())
			model.regenerateDeck();
		if (!player.isAI()) model.notifyViewCardDrawn(player, card);		
	}
}
