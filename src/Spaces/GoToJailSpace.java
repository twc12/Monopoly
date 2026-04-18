package Spaces;

import Monopoly.Model;

public class GoToJailSpace extends Space {
	
	private Jail jailSpaceObject;
	
	public GoToJailSpace(Jail jailSpaceInModel) {
		super("Go to Jail");
		jailSpaceObject = jailSpaceInModel;
	}
	
	public Jail getJailSpace() {
		return jailSpaceObject;
	}

	@Override
	protected void processSpace(Player player, Model model) {
		player.putInJail();
	}
	

}