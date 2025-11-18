package main.admin;

import main.database.DatabaseController;
import main.user.TeamManagementView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class LoginView extends JPanel {


    public LoginView(){
        createLoginView();
    }

    /**
     * @return Button for logging out.
     */
    private JButton createLogOutButton(){
        JButton logOutButton = new JButton("Log Out");
        logOutButton.addActionListener(e -> {
            removeAll();
            setLayout(new BorderLayout());
            createLoginView();
            revalidate();
            repaint();
        });

        return logOutButton;
    }

    /**
     * Creates and displays login form.
     * Creates and displays user/admin profiles.
     */
    private  void createLoginView(){

        setLayout(new BorderLayout());

        JLabel label = new JLabel("Enter your username", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 30));
        label.setBorder(new EmptyBorder(200, 30, 20, 30));

        JTextField textField = new JTextField(16);

        JButton button = new JButton("Login");
        button.setPreferredSize(new Dimension(100,40));

        button.addActionListener(e -> {
            int teamID = DatabaseController.getTeamIDByName(textField.getText());
            if(teamID == 0){
                JOptionPane.showMessageDialog(getTopLevelAncestor(),
                        "Unknown user",
                        "Inane error",
                        JOptionPane.ERROR_MESSAGE);
            }else {
                removeAll();
                setLayout(new BorderLayout());
                add(createLogOutButton(), BorderLayout.NORTH);
                add(teamID > 0 ? new TeamManagementView(teamID) : new AdminView(), BorderLayout.CENTER);
                revalidate();
                repaint();
            }
            textField.setText("");
        });

        JPanel loginPanel = new JPanel();
        loginPanel.setPreferredSize(new Dimension(300,70));
        loginPanel.add(textField);
        loginPanel.add(button);

        add(label, BorderLayout.NORTH);
        add(loginPanel, BorderLayout.CENTER);
    }


}

