package monopoly;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


// game state
public class Model extends Observable {

	private int totalPlayers = 4;
	public Board board;
	private List<Player> players;
	private Player currentPlayer;
	
	
	//constructor, initializes board, players
	public Model(View viewClassObj) {
		this.addObserver(viewClassObj);
		board = new Board();
		
		players = new ArrayList<>();
		
		for (int i=0; i<totalPlayers; i++) {
			players.add(new Player(i+1,this));
		}
		
		currentPlayer = players.get(0);
	}
	
	/**
	 * setCurrentPlayerToNext(): This function will 
	 * be called when the controller handles the end of a turn.
	 * I think for a end of turn it JUST means that we move
	 * the currentPlayer to the next one in the list and tell
	 * the view to change the player card shown in the bottom left
	 */
	public void setCurrentPlayerToNext() {
		// reset current players ability to roll dice for their next turn 
		currentPlayer.setIsDoneRollingDice(false);
		
		int currPlayerIndex = players.indexOf(currentPlayer);
		int nextPlayerIndex;
		// if the index is the last player then wrap around
		if (currPlayerIndex == players.size()-1) {
			nextPlayerIndex = 0;
		}
		else {
			nextPlayerIndex = currPlayerIndex+1;
		}
		currentPlayer = players.get(nextPlayerIndex);
		
		this.notifyViewOfNextPlayersTurn(currentPlayer);
	}
	
	
	public List<Space> getSpaces() {
	    return board.getSpaces();
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	/**
	 * notifyViewOfDiceResult(dice1Result, dice2Result): This function
	 * will tell the view object the results of the players dice roll 
	 * so it can animate that on the view and show the viewer
	 * @param dice1Result (int): The result of dice 1 
	 * @param dice2Result (int): The result of dice 2
	 */
	public void notifyViewOfDiceResult(int dice1Result, int dice2Result) {
		// TESTING
		System.out.println("model: notifyViewOfDiceResult("+dice1Result+","+dice2Result+") called");
		DiceRollResultMessage diceResultMessage = new DiceRollResultMessage(dice1Result, dice2Result);
		this.setChanged();
		this.notifyObservers(diceResultMessage);
		this.clearChanged();
	}
	
	/**
	 * notifyViewOfPlayerMoved(currPlayer, spaceTheyMovedTo): This function 
	 * will tell the view that the player moved. It will tell them how many 
	 * spaces they moved so that the view can animate the player moving.
	 * 
	 * This function should only be used for normal forward movements
	 * 
	 * @param currPlayer (Player): The player that moved
	 * @param ammtMoved (int): The ammount of spaces they moved
	 */
	public void notifyViewOfPlayerMoved(Player currPlayer, int ammtMoved) {
		PlayerMovedMessage movementMessage = new PlayerMovedMessage(currPlayer, ammtMoved);
		this.setChanged();
		this.notifyObservers(movementMessage);
		this.clearChanged();
	}
	
	/**
	 * notifyViewOfNextPlayersTurn(theNextPlayer): This function 
	 * will tell the view class that its the next players turn so it can 
	 * pull down the next players into into the bottom left player card spot
	 * @param theNextPlayerObj (Player): the next player whos turn it is NOW
	 */
	public void notifyViewOfNextPlayersTurn(Player theNextPlayer) {
		NextPlayerMessage nextPlayerMessage = new NextPlayerMessage(theNextPlayer);
		this.setChanged();
		this.notifyObservers(nextPlayerMessage);
		this.clearChanged();
	}
	
}
