package monopoly;

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

	public int getAmountOfCostToChargePlayer(Player player) {
		
		if (taxSpaceType.equals(TaxSpaceType.LUXURY)) {
			return luxuryTaxFlatAmount;
		}
		
		else {
			
		int player10Percent = (int) (player.getCashAmmt()/10);
		if (player10Percent < incomeTaxFlatAmount) return player10Percent;	
		
		return incomeTaxFlatAmount;

		}
		
	}
}
