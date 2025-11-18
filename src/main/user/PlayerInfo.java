package main.user;

import java.util.Arrays;
import java.util.List;

/**
 * Player data
 */
public class PlayerInfo {
    public int ID;
    public String name;
    public int teamID;
    public Position position;
    public String realTeam;

    public int roasterSpot;

    public int pointsPerGame;

    /**
     * Constructor
     */
    public PlayerInfo(){
        this.ID = 0;
    }

    /**
     * @param ID Identification
     * @param teamID Identification od team.
     */
    public PlayerInfo(int ID, int teamID){
        this.ID = ID;
        this.teamID = teamID;
    }

    /**
     * @param ID *
     * @param name *
     * @param teamID *
     * @param position *
     * @param realTeam *
     * @param roasterSpot *
     * @param pointsPerGame *
      */
    public PlayerInfo(int ID, String name, int teamID, String position, String realTeam, int roasterSpot, int pointsPerGame){
        this.ID = ID;
        this.name = name;
        this.teamID = teamID;
        stringToPosition(position);
        this.realTeam = realTeam;
        this.roasterSpot = roasterSpot;
        this.pointsPerGame = pointsPerGame;

    }

    /**
     * @param str positon string
     */
    private void stringToPosition(String str){
        switch (str) {
            case "G" -> position = Position.G;
            case "F" -> position = Position.F;
            case "D" -> position = Position.D;
            default -> position = Position.Empty;
        }
    }

    /**
     * Drops the player
     */
    public void drop(){
        teamID = 0;
        roasterSpot = 0;
    }


    /**
     * @return position title to be displayed in GUI
     */
    public String getPositionTitle(){
        return switch (position) {
            case F -> "Forward";
            case D -> "Defender";
            case G -> "Goalie";
            default -> "";
        };
    }

    /**
     * @return points per game
     */
    public double getAverageScore(){
        return pointsPerGame;
    }

    /**
     * @param roasterSpot spot on roaster
     * @return label for the spot
     */
    public static String getRoasterLabel(int roasterSpot){
        List<Integer> forwards = Arrays.asList(2,3,4,7,8,9);
        List<Integer> defenders = Arrays.asList(5,6,10,11);
        if(roasterSpot == 1){
            return "G";
        } else if(forwards.contains(roasterSpot)){
            return "F";
        }else if(defenders.contains(roasterSpot)){
            return "D";
        }
        return "BN";
    }


    /**
     * @return string
     */
    @Override
    public String toString() {
        return name+" "+position+" "+realTeam;
    }
}
