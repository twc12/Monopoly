# Monopoly

A fully featured desktop version of **Monopoly**, built entirely in **Java** with a **JavaFX** graphical user interface.

The game is designed for local play and includes configurable human and AI players, multiple rule options, saving and loading games, trading, and three complete visual themes.

## Features

* 🎲 Classic Monopoly-style gameplay
* 👥 Local multiplayer
* 🤖 Computer-controlled AI players
* 🏠 Property purchasing and ownership
* 🏘️ Houses and property development
* 💵 Rent, player balances, and transactions
* 🔄 Property trading between players
* 🎴 Chance and Community Chest cards
* 🚂 Railroads and utilities
* 🚔 Jail and Go To Jail mechanics
* 💸 Taxes and other board spaces
* 🏆 Bankruptcy and game winner detection
* 💾 Save and load games using `.monopoly` save files
* 🎨 Three selectable themes
* ⚙️ Customizable game rules
* 🎬 JavaFX animations and graphical interface
* 🔊 JavaFX media/audio support

## Themes

The game includes three different themes that change the appearance of the board and game assets:

* **Standard Monopoly**
* **Pirate**
* **Tucson**

Theme-specific images and resources are stored in the `resources/` directory.

## Custom Game Settings

Before starting a game, players can configure several gameplay options, including:

* Number of human players
* Number of AI players
* Starting money
* Money received for passing GO
* Property price adjustment
* Optional property purchasing
* Free Parking rule
* Player trading

This allows games to be played with either more traditional Monopoly rules or customized house rules.

## AI Players

The game includes computer-controlled players that can make gameplay decisions automatically.

AI players are capable of actions such as:

* Purchasing properties
* Managing available money
* Evaluating properties
* Developing owned properties
* Evaluating other players
* Initiating trades
* Responding to trades
* Attempting to complete monopolies

AI behavior is implemented in `AIPlayer.java`.

## Saving and Loading

Games can be saved from within the application and resumed later.

Save files use the custom:

```text
.monopoly
```

file extension.

The game provides JavaFX file dialogs for selecting save locations and loading existing Monopoly games.

## Technologies

* **Java**
* **JavaFX**
* **JUnit 5**
* **Git**
* **GitHub**

JavaFX is used for the entire graphical interface, including the board, controls, animations, dialogs, images, and media.

JUnit 5 is used for automated testing.

## Project Structure

```text
Monopoly/
├── resources/
│   ├── pirateTheme/
│   ├── standardTheme/
│   └── tucsonTheme/
│
├── src/
│   ├── Cards/
│   ├── Messages/
│   ├── Monopoly/
│   ├── MonopolyTests/
│   ├── Spaces/
│   └── View/
│
├── .classpath
├── .project
└── gameBackground.jpg
```

### `src/Monopoly`

Contains the main game logic and coordination classes:

```text
Board.java
Controller.java
GameSettings.java
Model.java
```

The project separates the game state and rules from the JavaFX user interface.

### `src/View`

Contains the JavaFX frontend.

`View.java` is responsible for displaying the game, handling user interaction, displaying player information, and running animations.

### `src/Spaces`

Contains the objects used to represent players and Monopoly board spaces, including:

```text
Player
AIPlayer
Property
RealEstate
Railroad
Utility
Chance
CommunityChest
Jail
GoToJailSpace
FreeParking
TaxSpace
```

### `src/Cards`

Contains the card and deck system used for Chance and Community Chest cards.

### `src/Messages`

Contains message objects used to communicate game events between different parts of the application.

### `src/MonopolyTests`

Contains the project's JUnit tests.

## Architecture

The application follows a design similar to **Model-View-Controller (MVC)**:

* **Model** — Maintains game state and core Monopoly logic.
* **View** — JavaFX graphical interface and user interaction.
* **Controller** — Handles communication between the view and game model.
* **Board / Spaces** — Represent the Monopoly board and its individual spaces.
* **Messages** — Communicate gameplay events between components.

This separation keeps the game logic independent from much of the graphical interface.

## Getting Started

### Prerequisites

You will need:

* **Java**
* **JavaFX**
* A Java IDE such as **Eclipse**
* **Git** if cloning the project

The repository contains Eclipse `.project` and `.classpath` configuration files and expects a JavaFX user library named `JavaFX`.

## Clone the Repository

```bash
git clone https://github.com/carpentercap/Monopoly.git
```

Move into the project directory:

```bash
cd Monopoly
```

Import the project into your Java IDE and make sure JavaFX is configured before running it.

### Eclipse

Because the repository includes Eclipse project configuration, it can be imported using:

**File → Import → Existing Projects into Workspace**

Then select the cloned `Monopoly` directory.

Make sure a **JavaFX** user library is configured in Eclipse if it is not already available.

## How to Play

1. Launch the application.
2. Choose a theme:

   * Standard
   * Pirate
   * Tucson
3. Configure the number of human and AI players.
4. Configure any optional game rules.
5. Start the game.
6. Roll the dice and move around the board.
7. Purchase properties and collect rent.
8. Trade properties with other players.
9. Build and manage your properties.
10. Continue until the remaining player wins the game.

Games can also be saved and loaded later using `.monopoly` save files.

## Contributors

Created by:

* **Tyler Carpenter**
* **Jarrod Martinez**
* **Alex Myers**
* **Jake Wick**

## Repository

[github.com/carpentercap/Monopoly](https://github.com/carpentercap/Monopoly)

## About the Project

This project demonstrates a larger object-oriented Java application combining game logic, artificial intelligence, persistent game saves, automated testing, and a complete JavaFX desktop interface.

It was developed collaboratively using Git and GitHub.

