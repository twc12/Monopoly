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
	public Jail jailSpace;
	
    public Board() {
        spacesLinkedList = new LinkedList<>();
        
        jailSpace = new Jail();
        
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
            jailSpace,
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
            new GoToJailSpace(jailSpace),
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
        for (int i = 0; i < spacesLinkedList.size()-1; i++) {
        	spacesLinkedList.get(i).setNextSpace(spacesLinkedList.get((i + 1)));
        }
        firstSpace = spacesLinkedList.get(0); //class attribute firstSpace
        spacesLinkedList.get(spacesLinkedList.size()-1).setNextSpace(firstSpace); // connecting the last space to the first

 
    }
	
	public int getTotalSpaces() {
		return totalSpaces;
	}
	
	public Space getFirstSpace() {
		return firstSpace;
	}
	
	public Space getJailSpace() {
		return jailSpace;
	}
	
	public int getBoardWidth() {
		return boardWidth;
	}
	
	/**
	 * getSpaces(): Returns a list of Space objects
	 * that make up the board
	 * @return List<Space>: All the spaces on the board
	 */
	public List<Space> getSpaces() {
	    return spacesLinkedList;
	}
	
	
}
