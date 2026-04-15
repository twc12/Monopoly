package monopoly;

import java.util.List;
import java.util.*;
public class Controller {

	private Model model;
	
	
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
		
		// Move player 
		player.move(ammtMoved);
		model.notifyViewOfPlayerMoved(player, ammtMoved);

		player.getCurrentSpace().processSpace(player, model);
		 
		//TODO
		//	process on enter space logic
		
	}
	
	public void executePropertySale (Player player, Property property) {
		
		player.addCash(-property.getPurchaseAmount());		
		player.addProperty(property);
		property.setOwner(player);
		
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


	// JARROD NOTE:
	// - Currently there's no actual popups or GUI effects for anything
	// regarding the effects of the processSpace() methods, current
	// print statements exist only for basic testing
	// - Some of these methods I wrote are kinda repetitive, might be a 
	// better way to condense them, but for now functionality is the only
	// priority

	/**
	 * 
	 * @param space
	 * @param player
	 */
	public void processSpace(Space space, Player player) {

		// PROPERTIES
		if (space instanceof Property property) {
			Player owner = checkOwner(property, player);
			
			// RealEstate branch
			if (property instanceof RealEstate realEstate) {
				processRealEstate(realEstate, owner, player);
			}
			// Railroad branch
			else if (property instanceof Railroad railRoad) {
				processRailRoad(railRoad, owner, player);
			}
			// Utility branch
			else if (property instanceof Utility utility) {
				processUtility(utility, owner, player);
			}
			else {
				// TODO: Some kind of error checking?
			}
		}

		// COSTSPACES
		else if (space instanceof CostSpace costSpace) {
			// Casts costSpace to taxSpace
			TaxSpace taxSpace = (TaxSpace) costSpace;
			processTaxSpace(taxSpace, player);
		}

		// REGULAR SPACES
		else {
			// Chance branch
			if (space instanceof Chance chance) {
				processChance(chance, player);
			}
			// FreeParking branch
			else if (space instanceof FreeParking freeParking) {
				processFreeParking(freeParking, player);
			}
			// GoSpace branch
			else if (space instanceof GoSpace go) {
				processGoSpace(go, player);
			}
			// Jail branch
			else if (space instanceof Jail jail) {
				processJailSpace(jail, player);
			}
			else {
				// TODO: Some kind of error checking?
			}	
		}
	}

	/**
	 * 
	 * @param space
	 * @param owner
	 * @param player
	 */
	private void processRailRoad(Railroad space, Player owner, Player player) {
		// Unowned - Ask if player wants to purchase it
		if (owner == null) {
			System.out.println("Do you want to buy this Railroad?");
			// TODO: Implement view portion of this 
		}
		// Owned by someone else - Calculate player payment
		if (!owner.equals(player)) {
			// TODO: Implement railroad cost calculations and then do this
		}
		// Owned by current player - do nothing
		else {
			return;
		}
	}

	/**
	 * 
	 * @param utility
	 * @param owner
	 * @param player
	 */
	private void processUtility(Utility utility, Player owner, Player player) {
		// Unowned - Ask if player wants to purchase it
		if (owner == null) {
			System.out.println("Do you want to buy this Utility?");
			// TODO: Implement view portion of this
		}
		// Owned by someone else - Calculate player payment
		if (!owner.equals(player)) {
			int cost = utility.getCostToCharge(player);
			// TODO: Apply the exchange of money to both the player and the owner
		}
		// Owned by current player - do nothing
		else {
			return;
		}
	}

	/**
	 * 
	 * @param property
	 * @param owner
	 * @param player
	 */
	private void processRealEstate(Property property, Player owner, Player player) {
		// Unowned - Ask if player wants to purchase it
		if (owner == null) {
			System.out.println("Do you want to buy this Real Estate?");
			// TODO: Implement view portion of this
		}
		// Owned by someone else - Calculate player payment
		if (!owner.equals(player)) {
			// TODO: Implement property cost claculations and then do this
		}
		// Owned by current player - do nothing
		else {
			return;
		}
	}

	/**
	 * Checks for the owner of this property
	 * @param space The current space we're on
	 * @param player The current player
	 * @return A player object if anyone owns it, null if no one does
	 */
	private Player checkOwner(Property space, Player player) {
		Player owner = space.getOwner();
		// If the space is owned by the current player
		if (owner != null && owner.getId() == player.getId()) {
			return player;
		}
		// If the space is owned by someone else
		else if (owner != null && owner.getId() != player.getId()) {
			return owner;
		}
		// If no one owns this space
		else {
			return null;
		}
	}

	/**
	 * 
	 * @param space
	 * @param player
	 */
	private void processTaxSpace(TaxSpace space, Player player) {
		int cost = space.getCostToCharge(player);
		player.addCash(-cost);
	}
	
	public void resolveCard(Card card, Player player) {
		card.apply(player, model);
	}

	/**
	 * 
	 * @param space
	 * @param player
	 */
	private void processGoSpace(GoSpace space, Player player) {
		int passGo = space.getAmountEarnedWhenPassingGo();
		player.addCash(passGo);
	}

	private void processFreeParking(FreeParking space, Player player) {
		// TODO: Implement this logic, unsur how to do right now
	}

	private void processJailSpace(Jail space, Player player) {
		// TODO: Implement this logic, unsure how to do right now
	}

	private void processChance(Chance space, Player player) {
		// TODO: Implement Chance class, then implement this logic
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
	
	
	
}
