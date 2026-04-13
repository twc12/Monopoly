package monopoly;

import java.util.List;
import java.util.*;
public class Controller {

	private Model model;
	
	
	public Controller() {
		model = new Model();
	}
	
	public ArrayList<Integer> rollDice(Player player) {
		ArrayList<Integer> rolls = new ArrayList<>();
		Random rand = new Random();
		int dice;
		
		// Roll dice, place into list
		dice = rand.nextInt(6)+1;
		rolls.set(0, dice);
		dice = rand.nextInt(6)+1;
		rolls.set(1,dice);
		
		// Move player 
		player.move(rolls.get(0)+rolls.get(1));
		
		//TODO
		//	process on enter space logic
		
		return rolls;
	}
	
	public int getTotalSpaces() {
		return model.board.getTotalSpaces();
	}
	
	/**
	 * getSpaces(): Returns a list of Space objects
	 * that make up the board
	 * @return List<Space>: All the spaces on the board
	 */
	public List<Space> getSpaces(){
		return model.board.getSpaces();
	}
	
	
	public int getBoardWidth() {
		return model.board.getBoardWidth();
	}
	
	public Space getFirstSpace() {
		return model.board.getFirstSpace();
	}
	
	
}
