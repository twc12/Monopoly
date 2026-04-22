package Monopoly;

import java.util.HashMap;

public class GameSettings  {
	
	private int amountOfPlayers = 4;
	private int amountOfAIPlayers = 1;
	private int startingMoney = 1500;
	private int passGoValue = 200;
	private int propertyPriceAdjust = 0;   //not multiplying, this is just an int
	private boolean optionalBuying = true;
    private boolean freeParkingEnabled = false;    
    public enum Theme {
        STANDARD,
        PIRATE,
        TUCSON
    }
    private Theme activeTheme = Theme.STANDARD;
    
	public String getActiveThemeString() {
		switch(activeTheme) {
			case STANDARD: return "standardTheme"; 
			case PIRATE: return "pirateTheme";
			default: return "standardTheme";	
		}
	}
	
	public void setTheme(Theme theme) {
		this.activeTheme = theme;
	}
    
    public boolean getFreeParkingRule() { 
    	return freeParkingEnabled; 
	} 
    
    public int getCustomGoValue() { 
    	return passGoValue; 
	}
    
    public void setFreeParkingRule() {
    	freeParkingEnabled = true;
    }

    public void setCustomGoValue(int val) {
    	passGoValue = val;
    }
    
	public boolean getOptionalBuying() {
		return optionalBuying;
	}
	
	public void setOptionalBuying(boolean optionalBuying) {
		this.optionalBuying = optionalBuying;
	}
	
	public int getPropertyPriceAdjust() {
		return propertyPriceAdjust;
	}
	
	public void setPropertyPriceAdjust(int propertyPriceAdjust) {
		if (propertyPriceAdjust < -60) { //because otherwise baltic avenue would be negative price to buy
			this.propertyPriceAdjust = -60;
		} else {
			this.propertyPriceAdjust = propertyPriceAdjust;
		}
	}
	
	public int getStartingMoney() {
		return startingMoney;
	}
	
	public void setStartingMoney(int startingMoney) {
		
		if (startingMoney < 0) {
			this.startingMoney = 0;
		} else {
			this.startingMoney = startingMoney;
		}
	}
	
	public int getAmountOfAIPlayers() {
		return amountOfAIPlayers;
	}
	
	public void setAmountOfAIPlayers(int amountOfAIPlayers) {
		if (amountOfAIPlayers > 7) {
			this.amountOfAIPlayers = 7;
		} else {
			this.amountOfAIPlayers = amountOfAIPlayers;
		}
	}
	
	public int getAmountOfPlayers() {
		return amountOfPlayers;
	}
	
	public void setAmountOfPlayers(int amountOfPlayers) {
		if (amountOfPlayers > 8) {
			this.amountOfPlayers = 8;
		} else if (amountOfPlayers < 2) {
			this.amountOfPlayers = 2;
		} else {
			this.amountOfPlayers = amountOfPlayers;
		}
	}

	
	
	
}
