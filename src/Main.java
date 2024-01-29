import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
            GameBoard gb = new GameBoard();
            gb.readInput("C:\\Users\\lmunn\\Desktop\\CarEscape\\CarEscape\\src\\1.txt");
            gb.initializeBoard();
//            ArrayList<Pair> path = gb.getPlan();
//            for (int i=0; i<path.size(); i++)
//                System.out.println(path.get(i).id + " " + path.get(i).direction);
//            System.out.println(gb.getNumOfPaths());

//            while(gs.getGb().canMove(gb.trucks.get(0), 'w')){
//                gs = gb.move(gs.getGb().trucks.get(0), 'w');
//                gs.print();
//                System.out.println(gs.getPair().printPair());
//            }
            ArrayList<GameBoard.Pair> pairs = gb.getPlan();
            for(int i = 0; i < pairs.size(); i++){
                System.out.println(pairs.get(i).printPair());
            }
            System.out.println(gb.getNumOfPaths());
//            System.out.println("Plan: " + gb.getPlan());


    }
    }