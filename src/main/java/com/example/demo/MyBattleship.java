package com.example.demo;

import java.util.ArrayList;

/**
 * my map
 *      # ship
 *      ~ opponent hit
 *      @ destroyed ship
 *      . water
 *
 * opponent map
 *      ? unknown field
 *      # destroyed ship
 *      . water
 *
 * every map consists 10 ships
 *      4 box - 1
 *      3 box - 2
 *      2 box - 3
 *      1 box - 4
 */
public class MyBattleship implements Battleship{
    final int mapSize = 10;

    char[][] myMap;
    char[][] opponentMap;

    int myHitShip;
    int opponentHitShip;

    MyBattleship(BattleshipGenerator battleshipGenerator){
        myMap = convertTo2DCharArray(battleshipGenerator.generateMyMap());
        opponentMap = convertTo2DCharArray(battleshipGenerator.generateOpponentMap());
        myHitShip = 0;
        opponentHitShip = 0;
    }

    private char[][] convertTo2DCharArray(String input) {
        char[][] output = new char[mapSize][mapSize];
        for(int i = 0; i < mapSize; ++i){
            for(int j = 0; j < mapSize; ++j){
                output[i][j] = input.charAt((10 * i) + j);
            }
        }
        return output;
    }

    @Override
    public String attackMe(String coordinate){
        int row = 'A' - coordinate.charAt(0);
        int column = '1' - coordinate.charAt(1);

        if(myMap[row][column] == '.'){// miss ship
            myMap[row][column] = '~';
            return "pudło";
        }

        if(myMap[row][column] == '#' || myMap[row][column] == '@'){// hit or destroy ship
            myMap[row][column] = '@';

            if(!MyShipIsDestroyed(row, column))
                return "trafiony zatopiony";
            else
                return "trafiony";
        }

        if(lose()) return "ostatni zatopiony";

        return "error";
    }

    private boolean lose(){
        for(int i = 0; i < mapSize; ++i){
            for(int j = 0; j < mapSize; ++j){
                if(myMap[i][j] == '#') return false;
            }
        }
        return true;
    }

    @Override
    public void attackOpponent(String coordinate, String command){
        int row = 'A' - coordinate.charAt(0);
        int column = '1' - coordinate.charAt(1);

        if(command.equals("pudło"))
            opponentMap[row][column] = '.';

        if(command.equals("trafiony")){
            opponentMap[row][column] = '#';
        }

        if(command.equals("trafiony zatopiony")){// unlock box around hit ship
            ArrayList<int[]> shipCoordinate = getShipCoordinate(opponentMap, row, column);
            for(int[] coor : shipCoordinate){
                for (int i = coor[0] - 1; i <= coor[0] + 1; i++) {
                    for (int j = coor[1] - 1; j <= coor[1] + 1; j++) {
                        if (i >= 0 && i < opponentMap.length && j >= 0 && j < opponentMap[i].length) {
                            if(opponentMap[i][j] == '?') opponentMap[i][j] = '.';
                        }
                    }
                }
            }
        }
    }

    private boolean MyShipIsDestroyed(int row, int column){
        ArrayList<int[]> myShipCoordinate = getShipCoordinate(myMap, row, column);

        for(int[] coordinate : myShipCoordinate){
            if( myMap[coordinate[0]][coordinate[1]] == '#' )
                return false;
        }
        return true;
    }

    private ArrayList<int[]> getShipCoordinate(char[][] map, int row, int column) {
        ArrayList<int[]> shipCoordinates = new ArrayList<>();

        dfs(map, row, column, shipCoordinates);// looking for ship coordinates

        return shipCoordinates;
    }

    private void dfs(char[][] map,int row, int column, ArrayList<int[]> shipCoordinates) {
        // check if ship exists at coordinate
        if (row >= 0 && row < map.length && column >= 0 && column < map[row].length &&
                (map[row][column] == '#' || map[row][column] == '@')) {

            shipCoordinates.add(new int[]{row, column});// add new coordinate

            char symbol = map[row][column];// save symbol
            map[row][column] = '.';

            dfs(map, row - 1, column - 1, shipCoordinates);// check up
            dfs(map, row - 1, column, shipCoordinates);
            dfs(map, row - 1, column + 1, shipCoordinates);

            dfs(map, row, column - 1, shipCoordinates);// check at
            dfs(map, row, column + 1, shipCoordinates);

            dfs(map, row + 1, column - 1, shipCoordinates);// check under
            dfs(map, row + 1, column, shipCoordinates);
            dfs(map, row + 1, column + 1, shipCoordinates);

            map[row][column] = symbol;// return symbol
        }
    }
}