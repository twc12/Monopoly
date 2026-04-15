package monopoly;


public class GoToJailSpace extends Space {
	
	private Jail jailSpaceObject;
	
	public GoToJailSpace() {
		super("GoToJailSpace");
	}
	
	public GoToJailSpace(Jail jailSpaceInModel) {
		jailSpaceObject = jailSpaceInModel;
	}
	
	public Jail getJailSpace() {
		return jailSpaceObject;
	}

	@Override
	protected void processSpace(Player player, Model model) {
		// TODO Auto-generated method stub
		
	}
	

}