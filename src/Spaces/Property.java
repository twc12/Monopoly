package Spaces;

import java.util.ArrayList;
import java.util.Objects;

import org.apiguardian.api.API;

import Monopoly.Model;

/**
 * Property, a subclass of CostSpace. Parent class of Utility, Real Estate, and Railroads.
 * @author: Jake
 */
public abstract class Property extends CostSpace {
	private int purchaseAmount;						//amount to purchase/own the property
	private Player owner;							//current owner
	protected ArrayList<Integer> rentStages;		//rent stages that will be used to determine what to charge players who land
	protected int rentStageIndex = 0;				//current stage of rent
	
	/**
	 * Constructor.
	 * Will need a purchase amount, and the unique stages of rent that get charged to whoever
	 * lands on the space.
	 * @param name - String the name of the property
	 * @param purchaseAmount - int how much to purchase
	 * @param rentStages - ArrayList<Integer> used to determine what to charge players who land
	 */
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
	
	/**
	 * When a player lands on a property, will charge them rent, will prompt them to 
	 * purchase it, or will do nothing if the owner landed on it.
	 */
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
	
	/**
	 * Called when a player purchases a property. Will transact cash and ownership.
	 * @param player - Player the player purchasing
	 * @param model - Model used to notify view's text of a purchase
	 */
	public void purchaseProperty(Player player, Model model) {
		//check can afford, if not exit
		if (player.getCashAmmt()<this.getPurchaseAmount()) return;
		
		//exchange cash and ownership
		player.addCash(-this.getPurchaseAmount());		
		player.addProperty(this);
		this.setOwner(player);		
		model.notifyViewOfInfoMessage(player.toString() + " purchased " + this.getName() + " for $" + this.getPurchaseAmount() + "!");
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

	/**
	 * Used during bankruptcy. Will transact the selling of the property, and 
	 * return how much money it was sold for, which is half of the original purchase price
	 * @param player - Player the player selling the property
	 * @return int - How much it was sold for, half the purchase price
	 */
    public int autoSellProperty(Player player) {
    	
    	//transfer cash
    	int sellPrice = purchaseAmount/2;
    	player.addCash(sellPrice);
    	
    	//transfer owner
    	this.setOwner(null);
    	player.removeProperty(this);
    	return sellPrice;
    }
    
    /**
     * Helper method overriding default .equals()
     */
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
    
    /**
     * Helper method overriding default .hashCode()
     */
    @Override 
    public int hashCode() {
    	return Objects.hash(this.getName());
    }
}
