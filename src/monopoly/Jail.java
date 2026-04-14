package monopoly;

import java.util.Map;
import java.util.Set;

public class Jail extends Space {
	
	
	private Set<Player> currPlayersInJail;
	private Map<Player, Integer> playerAttemptsToGetOutMap;
	
	public Jail() {
		super("JailSpace");
	}
	
	public Set<Player> getCurrentPlayersInJail(){
		return currPlayersInJail;
	}
	
	
	
	

}
