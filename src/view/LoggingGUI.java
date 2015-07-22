package view;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

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
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class LoggingGUI extends JFrame {
    private static final long serialVersionUID = 1L; //required
    
    private final JTextField commentFieldTxt;
    //private final JTextField audioSourceTxt;
      
    private final JButton enterTextBtn;
    private final JButton playpauseBtn;
    //private final JButton enterAudioBtn;
    private final JButton rewindBtn;
    private final JButton fastforwardBtn;
    private final JButton volumeUpBtn;
    private final JButton volumeDownBtn;
    private final JButton exportBtn;
    private final JButton clearLogBtn;
    
    private Container contentPane;
    
    private final JPanel userPanel; //contains the user interface
    
    private final JLabel currentAudioSource;
    private final JLabel timeStamp;
    
    private final JTextPane outputLog;
    
    private final JScrollPane displayScrollPane;
    
    //private final JSlider slider;
    private final JProgressBar audioProgressBar;
    
    private final BlockingQueue<String[]> audioQueue;
    private final BlockingQueue<String> timeQueue;
    private final BlockingQueue<String> outputQueue;
    private final BlockingQueue<String[]> menuQueue;
    private final BlockingQueue<String[]> performSaveQueue;
    
    private final OutputLogDisplay outputLogDisplay;
    private final SoundPlayerFX player;
    private final MenuController menuController;
    
    private final JRadioButton highlightBtn;
    
    //menu bar
	private JMenuBar menuBar;
	private JMenu menu, submenu;
	private JMenuItem menuItem;
	//private JRadioButtonMenuItem rbMenuItem;
	//private JCheckBoxMenuItem cbMenuItem;
    
    private AtomicInteger time; //holds a rounded time stamp, as I don't feel its necessary to have precision greater than a second
    
    public LoggingGUI() throws IOException {

    	//Configure the GUI elements
        commentFieldTxt = new JTextField();
        commentFieldTxt.setName("commentField");
        commentFieldTxt.setText("");
        commentFieldTxt.setMaximumSize(new Dimension(Integer.MAX_VALUE, commentFieldTxt.getPreferredSize().height));
        /*
        audioSourceTxt = new JTextField();
        audioSourceTxt.setName("audioSource");
        audioSourceTxt.setText("");
        audioSourceTxt.setMaximumSize(new Dimension(Integer.MAX_VALUE, audioSourceTxt.getPreferredSize().height));
        */
        enterTextBtn = new JButton();
        enterTextBtn.setName("enterText");
        enterTextBtn.setText("Enter Comment");
        enterTextBtn.setMaximumSize(new Dimension(80,enterTextBtn.getSize().height));
        
        playpauseBtn = new JButton();
        playpauseBtn.setName("playpause");
        playpauseBtn.setText("Play");
        playpauseBtn.setMinimumSize(new Dimension(80,enterTextBtn.getSize().height));
        /*
        enterAudioBtn = new JButton();
        enterAudioBtn.setName("enterAudio");
        enterAudioBtn.setText("Enter Path");
        enterAudioBtn.setMinimumSize(new Dimension(80,enterTextBtn.getSize().height));
        */
        rewindBtn = new JButton();
        rewindBtn.setName("rewind");
        rewindBtn.setText("<<");
        rewindBtn.setMaximumSize(new Dimension(30,rewindBtn.getSize().height));
        
        fastforwardBtn = new JButton();
        fastforwardBtn.setName("fastforward");
        fastforwardBtn.setText(">>");
        fastforwardBtn.setMaximumSize(new Dimension(30,fastforwardBtn.getSize().height));
        
        volumeUpBtn = new JButton();
        volumeUpBtn.setName("voluemUp");
        volumeUpBtn.setText("Volume Up");
        volumeUpBtn.setMaximumSize(new Dimension(30,volumeUpBtn.getSize().height));
        
        volumeDownBtn = new JButton();
        volumeDownBtn.setName("voluemDown");
        volumeDownBtn.setText("Volume Down");
        volumeDownBtn.setMaximumSize(new Dimension(30,volumeDownBtn.getSize().height));
        
        exportBtn = new JButton();
        exportBtn.setName("export");
        exportBtn.setText("Export log");
        
        clearLogBtn = new JButton();
        clearLogBtn.setName("clearLog");
        clearLogBtn.setText("Clear");
        
        highlightBtn= new JRadioButton();
        highlightBtn.setName("highlightBtn");
        highlightBtn.setText("Highlight");
        
        currentAudioSource = new JLabel();
        currentAudioSource.setName("currentAudioSource");
        currentAudioSource.setText("File: None");
        
        Font fixedWidthFont = new Font("Courier", Font.PLAIN, 14);
        timeStamp = new JLabel();
        timeStamp.setName("timeStamp");
        timeStamp.setFont(fixedWidthFont);
        timeStamp.setText("      0:00 / 0:00"); //TODO: space this somehow else, so it doesn't jump around
        
        time = new AtomicInteger();
        time.set(0);
        
        this.audioProgressBar = new JProgressBar();
        this.audioProgressBar.setName("audioProgressBar");
        
        audioQueue = new LinkedBlockingQueue<String[]>();
        outputQueue = new LinkedBlockingQueue<String>();
        menuQueue = new LinkedBlockingQueue<String[]>(); //TODO: I don't think menuQueue does anythings
        performSaveQueue = new LinkedBlockingQueue<String[]>();
        player = new SoundPlayerFX(audioQueue, timeStamp, currentAudioSource, time, playpauseBtn, 
        		performSaveQueue,this,audioProgressBar);
        timeQueue = new LinkedBlockingQueue<String>();
        
        outputLog = new JTextPane();
        outputLog.setName("outputField");
        outputLog.setText("");
        outputLog.addHyperlinkListener(new TextPaneHyperLinkListener(audioQueue));
        outputLog.setEditable(false);
        outputLog.setFont(fixedWidthFont);
	    //HTMLDocument doc = new HTMLDocument();
	    outputLog.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
	    //outputLog.setDocument(doc);
	    //outputLog.setText("<a href='#'>C</a>");
	    
        this.outputLogDisplay = new OutputLogDisplay(outputQueue, commentFieldTxt, outputLog, time);
        this.menuController = new MenuController(menuQueue,this,audioQueue);
        
        contentPane = getContentPane();
        userPanel = new JPanel();
        
        //displayPanel = new DisplayBox(); //this is the old version
        displayScrollPane = new JScrollPane(outputLog);
        
        //Configure slider
        int initialTime = 0;
        int finalTime = 100; //TODO: get this somewhere
        int startTime = 0;
        
        //TODO: re-enable slider once it actually does something
        //this.slider = new JSlider(JSlider.HORIZONTAL,initialTime,finalTime,startTime);
        //SetupUtils.setupSlider(slider, initialTime, finalTime, startTime, 4, 5);
        
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
        playpauseBtn.addActionListener(new PauseActionListener(audioQueue));
        
        //SoundActionListener soundAction = new SoundActionListener(audioSourceTxt, audioQueue);
        //enterAudioBtn.addActionListener(soundAction);
        //audioSourceTxt.addActionListener(soundAction);
        
        AddToOutputQueue enterAction = new AddToOutputQueue(outputQueue,"enter");
        enterTextBtn.addActionListener(enterAction);
        commentFieldTxt.addActionListener(enterAction);
        
        rewindBtn.addActionListener(new AudioControlActionListener(audioQueue,"rewind"));
        fastforwardBtn.addActionListener(new AudioControlActionListener(audioQueue,"fastforward"));
        
        volumeUpBtn.addActionListener(new AudioControlActionListener(audioQueue,"volume","up"));
        volumeDownBtn.addActionListener(new AudioControlActionListener(audioQueue,"volume","down"));
        
        clearLogBtn.addActionListener(new AddToOutputQueue(outputQueue,"clear"));
        highlightBtn.addActionListener(new AddToOutputQueue(outputQueue, "toggle highlight"));
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
            	    .addComponent(commentFieldTxt)
                    .addComponent(enterTextBtn)
                )
                /*.addGroup(userLayout.createSequentialGroup()
                	.addComponent(audioSourceTxt)
                	.addComponent(enterAudioBtn)
                )*/
                .addGroup(userLayout.createSequentialGroup()
                	.addComponent(rewindBtn)
                	.addComponent(playpauseBtn)
                	.addComponent(fastforwardBtn)
                	.addComponent(volumeDownBtn)
                	.addComponent(volumeUpBtn)
                	.addComponent(highlightBtn)
                )
                //.addComponent(slider)
                .addComponent(audioProgressBar)
                .addGroup(userLayout.createSequentialGroup()
                	.addComponent(clearLogBtn)
                	.addComponent(currentAudioSource)
                	.addComponent(timeStamp)
                )
        );
        
        userLayout.setVerticalGroup(
            userLayout.createSequentialGroup()
                .addGroup(userLayout.createParallelGroup()
                	.addComponent(commentFieldTxt)
                    .addComponent(enterTextBtn)
                )/*
                .addGroup(userLayout.createParallelGroup()
                  	.addComponent(audioSourceTxt)
                  	//.addComponent(enterAudioBtn)
                )*/
                .addGroup(userLayout.createParallelGroup()
                	.addComponent(rewindBtn)
                	.addComponent(playpauseBtn)
                	.addComponent(fastforwardBtn)
                	.addComponent(volumeDownBtn)
                	.addComponent(volumeUpBtn)
                	.addComponent(highlightBtn)
                )
                //.addComponent(slider)
                .addComponent(audioProgressBar)
                .addGroup(userLayout.createParallelGroup()
                	.addComponent(clearLogBtn)
                	.addComponent(currentAudioSource)
                	.addComponent(timeStamp)
                )
        );
        
        ScrollPaneLayout scrollPaneLayout = new ScrollPaneLayout();
        displayScrollPane.setLayout(scrollPaneLayout);
        displayScrollPane.setPreferredSize(new Dimension(750,400));
        
        //Configure the layout specifications for both panels
        userPanel.setPreferredSize(new Dimension(750,200));
        
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
    	
    	menuItem = new JMenuItem("Open Project", KeyEvent.VK_T);
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
    	
    	//////////// Export Menu /////////////
    	menu = new JMenu("Export");
    	menu.setMnemonic(KeyEvent.VK_N);
    	menuBar.add(menu);
    	
    	menuItem = new JMenuItem("Export as .txt");
    	menuItem.setMnemonic(KeyEvent.VK_B);
    	//menuItem.addActionListener(new MenuActionListener("export txt",menuQueue));
    	menuItem.addActionListener(new ExportLogActionListener(this,outputLog,".txt"));
    	menu.add(menuItem);
    	/*
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
    	*/
    	//////////// Options Menu /////////////
    	menu = new JMenu("Audio");
    	menu.setMnemonic(KeyEvent.VK_N);
    	menuBar.add(menu);
    	
    	menuItem = new JMenuItem("Open Audio");
    	menuItem.setMnemonic(KeyEvent.VK_B);
    	menuItem.addActionListener(new MenuActionListener("open audio",menuQueue));
    	menu.add(menuItem);
    	
    	menuItem = new JMenuItem("Playback Rate");
    	menuItem.setMnemonic(KeyEvent.VK_B);
    	menuItem.addActionListener(new MenuActionListener("playback rate",menuQueue));
    	menu.add(menuItem);
    	
    	menuItem = new JMenuItem("Rewind Gain");
    	menuItem.setMnemonic(KeyEvent.VK_B);
    	menuItem.addActionListener(new MenuActionListener("rewind gain",menuQueue));
    	menu.add(menuItem);
    	
    	menuItem = new JMenuItem("Fast Forward Gain");
    	menuItem.setMnemonic(KeyEvent.VK_B);
    	menuItem.addActionListener(new MenuActionListener("fastforward gain",menuQueue));
    	menu.add(menuItem);
    	
    	//////////// Help Menu /////////////
    	menu = new JMenu("Help");
    	menuBar.add(menu);
    	
    	menuItem = new JMenuItem("About");
    	menuItem.addActionListener(new MenuActionListener("about",menuQueue));
    	menu.add(menuItem);
    	
    	menuItem = new JMenuItem("View Help");
    	menuItem.addActionListener(new MenuActionListener("view help",menuQueue));
    	menu.add(menuItem);
    	
    	menuItem = new JMenuItem("Known Bugs");
    	menuItem.addActionListener(new MenuActionListener("known bugs",menuQueue));
    	menu.add(menuItem);
    	
    	menuItem = new JCheckBoxMenuItem("Enable debug mode");
    	menuItem.setSelected(false);
    	menuItem.addActionListener(new MenuActionListener("debug",menuQueue));
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
        		
                LoggingGUI main = null;
                //TODO: get rid of try catch eventually
				try {
					main = new LoggingGUI();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                main.requestFocus();
                main.setVisible(true);
            }
        });
        latch.await();
    }
}
