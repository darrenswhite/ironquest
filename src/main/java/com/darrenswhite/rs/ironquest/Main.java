package com.darrenswhite.rs.ironquest;

import com.darrenswhite.rs.ironquest.action.Action;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Darren White
 */
public class Main {

  /**
   * The logger
   */
  private static final Logger LOG = LogManager.getLogger(Main.class);

  /**
   * Main entry point
   *
   * @param args The program arguments
   */
  public static void main(String[] args) {
    // Initialize JavaFX toolkit
    new JFXPanel();

    // Create a new instance
    IronQuest quest = IronQuest.getInstance();

    // First arg is player name
    if (args.length == 1) {
      String name = args[0];

      LOG.debug("Setting player name as: {0}", name);

      quest.setPlayer(name);
    }

    // Run on FX thread
    Platform.runLater(() -> {
      quest.run();

      // Print the actions
      for (Action a : quest.getActions()) {
        LOG.info(a.getMessage());
      }
    });

    // Terminate JavaFX
    Platform.exit();
  }
}