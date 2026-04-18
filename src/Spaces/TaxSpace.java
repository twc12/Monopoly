package Spaces;

import Monopoly.Model;

public class TaxSpace extends CostSpace{
	
	private int incomeTaxFlatAmount = 200;
	private int luxuryTaxFlatAmount = 75;

	public TaxSpaceType taxSpaceType;
	public enum TaxSpaceType{
		INCOME,
		LUXURY
		};
	
	
	public TaxSpace(TaxSpaceType type) {
		super("TaxSpace");
		this.taxSpaceType = type;
	}

	public int getCostToCharge(Player player, int diceRoll) {
		
		if (taxSpaceType.equals(TaxSpaceType.LUXURY)) {
			return luxuryTaxFlatAmount;
		}
		
		else {
			
		int player10Percent = (int) (player.getCashAmmt()/10);
		if (player10Percent < incomeTaxFlatAmount) return player10Percent;	
		
		return incomeTaxFlatAmount;

		}
		
	}

	protected void processSpace(Player player, Model model) {
		int costToCharge = getCostToCharge(player, 0);
		player.addCash(-costToCharge);	

		// If the rule for taxes to go to free parking is enabled, it will add
		// the deducted amount from the player to the reward pool
		// Currently this is very janky, feel free to change as needed
		if (model.getRuleSet().getFreeParkingRule()) {
			model.getRuleSet().addToReward(costToCharge);
		}

		System.out.println(player.toString() + " taxed $" + getCostToCharge(player, 0));
	}

}
