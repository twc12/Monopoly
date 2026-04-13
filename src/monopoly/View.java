package monopoly;

import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;


/**
 * File: View.java Purpose: This class holds the java fx view of the class. it
 * will handel user input logic and pass results to the controller. It will
 * animate the dice. It will also show different views of the player
 * 
 * @author Alex Myers
 * @author Jake
 */
public class View extends Application {

	private Controller controller;
	private int widthOfPropertySpaceCards = 30;
	private int heightOfPropertySpaceCards = 60;
	private int heightOfColorOnSpaceCard = 15;

	@Override
	public void start(Stage stage) throws Exception {
		controller = new Controller();

		BorderPane pane = new BorderPane();

		// Set the monopoly title at the top
		Label label = new Label("MONOPOLY");
		label.setStyle("-fx-font-size: 30px; -fx-text-fill: darkslateblue; -fx-font-weight: bold;");
		pane.setTop(label);
		BorderPane.setAlignment(label, Pos.CENTER);

		// Create the board
		GridPane gridPane = buildMonopolyBoard();
		pane.setCenter(gridPane);

		// bottom playerinfo+controls
		HBox bottomArea = buildBottomSection();
		pane.setBottom(bottomArea);

		Scene scene = new Scene(pane, 800, 800);
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

<<<<<<< HEAD
		// FREE PARKING
		StackPane freeParkingStackPane = new StackPane();
		// The size and shape of normal size space
		Rectangle baseBottomRect = new Rectangle(heightOfPropertySpaceCards, heightOfPropertySpaceCards, Color.AQUA);
		Label freeParkingText = new Label("Free Parking");
		freeParkingText.setFont(new Font(10));
		freeParkingStackPane.getChildren().add(baseBottomRect);
		freeParkingStackPane.getChildren().add(freeParkingText);
		mainBoardGridPane.add(freeParkingStackPane, 0, 0);

		// GO SPACE
		StackPane goSpaceStackPane = new StackPane();
		// The size and shape of normal size space
		baseBottomRect = new Rectangle(heightOfPropertySpaceCards, heightOfPropertySpaceCards, Color.AQUA);
		Label goSpaceText = new Label("Go!");
		goSpaceText.setFont(new Font(10));
		goSpaceStackPane.getChildren().add(baseBottomRect);
		goSpaceStackPane.getChildren().add(goSpaceText);
		mainBoardGridPane.add(goSpaceStackPane, 10, 10);
		
		// JAIL SPACE
		StackPane jailStackPane = new StackPane();
		// The size and shape of normal size space
		baseBottomRect = new Rectangle(heightOfPropertySpaceCards, heightOfPropertySpaceCards, Color.AQUA);
		Label jailText = new Label("Jail: Just Visiting");
		jailText.setFont(new Font(7));
		jailStackPane.getChildren().add(baseBottomRect);
		jailStackPane.getChildren().add(jailText);
		mainBoardGridPane.add(jailStackPane, 0, 10);
		
		// Jail PARKING
		StackPane goToJailStackPane = new StackPane();
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

	private HBox buildBottomSection() {
		// Player info
		Label playerName = new Label("Player X");
		Label playerCash = new Label("$$$$$$");
		Label playerProps = new Label("PROPERTIES OWNED LIST");
		VBox playerCard = new VBox(playerName, playerCash, playerProps);
		playerCard.setMinWidth(300);

		// Buttons
		Button rollDiceButton = new Button("Roll Dice");
		rollDiceButton.setOnAction(event -> handleDiceRoll());

		Button tradeButton = new Button("Trade");
		Button mortgagePropertyButton = new Button("Mortgage");
		Button endTurnButton = new Button("End Turn");

		FlowPane buttonPane = new FlowPane();
		buttonPane.getChildren().addAll(rollDiceButton, tradeButton, mortgagePropertyButton, endTurnButton);

		// returning full bottom section
		HBox bottomBox = new HBox(playerCard, buttonPane); // player info card left, buttons right
		return bottomBox;
	}

	private void handleDiceRoll() {
		// controller.rollDice();
		return;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
