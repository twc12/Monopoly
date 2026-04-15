package monopoly;


public abstract class Property extends CostSpace {
	private int purchaseAmount;
	private Player owner;
	private boolean isMortgaged;
	private int stageModifier;
	
	public Property(String name, int purchaseAmount) {
		super(name);
		this.purchaseAmount = purchaseAmount;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public void setOwner(Player player) {
		this.owner = player;
	}
	
	public boolean getIsMortgaged() {
		return isMortgaged;
	}
	
	public int getMortgageAmount() {
		return purchaseAmount/2;
	}
	
	public int getPurchaseAmount() {
		return purchaseAmount;
	}
	
	public void processSpace(Player player, Model model) {
		
		//do nothing if mortgaged
		if (this.getIsMortgaged()) return;
		
		//if unowned, prompt player to purchase
		if (this.getOwner() == null) {
			model.notifyViewPurchasePrompt(player, this);
			
		//otherwise, charge/transfer $
		} else {
			player.addCash(-getCostToCharge(player));
			this.getOwner().addCash(getCostToCharge(player));
		}

	}
}
