import java.awt.Color;
import java.awt.Graphics2D;

public class Board {
  public static final int SQUARE_SIZE = 100;
  public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;

  final int MAX_ROW = 8;
  final int MAX_COL = 8;

  public void draw(Graphics2D g2) {
    boolean whiteSquare = true;
    for (int row = 0; row < MAX_ROW; row++) {
      for (int col = 0; col < MAX_COL; col++) {
        Color squareColor = whiteSquare ? new Color(210, 165, 125) : new Color(175, 115, 70);
        g2.setColor(squareColor);
        g2.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        whiteSquare = !whiteSquare;
      }
      whiteSquare = !whiteSquare;
    }
  }
}
