package monopoly;

import java.util.ArrayList;

public abstract class Property extends CostSpace {
	private int purchaseAmount;
	private Player owner;
	private boolean isMortgaged;
	protected ArrayList<Integer> rentStages;
	protected int rentStageIndex = 0;
	
	public Property(String name, int purchaseAmount, int[] rentStages) {
		super(name);
		this.purchaseAmount = purchaseAmount;
		
		//converting the primitive arraylist to arraylist obj. using the primitive since its easier to copy/paste for now
		ArrayList<Integer> arrayListObj = new ArrayList<Integer>();
		for (int i=0; i<rentStages.length; i++) {
			arrayListObj.add(rentStages[i]);
		}
		
		
		
		
		
		
		this.rentStages = arrayListObj;
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
	
	

	
	protected abstract void applyMatchedPropertyEffect(int matchedOwnedPropertiesCount);

	
	public void processSpace(Player player, Model model) {
		
		//do nothing if mortgaged, or the player is the owner
		if (this.getIsMortgaged() || this.getOwner() == player) return;
		
		//if unowned, prompt player to purchase
		if (this.getOwner() == null) {
			model.notifyViewPurchasePrompt(player, this);
		
			
			//TODO check if purchasing a property increments the rent via monopoly or owning multiple utilities, railroads, etc
			
		//otherwise, charge/transfer $
		} else {

			
			

			player.addCash(-getCostToCharge(player));
			this.getOwner().addCash(getCostToCharge(player));
		}

	}
	

	public void incrementRentStage(Player player) {
		if (rentStageIndex < (rentStages.size()-1)) {
			rentStageIndex += 1;
		}
	}
	
	public void decrementRentStage() {
		if (rentStageIndex > 0) {
			rentStageIndex -=1;
		}
	}
	
	public int getCostToCharge(Player player) {

		


		return rentStages.get(rentStageIndex);
		
	}
	
}
