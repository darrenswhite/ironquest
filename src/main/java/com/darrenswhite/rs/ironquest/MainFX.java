package com.darrenswhite.rs.ironquest;

import com.darrenswhite.rs.ironquest.gui.MainPane;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Darren S. White
 */
public class MainFX extends Application {

  private static final Logger LOG = LogManager.getLogger(MainFX.class);

  private static final String STAGE_NAME = "Project IronQuest by Ramus";

  private static final double APP_WIDTH = 800;

  private static final double APP_HEIGHT = 600;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    MainPane pane = new MainPane();

    stage.setOnCloseRequest(e -> {
      pane.close();
      try {
        IronQuest.getInstance().save();
      } catch (IOException ex) {
        LOG.error("Failed to save", ex);
      }
    });
    stage.setOnShowing(e -> {
      try {
        IronQuest.getInstance().load();
      } catch (IOException ex) {
        LOG.error("Failed to load", ex);
      }
      pane.init();
      pane.run();
    });

    Scene scene = new Scene(pane, APP_WIDTH, APP_HEIGHT);

    stage.setScene(scene);
    stage.setTitle(STAGE_NAME);
    stage.show();
  }
}
