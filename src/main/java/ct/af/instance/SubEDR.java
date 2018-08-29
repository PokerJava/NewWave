package ct.af.instance;

import org.apache.commons.lang3.StringUtils;

import ec02.data.enums.EEDRReceivedEvent;

public class SubEDR {

	//Global Static Fields
    private String recordName = "";
    private String sessionId = "";
    private String destinationServiceName = "";
    private String invokeId = "";
    private String receivedEvent="";
    private String serviceResult = "";
    private String responseTime;
    private String timeStampOut = "";
    private String timeStampIn = "";
    private String nextState = "";
    private String method ="";
    private String command="";
    private String counter="";
    private String invokeParrent = "";
    private String serviceOrderId = "";
    private String serviceOrderType = "";
    private String resourceOrderId = "";
    private String resourceName = "";
    private String mobileNo = "";
    private String imsi = "";
    private String taskId = "";
    private String neId = "";
    private String status = "";
    private String errorDesc = "";
    private String invokeIdResp = "";
    private String resourceGroupId = "";
    private String customerOrderId = "";
    private String customerOrderType = "";
    private String statsIn = "";
    private String statsOut = "";
    private String statsExeTime = "";
    private String resourceOrderType = "";
    
    public SubEDR setStat(SubEDR subEDR, AFSubInstance afSubIns) {
    	if (afSubIns.getStatsIn() != null) {
			subEDR.setStatsIn(afSubIns.getStatsIn().getStatName());
		}
		if (afSubIns.getStatsOut() != null) {
			subEDR.setStatsOut(afSubIns.getStatsOut().getStatName());
		}
		if (afSubIns.getStatsExeTime() != null) {
			if (StringUtils.isNotBlank(afSubIns.getStatsExeTime().getStatName())) {
				subEDR.setStatsExeTime(afSubIns.getStatsExeTime().getStatName());
			}
		}
    	return subEDR;
    }
	public String getResourceOrderType() {
		return resourceOrderType;
	}

	public void setResourceOrderType(String resourceOrderType) {
		this.resourceOrderType = resourceOrderType;
	}

	public String getStatsIn() {
		return statsIn;
	}

	public void setStatsIn(String statsIn) {
		this.statsIn = statsIn;
	}

	public String getStatsExeTime() {
		return statsExeTime;
	}

	public void setStatsExeTime(String statsExeTime) {
		this.statsExeTime = statsExeTime;
	}

	public String getStatsOut() {
		return statsOut;
	}

	public void setStatsOut(String statsOut) {
		this.statsOut = statsOut;
	}

	public String getCustomerOrderType() {
		return customerOrderType;
	}

	public void setCustomerOrderType(String customerOrderType) {
		this.customerOrderType = customerOrderType;
	}

	public String getNextState() {
		return nextState;
	}

	public void setNextState(String nextState) {
		this.nextState = nextState;
	}

	public String getCustomerOrderId() {
		return customerOrderId;
	}

	public void setCustomerOrderId(String customerOrderId) {
		this.customerOrderId = customerOrderId;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}

	public String getInvokeIdResp() {
		return invokeIdResp;
	}

	public void setInvokeIdResp(String invokeIdResp) {
		this.invokeIdResp = invokeIdResp;
	}

	public String getResourceGroupId() {
		return resourceGroupId;
	}

	public void setResourceGroupId(String resourceGroupId) {
		this.resourceGroupId = resourceGroupId;
	}

	public String getInvokeParrent() {
		return invokeParrent;
	}

	public void setInvokeParrent(String invokeParrent) {
		this.invokeParrent = invokeParrent;
	}

    public String getServiceOrderId() {
		return serviceOrderId;
	}

	public void setServiceOrderId(String serviceOrderId) {
		this.serviceOrderId = serviceOrderId;
	}

	public String getServiceOrderType() {
		return serviceOrderType;
	}

	public void setServiceOrderType(String serviceOrderType) {
		this.serviceOrderType = serviceOrderType;
	}

	public String getResourceOrderId() {
		return resourceOrderId;
	}

	public void setResourceOrderId(String resourceOrderId) {
		this.resourceOrderId = resourceOrderId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCounter() {
		return counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}

	public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDestinationServiceName() {
        return destinationServiceName;
    }

    public void setDestinationServiceName(String destinationServiceName) {
        this.destinationServiceName = destinationServiceName;
    }

    public String getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(String invokeId) {
        this.invokeId = invokeId;
    }

    public String getReceivedEvent() {
        return receivedEvent;
    }

    public void setReceivedEvent(String receivedEvent) {
        this.receivedEvent = receivedEvent;
    }

    public String getServiceResult() {
        return serviceResult;
    }

    public void setServiceResult(String serviceResult) {
        this.serviceResult = serviceResult;
    }



    public String getTimeStampOut() {
        return timeStampOut;
    }

    public void setTimeStampOut(String timeStampOut) {
        this.timeStampOut = timeStampOut;
    }

    public String getTimeStampIn() {
        return timeStampIn;
    }

    public void setTimeStampIn(String timeStampIn) {
        this.timeStampIn = timeStampIn;
    }
}
