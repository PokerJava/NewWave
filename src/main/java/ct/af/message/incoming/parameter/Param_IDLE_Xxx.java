package ct.af.message.incoming.parameter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.google.gson.internal.LinkedTreeMap;


public class Param_IDLE_Xxx{
	private Object A;
	private Object B;
	private Object C;
	private Object D;
	
	private String company;  
    private String name;
    private String invoke;
    private String mobileNo;

    private boolean isValid;
	public Object getA() {
		return A;
	}
	public void setA(Object a) {
		A = a;
	}
	public Object getB() {
		return B;
	}
	public void setB(Object b) {
		B = b;
	}
	public Object getC() {
		return C;
	}
	public void setC(Object c) {
		C = c;
	}
	//=================================================================XML=================================================================//
	
	
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public static HashMap<String,String> getXMLMsgToHashmap(String eqxMessage){
	       HashMap<String, String> rawData = new HashMap<String, String>();

	       try {
			String data = eqxMessage.substring(eqxMessage.indexOf("<ERDData"), eqxMessage.length()).trim();
			   
			   data = data.substring(data.indexOf("value="), data.indexOf("/>")).trim();
			   data = data.substring(data.indexOf("value=") + 7, data.length() - 1);
			   rawData.put("ERDData",data);
	       } catch (Exception e) {
	       }
	         
	       return rawData;
	       
	   }
	
	
	 public static String decodeUrl(String url) {
     	try {
 			String result = java.net.URLDecoder.decode(url, "UTF-8");
 			return result;
     	} catch (UnsupportedEncodingException e) {
 			return url;
 		}
     }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInvoke() {
		return invoke;
	}
	public void setInvoke(String invoke) {
		this.invoke = invoke;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	
    public static String getParameterValueFromUrl(String url, String parameterName) {
        try {
			if(url == null || "".equals(url)) {
				return null;
			}
			
			String[] splittedUrl = url.split("\\?", -1);
			if(splittedUrl.length != 2) {
				return null;
			}
			
			String parameter = splittedUrl[1];
			String[] splittedParameter = parameter.split("&", -1);
			if(splittedParameter.length == 0) {
				return null;
			}
			
			for(String param : splittedParameter) {
                if(null==param || "".equals(param)){
                    return null;
                }
				String[] splittedParam = param.split("=", -1);
				String key = splittedParam[0];
				String value = splittedParam[1];
				
				if(splittedParam.length > 2) {
					for(int i = 2;i < splittedParam.length;i++) {
						value += "=" + splittedParam[i];
					}
				}
				
				if(key.equals(parameterName)) {
					return value;
				}
			}
			
			return null;
		} catch (Exception e) {
			return null;
		}
    }
	
//=================================================================XML=================================================================//	
	
	
	
	
	
	
	public Object getD() {
		return D;
	}
	public void setD(Object d) {
		D = d;
	}
	
	public Object getHashMap(Object data){
		HashMap<String, Object> resourceMap;
		if(data instanceof LinkedTreeMap) {
			resourceMap = new HashMap<>();
			Set<String> keySet = ((LinkedTreeMap) data).keySet();
			for (String keyStr : keySet) {
				resourceMap.put(keyStr, ((LinkedTreeMap) data).get(keyStr));
			}
		}else if(data instanceof HashMap) {
			resourceMap = new HashMap<>();
		}else if(data instanceof ArrayList) {	
			resourceMap = new HashMap<>();
			ArrayList<HashMap<String, Object>> resourceArry = new ArrayList<>();
			for(int i=((ArrayList) data).size()-1;i>=0;i--) 
			{
				if(((ArrayList) data).get(i) instanceof ArrayList || ((ArrayList) data).get(i) instanceof LinkedTreeMap)
				{
					((ArrayList) data).add(getHashMap(((ArrayList) data).get(i)));
					((ArrayList) data).remove(i);
				}
			}
			return data;
		}else {
			resourceMap = new HashMap<>();
		}
		return resourceMap;
	}
}
