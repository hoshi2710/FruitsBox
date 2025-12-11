package controller;

import model.Button;

import java.util.HashSet;
import java.util.Set;

public class ButtonManager {
    Set<Button> buttons;
    public ButtonManager() {
        this.buttons = new HashSet<>();
    }
    public void enableButton(Button button) {
        buttons.add(button);
        button.setEnabled(true);
    }
    public void disableAllButtons() {
        for (Button button : buttons) {
            button.setEnabled(false);
        }
        buttons.clear();
    }
}
