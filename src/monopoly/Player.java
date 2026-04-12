package monopoly;
import java.util.*;
public class Player {
	
    private int playerId;
    private boolean inJail;
    private int cashAmmt;
    private Space currentSpace;
    private ArrayList<Space> listOfProperties;
    private int outOfJailCards; 
    public Player(int id) {
        playerId = id;
        inJail = false;
        cashAmmt = 1500;
        currentSpace = Board.getFirstSpace();
        listOfProperties = new ArrayList<Space>();
        outOfJailCards = 0;
    }
    
    // Getters
    public int getId() {return playerId;}
    
    public int getCashAmmt() {return cashAmmt;}
    
    public boolean isInJail() {return inJail;}
    
    public Space getCurrentSpace() {return currentSpace;}
    
    public ArrayList<Space> getListOfProperties(){return listOfProperties;}
    
    // Setters
    public void move(int ammt) {
    	for (int i = 0; i < ammt; i++) {
    		currentSpace = currentSpace.getNextSpace();
    	}
    }
    
    public void addCash(int ammt) {
    	cashAmmt += ammt;
    }
    
    public void addJailCard() {
    	outOfJailCards++;
    }
    
    public void putInJail() {
    	inJail = true;
    	//TODO move to jail space
    }
    
    public void getOutOfJail() {
    	inJail = false;
    }
    
    public void removeProperty(Space property) {
    	listOfProperties.remove(property);
    }
    
    public void addProperty(Space property) {
    	listOfProperties.add(property);
    }
    
    /*
     * TODO
     * 		Add card effect setter after card implementation
     */
    
    
    

}
