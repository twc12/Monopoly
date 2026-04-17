package monopoly;

public class RealEstate extends Property {
	
	private int buildPrice;
	protected Color color; //default to none
	private boolean buildingEnabled=false;
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
	            buildPrice = 50;
	        case LIGHTBLUE:
	            buildPrice = 50;
	        case PINK:
	            buildPrice = 100;
	        case ORANGE:
	            buildPrice = 100;
	        case RED:
	            buildPrice = 150;
	        case YELLOW:
	            buildPrice = 150;
	        case BLUE:
	            buildPrice = 200;
	        case GREEN:
	            buildPrice = 200;
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

	@Override
	protected void applyMatchedPropertyEffect(int matchedOwnedPropertiesCount) {
		
		if (matchedOwnedPropertiesCount == 2 & (this.color.equals(Color.BROWN) || this.color.equals(Color.BLUE))) {
			if (this.rentStageIndex == 0) {
				this.rentStageIndex = 1;
			}
		}

		if (matchedOwnedPropertiesCount == 3) {
			if (this.rentStageIndex == 0) {
				this.rentStageIndex = 1;
			}
		}
		
	}


}
