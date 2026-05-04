package Spaces;

import Cards.Card;
import Monopoly.Model;

public class GoToJailSpace extends Space {
	
	private Jail jailSpaceObject;
	
	public GoToJailSpace(Jail jailSpaceInModel, String imageFile) {
		super("Go to Jail");
		jailSpaceObject = jailSpaceInModel;
		this.imageFile = imageFile;
	}
	
	public Jail getJailSpace() {
		return jailSpaceObject;
	}

	
	@Override
	public void processSpace(Player player, Model model) {
		Card card = model.getGoToJail();
		if (!player.isAI()) model.notifyViewCardDrawn(player, card);
	}
	

}