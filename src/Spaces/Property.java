package Spaces;

import java.util.ArrayList;

import Monopoly.Model;

public abstract class Property extends CostSpace {
	private int purchaseAmount;
	private Player owner;
	private boolean isMortgaged = false;
	protected ArrayList<Integer> rentStages;
	protected int rentStageIndex = 0;
	
	public Property(String name, int purchaseAmount, int[] rentStages) {
		super(name);
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
		if (this.getIsMortgaged() || this.getOwner() == player) return;
		
		//if unowned, prompt player to purchase
		if (this.getOwner() == null) {
			//if rule=optional property sale=true AND currplayer not AI:
			model.notifyViewPurchasePrompt(player, this);
			//else:
			//this.purchaseProperty(player);
			
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
			model.notifyViewOfInfoMessage(player.toString() + " charged rent $" + cost + " on " + this.getName() + ". Given to " + this.getOwner().toString());

		}

	}
	
	public void purchaseProperty(Player player, Model model) {
		player.addCash(-this.getPurchaseAmount());		
		player.addProperty(this);
		this.setOwner(player);		
		model.notifyViewOfInfoMessage(player.toString() + " purchased " + this.getName() + " for $" + this.getPurchaseAmount());
	}
	
	public void mortgageProperty(Player player, Model model) {
		
		// can only mortgage if all building sold
		if (((RealEstate)this).getBuildingStage() > 0){
			int mortgageAmount = this.getPurchaseAmount()/2;
			player.addCash(mortgageAmount);		
			this.setIsMortgaged(true);
			model.notifyViewOfInfoMessage(player.toString() + " mortgaged " + this.getName() + " for $" + mortgageAmount);

		} else {
			model.notifyViewOfInfoMessage("Must sell all buildings before mortgaging!");
		}
		
		
	}
	
	public void unmortgageProperty(Player player) {
		
	}
	
	public ArrayList<Integer> getRentStages(){
		return rentStages;
	}
	
	public int getCostToCharge(Player player, int diceRoll) {
		return rentStages.get(rentStageIndex);
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
	
	public void setIsMortgaged(boolean val) {
		this.isMortgaged = val;
	}
	
	public int getMortgageAmount() {
		return purchaseAmount/2;
	}
	
	public int getPurchaseAmount() {
		return purchaseAmount;
	}
	
	//helper function when player buys a property, will check if matching properties and update rent stage/enable building,etc.
	protected abstract void applyMatchedPropertyEffect(int matchedOwnedPropertiesCount);



}
