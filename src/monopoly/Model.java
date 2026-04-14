package monopoly;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Stack;


// game state
public class Model extends Observable {

	private int totalPlayers = 4;
	public Board board;
	private List<Player> players;
	
	private Stack<Card> chanceCards;
	private Stack<Card> communityChestCards;
	
	
	//constructor, initializes board, players
	public Model() {
		board = new Board();
		
		
		Deck deck = new Deck();
		chanceCards = deck.getChanceCards();
		communityChestCards = deck.getCommunityChestCards();

		
		players = new ArrayList<>();
		
		
		for (int i=0; i<totalPlayers; i++) {
			players.add(new Player(i+1,this));
		}
	}
	
	
	public List<Space> getSpaces() {
	    return board.getSpaces();
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
}
