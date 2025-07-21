package javadsa;
import javax.swing.*;
import java.awt.*;

public class GraphPanel1 extends JPanel {
    private Board1 board;

    public GraphPanel1(Board1 board) {
        this.board = board;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawPopulationDensity(g);
    }

    private void drawPopulationDensity(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        int cellWidth = width / board.getMaxCol();
        int cellHeight = height / board.getMaxRow();

        for (Actor1 actor : board.getActors()) {
            int x = actor.getCol() * cellWidth;
            int y = actor.getRow() * cellHeight;

            if (actor.isVaccinated()) {
                // Draw vaccinated individuals
                g.setColor(Color.BLUE);
                g.fillRect(x, y, cellWidth, cellHeight);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellWidth, cellHeight);
            } else {
                // Draw healthy susceptible individuals
                switch (actor.getState()) {
                    case SUSCEPTIBLE:
                        g.setColor(Color.GREEN);
                        break;
                    case INFECTED:
                        g.setColor(Color.RED);
                        break;
                    case RECOVERED:
                        g.setColor(Color.ORANGE);
                        break;
                    case REMOVED:
                        g.setColor(Color.GRAY);
                        break;
                }
                g.fillRect(x, y, cellWidth, cellHeight);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellWidth, cellHeight);
            }
        }
    }

}


