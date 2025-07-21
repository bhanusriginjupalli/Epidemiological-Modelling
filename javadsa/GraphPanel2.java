package javadsa;
import javax.swing.*;
import java.awt.*;

public class GraphPanel2 extends JPanel {
    private Board1 board;

    public GraphPanel2(Board1 board) {
        this.board = board;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawInfectionGraph(g);
    }

    private void drawInfectionGraph(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        int totalActors = board.getActors().size();

        int healthy = board.countHealthy();
        int sick = board.countSick();
        int cured = board.countCured();

        int healthyHeight = (int) ((healthy / (double) totalActors) * height);
        int sickHeight = (int) ((sick / (double) totalActors) * height);
        int curedHeight = (int) ((cured / (double) totalActors) * height);

        g.setColor(Color.GREEN);
        g.fillRect(0, height - healthyHeight, width / 3, healthyHeight);
        g.setColor(Color.BLACK);
        g.drawRect(0, height - healthyHeight, width / 3, healthyHeight);

        g.setColor(Color.RED);
        g.fillRect(width / 3, height - sickHeight, width / 3, sickHeight);
        g.setColor(Color.BLACK);
        g.drawRect(width / 3, height - sickHeight, width / 3, sickHeight);

        g.setColor(Color.ORANGE);
        g.fillRect((width * 2) / 3, height - curedHeight, width / 3, curedHeight);
        g.setColor(Color.BLACK);
        g.drawRect((width * 2) / 3, height - curedHeight, width / 3, curedHeight);
    }
}


