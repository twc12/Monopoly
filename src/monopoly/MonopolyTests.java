package monopoly;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MonopolyTests {

	@Test
	void test() {

		
		Controller controller = new Controller(new View());
		Model model = controller.model;
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

}
