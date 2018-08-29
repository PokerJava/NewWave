package ct.af.resourceModel;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ResourceErrorHandling {
	
	@SerializedName("searchKey")
	private String searchKey;
	
	@SerializedName("suppCode")
	private String suppCode;
	
	@SerializedName("errCode")
	private String errCode;
	
	@SerializedName("errDescription")
	private String errDescription;
	
	@SerializedName("errAction")
	private String errAction;
	
	@SerializedName("retryTime")
	private String retryTime;

	@SerializedName("retrySleep")
	private String retrySleep;

	@SerializedName("ErrHandlingSuppCode")
	private List<String> ErrHandlingSuppCode;

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public String getSuppCode() {
		return suppCode;
	}

	public void setSuppCode(String suppCode) {
		this.suppCode = suppCode;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrDescription() {
		return errDescription;
	}

	public void setErrDescription(String errDescription) {
		this.errDescription = errDescription;
	}

	public String getErrAction() {
		return errAction;
	}

	public void setErrAction(String errAction) {
		this.errAction = errAction;
	}

	public String getRetryTime() {
		return retryTime;
	}

	public void setRetryTime(String retryTime) {
		this.retryTime = retryTime;
	}

	public String getRetrySleep() {
		return retrySleep;
	}

	public void setRetrySleep(String retrySleep) {
		this.retrySleep = retrySleep;
	}

	public List<String> getErrHandlingSuppCode() {
		return ErrHandlingSuppCode;
	}

	public void setErrHandlingSuppCode(List<String> errHandlingSuppCode) {
		ErrHandlingSuppCode = errHandlingSuppCode;
	}

	@Override
	public String toString() {
		return "ResourceErrorHandling{" +
				"searchKey='" + searchKey + '\'' +
				", suppCode='" + suppCode + '\'' +
				", errCode='" + errCode + '\'' +
				", errDescription='" + errDescription + '\'' +
				", errAction='" + errAction + '\'' +
				", retryTime='" + retryTime + '\'' +
				", retrySleep='" + retrySleep + '\'' +
				", ErrHandlingSuppCode=" + ErrHandlingSuppCode +
				'}';
	}
}
