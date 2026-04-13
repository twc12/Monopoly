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
     * moves the player ammt of spaces forward.
     * 
     * @param ammt: The integer amount of spaces to move forward
     */
    public void move(int ammt) {
    	currentSpace.getPlayersOnSpace().remove(this);
    	for (int i = 0; i < ammt; i++) {
    		currentSpace = currentSpace.getNextSpace();
    		if(currentSpace == model.board.getFirstSpace())
    			this.addCash(200);
    	}
    	currentSpace.getPlayersOnSpace().add(this);
    	
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
    	//TODO move to jail space
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
    	//TODO add dynamic go amount
    	currentSpace = model.board.getFirstSpace();
    	this.addCash(200);
    }
    
}
