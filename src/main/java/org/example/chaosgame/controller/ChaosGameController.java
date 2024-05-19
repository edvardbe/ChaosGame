package org.example.chaosgame.controller;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.example.chaosgame.model.chaos.*;
import org.example.chaosgame.controller.observer.GameObserver;
import org.example.chaosgame.controller.observer.PageObserver;
import org.example.chaosgame.controller.observer.PageSubject;
import org.example.chaosgame.model.linalg.Complex;
import org.example.chaosgame.model.linalg.Vector2D;
import org.example.chaosgame.model.transformations.AffineTransform2D;
import org.example.chaosgame.model.transformations.JuliaTransform;
import org.example.chaosgame.model.transformations.Transform2D;
import org.example.chaosgame.view.ChaosPage;
import org.example.chaosgame.view.components.AlertUtility;
import org.example.chaosgame.view.components.CreateFractalDialog;
import org.example.chaosgame.view.components.MinMaxDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ChaosGameController implements GameObserver, PageSubject {
  private final ChaosGame chaosGame;
  private final ChaosPage chaosPage;
  private final List<PageObserver> pageObservers;
  private static final int WIDTH = 1200;
  private static final int HEIGHT = 800;
  private Canvas canvas;

  public ChaosGameController() {
    this.chaosGame = ChaosGame.getInstance(Objects.requireNonNull(
                    ChaosGameDescriptionFactory.get(ChaosGameType.SIERPINSKI)),
            WIDTH, HEIGHT);
    this.chaosPage = new ChaosPage(this);
    setCanvas(chaosPage.getGraphicsContex().getCanvas());
    this.pageObservers = new ArrayList<>();
    chaosGame.registerObserver(this);
  }

  public ChaosGame getChaosGame() {
    return chaosGame;
  }

  public ChaosPage getChaosPage() {
    return chaosPage;
  }

  private void updateChaosGame(ChaosGameDescription description) {
    chaosGame.setChaosGameDescription(description);
  }

  public void gameSelection(String selectedGame) {
    if (selectedGame == null || selectedGame.trim().isEmpty()) {
      AlertUtility.showErrorDialog("Invalid input", "Please select a game.");
    } else {
      updateChaosGame(ChaosGameDescriptionFactory.get(ChaosGameType.valueOf(selectedGame)));
    }
  }

  public void runStepsValidation(TextField stepsField) {
    String input = stepsField.getText();
    try {
      int steps = Integer.parseInt(input);
      if (steps < 1 || steps > 10000000) {
        throw new NumberFormatException();
      }
      chaosGame.setSteps(steps);
      chaosGame.addTotalSteps(steps);
      chaosGame.runSteps();
      stepsField.getStyleClass().remove("text-field-invalid");
    } catch (NumberFormatException ex) {
      stepsField.clear();
      stepsField.getStyleClass().add("text-field-invalid");
      AlertUtility.showErrorDialog("Invalid input", "Please enter a number between 1 - 10 000 000.");
    }
  }

  public void setMaxMinCoords() {
    MinMaxDialog dialog = new MinMaxDialog();
    Optional<Pair<Vector2D, Vector2D>> result = dialog.showAndWait();

    if (result.isPresent()) {
      Pair<Vector2D, Vector2D> minMax = result.get();
      updateChaosGame(new ChaosGameDescription(
              minMax.getKey(), minMax.getValue(),
              chaosGame.getDescription().getTransforms()));
    }
  }

  public void openFromFile() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
    File selectedFile = fileChooser.showOpenDialog(null);

    if (selectedFile != null) {
      try {
        ChaosGameFileHandler fileHandler = new ChaosGameFileHandler();
        updateChaosGame(fileHandler.readFromFile(selectedFile.getAbsolutePath()));
      } catch (NumberFormatException ex) {
        AlertUtility.showErrorDialog("Error", "Invalid input in the file. Try another file.");
      } catch (IOException e) {
        AlertUtility.showErrorDialog("Error", "Could not read the file. Try another file.");
      }
    }
  }

  public void updateFractalColor(Color color) {
    chaosPage.setFractalColor(color);
    chaosPage.updateCanvas(chaosGame.getCanvas());
  }

  public void createOwnFractal() {
    CreateFractalDialog dialog = new CreateFractalDialog();
    Optional<Object> result = dialog.showAndWait();

    if (result.isPresent()) {
      Object fractalData = result.get();

      if (fractalData instanceof List) {
        List<AffineTransform2D> transformations = (List<AffineTransform2D>) fractalData;
        List<Transform2D> transforms = new ArrayList<>(transformations);
        updateChaosGame(new ChaosGameDescription(
                new Vector2D(0, 0),
                new Vector2D(1.0, 1.0),
                transforms));
      } else if (fractalData instanceof Pair) {
        Pair<String, String> userInput = (Pair<String, String>) fractalData;
        double real = Double.parseDouble(userInput.getKey());
        double imaginary = Double.parseDouble(userInput.getValue());

        if (real < -1 || real > 1 || imaginary < -1 || imaginary > 1) {
          AlertUtility.showErrorDialog("Invalid input", "Please enter a double between -1 and 1. No letters are allowed.");
        } else {
          updateChaosGame(new ChaosGameDescription(
                  new Vector2D(-1.6, -1),
                  new Vector2D(1.6, 1.0),
                  List.of(new JuliaTransform(new Complex(real, imaginary), 1))));
        }
      }
    }
  }

  public void updateJuliaValue(String partType, double value) {
    JuliaTransform juliaTransform = (JuliaTransform) chaosGame.getDescription().getTransforms().getFirst();
    double realPart = partType.equals("real") ? value : juliaTransform.getComplex().getX();
    double imaginaryPart = partType.equals("imaginary") ? value : juliaTransform.getComplex().getY();

    updateChaosGame(new ChaosGameDescription(
            new Vector2D(-1.6, -1),
            new Vector2D(1.6, 1.0),
            List.of(new JuliaTransform(new Complex(realPart, imaginaryPart), 1))));
    chaosGame.runSteps();
  }

  public void saveFractal() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
    File selectedFile = fileChooser.showSaveDialog(null);

    if (selectedFile != null) {
      try {
        ChaosGameFileHandler fileHandler = new ChaosGameFileHandler();
        ChaosGameDescription description = chaosGame.getDescription();
        fileHandler.writeToFile(description, selectedFile.getAbsolutePath());
      } catch (IOException ex) {
        AlertUtility.showErrorDialog("Error", "Could not save file. Try again.");
      }
    }
  }

  public void resetGame() {
    //chaosGame.resetTotalSteps();
    chaosGame.setChaosGameDescription(null);
    update();
    chaosPage.clearCanvas();
  }

  public void homeButtonClicked() {
    notifyObservers(chaosPage);
  }

  @Override
  public void update() {
    chaosPage.updateInformation(chaosGame.getDescription().getTransforms().getFirst(),
            chaosGame.getTotalSteps(),
            chaosGame.getDescription().getMinCoords(),
            chaosGame.getDescription().getMaxCoords());
    chaosPage.updateCanvas(chaosGame.getCanvas());
  }

  @Override
  public void registerObserver(PageObserver observer) {
    pageObservers.add(observer);
  }

  @Override
  public void removeObserver(PageObserver observer) {
    pageObservers.remove(observer);
  }

  @Override
  public void notifyObservers(Node chaosPage) {
    for (PageObserver pageObserver : pageObservers) {
      pageObserver.update(chaosPage);
    }
  }

  public void setCanvas(Canvas canvas) {
    this.canvas = canvas;
  }

  public void setBind(StackPane mainPane) {
    canvas.widthProperty().bind(mainPane.widthProperty().multiply(0.85));
    canvas.heightProperty().bind(mainPane.heightProperty().multiply(0.85));
  }
}
