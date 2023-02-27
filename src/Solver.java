
import java.awt.Color;
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
public class Solver {

    private ArrayList<Cell> openSet = new ArrayList<Cell>();
    private ArrayList<Cell> closedSet = new ArrayList<Cell>();
    private ArrayList<Cell> path = new ArrayList<Cell>();
    private Cell start, end;
    private Panel p;
    private Cell[][] grid;
    private boolean noSolution = false, found = false;
    public int steps;
    public Solver(Cell start, Cell end, Cell[][] grid, Panel p) {
        this.start = start;
        this.end = end;
        this.p = p;
        this.grid = grid;
        openSet.add(start);
        noSolution = false;
        found = false;
        steps = 0;
    }
    Cell current;

    public void calculate() {
        if (!isNoSolution() && !isFound()) {
            if (openSet.size() > 0) { //Wenn das offene Set noch nicht leer ist suchen
                steps++;
                int lowestIndex = 0;
                for (int i = 0; i < openSet.size(); i++) { // Die Zelle finden mit dem niedrigsten G-Wert und Speichern
                    //Wenn der F-Wert derselbe ist soll es das Grid mit dem niedrigsten H-Wert beforzugen
                    if (openSet.get(i).f < openSet.get(lowestIndex).f || openSet.get(i).f == openSet.get(lowestIndex).f && openSet.get(i).h < openSet.get(lowestIndex).h) {
                        lowestIndex = i;
                    }
                }
                Cell current = openSet.get(lowestIndex); // Einfachheits halber eine temporäre Zelle erstellen mit dem niedrigsten G-Wert
                if (current == end) { // Wenn diese Zelle die Endzelle ist dann ist das Ziel gefunden
                    System.out.println("FOUND");
                    String tmpName = new String();
                    switch (Frame.selected) {
                        case Frame.PYTHAGORAS:
                            tmpName = "Mod. Pythagoras";
                            break;
                        case Frame.STRAIGHT:
                            tmpName = "Straight Line";
                            break;
                        default:
                            tmpName = "Manhattan";
                            break;
                    }
                    System.err.println("STEPS TAKEN: "+steps+" used: " + tmpName );
                   // Frame.setTitle("STEPS TAKEN: "+steps+" used: " + tmpName+"\n");
                   PopupFrame.jTextArea1.append("steps taken: "+steps+"  ->  used: " + tmpName+"\n");
                   
                    steps = 0;
                    found = true;
                    if (p.timr.isRunning()) {
                        p.timr.stop();
                    }
                }

                closedSet.add(current); // Aktuelle Zelle ist abgelaufen und kann ich das geschlossene Set gepackt werden 
                openSet.remove(current);//Die Zelle aus dem offenen Set entfernen
                ArrayList<Cell> neighbors = current.neighbors; // Die anliegenden Nachbarn der Zelle speichern

                for (Cell neighbor : neighbors) {  // Jeden Nachbar durchgehen
                    if (!closedSet.contains(neighbor) && !neighbor.isObstacle()) { // Wenn der Nachbar noch "offen" ist dan
                        int tmpG = current.g + distance(current, neighbor); // Neuen G-Wert berechnen
                        boolean newPath = false;
                        if (openSet.contains(neighbor)) { // Wenn die Nachbar in dem offenen Set ist und die Nachbar G größer als die aktuelle G ist
                            if (tmpG < neighbor.g) {
                                neighbor.g = tmpG; // Den G-Wert des Nachbarns berechnen/aktuallisieren
                                newPath = true;
                            }
                        } else {  // Wenn der Nachbar noch nicht im offenen Set ist dann:
                            neighbor.g = tmpG;  // Den G-Wert aktuallisieren
                            newPath = true;
                            openSet.add(neighbor); // und den Nachbar in das offene Set packen
                        }
                        if (newPath) {
                            neighbor.h = distance(neighbor, end); // länge des Nachbarn zum Ende berechnen
                            neighbor.f = neighbor.g + neighbor.h; // f ausrechnen
                            neighbor.previous = current; // vorhereiger Nachbar ist aktuelle Zelle
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
            } else { // wenn das offene Set keine Objekte mehr hat gibt es keine Lösung
                // no solution
                System.out.println("Keine Lösung");
                PopupFrame.jTextArea1.append("no solution "+"\n");
                steps = 0;
                noSolution = true;
                if (p.timr.isRunning()) {
                    p.timr.stop();
                }
            }
        }
    }

    public void draw(Graphics g) {
        openSet.forEach(c -> c.draw(g, Color.red));
        closedSet.forEach(c -> c.draw(g, new Color(220, 220, 220)));
        path.forEach(c -> c.draw(g, Color.green));
        start.draw(g, Color.MAGENTA);
        end.draw(g, Color.PINK);
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
