
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
public class NewClass {
    private ArrayList<Cell> openSet = new ArrayList<Cell>();
    private ArrayList<Cell> closedSet = new ArrayList<Cell>();
    private ArrayList<Cell> path = new ArrayList<Cell>();
    private Cell start, end;
    private Panel p;
    private Cell[][] grid;
    private boolean noSolution = false, found = false;
    public int steps;
    
    
    
    
    public void calculate() {
        while (!isNoSolution() && !isFound()) {
            if (openSet.size() > 0) { 
                int lowestIndex = 0;
                for (int i = 0; i < openSet.size(); i++) {
                    if (openSet.get(i).f < openSet.get(lowestIndex).f || openSet.get(i).f == openSet.get(lowestIndex).f && openSet.get(i).h < openSet.get(lowestIndex).h) {
                        lowestIndex = i;
                    }
                }
                Cell current = openSet.get(lowestIndex); 
                if (current == end) { 
                    System.out.println("FOUND");
                    found = true;
                }
                closedSet.add(current);
                openSet.remove(current);
                ArrayList<Cell> neighbors = current.neighbors;

                for (Cell neighbor : neighbors) {
                    if (!closedSet.contains(neighbor) && !neighbor.isObstacle()) {
                        int tmpG = current.g + distance(current, neighbor);
                        boolean newPath = false;
                        if (openSet.contains(neighbor)) {
                            if (tmpG < neighbor.g) {
                                neighbor.g = tmpG;
                                newPath = true;
                            }
                        } else {
                            neighbor.g = tmpG;
                            newPath = true;
                            openSet.add(neighbor);
                        }
                        if (newPath) {
                            neighbor.h = distance(neighbor, end);
                            neighbor.f = neighbor.g + neighbor.h;
                            neighbor.previous = current;
                        }
                    }
                }
                Cell temp = current;
                path.clear();
                path.add(temp);
                while (temp.previous != null) {
                    path.add(temp.previous);
                    temp = temp.previous;
                }
            } else {
                System.out.println("Keine Lösung");
                noSolution = true;
            }
        }
    }
      private int distance(Cell a, Cell b) {
//        Brechnung nach gerade 10 und quer 14(pythagoras) 14y+10(x-y)
        if (Frame.selected == Frame.PYTHAGORAS) {
            int dstX = Math.abs(a.x - b.x);
            int dstY = Math.abs(a.y - b.y);
            if (dstX > dstY) {
                return 14 * dstY + 10 * (dstX - dstY);
            }
            return 14 * dstX + 10 * (dstY - dstX);
        }

        //Einfache Gerade
        if (Frame.selected == Frame.STRAIGHT) {
            return (int) Math.hypot(a.x - b.x, a.y - b.y);
        }
        //Manhatten distance
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    /**
     * @return the noSolution
     */
    public boolean isNoSolution() {
        return noSolution;
    }

    /**
     * @return the found
     */
    public boolean isFound() {
        return found;
    }
}
