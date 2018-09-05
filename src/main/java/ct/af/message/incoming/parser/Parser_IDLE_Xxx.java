package ct.af.message.incoming.parser;

import java.util.ArrayList;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_Xxx;
import ct.af.utils.GsonPool;
import ec02.af.abstracts.AbstractAF;
import ec02.data.interfaces.EquinoxRawData;




public class Parser_IDLE_Xxx {
	public Param_IDLE_Xxx doParser(AbstractAF abstractAF, EquinoxRawData eqxRawData, AFInstance afInstance, AFSubInstance afSubIns) {
        Param_IDLE_Xxx param = new Param_IDLE_Xxx();
        Gson gson = GsonPool.getGson();

		String rawCType = "text/xml";
        String rawPlainMessage = "{ \"company\" : \"CT\","
        		+ "\"animal\" : [\"dog\",\"cat\"],"
        		+ "\"userName\" : {\"jojo\" : \"1234\","
        		+ "\"momo\" : \"4321\"},"
        		+ "\"resourceName\" : [{\"resourceA1\" : \"A1\","
        		+ "\"resourceA2\" : \"A2\"},"
        		+ "{\"resourceB1\" : \"B1\","
        		+ "\"resourceB2\" : \"B2\"}]  }";

        String rawPlainMessage2 = "{ \"A\" : \"CT\","
        		+ "\"B\" : [\"dog\",\"cat\"],"
        		+ "\"C\" : {\"jojo\" : \"1234\","
        		+ "\"momo\" : \"4321\"},"
        		+ "\"D\" : [{\"resourceA1\" : \"A1\","
        		+ "\"resourceA2\" : {\"jojo\" : \"1234\"," + 
        		"\"momo\" : \"4321\"}},"
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
        
        String rawDiameterMessage = "<![CDATA[" + 
        		"<Session-Id value=\"session_gx_0\"/>" + 
        		"<Origin-Host value=\"pcrf_p3\"/>" + 
        		"<Origin-Realm value=\"pcrf_p3.sand.ais.co.th\"/>" + 
        		"<Destination-Realm value=\"dgw_gx.sand.ais.co.th\"/>" + 
        		"<Destination-Host value=\"dgw_gx\"/>" + 
        		"<CC-Request-Type value=\"1\"/>" + 
        		"<CC-Request-Number value=\"0\"/>" + 
        		"<Result-Code value=\"2001\"/>" + 
        		"<Auth-Application-Id value=\"16777238\"/>" + 
        		"<CC-Session-Failover value=\"1\"/>" + 
        		"<Service-Information>" + 
        		"    <PS-Information>" + 
        		"        <PS-Furnish-Charging-Information>" + 
        		"            <PS-Free-Format-Data value=\"0X22\"/>" + 
        		"            <PS-Append-Free-Format-Data value=\"1\"/>" + 
        		"        </PS-Furnish-Charging-Information>" + 
        		"    </PS-Information>" + 
        		"</Service-Information>]]>";

        if(rawCType.equals("text/plain")) 
        {	
        	JsonParser jsonParser = new JsonParser();
    		JsonObject resourceOrderJsonObject = jsonParser.parse(rawPlainMessage2).getAsJsonObject();	
    		HashMap<String, Object> resourceHash = gson.fromJson(resourceOrderJsonObject, HashMap.class);
    		GsonPool.pushGson(gson);
    		
    		if(validateParam(resourceHash))
    		{
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
    		}
        } 
        else if(rawCType.equals("text/xml")) 
        {
        	String XmlHeader = getHeaderXML(xmlMessage2);
        	HashMap<String, Object> resourceHashMap = (HashMap<String, Object>) xmlToHash(xmlMessage2);
        	Object jsonFormat = gson.toJson(resourceHashMap.get(XmlHeader));
        	GsonPool.pushGson(gson);
        	
        	if(validateParam((HashMap<String, Object>) resourceHashMap.get("root")))
        	{
            	param = gson.fromJson((String) jsonFormat, Param_IDLE_Xxx.class);
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
//          diameterToHash(rawDiameterMessage);
        } else if(rawCType.equals("Ldap")) {
        	
        } else {
        	return param;
        }
        
        
	
        return param;
	}
	public Object diameterToHash(Object message)
	{
		Object finalData = new Object();
		if(message instanceof String)
		{
			finalData = new Object();
			String dataStr = (String)message;
			dataStr = dataStr.replace("<![CDATA[", "");
			dataStr = dataStr.replace("]]>", "");
			
			String[] segment = dataStr.split("/><");
			if(dataStr.contains("<")|| dataStr.contains(">"))
	        {
			 	String key;
			 	String data;
			 	HashMap<String, Object> dataHash = new HashMap<>();
			 	String tempMsg = dataStr;
	        	int startPos;
	        	int endPos;
	     
	        	key = dataStr.substring(dataStr.indexOf("<")+1, dataStr.indexOf("value")).trim();     	
	        	startPos = dataStr.indexOf("<" + key + ">")+key.length()+2;
	        	endPos = dataStr.indexOf("</" + key + ">");	  
	        	data = dataStr.substring(startPos, endPos);
	        	
	        	dataHash.put(key, data);
	        	finalData = xmlToHash(dataHash);

	        }
		}
		
		return finalData;
	}
	public Object xmlToHash(Object xmlMessage2)
	{
		Object finalData;

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
				 	String tempMsg = strMessage;
		        	int startPos;
		        	int endPos;
		     
		        	key = strMessage.substring(strMessage.indexOf("<")+1, strMessage.indexOf(">"));     	
		        	startPos = strMessage.indexOf("\"")+1;
		        	endPos = strMessage.indexOf("\"/");	  
		        	data = strMessage.substring(startPos, endPos);
		        	
		        	dataHash.put(key, data);
		        	finalData = xmlToHash(dataHash);

		        }
		}
		else if(xmlMessage2 instanceof HashMap)
		{
			finalData = xmlMessage2;
			HashMap<String, Object> dataHash = (HashMap<String, Object>) xmlMessage2;
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
					hashToJson(dataHash.get(key), jsonData);
				}
			}
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
	public boolean validateParam(HashMap<String, Object> dataHash)
	{
		boolean isValidate = false;
		if(dataHash.containsKey("A")&& dataHash.containsKey("B") && dataHash.containsKey("C") && dataHash.containsKey("D"))
		{
			isValidate = true;
		}
		
		return isValidate;
	}
	public boolean validateFormatXML(String xml)
	{
		boolean isValid = false;
		int tagFirst = countText(xml, "<");
		int tagSecond = countText(xml, ">");
		int tagEnd = 0;
		
		if(tagFirst==tagSecond)
		{
			isValid = true;
		}
		return isValid;
	}
	public String getHeaderXML(String xml)
	{
		int headStartPosition;
		int headEndPosition;
		String headFirst;
		String headSecond;
		String header = "";
		
		headStartPosition = xml.indexOf("<")+1;
		headEndPosition = xml.indexOf(">");
		headFirst = xml.substring(headStartPosition, headEndPosition);
		
		headStartPosition = xml.lastIndexOf("</")+2;
		headEndPosition = xml.lastIndexOf(">");
		headSecond = xml.substring(headStartPosition, headEndPosition);
		
		if(headFirst.equals(headSecond))
		{
			header = headFirst;
		}
		
		return header;
	}
}
