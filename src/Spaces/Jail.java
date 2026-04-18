package Spaces;

import java.util.Map;
import java.util.Set;

import Monopoly.Model;

public class Jail extends Space {
	
	
	private Set<Player> currPlayersInJail;
	private Map<Player, Integer> playerAttemptsToGetOutMap;
	
	public Jail() {
		super("Jail");
	}
	
	public Set<Player> getCurrentPlayersInJail(){
		return currPlayersInJail;
	}

	@Override
	protected void processSpace(Player player, Model model) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	

}
