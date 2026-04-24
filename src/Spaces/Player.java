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
import javafx.scene.image.Image;
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
    private Image playersIconImage; 
    private boolean gameOver = false;
    
    /**
     * Constructor for the Player Class
     * 
     * @param id the player identification
     * @param model the model for the player class to interact with
     * @param playersIconStr (String): The string of the first word of the icon this player will be 
     * 									e.g. "dog" "evil" "boat"
     * @param theme (String): The theme folder for this player icon to be loaded
     */
    public Player(int id, String playersIconStr, String theme, Model model) {
        playerId = id;
        this.model = model;
        inJail = false;
        cashAmmt = model.getGameSettings().getStartingMoney();// FUTURE - This should be updateable from user input on the start screen 
        
        // IMPORTANT NOTE: the naming format for the player icons for this implementation is "namePlayerIcon.png"
        Image playerIcon = new Image("/"+theme+"/"+playersIconStr+"PlayerIcon.png");
        playersIconImage = playerIcon;
       
        // TODO get model method
        currentSpace = model.board.getFirstSpace();
        listOfProperties = new ArrayList<Property>();
        ammtOfGetOutOfJailCards = 0;
        isDoneRollingDice = false;
    }

    /**
     * Checks if this is an AI player or not
     * @return true if so, false if not
     */
    public boolean isAI() {
        return false;
    }
    public String toString() {return "Player " + this.getId();}
    
    /**
     * @return The player id integer
     */
    public int getId() {return playerId;}
    
    /**
     * Getter: Returns the player name
     * FUTURE NOTE: This function should be changed to represent the user inputed
     * player name, this function is called by the view but because the 
     * custom names arent inputed right now it returns this basic string
     * 
     * @return String: The name of the player (LOOK 2 LINES UP AND READ)
     */
    public String getPlayerName() {
    	return "Player Id: " + getId(); // SHOULD BE CHANGED IN THE FUTURE TO THE PLAYERS NAME 
    }
    
    /**
     * @return The players total cash integer
     */
    public int getCashAmmt() {return cashAmmt;}
    
    /**
     * @return The players JavaFX Image of their icon
     */
    public Image getPlayerIconImage() { return playersIconImage; }
    
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
     * Returns how many houses owned based on the buildstage of player's properties (buildstages 1 through 4 would have houses)
     * @return The count of houses owned by the player
     */
    public int getHousesOwnedCount() {
    	int count = 0;
    	for (Property myProperty: this.listOfProperties) {
    		if (myProperty instanceof RealEstate re && re.getBuildingStage() >= 1 && re.getBuildingStage() <= 4) {
    			count += re.getBuildingStage();
    		} 
    	}
    	return count;
    }
    
    /**
     * Returns how many hotels owned based on the buildStage of player's properties (buildstages 5 would have a hotel)
     * @return The count of hotels owned by the player
     */
    public int getHotelsOwnedCount() {
    	int count = 0;
    	for (Property myProperty: this.listOfProperties) {
    		if (myProperty instanceof RealEstate re && re.getBuildingStage() == 5) {
    			count += 1;
    		} 
    	}
    	return count;
    }
    
    
    
    /**
     * @return the model object
     */
    public Model getModel() { return model; }
    
    /**
     * moves the player ammt of spaces forward. and processes space
     * 
     * @param ammt: The integer amount of spaces to move forward
     */
    public void move(int ammt) {

    	currentSpace.getPlayersOnSpace().remove(this); 

    	for (int i = 0; i < ammt; i++) {
    		if(currentSpace instanceof GoSpace && i != 0) // `i!=0` was added because players would get +$200 on their first move because it was go space
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
    	
    	
    	//if negative
    	if (ammt < 0) {
    		
    		int cost = Math.abs(ammt);
    		if (cost > this.getCashAmmt()) {
    			this.bankruptcy(cost);
    			//PLAYER GOING NEGATIVE
    			//AUTOSELL PROPERTIES/HOMES
    			//IF CAN'T SELL ENOUGH, PLAYER.LOSE

    		}
    	}
    	cashAmmt += ammt;
    }
    
    public boolean getGameOver() {
    	return this.gameOver;
    }
    
    private void bankruptcy(int ammtOwed) {
    	int ammtPayed = 0;
    	int buildingsSoldCount = 0;
    	List<Property> propertiesSold = new ArrayList<Property>();
    	
    	List<Property> myProperties = this.getListOfProperties();
    	//selling off all buildings first
    	for (Property property: myProperties) {
    		if (property instanceof RealEstate re && re.getBuildingStage() > 0) {
    			for (int i=0; i<re.getBuildingStage(); i++) {
        			ammtPayed += re.autoSellHouseHotel(this, model);
        			if (ammtPayed > ammtOwed) return;
        			buildingsSoldCount++;
        			i++;
    			}
    		}
    	}
    	//selling off properties
    	for (Property property: myProperties) {

			ammtPayed += property.autoSellProperty(this, model);
			if (ammtPayed > ammtOwed) return;
			propertiesSold.add(property);
    	}
    	
    	if (ammtOwed > ammtPayed) {
    		
    		this.gameOver = true;
    		System.out.println("GAME OVER");
    		// model.removeplayerfromturncycle
    	}
    	
    	
    	model.notifyViewBankruptcy(this, ammtPayed, buildingsSoldCount, propertiesSold, this.gameOver);
    	
    	
    	
    	
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
        setIsDoneRollingDice(true);
    	inJail = true;	
    	model.putPlayerInJail(this);
    }
    
    /**
     * Removes the player from jail
     */
    public void getOutOfJail() {
    	inJail = false; 
    	model.board.jailSpace.playerAttemptsToGetOutMapping.remove(this);
        // FUTURE PROOF: Call a model.notifyViewthatPlayerisOutOfJail() message so it can do sounds
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
    	int ammtMoved = 0;
    	currentSpace.getPlayersOnSpace().remove(this);
    	while (currentSpace.getName() != name) {
    		if(currentSpace.getName().equals(name))
    			currentSpace.processSpace(this, model);
    		ammtMoved++;
    		currentSpace = currentSpace.getNextSpace();
    	}
    	model.notifyViewOfPlayerMoved(this, ammtMoved);
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
				if (myProperty.getClass().equals(property.getClass())){
					matchedPropertiesCount+=1;
				}
			}
			
			//applying effect of matching properties (rent will increase/decrease)
			for (Property myProperty: this.getListOfProperties()) {
				if (myProperty.getClass().equals(property.getClass())){
					myProperty.applyMatchedPropertyEffect(matchedPropertiesCount);
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
