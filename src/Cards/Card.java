/**
 * This file contains the Card class, which is designed
 * to contain a description, and an effect. These effects may be
 * used with apply() to act upon the model or player.
 *
 * @author Tyler Carpenter
 */
package Cards;
import Monopoly.Model;
import Spaces.Player;
import javafx.scene.image.Image;
public class Card {
	private String description;
	private CardEffect effect;

	/**
	 * Constructor Class for the Card Object
	 * 
	 * @param description: the text explaining the cards effect.
	 * @param effect: the code for the actual effect of the card
	 */
	public Card(String description, CardEffect effect) {
		this.description = description;

		this.effect = effect;
	}
	
	/**
	 * Applys the specified code from the CardEffect
	 * 
	 * @param player: player to act upon 
	 * @param model: model for wider scale effects
	 */
	public void apply(Player player, Model model) {
		effect.execute(player, model);
	}
	
	/**
	 * @return The description text
	 */
	public String getDescription() {
		return description;
	}
	

}


