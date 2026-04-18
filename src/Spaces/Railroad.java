package Spaces;

public class Railroad extends Property {
	
	public Railroad(String name) {
		int[] rentStages = new int[] {25, 50, 100, 200};

		super(name, 200, rentStages);
	}

	protected void applyMatchedPropertyEffect(int matchedOwnedPropertiesCount) {
		
		this.rentStageIndex = matchedOwnedPropertiesCount-1;
    }
}
