package Spaces;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import Monopoly.Model;
import javafx.scene.paint.Color;
public abstract class Space implements Serializable{
	private static final long serialVersionUID = 1L;
	
	
	public Space nextSpace;
	public String name;
	public Set<Player> playersOnSpace;
	public String imageFile = "";
	/**
	 * Constructor: Initualizes a space 
	 * @param name (String): The name of the space "BoardWalk"
	 */
	public Space(String name) {
		nextSpace = null;
		this.name = name;
		playersOnSpace = new HashSet<Player>();
	}
	
	/**
	 * @return A string, the name of this space
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return A set of each player on this space
	 */
	public Set<Player> getPlayersOnSpace(){
		return playersOnSpace;
	}
	
	/**
	 * sets the space after this current space
	 * @param newSpace the space after
	 */
	public void setNextSpace(Space newSpace) {
		nextSpace = newSpace;
	}
	
	/**
	 * @return the sequential next space from this one
	 */
	public Space getNextSpace() {
		return nextSpace;
	}
	
	/**
	 * @return boolean, if hte space has an image or not
	 */
	public boolean hasImage() {
		if(!imageFile.equals(""))
			return true;
		return false;
	}
	
	/**
	 * @return a string, the path of the image file
	 */
	public String getImageFile() {
		return imageFile;
	}
	
	/**
	 * Checks if another object equals this space based on name
	 * @param Object other the other object
	 */
	@Override 
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof Space) {
			Space otherSpace = (Space) other;
			if (otherSpace.name.equals(this.name)) {
				return true;
			}
		}
		return false;
	}

	protected abstract void processSpace(Player player, Model model);

		
	
	
}