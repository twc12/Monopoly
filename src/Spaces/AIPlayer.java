package Spaces;

import java.util.*;
import Monopoly.Controller;
import Monopoly.Model;
import Monopoly.Controller.JAIL_CHOICE;

/**
 * @author Jarrod Heyer Martinez
 * This class represents the AI, the AI makes strategic decisions based largely on
 * who the determined weakest player in the game is, and makes an effort to target
 * them with build decisions to attempt to eliminate them from the game. Tries to
 * build on it's owned properties often and will buy properties most of the time 
 * until they cannot any longer.
 * 
 * The player's weakness is determined based on several factors the most important
 * of which being their total net worth, which is the best way to determine if 
 * someone can be eliminated with a good building placement, etc
 */
public class AIPlayer extends Player {

    private Player weakestPlayer;
    private int startingPlayerCount;

    private static final HashMap<RealEstate.Color, Integer> SET_SIZES = new HashMap<>();
    
    /**
     * A simple field to hold the proper amount of cards for each color set of
     * properties, since some colors have different amounts of properties associated
     * we set this up for easy access
     */
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
    
    
    /**
     * calculateTrade(): This function will determine if 
     * the Ai player should attempt a trade and who to trade with.
     *   Overall goal
	 * 		- If the ai player does not have 2 monopolies yet & has atleast $700 cash, attempt a trade
	 * 		- Find all properties that I can attempt to trade with others to get monoploly 
	 *		- Rank these properties from "CLOSE TO MONOPOLY" -> "Far from monopoly"
	 * 		- Pick the list of properties that are CLOSES TO MONOPOLY
	 * 		- Find the WEAKEST OWNER out of all of those properties 
	 *		- If I have already attempted to trade for this property, then no matter the current owner, based on the prev owner cash ammount
	 *		-   if they lost   money since then, then offer $50 less than my previous offer
	 *		-   .. .... gained money ..... ..... .... ..... $50 more .... .. ........ .....
     */
    public void calculateTrade() {
    	int aiBudgetForTrading = 700; // the amount the ai must have to trade
    	
    	System.out.println("[+] Starting a Ai Player Calculate Trade");
    	boolean aiShouldDoTrade = aiShouldContinueWithTrade(aiBudgetForTrading);
    	if (aiShouldDoTrade == false) return;
    	
    	// find all properties that are not monopolies for the ai player to trade
    	List<Property> opprotuneProperties = getOpprotuneProperties();
    	// if there arent any opprotune properties then cancle the trade
    	if (opprotuneProperties.isEmpty()) {
    		System.out.println("	[x] there is no opprotuneProperties, cancling trade");
    		return;
    	}
    	
    	// now search out for players who have matching colors of our opportune properties
    	
    }
    
    /// ----- vvvv ----- helper functions ----- vvvv -----
    
    /**
     * playersICanTradeWith(): This function will go over each opportune property
     * take its color, then try to find other players that have that same color of properties.
     * This function searches for players I can trade with for a specific property that will
     * help me out. 
     * @return Map<Property, Player>: A map of properties that would help me, the player I can buy them from 
     */
    private Map<Property, Player> playersICanTradeWith(List<Property> opportuneProperties){
    	Map<Property, Player> propertiesThatHelpMe = new HashMap<>();
    	
    	Set<RealEstate.Color> setOfAlreadyAddedColors = new HashSet<>();
    	boolean alreadyAddedRailroad = false;
    	// remember there could be duplicate colors in the opportuneProperties
    	for (Property property : opportuneProperties) {
    		// if I have a railroad and I havent searched for others, then search
    		if (property instanceof Railroad && alreadyAddedRailroad == false) {
    			List<Player> playersWhoHaveRailroads = getListOfPlayersWhoHaveRailroads(this.getModel());
    		}
    	}
    	return null;
    }
    
    /**
     * getListOfPlayersWhoHaveRailroads(): This function finds the players who 
     * 
     * @param model (Model): The model of the entire game, so we can get the list
     * 						of all other players
     * 
     * @return
     */
    private List<Player> getListOfPlayersWhoHaveRailroads(Model model){
    	List<Player> listOfPlayersWhoHaveRailroads = new ArrayList<>();
    	for (Player player : model.getPlayers()) {
    		// make sure player isnt us 
    		if (player == this) continue;
    		
    		// search for if this player has a railroad
    		for (Property property : player.getListOfProperties()) {
    			// if this player has a railroad, andd then and move onto next player
    			if (property instanceof Railroad) {
    				listOfPlayersWhoHaveRailroads.add(player);
    				break;
    			}
    		}
    	}
    	return listOfPlayersWhoHaveRailroads;
    }
    
    
    
    /**
     * getOpprotuneProperties(): This function will go over every 
     * property that the ai player has, checking if its not a utility, 
     * and if its not already apart of a monopoly 
     * This could return two pink properties for example
     * 
     * @return List<Property>: List of all properites (including duplicates) that
     * 							are not apart of a monopoly
     */
    public List<Property> getOpprotuneProperties(){
    	List<Property> opprotuneProperties = new ArrayList<>();
    	for (Property property: this.getListOfProperties()) {
    		if (property instanceof Utility) {
    			continue; // UTILITIES SUUUUCK
    		}
    		else if (property instanceof Railroad) {
    			if (property.getRentStageIndex() < 3) opprotuneProperties.add(property);
    		}
    		else if (property instanceof RealEstate) {
    			if (property.getRentStageIndex() == 0) opprotuneProperties.add(property);
    		}
    	}
    	return opprotuneProperties;
    }
    
    /**
     * aiShouldContinueWithTrade(): This function will do the 
     * initial checks for if the AI player should continue the trade.
     * it will return false if the player already has >=2 monopolies,
     * or doesnt have any properties to attempt to build up 
     * or doesnt have atleast the budget given.
     * 
     * @param aiBudgetForTrading (int): the amount the ai must have for trading
     * 
     * @return boolean: true if the ai should trade, false if not 
     */
    public boolean aiShouldContinueWithTrade(int aiBudgetForTrading) {
    	int numOfCurrentMonopolies = calculateNumOfCurrentMonopolies();
    	if (numOfCurrentMonopolies >= 2) {
    		System.out.printf("		[x] Ai has %d num of monpolies, cancling trade\n",numOfCurrentMonopolies);
    		return false;
    	}
    	
    	// if the ai player has at least 1 property we can build then continue the trade
    	boolean aiPlayerHasAtleast1Property = false;
    	for (Property property: this.getListOfProperties()) {
    		if (! (property instanceof Utility)) {
    			aiPlayerHasAtleast1Property = true;
    			break;
    		}
    	}
    	if (aiPlayerHasAtleast1Property == false) {
    		System.out.println("		[x] AI doesnt have atleast 1 property, cancling trade");
    		return false;
    	}
    	
    	// if the player doesnt have the budget to trade then it should cancle
    	if (this.getCashAmmt() < aiBudgetForTrading) {
    		System.out.println("		[x] AI doesnt have enough cash, cancling trade");
    		return false;
    	}
    	
    	return true;
    }
    
    /**
     * calculateNumOfCurrentMonopolies(): This function is called to 
     * make sure the Ai does not already have a certain number of monopolies 
     * before continuing the trade 
     * @return int: the current number of monopolies the ai player has 
     */
    public int calculateNumOfCurrentMonopolies() {
    	// Go over every property checking if its not already a color checked or at color stage or above
    	int railRoadMonopoly = 0; //counts the number of rail roads 4=monopoly
    	
    	Map<RealEstate.Color, Integer> colorCounts = new HashMap<>();
    	
    	for (Property property : this.getListOfProperties()) {
    		if (property instanceof Railroad) {
    			railRoadMonopoly++;
    		}
    		else if (property instanceof Utility) {
    			// WE DONT GIVE A CRAP BECAUSE UTILITIES SUUUUCK
    			continue;
    		}
    		// count number of color properties 
    		else if (property instanceof RealEstate) {
    			RealEstate realEstate = (RealEstate) property;
    			RealEstate.Color estateColor = realEstate.getColor();
    			if (! colorCounts.containsKey(estateColor)) {
    				colorCounts.put(estateColor, 0);
    			}
    			colorCounts.put(estateColor, colorCounts.get(estateColor) +1);
    		}
    	}
    	
    	int monopolyCount = 0;
    	if (railRoadMonopoly == 4) monopolyCount ++;
    	// go over all the colors, if there is a 3 (or 2 if blue or brown) then count as monopoly
    	for (RealEstate.Color estateColor: colorCounts.keySet()) {
    		// Blues have monopolies of 2 
    		if (estateColor.equals(RealEstate.Color.BLUE) || estateColor.equals(RealEstate.Color.BROWN)) {
    			if (colorCounts.get(estateColor) == 2) monopolyCount++;
    		}
    		else {
    			if (colorCounts.get(estateColor) == 3) monopolyCount++;
    		}
    	}
    	
    	return monopolyCount;
    }
    
    
    
    
    
    
    
    
    
    
    
    
}