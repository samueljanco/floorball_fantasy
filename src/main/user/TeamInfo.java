package main.user;

/**
 * Structure for team data
 */
public class TeamInfo {
    public int ID;
    public String name;
    public int wins;
    public int draws;
    public int losses;
    public Integer draftOrder;

    /**
     *  Constructor
     */
    public TeamInfo(){
        this.ID = 0;
    }

    /**
     * @param ID *
     * @param name *
     * @param wins *
     * @param draws *
     * @param losses *
     * @param draftOrder *
     */
    public TeamInfo(int ID, String name, int wins, int draws, int losses, int draftOrder){
        this.ID = ID;
        this.name = name;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.draftOrder = draftOrder;
    }

    /**
     * @return string
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * @return record
     */
    public String getRecord(){
        return wins +"-"+ draws +"-"+losses;
    }


}
