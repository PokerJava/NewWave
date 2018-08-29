package ct.af.resourceModel;

import com.google.gson.annotations.SerializedName;

public class ResourceInfraNode {
    private String neId;
    private String neState;
    private Long quotaPlan;
    private Long quotaAvailability;
    private Long alertRatio;

    @SerializedName("pfUsername")
    private String userName;

    @SerializedName("pfPassword")
    private String password;

    private String hlrSN;


    public String getNeId() {
        return neId;
    }

    public void setNeId(String neId) {
        this.neId = neId;
    }

    public String getNeState() {
        return neState;
    }

    public void setNeState(String neState) {
        this.neState = neState;
    }

    public Long getQuotaPlan() {
        return quotaPlan;
    }

    public void setQuotaPlan(Long quotaPlan) {
        this.quotaPlan = quotaPlan;
    }

    public Long getQuotaAvailability() {
        return quotaAvailability;
    }

    public void setQuotaAvailability(Long quotaAvailability) {
        this.quotaAvailability = quotaAvailability;
    }

    public Long getAlertRatio() {
        return alertRatio;
    }

    public void setAlertRatio(Long alertRatio) {
        this.alertRatio = alertRatio;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHlrSN() {
        return hlrSN;
    }

    public void setHlrSN(String hlrSN) {
        this.hlrSN = hlrSN;
    }

	@Override
	public String toString() {
		return "ResourceInfraNode [neId=" + neId + ", neState=" + neState + ", quotaPlan=" + quotaPlan
				+ ", quotaAvailability=" + quotaAvailability + ", alertRatio=" + alertRatio + ", userName=" + userName
				+ ", password=" + password + ", hlrSN=" + hlrSN + "]";
	}
    
    
}
