package Monopoly;


public class GameSettings  {
	
	private int amountOfPlayers;
	private int amountOfAIPlayers;
	private int startingMoney;
	private int passGoValue;
	private int propertyPriceAdjust;   //not multiplying, this is just an int
	private boolean optionalBuying;
    private boolean freeParkingEnabled;
    private boolean tradingEnabled;
    
    public enum Theme {
        STANDARD,
        PIRATE,
        TUCSON
    }
    private Theme activeTheme = Theme.STANDARD;
    
    /**
     * Constructor: Sets default fields
     */
    public GameSettings() {
		amountOfPlayers = 4;
		amountOfAIPlayers = 1;
		startingMoney = 1500;
		passGoValue = 200;
		propertyPriceAdjust = 0;   //not multiplying, this is just an int
		optionalBuying = true;
		freeParkingEnabled = false;
		tradingEnabled = true;
    }
    
    /**
     * 
     * @return
     */
	public String getActiveThemeString() {
		switch(activeTheme) {
			case STANDARD: return "standardTheme"; 
			case PIRATE: return "pirateTheme";
			default: return "standardTheme";	
		}
	}
	
	/**
	 * 
	 * @param theme (ex: GameSettings.Theme STANDARD)
	 */
	public void setTheme(Theme theme) {
		this.activeTheme = theme;
	}
    
    public boolean getFreeParkingRule() { 
    	return freeParkingEnabled; 
	} 
    
    public int getCustomGoValue() { 
    	return passGoValue; 
	}
    
    public void setFreeParkingRule(boolean val) {
    	freeParkingEnabled = val;
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
		// If its not a new value, ignore it 
		if (amountOfAIPlayers == this.amountOfAIPlayers) {
			return;
		}
		System.out.println("GameSettings: AI Players = "+amountOfAIPlayers);
		this.amountOfAIPlayers = amountOfAIPlayers;
		
	}
	
	public int getAmountOfPlayers() {
		return amountOfPlayers;
	}
	
	public void setAmountOfPlayers(int amountOfPlayers) {
		// If its not a new value, ignore it 
		if (amountOfPlayers == this.amountOfPlayers) {
			return;
		}
		System.out.println("GameSettings: HUMAN Players = "+amountOfPlayers);
		this.amountOfPlayers = amountOfPlayers;
		
	}
	
	/**
	 * Getter: Returns if trading is enabled
	 * @return boolean: True if trading is enabled, false if not
	 */
	public boolean getTradingEnabled() {
		return tradingEnabled;
	}
	
	/**
	 * Setter: This sets the tradingEnabled to the new value 
	 * @param newTradingValue (Boolean): if trading is enabled or not 
	 */
	public void setTradingEnabled(boolean newTradingValue) {
		tradingEnabled = newTradingValue;
	}

	
	
	
}
