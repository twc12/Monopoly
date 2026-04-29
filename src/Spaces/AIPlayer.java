package Spaces;

import java.util.*;
import Monopoly.Controller;
import Monopoly.Model;
import Monopoly.Controller.JAIL_CHOICE;

/**
 * @author
 * This class represents the AI, currently all choices are just made at 
 * random by the AI, has implementation for every system currently implemented
 */
public class AIPlayer extends Player {

    private Player weakestPlayer;
    private int startingPlayerCount;

    private static final HashMap<RealEstate.Color, Integer> SET_SIZES = new HashMap<>();
    
    static {
        SET_SIZES.put(RealEstate.Color.BROWN, 2);
        SET_SIZES.put(RealEstate.Color.BLUE, 2);
        SET_SIZES.put(RealEstate.Color.LIGHTBLUE, 3);
        SET_SIZES.put(RealEstate.Color.PINK, 3);
        SET_SIZES.put(RealEstate.Color.ORANGE, 3);
        SET_SIZES.put(RealEstate.Color.RED, 3);
        SET_SIZES.put(RealEstate.Color.YELLOW, 3);
        SET_SIZES.put(RealEstate.Color.GREEN, 3);
    }

    /**
     * Inherits the fields from the Player class
     * @param id the player's identification id
     * @param icon the player's ingame icon that will show on the board
     * @param theme the theme of this player and the game generally
     * @param model the model of the game itself
     */
    public AIPlayer(int id, String icon, String theme, Model model) {
        super(id, icon, theme, model);
        startingPlayerCount = model.getPlayers().size();
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
     * Checks if the given player owns a complete color set of real estate properties.
     * @param player the player to check
     * @return true if the player owns at least one full color set, false otherwise
     */
    private boolean hasMonopoly(Player player) {
        List<Property> properties = player.getListOfProperties();
        HashMap<RealEstate.Color, Integer> propertySets = new HashMap<>();

        for (Property property : properties) {
            if (property instanceof RealEstate realEstate) {
                propertySets.merge(realEstate.getColor(), 1, (a, b) -> a + b);
            }
        }

        for (Map.Entry<RealEstate.Color, Integer> entry : propertySets.entrySet()) {
            if (entry.getValue() >= SET_SIZES.get(entry.getKey())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the given player owns any railroad properties.
     * @param player the player to check
     * @return true if the player owns at least one railroad, false otherwise
     */
    private boolean hasRailroads(Player player) {
        List<Property> properties = player.getListOfProperties();
        for (Property property : properties) {
            if (property instanceof Railroad) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the net worth of the player, calculated as their cash reserves
     * plus the purchase price of all owned properties.
     * @param player the player to evaluate
     * @return the player's total net worth
     */
    private int getNetWorth(Player player) {
        List<Property> properties = player.getListOfProperties();
        int worth = player.getCashAmmt();
        for (Property property : properties) {
            worth += property.getPurchaseAmount();
        }
        return worth;
    }

    /**
     * Determines which opposing player is the most vulnerable, based on
     * net worth, whether they own a monopoly, and whether they own any railroads.
     * The AI uses this player as its primary target when making strategic decisions.
     * @return the player deemed weakest among all opponents
     */
    private Player determineWeakest() {
        List<Player> players = getModel().getPlayers();
        Player weakest = players.get(0);
        int highestWeakness = 0;
        for (Player player : players) {
            int weakness = 0;
            if (getNetWorth(player) < 300) {
                weakness += 4;
            } else if (getNetWorth(player) < 600) {
                weakness += 2;
            } else if (getNetWorth(player) < 1000) {
                weakness += 1;
            }
            if (!hasMonopoly(player)) {
                weakness += 2;
            }
            if (!hasRailroads(player)) {
                weakness += 1;
            }
            if (player == this) {
                weakness = 0;
            }
            if (weakness > highestWeakness) {
                highestWeakness = weakness;
                weakest = player;
            }
        }
        return weakest;
    }

    /**
     * Executes all logic for a complete AI turn, including jail handling,
     * dice rolling, building decisions, and ending the turn.
     * A safety cap prevents infinite loops during the dice-rolling phase.
     * @param controller the game controller
     */
    public void playAITurn(Controller controller) {
        weakestPlayer = determineWeakest();

        if (this.isInJail()) {
            handleJail(controller);
            setIsDoneRollingDice(true);
            if (this.isInJail()) {
                return;
            }
            build(controller);
            controller.processEndTurn();
            return;
        }

        int safetyCap = 0;
        while (!getIsDoneRollingDice() && safetyCap < 10) { 
            controller.rollDice(this);
            safetyCap++;
            if (this.isInJail()) break;
        }

        build(controller);
        controller.processEndTurn();
    }

    /**
     * Handles the AI's decision-making while in jail. Prioritizes using a
     * Get Out of Jail Free card if available. In late game, prefers rolling
     * for doubles to stay in jail longer. Otherwise, pays the fine if cash allows.
     * @param controller the game controller
     */
    private void handleJail(Controller controller) {
        List<JAIL_CHOICE> options = new ArrayList<>();
        options.add(JAIL_CHOICE.ROLL_DUBLES);
        if (getCashAmmt() >= 50) options.add(JAIL_CHOICE.PAY_FIFTY);
        if (getAmmtOfGOOJCards() > 0) options.add(JAIL_CHOICE.OUT_OF_JAIL_CARD);

        JAIL_CHOICE choice;
        if (getAmmtOfGOOJCards() > 0) {
            choice = JAIL_CHOICE.OUT_OF_JAIL_CARD;
        }
        else if (isLateGame()) {
            choice = JAIL_CHOICE.ROLL_DUBLES;
        } 
        else if (getCashAmmt() > 300) {
            choice = JAIL_CHOICE.PAY_FIFTY;
        } 
        else {
            choice = JAIL_CHOICE.ROLL_DUBLES;
        }
        controller.processJailLogic(this, choice);
    }

    /**
     * Counts the total number of hotels currently built across all players' properties.
     * @return the total hotel count on the board
     */
    private int countHotels() {
        int hotels = 0;
        for (Player player : getModel().getPlayers()) {
            for (Property property : player.getListOfProperties()) {
                if (property instanceof RealEstate re) {
                    if (re.getBuildingStage() == 5)
                        hotels++;
                }
            }
        }
        return hotels;
    }

    /**
     * Determines whether the game is in a late stage based on player eliminations,
     * the number of monopolies held across all players, and the number of hotels on the board.
     * @return true if late-game conditions are met, false otherwise
     */
    private boolean isLateGame() {
        List<Player> players = getModel().getPlayers();
        
        if (players.size() < startingPlayerCount) {
            return true;
        }
        
        int monopolyCount = 0;
        for (Player player : players) {
            if (hasMonopoly(player)) monopolyCount++;
        }
        if (monopolyCount >= 2) return true;
        
        if (countHotels() >= 3) return true;
        
        return false;
    }

    /**
     * Decides whether and where to build houses or hotels this turn.
     * The AI skips building if it lacks a monopoly or has insufficient funds.
     * Otherwise, it selects the most strategically threatening buildable property.
     * @param controller the game controller
     */
    private void build(Controller controller) {

        if (!hasMonopoly(this)) {
            getModel().notifyViewOfAiAction(getPlayerName() + " has no monopoly, cannot build");
            return;
        }

        if (getNetWorth(this) < 600) {
            getModel().notifyViewOfAiAction(getPlayerName() + " saving money, not building");
            return;
        }

        List<RealEstate> buildable = getBuildableProperties();

        if (buildable.isEmpty()) {
            getModel().notifyViewOfAiAction(getPlayerName() + " no buildable properties");
            return;
        }

        RealEstate pick = getMostThreateningProperty(buildable);

        if (pick == null) return;

        getModel().notifyViewOfAiAction(getPlayerName() + " building on " + pick.getName());
        controller.buildHouseHotel(this, pick);
    }

    /**
     * From the list of buildable properties, selects the one the weakest player
     * is most likely to land on based on their current board position and dice range.
     * Falls back to the property with the lowest building stage if none are in range.
     * @param buildable the list of properties eligible for building
     * @return the most strategically threatening property, or null if the list is empty
     */
    private RealEstate getMostThreateningProperty(List<RealEstate> buildable) {
        if (weakestPlayer == null) {
            return buildable.stream()
                .min(Comparator.comparingInt(RealEstate::getBuildingStage))
                .orElse(null);
        }

        int weakestPosition = getSpaceIndex(weakestPlayer.getCurrentSpace());
        int boardSize = getModel().board.getTotalSpaces();

        RealEstate bestPick = null;
        int closestDistance = Integer.MAX_VALUE;

        for (RealEstate re : buildable) {
            int propertyPosition = getSpaceIndex(re);
            int distance = (propertyPosition - weakestPosition + boardSize) % boardSize;

            if (distance <= 12 && distance >= 2) {
                if (bestPick == null || distance < closestDistance) {
                    bestPick = re;
                    closestDistance = distance;
                }
            }
        }

        if (bestPick == null) {
            return buildable.stream()
                .min(Comparator.comparingInt(RealEstate::getBuildingStage))
                .orElse(null);
        }

        return bestPick;
    }

    /**
     * Returns the board index of the given space.
     * @param space the space to locate
     * @return the zero-based index of the space on the board
     */
    private int getSpaceIndex(Space space) {
        return getModel().board.getSpaces().indexOf(space);
    }

    /**
     * Returns only the real estate properties that belong to a completed color set,
     * meaning the AI owns all properties of that color and can legally build on them.
     * @return a list of buildable real estate properties
     */
    private List<RealEstate> getBuildableProperties() {
        List<Property> properties = getListOfProperties();
        
        HashMap<RealEstate.Color, Integer> colorCounts = new HashMap<>();
        for (Property property : properties) {
            if (property instanceof RealEstate re) {
                colorCounts.merge(re.getColor(), 1, (a, b) -> a + b);
            }
        }

        List<RealEstate> buildable = new ArrayList<>();
        for (Property property : properties) {
            if (property instanceof RealEstate re) {
                int owned = colorCounts.getOrDefault(re.getColor(), 0);
                int needed = SET_SIZES.get(re.getColor());
                if (owned >= needed) {
                    buildable.add(re);
                }
            }
        }
        return buildable;
    }

    /**
     * Decides whether the AI should purchase a landed-on property.
     * The AI buys only if it can afford the purchase price with at least
     * $200 remaining as a cash buffer afterward.
     * @param property the property to potentially purchase
     * @param model the game model
     */
    public void decidePurchase(Property property, Model model) {
        if (getCashAmmt() >= property.getPurchaseAmount() + 200) {
            getModel().notifyViewOfAiAction(getPlayerName() + " decided to buy \n" + property.getName());
            property.purchaseProperty(this, model);
        } else {
            getModel().notifyViewOfAiAction(getPlayerName() + " cannot afford \n" + property.getName());
        }
    }
}