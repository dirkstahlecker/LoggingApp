package view;
import javax.swing.*;

import audio.AudioControlActionListener;
import audio.SoundActionListener;
import audio.SoundPlayer;
import model.*;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
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
    private final JButton volumeUp;
    private final JButton volumeDown;
    private final JButton export;
    private final JButton clearLog;
    
    private Container contentPane;
    
    private final JPanel userPanel; //contains the user interface
    
    private final JLabel currentAudioSource;
    private final JLabel timeStamp;
    
    private final JTextArea outputLog;
    
    private final JScrollPane displayScrollPane;
    
    private final BlockingQueue<String[]> audioQueue;
    private final BlockingQueue<String> timeQueue;
    private final BlockingQueue<String> outputQueue;
    
    private final OutputLogDisplay outputLogDisplay;
    private final SoundPlayer player;
    
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
        enterText.setText("Enter Comment");
        enterText.setMaximumSize(new Dimension(80,enterText.getSize().height));
        
        playpause = new JButton();
        playpause.setName("playpause");
        playpause.setText("Play");
        playpause.setMinimumSize(new Dimension(80,enterText.getSize().height));
        
        enterAudio = new JButton();
        enterAudio.setName("enterAudio");
        enterAudio.setText("Enter Path");
        enterAudio.setMinimumSize(new Dimension(80,enterText.getSize().height));
        
        rewind = new JButton();
        rewind.setName("rewind");
        rewind.setText("<<");
        rewind.setMaximumSize(new Dimension(30,rewind.getSize().height));
        
        fastforward = new JButton();
        fastforward.setName("fastforward");
        fastforward.setText(">>");
        fastforward.setMaximumSize(new Dimension(30,fastforward.getSize().height));
        
        volumeUp = new JButton();
        volumeUp.setName("voluemUp");
        volumeUp.setText("Volume Up");
        volumeUp.setMaximumSize(new Dimension(30,volumeUp.getSize().height));
        
        volumeDown = new JButton();
        volumeDown.setName("voluemDown");
        volumeDown.setText("Volume Down");
        volumeDown.setMaximumSize(new Dimension(30,volumeDown.getSize().height));
        
        export = new JButton();
        export.setName("export");
        export.setText("Export log");
        
        clearLog = new JButton();
        clearLog.setName("clearLog");
        clearLog.setText("Clear");
        
        outputLog = new JTextArea();
        outputLog.setName("outputField");
        outputLog.setText("");
        outputLog.setEditable(false);
        Font font = new Font("Courier New", Font.PLAIN, 14);
        outputLog.setFont(font);
        
        contentPane = getContentPane();
        userPanel = new JPanel();
        
        //displayPanel = new DisplayBox(); //this is the old version
        displayScrollPane = new JScrollPane(outputLog);
        
        currentAudioSource = new JLabel();
        currentAudioSource.setName("currentAudioSource");
        currentAudioSource.setText("File: None");
        
        timeStamp = new JLabel();
        timeStamp.setName("timeStamp");
        timeStamp.setText("      0:00 / 0:00");
        
        time = new AtomicInteger();
        time.set(0);
        
        audioQueue = new LinkedBlockingQueue<String[]>();
        outputQueue = new LinkedBlockingQueue<String>();
        player = new SoundPlayer(audioQueue, timeStamp, currentAudioSource, time);
        timeQueue = new LinkedBlockingQueue<String>();
        this.outputLogDisplay = new OutputLogDisplay(outputQueue, commentField, outputLog, time);
        
        //Configure everything else, separated for readability
        configureLayouts();
        addActionListeners();
        startThreads();
        
        //enterText.addKeyListener(new KeyPressedListener());
        //enterText.requestFocusInWindow();
        
        setFocusable(true);
        this.addKeyListener(new KeyPressedListener());
        //this.requestFocusInWindow();
        pack();
    }
    
    private void addActionListeners() {
        playpause.addActionListener(new PauseActionListener(audioQueue));
        
        SoundActionListener soundAction = new SoundActionListener(audioSource, audioQueue);
        enterAudio.addActionListener(soundAction);
        audioSource.addActionListener(soundAction);
        
        AddToOutputQueue enterAction = new AddToOutputQueue(outputQueue,"enter");
        enterText.addActionListener(enterAction);
        commentField.addActionListener(enterAction);
        
        rewind.addActionListener(new AudioControlActionListener(audioQueue,"rewind"));
        fastforward.addActionListener(new AudioControlActionListener(audioQueue,"fastforward"));
        
        volumeUp.addActionListener(new AudioControlActionListener(audioQueue,"volume","up"));
        volumeDown.addActionListener(new AudioControlActionListener(audioQueue,"volume","down"));
        
        export.addActionListener(new ExportLogActionListener(this, outputLog));
        
        clearLog.addActionListener(new AddToOutputQueue(outputQueue,"clear"));
    }
    
    private void startThreads() {
        Thread soundPlayerThread = new Thread(player);
        soundPlayerThread.start();
        
        Thread textOutputThread = new Thread(outputLogDisplay);
        textOutputThread.start();
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
                	.addComponent(volumeDown)
                	.addComponent(volumeUp)
                )
                .addGroup(userLayout.createSequentialGroup()
                	.addComponent(clearLog)
                	.addComponent(export)
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
                	.addComponent(volumeDown)
                	.addComponent(volumeUp)
                )
                .addGroup(userLayout.createParallelGroup()
                	.addComponent(clearLog)
                	.addComponent(export)
                	.addComponent(currentAudioSource)
                	.addComponent(timeStamp)
                )
        );
        
        ScrollPaneLayout scrollPaneLayout = new ScrollPaneLayout();
        displayScrollPane.setLayout(scrollPaneLayout);
        displayScrollPane.setPreferredSize(new Dimension(500,400));
        
        //Configure the layout specifications for both panels
        userPanel.setPreferredSize(new Dimension(500,125));

        contentPane.add(userPanel, BorderLayout.SOUTH);
        contentPane.add(displayScrollPane, BorderLayout.NORTH);
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
}
