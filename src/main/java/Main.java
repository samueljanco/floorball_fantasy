package main.java;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;

import javax.swing.*;

public class Main {
    public static void main(String[] args){

        try{
            UIManager.setLookAndFeel(new FlatOneDarkIJTheme());
            FantasyLeague fl = new FantasyLeague();
            fl.start();

        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

    }

}

