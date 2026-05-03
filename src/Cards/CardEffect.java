/**
 * This file contains the Interface object for card effects, for 
 * use of lambda expressions to put effect in declaration arguments.
 *
 * @author Tyler Carpenter
 */
package Cards;

import Monopoly.Model;
import Spaces.Player;

public interface CardEffect {

	/**
	 * executes given code
	 * 
	 * @param player: Specified player object
	 * @param model:  Model class for larger scale interaction
	 */
	void execute(Player player, Model model);
}
