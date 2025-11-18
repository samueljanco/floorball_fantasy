package main.database;

import main.user.GoalieStatLine;
import main.user.PlayerInfo;
import main.user.PlayerStatLine;
import main.user.StatLinePair;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Handles statistic loading
 */
public class StatisticLoader {

    private static final int MAX_MATCH_ID = 132;

    private static final int PAGES = 11;

    private static final String BASE_URL = "https://www.szfb.sk";

    private static final String STATS_URL = "/Stats#matchDetailTabs";

    private static final String PLAYERS_URL = "https://www.szfb.sk/sk/stats/players/928/fortuna-florbalova-extraliga?statsType=points&page=";

    private static final String MATCHES_URL = "https://www.szfb.sk/sk/stats/results/928/fortuna-florbalova-extraliga";

    private static final int GOAL_POINTS = 30;
    private static final int ASSIST_POINTS = 20;
    private static final int PENALTY_POINTS = 5;
    private static final int BLOCKED_SHOT_POINTS = 2;

    /**
     * Load all player form the website
     */
    public static void loadPlayers(){

        try {
            Statement statement = DBConnection.connection.createStatement();

            for (int i = 1; i <= PAGES; ++i) {
                loadPlayersPage(PLAYERS_URL + i, statement);
            }
        }catch (SQLException | IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Loads one page of players form the website.
     * @param url url
     * @param statement Statement
     * @throws IOException .
     * @throws SQLException .
     */
    private static void loadPlayersPage(String url, Statement statement) throws IOException, SQLException {
        String content = getWebsiteContent(url);
        Document doc = Jsoup.parse(content);

        for (Element table : doc.select("table")) {

            if (table.attributes().get("class").contains("table-hover")){

                for (Element body : table.select("tbody")) {

                    for (Element row : body.select("tr")) {

                        Elements tds = row.select("td");

                        try{
                            String playerName = anonymizeName(tds.get(1).text());
                            String team = tds.get(3).text();
                            String position = tds.get(4).text();

                            statement.executeUpdate("INSERT INTO Players (TeamID, Name, RealTeam, Position) VALUES (0, '"+playerName+"', '"+team+"', '"+position+"')");


                        }catch (IndexOutOfBoundsException e){
                            e.printStackTrace();
                        }
                    }
                }

            }

        }


    }


    /**
     * @param title team title
     * @return team code
     */
    static String translateTeamTitle(String title){
        switch (title){
            case "FBK AS Trenčín"  -> {return "AST";}
            case "FBC Grasshoppers AC UNIZA Žilina"  -> {return "GRA";}
            case "Exel Snipers Bratislava"  -> {return "SNI";}
            case "ŠK Lido Prírodovedec Bratislava"  -> {return "LID";}
            case "TEMPISH CAPITOL Floorball Club"  -> {return "CAP";}
            case "Tsunami Záhorská Bystrica"  -> {return "TSU";}
            case "FaBK ATU Košice"  -> {return "ATU";}
            case "1. FBC Florbal Trenčín"  -> {return "FCT";}
            case "ŠK Victory Stars Dubnica n/V"  -> {return "DNV";}
            case "FBK Nižná" -> {return "FKN";}
            case "FBC alkoholonline.sk Prešov"  -> {return "PRE";}
            case "FK Florko Košice" -> {return "FLK";}
            default -> {return "";}

        }
    }

    /**
     * @param url url of match
     * @param date date
     * @param statement Statement
     * @return Statistics from the match.
     * @throws JSONException .
     * @throws IOException .
     * @throws SQLException .
     */
    static StatLinePair loadMatchData(String url, Date date, Statement statement) throws JSONException, IOException, SQLException {

        String content = getWebsiteContent(url);
        Document doc = Jsoup.parse(content);
        ArrayList<GoalieStatLine> goalieStatLines = new ArrayList<>();
        ArrayList<PlayerStatLine> playerStatLines = new ArrayList<>();

        String homeTitle = doc.select("div.HomeCompetitorTitle").text();
        String awayTitle = doc.select("div.AwayCompetitorTitle").text();

        String homeTeamName = translateTeamTitle(homeTitle);
        String awayTeamName = translateTeamTitle(awayTitle);
        int i = 0;
        for (Element table : doc.select("table")) {

            String tableClass = table.parent().attributes().get("class");
            if (!tableClass.contains("match-quickStats") && tableClass.contains("table-responsive")){
                for (Element body : table.select("tbody")) {
                    for (Element row : body.select("tr")) {
                        Elements tds = row.select("td");
                        String name = "";
                        try {
                            name = anonymizeName(tds.get(0).text());
                        } catch (StringIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                        if (!name.equals("")) {
                            PlayerInfo player = DatabaseController.getPlayerByName(name, (i < 2 ? homeTeamName : awayTeamName), statement);
                            int matchID = DatabaseController.getFantasyMatchID(player.teamID, date, statement);
                            if (player.ID > 0) {
                                if (i % 2 == 0) {
                                    GoalieStatLine statLine = new GoalieStatLine();
                                    statLine.playerID = player.ID;
                                    statLine.teamID = player.teamID;
                                    statLine.matchID = matchID;
                                    statLine.shotsOnGoal = Integer.parseInt(tds.get(2).text());
                                    statLine.goals = Integer.parseInt(tds.get(4).text());
                                    statLine.penalty = Integer.parseInt(tds.get(7).text());
                                    statLine.fantasyScore = getFantasyScore(statLine);

                                    goalieStatLines.add(statLine);
                                } else {
                                    PlayerStatLine statLine = new PlayerStatLine();
                                    statLine.playerID = player.ID;
                                    statLine.teamID = player.teamID;
                                    statLine.matchID = matchID;
                                    statLine.goals = Integer.parseInt(tds.get(3).text());
                                    statLine.assists = Integer.parseInt(tds.get(4).text());
                                    statLine.penalty = Integer.parseInt(tds.get(6).text());
                                    statLine.fantasyScore = getFantasyScore(statLine);

                                    playerStatLines.add(statLine);
                                }
                            }
                        }
                    }
                }
                ++i;
            }

        }
        return new StatLinePair(goalieStatLines, playerStatLines);
    }

    /**
     * @param name Full name
     * @return changed name
     */
    private static String anonymizeName(String name){
        String[] splited = name.split(",");
        return splited[1]+ " " + splited[0].substring(0,3) + ".";

    }

    /**
     * @param urlString url of the website
     * @return content of the website
     * @throws IOException .
     */
    private static String getWebsiteContent(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        String encoding = con.getContentEncoding();
        encoding = encoding == null ? "UTF-8" : encoding;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int len;
        while ((len = in.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return baos.toString(encoding);
    }

    /**
     * Loads all real matches form the website and stores the date to the database.
     */
    public static void loadRealMatches(){
        try {
            Statement statement = DBConnection.connection.createStatement();
            String content = getWebsiteContent(MATCHES_URL);
            Document doc = Jsoup.parse(content);
            SimpleDateFormat inputFormater = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat outputFormater = new SimpleDateFormat("yyyy-MM-dd");

            for (Element table : doc.select("table")) {

                if (table.attributes().get("class").contains("table-game-preview")) {

                    for (Element body : table.select("tbody")) {

                        for (Element row : body.select("tr")) {

                            if(!row.attributes().get("class").contains("mobile-g-preview")) {

                                Elements tds = row.select("td");

                                int id = Integer.parseInt(tds.get(0).text());

                                if (id <= MAX_MATCH_ID) {
                                    String url = BASE_URL+tds.get(2).select("div").first().select("a[href]").first().attributes().get("href")+STATS_URL;
                                    String dateString = tds.get(2).select("div").last().select("div").last().text();
                                    String date = outputFormater.format(inputFormater.parse(dateString));
                                    statement.executeUpdate("INSERT INTO RealMatches (URL, Date) VALUES ('"+url+"', '"+date+"')");


                                }
                            }
                        }
                    }

                }

            }
        }catch (IndexOutOfBoundsException | NumberFormatException | ParseException | IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Loads start and end of the season
     */
    public static void loadStartAndEnd(){
        try {

            Statement statement = DBConnection.connection.createStatement();
            ResultSet startResultSet = statement.executeQuery("SELECT Date FROM RealMatches ORDER BY Date ASC LIMIT 1");
            Date start = new Date();
            if(startResultSet.next()){
                start = startResultSet.getDate("Date");
            }

            ResultSet endResultSet = statement.executeQuery("SELECT Date FROM RealMatches ORDER BY Date DESC LIMIT 1");
            Date end = new Date();
            if(endResultSet.next()){
                end = endResultSet.getDate("Date");
            }

            Calendar calendar = Calendar.getInstance();

            calendar.setTime(start);
            int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int startDifference = (startDayOfWeek + 3) % 7;
            calendar.add(Calendar.DAY_OF_YEAR, -startDifference);
            start = calendar.getTime();

            calendar.setTime(end);
            int endDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int endDifference = (Calendar.TUESDAY - endDayOfWeek + 7) % 7;
            calendar.add(Calendar.DAY_OF_YEAR, endDifference);
            end = calendar.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            statement.executeUpdate("UPDATE Settings SET SeasonStart='"+formatter.format(start)+"', SeasonEnd='"+formatter.format(end)+"', CurrentDate='"+formatter.format(start)+"', LastUpdate='"+formatter.format(start)+"', NextUpdate='"+formatter.format(start)+"' WHERE ID=1");


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param statLine Player statistics
     * @return Points scored by player.
     */
    public static int getFantasyScore(PlayerStatLine statLine){
        return statLine.goals*GOAL_POINTS + statLine.assists*ASSIST_POINTS - statLine.penalty*PENALTY_POINTS;
    }

    /**
     * @param statLine Player statistics
     * @return Points scored by player.
     */
    public static int getFantasyScore(GoalieStatLine statLine){
        return (statLine.shotsOnGoal - statLine.goals)*BLOCKED_SHOT_POINTS - statLine.penalty*PENALTY_POINTS;
    }
}
