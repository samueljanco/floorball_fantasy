package main.user;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 *  JPanel of Matchup View
 */
public class MatchupView extends JPanel {

    private final MatchupController matchupController;

    /**
     * @param matchupController controller
     */
    public MatchupView(MatchupController matchupController){
        this.matchupController = matchupController;
        try{
            setLayout(new BorderLayout());
            add(createHeader(), BorderLayout.NORTH);
            add(createMatchupScroller(
                    this.matchupController.getPlayers(true),
                    this.matchupController.getPlayers(false)),
                    BorderLayout.CENTER
            );

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * @return header for matchup table
     * @throws IOException .
     */
    private JPanel createHeader() throws IOException {
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridBagLayout());

        JLabel dashLabel = new JLabel("-");
        dashLabel.setFont(new Font("Arial", Font.PLAIN, 25));

        middlePanel.add(dashLabel);

        header.add(createTeamHeader(true), BorderLayout.WEST);
        header.add(middlePanel, BorderLayout.CENTER);
        header.add(createTeamHeader(false),BorderLayout.EAST);
        return header;
    }

    /**
     * @param isMyTeam boolean
     * @return header for team
     * @throws IOException .
     */
    private JPanel createTeamHeader(boolean isMyTeam) throws IOException {

        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(450, 80));

        BufferedImage jersey = ImageIO.read(new File("./src/main/icons/jerseyIcon.png"));
        JLabel jerseyLabel = new JLabel(new ImageIcon(jersey));


        JPanel teamPanel = new JPanel();
        teamPanel.setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());


        JLabel nameLabel = new JLabel(matchupController.getTeamsName(isMyTeam));
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        nameLabel.setBorder(new EmptyBorder(20, 20, 1, 5));
        infoPanel.add(nameLabel, BorderLayout.NORTH);

        JLabel recordLabel = new JLabel(matchupController.getRecord(isMyTeam));
        recordLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        recordLabel.setBorder(new EmptyBorder(0, 20, 20, 5));
        infoPanel.add(recordLabel, BorderLayout.SOUTH);

        JLabel pointTotalLabel = new JLabel(String.valueOf(matchupController.getScore(isMyTeam)));
        pointTotalLabel.setFont(new Font("Arial", Font.PLAIN, 25));

        teamPanel.add(jerseyLabel, isMyTeam ? BorderLayout.WEST : BorderLayout.EAST);
        teamPanel.add(infoPanel, isMyTeam ? BorderLayout.EAST : BorderLayout.WEST);

        header.add(teamPanel,isMyTeam ? BorderLayout.WEST : BorderLayout.EAST);
        header.add(pointTotalLabel, isMyTeam ? BorderLayout.EAST : BorderLayout.WEST);

        return header;

    }


    /**
     * @param myPlayers players
     * @param opponentPlayers players
     * @return Scrollable table of players in matchup
     */
    private JScrollPane createMatchupScroller(ArrayList<MatchupPlayerInfo> myPlayers, ArrayList<MatchupPlayerInfo> opponentPlayers){
        JPanel matchupPanel = new JPanel();
        matchupPanel.setLayout(new BorderLayout());
        if(myPlayers.size() == 0 || opponentPlayers.size() == 0){
            matchupPanel.add(createMiddlePanel(true),BorderLayout.CENTER);
        }else {
            matchupPanel.add(createTeamPanel(myPlayers, true), BorderLayout.WEST);
            matchupPanel.add(createMiddlePanel(false),BorderLayout.CENTER);
            matchupPanel.add(createTeamPanel(opponentPlayers, false), BorderLayout.EAST);
        }
        return new JScrollPane(matchupPanel);
    }


    /**
     * @param players players form my team
     * @param isMyTeam boolean
     * @return Create matchup table of players with header of their team
     */
    private JPanel createTeamPanel(ArrayList<MatchupPlayerInfo> players, boolean isMyTeam){

        JPanel myTeamPanel = new JPanel();
        myTeamPanel.setLayout(new GridLayout(players.size(), 1, 0, 0));

        for(MatchupPlayerInfo p : players){
            JPanel playerPanel = new JPanel();
            playerPanel.setLayout(new BorderLayout());
            playerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50,50,50)));
            playerPanel.setPreferredSize(new Dimension(450, 60));

            JPanel playerInfoPanel = new JPanel();
            playerInfoPanel.setLayout(new BorderLayout());

            JLabel playerLabel = new JLabel(p.name);
            playerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            playerLabel.setBorder(new EmptyBorder(10, 10, 0, 10));

            JLabel matchLabel = new JLabel(p.getPositionTitle());
            matchLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            matchLabel.setBorder(new EmptyBorder(0, 20, 10, 10));

            JLabel pointsLabel = new JLabel(String.valueOf(p.pointsSum));
            pointsLabel.setFont(new Font("Arial", Font.PLAIN, 20));
            pointsLabel.setBorder(new EmptyBorder(0, 10, 0, 10));

            playerInfoPanel.add(playerLabel, BorderLayout.NORTH);
            playerInfoPanel.add(matchLabel, BorderLayout.SOUTH);

            playerPanel.add(playerInfoPanel, isMyTeam ? BorderLayout.WEST : BorderLayout.EAST);
            playerPanel.add(pointsLabel, isMyTeam ? BorderLayout.EAST : BorderLayout.WEST);

            myTeamPanel.add(playerPanel);

        }

        return myTeamPanel;
    }

    /**
     * Create panel between matchup player lists.
     * @param isEmpty boolean
     * @return panel
     */
    private JPanel createMiddlePanel(boolean isEmpty){
        JPanel middlePanel = new JPanel();
        if(isEmpty){
            JLabel label = new JLabel("No matches have been played so far.");
            label.setFont(new Font("Arial", Font.PLAIN, 20));
            middlePanel.add(label);
        }else {
            middlePanel.setBackground(Color.BLACK);
        }
        return middlePanel;
    }
}
