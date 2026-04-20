/**
 * This file contains the CardBuilder object, which creates each
 * effect of every card, that can directly effect all players or just 
 * the player of the drawn card. it creates two lists of these built cards,
 * one for community chest and the other for chance cards.
 *
 * @author Tyler Carpenter
 */
package Cards;
import java.util.*;

import Spaces.Player;

public class CardBuilder {
	ArrayList<Card> chanceCards;
	ArrayList<Card> communityChestCards;
	
	/**
	 * Constructor for CardBuilder
	 * 
	 * Assigns ArrayLists and then calls build cards
	 * to add effects to cards and add them to lists.
	 */
	public CardBuilder() {
		chanceCards = new ArrayList<>();
		communityChestCards = new ArrayList<>();
		buildCards();
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
		chanceCards.add(new Card("Bank Pays You $50", (player,model)->{
			player.addCash(50);
		}));
		
		// #2
		chanceCards.add(new Card("Your Building Loan Matures, Collect $150", (player,model)->{
			player.addCash(150);
		}));
		
		// #3
		chanceCards.add(new Card("Speeding Fine, Pay $15", (player,model)->{
			player.addCash(-15);
		}));
		
		// #4
		chanceCards.add(new Card("Pay Poor Tax Of $15", (player,model)->{
			player.addCash(-15);
		}));
		
		// #5
		chanceCards.add(new Card("Go Directly\n To Jail\n Do Not Pass Go, Do Not Collect $200", (player,model)->{
			player.putInJail();
		}));
		
		
//		TODO advance to cards
		chanceCards.add(new Card("Advance To Railroad", (player,model)->{
			player.advanceToRailroad();
		}));
		
		chanceCards.add(new Card("Advance To Utility", (player,model)->{
			player.advanceToUtility();
		}));
		
		
		
//		TODO when access to players from model implement Player Interaction Card
//		chanceCards.add(new Card("", (player,model)->{
//			;
//		}));
		
//		TODO Add Poperty-Based card once properties are implemented
//		chanceCards.add(new Card("Make general repairs on all your properties\n
//		For each house pay $25, For each hotel pay $100", (player,model)->{
//		;
//		}));
		
		//Community chest cards
		
		// #1
		communityChestCards.add(new Card("Life Insurance Matures, Collect $100", (player,model)->{
			player.addCash(100);
		}));
		
		// #2
		communityChestCards.add(new Card("Bank Error In Your Favor, Collect $200", (player,model)->{
			player.addCash(200);
		}));
		
		// #3
		communityChestCards.add(new Card("From Sale Of Stock, You get $45", (player,model)->{
			player.addCash(45);
		}));
		
		// #4
		communityChestCards.add(new Card("You Have Won\nSecond Prize\nIn A Beauty Contest\n Collect $10", (player,model)->{
			player.addCash(10);
		}));
		
		// #5
		communityChestCards.add(new Card("Get Out Of Jail, Free!", (player,model)->{
			player.addJailCard();
		}));
		
		// #6
		communityChestCards.add(new Card("You Innherit $100", (player,model)->{
			player.addCash(100);
		}));
		
		// #7
		communityChestCards.add(new Card("Doctor's Fee\nPay $50", (player,model)->{
			player.addCash(50);
		}));
		
		// #8
		communityChestCards.add(new Card("Advance To Go", (player,model)->{
			player.advanceToGo();
		}));
		
		// #9
		communityChestCards.add(new Card("Xmas Fund Matures\nCollect $100", (player,model)->{
			player.addCash(100);
		}));
		
		// #10
		communityChestCards.add(new Card("Recieve For Services $25", (player,model)->{
			player.addCash(25);
		}));
		
		// #11
		communityChestCards.add(new Card("Go Directly\n To Jail\n Do Not Pass Go, Do Not Collect $200", (player,model)->{
			player.putInJail();
		}));
		
		// #12
		communityChestCards.add(new Card("Pay School Tax\nOf $150", (player,model)->{
			player.addCash(150);
		}));
		
		// #13
		communityChestCards.add(new Card("Income Tax Refund\nCollect $20", (player,model)->{
			player.addCash(20);
		}));
		
//		TODO street repairs, need house implementation
//		communityChestCards.add(new Card("You Are Assessed For\nStreet Repairs\n", (player,model)->{
//			;
//		}));
		
		communityChestCards.add(new Card("Grand Opera Opening\nCollect $50 from\nEvery Player", (player,model)->{
			for(Player opponent : model.getPlayers()) {
				if(!player.equals(opponent)) {
					opponent.addCash(-50);
					player.addCash(50);
				}
			}
		}));
	
	}
}
