import java.util.ArrayList;
import java.util.Arrays;
import tester.*;
import javalib.impworld.*;
import javalib.worldcanvas.WorldCanvas;
import javalib.colors.*;
import javalib.worldimages.*;
import java.util.Random;
import java.awt.Color;

//Assignment 9
//Zhang Xi
//xizhang2
//Yifan Xing
//xingyif



// Note to Grader: when you run our test, when all the cells become blue,
// wait for a while, then you can see that the cells are getting
// darker and darker, and at the end they all become black

// Represents a cell (a single square of the game area)
class Cell {
    // represents absolute height of this cell, in feet
    double height;
    int x;
    int y;
    // the four adjacent cells to this one
    Cell left;
    Cell top;
    Cell right;
    Cell bottom;
    // reports whether this cell is flooded or not
    boolean isFlooded;
    Cell(double height, int x, int y) {
        this.height = height;
        this.x = x;
        this.y = y;
        this.left = null;
        this.top = null;
        this.right = null;
        this.bottom = null;
        this.isFlooded = false;
    }

    // draw the mountain cells
    public WorldImage mountainCell(int waterHeight) {
        // convert height to a number in range 0 to 255
        int f = (int)(Math.abs(waterHeight - this.height) * (255 / 32) + 31);
        // creates a color that switches between white and green color
        Color myGreen = new Color(f, 255, f);
        return new RectangleImage(new Posn(x * 10 + 5, y * 10 + 5),
                10, 10, myGreen);

    }

    // draw the flooded cells
    public WorldImage floodedCell(int waterHeight) {
        // convert height to a number in order to create a color
        int f = 255 - (int) (waterHeight - height) - 130;
        //  creates a color that switches between blue and black
        Color myBlack = new Color(0, 0, (Math.max(0, f)));
        return new RectangleImage(new Posn(x * 10 + 5, y * 10 + 5),
                10, 10, myBlack);
    }

    // draw the cells that are below current water level,
    // but that are not yet flooded
    public WorldImage belowWater(int waterHeight) {
        // convert height to a number in range 0 to 255
        int f = 255 - (int) Math.abs(waterHeight - height * 10);
        // creates a color that switches between light red and dark red
        Color myColor = new Color(f, (255 - f) / 5 , 0);
        return new RectangleImage(new Posn(x * 10 + 5, y * 10 + 5),
                10, 10, myColor);
    }

    // draw one cell based on its current condition
    public WorldImage drawCell(int waterHeight) {
        // draws a land cell
        if (this.height > waterHeight) {
            return this.mountainCell(waterHeight);
        }
        // draws a flooded cell
        else if (this.isFlooded) {
            return this.floodedCell(waterHeight);
        }
        // draws a cell below water level
        else {
            return this.belowWater(waterHeight);
        }
    }

    // EFFECT: floods the neighbors of the cell
    public void floodNeighbors(int waterHeight) {
        if (this.left.height < waterHeight && !this.left.isFlooded) {
            this.left.isFlooded = true;
            this.left.floodNeighbors(waterHeight);
        }
        if (this.right.height < waterHeight && !this.right.isFlooded) {
            this.right.isFlooded = true;
            this.right.floodNeighbors(waterHeight);
        }
        if (this.top.height < waterHeight && !this.top.isFlooded) {
            this.top.isFlooded = true;
            this.top.floodNeighbors(waterHeight);
        }
        if (this.bottom.height < waterHeight && !this.bottom.isFlooded) {
            this.bottom.isFlooded = true;
            this.bottom.floodNeighbors(waterHeight);
        }
    }
}

// represents the ocean cell    
class OceanCell extends Cell {

    OceanCell(double height, int x, int y) {
        super(height, x, y);
        this.isFlooded = true;
    }

    // draws the ocean cells
    public WorldImage drawCell(int waterHeight) {
        return new RectangleImage(new Posn(x * 10 + 5, y * 10 + 5),
                10, 10, new Color(31, 31, 122));
    }
}



// to represent the target class        
// represent everything that the player needs to pick up
//x and y are positive integers representing its location
class Target {                                        
    Cell c;  // the cell which the target is on
    int x;  
    int y;
    Random r = new Random();
    Target(Cell c) {
        this.c = c;
        this.x = c.x;   
        this.y = c.y;
        this.r = new Random();
    }

    // draws the target
    WorldImage draw() {
        return new CircleImage(new Posn(this.x, this.y), 10, new Yellow());
    }

    // determines whether the target is flooded with the cell
    boolean isTargetFlooded() {
        return this.c.isFlooded;
    }
}


// to represent the pilot class, a player
class Pilot {                                  
    Cell c;    // the cell which the pilot is on
    int x;
    int y;
    int targetPicked;
    Random r = new Random();
    Pilot(Cell c, int targetPicked) {
        this.c = c;
        this.x = c.x;
        this.y = c.y;
        this.targetPicked = targetPicked;
        this.r = new Random();
    }

    // draws the pilot
    WorldImage draw() {
        return new FromFileImage(new Posn(200, 200), "pilot-icon.png");
    }

    // EFFECT: moves the pilot
    // changes the x or y position of the pilot when press a key
    void moveOnKey(String ke) {
        if (ke.equals("left") && !c.left.isFlooded) {
            x = x - 2;
        }
        else if (ke.equals("right") && !c.right.isFlooded) {
            x = x + 2;
        }
        else if (ke.equals("up") && !c.top.isFlooded) {
            y = y - 2;
        }
        else if (ke.equals("bottom") && !c.bottom.isFlooded) {
            y = y + 2;
        }
        else {
            // do nothing
        }
    }

    // determines whether the pilot picked up the given target
    boolean isPickedUpT(Target t) {
        return t.x == this.x && t.y == this.y;
    }

    // determines whether the pilot picked up the given helicopter
    boolean isPickedUpH(HelicopterTarget h) {
        return h.x == this.x && h.y == this.y;
    }

    // keeps track of how many target it has picked up
    public int updatePicked(Target t) {
        if (this.isPickedUpT(t)) {
            return this.targetPicked + 1;
        }
        else {
            return this.targetPicked;
        }
    }

    // determines if this pilot is flooded
    public boolean isFlooded() {
        return this.c.isFlooded;
    }
}


//to represent the helicopter class, a helicopter 
class HelicopterTarget {
    Cell c;   // the cell which the helicopter is on
    int x;
    int y;
    Random r = new Random();
    HelicopterTarget(Cell c) {
        this.c = c;
        this.x = c.x;   
        this.y = c.y;
        this.r = new Random();
    }

    // draws the helicopter target
    public WorldImage draw() {
        return new FromFileImage(new Posn(c.x, c.y), "helicopter.png");
    }


    // determines whether the helicopter is flooded with the cell
    boolean isHelicopterFlooded() {
        return this.c.isFlooded;
    }
}


// represents the world of Fobbiden Island
class ForbiddenIslandWorld extends World {
    // a constant island size
    static final int ISLAND_SIZE = 64;

    IList<Cell> board;                         
    // all the cells of the game, including the ocean
    ArrayList<ArrayList<Cell>> secondboard;
    // the current water height
    int waterHeight;
    Random r = new Random();
    HelicopterTarget helicopter; 
    IList<Target> lot;
    Pilot pilot;

    // the constructor
    ForbiddenIslandWorld(ArrayList<ArrayList<Cell>> secondboard) {
        this.board = new MtList<Cell>();               
        this.secondboard = secondboard;
        this.waterHeight = 0;
        this.r = new Random();
        //        this.helicopter = new HelicopterTarget(
        //                              findHighestCell(this.secondboard));
        //        this.lot = this.randomLoTarget(10);
        //        this.pilot = new Pilot(this.randomPilot(), 0);
    }


    // finds the highest cell
    static Cell findHighestCell(ArrayList<ArrayList<Cell>> secondboard) {
        Cell max = secondboard.get(0).get(0);
        for (int y = 0; y < ISLAND_SIZE + 1; y = y + 1) {
            for (int x = 0; x < ISLAND_SIZE + 1; x = x + 1) {
                if (max.height < secondboard.get(y).get(x).height) {
                    max = secondboard.get(y).get(x);
                }
            }
        }
        return max;
    }

    // assigns the position of the target randomly
    Target randomTarget() {
        Cell randomCell = this.secondboard.get(
                this.r.nextInt(ISLAND_SIZE)).get(
                        this.r.nextInt(ISLAND_SIZE));
        if (!randomCell.isFlooded) {
            return new Target(randomCell);
        }
        else {
            return this.randomTarget();
        }
    }

    // assigns the position of the pilot randomly
    Cell randomPilot() {
        Cell temp = this.secondboard.get(
                this.r.nextInt(ISLAND_SIZE)).get(
                        this.r.nextInt(ISLAND_SIZE));
        if (temp.isFlooded) {
            return this.randomPilot();   
        }
        else {
            return temp;
        }
    }

    // assigns the positions of a list of targets randomly
    IList<Target> randomLoTarget(int n) {
        IList<Target> result = new MtList<Target>();
        while (n > 1) {
            result = new ConsList<Target>(this.randomTarget(), result);
            n = n - 1;
        }
        return result;
    }


    // produces an ArrayList<ArrayList<Double>>
    // that represents the height of each cell
    ArrayList<ArrayList<Double>> cellHeight() {

        // represents the outside array list
        ArrayList<ArrayList<Double>> outsideArray =
                new ArrayList<ArrayList<Double>>();

        for (int y = 0; y < ISLAND_SIZE + 1; y = y + 1) {
            // represents the inside array list
            ArrayList<Double> insideArray = new ArrayList<Double>();

            for (int x = 0; x < ISLAND_SIZE + 1; x = x + 1) {

                insideArray.add((double) ISLAND_SIZE / 2 -
                        (Math.abs((ISLAND_SIZE / 2) - x)
                                + Math.abs((ISLAND_SIZE / 2) - y)));
            }
            outsideArray.add(insideArray);
        }
        return outsideArray;
    }

    // EFFECT: to update a cell and assign its neighbor when it is not at the
    // border, and when it is at the border
    void updateCell(ArrayList<ArrayList<Cell>> that) {
        for (int y = 0; y < ISLAND_SIZE + 1; y = y + 1) {
            for (int x = 0; x < ISLAND_SIZE + 1; x = x + 1) {
                if (x != 0) {
                    that.get(y).get(x).left = that.get(y).get(x - 1);
                }
                else {
                    that.get(y).get(x).left = that.get(y).get(x);
                }
                if (x != ForbiddenIslandWorld.ISLAND_SIZE) {
                    that.get(y).get(x).right = that.get(y).get(x + 1);
                }
                else {
                    that.get(y).get(x).right = that.get(y).get(x);
                }
                if (y != 0) {
                    that.get(y).get(x).top = that.get(y - 1).get(x);
                }
                else  {
                    that.get(y).get(x).top = that.get(y).get(x);
                }
                if (y != ForbiddenIslandWorld.ISLAND_SIZE) {
                    that.get(y).get(x).bottom = that.get(y + 1).get(x);
                }
                else {
                    that.get(y).get(x).bottom = that.get(y).get(x);
                }
            }
        }
    }

    // produces an ArrayList<ArrayList<Double>> that 
    // represents random height of each cell
    ArrayList<ArrayList<Double>> randomCellHeight() {

        // represents the outside array list
        ArrayList<ArrayList<Double>> outsideArray =
                new ArrayList<ArrayList<Double>>();

        for (int y = 0; y < ISLAND_SIZE + 1; y = y + 1) {
            // represents the inside array list
            ArrayList<Double> insideArray = new ArrayList<Double>();

            for (int x = 0; x < ISLAND_SIZE + 1; x = x + 1) {

                if (((double) ISLAND_SIZE / 2 - (Math.abs(ISLAND_SIZE / 2 - x)
                        + Math.abs(ISLAND_SIZE / 2 - y))) >= 0) {
                    insideArray.add((double) r.nextInt(ISLAND_SIZE / 2) + 1);
                }
                else {
                    insideArray.add(0.0);
                }
            }
            outsideArray.add(insideArray);
        }
        return outsideArray;
    }

    // produces an ArrayList<ArrayList<Double>> that
    // represents random terrain island
    ArrayList<ArrayList<Double>> terrain() {

        // represents the outside array list
        ArrayList<ArrayList<Double>> outsideArray =
                new ArrayList<ArrayList<Double>>();
        ArrayList<Double> insideArray = new ArrayList<Double>();

        for (int y = 0; y < ISLAND_SIZE + 1; y++) {
            // represents the inside array list
            insideArray = new ArrayList<Double>();

            for (int x = 0; x < ISLAND_SIZE + 1; x++) {
                insideArray.add(0.0);
            }
            outsideArray.add(insideArray);
        }

        // initialize the middles of the four edges to height 1
        outsideArray.get(0).set(ISLAND_SIZE / 2, 1.0);
        outsideArray.get(ISLAND_SIZE / 2).set(0, 1.0);
        outsideArray.get(ISLAND_SIZE / 2).set(ISLAND_SIZE, 1.0);
        outsideArray.get(ISLAND_SIZE).set(ISLAND_SIZE / 2, 1.0);

        // set the center of the grid as the max height of the island
        outsideArray.get(
                outsideArray.size() / 2).set(
                        insideArray.size() / 2, ISLAND_SIZE / 2 * 1.0);
        // assign random values to each of the cell   
        this.terrainHelp(outsideArray, 0, ISLAND_SIZE, 0, ISLAND_SIZE);

        this.terrainHelp(outsideArray, 0, ISLAND_SIZE / 2,
                0, ISLAND_SIZE / 2);
        this.terrainHelp(outsideArray, ISLAND_SIZE / 2,
                ISLAND_SIZE, 0, ISLAND_SIZE / 2);
        this.terrainHelp(outsideArray, 0, ISLAND_SIZE / 2,
                ISLAND_SIZE / 2, ISLAND_SIZE);
        this.terrainHelp(outsideArray, ISLAND_SIZE / 2,
                ISLAND_SIZE, ISLAND_SIZE / 2, ISLAND_SIZE);

        return outsideArray;
    }

    // EFFECT: use subdivision algorithms to divide the grid into quarters
    // and to determine their heights
    void terrainHelp(ArrayList<ArrayList<Double>> arr,
            int x0, int x1, int y0, int y1) {
        // middle x
        int mx = (x0 + x1) / 2;
        // middle y
        int my = (y0 + y1) / 2;

        double tl = arr.get(y0).get(x0);
        double bl = arr.get(y1).get(x0);
        double tr = arr.get(y0).get(x1);
        double br = arr.get(y1).get(x1);

        double t = this.setCorner(tl, tr);
        double l = this.setCorner(tl, bl);
        double b = this.setCorner(bl, br);
        double r = this.setCorner(tr, br);
        double m = this.setMiddle(tl, tr, bl, br);

        if (x1 - x0 >= 2) {

            arr.get(y0).set(mx, t);
            arr.get(y1).set(mx, b);
            arr.get(my).set(x0, l);
            arr.get(my).set(x1, r);
            arr.get(my).set(mx, m);

            this.terrainHelp(arr, x0, mx, y0, my);
            this.terrainHelp(arr, mx, x1, y0, my);
            this.terrainHelp(arr, x0, mx, my, y1);
            this.terrainHelp(arr, mx, x1, my, y1);
        }
    }


    // EFFECT: calculate the height of a corner point
    double setCorner(double min, double max) {
        double dis = (max - min) / 20;
        return Math.abs((min + max) / 2 +
                (this.r.nextDouble() - 0.5) * dis / 2 - 35);
    }

    // EFFECT: calculate the height of a middle point                       
    double setMiddle(double left, double right, double top, double bottom) {
        double dis = bottom - top;
        return (Math.abs((left + right + top + bottom) / 4 
                + (this.r.nextDouble() - 0.5) * dis / 20 - 70));
    }

    // produces the cells based on given ArrayList<ArrayList<Double>>
    // the ArrayList<ArrayList<Cell>> is a mountain when it takes in 
    // an ArrayList<ArrayList<Double>> of cell height
    // it is an island when it takes in 
    // an ArrayList<ArrayList<Double>> of random cell height
    ArrayList<ArrayList<Cell>> makeCell(ArrayList<ArrayList<Double>> that) {
        // represents the outside array list
        ArrayList<ArrayList<Cell>> outsideArray =
                new ArrayList<ArrayList<Cell>>();

        for (int y = 0; y < ISLAND_SIZE + 1; y = y + 1) {
            // represents the inside array list
            ArrayList<Cell> insideArray = new ArrayList<Cell>();
            for (int x = 0; x < ISLAND_SIZE + 1; x = x + 1) {
                if (that.get(y).get(x) <= this.waterHeight) {
                    insideArray.add(new OceanCell(that.get(y).get(x), x, y));
                }
                else {
                    insideArray.add(new Cell(that.get(y).get(x), x, y));
                }
            }
            outsideArray.add(insideArray);
        }
        updateCell(outsideArray);
        this.secondboard = outsideArray;
        return outsideArray;
    }

    // draws the game
    public WorldImage makeImage() {
        return this.makeImageHelp(this.secondboard);
    }

    // Helper for makeImage()
    public WorldImage makeImageHelp(ArrayList<ArrayList<Cell>> that) {
        // represents the outside array list
        WorldImage outsideArray =
                new RectangleImage(new Posn(ISLAND_SIZE / 2 * 10 + 5,
                        ISLAND_SIZE / 2 * 10 + 5),
                        ISLAND_SIZE * 10 + 10, ISLAND_SIZE * 10 + 10,
                        new White());
        for (int y = 0; y < that.size(); y = y + 1) {
            for (int x = 0; x < that.get(y).size(); x = x + 1) {
                outsideArray = new OverlayImages(outsideArray,
                        that.get(y).get(x).drawCell(this.waterHeight));
            }
        }

        outsideArray = new OverlayImages(outsideArray,
                new OverlayImages(new Pilot(this.randomPilot(), 0).draw(),
                        new OverlayImages(
                                new HelicopterTarget(
                                        findHighestCell(
                                                this.secondboard)).draw(), 
                                this.randomLoTarget(10).drawLoT())));
        return outsideArray;
    }


    // EFFECT: floods cells if the cell is below water height and
    // any of its neighbor is flooded
    public void floodCells() {
        for (ArrayList<Cell> cells : this.secondboard) {
            for (Cell c : cells) {
                if (c.height <= this.waterHeight &&
                        (c.left.isFlooded || c.right.isFlooded ||
                                c.top.isFlooded || c.bottom.isFlooded)) {
                    c.isFlooded = true;
                    c.floodNeighbors(this.waterHeight);
                }
            }
        }
    }

    // produces the game on the next tick
    public void onTick() {
        this.waterHeight = waterHeight + 1;
        this.floodCells();
    }

    // to allow the player to control the player  
    public void onKeyEvent(String ke) {
        pilot.moveOnKey(ke);
        if (ke.equals("m")) {
            this.makeCell(this.cellHeight());    
        }
        else if (ke.equals("r")) {
            this.makeCell(this.randomCellHeight());
        }
        else if (ke.equals("t")) {
            this.makeCell(this.terrain());
        }
        else {
            // do nothing
        }
    }


    // to produce the game over image
    public WorldImage lastImage() {
        return new OverlayImages(this.makeImage(),
                new TextImage(new Posn(200, 200),
                        "The End!", 50, new Red()));
    }


    // to produce a new instance of the class WorldEnd
    public WorldEnd worldEnds() {
        // if the pilot is flooded, the game is over.
        // if the helicopter is picked up, the pilot wins
        if (this.pilot.isFlooded()) {
            return new WorldEnd(true, this.lastImage()); 
        }
        else if (this.pilot.isPickedUpH(this.helicopter)) {
            return new WorldEnd(true, this.lastImage()); 
        }
        else {
            return new WorldEnd(false, this.makeImage());
        }
    }
}



//represents anything that can be iterated over
interface Iterable<T> {
    // returns an iterator over this collection
    Iterator<T> iterator();
}

//represents the ability to produce a sequence of value of type T,
//one at a time
interface Iterator<T> {

}

//represents the ability to produce a list of T,
//one at a time
class IListIterator<T> implements Iterator<T> {
    IList<T> items;
    IListIterator(IList<T> items) {
        this.items = items;
    }

}

//represents an interface IList
interface IList<T> extends Iterable<T> {

    // draw a list of T
    WorldImage drawLoT();
    // determine if this list is cons
    boolean isCons();

}

//represents an empty list 
class MtList<T> implements IList<T> {

    // draw a list of T
    public WorldImage drawLoT() {
        return new CircleImage(new Posn(0, 0), 0, new White());
    }

    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }

    // determine if this list is cons
    public boolean isCons() {
        return false;
    }

}
//represents a cons list
class ConsList<T> implements IList<T> {
    T first;
    IList<T> rest;

    //the constructor
    ConsList(T first, IList<T> rest) {
        this.first = first;
        this.rest = rest;
    }

    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }

    // determine if this list is cons
    public boolean isCons() {
        return true;
    }

    // draw a list of T
    public WorldImage drawLoT() {
        return new OverlayImages(((Target) this.first).draw(),
                this.rest.drawLoT());
    }
}




//represents the examples class
class ExamplesForbiddenIsland {
    // produce the canvas
    WorldCanvas c = new WorldCanvas(ForbiddenIslandWorld.ISLAND_SIZE * 10 + 10,
            ForbiddenIslandWorld.ISLAND_SIZE * 10 + 10);

    Cell c1 = new Cell(-32.0, 0, 0);
    Cell c2 = new Cell(-30.0, 1, 1);
    Cell c3 = new Cell(-28.0, 2, 2);
    Cell c4 = new Cell(-26.0, 3, 3);
    Cell c5 = new Cell(12.0, 23, 13);


    ArrayList<Double> ad0 = new ArrayList<Double>();
    ArrayList<Double> ad1 = new ArrayList<Double>(Arrays.asList(-32.0));
    ArrayList<Double> ad2 = new ArrayList<Double>(Arrays.asList(-30.0));

    ArrayList<Cell> ac0 = new ArrayList<Cell>();
    ArrayList<Cell> ac2 = new ArrayList<Cell>(Arrays.asList(c3, c4));
    ArrayList<Cell> ac1;

    ArrayList<ArrayList<Double>> aad0;
    ArrayList<ArrayList<Double>> aad1;
    ArrayList<ArrayList<Double>> aad2;
    ArrayList<ArrayList<Cell>> aac0;
    ArrayList<ArrayList<Cell>> aac1;
    ArrayList<ArrayList<Cell>> aac2;


    ArrayList<Double> adTerrain1;
    ArrayList<ArrayList<Double>> aadTerrain1;
    ArrayList<Double> adTerrain2;
    ArrayList<ArrayList<Double>> aadTerrain2;
    ArrayList<Double> adTerrain3;
    ArrayList<ArrayList<Double>> aadTerrain3;
    ArrayList<Double> adTerrain4;
    ArrayList<ArrayList<Double>> aadTerrain4;



    Pilot p1 = new Pilot(new Cell(32.0, 32, 32), 1);
    Pilot p2 = new Pilot(new Cell(2.0, 2, 4), 2);
    Target t1 = new Target(new Cell(32.0, 32, 32));
    Target t2 = new Target(new Cell(30.0, 30, 28));
    HelicopterTarget h1 = new HelicopterTarget(new Cell(32.0, 32, 32));
    HelicopterTarget h2 = new HelicopterTarget(new Cell(0.0, 2, 16));
    HelicopterTarget h5 = new HelicopterTarget(c5);
    Target t5 = new Target(this.c5);


    IList<Target> lot0 = new MtList<Target>();
    IList<Target> lot = new ConsList<Target>(t1, this.lot0);

    // EFFECT: sets up the initial conditions for our tests, by
    // re-initializing the examples
    void initConditionAll() {
        // ArrayList<ArrayList<Double>>
        this.aad0 = new ArrayList<ArrayList<Double>>();
        this.aad1 = new ArrayList<ArrayList<Double>>();
        this.aad2 = new ArrayList<ArrayList<Double>>();
        this.aac1 = new ArrayList<ArrayList<Cell>>();

        this.aad1.add(ad1);
        this.aad1.add(ad2);

        this.aad2.add(ad0);
        this.aad2.add(ad1);
        this.aad2.add(ad2);
        // ArrayList<ArrayList<Cell>>
        this.aac0 = new ArrayList<ArrayList<Cell>>();
        this.aac1.add(ac1);
        this.aac1.add(ac2);
    }

    // examples of Forbidden Island
    ForbiddenIslandWorld w1 = new ForbiddenIslandWorld(this.aac0);
    ForbiddenIslandWorld w3 = new ForbiddenIslandWorld(aac2);
    ForbiddenIslandWorld w2;

    // EFFECT: sets up the initial conditions for w2, by
    // re-initializing the examples
    void initCondition() {
        w2 = new ForbiddenIslandWorld(aac1);
        ac1 = new ArrayList<Cell>(Arrays.asList(c1, c2));
        aac1 = new ArrayList<ArrayList<Cell>>();
        this.aac1.add(ac1);
    }

    //tests the method cellHeight()
    void testCellHeight(Tester t) {
        // sets the initial condition
        this.initConditionAll();
        ArrayList<ArrayList<Double>> testMountain = this.w1.cellHeight();
        for (int y = 0; y < ForbiddenIslandWorld.ISLAND_SIZE + 1; y++) {
            ArrayList<Double> testRow = testMountain.get(y);
            for (int x = 0; x < ForbiddenIslandWorld.ISLAND_SIZE + 1; x++) {
                double manhattan =
                        (double) ForbiddenIslandWorld.ISLAND_SIZE / 2 -
                        (Math.abs((ForbiddenIslandWorld.ISLAND_SIZE / 2) - x)
                                +
                                Math.abs((ForbiddenIslandWorld.ISLAND_SIZE / 2)
                                        - y));
                t.checkExpect(testRow.get(x), manhattan);
            }
        }
    }

    // tests the method randomCellHeight()
    void testRandomCellHeight(Tester t) {
        // sets the initial condition
        this.initConditionAll();
        ArrayList<ArrayList<Double>> testMountain = this.w1.randomCellHeight();
        for (int y = 0; y < ForbiddenIslandWorld.ISLAND_SIZE + 1; y++) {
            ArrayList<Double> testRow = testMountain.get(y);
            for (int x = 0; x < ForbiddenIslandWorld.ISLAND_SIZE + 1; x++) {
                t.checkExpect(testRow.size(),
                        ForbiddenIslandWorld.ISLAND_SIZE + 1);
            }
        }
    }

    // tests the method updateCell(ArrayList<ArrayList<Cell>>)
    // for mountain with non-random cell height
    void testUpdateCell(Tester t) {
        // set the initial condition
        this.initCondition();
        // modify
        ArrayList<ArrayList<Double>> testDouble = this.w2.cellHeight();
        ArrayList<ArrayList<Cell>> testCell = this.w2.makeCell(testDouble);
        this.w2.updateCell(testCell);
        // check the expected changes
        t.checkExpect(testCell.get(10).get(32).left.height,
                testCell.get(10).get(31).height);
        t.checkExpect(testCell.get(1).get(32).right.height,
                testCell.get(1).get(31).height);
        t.checkExpect(testCell.get(1).get(32).top.height,
                testCell.get(1).get(31).height);
        t.checkExpect(testCell.get(32).get(32).left.height,
                testCell.get(32).get(31).height);
        t.checkExpect(testCell.get(32).get(32).right.height,
                testCell.get(32).get(31).height);
        t.checkExpect(testCell.get(32).get(32).top.height,
                testCell.get(31).get(32).height);
        t.checkExpect(testCell.get(32).get(32).bottom.height,
                testCell.get(31).get(32).height);
        t.checkExpect(testCell.get(32).get(22).right.height,
                testCell.get(32).get(22).height + 1);
        t.checkExpect(testCell.get(32).get(22).left.height,
                testCell.get(32).get(22).height - 1);
        t.checkExpect(testCell.get(25).get(22).bottom.height,
                testCell.get(25).get(22).height + 1);
        t.checkExpect(testCell.get(22).get(22).top.height,
                testCell.get(22).get(22).height - 1);
    }

    // tests the method updateCell(ArrayList<ArrayList<Cell>>)
    // for island with random cell height      
    void testUpdateCellforIsland(Tester t) {
        // set the initial condition
        this.initCondition();
        // modify
        ArrayList<ArrayList<Double>> testDouble = this.w2.randomCellHeight();
        ArrayList<ArrayList<Cell>> testCell = this.w2.makeCell(testDouble);
        this.w2.updateCell(testCell);
        // check the expected changes
        t.checkExpect(testCell.get(10).get(32).left.height,
                testCell.get(10).get(31).height);
        t.checkExpect(testCell.get(1).get(1).right.height,
                testCell.get(1).get(1).height);
        t.checkExpect(testCell.get(1).get(1).top.height,
                testCell.get(1).get(1).height);
        t.checkExpect(testCell.get(1).get(1).bottom.height,
                testCell.get(1).get(1).height);
        t.checkRange(testCell.get(32).get(32).height, 0.0, 33.0);
        t.checkRange(testCell.get(32).get(32).left.height, 0.0, 33.0);
        t.checkRange(testCell.get(32).get(32).right.height, 0.0, 33.0);
        t.checkRange(testCell.get(32).get(32).top.height, 0.0, 33.0);
        t.checkRange(testCell.get(32).get(32).bottom.height, 0.0, 33.0);
    }


    // tests the method makeCell(ArrayList<ArrayList<Double>> that)
    // makes an island
    void testMakeIslandCell(Tester t) {
        // sets the initial condition
        this.initConditionAll();
        ArrayList<ArrayList<Double>> testRandom = this.w1.randomCellHeight();
        ArrayList<ArrayList<Cell>> testIsland = this.w1.makeCell(testRandom);
        for (int y = 0; y < ForbiddenIslandWorld.ISLAND_SIZE + 1; y++) {
            ArrayList<Cell> testRow = testIsland.get(y);
            for (int x = 0; x < ForbiddenIslandWorld.ISLAND_SIZE + 1; x++) {
                t.checkExpect(testRow.size(),
                        ForbiddenIslandWorld.ISLAND_SIZE + 1);
                t.checkExpect(testIsland.get(1).get(1).height, 0.0);
                t.checkRange(testRow.get(1).height, 0.0, 33.0);
                t.checkRange(testRow.get(32).height, 0.0, 33.0);
            }
        }
    }

    // tests the method makeCell(ArrayList<ArrayList<Double>> that)
    // makes a mountain
    void testMakeMountainCell(Tester t) {
        // sets the initial condition
        this.initConditionAll();
        ArrayList<ArrayList<Double>> testDouble = this.w1.cellHeight();
        ArrayList<ArrayList<Cell>> testMountain = this.w1.makeCell(testDouble);
        for (int y = 0; y < ForbiddenIslandWorld.ISLAND_SIZE + 1; y++) {
            ArrayList<Cell> testRow = testMountain.get(y);
            for (int x = 0; x < ForbiddenIslandWorld.ISLAND_SIZE + 1; x++) {
                double manhattan =
                        (double) ForbiddenIslandWorld.ISLAND_SIZE / 2 -
                        (Math.abs((ForbiddenIslandWorld.ISLAND_SIZE / 2) - x)
                                + Math.abs((ForbiddenIslandWorld.ISLAND_SIZE
                                        / 2) - y));
                t.checkExpect(testRow.size(), ForbiddenIslandWorld.ISLAND_SIZE
                        + 1);
                t.checkExpect(testRow.get(x).height, manhattan);
            }
        }
    }

    // tests the method floodNeighbors(int waterHeight)    
    void testFloodNeighbors(Tester t) {
        // sets the initial condition
        this.initConditionAll();
        ArrayList<ArrayList<Double>> testRandom = this.w3.cellHeight();
        ArrayList<ArrayList<Cell>> testMountain = this.w3.makeCell(testRandom);
        for (int y = 0; y < ForbiddenIslandWorld.ISLAND_SIZE + 1; y++) {
            ArrayList<Cell> testRow = testMountain.get(y);
            for (int x = 0; x < ForbiddenIslandWorld.ISLAND_SIZE + 1; x++) {
                t.checkExpect(testRow.size(),
                        ForbiddenIslandWorld.ISLAND_SIZE + 1);
                t.checkRange(this.w3.waterHeight - testRow.get(x).height,
                        -33.0, 33.0);
                t.checkRange(this.w3.waterHeight - testRow.get(x).left.height,
                        -33.0, 33.0);
                t.checkRange(this.w3.waterHeight - testRow.get(x).right.height,
                        -33.0, 33.0);
                t.checkRange(this.w3.waterHeight - testRow.get(x).top.height,
                        -33.0, 33.0);
                t.checkRange(this.w3.waterHeight -
                        testRow.get(x).bottom.height,
                        -33.0, 33.0);
            }
        }
        testMountain.get(32).get(32).floodNeighbors(32);
        t.checkExpect(testMountain.get(32).get(32).right.isFlooded, true);
        t.checkExpect(testMountain.get(32).get(32).left.isFlooded, true);
        t.checkExpect(testMountain.get(32).get(32).top.isFlooded, true);
        t.checkExpect(testMountain.get(32).get(32).bottom.isFlooded, true);
    }

    // tests the method floodCells()                   
    void testFloodCells(Tester t) {
        // set the initial condition
        this.initCondition();
        // modify
        ArrayList<ArrayList<Double>> testDouble = this.w2.cellHeight();
        ArrayList<ArrayList<Cell>> testCell = this.w2.makeCell(testDouble);
        this.w2.waterHeight = 1;
        this.w2.updateCell(testCell);
        t.checkExpect(testCell.get(15).get(20).isFlooded, false);

        this.w2.waterHeight = 30;
        this.w2.floodCells();
        t.checkExpect(testCell.get(32).get(32).right.isFlooded, false);
        t.checkExpect(testCell.get(15).get(20).isFlooded, true); 
        t.checkExpect(testCell.get(15).get(19).isFlooded, true); 
        t.checkExpect(testCell.get(14).get(20).isFlooded, true); 
        t.checkExpect(testCell.get(16).get(20).isFlooded, true); 
    }


    // tests the method setCorner()
    boolean testSetCorner(Tester t) {
        // sets the initial condition
        this.initConditionAll();
        return t.checkRange(this.w1.setCorner(0, 20), 0.0, 12.0) &&       
                t.checkRange(this.w1.setCorner(10, 30), 0.0, 22.0) &&    
                t.checkRange(this.w1.setCorner(20, 30), 0.0, 26.0);     


    }

    // test the method setMiddle()
    void testSetMiddle(Tester t) {
        // sets the initial condition
        this.initConditionAll();
        // check the expected changes                                   
        t.checkRange(this.w1.setMiddle(0, 1, 0, 1), 0.0, 1.0); 
        t.checkRange(this.w1.setMiddle(0, 2, 0, 2), 0.0, 2.0);     
        t.checkRange(this.w1.setMiddle(0, 10, 0, 10), 0.0, 6.0);  
    }

    // EFFECT: sets up the initial conditions for terrainHelp, by
    // re-initializing the examples
    void initConditionTerrain() {
        // 2 x 3 rectangle
        adTerrain2 = new ArrayList<Double>();
        aadTerrain2 = new ArrayList<ArrayList<Double>>();
        adTerrain2.add(0.0);
        adTerrain2.add(0.0);
        adTerrain2.add(0.0);
        aadTerrain2.add(adTerrain2);
        aadTerrain2.add(adTerrain2);

        // 2 x 2 rectangle
        adTerrain1 = new ArrayList<Double>();
        aadTerrain1 = new ArrayList<ArrayList<Double>>();

        adTerrain1.add(0.0);
        adTerrain1.add(0.0);
        aadTerrain1.add(adTerrain1);
        aadTerrain1.add(adTerrain1);

        // 3 x 2 rectangle
        adTerrain3 = new ArrayList<Double>();
        aadTerrain3 = new ArrayList<ArrayList<Double>>();

        adTerrain3.add(0.0);
        adTerrain3.add(0.0);
        aadTerrain3.add(adTerrain3);
        aadTerrain3.add(adTerrain3);
        aadTerrain3.add(adTerrain3);

        // 3 x 3 rectangle
        adTerrain4 = new ArrayList<Double>();
        aadTerrain4 = new ArrayList<ArrayList<Double>>();

        adTerrain4.add(0.0);
        adTerrain4.add(0.0);
        adTerrain4.add(0.0);
        aadTerrain4.add(adTerrain4);
        aadTerrain4.add(adTerrain4);
        aadTerrain4.add(adTerrain4);


    }

    // tests the method terrainHelp()
    void testTerrainHelp(Tester t) {
        // sets the initial condition
        this.initConditionTerrain();
        // modify the condition
        this.w1.terrainHelp(this.aadTerrain1, 0, 1, 0, 1);
        this.w1.terrainHelp(this.aadTerrain2, 0, 2, 0, 1);
        this.w1.terrainHelp(this.aadTerrain3, 0, 1, 0, 2);
        this.w1.terrainHelp(this.aadTerrain4, 0, 2, 0, 2);
        // check the expect changes
        // 2 x 2 Rectangle
        t.checkRange(this.aadTerrain1.get(0).get(1), 0.0, 1.0);
        t.checkRange(this.aadTerrain1.get(0).get(0), 0.0, 1.0);
        t.checkRange(this.aadTerrain1.get(1).get(0), 0.0, 1.0);
        t.checkRange(this.aadTerrain1.get(1).get(1), 0.0, 1.0);
        // 2 x 3 Rectangle
        t.checkRange(this.aadTerrain2.get(0).get(1), 0.0, 2.0); 
        t.checkRange(this.aadTerrain2.get(1).get(1), 0.0, 2.0);   
        t.checkRange(this.aadTerrain2.get(0).get(0), 0.0, 2.0); 
        t.checkRange(this.aadTerrain2.get(1).get(0), 0.0, 2.0);  
        // 3 x 2 Rectangle
        t.checkRange(this.aadTerrain3.get(0).get(1), 0.0, 2.0);
        t.checkRange(this.aadTerrain3.get(1).get(1), 0.0, 2.0);
        // 3 x 3 Rectangle
        t.checkRange(this.aadTerrain4.get(1).get(0), 0.0, 2.0); 
        t.checkRange(this.aadTerrain4.get(0).get(1), 0.0, 2.0);
        t.checkRange(this.aadTerrain4.get(1).get(1), 0.0, 2.0);
        t.checkRange(this.aadTerrain4.get(2).get(1), 0.0, 2.0);
        t.checkRange(this.aadTerrain4.get(1).get(2), 0.0, 2.0);
    }

    // tests the method terrain()
    void testTerrain(Tester t) {
        // sets the initial Conditions
        this.initConditionAll();
        ArrayList<ArrayList<Double>> testMountain = this.w1.terrain();
        for (int y = 0; y < ForbiddenIslandWorld.ISLAND_SIZE + 1; y++) {
            ArrayList<Double> testRow = testMountain.get(y);
            for (int x = 0; x < ForbiddenIslandWorld.ISLAND_SIZE + 1; x++) {
                t.checkExpect(testRow.size(),
                        ForbiddenIslandWorld.ISLAND_SIZE + 1);
                t.checkRange(testRow.get(x), 0.0, 33.0);
            }
        }

        t.checkExpect(testMountain.get(0).get(
                ForbiddenIslandWorld.ISLAND_SIZE / 2), 1.0);
        t.checkExpect(testMountain.get(
                ForbiddenIslandWorld.ISLAND_SIZE / 2).get(0), 1.0);
        t.checkExpect(testMountain.get(
                ForbiddenIslandWorld.ISLAND_SIZE / 2).get(
                        ForbiddenIslandWorld.ISLAND_SIZE), 1.0);
        t.checkExpect(testMountain.get(
                ForbiddenIslandWorld.ISLAND_SIZE).get(
                        ForbiddenIslandWorld.ISLAND_SIZE / 2), 1.0);

        t.checkExpect(testMountain.get(
                ForbiddenIslandWorld.ISLAND_SIZE / 2).get(
                        ForbiddenIslandWorld.ISLAND_SIZE / 2), 32.0);
    }


    // tests the method isPickedUp()

    boolean testIsPickedUpT(Tester t) {
        return t.checkExpect(this.p1.isPickedUpT(this.t1), true) &&
                t.checkExpect(this.p1.isPickedUpT(this.t2), false) &&
                t.checkExpect(this.p2.isPickedUpT(this.t2), false);
    }
    // tests the method isPickedUp()
    boolean testIsPickedUpH(Tester t) {
        return t.checkExpect(this.p1.isPickedUpH(this.h1), true) &&
                t.checkExpect(this.p1.isPickedUpH(this.h2), false);
    }


    // changes the cell's flooded status
    void changeFlooded() {
        this.c5.isFlooded = true;
    }
    // tests the method isHelicopterFlooded()
    void testIsHelicopterFlooded(Tester t) {
        t.checkExpect(this.h1.isHelicopterFlooded(), false);
        // change the c5 to be flooded
        this.changeFlooded();
        t.checkExpect(this.h5.isHelicopterFlooded(), true);
    }





    // tests the method isTargetFlooded()
    boolean testIsTargetFlooded(Tester t) {
        // change the c5 to be flooded
        this.changeFlooded();
        return t.checkExpect(this.t5.isTargetFlooded(), true) &&
                t.checkExpect(this.t1.isTargetFlooded(), false);

    }

    Pilot p5 = new Pilot(this.c5, 0);
    // tests the method isTargetFlooded()
    boolean testIsFlooded(Tester t) {
        // change the c5 to be flooded
        this.changeFlooded();
        return t.checkExpect(this.p5.isFlooded(), true) &&
                t.checkExpect(this.p1.isFlooded(), false);

    }

    // tests the method randomPilot()
    boolean testRandomPilot(Tester t) {
        // sets the initial condition
        this.initConditionAll();
        this.w3.makeCell(this.w3.cellHeight());
        return t.checkRange(this.w3.randomPilot().height, 0.0, 33.0);
    }

    // tests the method randomTarget()
    boolean testRandomTarget(Tester t) {
        // sets the initial condition
        this.initConditionAll();
        this.w3.makeCell(this.w3.randomCellHeight());
        return t.checkRange(this.w3.randomTarget().c.height, 0.0, 33.0);
    }

    // tests the method randomLoTarget()
    boolean testRandomLoTarget(Tester t) {
        // sets the initial condition
        this.initConditionAll();
        return t.checkExpect(this.w1.randomLoTarget(0).isCons(), false) &&
                t.checkExpect(this.w1.randomLoTarget(6).isCons(), true);
    }


    // tests the method updatePicked(Target t)
    boolean testUpdatePicked(Tester t) {
        return t.checkExpect(this.p1.updatePicked(t1), 2) &&
                t.checkExpect(this.p1.updatePicked(t2), 1);

    }

    // tests the method findHighestCell(ArrayList<ArrayList<Cell>> secondboard)
    boolean testFindHighestCell_Mountain(Tester t) {
        // sets the initial Condition
        this.initConditionAll();
        ArrayList<ArrayList<Double>> arrayDouble = this.w1.cellHeight();
        ArrayList<ArrayList<Cell>> arrayCell = this.w1.makeCell(arrayDouble);
        return t.checkExpect(
                ForbiddenIslandWorld.findHighestCell(arrayCell).height,
                arrayCell.get(32).get(32).height);
    }

    // tests the method findHighestCell(ArrayList<ArrayList<Cell>> secondboard)
    boolean testFindHighestCell_Island(Tester t) {
        // sets the initial Condition
        this.initConditionAll();
        ArrayList<ArrayList<Double>> arrayDouble = this.w1.randomCellHeight();
        ArrayList<ArrayList<Cell>> arrayCell = this.w1.makeCell(arrayDouble);
        return t.checkRange(
                ForbiddenIslandWorld.findHighestCell(arrayCell).height,
                0.0, 33.0);
    }



    // tests the method isCons()
    boolean testIsCons(Tester t) {
        return t.checkExpect(this.lot0.isCons(), false) &&
                t.checkExpect(this.lot.isCons(), true);
    }

    // tests the method onTick()
    void testOnTick(Tester t) {
        // modify
        this.w2.onTick();
        // initial
        this.initCondition();
        //check if the water level if it is increasing at every tick
        this.w2.waterHeight = this.w2.waterHeight + 1;
    }

    // run the first game
    void testAnimation(Tester t) {
        // sets the initial condition
        this.initConditionAll();
        // this will make a mountain with non-random cell height
        //    this.w1.makeCell(this.w1.cellHeight());
        // this will make an island with random cell height
        this.w1.makeCell(this.w1.randomCellHeight());
        // this will make an terrain
        // this.w1.makeCell(this.w1.terrain());
        this.w1.bigBang(ForbiddenIslandWorld.ISLAND_SIZE * 10 + 10,
                ForbiddenIslandWorld.ISLAND_SIZE * 10 + 10, 0.5);
    }

}