package monopoly;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import monopoly.Space.Color;

//11x11 board
public class Board extends Observable {
	
	private int totalSpaces = 40;	
	private int boardWidth = 11;
	
	
	private LinkedList<Space> spacesList;
	public Space firstSpace;
	
	public Board() {
		spacesList = new LinkedList<>();
		
		Space head = null;
		Space prev = null;
					
		for (int i=0; i<totalSpaces; i++) {

			
			
			//building out board with all realestate spaces for now... proof of concept of grid working... sorry alex!
			
			Space current = new RealEstate(Space.Color.BLUE);
			
			if (i==0) {
				head = current;
				firstSpace = head;
				current.setColor(Color.GREEN);
			}
			
			
			
			
			
			
			
			
			
							
			if (prev != null) {
				prev.setNextSpace(current);
			}
			
			spacesList.add(current);
		
			
			
			
			
			
			
			
			
			
		}
		
		
		
		
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
	    return spacesList;
	}
	
	
}
