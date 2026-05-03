package Monopoly;

import Cards.Card;
import Cards.Deck;
import Spaces.Chance;
import Spaces.CostSpace;
import Spaces.FreeParking;
import Spaces.GoSpace;
import Spaces.Jail;
import Spaces.Player;
import Spaces.Property;
import Spaces.Railroad;
import Spaces.RealEstate;
import Spaces.Space;
import Spaces.TaxSpace;
import Spaces.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
public class Controller {
	
	public enum JAIL_CHOICE{
		ROLL_DUBLES, PAY_FIFTY, OUT_OF_JAIL_CARD
	}

	public Model model;
	
	//contructor for JUNIT, doesn't create a view
	public Controller() {
		this.model = new Model();
	}
	
	/**
	 * Constructor: This constructor will be called 
	 * when the user selects "Load Game" in the start menu.
	 * This constructor will load the model object from the file given 
	 * and then it will create a model and start the game from this point in 
	 * that games saved history with its entire state as is was at saving
	 * 
	 * @param viewClassObj (View): This is the view class object that will be added 
	 * 								as a "Observer" of the model 
	 * @param selectedFile (File): This file is expected to end in ".monopoly" so we know
	 * 								for sure that is has a model object we need.
	 */
	public Controller(View viewClassObj, File selectedFile) {
		ObjectInputStream objInputStream = null;
		try {
			objInputStream = new ObjectInputStream(new FileInputStream(selectedFile));
			Model myModel = null;
			myModel = (Model) objInputStream.readObject();
			myModel.deleteObservers();
			myModel.addObserver(viewClassObj);
			model = myModel;
			System.out.println("Log: Hopefullly the model is loaded now");
			objInputStream.close();
			if (model.getGameSettings().getAmountOfAIPlayers()> 0) {
				model.notifyViewAILogsEnabled();
			}
		} catch (Exception e){
			System.out.println("Log: File not found/io exception when loading model from file. Will create default game");
			model = new Model(viewClassObj);
			return;
		}
	}
		
	public Controller(View viewClassObj) {
		model = new Model(viewClassObj);
	}

	/**
	 * Dedicated method to compute a dice roll and notify the UI of it
	 * @return (int[]) an integer array of two dice roll results
	 */
	public int[] diceRollGeneration() {
		Random rand = new Random();
		int dice1 = rand.nextInt(6) + 1;
		int dice2 = rand.nextInt(6) + 1;
		model.notifyViewOfDiceResult(dice1, dice2);
		System.out.println("Log: Dice roll computed: " + dice1 +", " + dice2); // debugging
		return new int[] {dice1, dice2};
	}
	
	/**
	 * rollDice(player): This function is given a player it rolls dice
	 * for and it will randomly generate dice rolls then send that message to the view
	 * then it will move the player, then notify the view again.
	 * The player.move function will account for it the player passes go
	 * @param player (Player): The current player rolling the dice 
	 */
	public void rollDice(Player player) {
		int[] roll = diceRollGeneration();
		int dice1 = roll[0];
		int dice2 = roll[1];
	
		// if the player did not get doubles then they are DONE ROLLING DICE 
		if (dice1 != dice2) {
			player.setIsDoneRollingDice(true);
		}
		
		int ammtMoved = dice1+dice2;
		model.setLastDiceRollAmmt(ammtMoved);
		player.move(ammtMoved);

	}
	
	/**
	 * Processes all logic for players while in jail, 3 options are given to get out,
	 * including using a "get ouf of jail free" card, paying a $50 fee, or rolling doubles where they
	 * have three attempts to roll doubles before being forced to pay the $50 fee
	 * @param playerInjail (Player) who is currently trying to get out of jail
	 * @param choice (JAIL_CHOICE) the choice the player has made by clicking on a button in the view
	 */
	public void processJailLogic(Player playerInjail, JAIL_CHOICE choice) {
		// When the player chooses to try and roll doubles to get out of jail
		if (choice == choice.ROLL_DUBLES) {
			System.out.println("Log: Player chose to attempt rolling doubles to get out of jail."); // debugging
			int[] diceRoll = diceRollGeneration();
			int dice1 = diceRoll[0];
			int dice2 = diceRoll[1];

			// If the attempt succeeded
			if (dice1 == dice2) {
				playerInjail.getOutOfJail();
				playerInjail.move(dice1 + dice2);

			}

			// If the attempt did not succeed
			else {
				System.out.println("Log: The attempt to get out of jail failed."); // debugging
				// Increments attempts to get out for this player
				int currentAttempts = getAmmtOfJailAttempts(playerInjail) + 1;
				model.board.getJailSpace().playerAttemptsToGetOutMapping.put(playerInjail, currentAttempts);

				// Forces the $50 payment if it didn't succeed
				if (currentAttempts >= 3) {
					System.out.println("Log: Player has failed 3 get out of jail attempts and was charged $50 to get out"); // debugging
					pay50(playerInjail);
					playerInjail.getOutOfJail();
					model.setCurrentPlayerToNext();
				}
				
				else {
					System.out.println("Log: Player has " + currentAttempts + "to get out of jail."); // debugging
					model.setCurrentPlayerToNext();
				}
			}
		}

		// Player uses a get out of jail free card, removes it
		else if (choice == choice.OUT_OF_JAIL_CARD) {
			System.out.println("Log: Player has chosen to use a get out of jail free card"); // debugging
			playerInjail.removeJailCard();
			playerInjail.getOutOfJail();
			rollDice(playerInjail);
		}

		// Player pays $50, gets out of jail
		else if (choice == choice.PAY_FIFTY) {
			System.out.println("Log: Player has chosen to pay $50");
			pay50(playerInjail);
			playerInjail.getOutOfJail();
			if (model.getGameSettings().getFreeParkingRule() == true) {
				model.addToFreeParkingFunds(50);
			}
			rollDice(playerInjail);
		}
	}

	/**
	 * This function will have the player pay 50 dollars to the center pot if its enables
	 * when they paid $50 to get out of jail.
	 * @param player (Player): The player trying to get out of jail
	 */
	private void pay50(Player player) {
		player.addCash(-50);
		if (model.getGameSettings().getFreeParkingRule()) {
			model.addToFreeParkingFunds(50);
		}
	}
	
	/**
	 * Processes the purchase of this property
	 * @param player The object of the player making a purchase
	 * @param property The object of the property being purchased
	 */
	public void purchaseProperty (Player player, Property property) {
		property.purchaseProperty(player, model);		
	}
		
	/**
	 * This is called from the view when the player presses "end turn"
	 * This function will move the models current player to the next on 
	 * in the list of players.
	 */
	public void processEndTurn() {
		
		Player originalPlayer = getCurrentPlayer();
		
	    while (true) {
	        model.setCurrentPlayerToNext();
	        Player currentPlayer = getCurrentPlayer();

	        //If looped back to the original player, then that mean's they're the last player=winner
	        if (currentPlayer == originalPlayer) {
	            model.setGameFinished(true);
	            model.notifyViewGameWinner(currentPlayer);
	            model.notifyViewOfInfoMessage("You did it!");
	            return;
	        }

	        //Normal case where the next player goes, as long as theyre still in the game
	        if (!currentPlayer.getGameOver()) {
	            return;
	        }
	    }
	}

	/**
	 * This function applies the card affect to the player from the `cardBuilder` class
	 * @param card 
	 * @param player
	 */
	public void resolveCard(Card card, Player player) {
		card.apply(player, model);
	}

	/**
	 * Getter: This function is only used
	 * when the view needs to save the model to a file 
	 * 
	 * !!! THIS SHOULD NOT BE USED FOR ANYTHING ELSE !!!
	 * !!! THIS SHOULD NOT BE USED FOR ANYTHING ELSE !!!
	 * 
	 * @return Model: The loaded model from a file 
	 */
	public Model getModel() {
		return model; 
	}

	/**
	 * Gets the total amount of spaces on the board
	 * @return an integer, the total number of spaces
	 */
	public int getTotalSpaces() {
		return model.board.getTotalSpaces();
	}
	
	/**
	 * getSpaces(): Returns a list of Space objects
	 * that make up the board
	 * @return List<Space>: All the spaces on the board
	 */
	public List<Space> getSpaces(){
		return model.board.getSpaces();
	}
	
	/**
	 * Grabs the board width 
	 * @return an integer, representing the width of the board
	 */
	public int getBoardWidth() {
		return model.board.getBoardWidth();
	}
	
	/**
	 * Grabs the first space on the board
	 * @return An object representing the first space
	 */
	public Space getFirstSpace() {
		return model.board.getFirstSpace();
	}
	
	/**
	 * Grabs the object of the current player
	 * @return A player object representing the current player
	 */
	public Player getCurrentPlayer() {
		return model.getCurrentPlayer();
	}
	
	/**
	 * Pulls a list of ALL the players in the game
	 * @return A List of every player object
	 */
	public List<Player> getAllPlayers(){
		return model.getPlayers();
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
		int attemptsAmmount = model.getAmmtOfJailAttempts(playerInJail);
		return attemptsAmmount;
	}
	
	
	//called by view when player successfully chooses to build on their property
	public void buildHouseHotel(Player player, RealEstate property) {
	        property.buildHouseHotel(player, model);
	}

	//START-SCREEN METHODS, 
	public String getThemeString() {
		return model.getGameSettings().getActiveThemeString();
	}
	
	/**
	 * This function is called by the view after the user presses 
	 * "Start Game". This function accepts all the settings the user entered
	 * at the start menu. This function just passes it down to the model
	 * to hold it  
	 * @param initGameSettingsObj (Game Settings): all the game settings that the user set in the 
	 * 												start menu
	 */
	public void initializeGameSettings(GameSettings initGameSettingsObj) {
		model.setGameSettingsObj(initGameSettingsObj);
	}
	
	/**
	 * When a player selects a property they want to acquire and clicks trade.
	 * Executes a trade initiated by the traderPlayer where the targetPlayer is only selling
	 * a single property. The traderPlayer can offer a mix of property, cash, and jailfree cards for it.
	 * @param traderPlayer
	 * @param targetPlayer
	 * @param targetProperty
	 * @param traderPropertiesOffer
	 * @param traderCashOffer
	 * @param traderJailFreeCardsOffer
	 */
	public void executeTrade(Player traderPlayer, Player targetPlayer, Property targetProperty, List<Property> traderPropertiesOffer, int traderCashOffer, int traderJailFreeCardsOffer) {		
		
		if (model.getGameSettings().getTradingEnabled() == false) {
			model.notifyViewOfInfoMessage("Trading is disabled, trade not executed!");
			return;
		}
		
		//moving ownership of trader's properties to target
		for (Property myProperty : traderPropertiesOffer) {
			myProperty.setOwner(targetPlayer);
			targetPlayer.addProperty(myProperty);
			traderPlayer.removeProperty(myProperty);
		}
		
		//transfer cash if valid amount from trader to target, avoid potential bankruptcy trigger
		if (traderPlayer.getCashAmmt() >= traderCashOffer) {
			targetPlayer.addCash(traderCashOffer);
			traderPlayer.addCash(-traderCashOffer);
		}

		//transfer jailfree cards if valid amount
		if (traderPlayer.getAmmtOfGOOJCards() >= traderJailFreeCardsOffer) {
			targetPlayer.addJailCard(traderJailFreeCardsOffer);
			traderPlayer.removeJailCard(traderJailFreeCardsOffer);
		}
		
		//trader acquires target property
		targetProperty.setOwner(traderPlayer);
		traderPlayer.addProperty(targetProperty);
		targetPlayer.removeProperty(targetProperty);
	}	
}
