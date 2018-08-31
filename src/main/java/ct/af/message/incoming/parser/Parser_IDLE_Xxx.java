package ct.af.message.incoming.parser;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.message.incoming.parameter.Param_IDLE_Xxx;
import ct.af.utils.GsonPool;
import ec02.af.abstracts.AbstractAF;
import ec02.data.interfaces.EquinoxRawData;
import phoebe.aaf.constants.rawdata.EnumMessage;
import phoebe.aaf.message.command.ChangePromotionRequestV2;
import phoebe.aaf.utils.GsonCenter;


public class Parser_IDLE_Xxx {
	public Param_IDLE_Xxx doParser(AbstractAF abstractAF, EquinoxRawData eqxRawData, AFInstance afInstance, AFSubInstance afSubIns) {
        Param_IDLE_Xxx param = new Param_IDLE_Xxx();
        
//        String rawDataMessage = eqxRawData.getRawDataAttribute("val");
        String rawDataMessageXml = "";
        String rawDataMessage = "{ \"company\" : \"CT\","
        		+ "\"animal\" : [\"dog\",\"cat\"],"
        		+ "\"userName\" : {\"jojo\" : \"1234\","
        		+ "\"momo\" : \"4321\"},"
        		+ "\"resourceName\" : [{\"resourceA1\" : \"A1\","
        		+ "\"resourceA2\" : \"A2\"},"
        		+ "{\"resourceB1\" : \"B1\","
        		+ "\"resourceB2\" : \"B2\"}]  }";
        
        String xml = "<ERD>"
			         +"<animal>"
			         +"<element>dog</element>"
			         +"<element>cat</element>"
			         +"</animal>"
			         +"<company>CT</company>"
			         +"<resourceName>"
			         +"<element>"
			         +"<resourceA1>A1</resourceA1>"
			         +"<resourceA2>A2</resourceA2>"
			         +"</element>"
			         +"<element>"
			         +"<resourceB1>B1</resourceB1>"
			         +"<resourceB2>B2</resourceB2>"
			         +"</element>"
			         +"</resourceName>"
			         +"<userName>"
			         +"<jojo>1234</jojo>"
			         +"<momo>4321</momo>"
			         +"</userName>"
			         +"</ERD>";
        
        String xmlMessage  = "<ERDData> value"
        			+"\""
        			+"{"  	         
        			+ "  \"sessionId\":\"564093493534958340\"," 
        			+ "  \"accessNum\":\"1775\","    
        			+ "  \"appName\":\"fb\","      
        			+ "  \"callBackUrl\":\"10.240.104.215:8443\","	            
        			+ "   \"submissionTime\":\"20150731091000\","	            
        			+ "   \"callBackUrl\":\"10.240.104.215:8443\","           
        			+ "   \"submissionTime\":\"150903111111\","        			           
        			+ "   \"partnerId\":\"30010\""
        			+ "}"
        			+ "\""
        			+ ">]]>";
        
        System.out.println(xmlMessage);
        			
        
        		//String rawCType = eqxRawData.getCType();
        		String rawCType = "text/xml";

        

        if(rawCType.equals("text/plain")) {
        	JsonParser jsonParser = new JsonParser();
    		JsonObject resourceOrderJsonObject = jsonParser.parse(rawDataMessage).getAsJsonObject();
    		Gson gson = GsonPool.getGson();
			HashMap<String, Object> resourceHashMap = gson.fromJson(resourceOrderJsonObject, HashMap.class);
    		GsonPool.pushGson(gson);
			
    		if(resourceHashMap.containsKey("A")) {
    			param.setA(resourceHashMap.get("A").toString());
    		}
    		if(resourceHashMap.containsKey("B")) {
    			ArrayList<String> multiValueIns = (ArrayList<String>)resourceHashMap.get("B");
    			param.setB(multiValueIns);
    		}
    		if(resourceHashMap.containsKey("C")) {
    			HashMap<String, Object> groupIns = gson.fromJson(resourceHashMap.get("C").toString(), HashMap.class);
    			param.setC(groupIns);
    		}
    		if(resourceHashMap.containsKey("D")) {
    			ArrayList<HashMap<String, Object>> multiGroupIns = gson.fromJson(resourceHashMap.get("D").toString(), ArrayList.class);
    			param.setD(multiGroupIns);
    		}
    	

    		
        } else if(rawCType.equals("text/xml")) {
            HashMap<String, String> rawData = new HashMap<String, String>();
        	 try {
        			String data = xmlMessage.substring(xmlMessage.indexOf("<ERDData"), xmlMessage.length()).trim();
        			   
        			changePromotionRequestV2= GsonCenter.getObjectFromGsonString(map.get(EnumMessage.ERDDATA.getKey()), ChangePromotionRequestV2.class);
                	String type = hashMap.getType();
                    String value = hashMap.getValue();
                    String uid = hashMap.getUid();
        			 
        			   data = data.substring(data.indexOf("value="), data.indexOf("/>")).trim();
        			   data = data.substring(data.indexOf("value=") + 7, data.length() - 1);
        			   rawData.put("ERDData",data);
        	       } catch (Exception e) {
        	       }
        	
        } else if(rawCType.equals("Diameter")) {
        	
        } else if(rawCType.equals("Ldap")) {
        	
        } else {
        	return param;
        }
        
        
	
        return param;
	}
}
