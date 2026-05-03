package Spaces;

/**
 * Utility, a subclass of Property. It's cost to charge is based on the diceroll, and if a player
 * owns a matching set.
 * @author: Jake
 */
public class Utility extends Property {
	/**
	 * Constructor.
	 * Pass in the name, the rent stages (based on match), and imagefile String.
	 * @param name - String the name
	 * @param rentStages - int[] rent stages. In the Board class these are hardcoded to {4, 10}
	 * @param imageFile - String imagefile string
	 */
	public Utility(String name, int[] rentStages, String imageFile) {
		super(name, 150, rentStages); 
		this.imageFile = imageFile;
	}

	/**
	 * Gets the amount to charge a player who lands on this utility. Utility is unique in that
	 * its the only space to consider the diceroll. The amount to charge is multipled by the dice
	 * roll amount, times its rent.
	 * @return int - the amount to charge
	 */
    public int getCostToCharge(Player player, int diceRoll) {
        return this.rentStages.get(rentStageIndex)*diceRoll; //rent value * diceroll
    }

    @Override
    /**
     * When a acquires/sells a Utility property, will update all other Utility properties and apply
     * matching effect, raising the rentStage index.
     */
	protected void applyMatchedPropertyEffect(int matchedOwnedPropertiesCount) {
		switch (matchedOwnedPropertiesCount) {
	        case 2: //matching pair
	            this.rentStageIndex = 1;
	            break;
	        default: //default/no match
	            this.rentStageIndex = 0;
		}
	}
}
