package ct.af.resourceModel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResourceMappingCommand {
    @SerializedName("suppCode")
    String suppCode;

    @SerializedName("suppCodeType")
    String suppCodeType;

    @SerializedName("timeOut")
    String timeOut;

    @SerializedName("neType")
    String neType;

    @SerializedName("identityType")
    String identityType;

    @SerializedName("quotaFlag")
    String quotaFlag;

//    @SerializedName("defaultNeId")
//    String defaultNeId;

//    @SerializedName("protocolInfo")
//    String protocolInfo;

//    @SerializedName("cType")
//    String cType;

    @SerializedName("url")
    String url;
    
    @SerializedName("SOAPAction")
    String SOAPAction;
    
    @SerializedName("requestMsgType")
    String requestMsgType;
    
    @SerializedName("responseMsgType")
    String responseMsgType;

    @SerializedName("description")
    String description;
    
    @SerializedName("method")
    String method;
    
    @SerializedName("forceNeid")
    String forceNeid;
    
    @SerializedName("messageFormat")
    String messageFormat;
    
    @SerializedName("successPattern")
    String successPattern;
    
    @SerializedName("createDate")
    String createDate;
    
    @SerializedName("createBy")
    String createBy;
    
    @SerializedName("lastUpdBy")
    String lastUpdBy;
    
    @SerializedName("lastUpdDate")
    String lastUpdDate;

	ArrayList<String> requestMessage = new ArrayList<>();
	
	ArrayList<String> responseMessage = new ArrayList<>();

	ArrayList<String> requestURLMessage = new ArrayList<>();

    public String getSuppcode() {
		return suppCode;
	}

	public void setSuppcode(String suppcode) {
		this.suppCode = suppcode;
	}

	public String getSuppCodeType() {
		return suppCodeType;
	}

	public void setSuppCodeType(String suppCodeType) {
		this.suppCodeType = suppCodeType;
	}

	public String getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}

	public String getNeType() {
		return neType;
	}

	public void setNeType(String neType) {
		this.neType = neType;
	}

	public String getIdentityType() {
		return identityType;
	}

	public void setIdentityType(String identityType) {
		this.identityType = identityType;
	}

	public String getQuotaFlag() {
		return quotaFlag;
	}

	public void setQuotaFlag(String quotaFlag) {
		this.quotaFlag = quotaFlag;
	}

//	public String getDefaultNeId() {
//		return defaultNeId;
//	}

//	public void setDefaultNeId(String defaultNeId) {
//		this.defaultNeId = defaultNeId;
//	}

//	public String getProtocolInfo() {
//		return protocolInfo;
//	}

//	public void setProtocolInfo(String protocolInfo) {
//		this.protocolInfo = protocolInfo;
//	}

//	public String getcType() {
//		return cType;
//	}

//	public void setcType(String cType) {
//		this.cType = cType;
//	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSOAPAction() {
		return SOAPAction;
	}

	public void setSOAPAction(String sOAPAction) {
		SOAPAction = sOAPAction;
	}

	public String getRequestMsgType() {
		return requestMsgType;
	}

	public void setRequestMsgType(String request) {
		this.requestMsgType = request;
	}

	public String getResponseMsgType() {
		return responseMsgType;
	}

	public void setResponseMsgType(String responseMsgType) {
		this.responseMsgType = responseMsgType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getForceNeid() {
		return forceNeid;
	}
	
	public void setForceNeid(String forceNeid) {
		this.forceNeid = forceNeid;
	}

	public String getMessageFormat() {
		return messageFormat;
	}

	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}

	public String getSuccessPattern() {
		return successPattern;
	}

	public void setSuccessPattern(String successPattern) {
		this.successPattern = successPattern;
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

    public ArrayList<String> getRequestMessage()
    {
        return requestMessage;
    }

    public void setRequestMessage(ArrayList<String> requestMessage)
    {
        this.requestMessage = requestMessage;
    }
    
    public ArrayList<String> getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(ArrayList<String> responseMessage) {
		this.responseMessage = responseMessage;
	}

	public ArrayList<String> getRequestURLMessage()
	{
		return requestURLMessage;
	}

	public void setRequestURLMessage(ArrayList<String> requestURLMessage)
	{
		this.requestURLMessage = requestURLMessage;
	}

	public ResourceMappingCommand trim() {
    	this.setSuppcode(suppCode.trim());
    	this.setSuppCodeType(suppCodeType.trim());
    	this.setTimeOut(timeOut.trim());
    	this.setNeType(neType.trim());
    	this.setIdentityType(identityType.trim());
    	this.setQuotaFlag(quotaFlag.trim());
//    	this.setDefaultNeId(defaultNeId.trim());
//    	this.setProtocolInfo(protocolInfo.trim());
//    	this.setcType(cType.trim());
    	this.setUrl(url.trim());
    	this.setSOAPAction(SOAPAction.trim());
    	this.setRequestMsgType(requestMsgType.trim());
    	this.setResponseMsgType(responseMsgType.trim());
    	this.setDescription(description.trim());
    	this.setMethod(method.trim());
    	this.setForceNeid(forceNeid.trim());
    	this.setCreateDate(createDate.trim());
    	this.setCreateBy(createBy.trim());
    	this.setLastUpdBy(lastUpdBy.trim());
    	this.setLastUpdDate(lastUpdDate.trim());
    	this.setMessageFormat(messageFormat.trim());
    	
		return this;
    }
    
    public String toString() {
    	// String str = requestMsgType.getRequestMessage().replace('\"', '|');
    	return "{ \"SuppCode\": " + "\"" + suppCode + "\""+
                ", \"SuppCodeType\": " + "\"" + suppCodeType + "\"" +
                ", \"TimeOut\": " + "\"" + timeOut + "\"" +
                ", \"NeType\": " + "\"" + neType + "\"" +
                ", \"IdentityType\": " + "\"" + identityType + "\"" +
                ", \"QuotaFlag\": " + "\"" + quotaFlag + "\"" +
//              ", \"DefaultNeId\": " + "\"" + defaultNeId + "\"" +
//              ", \"ProtocolInfo\": " + "\"" + protocolInfo + "\"" +
//              ", \"cType\": " + "\"" + cType + "\"" +
                ", \"Url\": " + "\"" + url + "\"" +
                ", \"SOAPAction\": " + "\"" + SOAPAction + "\"" +
                ", \"RequestMsgType\": " + "\"" + requestMsgType + "\"" +
                ", \"ResponseMsgType\": " + "\"" + responseMsgType + "\"" +
                ", \"Description\": " + "\"" + description + "\"" +
                ", \"Method\": " + "\"" + method + "\"" +
                ", \"ForceNeid\": " + "\"" + forceNeid + "\"" +
                ", \"CreateDate\": " + "\"" + createDate + "\"" +
                ", \"CreateBy\": " + "\"" + createBy + "\"" +
                ", \"LastUpdBy\": " + "\"" + lastUpdBy + "\"" +
                ", \"LastUpdDate\": " + "\"" + lastUpdDate + "\"" +
                ", \"MessageFormat\": " + "\"" + messageFormat + "\"" +
                "}";
    }
}
