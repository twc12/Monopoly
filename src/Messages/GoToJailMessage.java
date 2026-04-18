package Messages;

import Spaces.Player;

/**
 * @author Jarrod Heyer Martinez
 * 
 * Standard message for a player to go to jail, may need some tweaking as I wrote 
 * it mostly based on what other messages we alrady have.
 */
public class GoToJailMessage {

    private Player currPlayer;

    public GoToJailMessage(Player player) {
        currPlayer = player;
    }

    public Player getPlayerGoingToJail() {
        return currPlayer;
    }

    
}
