package monopoly;

public class RealEstate extends Property {
	
	private int buildPrice;
	protected Color color; //default to none
	private boolean canBuild=false;
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
    
    
    
    
    
//	@Override
//	//checks for monopolies. if no monopoly, makes sure that rents are reset to first stage and can't build
//	protected void applyMatchedPropertyEffect(int matchedOwnedPropertiesCount) {
//		
//		//brown and blue are only groups of 2
//		if (matchedOwnedPropertiesCount == 2 && (this.color.equals(Color.BROWN) || this.color.equals(Color.BLUE))) {
//			if (this.rentStageIndex == 0) {
//				this.rentStageIndex = 1;
//				this.canBuild = true;
//			}
//		}
//
//		//all other colors are groups of 3
//		else if (matchedOwnedPropertiesCount == 3) {
//			if (this.rentStageIndex == 0) {
//				this.rentStageIndex = 1;
//				this.canBuild = true;
//
//			}
//		} else {
//			this.rentStageIndex = 0;
//			this.canBuild = false;
//		}
//		
//	}


}
