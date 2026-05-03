/**
 * This file contains the CardBuilder object, which creates each
 * effect of every card, that can directly effect all players or just 
 * the player of the drawn card. it creates two lists of these built cards,
 * one for community chest and the other for chance cards.
 *
 * @author Tyler Carpenter
 */
package Cards;
import java.io.Serializable;
import java.util.*;
import javafx.scene.image.*;
import Spaces.Player;


public class CardBuilder implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	String theme;
	ArrayList<String> cardDescription = new ArrayList<>();
	ArrayList<String> imageFilesChance = new ArrayList<>(); // Chance fileNames
	ArrayList<String> imageFilesChest = new ArrayList<>(); // Chest fileNames
	ArrayList<Card> chanceCards;
	ArrayList<Card> communityChestCards;
	
	/**
	 * Constructor for CardBuilder
	 * Assigns ArrayLists and then calls build cards
	 * to add effects to cards and add them to lists.
	 */
	public CardBuilder(String theme) {
		this.theme = theme;
		chanceCards = new ArrayList<>();
		communityChestCards = new ArrayList<>();
		buildDescription();
		buildCards();
	}
	
	
	/**
	 * This function is used to build the description for the games chance
	 * and community chest cards. its also used to decide 
	 * what image files are pulled for the cards.
	 */
	private void buildDescription(){
		
		// Standard Theme
		if (theme.equals("standardTheme")) {
			// Chance
			cardDescription.add("Bank Pays You $50");
			cardDescription.add("Your Building Loan Matures, Collect $150");
			cardDescription.add("Speeding Fine, Pay $15");
			cardDescription.add("Pay Poor Tax Of $15");
			cardDescription.add("Go Directly\n To Jail\n Do Not Pass Go\nDo Not Collect $200");
			cardDescription.add("Advance To Go");
			cardDescription.add("Advance To Railroad");
			cardDescription.add("Advance To Utility");
			cardDescription.add("Advance To Illinois Ave.");
			cardDescription.add("Take A Walk On The\nBoard Walk\nAdvance To Board Walk");
			cardDescription.add("Advance To St. Charles Place");
			cardDescription.add("Take A Ride On The Reading");
			cardDescription.add("You Have Been Elected\nChairman Of The Board\nPay Each Player $50");
			cardDescription.add("Get Out Of Jail Free!\nThis Card Made Be Kept");
			cardDescription.add("Go Forward 3 Spaces");
			cardDescription.add("Make General Repairs on\nall your properties\nFor each house pay $25\nFor each hotel pay $100");
			
			// Community Chest
			cardDescription.add("Life Insurance Matures\nCollect $100");
			cardDescription.add("Bank Error In Your Favor\nCollect $200");
			cardDescription.add("From Sale Of Stock, You get $45");
			cardDescription.add("You Have Won\nSecond Prize\nIn A Beauty Contest\n Collect $10");
			cardDescription.add("Get Out Of Jail\nFree!");
			cardDescription.add("You Innherit $100");
			cardDescription.add("Doctor's Fee\nPay $50");
			cardDescription.add("Advance To Go");
			cardDescription.add("Xmas Fund Matures\nCollect $100");
			cardDescription.add("Recieve For Services $25");
			cardDescription.add("Go Directly\nTo Jail Do Not Pass Go\nDo Not Collect $200");
			cardDescription.add("Pay School Tax\nOf $150");
			cardDescription.add("Income Tax Refund\nCollect $20");
			cardDescription.add("You Are Assessed For\nStreet Repairs\n Pay $40 For Each House\n");
			cardDescription.add("Grand Opera Opening\nCollect $50 from\nEvery Player");
			cardDescription.add("Pay\nHospital\n$100");			
			
			for(int i = 0; i < 16; i++) {
				imageFilesChance.add("chanceCard" + String.valueOf(i) + ".png");
				imageFilesChest.add("chestCard" + String.valueOf(i) + ".png");
				
			}
		}
		if(theme.equals("pirateTheme")) {
			// Chance cards
			cardDescription.add("Ship's Bank Pays Ye $50");
			cardDescription.add("Yer Ship Loan Matures, Collect $150");
			cardDescription.add("Sailin' Too Fast, Pay $15");
			cardDescription.add("Pay Poor Pirate Tax Of $15");
			cardDescription.add("Go Directly\nTo Brig\nDo Not Pass Go\nDo Not Collect $200");
			cardDescription.add("Advance To Go");
			cardDescription.add("Advance To Ship");
			cardDescription.add("Advance To Utility");
			cardDescription.add("Advance To Isla Cruces");
			cardDescription.add("Take A Stroll On The\nPlank Walk\nAdvance To Plank Walk");
			cardDescription.add("Advance To St. Charles Port");
			cardDescription.add("Take A Ride On The Black Pearl");
			cardDescription.add("Ye Have Been Elected\nCaptain Of The Crew\nPay Each Player $50");
			cardDescription.add("Get Out Of Brig Free!\nThis Card May Be Kept");
			cardDescription.add("Sail Forward 3 Spaces");
			cardDescription.add("Make General Repairs on\nall yer properties\nFor each hut pay $25\nFor each inn pay $100");

			// Community Chest
			cardDescription.add("Pirate Insurance Matures\nCollect $100");
			cardDescription.add("Bank Error In Yer Favor\nCollect $200");
			cardDescription.add("From Sale Of Treasure\nYe get $45");
			cardDescription.add("Ye Have Won\nSecond Prize\nIn A Pirate Contest\n Collect $10");
			cardDescription.add("Get Out Of Brig\nFree!");
			cardDescription.add("Ye Inherit $100");
			cardDescription.add("Ship Doctor's Fee\nPay $50");
			cardDescription.add("Advance To Go");
			cardDescription.add("Treasure Fund Matures\nCollect $100");
			cardDescription.add("Receive For Services $25");
			cardDescription.add("Go Directly\nTo Brig Do Not Pass Go\nDo Not Collect $200");
			cardDescription.add("Pay Dock Tax\nOf $150");
			cardDescription.add("Booty Tax Refund\nCollect $20");
			cardDescription.add("Ye Are Assessed For\nDock Repairs\nPay $40 For Each Dock\n");
			cardDescription.add("Grand Tavern Opening\nCollect $50 from\nEvery Player");
			cardDescription.add("Pay\nShip Healer\n$100");
		
			for(int i = 0; i < 16; i++) {
				imageFilesChance.add("chanceCard0.png");
				imageFilesChest.add("chestCard0.png");
				
			}
		}
		if(theme.equals("tucsonTheme")) {
			// Chance cards
			cardDescription.add("Bank Pays You $50");
			cardDescription.add("Yer Ranch Loan Matures, Collect $150");
			cardDescription.add("Speedin' Through Town\nPay $15");
			cardDescription.add("Pay Poor Tax Of $15");
			cardDescription.add("Go Directly\n To Jail\nDo Not Pass Go\nDo Not Collect $200");
			cardDescription.add("Advance To Go");
			cardDescription.add("Advance To Railroad");
			cardDescription.add("Advance To Utility");
			cardDescription.add("Advance To Illinois Territory");
			cardDescription.add("Take A Walk Down The\nBoardwalk\nAdvance To Boardwalk");
			cardDescription.add("Advance To St. Charles Place");
			cardDescription.add("Take A Ride On The Reading Rail");
			cardDescription.add("You Have Been Elected\nSheriff Of The Town\nPay Each Player $50");
			cardDescription.add("Get Out Of Jail Free!\nThis Card May Be Kept");
			cardDescription.add("Move Forward 3 Spaces");
			cardDescription.add("Make General Repairs on\nall yer properties\nFor each house pay $25\nFor each hotel pay $100");

			// Community Chest
			cardDescription.add("Life Insurance Matures\nCollect $100");
			cardDescription.add("Bank Error In Yer Favor\nCollect $200");
			cardDescription.add("From Sale Of Cattle\nYou get $45");
			cardDescription.add("You Have Won\nSecond Prize\nIn A Rodeo Contest\n Collect $10");
			cardDescription.add("Get Out Of Jail\nFree!");
			cardDescription.add("You Inherit $100");
			cardDescription.add("Doctor's Fee\nPay $50");
			cardDescription.add("Advance To Go");
			cardDescription.add("Xmas Fund Matures\nCollect $100");
			cardDescription.add("Receive For Work Done $25");
			cardDescription.add("Go Directly\nTo Jail Do Not Pass Go\nDo Not Collect $200");
			cardDescription.add("Pay School Tax\nOf $150");
			cardDescription.add("Income Tax Refund\nCollect $20");
			cardDescription.add("You Are Assessed For\nTown Repairs\n Pay $40 For Each House\n");
			cardDescription.add("Grand Saloon Opening\nCollect $50 from\nEvery Player");
			cardDescription.add("Pay\nHospital\n$100");
		
			for(int i = 0; i < 16; i++) {
				imageFilesChance.add("chanceCard0.png");
				imageFilesChest.add("chestCard0.png");
				
			}
		}
	}
	
	
	/**
	 * @return The chance card list
	 */
	public ArrayList<Card> getChanceCards(){
		return chanceCards;
	}
	
	/**
	 * @return The community chest cards
	 */
	public ArrayList<Card> getCommunityChestCards(){
		return communityChestCards;
	}
	
	/**
	 * Creates all the card descriptions and effects, then adds them to the 
	 * specified ArrayList they belong to.
	 */
	public void buildCards() {
		
		// Chance Card
		
		
		
		// #1
		CardEffect r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(50);
		};
		chanceCards.add(new Card(cardDescription.get(0),imageFilesChance.get(0) , r));
		
		// #2
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(150);
		};
		chanceCards.add(new Card(cardDescription.get(1),imageFilesChance.get(1) , r));
		
		// #3
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(-15);
			if (model.getGameSettings().getFreeParkingRule()) {
				model.addToFreeParkingFunds(15);
			}
		};
		chanceCards.add(new Card(cardDescription.get(2),imageFilesChance.get(2) , r));
		
		// #4
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(-15);
		};
		chanceCards.add(new Card(cardDescription.get(3),imageFilesChance.get(3) , r));
		
		// #5
		r = (CardEffect & Serializable)(player, model) -> {
			player.putInJail();
		};
		chanceCards.add(new Card(cardDescription.get(4),imageFilesChance.get(4), r));
		
		// #6
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToGo();
		};
		chanceCards.add(new Card(cardDescription.get(5),imageFilesChance.get(5) , r));
		
		// #7
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToRailroad();
		};
		chanceCards.add(new Card(cardDescription.get(6),imageFilesChance.get(6) , r));
		
		
		// #8
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToUtility();
		};
		chanceCards.add(new Card(cardDescription.get(7),imageFilesChance.get(7) , r));
		
		// #9
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToProperty("Illinois Avenue");
		};
		chanceCards.add(new Card(cardDescription.get(8),imageFilesChance.get(8) , r));
		
		// #10
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToProperty("Board Walk");
		};
		chanceCards.add(new Card(cardDescription.get(9),imageFilesChance.get(9) , r));
		
		// #11
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToProperty("St. Charles Place");
		};
		chanceCards.add(new Card(cardDescription.get(10),imageFilesChance.get(10) , r));
		
		// #12
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToProperty("Reading Railroad");
		};
		chanceCards.add(new Card(cardDescription.get(11),imageFilesChance.get(11) , r));
		
		// #13
		r = (CardEffect & Serializable)(player, model) -> {
			for(Player opponent : model.getPlayers()) {
				if(!player.equals(opponent)) {
					opponent.addCash(50);
					player.addCash(-50);
				}
			};
		};
		chanceCards.add(new Card(cardDescription.get(12),imageFilesChance.get(12) , r));
		
		// #14
		r = (CardEffect & Serializable)(player, model) -> {
			player.addJailCard();
		};
		chanceCards.add(new Card(cardDescription.get(13),imageFilesChance.get(13) , r));
		
		// #15 TODO need change to move back 3, placeholder for now
		r = (CardEffect & Serializable)(player, model) -> {
			player.move(3);
		};
		chanceCards.add(new Card(cardDescription.get(14),imageFilesChance.get(14) , r));
		
		// #16
		r = (CardEffect & Serializable)(player, model) -> {
			for(int i = 0; i < player.getHousesOwnedCount(); i++) 
				player.addCash(-25);
			for(int i = 0; i < player.getHotelsOwnedCount(); i++) 
				player.addCash(-100);
		};
		chanceCards.add(new Card(cardDescription.get(15),imageFilesChance.get(15) , r));
		
		//Community chest cards
		
		// #1
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(100);
		};
		communityChestCards.add(new Card(cardDescription.get(16),imageFilesChest.get(0) , r));
		
		// #2
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(200);
		};
		communityChestCards.add(new Card(cardDescription.get(17),imageFilesChest.get(1) , r));
		
		// #3
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(45);
		};
		communityChestCards.add(new Card(cardDescription.get(18),imageFilesChest.get(2) , r));
		
		// #4
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(10);
		};
		communityChestCards.add(new Card(cardDescription.get(19),imageFilesChest.get(3) , r));
		
		// #5
		r = (CardEffect & Serializable)(player, model) -> {
			player.addJailCard();
		};
		communityChestCards.add(new Card(cardDescription.get(20),imageFilesChest.get(4) , r));
		
		// #6
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(100);
		};
		communityChestCards.add(new Card(cardDescription.get(21),imageFilesChest.get(5) , r));
		
		// #7
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(50);
		};
		communityChestCards.add(new Card(cardDescription.get(22),imageFilesChest.get(6), r));
		
		// #8
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToGo();
		};
		communityChestCards.add(new Card(cardDescription.get(23),imageFilesChest.get(7), r));
		
		// #9
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(100);
		};
		communityChestCards.add(new Card(cardDescription.get(24),imageFilesChest.get(8) , r));
		
		// #10
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(25);
		};
		communityChestCards.add(new Card(cardDescription.get(25),imageFilesChest.get(9) , r));
		
		// #11
		r = (CardEffect & Serializable)(player, model) -> {
			player.putInJail();
		};
		communityChestCards.add(new Card(cardDescription.get(26),imageFilesChest.get(10) , r));
		
		// #12
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(150);
		};
		communityChestCards.add(new Card(cardDescription.get(27),imageFilesChest.get(11) , r));
		
		// #13
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(20);
		};
		communityChestCards.add(new Card(cardDescription.get(28),imageFilesChest.get(12), r));
		

		// #14
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(-50);
		};
		communityChestCards.add(new Card(cardDescription.get(29),imageFilesChest.get(13), r));
		
		// #15
		r = (CardEffect & Serializable)(player, model) -> {
			for(Player opponent : model.getPlayers()) {
				if(!player.equals(opponent)) {
					opponent.addCash(-50);
					player.addCash(50);
				}
			}
		};
		communityChestCards.add(new Card(cardDescription.get(30),imageFilesChest.get(14) ,r));
		
		// #16
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(50);
		};
		communityChestCards.add(new Card(cardDescription.get(31),imageFilesChest.get(15), r));
	
	}
}
