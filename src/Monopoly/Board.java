package Monopoly;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import Spaces.Chance;
import Spaces.CommunityChest;
import Spaces.FreeParking;
import Spaces.GoSpace;
import Spaces.GoToJailSpace;
import Spaces.Jail;
import Spaces.Railroad;
import Spaces.RealEstate;
import Spaces.Space;
import Spaces.TaxSpace;
import Spaces.Utility;
import Spaces.RealEstate.Color;

//11x11 board
public class Board implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int totalSpaces = 40;	
	private int boardWidth = 11;	
	
	private LinkedList<Space> spacesLinkedList;
	public Space firstSpace;
	public Jail jailSpace;
    private FreeParking parking;
	private Model model;
    
	private int[] utilityRentStages = new int[] {4, 10};
	private int[] railRoadRentStages = new int[] {25, 50, 100, 200};
	
    public Board(Model model) {
        spacesLinkedList = new LinkedList<>(); 
        jailSpace = new Jail("inJailVisiting.png");
        parking = new FreeParking("freeParking.png");
        
        
        //building full board
        Space[] spacesArray = {};
        if(model.getTheme().equals("standard")) {
	        Space[] standardArray = {
	            new GoSpace("go.png"),																								//1
	            new RealEstate(Color.BROWN, "Mediterranean Avenue", 60, new int[]{2,4,10,30,90,160,250}),					//2
	            new CommunityChest("communityChestIcon.png"),																						//3
	            new RealEstate(Color.BROWN, "Baltic Avenue", 60, new int[]{4,8,20,60,180,320,450}),							//4
	            new TaxSpace(TaxSpace.TaxSpaceType.INCOME),																	//5
	            new Railroad("Reading Railroad", railRoadRentStages, "railroad.png"),									
	            new RealEstate(Color.LIGHTBLUE, "Oriental Avenue", 100, new int[]{6,12,30,90,270,400,550}),
	            new Chance("chanceIcon.png"),
	            new RealEstate(Color.LIGHTBLUE, "Vermont Avenue", 100, new int[]{6,12,30,90,270,400,550}),
	            new RealEstate(Color.LIGHTBLUE, "Connecticut Avenue", 120, new int[]{8,16,40,100,300,450,600}),				//10
	            jailSpace,
	            new RealEstate(Color.PINK, "St. Charles Place", 140, new int[]{10,20,50,150,450,625,750}),
	            new Utility("Electric Company", utilityRentStages, "electricCompany.png"),
	            new RealEstate(Color.PINK, "States Avenue", 140, new int[]{10,20,50,150,450,625,750}),
	            new RealEstate(Color.PINK, "Virginia Avenue", 160, new int[]{12,24,60,180,500,700,900}),						//15
	            new Railroad("Pennsylvania Railroad", railRoadRentStages, "railroad.png"),									
	            new RealEstate(Color.ORANGE,"St. James Place", 180, new int[]{14,28,70,200,550,750,950}),
	            new CommunityChest("communityChestIcon.png"),
	            new RealEstate(Color.ORANGE, "Tennessee Avenue", 180, new int[]{14,28,70,200,550,750,950}),
	            new RealEstate(Color.ORANGE, "New York Avenue", 200, new int[]{16,32,80,220,600,800,1000}),					//20
	            parking,
	            new RealEstate(Color.RED, "Kentucky Avenue", 220, new int[]{16,32,80,220,600,800,1000}),
	            new Chance("chanceIcon.png"),
	            new RealEstate(Color.RED, "Indiana Avenue", 220, new int[]{18,36,90,250,700,875,1050}),
	            new RealEstate(Color.RED, "Illinois Avenue", 240, new int[]{20,40,100,300,750,925,1100}),						//25
	            new Railroad("B. & O. Railroad", railRoadRentStages, "railroad.png"),
	            new RealEstate(Color.YELLOW, "Atlantic Avenue", 260, new int[]{22,44,110,330,800,975,1150}),
	            new RealEstate(Color.YELLOW, "Ventnor Avenue", 260, new int[]{22,44,110,330,800,975,1150}),
	            new Utility("Water Works", utilityRentStages, "waterWorks.png"),
	            new RealEstate(Color.YELLOW, "Marvin Gardens", 280, new int[]{24,48,120,360,850,1025,1200}),					//30
	            new GoToJailSpace(jailSpace,"goToJail.png"),
	            new RealEstate(Color.GREEN, "Pacific Avenue", 300, new int[]{26,52,130,390,900,1100,1275}),
	            new RealEstate(Color.GREEN, "North Carolina Avenue", 300, new int[]{26,52,130,390,900,1100,1275}),
	            new CommunityChest("communityChestIcon.png"),
	            new RealEstate(Color.GREEN, "Pennsylvania Avenue", 320, new int[]{28,56,150,450,1000,1200,1400}),					//35
	            new Railroad("Short Line", railRoadRentStages, "railroad.png"),
	            new Chance("chanceIcon.png"),
	            new RealEstate(Color.BLUE, "Park Place", 350, new int[]{35,70,175,500,1100,1300,1500}),
	            new TaxSpace(TaxSpace.TaxSpaceType.LUXURY),
	            new RealEstate(Color.BLUE, "Board Walk", 400, new int[]{50,100,200,600,1400,1700,2000}),						//40
	        };
	        spacesArray = standardArray;
        }
        
        if(model.getTheme().equals("pirateTheme")) {
	        Space[] pirateArray = {
	            new GoSpace("go.png"),																								//1
	            new RealEstate(Color.BROWN, "Shipwreck Cove", 60, new int[]{2,4,10,30,90,160,250}),					//2
	            new CommunityChest("communityChestIcon.png"),																						//3
	            new RealEstate(Color.BROWN, "Tortuga", 60, new int[]{4,8,20,60,180,320,450}),							//4
	            new TaxSpace(TaxSpace.TaxSpaceType.INCOME),																	//5
	            new Railroad("Black Pearl", railRoadRentStages, "railroad.png"),									
	            new RealEstate(Color.LIGHTBLUE, "Port Singapore", 100, new int[]{6,12,30,90,270,400,550}),
	            new Chance("chanceIcon.png"),
	            new RealEstate(Color.LIGHTBLUE, "Hong Kong Harbour", 100, new int[]{6,12,30,90,270,400,550}),
	            new RealEstate(Color.LIGHTBLUE, "Port of Nagoya", 120, new int[]{8,16,40,100,300,450,600}),				//10
	            jailSpace,
	            new RealEstate(Color.PINK, "St. Charles Port", 140, new int[]{10,20,50,150,450,625,750}),
	            new Utility("Spice Co.", utilityRentStages, "electricCompany.png"),
	            new RealEstate(Color.PINK, "East India Port", 140, new int[]{10,20,50,150,450,625,750}),
	            new RealEstate(Color.PINK, "Bistol Port", 160, new int[]{12,24,60,180,500,700,900}),						//15
	            new Railroad("The Flying Dutchman", railRoadRentStages, "railroad.png"),									
	            new RealEstate(Color.ORANGE,"St. James Port", 180, new int[]{14,28,70,200,550,750,950}),
	            new CommunityChest("communityChestIcon.png"),
	            new RealEstate(Color.ORANGE, "Cardiff Docks", 180, new int[]{14,28,70,200,550,750,950}),
	            new RealEstate(Color.ORANGE, "Bideford Harbor", 200, new int[]{16,32,80,220,600,800,1000}),					//20
	            parking,
	            new RealEstate(Color.RED, "Isla de Pelegostos", 220, new int[]{16,32,80,220,600,800,1000}),
	            new Chance("chanceIcon.png"),
	            new RealEstate(Color.RED, "Isla de Muerta", 220, new int[]{18,36,90,250,700,875,1050}),
	            new RealEstate(Color.RED, "Isla Cruces", 240, new int[]{20,40,100,300,750,925,1100}),						//25
	            new Railroad("HMS Interceptor", railRoadRentStages, "railroad.png"),
	            new RealEstate(Color.YELLOW, "Puerto Dorado", 260, new int[]{22,44,110,330,800,975,1150}),
	            new RealEstate(Color.YELLOW, "Puerto San Juan", 260, new int[]{22,44,110,330,800,975,1150}),
	            new Utility("Rum Dealer", utilityRentStages, "waterWorks.png"),
	            new RealEstate(Color.YELLOW, "Puerto Marseille", 280, new int[]{24,48,120,360,850,1025,1200}),					//30
	            new GoToJailSpace(jailSpace,"goToJail.png"),
	            new RealEstate(Color.GREEN, "South Sea Harbour", 300, new int[]{26,52,130,390,900,1100,1275}),
	            new RealEstate(Color.GREEN, "East Indian Port", 300, new int[]{26,52,130,390,900,1100,1275}),
	            new CommunityChest("communityChestIcon.png"),
	            new RealEstate(Color.GREEN, "Port of London", 320, new int[]{28,56,150,450,1000,1200,1400}),					//35
	            new Railroad("East India Galleon", railRoadRentStages, "railroad.png"),
	            new Chance("chanceIcon.png"),
	            new RealEstate(Color.BLUE, "Port Royal", 350, new int[]{35,70,175,500,1100,1300,1500}),
	            new TaxSpace(TaxSpace.TaxSpaceType.LUXURY),
	            new RealEstate(Color.BLUE, "Port Cornwall", 400, new int[]{50,100,200,600,1400,1700,2000}),						//40
	        };
	        spacesArray = pirateArray;
        }
        
        if(model.getTheme().equals("tucsonTheme")) {
        	Space[] tucsonArray = {
        		    new GoSpace("go.png"),                                                                                                //1
        		    new RealEstate(Color.BROWN, "Mesquite Camp", 60, new int[]{2,4,10,30,90,160,250}),             //2
        		    new CommunityChest("communityChestIcon.png"),                                                                         //3
        		    new RealEstate(Color.BROWN, "Adobe Homestead", 60, new int[]{4,8,20,60,180,320,450}),               //4
        		    new TaxSpace(TaxSpace.TaxSpaceType.INCOME),                                                                           //5
        		    new Railroad("Desert Iron Horse Line", railRoadRentStages, "railroad.png"),

        		    new RealEstate(Color.LIGHTBLUE, "Cactus Trail", 100, new int[]{6,12,30,90,270,400,550}),
        		    new Chance("chanceIcon.png"),
        		    new RealEstate(Color.LIGHTBLUE, "Rattlesnake Run", 100, new int[]{6,12,30,90,270,400,550}),
        		    new RealEstate(Color.LIGHTBLUE, "Dry Gulch Way", 120, new int[]{8,16,40,100,300,450,600}),            //10

        		    jailSpace,
        		    new RealEstate(Color.PINK, "Old Mission Trail", 140, new int[]{10,20,50,150,450,625,750}),
        		    new Utility("Frontier Electric Co.", utilityRentStages, "electricCompany.png"),
        		    new RealEstate(Color.PINK, "Bandit’s Pass", 140, new int[]{10,20,50,150,450,625,750}),
        		    new RealEstate(Color.PINK, "Gunslinger’s Ridge", 160, new int[]{12,24,60,180,500,700,900}),          //15

        		    new Railroad("Silver Spur Railroad", railRoadRentStages, "railroad.png"),
        		    new RealEstate(Color.ORANGE,"Saloon Row", 180, new int[]{14,28,70,200,550,750,950}),
        		    new CommunityChest("communityChestIcon.png"),
        		    new RealEstate(Color.ORANGE, "Whiskey Alley", 180, new int[]{14,28,70,200,550,750,950}),
        		    new RealEstate(Color.ORANGE, "Outlaw Crossing", 200, new int[]{16,32,80,220,600,800,1000}),          //20

        		    parking,
        		    new RealEstate(Color.RED, "Sheriff’s Corner", 220, new int[]{16,32,80,220,600,800,1000}),
        		    new Chance("chanceIcon.png"),
        		    new RealEstate(Color.RED, "Boot Hill", 220, new int[]{18,36,90,250,700,875,1050}),
        		    new RealEstate(Color.RED, "High Noon Street", 240, new int[]{20,40,100,300,750,925,1100}),           //25

        		    new Railroad("Stagecoach Line", railRoadRentStages, "railroad.png"),
        		    new RealEstate(Color.YELLOW, "Gold Dust Trail", 260, new int[]{22,44,110,330,800,975,1150}),
        		    new RealEstate(Color.YELLOW, "Sunset Canyon", 260, new int[]{22,44,110,330,800,975,1150}),
        		    new Utility("Desert Water Works", utilityRentStages, "waterWorks.png"),
        		    new RealEstate(Color.YELLOW, "Prospector’s Bend", 280, new int[]{24,48,120,360,850,1025,1200}),      //30

        		    new GoToJailSpace(jailSpace,"goToJail.png"),
        		    new RealEstate(Color.GREEN, "Longhorn Trail", 300, new int[]{26,52,130,390,900,1100,1275}),
        		    new RealEstate(Color.GREEN, "Dust Devil Road", 300, new int[]{26,52,130,390,900,1100,1275}),
        		    new CommunityChest("communityChestIcon.png"),
        		    new RealEstate(Color.GREEN, "Coyote Creek", 320, new int[]{28,56,150,450,1000,1200,1400}),           //35

        		    new Railroad("Prairie Express", railRoadRentStages, "railroad.png"),
        		    new Chance("chanceIcon.png"),
        		    new RealEstate(Color.BLUE, "Marshal’s Office", 350, new int[]{35,70,175,500,1100,1300,1500}),
        		    new TaxSpace(TaxSpace.TaxSpaceType.LUXURY),
        		    new RealEstate(Color.BLUE, "Old Tucson Town", 400, new int[]{50,100,200,600,1400,1700,2000}),        //40
        		};
        	spacesArray = tucsonArray;
        }

        //populate the linkedlist from the array
        for (int i = 0; i < spacesArray.length; i++) {
        	spacesLinkedList.add(spacesArray[i]);
        }
        

        // set .next for each space
        for (int i = 0; i < spacesLinkedList.size()-1; i++) {
        	spacesLinkedList.get(i).setNextSpace(spacesLinkedList.get((i + 1)));
        }
        firstSpace = spacesLinkedList.get(0); //class attribute firstSpace
        spacesLinkedList.get(spacesLinkedList.size()-1).setNextSpace(firstSpace); // connecting the last space to the first

 
    }

    /**
     * Grabs the free parking space
     * @return the free parking space object
     */
    public FreeParking getFreeParking() {
        return parking;
    }
	
    /**
     * Grabs the total amount of spaces in the board
     * @return an integer, the number of board spaces
     */
	public int getTotalSpaces() {
		return totalSpaces;
	}
	
    /**
     * Grabs the first space of the game
     * @return the object of the first space
     */
	public Space getFirstSpace() {
		return firstSpace;
	}
	
    /**
     * Grabs the jail space object
     * @return the jail space object
     */
	public Jail getJailSpace() {
		return jailSpace;
	}
	
    /**
     * Grabs the width of the board
     * @return an integer, the boards width
     */
	public int getBoardWidth() {
		return boardWidth;
	}
	
	/**
	 * getSpaces(): Returns a list of Space objects
	 * that make up the board
	 * @return List<Space>: All the spaces on the board
	 */
	public List<Space> getSpaces() {
	    return spacesLinkedList;
	}	
}
