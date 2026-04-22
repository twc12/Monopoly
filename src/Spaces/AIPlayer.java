package Spaces;

import java.util.*;
import Monopoly.Controller;
import Monopoly.Model;
import Monopoly.Controller.JAIL_CHOICE;

/**
 * @author Jarrod Heyer Martinez
 * This class represents the AI, currently all choices are just made at 
 * random by the AI, has implementation for every system currently implemented
 */
public class AIPlayer extends Player {

    private Random random = new Random();

    /**
     * Inherits the fields from the Player class
     * @param id the player's identification id
     * @param icon the player's ingame icon that will show on the board
     * @param theme the theme of this player and the game generally
     * @param model the model of the game itself
     */
    public AIPlayer(int id, String icon, String theme, Model model) {
        super(id, icon, theme, model);
    }

    /**
     * Checks if this is an AI player or not
     * @return true if so, false if not
     */
    @Override
    public boolean isAI() {
        return true;
    }

    /**
     * Plays through all the logic of a typical turn
     * @param controller
     */
    public void playAITurn(Controller controller) {
        // Jail logic
        if (this.isInJail()) {
            handleJail(controller);
            if (this.isInJail()) {
                controller.processEndTurn();
                return;
            }
        }
        // safety cap is just to prevent an infinite loop
        // this may prove unnecessary but exists just to 
        // be safe
        int safetyCap = 0;
        // Rolls dice
        while (!getIsDoneRollingDice() && safetyCap < 10) {
            controller.rollDice(this);
            safetyCap++;
            if (this.isInJail()) break;
        }
        // Randomly chooses to build or not
        buildRandomly(controller);
        // Ends the turn
        controller.processEndTurn();
    }

    /**
     * The helper for the AI jail logic, looks very similar to that
     * method for normal players but is well, essentially chosen randomly
     * @param controller the game controller 
     */
    private void handleJail(Controller controller) {
        // Roll double is always a choice, checks if the other ways to get
        // out of jail are valid for this AI
        List<JAIL_CHOICE> options = new ArrayList<>();
        options.add(JAIL_CHOICE.ROLL_DUBLES);
        if (getCashAmmt() >= 50) {
            options.add(JAIL_CHOICE.PAY_FIFTY);
        }
        if (getAmmtOfGOOJCards() > 0) {
            options.add(JAIL_CHOICE.OUT_OF_JAIL_CARD);
        }

        // Randomly chooses an option to getout of jail
        JAIL_CHOICE choice = options.get(random.nextInt(options.size()));
        controller.processJailLogic(this, choice);
    }

    /**
     * Randomly chooses if the AI should built or not
     * @param controller
     */
    private void buildRandomly(Controller controller) {
        // it's a binary choice so it just chooses at a 50% change
        // if it will build on a property or not
        if (!random.nextBoolean()) return;

        // Acquires the AI owned properties
        List<RealEstate> owned = new ArrayList<>();
        for (Property property : getListOfProperties()) {
            if (property instanceof RealEstate) {
                owned.add((RealEstate) property);
            }
        }
        // If the AI owns no properties, does not attempt to build
        if (owned.isEmpty()) return;

        // Likely going to need to add a check for if the AI owns
        // a full set of properties or not

        // Picks a piece of owned real estate to build on
        RealEstate pick = owned.get(random.nextInt(owned.size()));
        controller.buildHouseHotel(this, pick);
    }

    /**
     * Randomly chooses whether or not to buy a property
     * @param property the property we're currently on 
     * @param model the game model
     */
    public void decidePurchase(Property property, Model model) {
        if (random.nextBoolean() && getCashAmmt() >= property.getPurchaseAmount()) {
            property.purchaseProperty(this, model);
        }
    }
}