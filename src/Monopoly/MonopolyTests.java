package Monopoly;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import Spaces.Player;
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
	

}
