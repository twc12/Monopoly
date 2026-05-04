package MonopolyTests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import Cards.*;
import Monopoly.*;
import Spaces.*;
import Spaces.RealEstate.*;
import Spaces.AIPlayer.*;

public class AITests {
	
	@Test
	void whoIsWeakest() {	
		Model model = new Model();	
		model.setGameSettingsObj(new GameSettings());
		AIPlayer playerOne = new AIPlayer(0, "", "", model);
		AIPlayer playerTwo = new AIPlayer(1, "", "", model);
		AIPlayer playerThree = new AIPlayer(2, "", "", model);
		Controller controller = new Controller();
		
		// Only two brown properties, player now has monopoly
		playerOne.addProperty(new RealEstate(Color.BROWN, "Mediterranean Avenue", 60, new int[]{2,4,10,30,90,160,250}));
		playerOne.addProperty(new RealEstate(Color.BROWN, "Baltic Avenue", 60, new int[]{4,8,20,60,180,320,450}));	
		playerTwo.addProperty(new Railroad("Reading Railroad", new int[] {25, 50, 10, 200}, "railroad.png"));
		playerOne.addProperty(new Railroad("Reading Railroad", new int[] {25, 50, 10, 200}, "railroad.png"));
					
		playerTwo.addCash(-1500);
		
		playerOne.playAITurn(controller);
		assertEquals(playerOne.getWeakest().getId(), playerTwo.getId());
		playerOne.playAITurn(controller);
		assertEquals(playerOne.getWeakest().getId(), playerTwo.getId());
	}
	

}
