package monopoly;

public class RealEstate extends Property {
	public RealEstate(Space.Color color, String name, int purchaseAmt) {
		super(name, purchaseAmt);
		this.color = color;
		
	}

	public int getCostToCharge(Player player) {
		// TODO Auto-generated method stub
		return 0;
	}

}
