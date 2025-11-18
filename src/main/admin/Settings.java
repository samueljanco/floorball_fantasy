package main.admin;

import java.util.Date;

/**
 * Settings of the league
 */
public class Settings {
    public Date seasonStart;

    public Date seasonEnd;
    public Date currentDate;
    public Date lastUpdate;

    public Date nextUpdate;

    public String state;


    /**
     * Constructor
     */
    public Settings(){}

    /**
     * @param currentDate *
     * @param seasonStart *
     * @param seasonEnd *
     * @param lastUpdate *
     * @param nextUpdate *
     * @param state *
     */
    public Settings(Date currentDate, Date seasonStart, Date seasonEnd, Date lastUpdate, Date nextUpdate, String state){
        this.currentDate = currentDate;
        this.seasonStart = seasonStart;
        this.seasonEnd = seasonEnd;
        this.lastUpdate = lastUpdate;
        this.nextUpdate = nextUpdate;
        this.state = state;
    }

}
