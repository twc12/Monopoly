package monopoly;

public class GoSpace extends Space {
	
	private int amountEarnedWhenPassingGo = 200;
	
	public GoSpace() {
		super("GoSpace");
	}
	
	public int getAmountEarnedWhenPassingGo() {
		return amountEarnedWhenPassingGo;
	}
	
	public void setAmountEarnedWhenPassingGo(int newAmount) {
		this.amountEarnedWhenPassingGo = newAmount;
	}
}
