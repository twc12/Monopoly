package Spaces;

import java.util.ArrayList;
import java.util.Objects;

import org.apiguardian.api.API;

import Monopoly.Model;

public abstract class Property extends CostSpace {
	private int purchaseAmount;
	private Player owner;
	protected ArrayList<Integer> rentStages;
	protected int rentStageIndex;
	
	public Property(String name, int purchaseAmount, int[] rentStages) {
		super(name);
		rentStageIndex = 0;
		this.purchaseAmount = purchaseAmount;
		
		//converting the primitive arraylist to arraylist obj. using the primitive since its easier to copy/paste in builder for now
		ArrayList<Integer> arrayListObj = new ArrayList<Integer>();
		for (int i=0; i<rentStages.length; i++) {
			arrayListObj.add(rentStages[i]);
		}
		this.rentStages = arrayListObj;
	}
	
	public void processSpace(Player player, Model model) {
		
		//do nothing if mortgaged, or the player is the owner
		if (this.getOwner() == player) return;
		
		//if unowned, prompt player to purchase
		if (this.getOwner() == null) {
			if (player.isAI()) {
				((AIPlayer) player).decidePurchase(this, model);
			}
			else if (model.getGameSettings().getOptionalBuying() == true) {
				if (!player.isAI()) model.notifyViewPurchasePrompt(player, this);
			} else {
				this.purchaseProperty(player, model);
			}
			
		//otherwise, charge/transfer $
		} else {
			// need to pass diceroll if landed on Utilities object
			int recentDiceRoll = 0;
			if (this instanceof Utility) {
				recentDiceRoll = model.getLastDiceRollAmmt();
			}
			int cost = getCostToCharge(player, recentDiceRoll); //only Utilities object will use diceroll arg
			player.addCash(-cost);
			this.getOwner().addCash(cost);
			model.notifyViewOfInfoMessage(player.toString() + " landed on " + this.getName() + "! Charged $" + cost + " by " + this.getOwner().toString());
		}

	}
	
	public void purchaseProperty(Player player, Model model) {
		if (player.getCashAmmt()<this.getPurchaseAmount())return;
		player.addCash(-this.getPurchaseAmount());		
		player.addProperty(this);
		this.setOwner(player);		
		model.notifyViewOfInfoMessage(player.toString() + " purchased " + this.getName() + " for $" + this.getPurchaseAmount() + "!");
	}
	
		
//	public void mortgageProperty(Player player, Model model) {
//		
//		// can only mortgage if all building sold
//		if (((RealEstate)this).getBuildingStage() > 0){
//			int mortgageAmount = this.getPurchaseAmount()/2;
//			player.addCash(mortgageAmount);		
//			this.setIsMortgaged(true);
//			model.notifyViewOfInfoMessage(player.toString() + " mortgaged\n " + this.getName() + " for $" + mortgageAmount + "!");
//
//		} else {
//			if (!player.isAI()) model.notifyViewOfInfoMessage("Must sell all buildings before mortgaging!");
//		}
//		
//		
//	}
	
	public void unmortgageProperty(Player player) {
		
	}
	
	/**
	 * @return an array list of the rent stages for a property
	 */
	public ArrayList<Integer> getRentStages(){
		return rentStages;
	}
	
	/**
	 * @return the amount to charge a player for rent
	 */
	public int getCostToCharge(Player player, int diceRoll) {
		return rentStages.get(rentStageIndex);
	}
	
	/**
	 * @return the owner of this property
	 */
	public Player getOwner() {
		return owner;
	}
	
	/**
	 * Getter: Returns the current index in 
	 * the rent stages this property is at
	 * @return int: the index in the rent stages
	 */
	public int getRentStageIndex() {
		return rentStageIndex;
	}
	
	/**
	 * Sets the owner of the property
	 * @param player the new owner
	 */
	public void setOwner(Player player) {
		this.owner = player;
	}
	
//	public boolean getIsMortgaged() {
//		return isMortgaged;
//	}
//	
//	public void setIsMortgaged(boolean val) {
//		this.isMortgaged = val;
//	}
	
	/**
	 * Gets worth of property mortgage
	 * @return amount mortgage is worth
	 */
	public int getMortgageAmount() {
		return purchaseAmount/2;
	}
	
	/**
	 * Gets the amount this porperty is being purchased for
	 * @return amount purchased for
	 */
	public int getPurchaseAmount() {
		return purchaseAmount;
	}
		
	/**
	 * Helper function for when property acquired in player.updatePropertiesMatches()
	 * Each property type (Utility/Railroad/Real Estate) has their rent scale differently
	 * based on when matching properties are paired.
	 * called when checking for matches when acquiring a property via buy/trade.
	 * 
	 * @param matchedOwnedPropertiesCount
	 */
	protected abstract void applyMatchedPropertyEffect(int matchedOwnedPropertiesCount);

	
    public int autoSellProperty(Player player, Model model) {
    	
    	//if found no conflicts, sell
    	int sellPrice = purchaseAmount/2;
    	player.addCash(sellPrice);
    	model.notifyViewOfInfoMessage(player.toString() + " sold property for $" + sellPrice + "!");
    	
    	this.setOwner(null);
    	player.removeProperty(this);
    	
    	
    	return sellPrice;
    	
    }
    
    @Override 
    public boolean equals(Object other) {
    	if (this == other) {
    		return true;
    	}
    	
    	if (other instanceof Property) {
    		Property otherProp = (Property) other;
    		if (this.getName().equals(otherProp.getName())) {
    			return true;
    		}
    	}
    	return false;
    	
    }
    
    @Override 
    public int hashCode() {
    	return Objects.hash(this.getName());
    }
}
