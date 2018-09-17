package ct.af.utils;

import java.util.ArrayList;
import java.util.TreeMap;

public class DiameterUtils {
	public Object parser(Object message){
		TreeMap<String, Object> messageTree;
		Object dummyMessage = "";
		
		if(message instanceof String)
		{
			messageTree = new TreeMap<>();
			String strMessage = message.toString();	
			String tempMessage, FocusMessage;
			int startKeyPosition;
			int destKeyPosition;
			int startPosition;
			int destPosition;
			int startValuePosition;
			int destValuePosition;
			String key;
			String value;
			tempMessage = strMessage;
			tempMessage = tempMessage.trim();
			tempMessage = tempMessage.replace("\r", "").replace("\n", "");
			while(!tempMessage.isEmpty())
			{
				startPosition = tempMessage.indexOf("<")+1;
				destPosition = tempMessage.indexOf(">");
				FocusMessage = tempMessage.substring(startPosition, destPosition);
				FocusMessage = FocusMessage.replace("/", "");
				if(FocusMessage.contains("value="))
				{
					destKeyPosition = FocusMessage.indexOf("value=")-1;
					key = FocusMessage.substring(0, destKeyPosition);
					value = FocusMessage.split("\"")[1];
					messageTree.put(key, value);
					tempMessage = tempMessage.substring(destPosition+1, tempMessage.length());
				}else {
					key = FocusMessage;
					destPosition = tempMessage.indexOf("</"+FocusMessage+">")+3+FocusMessage.length();
					startValuePosition = tempMessage.indexOf("<"+FocusMessage+">")+2+FocusMessage.length();
					destValuePosition = tempMessage.indexOf("</"+FocusMessage+">");
					value = tempMessage.substring(startValuePosition, destValuePosition);
					if(messageTree.containsKey(key))
					{
						ArrayList<TreeMap<String, Object>> aryTree = new ArrayList<TreeMap<String, Object>>();
						aryTree.add((TreeMap<String, Object>) messageTree.get(key));
						aryTree.add((TreeMap<String, Object>) parser(value));
						messageTree.put(key, aryTree);
					}else {
						messageTree.put(key, parser(value));
					}
					tempMessage = tempMessage.substring(destPosition, tempMessage.length());
				}
			}
			dummyMessage = messageTree;
			return dummyMessage;
		}
		
		return dummyMessage;
	}
}
