package main.user;

import main.admin.Settings;

import java.util.ArrayList;

/**
 * Controller for PlayersView
 */
public class PlayersController {

    private final int teamID;

    private final ArrayList<PlayerInfo> allPlayers;

    private final Settings settings;

    /**
     * @param teamID *
     * @param settings *
     * @param allPlayers *
     */
    public PlayersController(int teamID, Settings settings, ArrayList<PlayerInfo> allPlayers){
        this.teamID = teamID;
        this.settings = settings;
        this.allPlayers = allPlayers;
    }


    /**
     * @return players
     */
    public ArrayList<PlayerInfo> getPlayers(){
        return allPlayers;
    }

    /**
     * @return team id
     */
    public int getTeamID(){
        return teamID;
    }

    /**
     * @return  boolean indicating if season has started
     */
    public boolean hasSeasonStarted(){
        return settings.state.equals("season");
    }

    /**
     * @param query 8
     * @return players matching search query
     */
    public ArrayList<PlayerInfo> getSearchResult(String query){
        if(query.isEmpty()){
            return allPlayers;
        }else {
            ArrayList<PlayerInfo> result = new ArrayList<>();
            for (PlayerInfo player : allPlayers) {
                if (player.name.contains(query)) {
                    result.add(player);
                }
            }
            return result;
        }
    }
}
