package main.user;

import main.admin.Settings;
import main.database.DatabaseController;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Controller for TeamManagementView
 */
public class TeamManagementController {

    PlayersController playersController;
    RoasterController roasterController;

    MatchupController matchupController;

    DraftController draftController;

    Settings settings;

    TeamInfo myTeam;

    private static final int MAX_ROASTER_SPOT = 14;

    /**
     * @param teamID *
     */
    public TeamManagementController(int teamID){


        settings = DatabaseController.getSettings();

        myTeam = DatabaseController.getTeam(teamID);

        if(shouldUpdate()){
            update();
        }

        ArrayList<PlayerInfo> allPlayers = DatabaseController.getPlayers();

        ArrayList<PlayerInfo> teamPlayers = new ArrayList<>();
        for (PlayerInfo player: allPlayers) {
            if(player.teamID == teamID){
                teamPlayers.add(player);
                if(teamPlayers.size() == MAX_ROASTER_SPOT){
                    break;
                }
            }
        }

        if(teamPlayers.size() == 0){
            for(int i = 1; i <= MAX_ROASTER_SPOT; ++i){
                teamPlayers.add(new PlayerInfo(0,"Empty",teamID,"","",  i,0));
            }
        }else if(teamPlayers.size() < 14){
            for (int i = 0; i < MAX_ROASTER_SPOT; ++i) {

                if(i == teamPlayers.size() || teamPlayers.get(i).roasterSpot != i+1) {
                    teamPlayers.add(i, new PlayerInfo(0, "Empty", teamID, "", "", i + 1,0));
                }
            }
        }

        playersController = new PlayersController(teamID, settings, allPlayers);
        roasterController = new RoasterController(teamID, teamPlayers);
        matchupController = new MatchupController(teamID, settings);
        draftController = new DraftController(myTeam, settings, allPlayers);



    }

    /**
     * @return true if data should be updated
     */
    private boolean shouldUpdate(){
        return settings.state.equals("season") && settings.nextUpdate.compareTo(settings.currentDate) <= 0;
    }

    /**
     * Performs update
     */
    private void update(){

        ArrayList<RealMatch> matches = DatabaseController.getMatchesToUpdate(settings.lastUpdate, settings.currentDate);
        DatabaseController.updateStats(matches);
        DatabaseController.updateMatchResults(settings.lastUpdate, settings.currentDate);

        settings.lastUpdate = settings.currentDate;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(settings.currentDate);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int difference = (Calendar.WEDNESDAY - dayOfWeek + 7) % 7;
        calendar.add(Calendar.DAY_OF_YEAR, difference);
        Date nextWednesday = calendar.getTime();

        Date nextMatch = DatabaseController.getNearestMatchDate(settings.currentDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        settings.nextUpdate = nextMatch.compareTo(nextWednesday) <= 0 ? nextMatch : nextWednesday;
        DatabaseController.setUpdateDates(formatter.format(settings.lastUpdate), formatter.format(settings.nextUpdate));


    }




}
