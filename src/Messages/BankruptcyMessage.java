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
	
	public int getAmmtOwed() {
		return ammtOwed;
	}
	
	public int getBuildingsSoldCount() {
		return buildingsSoldCount;
	}
	
	public List<Property> getPropertiesSold(){
		return propertiesSold;
	}
	
	public boolean getGameOver() {
		return gameOver;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	

}
