/**
 * This file contains the Player Class. The Player class
 * contains all player information including cash and owned properties.
 * 
 * 
 * @author Tyler Carpenter
 */
package Spaces;

import java.util.*;

import Monopoly.Model;
import javafx.scene.paint.Color;
public class Player {
	
    private int playerId;
    private boolean inJail;
    private int cashAmmt;
    private Space currentSpace;
    private List<Property> listOfProperties;
    private int ammtOfGetOutOfJailCards; 
    private Model model;
    private boolean isDoneRollingDice;
    private Color playersColor; 
    
    /**
     * Constructor for the Player Class
     * 
     * @param id the player identification
     * @param model the model for the player class to interact with
     */
    public Player(int id, Color playersColor, Model model) {
        playerId = id;
        this.model = model;
        inJail = false;
        cashAmmt = 1500; // FUTURE - This should be updateable from user input on the start screen 
        this.playersColor = playersColor;
       
        // TODO get model method
        currentSpace = model.board.getFirstSpace();
        listOfProperties = new ArrayList<Property>();
        ammtOfGetOutOfJailCards = 0;
        isDoneRollingDice = false;
    }
    
    public String toString() {return "Player " + this.getId();}
    
    /**
     * @return The player id integer
     */
    public int getId() {return playerId;}
    
    /**
     * @return The players total cash integer
     */
    public int getCashAmmt() {return cashAmmt;}
    
    /**
     * @return The players JavaFx color
     */
    public Color getColor() { return playersColor; }
    
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
    public List<Property> getListOfProperties(){return listOfProperties;}
    
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
    	System.out.println("Processing Space: " + currentSpace.getName());
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
    	ammtOfGetOutOfJailCards++;
    }

    /**
     * Removes the players get out of jail free card when they use it
     */
    public void removeJailCard() {
        ammtOfGetOutOfJailCards--;
    }
    
    /**
     * @return int: The number of get out of jail cards this player has
     */
    public int getAmmtOfGOOJCards() {
    	return ammtOfGetOutOfJailCards;
    }
    
    /**
     * putInJail()
     */
    public void putInJail() {
    	inJail = true;
    	
    	model.putPlayerInJail(this);
    }
    
    /**
     * Removes the player from jail
     */
    public void getOutOfJail() {
    	inJail = false; 
    	model.board.jailSpace.playerAttemptsToGetOutMapping.remove(this);
        model.setCurrentPlayerToNext();
    }
    
    /**
     * Removes property from players owned property list 
     * 
     * @param property: the property in the list to remove
     */
    public void removeProperty(Property property) {
    	listOfProperties.remove(property);
    	updatePropertiesMatches(property);

    }
    
    /**
     * Adds a Property to the players property list
     * 
     * @param property to add to the list
     */
    public void addProperty(Property property) {
    	listOfProperties.add(property);
    	updatePropertiesMatches(property);
   	
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
    
    private void updatePropertiesMatches(Property property) {
       	
    	//updating other properties of the same type that i own to have increased rents
    	if (!(property instanceof RealEstate)) {//only railroads+utility
	   		
    		//scanning for matching properties
    		int matchedPropertiesCount = 0;
			for (Property myProperty: this.getListOfProperties()) {
				if (myProperty.getClass().equals(this.getClass())){
					matchedPropertiesCount+=1;
				}
			}
			
			//applying effect of matching properties (rent will increase/decrease)
			for (Property myProperty: this.getListOfProperties()) {
				if (myProperty.getClass().equals(this.getClass())){
					property.applyMatchedPropertyEffect(matchedPropertiesCount);
				}
			} 
		}else {// only real-estate
	   		int matchedPropertiesCount = 0;
	   		RealEstate realEstate = (RealEstate)property;
	   		
    		//scanning for matching colors
			for (Property myProperty: this.getListOfProperties()) {
				if (myProperty instanceof RealEstate) {
			   		RealEstate myRealEstate = (RealEstate)myProperty;
					if (myRealEstate.getColor().equals(realEstate.getColor())){
						matchedPropertiesCount+=1;
					}
				}
			}

			//applying effect to owned properties with matching colors
			for (Property myProperty: this.getListOfProperties()) {
				if (myProperty instanceof RealEstate) {
			   		RealEstate myRealEstate = (RealEstate)myProperty;
					if (myRealEstate.getColor().equals(realEstate.getColor())){
						myProperty.applyMatchedPropertyEffect(matchedPropertiesCount);
					}
				}
			} 
		}  
    }
    
}
