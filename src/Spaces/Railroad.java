package Spaces;

public class Railroad extends Property {

	public Railroad(String name, int[] rentStages, String imageFile) {
		super(name, 200, rentStages);
		this.imageFile = imageFile;
	}
	
    @Override
	protected void applyMatchedPropertyEffect(int matchedOwnedPropertiesCount) {
		this.rentStageIndex = matchedOwnedPropertiesCount-1;
    }
}
