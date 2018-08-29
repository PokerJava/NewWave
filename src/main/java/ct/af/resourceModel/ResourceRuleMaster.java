package ct.af.resourceModel;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ResourceRuleMaster {
	
    @SerializedName("resourceRuleId")
    private String resourceRuleId;

    @SerializedName("description")
    private String description;

    @SerializedName("resourceRuleState")
    private String resourceRuleState;

    @SerializedName("effectiveDate")
    private String effectiveDate;
    
    @SerializedName("expireDate")
    private String expireDate;
    
    @SerializedName("ruleDetail")
    private List<ResourceRule> ruleDetail;
    
    @SerializedName("createDate")
    private String createDate;
    
    @SerializedName("createBy")
    private String createBy;
    
    @SerializedName("lastUpdBy")
    private String lastUpdBy;
    
    @SerializedName("lastUpdDate")
    private String lastUpdDate;

	public String getResourceRuleId() {
		return resourceRuleId;
	}

	public void setResourceRuleId(String resourceRuleId) {
		this.resourceRuleId = resourceRuleId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResourceRuleState() {
		return resourceRuleState;
	}

	public void setResourceRuleState(String resourceRuleState) {
		this.resourceRuleState = resourceRuleState;
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

	public List<ResourceRule> getRuleDetail() {
		return ruleDetail;
	}

	public void setRuleDetail(List<ResourceRule> ruleDetail) {
		this.ruleDetail = ruleDetail;
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
	
	public ResourceRuleMaster trim() {
		this.setDescription(description.trim());
		this.setResourceRuleState(resourceRuleState.trim());
		this.setEffectiveDate(effectiveDate.trim());
		this.setExpireDate(expireDate.trim());
		for(int i=0; i<ruleDetail.size(); i++){
			ruleDetail.set(i,ruleDetail.get(i).trim());
		}
		this.setCreateDate(createDate.trim());
		this.setCreateBy(createBy.trim());
		this.setLastUpdBy(lastUpdBy.trim());
		this.setLastUpdDate(lastUpdDate.trim());
		
		return this;
	}
	
	@Override
    public String toString() {
		
		String str = "{ \"resourceRuleId\" : \""+resourceRuleId+"\", "
						+ "\"description\" : \""+description+"\", "
						+ "\"resourceRuleState\" : \""+resourceRuleState+"\", "
						+ "\"effectiveDate\" : \""+effectiveDate+"\", "
						+ "\"expireDate\" : \""+expireDate+"\", "
						+ "\"ruleDetail\" : [";
		for(int i=0; i<ruleDetail.size(); i++){
			str += ruleDetail.get(i).toString();
			if(i<ruleDetail.size()-1)
				str += ", ";
		}
		
		str += "], \"createDate\" : \""+createDate+"\", "
				+ "\"createBy\" : \""+createBy+"\", "
				+ "\"lastUpdBy\" : \""+lastUpdBy+"\", "
				+ "\"lastUpdDate\" : \""+lastUpdDate+"\" }";		
		return str;
	}

}
