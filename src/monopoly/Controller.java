package monopoly;

import java.util.List;
import java.util.*;
public class Controller {

	private Model model;
	
	
	public Controller(View viewClassObj) {
		model = new Model(viewClassObj);
	}
	
	/**
	 * rollDice(player): This function is given a player it rolls dice
	 * for and it will randomly generate dice rolls then send that message to the view
	 * then it will move the player, then notify the view again.
	 * The player.move function will account for it the player passes go
	 * @param player (Player): The current player rolling the dice 
	 */
	public void rollDice(Player player) {
		
		Random rand = new Random();
		int dice1Result;
		int dice2Result;
		
		// Roll dice, place into list
		dice1Result = rand.nextInt(6)+1;
		dice2Result = rand.nextInt(6)+1;
		
		// if the player did not get doubles then they are DONE ROLLING DICE 
		if (dice1Result != dice2Result) {
			player.setIsDoneRollingDice(true);
		}
		
		model.notifyViewOfDiceResult(dice1Result, dice2Result);
		
		int ammtMoved = dice1Result+dice2Result;
		
		// Move player 
		player.move(ammtMoved);
		 
		//TODO
		//	process on enter space logic
		
		model.notifyViewOfPlayerMoved(player, ammtMoved);
	}
	
	/**
	 * This is called from the view when the player presses "end turn"
	 * This function will move the models current player to the next on 
	 * in the list of players.
	 * 
	 * This function doesnt accept a currentPlayer parameter because 
	 * this controller can get the current player anyways 
	 * 
	 * What does it mean to end a turn?
	 * Is it just changing the "currentPlayer" to the next one?
	 * 	- if so then great!
	 */
	public void processEndTurn() {
		model.setCurrentPlayerToNext();
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
	
	public Player getCurrentPlayer() {
		return model.getCurrentPlayer();
	}
	
	public List<Player> getAllPlayers(){
		return model.getPlayers();
	}
	
	
	
}
