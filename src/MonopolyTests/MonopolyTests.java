package MonopolyTests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import Messages.AiLogsEnabledMessage;
import Messages.BankruptcyMessage;
import Messages.PurchasePromptMessage;
import Monopoly.Controller;
import Monopoly.GameSettings;
import Monopoly.Model;
import Monopoly.Controller.JAIL_CHOICE;
import Spaces.AIPlayer;
import Spaces.GoToJailSpace;
import Spaces.Jail;
import Spaces.Player;
import Spaces.Property;
import Spaces.Railroad;
import Spaces.RealEstate;
import Spaces.RealEstate.Color;
import Spaces.Space;
import Spaces.TaxSpace;
import Spaces.Utility;

class MonopolyTests {
	
//	@Test
//	void test() {
//		Controller controller = new Controller();
//		Model model = controller.model;
//		model.setGameSettingsObj(new GameSettings());
//		controller.rollDice(controller.getCurrentPlayer());
//		controller.rollDice(controller.getCurrentPlayer());
//		controller.rollDice(controller.getCurrentPlayer());
//		controller.rollDice(controller.getCurrentPlayer());
//		controller.rollDice(controller.getCurrentPlayer());
//		controller.rollDice(controller.getCurrentPlayer());
//		controller.rollDice(controller.getCurrentPlayer());
//		controller.rollDice(controller.getCurrentPlayer());
//		controller.rollDice(controller.getCurrentPlayer());
//		controller.rollDice(controller.getCurrentPlayer());
//	}
	
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
		baltic.autoSellHouseHotel(player1);
		assertEquals(0, baltic.getBuildingStage());

		//selling when have 0 buildings
		baltic.autoSellHouseHotel(player1);
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
		
		baltic.autoSellHouseHotel(player1);
		assertEquals(4, baltic.getBuildingStage());
	}
	
	@Test
	void testRealEstateThreeSet() {
		Controller controller = new Controller();
		Model model = controller.model;
		model.setGameSettingsObj(new GameSettings());
		model.getGameSettings().setOptionalBuying(false);
		
		//get 3 red properties
		List<Space> spaces = controller.getSpaces();
		RealEstate james = (RealEstate) spaces.get(16);
		RealEstate tennessee = (RealEstate) spaces.get(18);
		RealEstate york = (RealEstate) spaces.get(19);
		Player player1 = controller.getCurrentPlayer();
		
		//attempt to build after only buying 1 property of set
		controller.purchaseProperty(player1, james);
		controller.purchaseProperty(player1, tennessee);
		controller.purchaseProperty(player1, york);
		assertTrue(york.getIfCanBuild());
		assertEquals(100, york.getBuildPrice());
		
		//misc methods
		assertFalse(york.equals(james));
		System.out.println(york.hashCode());
		
		//test purchasing when optional buying=false
		york.autoSellProperty(player1);
		york.processSpace(player1, model);
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
	
	@Test
	void testAlex() {
		Controller controller = new Controller();
	    Model model = controller.model;
	    GameSettings customSettings = new GameSettings();
	    customSettings.setAmountOfPlayers(2);
	    customSettings.setAmountOfAIPlayers(0);
	    customSettings.setFreeParkingRule(true);
	    model.setGameSettingsObj(customSettings);
	    Player player = controller.getCurrentPlayer();
	    List<Space> spaces = controller.getSpaces();
	    RealEstate mediterranean = (RealEstate) spaces.get(1);
	    RealEstate baltic = (RealEstate) spaces.get(3);
	    model.setCurrentPlayerToNext();
	    model.setCurrentPlayerToNext();
	    model.setCurrentPlayerToNext();
	    
	    model.addToFreeParkingFunds(100);
	    
	    model.getSpaces();

	    model.notifyViewOfNextPlayersTurn(player);
	    
	    model.notifyViewCardDrawn(player, null);
	    
	    model.notifyViewOfAiAction(null);
	    
	    model.getLastDiceRollAmmt();
	    
	    model.getAmmtOfJailAttempts(player);
	    
	    model.getThemes();
	    
	    model.putPlayerInJail(player);
	    
	    assertEquals(model.getGameFinished(), false);
	    
	    model.setGameFinished(model.getGameFinished());
	    
	    model.makeJailCard();
	    
	    model.getGoToJail().apply(player, model);
	    
	    model.board.getTotalSpaces();
	    
	    model.board.getBoardWidth();
	    
	    controller.processJailLogic(player, JAIL_CHOICE.PAY_FIFTY);
	    for (int i=0; i<100; i++)
	    	controller.processJailLogic(player, JAIL_CHOICE.ROLL_DUBLES); // gaurenty they roll doubles
	    controller.processJailLogic(player, JAIL_CHOICE.OUT_OF_JAIL_CARD);
	    
	    controller.processEndTurn();
	    controller.processEndTurn();controller.processEndTurn();
	    controller.getCurrentPlayer().getGameOver();
	    controller.getCurrentPlayer().bankruptcy(3000);
	    controller.processEndTurn();controller.processEndTurn();
	    
	    model.getCommunityChestCards();
	    
	    controller.resolveCard(model.getChanceCards().pop(), player);
	    controller.getModel();
	    controller.getTotalSpaces();
	    
	    controller.getBoardWidth();
	    controller.getFirstSpace();
	    
	    controller.getThemeString();

	    GameSettings customSettings2 = new GameSettings();
	    customSettings2.setTradingEnabled(false);
	    controller.initializeGameSettings(customSettings2);
	    
	    controller.executeTrade(player, player, baltic, null, 0, 0);
	    
	    File mf = new File("MonopolyThemePicker.png");
	    Controller myC = new Controller(null, mf);
	    
	    Controller mfC = new Controller(null);
	    
	    
	    
	}

	@Test
	void testMessages() {
		//purchase prompt
		Controller controller = new Controller();
	    Model model = controller.model;		
		model.setGameSettingsObj(new GameSettings());
		List<Space> spaces = controller.getSpaces();
		RealEstate baltic = (RealEstate) spaces.get(3);
		Player player1 = controller.getCurrentPlayer();
		PurchasePromptMessage msg = new PurchasePromptMessage(player1, baltic);
		assertEquals(player1, msg.getCurrentPlayer());
		assertEquals(baltic, msg.getProperty());
	    
		//ailogsenabled
		AiLogsEnabledMessage msgAi = new AiLogsEnabledMessage(); 
		assertTrue(msgAi.getMessage());	//if message exists, return true
		
		//bankruptcy
		BankruptcyMessage msgBank = new BankruptcyMessage(player1, 0, 0, null, false); 
		assertEquals(0, msgBank.getAmmtOwed());
		assertEquals(0, msgBank.getBuildingsSoldCount());
		assertEquals(null, msgBank.getPropertiesSold());
		assertEquals(false, msgBank.getGameOver());
		assertEquals(player1, msgBank.getPlayer());
	}
	
	@Test
	void testPropertyMisc() {
		Controller controller = new Controller();
		Model model = controller.model;
		model.setGameSettingsObj(new GameSettings());
		model.getGameSettings().setOptionalBuying(false);
		
		//get 3 red properties
		List<Space> spaces = controller.getSpaces();
		RealEstate james = (RealEstate) spaces.get(16);
		Utility electric = (Utility) spaces.get(12);
		Player player1 = controller.getCurrentPlayer();
		Player player5AI = controller.getAllPlayers().get(4);
		
		//attempt to build after only buying 1 property of set
		controller.purchaseProperty(player1, james);
		
		//misc .equals property method
		RealEstate james2 = new RealEstate(Color.ORANGE, "St. James Place", 1, new int[]{2,4,10,30,90,160,250});
		assertTrue(james.equals(james2));
		
		//check utility price is expected when landed on
		electric.processSpace(player5AI, model); //ai player5 buys
		model.setLastDiceRollAmmt(2);
		electric.processSpace(player1, model); //player 1 should get charged 4 x 2
	}
	
	@Test
<<<<<<< HEAD
	void aiTradeTests() {
		//
		Controller controller = new Controller();
		GameSettings customSettings = new GameSettings();
		customSettings.setAmountOfPlayers(0);
	    customSettings.setAmountOfAIPlayers(3);
		controller.initializeGameSettings(customSettings);
	    Model model = controller.model;
	    
	    Player player = controller.getCurrentPlayer();
	    List<Space> spaces = controller.getSpaces();
	    RealEstate mediterranean = (RealEstate) spaces.get(1);
	    RealEstate baltic = (RealEstate) spaces.get(3);
	    
	    
	    if (player instanceof AIPlayer) {
	    	AIPlayer aiPlayer = (AIPlayer) player;
	    	aiPlayer.calculateTrade();
	    	controller.purchaseProperty(aiPlayer, (Property) spaces.get(18)); // ORANGE
	    	controller.purchaseProperty(aiPlayer, (Property) spaces.get(24)); // RED
	    	assertEquals(0,aiPlayer.calculateNumOfCurrentMonopolies());
	    	assertEquals(true, aiPlayer.aiShouldContinueWithTrade(700));
	    	assertTrue(!aiPlayer.getOpprotuneProperties().isEmpty()); // player only has monopolies
	    	
	    	
	    	controller.purchaseProperty(aiPlayer, (Property) spaces.get(5)); // TRAIN
	    	controller.purchaseProperty(aiPlayer, (Property) spaces.get(25)); // TRAIN
	    	controller.purchaseProperty(aiPlayer, (Property) spaces.get(15)); // TRAIN
	    	controller.purchaseProperty(aiPlayer, mediterranean);
	    	controller.purchaseProperty(aiPlayer, baltic);
	    	controller.purchaseProperty(aiPlayer, (Property) spaces.get(39));
	    	aiPlayer.addCash(600);
	    	assertEquals(1,aiPlayer.calculateNumOfCurrentMonopolies());
	    	
	    	assertTrue(!aiPlayer.getOpprotuneProperties().isEmpty());
	    	assertEquals(aiPlayer.getOpprotuneProperties().get(0), spaces.get(18));
	    	aiPlayer.calculateTrade();
	    	
	    	AIPlayer aiPlayer2 = (AIPlayer) model.getPlayers().get(1);
	    	controller.purchaseProperty(aiPlayer2, (Property) spaces.get(21)); //RED
	    	controller.purchaseProperty(aiPlayer2, (Property) spaces.get(37));
	    	
	    	AIPlayer aiPlayer3 = (AIPlayer) model.getPlayers().get(2);
	    	controller.purchaseProperty(aiPlayer3, (Property) spaces.get(35)); // railroad
	    	controller.purchaseProperty(aiPlayer3, (Property) spaces.get(19)); // ORANGE
	    	
	    	aiPlayer3.addCash(-50);
	    	
	    	
	    	List<Property> propertiesThatCanHelpMe = aiPlayer.getTheOtherOwnedPropertiesThatCanHelpMe(aiPlayer.getOpprotuneProperties());
	    	System.out.println(propertiesThatCanHelpMe);
	    	
	    	Map<Integer, List<Property>> numOfPurchasesToGetMonopoly = aiPlayer.generateRankingMap(propertiesThatCanHelpMe);
	    	System.out.println(numOfPurchasesToGetMonopoly);
	    	
	    	
	    	aiPlayer.calculateTrade();
	    	
	    	aiPlayer.calculateTrade();
	    	
	    	aiPlayer3.addCash(-100);
	    	
	    	aiPlayer.calculateTrade();
	    	
	    }
	    // we are only testing ai player 
	    else {
	    	fail();
	    }
	}
	
	
	
	@Test
	void testAi2(){
		//
		Controller controller = new Controller();
		GameSettings customSettings = new GameSettings();
		customSettings.setAmountOfPlayers(0);
	    customSettings.setAmountOfAIPlayers(3);
		controller.initializeGameSettings(customSettings);
	    Model model = controller.model;
	    
	    Player player = controller.getCurrentPlayer();
	    List<Space> spaces = controller.getSpaces();
	    RealEstate mediterranean = (RealEstate) spaces.get(1);
	    RealEstate baltic = (RealEstate) spaces.get(3);
	    
	    
	    if (player instanceof AIPlayer) {
	    	AIPlayer aiPlayer = (AIPlayer) player;
	    	aiPlayer.calculateTrade();
	    	controller.purchaseProperty(aiPlayer, (Property) spaces.get(18)); // ORANGE
	    	controller.purchaseProperty(aiPlayer, (Property) spaces.get(24)); // RED
	    	assertEquals(0,aiPlayer.calculateNumOfCurrentMonopolies());
	    	assertEquals(true, aiPlayer.aiShouldContinueWithTrade(700));
	    	assertTrue(!aiPlayer.getOpprotuneProperties().isEmpty()); // player only has monopolies
	    	
	    	
	    	controller.purchaseProperty(aiPlayer, (Property) spaces.get(5)); // TRAIN
	    	controller.purchaseProperty(aiPlayer, mediterranean);
	    	controller.purchaseProperty(aiPlayer, baltic);
	    	controller.purchaseProperty(aiPlayer, (Property) spaces.get(39));
	    	aiPlayer.addCash(400);
	    	assertEquals(1,aiPlayer.calculateNumOfCurrentMonopolies());
	    	
	    	assertTrue(!aiPlayer.getOpprotuneProperties().isEmpty());
	    	assertEquals(aiPlayer.getOpprotuneProperties().get(0), spaces.get(18));
	    	aiPlayer.calculateTrade();
	    	
	    	AIPlayer aiPlayer2 = (AIPlayer) model.getPlayers().get(1);
	    	controller.purchaseProperty(aiPlayer2, (Property) spaces.get(15)); // TRAIN
	    	controller.purchaseProperty(aiPlayer2, (Property) spaces.get(21)); //RED
	    	controller.purchaseProperty(aiPlayer2, (Property) spaces.get(37));
	    	
	    	AIPlayer aiPlayer3 = (AIPlayer) model.getPlayers().get(2);
	    	controller.purchaseProperty(aiPlayer3, (Property) spaces.get(35)); // railroad
	    	controller.purchaseProperty(aiPlayer3, (Property) spaces.get(19)); // ORANGE
	    	aiPlayer3.addCash(-50);
	    	
	    	assertTrue(aiPlayer.getListOfAllOwnedRailroads(model).size() == 2);
	    	assertTrue(aiPlayer.getListOfAllOwnedRailroads(model).contains(spaces.get(25))== false);
	    	
	    	List<Property> propertiesThatCanHelpMe = aiPlayer.getTheOtherOwnedPropertiesThatCanHelpMe(aiPlayer.getOpprotuneProperties());
	    	System.out.println(propertiesThatCanHelpMe);
	    	
	    	Map<Integer, List<Property>> numOfPurchasesToGetMonopoly = aiPlayer.generateRankingMap(propertiesThatCanHelpMe);
	    	System.out.println(numOfPurchasesToGetMonopoly);
	    	
	    	
	    	aiPlayer.calculateTrade();
	    	
	    	aiPlayer.calculateTrade();
	    	
	    	aiPlayer3.addCash(-100);
	    	
	    	aiPlayer.calculateTrade();
	    	
	    }
	    // we are only testing ai player 
	    else {
	    	fail();
	    }
	}
	
=======
	void testTax() {
		Controller controller = new Controller();
		Model model = controller.model;
		model.setGameSettingsObj(new GameSettings());
		model.getGameSettings().setFreeParkingRule(true); //set freeparking
		Player player1 = controller.getCurrentPlayer();
		
		//get 3 red properties
		List<Space> spaces = controller.getSpaces();
		TaxSpace income = (TaxSpace) spaces.get(4);
		TaxSpace luxury = (TaxSpace) spaces.get(38);
		
		luxury.processSpace(player1, model);//charging flax luxury tax
		income.processSpace(player1, model);//charging 10% income tax ($150)
		
		//flat $200 tax will be charged
		player1.addCash(10000);
		income.processSpace(player1, model);
	}
>>>>>>> refs/heads/main
	
	@Test
	void testUtility() {
		Controller controller = new Controller();
		Model model = controller.model;
		model.setGameSettingsObj(new GameSettings());
		model.getGameSettings().setOptionalBuying(false);
		Player player1 = controller.getCurrentPlayer();
		Player player2 = controller.getAllPlayers().get(1);

		List<Space> spaces = controller.getSpaces();
		Utility waterworks = (Utility) spaces.get(28);
		Utility electric = (Utility) spaces.get(12);
		
		
		waterworks.processSpace(player1, model); 	//player 1 buys it
		waterworks.processSpace(player2, model); 	//player 2 lands on it
		electric.processSpace(player1, model); 		//player 1 completes the match
	}

}
