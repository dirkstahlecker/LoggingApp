package model;

import java.net.URL;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class TextPaneHyperLinkListener implements HyperlinkListener  {

    public void hyperlinkUpdate(HyperlinkEvent event) {
    	if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
    		//JEditorPane pane = (JEditorPane)event.getSource();

    		URL url = event.getURL();
    		System.out.println("HTML LINK IS WORKING");
    	}
    }
}