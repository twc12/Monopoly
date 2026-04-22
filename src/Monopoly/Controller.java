package Monopoly;

import java.util.List;

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
		System.out.println("Dice roll computed: " + dice1 +", " + dice2); // debugging
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
			System.out.println("Player chose to attempt rolling doubles to get out of jail."); // debugging
			int[] diceRoll = diceRollGeneration();
			int dice1 = diceRoll[0];
			int dice2 = diceRoll[1];

			// If the attempt succeeded
			if (dice1 == dice2) {
				System.out.println("The attempt to get out of jail succeded."); // debugging
				playerInjail.getOutOfJail();
				System.out.println("The player has moved: " + dice1 + dice2);
				playerInjail.move(dice1 + dice2);
			}

			// If the attempt did not succeed
			else {
				System.out.println("The attempt to get out of jail failed."); // debugging
				// Increments attempts to get out for this player
				int currentAttempts = getAmmtOfJailAttempts(playerInjail) + 1;
				model.board.getJailSpace().playerAttemptsToGetOutMapping.put(playerInjail, currentAttempts);

				// Forces the $50 payment if it didn't succeed
				if (currentAttempts >= 3) {
					System.out.println("Player has failed 3 get out of jail attempts and was charged $50 to get out"); // debugging
					pay50(playerInjail);
					playerInjail.getOutOfJail();
					model.setCurrentPlayerToNext();
				}
				
				else {
					System.out.println("Player has " + currentAttempts + "to get out of jail."); // debugging
					model.setCurrentPlayerToNext();
				}
			}
		}

		// Player uses a get out of jail free card, removes it
		else if (choice == choice.OUT_OF_JAIL_CARD) {
			System.out.println("Player has chosen to use a get out of jail free card"); // debugging
			playerInjail.removeJailCard();
			playerInjail.getOutOfJail();
			rollDice(playerInjail);
		}

		// Player pays $50, gets out of jail
		else if (choice == choice.PAY_FIFTY) {
			System.out.println("Player has chosen to pay $50");
			pay50(playerInjail);
			playerInjail.getOutOfJail();
			if (model.getGameSettings().getFreeParkingRule() == true) {
				model.addToFreeParkingFunds(50);
			}
			rollDice(playerInjail);
		}
	}

	private void pay50(Player player) {
		player.addCash(-50);
		if (model.getGameSettings().getFreeParkingRule()) {
			model.addToFreeParkingFunds(50);
		}
	}
	
	public void purchaseProperty (Player player, Property property) {
		property.purchaseProperty(player, model);		
	}
		
	/**
	 * This is called from the view when the player presses "end turn"
	 * This function will move the models current player to the next on 
	 * in the list of players.
	 * 
	 * This function doesnt accept a currentPlayer parameter because 
	 * this controller can get the current player anyways 
	 * 
	 * What does it mean to end a turn?
	 * Is it just changing the "currentPlayer" to the next one?
	 * 	- if so then great!
	 */
	public void processEndTurn() {
		model.setCurrentPlayerToNext();
	}

	/**
	 * This function applies the card affect to the player from the `cardBuilder` class
	 * @param card
	 * @param player
	 */
	public void resolveCard(Card card, Player player) {
		card.apply(player, model);
		if(model.getChanceCards().isEmpty()) {
			model.setChanceCards(new Deck().getChanceCards());
		}
		if(model.getCommunityChestCards().isEmpty()) {
			model.setCommunityChestCards(new Deck().getCommunityChestCards());
		}
	}


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
	
	
	public int getBoardWidth() {
		return model.board.getBoardWidth();
	}
	
	public Space getFirstSpace() {
		return model.board.getFirstSpace();
	}
	
	public Player getCurrentPlayer() {
		return model.getCurrentPlayer();
	}
	
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
	
	//called by view when player successfully chooses to build on their property
	public void sellHouseHotel(Player player, RealEstate property) {
	        property.sellHouseHotel(player, model);
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


	
}
