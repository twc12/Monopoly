package Monopoly;

import java.util.List;

import Spaces.Player;
import Spaces.Property;

public class BankruptcyMessage {

	private int ammtPayed;
	private int buildingsSoldCount;
	private List<Property> propertiesSold;
	private Player player;
	
	public BankruptcyMessage(Player player, int ammtPayed, int buildingsSoldCount, List<Property> propertiesSold) {
		this.ammtPayed = ammtPayed;
		this.buildingsSoldCount = buildingsSoldCount;
		this.propertiesSold = propertiesSold;
	}
	
	public int getAmmtPayed() {
		return ammtPayed;
	}
	
	public int getBuildingsSoldCount() {
		return buildingsSoldCount;
	}
	
	public List<Property> getPropertiesSold(){
		return propertiesSold;
	}
	
	

}
