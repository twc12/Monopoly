package Spaces;

import java.util.*;
import Monopoly.Controller;
import Monopoly.Model;
import Monopoly.Controller.JAIL_CHOICE;

/**
 * This class represents the AI, the AI makes strategic decisions based largely on
 * who the determined weakest player in the game is, and makes an effort to target
 * them with build decisions to attempt to eliminate them from the game. Tries to
 * build on it's owned properties often and will buy properties most of the time 
 * until they cannot any longer.
 * 
 * The player's weakness is determined based on several factors the most important
 * of which being their total net worth, which is the best way to determine if 
 * someone can be eliminated with a good building placement, etc
 * @author Jarrod Heyer Martinez
 * @author Alex
 */
public class AIPlayer extends Player {

    private Player weakestPlayer;
    private int startingPlayerCount;
    /**
     * This keeps a log of the trades this Ai player did for a specific property 
     */
    private Map<Property, TradeInfo> prevTradeAttempsPropertiesMap;
    private Map<Property, Integer> successsFullTrades;

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
        prevTradeAttempsPropertiesMap = new HashMap<>();
        successsFullTrades = new HashMap<>();
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
    	System.out.println("[-] <AiPlayer>playAiTurn: currPlayer ("+this.getPlayerName()+")");
    	
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
           
        

        calculateTrade();
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
     * decideOnOfferedTrade(thePropertyIHaveTheyWant, tradeOfferCash): This function is called
     * when another player tries to attempt to trade with a AI Player, the ai player is given 
     * the amount of cash the player offers and the property the other player wants in return.
     * This function will determine if they want to sell that property based on a threshold for profit
     * @param thePropertyIHaveTheyWant (Property): The property this player would give up for the cash if they accept
     * @param tradeOfferCash (int): The amount of cash this player would get if it accepts
     * @return boolean: True for if the trade is accepted, false if rejected
     */
    public boolean decideOnOfferedTrade(Property thePropertyIHaveTheyWant, int tradeOfferCash) {
    	System.out.println("[+] Ai player is asked to trade their "+thePropertyIHaveTheyWant.getName()+" for $"+tradeOfferCash);
    	int amtOfProfitDesired = 100;
    	// if we have traded to gain this property I have they want then base my asking price off of that if its a higher price 
    	if (successsFullTrades.containsKey(thePropertyIHaveTheyWant)) {
    		System.out.println("	[+] The ai found that it traded to get this property in he past, if that trades price is higher than the default it will use that");
    		int prevTradePrice = successsFullTrades.get(thePropertyIHaveTheyWant);
    		int defaultAskingPrice = thePropertyIHaveTheyWant.getPurchaseAmount();
    		
    		// only if the prevTrading price is larger then use that for the base price 
    		if (defaultAskingPrice < prevTradePrice) {
    			if (tradeOfferCash >= prevTradePrice + amtOfProfitDesired) {
    	    		System.out.println("	[+] The ai player accepts the trade! Its better than the prev trade price");
    	    		return true;
    			}
    			else {
    				// BUT if the ai player is down on their luck (lower than $120) they will accept 
    				if (this.getCashAmmt() < 120) {
    					System.out.println("	[+] The Ai player is low on cash, so they will accept trade");
    					return true;
    				}
    				System.out.println("	[+] The ai player declines the trade. Its not better than the prev trade price");
    	    		return false;
    			}
    		}
    	}
    	
    	if (tradeOfferCash >= thePropertyIHaveTheyWant.getPurchaseAmount() + amtOfProfitDesired) {
    		System.out.println("	[+] The ai player accepts the trade!");
    		return true;
    	}
    	else {
    		// BUT if the ai player is down on their luck (lower than $120) they will accept 
			if (this.getCashAmmt() < 120) {
				System.out.println("	[+] The Ai player is low on cash, so they will accept trade");
				return true;
			}
    		System.out.println("	[+] The ai player declines the trade.");
    		return false;
    	}
    }
    
    
    /**
     * removeTradeLog(property): This function is called when 
     * a successful trade goes through, so we should remove the log of that 
     * trade going through. 
     * 
     * That also means we should keep a log of how much we got it for 
     * so another player cannot just trade for it back and make easy
     * profit.
     * @param property (Property): The property we successfully paid for and got during 
     * 								a trade.
     */
	public void removeTradeLog(Property property, int purchasePrice) {
		// if the property was a successful trade keep track of the price 
		successsFullTrades.put(property, purchasePrice);
		prevTradeAttempsPropertiesMap.remove(property);
	}
    
    
    /**
     * calculateTrade(): This function will determine if 
     * the Ai player should attempt a trade and who to trade with.
     *   Overall goal
	 * 		- If the ai player does not have 2 monopolies yet & has atleast $500 cash, attempt a trade
	 * 		- Find all properties that I can attempt to trade with others to get monoploly 
	 *		- Rank these properties from "CLOSE TO MONOPOLY" -> "Far from monopoly"
	 * 		- Pick the list of properties that are CLOSES TO MONOPOLY
	 * 		- Find the WEAKEST OWNER out of all of those properties 
	 *		- If I have already attempted to trade for this property, then no matter the current owner, based on the prev owner cash ammount
	 *		-   if they lost   money since then, then offer $50 less than my previous offer
	 *		-   .. .... gained money ..... ..... .... ..... $50 more .... .. ........ .....
     */
    public void calculateTrade() {
    	int aiBudgetForTrading = 500; // the amount the ai must have to trade
    	
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
    	
    	// now search out for properties that are owned by others that have matching colors of our opportune properties
    	List<Property> otherPropertiesThatHelpMe = getTheOtherOwnedPropertiesThatCanHelpMe(opprotuneProperties);
    	
    	// if no one else owns anything that matches mine, then cancel the trade because there are no properties that i can benifit from 
    	if (otherPropertiesThatHelpMe.isEmpty()) {
    		System.out.println("	[x] no one owns any properties that match with mine, cancling tarde");
    		return;
    	}
    	
    	// Now Create a ranking map of the properties based on how close they get me to a monopoly in their region. Lower number is better
    	Map<Integer, List<Property>> numOfPurchasesToGetMonopoly = generateRankingMap(otherPropertiesThatHelpMe);
    	
    	// find the properties that have the minimum number of purchases to get monopoly 
    	int min = Collections.min(numOfPurchasesToGetMonopoly.keySet());
    	List<Property> bestPropertiesToBuy = numOfPurchasesToGetMonopoly.get(min);
    	System.out.println("	[+] Ive determined the best properties to buy are "+bestPropertiesToBuy);
    	
    	// determine out of all the owners of the properties who is the weakest
    	Property thePropertyWeakestOwnerOwnsThatIWant = determineWeakestOwnersProperty(bestPropertiesToBuy);
    	System.out.printf(" 	[+] calculateTrade determined that %s is the property they want from weakest player -> %s\n", thePropertyWeakestOwnerOwnsThatIWant.getName(), thePropertyWeakestOwnerOwnsThatIWant.getOwner().getPlayerName());
    	
    	// keep a record of previous attempts for this trade 
    	int newTradeOfferCash = recordAndComputeTradeOffer(thePropertyWeakestOwnerOwnsThatIWant, aiBudgetForTrading);
    	
    	System.out.printf(" 	[+] calculateTrade determined it will attempted to buy %s for %d from %s\n",thePropertyWeakestOwnerOwnsThatIWant.getName(), newTradeOfferCash, thePropertyWeakestOwnerOwnsThatIWant.getOwner().getPlayerName());
    	
    	Player weakestSeller = thePropertyWeakestOwnerOwnsThatIWant.getOwner();
    	this.getModel().notifyViewOfAiAttemptedTrade(this, thePropertyWeakestOwnerOwnsThatIWant, newTradeOfferCash, weakestSeller);
    }
    
    /// ----- vvvv ----- helper functions ----- vvvv -----
    
    
    /**
     * recordAndComputeTradeOffer(thePropertyIWant): This function will
     * check if this ai player has already attempted to purchase 
     * @param thePropertyIWant (Property): The property I want to trade for
     * @param aiBudgetForTrading (int): The amount the ai is allowed to spend on a trade 
     * @return int: the newTradeOfferCash amount
     */
    private int recordAndComputeTradeOffer(Property thePropertyIWant, int aiBudgetForTrading) {
    	int newTradeOfferCash;
    	Player weakestPlayer = thePropertyIWant.getOwner();
    	
    	// if this ai player has already attempted to purchase this space then pull the old price and update it 
    	if (prevTradeAttempsPropertiesMap.containsKey(thePropertyIWant)) {
    		System.out.println("	[+] there is a record of this property being traded before");
    		// it doesnt matter if the owner of the properties have changed, ill still base it off of my last trade and the players cash amount
    		TradeInfo prevTradeInfo = prevTradeAttempsPropertiesMap.get(thePropertyIWant);
    		int prevTradeOwnersCashAmt = prevTradeInfo.getPlayersPrevCashAmount();
    		int currTradeOwnersCashAmt = weakestPlayer.getCashAmmt();
    		
    		// if the current potential seller is richer than before then increase the price to intise them more 
    		if (currTradeOwnersCashAmt >= prevTradeOwnersCashAmt) {
    			newTradeOfferCash = prevTradeInfo.getPreviousTradeOfferAmt() + 50;
    			System.out.println("	[+] ive determined that we should increase the price from the previous trade");
    		}
    		else { // if they are poorer then we dont need to pay so much because they might really want the money
    			newTradeOfferCash = prevTradeInfo.getPreviousTradeOfferAmt() - 20;
    			if (newTradeOfferCash < 50) { // dont go below $10 at least
    				newTradeOfferCash = 50;
    			}
    			// make sure the newTradeOfferCash isnt over our current budget
    	    	if (newTradeOfferCash > aiBudgetForTrading) newTradeOfferCash = aiBudgetForTrading;
    			System.out.println("	[+] ive determined that we should decrease the price from the previous trade");
    		}
    		
    		// store a record of the new trade
    		int playersCurrCashAmt = weakestPlayer.getCashAmmt();
    		TradeInfo newTradeInfo = new TradeInfo(newTradeOfferCash, playersCurrCashAmt);
    		prevTradeAttempsPropertiesMap.put(thePropertyIWant, newTradeInfo);
    	}
    	// if there wasnt a previous time we've traded then store current trade info 
    	else {
    		newTradeOfferCash = thePropertyIWant.getPurchaseAmount() + 50; // offer $50 more than original price
    		// make sure the newTradeOfferCash isnt over our current budget
        	if (newTradeOfferCash > aiBudgetForTrading) newTradeOfferCash = aiBudgetForTrading;
    		int playersCurrCashAmt = weakestPlayer.getCashAmmt();
    		TradeInfo newTradeInfo = new TradeInfo(newTradeOfferCash, playersCurrCashAmt);
    		prevTradeAttempsPropertiesMap.put(thePropertyIWant, newTradeInfo);
    	}
    	
    	
    	
    	return newTradeOfferCash;
    }
    
    /**
     * determineWeakestOwnersProperty(bestPropertiesToBuy): This function 
     * will determine which of all the owners of these properties is the weakest
     * and return the property of that player.
     * 
     * If there are multiple properties from the same owner who is the weakest
     * we will simply take the first property found.
     * 
     * @param bestPropertiesToBuy (List<Property>): A list of properties that will help me get a monopoly the quickest
     * @return Property: The property that I want from the weakest owner 
     */
    private Property determineWeakestOwnersProperty(List<Property> bestPropertiesToBuy) {
    	Player weekestPlayer = null;
    	int weakestPlayerScore = Integer.MAX_VALUE; //  the lower the score the weaker the player
    	Property thePropertyWeakestOwnerOwnsThatIWant = null;
    	
    	for (Property othersProperty: bestPropertiesToBuy) {
    		Player curOtherPlayer = othersProperty.getOwner();
    		int currWeaknessScore = generateWeaknessScore(curOtherPlayer);
    		if (weekestPlayer == null || currWeaknessScore < weakestPlayerScore) {
    			weekestPlayer = curOtherPlayer;
    			weakestPlayerScore = currWeaknessScore;
    			thePropertyWeakestOwnerOwnsThatIWant = othersProperty;
    		}
    	}
    	return thePropertyWeakestOwnerOwnsThatIWant;
    }
    
    /**
     * generateWeaknessScore(player): This function will generate
     * a weakness score for a player, the lower the number generated
     * the weaker the player is. Currently it just returns how much cash
     * the player has, because that is kinda a signal for how weak a player is
     * is if their cash is low
     * @param player (Player): the player we are generating a weakness score for 
     * @return int: the players generated weakness score, lower the weaker
     */
    private int generateWeaknessScore(Player player) {
    	return player.getCashAmmt();
    }
    
    /**
     * generateRankingMap(otherPropertiesThatHelpMe): This function will create a map the number of 
     * properties Id need to purchase in this region to get a monpoly. The goal is to 
     * use this map to find a set of properties that are good for us to attempt to trade for 
     * @param otherPropertiesThatHelpMe (List<Property>): A map of all properties that would help me 
     * 														get a monopoly in some way
     * @return Map<Integer, List<Property>>: A mapping of the number of properties Id need to buy of this color type mapped to the 
     * 											list of properties that hold that value (the amount I need to buy of that type to get a monopoly)
     */
    public Map<Integer, List<Property>> generateRankingMap(List<Property> otherPropertiesThatHelpMe) {
    	Map<Integer, List<Property>> numOfPurchasesToGetMonopoly = new HashMap<>();
    	
    	// go over every othersProperty, then findout how many I already own of that type
    	for (Property othersProperty: otherPropertiesThatHelpMe) {
    		// determine how many of this property color/type I already have, then the inverse of that to get a monopoly is the amount Id need to buy to get monopoly
    		int amtToBuyToGetMonopoly = -1;
    		
    		if (othersProperty instanceof Railroad) {
    			int railroadCount = 0;
    			// find out how many railroads I have 
    			for (Property myProperty: this.getListOfProperties()){
    				if (myProperty instanceof Railroad) {
    					railroadCount++;
    				}
    			}
    			amtToBuyToGetMonopoly = 4 - railroadCount;
    		}
    		// count the matching colors I personally have then compute how many other of that same type id have to buy to get monopoly 
    		if (othersProperty instanceof RealEstate) {
    			RealEstate.Color othersColor = ((RealEstate) othersProperty).getColor();
    			
    			
    			if (othersColor.equals(RealEstate.Color.BLUE) || othersColor.equals(RealEstate.Color.BROWN)) {
    				// count the number of matching colors I have, then the inverse is only 2 - `myCount`
    				int twoMaxColorCount = 0; // the number of blues (or browns) i have
    				for (Property myProperty: this.getListOfProperties()) {
    					if (myProperty instanceof RealEstate && ((RealEstate) myProperty).getColor().equals(othersColor)) {
    						twoMaxColorCount++;
    					}
    				}
    				amtToBuyToGetMonopoly = 2 - twoMaxColorCount;
    			}
    			else {
    				// count the number of matching colors I have, then the inverse is 3 - `myCount`
    				int matchingColorCount = 0; // the number of blues (or browns) i have
    				for (Property myProperty: this.getListOfProperties()) {
    					if (myProperty instanceof RealEstate && ((RealEstate) myProperty).getColor().equals(othersColor)) {
    						matchingColorCount++;
    					}
    				}
    				amtToBuyToGetMonopoly = 3 - matchingColorCount;
    			}
    		}
    		// amtToBuyToGetMonopoly is now calculated!!
    		if (!numOfPurchasesToGetMonopoly.containsKey(amtToBuyToGetMonopoly)) {
    			numOfPurchasesToGetMonopoly.put(amtToBuyToGetMonopoly, new ArrayList<>());
    		}
    		List<Property> propertiesThatMatchRank = numOfPurchasesToGetMonopoly.get(amtToBuyToGetMonopoly);
    		propertiesThatMatchRank.add(othersProperty);
    	}
    	return numOfPurchasesToGetMonopoly;
    }
    
    /**
     * getTheOtherOwnedPropertiesThatCanHelpMe(opportuneProperties): This function will go over each opportune property
     * take its color, then try to find other players that have that same color of properties. Then it will
     * add those properties that help me get a monopoly to a resulting list to return later
     * This function searches for players I can trade with for a specific property that will
     * help me out. 
     * @return List<Property>: A list of other properties that would help me get a monopoly
     */ 
    public List<Property> getTheOtherOwnedPropertiesThatCanHelpMe(List<Property> opportuneProperties){
    	List<Property> otherPropertiesThatHelpMe = new ArrayList<>();
    	
    	Set<RealEstate.Color> setOfAlreadyAddedColors = new HashSet<>();
    	boolean alreadyAddedRailroad = false;
    	// remember there could be duplicate colors in the opportuneProperties
    	for (Property myProperty : opportuneProperties) {
    		// if I have a railroad and I havent searched for others, then search
    		if (myProperty instanceof Railroad && alreadyAddedRailroad == false) {
    			List<Property> listOfAllOwnedRailroads = getListOfAllOwnedRailroads(this.getModel());
    			// add all of these unowned-by-me trains to the mapping of properties that could help me 
    			for (Property othersProperty: listOfAllOwnedRailroads) {
    				otherPropertiesThatHelpMe.add(othersProperty);
    			}
    			alreadyAddedRailroad = true;
    		}
    		// if its an instance of a RealEstate
    		else if (myProperty instanceof RealEstate) {
    			RealEstate myRealEstate = (RealEstate) myProperty;
    			RealEstate.Color myRealEstateColor = myRealEstate.getColor();
    			if (! setOfAlreadyAddedColors.contains(myRealEstateColor)) {
    				
    				List<Property> listOfAllMatchingColorRealEstate = getListOfAllMatchingColorRealEstate(this.getModel(), myRealEstate.getColor());
        			for (Property othersProperty: listOfAllMatchingColorRealEstate) {
        				otherPropertiesThatHelpMe.add(othersProperty);
        			}
        			// make sure if another one of our prperties is a pink, we dont search for pinks again
        			setOfAlreadyAddedColors.add(myRealEstateColor);
    			}
    		}
    	}
    	return otherPropertiesThatHelpMe;
    }
    
    /**
     * getListOfAllMatchingColorRealEstate(model, myRealEstateColor): This function will find
     * the other players who own a real estate that matches the color given. Then it will add that 
     * to the resulting list and return that list.
     * @param model (Model): The model is used to get the list of players
     * @param myRealEstateColor (RealEstate.Color): The color of the properties we are looking for
     * @return List<Property>: A list of other players properties that match the color of our own property 
     */
    public List<Property> getListOfAllMatchingColorRealEstate(Model model, RealEstate.Color myRealEstateColor){
    	List<Property> listOfAllOwnedMatchingColorRealEstate = new ArrayList<>();
    	
    	for (Player player : model.getPlayers()) {
    		// make sure player isnt us
    		if (player == this) continue;
    		
    		// search this players properties for real estate that match color given
    		for (Property othersProperty : player.getListOfProperties()) {
    			if (othersProperty instanceof RealEstate) {
    				RealEstate.Color othersRealEstateColor = ((RealEstate) othersProperty).getColor();
    				if (othersRealEstateColor.equals(myRealEstateColor)) {
    					listOfAllOwnedMatchingColorRealEstate.add(othersProperty);
    				}
    			}
    		}
    	}
    	return listOfAllOwnedMatchingColorRealEstate;
    }
    
    /**
     * getListOfAllOwnedRailroads(model): This function finds the players who 
     * own a railroad then adds that railroad to the list to be returned 
     * @param model (Model): The model of the entire game, so we can get the list
     * 						of all other players
     * 
     * @return List<Property>: All the owned-by-others railroads
     */
    public List<Property> getListOfAllOwnedRailroads(Model model){
    	List<Property> listOfAllOwnedRailroads = new ArrayList<>();
    	for (Player player : model.getPlayers()) {
    		// make sure player isnt us 
    		if (player == this) continue;
    		
    		// search for if this player has a railroad
    		for (Property property : player.getListOfProperties()) {
    			// if this player has a railroad, andd then and move onto next player
    			if (property instanceof Railroad) {
    				listOfAllOwnedRailroads.add(property);
    			}
    		}
    	}
    	return listOfAllOwnedRailroads;
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