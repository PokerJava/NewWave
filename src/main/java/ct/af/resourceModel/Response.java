package ct.af.resourceModel;

import com.google.gson.annotations.SerializedName;

public class Response {
	@SerializedName("responseMsgType")
	String responseMsgType;
	
	@SerializedName("responseMessage")
	String responseMessage;

	public String getResponseMsgType() {
		return responseMsgType;
	}

	public void setResponseMsgType(String responseMsgType) {
		this.responseMsgType = responseMsgType;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
}
