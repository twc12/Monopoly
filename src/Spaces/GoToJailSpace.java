package Spaces;

import Monopoly.Model;

public class GoToJailSpace extends Space {
	
	private Jail jailSpaceObject;
	
	public GoToJailSpace(Jail jailSpaceInModel, String imageFile) {
		super("Go to Jail");
		jailSpaceObject = jailSpaceInModel;
		this.imageFile = imageFile;
	}
	
	public Jail getJailSpace() {
		return jailSpaceObject;
	}

	
	@Override
	protected void processSpace(Player player, Model model) {
		player.putInJail();
	}
	

}