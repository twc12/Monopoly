package Spaces;

public class Railroad extends Property {

	public Railroad(String name, int[] rentStages) {
		super(name, 200, rentStages);
	}
	
    @Override
	protected void applyMatchedPropertyEffect(int matchedOwnedPropertiesCount) {
		this.rentStageIndex = matchedOwnedPropertiesCount-1;
    }
}
