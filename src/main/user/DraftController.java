package main.user;

import java.util.ArrayList;
import main.admin.Settings;
import main.database.DatabaseController;

/**
 * Contorller for draft
 */
public class DraftController {

    DraftInfo info;

    TeamInfo myTeam;

    ArrayList<PlayerInfo> players;

    Settings settings;

    public DraftController(TeamInfo myTeam, Settings settings, ArrayList<PlayerInfo> players){
        this.myTeam = myTeam;
        this.settings = settings;
        this.players = players;
        info = DatabaseController.getDraftInfo();
    }

    /**
     * @return boolean indicating whether is my teams turn to pick
     */
    public boolean isMyTurn(){
        return myTeam.ID == info.nextPickID;
    }

    public ArrayList<PlayerInfo> getPlayers(){
        return players;
    }

    /**
     * @return boolean indicating whether the draft has ended
     */
    public boolean hasDraftEnded(){

        return settings.state.equals("season");
    }

    /**
     * Loads draft info
     */
    public void loadInfo(){
        info = DatabaseController.getDraftInfo();
    }

    /**
     * Loads settings
     */
    public void loadSettings(){
        settings = DatabaseController.getSettings();
    }

    /**
     * @return boolean indicating whether the draft has started
     */
    public boolean hasDraftStarted(){
        return settings.state.equals("draft");
    }

    /**
     * Sets id of next team in draft order
     */
    public void setNextToPick(){
        int next = myTeam.draftOrder == info.teamCount ? 1 : myTeam.draftOrder + 1 ;
        ++info.nextPickID;
        ++info.place;
        if(info.place > DraftInfo.PICKS_PER_TEAM * info.teamCount ){
            info.nextPickID = 0;
            info.place = 0;

            DatabaseController.endTheDraft();
        }else{
            DatabaseController.setNextPick(next, info.place);
        }

    }

}
