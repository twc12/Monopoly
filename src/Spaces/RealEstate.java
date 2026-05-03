package Spaces;

import java.util.*;

import Monopoly.Model;

/**
 * Real Estate properties, a subclass of Property. Allows buying/selling buildings, which update
 * their rent prices. Each Real Estate has their own color grouping.
 * @author Jake
 */
public class RealEstate extends Property {
	private int buildPrice;					//cost to build on real estate
	protected Color color; 					//default to none
	private boolean canBuild = false;		//boolean flag for if a player can build
	private int buildingStage = 0; 			//how many buildings are built on a real estate property
	public enum Color { 					//different color sets of real estate
		BROWN,
		LIGHTBLUE,
		PINK,
		ORANGE,
		RED,
		YELLOW,
		GREEN,
		BLUE
	}
	
	/**
	 * Constructor
	 * Requires a Color, name, purchaseAmmt, and the different unique rent prices at each "build" stage
	 * @param color - Color enum
	 * @param name - String name
	 * @param purchaseAmt - int Cost to purchase
	 * @param rentStages - int[] the different costs at each building stage
	 */
	public RealEstate(Color color, String name, int purchaseAmt, int[] rentStages) {
		super(name, purchaseAmt, rentStages);
		this.color = color;

		//price to build is determined by color group
        switch (color) {
	        case BROWN:
	            buildPrice = 50; break;
	        case LIGHTBLUE:
	            buildPrice = 50; break;
	        case PINK:
	            buildPrice = 100; break;
	        case ORANGE:
	            buildPrice = 100; break;
	        case RED:
	            buildPrice = 150; break;
	        case YELLOW:
	            buildPrice = 150; break;
	        case BLUE:
	            buildPrice = 200; break;
	        case GREEN:
	            buildPrice = 200; break;
	    }
	}

	/**
	 * The color representing the group this property is part of
	 * @return the color of the property
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Gets the cost to build on this space
	 * @return A number representing the cost
	 */
	public int getBuildPrice() {
		return buildPrice;
	}

	/**
	 * The current building stage of this property
	 * @return the stage number
	 */
    public int getBuildingStage() {
    	return buildingStage;
    }
    
	/**
	 * Checks if building is allowed on this property
	 * @return true if so, false otherwise
	 */
    public boolean getIfCanBuild() {
    	return canBuild;
    }
    
    /**
     * Builds a house or hotel on a particular realestate property.
     * Validates ownership, and if building is allowed, if can afford,
     * and if building evenly. If passes, then transacts the build and
     * increments buildstage/rentstage.
     * @param player - the Player purchasing the building
     * @param model - Model gamestate object to send notifications
     */
    public void buildHouseHotel(Player player, Model model) {
    	
    	//check if this space is owned by the purchaser
    	if (this.getOwner() == null || !this.getOwner().equals(player)) {
    		return;
    	}
    	
    	//must get a color set before can build
    	if (!this.canBuild) {
    		if (!player.isAI()) model.notifyViewOfInfoMessage("Can't build! Must aquire a set!");
    		return; 
    	}
    	
    	//can't build if already at stage 5
    	if (this.buildingStage>=5) {
    		if (!player.isAI()) model.notifyViewOfInfoMessage("Already fully developed!");
    		return;
    	}
    	
    	//monopoly rule where you can only build houses/hotels evenly across properties, checking if violation
		//checking the player's other owned realestate properties of the same color
	    	List<Property> myProperties = this.getOwner().getListOfProperties();
	    	for (Property p: myProperties) {
	    		if (p instanceof RealEstate) {
	    			RealEstate checkRealEstate = (RealEstate)p;
	 
    				//if i have another realestate of the same color that has a lower buildstage, can't buy
	    			if (checkRealEstate.getColor().equals(this.getColor())) {
	        			if(checkRealEstate.getBuildingStage()<this.getBuildingStage()) { 
	        				if (!player.isAI()) model.notifyViewOfInfoMessage("Can't build, must build evenly!");
	        				return;
	        			}
	    			}	
	    		}
	    	}
	    	
	    //final check if player can afford it
    	if (player.getCashAmmt() >= this.buildPrice) {
        	player.addCash(-buildPrice);
        	this.rentStageIndex += 1;
        	this.buildingStage += 1;
    		model.notifyViewOfInfoMessage((player.toString() + " built on " + this.name));
	    }else {
	    	if (!player.isAI()) model.notifyViewOfInfoMessage("Not enough funds!");
    	}
    }
    
    /**
     * Used during bankruptcy. Will make the transaction,
     * and return the amount of cash raised from selling a
     * single building on the property.
     * @param player - Player the player doing the selling
     * @return - int how much raised cash from selling
     */
    public int autoSellHouseHotel(Player player) {
    	//if found no conflicts, sell
    	if (this.buildingStage > 0) {
	    	int sellPrice = buildPrice/2;
	    	player.addCash(sellPrice);
	    	this.rentStageIndex -= 1;
	    	this.buildingStage -= 1;
	    	return sellPrice;
    	}
    	return 0;
    }
    
    
	/**
	 * Helper function for when property acquired in player.updatePropertiesMatches()
	 * Will update all matching real estate cards to make sure their rents are upgraded due to match, 
	 * and will enable building.
	 * @param matchedOwnedPropertiesCount - int how many matching properties of a particular real estate object
	 */
    @Override
	public void applyMatchedPropertyEffect(int matchedOwnedPropertiesCount) {
		//brown and blue are only groups of 2
		if (matchedOwnedPropertiesCount == 2 && (this.color.equals(Color.BROWN) || this.color.equals(Color.BLUE))) {
			if (this.rentStageIndex == 0) {
				this.rentStageIndex = 1;
				this.canBuild = true;
			}
		}

		//all other colors are groups of 3
		else if (matchedOwnedPropertiesCount == 3) {
			if (this.rentStageIndex == 0) {
				this.rentStageIndex = 1;
				this.canBuild = true;
			}
		} else {
			this.rentStageIndex = 0;
			this.canBuild = false;
		}
		
	}
}
