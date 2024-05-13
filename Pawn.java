public class Pawn extends Piece {
  Pawn(int row, int col, PieceColor color) {
    super(row, col, color);

    type = PieceType.Pawn;

    if (color == PieceColor.White) {
      image = getImage("pieces/w-pawn.png");
    } else {
      image = getImage("pieces/b-pawn.png");
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

    int moveVal = color == PieceColor.White ? -1 : 1;

    hittingPiece = getHittingPiece(targetRow, targetCol);

    if (targetCol == prevCol && targetRow == prevRow + moveVal && hittingPiece == null) {
      return true;
    }

    if (targetCol == prevCol
        && targetRow == prevRow + moveVal * 2
        && hittingPiece == null
        && !moved
        && !pieceIsOnStraightLine(targetRow, targetCol)) {
      return true;
    }

    if (Math.abs(targetCol - prevCol) == 1
        && targetRow == prevRow + moveVal
        && hittingPiece != null
        && hittingPiece.color != color) {
      return true;
    }

    if (Math.abs(targetCol - prevCol) == 1 && targetRow == prevRow + moveVal) {
      for (Piece piece : GamePanel.simulationPieces) {
        if (piece.col == targetCol && piece.row == prevRow && piece.twoStepped) {
          hittingPiece = piece;
          return true;
        }
      }
    }

    return false;
  }

}
