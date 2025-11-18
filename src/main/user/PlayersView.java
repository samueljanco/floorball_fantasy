package main.user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 *  Players view
 */
public class PlayersView extends PlayerListView {

    private JTextField seachField;

    private final PlayersController playersController;

    /**
     * @param playersController *
     * @param roasterController *
     */
    public PlayersView(PlayersController playersController, RoasterController roasterController) {
        super(playersController.getPlayers(), roasterController);
        createSearchBar();
        this.playersController = playersController;

    }

    /**
     *  Creates search bar
     */
    private void createSearchBar(){
        JPanel searchBar = new JPanel();
        searchBar.setLayout(new BorderLayout());
        seachField = new JTextField();
        JButton button = new JButton("Search");
        button.addActionListener(e -> {
            ArrayList<PlayerInfo> players = playersController.getSearchResult(seachField.getText());
            reloadList(players);

        });

        searchBar.add(seachField, BorderLayout.CENTER);
        searchBar.add(button, BorderLayout.EAST);
        add(searchBar, BorderLayout.NORTH);
    }


    /**
     * @param player *
     * @return label for button
     */
    private String getButtonLabel(PlayerInfo player){
        if(player.teamID == roasterController.getID()){
            return "-";
        }else if(player.teamID == 0){
            return "+";
        }
        return "*";
    }

    /**
     * @param player *
     * @return color for button
     */
    private Color getButtonColor(PlayerInfo player){
        if(player.teamID == roasterController.getID()){
            return new Color(248, 18, 18);
        }else if(player.teamID == 0){
            return new Color(68, 248, 18);
        }
        return new Color(18, 194, 248);
    }


    /**
     * @param player *
     * @return button for player
     */
    @Override
    JButton createButton(PlayerInfo player) {
        JButton positionButton = new JButton(getButtonLabel(player));
        positionButton.setFocusPainted(false);
        positionButton.setMargin(new Insets(0, 0, 0, 0));
        positionButton.setContentAreaFilled(false);
        positionButton.setBorderPainted(false);
        positionButton.setOpaque(false);
        positionButton.setFont(new Font("Arial", Font.PLAIN, 45));
        positionButton.setForeground(getButtonColor(player));
        positionButton.setBorder(new EmptyBorder(0, 25, 0, 25));
        positionButton.addActionListener(e -> buttonFunction(positionButton, player));

        return positionButton;

    }

    /**
     * @param button *
     * @param player *
     */
    @Override
    void buttonFunction(JButton button, PlayerInfo player){
        if(playersController.hasSeasonStarted()) {
            System.out.println("clicked");
            if (player.teamID == 0) {
                if (roasterController.addPlayer(player)) {
                    button.setText(getButtonLabel(player));
                    button.setForeground(getButtonColor(player));
                    reloadRoaster();
                } else {
                    JOptionPane.showMessageDialog(getTopLevelAncestor(),
                            "Unable to add the player",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }


            } else if (player.teamID == roasterController.getID()) {
                roasterController.dropPlayer(player);
                button.setText(getButtonLabel(player));
                button.setForeground(getButtonColor(player));
                reloadRoaster();
            }
        }else{
            JOptionPane.showMessageDialog(getTopLevelAncestor(),
                    "Adding players is possible only after the draft has ended.",
                    "Inane error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reload roaster after changes
     */
    private void reloadRoaster(){
        PlayerListView roaster = (PlayerListView) ((TeamManagementView)getParent()).getComponentAt(0);
        roaster.reloadList(roasterController.getRoaster());
    }


}
