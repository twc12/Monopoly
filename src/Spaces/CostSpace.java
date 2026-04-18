package Spaces;

public abstract class CostSpace extends Space {
	
	public CostSpace(String name) {
		super(name);
	}
	
	/**
	 * 
	 * @param player
	 * @param diceRoll - only used by Utility - set to 0 for non-Utility spaces!
	 * @return
	 */
	public abstract int getCostToCharge(Player player, int diceRoll);

	
}