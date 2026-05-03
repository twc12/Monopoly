package Spaces;

import Cards.Card;
import Monopoly.Model;


/**
 * GoToJailSpace: This class represents the 
 * go to jail space in the top right of the monopoly board.
 * When a player lands on this space they should be sent to jail.
 * 
 * @author Jarrod
 * @author Alex
 */
public class GoToJailSpace extends Space {
	
	private Jail jailSpaceObject;
	
	/**
	 * Constructor: This will create a Go To jail space in the 
	 * model. It is given the jail space so it can link to the 
	 * actual jail space to better help move the player in the 
	 * view.
	 * @param jailSpaceInModel (Jail): The actual Jail Space in the model
	 * @param imageFile (String): The file path of the image for this space.
	 */
	public GoToJailSpace(Jail jailSpaceInModel, String imageFile) {
		super("Go to Jail");
		jailSpaceObject = jailSpaceInModel;
		this.imageFile = imageFile;
	}
	
	
	public Jail getJailSpace() {
		return jailSpaceObject;
	}

	
	/**
	 * processSpace(player, model): This function will show 
	 * a "GOING TO JAIL" type of card on the screen to delay 
	 * the animation long enough for the player circle icon in 
	 * the view to get to the right space before the player is
	 * actually moved to the jail space on the view. 
	 * THIS DELAY IS CRUCIAL
	 * 
	 * @param player (Player): The player being sent to jail
	 * @param model (Model): The instance of the model we are playing so we can 
	 * 						notify the view to show a card 
	 */
	@Override
	public void processSpace(Player player, Model model) {
		Card card = model.getGoToJailCard();
		if (!player.isAI()) model.notifyViewCardDrawn(player, card);
	}
	

}