public class King extends Piece {
  King(int row, int col, PieceColor color) {
    super(row, col, color);

    type = PieceType.King;

    if (color == PieceColor.White) {
      image = getImage("pieces/w-king.png");
    } else {
      image = getImage("pieces/b-king.png");
    }
  }

  @Override
  public boolean canMove(int targetRow, int targetCol) {
    if (!isWithinBoard(targetRow, targetCol)) {
      return false;
    }

    if (Math.abs(targetRow - prevRow) + Math.abs(targetCol - prevCol) == 1) {
      return isValidSquare(targetRow, targetCol);
    }

    if (Math.abs(targetRow - prevRow) * Math.abs(targetCol - prevCol) == 1) {
      return isValidSquare(targetRow, targetCol);
    }

    if (moved) {
      return false;
    }

    if (targetCol == prevCol + 2 && targetRow == prevRow && !pieceIsOnStraightLine(targetRow, targetCol)) {
      for (Piece piece : GamePanel.simulationPieces) {
        if (piece.col == prevCol + 3 && piece.row == prevCol && !piece.moved) {
          GamePanel.castlingPiece = piece;
          return true;
        }
      }
    }

    if (targetCol == prevCol - 2 && targetRow == prevRow && !pieceIsOnStraightLine(targetRow, targetCol)) {
      Piece[] pieces = new Piece[2];
      for (Piece piece : GamePanel.simulationPieces) {
        if (piece.col == prevCol - 3 && piece.row == targetRow) {
          pieces[0] = piece;
        }

        if (piece.col == prevCol - 4 && piece.row == targetRow) {
          pieces[1] = piece;
        }

        if (pieces[0] == null && pieces[1] != null && !pieces[1].moved) {
          GamePanel.castlingPiece = pieces[1];
          return true;
        }
      }
    }

    return false;
  }
}
