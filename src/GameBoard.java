/**
 * @author Luke Munn
 */
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * GameBoard class representing the escape game
 * which holds trucks.
 */
public class GameBoard {

    ArrayList<Truck> trucks;
    int numCars;
    String[][] board;
    Pair pair;
    ArrayList<Pair> paths;

    /**
     * Empty GameBoard constructor
     */
    public GameBoard() {
    }

    /**
     * Creates GameBoard based on a given ArrayList
     * of Trucks and initializes the board
     * @param trucks
     */
    public GameBoard(ArrayList<Truck> trucks) {
        this.trucks = trucks;
        numCars = this.trucks.size();
        this.initializeBoard();
    }

    /**
     * Creates a GameBoard based on an old
     * GameBoard and initializes the board
     * @param oldGameboard
     */
    public GameBoard(GameBoard oldGameboard){
        this.trucks = new ArrayList<Truck>();
        for(int i = 0; i < oldGameboard.trucks.size(); i++){
            this.trucks.add(new Truck(oldGameboard.trucks.get(i)));
        }
        numCars = this.trucks.size();
        this.initializeBoard();
    }

    /**
     * Reads a GameBoard from a given text file
     * @param FileName
     * @throws IOException
     */
    public void readInput(String FileName) throws IOException {
        File file = new File(FileName);
        trucks = new ArrayList<Truck>();
        Scanner scnr = new Scanner(file);
        numCars = scnr.nextInt();
        scnr.nextLine();
        for(int i = 0; i < numCars; i++){
            Truck t;
            ArrayList<Integer> in = new ArrayList<>();
            String[] s = scnr.nextLine().split("\\s+");
            for (String f : s) {
                in.add(Integer.parseInt(f));
            }
            t = new Truck(i, in);
            trucks.add(t);
        }
    }

    /**
     * Method to deepCopy an ArrayList of Trucks
     * @param t
     * @param l
     * @return ArrayList of Trucks
     */
    public ArrayList<Truck> deepCopyTrucks(Truck t, int l){
        ArrayList<Truck> copyTrucks = new ArrayList<Truck>();
        for(int i = 0; i < this.trucks.size(); i++){
            if(i == l)
                copyTrucks.add(t);
            else
                copyTrucks.add(new Truck(this.trucks.get(i)));
        }
        return copyTrucks;
    }

    /**
     * Method that creates a 2d array representation
     * of the current GameBoard and places Trucks within
     * their coordinates.
     */
    public void initializeBoard() {
        board = new String[6][6];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                board[i][j] = ".";
            }
        }
        for (Integer i = 0; i < numCars; i++) {
            for (int j = 0; j < trucks.get(i).getLength(); j++){
                int k = trucks.get(i).getCoordinates().get(j);
                String truck = i.toString();
                board[(k-1) / 6][(k - 1) % 6] = truck;
            }
        }
        Pair pair = new Pair(-1, ' ');
    }

    /**
     * Method that returns a LinkedList of newly
     * created GameStates based off of possible moves
     * a given GameState can make. This list
     * represents the neighbors to a given GameState
     * @param gs
     * @return LinkedList of GameStates
     */
    public LinkedList<GameState> getNeighbors(GameState gs){
        LinkedList<GameState> neighbors = new LinkedList<>();
        ArrayList<Truck> trucks = gs.gb.trucks;
        for(int i = 0; i < trucks.size(); i++){
            ArrayList<Truck> trucksCopy;
            Truck t = new Truck(trucks.get(i));
            if(t.getOrientation() == 0) { //If vehicle is horizontal
                if (t.canMove(gs,'w')){
                    Truck moved = new Truck(t);
                    Pair pair = new Pair(t.getId(), 'w');
                    moved.moveDirection('w');
                    trucksCopy = gs.gb.deepCopyTrucks(moved, i);
                    GameState newGameState = new GameState(trucksCopy, pair);
                    neighbors.add(newGameState);
                }
                if (t.canMove(gs,'e')){
                    Truck moved = new Truck(t);
                    Pair pair = new Pair(t.getId(), 'e');
                    moved.moveDirection('e');
                    trucksCopy = gs.gb.deepCopyTrucks(moved, i);
                    GameState newGameState = new GameState(trucksCopy, pair);
                    neighbors.add(newGameState);
                }
                }
            else { //If vehicle is vertical
                    if (t.canMove(gs,'n')){
                        Truck moved = new Truck(t);
                        Pair pair = new Pair(t.getId(), 'n');
                        moved.moveDirection('n');
                        trucksCopy = gs.gb.deepCopyTrucks(moved, i);
                        GameState newGameState = new GameState(trucksCopy, pair);
                        neighbors.add(newGameState);
                    }
                    if (t.canMove(gs,'s')){
                        Truck moved = new Truck(t);
                        Pair pair = new Pair(t.getId(), 's');
                        moved.moveDirection('s');
                        trucksCopy = gs.gb.deepCopyTrucks(moved, i);
                        GameState newGameState = new GameState(trucksCopy, pair);
                        neighbors.add(newGameState);
                    }
                }
            }
        return neighbors;
    }

    /**
     * Method that uses bfs traversal to return an
     * ArrayList of paths to a solution in our original
     * GameBoard
     * @return ArrayList paths
     */
    public ArrayList<Pair> getPlan(){
        ArrayList<Pair> plans = new ArrayList<>();
        GameState base = new GameState(this); //base GameState
        HashKey baseKey = new HashKey(base.gb.board);
        Graph G = new Graph(baseKey.hashCode());
        LinkedList<GameState> Q = new LinkedList<>();
        HashMap<Integer, GameState> neighborList = new HashMap<>();
        Stack<GameState> moves = new Stack<>();
        int planIncrement = 1;
        base.explored = true;
        base.layer = 0;
        base.cnt = 1; // cnt keeps track of number of paths from u
        Q.add(base);
        while(Q.size() != 0){
            GameState v = Q.poll();
            HashKey x = new HashKey(v.gb.board);
            LinkedList<GameState> neighbors = getNeighbors(v);
            int neighborCount = neighbors.size();
            for(int i = 0; i < neighborCount; i++) {
                GameState neighbor = neighbors.get(i);
                HashKey z = new HashKey(neighbor.gb.board);
                int code = z.hashCode();
                if (G.hashedMap.containsKey(z.hashCode())){
                    neighbors.remove(i);
                    i--;
                    neighborCount--;
                }
                else if (!neighborList.containsKey(code)){ //neighborList lets us know if
                                                            //the node has been explored.
                    // first time w is explored
                    neighbor.cnt = v.cnt;
                    neighbor.explored = true;
                    neighbor.layer = v.layer + 1;
                    neighborList.put(code, neighbor);
                    Q.add(neighbor);
                    moves.push(neighborList.get(code));
                }
                else {
                    if (neighborList.get(code).layer == v.layer + 1) {
                        // v and w’s parents are present in the same layer
                        neighborList.get(code).cnt = v.cnt + neighborList.get(code).cnt;
                        Q.add(neighbor);
                        moves.push(neighborList.get(code));
                    }
                }
                if(isSolution(z)){
                    planIncrement = v.layer;
                    GameState gs = moves.pop(); //winning
                    Pair move = gs.getPair();
                    char d = move.getDirection(); //directions to get to winning
                    int prevCnt = gs.cnt;
                    plans.add(gs.getPair());
                    while(planIncrement != z.layer){
                        GameState parent = moves.pop();
                        int truckId = move.getId();
                        if(parent.layer == planIncrement && parent.cnt <= prevCnt){
                            //is this for sure a parent of the last gs
                            if(parent.gb.trucks.get(truckId).canMove(parent, d)){
                                Truck moved = new Truck(parent.gb.trucks.get(truckId));
                                moved.moveDirection(d);
                                ArrayList<Truck> trucksCopy = parent.gb.deepCopyTrucks(moved, truckId);
                                GameState newGameState = new GameState(trucksCopy, pair);
                                HashKey y = new HashKey(newGameState.gb.board);
                                int newHashCode = y.hashCode();
                                if(newHashCode == gs.hashkey.hashCode()){
                                    plans.add(parent.getPair());
                                    planIncrement--;
                                    prevCnt = parent.cnt;
                                    gs = parent;
                                    move = parent.getPair();
                                    d = move.getDirection();
                                }
                            }
                        }
                    }
                    paths = new ArrayList<>();
                    for(i = plans.size()-1; i >=0 ; i--){
                        paths.add(plans.get(i));
                    }
                    return paths;
                }
            }
            G.hashedMap.put(x.hashCode(), neighbors);
        }
        paths = new ArrayList<>();
        return paths;
    }

    /**
     * Method to realize if a current state
     * is a solution to the game.
     * @param state
     * @return
     */
    public boolean isSolution(HashKey state){
        String[] solution = state.c;
        String zero = solution[17];
        if(zero.equals("0")){
            return true;
        }
        return false;
    }

    public int getNumOfPaths() {
        GameState base = new GameState(this); //base GameState
        HashKey baseKey = new HashKey(base.gb.board);
        Graph G = new Graph(baseKey.hashCode());
        LinkedList<GameState> Q = new LinkedList<>();
        HashMap<Integer, GameState> neighborList = new HashMap<>();
        int numOfPaths = 0;
        base.explored = true;
        base.layer = 0;
        base.cnt = 1; // cnt keeps track of number of paths from u
        Q.add(base);
        while(Q.size() != 0){
            GameState v = Q.poll();
            HashKey x = new HashKey(v.gb.board);
            LinkedList<GameState> neighbors = getNeighbors(v);
            int neighborCount = neighbors.size();
            for(int i = 0; i < neighborCount; i++) {
                GameState neighbor = neighbors.get(i);
                HashKey z = new HashKey(neighbor.gb.board);
                int code = z.hashCode();
                if (G.hashedMap.containsKey(z.hashCode())){
                    neighbors.remove(i);
                    i--;
                    neighborCount--;
                }
                else if (!neighborList.containsKey(code)){
                    // first time w is explored
                    neighbor.cnt = v.cnt;
                    neighbor.explored = true;
                    neighbor.layer = v.layer + 1;
                    neighborList.put(code, neighbor);
                    Q.add(neighbor);
                }
                else {
                    if (neighborList.get(code).layer == v.layer + 1) {
                        // v and w’s parents are present in the same layer
                        neighborList.get(code).cnt = v.cnt + neighborList.get(code).cnt;
                        Q.add(neighbor);
                    }
                }
                if(isSolution(z)){
                    numOfPaths = v.cnt;
                    return numOfPaths;
                }
            }
            G.hashedMap.put(x.hashCode(), neighbors);
        }
        return numOfPaths;
    }

    /**
     * Class GameState that represents a node holding a GameBoard
     * at a specific state (after one move).
     */
    class GameState{
    public GameBoard gb;
    public Pair pair;
    public HashKey hashkey;
    public boolean explored;
    public int layer;
    public int cnt;

        /**
         * Constructor to create a new GameState based on
         * an already existing Gameboard
         * @param oldGameboard
         */
    public GameState(GameBoard oldGameboard){
        this.gb = new GameBoard(oldGameboard);
        this.gb.initializeBoard();
        hashkey = new HashKey(gb.board);
        this.pair = new Pair(-1, ':');
        }

        /**
         * Constructor to create a new GameState based on
         * an already existing ArrayList of Trucks and a
         * Pair that represents the move it made to get
         * to this specific GameState.
         *
         * @param trucks
         * @param pair
         */
    public GameState(ArrayList<Truck> trucks, Pair pair){
        this.gb = new GameBoard(trucks);
        this.pair = pair;
        hashkey = new HashKey(gb.board);
    }

    public Pair getPair() {
        return pair;
    }

}

    /**
     * Pair class that represents a single move
     * by a Truck.
     */
    class Pair {
    int id;
    char direction; // {’e’, ’w’, ’n’, ’s’}
    public Pair(int i, char d) { id = i; direction = d; }
    char getDirection() { return direction; }
    int getId() { return id; }
    void setDirection(char d) { direction = d; }
    void setId(int i) { id = i; }
    String printPair(){
        String s = String.valueOf(id)+ " " + direction;
        return s;
    }
}


    /**
     * Truck class to represent a vehicle on the GameBoard.
     */
    class Truck{
    private ArrayList<Integer> coordinates;
    private int orientation;
    private int id;

        /**
         * Truck constructor that creates a new Truck
         * based on a given id and coordinates
         * @param id
         * @param coordinates
         */
    public Truck(int id, ArrayList<Integer> coordinates){
        this.id = id;
        this.coordinates = coordinates;
        orientation = 1;
        if (coordinates.get(1) - coordinates.get(0) != 6){
            orientation = 0;
        }
    }

        /**
         * Truck constructor that creates a new Truck
         * based on an already existing Truck
         * @param oldTruck
         */
    public Truck(Truck oldTruck){
        this.orientation = oldTruck.orientation;
        this.id = oldTruck.id;
        this.coordinates = new ArrayList<Integer>();
        for(int i = 0; i < oldTruck.coordinates.size(); i++){
            this.coordinates.add(oldTruck.coordinates.get(i));
        }
    }
    public int getId(){
        return id;
    }
    public int getOrientation(){
        return orientation;
    }

    public ArrayList<Integer> getCoordinates(){
        return coordinates;
    }

    /**
     * Method to see if a car at a given GameState can move
     * in a desired direction.
     * @param gs
     * @param d
     * @return boolean
     */
    public boolean canMove(GameState gs, char d){
        if(this.getOrientation() == 0) { //If vehicle is horizontal
            if (d == 'w') {
                if((this.getCoordinates().get(0) - 1) % 6 == 0){
                    return false;
                }
                int k = this.getCoordinates().get(0);
                if(gs.gb.board[(k-1) / 6][((k - 1) % 6) - 1] != "."){
                    return false;
                }
                return true;
            }
            if (d == 'e') {
                if(this.getCoordinates().get(this.getLength()-1) % 6 == 0){
                    return false;
                }
                int k = this.getCoordinates().get(this.getLength()-1);
                if(gs.gb.board[(k-1) / 6][((k - 1) % 6) + 1] != "."){
                    return false;
                }
                return true;
            }
        }
        else {
            if(d == 'n'){
                if((this.getCoordinates().get(0) - 1) / 6 == 0){
                    return false;
                }
                int k = this.getCoordinates().get(0);
                if(gs.gb.board[((k-1) / 6)-1][((k - 1) % 6)] != "."){
                    return false;
                }
                return true;
            }
            if(d == 's'){
                if((this.getCoordinates().get(this.getLength()-1) - 1) / 6 == 5){
                    return false;
                }
                int k = this.getCoordinates().get(this.getLength()-1);
                if(gs.gb.board[((k-1) / 6)+1][((k - 1) % 6)] != "."){
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Called in order to move a truck. This alters
     * the instance of the Truck to a new coordinate
     * based on the direction parameter d.
     * @param d
     */
    public void moveDirection(char d){
        if(this.getOrientation() == 0) { //If vehicle is horizontal
            if (d == 'w') {
                for(int i = 0; i < this.getLength(); i++){
                    this.coordinates.set(i, this.coordinates.get(i)-1);
                }
            }
            if (d == 'e') {
                for(int i = 0; i < this.getLength(); i++){
                    this.coordinates.set(i, this.coordinates.get(i)+1);
                }
            }
        }
        else {
            if(d == 'n'){
                for(int i = 0; i < this.getLength(); i++){
                    this.coordinates.set(i, this.coordinates.get(i)-6);
                }
            }
            if(d == 's'){
                for(int i = 0; i < this.getLength(); i++){
                    this.coordinates.set(i, this.coordinates.get(i)+6);
                }
            }
        }
    }
    public int getLength(){
        return coordinates.size();
    }

}

    /**
     * HashKey class that converts the 2d representation
     * of a GameBoard into a 1d array, and allows that
     * array to be hashed into a special hashCode
     */
    class HashKey {
    String[] c; // attribute
    boolean explored;
    int layer;
    int cnt;
    public HashKey(String[][] inputc) {
        c = new String[inputc.length * inputc[0].length];
        for(int i = 0; i < inputc.length; i++){
            String[] row = inputc[i];
            for(int j = 0; j < inputc.length; j++){
                String num = inputc[i][j];
                c[i*row.length+j] = num;
            }
        }

    }

    /**
     * Method to create a hashCode of a HashKey
     * @return int representation of HashKey
     */
    public int hashCode() {
        return Arrays.hashCode(c); // using default hashing of arrays
    }

}

    /**
     * Graph class that holds a linked list to represent
     * the iteratively built graph in the bfs functions.
     */
    class Graph {

    HashMap<Integer, LinkedList<GameState>> hashedMap;

    /**
     * Creates new Graph based off of an int hashCode that
     * is created to represent a specific GameState.
     * @param hash
     */
    public Graph(Integer hash) {
        hashedMap = new HashMap<Integer, LinkedList<GameState>>();
        LinkedList<GameState> l = new LinkedList<>();
        hashedMap.put(hash, l);
    }

}
}

