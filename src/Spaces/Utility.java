package Spaces;

public class Utility extends Property {
	
	
	public Utility(String name, int[] rentStages, String imageFile) {
		super(name, 150, rentStages); 
		this.imageFile = imageFile;
	}
	
	//will need to multiply this amount by the dice roll...
    public int getCostToCharge(Player player, int diceRoll) {
    	
    	// if not owned, or is mortgaged, return 0
        if (getOwner() == null) {//|| this.getIsMortgaged()) {
        	return 0;
        }
        
        return this.rentStages.get(rentStageIndex)*diceRoll;
    }

    @Override
	protected void applyMatchedPropertyEffect(int matchedOwnedPropertiesCount) {
		
		switch (matchedOwnedPropertiesCount) {
			case 1:
	            this.rentStageIndex = 0;
	            break;
	        case 2:
	            this.rentStageIndex = 1;
	            break;
	        default:
	            this.rentStageIndex = 0;
		}
		
	}



}
