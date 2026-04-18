package Spaces;

import Monopoly.Model;
import Monopoly.Rules;

public class FreeParking extends Space {

	private int currFreeParkingReward;
	
	public FreeParking() {
		super("FreeParkingSpace");
		currFreeParkingReward = 0;
	}
	
	public void addCashToFreeParkingReward(int cashAmount) {
		currFreeParkingReward += cashAmount;
	}
	
	public int getFreeParkingRewardAmount() {
		return currFreeParkingReward;
	}

	/**
	 * Adds whatever amount is in the free parking reward if that rule has been
	 * set. The check for adding money to the pool is in the TaxSpace. Once the
	 * reward is given out, the reward pot is reset to 0
	 */
	@Override
	protected void processSpace(Player player, Model model) {
		if (model.getRuleSet().getFreeParkingRule()) {
			player.addCash(currFreeParkingReward);
			currFreeParkingReward = 0;
		}
		return;
	}
	
}
