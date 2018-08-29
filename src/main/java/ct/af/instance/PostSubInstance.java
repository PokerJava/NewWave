package ct.af.instance;

import java.util.ArrayList;
import com.google.gson.JsonObject;

public class PostSubInstance {

	private String subInstanceNo = "";
	private String resourceName = "";
	private String resourceItemId = "";
	private String resourceItemNeid = "";
	private String resourceItemStatus = "";
	private String resourceItemErrMessage = "";
	private String resourceItem_error_flag = "";
	private String resourceIndex = "";
	private JsonObject resourceItemAttribute;
	private ArrayList<TaskDetail> TaskDetailList = new ArrayList<>();
	private ArrayList<String> suppCode = new ArrayList<>();
	private ArrayList<String> taskKeyCondition = new ArrayList<>();
	private ArrayList<String> taskDeveloperMessage = new ArrayList<>();
    private ArrayList<String> resourceSearchKey = new ArrayList<>();

    private String requestStatus = "";
    private String responseStatus = "";
    private String errorMessage = "";
    private String developMessage = "";
    private String orderDate = "";
    private String submittedDate = "";
    private String completeDate = "";
    private String new_errorFlag = "";
    private String expirationDate = "";
    private String expirationStatus = "";
    private String subInitSearchKey = "";
    private String resultCode ="";
    
    private boolean dropResourceFlag = false;
    private String dropResourceParent = "";
    private String resourceActivatedDate="";

	public String getResourceActivatedDate() {
		return resourceActivatedDate;
	}

	public void setResourceActivatedDate(String resourceActivatedDate) {
		this.resourceActivatedDate = resourceActivatedDate;
	}

	public String getExpirationStatus() {
		return expirationStatus;
	}

	public void setExpirationStatus(String expirationStatus) {
		this.expirationStatus = expirationStatus;
	}

	public String getSubInitSearchKey() {
		return subInitSearchKey;
	}

	public void setSubInitSearchKey(String subInitSearchKey) {
		this.subInitSearchKey = subInitSearchKey;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public ArrayList<String> getResourceSearchKey() {
		return resourceSearchKey;
	}

	public void setResourceSearchKey(ArrayList<String> resourceSearchKey) {
		this.resourceSearchKey = resourceSearchKey;
	}

	public String getNew_errorFlag() {
		return new_errorFlag;
	}

	public void setNew_errorFlag(String new_errorFlag) {
		this.new_errorFlag = new_errorFlag;
	}

	public String getResourceItemErrMessage() {
		return resourceItemErrMessage;
	}

	public void setResourceItemErrMessage(String resourceItemErrMessage) {
		this.resourceItemErrMessage = resourceItemErrMessage;
	}

	public String getResourceItem_error_flag() {
		return resourceItem_error_flag;
	}

	public void setResourceItem_error_flag(String resourceItem_error_flag) {
		this.resourceItem_error_flag = resourceItem_error_flag;
	}

	public String getResourceIndex() {
		return resourceIndex;
	}

	public void setResourceIndex(String resourceIndex) {
		this.resourceIndex = resourceIndex;
	}

	public JsonObject getResourceItemAttribute() {
		return resourceItemAttribute;
	}

	public void setResourceItemAttribute(JsonObject resourceItemAttribute) {
		this.resourceItemAttribute = resourceItemAttribute;
	}

	public ArrayList<String> getSuppCode() {
		return suppCode;
	}

	public ArrayList<TaskDetail> getTaskDetailList() {
		return TaskDetailList;
	}

	/*
	 * public void setTaskDetailList(ArrayList<TaskDetail> taskDetailList) {
	 * TaskDetailList = taskDetailList; }
	 */

	public void setSuppCode(ArrayList<String> suppCode) {
		this.suppCode = suppCode;
	}

	public ArrayList<String> getTaskKeyCondition() {
		return taskKeyCondition;
	}

	public void setTaskKeyCondition(ArrayList<String> taskKeyCondition) {
		this.taskKeyCondition = taskKeyCondition;
	}

	public ArrayList<String> getTaskDeveloperMessage() {
		return taskDeveloperMessage;
	}

	public void setTaskDeveloperMessage(ArrayList<String> taskDeveloperMessage) {
		this.taskDeveloperMessage = taskDeveloperMessage;
	}

	/*
	 * public void setTaskDetailList(ArrayList<TaskDetail> taskDetailList) {
	 * TaskDetailList = taskDetailList; }
	 */

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCodeFromNE) {
		this.resultCode = resultCodeFromNE;
	}

	public String getSubInstanceNo() {
		return subInstanceNo;
	}

	public void setSubInstanceNo(String subInstanceNo) {
		this.subInstanceNo = subInstanceNo;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceItemId() {
		return resourceItemId;
	}

	public void setResourceItemId(String resourceItemId) {
		this.resourceItemId = resourceItemId;
	}

	public String getResourceItemNeid() {
		return resourceItemNeid;
	}

	public void setResourceItemNeid(String resourceItemNeid) {
		this.resourceItemNeid = resourceItemNeid;
	}

	public String getResourceItemStatus() {
		return resourceItemStatus;
	}

	public void setResourceItemStatus(String resourceItemStatus) {
		this.resourceItemStatus = resourceItemStatus;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getDevelopMessage() {
		return developMessage;
	}

	public void setDevelopMessage(String developMessage) {
		this.developMessage = developMessage;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(String submittedDate) {
		this.submittedDate = submittedDate;
	}

	public String getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}

	public boolean isDropResourceFlag() {
		return dropResourceFlag;
	}

	public void setDropResourceFlag(boolean dropResourceFlag) {
		this.dropResourceFlag = dropResourceFlag;
	}
	
	public String getDropResourceParent() {
		return dropResourceParent;
	}

	public void setDropResourceParent(String dropResourceParent) {
		this.dropResourceParent = dropResourceParent;
	}

	public String toString() {
		String str = "subInstanceNo : " + subInstanceNo + "\nresourceName : " + resourceName + "\nresourceItemId : "
				+ resourceItemId + "\nresourceItemNeid : " + resourceItemNeid + "\nresourceItemStatus : "
				+ resourceItemStatus + "\nresourceItemErrMessage : " + resourceItemErrMessage
				+ "\nresourceItem_error_flag : " + resourceItem_error_flag + "\nresourceActivatedDate : " + resourceActivatedDate;

		str += "\nresourceItemAttribute : " + (resourceItemAttribute == null ? "" : resourceItemAttribute.toString());

		str += "\nsuppCode : [";
		for (int i = 0; i < suppCode.size(); i++) {
			str += suppCode.get(i);
			if (i < suppCode.size() - 1) {
				str += ",";
			}
		}
		str += "]";

		str += "\ntaskKeyCondition : [";
		for (int i = 0; i < taskKeyCondition.size(); i++) {
			str += taskKeyCondition.get(i);
			if (i < taskKeyCondition.size() - 1) {
				str += ",";
			}
		}
		str += "]";

		str += "\ntaskDeveloperMessage : [";
		for (int i = 0; i < taskDeveloperMessage.size(); i++) {
			str += taskDeveloperMessage.get(i);
			if (i < taskDeveloperMessage.size() - 1) {
				str += ",";
			}
		}
		str += "]";
		return str;

	}

}
