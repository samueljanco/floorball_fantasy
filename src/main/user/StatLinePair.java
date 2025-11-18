package main.user;

import java.util.ArrayList;

/**
 * Structure for player and goalie stats
 */
public class StatLinePair {
    public ArrayList<GoalieStatLine> goalieStatLines;
    public ArrayList<PlayerStatLine> playerStatLines;

    /**
     * @param goalies *
     * @param players *
     */
    public StatLinePair(ArrayList<GoalieStatLine> goalies, ArrayList<PlayerStatLine> players){
        goalieStatLines = goalies;
        playerStatLines = players;
    }
}
