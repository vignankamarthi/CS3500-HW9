package cs3500.pawnsboard;

import cs3500.pawnsboard.controller.AIPawnsBoardController;
import cs3500.pawnsboard.controller.HumanPawnsBoardController;
import cs3500.pawnsboard.controller.PawnsBoardController;
import cs3500.pawnsboard.model.PawnsBoardBase;
import cs3500.pawnsboard.model.cards.PawnsBoardBaseCard;
import cs3500.pawnsboard.model.enumerations.PlayerColors;
import cs3500.pawnsboard.player.AIPlayer;
import cs3500.pawnsboard.player.HumanPlayer;
import cs3500.pawnsboard.player.Player;
import cs3500.pawnsboard.player.strategy.PawnsBoardStrategy;
import cs3500.pawnsboard.player.strategy.StrategyFactory;
import cs3500.pawnsboard.player.strategy.moves.PawnsBoardMove;
import cs3500.pawnsboard.player.strategy.types.ChainedStrategy;
import cs3500.pawnsboard.player.strategy.types.ControlBoardStrategy;
import cs3500.pawnsboard.player.strategy.types.FillFirstStrategy;
import cs3500.pawnsboard.player.strategy.types.MaximizeRowScoreStrategy;
import cs3500.pawnsboard.player.strategy.types.MinimaxStrategy;
import cs3500.pawnsboard.view.PawnsBoardGUIView;
import cs3500.pawnsboard.view.PawnsBoardGraphicalView;

import java.io.File;

/**
 * Main class for the Pawns Board game.
 * This class contains the entry point to run the game with a graphical user interface.
 * It supports command-line arguments for customizing the game setup.
 */
public class PawnsBoardGameLevel0 {

  // Constants for board dimensions and hand size
  private static final int DEFAULT_ROWS = 3;
  private static final int DEFAULT_COLUMNS = 5;
  private static final int DEFAULT_HAND_SIZE = 5;

  /**
   * Main method that initializes and starts the game.
   *
   * @param args command line arguments:
   *             args[0] - Path to RED player's deck configuration file
   *             args[1] - Path to BLUE player's deck configuration file
   *             args[2] - RED player type: "human", "strategy1", "strategy2", "strategy3",
   *             "minimax"
   *             args[3] - BLUE player type: "human", "strategy1", "strategy2", "strategy3",
   *             "minimax"
   */
  public static void main(String[] args) {
    // Process command-line arguments
    String redDeckPath;
    String blueDeckPath;
    String redPlayerType;
    String bluePlayerType;

    // Override defaults with command-line arguments if provided
    if (args.length >= 2) {
      redDeckPath = args[0];
      blueDeckPath = args[1];
    } else {
      blueDeckPath = "docs" + File.separator + "BLUE3x5PawnsBoardBaseCompleteDeck.config";
      redDeckPath = "docs" + File.separator + "RED3x5PawnsBoardBaseCompleteDeck.config";
    }

    if (args.length >= 4) {
      redPlayerType = args[2];
      bluePlayerType = args[3];
    } else {
      bluePlayerType = "strategy1";
      redPlayerType = "human";
    }

    // Launch the game
    createGame(redDeckPath, blueDeckPath, redPlayerType, bluePlayerType);
  }

  /**
   * Starts the game with the specified parameters.
   *
   * @param redDeckPath    path to RED player's deck configuration file
   * @param blueDeckPath   path to BLUE player's deck configuration file
   * @param redPlayerType  RED player type
   * @param bluePlayerType BLUE player type
   */
  private static void createGame(String redDeckPath, String blueDeckPath,
                                 String redPlayerType, String bluePlayerType) {
    try {
      // Create the model
      PawnsBoardBase<PawnsBoardBaseCard> model = new PawnsBoardBase<>();

      // Initialize the game with default dimensions and hand size
      model.startGame(DEFAULT_ROWS, DEFAULT_COLUMNS, redDeckPath, blueDeckPath, DEFAULT_HAND_SIZE);

      // Create players based on specified types
      Player<PawnsBoardBaseCard> redPlayer = createPlayerSettings(redPlayerType, PlayerColors.RED);
      Player<PawnsBoardBaseCard> bluePlayer = createPlayerSettings(bluePlayerType,
              PlayerColors.BLUE);

      // Create views for each player
      PawnsBoardGraphicalView redView = new PawnsBoardGraphicalView(model);
      PawnsBoardGraphicalView blueView = new PawnsBoardGraphicalView(model);

      // Set the view player for each view
      redView.setViewPlayer(PlayerColors.RED);
      blueView.setViewPlayer(PlayerColors.BLUE);

      // Create controllers for each player
      PawnsBoardController redController = createController(model, redPlayer, redView);
      PawnsBoardController blueController = createController(model, bluePlayer, blueView);

      // Initialize controllers
      redController.initialize(model, redView);
      blueController.initialize(model, blueView);

      // Configure views
      redView.setSize(1200, 900);
      blueView.setSize(1200, 900);

      // Position windows side by side
      redView.setPosition(50, 50);
      blueView.setPosition(1300, 50);

      // Make views visible
      redView.setVisible(true);
      blueView.setVisible(true);

    } catch (Exception e) {
      throw new IllegalStateException("Game configuration is invalid", e);
    }
  }

  /**
   * Creates a player of the specified type.
   *
   * @param playerType the type of player to create
   * @param color      the player's color
   * @return a new Player instance
   */
  private static Player<PawnsBoardBaseCard> createPlayerSettings(String playerType,
                                                                 PlayerColors color) {
    switch (playerType.toLowerCase()) {
      case "human":
        return new HumanPlayer<>(color);
      case "strategy1":
        return new AIPlayer<>(color, new FillFirstStrategy<>());
      case "strategy2":
        return new AIPlayer<>(color, new MaximizeRowScoreStrategy<>());
      case "strategy3":
        return new AIPlayer<>(color, new ControlBoardStrategy<>());
      case "minimax":
        // For minimax, use a simple strategy for the opponent model
        PawnsBoardStrategy<PawnsBoardBaseCard, PawnsBoardMove> opponentStrategy =
                new FillFirstStrategy<>();
        return new AIPlayer<>(color, new MinimaxStrategy<>(opponentStrategy));
      case "chained":
        // Create a chained strategy using the strategy factory
        StrategyFactory<PawnsBoardBaseCard> factory = new StrategyFactory<>();
        factory.createMaximizeRowScoreStrategy()
                .addControlBoard()
                .addFillFirst()
                .setFallbackStrategy(new FillFirstStrategy<>());
        return new AIPlayer<>(color, new ChainedStrategy<>(factory));
      default:
        // Default to FillFirst strategy if unknown type
        System.out.println("Unknown player type: " + playerType + ", defaulting to strategy1");
        return new AIPlayer<>(color, new FillFirstStrategy<>());
    }
  }

  /**
   * Creates a controller appropriate for the player type.
   *
   * @param model  the game model
   * @param player the player
   * @param view   the player's view
   * @return a controller for the player
   */
  private static PawnsBoardController createController(
          PawnsBoardBase<PawnsBoardBaseCard> model,
          Player<PawnsBoardBaseCard> player,
          PawnsBoardGUIView view) {

    if (player.isHuman()) {
      return new HumanPawnsBoardController<>(model, (HumanPlayer<PawnsBoardBaseCard>) player, view);
    } else if (!player.isHuman()) {
      return new AIPawnsBoardController<>(model, (AIPlayer<PawnsBoardBaseCard>) player, view);
    } else {
      throw new IllegalArgumentException("Unsupported player type: " + player.getClass().getName());
    }
  }


}