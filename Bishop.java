public class Bishop extends Piece {
  Bishop(int row, int col, PieceColor color) {
    super(row, col, color);

    type = PieceType.Bishop;

    if (color == PieceColor.White) {
      image = getImage("pieces/w-bishop.png");
    } else {
      image = getImage("pieces/b-bishop.png");
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

    if (Math.abs(targetRow - prevRow) == Math.abs(targetCol - prevCol)) {
      return isValidSquare(targetRow, targetCol) && !pieceIsOnDiagonalLine(targetRow, targetCol);
    }

    return false;
  }
}
