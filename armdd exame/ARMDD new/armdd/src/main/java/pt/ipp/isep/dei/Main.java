package pt.ipp.isep.dei;

import pt.ipp.isep.dei.ui.console.menu.MainMenuUI;
import pt.isep.lei.esoft.auth.domain.model.User;

public class Main {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.run();

        try {
            MainMenuUI menu = new MainMenuUI();
            menu.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 