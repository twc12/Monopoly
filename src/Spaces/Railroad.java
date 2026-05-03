package Spaces;

/**
 * Railroad, a subclass of Property. 4 railroads are placed on the board.
 * @author: Jake
 */
public class Railroad extends Property {

	/**
	 * Constructor
	 * Requires the name of the property, the rentStages, and the imageFile String.
	 * @param name - String name of the Property
	 * @param rentStages - int[] of the rentStages, which are the costs that will be applied when landed on
	 * 					   {25, 50, 100, 200} are the values used when constructing the Board
	 * @param imageFile - String for the image file
	 */
	public Railroad(String name, int[] rentStages, String imageFile) {
		super(name, 200, rentStages);
		this.imageFile = imageFile;
	}
	
    @Override
    /**
     * If found to own multiple railroads, will update the rent index to that amount minus 1.
     * Max of 4 railroads owned.
     */
	protected void applyMatchedPropertyEffect(int matchedOwnedPropertiesCount) {
		this.rentStageIndex = matchedOwnedPropertiesCount-1;
    }
}
