package Spaces;

import Monopoly.Model;

/**
 *
 */
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

	/**
	 * 
	 */
	public int getCostToCharge(Player player, int diceRoll) {
		// Check for if it's a Luxury tax space
		if (taxSpaceType.equals(TaxSpaceType.LUXURY)) {
			// Charges flat $200 for luxury tax
			return luxuryTaxFlatAmount;
		}	
		// If it's an income tax
		else {
			// Gets value for 10% tax
			int player10Percent = (int) (player.getCashAmmt()/10);	
			// Applies 10% tax to be charged
			if (player10Percent < incomeTaxFlatAmount) {
				return player10Percent;	
			}	
			// Applies $75 tax
			return incomeTaxFlatAmount;
		}
		
	}

	/**
	 * Checks if the freeparking rule is enabled and will add the taken tax money
	 * and add it to hte freeparking pool based on that
	 */
	@Override
	protected void processSpace(Player player, Model model) {

		int costToCharge = getCostToCharge(player, 0);
		player.addCash(-costToCharge);	

		if (model.getRuleSet().getFreeParkingRule()) {
			FreeParking parking = model.board.getFreeParking();
			parking.addCashToFreeParkingReward(costToCharge);
		}

		System.out.println(player.toString() + " taxed $" + getCostToCharge(player, 0));
	}

}
