package monopoly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
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
	private int playerCircleRadius = 15;
	
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

	@Override
	public void start(Stage stage) throws Exception {
		whichStackPanesPlayersAreOn = new HashMap<Player, StackPane>();
		playerObjToPlayerPiece = new HashMap<Player, Circle>();
		
		controller = new Controller(this);
		

		BorderPane mainScreen = new BorderPane();

		// Set the monopoly title at the top
		VBox topLabelSection = new VBox(10);
		topLabelSection.setAlignment(Pos.CENTER);
		Label titleLabel = new Label("MONOPOLY");
		titleLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: darkslateblue; -fx-font-weight: bold;");
		BorderPane.setAlignment(topLabelSection, Pos.CENTER);
		Label infoToTellPlayer = new Label("");
		infoToTellPlayer.setFont(new Font(15));
		this.infoToTellPlayer = infoToTellPlayer;
		topLabelSection.getChildren().addAll(titleLabel, infoToTellPlayer);
		mainScreen.setTop(topLabelSection);
		

		// Create the board
		GridPane visualGameBoard = buildMonopolyBoard();
		
		
		// Go over all the Spaces panes, add them to list, to allow for easier movement when player moves
		StackPane goSpacePane = listOfSpacesPanes.get(0);
		
		List<Player> allPlayers = controller.getAllPlayers();
		Random rand = new Random(); // FUTURE REMOVAL - because players will chose their own pieces
		// FUTURAL REMOVAL (the circle part) - Go over each player and assign them a piece 
		for (Player currPlayer: allPlayers) {
			// Create a new circle for each player 
			playerObjToPlayerPiece.put(currPlayer, new Circle(0, 0, playerCircleRadius, Color.color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble())));
			
			// Move the circles into the GO SPACE
			goSpacePane.getChildren().add(playerObjToPlayerPiece.get(currPlayer));
			
			// initualize which pane the player is one 
			whichStackPanesPlayersAreOn.put(currPlayer, goSpacePane);
		}
		
		
		
		mainScreen.setCenter(visualGameBoard);

		// bottom playerinfo+controls
		BorderPane bottomArea = buildBottomSection();
		mainScreen.setBottom(bottomArea);

		Scene scene = new Scene(mainScreen, 800, 600);
		stage.setScene(scene);
		stage.setTitle("MONOPOLY");
		stage.show();

	}

	/**
	 * buildMonopolyBoard(): This function will craft the center monopoly board with
	 * all spaces.
	 * 
	 * @return GridPane: The one to set at the center of the screen
	 */
	public GridPane buildMonopolyBoard() {
		
		listOfSpacesPanes = new ArrayList<StackPane>();

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
					addPropertySpaceObjToBoard(mainBoardGridPane, allSpaces.get(spaceIdx), col, row, 0);
				}
			}

			// if on the left side (11-22 index) (excluding jail automatically because thats
			// in the first row 0-11)
			if (11 <= spaceIdx && spaceIdx <= 20) {
				// and the space is not a free parking
				if (!(currSpace instanceof FreeParking)) {
					int col = 0; // far left side
					int row = 9 - (spaceIdx - boardHeight); // gets us to right above jail; (12 - 11) + 9, then 13 - 11
					addPropertySpaceObjToBoard(mainBoardGridPane, allSpaces.get(spaceIdx), col, row, 90);
				}
			}

			// if on the TOP side (21-30 index) (excluding free parking automatically
			// because thats in the left column 12-22)
			if (21 <= spaceIdx && spaceIdx <= 30) {
				// and the space is not a free parking
				if (!(currSpace instanceof GoToJailSpace)) {
					int col = spaceIdx - 20; // 23 - 22; 24 - 22 gets the column of the top
					int row = 0; // top of the board
					addPropertySpaceObjToBoard(mainBoardGridPane, allSpaces.get(spaceIdx), col, row, 180);
				}
			}

			// if on the TOP side (31-40 index) (excluding goToJail space automatically
			// because thats in the TOP Row 21-30)
			if (31 <= spaceIdx && spaceIdx <= 40) {
				// and the space is not a Go Space
				if (!(currSpace instanceof GoSpace)) {
					int col = boardWidth - 1;
					int row = spaceIdx - 30;
					addPropertySpaceObjToBoard(mainBoardGridPane, allSpaces.get(spaceIdx), col, row, -90);
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
		listOfSpacesPanes.add(20, freeParkingStackPane); // this list is used for player movement later
		// The size and shape of normal size space
		Rectangle baseBottomRect = new Rectangle(heightOfPropertySpaceCards, heightOfPropertySpaceCards, Color.AQUA);
		Label freeParkingText = new Label("Free Parking");
		freeParkingText.setFont(new Font(10));
		freeParkingStackPane.getChildren().add(baseBottomRect);
		freeParkingStackPane.getChildren().add(freeParkingText);
		mainBoardGridPane.add(freeParkingStackPane, 0, 0);

		// GO SPACE
		StackPane goSpaceStackPane = new StackPane();
		listOfSpacesPanes.add(0, goSpaceStackPane); // this list is used for player movement later 
		// The size and shape of normal size space
		baseBottomRect = new Rectangle(heightOfPropertySpaceCards, heightOfPropertySpaceCards, Color.AQUA);
		Label goSpaceText = new Label("Go!");
		goSpaceText.setFont(new Font(10));
		goSpaceStackPane.getChildren().add(baseBottomRect);
		goSpaceStackPane.getChildren().add(goSpaceText);
		mainBoardGridPane.add(goSpaceStackPane, 10, 10);
		
		// JAIL SPACE
		StackPane jailStackPane = new StackPane();
		listOfSpacesPanes.add(10, jailStackPane); // this list is used for player movement later 
		// The size and shape of normal size space
		baseBottomRect = new Rectangle(heightOfPropertySpaceCards, heightOfPropertySpaceCards, Color.AQUA);
		Label jailText = new Label("Jail: Just Visiting");
		jailText.setFont(new Font(7));
		jailStackPane.getChildren().add(baseBottomRect);
		jailStackPane.getChildren().add(jailText);
		mainBoardGridPane.add(jailStackPane, 0, 10);
		
		// GO TO JAIL SPACE
		StackPane goToJailStackPane = new StackPane();
		listOfSpacesPanes.add(30, goToJailStackPane); // this list is used for player movement later
		// The size and shape of normal size space
		baseBottomRect = new Rectangle(heightOfPropertySpaceCards, heightOfPropertySpaceCards, Color.AQUA);
		Label goToJailText = new Label("GO TO JAIL LOSER");
		goToJailText.setFont(new Font(6));
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
	private void addPropertySpaceObjToBoard(GridPane mainBoardGridPane, Space space, int col, int row, int rotateAmmt) {
		// TESTING
		System.out.println("\nPutting Space: " + space.toString() + "in col=" + col + " row=" + row);
		// ^^ TESTING

		// IN THE FUTURE WE WILL CALL A FUNCTION THAT RETURNS A STACK FRAME WHICH REPRESENTS THE SPACE CARD
		
		StackPane spaceCardPane = new StackPane();
		
		listOfSpacesPanes.add(spaceCardPane); // This list is used for player movement in the future

		// The size and shape of normal size space
		Rectangle baseBottomRect = new Rectangle(widthOfPropertySpaceCards, heightOfPropertySpaceCards, Color.BISQUE);
		spaceCardPane.getChildren().add(baseBottomRect);

		// TEST The top color of properties
		Rectangle topColorBandRect = new Rectangle(widthOfPropertySpaceCards, heightOfColorOnSpaceCard,
				space.getFXColor());
		topColorBandRect.setTranslateY(-23);
		spaceCardPane.getChildren().add(topColorBandRect);

		// Rotate the
		spaceCardPane.getTransforms().add(new Rotate(rotateAmmt, 33, 9));
		spaceCardPane.setStyle("-fx-border-style: solid; -fx-border-width: 1; -fx-border-color: black");

		// This is required to keep the rotation effect of the rectangles
		Group spaceCardGroup = new Group(spaceCardPane);

		mainBoardGridPane.add(spaceCardGroup, col, row);
	}

	/**
	 * This creates and returns the boardpane for the bottom section 
	 * @return
	 */
	private BorderPane buildBottomSection() {
		
		// Player info
		Group bottomLeftPlayerCardGroup = new Group();
		this.bottomLeftPlayerCardGroup = bottomLeftPlayerCardGroup;
		Player currPlayer = controller.getCurrentPlayer();
		Node playerCard = createBottomLeftPlayerCard(currPlayer);
		bottomLeftPlayerCardGroup.getChildren().add(playerCard);
		
		
		
		// Middle dice roll area
		Group bottomMiddleOfScreenGroup = new Group();
		this.bottomMiddleOfScreenGroup = bottomMiddleOfScreenGroup;

		// Buttons
		Button rollDiceButton = new Button("Roll Dice");
		this.rollDiceButton = rollDiceButton;
		rollDiceButton.setOnAction(event -> handleDiceRoll());

		Button tradeButton = new Button("Trade");
		Button mortgagePropertyButton = new Button("Mortgage");
		Button endTurnButton = new Button("End Turn");
		this.endTurnButton = endTurnButton;
		endTurnButton.setDisable(true); //initially will be disabled until dice roll
		endTurnButton.setOnAction((event) -> {
			handleEndTurnButton();
		});

		FlowPane buttonPane = new FlowPane();
		buttonPane.getChildren().addAll(rollDiceButton, tradeButton, mortgagePropertyButton, endTurnButton);

		// returning full bottom section
		BorderPane bottomBorderPane = new BorderPane(); // player info card left, buttons right
		bottomBorderPane.setLeft(bottomLeftPlayerCardGroup);
		bottomBorderPane.setCenter(bottomMiddleOfScreenGroup);
		bottomBorderPane.setRight(buttonPane);
		return bottomBorderPane;
	}
	
	/**
	 * createBottomLeftPlayerCard(currPlayer): This function will
	 * create the info shown in the bottom left. It will have the players 
	 * name (id), the amount of money the player has, and the properties owned by the player
	 * 
	 * Parameters:
	 * 	currPlayer (Player): The new player that will have their info shown 
	 * 
	 * Returns:
	 * 	Node: The Java fx pane that will be added to the bottom left
	 */
	private Node createBottomLeftPlayerCard(Player currPlayer) {
		Label playerName = new Label("Player Id:"+currPlayer.getId());
		Label playerCash = new Label("$"+currPlayer.getCashAmmt());
		Label playerProps = new Label("PROPERTIES OWNED LIST");
		VBox playerCard = new VBox(playerName, playerCash, playerProps);
		playerCard.setMinWidth(300);
		return playerCard;
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
		Node playerCard = createBottomLeftPlayerCard(theNextPlayer);
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
		}
		
		else if (message instanceof PurchasePromptMessage) {
			PurchasePromptMessage purchasePromptMsg = (PurchasePromptMessage) message;
			Player player = purchasePromptMsg.getCurrentPlayer();
			Property property = purchasePromptMsg.getProperty();
			
			
			Alert a = new Alert(Alert.AlertType.ERROR);
			a.setTitle("FOR SALE");
			a.setHeaderText("Do you want to purchase " + property.getName() + "?");
			a.setContentText("YES/NO Cost: $" + property.getPurchaseAmount());
			a.showAndWait();
			
			//TODO add buttons, or prevent user from closing the show/wait prompt, maybe use another method
			
			//TODO if player clicks yes, execute the below. currently always assuming yes
			controller.executePropertySale(player, property);
		}
		
	}

}
