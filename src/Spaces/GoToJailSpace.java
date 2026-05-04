package Spaces;

import Cards.Card;
import Monopoly.Model;

public class GoToJailSpace extends Space {
	
	private Jail jailSpaceObject;
	
	/**
	 * represents the space that will send players how land on it to jail
	 * @param jailSpaceInModel - the jail object itself, a separate object
	 * @param imageFile - the path for the image file that is displayed on this space
	 */
	public GoToJailSpace(Jail jailSpaceInModel, String imageFile) {
		super("Go to Jail");
		jailSpaceObject = jailSpaceInModel;
		this.imageFile = imageFile;
	}
	
	/**
	 * @return Gets the jail space object
	 */
	public Jail getJailSpace() {
		return jailSpaceObject;
	}

	
	/**
	 * Sends the player to jail once they land on it and notifies the GUI
	 * @param player - the player we're sending to jail, ai or human
	 * @param model - the game model
	 */ 
	@Override
	public void processSpace(Player player, Model model) {
		Card card = model.getGoToJail();
		if (!player.isAI()) model.notifyViewCardDrawn(player, card);
	}
	

}