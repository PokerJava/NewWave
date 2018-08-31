package ct.af.message.incoming.parser;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
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
			HashMap<String, Object> resourceHashMap = gson.fromJson(resourceOrderJsonObject, HashMap.class);
    		GsonPool.pushGson(gson);
			param = gson.fromJson(resourceOrderJsonObject, Param_IDLE_Xxx.class);
//			param.getHashMap()
			
//			resourceHashMap = paramXxx.getHashMap(paramXxx.getD());

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
            HashMap<String, String> rawData = new HashMap<String, String>();
            		JsonParser jsonParser = new JsonParser();
          			String data = xmlMessage.substring(xmlMessage.indexOf("<ERDData"), xmlMessage.length()).trim();
        			JsonObject resourceOrderJsonObject = jsonParser.parse(xmlMessage).getAsJsonObject();
            		Gson gson = GsonPool.getGson();
        
        	
        } else if(rawCType.equals("Diameter")) {
        	
        } else if(rawCType.equals("Ldap")) {
        	
        } else {
        	return param;
        }
        
        
	
        return param;
	}
}
