package Spaces;

import Cards.Card;
import Monopoly.Model;

public class CommunityChest extends Space {

	public CommunityChest() {
		super("CommunityChestSpace");
	}

	@Override
	protected void processSpace(Player player, Model model) {
		Card card = model.getChanceCards().pop();
		if (!player.isAI()) model.notifyViewCardDrawn(player, card);		
	}
}
