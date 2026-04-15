package monopoly;

public class CommunityChest extends Space {

	public CommunityChest() {
		super("CommunityChestSpace");
	}

	@Override
	protected void processSpace(Player player, Model model) {
		Card card = model.getChanceCards().pop();
		model.notifyViewCardDrawn(player, card);
		
	}
}
