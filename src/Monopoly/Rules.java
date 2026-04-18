package Monopoly;

public class Rules {

    // This whole file is a placeholder until Jake makes the ruleset configuration
    // has been implemented
    
    private boolean freeParking = false;
    private int freeParkingAmount = 0;

    public boolean getFreeParkingRule() { return freeParking; }
    public int getFreeParkingRewardAmount() { return freeParkingAmount; }

    public void setFreeParkingRule() {
        freeParking = true;
    }

    public void addToReward(int reward) {
        freeParkingAmount += reward;
    }
}
