package ct.af.resourceModel;

import com.google.gson.annotations.SerializedName;

public class ResourceRule {
	@SerializedName("ruleType")
    private String ruleType;
	
	@SerializedName("paramName")
    private String paramName;
	
	@SerializedName("paramOperand")
    private String paramOperand;
	
	@SerializedName("paramStart")
    private String paramStart;
	
	@SerializedName("paramStop")
    private String paramStop;
	
	@SerializedName("paramValue")
    private String paramValue;

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamOperand() {
		return paramOperand;
	}

	public void setParamOperand(String paramOperand) {
		this.paramOperand = paramOperand;
	}

	public String getParamStart() {
		return paramStart;
	}

	public void setParamStart(String paramStart) {
		this.paramStart = paramStart;
	}

	public String getParamStop() {
		return paramStop;
	}

	public void setParamStop(String paramStop) {
		this.paramStop = paramStop;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	
	public ResourceRule trim(){
		this.setRuleType(ruleType.trim());
		this.setParamName(paramName.trim());
		this.setParamOperand(paramOperand.trim());
		if(paramStart!=null)
			this.setParamStart(paramStart.trim());
		if(paramStop!=null)
			this.setParamStop(paramStop.trim());
		if(paramValue!=null)
			this.setParamValue(paramValue.trim());
		return this;
	}
	
	@Override
    public String toString() {
		String str = "{ \"ruleType\" : \""+ruleType+"\","
		          		+"\"paramName\" : \""+paramName+"\","
		          		+"\"paramOperand\" : \""+paramOperand+"\",";
		          				
		
		if(paramStart!=null&&paramStop!=null){
			str+="\"paramStart\" : \""+paramStart+"\",";		
			str+="\"paramStop\" : \""+paramStop+"\"";
		}
		if(paramValue!=null){
			str+="\"paramValue\" : \""+paramValue+"\"";
		}
		str+="}";
		
		return str;
	}
	
}
