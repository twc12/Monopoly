package Spaces;

import Cards.Card;
import Monopoly.Model;

public class Chance extends Space {
	
	public Chance() {
		super("Chance");
	}

	@Override
	protected void processSpace(Player player, Model model) {
		Card card = model.getChanceCards().pop();
		if (!player.isAI()) model.notifyViewCardDrawn(player, card);
	}
	
}
