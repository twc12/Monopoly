package monopoly;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Jail extends Space {
	
	
	private Set<Player> currPlayersInJail;
	public  Map<Player, Integer> playerAttemptsToGetOutMapping;
	
	public Jail() {
		super("Jail");
		playerAttemptsToGetOutMapping = new HashMap<>();
	}
	
	public Set<Player> getCurrentPlayersInJail(){
		return currPlayersInJail;
	}
	
	/**
	 * Getter: it will ask the Jail space in the model how many times a player has 
	 * attempted to get out of jail.
	 * This function is only called in the view if the player.isInJail() return true
	 * so that means they MUST be in the jail.playerAttemptsToGetOutMapping. If not
	 * that is major problem, send -1 to notify the model of error
	 * @param playerInJail (Player): The player that should be in jail
	 * @return int: the number of times the player has tried to roll doubles to get out of jail, -1 if not in map
	 */
	public int getAmmtOfJailAttempts(Player playerInJail) {
		if (! playerAttemptsToGetOutMapping.containsKey(playerInJail)) {
			return -1;
		}
		return playerAttemptsToGetOutMapping.get(playerInJail);
	}
	

	@Override
	protected void processSpace(Player player, Model model) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	

}
