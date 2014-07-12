package view;
import javax.swing.*;

import model.*;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LoggingGUI extends JFrame {
    private static final long serialVersionUID = 1L; //required
    
    
    private final JTextField commentField;
    private final JTextField audioSource;
    private final JButton enterText;
    private final JButton playpause;
    private final JButton enterAudio;
    private final JButton rewind;
    private final JButton fastforward;
    private Container contentPane;
    private final JPanel userPanel; //contains the user interface
    private final JLabel currentAudioSource;
    private final JLabel timeStamp;
    private final JTextArea outputLog;
    private final JScrollPane displayScrollPane;
    
    private final SoundPlayer player;
    private final BlockingQueue<String[]> audioQueue;
    private final BlockingQueue<String> timeQueue;
    
    private AtomicInteger time; //holds a rounded time stamp, as I don't feel its necessary to have precision greater than a second
    
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
        
        rewind = new JButton();
        rewind.setName("rewind");
        rewind.setText("<<");
        rewind.setMaximumSize(new Dimension(30,enterText.getSize().height));
        
        fastforward = new JButton();
        fastforward.setName("fastforward");
        fastforward.setText(">>");
        fastforward.setMaximumSize(new Dimension(30,enterText.getSize().height));
        
        outputLog = new JTextArea(5,20);
        outputLog.setName("outputField");
        outputLog.setText("");
        outputLog.setEditable(false);
        
        contentPane = getContentPane();
        userPanel = new JPanel();
        
        //displayPanel = new DisplayBox(); //this is the old version
        
        displayScrollPane = new JScrollPane(outputLog); //TODO: make this replace displayPanel
        
        currentAudioSource = new JLabel();
        currentAudioSource.setName("currentAudioSource");
        currentAudioSource.setText("File: None");
        
        timeStamp = new JLabel();
        timeStamp.setName("timeStamp");
        timeStamp.setText("      0:00 / 0:00");
        
        time = new AtomicInteger();
        time.set(0);
        
        audioQueue = new LinkedBlockingQueue<String[]>();
        player = new SoundPlayer(audioQueue, timeStamp, time);
        timeQueue = new LinkedBlockingQueue<String>();
        
        configureLayouts();
        addActionListeners();
        
        //Start threads
        Thread soundPlayerThread = new Thread(player);
        soundPlayerThread.start();
        
        //enterText.addKeyListener(new KeyPressedListener());
        //enterText.requestFocusInWindow();
        
        setFocusable(true);
        this.addKeyListener(new KeyPressedListener());
        //this.requestFocusInWindow();
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
                main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                main.requestFocus();
                main.setVisible(true);
            }
        });
    }
    
    private void addActionListeners() {
        playpause.addActionListener(new PauseActionListener(playpause, audioQueue));
        
        SoundActionListener soundAction = new SoundActionListener(audioSource, audioQueue, currentAudioSource);
        enterAudio.addActionListener(soundAction);
        audioSource.addActionListener(soundAction);
        
        EnterMessageActionListener enterAction = new EnterMessageActionListener(commentField, outputLog, time);
        enterText.addActionListener(enterAction);
        commentField.addActionListener(enterAction);
        
        rewind.addActionListener(new PositionControlActionListener(audioQueue,"rewind"));
        fastforward.addActionListener(new PositionControlActionListener(audioQueue,"fastforward"));
    }
    
    
    private void configureLayouts() {
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
                	.addComponent(audioSource)
                	.addComponent(enterAudio)
                )
                .addGroup(userLayout.createSequentialGroup()
                	.addComponent(rewind)
                	.addComponent(playpause)
                	.addComponent(fastforward)
                )
                .addGroup(userLayout.createSequentialGroup()
                	.addComponent(currentAudioSource)
                	.addComponent(timeStamp)
                )
        );
        
        userLayout.setVerticalGroup(
            userLayout.createSequentialGroup()
                .addGroup(userLayout.createParallelGroup()
                	.addComponent(commentField)
                    .addComponent(enterText)
                )
                .addGroup(userLayout.createParallelGroup()
                  	.addComponent(audioSource)
                  	.addComponent(enterAudio)
                )
                .addGroup(userLayout.createParallelGroup()
                	.addComponent(rewind)
                	.addComponent(playpause)
                	.addComponent(fastforward)
                )
                .addGroup(userLayout.createParallelGroup()
                	.addComponent(currentAudioSource)
                	.addComponent(timeStamp)
                )
        );
        
        //Set up the display panel
        //GroupLayout displayLayout = new GroupLayout(displayPanel);
        //displayPanel.setLayout(displayLayout);
        
        ScrollPaneLayout scrollPaneLayout = new ScrollPaneLayout();
        displayScrollPane.setLayout(scrollPaneLayout);
        displayScrollPane.setPreferredSize(new Dimension(500,400));
        
        /*In a container that uses a BorderLayout:
        textArea = new JTextArea(5, 30);
        ...
        JScrollPane scrollPane = new JScrollPane(textArea);
        ...
        setPreferredSize(new Dimension(450, 110));
        ...
        add(scrollPane, BorderLayout.CENTER);*/
        
        //Configure the layout specifications for both panels
        userPanel.setPreferredSize(new Dimension(500,100));

        contentPane.add(userPanel, BorderLayout.SOUTH);
        contentPane.add(displayScrollPane, BorderLayout.NORTH);
    }
}
