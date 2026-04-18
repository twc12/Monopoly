package Messages;

/**
 * This classes purpose is to return the dice 
 * roll result from the model to the viewer so it can animate it
 * 
 * Fields:
 * 	int dice1Result
 * 	int dice2Result
 * 
 * @author Alex Myers
 */
public class DiceRollResultMessage {
	private int dice1Result;
	private int dice2Result;
	
	/**
	 * Constructor: Builds the DiceRollResult Message
	 * @param dice1Result (int): Result of dice 1
	 * @param dice2Result (int): Result of dice 2 
	 */
	public DiceRollResultMessage(int dice1Result, int dice2Result) {
		this.dice1Result = dice1Result;
		this.dice2Result = dice2Result;
	}
	
	/**
	 * Getter: Returns dice 1 result
	 * @return int: Dice 1 result for this message
	 */
	public int getDice1Result() {
		return dice1Result;
	}
	
	/**
	 * Getter: Returns dice 2 result
	 * @return int: Dice 2 result for this message
	 */
	public int getDice2Result() {
		return dice2Result;
	}
}
