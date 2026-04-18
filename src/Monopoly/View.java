package Monopoly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import Cards.Card;
import Messages.AiActionMessage;
import Messages.CardDrawnMessage;
import Messages.DiceRollResultMessage;
import Messages.NextPlayerMessage;
import Messages.PlayerMovedMessage;
import Messages.PurchasePromptMessage;
import Spaces.Chance;
import Spaces.FreeParking;
import Spaces.GoSpace;
import Spaces.GoToJailSpace;
import Spaces.Jail;
import Spaces.Player;
import Spaces.Property;
import Spaces.Railroad;
import Spaces.RealEstate;
import Spaces.Space;
import Spaces.Utility;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;


/**
 * File: View.java Purpose: This class holds the java fx view of the class. it
 * will handle user input logic and pass results to the controller. It will
 * animate the dice. It will also show different views of the player
 * 
 * @author Alex Myers
 * @author Jake
 */
public class View extends Application implements Observer {

	private Controller controller;
	private int widthOfPropertySpaceCards = 30;
	private int heightOfPropertySpaceCards = 60;
	private int heightOfColorOnSpaceCard = 15;
	private int playerCircleRadius = 10;
	
	// PLAYER INFO CARD CONSTANTS
	private int widthOfPlayerCardProperties = 70;
	private int widthOfPlayerCardPropertiesScrollPane = 300;
	private int widthOfPlayerInfoCard = 320;
	private int heighOfPlayerInfoCard = 210;
	private String playerCardBackgroundColor = "lightblue";
	
	// Large detailed property info cards constants
	private int widthOfLargeDetailedPropertyCards = 200;
	
	// Player information picker on the right side constants
	private int widthOfRightSideRectangle = 100;
	private int heightOfRightSideRectangle = 50;
	private Group rightSidePlayerPickerGroup; // This holds the VBox of Player clickable rectangles to populate the center with their player card
	
	private final Color defaultSpaceColor = Color.WHITE;
	/**
	 * This group will change from holding a grid pane for dice results,
	 * and a stack pane to show a OTHER players card to do trading.
	 */
	private Group bottomMiddleOfScreenGroup; 
	
	/**
	 * This group will hold the player cards in the bottom left
	 * If you are to change the player card then you should 
	 * CLEAR THIS GROUPS CHILDREN and then add the new player card
	 */
	private Group bottomLeftPlayerCardGroup;
	
	private Button rollDiceButton; // used to grey out when unavailable 
	private Button endTurnButton; // used to grey out unavailable
	private Label currPlayerLabel;
	
	/**
	 * This is to tell the user any important messages
	 */
	private Label infoToTellPlayer;
	
	/**
	 * This is a list of Stack Panes. The stack panes are all of the 
	 * spaces on the outskirts of the board. Every space should be built
	 * with a stack pane. That way when I move a player I can just get the x and y 
	 * location of that pane and set the visual object for that player to that x and y 
	 */
	private List<StackPane> listOfSpacesPanes;
	/**
	 * I was struggling figuring out how to figure out where the current player is at 
	 * regarding the stack pane visual type of board. I needed to know where they are at so 
	 * I can start at the correct spot in the `listOfSpacesPanes` above. 
	 * 
	 * The goal here is for 1. the view to receive a PlayerMovedMessage 
	 * 2. Then we figure out where that player is on the board using this map
	 * 3. Then we find the index of that pane in the listOfSpacesPanes
	 * 4. Then we loop `while (currAmmtMoved < ammtToMove)` to move the player one space
	 * 		at a time using the Screen X Y values of the StackPane pulled from `listOfSpacesPanes`
	 */
	private Map<Player, StackPane> whichStackPanesPlayersAreOn;
	
	/**
	 * This holds a mapping of player objects to the visual player pieces
	 * This is used for moving, so when the view receives a PlayerMovedMessage
	 * we can get the object that should be translated for every move.
	 */
	private Map<Player, Circle> playerObjToPlayerPiece;
	
	/**
	 * This VBox will have messages added to it for every ai decision made.
	 * It will get populated in the `update()` function when we recieve a 
	 * `aiDecisionMessage` from the model 
	 */
	private VBox aiLoggerVBox;
	
	/**
	 * This Customizes the look of the ai logger text labels
	 * Hacker style text
	 */
	private String aiLoggerLabelSetStyle = "-fx-background-color: black; -fx-text-fill: green; -fx-padding: 5px;"; 
	
	/*
	 * These are used for displaying a card when the player lands on a chance card
	 */
	private Label cardLabel;
	private StackPane cardOverlay;
	private Label cardTitle;
	
	//similar to cards, for purchaseprompts
	private StackPane purchaseOverlay;
	
	/**
	 * This is used for displaying a properties card detailed information when a user clicks on it 
	 */
	private StackPane detailedCardInfoOverlay;
	
	private StackPane root;

	@Override
	public void start(Stage stage) throws Exception {
		
		whichStackPanesPlayersAreOn = new HashMap<Player, StackPane>();
		playerObjToPlayerPiece = new HashMap<Player, Circle>();
		
		controller = new Controller(this);
	
		BorderPane mainScreen = new BorderPane();
 
		// Set the monopoly title at the top
		VBox topLabelSection = createTopLabelSection();

		mainScreen.setTop(topLabelSection);

		// Create the board
		GridPane visualGameBoard = buildMonopolyBoard();
		
		
		// Put all the players pieces on the go space, then track which space the players are on 
		StackPane goSpacePane = listOfSpacesPanes.get(0);
		List<Player> allPlayers = controller.getAllPlayers();
		// FUTURAL REMOVAL (the circle part) - Go over each player and assign them a piece 
		for (Player currPlayer: allPlayers) {
			// Create a new circle for each player 
			playerObjToPlayerPiece.put(currPlayer, new Circle(0, 0, playerCircleRadius, currPlayer.getColor()));
			
			// Move the circles into the GO SPACE
			goSpacePane.getChildren().add(playerObjToPlayerPiece.get(currPlayer));
			
			// initualize which pane the player is on 
			whichStackPanesPlayersAreOn.put(currPlayer, goSpacePane);
		}

		mainScreen.setCenter(visualGameBoard);
		
		Group rightSidePlayerPickerGroup = new Group();	
		this.rightSidePlayerPickerGroup = rightSidePlayerPickerGroup;
		buildRightPlayerPicker(controller.getCurrentPlayer()); // THIS FUNCTION WILL ADD TO THE GROUP ONE LINE ABOVE!
		mainScreen.setRight(rightSidePlayerPickerGroup);
		
		// bottom playerinfo+controls
		BorderPane bottomArea = buildBottomSection();
		mainScreen.setBottom(bottomArea);

		
		// ADDED: root StackPane so overlay can sit above mainScreen
		root = new StackPane();
		root.getChildren().add(mainScreen);

		// For chance and community chests
		buildCardOverlay();
		root.getChildren().add(cardOverlay);
		
		// For purchase prompts
		StackPane purchaseOverlay = new StackPane();
		purchaseOverlay.setVisible(false);
		this.purchaseOverlay = purchaseOverlay;
		root.getChildren().add(purchaseOverlay);
		
		// Build the overlay for the detailed card info on mouse click 
		StackPane detailedCardInfoOverlay = new StackPane();
		this.detailedCardInfoOverlay = detailedCardInfoOverlay;
		detailedCardInfoOverlay.setVisible(false); // dont show anything, not until mouse click 
		root.getChildren().add(detailedCardInfoOverlay);
		
		// CHANGED TARGET ONLY: scene now uses root instead of mainScreen
		Scene scene = new Scene(root, 1280, 820);
		stage.setScene(scene);
		stage.setTitle("MONOPOLY");
		stage.show();
	}
	
	/**
	 * buildRightPlayerPicker(): This function will create the right side
	 * rectangles for selecting a player to see their properties and information
	 * Instead of returning it overwrites the VBox that is in the right side group 
	 * so it displays the current "other players" meaning it WONT show the `currPlayer` rectangle
	 * 
	 * @param currPlayer (Player): The current player in the game to not show their box 
	 */
	private void buildRightPlayerPicker(Player currPlayer) {
		
		VBox playersRectangleStack = new VBox();
		List<Player> allPlayers = controller.getAllPlayers();
		
		// loop over all the players, adding their rectangle to the right size with their color and Id 
		for (Player player : allPlayers) {
			Rectangle newRectangle;
			Label currPlayerLabel; 
			StackPane currPlayerStackPane;
			
			// for the current player, create a empty white box for them
			if (player.equals(currPlayer)) {
				newRectangle = new Rectangle(widthOfRightSideRectangle, heightOfRightSideRectangle, Color.GREY);
				currPlayerLabel = new Label("Current Player");
				currPlayerStackPane = new StackPane();
			}
			// if its not he current player then make the fully feature rich rectangle with color and rectangle
			else {
				newRectangle = new Rectangle(widthOfRightSideRectangle, heightOfRightSideRectangle, player.getColor());
				currPlayerLabel = new Label("Player "+player.getId());
				
				currPlayerStackPane = new StackPane();
				currPlayerStackPane.setUserData(player);
				currPlayerStackPane.setOnMouseClicked((e) -> {
					showOtherPlayersInfoInTheMiddle((Player)currPlayerStackPane.getUserData());
				});
			}
			
			currPlayerStackPane.getChildren().addAll(newRectangle, currPlayerLabel);
			playersRectangleStack.getChildren().add(currPlayerStackPane);
		}
		
		rightSidePlayerPickerGroup.getChildren().clear();
		rightSidePlayerPickerGroup.getChildren().add(playersRectangleStack);
	}
	
	
	/**
	 * showOtherPlayersInfoInTheMiddle(): This function will be called when the player card on the right side 
	 * is clicked. This function will place the selected other players info card in the middle of the screen to replace 
	 * the dice area temporally until roll dice happens again 
	 * 
	 * @param selectedOtherPlayer (Player): The object of the selected other player chosen to be shown
	 */
	private void showOtherPlayersInfoInTheMiddle(Player selectedOtherPlayer) {
		String otherPlayersColor = colorObjectToString(selectedOtherPlayer.getColor());
		Node othersInfoCard = createVisualPlayerInfoCard(selectedOtherPlayer, otherPlayersColor);
		bottomMiddleOfScreenGroup.getChildren().clear();
		bottomMiddleOfScreenGroup.getChildren().add(othersInfoCard);
	}
	
	
	/**
	 * This will create the top "MONOPOLY" Text, the user error info 
	 * and the Ai logger 
	 * @return VBox: IT holds top: "MOPOLY", middle: "User error info", bottom: VBox: Ai logger
	 */
	private VBox createTopLabelSection() {
		
		VBox topLabelSection = new VBox(10);
		topLabelSection.setAlignment(Pos.CENTER);
		
		// TOP MONOPOLY TEXT 
		Label titleLabel = new Label("MONOPOLY");
		titleLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: darkslateblue; -fx-font-weight: bold;");
		BorderPane.setAlignment(topLabelSection, Pos.CENTER);
		
		// CURR PLAYER LABEL
		Label currPlayerLabel = new Label("Player " + controller.getCurrentPlayer().getId() + "'s Turn");
		currPlayerLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: darkslateblue; -fx-font-weight: bold;");
		this.currPlayerLabel = currPlayerLabel;
		
		// PLAYER INFO LABEL for when there are errors
		Label infoToTellPlayer = new Label("");
		infoToTellPlayer.setFont(new Font(15));
		this.infoToTellPlayer = infoToTellPlayer; // we store it so in the future we can change the text to inform the user of something		
		
		// AI MOVEMENT/DECISION LOGGER
		VBox aiLoggerVBox = new VBox(5); // 5px of separation between the messages
		aiLoggerVBox.setAlignment(Pos.CENTER);
		this.aiLoggerVBox = aiLoggerVBox;
		
		topLabelSection.getChildren().addAll(titleLabel, currPlayerLabel, infoToTellPlayer, aiLoggerVBox);
		
		return topLabelSection;
	}

	/**
	 * buildMonopolyBoard(): This function will craft the center monopoly board with
	 * all spaces.
	 * 
	 * @return GridPane: The one to set at the center of the screen
	 */
	public GridPane buildMonopolyBoard() {
		
		//creating empty list of size 40
		listOfSpacesPanes = new ArrayList<StackPane>();
		for(int i = 0; i < controller.getSpaces().size(); i++) {
	        listOfSpacesPanes.add(null);
	    }
		
		
		GridPane mainBoardGridPane = new GridPane();

		// TESTING --
		mainBoardGridPane.setGridLinesVisible(true);
		// ^^^ TESTING

		int boardWidth = controller.getBoardWidth();
		int boardHeight = boardWidth;
		List<Space> allSpaces = controller.getSpaces();

		mainBoardGridPane.setAlignment(Pos.CENTER);
		mainBoardGridPane.setPadding(new Insets(8, 8, 8, 8));

		placeAllPropertySpaces(mainBoardGridPane, allSpaces, boardWidth, boardHeight);

		placeGoParkingAndJails(mainBoardGridPane, allSpaces);

		return mainBoardGridPane;

	}

	/**
	 * placeAllPropertySpaces(mainBoardGridPane, allSpaces, boardWidth,
	 * boardHeight): This function will loop over all the spaces cards and add them
	 * to the main board grid pane in their corrisponding orientation and such
	 * 
	 * @param mainBoardPane     (GridPane): The main visual grid pane in the middle
	 * @param allSpaces         (List<Spaces>): A list of all spaces on the board
	 * @param boardWidth        (int): The 11x11 dimension of the board
	 * @param boardHeight(int): The 11x11 dimension of the board
	 */
	private void placeAllPropertySpaces(GridPane mainBoardGridPane, List<Space> allSpaces, int boardWidth,
			int boardHeight) {

		for (int spaceIdx = 0; spaceIdx < allSpaces.size(); spaceIdx++) {
			// TESTING
			System.out.println("SpaceIdx=" + spaceIdx);

			Space currSpace = allSpaces.get(spaceIdx);

			// if on the bottom row
			if (0 <= spaceIdx && spaceIdx <= 10) {
				// and the space is not a go or a jail
				if (!(currSpace instanceof GoSpace) && !(currSpace instanceof Jail)) {
					int col = boardWidth - spaceIdx - 1;
					int row = boardWidth - 1; // 0 BASED INDEXING 11-1 = INDEX 10
					addPropertySpaceObjToBoard(mainBoardGridPane, allSpaces.get(spaceIdx), col, row, 0, spaceIdx);
				}
			}

			// if on the left side (11-22 index) (excluding jail automatically because thats
			// in the first row 0-11)
			if (11 <= spaceIdx && spaceIdx <= 20) {
				// and the space is not a free parking
				if (!(currSpace instanceof FreeParking)) {
					int col = 0; // far left side
					int row = 9 - (spaceIdx - boardHeight); // gets us to right above jail; (12 - 11) + 9, then 13 - 11
					addPropertySpaceObjToBoard(mainBoardGridPane, allSpaces.get(spaceIdx), col, row, 90, spaceIdx);
				}
			}

			// if on the TOP side (21-30 index) (excluding free parking automatically
			// because thats in the left column 12-22)
			if (21 <= spaceIdx && spaceIdx <= 30) {
				// and the space is not a free parking
				if (!(currSpace instanceof GoToJailSpace)) {
					int col = spaceIdx - 20; // 23 - 22; 24 - 22 gets the column of the top
					int row = 0; // top of the board
					addPropertySpaceObjToBoard(mainBoardGridPane, allSpaces.get(spaceIdx), col, row, 180, spaceIdx);
				}
			}

			// if on the TOP side (31-40 index) (excluding goToJail space automatically
			// because thats in the TOP Row 21-30)
			if (31 <= spaceIdx && spaceIdx <= 40) {
				// and the space is not a Go Space
				if (!(currSpace instanceof GoSpace)) {
					int col = boardWidth - 1;
					int row = spaceIdx - 30;
					addPropertySpaceObjToBoard(mainBoardGridPane, allSpaces.get(spaceIdx), col, row, -90, spaceIdx);
				}
			}
		}
	}

	/**
	 * placeGoParkingAndJails(mainBoardGridPane, allSpaces): This function will seek
	 * out the Go Space, Free Parking Space, and the two jail spaces, then use their
	 * space objects information to put on the main grid pane
	 * 
	 * @param mainBoardPane (GridPane): The main visual grid pane in the middle
	 * @param allSpaces     (List<Spaces>): A list of all spaces on the board
	 */
	private void placeGoParkingAndJails(GridPane mainBoardGridPane, List<Space> allSpaces) {

		FreeParking freeParkingSpaceObj = null;
		GoSpace goSpaceObj = null;
		Jail jailSpaceObj = null;
		GoToJailSpace goToJailSpaceObj = null;

		for (Space currSpace : allSpaces) {
			if (currSpace instanceof Jail)
				jailSpaceObj = (Jail) currSpace;
			else if (currSpace instanceof GoToJailSpace)
				goToJailSpaceObj = (GoToJailSpace) currSpace;
			else if (currSpace instanceof GoSpace)
				goSpaceObj = (GoSpace) currSpace;
			else if (currSpace instanceof FreeParking)
				freeParkingSpaceObj = (FreeParking) currSpace;
		}

		// CURRENTLY DOESNT USE OBJECT FOR ANYTHING
		// in the future we should pull the image from the space object

		
		// FREE PARKING
		StackPane freeParkingStackPane = new StackPane();
		listOfSpacesPanes.set(20, freeParkingStackPane); // this list is used for player movement later
		// The size and shape of normal size space
		Rectangle baseBottomRect = new Rectangle(heightOfPropertySpaceCards, heightOfPropertySpaceCards, Color.AQUA);
		Label freeParkingText = new Label("Free Parking");
		freeParkingText.setFont(new Font(10));
		freeParkingStackPane.getChildren().add(baseBottomRect);
		freeParkingStackPane.getChildren().add(freeParkingText);
		mainBoardGridPane.add(freeParkingStackPane, 0, 0);

		// GO SPACE
		StackPane goSpaceStackPane = new StackPane();
		listOfSpacesPanes.set(0, goSpaceStackPane); // this list is used for player movement later 
		// The size and shape of normal size space
		baseBottomRect = new Rectangle(heightOfPropertySpaceCards, heightOfPropertySpaceCards, Color.AQUA);
		Label goSpaceText = new Label("GO");
		goSpaceText.setFont(new Font(20));
		goSpaceStackPane.getChildren().add(baseBottomRect);
		goSpaceStackPane.getChildren().add(goSpaceText);
		mainBoardGridPane.add(goSpaceStackPane, 10, 10);
		
		// JAIL SPACE
		StackPane jailStackPane = new StackPane();
		listOfSpacesPanes.set(10, jailStackPane); // this list is used for player movement later 
		// The size and shape of normal size space
		baseBottomRect = new Rectangle(heightOfPropertySpaceCards, heightOfPropertySpaceCards, Color.AQUA);
		Label jailText = new Label("Jail/Just Visiting");
		jailText.setFont(new Font(8));
		jailStackPane.getChildren().add(baseBottomRect);
		jailStackPane.getChildren().add(jailText);
		mainBoardGridPane.add(jailStackPane, 0, 10);
		
		// GO TO JAIL SPACE
		StackPane goToJailStackPane = new StackPane();
		listOfSpacesPanes.set(30, goToJailStackPane); // this list is used for player movement later
		// The size and shape of normal size space
		baseBottomRect = new Rectangle(heightOfPropertySpaceCards, heightOfPropertySpaceCards, Color.AQUA);
		Label goToJailText = new Label("Go to Jail");
		goToJailText.setFont(new Font(13));
		goToJailStackPane.getChildren().add(baseBottomRect);
		goToJailStackPane.getChildren().add(goToJailText);
		mainBoardGridPane.add(goToJailStackPane, 10, 0);
	}

	/**
	 * addSpaceToGrid(gridPane, space, col, row): This function will be given a new
	 * space object and it will add the details of that space to the board. It will
	 * craft a rectangle and put all the info on that and then add that to the grid
	 * pane. Rotating it if necessary.
	 * 
	 * @param mainBoardGridPane (GridPane): The grid pane the board is made out of
	 * @param space             (Space): The space for the location to be added.
	 *                          Could be a Free Parking, or a Realestate, or a
	 *                          Chance card.
	 * @param col               (int): The column on the board. Remember a board is
	 *                          11x11 and only uses the outer edge
	 * @param row               (int): The row on the board. ^ ^^^ ^^^
	 * @param rotateAmmt        (int): Some spaces are rotated so this will be
	 *                          inputed based on the side of the board
	 */
	private void addPropertySpaceObjToBoard(GridPane mainBoardGridPane, Space space, int col, int row, int rotateAmmt, int spaceIdx) {
		// TESTING
		System.out.println("\nPutting Space: " + space.toString() + "in col=" + col + " row=" + row);
		// ^^ TESTING

		// IN THE FUTURE WE WILL CALL A FUNCTION THAT RETURNS A STACK FRAME WHICH REPRESENTS THE SPACE CARD
		
		StackPane spaceCardPane = new StackPane();
		
		listOfSpacesPanes.set(spaceIdx, spaceCardPane); // This list is used for player movement in the future

		// The size and shape of normal size space
		Rectangle baseBottomRect = new Rectangle(widthOfPropertySpaceCards, heightOfPropertySpaceCards, Color.BISQUE);
		spaceCardPane.getChildren().add(baseBottomRect);

		// TEST The top color of properties
		if (space instanceof RealEstate) {
		Rectangle topColorBandRect = new Rectangle(widthOfPropertySpaceCards, heightOfColorOnSpaceCard,
				((RealEstate)space).getFXColor());
		topColorBandRect.setTranslateY(-23); // -23 is to put match the corners perfectly
		spaceCardPane.getChildren().add(topColorBandRect);
		}

		// Rotate the
		spaceCardPane.getTransforms().add(new Rotate(rotateAmmt, 33, 9));
		spaceCardPane.setStyle("-fx-border-style: solid; -fx-border-width: 1; -fx-border-color: black");

		// This is required to keep the rotation effect of the rectangles
		Group spaceCardGroup = new Group(spaceCardPane);
		
		// FUTURE PROOOF CODE: this code should stay after that function above (line 448) is made because we need clickable stuff
		// Whenever this -- on the board -- property pane is clicked show the detailed stats
		spaceCardGroup.setUserData(space);
		spaceCardGroup.setOnMouseClicked((e) -> {
			showDetailedPropertyInfo((Property) spaceCardGroup.getUserData());
		});

		mainBoardGridPane.add(spaceCardGroup, col, row);
	}

	/**
	 * This creates and returns the boardpane for the bottom section 
	 * @return
	 */
	private BorderPane buildBottomSection() {
		
		// Player info group - This group is used to change the property card that is held in the bottom left
		Group bottomLeftPlayerCardGroup = new Group();
		this.bottomLeftPlayerCardGroup = bottomLeftPlayerCardGroup;  
		
		// Build player card to go in the bottom left
		Player currPlayer = controller.getCurrentPlayer();
		Node visualPlayerCard = createVisualPlayerInfoCard(currPlayer, playerCardBackgroundColor);
		
		bottomLeftPlayerCardGroup.getChildren().add(visualPlayerCard);
		
		
		// Middle dice roll area
		Group bottomMiddleOfScreenGroup = new Group();
		this.bottomMiddleOfScreenGroup = bottomMiddleOfScreenGroup;
		
		// This will put a gridpane object in the bottom center group, it will fill that group so when the dice get populated, its not a sudon jolt up 
		GridPane sudoEmptyGridPane = new GridPane();
		sudoEmptyGridPane.setPrefHeight(118);
		bottomMiddleOfScreenGroup.getChildren().add(sudoEmptyGridPane);
		
		

		// Buttons
		Button rollDiceButton = new Button("Roll Dice");
		this.rollDiceButton = rollDiceButton;
		rollDiceButton.setOnAction(event -> handleDiceRoll());

		Button tradeButton = new Button("Trade");
		Button buildButton = new Button("Build");
		Button endTurnButton = new Button("End Turn");
		this.endTurnButton = endTurnButton;
		endTurnButton.setDisable(true); //initially will be disabled until dice roll
		endTurnButton.setOnAction((event) -> {
			handleEndTurnButton();
		});

		FlowPane buttonPane = new FlowPane();
		buttonPane.getChildren().addAll(rollDiceButton, tradeButton, buildButton, endTurnButton);

		// returning full bottom section
		BorderPane bottomBorderPane = new BorderPane(); // player info card left, buttons right
		bottomBorderPane.setLeft(bottomLeftPlayerCardGroup);
		bottomBorderPane.setCenter(bottomMiddleOfScreenGroup);
		bottomBorderPane.setRight(buttonPane);
		return bottomBorderPane;
	}
	
	/**
	 * createVisualPlayerInfoCard(currPlayer): This function will
	 * create a visual player card, showing amount of money, player name
	 * a scroll pane of properties. This visual card is usually placed in the bottom 
	 * left of the screen to show the current players info 
	 * 
	 * @param currPlayer (Player): The new player that will have their info shown 
	 * 
	 * @return Node: The Java fx pane that will be a visual representation of a player card
	 */
	private Node createVisualPlayerInfoCard(Player currPlayer, String colorOfBackground) {
		GridPane visualPlayerCardGridPane = new GridPane(10, 10);
		visualPlayerCardGridPane.setPrefWidth(widthOfPlayerInfoCard);
		visualPlayerCardGridPane.setPrefHeight(heighOfPlayerInfoCard);
		String currPlayersColor = colorObjectToString(currPlayer.getColor());
		visualPlayerCardGridPane.setStyle("-fx-border-color: "+currPlayersColor+"; -fx-border-width: 2; -fx-padding: 5; -fx-background-color: "+colorOfBackground+";");
		
		
		Label playerName = new Label("Player Id: "+currPlayer.getId());
		playerName.setFont(Font.font("Roboto Mono", FontWeight.BOLD, 25));
		
		Label playerCash = new Label("$"+currPlayer.getCashAmmt());
		playerCash.setFont(Font.font("Roboto Mono", FontWeight.BOLD, 20));
		playerCash.setTextFill(Color.DARKGREEN);
		
		int seperationBetweenNameAndCash = 90;
		HBox playerNameAndCashHBox = new HBox(seperationBetweenNameAndCash);
		playerNameAndCashHBox.getChildren().addAll(playerName, playerCash);
		
		
		Label playerGetOutOfJailCardsAmmtLabel = new Label("Ammount of Get out of jail cards: "+currPlayer.getAmmtOfGOOJCards());
		
		Label playerPropsLabel = new Label("Properties Owned List");
		playerPropsLabel.setFont(Font.font("Roboto Mono", FontWeight.BOLD, 20));
		
		ScrollPane playersVisualProperties = createScrollPaneOfPlayersProperties(currPlayer);
		
		visualPlayerCardGridPane.add(playerNameAndCashHBox, 0, 0);
		visualPlayerCardGridPane.add(playerGetOutOfJailCardsAmmtLabel, 0, 1);
		visualPlayerCardGridPane.add(playerPropsLabel, 0, 2);
		visualPlayerCardGridPane.add(playersVisualProperties, 0, 3);
		
		
		return visualPlayerCardGridPane;
	}
	
	
	/**
	 * This is a helper fucntion to `Node createVisualPlayerInfoCard(currPlyaer)` because I needed the string 
	 * of a color name and I didnt want the large switch statement in that function 
	 * @param color (Color): Javafx color object
	 * @return String, the lowercase name of the color object
	 */
	private String colorObjectToString(Color color) {
		if (color.equals(Color.RED)) return "red";
		if (color.equals(Color.BLUE)) return "blue";
		if (color.equals(Color.GREEN)) return "green";
		if (color.equals(Color.YELLOW)) return "yellow";
		if (color.equals(Color.PURPLE)) return "purple";
		if (color.equals(Color.PINK)) return "pink";
		if (color.equals(Color.BLACK)) return "black";
		if (color.equals(Color.ORANGE)) return "orange";
		return "black"; // if no color here then black is fine 
		
	}
	
	/**
	 * createScrollPaneOfPlayersProperties(player): This function will create a visual
	 * java fx scroll pane that will be horizontal. It will show the players owned properties 
	 * with details on how much they get per house they own how much rent will they get.
	 * 
	 * @param player (Player): The player we are basing the owned properties of 
	 * @return ScrollPane: A horizontal scroll pane showing the properies of the player
	 */
	private ScrollPane createScrollPaneOfPlayersProperties(Player player) {
		int paddingAmmtAroundProperties = 10;
		List<Property> playersProperties = player.getListOfProperties();
		
		GridPane propertiesGridPane = new GridPane(10, 0); // holds the stack pane property info cards
		propertiesGridPane.setPadding(new Insets(paddingAmmtAroundProperties));
		
		// This sets the default height of a EMPTY properties scroll pane to be the same as if there were properties so there isnt a suddon jolt when a property is purchased
		propertiesGridPane.setMinHeight(widthOfPlayerCardProperties*1.6 + paddingAmmtAroundProperties); // the 1.6 is to convert width to height because of the `buildSpaceCard()` height rule
		
		int gridPaneIdx = 0; // this will move each property to to the next column in the grid pane
		
		// go over every property they have, create a visual stack pane for it, add it to the grid pane
		for (Property currProperty : playersProperties) {
			StackPane visualPropertyInfoCard = buildSpaceCard(currProperty, widthOfPlayerCardProperties);
			
			// Whenever this visual property stack pane is clicked show the detailed stats
			visualPropertyInfoCard.setUserData(currProperty);
			visualPropertyInfoCard.setOnMouseClicked((e) -> {
				showDetailedPropertyInfo((Property) visualPropertyInfoCard.getUserData());
			});
			// add this visual card to the underlying gridpane of the scroll pane
			propertiesGridPane.add(visualPropertyInfoCard, gridPaneIdx, 0);
			gridPaneIdx++;
		}
		ScrollPane scrollablePropertiesPane = new ScrollPane(propertiesGridPane);
		// Set the default size of the scroll pane
		scrollablePropertiesPane.setPrefWidth(widthOfPlayerCardPropertiesScrollPane);
		scrollablePropertiesPane.setMinHeight(widthOfPlayerCardProperties*1.6+20); // the 1.6 is from how the cards heigh is determined in `buildSpaceCard()` 
		scrollablePropertiesPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // dont show the scroll bars
		scrollablePropertiesPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		return scrollablePropertiesPane;
	}
	
	/**
	 * When the user presses "end turn" then let the controller know to move to the next 
	 * player
	 */
	private void handleEndTurnButton() {
		infoToTellPlayer.setText(""); // Clear the error info 
		controller.processEndTurn();
		endTurnButton.setDisable(true);
	}

	/**
	 * handleDiceRoll(): This function is called when the user presses
	 * the "Roll Dice" button
	 */
	private void handleDiceRoll() {

		infoToTellPlayer.setText(""); // Clear the error info on dice turn. 
		
		// TESTING
		System.out.println("handleDiceRol: called");
		
		Player currentPlayer = controller.getCurrentPlayer();
		
		// if the player is in jail then get the user input and process the user decision
		if (currentPlayer.isInJail() == true) {
			// Future issue ! controller.handleJailLogic();
			// MAYBE CHANGE THIS to when we get a message from the model its the next persons turn they are prompted already with the options for how to get out of jail
		}
		else{
			// TESTING
			System.out.println("handleDiceRol: calling controller.rollDice(currPlayer)");
			
			controller.rollDice(currentPlayer);
			// we will get a message back from the model with the resulting dice rolled
		}
		
		// if the player is done rolling dice then dont allow them to roll dice
		if (controller.getCurrentPlayer().getIsDoneRollingDice() == true) {
			// disable the roll dice button because they finished rolling
			rollDiceButton.setDisable(true);
			endTurnButton.setDisable(false);
		} else { //must have rolled doubles
			infoToTellPlayer.setText("Doubles! Roll again!"); // Clear the error info on dice turn. 
		}
		
		populatePlayerCardWithNewInfo(currentPlayer);  //refresh player card after dice-roll phase resolves
		
		return;
	}
	
	/**
	 * animateDiceRoll(): This function will show the dice result of a turn
	 * in the bottom center of the screen with two large dice.
	 * 
	 * Parameters:
	 * 	dice1Result (int): The result for the first dice 
	 * 	dice2Result (int): The result for the second dice
	 */
	private void animateDiceRoll(int dice1Result, int dice2Result) {
		// override the group in the bottom center of the screen to be available to show dice
		bottomMiddleOfScreenGroup.getChildren().clear();
		
		GridPane diceResultGridPane = new GridPane(20, 0);
		diceResultGridPane.setPadding(new Insets(20));
		BorderStroke borderStroke = new BorderStroke(
				Color.GREY,
				BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY,
				new BorderWidths(2)
			);
		diceResultGridPane.setBorder(new Border(borderStroke));
		
		bottomMiddleOfScreenGroup.getChildren().add(diceResultGridPane);
		
		diceResultGridPane.add(createADice(dice1Result), 0, 0);
		diceResultGridPane.add(createADice(dice2Result), 1, 0);
	}
	
	/**
	 * createADice(diceAmmt): This function will create a dice looking square using a 
	 * grid pane. It will have black circles in the grid and there will be `diceAmmt` of
	 * circles
	 * @param diceAmmt (int): The amount of dice requested to show on the dice
	 * @return GridPane: A 5x5 with the number of black dots being the `diceAmmt` given
	 */
	private GridPane createADice(int diceAmmt) {
		int horiontalAmmtBetweenDots = 10;
		int verticalAmmtBetweenDots = 10;
		int paddingAroundAllDots = 10;
		int diceCircleRadius = 5;
		
		BorderStroke borderStroke = new BorderStroke(
				Color.BLACK,
				BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY,
				new BorderWidths(2)
			);
		
		GridPane newDice = new GridPane(horiontalAmmtBetweenDots, verticalAmmtBetweenDots);
		newDice.setPadding(new Insets(paddingAroundAllDots));
		newDice.setBorder(new Border(borderStroke));
		
		// Record which slots on the dice got filled, so we can fill others with empty space
		Circle newDot;
		switch (diceAmmt) {
			case 1:
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 1, 1);
				
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 0, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 0, 1);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 0, 2);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 1, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 1, 2);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 2, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 2, 1);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 2, 2);
				break;
			case 2:
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 0, 2);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 2, 0);
				
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 0, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 0, 1);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 1, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 1, 1);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 1, 2);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 2, 1);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 2, 2);
				break;
			case 3:
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 0, 2);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 1, 1);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 2, 0);
				
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 0, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 0, 1);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 1, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 1, 2);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 2, 1);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 2, 2);
				break;
			case 4:
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 0, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 0, 2);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 2, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 2, 2);
				
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 0, 1);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 1, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 1, 1);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 1, 2);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 2, 1);
				break;
			case 5:
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 0, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 0, 2);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 2, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 2, 2);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 1, 1);
				
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 0, 1);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 1, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 1, 2);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 2, 1);
				break;
			case 6:
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 0, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 1, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 2, 0);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 0, 2);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 1, 2);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.BLACK), 2, 2);
				
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 0, 1);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 1, 1);
				newDice.add(new Circle(0, 0, diceCircleRadius, Color.TRANSPARENT), 2, 1);
				break;
		}
		
		return newDice;
				
	}
	
	/**
	 * animatePlayerMoving(player, ammtMoved): This function will 
	 * move a player `ammtMoved` number of spaces. This function 
	 * is called after a message is received from the model.
	 * 
	 * MAJOR PROBLEM ALERT!!!!
	 * I dont think we can "Animate" if we are not doing circle.translateX!!!
	 * Right now this function will just place the player piece into the next stack 
	 * frame by adding it as a child of that frame. To do actual animation we have to 
	 * do translates which Alex M couldnt figure out in time for the milestone 1.
	 * The problem he was facing was getting the location of the stack frame I wanted
	 * to move to and then translating a player piece to that x location. 
	 *  - WE CAN GET X,Y locations of the board space stack panes BUT I kept getting
	 *  	super high numbers and I couldnt debug the offset in time
	 * 
	 * @param player (Player): The player that moved
	 * @param ammtToMove (int): The amount they moved and should be moved
	 */
	public void animatePlayerMoving(Player player, int ammtToMove) {
		// TESTING
		System.out.println("view: animatePlayerMoving");
		
		// get the current pane the player is one, then use it to find the index in the list of all panes
		StackPane currentSpacesPane = whichStackPanesPlayersAreOn.get(player);
		if (currentSpacesPane == null) System.out.println("ERROR: Player object is not in hash map somehow");
		// Now find the index in the list of all the pains so we can loop nicely
		int indexOfCurrentSpacesPane = listOfSpacesPanes.indexOf(currentSpacesPane);
		
		Circle playersPeiceToMove = playerObjToPlayerPiece.get(player);
		
		// start our looping from this index, moving our player circle 
		int currMoved = 0;
		StackPane nextSpacePane = listOfSpacesPanes.get(indexOfCurrentSpacesPane);
		while (currMoved < ammtToMove) {
			
			nextSpacePane.getChildren().remove(playersPeiceToMove);
			
			// if the index is the last index then WRAP AROUND
			if (indexOfCurrentSpacesPane == listOfSpacesPanes.size()-1) {
				indexOfCurrentSpacesPane = 0;
			}
			else {
				indexOfCurrentSpacesPane++;
			}
			
			
			nextSpacePane = listOfSpacesPanes.get(indexOfCurrentSpacesPane);
			
			// And the players circle piece to the stack pane
			nextSpacePane.getChildren().add(playersPeiceToMove);
			
			
			
			
			currMoved++;
		}
		
		whichStackPanesPlayersAreOn.put(player, nextSpacePane);
		
	}
	
	
	/**
	 * populatePlayerCardWithNewInfo(theNextPlayer): This function is called
	 * after we receive a message from the model that its the next players turn.
	 * THis function will use the player object given to replace the info card on the 
	 * bottom left.
	 * @param theNextPlayer (Player): The player object of the next player 
	 */
	private void populatePlayerCardWithNewInfo(Player theNextPlayer) {
		Node playerCard = createVisualPlayerInfoCard(theNextPlayer, playerCardBackgroundColor);
		// REMEMBER TO CLEAN THE OLD PLAYING CARD
		bottomLeftPlayerCardGroup.getChildren().clear();
		bottomLeftPlayerCardGroup.getChildren().add(playerCard);
	}
	
	

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void update(Observable model, Object message) {
		// TESTING
		System.out.println("view: update: received '"+message+"' message");
		
		// if the message is a dice roll result, show the dice rolled
		if (message instanceof DiceRollResultMessage) {
			DiceRollResultMessage diceRollResult = (DiceRollResultMessage) message;
			animateDiceRoll(diceRollResult.getDice1Result(), diceRollResult.getDice2Result());
		}
		
		// if the message is that a player moved, animate the player moving on the board
		else if (message instanceof PlayerMovedMessage) {
			PlayerMovedMessage movedMessage = (PlayerMovedMessage) message;
			animatePlayerMoving(movedMessage.getPlayer(), movedMessage.getAmmtMoved());
		}
		
		// if the message is that its the next players turn, switch the bottom left player card to the new player
		else if (message instanceof NextPlayerMessage) {
			NextPlayerMessage nextPlayerMsg = (NextPlayerMessage) message;
			populatePlayerCardWithNewInfo(nextPlayerMsg.getNextPlayer());
			
			rollDiceButton.setDisable(false);
			endTurnButton.setDisable(false);
			
			// at the start of another player, clear the Ai logger of any potential messages
			aiLoggerVBox.getChildren().clear();

			currPlayerLabel.setText(("Player " + controller.getCurrentPlayer().getId() + "'s Turn")); 
			
			buildRightPlayerPicker(nextPlayerMsg.getNextPlayer());
		}
		
		// if the message is that a player landed on an unowned property, buying is optional
		else if (message instanceof PurchasePromptMessage) {
			PurchasePromptMessage purchasePromptMsg = (PurchasePromptMessage) message;
			Player player = purchasePromptMsg.getCurrentPlayer();
			Property property = purchasePromptMsg.getProperty();
			showPurchasePrompt(player, property);
		}
		
		// if the message is that a chance/chest card was drawn, show the card
		else if (message instanceof CardDrawnMessage) {
		    CardDrawnMessage msg = (CardDrawnMessage) message;
		    showCard(msg.getCard());
		}
		
		// if the message is that a Ai Took an action, display the action to the other players 
		else if (message instanceof AiActionMessage) {
			
			// IMPORTANT NOTE; We must pause between messages some how because the ai will kinda happen instantly, this is a problem.
			
			AiActionMessage aiActionMsg = (AiActionMessage) message;
			Label newAiActionLabel = new Label(aiActionMsg.getAiAction());
			newAiActionLabel.setStyle(aiLoggerLabelSetStyle); // make it have a specific theme for ai text
			//newAiActionLabel.setStyle("-fx-background-color: lightblue; -fx-padding: 10px;");
			aiLoggerVBox.getChildren().add(newAiActionLabel);
		}
		
		
		
	}
	
	/**
	 * Adds an Overlay to the board that can display a card.
	 * This will show the card right in the middle of the screen.
	 */
	private void buildCardOverlay() {
		// Sizing
		int width = 350;
		int height = 220;
		
	    StackPane overlay = new StackPane();
	    overlay.setVisible(false);
	    overlay.setStyle("-fx-background-color: rgba(0,0,0,0);");

	    VBox cardBox = new VBox();
	    cardBox.setAlignment(Pos.CENTER);
	
	    cardBox.setMaxWidth(width); 
	    cardBox.setMaxHeight(height);
	    
	    // Change style here
	    cardBox.setStyle(
	        "-fx-background-color: white;" +
	        "-fx-border-color: black;" +
	        "-fx-border-width: 3;" +
	        "-fx-padding: 20;"
	    );
	    
	    // Title label
	    cardTitle = new Label();
	    cardTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

	    // Card description
	    cardLabel = new Label();
	    cardLabel.setWrapText(true);
	    cardLabel.setTextAlignment(TextAlignment.CENTER);
	    cardLabel.setAlignment(Pos.CENTER);
	    cardLabel.setStyle("-fx-font-size: 16px;");

	    Label continueLabel = new Label("Click to continue");
	    continueLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

	    cardBox.getChildren().addAll(cardTitle, cardLabel, continueLabel);
	    overlay.getChildren().add(cardBox);


	    cardOverlay = overlay;
	}
	
	/**
	 * Used for displaying and updating the card Overlay when a card
	 * space is hit.
	 * 
	 * @param card
	 */
	public void showCard(Card card) {
		if (controller.getCurrentPlayer().getCurrentSpace() instanceof Chance) 
			cardTitle.setText("Chance");		
		else
			cardTitle.setText("Community Chest");
	    cardLabel.setText(card.getDescription());
	    cardOverlay.setVisible(true);
	    if (controller.getCurrentPlayer().getIsDoneRollingDice() == true) {
			// disable the roll dice button because they finished rolling
			rollDiceButton.setDisable(true);
			endTurnButton.setDisable(false);
	    }

	    cardOverlay.setOnMouseClicked(e -> {
	        cardOverlay.setVisible(false);
	        cardOverlay.setOnMouseClicked(null);
	        controller.resolveCard(card, controller.getCurrentPlayer());
	        populatePlayerCardWithNewInfo(controller.getCurrentPlayer()); // incase they get a get out of jail free card
	        e.consume();
	    });
	}

	//purchaseprompt hbox with property infocard on left, buttons on right
	public void showPurchasePrompt(Player player, Property property) {
		
	    this.purchaseOverlay.getChildren().clear();//clear old version
	    
	    //copying style from cardprompt
		int width = 350;
		int height = 220;
	    HBox hboxContainer = new HBox();
	    hboxContainer.setAlignment(Pos.CENTER);
	    hboxContainer.setMaxWidth(width); //avoid stackpane taking up whole screen
	    hboxContainer.setMaxHeight(height);
	    hboxContainer.setStyle(
	        "-fx-background-color: white;" +
	        "-fx-border-color: black;" +
	        "-fx-border-width: 3;" +
	        "-fx-padding: 20;"
	    );

	    //adding card to the left spot in the hbox
	    StackPane spaceCard = this.buildSpaceCard(property, 150);
	    hboxContainer.getChildren().add(spaceCard);

	    //adding buttons to the right spot in the hbox
	    VBox buttonBox = new VBox();
	    buttonBox.setAlignment(Pos.CENTER);
	    Button buyButton = new Button("Buy");
	    buyButton.setOnAction(e -> {		//On click, buy property, update playerinfo, hide overlay
	        controller.purchaseProperty(player, property);
	        populatePlayerCardWithNewInfo(player);
	        this.purchaseOverlay.setVisible(false);
	    });
	    Button skipButton = new Button("Skip"); //On click for skip, just hide overlay
	    skipButton.setOnAction(e -> {
	    	this.purchaseOverlay.setVisible(false);
	    });
	    buttonBox.getChildren().addAll(buyButton, skipButton);
	    hboxContainer.getChildren().add(buttonBox);

	    //add hbox to stackpane atribute
	    this.purchaseOverlay.getChildren().add(hboxContainer);
	    this.purchaseOverlay.setVisible(true);
	}
	
	/**
	 * showDetailedPropertyInfo(property): This function will display to the user a 
	 * larger version of the detailed property info which floats on the board until pressed out  
	 * @param property (Property): The property we want to show to the user 
	 */
	private void showDetailedPropertyInfo(Property property) {
		detailedCardInfoOverlay.getChildren().clear(); // remove any old detailed cards
		
		StackPane largeDetailedCardPane = buildSpaceCard(property, widthOfLargeDetailedPropertyCards);
		
		detailedCardInfoOverlay.getChildren().add(largeDetailedCardPane);
		
		detailedCardInfoOverlay.setVisible(true);
		detailedCardInfoOverlay.setOnMouseClicked((e) -> {
			detailedCardInfoOverlay.setVisible(false);
			detailedCardInfoOverlay.setOnMouseClicked(null);
		});
	}
	
	/**
	 * Builds a resizable stackframe for viewing property cards.
	 * used for tracking rent prices and general space info
	 * 
	 * @param space the space that the card is basing itself on
	 * @param cardWidth the width of the card to scale from
	 * @return StackPane the card image
	 */
	public StackPane buildSpaceCard(Property space, int cardWidth) {
		double cardHeight = cardWidth * 1.6;
	    double borderWidth = Math.max(1, cardWidth * 0.016);
	    double borderInset = cardWidth * 0.05;
	    double headerHeight = cardHeight * 0.20;
	    double titleDeedFont = cardWidth * 0.045;
	    double nameFont = cardWidth * 0.08;
	    double innerWidthOffset = borderInset * 2 + borderWidth * 2;

	    StackPane root = new StackPane();

	    VBox card = new VBox();
	    card.setAlignment(Pos.TOP_CENTER);
	    card.setPrefWidth(cardWidth);
	    card.setMaxWidth(cardWidth);
	    card.setPrefHeight(cardHeight);
	    
	    root.setMaxSize(cardWidth, cardHeight);

	    card.setStyle(
	        "-fx-background-color: white;" +
	        "-fx-border-color: black;" +
	        "-fx-border-width: " + borderWidth + ";" +
	        "-fx-border-insets: " + borderInset + ";"
	    );

	    List<String> lines = new ArrayList<>();

	    // Property Cards
	    if (space instanceof RealEstate) {
	    	StackPane headerBox = new StackPane();
	    	headerBox.setMaxWidth(Double.MAX_VALUE);
	    	headerBox.setPrefHeight(headerHeight);
	    	headerBox.setAlignment(Pos.CENTER);

	    	Rectangle colorRect = new Rectangle();
	    	colorRect.setHeight(headerHeight);
	    	
	    	//only realestate has colors
	    	if (space instanceof RealEstate) {
	    	colorRect.setFill(((RealEstate)space).getFXColor()); //cast to realestate obj so can get javafx color
	    	} else {
	    		colorRect.setFill(defaultSpaceColor);
	    	}
	    
	    	colorRect.widthProperty().bind(card.widthProperty().subtract(innerWidthOffset*.8));

	    	Text t1 = new Text("TITLE DEED\n");
	    	t1.setStyle("-fx-font-size: " + titleDeedFont + "px;");

	    	Text t2 = new Text(space.getName().toUpperCase());
	    	t2.setStyle("-fx-font-size: " + nameFont + "px; -fx-font-weight: bold;");

	    	TextFlow flow = new TextFlow(t1, t2);
	    	flow.setTextAlignment(TextAlignment.CENTER);
	    	flow.setMaxWidth(cardWidth - innerWidthOffset - 8);

	    	headerBox.getChildren().addAll(colorRect, flow);
	    	card.getChildren().add(headerBox);
		}
		
		if(space instanceof Railroad) {
			//TODO prices
		}
		
		if(space instanceof Utility) {
			//TODO prices
		}
		root.getChildren().add(card);
		return  root;
	}

}