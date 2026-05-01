package Monopoly;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Stack;
import Spaces.AIPlayer;
import Spaces.GoSpace;
import Cards.Card;
import Cards.CardEffect;
import Cards.Deck;
import Messages.AiActionMessage;
import Messages.AiLogsEnabledMessage;
import Messages.BankruptcyMessage;
import Messages.CardDrawnMessage;
import Messages.DiceRollResultMessage;
import Messages.GameOverMessage;
import Messages.NextPlayerMessage;
import Messages.PlayerMovedMessage;
import Messages.PurchasePromptMessage;
import Spaces.Jail;
import Spaces.Player;
import Spaces.Property;
import Spaces.Space;

// game state
public class Model extends Observable implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
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
	
	private Deck cardDeck;
	private Card jailCard;
	
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
		makeJailCard();
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
		
		List<String> playerIconsToPickFrom = new ArrayList<>();
		playerIconsToPickFrom.add("boat");    playerIconsToPickFrom.add("car");   playerIconsToPickFrom.add("cop"); playerIconsToPickFrom.add("dog"); playerIconsToPickFrom.add("evil");
		playerIconsToPickFrom.add("thimble"); playerIconsToPickFrom.add("train"); playerIconsToPickFrom.add("wheeler");
		
		
		//instantiate the board of spaces and apply gamesettings
		board = new Board();
		GoSpace goSpace = (GoSpace) board.firstSpace;
		//applying go value to go space
		goSpace.setAmountEarnedWhenPassingGo(gameSettings.getCustomGoValue());
		//apply price multiplier to all properties' rents
		for (Space space : board.getSpaces()) {
		    if (space instanceof Property p) {
		        ArrayList<Integer> oldRents = p.getRentStages();
		        for (int i = 0; i < oldRents.size(); i++) {
		            int newRentVal = (int) (oldRents.get(i) * gameSettings.getPropertyPriceAdjust());
		            oldRents.set(i, newRentVal);
		        }
		    }
		}
		
		cardDeck = new Deck(theme);

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
	
	/**
	 * Returns the game settings object
	 * @return the game settings object
	 */
	public GameSettings getGameSettings() {
		return gameSettings;
	}
	
	/**
	 * Returns all of the spaces in the board as a list
	 * @return a list of all game spaces
	 */
	public List<Space> getSpaces() {
	    return board.getSpaces();
	}
	
	/**
	 * Gets all of  the players in the game as a list 
	 * @return a list of all player objects
	 */
	public List<Player> getPlayers() {
		return players;
	}
	
	/**
	 * Gets the current player object, the one who is playing their turn out
	 * @return the current players object
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	/**
	 * Gets the CHANCE cards, every single one in a stack
	 * @return a stack of chance cards
	 */
	public Stack<Card> getChanceCards(){
		return cardDeck.getChanceCards();
	}
	
	/**
	 * Gets the COMMUNITY CHEST cards, every single one in a stack
	 * @return a stack of community chest cards
	 */	
	public Stack<Card> getCommunityChestCards(){
		return cardDeck.getCommunityChestCards();
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
	
	public void notifyViewGameWinner(Player player) {
		GameOverMessage gameOverMessage = new GameOverMessage(player);
		this.setChanged();
		this.notifyObservers(gameOverMessage);
		this.clearChanged();	
	}

	/**
	 * Sets the amount moved during the last dice roll for hte PREVIOUS dice roll
	 * @param ammtMoved number of spaces moved
	 */
	public void setLastDiceRollAmmt(int ammtMoved) {
		this.lastDiceRollAmmt = ammtMoved;
	}
	
	/**
	 * Grabs the number of spaces the last dice roll moved a player
	 * @return the number of spaced moved
	 */
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
		return attemptsAmmount;
	}
	
	/**
	 * Gets a hash map of all the themes for the game
	 * @return A hash map of all game themes
	 */
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
		
	}
	
	/**
	 * Checks if the game is over or not based on return
	 * @return true if the game is over, false if not
	 */
	public boolean getGameFinished() {
		return this.gameFinished;
	}
	
	/**
	 * Sets the game to end 
	 * @param val boolean parameter to change the game status to
	 */
	public void setGameFinished(boolean val) {
		this.gameFinished = val;
	}
	
	/**
	 * For moving player to jail, using a Card to display and delay
	 * the view to fix a bug. 
	 */
	public void makeJailCard() {
		CardEffect r = (CardEffect & Serializable)(player, model) -> {
			player.putInJail();
		};
			jailCard = new Card("Go Directly\nTo Jail Do Not Pass Go\nDo Not Collect $200","chanceCard4.png" , r);
	}
	
	/**
	 * Grabs the card that send the player to jail
	 * @return the jail card, a card object
	 */
	public Card getGoToJail() {
		return jailCard;
	}
}
