package Messages;

import java.util.List;

import Spaces.Player;
import Spaces.Property;

public class BankruptcyMessage {

	private int ammtOwed;
	private int buildingsSoldCount;
	private List<Property> propertiesSold;
	private Player player;
	private boolean gameOver;
	
	public BankruptcyMessage(Player player, int ammtOwed, int buildingsSoldCount, List<Property> propertiesSold, boolean gameOver) {
		this.ammtOwed = ammtOwed;
		this.buildingsSoldCount = buildingsSoldCount;
		this.propertiesSold = propertiesSold;
		this.gameOver = gameOver;
		this.player = player;
	}

	/**
	 * GETTER: Gets the amount of money the player pays to get out of bankrupcy
	 * @return an integer representing the amount of money needed to pay
	 */
	public int getAmmtOwed() {
		return ammtOwed;
	}
	
	/**
	 * GETTER: Gets the amount of buildings sold to get out of bankruptcy
	 * @return an integer representing the amount of buildings sold 
	 */
	public int getBuildingsSoldCount() {
		return buildingsSoldCount;
	}
	
	/**
	 * GETTER: Gets the specific property objects that were sold
	 * @return a List of the properties sold 
	 */
	public List<Property> getPropertiesSold(){
		return propertiesSold;
	}
	
	/**
	 * GETTER: Gets the status of this player's game, if the game is over or not
	 * @return a boolean representing the game state for this current player
	 */
	public boolean getGameOver() {
		return gameOver;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	

}
