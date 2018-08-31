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
                                  +"<jojo>1234</jojo>"
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
                                    +" <resourceB1>B1</resourceB1>"
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
			HashMap<String, Object> resourceHashMap = gson.fromJson(resourceOrderJsonObject, HashMap.class);
    		GsonPool.pushGson(gson);
			param = gson.fromJson(resourceOrderJsonObject, Param_IDLE_Xxx.class);

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

            Param_IDLE_Xxx paramIdle = new Param_IDLE_Xxx();
            
            String company = Param_IDLE_Xxx.getParameterValueFromUrl(xmlMessage1, "company");
            String name = Param_IDLE_Xxx.getParameterValueFromUrl(xmlMessage1, "name");
            String invoke = Param_IDLE_Xxx.getParameterValueFromUrl(xmlMessage1, "invoke");
            String mobile = Param_IDLE_Xxx.getParameterValueFromUrl(xmlMessage1, "mobile");
            
            paramIdle.setCompany(company); 
            paramIdle.setCompany(name); 
            paramIdle.setCompany(company); 
            paramIdle.setCompany(company); 
            
            

            
            
            
            
            
            
            
            
            
            
            
            
            
            
        	
        } else if(rawCType.equals("Diameter")) {
        	
        } else if(rawCType.equals("Ldap")) {
        	
        } else {
        	return param;
        }
        
        
	
        return param;
	}
}
