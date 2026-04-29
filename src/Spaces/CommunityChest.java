package Spaces;

import Cards.Card;
import Monopoly.Model;

public class CommunityChest extends Space {

	public CommunityChest(String imageFile) {
		super("Community Chest");
		this.imageFile = imageFile;
	}

	@Override
	protected void processSpace(Player player, Model model) {
		Card card = model.getChanceCards().pop();
		if (!player.isAI()) model.notifyViewCardDrawn(player, card);		
	}
}
