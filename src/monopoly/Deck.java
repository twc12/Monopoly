/**
 * This file contains the deck object, which creates two
 * stacks of shuffled card objects which can be used to 
 * pop and apply effects to the specified player.
 *
 * @author Tyler Carpenter
 */
package monopoly;
import java.util.*;

public class Deck {
	private CardBuilder cards;
	private Stack<Card> chanceCards;
	private Stack<Card> communityChestCards;
	
	public Deck(){
		cards = new CardBuilder();
		ArrayList<Card> unShuffled;
		
		// Shuffle Chance Cards
		unShuffled = cards.getChanceCards();
		chanceCards = shuffle(unShuffled);
		
		// Shuffle community chest cards
		unShuffled = cards.getCommunityChestCards();
		communityChestCards = shuffle(unShuffled);
	}
	
	public Stack<Card> getChanceCards(){
		return chanceCards;
	}
	
	public Stack<Card> getCommunityChestCards(){
		return communityChestCards;
	}
	
	
	private Stack<Card> shuffle(ArrayList<Card> unShuffled) {
		Stack<Card> stack = new Stack<>();
		Collections.shuffle(unShuffled);
		stack.addAll(unShuffled);
		return stack;
	}
}
