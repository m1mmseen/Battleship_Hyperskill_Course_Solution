package battleship;
import com.sun.source.doctree.HiddenTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Main {

    public enum Ships {
        AIRCRAFT("Aircraft Carrier",5, false, 0),
        BATTLESHIP("Battleship",4, false, 1),
        SUBMARINE("Submarine", 3, false, 2),
        CRUISER("Cruiser", 3, false, 3),
        DESTROYER("Destroyer", 2, false, 4);

        private final String name;
        private final int size;
        private boolean sank;

        private int index;

        Ships(String name,int size, boolean sank, int index) {
            this.name = name;
            this.size = size;
            this.sank = sank;
            this.index = index;

        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }
        public int getSize() {
            return size;
        }
        public static int getShipSize(int index) {
            for (Ships ship : Ships.values()) {
                if (index == ship.index) {
                    return ship.size;
                }
            }
            return -1;
        }

        public static int getSank() {
            int counter = 0;
            for (Ships ship : Ships.values()) {
                if (ship.sank){
                    counter++;
                }
            }
            return counter;
        }

        public static void setSank(int input) {

            for (Ships value : values()) {
                if (value.index == input) {
                    value.sank = true;
                }
            }
        }
    }

    public static enum CoordinateSystem {
        A("A",1), B("B",2), C("C",3), D("D",4), E("E",5), F("F",6), G("G",7), H("H",8), I("I",9), J("J",10);

        private final String name;

        private final int index;

        CoordinateSystem(String name, int index) {
            this.name = name;
            this.index = index;
        }
        public static String getConst(int input) {

            for (CoordinateSystem value : values()) {
                if (value.index == input) {
                    return value.toString();
                }
            }
            return "";
        }
        public static int getIndex(String input) {

            for (CoordinateSystem value : values()) {
                if (value.name.equals(input)) {
                    return value.index;
                }
            }
            return -1;
        }
    }

    public static Scanner sc = new Scanner(System.in);
    // ----- SETUP PLAYER_ONE ------//
    public static String[][] gameboardPlayerOne = createBoard();

    public static String[][] fogOfWarPlayerOne = createBoard();

    public static ArrayList<ArrayList<ArrayList<Integer>>> hitShipsPlayerOne = new ArrayList<>(5);

    // ------ SETUP PLAYER_TWO------//
    public static String[][] gameboardPlayerTwo = createBoard();

    public static String[][] fogOfWarPlayerTwo = createBoard();

    public static ArrayList<ArrayList<ArrayList<Integer>>> hitShipsPlayerTwo = new ArrayList<>(5);

    public static int activePlayer = 1;

    public static void main(String[] args) {
        System.out.printf("Player %d, place your ships to the game field%n", activePlayer);
        System.out.println();
        prepareGame(gameboardPlayerOne);
        switchPlayer();
        System.out.printf("Player %d, place your ships to the game field%n", activePlayer);
        System.out.println();
        prepareGame(gameboardPlayerTwo);
        switchPlayer();

        playGame();

        sc.close();

    }

    // ------ MAIN METHODS ------//
    public static void prepareGame(String[][] gameboard) {
        printBoard(gameboard);
        System.out.println();
        for (Ships ship : Ships.values()){
            System.out.printf("Enter the coordinates of the %s (%d cells):%n", ship.getName(), ship.getSize());
            setShip(gameboard,ship);
            printBoard(gameboard);
        }
    }

    public static void playGame() {
        do {
            switch (activePlayer) {
                case 1:
                    takeShot(gameboardPlayerOne, fogOfWarPlayerOne, gameboardPlayerTwo, fogOfWarPlayerTwo);
                    break;
                case 2:
                    takeShot(gameboardPlayerTwo, fogOfWarPlayerTwo, gameboardPlayerOne, fogOfWarPlayerOne);
                    break;
            }
        } while(!hitShipsPlayerOne.isEmpty() && !hitShipsPlayerTwo.isEmpty());
    }
    //____________________________________________________________________________________


    // ------ PREPARE GAME METHODS ------//
    public static void setShip(String [][] gameboard,Ships ship) {
        int curShip = ship.getIndex();
        boolean uncheckedCoordinates = true;
        String shipOrientation = "";

        int yone = -1;
        int xone = -1;
        int ytwo = -1;
        int xtwo = -1;
        int horizontalDifference = -1;
        int verticalDifference = -1;


        do {
            String[] coordinates = getCoordinates();
            try {
                yone = CoordinateSystem.getIndex(coordinates[0]);
                xone = Integer.parseInt(coordinates[1]);
                ytwo = CoordinateSystem.getIndex(coordinates[2]);
                xtwo = Integer.parseInt(coordinates[3]);
                horizontalDifference = Math.abs(xone - xtwo) + 1;
                verticalDifference = Math.abs(yone - ytwo) + 1;
            } catch (Exception e){
                System.out.println("Mistake! Wrong coordinate format or missing coordinates. Try again");
                continue;
            }
            if (xone > xtwo){
                int tmp = xone;
                xone = xtwo;
                xtwo = tmp;
            }
            if (yone > ytwo){
                int tmp = yone;
                yone = ytwo;
                ytwo = tmp;
            }
            if (yone == ytwo) {
                if (horizontalDifference != ship.getSize()) {
                    System.out.printf("Error! Wrong length of the %s! Try again:%n", ship.getName());
                } else if (gameboardOccupiedHorizontal(gameboard,xone, xtwo, yone)) {
                    System.out.println("Error! Wrong ship location! Try again:");
                } else {
                    uncheckedCoordinates = false;
                    shipOrientation = "horizontal";

                }
            } else if (xone == xtwo) {
                if (verticalDifference != ship.getSize()) {
                    System.out.printf("Error! Wrong length of the %s! Try again:%n", ship.getName());
                } else if (gameboardOccupiedVertical(gameboard,yone, ytwo, xone)) {
                    System.out.println("Error! Wrong ship location! Try again:");
                } else {
                    uncheckedCoordinates = false;
                    shipOrientation = "vertical";
                }
            } else {
                System.out.println("Error! Wrong ship coordinates! Try again:");
            }
        } while (uncheckedCoordinates);
        if (activePlayer == 1) {
            switch (shipOrientation) {
                case "horizontal":
                    tagFieldsHorizontal(gameboard,xone, xtwo, yone);
                    addHorizontalShipPlayerOne(curShip, yone, xone, xtwo);
                    break;
                case "vertical":
                    tagFieldsVertical(gameboard,yone, ytwo, xone);
                    addVerticalShipPlayerOne(curShip, xone, yone, ytwo);
                    break;
            }
        } else if (activePlayer == 2) {
            switch (shipOrientation) {
                case "horizontal":
                    tagFieldsHorizontal(gameboard,xone, xtwo, yone);
                    addHorizontalShipPlayerTwo(curShip, yone, xone, xtwo);
                    break;
                case "vertical":
                    tagFieldsVertical(gameboard,yone, ytwo, xone);
                    addVerticalShipPlayerTwo(curShip, xone, yone, ytwo);
                    break;
            }
        }
    }

    public static String[] getCoordinates() {
        String inputString = sc.nextLine();
        String[] input = inputString.split(" ");
        String xInput = input[0];
        if (xInput.equals("exit")) {
            System.exit(0);
        }
        String yInput = input[1];

        String[] coordinates = new String[]{xInput.substring(0, 1),xInput.substring(1, xInput.length()),yInput.substring(0, 1),yInput.substring(1, yInput.length())};

        return coordinates;
    }

    public static boolean gameboardOccupiedHorizontal(String[][] gameboard,int movableCoo, int endCoo, int fixCoo) {
        for (int i = movableCoo; i <= endCoo; i++) {
            if (checkEnvironment(gameboard,fixCoo, i)){
                return true;
            }
        }
        return false;
    }
    public static boolean gameboardOccupiedVertical(String[][] gameboard,int movableCoo, int endCoo, int fixCoo) {
        for (int i = movableCoo; i <= endCoo; i++) {
            if (checkEnvironment(gameboard,i,fixCoo)){
                return true;
            }
        }
        return false;
    }

    public static  boolean checkEnvironment(String[][] gameboard,int y, int x) {
        try {
            if (gameboard[y][x] == "O"){
                return true;
            } else if (gameboard[y][x+1] == "O" ) {
                return true;
            } else if (gameboard[y][x-1] == "O") {
                return true;
            } else if (gameboard[y+1][x] == "O") {
                return true;
            } else if (gameboard[y-1][x] == "O"){
                return true;
            }
        } catch (ArrayIndexOutOfBoundsException e){

        }
        return false;
    }
    public static void tagFieldsHorizontal(String[][] gameboard,int movableCoo, int endCoo, int fixCoo) {
        for (int j = 0; j < endCoo; j++) {
            for (int k = movableCoo; k <= endCoo; k++) {
                gameboard[fixCoo][k] = "O";
            }
        }
    }
    public static void tagFieldsVertical(String[][] gameboard, int movableCoo, int endCoo, int fixCoo) {
        for (int j = 0; j < endCoo; j++) {
            for (int k = movableCoo; k <= endCoo; k++) {
                gameboard[k][fixCoo] = "O";
            }
        }
    }

    public static void addHorizontalShipPlayerOne(int curShip, int yone, int xone, int xtwo) {
        hitShipsPlayerOne.add(new ArrayList<ArrayList<Integer>>(Ships.getShipSize(curShip)));
        int shipLength = xone;
        for (int j = 0; j < Ships.getShipSize(curShip); j++) {
            hitShipsPlayerOne.get(curShip).add(new ArrayList<Integer>(2));
            hitShipsPlayerOne.get(curShip).get(j).add(yone);
            hitShipsPlayerOne.get(curShip).get(j).add(shipLength);
            shipLength++;

        }
    }

    public static void addVerticalShipPlayerOne(int curShip, int xone, int yone, int ytwo) {
        hitShipsPlayerOne.add(new ArrayList<ArrayList<Integer>>(Ships.getShipSize(curShip)));
        int shipLength = yone;
        for (int j = 0; j < Ships.getShipSize(curShip); j++) {
            hitShipsPlayerOne.get(curShip).add(new ArrayList<Integer>(2));
            hitShipsPlayerOne.get(curShip).get(j).add(shipLength);
            hitShipsPlayerOne.get(curShip).get(j).add(xone);
            shipLength++;

        }
    }
    public static void addHorizontalShipPlayerTwo(int curShip, int yone, int xone, int xtwo) {
        hitShipsPlayerTwo.add(new ArrayList<ArrayList<Integer>>(Ships.getShipSize(curShip)));
        int shipLength = xone;
        for (int j = 0; j < Ships.getShipSize(curShip); j++) {
            hitShipsPlayerTwo.get(curShip).add(new ArrayList<Integer>(2));
            hitShipsPlayerTwo.get(curShip).get(j).add(yone);
            hitShipsPlayerTwo.get(curShip).get(j).add(shipLength);
            shipLength++;

        }
    }

    public static void addVerticalShipPlayerTwo(int curShip, int xone, int yone, int ytwo) {
        hitShipsPlayerTwo.add(new ArrayList<ArrayList<Integer>>(Ships.getShipSize(curShip)));
        int shipLength = yone;
        for (int j = 0; j < Ships.getShipSize(curShip); j++) {
            hitShipsPlayerTwo.get(curShip).add(new ArrayList<Integer>(2));
            hitShipsPlayerTwo.get(curShip).get(j).add(shipLength);
            hitShipsPlayerTwo.get(curShip).get(j).add(xone);
            shipLength++;

        }
    }
    //____________________________________________________________________________________

    // ----- PLAY GAME METHODS ------//
    public static void  takeShot(String[][] gameboard, String [][] fogOfWar,String[][] opponentGB, String [][] opponentFoW ) {
        printViews(gameboard, opponentFoW);
        String resultMsg = "";
        boolean shotTaken = false;


        int xCoo = -1;
        int yCoo = -1;
        do {
            System.out.println("Take a shot!");
            String shotInput = sc.nextLine();
            try {
                yCoo = CoordinateSystem.getIndex(shotInput.substring(0, 1));
                xCoo = Integer.parseInt(shotInput.substring(1, shotInput.length()));
            } catch (Exception e) {
                System.out.println("Error! You entered the wrong coordinates!%n Try again:");

            }
            try {
                if (opponentGB[yCoo][xCoo] == "~") {
                    resultMsg = "You missed!";
                    opponentGB[yCoo][xCoo] = "M";
                    opponentFoW[yCoo][xCoo] = "M";
                    shotTaken = true;
                } else if (opponentGB[yCoo][xCoo] == "O") {
                    resultMsg = "You hit a ship!";
                    opponentGB[yCoo][xCoo] = "X";
                    opponentFoW[yCoo][xCoo] = "X";
                    shotTaken = true;
                    switch (activePlayer) {
                        case 1:
                            removeShipsPlayerTwo(yCoo, xCoo);
                            resultMsg = shipSankPlayerTwo();
                            break;
                        case 2:
                            removeShipsPlayerOne(yCoo, xCoo);
                            resultMsg = shipSankPlayerOne();
                            break;
                    }
                } else if (opponentGB[yCoo][xCoo] == "X") {
                    shotTaken = true;
                    resultMsg = "You hit a ship!";
                }
            } catch (Exception e) {
                System.out.println("\\s Error! You entered the wrong coordinates!%n Try again:%n");
            }

        } while (!shotTaken);

        System.out.println();
        System.out.println(resultMsg);
        switchPlayer();
    }

    public static boolean removeShipsPlayerOne(int yCoo, int xCoo) {
        for(int i = 0; i < hitShipsPlayerOne.size(); i++) {
            for (int j = 0; j < hitShipsPlayerOne.get(i).size(); j++) {
                if (hitShipsPlayerOne.get(i).get(j).get(0) == yCoo && hitShipsPlayerOne.get(i).get(j).get(1) == xCoo) {
                    hitShipsPlayerOne.get(i).remove(j);
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean removeShipsPlayerTwo(int yCoo, int xCoo) {
        for(int i = 0; i < hitShipsPlayerTwo.size(); i++) {
            for (int j = 0; j < hitShipsPlayerTwo.get(i).size(); j++) {
                if (hitShipsPlayerTwo.get(i).get(j).get(0) == yCoo && hitShipsPlayerTwo.get(i).get(j).get(1) == xCoo) {
                    hitShipsPlayerTwo.get(i).remove(j);
                    return true;
                }
            }
        }
        return false;
    }
    public static String shipSankPlayerOne() {
        for(int a = 0; a < hitShipsPlayerOne.size(); a++) {
            if(hitShipsPlayerOne.get(a).isEmpty()){
                Ships.setSank(a);
                hitShipsPlayerOne.remove(a);
                if (hitShipsPlayerOne.isEmpty()) {
                    return "You sank the last ship. You won. Congratulations!";
                }
                return "You sank a ship! Specify a new target:";
            }

        }
        return "You hit a ship!";
    }
    public static String shipSankPlayerTwo() {
        for(int a = 0; a < hitShipsPlayerTwo.size(); a++) {
            if(hitShipsPlayerTwo.get(a).isEmpty()){
                Ships.setSank(a);
                hitShipsPlayerTwo.remove(a);
                if (hitShipsPlayerTwo.isEmpty()) {
                    return "You sank the last ship. You won. Congratulations!";
                }
                return "You sank a ship! Specify a new target:";
            }

        }
        return "You hit a ship! %n";
    }

    public static void  switchPlayer() {
        System.out.println("Press Enter and pass the move to another player");
        System.out.println("...");
        if (sc.nextLine().isEmpty()){
            if (activePlayer == 1) {
                activePlayer = 2;
            } else if (activePlayer == 2) {
                activePlayer = 1;
            }

            clearScreen();
        }
    }

    public static void clearScreen() {
        for (int i = 0; i< 30; i++) {
            System.out.println("\n");
        }
    }
    //____________________________________________________________________________________

    // ----- GENERAL METHODS (Prepare & Play) ------//
    public static String[][] createBoard() {
        String[][] board = new String[11][11];
        for (int i  =  0; i <= 10; i++) {
            for (int j = 0; j <= 10; j++) {
                if (i == 0 && j == 0) {
                    board[i][j] = " ";
                } else if (i == 0 && j>0) {
                    board[i][j] = String.valueOf(j);
                } else {
                    if (j == 0) {
                        board[i][j] = CoordinateSystem.getConst(i);
                    } else {
                        board[i][j] = "~";
                    }
                }
            }
        }
        return board;
    }
    public static void printBoard(String[][] gameboard) {
        for (int i = 0; i < gameboard.length; i++) {
            for (int j = 0; j < gameboard[i].length; j++) {
                System.out.print(gameboard[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void printViews(String[][] gameboard, String [][] fogOfWar) {
        printBoard(fogOfWar);
        System.out.println("---------------------");
        printBoard(gameboard);
        System.out.printf("Player %d, it's your turn:%n", activePlayer);
    }
    //____________________________________________________________________________________

    // ----- DEBUG METHOD ------//
    public static void printHitShips(ArrayList<ArrayList<ArrayList<Integer>>> input) {
        for(int i = 0; i < input.size(); i++) {
            for (int j = 0; j < input.get(i).size(); j++) {
                System.out.print("[");
                for (int k = 0; k < input.get(i).get(j).size();k++) {
                    System.out.print(input.get(i).get(j).get(k));
                }
                System.out.print("]");
            }
            System.out.println();
        }
    }
}
