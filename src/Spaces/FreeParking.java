package Spaces;

import Monopoly.Model;
import Monopoly.GameSettings;

/**
 * Represents a FreeParking space, it's functionality 
 */
public class FreeParking extends Space {

	private int currFreeParkingReward;
	
	public FreeParking(String imageFile) {
		super("FreeParkingSpace");
		currFreeParkingReward = 0;
		this.imageFile = imageFile;
	}
	
	/**
	 * SETTER: Adds cash to the freeparking reward when the associated game
	 * setting is enabled
	 * @param cashAmount the money to be added to the reward
	 */
	public void addCashToFreeParkingReward(int cashAmount) {
		currFreeParkingReward += cashAmount;
	}
	
	/**
	 * GETTTER: Pulls the current reward for free parking from the associated
	 * field in this class
	 * @return an integer representing the current free parking reward
	 */
	public int getFreeParkingRewardAmount() {
		return currFreeParkingReward;
	}

	/**
	 * Adds whatever amount is in the free parking reward if that rule has been
	 * set. The check for adding money to the pool is in the TaxSpace. Once the
	 * reward is given out, the reward pot is reset to 0
	 */
	@Override
	public void processSpace(Player player, Model model) {
		// Checks if the rule to collect tax money from free parking is enabled
		if (model.getGameSettings().getFreeParkingRule()) {
			player.addCash(currFreeParkingReward);
			model.notifyViewOfInfoMessage("Free parking giving player $"+currFreeParkingReward);
			currFreeParkingReward = 0;
		}
		return;
	}	
}
