package ct.af.instance;

import ct.af.utils.JsonUtils;

public class TaskDetail {
	private String taskId = "";
	private String neId = "";
	private String taskType = "";
	private String neType = "";
	private String partyName = "";
	private String productVersion = "";
	private String parallelTask = "";
	private String taskStatus = "";
	private String neSubmissionDate = "";
	private String neCompletionDate = "";
	private String neExpirationDate = "";
	private String orgTaskIds = "";
	private String suppCode = "";
	private String taskErrorFlag = "";
	private String mandatoryFlag = "";
	private String requestRefInvoke = "";
	private String responseRefInvoke = "";
	private String errorCode = "";
	private String errorDesc = "";
	private String taskSmessage = "";
	private String taskKeyCondition = "";

	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getNeId() {
		return neId;
	}
	public void setNeId(String neId) {
		this.neId = neId;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getNeType() {
		return neType;
	}
	public void setNeType(String neType) {
		this.neType = neType;
	}
	public String getPartyName() {
		return partyName;
	}
	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}
	public String getProductVersion() {
		return productVersion;
	}
	public void setProductVersion(String productVersion) {
		this.productVersion = productVersion;
	}
	public String getParallelTask() {
		return parallelTask;
	}
	public void setParallelTask(String parallelTask) {
		this.parallelTask = parallelTask;
	}
	public String getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	public String getNeSubmissionDate() {
		return neSubmissionDate;
	}
	public void setNeSubmissionDate(String neSubmissionDate) {
		this.neSubmissionDate = neSubmissionDate;
	}
	public String getNeCompletionDate() {
		return neCompletionDate;
	}
	public void setNeCompletionDate(String neCompletionDate) {
		this.neCompletionDate = neCompletionDate;
	}
	public String getNeExpirationDate() {
		return neExpirationDate;
	}
	public void setNeExpirationDate(String neExpirationDate) {
		this.neExpirationDate = neExpirationDate;
	}
	public String getOrgTaskIds() {
		return orgTaskIds;
	}
	public void setOrgTaskIds(String orgTaskIds) {
		this.orgTaskIds = orgTaskIds;
	}
	public String getSuppCode() {
		return suppCode;
	}
	public void setSuppCode(String suppCode) {
		this.suppCode = suppCode;
	}
	public String getTaskErrorFlag() {
		return taskErrorFlag;
	}
	public void setTaskErrorFlag(String taskErrorFlag) {
		this.taskErrorFlag = taskErrorFlag;
	}
	public String getMandatoryFlag() {
		return mandatoryFlag;
	}
	public void setMandatoryFlag(String mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}
	public String getRequestRefInvoke() {
		return requestRefInvoke;
	}
	public void setRequestRefInvoke(String requestRefInvoke) {
		this.requestRefInvoke = requestRefInvoke;
	}
	public String getResponseRefInvoke() {
		return responseRefInvoke;
	}
	public void setResponseRefInvoke(String responseRefInvoke) {
		this.responseRefInvoke = responseRefInvoke;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorDesc() {
		return errorDesc;
	}
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
	public String getTaskSmessage() {
		return taskSmessage;
	}
	public void setTaskSmessage(String taskSmessage) {
		this.taskSmessage = taskSmessage;
	}
	public String getTaskKeyCondition() {
		return taskKeyCondition;
	}
	public void setTaskKeyCondition(String taskKeyCondition) {
		this.taskKeyCondition = taskKeyCondition;
	}

	
	
    public String toStringOut() {
        return 
        		"	 {" + "\n" +
                "	  \"taskId\": \"" + taskId + "\"," + "\n" +
                "	  \"neId\": \"" + neId + "\"," + "\n" +
                "	  \"taskType\": \"" + taskType + "\"," + "\n" +
                "	  \"neType\": \"" + neType + "\"," + "\n" +
                "	  \"partyName\": \"" + partyName + "\"," + "\n" +
                "	  \"productVersion\": \"" + productVersion + "\"," + "\n" +
                "	  \"parallelTask\": \"" + parallelTask + "\"," + "\n" +
                "	  \"taskStatus\": \"" + taskStatus + "\"," + "\n" +
                "	  \"neSubmissionDate\": \"" + neSubmissionDate + "\"," + "\n" +
                "	  \"neCompletionDate\": \"" + neCompletionDate + "\"," + "\n" +
                "	  \"neExpirationDate\": \"" + neExpirationDate + "\"," + "\n" +
                "	  \"orgTaskIds\": \"" + orgTaskIds + "\"," + "\n" +
                "	  \"suppCode\": \"" + suppCode + "\"," + "\n" +
                "	  \"taskErrorFlag\": \"" + taskErrorFlag + "\"," + "\n" +
                "	  \"mandatoryFlag\": \"" + mandatoryFlag + "\"," + "\n" +
                "	  \"requestRefInvoke\": \"" + requestRefInvoke + "\"," + "\n" +
                "	  \"responseRefInvoke\": \"" + responseRefInvoke + "\"," + "\n" +
                "	  \"errorCode\": \"" + errorCode + "\"," + "\n" +
                "	  \"errorDesc\": \"" + errorDesc + "\"," + "\n" +
                "	  \"taskSmessage\": \"" + JsonUtils.escape(taskSmessage) + "\"," + "\n" +
                "	  \"taskKeyCondition\": \"" + taskKeyCondition + '\"' + "\n" +
                "	}";
    }

}
