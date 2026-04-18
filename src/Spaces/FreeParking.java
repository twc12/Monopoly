package Spaces;

import Monopoly.Model;

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

	@Override
	protected void processSpace(Player player, Model model) {
		// TODO Auto-generated method stub
		
	}
	
}
