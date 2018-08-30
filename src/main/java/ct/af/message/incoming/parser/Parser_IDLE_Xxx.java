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
        String rawDataMessage = "{ \"company\" : \"CT\","
        		+ "\"animal\" : [\"dog\",\"cat\"],"
        		+ "\"userName\" : {\"jojo\" : \"1234\","
        		+ "\"momo\" : \"4321\"},"
        		+ "\"resourceName\" : [{\"resourceA1\" : \"A1\","
        		+ "\"resourceA2\" : \"A2\"},"
        		+ "{\"resourceB1\" : \"B1\","
        		+ "\"resourceB2\" : \"B2\"}]  }";
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
    		System.out.println(param.getD().get(0).get("v"));
    		
        } else if(rawCType.equals("text/xml")) {
        	
        } else if(rawCType.equals("Diameter")) {
        	
        } else if(rawCType.equals("Ldap")) {
        	
        } else {
        	return param;
        }
        
        
	
        return param;
	}
}
