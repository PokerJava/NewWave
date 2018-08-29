package ct.af.instance;

public class TransactionLog {
		
	private String Actor;
	private String InvokeId;
	private String ResourceName;
	private String ResourceOrderId;
	private String NeId;
	private String TaskId;
	private String Suppcode;
	private String ReqRawData;
	private String RespRawData;
	public String getActor() {
		return Actor;
	}
	public void setActor(String actor) {
		Actor = actor;
	}
	public String getInvokeId() {
		return InvokeId;
	}
	public void setInvokeId(String invokeId) {
		InvokeId = invokeId;
	}
	public String getResourceName() {
		return ResourceName;
	}
	public void setResourceName(String resourceName) {
		ResourceName = resourceName;
	}
	public String getResourceOrderId() {
		return ResourceOrderId;
	}
	public void setResourceOrderId(String resourceOrderId) {
		ResourceOrderId = resourceOrderId;
	}
	public String getNeId() {
		return NeId;
	}
	public void setNeId(String neId) {
		NeId = neId;
	}
	public String getTaskId() {
		return TaskId;
	}
	public void setTaskId(String taskId) {
		TaskId = taskId;
	}
	public String getSuppcode() {
		return Suppcode;
	}
	public void setSuppcode(String suppcode) {
		Suppcode = suppcode;
	}
	public String getReqRawData() {
		return ReqRawData;
	}
	public void setReqRawData(String reqRawData) {
		ReqRawData = reqRawData;
	}
	public String getRespRawData() {
		return RespRawData;
	}
	public void setRespRawData(String respRawData) {
		RespRawData = respRawData;
	}
}
