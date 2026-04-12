package monopoly;

public class Utility extends Property {
	
	
	//will need to multiply this amount by the dice roll...
    public int getAmountOfCostToChargePlayer(Player player) {
    	
    	// if not owned, or is mortgaged, return 0
        if (getOwner() == null || this.getIsMortgaged()) {
        	return 0;
        }
 
        
        //check how many utilities the owner owns. if owns 2, return 10. otherwise 4. 
        int ownedPropertyCount = 0;
        for (Space checkSpace : getOwner().getListOfProperties()) {
            if (checkSpace instanceof Utility) ownedPropertyCount++;
        }
        if (ownedPropertyCount == 2) {
        	return 10;
        }
        return 4;
        
    }
	
	

}
