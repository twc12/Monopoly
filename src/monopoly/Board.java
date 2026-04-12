package monopoly;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import monopoly.Space.Color;

//11x11 board
public class Board extends Observable {
	
	private int totalSpaces = 40;	
	private int boardWidth = 11;
	
	
	private LinkedList<Space> spacesLinkedList;
	public Space firstSpace;
	
    public Board() {
        spacesLinkedList = new LinkedList<>();
        
        //constructing all spaces in an array so i can see them neatly
        Space[] spacesArray = {
            new GoSpace(),									//1
            new RealEstate(Color.BROWN),					//2
            new CommunityChest(),							//3
            new RealEstate(Color.BROWN),					//4
            new TaxSpace(TaxSpace.TaxSpaceType.INCOME),		//5
            new Railroad(),									
            new RealEstate(Color.LIGHTBLUE),
            new Chance(),
            new RealEstate(Color.LIGHTBLUE),
            new RealEstate(Color.LIGHTBLUE),				//10
            new Jail(),
            new RealEstate(Color.PINK),
            new Utility(),
            new RealEstate(Color.PINK),
            new RealEstate(Color.PINK),						//15
            new Railroad(),
            new RealEstate(Color.ORANGE),
            new CommunityChest(),
            new RealEstate(Color.ORANGE),
            new RealEstate(Color.ORANGE),					//20
            new FreeParking(),
            new RealEstate(Color.RED),
            new Chance(),
            new RealEstate(Color.RED),
            new RealEstate(Color.RED),						//25
            new Railroad(),
            new RealEstate(Color.YELLOW),
            new RealEstate(Color.YELLOW),
            new Utility(),
            new RealEstate(Color.YELLOW),					//30
            new Jail(),
            new RealEstate(Color.GREEN),
            new RealEstate(Color.GREEN),
            new CommunityChest(),
            new RealEstate(Color.GREEN),					//35
            new Railroad(),
            new Chance(),
            new RealEstate(Color.BLUE),
            new TaxSpace(TaxSpace.TaxSpaceType.LUXURY),
            new RealEstate(Color.BLUE),						//40
        };
 

        //populate the linkedlist from the array
        for (int i = 0; i < spacesArray.length; i++) {
        	spacesLinkedList.add(spacesArray[i]);
        }
        
        
        // set .next for each space
        for (int i = 0; i < spacesLinkedList.size(); i++) {
        	spacesLinkedList.get(i).setNextSpace(spacesLinkedList.get((i + 1)));
        }
 
        firstSpace = spacesLinkedList.get(0);
    }
	
	public int getTotalSpaces() {
		return totalSpaces;
	}
	
	public Space getFirstSpace() {
		return firstSpace;
	}
	
	public int getBoardWidth() {
		return boardWidth;
	}
	
	public List<Space> getSpaces() {
	    return spacesLinkedList;
	}
	
	
}
