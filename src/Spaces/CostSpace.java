package Spaces;

public abstract class CostSpace extends Space {
	
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