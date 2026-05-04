package Messages;

import Spaces.AIPlayer;
import Spaces.Player;
import Spaces.Property;

/**
 * AiAttemptingTradeMessage:
 * This Class is used to tell the view that
 * a ai player is attempting to trade with someone else .
 * 
 * The view will know if the ai player is trying to trade with another
 * ai by checking if the sellerPlayer instanceof AIPlayer is true
 * 
 * Read up on the models notifyView function that uses 
 * this message to learn more about how this works 
 * 
 * @author Alex
 */
public class AiAttemptingTradeMessage {
	
	private AIPlayer aiBuyer;
	private Property thePropertyTheBuyerWants;
	private int newTradeOfferCash;
	private Player sellerPlayer;
	private Boolean otherAiDecision;
	
	/**
	 * Constructor: This builds the message
	 * that holds the info for theAiAttemptingTradeMessage
	 * @param aiBuyer (Player): The ai buyer attempting to trade 
	 * @param thePropertyTheBuyerWants (Property): The property the ai player wants
	 * @param newTradeOfferCash (int): the amount of cash the ai player is willing to pay 
	 * @param sellerPlayer (Player): The player who is going to be given the option to sell their property
	 * @param otherAiDecision (Boolean): null if seller is a human, but not null if the seller is ai, its a preloaded ai trade decision
	 */
	public AiAttemptingTradeMessage(AIPlayer aiBuyer, Property thePropertyTheBuyerWants, int newTradeOfferCash, Player sellerPlayer, Boolean otherAiDecision) {
		this.aiBuyer = aiBuyer;
		this.thePropertyTheBuyerWants = thePropertyTheBuyerWants;
		this.newTradeOfferCash = newTradeOfferCash;
		this.sellerPlayer = sellerPlayer;
		this.otherAiDecision = otherAiDecision;
	}
	
	/**
	 * Getter: Returns Ai buyer
	 * @return AIPlayer ^^
	 */
	public AIPlayer getAiBuyer() {
		return aiBuyer;
	}
	
	/**
	 * Getter: Returns the property the buyer wants
	 * @return Property ^^^
	 */
	public Property getDesiredProperty() {
		return thePropertyTheBuyerWants;
	}
	
	/**
	 * Getter: returns the cash offered
	 * @return int ^^^
	 */
	public int getTradeOfferCash() {
		return newTradeOfferCash;
	}
	
	/**
	 * Getter: returns the seller player 
	 * @return Player: ^^^
	 */
	public Player getSellerPlayer() {
		return sellerPlayer;
	}
	
	/**
	 * Getter: if the seller is AI then this 
	 * boolean would be the descision from the 
	 * seller AI on if they want to trade or not
	 * @return Boolean ^^
	 */
	public Boolean getOtherAiDecision() {
		return otherAiDecision;
	}
}
