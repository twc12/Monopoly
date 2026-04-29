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
	ArrayList<Card> chanceCards;
	ArrayList<Card> communityChestCards;
	
	/**
	 * Constructor for CardBuilder
	 * 
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
	
	
	
	private void buildDescription(){
		
		// Standard Theme
		if (theme.equals("standard")) {
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
		chanceCards.add(new Card(cardDescription.get(0),"chanceCard0.png" , r));
		
		// #2
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(150);
		};
		chanceCards.add(new Card(cardDescription.get(1),"chanceCard1.png" , r));
		
		// #3
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(-15);
			if (model.getGameSettings().getFreeParkingRule()) {
				model.addToFreeParkingFunds(15);
			}
		};
		chanceCards.add(new Card(cardDescription.get(2),"chanceCard2.png" , r));
		
		// #4
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(-15);
		};
		chanceCards.add(new Card(cardDescription.get(3),"chanceCard3.png" , r));
		
		// #5
		r = (CardEffect & Serializable)(player, model) -> {
			player.putInJail();
		};
		chanceCards.add(new Card(cardDescription.get(4),"chanceCard4.png" , r));
		
		// #6
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToGo();
		};
		chanceCards.add(new Card(cardDescription.get(5),"chanceCard5.png" , r));
		
		// #7
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToRailroad();
		};
		chanceCards.add(new Card(cardDescription.get(6),"chanceCard6.png" , r));
		
		
		// #8
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToUtility();
		};
		chanceCards.add(new Card(cardDescription.get(7),"chanceCard7.png" , r));
		
		// #9
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToProperty("Illinois Avenue");
		};
		chanceCards.add(new Card(cardDescription.get(8),"chanceCard8.png" , r));
		
		// #10
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToProperty("Board Walk");
		};
		chanceCards.add(new Card(cardDescription.get(9),"chanceCard9.png" , r));
		
		// #11
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToProperty("St. Charles Place");
		};
		chanceCards.add(new Card(cardDescription.get(10),"chanceCard10.png" , r));
		
		// #12
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToProperty("Reading Railroad");
		};
		chanceCards.add(new Card(cardDescription.get(11),"chanceCard11.png" , r));
		
		// #13
		r = (CardEffect & Serializable)(player, model) -> {
			for(Player opponent : model.getPlayers()) {
				if(!player.equals(opponent)) {
					opponent.addCash(50);
					player.addCash(-50);
				}
			};
		};
		chanceCards.add(new Card(cardDescription.get(12),"chanceCard3.png" , r));
		
		// #14
		r = (CardEffect & Serializable)(player, model) -> {
			player.addJailCard();
		};
		chanceCards.add(new Card(cardDescription.get(13),"chanceCard13.png" , r));
		
		// #15 TODO need change to move back 3, placeholder for now
		r = (CardEffect & Serializable)(player, model) -> {
			player.move(3);
		};
		chanceCards.add(new Card(cardDescription.get(14),"chanceCard14.png" , r));
		
		// #16
		r = (CardEffect & Serializable)(player, model) -> {
			for(int i = 0; i < player.getHousesOwnedCount(); i++) 
				player.addCash(-25);
			for(int i = 0; i < player.getHotelsOwnedCount(); i++) 
				player.addCash(-100);
		};
		chanceCards.add(new Card(cardDescription.get(15),"chanceCard15.png" , r));
		
		//Community chest cards
		
		// #1
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(100);
		};
		communityChestCards.add(new Card(cardDescription.get(16),"chestCard0.png" , r));
		
		// #2
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(200);
		};
		communityChestCards.add(new Card(cardDescription.get(17),"chestCard1.png" , r));
		
		// #3
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(45);
		};
		communityChestCards.add(new Card(cardDescription.get(18),"chestCard2.png" , r));
		
		// #4
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(10);
		};
		communityChestCards.add(new Card(cardDescription.get(19),"chestCard3.png" , r));
		
		// #5
		r = (CardEffect & Serializable)(player, model) -> {
			player.addJailCard();
		};
		communityChestCards.add(new Card(cardDescription.get(20),"chestCard4.png" , r));
		
		// #6
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(100);
		};
		communityChestCards.add(new Card(cardDescription.get(21),"chestCard5.png" , r));
		
		// #7
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(50);
		};
		communityChestCards.add(new Card(cardDescription.get(22),"chestCard6.png" , r));
		
		// #8
		r = (CardEffect & Serializable)(player, model) -> {
			player.advanceToGo();
		};
		communityChestCards.add(new Card(cardDescription.get(23),"chestCard7.png" , r));
		
		// #9
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(100);
		};
		communityChestCards.add(new Card(cardDescription.get(24),"chestCard8.png" , r));
		
		// #10
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(25);
		};
		communityChestCards.add(new Card(cardDescription.get(25),"chestCard9.png" , r));
		
		// #11
		r = (CardEffect & Serializable)(player, model) -> {
			player.putInJail();
		};
		communityChestCards.add(new Card(cardDescription.get(26),"chestCard10.png" , r));
		
		// #12
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(150);
		};
		communityChestCards.add(new Card(cardDescription.get(27),"chestCard11.png" , r));
		
		// #13
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(20);
		};
		communityChestCards.add(new Card(cardDescription.get(28),"chestCard12.png", r));
		

		// #14
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(-50);
		};
		communityChestCards.add(new Card(cardDescription.get(29),"chestCard13.png", r));
		
		// #15
		r = (CardEffect & Serializable)(player, model) -> {
			for(Player opponent : model.getPlayers()) {
				if(!player.equals(opponent)) {
					opponent.addCash(-50);
					player.addCash(50);
				}
			}
		};
		communityChestCards.add(new Card(cardDescription.get(30),"chestCard14.png" ,r));
		
		// #16
		r = (CardEffect & Serializable)(player, model) -> {
			player.addCash(50);
		};
		communityChestCards.add(new Card(cardDescription.get(31),"chestCard15.png", r));
	
	}
}
