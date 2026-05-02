package Spaces;

import Cards.Card;
import Monopoly.Model;

public class Chance extends Space {
	
	public Chance(String imageFile) {
		super("Chance");
		this.imageFile = imageFile;
	}

	@Override
	protected void processSpace(Player player, Model model) {
		Card card = model.getChanceCards().pop();
		if(model.getChanceCards().isEmpty())
			model.regenerateDeck();
		if (!player.isAI()) model.notifyViewCardDrawn(player, card);
	}
	
}
