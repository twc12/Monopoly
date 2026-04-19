package Spaces;

import Monopoly.Model;
import Monopoly.Rules;

public class GoSpace extends Space {
	
	private int amountEarnedWhenPassingGo = 200;
	
	public GoSpace() {
		super("GO");
	}
	
	public int getAmountEarnedWhenPassingGo() {
		return amountEarnedWhenPassingGo;
	}
	
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
