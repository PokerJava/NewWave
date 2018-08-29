package ct.af.resourceModel;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ResourceMaster {
	
	@SerializedName("resourceIndex")
	private String resourceIndex;
	
	@SerializedName("mandatoryParam")
	private List<String> mandatoryParam;
	
	@SerializedName("validateParam")
	private List<String> validateParam;
	
	@SerializedName("suppCodesList")
	private List<SuppCode> suppCodesList;
	
	@SerializedName("rollbackFlag")
	private String rollbackFlag;
	
	@SerializedName("resourceRollbackThreshold")
	private String resourceRollbackThreshold;
	
	@SerializedName("responseToClientType")
	private String responseToClientType;
	
	@SerializedName("responseToClient")
	private String responseToClient;
	
	@SerializedName("effectiveDate")
	private String effectiveDate;
	
	@SerializedName("expireDate")
	private String expireDate;
	
	@SerializedName("createDate")
	private String createDate;
	
	@SerializedName("createBy")
	private String createBy;
	
	@SerializedName("lastUpdBy")
	private String lastUpdBy;
	
	@SerializedName("lastUpdDate")
	private String lastUpdDate;
	
	@SerializedName("description")
	private String description;
	
	public String getResourceIndex() {
		return resourceIndex;
	}
	public void setResourceIndex(String resourceIndex) {
		this.resourceIndex = resourceIndex;
	}
	public List<String> getMandatory() {
		return mandatoryParam;
	}
	public void setMandatory(List<String> mandatory) {
		this.mandatoryParam = mandatory;
	}
	public List<String> getValidate() {
		return validateParam;
	}
	public void setValidate(List<String> validate) {
		this.validateParam = validate;
	}
	public List<SuppCode> getSuppCodesList() {
		return suppCodesList;
	}
	public void setSuppCodesList(List<SuppCode> suppCodesList) {
		this.suppCodesList = suppCodesList;
	}
	public String getRollbackFlag() {
		return rollbackFlag;
	}
	public void setRollbackFlag(String rollbackFlag) {
		this.rollbackFlag = rollbackFlag;
	}
	public String getResourceRollbackThreshold() {
		return resourceRollbackThreshold;
	}
	public void setResourceRollbackThreshold(String resourceRollbackThreshold) {
		this.resourceRollbackThreshold = resourceRollbackThreshold;
	}
	public String getResponseToClientType() {
		return responseToClientType;
	}
	public void setResponseToClientType(String responseToClientType) {
		this.responseToClientType = responseToClientType;
	}
	public String getResponseToClient() {
		return responseToClient;
	}
	public void setResponseToClient(String responseToClient) {
		this.responseToClient = responseToClient;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getLastUpdBy() {
		return lastUpdBy;
	}
	public void setLastUpdBy(String lastUpdBy) {
		this.lastUpdBy = lastUpdBy;
	}
	public String getLastUpdDate() {
		return lastUpdDate;
	}
	public void setLastUpdDate(String lastUpdDate) {
		this.lastUpdDate = lastUpdDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public ResourceMaster trim(){
		
		for(int i=0; i<mandatoryParam.size(); i++){
			mandatoryParam.set(i, mandatoryParam.get(i).trim());
		}
		
		for(int i=0; i<validateParam.size(); i++){
			validateParam.set(i, validateParam.get(i).trim());
		}
		
		for(int i=0; i<suppCodesList.size(); i++){
			suppCodesList.set(i, suppCodesList.get(i).trim());
		}
		this.setRollbackFlag(rollbackFlag.trim());
		this.setResourceRollbackThreshold(resourceRollbackThreshold.trim());
		this.setEffectiveDate(effectiveDate.trim());
		this.setExpireDate(expireDate.trim());
		
		return this;
	}
	
	@Override
    public String toString() {
		
		String str = "{ \"ResourceIndex\" : " + resourceIndex + ",";
		str += " \"mandatoryParam\" : [";
		for(int i=0;i<mandatoryParam.size();i++){
			str += "\"" + mandatoryParam.get(i) + "\"";
			if(i<mandatoryParam.size()-1)
				str += ", ";
		}
		str += "], \"validateParam\" : [";
		for(int i=0;i<validateParam.size();i++){
			str += "\"" + validateParam.get(i) + "\"";
			if(i<validateParam.size()-1)
				str += ", ";
		}
		str += "], \"suppCodesList\" : [";
		for(int i=0;i<suppCodesList.size();i++){
			str += suppCodesList.get(i).toString();
			if(i<suppCodesList.size()-1)
				str += ", ";
		}
		str += "], \"rollbackFlag\" : \"" + rollbackFlag + "\" ";
		str += ", \"resourceRollbackThreshold\" : \"" + resourceRollbackThreshold + "\" ";
		str += ", \"responseToClientType\" : \"" + responseToClientType + "\" ";
		str += ", \"responseToClient\" : \"" + responseToClient + "\" ";
		str += ", \"effectiveDate\" : \"" + effectiveDate + "\" ";
		str += ", \"expireDate\" : \"" + expireDate + "\" ";
		str += ", \"createDate\" : \"" + createDate + "\" ";
		str += ", \"createBy\" : \"" + createBy + "\" ";
		str += ", \"lastUpdBy\" : \"" + lastUpdBy + "\" ";
		str += ", \"lastUpdDate\" : \"" + lastUpdDate + "\" ";
		str += ", \"description\" : \"" + description + "\" }";
		return str;
	}

}
