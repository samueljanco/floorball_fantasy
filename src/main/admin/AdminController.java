package main.admin;

import main.database.DatabaseController;
import main.user.MatchupPair;
import main.user.TeamInfo;

import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

/**
 * Contorller for admin
 */
public class AdminController {

    private static final int ROUNDS = 22;

    Settings settings;
    public ArrayList<TeamInfo> teams;

    public AdminController(){
        reloadSettings();
        teams = DatabaseController.getTeams();
    }


    /**
     * @return Identifications of teams
     */
    private ArrayList<Integer> getTeamIDs(){
        ArrayList<Integer> teamsIDs = new ArrayList<>();
        for (TeamInfo t: teams) {
            teamsIDs.add(t.ID);
        }
        return teamsIDs;
    }


    /**
     * Crates pairing for league matches for the entire season.
     *
     * @return 2D ArrayList containing match pairings
     */
    public ArrayList<ArrayList<MatchupPair>> cratePairing(){
        ArrayList<ArrayList<MatchupPair>> pairs = new ArrayList<>();


        while(pairs.size() < ROUNDS) {

            ArrayList<Integer> teamsIDs = getTeamIDs();

            for (int i = 0; i < teams.size() - 1; ++i) {
                ArrayList<MatchupPair> roundMatches = new ArrayList<>();

                for (int j = 0; j < teams.size()/2; j++) {
                    roundMatches.add(new MatchupPair(teamsIDs.get(j), teamsIDs.get(teams.size() - 1 - j)));
                }

                Collections.rotate(teamsIDs.subList(1, teams.size()), 1);
                pairs.add(roundMatches);
                if (pairs.size() == ROUNDS){
                    break;
                }
            }
        }

        return pairs;
    }

    /**
     * Based on the parings provided by createPairing() schedules all matches.
     * Every match starts on Wednesday anh has duration of one week.
     * Assign dates to match pairs.
     * Stores the result into database.
     */
    public void createSchedule(){
        ArrayList<ArrayList<MatchupPair>> pairs = cratePairing();


        LocalDate matchStart = Instant.ofEpochMilli(settings.seasonStart.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate seasonEnd = Instant.ofEpochMilli(settings.seasonEnd.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate matchEnd = matchStart.plusDays(6);

        int i = 0;
        while(matchStart.isBefore(seasonEnd)){
            Date from = Date.from(matchStart.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date to = Date.from(matchEnd.atStartOfDay(ZoneId.systemDefault()).toInstant());
            if(DatabaseController.realMatchExists(from, to)){
                DatabaseController.createMatches(pairs.get(i++), from, to);
            }
            matchStart = matchStart.plusDays(7);
            matchEnd = matchEnd.plusDays(7);
        }
    }

    /**
     * @param name Team name
     * @return boolean indicating whether the addition was successful
     */
    public boolean addTeam(String name){
        if(name.length() >= 5){
            DatabaseController.addTeam(name);
            int teamID = DatabaseController.getTeamIDByName(name);
            teams.add(new TeamInfo(teamID, name,0,0,0,0));
            return true;
        }
        return false;
    }

    /**
     * Removes team.
     * @param team Team name
     */
    public void removeTeam(TeamInfo team){
        DatabaseController.removeTeam(team.ID);
        teams.remove(team);
    }

    /**
     * Changes league state.
     * @param state New state
     */
    public void changeState(String state){
        settings.state = state;
        DatabaseController.changeState(state);
    }

    /**
     * Randomly assigns order of draft picks.
     */
    public void chooseDraftOrder(){
        ArrayList<Integer> teamIDs = getTeamIDs();
        Random rand = new Random();
        int teamCount = teamIDs.size();
        for (int i = 1; i <= teamCount; ++i) {
            int randomIdx = rand.nextInt(teamIDs.size());
            teams.stream().filter((team) -> team.ID == teamIDs.get(randomIdx)).findFirst().orElse(teams.get(0)).draftOrder = i;
            teamIDs.remove(randomIdx);
        }
        DatabaseController.setDraftOrder(teams);
    }

    /**
     * @return ArrayList of teams sorted by draft order.
     */
    public ArrayList<TeamInfo> getLottery(){
        teams.sort(new LotteryPickComparator());
        return teams;
    }

    /**
     * Draft order comparator
     */
    private static class LotteryPickComparator implements Comparator<TeamInfo> {
        @Override
        public int compare(TeamInfo x, TeamInfo y) {
            return x.draftOrder.compareTo(y.draftOrder);
        }
    }

    /**
     * Reloads the setting from database.
     */
    public void reloadSettings(){
        settings = DatabaseController.getSettings();
    }

    /**
     * @return boolean indicating whether the season has started
     * Season starts when draft ends.
     */
    public boolean hasSeasonStarted(){
        return settings.state.equals("season");
    }

}
