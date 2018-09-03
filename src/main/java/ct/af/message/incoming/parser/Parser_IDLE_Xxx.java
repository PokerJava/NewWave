package ct.af.message.incoming.parser;


import java.io.IOException;
import java.awt.RenderingHints.Key;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;

import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.message.incoming.parameter.Param_IDLE_Xxx;
import ct.af.utils.GsonPool;
import ec02.af.abstracts.AbstractAF;
import ec02.data.interfaces.EquinoxRawData;




public class Parser_IDLE_Xxx {
	public Param_IDLE_Xxx doParser(AbstractAF abstractAF, EquinoxRawData eqxRawData, AFInstance afInstance, AFSubInstance afSubIns) {
        Param_IDLE_Xxx param = new Param_IDLE_Xxx();

		String rawCType = "text/xml";
        String rawDataMessage = "{ \"company\" : \"CT\","
        		+ "\"animal\" : [\"dog\",\"cat\"],"
        		+ "\"userName\" : {\"jojo\" : \"1234\","
        		+ "\"momo\" : \"4321\"},"
        		+ "\"resourceName\" : [{\"resourceA1\" : \"A1\","
        		+ "\"resourceA2\" : \"A2\"},"
        		+ "{\"resourceB1\" : \"B1\","
        		+ "\"resourceB2\" : \"B2\"}]  }";

     
        
        String xmlMessage1 = "<ERDData value="+"/api/v1/aaf/publicId.json?company=CT&name=nutchapol.thathaii@gmail.co.th&invoke=999999&mobile=0909767978" +"/>]]>";
       
        String xmlMessage2 = "<root>"
                               +"<A>CT</A>"
                               +"<B>"
                                  +"<element>dog</element>"
                                  +"<element>cat</element>"
                               +"</B>"
                               +"<C>"
                                  +"111<jojo>1234</jojo>"
                                  +"<momo>4321</momo>"
                               +"</C>"
                               +"<D>"
                               +"<element>"
                                    +"<resourceA1>A1</resourceA1>"
                                    +"<resourceA2>"
                                        +"<jojo>1234</jojo>"
                                        +" <momo>4321</momo>"
                                    +"</resourceA2>"
                               +"</element>"
                               +"<element>"
                                    +" <resourceB1>B1"
                                    	+ "<jojo>1234"
                                    	+ "<A><B></B></A></jojo>"
                                    	+ "<momo>3242</momo>"
                                    + "</resourceB1>"
                                    +" <resourceB2>B2</resourceB2>"
                              +"</element>"
                              +"</D>"
                             +"</root>";

        
        String rawDataMessage2 = "{ \"A\" : \"CT\","
        		+ "\"B\" : [\"dog\",\"cat\"],"
        		+ "\"C\" : {\"jojo\" : \"1234\","
        		+ "\"momo\" : \"4321\"},"
        		+ "\"D\" : [{\"resourceA1\" : \"A1\","
        		+ "\"resourceA2\" : {\"jojo\" : \"1234\"," + 
        		"\"momo\" : \"4321\"}},"
        		+ "{\"resourceB1\" : \"B1\","
        		+ "\"resourceB2\" : \"B2\"}]  }";
        
        if(rawCType.equals("text/plain")) 
        {
        	JsonParser jsonParser = new JsonParser();
    		JsonObject resourceOrderJsonObject = jsonParser.parse(rawDataMessage2).getAsJsonObject();
    		Gson gson = GsonPool.getGson();
//			HashMap<String, Object> resourceHashMap = gson.fromJson(resourceOrderJsonObject, HashMap.class);
			param = gson.fromJson(resourceOrderJsonObject, Param_IDLE_Xxx.class);
			GsonPool.pushGson(gson);

			if(param.getA() instanceof ArrayList || param.getA() instanceof LinkedTreeMap)
			{
				param.setA(param.getHashMap(param.getA()));
			}
			if(param.getB() instanceof ArrayList || param.getB() instanceof LinkedTreeMap)
			{
				param.setB(param.getHashMap(param.getB()));
			}
			if(param.getC() instanceof ArrayList || param.getC() instanceof LinkedTreeMap)
			{
				param.setC(param.getHashMap(param.getC()));
			}
			if(param.getD() instanceof ArrayList || param.getD() instanceof LinkedTreeMap)
			{
				param.setD(param.getHashMap(param.getD()));
			}

        } else if(rawCType.equals("text/xml")) {
        	HashMap<String, Object> resourceHashMap = (HashMap<String, Object>) xmlToHash(xmlMessage2);
        	Gson gson = GsonPool.getGson();
        	Object test = gson.toJson(resourceHashMap.get("root"));
        	GsonPool.pushGson(gson);
        	param = gson.fromJson((String) test, Param_IDLE_Xxx.class);
        	if(param.getA() instanceof ArrayList || param.getA() instanceof LinkedTreeMap)
			{
				param.setA(param.getHashMap(param.getA()));
			}
			if(param.getB() instanceof ArrayList || param.getB() instanceof LinkedTreeMap)
			{
				param.setB(param.getHashMap(param.getB()));
			}
			if(param.getC() instanceof ArrayList || param.getC() instanceof LinkedTreeMap)
			{
				param.setC(param.getHashMap(param.getC()));
			}
			if(param.getD() instanceof ArrayList || param.getD() instanceof LinkedTreeMap)
			{
				param.setD(param.getHashMap(param.getD()));
			}
            
//
//            Param_IDLE_Xxx paramIdle = new Param_IDLE_Xxx();
//            
//            String company = Param_IDLE_Xxx.getParameterValueFromUrl(xmlMessage1, "company");
//            String name = Param_IDLE_Xxx.getParameterValueFromUrl(xmlMessage1, "name");
//            String invoke = Param_IDLE_Xxx.getParameterValueFromUrl(xmlMessage1, "invoke");
//            String mobile = Param_IDLE_Xxx.getParameterValueFromUrl(xmlMessage1, "mobile");
//            
//            paramIdle.setCompany(company); 
//            paramIdle.setCompany(name); 
//            paramIdle.setCompany(company); 
//            paramIdle.setCompany(company); 
//            
//            DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
//           try {
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			Document doc = builder.parse("paser.xml");
//			NodeList personList = doc.getElementsByTagName("person");
//			for(int i=0; i<personList.getLength();i++){
//				Node p = personList.item(i);
//				if(p.getNodeType() ==Node.ELEMENT_NODE){
//					Element person =  (Element) p ;
//					String id = person.getAttribute("id");
//					NodeList nameList = person.getChildNodes();
//					
//					
//					for(int j=0; j<nameList.getLength();j++){
//						Node n = nameList.item(j);
//						if(n.getNodeType() ==Node.ELEMENT_NODE){
//							Element names = (Element) n;
//							System.out.println("person " + id + ":" + names.getTagName()+ "=" + names.getTextContent());
//						}
//				}
//			}
//			
//			
//			}
//			
//		} catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

        	
        } else if(rawCType.equals("Diameter")) {
        	
        } else if(rawCType.equals("Ldap")) {
        	
        } else {
        	return param;
        }
        
        
	
        return param;
	}
	public Object xmlToHash(Object xmlMessage2)
	{
		Object finalData;
//        if(!xmlData.startsWith("\"")&& !xmlData.endsWith("\""))
//        {
//        	xmlData = "\"" + xmlData + "\"";
//        }
//        else
//        {
//        	
//        }
//        if(xmlMessage2.contains("<A>")
//        		||xmlMessage2.contains("<B>")
//        		||xmlMessage2.contains("<D>")
//        		||xmlMessage2.contains("<C>"))
//        {
//            String xmlData;
//            int start = xmlMessage2.indexOf(">")+1;
//            int end = xmlMessage2.lastIndexOf("</");
//            xmlData = xmlMessage2.substring(start, end);
//        	StringBuilder str = new StringBuilder();
//        	if(xmlData.contains("<A>"))
//            {
//            	int startA = xmlData.indexOf("A>")+2;
//            	int endA = xmlData.indexOf("/A")-1;
//            	String A = xmlData.substring(startA, endA);
//            	str.append("\"A\" : " + "\"" + A + "\"@");
////            	System.out.println("A : " + str);
//            	
//            }
//            if(xmlData.contains("<B>"))
//            {
//            	int startB = xmlData.indexOf("B>")+2;
//            	int endB = xmlData.indexOf("/B")-1;
//            	String B = xmlData.substring(startB, endB);
//            	str.append("\"B\" : " + "\"" + B + "\"@");
////            	System.out.println("B : " + str);
//            }
//            if(xmlData.contains("<C>"))
//            {
//            	int startC = xmlData.indexOf("C>")+2;
//            	int endC = xmlData.indexOf("/C")-1;
//            	String C = xmlData.substring(startC, endC);
//            	str.append("\"C\" : " + "\"" + C + "\"@");
////            	System.out.println("C : " + str);
//            }
//            if(xmlData.contains("<D>"))
//            {
//            	int startD = xmlData.indexOf("D>")+2;
//            	int endD = xmlData.indexOf("/D")-1;
//            	String D = xmlData.substring(startD, endD);
//            	str.append("\"D\" : " + "\"" + D + "\"@");
////            	System.out.println("D : " + str);
//            }
//            
//            xmlParser("{" + str.toString() + "}");
//        }
		//TODO Validate T3 from Config
		if(xmlMessage2 instanceof String)
		{
			finalData = new Object();
			String strMessage = xmlMessage2.toString();
			 if(strMessage.contains("<")|| strMessage.contains(">"))
		        {
				 	String key;
				 	String data;
				 	HashMap<String, Object> dataHash = new HashMap<>();
				 	String tempMsg;
		        	int startPos;
		        	int endPos;
		        	tempMsg = strMessage;
		        	
		        	key = strMessage.substring(strMessage.indexOf("<")+1, strMessage.indexOf(">"));
		        	
		        	startPos = strMessage.indexOf("<" + key + ">")+key.length()+2;
		        	endPos = strMessage.indexOf("</" + key + ">");	  
		        	data = strMessage.substring(startPos, endPos);
		        	
		        	dataHash.put(key, data);
		        	finalData = xmlToHash(dataHash);

		        }
	        else if(strMessage.contains("<element>"))
	        {
	        	strMessage = strMessage.replaceAll("</element><element>", "\",\"");
	        	strMessage = strMessage.replaceAll("<element>", "[\"");
	        	strMessage = strMessage.replace("\"[", "[");
	        	strMessage = strMessage.replaceAll("</element>", "\"]");
	        	strMessage = strMessage.replace("]\"", "]");
	        	String[] textSum = strMessage.split("@");
	        	StringBuilder str = new StringBuilder();
	        	int first = 1;
	        	int second = countText(strMessage, "@");
	        	for(String t : textSum)
	        	{
	        		if(first!=second)
	        		{
	        			str.append(t + ",");
	        			++first;
	        		}
	        		else
	        		{
	        			str.append(t);
	        		}
	        		
	        	}
	        	System.out.println(str);
	        	xmlToHash(str.toString());
	        }
		}
		else if(xmlMessage2 instanceof HashMap)
		{
			finalData = xmlMessage2;
			HashMap<String, Object> dataHash = (HashMap<String, Object>) xmlMessage2;
//			HashMap<String, Object> dataHash1 = new HashMap<>();
			for(String key : dataHash.keySet())
			{
				HashMap<String, Object> dataHash1 = new HashMap<>();
				if(dataHash.get(key) instanceof String)
				{
					String strMessage = dataHash.get(key).toString();
					if(strMessage.contains("<")|| strMessage.contains(">"))
			        {
						if(strMessage.contains("<element>"))
						{
							strMessage = strMessage.replaceAll("</element><element>", ",");
							strMessage = strMessage.replaceAll("<element>", "[");
							strMessage = strMessage.replaceAll("</element>", "]");
						}
		//	        	String[] key = {};
		//	        	String[] data = {};
					 	String key1 = "";
					 	String data = "";
					 	String tempMsg;
			        	int startPos;
			        	int endPos;
			        	int loop = countText(strMessage, "<")/2;
			        	tempMsg = strMessage;
			        	while(!tempMsg.isEmpty())
			        	{
			        		if(tempMsg.contains("<")||tempMsg.contains(">"))
			        		{
			        		
			        		key1 = tempMsg.substring(tempMsg.indexOf("<")+1, tempMsg.indexOf(">"));
				        	
				        	startPos = tempMsg.indexOf("<" + key1 + ">")+key1.length()+2;
				        	endPos = tempMsg.indexOf("</" + key1 + ">");	  
				        	data = tempMsg.substring(startPos, endPos);
				        	tempMsg = tempMsg.substring(endPos+key1.length()+3);
				        	dataHash1.put(key1, data);
			        		}else if(tempMsg.contains("]")) {
			        			tempMsg = "";
			        		}
			        		else {
			        			dataHash.put(key, dataHash1);
			        			return xmlMessage2;
			        		}
			        	}
			        	
			        	
			        	
//			        	finalData = xmlParser(dataHash.get(key));
			        	dataHash.put(key, dataHash1);
						xmlToHash(dataHash.get(key));
			        }
				}
				else if(dataHash.get(key) instanceof HashMap)
				{
					finalData = xmlToHash(dataHash.get(key));
				}
			}
		}else {
			finalData = xmlMessage2;
		}

//        xmlMessage2 = finalData;	
		return finalData;
        }
        
	public Object hashToJson(Object data, JsonObject jsonData)
	{
		if(data instanceof HashMap)
		{
			HashMap<String, Object> dataHash = (HashMap<String, Object>) data;
			for(String key : dataHash.keySet())
			{
				if(dataHash.get(key) instanceof String)
				{
					jsonData.addProperty(key, (String) dataHash.get(key));
				}else {
//					jsonData.add(key, (JsonElement) hashToJson(dataHash.get(key), jsonData));
					hashToJson(dataHash.get(key), jsonData);
				}
			}
//			return jsonData;
		}else {
			jsonData = new JsonObject();
		}
		return jsonData;
	}
	public int countText(String text, String textForCount)
	{
		int count = 0;
		for(int i=0;i<text.length();i++)
		{
			if(text.substring(i, i+1).equals(textForCount))
			{
				++count;
			}
		}
		
		return count;
	}
}
