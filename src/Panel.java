
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author radle
 */
public class Panel extends javax.swing.JPanel {

    private int WIDTH = 1000, HEIGHT = 1000;
    public static int cols = 10, rows = 10, w = 100;
    private Cell[][] grid = new Cell[cols][rows];

    private Cell start, end;
    private Solver s;
    public Timer timr;
    private int delay = 1;
    private final int OBSTACLE = 0, START = 1, END = 2;
    private int selected = 0, selectedSize;
    public boolean diagonal = true, showValue = false, showGrid = true;
    public boolean randomObstacles = false;
    private Random r = new Random();
    public float p = 0.3f;

    public Panel() {
        initComponents();
        this.setFocusable(true);
        this.requestFocus();
        w = WIDTH / cols;
        selectedSize = w / 2;
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j] = new Cell(i, j, this);
            }
        }
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j].addNeighbors(grid);
            }
        }

        start = grid[0][0];
        end = grid[cols - 1][rows - 1];
        //  randomObstacles();

        s = new Solver(start, end, grid, this);

        timr = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                s.calculate();
            }
        });
        //timr.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j].draw(g, Color.white);
            }
        }
        s.draw(g);
        if (showGrid) {
            for (int i = 0; i < cols; i++) {
                for (int j = 0; j < rows; j++) {
                    g.setColor(Color.black);
                    g.drawRect(i * w, j * w, w - 1, w - 1);
                }
            }
        }

        switch (selected) {
            case OBSTACLE:
                g.setColor(Color.black);
                g.fillRect(0, HEIGHT - selectedSize, selectedSize, selectedSize);
                break;
            case START:
                g.setColor(Color.MAGENTA);
                g.fillRect(0, HEIGHT - selectedSize, selectedSize, selectedSize);
                break;
            case END:
                g.setColor(Color.pink);
                g.fillRect(0, HEIGHT - selectedSize, selectedSize, selectedSize);
                break;
        }
        repaint();
    }

    public void showValues(boolean v) {
        showValue = v;
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j].setShowValues(v);
            }
        }
    }

    public void skipCalculation() {
        long startTime = System.nanoTime();
        while (!s.isFound() && !s.isNoSolution()) {
            s.calculate();
        }
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println(estimatedTime);
           PopupFrame.jTextArea1.append("->TIME : "+estimatedTime/1000+"µs \n");
        repaint();
    }

    public void diagonalState() {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j].addNeighbors(grid);
            }
        }
    }

    public void updateCols(int cols) {
        this.cols = cols;
        w = WIDTH / this.cols;
        selectedSize = w / 2;
        rows = this.cols;
        grid = new Cell[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j] = new Cell(i, j, this);
                grid[i][j].setShowValues(showValue);
            }
        }
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j].addNeighbors(grid);
            }
        }
        start = grid[0][0];
        end = grid[cols - 1][rows - 1];
        s = new Solver(start, end, grid, this);
    }

    public void reset() {
        this.requestFocus();
        w = WIDTH / cols;

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j].setObstacle(false);
                grid[i][j].f = 0;
                grid[i][j].g = 0;
                grid[i][j].h = 0;
            }
        }

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j].addNeighbors(grid);
            }
        }
        if (randomObstacles) {
            randomObstacles();
            System.out.println("RANDOM OBSTACLES");
        }
        repaint();
        s = new Solver(start, end, grid, this);
    }

    public void resetSolver() {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j].f = 0;
                grid[i][j].g = 0;
                grid[i][j].h = 0;
            }
        }
        diagonalState();
        s = new Solver(start, end, grid, this);
    }

    public void randomObstacles() {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (r.nextFloat() <= p && randomObstacles) {
                    grid[i][j].setObstacle(true);
                }
                if (!randomObstacles) {
                    grid[i][j].setObstacle(false);
                }
            }
        }
        start.setObstacle(false);
        end.setObstacle(false);
    }

    private void select(int x, int y, boolean button) {
        switch (selected) {
            case OBSTACLE:
                if (button) {
                    grid[x / w][y / w].setObstacle(false);
                    break;
                } else {
                    grid[x / w][y / w].setObstacle(true);
                }

                break;
            case START:
                start = grid[x / w][y / w];
                s = new Solver(start, end, grid, this);
                break;
            case END:
                end = grid[x / w][y / w];
                s = new Solver(start, end, grid, this);
                break;
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                formMouseWheelMoved(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (evt.getKeyChar() == 'w') {
            s.calculate();
        }
    }//GEN-LAST:event_formKeyPressed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        select(evt.getX(), evt.getY(), evt.isShiftDown());
//        if (evt.getButton() == 3 && selected == OBSTACLE) {
//            grid[evt.getX() / w][evt.getY() / w].setObstacle(false);
//        }
    }//GEN-LAST:event_formMouseClicked

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        //  select(evt.getX(), evt.getY(), evt);
    }//GEN-LAST:event_formMousePressed

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        select(evt.getX(), evt.getY(), evt.isShiftDown());
    }//GEN-LAST:event_formMouseDragged

    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved

        if (evt.getPreciseWheelRotation() < 0) {
            selected++;
            if (selected == 3) {
                selected = 0;
            }
        } else {
            selected--;
            if (selected <= -1) {
                selected = 2;
            }
        }
    }//GEN-LAST:event_formMouseWheelMoved


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
