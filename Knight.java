public class Knight extends Piece {
  Knight(int row, int col, PieceColor color) {
    super(row, col, color);

    type = PieceType.Knight;

    if (color == PieceColor.White) {
      image = getImage("pieces/w-knight.png");
    } else {
      image = getImage("pieces/b-knight.png");
    }
  }

  @Override
  public boolean canMove(int targetRow, int targetCol) {
    if (!isWithinBoard(targetRow, targetCol)) {
      return false;
    }

    if (Math.abs(targetRow - prevRow) * Math.abs(targetCol - prevCol) == 2) {
      return isValidSquare(targetRow, targetCol);
    }

    return false;
  }
}
