import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter {
  public int x;
  public int y;
  public boolean pressed;

  @Override
  public void mousePressed(MouseEvent e) {
    super.mousePressed(e);

    pressed = true;
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    super.mouseReleased(e);

    pressed = false;
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    super.mouseDragged(e);

    x = e.getX();
    y = e.getY();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    super.mouseMoved(e);

    x = e.getX();
    y = e.getY();
  }
}
