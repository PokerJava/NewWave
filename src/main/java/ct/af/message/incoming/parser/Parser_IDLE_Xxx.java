package ct.af.message.incoming.parser;

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
        String rawDataMessage = "{ \"A\" : \"a\" }";
        String rawCType = eqxRawData.getCType();
        
        if(rawCType.equals("text/plain")) {
        	JsonParser jsonParser = new JsonParser();
    		JsonObject resourceOrderJsonObject = jsonParser.parse(rawDataMessage).getAsJsonObject();
    		Gson gson = GsonPool.getGson();
    		HashMap<String, String> test = gson.fromJson(resourceOrderJsonObject, HashMap.class);
        } else if(rawCType.equals("text/xml")) {
        	
        } else if(rawCType.equals("Diameter")) {
        	
        } else if(rawCType.equals("Ldap")) {
        	
        } else {
        	return param;
        }
        
        
	
        return param;
	}
}
