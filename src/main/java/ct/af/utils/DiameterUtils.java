package ct.af.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
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
		PgwStandardPlattern SessionId;
		@Element(name="Auth-Application-Id")
		PgwStandardPlattern authApplicationId;
		@Element(name="Origin-Host")
		PgwStandardPlattern originHost;
		@Element(name="Origin-Realm")
		PgwStandardPlattern originRealm;
		@Element(name="CC-Request-Number")
		PgwStandardPlattern ccRequestType; 
		@Element(name="CC-Request-Type")
		PgwStandardPlattern ccRequestNumber;
		@Element(name="Destination-Realm")	
		PgwStandardPlattern destinationRealm;	
		@Element(name="Destination-Host")
		PgwStandardPlattern destinationHost;
		@Element(name="Origin-State-Id")
		PgwStandardPlattern originStateId;
		@Element(name="Network-Request-Support")
		PgwStandardPlattern networkRequestSupport;
		@Element(name="Bearer-Identifier")
		PgwStandardPlattern bearerIdentifier;	
		@Element(name="Bearer-Operation")
		PgwStandardPlattern bearerOperation;
		@Element(name="Framed-IP-Address")
		PgwStandardPlattern framedIpAddress;
		@Element(name="IP-CAN-Type")
		PgwStandardPlattern ipCanType;
		@Element(name="RAT-Type")
		PgwStandardPlattern ratType;	
		@Element(name="TGPP-SGSN-MCC-MNC")
		PgwStandardPlattern tgppSgsnMccMnc;
		@Element(name="TGPP-SGSN-Address")
		PgwStandardPlattern tgppSgsnAddress;
		@Element(name="TGPP-User-Location-Info")
		PgwStandardPlattern tgppUserLocationInfo;
		@Element(name="TGPP-MS-TimeZone")
		PgwStandardPlattern tgppMsTimeZone;
		@Element(name="Called-Station-Id")
		PgwStandardPlattern calledStationId;
		@Element(name="Bearer-Usage")
		PgwStandardPlattern bearerUsage;
		@Element(name="Offline")
		PgwStandardPlattern offline;
		@Element(name="Access-Network-Charging-Address")
		PgwStandardPlattern accessNetworkChargingAddress;
		@Element(name="Online")
		PgwStandardPlattern online;
		
//		@Element(name="Subscription-Id")
//		SubscriptionId subscriptionId;
		
//		@ElementList(name="Subcription-Id")
//		SubscriptionId subscriptionId;
		@Element(name="Subscription-Id")
		SubscriptionId subscriptionId;

		

		
//		@Path("Subscription-Id/Subscription-Id-Type/@value")
//		@Element
//		String test;
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
	

	
	static class PgwStandardPlattern {
		 @Attribute(required = true)
		 private String value;
	}
	static class SubscriptionId{
		@Element(name="Subscription-Id-Type")
		PgwStandardPlattern subscriptionIdType;
		@Element(name="Subscription-Id-Data")
		PgwStandardPlattern subscriptionIdData;
	}
	static class ERDData{

		
		
		 @Element
		PgwStandardPlattern SessionId;
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
	
	
	public String pokXMLArray(String xmlMessage) {
		String tempMessage = xmlMessage;
		String headData = "";
		while(!tempMessage.isEmpty()) 
		{
			if(tempMessage.contains("<")||tempMessage.contains(">"))
			{
				tempMessage = tempMessage.trim();
				int startFirstPosition = tempMessage.indexOf("<")+1;
				int destinationFirstPosition = tempMessage.indexOf(">");
				String data = tempMessage.substring(startFirstPosition, destinationFirstPosition);
				if(data.equals(headData))
				{
					int length = countText(xmlMessage, data)/2;
					int startInsert = xmlMessage.indexOf("<"+data);
					int endInsert = xmlMessage.lastIndexOf("</"+data);
					
					StringBuilder text = new StringBuilder();
					String first = xmlMessage.substring(0, startInsert);
					String mid = xmlMessage.substring(startInsert, endInsert);
					String end = xmlMessage.substring(endInsert, xmlMessage.length());
					text.append(first);
					text.append("<array length=\""+length+"\">");
					text.append(mid);
					text.append("</array>");
					text.append(end);
					tempMessage = text.toString();
				}
				tempMessage = tempMessage.substring(tempMessage.indexOf("</"+data+">")+3+data.length());
				headData = data;
				
			}
		}
		
		return xmlMessage;
	}
	public int countText(String text, String textForCount) {
		int count = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.substring(i, i + 1).equals(textForCount)) {
				++count;
			}
		}

		return count;
	}
}
