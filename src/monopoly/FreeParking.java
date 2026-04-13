package monopoly;


public class FreeParking extends Space {

	private int freeParkingReward;
	
	public void addCashToFreeParkingReward(int cashAmount) {
		freeParkingReward += cashAmount;
	}
	
	public int getFreeParkingRewardAmount() {
		return freeParkingReward;
	}
	
}
