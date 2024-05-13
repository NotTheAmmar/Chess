public class Queen extends Piece {
  Queen(int row, int col, PieceColor color) {
    super(row, col, color);

    type = PieceType.Queen;

    if (color == PieceColor.White) {
      image = getImage("pieces/w-queen.png");
    } else {
      image = getImage("pieces/b-queen.png");
    }
  }

  @Override
  public boolean canMove(int targetRow, int targetCol) {
    if (!isWithinBoard(targetRow, targetCol)) {
      return false;
    }

    if (isSameSquare(targetRow, targetCol)) {
      return false;
    }

    if (targetRow == prevRow || targetCol == prevCol) {
      return isValidSquare(targetRow, targetCol) && !pieceIsOnStraightLine(targetRow, targetCol);
    }

    if (Math.abs(targetRow - prevRow) == Math.abs(targetCol - prevCol)) {
      return isValidSquare(targetRow, targetCol) && !pieceIsOnDiagonalLine(targetRow, targetCol);
    }

    return false;
  }
}
