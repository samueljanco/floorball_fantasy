package main.database;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import main.admin.Settings;
import main.user.*;


/**
 * Handles database writes and reads
 */
public class DatabaseController {

    private static final String ADMIN_NAME = "Admin";

    /**
     * Loads team from database.
     * @param teamID Identification of team.
     * @return team
     */
    public static TeamInfo getTeam(int teamID){
        TeamInfo team = new TeamInfo();
        try{
            Statement statement = DBConnection.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Teams WHERE ID="+teamID);

            if (resultSet.next()){
                int ID = resultSet.getInt("ID");
                String name = resultSet.getString("Name");
                int wins = resultSet.getInt("Wins");
                int draws = resultSet.getInt("Draws");
                int losses = resultSet.getInt("Losses");
                int draftOrder = resultSet.getInt("DraftOrder");
                team = new TeamInfo(ID,name,wins,draws,losses,draftOrder);

            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        return team;
    }

    /**
     * Loads all teams form database.
     * @return ArrayList of teams.
     */
    public static ArrayList<TeamInfo> getTeams(){
        ArrayList<TeamInfo> teams = new ArrayList<>();
        try{

            Statement statement = DBConnection.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Teams ORDER BY Wins DESC, Draws DESC");

            while (resultSet.next()){
                int ID = resultSet.getInt("ID");
                String name = resultSet.getString("Name");
                int wins = resultSet.getInt("Wins");
                int draws = resultSet.getInt("Draws");
                int losses = resultSet.getInt("Losses");
                int draftOrder = resultSet.getInt("DraftOrder");
                teams.add(new TeamInfo(ID,name,wins,draws,losses,draftOrder));

            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        return teams;
    }

    /**
     * @param teamID Identification of team.
     * @param currentDate Current date.
     * @return Opponent for the team on the date.
     */
    public static int getOpponentID(int teamID, Date currentDate){
        try{
            //Connection connection = DriverManager.getConnection(DBConnection.url, DBConnection.username, DBConnection.password);
            Statement statement = DBConnection.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Teams.ID FROM Teams " +
                    "JOIN Matches ON (Matches.Home = Teams.ID OR Matches.Away = Teams.ID)" +
                    "WHERE (Matches.StartDate <= '"+currentDate+"' AND Matches.EndDate >= '"+currentDate+"' AND Teams.ID !="+teamID+")");
            if(resultSet.next()){
                return resultSet.getInt("ID");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param teamID Identification of team.
     * @param currentDate Current date.
     * @return ArrayList of players that participate for the team on the date.
     */
    public static ArrayList<MatchupPlayerInfo> getMatchupPlayers(int teamID, Date currentDate){
        ArrayList<MatchupPlayerInfo> players = new ArrayList<>();
        try{
            //Connection connection = DriverManager.getConnection(DBConnection.url, DBConnection.username, DBConnection.password);
            Statement statement = DBConnection.connection.createStatement();


            ResultSet resultSet = statement.executeQuery("SELECT Players.ID, Players.Name, Players.Position, Players.RealTeam, Players.RoasterSpot, SUM(PlayerStatLines.FantasyScore) AS PointsSum " +
                    "FROM PlayerStatLines JOIN Players ON PlayerStatLines.PlayerID = Players.ID " +
                    "JOIN Matches ON PlayerStatLines.MatchID = Matches.ID WHERE PlayerStatLines.TeamID="+teamID+" AND Matches.StartDate <= '"+currentDate+"' AND Matches.EndDate >= '"+currentDate+"'  GROUP BY Players.ID");

            while (resultSet.next()){
                int ID = resultSet.getInt("ID");
                String name = resultSet.getString("Name");
                String position = resultSet.getString("Position");
                String realTeam = resultSet.getString("RealTeam");
                int roasterSpot = resultSet.getInt("RoasterSpot");
                int pointSum = resultSet.getInt("PointsSum");
                players.add(new MatchupPlayerInfo(ID,name,teamID,position,realTeam,roasterSpot, pointSum));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return players;
    }

    /**
     * @param from Date
     * @param to Date
     * @return Return Real matches between form and to that should be updated.
     */
    public static ArrayList<RealMatch> getMatchesToUpdate(Date from, Date to){
        ArrayList<RealMatch> matches = new ArrayList<>();
        try{
           // Connection connection = DriverManager.getConnection(DBConnection.url, DBConnection.username, DBConnection.password);
            Statement statement = DBConnection.connection.createStatement();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");



            ResultSet resultSet = statement.executeQuery("SELECT * FROM RealMatches WHERE Date>='"+formatter.format(from)+"' AND Date<'"+formatter.format(to)+"'");

            while (resultSet.next()){

                String url = resultSet.getString("URL");
                Date date = resultSet.getDate("Date");

                matches.add(new RealMatch(url, date));

            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return matches;

    }


    /**
     * Updates statistics from matches.
     * @param matches  ArrayList of real matches.
     */
    public static void updateStats(ArrayList<RealMatch> matches){

        try{
          //  Connection connection = DriverManager.getConnection(DBConnection.url, DBConnection.username, DBConnection.password);
            Statement statement = DBConnection.connection.createStatement();

            for(RealMatch match : matches){

                StatLinePair pair = StatisticLoader.loadMatchData(match.url, match.date, statement);

                for (PlayerStatLine playerStats: pair.playerStatLines) {

                    statement.executeUpdate("INSERT INTO PlayerStatLines (MatchID, TeamID, PlayerID, Goals, Assists, Penalty, FantasyScore) " +
                            "VALUES ("+playerStats.matchID+","+playerStats.teamID+","+playerStats.playerID+","+playerStats.goals+","
                            +playerStats.assists+","+playerStats.penalty+","+playerStats.fantasyScore+")");
                }

                for (GoalieStatLine goalieStats: pair.goalieStatLines) {
                    statement.executeUpdate("INSERT INTO GoaliesStatLines (MatchID, TeamID, PlayerID, Goals, ShotsOnGoal, Penalty, FantasyScore) " +
                            "VALUES ("+goalieStats.matchID+","+goalieStats.teamID+","+goalieStats.playerID+","+goalieStats.goals+","
                            +goalieStats.shotsOnGoal+","+goalieStats.penalty+","+goalieStats.fantasyScore+")");
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Updates final results of the fantasy matches.
     * Updates win/draw/loss counter for teams.
     * @param from Date
     * @param to Date
     */
    public static void updateMatchResults(Date from, Date to){

        try{
        //    Connection connection = DriverManager.getConnection(DBConnection.url, DBConnection.username, DBConnection.password);
            Statement statement = DBConnection.connection.createStatement();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


            String sql = "SELECT m.ID AS MatchID, " +
                    "m.Home AS HomeID, "+
                    "m.Away AS AwayID, "+
                    "SUM(CASE WHEN s.TeamID = m.Home THEN s.FantasyScore ELSE 0 END) AS HomeScore, " +
                    "SUM(CASE WHEN s.TeamID = m.Away THEN s.FantasyScore ELSE 0 END) AS AwayScore " +
                    "FROM Matches m " +
                    "INNER JOIN PlayerStatLines s ON m.ID = s.MatchID " +
                    "WHERE m.EndDate>='"+formatter.format(from)+"' AND m.EndDate<'"+formatter.format(to)+"' "+
                    "GROUP BY m.ID";
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()){
                int homeID = resultSet.getInt("HomeID");
                int homeScore = resultSet.getInt("HomeScore");
                int awayID = resultSet.getInt("AwayID");
                int awayScore = resultSet.getInt("AwayScore");

                if(homeScore > awayScore){
                    DBConnection.connection.createStatement().executeUpdate("UPDATE Teams SET Wins=(Wins + 1) WHERE ID="+homeID);
                    DBConnection.connection.createStatement().executeUpdate("UPDATE Teams SET Losses=(Losses + 1) WHERE ID="+awayID);
                } else if (homeScore < awayScore) {
                    DBConnection.connection.createStatement().executeUpdate("UPDATE Teams SET Wins=(Wins + 1) WHERE ID="+awayID);
                    DBConnection.connection.createStatement().executeUpdate("UPDATE Teams SET Losses=(Losses + 1) WHERE ID="+homeID);
                }else{
                    DBConnection.connection.createStatement().executeUpdate("UPDATE Teams SET Draws=(Draws + 1) WHERE ID="+homeID);
                    DBConnection.connection.createStatement().executeUpdate("UPDATE Teams SET Draws=(Draws + 1) WHERE ID="+awayID);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    /**
     * Loads settings.
     * @return league settings.
     */
    public static Settings getSettings(){
        Settings settings = new Settings();
        try{
            Statement statement = DBConnection.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Settings WHERE ID=1");
            if(resultSet.next()){
                Date currentDate = resultSet.getDate("CurrentDate");
                if(currentDate == null){
                    currentDate = new Date();
                }
                Date seasonStart = resultSet.getDate("SeasonStart");
                Date seasonEnd = resultSet.getDate("SeasonEnd");

                Date lastUpdate = resultSet.getDate("LastUpdate");
                Date nextUpdate = resultSet.getDate("NextUpdate");
                String state = resultSet.getString("DraftState");
                settings = new Settings(currentDate,seasonStart, seasonEnd, lastUpdate, nextUpdate, state);
            }



        }catch (SQLException e){
            e.printStackTrace();
        }
        return settings;
    }

    /**
     * Stores new team to the database.
     * @param name team name
     */
    public static void addTeam(String name)  {
        try{
         //   Connection connection = DriverManager.getConnection(DBConnection.url, DBConnection.username, DBConnection.password);
            Statement statement = DBConnection.connection.createStatement();
            statement.executeUpdate("INSERT INTO Teams (Name, Wins,Draws,Losses,DraftOrder) VALUES ('"+name+"',0,0,0,0)");

        }catch (SQLException e){
            e.printStackTrace();
        }


    }

    /**
     * Removes team form the database.
     * @param teamID Identification of team.
     */
    public static void removeTeam(int teamID){
        try{
            Statement statement = DBConnection.connection.createStatement();
            statement.executeUpdate("DELETE FROM Teams WHERE ID="+teamID);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Sets the draft order for every team.
     * @param teams teams
     */
    public static void setDraftOrder(ArrayList<TeamInfo> teams){
        try{
            Statement statement = DBConnection.connection.createStatement();
            for(TeamInfo team : teams){
                statement.executeUpdate("UPDATE Teams SET DraftOrder ="+team.draftOrder+" WHERE ID="+team.ID);
                if(team.draftOrder == 1){
                    statement.executeUpdate("UPDATE Settings SET NextPick ="+team.ID+", DraftPlace=1 WHERE ID=1");
                }
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    /**
     * @param name team name
     * @return Team with Name == name.
     */
    public static int getTeamIDByName(String name){
        if(name.equals(ADMIN_NAME)) return -1;

        try{
        //    Connection connection = DriverManager.getConnection(DBConnection.url, DBConnection.username, DBConnection.password);
            Statement statement = DBConnection.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT ID FROM Teams WHERE Name='"+name+"'");
            if(resultSet.next()){
                return resultSet.getInt("ID");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public static void changeState(String state){
        try {
            Statement statement = DBConnection.connection.createStatement();
            statement.executeUpdate("UPDATE Settings SET DraftState = '" + state + "' WHERE ID=1");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    /**
     * @return information about draft
     */
    public static DraftInfo getDraftInfo(){
        DraftInfo info = new DraftInfo();
        try{
            Statement statement = DBConnection.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Settings WHERE ID=1");
            if(resultSet.next()){
                info.nextPickID = resultSet.getInt("NextPick");
                info.place = resultSet.getInt("DraftPlace");
                ResultSet teamCount = statement.executeQuery("SELECT COUNT(*) FROM Teams");
                if(teamCount.next()){
                    info.teamCount = teamCount.getInt("COUNT(*)");
                }

            }



        }catch (SQLException e){
            e.printStackTrace();
        }
        return info;
    }

    /**
     * Sets new draft attributes.
     * @param draftOrder Order of the draft.
     * @param draftPlace Draft pick place.
     */
    public static void setNextPick(int draftOrder, int draftPlace){
        try {
            Statement statement = DBConnection.connection.createStatement();
            ResultSet team = statement.executeQuery("SELECT * FROM Teams WHERE DraftOrder="+draftOrder);

            if (team.next()) {
                int nextPickID = team.getInt("ID");
                statement.executeUpdate("UPDATE Settings SET DraftPlace=" + draftPlace + ", NextPick="+nextPickID+" WHERE ID=1");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    /**
     * Ends the draft.
     */
    public static void endTheDraft(){
        try {
         //   Connection connection = DriverManager.getConnection(DBConnection.url, DBConnection.username, DBConnection.password);
            Statement statement = DBConnection.connection.createStatement();
            statement.executeUpdate("UPDATE Settings SET DraftPlace=0 , NextPick=0, DraftState='season' WHERE ID=1");
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    /**
     * Changes players rosters spot.
     * @param playerID Identification of player
     * @param newRoasterSpot Roaster spot
     */
    public static void changeRosterSpot(int playerID, int newRoasterSpot){
        try{
            Statement statement = DBConnection.connection.createStatement();
            statement.executeUpdate("UPDATE Players SET RoasterSpot="+newRoasterSpot+" WHERE ID="+playerID);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Adds player to the database.
     * @param playerID Player
     * @param teamID Team
     * @param roasterSpot roaster spot
     */
    public static void addPlayer(int playerID, int teamID, int roasterSpot){
        try{
            Statement statement = DBConnection.connection.createStatement();
            statement.executeUpdate("UPDATE Players SET TeamID="+teamID+", RoasterSpot="+roasterSpot+"  WHERE ID="+playerID);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Removes player form the database.
     * @param playerID player
     */
    public static void dropPlayer(int playerID){
        try{
            Statement statement = DBConnection.connection.createStatement();
            statement.executeUpdate("UPDATE Players SET TeamID=0 WHERE ID="+playerID);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    /**
     * Loads all players with theirs point per game form the database.
     * @return ArrayList of players
     */
    public static ArrayList<PlayerInfo> getPlayers(){
        ArrayList<PlayerInfo> players = new ArrayList<>();
        try{
            Statement statement = DBConnection.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT p.ID, p.Name, p.TeamID, p.Position, p.RealTeam, p.RoasterSpot, AVG(s.FantasyScore) AS PointsPerGame " +
                            "FROM Players p " +
                            "LEFT JOIN PlayerStatLines s ON p.ID = s.PlayerID " +
                            "WHERE p.Position != 'G' " +
                            "GROUP BY p.ID, p.Name, p.TeamID, p.Position, p.RealTeam, p.RoasterSpot " +
                            "UNION " +
                            "SELECT p.ID, p.Name, p.TeamID, p.Position, p.RealTeam, p.RoasterSpot, AVG(s.FantasyScore) AS PointsPerGame " +
                            "FROM Players p " +
                            "LEFT JOIN GoaliesStatLines s ON p.ID = s.PlayerID " +
                            "WHERE p.Position='G' " +
                            "GROUP BY p.ID, p.Name, p.TeamID, p.Position, p.RealTeam, p.RoasterSpot " +
                            "ORDER BY RoasterSpot"
            );

            while (resultSet.next()){
                int ID = resultSet.getInt("ID");
                String name = resultSet.getString("Name");
                int teamID = resultSet.getInt("TeamID");
                String position = resultSet.getString("Position");
                String realTeam = resultSet.getString("RealTeam");
                int roasterSpot = resultSet.getInt("RoasterSpot");
                int pointsPerGame = resultSet.getInt("PointsPerGame");
                players.add(new PlayerInfo(ID,name,teamID,position,realTeam, roasterSpot, pointsPerGame));
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        return players;
    }


    /**
     * @param from date
     * @return Nearest date on which there is a match played.
     */
    public static Date getNearestMatchDate(Date from){
        Date date = new Date();
        try{
            Statement statement = DBConnection.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Date FROM RealMatches WHERE Date>='"+from+"' LIMIT 1");

            if (resultSet.next()){
               date = resultSet.getDate("Date");
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @param lastUpdate Date of last update.
     * @param nextUpdate Date of next update.
     */
    public static void setUpdateDates(String lastUpdate, String nextUpdate){
        try{
            Statement statement = DBConnection.connection.createStatement();
            statement.executeUpdate("UPDATE Settings SET LastUpdate='"+lastUpdate+"', NextUpdate='"+nextUpdate+"' WHERE ID=1");

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    /**
     * @param from date
     * @param to date
     * @return boolean indicating whether there is a match between from and to.
     */
    public static boolean realMatchExists(Date from, Date to){
        try{
            Statement statement = DBConnection.connection.createStatement();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            ResultSet resultSet = statement.executeQuery("SELECT Date FROM RealMatches WHERE Date>='"+formatter.format(from)+"' AND Date<'"+formatter.format(to)+"' LIMIT 1");

            if (resultSet.next()){
                return true;
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Creates matches for the pairs.
     * @param pairs Match pairs
     * @param from date
     * @param to date
     */
    public static void createMatches(ArrayList<MatchupPair> pairs, Date from, Date to){
        try{
            Statement statement = DBConnection.connection.createStatement();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            for(MatchupPair pair : pairs){
                statement.executeUpdate("INSERT INTO Matches (Home, Away, StartDate, EndDate) VALUES ("+pair.home+","+pair.away+",'"+formatter.format(from)+"', '"+formatter.format(to)+"' )");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    /**
     * @param playerName Player name
     * @param teamName Player team name
     * @param statement Statement
     * @return player with given name and given team.
     */
    public static PlayerInfo getPlayerByName(String playerName, String teamName, Statement statement) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT ID, TeamID FROM Players WHERE Name='" + playerName + "' AND RealTeam='"+teamName+"'");
            if (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int teamID = resultSet.getInt("TeamID");
                return new PlayerInfo(id, teamID);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return new PlayerInfo();

    }


    /**
     * @param teamID Identification of team
     * @param matchDate date
     * @param statement Statement
     * @return Identification of fantasy match played by team.
     */
    public static int getFantasyMatchID(int teamID, Date matchDate, Statement statement) {
        try {

            if(teamID > 0){
                ResultSet resultSet =  statement.executeQuery("SELECT * FROM Matches WHERE " +
                        "(Home='"+teamID+"' OR Away='"+teamID+"')" +
                        " AND StartDate<='"+matchDate+"' AND EndDate>='"+matchDate+"'");
                if(resultSet.next()){
                    return resultSet.getInt("ID");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;

    }


    /**
     * @param num Number of days
     */
    public static void addDays(int num){
        try{
            Statement statement = DBConnection.connection.createStatement();
            statement.executeUpdate("UPDATE Settings SET CurrentDate = DATE_ADD(CurrentDate, INTERVAL "+num+" DAY) WHERE ID=1");

        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    /**
     * Restarts the season by removing all data form database.
     */
    public static void restartSeason(){
        try{
            Statement statement = DBConnection.connection.createStatement();
            statement.executeUpdate("DELETE FROM Players");
            statement.executeUpdate("DELETE FROM Teams");
            statement.executeUpdate("DELETE FROM RealMatches");
            statement.executeUpdate("DELETE FROM Matches");
            statement.executeUpdate("DELETE FROM PlayerStatLines");
            statement.executeUpdate("DELETE FROM GoaliesStatLines");

            statement.executeUpdate("UPDATE Settings SET NextPick=0, DraftPlace=0, DraftState='teams' WHERE ID=1");

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Simulates draft for easier testing.
     * @param teams League teams
     */
    public static void simulateDraft(ArrayList<TeamInfo> teams){
        try{
       //     Connection connection = DriverManager.getConnection(DBConnection.url, DBConnection.username, DBConnection.password);
            Statement statement = DBConnection.connection.createStatement();

            ResultSet goalieResultSet =  statement.executeQuery("SELECT ID FROM Players WHERE Position='G'");
            int goalieSpot = 1;
            int teamNum = 0;
            while (goalieResultSet.next()){
                int id = goalieResultSet.getInt("ID");
                statement = DBConnection.connection.createStatement();
                statement.executeUpdate("UPDATE Players SET TeamID="+teams.get(teamNum).ID+", RoasterSpot="+goalieSpot+" WHERE ID="+id);
                ++teamNum;

                if(teamNum == teams.size()){
                    break;
                }
            }

            ResultSet forwardsResultSet =  statement.executeQuery("SELECT ID FROM Players WHERE Position='F'");
            int[] forwards = new int[]{2,3,4,7,8,9, 12, 13};

            teamNum = 0;
            int posID = 0;
            while (forwardsResultSet.next()){
                int id = forwardsResultSet.getInt("ID");
                statement = DBConnection.connection.createStatement();
                statement.executeUpdate("UPDATE Players SET TeamID="+teams.get(teamNum).ID+", RoasterSpot="+forwards[posID]+" WHERE ID="+id);
                ++posID;
                if(posID == forwards.length){
                    ++teamNum;
                    posID = 0;
                }
                if(teamNum == teams.size()){
                    break;
                }

            }

            ResultSet defendersResultSet =  statement.executeQuery("SELECT ID FROM Players WHERE Position='D'");
            int[] defenders = new int[]{5,6,10,11,14};

            teamNum = 0;
            posID = 0;
            while (defendersResultSet.next()) {
                int id = defendersResultSet.getInt("ID");
                statement = DBConnection.connection.createStatement();
                statement.executeUpdate("UPDATE Players SET TeamID=" + teams.get(teamNum).ID + ", RoasterSpot=" + defenders[posID] + " WHERE ID=" + id);
                ++posID;
                if (posID == defenders.length) {
                    ++teamNum;
                    posID = 0;
                }
                if (teamNum == teams.size()) {
                    break;
                }

            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }








}
