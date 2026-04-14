package monopoly;

public abstract class CostSpace extends Space {
	
	public CostSpace(String name) {
		super(name);
	}
	
	public abstract int getAmountOfCostToChargePlayer(Player player);

	
}