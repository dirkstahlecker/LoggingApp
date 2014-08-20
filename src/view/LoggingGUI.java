package view;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;

import audio.AudioControlActionListener;
import audio.SoundActionListener;
import audio.SoundPlayer;
//import audio.SoundPlayer;
import audio.SoundPlayerFX;
import menu.MenuController;
import menu.MenuActionListener;
import menu.PerformFileAction;
import model.*;
import model.Constants.FileAction;

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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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
    private final BlockingQueue<String> menuQueue;
    private final BlockingQueue<String[]> performSaveQueue;
    
    private final OutputLogDisplay outputLogDisplay;
    private final SoundPlayerFX player;
    private final MenuController menuController;
    
    //menu bar
	private JMenuBar menuBar;
	private JMenu menu, submenu;
	private JMenuItem menuItem;
	private JRadioButtonMenuItem rbMenuItem;
	private JCheckBoxMenuItem cbMenuItem;
    
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
        timeStamp.setText("      0:00 / 0:00"); //TODO: space this somehow else, so it doesn't jump around
        
        time = new AtomicInteger();
        time.set(0);
        
        audioQueue = new LinkedBlockingQueue<String[]>();
        outputQueue = new LinkedBlockingQueue<String>();
        menuQueue = new LinkedBlockingQueue<String>();
        performSaveQueue = new LinkedBlockingQueue<String[]>();
        player = new SoundPlayerFX(audioQueue, timeStamp, currentAudioSource, time, playpause, performSaveQueue,this);
        timeQueue = new LinkedBlockingQueue<String>();
        this.outputLogDisplay = new OutputLogDisplay(outputQueue, commentField, outputLog, time);
        this.menuController = new MenuController(menuQueue,this);
        
        //Configure everything else, separated for readability
        configureLayouts();
        addActionListeners();
        startThreads();
        setUpMenuBar();
        
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
        
        clearLog.addActionListener(new AddToOutputQueue(outputQueue,"clear"));
    }
    
    private void startThreads() {
        Thread soundPlayerFXThread = new Thread(player);
        soundPlayerFXThread.start();
        
        Thread textOutputThread = new Thread(outputLogDisplay);
        textOutputThread.start();
        
        Thread menuControllerThread = new Thread(menuController);
        menuControllerThread.start();
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
                	.addComponent(currentAudioSource)
                	.addComponent(timeStamp)
                )
        );
        
        ScrollPaneLayout scrollPaneLayout = new ScrollPaneLayout();
        displayScrollPane.setLayout(scrollPaneLayout);
        displayScrollPane.setPreferredSize(new Dimension(750,400));
        
        //Configure the layout specifications for both panels
        userPanel.setPreferredSize(new Dimension(750,125));
        
        contentPane.add(userPanel, BorderLayout.SOUTH);
        contentPane.add(displayScrollPane, BorderLayout.NORTH);
    }
    
    private void setUpMenuBar() {
    	AtomicReference<String> audioFilePathReference = new AtomicReference<String>();
    	audioFilePathReference.set(null);
    	
    	//Create the menu bar.
    	menuBar = new JMenuBar();

    	//Build the first menu.
    	menu = new JMenu("File");
    	menu.setMnemonic(KeyEvent.VK_A);
    	//menu.getAccessibleContext().setAccessibleDescription("File");
    	menuBar.add(menu);
    	
    	//a group of JMenuItems
    	menuItem = new JMenuItem("New");
    	menuItem.setMnemonic(KeyEvent.VK_B);
    	menuItem.addActionListener(new PerformFileAction(this,performSaveQueue,outputLog,FileAction.NEW,audioQueue,outputLogDisplay,audioFilePathReference));
    	menu.add(menuItem);
    	
    	menuItem = new JMenuItem("Open", KeyEvent.VK_T);
    	//menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
    	menuItem.addActionListener(new PerformFileAction(this,performSaveQueue,outputLog,FileAction.OPEN,audioQueue,outputLogDisplay,audioFilePathReference));
    	menu.add(menuItem);
    	
    	menuItem = new JMenuItem("Save");
    	menuItem.setMnemonic(KeyEvent.VK_B);
    	menuItem.addActionListener(new PerformFileAction(this,performSaveQueue,outputLog,FileAction.SAVE,audioQueue,outputLogDisplay,audioFilePathReference));
    	menu.add(menuItem);
    	
    	menuItem = new JMenuItem("Save As");
    	menuItem.setMnemonic(KeyEvent.VK_B);
    	menuItem.addActionListener(new PerformFileAction(this,performSaveQueue,outputLog,FileAction.SAVE_AS,audioQueue,outputLogDisplay,audioFilePathReference));
    	menu.add(menuItem);
    	
    	
    	//menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));
    	
    	/*
    	//a group of radio button menu items
    	menu.addSeparator();
    	ButtonGroup group = new ButtonGroup();
    	rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
    	rbMenuItem.setSelected(true);
    	rbMenuItem.setMnemonic(KeyEvent.VK_R);
    	group.add(rbMenuItem);
    	menu.add(rbMenuItem);
    	
    	rbMenuItem = new JRadioButtonMenuItem("Another one");
    	rbMenuItem.setMnemonic(KeyEvent.VK_O);
    	group.add(rbMenuItem);
    	menu.add(rbMenuItem);
		*/
    	/*
    	//a group of check box menu items
    	menu.addSeparator();
    	cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
    	cbMenuItem.setMnemonic(KeyEvent.VK_C);
    	menu.add(cbMenuItem);

    	cbMenuItem = new JCheckBoxMenuItem("Another one");
    	cbMenuItem.setMnemonic(KeyEvent.VK_H);
    	menu.add(cbMenuItem);
		*/
    	/*
    	//a submenu
    	menu.addSeparator();
    	submenu = new JMenu("A submenu");
    	submenu.setMnemonic(KeyEvent.VK_S);

    	menuItem = new JMenuItem("An item in the submenu");
    	menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
    	submenu.add(menuItem);

    	menuItem = new JMenuItem("Another item");
    	submenu.add(menuItem);
    	menu.add(submenu);
    	 */
    	
    	//Build second menu in the menu bar.
    	menu = new JMenu("Export");
    	menu.setMnemonic(KeyEvent.VK_N);
    	menuBar.add(menu);
    	
    	menuItem = new JMenuItem("Export as .txt");
    	menuItem.setMnemonic(KeyEvent.VK_B);
    	//menuItem.addActionListener(new MenuActionListener("export txt",menuQueue));
    	menuItem.addActionListener(new ExportLogActionListener(this,outputLog,".txt"));
    	menu.add(menuItem);
    	
    	menuItem = new JMenuItem("Export as .rtf");
    	menuItem.setMnemonic(KeyEvent.VK_B);
    	menuItem.addActionListener(new ExportLogActionListener(this,outputLog,".rtf"));
    	menu.add(menuItem);
    	
    	menuItem = new JMenuItem("Export as PDF");
    	menuItem.setMnemonic(KeyEvent.VK_B);
    	menuItem.addActionListener(new ExportLogActionListener(this,outputLog,".pdf"));
    	menu.add(menuItem);
    	
    	menuItem = new JMenuItem("Export to Excel");
    	menuItem.setMnemonic(KeyEvent.VK_B);
    	menuItem.addActionListener(new ExportLogActionListener(this,outputLog,".xls"));
    	menu.add(menuItem);
    	
    	menu = new JMenu("Help");
    	menu.setMnemonic(KeyEvent.VK_N);
    	menuBar.add(menu);
    	
    	menuItem = new JMenuItem("About");
    	menuItem.setMnemonic(KeyEvent.VK_B);
    	menuItem.addActionListener(new MenuActionListener("about",menuQueue));
    	menu.add(menuItem);
    	
    	menuItem = new JMenuItem("View Help");
    	menuItem.setMnemonic(KeyEvent.VK_B);
    	menuItem.addActionListener(new MenuActionListener("view help",menuQueue));
    	menu.add(menuItem);

    	this.setJMenuBar(menuBar);

    }
    
    
    /**
     * necessary to run from command line
     * @param args unused
     * @throws InterruptedException for latch.await()
     */
    public static void main(final String[] args) throws InterruptedException {
    	final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(new Runnable() {
        	@Override
            public void run() {
        		new JFXPanel(); // initializes JavaFX environment
        		latch.countDown();
        		
                LoggingGUI main = new LoggingGUI();
                main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                main.requestFocus();
                main.setVisible(true);
            }
        });
        latch.await();
    }
}
