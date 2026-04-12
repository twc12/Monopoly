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
			
			Space current = new RealEstate(Space.Color.NONE);
			
			if (i==0) {
				head = current;
				firstSpace = head;
			}
			
			if (i == 1 || i ==3) {
				current.setColor(Color.BROWN);
			}
			
			if (i == 6 || i ==8 || i==9) {
				current.setColor(Color.LIGHTBLUE);
			}
			
			if (i == 11 || i == 13 || i==14) {
				current.setColor(Color.PINK);
			}
			
			if (i == 16 || i == 18 || i==19) {
				current.setColor(Color.ORANGE);
			}
			
		
			if (i == 21 || i == 23 || i==24) {
				current.setColor(Color.RED);
			}
			
			if (i == 26 || i == 27 || i==29) {
				current.setColor(Color.YELLOW);
			}
			
			if (i == 31 || i == 32 || i==34) {
				current.setColor(Color.GREEN);
			}
			
			if (i == 37 || i == 39) {
				current.setColor(Color.BLUE);
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
