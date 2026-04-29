package Monopoly;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import Spaces.Player;
import Spaces.Property;
import Spaces.Railroad;
import Spaces.RealEstate;
import Spaces.Space;
import javafx.application.Platform;


class MonopolyTests {

	//needed to avoid java fx errors when running test suite
	//the javafx Image class is an attr of the Player class, causing the tests to fail
	//ISSUE: our Player class in the model is no longer swappable w/ diff views
	//TODO fix all model files to not have any imports/attributes/etc related to javafx
    @BeforeAll
    static void startJAVAFXinTestSuite() {
        Platform.startup(() -> {}); 
    }
	
	@Test
	void test() {

		
		Controller controller = new Controller();
		Model model = controller.model;
		model.setGameSettingsObj(new GameSettings());

		
		controller.rollDice(controller.getCurrentPlayer());
		controller.rollDice(controller.getCurrentPlayer());
		controller.rollDice(controller.getCurrentPlayer());
		controller.rollDice(controller.getCurrentPlayer());
		controller.rollDice(controller.getCurrentPlayer());
		controller.rollDice(controller.getCurrentPlayer());
		controller.rollDice(controller.getCurrentPlayer());
		controller.rollDice(controller.getCurrentPlayer());
		controller.rollDice(controller.getCurrentPlayer());
		controller.rollDice(controller.getCurrentPlayer());
		assertTrue(true);
	
	}
	
	
	
	@Test
	void testRealEstateBuild() {

		Controller controller = new Controller();
		Model model = controller.model;
		model.setGameSettingsObj(new GameSettings());
		
		//get 2 brown properties
		List<Space> spaces = controller.getSpaces();
		RealEstate mediterranean = (RealEstate) spaces.get(1);
		RealEstate baltic = (RealEstate) spaces.get(3);
		Player player1 = controller.getCurrentPlayer();
		
		//attempt to build after only buying 1 property of set
		controller.purchaseProperty(player1, mediterranean);
		controller.buildHouseHotel(player1, mediterranean);
		assertEquals(0, mediterranean.getBuildingStage());
		
		//cant build on property you don't own
		controller.buildHouseHotel(player1, baltic);
		assertEquals(0, baltic.getBuildingStage());
		
		//attempt to build after acquiring both properties
		controller.purchaseProperty(player1, baltic);
		controller.buildHouseHotel(player1, baltic);
		assertEquals(1, baltic.getBuildingStage());
		assertEquals(0, mediterranean.getBuildingStage());

		//selling when have 1 building
		baltic.autoSellHouseHotel(player1, model);
//		controller.autoSellHouseHotel(player1, baltic);
		assertEquals(0, baltic.getBuildingStage());

		//selling when have 0 buildings
		baltic.autoSellHouseHotel(player1, model);
		assertEquals(0, baltic.getBuildingStage());

		//buying back to 1 building
		controller.buildHouseHotel(player1, baltic);
		assertEquals(1, baltic.getBuildingStage());
		
		//attempt to build with 0 cash
		player1.addCash(-player1.getCashAmmt());
		controller.buildHouseHotel(player1, mediterranean);
		assertEquals(0, mediterranean.getBuildingStage());
		player1.addCash(100000000);

		//second building should fail, need to build evenly
		controller.buildHouseHotel(player1, baltic);
		assertEquals(1, baltic.getBuildingStage());
		
		controller.buildHouseHotel(player1, mediterranean);//even the buildings out
		controller.buildHouseHotel(player1, baltic);
		assertEquals(2, baltic.getBuildingStage()); //now able to build on baltic
		
		//attempting to build past stage 5
		controller.buildHouseHotel(player1, mediterranean);//2
		controller.buildHouseHotel(player1, baltic);//3
		controller.buildHouseHotel(player1, mediterranean);//3
		controller.buildHouseHotel(player1, baltic);//4
		controller.buildHouseHotel(player1, mediterranean);//4
		controller.buildHouseHotel(player1, baltic);//5
		controller.buildHouseHotel(player1, mediterranean);//5
		controller.buildHouseHotel(player1, baltic);//6?
		assertEquals(5, baltic.getBuildingStage());
		
		baltic.autoSellHouseHotel(player1, model);
		assertEquals(4, baltic.getBuildingStage());
		
		//can only sell evenly
//		baltic.autoSellHouseHotel(player1, model);
//		assertEquals(4, baltic.getBuildingStage());

		
	}
	
	@Test
	void testRailroadRentStages() {
	    Controller controller = new Controller();
	    Model model = controller.model;
	    model.setGameSettingsObj(new GameSettings());

	    List<Space> spaces = controller.getSpaces();

	    //get all railroads
	    Railroad reading      = (Railroad) spaces.get(5);
	    Railroad pennsylvania = (Railroad) spaces.get(15);
	    Railroad bo           = (Railroad) spaces.get(25);
	    Railroad shortLine    = (Railroad) spaces.get(35);

	    Player player1 = controller.getAllPlayers().get(0);
	    Player player2 = controller.getAllPlayers().get(1);

	    // 1  railroads should cause player 2 to lose $25
	    controller.purchaseProperty(player1, reading);
	    int p2Cash = player2.getCashAmmt();
	    reading.processSpace(player2, model);
	    assertEquals(p2Cash - 25, player2.getCashAmmt());

	    // 2  railroads should cause player 2 to lose $50
	    controller.purchaseProperty(player1, pennsylvania);
	    p2Cash = player2.getCashAmmt();
	    reading.processSpace(player2, model);
	    assertEquals(p2Cash - 50, player2.getCashAmmt());

	    // 3 railroads should cause player 2 to lose $100
	    controller.purchaseProperty(player1, bo);
	    p2Cash = player2.getCashAmmt();
	    reading.processSpace(player2, model);
	    assertEquals(p2Cash - 100, player2.getCashAmmt());

	    // 4 railroads should cause player 2 to lose $200
	    controller.purchaseProperty(player1, shortLine);
	    p2Cash = player2.getCashAmmt();
	    reading.processSpace(player2, model);
	    assertEquals(p2Cash - 200, player2.getCashAmmt());
	}
	
	@Test
    //player 1 wants to acquire a 2nd brown real estate
	//offering a railroad to p2, 100 cash, and 1 jailfree card

	void testExecuteTrade() {
		
		//setting up game state objects
	    Controller controller = new Controller();
	    Model model = controller.model;
	    model.setGameSettingsObj(new GameSettings());
	    List<Space> spaces = controller.getSpaces();
	    Player player1 = controller.getAllPlayers().get(0);
	    Player player2 = controller.getAllPlayers().get(1);

	    //setting up assets for both players
	    RealEstate mediterranean = (RealEstate) spaces.get(1);  // brown real estate
	    RealEstate baltic = (RealEstate) spaces.get(3);         // Brown
	    Railroad reading = (Railroad) spaces.get(5);            // Railroad
	    controller.purchaseProperty(player1, mediterranean);
	    controller.purchaseProperty(player1, reading);
	    controller.purchaseProperty(player2, baltic);
	    player1.addJailCard();
	    int p1StartCash = player1.getCashAmmt();
	    int p2StartCash = player2.getCashAmmt();
	    int p1StartJailCards = player1.getAmmtOfGOOJCards();
	    int p2StartJailCards = player2.getAmmtOfGOOJCards();
	    
	    
	    //trade offer/execution
	    List<Property> traderOffer = new ArrayList<>();
	    traderOffer.add(reading);
	    controller.executeTrade(
	    	player1,           // trader
	        player2,           // target
	        baltic,            // targetProperty
	        traderOffer,        // traderPropertiesOffer containing railroad
	        100,               // traderCashOffer
	        1                  // traderJailFreeCardsOffer
	    );

	    
	    // after trade executed
	    // ownership changed correctly
	    assertEquals(player2, reading.getOwner());
	    assertEquals(player1, baltic.getOwner());
	    assertTrue(player2.getListOfProperties().contains(reading));
	    assertTrue(player1.getListOfProperties().contains(baltic));
	    assertFalse(player1.getListOfProperties().contains(reading));
	    
	    // cash transferred
	    assertEquals(p1StartCash - 100, player1.getCashAmmt());
	    assertEquals(p2StartCash + 100, player2.getCashAmmt());
	    
	    //verify jailcards transferred
	    assertEquals(p1StartJailCards, player2.getAmmtOfGOOJCards());
	    assertEquals(p2StartJailCards, player1.getAmmtOfGOOJCards());
	    
	    //very rent stages are updated for the realestate set and can build
	    assertTrue(baltic.getIfCanBuild());
	}
	
	
	@Test
	//player forced to sell buildings then properties, able to cover the bankruptcy with property alone
	//then give a player another bankruptcy they can't cover, game over
	void testBankruptcy() {
	    Controller controller = new Controller();
	    Model model = controller.model;
	    model.setGameSettingsObj(new GameSettings());
	    Player player = controller.getCurrentPlayer();
	    List<Space> spaces = controller.getSpaces();
	    RealEstate mediterranean = (RealEstate) spaces.get(1);
	    RealEstate baltic = (RealEstate) spaces.get(3);

	    
	    // SETUP, player gets 2 properties/buildings
	    controller.purchaseProperty(player, mediterranean); //will sell for $30  (half of 
	    controller.purchaseProperty(player, baltic);		//will sell for $30
	    controller.buildHouseHotel(player, mediterranean);  //house will sell for $25
	    controller.buildHouseHotel(player, baltic);         //house will sell for $25
	    													//total 110
	    													//player will owe 1$ less than property sales value and be able to cover it
	    int debt = 109;
 	    player.addCash(-player.getCashAmmt()); // make player's cash = 0
	    
	    
	    //force liquidate the properties, skipping the regular player.addCash route
	    player.addCash(-debt);
	    assertFalse(player.getGameOver());
	    assertEquals(0, player.getListOfProperties().size());
	    assertEquals(1, player.getCashAmmt());	//player will have $1 after liquidating
	    assertEquals(0, player.getHousesOwnedCount());
	    
	    //initiate another bankruptcy when player only has $1
	    player.addCash(-5);
	    assertTrue(player.getGameOver());
	    assertEquals(-4, player.getCashAmmt());

	}

	
	
	

}
