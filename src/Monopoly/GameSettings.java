package Monopoly;

import java.io.Serializable;

/**
 * GameSettings - the player's chosen rules/theme selected when starting the game
 */
public class GameSettings implements Serializable{
	private static final long serialVersionUID = 1L;
	
	//variables for each of the configurable rules/themes
	private int amountOfPlayers;
	private int amountOfAIPlayers;
	private int startingMoney;
	private int passGoValue;
	private int propertyPriceAdjust;//multiplier
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
		propertyPriceAdjust = 1;//multiplier
		optionalBuying = true;
		freeParkingEnabled = false;
		tradingEnabled = true;
    }
    
    /**
     * Gets a string for the current active game theme
     * @return A string, the current game theme
     */
	public String getActiveThemeString() {
		switch(activeTheme) {
			case STANDARD: return "standardTheme"; 
			case PIRATE: return "pirateTheme";
			case TUCSON: return "tucsonTheme";
			default: return "standardTheme";	
		}
	}
	
	/**
	 * Sets the current game theme based onthe input parameter
	 * @param theme the theme the game is being set to
	 */
	public void setTheme(Theme theme) {
		this.activeTheme = theme;
	}
    
	/**
	 * Gets if the free parking rule is enabled
	 * @return a boolean, true if rule is enabled, false otherwise
	 */
    public boolean getFreeParkingRule() { 
    	return freeParkingEnabled; 
	} 
    
	/**
	 * Gets if the GO space will have a custom value instead of the standard 
	 * $200 for passing it
	 * @return The set go value if it's custom as an integer
	 */
    public int getCustomGoValue() { 
    	return passGoValue; 
	}
    
	/**
	 * Sets the rule for freeparking, true if enabled, false otherwise
	 * @param val a boolean represenitng the status of the free parking rule
	 */
    public void setFreeParkingRule(boolean val) {
    	freeParkingEnabled = val;
    }

	/**
	 * Sets the custom value for passing the GO space
	 * @param val the custom go value being set to
	 */
    public void setCustomGoValue(int val) {
    	passGoValue = val;
    }
    
	/**
	 * Gets value for if optional buying setting is enabled or not
	 * @return true if enabled, false otherwise
	 */
	public boolean getOptionalBuying() {
		return optionalBuying;
	}
	
	/**
	 * Sets the optional buying rule up
	 * @param optionalBuying a boolean represenitng if the rule is enabled or not
	 */
	public void setOptionalBuying(boolean optionalBuying) {
		this.optionalBuying = optionalBuying;
	}
	
	/**
	 * Gets if the property price has been adjusted or not
	 * @return an integer, represenitng the new adjusted property price
	 */
	public int getPropertyPriceAdjust() {
		return propertyPriceAdjust;
	}
	
	/**
	 * Sets the new adjusted property price
	 * @param propertyPriceAdjust The new adjusted property price
	 */
	public void setPropertyPriceAdjust(int propertyPriceAdjust) {
		this.propertyPriceAdjust = propertyPriceAdjust;
	}
	
	/**
	 * Grabs hte current starting money
	 * @return the money players have at the start of the game
	 */
	public int getStartingMoney() {
		return startingMoney;
	}
	
	/**
	 * Sets the starting money, and has a check to prevent setting it to
	 * any negative numbers
	 * @param startingMoney the new starting money amount
	 */
	public void setStartingMoney(int startingMoney) {	
		if (startingMoney < 0) {
			this.startingMoney = 0;
		} else {
			this.startingMoney = startingMoney;
		}
	}
	
	/**
	 * Gets the amount of AI players in the game, this is set in the game 
	 * settings
	 * @return the number of Ai players present
	 */
	public int getAmountOfAIPlayers() {
		return amountOfAIPlayers;
	}
	
	/**
	 * Sets the amount of AI players present in the game
	 * @param amountOfAIPlayers the number of AI in the game
	 */
	public void setAmountOfAIPlayers(int amountOfAIPlayers) {
		// If its not a new value, ignore it 
		if (amountOfAIPlayers == this.amountOfAIPlayers) {
			return;
		}
		System.out.println("[+] GameSettings: AI Players = "+amountOfAIPlayers);
		this.amountOfAIPlayers = amountOfAIPlayers;
		
	}
	
	/**
	 * Gets the amount of players in the game TOTAL
	 * @return the number of total players
	 */
	public int getAmountOfPlayers() {
		return amountOfPlayers;
	}
	
	/**
	 * Sets the amount of players in the game, set in game settings
	 * @param amountOfPlayers the amount of players in the game
	 */
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
