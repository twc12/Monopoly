package Monopoly;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import Cards.Card;
import Messages.AiActionMessage;
import Messages.CardDrawnMessage;
import Messages.DiceRollResultMessage;
import Messages.GoToJailMessage;
import Messages.NextPlayerMessage;
import Messages.PlayerMovedMessage;
import Messages.PurchasePromptMessage;
import Monopoly.Controller.JAIL_CHOICE;
import Monopoly.GameSettings.Theme;
import Spaces.AIPlayer;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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
	private String theme;
	private String themeFolder;
	
	private int widthOfPropertySpaceCards = 40;
	private int heightOfPropertySpaceCards = 80;
	private int heightOfColorOnSpaceCard = 15;
	private int playerCircleRadius = 10;
	
	// BOTTOM ROW CONSTANTS
	/**
	 * This is the height of the bottom controll bar section
	 * Player info card - Dice ROll Section - Button Section - Other player section
	 */
	private int bottomHBoxHeight = 220;
	private int diceRollAreaWidth = 300;
	
	// PLAYER INFO CARD CONSTANTS
	private int widthOfPlayerCardProperties = 40;
	private int widthOfPlayerCardPropertiesScrollPane = 300;
	private int widthOfPlayerInfoCard = 320;
	private int heighOfPlayerInfoCard = 210;
	
	// Large detailed property info cards constants
	private int widthOfLargeDetailedPropertyCards = 200;
	
	// Player information picker on the right side constants
	private int widthOfRightSideRectangle = 100;
	private int heightOfRightSideRectangle = 50;
	private Group rightSidePlayerPickerGroup; // This holds the VBox of Player clickable rectangles to populate the center with their player card
	
	private final Color defaultSpaceColor = Color.WHITE;
	/**
	 * This stackPane will be holding a grid pane for dice results,
	 * hopefully In the future I can make the dice roll around in this pane
	 */
	private StackPane diceRollStackPane; 
	
	/**
	 * This stackPane will be holding a optional additional 
	 * player card info from another player in the bottom right 
	 */
	private StackPane otherPlayerInfoCardStackPane;
	/**
	 * This allows us to detect when the player selects another player again which 
	 * should remove the other optional additional card 
	 */
	private Player previousSelectedOtherPlayer = null; 
	
	/**
	 * This group will hold the player cards in the bottom left
	 * If you are to change the player card then you should 
	 * CLEAR THIS GROUPS CHILDREN and then add the new player card
	 */
	private Group bottomLeftPlayerCardGroup;
	
	// THE MAIN BUTTONS IN BOTTOM RIGHT 
	private Group mainButtonsGroup; // This will be used to change the buttons to present the jail options

	private VBox coreButtonsVBox; // This will save the core buttons so the jail logic can revert them back 
	private Node rollDiceButton; // used to grey out when unavailable 
	private Node endTurnButton; // used to grey out unavailable
	
	/**
	 * These are the sizes of the main buttons also the get out of jail buttons
	 */
	private int coreButtonWidth  = 150; 
	private int coreButtonHeight = 45;

	private StackPane buildButton; // disabled until a buildable RealEstate card is selected

	private Label currPlayerLabel;
	
	/** The RealEstate property selected in the player info panel for building. Null = none selected. */
	private RealEstate selectedPropertyToBuild = null;
	
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
	private ImageView cardIcon;
	
	//similar to cards, for purchaseprompts
	private StackPane purchaseOverlay;
	
	/**
	 * This is used for displaying a properties card detailed information when a user clicks on it 
	 */
	private StackPane detailedCardInfoOverlay;
	
	private StackPane root;
	
	/**
	 * This is used for moving the player to jail after they are sent to jail, it will
	 * hold the stack pane that jail is made out of 
	 */
	private StackPane jailSpaceStackPane;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		
		
		
		// --------------- START SCREEEN --------------- 
		GameSettings gameSettings = new GameSettings();
		BorderPane startScreenBorderPane = new BorderPane();
		
		
		// ---- SHOW THE RESULTS OF THE USER CLICKS ---- 
		VBox resultsOfInputVBox = new VBox(20); resultsOfInputVBox.setPadding(new Insets(10));
		resultsOfInputVBox.setMinWidth(280);
		startScreenBorderPane.setRight(resultsOfInputVBox);
		Label allowSkipPropPurchLabel = new Label("Allow skipping property purcahses: ✅");
		Label passingGoAmountLabel = new Label("Amount for passing go: $200 (default)");
		Label propPricesMultiLabel = new Label("Property Prices Multiplier: 1x (default)");
		Label freeParkCollectsFundsLabel = new Label("Free Parking Collects Funds: ❌");
		Label amtOfStartingMoneyLabel = new Label("Amt of money you start with: $1500 (default)");
		Label tradingWithOthersLabel = new Label("Trading with other players is enabled: ✅");
		Label numOfHumanPlayersLabel = new Label("Amt of Human Players: 4 (Default)");
		Label numOfAiPlayersLabel = new Label("Amt of Ai Players: 1 (Default)");
		resultsOfInputVBox.getChildren().addAll(allowSkipPropPurchLabel, passingGoAmountLabel, propPricesMultiLabel, freeParkCollectsFundsLabel, amtOfStartingMoneyLabel, tradingWithOthersLabel, numOfHumanPlayersLabel, numOfAiPlayersLabel);
		
		
		// ------ BUTTONS ----- 
		Label topStartScreenLabel = new Label("Start Screen");
		topStartScreenLabel.setFont(new Font(20));
		BorderPane.setAlignment(topStartScreenLabel, Pos.CENTER);
		startScreenBorderPane.setTop(topStartScreenLabel);
		
		// SKIPING PURCHASE PROPERTIES 
		ToggleButton allowSkippingPropertyPurchaseButton = new ToggleButton("Allow Skipping Property Purchases");
		allowSkippingPropertyPurchaseButton.setOnAction(event -> {
			if (gameSettings.getOptionalBuying() == true) {
				allowSkipPropPurchLabel.setText("Allow skipping property purcahses: ❌");
				gameSettings.setOptionalBuying(false);
			}
			else {
				allowSkipPropPurchLabel.setText("Allow skipping property purcahses: ✅");
				gameSettings.setOptionalBuying(true);
			}
		});
		// PASSING GO MONEY 
		TextField moneyPassingGoInput = new TextField();
		moneyPassingGoInput.setPromptText("Amt of money for passing go");
		moneyPassingGoInput.setMaxWidth(250);
		moneyPassingGoInput.setOnAction(event -> {
			String playersInput = moneyPassingGoInput.getText();
			if (playersInput.length() == 0 || playersInput.equals("200")) {
				gameSettings.setCustomGoValue(200);
				passingGoAmountLabel.setText("Amount for passing go: $200 (default)");
			}
			else {
				try {
					Integer customAmt = Integer.valueOf(playersInput);
					gameSettings.setCustomGoValue(customAmt);
					passingGoAmountLabel.setText("Amount for passing go: $"+customAmt);
					
				} catch (NumberFormatException e) {
					gameSettings.setCustomGoValue(200);
					passingGoAmountLabel.setText("Invalid Input, defaulting to $200");
				}
				
			}
		});
		
		// PRICE PER SPACE 
		ChoiceBox<String> pricePerSpaceMultiplyerInput = new ChoiceBox<>();
		pricePerSpaceMultiplyerInput.getItems().addAll("1 (default)", "2x (Meh)", "3x (Woozers)", "5x (Good luck)", "10 (Ultra Mega Extreme)");
		pricePerSpaceMultiplyerInput.setOnAction(event -> {
			String userSelction = pricePerSpaceMultiplyerInput.getValue();
			propPricesMultiLabel.setText("Property Prices Multiplier: "+userSelction);
			switch (userSelction) {
				case "1 (default)":
					gameSettings.setPropertyPriceAdjust(1);
				case "2x (Meh)":
					gameSettings.setPropertyPriceAdjust(2);
				case "3x (Woozers)":
					gameSettings.setPropertyPriceAdjust(3);
				case "5x (Good luck)":
					gameSettings.setPropertyPriceAdjust(5);
				case "10 (Ultra Mega Extreme)":
					gameSettings.setPropertyPriceAdjust(10);
			}
		});
		
		// ENABLE/DISABLE FREE PARKING REWARDS
		ToggleButton freeParkingRewardsButton = new ToggleButton("Free Parking Collects Funds");
		freeParkingRewardsButton.setOnAction(event -> {
			if (gameSettings.getFreeParkingRule() == true) {
				freeParkCollectsFundsLabel.setText("Free Parking Collects Funds: ❌");
				gameSettings.setFreeParkingRule(false);
			}
			else {
				freeParkCollectsFundsLabel.setText("Free Parking Collects Funds: ✅");
				gameSettings.setFreeParkingRule(true);
			}
		});
		
		// AMOUNT OF STARTING MONEY
		TextField startingMoneyInput = new TextField();
		startingMoneyInput.setPromptText("Amt of money you start with");
		startingMoneyInput.setMaxWidth(250);
		startingMoneyInput.setOnAction(event -> {
			String playersInput = startingMoneyInput.getText();
			if (playersInput.length() == 0 || playersInput.equals("1500")) {
				gameSettings.setStartingMoney(1500);
				amtOfStartingMoneyLabel.setText("Amt of money you start with: $1500 (default)");
			}
			else {
				try {
					Integer customAmt = Integer.valueOf(playersInput);
					gameSettings.setStartingMoney(customAmt);
					amtOfStartingMoneyLabel.setText("Amt of money you start with: $"+customAmt);
					
				} catch (NumberFormatException e) {
					gameSettings.setStartingMoney(1500);
					amtOfStartingMoneyLabel.setText("Invalid Input, defaulting to $1500");
				}
				
			}
		});
		
		// ENABLE/DISABLE TRADING
		ToggleButton tradingButton = new ToggleButton("Trading with players");
		tradingButton.setOnAction(event -> {
			if (gameSettings.getTradingEnabled() == true) {
				tradingWithOthersLabel.setText("Trading with other players is enabled: ❌");
				gameSettings.setTradingEnabled(false);
			}
			else {
				tradingWithOthersLabel.setText("Trading with other players is enabled: ✅");
				gameSettings.setTradingEnabled(true);
			}
		});
		
		// NUMBER OF PLAYERS
		ChoiceBox<Integer> numberOfAiPlayersChoiceBox = new ChoiceBox<>();; // Message to choice box below "IT WILL BE DEFINED LATER TRUST"
		
		ChoiceBox<Integer> numberOfHumanPlayersChoiceBox = new ChoiceBox<>(); 
		numberOfHumanPlayersChoiceBox.getItems().addAll(0,1,2,3,4,5,6,7,8);
		numberOfHumanPlayersChoiceBox.setValue(4);
		numberOfHumanPlayersChoiceBox.setOnAction(event -> {
			Integer numOfHumans = numberOfHumanPlayersChoiceBox.getValue();
			if (numOfHumans == null) // this is a fix to when I would change a value in the ai box it would activate this box when i didnt want to 
				return;
			if (numOfHumans == 0) {
				boolean numOfAisIsNullOrLessThan2 = numberOfAiPlayersChoiceBox.getValue() == null || numberOfAiPlayersChoiceBox.getValue() < 2;
				if (numOfAisIsNullOrLessThan2) {
					gameSettings.setAmountOfAIPlayers(2);
					numberOfAiPlayersChoiceBox.setValue(2);
					numOfAiPlayersLabel.setText("Amt of Ai Players: 2 (Automatic)");
					return;
				}
			}
			// if the user selected one then by default the atleast 1 ai is selected
			boolean numOfAisIsNullOrZero = numberOfAiPlayersChoiceBox.getValue() == null || numberOfAiPlayersChoiceBox.getValue() == 0;
			if (numOfHumans == 1 && numOfAisIsNullOrZero) { // only forcefully update the other box if it hasnt been picked or is set to zero
				gameSettings.setAmountOfAIPlayers(1);
				numberOfAiPlayersChoiceBox.setValue(1);
				numOfAiPlayersLabel.setText("Amt of Ai Players: 1 (Automatic)");
				return;
			}
			else if (numOfHumans == 4){
				numOfHumanPlayersLabel.setText("Amt of Human Players: 4 (default)");
			}
			else {				
				numOfHumanPlayersLabel.setText("Amt of Human Players: "+numOfHumans);
			}
			gameSettings.setAmountOfPlayers(numOfHumans);
			
			// Change the options for the number of Ai to max the max of 8
			Integer previousSelection = numberOfAiPlayersChoiceBox.getValue();
			numberOfAiPlayersChoiceBox.getItems().clear();
			// Allow the user to select the number of ai players that would fill at most the 8 total allowed, only show options for that
			for (int i=0; i<=(8-numOfHumans); i++) {
				numberOfAiPlayersChoiceBox.getItems().add(i);
			}
			// if the preivous selection was to large, set it to the most 
			if (previousSelection != null && previousSelection > (8-numOfHumans)) {
				numberOfAiPlayersChoiceBox.setValue(8-numOfHumans);
			}
			// otherwise the previous selection was fine and we keep that 
			else {
				numberOfAiPlayersChoiceBox.setValue(previousSelection);
			}
		}); // ---- END OF MASSIVE HUMAN CHOICE BOX LAMBADA -----
		
		// AI PLAYERS
		numberOfAiPlayersChoiceBox.getItems().addAll(0,1,2,3,4,5,6,7,8);
		numberOfAiPlayersChoiceBox.setValue(1);
		numberOfAiPlayersChoiceBox.setOnAction(event -> {
			Integer numOfAis = numberOfAiPlayersChoiceBox.getValue();
			if (numOfAis == null) // this is a fix to when I would change a value in the human box it would activate this box when i didnt want to 
				return;
			// if the user selects zero ais, then the other box must be 2 or more 
			if (numOfAis == 0) {
				boolean numOfHumansIsNullOrLessThan2 = numberOfHumanPlayersChoiceBox.getValue() == null || numberOfHumanPlayersChoiceBox.getValue() < 2;
				if (numOfHumansIsNullOrLessThan2) {
					gameSettings.setAmountOfPlayers(2);
					numberOfHumanPlayersChoiceBox.setValue(2);
					numOfHumanPlayersLabel.setText("Amt of Human Players: 2 (Automatic)");
					return;
				}
			}
			// if the user selected one ai then by default at least 1 human is selected
			boolean numOfHumansIsNullOrZero = numberOfHumanPlayersChoiceBox.getValue() == null || numberOfHumanPlayersChoiceBox.getValue() == 0;
			if (numOfAis == 1 && numOfHumansIsNullOrZero) { // only forcefully update the other box if its unchosesn or is zero
				gameSettings.setAmountOfPlayers(1);
				numberOfHumanPlayersChoiceBox.setValue(1);
				numOfHumanPlayersLabel.setText("Amt of Human Players: 1 (Automatic)");
				numOfAiPlayersLabel.setText("Amt of Ai Players: 1 (default)");
				return;
			}
			else if (numOfAis == 1) {
				numOfAiPlayersLabel.setText("Amt of Ai Players: 1 (Default)");
				gameSettings.setAmountOfAIPlayers(numOfAis);
			}
			else {
				gameSettings.setAmountOfAIPlayers(numOfAis);
				numOfAiPlayersLabel.setText("Amt of Ai Players: "+numOfAis);
			}
			// Now update the human player options to only have the rest over amount to hold the max of 8 total true
			Integer previousSelection = numberOfHumanPlayersChoiceBox.getValue();
			numberOfHumanPlayersChoiceBox.getItems().clear();
			for (int i=0; i<=(8-numOfAis); i++) {
				numberOfHumanPlayersChoiceBox.getItems().add(i);
			}
			// if the preivous selection was to large, set it to the most 
			if (previousSelection != null && previousSelection > (8-numOfAis)) {
				numberOfHumanPlayersChoiceBox.setValue(8-numOfAis);
				gameSettings.setAmountOfPlayers(8-numOfAis);
			}
			// otherwise the previous selection was fine and we keep that 
			else {
				numberOfHumanPlayersChoiceBox.setValue(previousSelection);
				gameSettings.setAmountOfPlayers(previousSelection);
			}
		}); // ---- END OF MASSIVE AI CHOICE BOX LAMBADA -----
		
		// PLAYERS, PUT A LABEL NEXT TO THE CHOICE BOX 
		HBox humanPlayersChoicesHBox = new HBox(10);
		humanPlayersChoicesHBox.getChildren().addAll(new Label("Num of Human Players"), numberOfHumanPlayersChoiceBox);
		
		HBox aiPlayersChoicesHBox = new HBox(10);
		aiPlayersChoicesHBox.getChildren().addAll(new Label("Num of Ai Players"), numberOfAiPlayersChoiceBox);
		
		Button startGameButton = new Button("Start Game");
		startGameButton.setOnAction(event -> {
			controller = new Controller(this);
			controller.initializeGameSettings(gameSettings);
			Scene gameScene = createMainGame();
			stage.setScene(gameScene);
		});
		
		
		VBox inputsVBox = new VBox(10); inputsVBox.setPadding(new Insets(10));
		inputsVBox.getChildren().addAll(allowSkippingPropertyPurchaseButton, moneyPassingGoInput, pricePerSpaceMultiplyerInput, freeParkingRewardsButton, startingMoneyInput, tradingButton, humanPlayersChoicesHBox, aiPlayersChoicesHBox, startGameButton);
		startScreenBorderPane.setCenter(inputsVBox);
		

		// ----- END OF NUMBER OF PLAYER CHOICES -----
		
		// CHOICES OF THEMES
		int MAX_IMAGE_WIDTH = 180;
		HBox themeChoicesHBox = new HBox(15); themeChoicesHBox.setPadding(new Insets(10)); 
		ToggleGroup toggleGroup = new ToggleGroup();
		
		// --- BASE MONOPOLY
		VBox baseMonopolyThemeChoiceVBox = new VBox(10); baseMonopolyThemeChoiceVBox.setAlignment(Pos.CENTER);
		Image baseMonopolyThemePreviewImage = new Image("/MonopolyThemePicker.png");
		ImageView baseMonopolyPreviewView = new ImageView(baseMonopolyThemePreviewImage);
		baseMonopolyPreviewView.setPreserveRatio(true); baseMonopolyPreviewView.setFitWidth(MAX_IMAGE_WIDTH); 
		RadioButton baseMonopolyRadioBtn = new RadioButton(); 
		baseMonopolyRadioBtn.setOnAction(event -> {
			gameSettings.setTheme(Theme.STANDARD);
		});
		baseMonopolyRadioBtn.fire(); // click the button by default
		baseMonopolyRadioBtn.setToggleGroup(toggleGroup);
		baseMonopolyThemeChoiceVBox.getChildren().addAll(baseMonopolyPreviewView, baseMonopolyRadioBtn);

		// --- PIRATES
		VBox pirateThemeChoiceVBox = new VBox(10); pirateThemeChoiceVBox.setAlignment(Pos.CENTER);
		Image pirateThemePreviewImage = new Image("/PirateThemePicker.png");
		ImageView piratePreviewView = new ImageView(pirateThemePreviewImage); 
		piratePreviewView.setPreserveRatio(true); piratePreviewView.setFitWidth(MAX_IMAGE_WIDTH); 
		RadioButton pirateRadioBtn = new RadioButton();
		pirateRadioBtn.setOnAction(event -> {
			gameSettings.setTheme(Theme.PIRATE);
		});
		pirateRadioBtn.setToggleGroup(toggleGroup);
		pirateThemeChoiceVBox.getChildren().addAll(piratePreviewView, pirateRadioBtn);
		
		// --- TUCSON
		VBox tucsonThemeChoiceVBox = new VBox(10); tucsonThemeChoiceVBox.setAlignment(Pos.CENTER);
		Image tucsonThemePreviewImage = new Image("/TucsonThemePicker.png");
		ImageView tucsonPreviewView = new ImageView(tucsonThemePreviewImage);
		tucsonPreviewView.setPreserveRatio(true); tucsonPreviewView.setFitWidth(MAX_IMAGE_WIDTH); 
		RadioButton tucsonRadioBtn = new RadioButton();
		tucsonRadioBtn.setOnAction(event -> {
			gameSettings.setTheme(Theme.TUCSON);
		});
		tucsonRadioBtn.setToggleGroup(toggleGroup);
		tucsonThemeChoiceVBox.getChildren().addAll(tucsonPreviewView, tucsonRadioBtn);
		
		themeChoicesHBox.getChildren().addAll(baseMonopolyThemeChoiceVBox, pirateThemeChoiceVBox, tucsonThemeChoiceVBox);
		startScreenBorderPane.setBottom(themeChoicesHBox);
		// ------ END OF THEMES -----
		
		Scene startGameScene = new Scene(startScreenBorderPane, 590,500);
		stage.setScene(startGameScene);
		stage.setTitle("MONOPOLY");
		stage.show();
	}
	
	/**
	 * createMainGame(): This function is called after the "Start game" button is 
	 * pressed from the start menu. The reason for this separation is because 
	 * the code in here relies on knowing how many players, and that information 
	 * isnt known until the player inputs that data. So only once the player 
	 * presses "Start game" does the controller->model get those new game settings
	 * and that information is available for the code in this function to use.
	 * This should make sense, we only create the main game once its ready to be made.
	 * @return
	 */
	private Scene createMainGame() {
		
		theme = controller.getThemeString();
		
		whichStackPanesPlayersAreOn = new HashMap<Player, StackPane>();
		playerObjToPlayerPiece = new HashMap<Player, Circle>();		
	
		BorderPane mainScreen = new BorderPane();
 
		// Set the monopoly title at the top
		VBox topLabelSection = createTopLabelSection();
		topLabelSection.setAlignment(Pos.TOP_CENTER);

		// Create the board
		StackPane visualGameBoard = buildMonopolyBoard();
		
		// __________Position board on screen________________________
		BorderPane.setMargin(visualGameBoard, new Insets(0, 0, 0, -200)); // TOP, RIGHT, BOTTOM, LEFT
		mainScreen.setCenter(visualGameBoard);
		//____________________________________________________________
		
		// Put all the players pieces on the go space, then track which space the players are on 
		StackPane goSpacePane = listOfSpacesPanes.get(0);
		List<Player> allPlayers = controller.getAllPlayers();
		// FUTURAL REMOVAL (the circle part) - Go over each player and assign them a piece 
		for (Player currPlayer: allPlayers) {
			// Create a new circle for each player 
			Circle playersIconCircle = new Circle(0, 0, playerCircleRadius);
			Image playersIconImage = currPlayer.getPlayerIconImage();
			playersIconCircle.setFill(new ImagePattern(playersIconImage));
			playerObjToPlayerPiece.put(currPlayer, playersIconCircle);
			
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
		StackPane bottomArea = buildBottomSection();
		mainScreen.setBottom(bottomArea);

		
		// ADDED: root StackPane so overlay can sit above mainScreen
		root = new StackPane();
		
		root.setStyle("-fx-background-color: rgb(168, 190, 168,.6);");

		root.getChildren().add(topLabelSection);
		StackPane.setAlignment(topLabelSection, Pos.TOP_CENTER);		
		topLabelSection.setMouseTransparent(true);
		topLabelSection.setTranslateY(15); // move down slightly
		
		// ___________________________Right side image___________________________________
		Image rightImage = new Image("/"+theme+"/uiRight.png");
		ImageView rightImageView = new ImageView(rightImage);
			
		rightImageView.setFitWidth(1500);
		rightImageView.setPreserveRatio(true);
		rightImageView.setMouseTransparent(true);
		
		StackPane.setAlignment(rightImageView, Pos.CENTER_RIGHT);

		rightImageView.setTranslateX(700);
		
		root.getChildren().add(rightImageView);
		//________________________________________________________________________________
		
		
		root.getChildren().add(mainScreen);
		
		
		// For chance and community chests
		buildCardOverlay();
		cardOverlay.setAlignment(Pos.CENTER_RIGHT);
		cardOverlay.setTranslateX(-110);
		cardOverlay.setTranslateY(-70);
		root.getChildren().add(cardOverlay);
		
		// For purchase prompts
		StackPane purchaseOverlay = new StackPane();
		purchaseOverlay.setAlignment(Pos.CENTER_RIGHT);
		purchaseOverlay.setTranslateX(-90);
		purchaseOverlay.setTranslateY(-100);
		purchaseOverlay.setVisible(false);
		this.purchaseOverlay = purchaseOverlay;
		
		root.getChildren().add(purchaseOverlay);
		
		// Build the overlay for the detailed card info on mouse click 
		StackPane detailedCardInfoOverlay = new StackPane();
		this.detailedCardInfoOverlay = detailedCardInfoOverlay;
		detailedCardInfoOverlay.setVisible(false); // dont show anything, not until mouse click 
		detailedCardInfoOverlay.setPickOnBounds(false); // allows "transparent" click thru to the board

		root.getChildren().add(detailedCardInfoOverlay);
		
		Scene gameScene = new Scene(root, 1180, 820);
		return gameScene; // THIS RETURNS TO `def start() -> StartGameBtn.onAction()` code look up ^^ a function
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
		
		VBox playersRectangleStack = new VBox(10);
		List<Player> allPlayers = controller.getAllPlayers();
		
		// loop over all the players, adding their rectangle to the right size with their color and Id 
		for (Player player : allPlayers) {
			Rectangle newRectangle;
			Label currPlayerLabel; 
			StackPane currPlayerStackPane;
			Image playerIconImage = player.getPlayerIconImage();
			Circle playersIconCircle = new Circle(12,new ImagePattern(playerIconImage));
			
			// for the current player, create a empty white box for them
			if (player.equals(currPlayer)) {
				newRectangle = new Rectangle(widthOfRightSideRectangle, heightOfRightSideRectangle, Color.GREY);
				currPlayerLabel = new Label("Current Player");
				currPlayerStackPane = new StackPane();
				
				
			}
			// if its not he current player then make the fully feature rich rectangle with color and rectangle
			else {
				newRectangle = new Rectangle(widthOfRightSideRectangle, heightOfRightSideRectangle, Color.DARKSLATEGRAY);
				currPlayerLabel = new Label(player.getPlayerName());
				
				currPlayerStackPane = new StackPane();
				
				currPlayerStackPane.setUserData(player);
				currPlayerStackPane.setOnMouseClicked((e) -> {
					showOtherPlayersInfoInBottomRight((Player)currPlayerStackPane.getUserData());
				});
			}
			
			currPlayerLabel.setTextFill(Color.BISQUE);
			playersIconCircle.setTranslateX(40);
			playersIconCircle.setTranslateY(-20);
			
			// Making rects look pretty
			newRectangle.setArcWidth(20);
			newRectangle.setArcHeight(20);
			newRectangle.setStroke(Color.rgb(0, 0, 0, 0.2));
			newRectangle.setStrokeWidth(1.5);
			
			
			currPlayerLabel.setFont(Font.font("Futura", FontWeight.BOLD, 15));
			
			currPlayerStackPane.getChildren().addAll(newRectangle, currPlayerLabel, playersIconCircle);
			playersRectangleStack.getChildren().add(currPlayerStackPane);
		}
		
		BorderPane.setAlignment(rightSidePlayerPickerGroup, Pos.CENTER);
		
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
	private void showOtherPlayersInfoInBottomRight(Player selectedOtherPlayer) {
		if (selectedOtherPlayer.equals(previousSelectedOtherPlayer)) {
			otherPlayerInfoCardStackPane.getChildren().clear();
			previousSelectedOtherPlayer = null; // allow them to click it again
			return;
		}
		previousSelectedOtherPlayer = selectedOtherPlayer;
		Node othersInfoCard = createVisualPlayerInfoCard(selectedOtherPlayer);
		otherPlayerInfoCardStackPane.getChildren().clear();
		otherPlayerInfoCardStackPane.getChildren().add(othersInfoCard);
	}
	
	
	/**
	 * This will create the top "MONOPOLY" Text, the user error info 
	 * and the Ai logger 
	 * @return VBox: IT holds top: "MOPOLY", middle: "User error info", bottom: VBox: Ai logger
	 */
	private VBox createTopLabelSection() {
		
		VBox topLabelSection = new VBox(10);
		topLabelSection.setAlignment(Pos.CENTER);
		
		//Move down slightly
		topLabelSection.setPadding(new Insets(-105, 0, 0, 0));
		
		
		
		BorderPane.setAlignment(topLabelSection, Pos.CENTER);
		
	
		
		return topLabelSection;
	}

	/**
	 * buildMonopolyBoard(): This function will craft the center monopoly board with
	 * all spaces.
	 * 
	 * @return GridPane: The one to set at the center of the screen
	 */
	public StackPane buildMonopolyBoard() {
		
		//creating empty list of size 40
		listOfSpacesPanes = new ArrayList<StackPane>();
		for(int i = 0; i < controller.getSpaces().size(); i++) {
	        listOfSpacesPanes.add(null);
	    }
		
		
		GridPane mainBoardGridPane = new GridPane();
		mainBoardGridPane.setAlignment(Pos.CENTER);
		mainBoardGridPane.setPadding(new Insets(40));


		int boardWidth = controller.getBoardWidth();
		int boardHeight = boardWidth;
		List<Space> allSpaces = controller.getSpaces();


		placeAllPropertySpaces(mainBoardGridPane, allSpaces, boardWidth, boardHeight);

		placeGoParkingAndJails(mainBoardGridPane, allSpaces);

		StackPane wrapper = new StackPane();
		
		// BACKGROUND IMAGE 
		Image backgroundImage = new Image("/"+theme+"/background.png");
		ImageView backgroundImageView = new ImageView(backgroundImage);
		backgroundImageView.setFitWidth(1000);
		backgroundImageView.setPreserveRatio(true);
		backgroundImageView.setManaged(false);
		backgroundImageView.setTranslateY(120);
		backgroundImageView.setTranslateX(-200);
				
		//place image at the bottom
		StackPane.setAlignment(backgroundImageView, Pos.BOTTOM_CENTER);
		
		
		// TITLE IMAGE added where the backgrounImage is added
		Image titleImage = new Image("/"+theme+"/gameTitle.png");
		ImageView titleImageView = new ImageView(titleImage);
		titleImageView.setFitWidth(500);
		titleImageView.setPreserveRatio(true); // don't distort the image 
		titleImageView.setTranslateY(-110); // move the title image "inside" the board at the top inner part
		

		// For center content
		VBox centerContent = new VBox(10);
		centerContent.setAlignment(Pos.CENTER);
		
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

	    StackPane centerOverlay = new StackPane();
	    centerOverlay.setStyle(
	        
	        "-fx-background-radius: 8;"
	    );

	    // auto-size to center area
	    centerOverlay.prefWidthProperty().bind(mainBoardGridPane.widthProperty().multiply(0.65));
	    centerOverlay.prefHeightProperty().bind(mainBoardGridPane.heightProperty().multiply(0.65));
	    
	    centerOverlay.getChildren().add(centerContent);
	    centerContent.getChildren().addAll(currPlayerLabel, infoToTellPlayer, aiLoggerVBox);
	    
	    wrapper.getChildren().addAll(backgroundImageView,titleImageView,mainBoardGridPane, centerOverlay);


	    return wrapper;

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
		jailSpaceStackPane = jailStackPane;
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
	private StackPane buildBottomSection() {
		
		// Player info group - This group is used to change the property card that is held in the bottom left
		Group bottomLeftPlayerCardGroup = new Group();
		this.bottomLeftPlayerCardGroup = bottomLeftPlayerCardGroup;  
		
		// Build player card to go in the bottom left
		Player currPlayer = controller.getCurrentPlayer();
		Node visualPlayerCard = createVisualPlayerInfoCard(currPlayer);
		
		bottomLeftPlayerCardGroup.getChildren().add(visualPlayerCard);
		
		
		// --- Middle dice roll area ---
		StackPane diceRollStackPane = new StackPane();
		diceRollStackPane.setPrefWidth(diceRollAreaWidth);
		diceRollStackPane.setPrefHeight(bottomHBoxHeight);
		diceRollStackPane.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
		diceRollStackPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		this.diceRollStackPane = diceRollStackPane;
		
		// --- Make the Roll Dice, Build, Trade, End Turn buttons ---
		Group mainButtonsGroup = buildMainButtons();
		
		StackPane otherPlayerInfoCardStackPane = new StackPane();
		otherPlayerInfoCardStackPane.setPrefWidth(diceRollAreaWidth);
		otherPlayerInfoCardStackPane.setPrefHeight(bottomHBoxHeight);
		otherPlayerInfoCardStackPane.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
		otherPlayerInfoCardStackPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		this.otherPlayerInfoCardStackPane = otherPlayerInfoCardStackPane;


		// !! NOW FILL THE BOTTOM ROW !!
		HBox bottomHBox = new HBox(); // player info card left, buttons right
		bottomHBox.getChildren().add(bottomLeftPlayerCardGroup);
		bottomHBox.getChildren().add(diceRollStackPane);
		bottomHBox.getChildren().add(mainButtonsGroup);
		bottomHBox.getChildren().add(otherPlayerInfoCardStackPane);
		
		bottomHBox.setPrefHeight(bottomHBoxHeight);

	    // background image
	    Image bottomFrameImage = new Image("/" + theme + "/uiBottom.png");

	    ImageView bottomFrameImageView = new ImageView(bottomFrameImage);
	    bottomFrameImageView.setPreserveRatio(false);
	    bottomFrameImageView.setFitHeight(500);
	    bottomFrameImageView.setTranslateY(-130);
	    bottomFrameImageView.setTranslateX(-50);
	    bottomFrameImageView.setMouseTransparent(true);
	    bottomFrameImageView.setManaged(false);

	    // wrapper
	    StackPane bottomWrapper = new StackPane();
	    bottomWrapper.setAlignment(Pos.BOTTOM_CENTER);

	    // make image stretch across the whole bottom section width
	    bottomFrameImageView.fitWidthProperty().bind(bottomWrapper.widthProperty());

	    bottomWrapper.getChildren().addAll(bottomFrameImageView, bottomHBox);

	    return bottomWrapper;
		
	}
	
	/**
	 * This function will build the group that holds a VBox of buttons as
	 * rectangles. There will be a Roll dice button, Trade button, build button, and 
	 * end turn button.
	 * @return Group -> VBox -> Stack Panes (Rectangles, Labels)a
	 */
	private Group buildMainButtons() {
		// Buttons
		// FUTURE NOTE - The get out of jail options will replace the children of this group with its own buttons
		// 			   - Then after its done it will replace it back with the object that is stored in the `coreButtonsVBox` attribute 
		Group mainButtonsGroup = new Group();
		this.mainButtonsGroup = mainButtonsGroup; // This will be used to change the buttons to present the jail options
		
		// CREATE ROLL DICE BUTTON
		Rectangle rollDiceButtonRect = new Rectangle(coreButtonWidth, coreButtonHeight, Color.BURLYWOOD);
		rollDiceButtonRect.setArcWidth(30); 
		rollDiceButtonRect.setArcHeight(30);
		Label rollDiceLabel = new Label("Roll Dice");
		StackPane rollDiceStackPane = new StackPane();
		rollDiceStackPane.getChildren().addAll(rollDiceButtonRect,rollDiceLabel);
		rollDiceStackPane.setOnMouseClicked(event -> handleDiceRoll());
		this.rollDiceButton = rollDiceStackPane; // used for disabling the button later
		
		
		// CREATE TRADE HOUSES BUTTON
		Rectangle tradeButtonRect = new Rectangle(coreButtonWidth, coreButtonHeight, Color.BURLYWOOD);
		tradeButtonRect.setArcWidth(30); 
		tradeButtonRect.setArcHeight(30);
		
		Label tradeLabel = new Label("Trade");
		StackPane tradeButtonStackPane = new StackPane();
		tradeButtonStackPane.getChildren().addAll(tradeButtonRect,tradeLabel);
		tradeButtonStackPane.setOnMouseClicked(event -> handleTradeButton());
		
		
		// CREATE BUILD HOUSES BUTTON
		Rectangle buildButtonRect = new Rectangle(coreButtonWidth, coreButtonHeight, Color.BURLYWOOD);
		buildButtonRect.setArcWidth(30); 
		buildButtonRect.setArcHeight(30);
		
		Label buildLabel = new Label("Build Houses");
		StackPane buildButtonStackPane = new StackPane();
		buildButtonStackPane.getChildren().addAll(buildButtonRect,buildLabel);
		buildButtonStackPane.setDisable(true);
		buildButtonStackPane.setOnMouseClicked(event -> {
			handleBuildButton();
			
		});
		this.buildButton = buildButtonStackPane;
		
		
		// CREATE END TURN BUTTON
		Rectangle endTurnButtonRect = new Rectangle(coreButtonWidth, coreButtonHeight, Color.BURLYWOOD);
		endTurnButtonRect.setArcWidth(30); 
		endTurnButtonRect.setArcHeight(30);
		
		Label endTurnLabel = new Label("End Turn");
		StackPane endTurnButtonStackPane = new StackPane();
		endTurnButtonStackPane.setDisable(true);
		endTurnButtonStackPane.getChildren().addAll(endTurnButtonRect,endTurnLabel);
		endTurnButtonStackPane.setOnMouseClicked(event -> handleEndTurnButton());
		this.endTurnButton = endTurnButtonStackPane;
		

		// FUTURE NOTE - READ FUTURE NOTE ABOVE IF YOU ARE CHANGING THESE BUTTONS
		VBox coreButtonsVBox = new VBox(5);
		coreButtonsVBox.setPadding(new Insets(8));
		coreButtonsVBox.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
		coreButtonsVBox.setPrefHeight(bottomHBoxHeight);
		
		this.coreButtonsVBox = coreButtonsVBox; // I need this saved so the getOutOfJailLogic can bring these buttons back 
		coreButtonsVBox.getChildren().addAll(rollDiceButton, tradeButtonStackPane, buildButtonStackPane, endTurnButton);
		mainButtonsGroup.getChildren().add(coreButtonsVBox);
		
		return mainButtonsGroup;
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
	private Node createVisualPlayerInfoCard(Player currPlayer) {
		GridPane visualPlayerCardGridPane = new GridPane(10, 5);
		visualPlayerCardGridPane.setPrefWidth(widthOfPlayerInfoCard);
		visualPlayerCardGridPane.setPrefHeight(heighOfPlayerInfoCard);
		
		
		visualPlayerCardGridPane.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 5; -fx-background-color: darkslategrey;");
		
		Label playerName = new Label(currPlayer.getPlayerName());
		playerName.setFont(Font.font("Roboto Mono", FontWeight.BOLD, 25));
		playerName.setTextFill(Color.BISQUE);
		playerName.setTextOverrun(OverrunStyle.CLIP); // removes the annoying "..." from happening in the text
		
		Image playerIconImage = currPlayer.getPlayerIconImage();
		Circle playersCircleIcon = new Circle(20, new ImagePattern(playerIconImage));
		
		Label playerCash = new Label("$"+currPlayer.getCashAmmt());
		playerCash.setFont(Font.font("Roboto Mono", FontWeight.BOLD, 15));
		playerCash.setTextFill(Color.LIMEGREEN);
		playerCash.setTextOverrun(OverrunStyle.CLIP); // removes the annoying "..." from happening in the text
		
		int seperationBetweenNameAndCash = 25;
		HBox playerNameAndCashHBox = new HBox(seperationBetweenNameAndCash);
		playerNameAndCashHBox.getChildren().addAll(playerName, playersCircleIcon, playerCash);
		
		
		Label playerGetOutOfJailCardsAmtLabel = new Label("Ammount of Get out of jail cards: "+currPlayer.getAmmtOfGOOJCards());
		playerGetOutOfJailCardsAmtLabel.setTextFill(Color.BISQUE);
		
		Label playerPropsLabel = new Label("Properties Owned List");
		playerPropsLabel.setFont(Font.font("Roboto Mono", FontWeight.BOLD, 20));
		playerPropsLabel.setTextFill(Color.BISQUE);
		
		ScrollPane playersVisualProperties = createScrollPaneOfPlayersProperties(currPlayer);
		
		visualPlayerCardGridPane.add(playerNameAndCashHBox, 0, 0);
		visualPlayerCardGridPane.add(playerGetOutOfJailCardsAmtLabel, 0, 1);
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

			// check if realestate, building the 5 "build-progress-dots" on top of a real estate card and add build-button onclick logic
			if (currProperty instanceof RealEstate re) {
				FlowPane buildingDots = new FlowPane(1, 0); // 1 horiz-pixel margin
				buildingDots.setAlignment(Pos.CENTER);
				//creating 5 dots, one for each buildstage value on the property
				int buildStage = re.getBuildingStage();
				for (int i = 1; i <= 5; i++) {
					Rectangle dot = new Rectangle(10, 10);
					if (buildStage >= i) {
						dot.setFill(Color.ORANGERED);
					} else {
						dot.setFill(Color.TRANSPARENT);
					}
					//styling and adding to flowpane
					dot.setStroke(Color.MAROON);
					dot.setStrokeWidth(1);
					buildingDots.getChildren().add(dot);
				}
				// add dots to the bottom of the card stackpane
				StackPane.setAlignment(buildingDots, Pos.BOTTOM_CENTER);
				visualPropertyInfoCard.getChildren().add(buildingDots);
				
				// adding on-click selects/deselects for building; enables/disables the Build button
				visualPropertyInfoCard.setOnMouseClicked((e) -> {
					selectedPropertyToBuild = re;					
					if (!re.getIfCanBuild() || re.getBuildingStage() >=5) { //if already fully developed, don't enable the build button when i click a card
						buildButton.setDisable(true);
					}
					
					buildButton.setDisable(false); //otherwise, enable the build button and show the card info
					showDetailedPropertyInfo(re);
	
				});
				
			//if not realestate, will only add an on-click for details and not build any dots
			} else {
			visualPropertyInfoCard.setOnMouseClicked((e) -> {
				showDetailedPropertyInfo((Property) visualPropertyInfoCard.getUserData());
				});
			}

			
			// add this visual card to the underlying gridpane of the scroll pane
			propertiesGridPane.add(visualPropertyInfoCard, gridPaneIdx, 0);
			gridPaneIdx++;
		}
		ScrollPane scrollablePropertiesPane = new ScrollPane(propertiesGridPane);
		// Set the default size of the scroll pane
		scrollablePropertiesPane.setPrefWidth(widthOfPlayerCardPropertiesScrollPane);
		scrollablePropertiesPane.setMinHeight(widthOfPlayerCardProperties*1.6+25); // the 1.6 is from how the cards heigh is determined in `buildSpaceCard()` 
		scrollablePropertiesPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // dont show the scroll bars
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
			// lets say they roll doubles for the 3rd time, then they get sent to jail it should be the next players turn 
			controller.model.notifyViewOfNextPlayersTurn(currentPlayer); // THIS IS WRONG WRONG WRONG, AS SOON AS a player rolls doubles 3 times it has to be detencted somehow, it wasnt so this is a quick fix so after they press roll dice AGAIN after being put in jail it moves on, THIS IS BAD 
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
	 * NOT IMPLEMENTED YET, this will be called when the (nicer looking) trade button is pressed
	 */
	private void handleTradeButton() {
		
	}
	
	/**
	 * NOT IMPLEMENTED YET, this will be called when the (nicer looking) build button is pressed
	 */
	private void handleBuildButton() {
		if (selectedPropertyToBuild != null) {
			controller.buildHouseHotel(controller.getCurrentPlayer(), selectedPropertyToBuild);
			selectedPropertyToBuild = null;
			buildButton.setDisable(true);
			populatePlayerCardWithNewInfo(controller.getCurrentPlayer());
		}
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
		diceRollStackPane.getChildren().clear();
		
		GridPane diceResultGridPane = new GridPane(20, 0);
		diceResultGridPane.setPadding(new Insets(20));
		BorderStroke borderStroke = new BorderStroke(
				Color.GREY,
				BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY,
				new BorderWidths(2)
			);
		diceResultGridPane.setBorder(new Border(borderStroke));
		
		diceRollStackPane.getChildren().add(diceResultGridPane);
		
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
		newDice.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
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
		Node playerCard = createVisualPlayerInfoCard(theNextPlayer);
		// REMEMBER TO CLEAN THE OLD PLAYING CARD
		bottomLeftPlayerCardGroup.getChildren().clear();
		bottomLeftPlayerCardGroup.getChildren().add(playerCard);
	}
	
	
	/**
	 * showOptionsForGettingOutOfJail(currentPlayer): This function will 
	 * determine which options are available for getting out of jail for this player 
	 * based on what the player has 
	 * @param currentPlayer (Player): the current player whos turn it is, who is found to be in jail 
	 */
	private void showOptionsForGettingOutOfJail(Player currentPlayer) {
		int doublesAttempts = controller.getAmmtOfJailAttempts(currentPlayer);
		System.out.println("View: Showing options for getting out of jail: doublesAttempts = "+doublesAttempts);
		mainButtonsGroup.getChildren().clear(); // remove core buttons
		
		VBox jailButtonsVBox = new VBox(5);
		jailButtonsVBox.setPadding(new Insets(8));
		jailButtonsVBox.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
		jailButtonsVBox.setPrefHeight(bottomHBoxHeight);
		
		mainButtonsGroup.getChildren().add(jailButtonsVBox);
		
		
		int playersCashTotal = currentPlayer.getCashAmmt();
		// if the player can still attempt to roll doubles then add a button for it, but if they dont have $50 then they must roll doubles
		if (doublesAttempts < 3 || playersCashTotal < 50) {
			
			// CREATE ROLL DICE BUTTON
			Rectangle rollDoublesButtonRect = new Rectangle(coreButtonWidth, coreButtonHeight, Color.BURLYWOOD);
			rollDoublesButtonRect.setArcWidth(30); 
			rollDoublesButtonRect.setArcHeight(30);
			Label rollDoublesLabel = new Label("Roll Doubles");
			StackPane rollDoublesStackPane = new StackPane();
			rollDoublesStackPane.getChildren().addAll(rollDoublesButtonRect,rollDoublesLabel);
			rollDoublesStackPane.setOnMouseClicked(event -> {
				controller.processJailLogic(currentPlayer, JAIL_CHOICE.ROLL_DUBLES);
				if (!currentPlayer.isInJail()) {
					mainButtonsGroup.getChildren().clear(); // remove the jail options
					mainButtonsGroup.getChildren().add(coreButtonsVBox); // add the core buttons back 
				}
			});
			
			jailButtonsVBox.getChildren().add(rollDoublesStackPane);
			
		}
		
		// if the user has enough to pay money then show that button 
		if (playersCashTotal >= 50) {
			
			// CREATE PAY $50 CASH BUTTON
			Rectangle payCashButtonRect = new Rectangle(coreButtonWidth, coreButtonHeight, Color.BURLYWOOD);
			payCashButtonRect.setArcWidth(30); 
			payCashButtonRect.setArcHeight(30);
			Label payCashLabel = new Label("Pay $50");
			StackPane payCashStackPane = new StackPane();
			payCashStackPane.getChildren().addAll(payCashButtonRect,payCashLabel);
			payCashStackPane.setOnMouseClicked(event -> {
				controller.processJailLogic(currentPlayer, JAIL_CHOICE.PAY_FIFTY);
				mainButtonsGroup.getChildren().clear(); // remove the jail options
				mainButtonsGroup.getChildren().add(coreButtonsVBox); // add the core buttons back 
			});
			jailButtonsVBox.getChildren().add(payCashStackPane);
		}
		
		// if the player has atleast 1 get out of jail free card, show that button 
		if (currentPlayer.getAmmtOfGOOJCards() >= 1) {
			
			// CREATE USE GET OUT OF JAIL FREE CARD BUTTON
			Rectangle useGOOJButtonRect = new Rectangle(coreButtonWidth, coreButtonHeight, Color.BURLYWOOD);
			useGOOJButtonRect.setArcWidth(30); 
			useGOOJButtonRect.setArcHeight(30);
			Label useGOOJLabel = new Label("Use Get Out Of Jail Free Card");
			StackPane useGOOJStackPane = new StackPane();
			useGOOJStackPane.getChildren().addAll(useGOOJButtonRect,useGOOJLabel);
			useGOOJStackPane.setOnMouseClicked(event -> {
				controller.processJailLogic(currentPlayer, JAIL_CHOICE.OUT_OF_JAIL_CARD);
				mainButtonsGroup.getChildren().clear(); // remove the jail options
				mainButtonsGroup.getChildren().add(coreButtonsVBox); // add the core buttons back 
			});
			jailButtonsVBox.getChildren().add(useGOOJStackPane);
			
		}
		
	}
	
	/**
	 * This function will decipher that type of message is received and then it will act on the message
	 * with a corresponding function 
	 * @param model (Model): The state of the model 
	 * @param message (Object): Could be any type of message from the model
	 */
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
			
			// at the start of another player, clear the Ai logger of any potential messages
			aiLoggerVBox.getChildren().clear();

			currPlayerLabel.setText(("Player " + controller.getCurrentPlayer().getId() + "'s Turn")); 
			
			buildRightPlayerPicker(nextPlayerMsg.getNextPlayer());
			
			// if the next player is in jail, show their next options for getting out of jail 
			Player currentPlayer = nextPlayerMsg.getNextPlayer();
			if (currentPlayer.isInJail()) {
				showOptionsForGettingOutOfJail(currentPlayer); // this will change the buttons in the bottom right 
			}
			else {
				rollDiceButton.setDisable(false);
				endTurnButton.setDisable(false);
			}
			
			if (currentPlayer instanceof AIPlayer) {
				rollDiceButton.setDisable(true);
				endTurnButton.setDisable(true);
				((AIPlayer) currentPlayer).playAITurn(controller);
			}
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
			aiLoggerVBox.getChildren().add(newAiActionLabel);
		}
		
		// if the view is notified that a player is going to jail, you can play sounds and animate
		else if (message instanceof GoToJailMessage) {
			GoToJailMessage goToJailMsg = (GoToJailMessage) message;
			Player player = goToJailMsg.getPlayerGoingToJail();
			// FUTURE NOTE: now you can play a sound of the player going to jail and animate them going there
			
			// Move the player to the jail space
			
			// get the current pane the player is one, then use it to find the index in the list of all panes
			StackPane currentSpacesPane = whichStackPanesPlayersAreOn.get(player);
			if (currentSpacesPane == null) System.out.println("ERROR: Player object is not in hash map somehow");
			Circle playersPeiceToMove = playerObjToPlayerPiece.get(player);
			currentSpacesPane.getChildren().remove(playersPeiceToMove);
			
			jailSpaceStackPane.getChildren().add(playersPeiceToMove);
		}
		
		//generic string can be used to update player info label in the center
		else if (message instanceof String) {
	        String sentence = (String) message;
	        infoToTellPlayer.setText(sentence);
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

	    cardIcon = new ImageView();
	    StackPane.setAlignment(cardIcon, Pos.BOTTOM_RIGHT);
	    
	    VBox cardBox = new VBox();
	    cardBox.setAlignment(Pos.TOP_LEFT);
	
	    cardBox.setMaxWidth(width); 
	    cardBox.setMaxHeight(height);
	    
	    // Change style here
	    cardBox.setStyle(
	        "-fx-background-color: white;" +
	        "-fx-border-color: black;" +
	        "-fx-border-width: 3;" +
	        "-fx-border-insets: 5;" +
	        "-fx-padding: 20;"
	    );
	    
	    // Title label
	    cardTitle = new Label();
	    cardTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-font-family: 'Lucida Calligraphy';");
	    // Card description
	    cardLabel = new Label();
	    cardLabel.setWrapText(true);
	    cardLabel.setTextAlignment(TextAlignment.CENTER);
	    cardLabel.setAlignment(Pos.CENTER);
	    cardLabel.setStyle("-fx-font-size: 16px;");

	    Label continueLabel = new Label("Click to continue");
	    continueLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

	    cardBox.getChildren().addAll(cardTitle, cardLabel, continueLabel);
	    overlay.getChildren().addAll(cardBox,cardIcon);


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
	 * @param testSpace the space that the card is basing itself on
	 * @param cardWidth the width of the card to scale from
	 * @return StackPane the card image
	 */
	public StackPane buildSpaceCard(Property testSpace, double cardWidth) {
		double cardHeight = cardWidth * 1.6;
	    double borderWidth = Math.max(1, cardWidth * 0.016);
	    double borderInset = cardWidth * 0.05;
	    double headerHeight = cardHeight * 0.20;
	    double centerHeight = cardHeight * .50;
	    double titleDeedFont = cardWidth * 0.045;
	    double nameFont = cardWidth * 0.08;
	    double rNameFont = cardWidth * 0.065;
	    double innerWidthOffset = borderInset * 2 + borderWidth * 2;
	    double bodyFont = cardWidth * 0.06;
	    double imageSize = cardWidth * .55;

	    // For lines
	    double sepWidth = cardWidth * 0.70;
		double sepThickness = Math.max(1, cardWidth * 0.006);
		double sepSpacing = cardHeight * 0.015;
	    
	    StackPane root = new StackPane();
	    StackPane imageOverlay = new StackPane();
	    
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
	    // For text lines for pricing
	    ArrayList<String> lines = new ArrayList<>();
	    // Prices
	    ArrayList<Integer> prices = new ArrayList<>();
	    int mortgageVal;
	    int purchasePrice = testSpace.getPurchaseAmount();
	    int buildPrice;
	    
	    // Property Cards
	    if (testSpace instanceof RealEstate) {
	    	RealEstate re = (RealEstate) testSpace;
	    	buildPrice = re.getBuildPrice();
	    
	    	StackPane headerBox = new StackPane();
	    	headerBox.setPrefHeight(headerHeight);
	    	headerBox.setAlignment(Pos.CENTER);

	    	Rectangle colorRect = new Rectangle();
	    	colorRect.setHeight(headerHeight);
	    	
	    	//only realestate has colors
	    	if (testSpace instanceof RealEstate) {
	    	colorRect.setFill(((RealEstate)testSpace).getFXColor()); //cast to realestate obj so can get javafx color
	    	} else {
	    		colorRect.setFill(defaultSpaceColor);
	    	}
	    
			// Changed from this to the new line, this was causing cards to expend horizontally to the window edges
	    	// colorRect.widthProperty().bind(card.widthProperty().subtract(innerWidthOffset*.8)); // Works for rest of team
			colorRect.setWidth(cardWidth - innerWidthOffset * 0.8); // Works for jarrod, jarrod should be only one using this line of code
																	   // since the other implementation for some unknowable reason doesn't work
																	   // on his computer

	    	Text t1 = new Text("TITLE DEED\n");
	    	t1.setStyle("-fx-font-size: " + titleDeedFont + "px;");

	    	Text t2 = new Text(testSpace.getName().toUpperCase());
	    	t2.setStyle("-fx-font-size: " + nameFont + "px; -fx-font-weight: bold;");

	    	TextFlow flow = new TextFlow(t1, t2);
	    	flow.setTextAlignment(TextAlignment.CENTER);
	    	flow.setMaxWidth(cardWidth - innerWidthOffset - 8);

	    	headerBox.getChildren().addAll(colorRect, flow);
	    	card.getChildren().add(headerBox);
	    	
	    	prices = testSpace.getRentStages();
	    	mortgageVal = testSpace.getPurchaseAmount()/2;

	    	buildPrice = ((RealEstate) testSpace).getBuildPrice();
	    	
	    	lines.add("Purchase Price             $" + purchasePrice +"\n");
	    	lines.add("Rent                             $" + prices.get(0)+"\n");
	    	lines.add("Rent w/ color set         $" + prices.get(1)+"\n");
	    	lines.add("Rent w/ 1 house          $" + prices.get(2)+"\n");
	    	lines.add("Rent w/ 2 houses        $" + prices.get(3)+"\n"); 
	    	lines.add("Rent w/ 3 houses        $" + prices.get(4)+"\n");
	    	lines.add("Rent w/ 4 houses        $" + prices.get(5)+"\n");
	    	lines.add("Rent w/ hotel              $" + prices.get(6)+"\n\n");
	    	lines.add("Houses cost                $" + buildPrice +"\n");


	    	StackPane bodyBox = new StackPane();
	        bodyBox.setMaxWidth(Double.MAX_VALUE);
	        bodyBox.setAlignment(Pos.TOP_CENTER);
	        
	        Text line1 = new Text(lines.get(0));
	        Text line2 = new Text(lines.get(1));
	        Text line3 = new Text(lines.get(2));
	        Text line4 = new Text(lines.get(3));
	        Text line5 = new Text(lines.get(4));
	        Text line6 = new Text(lines.get(5));
	        Text line7 = new Text(lines.get(6));
	        Text line8 = new Text(lines.get(7));
	        Text line9 = new Text(lines.get(8));

	        
	        line1.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line2.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line3.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line4.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line5.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line6.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line7.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line8.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line9.setStyle("-fx-font-size: " + bodyFont + "px;");

	        TextFlow bodyFlow = new TextFlow(line1, line2, line3, line4,line5, line6, line7, line8, line9);
//	        bodyFlow.setTextAlignment(TextAlignment.CENTER);
	        bodyFlow.setMaxWidth(cardWidth - innerWidthOffset - 5); // CHANGED TO -5 so text wouldnt wrap in properties owned lists in player info card
	        bodyFlow.setLineSpacing(cardHeight * 0.0050);
	        
	        bodyBox.getChildren().add(bodyFlow);

	        card.getChildren().addAll( bodyBox);
	    	
		}
		
		if(testSpace instanceof Railroad) {
			
			Image image = new Image("/"+theme +"/" + testSpace.getImageFile());
			ImageView icon = new ImageView(image);
			
			icon.setFitWidth(imageSize);
			icon.setPreserveRatio(true);
			icon.setManaged(false);
			icon.setTranslateX(cardWidth*.25);
			
			StackPane.setAlignment(icon, Pos.TOP_CENTER);
			
			Line topLine = new Line(0, 0, sepWidth, 0);
			topLine.setStrokeWidth(sepThickness);

			Line bottomLine = new Line(0, 0, sepWidth, 0);
			bottomLine.setStrokeWidth(sepThickness);

			Text title = new Text(testSpace.getName().toUpperCase());
			TextFlow titleFlow = new TextFlow(title);
			titleFlow.setTextAlignment(TextAlignment.CENTER);
			titleFlow.setStyle("-fx-font-size: " + rNameFont + "px; -fx-font-weight: bold;");
			
			VBox railroadTitleBox = new VBox(sepSpacing, topLine, titleFlow, bottomLine);
			railroadTitleBox.setAlignment(Pos.CENTER);

			// Moves title to center
			VBox.setMargin(railroadTitleBox, new Insets(cardWidth * 0.50, 0, 0, 0));
			
			
			card.getChildren().addAll(icon,railroadTitleBox);
	    	
	    	
	    	prices = testSpace.getRentStages();
	    	mortgageVal = testSpace.getPurchaseAmount()/2;
	    	
	    	lines.add("Purchase Price $" + purchasePrice +"\n");
	    	lines.add("Rent		$" + prices.get(0)+ "\n");
	    	lines.add("If 2 R,R.'s are owned $" + prices.get(1)+"\n");
	    	lines.add("If 3 R.R.'s are owned $" + prices.get(2)+"\n");
	    	lines.add("If 4 R.R.'s are owned $" + prices.get(3)+"\n"); 
	    	lines.add("\nMortgage Value		$" + mortgageVal);
	    	
	    	StackPane bodyBox = new StackPane();
	        bodyBox.setAlignment(Pos.TOP_CENTER);
	        
	        Text line1 = new Text(lines.get(0));
	        Text line2 = new Text(lines.get(1));
	        Text line3 = new Text(lines.get(2));
	        Text line4 = new Text(lines.get(3));
	        Text line5 = new Text(lines.get(4));
	        Text line6 = new Text(lines.get(5));


	        line1.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line2.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line3.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line4.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line5.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line6.setStyle("-fx-font-size: " + bodyFont + "px;");



	        TextFlow bodyFlow = new TextFlow(line1, line2, line3, line4,line5);
	        bodyFlow.setTextAlignment(TextAlignment.CENTER);
	        bodyFlow.setMaxWidth(cardWidth - innerWidthOffset - 20); 
	        bodyFlow.setLineSpacing(cardHeight * 0.0033);
	        
	        bodyBox.getChildren().add(bodyFlow);

	        card.getChildren().addAll(bodyBox);
	    	
		}
		
		if(testSpace instanceof Utility) {
			
			
			Image image = new Image("/" + theme +"/" + testSpace.getImageFile());
			ImageView icon = new ImageView(image);
			
			
			icon.setFitWidth(imageSize);
			icon.setPreserveRatio(true);
			icon.setManaged(false);
			icon.setTranslateX(cardWidth*.25);
			
			Line topLine = new Line(0, 0, sepWidth, 0);
			topLine.setStrokeWidth(sepThickness);

			Line bottomLine = new Line(0, 0, sepWidth, 0);
			bottomLine.setStrokeWidth(sepThickness);
			Text title = new Text(testSpace.getName().toUpperCase());
			TextFlow titleFlow = new TextFlow(title);
			titleFlow.setTextAlignment(TextAlignment.CENTER);
			titleFlow.setStyle("-fx-font-size: " + rNameFont + "px; -fx-font-weight: bold;");
			
			VBox utilityTitleBox = new VBox(sepSpacing, topLine, titleFlow, bottomLine);
			utilityTitleBox.setAlignment(Pos.CENTER);

			// Moves title to center
			VBox.setMargin(utilityTitleBox, new Insets(cardWidth * 0.50, 0, 0, 0));
			
			card.getChildren().addAll(icon, utilityTitleBox);
	    	
	    	StackPane bodyBox = new StackPane();
	        bodyBox.setMaxWidth(Double.MAX_VALUE);
	        bodyBox.setAlignment(Pos.TOP_CENTER);
	        
	        // Moves body up a bit
	        VBox.setMargin(bodyBox, new Insets( cardWidth *.05, 0,0 , 0));
	        
	        Text line1 = new Text("Purchase Price: $" + purchasePrice +"\n");
	        Text line2= new Text("If one utility is owned\n");
	        Text line3 = new Text("rent is $4 times amount\n");
	        Text line4 = new Text("shown on dice.\n");
	        Text line5 = new Text("If both Utilities are owned,\nrent is $10 ");
	        Text line6 = new Text("times amount shown on dice.");

	        line1.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line2.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line3.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line4.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line5.setStyle("-fx-font-size: " + bodyFont + "px;");
	        line6.setStyle("-fx-font-size: " + bodyFont + "px;");


	        TextFlow bodyFlow = new TextFlow(line1, line2, line3, line4,line5);
	        bodyFlow.setTextAlignment(TextAlignment.CENTER);
	        bodyFlow.setMaxWidth(cardWidth - innerWidthOffset - 20);
	        bodyFlow.setLineSpacing(cardHeight * 0.0033);
	        
	        bodyBox.getChildren().add(bodyFlow);

	        card.getChildren().addAll( bodyBox);
	    	
		
		}
		root.getChildren().add(card);
		root.setUserData(testSpace);
		return  root;
	}

}