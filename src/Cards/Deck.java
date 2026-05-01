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

public class Deck implements Serializable{
	private static final long serialVersionUID = 1L;
	private CardBuilder cards;
	private Stack<Card> chanceCards;
	private Stack<Card> communityChestCards;
	
	public Deck(){
		cards = new CardBuilder("standard");
		ArrayList<Card> unShuffled;
		
		// Shuffle Chance Cards
		unShuffled = cards.getChanceCards();
		chanceCards = shuffle(unShuffled);
		
		// Shuffle community chest cards
		unShuffled = cards.getCommunityChestCards();
		communityChestCards = shuffle(unShuffled);
	}
	
	public Stack<Card> getChanceCards(){
		if (chanceCards.isEmpty()) {
			return replenishChanceCards();
		}
		return chanceCards;
	}
	
	/**
	 * The cards would run out in testing so this was made
	 * @return
	 */
	public Stack<Card> replenishChanceCards(){
		ArrayList<Card> unShuffled = cards.getChanceCards();
		chanceCards = shuffle(unShuffled);
		return chanceCards;
	}
	
	public Stack<Card> getCommunityChestCards(){
		if (communityChestCards.isEmpty()) {
			return replenishCommunityChestCards();
		}
		return communityChestCards;
	}
	
	/**
	 * The cards would run out in testing so this was made
	 * @return
	 */
	public Stack<Card> replenishCommunityChestCards(){
		ArrayList<Card> unShuffled = cards.getCommunityChestCards();
		chanceCards = shuffle(unShuffled);
		return chanceCards;
	}
	
	
	private Stack<Card> shuffle(ArrayList<Card> unShuffled) {
		Stack<Card> stack = new Stack<>();
		Collections.shuffle(unShuffled);
		stack.addAll(unShuffled);
		return stack;
	}
	
}
