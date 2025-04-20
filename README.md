# Pawns Board Game

## Overview

The Pawns Board game is a two-player card placement strategy game played on a rectangular grid.
Players take turns placing cards from their hands onto cells with their pawns, with each card having
unique influence patterns that affect the board state. The game implements a complete model of the
game rules described in the assignment, including card placement mechanics, influence patterns, pawn
ownership, and scoring.

### Key Assumptions and Design Goals

- The game supports different board sizes, though rows must be positive and columns must be odd
- Players can be human or AI-controlled (interface provided, implementation to come)
- The game is extensible to support different types of cards and influence mechanics
- Cards are loaded from configuration files, allowing for customizable decks

## Quick Start

### Running the Game

The easiest way to start the game is by running the `PawnsBoardGame` class, which will set up a GUI
version of the game:

```java
public static void main(String[] args) {
  // Create the model
  PawnsBoardBase<PawnsBoardBaseCard> model = new PawnsBoardBase<>();

  // Get the deck configuration file paths
  String redDeckPath = "docs" + File.separator + "RED3x5PawnsBoardBaseCompleteDeck.config";
  String blueDeckPath = "docs" + File.separator + "BLUE3x5PawnsBoardBaseCompleteDeck.config";

  // Initialize the game with 3 rows, 5 columns, and 5 cards per hand
  model.startGame(3, 5, redDeckPath, blueDeckPath, 5);

  // Create the view and controller
  PawnsBoardGUIView view = new PawnsBoardGraphicalView(model);
  PawnsBoardController controller = new PawnsBoardStubController();
  controller.initialize(model, view);

  // Make the view visible
  view.setVisible(true);
}
```

### Game Controls

- **Select a card**: Click on a card in your hand at the bottom of the screen
- **Select a cell**: Click on a cell on the game board where you want to place the card
- **Confirm your move**: Press the ENTER key
- **Pass your turn**: Press the P key
- **Deselect a card/cell**: Click on it again

### Gameplay Basics

The game alternates between RED and BLUE players. On your turn:

1. Select a card from your hand that you want to play
2. Select a cell on the board where you have enough of your pawns to cover the card's cost
3. Press ENTER to place the card
4. The card's influence pattern will affect surrounding cells
5. The player with higher row scores wins

## Game Visualization

You can see a running game by executing the `PawnsBoardGame` class main method. The GUI provides a
visual representation of the board, cards, and game state.

## Key Components

### Model

The model manages the game state and enforces the rules of Pawns Board. It's built as a hierarchical
set of interfaces and implementations:

- **PawnsBoard Interface**: Defines all operations for the game, including setup, turn management,
  board queries, and scoring
- **AbstractPawnsBoard**: Provides common functionality for game state tracking and player turn
  management
- **PawnsBoardBase**: Concrete implementation with complete game logic for a rectangular grid board

### View

The view provides a text-based rendering of the game state:

- **PawnsBoardView Interface**: Defines methods for rendering the game state
- **PawnsBoardTextualView**: Implements a console-friendly text representation of the board, hands,
  and scores

### Player Framework

The player framework defines abstractions for different player types:

- **Player Interface**: Unified interface for all player types
- **HumanPlayer**: Implementation for human players, interacting via controller/UI
- **AIPlayer**: Stub implementation for computer players (to be completed)

## Key Subcomponents

### Card System

- **Card Interface**: Defines card properties (name, cost, value score, and influence grid)
- **PawnsBoardBaseCard**: Standard card implementation
- **DeckBuilder**: Creates and validates decks from configuration files
- **CardFactory**: Creates cards using the Builder pattern
- **CardReader**: Reads card definitions from files

### Board Representation

- **PawnsBoardCell Interface**: Represents a single cell on the board
- **PawnsBoardBaseCell**: Concrete cell implementation
- **CellContent Enum**: Defines possible cell contents (EMPTY, PAWNS, CARD)
- **PlayerColors Enum**: Represents player ownership (RED, BLUE)

### Exception Handling

Several custom exceptions handle different error cases:

- **IllegalAccessException**: For insufficient resources
- **IllegalOwnerException**: For ownership violations
- **IllegalCardException**: For invalid card operations
- **InvalidDeckConfigurationException**: For deck loading errors

## Source Organization

The codebase is organized into the following package structure:

- **cs3500.pawnsboard.model**: Core game model
    - **cards**: Card-related classes
        - **factory**: Card creation
        - **reader**: Card loading from files
        - **deckbuilder**: Deck creation and validation
    - **cell**: Cell representation
    - **enumerations**: Game enums
    - **exceptions**: Custom exception types
- **cs3500.pawnsboard.view**: View components
- **cs3500.pawnsboard.player**: Player abstractions
- **docs**: Configuration files and documentation
    - Card deck configurations
    - Player interface design documentation
- **PawnsBoard.java**: Main class demonstrating gameplay

## Testing

The project includes comprehensive tests:

- Unit tests for individual components
- Integration tests for the model-view interaction
- Mock model implementations for controlled testing

### GUI Component Testing

The `PawnsBoardGameTest` provides a comprehensive testing tool for GUI components. This test class
allows interactive visualization and testing of different game states for the Graphical User
Interface.

#### How to Use PawnsBoardGameTest

When you run the `PawnsBoardGameTest` class, you'll be presented with a dialog box offering three
different test scenarios:

1. **Game Start**
    - Displays the initial game state
    - Shows the board at the beginning of the game
    - Highlights Red player's initial turn

2. **Blue's Turn with Selected Card**
    - Demonstrates the view from Blue player's perspective
    - Shows a card pre-selected in Blue's hand
    - Useful for testing hand rendering and selection mechanics

3. **Mid-Game State**
    - Displays a more complex game board
    - Shows multiple cards played by both Red and Blue players
    - Demonstrates board state rendering with multiple game elements

##### Running the Test

To run the test:

1. Open the `PawnsBoardGameTest` class
2. Run the `main()` method
3. Select the desired test scenario from the dialog
4. Observe the rendered game state and interactions

This testing approach allows developers to:

- Verify GUI rendering at different game stages
- Test view components without full game logic
- Quickly validate visual representations
- Understand how different game states are displayed

#### Recommended Testing Flow

- Start with "Game Start" to verify initial rendering
- Progress to "Blue's Turn with Selected Card" to test hand and selection mechanics
- Conclude with "Mid-Game State" to validate complex board rendering

To run the tests, use JUnit4 through your IDE or build system.

There are also screenshots in the 'test/cs3500/pawnsboard/view/TestingImages' directory.

## Changes for Part 2

In this iteration, we made several important changes to improve the model design and provide better
support for the view and future AI players:

1. **Read-Only Interface**:
    - The existing PawnsBoard interface now extends a new `ReadOnlyPawnsBoard` interface
    - `ReadOnlyPawnsBoard` contains all observation methods for viewing the game state
    - This separation ensures that views can only read the game state, not modify it

2. **Added Missing Functionality**:

    - **Move Legality Checking**:
        - Added `isLegalMove(int cardIndex, int row, int col)` method to check if a move is legal
          without actually making it
        - Previously, the only way to check move legality was to attempt the move and catch
          exceptions
        - This addition helps AI strategies evaluate possible moves without modifying the game state
        - Implemented in both `AbstractPawnsBoard` and `PawnsBoardMock` classes

    - **Board Copying**:
        - Added `copy()` method to create a deep copy of the board state
        - This allows AI players to simulate potential moves and evaluate their outcomes
        - The copy is completely independent and changes to it don't affect the original game
        - Implemented in both `PawnsBoardBase` and `PawnsBoardMock` classes

### Required Strategy Implementations

1. **Fill First Strategy** (`FillFirstStrategy`)
    - Location: `src/cs3500/pawnsboard/player/strategy/types/FillFirstStrategy.java`
    - Purpose: Implements a simple but reliable strategy that finds the first legal move available
    - Behavior:
        - Systematically searches through cards and board positions
        - Uses a directional search pattern based on player color (RED searches left-to-right, BLUE
          searches right-to-left)
        - INFALLIBLE: Always produces a move, defaulting to a pass move if no legal placement is
          found
    - Test Location: `test/cs3500/pawnsboard/player/strategy/types/FillFirstStrategyTest.java`

2. **Maximize Row Score Strategy** (`MaximizeRowScoreStrategy`)
    - Location: `src/cs3500/pawnsboard/player/strategy/types/MaximizeRowScoreStrategy.java`
    - Purpose: Takes a smarter approach by placing cards to gain advantage in row-based scoring
    - Behavior:
        - Prioritizes improving rows where the player is behind or tied with the opponent
        - Simulates potential moves to find those that will increase the player's row score above
          the opponent's
        - Examines rows in top-to-bottom order for methodical decision making
        - INFALLIBLE: Always produces a move, defaulting to a pass move if no score-improving move
          is found
    - Test Location:
      `test/cs3500/pawnsboard/player/strategy/types/MaximizeRowScoreStrategyTest.java`

## Extra Credit

### Strategy Implementations

We have enhanced the PawnsBoard game strategies by implementing additional strategic approaches and
creating a flexible strategy composition mechanism.

#### New Strategies

1. **Control Board Strategy** (`ControlBoardStrategy`)
    - Location: `src/cs3500/pawnsboard/player/strategy/types/ControlBoardStrategy.java`
    - Purpose: Selects moves that maximize the number of cells controlled by the player
    - Test Location: `test/cs3500/pawnsboard/player/strategy/types/ControlBoardStrategyTest.java`

2. **Minimax Strategy** (`MinimaxStrategy`)
    - Location: `src/cs3500/pawnsboard/player/strategy/types/MinimaxStrategy.java`
    - Purpose: Simulates potential opponent moves and chooses moves that minimize the opponent's
      best possible response
    - Test Location: `test/cs3500/pawnsboard/player/strategy/types/MinimaxStrategyTest.java`

### Strategy Composition

We have implemented a flexible strategy composition mechanism using two key components:

1. **Strategy Factory** (`StrategyFactory`)
    - Location: `src/cs3500/pawnsboard/player/strategy/StrategyFactory.java`
    - Allows dynamic chaining and combination of strategies

2. **Chained Strategy** (`ChainedStrategy`)
    - Location: `src/cs3500/pawnsboard/player/strategy/types/ChainedStrategy.java`
    - Enables running multiple strategies in sequence, falling back to subsequent strategies if
      earlier ones fail

### Demonstration

Strategy composition is demonstrated in the strategy tests, particularly in
`ChainedStrategyTest.java`. Key examples include:

```java
// Example of creating a complex strategy chain
ChainedStrategy<PawnsBoardBaseCard> chainedStrategy = new ChainedStrategy<>(
                strategyFactory.createFillFirstStrategy()
                        .addMaximizeRowScore()
                        .addControlBoard()
                        .addMinimax(opponentStrategy)
        );
```

## Changes for Part 3

In this iteration, we implemented a complete MVC (Model-View-Controller) architecture for the Pawns
Board game, focusing on creating a flexible and extensible player and controller system. The key
additions and changes include:

### New Listeners Class

* **Model Status Listener (`ModelStatusListener`)**:
    - Location: `src/cs3500/pawnsboard/controller/listeners/ModelStatusListener.java`
    - Purpose: Provides a communication mechanism for the model to notify controllers about game
      state changes
    - Methods:
        - `onTurnChange(PlayerColors newCurrentPlayer)`
        - `onGameOver(PlayerColors winner, int[] finalScores)`
        - `onInvalidMove(String errorMessage)`

### New Controller Classes

1. **Abstract Pawns Board Controller (`AbstractPawnsBoardController`)**:
    - Location: `src/cs3500/pawnsboard/controller/AbstractPawnsBoardController.java`
    - Purpose: Provides common functionality for both human and AI player controllers
    - Key Features:
        - Handles model status listener registration
        - Manages turn status and view updates
        - Provides abstract methods for game-specific behaviors

2. **Human Pawns Board Controller (`HumanPawnsBoardController`)**:
    - Location: `src/cs3500/pawnsboard/controller/HumanPawnsBoardController.java`
    - Purpose: Manages user interactions for human players
    - Key Features:
        - Handles card and cell selections
        - Manages move confirmation and passing
        - Displays error and game-over messages

3. **AI Pawns Board Controller (`AIPawnsBoardController`)**:
    - Location: `src/cs3500/pawnsboard/controller/AIPawnsBoardController.java`
    - Purpose: Automates move selection and execution for AI players
    - Key Features:
        - Uses player strategies to make moves
        - Automatically executes moves with a delay
        - Handles AI-specific error and game-over scenarios

### Fully Complete Player Implementations

#### (Player implementations were placeholders in HW6)

1. **Human Player (`HumanPlayer`)**:
    - Location: `src/cs3500/pawnsboard/player/HumanPlayer.java`
    - Purpose: Represents a human player with error tracking and state management
    - Key Features:
        - Tracks invalid move state
        - Provides methods for error handling
        - Supports human-specific interaction flow

2. **AI Player (`AIPlayer`)**:
    - Location: `src/cs3500/pawnsboard/player/AIPlayer.java`
    - Purpose: Represents an AI player that uses strategies to make decisions
    - Key Features:
        - Supports different strategy implementations
        - Automatically makes moves based on strategy
        - Handles error scenarios and game-end notifications

### View Enhancements

1. **PawnsBoardGUIView Modifications**:
    - Updated to support more systematic event handling
    - Added methods for player-specific view management
    - Improved support for different player types

### Key Design Changes

- Implemented the Observer pattern for communication between model, view, and controller
- Created a flexible player system supporting both human and AI players
- Enhanced game flow management with turn-based and player-specific controllers, and
  strategic delays for the AI player to imporve game flow.

These changes provide a robust, extensible framework for the Pawns Board game, allowing for easy
addition of new player types, strategies, and game features.

### Command Line Instructions to Ensure Smooth Submission

zip -rX -D submission.zip src/ test/ docs/ homework5.jar README.md -x '*/.*' '*/__MACOSX/*'


