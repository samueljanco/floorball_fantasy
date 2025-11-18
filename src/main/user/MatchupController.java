package main.user;

import main.admin.Settings;
import main.database.DatabaseController;
import java.util.ArrayList;

/**
 * Contorller for Matchups
 */
public class MatchupController {

    private final ArrayList<MatchupPlayerInfo> myPlayers;
    private final ArrayList<MatchupPlayerInfo> opponentsPlayers;

    private final TeamInfo myTeam;

    private final TeamInfo opponentsTeam;

    public MatchupController(int myTeamID, Settings settings){
        myTeam = DatabaseController.getTeam(myTeamID);
        int opponentsTeamID = DatabaseController.getOpponentID(myTeamID, settings.currentDate);
        opponentsTeam = DatabaseController.getTeam(opponentsTeamID);
        myPlayers = DatabaseController.getMatchupPlayers(myTeamID, settings.currentDate);
        opponentsPlayers = DatabaseController.getMatchupPlayers(opponentsTeamID, settings.currentDate);
    }

    /**
     * @param isMyTeam  bool
     * @return ArrayList of playes to be displays in matchup view
     */
    public ArrayList<MatchupPlayerInfo> getPlayers(boolean isMyTeam){
        return isMyTeam ? myPlayers : opponentsPlayers;
    }


    /**
     * @param isMyTeam bool
     * @return score of the team in current match.
     */
    public int getScore(boolean isMyTeam){
        int score = 0;
        if(isMyTeam){
            for (MatchupPlayerInfo player : myPlayers) {
                score += player.pointsSum;
            }
        }else{
            for (MatchupPlayerInfo player : opponentsPlayers) {
                score += player.pointsSum;
            }
        }
        return score;
    }


    /**
     * @param isMyTeam bool
     * @return name of the team
     */
    public String getTeamsName(boolean isMyTeam){
        return isMyTeam ? myTeam.name : opponentsTeam.name;
    }

    /**
     * @param isMyTeam bool
     * @return record of the team
     */
    public String getRecord(boolean isMyTeam){
        return isMyTeam ? myTeam.getRecord() : opponentsTeam.getRecord();
    }

}
