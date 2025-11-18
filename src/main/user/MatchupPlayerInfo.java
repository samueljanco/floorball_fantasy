package main.user;

/**
 * Player data for matchup
 */
public class MatchupPlayerInfo  extends PlayerInfo{

    public int pointsSum;

    public MatchupPlayerInfo(int ID, String name, int teamID, String position, String realTeam,int roasterSpot, int pointsSum) {
        super(ID, name, teamID, position, realTeam, roasterSpot, 0);
        this.pointsSum = pointsSum;
    }

}
