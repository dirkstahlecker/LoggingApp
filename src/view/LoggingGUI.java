package view;
import javax.swing.*;

import model.SoundActionListener;
import model.SoundPlayer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

public class LoggingGUI extends JFrame {
    private static final long serialVersionUID = 1L; //required
    
    private final JTextField commentField;
    private final JTextField audioSource;
    private final JButton enterText;
    private final JButton playpause;
    private final JButton enterAudio;
    private Container contentPane;
    private final JPanel userPanel; //contains the user interface
    private final JPanel displayPanel; //displays the past logs
    
    private final SoundPlayer player;
    
    public LoggingGUI() {
        
    	//Configure the GUI elements
        commentField = new JTextField();
        commentField.setName("commentField");
        commentField.setText("");
        commentField.setMaximumSize(new Dimension(Integer.MAX_VALUE, commentField.getPreferredSize().height));
        
        audioSource = new JTextField();
        audioSource.setName("audioSource");
        audioSource.setText("");
        audioSource.setMaximumSize(new Dimension(Integer.MAX_VALUE, commentField.getPreferredSize().height));
        
        enterText = new JButton();
        enterText.setName("enterText");
        enterText.setText("Enter");
        enterText.setMaximumSize(new Dimension(80,enterText.getSize().height));
        
        playpause = new JButton();
        playpause.setName("playpause");
        playpause.setText("Play");
        playpause.setMinimumSize(new Dimension(80,enterText.getSize().height));
        
        enterAudio = new JButton();
        enterAudio.setName("enterAudio");
        enterAudio.setText("Enter");
        enterAudio.setMinimumSize(new Dimension(80,enterText.getSize().height));
        
        contentPane = getContentPane();
        userPanel = new JPanel();
        displayPanel = new DisplayBox();
        
        player = new SoundPlayer();
        
        //Set up the user interface panel
        GroupLayout userLayout = new GroupLayout(userPanel);
        userPanel.setLayout(userLayout);
        
        userLayout.setHorizontalGroup(
        	userLayout.createParallelGroup()
            	.addGroup(userLayout.createSequentialGroup()
            	    .addComponent(commentField)
                    .addComponent(enterText)
                )
                .addGroup(userLayout.createSequentialGroup()
                	.addComponent(playpause)
                	.addComponent(audioSource)
                	.addComponent(enterAudio)
                )
        );
        
        userLayout.setVerticalGroup(
            userLayout.createSequentialGroup()
                .addGroup(userLayout.createParallelGroup()
                	.addComponent(commentField)
                    .addComponent(enterText)
                )
                .addGroup(userLayout.createParallelGroup()
                  	.addComponent(playpause)
                  	.addComponent(audioSource)
                  	.addComponent(enterAudio)
                )
        );
        
        //Set up the display panel
        GroupLayout displayLayout = new GroupLayout(displayPanel);
        displayPanel.setLayout(displayLayout);

        //Configure the layout specifications for both panels
        userPanel.setPreferredSize(new Dimension(500,75));
        displayPanel.setPreferredSize(new Dimension(500,400));
        contentPane.add(userPanel, BorderLayout.SOUTH);
        contentPane.add(displayPanel, BorderLayout.NORTH);
        
        //Add action listeners
        playpause.addActionListener(new PauseActionListener(player,playpause));
        enterAudio.addActionListener(new SoundActionListener(player,audioSource));
        
        pack();
    }
    
    /**
     * necessary to run from command line
     * @param args unused
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoggingGUI main = new LoggingGUI();

                main.setVisible(true);
            }
        });
    }
    
}
