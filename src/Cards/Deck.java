/**
 * This file contains the deck object, which creates two
 * stacks of shuffled card objects which can be used to 
 * pop and apply effects to the specified player.
 *
 * @author Tyler Carpenter
 */
package Cards;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;

public class Deck implements Serializable {
	private static final long serialVersionUID = 1L;
	private CardBuilder cards;
	private Stack<Card> chanceCards;
	private Stack<Card> communityChestCards;
	private String theme;

	/**
	 * Constuctor: Builds a deck based on the theme given
	 * 
	 * @param theme (Str): The pirate, standard, or tucson theme
	 */
	public Deck(String theme) {
		this.theme = theme;
		cards = new CardBuilder(theme);
		ArrayList<Card> unShuffled;

		// Shuffle Chance Cards
		unShuffled = cards.getChanceCards();
		chanceCards = shuffle(unShuffled);

		// Shuffle community chest cards
		unShuffled = cards.getCommunityChestCards();
		communityChestCards = shuffle(unShuffled);
	}

	/**
	 * @return Stack of card objects for the chance cards
	 */
	public Stack<Card> getChanceCards() {
		if (chanceCards.isEmpty()) {
			return replenishChanceCards();
		}
		return chanceCards;
	}

	/**
	 * The cards would run out in testing so this was made
	 * 
	 * @return Stack<Card> the chance cards 
	 */
	public Stack<Card> replenishChanceCards() {
		ArrayList<Card> unShuffled = cards.getChanceCards();
		chanceCards = shuffle(unShuffled);
		return chanceCards;
	}

	/**
	 * Getter: for the community chest cards 
	 * 
	 * @return Stack of card objects for the community chest cards
	 */
	public Stack<Card> getCommunityChestCards() {
		if (communityChestCards.isEmpty()) {
			return replenishCommunityChestCards();
		}
		return communityChestCards;
	}

	/**
	 * The cards would run out in testing so this was made
	 * 
	 * @return Stack<Card>: The stack of cards that are community chest 
	 */
	public Stack<Card> replenishCommunityChestCards() {
		ArrayList<Card> unShuffled = cards.getCommunityChestCards();
		chanceCards = shuffle(unShuffled);
		return chanceCards;
	}

	/**
	 * This function will shuffle the list given , then turn it into a stack
	 * and return it 
	 * 
	 * @param unShuffled The list of coards unshuffled
	 * @return Stack<Card> The stacks of shuffled card
	 */
	private Stack<Card> shuffle(ArrayList<Card> unShuffled) {
		Stack<Card> stack = new Stack<>();
		Collections.shuffle(unShuffled);
		stack.addAll(unShuffled);
		return stack;
	}

}
