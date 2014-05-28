package view;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

public class LoggingGUI extends JFrame {
    private static final long serialVersionUID = 1L; //required
    
    private final JTextField commentField;
    private final JButton enterText;
    private Container contentPane;
    private final JPanel userPanel;
    private final JPanel displayPanel;
    
    public LoggingGUI() {
        
        commentField = new JTextField();
        commentField.setName("commentField");
        commentField.setText("");
        commentField.setMaximumSize(new Dimension(Integer.MAX_VALUE, commentField.getPreferredSize().height));
        
        enterText = new JButton();
        enterText.setName("enterText");
        enterText.setText("Enter");
        enterText.setMaximumSize(new Dimension(80,enterText.getSize().height));
        
        
        contentPane = getContentPane();
        userPanel = new JPanel();
        displayPanel = new DisplayBox();
        
        //Set up the user interface panel
        GroupLayout userLayout = new GroupLayout(userPanel);
        userPanel.setLayout(userLayout);
        
        userLayout.setHorizontalGroup(
            userLayout.createSequentialGroup()
                 .addComponent(commentField)
                 .addComponent(enterText)
        );
        
        userLayout.setVerticalGroup(
            userLayout.createParallelGroup()
                .addComponent(commentField)
                .addComponent(enterText)
        );
        
        //Set up the display panel
        GroupLayout displayLayout = new GroupLayout(displayPanel);
        displayPanel.setLayout(displayLayout);
        
        /*
        displayLayout.setHorizontalGroup(
             displayLayout.createSequentialGroup()
                 .addComponent(displayBox)
        );
        
        displayLayout.setVerticalGroup(
            displayLayout.createParallelGroup()
                .addComponent(displayBox)
        );
        */
        
        userPanel.setPreferredSize(new Dimension(500,75));
        displayPanel.setPreferredSize(new Dimension(500,400));
        contentPane.add(userPanel, BorderLayout.SOUTH);
        contentPane.add(displayPanel, BorderLayout.NORTH);
        
        pack();
    }
    
    
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoggingGUI main = new LoggingGUI();

                main.setVisible(true);
            }
        });
    }
    
}
