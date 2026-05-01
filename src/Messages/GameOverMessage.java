package Messages;

import Spaces.Player;

public class GameOverMessage {

	private Player player;
	
	public GameOverMessage(Player winner) {
		this.player=winner;
	}
	
	public String getPlayerName() {
		return player.toString();
	}

}
