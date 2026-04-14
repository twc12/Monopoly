package monopoly;

import java.util.List;
import java.util.*;
public class Controller {

	private Model model;
	
	
	public Controller() {
		model = new Model();
	}
	
	public ArrayList<Integer> rollDice(Player player) {
		ArrayList<Integer> rolls = new ArrayList<>();
		Random rand = new Random();
		int dice;
		
		// Roll dice, place into list
		dice = rand.nextInt(6)+1;
		rolls.set(0, dice);
		dice = rand.nextInt(6)+1;
		rolls.set(1,dice);
		
		// Move player 
		player.move(rolls.get(0)+rolls.get(1));
		
		//TODO
		//	process on enter space logic
		
		return rolls;
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
			int cost = utility.getAmountOfCostToChargePlayer(player);
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
		int cost = space.getAmountOfCostToChargePlayer(player);
		player.addCash(-cost);
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
	
	public List<Space> getSpaces(){
		return model.board.getSpaces();
	}
	
	
	public int getBoardWidth() {
		return model.board.getBoardWidth();
	}	
}
