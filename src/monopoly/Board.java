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
        
        //building full board
        Space[] spacesArray = {
            new GoSpace(),									//1
            new RealEstate(Color.BROWN, "Mediterranean Avenue", 60),					//2
            new CommunityChest(),							//3
            new RealEstate(Color.BROWN, "Baltic Avenue", 60),					//4
            new TaxSpace(TaxSpace.TaxSpaceType.INCOME),		//5
            new Railroad(),									
            new RealEstate(Color.LIGHTBLUE, "Oriental Avenue", 100),
            new Chance(),
            new RealEstate(Color.LIGHTBLUE, "Vermont Avenue", 100),
            new RealEstate(Color.LIGHTBLUE, "Connecticut Avenue", 120),				//10
            jailSpace,
            new RealEstate(Color.PINK, "St. Charles Place", 140),
            new Utility("Electric Company"),
            new RealEstate(Color.PINK, "States Avenue", 140),
            new RealEstate(Color.PINK, "Virginia Avenue", 160),						//15
            new Railroad(),
            new RealEstate(Color.ORANGE,"St. James Place", 180),
            new CommunityChest(),
            new RealEstate(Color.ORANGE, "Tennessee Avenue", 180),
            new RealEstate(Color.ORANGE, "New York Avenue", 200),					//20
            new FreeParking(),
            new RealEstate(Color.RED, "Kentucky Avenue", 220),
            new Chance(),
            new RealEstate(Color.RED, "Indiana Avenue", 220),
            new RealEstate(Color.RED, "Illinois Avenue", 240),						//25
            new Railroad(),
            new RealEstate(Color.YELLOW, "Atlantic Avenue", 260),
            new RealEstate(Color.YELLOW, "Ventnor Avenue", 260),
            new Utility("Water Works"),
            new RealEstate(Color.YELLOW, "Marvin Gardens", 280),					//30
            new GoToJailSpace(jailSpace),
            new RealEstate(Color.GREEN, "Pacific Avenue", 300),
            new RealEstate(Color.GREEN, "North Carolina Avenue", 300),
            new CommunityChest(),
            new RealEstate(Color.GREEN, "Pennsylvania Avenue", 320),					//35
            new Railroad(),
            new Chance(),
            new RealEstate(Color.BLUE, "Park Place", 350),
            new TaxSpace(TaxSpace.TaxSpaceType.LUXURY),
            new RealEstate(Color.BLUE, "Board Walk", 400),						//40
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
