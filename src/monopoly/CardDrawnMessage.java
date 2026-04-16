package monopoly;
/**
 * The purpose of this is to notify the view to display
 * card info
 * 
 * @author Tyler Carpenter
 */
public class CardDrawnMessage {
    private Player player;
    private Card card;

    public CardDrawnMessage(Player player, Card card) {
        this.player = player;
        this.card = card;
    }

    public Player getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }
}
