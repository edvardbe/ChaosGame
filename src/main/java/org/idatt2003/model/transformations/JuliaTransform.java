package org.idatt2003.model.transformations;

import java.util.Objects;
import org.idatt2003.model.linalg.Complex;
import org.idatt2003.model.linalg.Vector2D;

/**
 * Class for the Julia transformation.
 * This formula describes the transformation:
 * <br>
 * <span style="font-family: Courier">
 *  z → &#177;&radic;&#x305;z&#x305; &#x305;-&#x305; &#x305;c</span>
 *
 */
public class JuliaTransform implements Transform2D {
  private final Complex point;
  private final int sign;

  /**
   * Constructor for JuliaTransform.
   * Creates a new JuliaTransform object with the given complex number and sign.
   *
   * @param point the complex number c
   * @param sign the sign of the transformation
   */
  public JuliaTransform(Complex point, int sign) {
    this.point = point;
    this.sign = sign;
  }

  public Complex getComplex() {
    return point;
  }

  /**
   * JuliaTransform implementation of the transform method in Transform2D.
   * Method to transform a 2D vector using the Julia transformation.
   * This formula describes the transformation:
   * <br>
   * <span style="font-family: Courier">
   *   z → &#177;&radic;&#x305;z&#x305; &#x305;-&#x305; &#x305;c </span>
   *
   * @param point the 2D vector to transform
   * @return a new 2D vector
   */
  @Override
  public Vector2D transform(Vector2D point) {
    Vector2D result = this.point.sqrt(
            point.getX() - this.point.getX(),
            point.getY() - this.point.getY());
    double a = sign * result.getX();
    double b = sign * result.getY();
    return new Vector2D(a, b);
  }


  /**
   * Equals method for JuliaTransform.
   * Compares the point and sign of two JuliaTransform objects.
   * Generated by IntelliJ IDEA.
   *
   * @param o the object to compare
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JuliaTransform that = (JuliaTransform) o;
    return sign == that.sign && Objects.equals(point, that.point);
  }

  /**
   * Hashcode method for JuliaTransform.
   * Generates a hashcode based on the point and sign of the JuliaTransform object.
   * Generated by IntelliJ IDEA.
   *
   * @return the hashcode of the object
   */
  @Override
  public int hashCode() {
    return Objects.hash(point, sign);
  }
}
