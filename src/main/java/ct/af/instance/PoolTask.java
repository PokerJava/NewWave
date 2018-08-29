package ct.af.instance;

public class PoolTask {

	private String neId;
	private String suppCode;
	private String status;
	private String errCode;
	private String errDesc;
	private String sMessage;
	private String resultCode;
	private String developerMsg;
	private String errorFlag = "0";

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public String getSuppCode() {
		return suppCode;
	}

	public void setSuppCode(String suppCode) {
		this.suppCode = suppCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrDesc() {
		return errDesc;
	}

	public void setErrDesc(String errDesc) {
		this.errDesc = errDesc;
	}

	public String getsMessage() {
		return sMessage;
	}

	public void setsMessage(String sMessage) {
		this.sMessage = sMessage;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getDeveloperMsg() {
		return developerMsg;
	}

	public void setDeveloperMsg(String developerMsg) {
		this.developerMsg = developerMsg;
	}

	public String getErrorFlag() {
		return errorFlag;
	}

	public void setErrorFlag(String errorFlag) {
		this.errorFlag = errorFlag;
	}
	
	public String toString() {
		return "\nResult from Success Pattern Logic :" +
				"\n>> neId = " + neId + 
				"\n>> suppCode = " + suppCode + 
				"\n>> status = " + status + 
				"\n>> errCode = " + errCode + 
				"\n>> errDesc = " + errDesc + 
				"\n>> Message = " + sMessage + 
				"\n>> resultCode = " + resultCode + 
				"\n>> developerMsg = " + developerMsg + 
				"\n>> errorFlag = " + errorFlag;
	}

}
