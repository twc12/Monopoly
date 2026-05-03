package Spaces;

import Monopoly.Model;

/**
 * A subclass of CostSpace. Can't have an owner. This class has two "modes", one for income
 * tax and one for luxury. That mode is set when constructing, passing in a TaxSpaceType enum.
 * @author: Jake
 */
public class TaxSpace extends CostSpace{
	
	//flat amounts charged based on type
	private int incomeTaxFlatAmount = 200;
	private int luxuryTaxFlatAmount = 75;
	public TaxSpaceType taxSpaceType;		//income vs luxury
	public enum TaxSpaceType{
		INCOME,
		LUXURY
		};
	
	/**
	 * Constructor. Pass in the type of taxspace it is, income or luxury.
	 * @param type - TaxSpaceType.INCOME or TaxSpaceType.LUXURY will determine which type it is
	 */
	public TaxSpace(TaxSpaceType type) {
		super("Income Tax");
		this.taxSpaceType = type;
		
		//workaround with superconstructor
		if (this.taxSpaceType == TaxSpaceType.LUXURY) {
			this.name = "Luxury Tax";
		}
	}

	/**
	 * Calculates the amount to charge the player who landed on it for this tax space.
	 * @return int - the amount to charge the player
	 */
	public int getCostToCharge(Player player, int diceRoll) {
		//LUXURY
		if (taxSpaceType.equals(TaxSpaceType.LUXURY)) {
			// Charges flat $200 for luxury tax
			return luxuryTaxFlatAmount;
		}	
		//INCOME
		else {
			int player10Percent = (int) (player.getCashAmmt()/10);	// Gets value for 10% tax
			if (player10Percent < incomeTaxFlatAmount) {			// If 10% of players cash (ex: $150) is smaller than flat $200, charge $150
				return player10Percent;	
			}	
			return incomeTaxFlatAmount;								// Applies $200 flat tax
		}
		
	}

	/**
	 * When a player lands on it, will charge the player. If free parking rule is enabled,
	 * will add that tax money to the free parking cash pool.
	 */
	@Override
	public void processSpace(Player player, Model model) {
		//charge tax to player
		int originalCash = player.getCashAmmt();
		int costToCharge = getCostToCharge(player, 0);
		player.addCash(-costToCharge);	

		//check free parking rule
		if (model.getGameSettings().getFreeParkingRule()) {
			FreeParking parking = model.board.getFreeParking();
			parking.addCashToFreeParkingReward(costToCharge);
		}

		//view notifications
		if (taxSpaceType == TaxSpaceType.INCOME) {
			model.notifyViewOfInfoMessage(player.toString() + " charged income tax: $" + costToCharge + " (Lesser of $200 or 10% of cash ($" + originalCash + ")");
		} else {
			model.notifyViewOfInfoMessage(player.toString() +" charged luxury tax: $" + costToCharge);
		}
	}

}
