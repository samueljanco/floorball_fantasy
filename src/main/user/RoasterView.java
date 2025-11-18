package main.user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


/**
 * Roaster View
 */
public class RoasterView extends PlayerListView{

    private boolean substitution = false;


    /**
     * @param roasterController *
     */
    public RoasterView(RoasterController roasterController) {
        super(roasterController.getRoaster(), roasterController);
    }

    /**
     * @param firstIndex *
     * @param secondIndex *
     */
    void swap(int firstIndex, int secondIndex) {

        if (firstIndex == secondIndex) {
            return;
        }

        if (firstIndex > secondIndex) {
            int temp = firstIndex;
            firstIndex = secondIndex;
            secondIndex = temp;
        }

        JPanel first = (JPanel) listPanel.getComponent(firstIndex);
        JPanel second = (JPanel) listPanel.getComponent(secondIndex);

        listPanel.remove(first);
        listPanel.remove(second);

        // switch button texts
        BorderLayout firstBorder = (BorderLayout) first.getLayout();
        JButton firstButton = ((JButton)((JPanel)firstBorder.getLayoutComponent(BorderLayout.WEST)).getComponent(0));
        BorderLayout secondBorder = (BorderLayout) second.getLayout();
        JButton secondButton = ((JButton)((JPanel)secondBorder.getLayoutComponent(BorderLayout.WEST)).getComponent(0));
        String firstText = firstButton.getText();
        firstButton.setText(secondButton.getText());
        secondButton.setText(firstText);

        listPanel.add(second, firstIndex);
        listPanel.add(first, secondIndex);
    }


    /**
     * @param player *
     * @return button
     */
    @Override
    JButton createButton(PlayerInfo player) {
        JButton positionButton=new JButton(PlayerInfo.getRoasterLabel(player.roasterSpot));
        positionButton.setFocusPainted(false);
        positionButton.setMargin(new Insets(0, 0, 0, 0));
        positionButton.setContentAreaFilled(false);
        positionButton.setBorderPainted(false);
        positionButton.setOpaque(false);
        positionButton.setFont(new Font("Arial", Font.PLAIN, 25));
        positionButton.setForeground(new Color(77, 136,250));
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
        if(substitution){
            if(roasterController.getSubstitutePlayerID() != player.ID){
                if(roasterController.canSwap(player)) {
                    swap(roasterController.getSubstitutePlayerRoasterSpot(), player.roasterSpot);


                    roasterController.swapRoasterSpots(player);
                    System.out.println("OFF " + player.name);
                }else{
                    JOptionPane.showMessageDialog(getTopLevelAncestor(),
                            "Unable to swap these players",
                            "Inane error",
                            JOptionPane.ERROR_MESSAGE);
                }

            }else {
                System.out.println("OFF SAME "+player.name);
            }
            substitution = false;
            JPanel panel =  (JPanel)listPanel.getComponent(roasterController.getSubstitutePlayerRoasterSpot());
            BorderLayout bl = (BorderLayout) panel.getLayout();
            ((JPanel)bl.getLayoutComponent(BorderLayout.WEST)).getComponent(0).setForeground(new Color(77, 136,250));

        }else{
            substitution = true;
            roasterController.setSubstitutePlayer(player);
            System.out.println("ON "+player.name);
            JPanel panel =  (JPanel)listPanel.getComponent(roasterController.getSubstitutePlayerRoasterSpot());
            BorderLayout bl = (BorderLayout) panel.getLayout();
            ((JPanel)bl.getLayoutComponent(BorderLayout.WEST)).getComponent(0).setForeground(new Color(238, 250, 77));
        }
    }
}

