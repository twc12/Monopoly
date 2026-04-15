package monopoly;

public class PurchasePromptMessage {
	private Player currentPlayer;
	private Property property;

	public PurchasePromptMessage(Player currentPlayer, Property property) {
		this.currentPlayer = currentPlayer;
		this.property = property;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	public Property getProperty() {
		return property;
	}
}
