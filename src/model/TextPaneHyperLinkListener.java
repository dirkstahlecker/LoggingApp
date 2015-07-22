package model;

import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class TextPaneHyperLinkListener implements HyperlinkListener  {
	
	private final BlockingQueue<String[]> audioQueue;
	
	public TextPaneHyperLinkListener(BlockingQueue<String[]> audioQueue) {
		this.audioQueue = audioQueue;
	}

    public void hyperlinkUpdate(HyperlinkEvent event) {
    	if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
    		URL url = event.getURL();
    		String urlStr = url.toString();
    		System.out.println(urlStr);
    		//System.out.println(urlStr.matches("http://([0-9]+)"));
    				
    		//Pattern pattern = Pattern.compile("http://([0-9]+)");
    		//Matcher matcher = pattern.matcher(urlStr);
    		
    		String num = urlStr.substring(7, urlStr.length());
    		
    		audioQueue.add(new String[]{"seek",num});
    	}
    }
}