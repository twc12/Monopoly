package Spaces;

/**
 * Abstract subclass of Space. Property and Tax Spaces are all CostSpaces
 * which will need to have a method .getCostToCharge based on different factors.
 * @author: Jake
 */
public abstract class CostSpace extends Space {
	
	/**
	 * Constructor
	 * @param name - String the name of the space
	 */
	public CostSpace(String name) {
		super(name);
	}
	
	/**
	 * Gets the amount of money to charge the player on this space
	 * @param player The playerto be charged
	 * @param diceRoll - only used by Utility - set to 0 for non-Utility spaces!
	 * @return the amount this player will be charged
	 */
	public abstract int getCostToCharge(Player player, int diceRoll);
}