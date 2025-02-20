package org.idatt2003.model.chaos;

import org.idatt2003.model.linalg.Matrix2x2;
import org.idatt2003.model.linalg.Vector2D;
import org.idatt2003.model.transformations.AffineTransform2D;

/**
 * A class representing a canvas for the chaos game.
 * The canvas is a 2D grid of pixels, where each pixel is represented by an integer value.
 * The canvas has a coordinate system, where the origin is in the lower left corner of the canvas,
 * and the x-axis and y-axis are horizontal and vertical, respectively.
 * The canvas has a minimum and maximum coordinate, which defines the extent of the canvas.
 * The canvas also has an Affine transformation that maps coordinates, {@link Vector2D},
 * to indices in the canvas array. This is used to map points to pixels in the canvas.
 */
public class ChaosCanvas {
  private int width;
  private int height;
  private final double[][] canvas;
  private Vector2D minCoords;
  private Vector2D maxCoords;
  private AffineTransform2D transformCoordsToIndices;

  /**
   * Creates a new ChaosCanvas with the given width, height, minimum and maximum coordinates.
   * The canvas is initialized with all pixel values set to 0.
   * The Affine transformation is calculated based on the width, height,
   * minimum and maximum coordinates.
   *
   * @param width The width of the canvas
   *
   * @param height The height of the canvas
   *
   * @param minCoords The minimum coordinates of the canvas
   *
   * @param maxCoords The maximum coordinates of the canvas
   *
   */

  public ChaosCanvas(int width,
                     int height, Vector2D minCoords,
                     Vector2D maxCoords) {
    this.width = width;
    this.height = height;
    this.minCoords = minCoords;
    this.maxCoords = maxCoords;
    this.transformCoordsToIndices = calculateTransformCoordsToIndices();
    this.canvas = new double[height][width];
  }

  public double[][] getCanvasArray() {
    return canvas;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public Vector2D getMinCoords() {
    return minCoords;
  }

  public Vector2D getMaxCoords() {
    return maxCoords;
  }

  public void setMinCoords(Vector2D minCoords) {
    this.minCoords = minCoords;
  }

  public void setMaxCoords(Vector2D maxCoords) {
    this.maxCoords = maxCoords;
  }

  public void setTransformCoordsToIndices() {
    this.transformCoordsToIndices = calculateTransformCoordsToIndices();
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setHeight(int height) {
    this.height = height;
  }


  public AffineTransform2D getTransformCoordsToIndices() {
    return transformCoordsToIndices;
  }

  /**
   * Calculates the Affine transformation that maps coordinates to indices in the canvas array.
   * The transformation is calculated based on the width, height, minimum and maximum coordinates.
   *
   * @return The Affine transformation that maps coordinates to indices in the canvas array
   */
  private AffineTransform2D calculateTransformCoordsToIndices() {
    return new AffineTransform2D(
            new Matrix2x2(
                    0.0, ((height - 1) / (minCoords.getY() - maxCoords.getY())),
                    ((width - 1) / (maxCoords.getX() - minCoords.getX())), 0.0),

            new Vector2D(
                    ((height - 1.0) * maxCoords.getY()) / (maxCoords.getY() - minCoords.getY()),
                    ((width - 1.0) * minCoords.getX()) / (minCoords.getX() - maxCoords.getX())
            ));

  }

  /**
   * Increments the pixel value at the given point by 1.
   * If the point is outside the canvas, the method does nothing.
   *
   * @param point The point to put a pixel at
   */
  public void putPixelChaos(Vector2D point) {
    Vector2D indices = transformCoordsToIndices.transform(point);
    int x = (int) indices.getX();
    int y = (int) indices.getY();
    if (x >= 0 && x < height && y >= 0 && y < width) {
      canvas[x][y] += 1;
    }
  }

  /**
   * Sets the pixel value at the given point to the given value.
   * If the point is outside the canvas, the method does nothing.
   *
   * @param x The x-coordinate of the point
   *
   * @param y The y-coordinate of the point
   *
   * @param iter The iteration value to set the pixel to
   */
  public void putPixelExplore(int x, int y, double iter) {
    if (y >= 0 && y < height && x >= 0 && x < width) {
      canvas[y][x] = iter;
    }
  }

  /**
   * Transforms indices in the canvas array to coordinate.
   * The method calculates the x-coordinate and y-coordinate based on the indices,
   * the width, height, minimum and maximum coordinates.
   * Used in the ExploreGame class to map indices to coordinate that iterates through each pixel.
   *
   * @param i The x-coordinate in the canvas array
   *
   * @param j The y-coordinate in the canvas array
   *
   * @return The coordinates of the point
   */
  public Vector2D transformIndicesToCoords(int i, int j) {
    double x = (i * (maxCoords.getX() - minCoords.getX()) / (width - 1)) + minCoords.getX();
    double y = (j * (minCoords.getY() - maxCoords.getY()) / (height - 1)) + maxCoords.getY();
    return new Vector2D(x, y);
  }

  /**
   * Clears the canvas by setting all pixel values to 0.
   */
  public void clearCanvas() {
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        canvas[i][j] = 0;
      }
    }
  }


  /**
   * Returns the pixel value at the given point.
   * If the point is outside the canvas, the method returns 0.
   *
   * @param point The point to get the pixel value at
   *
   * @return The pixel value at the given point
   */
  public double getPixel(Vector2D point) {
    Vector2D indices = transformCoordsToIndices.transform(point);
    int x = (int) indices.getX();
    int y = (int) indices.getY();
    if (x >= 0 && x < height && y >= 0 && y < width) {
      return canvas[x][y];
    }
    return 0.0;
  }
}
