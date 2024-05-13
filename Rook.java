public class Rook extends Piece {
  Rook(int row, int col, PieceColor color) {
    super(row, col, color);

    type = PieceType.Rook;

    if (color == PieceColor.White) {
      image = getImage("pieces/w-rook.png");
    } else {
      image = getImage("pieces/b-rook.png");
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

    return false;
  }
}
