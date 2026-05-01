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
	
	/**
	 * @return Stack of card objects for the chance cards
	 */
	public Stack<Card> getChanceCards(){
		return chanceCards;
	}
	
	/**
	 * @return Stack of card objects for the community chest cards
	 */
	public Stack<Card> getCommunityChestCards(){
		return communityChestCards;
	}
	
	private Stack<Card> shuffle(ArrayList<Card> unShuffled) {
		Stack<Card> stack = new Stack<>();
		Collections.shuffle(unShuffled);
		stack.addAll(unShuffled);
		return stack;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		
	}
}
