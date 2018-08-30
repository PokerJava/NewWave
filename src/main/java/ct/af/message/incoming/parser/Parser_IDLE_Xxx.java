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
        String rawDataMessageXml =  "<bookstore><book>" +"<title>Everyday Italian</title>" +"<author>Giada De Laurentiis</author>" +"<year>2005</year>" +
        							"</book></bookstore>";
        //String rawCType = eqxRawData.getCType();
        String rawCType = "text/xml";
        String rawDataMessage = "{ \"A\" : \"a\","
        		+ "\"B\" : [\"b1\",\"b2\"],"
        		+ "\"C\" : {\"X\" : \"x\","
        		+ "\"Y\" : \"y\"},"
        		+ "\"D\" : [{\"u\" : \"u1\","
        		+ "\"v\" : \"v1\"},"
        		+ "{\"u\" : \"v2\"}]  }";
        

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
