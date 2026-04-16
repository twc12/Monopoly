/**
 * This file contains the Player Class. The Player class
 * contains all player information including cash and owned properties.
 * 
 * 
 * @author Tyler Carpenter
 */
package monopoly;

import java.util.*;
public class Player {
	
    private int playerId;
    private boolean inJail;
    private int cashAmmt;
    private Space currentSpace;
    private ArrayList<Property> listOfProperties;
    private int outOfJailCards; 
    private Model model;
    private boolean isDoneRollingDice;
    
    /**
     * Constructor for the Player Class
     * 
     * @param id the player identification
     * @param model the model for the player class to interact with
     */
    public Player(int id, Model model) {
        playerId = id;
        this.model = model;
        inJail = false;
        cashAmmt = 1500;
       
        // TODO get model method
        currentSpace = model.board.getFirstSpace();
        listOfProperties = new ArrayList<Property>();
        outOfJailCards = 0;
        isDoneRollingDice = false;
    }
    
    /**
     * @return The player id integer
     */
    public int getId() {return playerId;}
    
    /**
     * @return The players total cash integer
     */
    public int getCashAmmt() {return cashAmmt;}
    
    /**
     * @return The players jail status boolean
     */
    public boolean isInJail() {return inJail;}
    
    /**
     * @return the current Space object the user is located
     */
    public Space getCurrentSpace() {return currentSpace;}
    
    /**
     * @return The list of the players owned properties
     */
    public ArrayList<Property> getListOfProperties(){return listOfProperties;}
    
    /**
     * moves the player ammt of spaces forward. and processes space
     * 
     * @param ammt: The integer amount of spaces to move forward
     */
    public void move(int ammt) {
    	
    	
    	currentSpace.getPlayersOnSpace().remove(this);
    	for (int i = 0; i < ammt; i++) {
    		if(currentSpace instanceof GoSpace)
    			currentSpace.processSpace(this, model);
    		currentSpace = currentSpace.getNextSpace();
    	}
    	currentSpace.getPlayersOnSpace().add(this);
    	
    	model.notifyViewOfPlayerMoved(this, ammt);
    	currentSpace.processSpace(this, model);
    	
    }
    
    /**
     * adds ammt of cash to player
     * 
     * @param ammt: amount of cash to add
     */
    public void addCash(int ammt) {
    	cashAmmt += ammt;
    }
    
    /**
     * Gives the player a get out of jail free card
     */
    public void addJailCard() {
    	outOfJailCards++;
    }
    
    /**
     * Sets the player in jail
     */
    public void putInJail() {
    	inJail = true;
    	int ammtMoved = 0;
    	currentSpace.getPlayersOnSpace().remove(this);
    	while (!(currentSpace instanceof Jail)) {
    		ammtMoved++;
    		currentSpace = currentSpace.getNextSpace();
    	}
    	model.notifyViewOfPlayerMoved(this, ammtMoved);
    }
    
    /**
     * Removes the player from jail
     */
    public void getOutOfJail() {
    	inJail = false;
    }
    
    /**
     * Removes property from players owned property list 
     * 
     * @param property: the property in the list to remove
     */
    public void removeProperty(Property property) {
    	listOfProperties.remove(property);
    }
    
    /**
     * Adds a Property to the players property list
     * 
     * @param property to add to the list
     */
    public void addProperty(Property property) {
    	listOfProperties.add(property);
    }
    
    /**
     * Advances the player to the Go/Start position, adds the start
     * specified amount of cash to player.
     */
    public void advanceToGo() {
    	int ammtMoved = 0;
    	currentSpace.getPlayersOnSpace().remove(this);
    	while (!(currentSpace instanceof GoSpace)) {
    		ammtMoved++;
    		currentSpace = currentSpace.getNextSpace();
    	}
    	model.notifyViewOfPlayerMoved(this, ammtMoved);
    	currentSpace.processSpace(this, model);
    }
    
    
    /**
     * Advances player to nearest Railroad
     */
    public void advanceToRailroad() {
    	int ammtMoved = 0;
    	currentSpace.getPlayersOnSpace().remove(this);
    	while (!(currentSpace instanceof Railroad)) {
    		if(currentSpace instanceof GoSpace)
    			currentSpace.processSpace(this, model);
    		ammtMoved++;
    		currentSpace = currentSpace.getNextSpace();
    	}
    	model.notifyViewOfPlayerMoved(this, ammtMoved);
    	currentSpace.processSpace(this, model);
    }
    
    /**
     * Advances player to a specific property
     * 
     * @param name property to move to
     */
    public void advanceToProperty(String name) {
    	currentSpace.getPlayersOnSpace().remove(this);
    	while (currentSpace.getName() != name) {
    		if(currentSpace instanceof GoSpace)
    			currentSpace.processSpace(this, model);
    		currentSpace = currentSpace.getNextSpace();
    	}
    	currentSpace.processSpace(this, model);
    }
    
    /**
     * Advances player to nearest utility
     */
    public void advanceToUtility() {
    	int ammtMoved = 0;
    	currentSpace.getPlayersOnSpace().remove(this);
    	while (!(currentSpace instanceof Utility)) {
    		if(currentSpace instanceof GoSpace)
    			currentSpace.processSpace(this, model);
    		ammtMoved++;
    		currentSpace = currentSpace.getNextSpace();
    	}
    	model.notifyViewOfPlayerMoved(this, ammtMoved);
    	currentSpace.processSpace(this, model);
    }
    
    /**
     * Returns if the player is done rolling dice 
     * @return Boolean: True if the player is done rolling dice 
     */
    public boolean getIsDoneRollingDice() {
    	return isDoneRollingDice;
    }
    
    /**
     * Setter for saying if this player is done rolling dice
     * @param playerIsDoneRollingDice (bool): True if the player is done rolling dice 
     */
    public void setIsDoneRollingDice(boolean playerIsDoneRollingDice) {
    	isDoneRollingDice = playerIsDoneRollingDice;
    }
    
}
