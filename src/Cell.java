
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author radle
 */
public class Cell {

    public int f, g, h;
    public int x, y;
    public ArrayList<Cell> neighbors = new ArrayList<Cell>();
    public Cell previous;
    private boolean obstacle = false, showValues = false;
    private Panel p;

    public Cell(int x, int y, Panel p) {
        f = 0;
        g = 0;
        h = 0;
        this.p = p;
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g, Color c) {
        if (!obstacle) {
            g.setColor(c);
            g.fillRect(x * Panel.w, y * Panel.w, Panel.w , Panel.w );
            g.setColor(Color.BLACK);
            if (showValues) {
                g.setFont(new Font("TimesRoman", Font.BOLD, Panel.w/6)); 
                g.drawString(Integer.toString(this.g)+" G", x * Panel.w, y * Panel.w + Panel.w/5);
                g.drawString(Integer.toString(this.h)+" H", x * Panel.w + Panel.w / 2, y * Panel.w + Panel.w/5);
                g.drawString(Integer.toString(this.f)+" F", x * Panel.w + Panel.w / 3, y * Panel.w + Panel.w / 2);
            }
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(x * Panel.w+1, y * Panel.w+1, Panel.w-1, Panel.w-1);
            // g.fillOval(x * Panel.w, y * Panel.w, Panel.w - 1, Panel.w - 1);
        }
    }

    public void addNeighbors(Cell[][] grid) {
        neighbors.clear();
        if (p.diagonal) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (x == 0 && y == 0) {
                    } else {
                        int checkX = grid[this.x][this.y].x + x;
                        int checkY = grid[this.x][this.y].y + y;
                        if (checkX >= 0 && checkX <= grid.length - 1 && checkY >= 0 && checkY <= grid[0].length - 1) {
                            neighbors.add(grid[checkX][checkY]);
                        }
                    }
                }
            }
        } else {
            if (y > 0) {
                this.neighbors.add(grid[x][y - 1]); // OBEN MITTE
            }
            if (x > 0) {
                this.neighbors.add(grid[x - 1][y]); //LINKS
            }
            if (x < grid.length - 1) {
                this.neighbors.add(grid[x + 1][y]);//RECHTS
            }
            if (y < grid[0].length - 1) {
                this.neighbors.add(grid[x][y + 1]);//UNTEN MITTE
            }
        }
    }

    /**
     * @return the obstacle
     */
    public boolean isObstacle() {
        return obstacle;
    }

    /**
     * @param obstacle the obstacle to set
     */
    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    /**
     * @param showValues the showValues to set
     */
    public void setShowValues(boolean showValues) {
        this.showValues = showValues;
    }
}
