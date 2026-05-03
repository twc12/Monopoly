package Spaces;

/**
 * TradeInfo: This classes purpose is to save
 * the previous Trade attempts by an AI Player
 * 
 * @author Alex
 */
public class TradeInfo {

	private int playersPreviousCashInWallet;
	private int previousTradeOfferAmt;
	
	/**
	 * Constructor: Builds a log of the previous trade
	 * @param tradeOfferAmt (int): The amount offered from the buying player for the property
	 * @param sellingPlayersCashAmt (int): A log of how much cash the selling player had
	 */
	public TradeInfo(int tradeOfferAmt, int sellingPlayersCashAmt) {
		playersPreviousCashInWallet = sellingPlayersCashAmt;
		previousTradeOfferAmt = tradeOfferAmt;
	}
	
	/**
	 * Getter: Returns the amount of cash the selling 
	 * player had in the last attempted trade
	 * @return int ^^^^
	 */
	public int getPlayersPrevCashAmount() {
		return playersPreviousCashInWallet;
	}
	
	/**
	 * Getter: Returns the previous amount offered for the trade 
	 * on this property
	 * @return int ^^^
	 */
	public int getPreviousTradeOfferAmt() {
		return previousTradeOfferAmt;
	}
}
