package ct.af.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import ct.af.utils.XMLTools.ERDContainer;
import ct.af.utils.XMLTools.ERDData;
import ct.af.utils.XMLTools.Elements;
import ct.af.utils.XMLTools.ElementsC;
import ec02.af.utils.AFLog;

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
	
	public Object getparserObject(String message) {
		
			Object parsedObject = null;
			HashMap<String, Object> paramHash = new HashMap<>();
			Serializer serializer = ParserPool.getPersister();

			try {
				ERDContainer container = serializer.read(ERDContainer.class, "<xml>" + message + "</xml>" , true);
				parsedObject = serializer.read(ERDContainer.class, "<xml>" + message + "</xml>" , false);

			} catch (Exception e) {
			
					AFLog.e("[Exception] Error invalid ERDContainer");
					AFLog.e(e.getMessage());
					AFLog.d(e);
				}
			
			ParserPool.pushPersister((Persister) serializer);
			return paramHash;		
	}
	
	static class ERDContainer {
		@Element(name="Session-Id")
		ElementsSess SessionId;
		@Element(name="Auth-Application-Id")
		ElementsSess authApplicationId;
//		@Element
//		private String originHost;
//		@Element
//		private String originRealm;
//		@Element
//		private String ccRequestType; 
//		@Element
//		private String ccRequestNumber;
//		@Element	
//		private String destinationRealm;	
//		@Element
//		private String destinationHost;
//		@Element
//		private String originStateId;
//		@Element
//		private String networkRequestSupport;
//		@Element
//		private String bearerIdentifier;	
//		@Element
//		private String bearerOperation;
//		@Element
//		private String framedIpAddress;
//		@Element
//		private String ipCanType;
//		@Element
//		private String ratType;	
//		@Element
//		private String tgppSgsnMccMnc;
//		@Element
//		private String tgppSgsnAddress;
//		@Element
//		private String tgppUserLocationInfo;
//		@Element
//		private String tgppMsTimeZone;
//		@Element
//		private String calledStationId;
//		@Element
//		private String bearerUsage;
//		@Element
//		private String offline;
//		@Element
//		private String accessNetworkChargingAddress;
//		@Element
//		private String online;
//		@Element(name = "ERDData", required = false)
//		ERDData data;// = new ERDData();
//
//		
//		public ERDData getData() {
//			return data;
//		}
//
//		public void setData(ERDData data) {
//			this.data = data;
//		}

	}
	

	
	static class ElementsSess {
		 @Attribute(required = false)
		 private String value;
	}
	static class ERDData{

		
		
		 @Element
		ElementsSess SessionId;
//		@Element
//		private String authApplicationId;
//		@Element
//		private String originHost;
//		@Element
//		private String originRealm;
//		@Element
//		private String ccRequestType; 
//		@Element
//		private String ccRequestNumber;
//		@Element	
//		private String destinationRealm;	
//		@Element
//		private String destinationHost;
//		@Element
//		private String originStateId;
//		@Element
//		private String networkRequestSupport;
//		@Element
//		private String bearerIdentifier;	
//		@Element
//		private String bearerOperation;
//		@Element
//		private String framedIpAddress;
//		@Element
//		private String ipCanType;
//		@Element
//		private String ratType;	
//		@Element
//		private String tgppSgsnMccMnc;
//		@Element
//		private String tgppSgsnAddress;
//		@Element
//		private String tgppUserLocationInfo;
//		@Element
//		private String tgppMsTimeZone;
//		@Element
//		private String calledStationId;
//		@Element
//		private String bearerUsage;
//		@Element
//		private String offline;
//		@Element
//		private String accessNetworkChargingAddress;
//		@Element
//		private String online;
	
	}
	
	
	
}
