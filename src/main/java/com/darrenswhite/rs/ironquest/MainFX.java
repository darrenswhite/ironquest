package com.darrenswhite.rs.ironquest;

import com.darrenswhite.rs.ironquest.gui.MainPane;
import java.net.URL;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Darren White
 */
public class MainFX extends Application {

  /**
   * The title of the application window
   */
  private static final String STAGE_NAME = "Project IronQuest by Ramus";

  /**
   * The main window width
   */
  private static final double APP_WIDTH = 800;

  /**
   * The main window height
   */
  private static final double APP_HEIGHT = 600;

  /**
   * Get the resource at the path
   *
   * @param path The relative path for the resource
   * @return The absolute URL for the resource
   */
  static URL getResource(String path) {
    // Try and find the resource locally (relative in the JAR file)
    if (!path.startsWith("/")) {
      path = '/' + path;
    }
    return MainFX.class.getResource(path);
  }

  /**
   * Main application entry point
   *
   * @param args The program arguments
   */
  public static void main(String[] args) {
    // Launch the application
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    // Initialize and show main app
    MainPane pane = new MainPane();

    // Close the pane and save settings
    stage.setOnCloseRequest(e -> {
      pane.close();
      IronQuest.getInstance().save();
    });
    // Load settings, initialize the pane, and
    // run the program
    stage.setOnShowing(e -> {
      IronQuest.getInstance().load();
      pane.init();
      pane.run();
    });

    // Create a new scene with the default width & height
    Scene scene = new Scene(pane, APP_WIDTH, APP_HEIGHT);

    // Set the primary stage scene and the default title
    stage.setScene(scene);
    stage.setTitle(STAGE_NAME);
    // Show main overview window
    stage.show();
  }
}