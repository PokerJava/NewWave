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
        String rawDataMessage = "{ \"A\" : \"a\","
        		+ "\"B\" : [\"b1\",\"b2\"],"
        		+ "\"C\" : {\"X\" : \"x\","
        		+ "\"Y\" : \"y\"}  }";
        String rawCType = eqxRawData.getCType();
        
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
    			param.setB((ArrayList<String>) resourceHashMap.get("B"));
    		}
    		if(resourceHashMap.containsKey("C")) {
    			HashMap<String, Object> instanceHash = gson.fromJson(resourceHashMap.get("C").toString(), HashMap.class);
    			param.setC(instanceHash);
    		}
    		if(resourceHashMap.containsKey("D")) {
    			ArrayList<HashMap<String, Object>>
    		}
    		System.out.println("Helo");
        } else if(rawCType.equals("text/xml")) {
        	
        } else if(rawCType.equals("Diameter")) {
        	
        } else if(rawCType.equals("Ldap")) {
        	
        } else {
        	return param;
        }
        
        
	
        return param;
	}
}
