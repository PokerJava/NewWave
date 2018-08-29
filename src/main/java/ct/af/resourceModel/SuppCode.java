package ct.af.resourceModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.annotations.SerializedName;

public class SuppCode {

	@SerializedName("suppCode")
    private String suppCode;
	
	@SerializedName("mandatoryFlag")
    private String mandatoryFlag;
	
	@SerializedName("errorHandlingPolicy")
	private HashMap<String, String> errorHandlingPolicy;
	
	@SerializedName("resourceRuleList")
    private List<String> resourceRuleList;

	public String getSuppCode() {
		return suppCode;
	}

	public void setSuppCode(String suppCode) {
		this.suppCode = suppCode;
	}

	public String getMandatoryFlag() {
		return mandatoryFlag;
	}

	public void setMandatoryFlag(String mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}

	public HashMap<String, String> getErrorHandlingPolicy() {
		return errorHandlingPolicy;
	}

	public void setErrorHandlingPolicy(HashMap<String, String> errorHandlingPolicy) {
		this.errorHandlingPolicy = errorHandlingPolicy;
	}

	public List<String> getResourceRuleList() {
		return resourceRuleList;
	}

	public void setResourceRuleList(List<String> resourceRuleList) {
		this.resourceRuleList = resourceRuleList;
	}
	
	public SuppCode trim(){
		HashMap<String, String> policy = new HashMap<>();
		
		this.setSuppCode(suppCode.trim());
		this.setMandatoryFlag(mandatoryFlag.trim());
			
		if(errorHandlingPolicy!=null){
			for (Map.Entry<String, String> map : errorHandlingPolicy.entrySet()){
				policy.put(map.getKey().toString().trim(), map.getValue().toString().trim());
				
			}
		setErrorHandlingPolicy(policy);
		}
		
		for(int i=0; i<resourceRuleList.size(); i++){
			resourceRuleList.set(i, resourceRuleList.get(i));
		}
		
		return this;		
	}
	
	@Override
    public String toString() {
        String str= " { "
        		+ "\"suppCode\" : \"" + suppCode + "\""
        		+ ", \"mandatoryFlag\" : \"" + mandatoryFlag + "\"";
        		
        			
        int cnt=0; 
        if (errorHandlingPolicy!=null){
        	str += ", \"errorHandlingPolicy\" : { ";
        	for ( Entry<String, String> entry : errorHandlingPolicy.entrySet()) {
            	str += "\"" + entry.getKey() + "\" : \"" + entry.getValue() + "\"";        
    	        
    	        cnt++;
    	        if(cnt<errorHandlingPolicy.size()){
    	        	str += ", ";
    	        }
    	    }
        	str +="} ";
        }
        
        str+=", \"resourceRuleList\" : \"" + resourceRuleList.toString() + "\" }";
        return str;
    }
	
}
