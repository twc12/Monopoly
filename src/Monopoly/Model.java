package Monopoly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Stack;

import Spaces.AIPlayer;
import Cards.Card;
import Cards.Deck;
import Messages.AiActionMessage;
import Messages.AiLogsEnabledMessage;
import Messages.CardDrawnMessage;
import Messages.DiceRollResultMessage;
import Messages.GoToJailMessage;
import Messages.NextPlayerMessage;
import Messages.PlayerMovedMessage;
import Messages.PurchasePromptMessage;
import Spaces.Jail;
import Spaces.Player;
import Spaces.Property;
import Spaces.Space;
import javafx.scene.paint.Color;


// game state
public class Model extends Observable {
	
	/**
	 * This variable must be filled in by a future implementation where the theme is 
	 * passed all the way down from the view into this models constructor and then changes 
	 * this theme variable. Right now for player icons, alex needs it hard coded BUT you should be able
	 * the change it later. just make sure that the player icons pngs are named the same as the ones in
	 * the playerIconsToPickFrom variable with "PlayerIcon.png" added to the end 
	 */
	private String theme;// = "standardTheme";
	private int totalHumanPlayers;
	private int totalAiPlayers;
	/**
	 * This holds the spaces of the monopoly board
	 */
	public Board board; 
	
	private List<Player> players;
	private Player currentPlayer;
	
	private Stack<Card> chanceCards;
	private Stack<Card> communityChestCards;
	
	private int lastDiceRollAmmt;

	private GameSettings gameSettings; // Placeholder for Jake
	
	private HashMap<String,Boolean> themes = new HashMap<>();
	private int turnCounter = 1;
	private boolean gameFinished = false;
	
	
	//constructor for JUNIT, doesn't create a view
	public Model() {
		this(null); 
		}
	
	//constructor, initializes board, players
	public Model(View viewClassObj) {

		gameSettings = null; // we havent gotten it yet from the view 
		//	required for JUNIT not having view
		if (viewClassObj != null) {
			this.addObserver(viewClassObj);
		}
		
		// THE POST INIT WILL BE CALLED ONCE GAME SETTINGS ARE RECIVED
	}
	
	/**
	 * This function accepts the game settings after the user presses
	 * "Start Game" in the start menu
	 * @param initGameSettingsObj (GameSettings): the game settings the user potentially 
	 * 												changed in the start menu
	 */
	public void setGameSettingsObj(GameSettings initGameSettingsObj) {
		gameSettings = initGameSettingsObj;
		post_init(); // LOOK A FEW LINES DOWN
	}
	
	/**
	 * post_init(): After the (potentially custom) game settings are 
	 * received from the view when the user presses "Start game"
	 * in the start menu then this function will run which will initialize
	 * the game creating the correct number of players
	 */
	private void post_init() {
		totalHumanPlayers = gameSettings.getAmountOfPlayers();
		totalAiPlayers = gameSettings.getAmountOfAIPlayers();
		if (totalAiPlayers > 0) this.notifyViewAILogsEnabled();
		
		theme = gameSettings.getActiveThemeString();
		
		// Create a list of Colors for player objects to pull from (8 max) - Players should have colors and have pieces assigned to them
		List<Color> playerColorsToPickFrom = new ArrayList<>();
		playerColorsToPickFrom.add(Color.RED); playerColorsToPickFrom.add(Color.BLUE); playerColorsToPickFrom.add(Color.GREEN); playerColorsToPickFrom.add(Color.YELLOW); 
		playerColorsToPickFrom.add(Color.PURPLE); playerColorsToPickFrom.add(Color.PINK); playerColorsToPickFrom.add(Color.BLACK); playerColorsToPickFrom.add(Color.ORANGE); 
		
		
		List<String> playerIconsToPickFrom = new ArrayList<>();
		playerIconsToPickFrom.add("boat"); playerIconsToPickFrom.add("car"); playerIconsToPickFrom.add("cop"); playerIconsToPickFrom.add("dog"); playerIconsToPickFrom.add("evil");
		
		board = new Board();
		
		Deck deck = new Deck();
		chanceCards = deck.getChanceCards();
		communityChestCards = deck.getCommunityChestCards();

		// Create all the HUMAN players 
		players = new ArrayList<>();
		for (int i=0; i<totalHumanPlayers; i++) {
			players.add(new Player(i+1,playerIconsToPickFrom.get(i), theme, this));
		}
		// Adding AI PLAYERS  
		for (int i=0; i<totalAiPlayers; i++) {
			players.add(new AIPlayer(totalHumanPlayers+i+1,playerIconsToPickFrom.get(totalHumanPlayers+i), theme, this));
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
		
		this.turnCounter++;
		this.notifyViewOfNextPlayersTurn(currentPlayer);
	}

	/**
	 * Adds money to the free parking pool
	 * @param moneyToAdd amount to add
	 */
	public void addToFreeParkingFunds(int moneyToAdd) {
		board.getFreeParking().addCashToFreeParkingReward(moneyToAdd);
	}
	
	public GameSettings getGameSettings() {
		return gameSettings;
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
	
	public void setChanceCards(Stack<Card> stack) {
		this.chanceCards = stack;
	}
	
	public void setCommunityChestCards(Stack<Card> stack) {
		this.communityChestCards = stack;
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
		AiActionMessage aiActionMsg = new AiActionMessage(">Turn " + this.turnCounter + ":\n" + theAiActionTaken);
	    this.setChanged();
	    this.notifyObservers(aiActionMsg);
	    this.clearChanged();
	}

	/**
	 * Notifies the view that a player has landed on the "go to jail" space
	 * @param player (Player): The player going to jail
	 */
	public void notifyViewOfPlayerGoingToJail(Player player) {
		GoToJailMessage jailMsg = new GoToJailMessage(player);
		this.setChanged();
		this.notifyObservers(jailMsg);
		this.clearChanged();	
	}

	public void notifyViewOfPlayerTryingToGetOutOfJail(Player player) {

	}

	/**
	 * Generic notify view method for displaying string message to the view (ex: "Player X charged rent!")
	 * @param message
	 */
	public void notifyViewOfInfoMessage(String message) {
	    this.setChanged();
	    this.notifyObservers(message);
	    this.clearChanged();
	}
	
	/**
	 * Generic notify view method for displaying string message to the view (ex: "Player X charged rent!")
	 * @param message
	 */
	public void notifyViewAILogsEnabled() {
		AiLogsEnabledMessage aiLogsEnabledMsg = new AiLogsEnabledMessage();
		this.setChanged();
		this.notifyObservers(aiLogsEnabledMsg);
		this.clearChanged();	
	}
	
	public void notifyViewBankruptcy(Player player, int ammtPayed, int buildingsSoldCount, List<Property> propertiesSold, boolean gameOver) {
		BankruptcyMessage bankruptcyMessage = new BankruptcyMessage(player, ammtPayed, buildingsSoldCount, propertiesSold, gameOver);
		this.setChanged();
		this.notifyObservers(bankruptcyMessage);
		this.clearChanged();	
	}
	


	public void setLastDiceRollAmmt(int ammtMoved) {
		this.lastDiceRollAmmt = ammtMoved;
	}
	
	public int getLastDiceRollAmmt() {
		return this.lastDiceRollAmmt;
	}
	
	/**
	 * Getter: it will ask the Jail space in the model how many times a player has 
	 * attempted to get out of jail.
	 * This function is only called in the view if the player.isInJail() return true
	 * so that means they MUST be in the jail.playerAttemptsToGetOutMapping. If not
	 * that is major problem, send debug message hopefully they see
	 * @param playerInJail (Player): The player that should be in jail
	 * @return int: the number of times the player has tried to roll doubles to get out of jail
	 */
	public int getAmmtOfJailAttempts(Player playerInJail) {
		int attemptsAmmount = board.jailSpace.getAmmtOfJailAttempts(playerInJail);
		// if the returned value was -1 that means the player given is not in the jail space, report error hopefully they see 
		if (attemptsAmmount == -1) {
			notifyViewOfAiAction("PLAYER GIVEN WAS NOT IN THE JAIL MAPPING DEBUG DEBUG DEBUG");
			return -1;
		}
		return attemptsAmmount;
	}
	
	public HashMap<String, Boolean> getThemes() {
		return themes;
	}

	/**
	 * putPlayerInJail(player): This function will be called when the player 
	 * should be put into jail, this method should be called for when a player rolls 3 doubles 
	 * in a row, or pulls a card that says go to jail, or lands on the Go To Jail space 
	 * @param player (Player): The player object that is going to be placed in jail
	 */
	public void putPlayerInJail(Player player) {
		// Remove the player from current space
		Space playersCurrentSpace = player.getCurrentSpace();
		playersCurrentSpace.getPlayersOnSpace().remove(player);
		
    	// Move the player to the jail space
		Jail jailSpace = board.getJailSpace();
		jailSpace.addPlayerToJail(player); // adds the player to the jail mapping of attempts to get out 
		
    	notifyViewOfPlayerGoingToJail(player);
		
	}
	
	public boolean getGameFinished() {
		return this.gameFinished;
	}
	
	public void setGameFinished(boolean val) {
		this.gameFinished = val;
	}

}
