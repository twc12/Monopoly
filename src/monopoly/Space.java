package monopoly;

import java.util.HashSet;
import java.util.Set;
import javafx.scene.paint.Color;
public abstract class Space {
	
	
	public Space nextSpace;
	public String name;
	public Set<Player> playersOnSpace;

	
	/**
	 * Constructor: Initualizes a space to null everything
	 */
	public Space() {
		nextSpace = null;
		name = "Nameless";
		playersOnSpace = new HashSet<Player>();
	}
	
	/**
	 * Constructor: Initualizes a space 
	 * @param name (String): The name of the space "BoardWalk"
	 */
	public Space(String name) {
		nextSpace = null;
		this.name = name;
		playersOnSpace = new HashSet<Player>();
	}
	
	public String getName() {
		return name;
	}
	
	public Set<Player> getPlayersOnSpace(){
		return playersOnSpace;
	}
	
	public void setNextSpace(Space newSpace) {
		nextSpace = newSpace;
	}
	
	public Space getNextSpace() {
		return nextSpace;
	}
	


	protected abstract void processSpace(Player player, Model model);

		
	
}