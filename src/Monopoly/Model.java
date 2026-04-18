package Monopoly;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Stack;

import Cards.Card;
import Cards.Deck;
import Messages.AiActionMessage;
import Messages.CardDrawnMessage;
import Messages.DiceRollResultMessage;
import Messages.NextPlayerMessage;
import Messages.PlayerMovedMessage;
import Messages.PurchasePromptMessage;
import Spaces.Player;
import Spaces.Property;
import Spaces.Space;
import javafx.scene.paint.Color;


// game state
public class Model extends Observable {

	private int totalPlayers = 4; // FOR THE FUTURE THIS SHOULD BE CHANGEABLE BASED ON USER INPUT FROM START SCREEN
	/**
	 * This holds the spaces of the monopoly board
	 */
	public Board board; 
	
	private List<Player> players;
	private Player currentPlayer;
	
	private Stack<Card> chanceCards;
	private Stack<Card> communityChestCards;
	private int lastDiceRollAmmt;
	
	//constructor, initializes board, players
	public Model(View viewClassObj) {
		
		// Create a list of Colors for player objects to pull from (8 max) - Players should have colors and have pieces assigned to them
		List<Color> playerColorsToPickFrom = new ArrayList<>();
		playerColorsToPickFrom.add(Color.RED); playerColorsToPickFrom.add(Color.BLUE); playerColorsToPickFrom.add(Color.GREEN); playerColorsToPickFrom.add(Color.YELLOW); 
		playerColorsToPickFrom.add(Color.PURPLE); playerColorsToPickFrom.add(Color.PINK); playerColorsToPickFrom.add(Color.BLACK); playerColorsToPickFrom.add(Color.ORANGE); 
		
		this.addObserver(viewClassObj);
		board = new Board();
		
		
		Deck deck = new Deck();
		chanceCards = deck.getChanceCards();
		communityChestCards = deck.getCommunityChestCards();

		// Create all the players 
		players = new ArrayList<>();
		for (int i=0; i<totalPlayers; i++) {
			players.add(new Player(i+1,playerColorsToPickFrom.get(i), this));
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
	
	public Stack<Card> getChanceCards(){
		return chanceCards;
	}
	
	public Stack<Card> getCommunityChestCards(){
		return communityChestCards;
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

	/**
	 * notifyViewPurchasePrompt(currPlayer, propertyObj): This function 
	 * will tell the view class that a property could be purchased and it will
	 * notify the player if they want to buy it 
	 * @param currentPlayer (Player): the current player whos turn it is 
	 * @param property (Property): The property that they could buy
	 */
	public void notifyViewPurchasePrompt(Player currPlayer, Property property) {
		PurchasePromptMessage purchasePromptMessage = new PurchasePromptMessage(currPlayer, property);
		this.setChanged();
		this.notifyObservers(purchasePromptMessage);
		this.clearChanged();		
	}
	
	/**
	 * notifyViewCardDrawn(currPlayer, card): 
	 * This function will tell the view class that a card was drawn so it 
	 * should display the card drawn
	 * @param currPlayer (Player): The current player whos turn it is and will have the 
	 * 								cards action acted on (the view doesnt do the "acting on")
	 * @param card (Card): the card that was drawn
	 */
	public void notifyViewCardDrawn(Player currPlayer, Card card) {
	    CardDrawnMessage cardDrawnMessage = new CardDrawnMessage(currPlayer, card);
	    this.setChanged();
	    this.notifyObservers(cardDrawnMessage);
	    this.clearChanged();
	}
	
	/**
	 * notifyViewOfAiAction(theAiActionTaken): This function will notify the view
	 * that an ai took an action and this will have the view show the action taken 
	 * by the ai 
	 * @param theAiActionTaken (String): A BRIEF string explaining the AI action 
	 */
	public void notifyViewOfAiAction(String theAiActionTaken) {
		AiActionMessage aiActionMsg = new AiActionMessage(theAiActionTaken);
	    this.setChanged();
	    this.notifyObservers(aiActionMsg);
	    this.clearChanged();
	}

	public void setLastDiceRollAmmt(int ammtMoved) {
		this.lastDiceRollAmmt = ammtMoved;
	}
	
	public int getLastDiceRollAmmt() {
		return this.lastDiceRollAmmt;
	}
	
}
