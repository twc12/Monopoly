package monopoly;


public class GoToJailSpace extends Space {
	
	private Jail jailSpaceObject;
	
	public GoToJailSpace(Jail jailSpaceInModel) {
		jailSpaceObject = jailSpaceInModel;
	}
	
	public Jail getJailSpace() {
		return jailSpaceObject;
	}
	

}