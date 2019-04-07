package com.darrenswhite.rs.ironquest;

import java.io.IOException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Darren S. White
 */
public class Main {

  private static final Logger LOG = LogManager.getLogger(Main.class);

  public static void main(String[] args) throws IOException {
    new JFXPanel();

    IronQuest quest = IronQuest.getInstance();

    if (args.length == 1) {
      String name = args[0];

      LOG.debug("Setting player name as: {}", name);

      quest.getPlayer().setName(name);
    }

    quest.load();

    Platform.runLater(() -> {
      quest.run();

//      for (Action a : quest.getActions()) {
//        LOG.info(a.getMessage());
//      }
    });

    Platform.exit();
  }
}
