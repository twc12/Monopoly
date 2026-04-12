package monopoly;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


// game state
public class Model extends Observable {

	private int totalPlayers = 4;
	public Board board;
	private List<Player> players;
	
	
	//constructor, initializes board, players
	public Model() {
		board = new Board();
		
		players = new ArrayList<>();
		
		
		for (int i=0; i<totalPlayers; i++) {
			players.add(new Player(i+1,this));
		}
	}
	
	
	public List<Space> getSpaces() {
	    return board.getSpaces();
	}
	
}
