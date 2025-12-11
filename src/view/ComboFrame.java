package view;

import javax.swing.*;
import java.awt.*;

public class ComboFrame extends JFrame {
    private JPanel panel;
    private JLabel label;
    private int windowOffset;
    public ComboFrame(int combo, int windowX, int windowY) {
        this.windowOffset = 10;
        setLayout(new BorderLayout());
        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        add(panel, BorderLayout.CENTER);
        setResizable(false);
        setSize(300,200);
        setLocation(windowX + (combo-1) * windowOffset,windowY + (combo-1) * windowOffset );
        setVisible(true);
        panel.setBackground(new Color(192,99,72,255));
        label = new JLabel(combo+"x");
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 100));
        label.setSize(panel.getSize().width,panel.getSize().height);
        label.setForeground(new Color(255,255,255,255));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label,BorderLayout.CENTER);
    }
}
