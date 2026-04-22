package Spaces;

import java.util.*;

public class RealEstate extends Property {
	
	private int buildPrice;
	protected Color color; //default to none
	private boolean canBuild = false;
	private int buildingStage = 0;
	public enum Color{
		BROWN,
		LIGHTBLUE,
		PINK,
		ORANGE,
		RED,
		YELLOW,
		GREEN,
		BLUE
		
	}
	
	
	public RealEstate(Color color, String name, int purchaseAmt, int[] rentStages) {
		super(name, purchaseAmt, rentStages);
		this.color = color;

		
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

	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public int getBuildPrice() {
		return buildPrice;
	}
	
	
    public javafx.scene.paint.Color getFXColor() {
    	
        switch (color) {
            case BLUE:
                return javafx.scene.paint.Color.BLUE;
            case BROWN:
                return javafx.scene.paint.Color.BROWN;
            case LIGHTBLUE:
                return javafx.scene.paint.Color.LIGHTBLUE;
            case PINK:
                return javafx.scene.paint.Color.PINK;
            case ORANGE:
                return javafx.scene.paint.Color.ORANGE;
            case GREEN:
                return javafx.scene.paint.Color.GREEN;
            case RED:
                return javafx.scene.paint.Color.RED;
            case YELLOW:
                return javafx.scene.paint.Color.YELLOW;
			default:
				return javafx.scene.paint.Color.LIGHTGREEN;
        }
        
    }

    public int getBuildingStage() {
    	return buildingStage;
    }
    
    public boolean getIfCanBuild() {
    	return canBuild;
    }
    
    public void buildHouseHotel(Player player) {
    	
    	//check if this space is owned by the purchaser
    	if (this.getOwner() == null || !this.getOwner().equals(player)) {
    		System.out.println(player.toString() + " does not own " + this.getName() + " can't build");
    		return;
    	}
    	
    	//first must get a color set before can build
    	if (!this.canBuild) {
    		System.out.println("Can't build! Must aquire a set!");
    		return; 
    	}
    	
    	if (this.buildingStage>=5) {
    		System.out.println("Already fully developed!");
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
	        		    	System.out.println("Must build evenly!");
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
        	System.out.println((player.toString() + " built on " + this.name + " buildstage: " + this.getBuildingStage()));
	    }else {
	    	System.out.println("Not enough funds!");
    	}
    }
    
    public void sellHouseHotel(Player player) {
    	
    	//monopoly rule where you can only sell houses/hotels evenly across properties, checking if violation
    	List<Property> myProperties = this.getOwner().getListOfProperties();
    	for (Property p: myProperties) {
    		if (p instanceof RealEstate) {
    			RealEstate checkRealEstate = (RealEstate)p;
    			
    			if (checkRealEstate.getColor().equals(this.getColor())) {
    				//if i have another realestate of the same color that has a higher buildstage, can't sell
        			if(checkRealEstate.getBuildingStage()>this.getBuildingStage()) { 
        				System.out.println("Not selling evenly across properties of same color!");
        				return; //Throw exception?
        			}
    			}	
    		}
    	}
    	
    	if (this.buildingStage < 1) {
    		System.out.println("Buildstage is less than 1, can't sell buildings!");
    		return;
    	}
    	
    	//if found no conflicts, sell
    	int sellPrice = buildPrice/2;
    	player.addCash(sellPrice);
    	this.rentStageIndex -= 1;
    	this.buildingStage -= 1;
    	System.out.println(player.toString() + " successfully sold house/hotel for $" + sellPrice);
    	
    }
    
    
	/**
	 * checks for monopolies. if no monopoly, makes sure that rents are reset to first stage and can't build
	 * 
	 * @param 
	 */
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
