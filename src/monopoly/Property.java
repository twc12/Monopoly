package monopoly;


public abstract class Property extends CostSpace {
	private int purchaseAmount;
	private Player owner;
	private boolean isMortgaged;
	private int stageModifier;
	
	
	public Player getOwner() {
		return owner;
	}
	
	public boolean getIsMortgaged() {
		return isMortgaged;
	}
	
}
