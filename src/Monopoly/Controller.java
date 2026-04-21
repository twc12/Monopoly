package Monopoly;

import java.util.List;

import Cards.Card;
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
	
	private String theme;
	
	//contructor for JUNIT, doesn't create a view
	public Controller() {
		this.model = new Model();
	}
	
	
	public Controller(View viewClassObj) {
		model = new Model(viewClassObj);
	}
	
	/**
	 * rollDice(player): This function is given a player it rolls dice
	 * for and it will randomly generate dice rolls then send that message to the view
	 * then it will move the player, then notify the view again.
	 * The player.move function will account for it the player passes go
	 * @param player (Player): The current player rolling the dice 
	 */
	public void rollDice(Player player) {
		
		Random rand = new Random();
		int dice1Result;
		int dice2Result;
		
		// Roll dice, place into list
		dice1Result = rand.nextInt(6)+1; 
		dice2Result = rand.nextInt(6)+1;
		
		// if the player did not get doubles then they are DONE ROLLING DICE 
		if (dice1Result != dice2Result) {
			player.setIsDoneRollingDice(true);
		}
		
		model.notifyViewOfDiceResult(dice1Result, dice2Result);
		
		int ammtMoved = dice1Result+dice2Result;
		
		model.setLastDiceRollAmmt(ammtMoved);
		
		player.move(ammtMoved);

	}
	
	/**
	 * This function is NOT THE TRUE IMPLEMENTATION, Needs to be rewrote
	 * @param choice (JAIL_CHOICE): the valid choice from the user 
	 */
	public void processJailLogic(Player playerInJail, JAIL_CHOICE choice) {
		// no matter the choice lets just say hes removed and he gets go roll dice for the mean time
		playerInJail.getOutOfJail();
		model.notifyViewOfNextPlayersTurn(playerInJail);
	}
	
	public void purchaseProperty (Player player, Property property) {
		property.purchaseProperty(player);		
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
	        property.buildHouseHotel(player);
	}
	
	//called by view when player successfully chooses to build on their property
	public void sellHouseHotel(Player player, RealEstate property) {
	        property.sellHouseHotel(player);
	}
	
	
}
