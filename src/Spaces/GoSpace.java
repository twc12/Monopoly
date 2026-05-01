package Spaces;

import Monopoly.Model;

public class GoSpace extends Space {
	
	private int amountEarnedWhenPassingGo = 200;
	
	public GoSpace(String imageFile) {
		super("GO");
		this.imageFile = imageFile;
	}
	
	/**
	 * Gets the amount a player is meant to earn passing go, whatever it is, the 
	 * standard $200 or a custom amount
	 * @return an integer, the amount the player gets
	 */
	public int getAmountEarnedWhenPassingGo() {
		return amountEarnedWhenPassingGo;
	}
	
	/**
	 * Sets the amount of money a player earns when passing go
	 * @param newAmount the new amount of money they will get for passing go
	 */
	public void setAmountEarnedWhenPassingGo(int newAmount) {
		this.amountEarnedWhenPassingGo = newAmount;
	}

	/**
	 * Adds how much money the player is set to get when passing go
	 */
	@Override
	protected void processSpace(Player player, Model model) {
		player.addCash(getAmountEarnedWhenPassingGo());
	}
}
