import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Piece {
  public PieceType type;
  public BufferedImage image;
  public int x;
  public int y;
  public int col;
  public int row;
  public int prevCol;
  public int prevRow;
  public PieceColor color;
  public Piece hittingPiece;
  public boolean moved = false;
  public boolean twoStepped = false;

  Piece(int row, int col, PieceColor color) {
    this.row = row;
    this.col = col;
    this.color = color;

    x = getX(col);
    y = getY(row);

    prevCol = col;
    prevRow = row;
  }

  public BufferedImage getImage(String path) {
    try {
      return ImageIO.read(getClass().getResourceAsStream(path));
    } catch (IOException exception) {
      exception.printStackTrace();
      return null;
    }
  }

  public int getX(int col) {
    return col * Board.SQUARE_SIZE;
  }

  public int getY(int row) {
    return row * Board.SQUARE_SIZE;
  }

  public int getRow(int y) {
    return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
  }

  public int getCol(int x) {
    return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
  }

  public void draw(Graphics2D g) {
    g.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
  }

  public void updatePosition() {
    if (type == PieceType.Pawn) {
      if (Math.abs(row - prevRow) == 2) {
        twoStepped = true;
      }
    }

    x = getX(col);
    y = getY(row);
    prevCol = getCol(x);
    prevRow = getRow(y);
    moved = true;
  }

  public boolean canMove(int targetRow, int targetCol) {
    return false;
  }

  public boolean isWithinBoard(int targetRow, int targetCol) {
    return targetRow >= 0 && targetRow <= 7 && targetCol >= 0 && targetCol <= 7;
  }

  public void resetPosition() {
    row = prevRow;
    col = prevCol;

    x = getX(col);
    y = getY(row);
  }

  public Piece getHittingPiece(int targetRow, int targetCol) {
    for (Piece piece : GamePanel.simulationPieces) {
      if (piece.row == targetRow && piece.col == targetCol && piece != this)
        return piece;
    }

    return null;
  }

  public boolean isValidSquare(int targetRow, int targetCol) {
    hittingPiece = getHittingPiece(targetRow, targetCol);

    if (hittingPiece == null) {
      return true;
    }

    if (hittingPiece.color != this.color) {
      return true;
    }

    hittingPiece = null;

    return false;
  }

  public boolean isSameSquare(int targetRow, int targetCol) {
    return targetRow == prevRow && targetCol == prevCol;
  }

  public boolean pieceIsOnStraightLine(int targetRow, int targetCol) {
    for (int col = prevCol - 1; col > targetCol; col--) {
      for (Piece piece : GamePanel.simulationPieces) {
        if (piece.col == col && piece.row == targetRow) {
          hittingPiece = piece;
          return true;
        }
      }
    }

    for (int col = prevCol + 1; col < targetCol; col++) {
      for (Piece piece : GamePanel.simulationPieces) {
        if (piece.col == col && piece.row == targetRow) {
          hittingPiece = piece;
          return true;
        }
      }
    }

    for (int row = prevRow - 1; row > targetRow; row--) {
      for (Piece piece : GamePanel.simulationPieces) {
        if (piece.col == targetCol && piece.row == row) {
          hittingPiece = piece;
          return true;
        }
      }
    }

    for (int row = prevRow + 1; row < targetRow; row++) {
      for (Piece piece : GamePanel.simulationPieces) {
        if (piece.col == targetCol && piece.row == row) {
          hittingPiece = piece;
          return true;
        }
      }
    }

    return false;
  }

  public boolean pieceIsOnDiagonalLine(int targetRow, int targetCol) {
    if (targetRow < prevRow) {
      for (int col = prevCol - 1; col > targetCol; col--) {
        int diff = Math.abs(prevCol - col);
        for (Piece piece : GamePanel.simulationPieces) {
          if (piece.col == col && piece.row == prevRow - diff) {
            hittingPiece = piece;
            return true;
          }
        }
      }

      for (int col = prevCol + 1; col < targetCol; col++) {
        int diff = Math.abs(prevCol - col);
        for (Piece piece : GamePanel.simulationPieces) {
          if (piece.col == col && piece.row == prevRow - diff) {
            hittingPiece = piece;
            return true;
          }
        }
      }
    } else {
      for (int col = prevCol - 1; col > targetCol; col--) {
        int diff = Math.abs(prevCol - col);
        for (Piece piece : GamePanel.simulationPieces) {
          if (piece.col == col && piece.row == prevRow + diff) {
            hittingPiece = piece;
            return true;
          }
        }
      }

      for (int col = prevCol + 1; col < targetCol; col++) {
        int diff = Math.abs(prevCol - col);
        for (Piece piece : GamePanel.simulationPieces) {
          if (piece.col == col && piece.row == prevRow + diff) {
            hittingPiece = piece;
            return true;
          }
        }
      }
    }

    return false;
  }
}
