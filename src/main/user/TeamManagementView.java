package main.user;

import javax.swing.*;

/**
 *  Team Management View
 */
public class TeamManagementView extends JTabbedPane {

    TeamManagementController controller;

    /**
     * @param teamID *
     */
    public TeamManagementView(int teamID){

        controller = new TeamManagementController(teamID);
        addTab("TEAM", new RoasterView(controller.roasterController));
        addTab("MATCHUP", new MatchupView(controller.matchupController));
        addTab("PLAYERS", new PlayersView(controller.playersController, controller.roasterController));
        addTab("LEAGUE", new LeagueView(controller.draftController, controller.roasterController));
    }
}
