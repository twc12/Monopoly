/**
 * CommunityChest: Represents
 * a community chest space on the monopoly
 * board in the backend
 * 
 * @author Tyler 
 */
package Spaces;

import Cards.Card;
import Monopoly.Model;

public class CommunityChest extends Space {

	/**
	 * Constructor: Builds a instance of the community
	 * chest card pulling space on the board
	 * @param imageFile (Str): The image path for this space
	 */
	public CommunityChest(String imageFile) {
		super("Community Chest");
		this.imageFile = imageFile;
	}

	/**
	 * processSpace(player, model): This function will act on the player
	 * the action of pulling a card from the community chest space 
	 * 
	 * @param Player; the player to act the card on 
	 * @param Modell: used to notify the view 
	 */
	@Override
	public void processSpace(Player player, Model model) {
		System.out.println("[+] ChanceCard: "+player.getPlayerName()+" landed on community chest card");
		Card card = model.getCommunityChestCards().pop();
		if(model.getCommunityChestCards().isEmpty())
			model.regenerateDeck();
		if (!player.isAI()) model.notifyViewCardDrawn(player, card);		
	}
}
