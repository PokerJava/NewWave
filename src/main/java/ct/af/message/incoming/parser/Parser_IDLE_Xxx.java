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
        
        String rawDataMessage2 = "{ \"A\" : \"CT\","
        		+ "\"B\" : [\"dog\",\"cat\"],"
        		+ "\"C\" : {\"jojo\" : \"1234\","
        		+ "\"momo\" : \"4321\"},"
        		+ "\"D\" : [{\"resourceA1\" : \"A1\","
        		+ "\"resourceA2\" : {\"jojo\" : \"1234\"," + 
        		"\"momo\" : \"4321\"}},"
        		+ "{\"resourceB1\" : \"B1\","
        		+ "\"resourceB2\" : \"B2\"}]  }";
        String rawCType = eqxRawData.getCType();        

        if(rawCType.equals("text/plain")) {
        	JsonParser jsonParser = new JsonParser();
    		JsonObject resourceOrderJsonObject = jsonParser.parse(rawDataMessage2).getAsJsonObject();
    		Gson gson = GsonPool.getGson();
			HashMap<String, Object> resourceHashMap = gson.fromJson(resourceOrderJsonObject, HashMap.class);
    		GsonPool.pushGson(gson);
			param = gson.fromJson(resourceOrderJsonObject, Param_IDLE_Xxx.class);
//			param.getHashMap()
			param.setD(resourceHashMap);
//			resourceHashMap = paramXxx.getHashMap(paramXxx.getD());


    		
        } else if(rawCType.equals("text/xml")) {
        	HashMap<String, String> rawData = new HashMap<String, String>();

        	       if(rawDataMessageXml.indexOf("<bookstore>") != -1)
        	       {
        	           String header = rawDataMessageXml.substring(0, rawDataMessageXml.indexOf("<bookstore>"));
        	           String[] headerVals = header.split("/>");

        	         
        	               for(String headerVal : headerVals) {
        	                   headerVal = headerVal.trim();
        	                   if("".equals(headerVal)) {
        	                       continue;
        	                   }
        	                   String key = headerVal.substring(headerVal.indexOf("name=") + 6, headerVal.indexOf("val") - 2);
        	                   String value = headerVal.substring(headerVal.indexOf("value=") + 7, headerVal.length() - 1);
        	                   rawData.put(key.toLowerCase(), value);
        	               }
        	       }
        	           
        } else if(rawCType.equals("Diameter")) {
        	
        } else if(rawCType.equals("Ldap")) {
        	
        } else {
        	return param;
        }
        
        
	
        return param;
	}
}
