import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;;

public class GamePanel extends JPanel implements Runnable {
  public static final int WIDTH = 1100;
  public static final int HEIGHT = 800;

  public static ArrayList<Piece> pieces = new ArrayList<>();
  public static ArrayList<Piece> simulationPieces = new ArrayList<>();
  public static Piece castlingPiece;

  private final JButton reset;

  final int FPS = 60;

  final ArrayList<Piece> promotionPieces = new ArrayList<>();

  final Board board = new Board();
  final Mouse mouse = new Mouse();
  final Thread thread;

  boolean canMove;
  boolean validateSquare;
  boolean promotion;
  boolean gameOver;
  boolean stalemate;
  PieceColor currentColor = PieceColor.White;
  Piece activePiece;
  Piece checkingPiece;

  GamePanel() {
    setPreferredSize(new Dimension(WIDTH, HEIGHT));
    setBackground(Color.BLACK);
    addMouseMotionListener(mouse);
    addMouseListener(mouse);

    thread = new Thread(this);

    reset = new JButton("Reset");
    reset.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        resetGame();
      }
    });
    this.add(reset);

    setPieces();
    copyPieces(pieces, simulationPieces);
  }

  public void launchGame() {
    thread.start();
  }

  private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
    target.clear();
    for (Piece piece : source) {
      target.add(piece);
    }
  }

  @Override
  public void run() {
    double interval = 1000000000 / FPS;

    long lastTime = System.nanoTime();
    long currentTime;
    double delta = 0;
    while (thread != null) {
      currentTime = System.nanoTime();

      delta += (currentTime - lastTime) / interval;
      lastTime = currentTime;
      if (delta >= 1) {
        update();
        repaint();

        delta--;
      }
    }

  }

  private void update() {
    if (promotion) {
      promote();
      return;
    }

    if (gameOver || stalemate)
      return;

    if (mouse.pressed) {
      if (activePiece == null) {
        for (Piece piece : simulationPieces) {
          if (piece.color == currentColor && piece.col == mouse.x / Board.SQUARE_SIZE
              && piece.row == mouse.y / Board.SQUARE_SIZE) {
            activePiece = piece;
          }
        }
      } else {
        simulate();
      }
    } else {
      if (activePiece == null) {
        return;
      }

      if (validateSquare) {
        copyPieces(simulationPieces, pieces);
        activePiece.updatePosition();

        if (castlingPiece != null) {
          castlingPiece.updatePosition();
        }

        if (isKingInCheck() && isCheckMate()) {
          gameOver = true;
        } else if (isStalemate() && !isKingInCheck()) {
          stalemate = true;
        } else {
          if (canPromote()) {
            promotion = true;
          } else {
            changePlayer();
          }
        }
      } else {
        activePiece.resetPosition();
        activePiece = null;
      }
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;

    board.draw(g2);

    for (Piece piece : simulationPieces) {
      piece.draw(g2);
    }

    if (activePiece != null) {
      if (canMove) {
        if (isIllegal(activePiece) || CanOpponentCaptureKing()) {
          g2.setColor(Color.gray);
        } else {
          g2.setColor(Color.white);
        }

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g2.fillRect(
            activePiece.col * Board.SQUARE_SIZE,
            activePiece.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE,
            Board.SQUARE_SIZE);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
      }

      activePiece.draw(g2);
    }

    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.setFont(new Font("Book Antique", Font.PLAIN, 40));
    g2.setColor(Color.WHITE);

    if (promotion) {
      g2.drawString("Promote To: ", 840, 150);
      for (Piece piece : promotionPieces) {
        g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE,
            null);
      }
    } else {
      if (currentColor == PieceColor.White) {
        g2.drawString("White's Turn", 840, 550);

        if (checkingPiece != null && checkingPiece.color == PieceColor.Black) {
          g2.setColor(Color.red);
          g2.drawString("The King", 840, 650);
          g2.drawString("is in Check!", 840, 700);
        }
      } else {
        g2.drawString("Black's Turn", 840, 250);
        if (checkingPiece != null && checkingPiece.color == PieceColor.White) {
          g2.setColor(Color.red);
          g2.drawString("The King", 840, 100);
          g2.drawString("is in Check!", 840, 150);
        }
      }
    }

    if (gameOver) {
      String msg;
      if (currentColor == PieceColor.White) {
        msg = "White Wins";
      } else {
        msg = "Black Wins";
      }

      g2.setFont(new Font("Arial", Font.PLAIN, 90));
      g2.setColor(Color.green);
      g2.drawString(msg, 200, 420);
    }

    if (stalemate) {
      g2.setFont(new Font("Arial", Font.PLAIN, 90));
      g2.setColor(Color.lightGray);
      g2.drawString("Stalemate", 200, 420);
    }

    reset.setBounds(400, 450, 75, 75);
    reset.setVisible(gameOver || stalemate);
  }

  private void simulate() {
    canMove = false;
    validateSquare = false;

    copyPieces(pieces, simulationPieces);

    if (castlingPiece != null) {
      castlingPiece.col = castlingPiece.prevCol;
      castlingPiece.x = castlingPiece.getX(castlingPiece.col);
      castlingPiece = null;
    }

    activePiece.x = mouse.x - Board.HALF_SQUARE_SIZE;
    activePiece.y = mouse.y - Board.HALF_SQUARE_SIZE;

    activePiece.row = activePiece.getRow(activePiece.y);
    activePiece.col = activePiece.getCol(activePiece.x);

    if (activePiece.canMove(activePiece.row, activePiece.col)) {
      canMove = true;

      if (activePiece.hittingPiece != null) {
        simulationPieces.remove(activePiece.hittingPiece);
      }

      checkCastling();

      if (!isIllegal(activePiece) && !CanOpponentCaptureKing()) {
        validateSquare = true;
      }
    }
  }

  public void setPieces() {
    pieces.add(new Pawn(6, 0, PieceColor.White));
    pieces.add(new Pawn(6, 1, PieceColor.White));
    pieces.add(new Pawn(6, 2, PieceColor.White));
    pieces.add(new Pawn(6, 3, PieceColor.White));
    pieces.add(new Pawn(6, 4, PieceColor.White));
    pieces.add(new Pawn(6, 5, PieceColor.White));
    pieces.add(new Pawn(6, 6, PieceColor.White));
    pieces.add(new Pawn(6, 7, PieceColor.White));
    pieces.add(new Rook(7, 0, PieceColor.White));
    pieces.add(new Rook(7, 7, PieceColor.White));
    pieces.add(new Knight(7, 1, PieceColor.White));
    pieces.add(new Knight(7, 6, PieceColor.White));
    pieces.add(new Bishop(7, 2, PieceColor.White));
    pieces.add(new Bishop(7, 5, PieceColor.White));
    pieces.add(new Queen(7, 3, PieceColor.White));
    pieces.add(new King(7, 4, PieceColor.White));

    pieces.add(new Pawn(1, 0, PieceColor.Black));
    pieces.add(new Pawn(1, 1, PieceColor.Black));
    pieces.add(new Pawn(1, 2, PieceColor.Black));
    pieces.add(new Pawn(1, 3, PieceColor.Black));
    pieces.add(new Pawn(1, 4, PieceColor.Black));
    pieces.add(new Pawn(1, 5, PieceColor.Black));
    pieces.add(new Pawn(1, 6, PieceColor.Black));
    pieces.add(new Pawn(1, 7, PieceColor.Black));
    pieces.add(new Rook(0, 0, PieceColor.Black));
    pieces.add(new Rook(0, 7, PieceColor.Black));
    pieces.add(new Knight(0, 1, PieceColor.Black));
    pieces.add(new Knight(0, 6, PieceColor.Black));
    pieces.add(new Bishop(0, 2, PieceColor.Black));
    pieces.add(new Bishop(0, 5, PieceColor.Black));
    pieces.add(new Queen(0, 3, PieceColor.Black));
    pieces.add(new King(0, 4, PieceColor.Black));
  }

  private void resetGame() {
    pieces.clear();

    setPieces();
    copyPieces(pieces, simulationPieces);

    canMove = false;
    validateSquare = false;
    promotion = false;
    gameOver = false;
    stalemate = false;
    currentColor = PieceColor.White;
    activePiece = null;
    checkingPiece = null;
  }

  private void changePlayer() {
    if (currentColor == PieceColor.White) {
      currentColor = PieceColor.Black;
    } else {
      currentColor = PieceColor.White;
    }

    for (Piece piece : pieces) {
      if (piece.color == currentColor) {
        piece.twoStepped = false;
      }
    }

    activePiece = null;
  }

  private void checkCastling() {
    if (castlingPiece == null) {
      return;
    }

    if (castlingPiece.col == 0) {
      castlingPiece.col += 3;
    } else if (castlingPiece.col == 7) {
      castlingPiece.col -= 2;
    }

    castlingPiece.x = castlingPiece.getX(castlingPiece.col);
  }

  private boolean canPromote() {
    if (activePiece.type != PieceType.Pawn) {
      return false;
    }

    if (currentColor == PieceColor.White && activePiece.row == 0
        || currentColor == PieceColor.Black && activePiece.row == 7) {
      promotionPieces.clear();

      promotionPieces.add(new Rook(2, 9, currentColor));
      promotionPieces.add(new Knight(3, 9, currentColor));
      promotionPieces.add(new Bishop(4, 9, currentColor));
      promotionPieces.add(new Queen(5, 9, currentColor));

      return true;
    }

    return false;
  }

  private void promote() {
    if (!mouse.pressed) {
      return;
    }

    for (Piece piece : promotionPieces) {
      if (piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
        switch (piece.type) {
          case King:
          case Pawn:
            break;
          case Bishop:
            simulationPieces.add(new Bishop(activePiece.row, activePiece.col, currentColor));
            break;
          case Knight:
            simulationPieces.add(new Knight(activePiece.row, activePiece.col, currentColor));
            break;
          case Queen:
            simulationPieces.add(new Queen(activePiece.row, activePiece.col, currentColor));
            break;
          case Rook:
            simulationPieces.add(new Rook(activePiece.row, activePiece.col, currentColor));
            break;
        }

        simulationPieces.remove(activePiece);
        copyPieces(simulationPieces, pieces);
        activePiece = null;
        promotion = false;
        changePlayer();
      }
    }
  }

  private boolean isIllegal(Piece king) {
    if (king.type != PieceType.King) {
      return false;
    }

    for (Piece piece : simulationPieces) {
      if (piece != king && piece.color != king.color && piece.canMove(king.row, king.col)) {
        return true;
      }
    }

    return false;
  }

  private boolean isKingInCheck() {
    Piece king = getKing(true);

    if (activePiece.canMove(king.row, king.col)) {
      checkingPiece = activePiece;
      return true;
    }

    checkingPiece = null;
    return false;
  }

  private Piece getKing(boolean opponent) {
    for (Piece piece : simulationPieces) {
      if (opponent) {
        if (piece.type == PieceType.King && piece.color != currentColor) {
          return piece;
        }
      } else {
        if (piece.type == PieceType.King && piece.color == currentColor) {
          return piece;
        }
      }
    }

    return null;
  }

  private boolean CanOpponentCaptureKing() {
    Piece king = getKing(false);

    for (Piece piece : simulationPieces) {
      if (piece.color != king.color && piece.canMove(king.row, king.col)) {
        return true;
      }
    }

    return false;
  }

  private boolean canKingMove(Piece king) {
    return isValidMove(king, -1, -1)
        || isValidMove(king, 0, -1)
        || isValidMove(king, 1, -1)
        || isValidMove(king, -1, 0)
        || isValidMove(king, 1, 0)
        || isValidMove(king, -1, 1)
        || isValidMove(king, 0, 1)
        || isValidMove(king, 1, 1);
  }

  private boolean isValidMove(Piece king, int rowPlus, int colPlus) {
    boolean isValidMove = false;

    king.row += rowPlus;
    king.col += colPlus;

    if (king.canMove(king.row, king.col)) {
      if (king.hittingPiece != null) {
        simulationPieces.remove(king.hittingPiece);
      }

      if (!isIllegal(king)) {
        isValidMove = true;
      }
    }

    king.resetPosition();
    copyPieces(pieces, simulationPieces);

    return isValidMove;
  }

  private boolean isCheckMate() {
    Piece king = getKing(true);

    if (canKingMove(king)) {
      return false;
    }

    int rowDiff = Math.abs(checkingPiece.row - king.row);
    int colDiff = Math.abs(checkingPiece.col - king.col);

    if (colDiff == 0) {
      if (checkingPiece.row < king.row) {
        for (int row = checkingPiece.row; row < king.row; row++) {
          for (Piece piece : simulationPieces) {
            if (piece != king && piece.color != currentColor && piece.canMove(row, checkingPiece.col)) {
              return false;
            }
          }
        }
      } else {
        for (int row = checkingPiece.row; row > king.row; row--) {
          for (Piece piece : simulationPieces) {
            if (piece != king && piece.color != currentColor && piece.canMove(row, checkingPiece.col)) {
              return false;
            }
          }
        }
      }
    } else if (rowDiff == 0) {
      if (checkingPiece.col < king.col) {
        for (int col = checkingPiece.col; col < king.col; col++) {
          for (Piece piece : simulationPieces) {
            if (piece != king && piece.color != currentColor && piece.canMove(checkingPiece.row, col)) {
              return false;
            }
          }
        }
      } else {
        for (int col = checkingPiece.col; col > king.col; col--) {
          for (Piece piece : simulationPieces) {
            if (piece != king && piece.color != currentColor && piece.canMove(checkingPiece.row, col)) {
              return false;
            }
          }
        }
      }
    } else if (rowDiff == colDiff) {
      if (checkingPiece.row < king.row) {
        if (checkingPiece.col < king.col) {
          for (int row = checkingPiece.row, col = checkingPiece.col; col < king.col; row++, col++) {
            for (Piece piece : simulationPieces) {
              if (piece != king && piece.color != currentColor && piece.canMove(row, col)) {
                return false;
              }
            }
          }
        } else {
          for (int row = checkingPiece.row, col = checkingPiece.col; col > king.col; row++, col--) {
            for (Piece piece : simulationPieces) {
              if (piece != king && piece.color != currentColor && piece.canMove(row, col)) {
                return false;
              }
            }
          }
        }
      } else {
        if (checkingPiece.col < king.col) {
          for (int row = checkingPiece.row, col = checkingPiece.col; col < king.col; row--, col++) {
            for (Piece piece : simulationPieces) {
              if (piece != king && piece.color != currentColor && piece.canMove(row, col)) {
                return false;
              }
            }
          }
        } else {
          for (int row = checkingPiece.row, col = checkingPiece.col; col > king.col; row--, col--) {
            for (Piece piece : simulationPieces) {
              if (piece != king && piece.color != currentColor && piece.canMove(row, col)) {
                return false;
              }
            }
          }
        }
      }
    }

    return true;
  }

  private boolean isStalemate() {
    int count = 0;
    for (Piece piece : simulationPieces) {
      if (piece.color != currentColor) {
        count++;
      }
    }

    return count == 1 && !canKingMove(getKing(true));
  }
}
