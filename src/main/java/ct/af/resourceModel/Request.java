package ct.af.resourceModel;

import com.google.gson.annotations.SerializedName;

public class Request {
	@SerializedName("requestMsgType")
	String requestMsgType;
	
	@SerializedName("requestMessage")
	String requestMessage;

	public String getRequestMsgType() {
		return requestMsgType;
	}

	public void setRequestMsgType(String requestMsgType) {
		this.requestMsgType = requestMsgType;
	}

	public String getRequestMessage() {
		return requestMessage;
	}

	public void setRequestMessage(String requestMessage) {
		this.requestMessage = requestMessage;
	}
}
