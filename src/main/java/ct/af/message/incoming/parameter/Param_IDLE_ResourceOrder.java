package ct.af.message.incoming.parameter;
import java.util.ArrayList;
import java.util.List;

public class Param_IDLE_ResourceOrder extends AbsClientParam {
    private String publicIdType = "";
    private String publicIdValue = "";
    private String privateIdType = "";
    private String privateIdValue = "";
    private String serviceOrderId = "";
    private String offeringId = "";
    private String serviceGroupId = "";
    private String resourceGroupId = "";
    private String resourceOrderType ="";
    private String reasonDesc = "";
    private String serviceOrderType = "";
    private String channel = "";
    private String user = "";
    private String referenceId = "";
    private String sourceSystem = "";
    private String userSys = "";
    private String reTransmit = "";
    private String customerOrderId = "";
    private String customerOrderType = "";
    
    public String getCustomerOrderType() {
		return customerOrderType;
	}

	public void setCustomerOrderType(String customerOrderType) {
		this.customerOrderType = customerOrderType;
	}

	public String getCustomerOrderId() {
		return customerOrderId;
	}

	public void setCustomerOrderId(String customerOrderId) {
		this.customerOrderId = customerOrderId;
	}


	private List<String> resourceItemList = new ArrayList<>();

	public String getPublicIdType() {
        return publicIdType;
    }

    public void setPublicIdType(String publicIdType) {
        this.publicIdType = publicIdType;
    }

    public String getPublicIdValue() {
        return publicIdValue;
    }

    public void setPublicIdValue(String publicIdValue) {
        this.publicIdValue = publicIdValue;
    }

    public String getPrivateIdType() {
        return privateIdType;
    }

    public void setPrivateIdType(String privateIdType) {
        this.privateIdType = privateIdType;
    }

    public String getPrivateIdValue() {
        return privateIdValue;
    }

    public void setPrivateIdValue(String privateIdValue) {
        this.privateIdValue = privateIdValue;
    }

    public String getOfferingId() {
        return offeringId;
    }

    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }

    public String getServiceOrderId() {
        return serviceOrderId;
    }

    public void setServiceOrderId(String serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
    }

    public String getResourceGroupId() {
        return resourceGroupId;
    }

    public void setResourceGroupId(String resourceGroupId) {
        this.resourceGroupId = resourceGroupId;
    }
    
	public String getResourceOrderType() {
		return resourceOrderType;
	}

	public void setResourceOrderType(String resourceOrderType) {
		this.resourceOrderType = resourceOrderType;
	}

    public String getReasonDesc() {
        return reasonDesc;
    }

    public void setReasonDesc(String reasonDesc) {
        this.reasonDesc = reasonDesc;
    }

    public String getServiceOrderType() {
        return serviceOrderType;
    }

    public void setServiceOrderType(String serviceOrderType) {
        this.serviceOrderType = serviceOrderType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getUserSys() {
        return userSys;
    }

    public void setUserSys(String userSys) {
        this.userSys = userSys;
    }


    public List<String> getResourceItemList() {
        return resourceItemList;
    }

    public void setResourceItemList(List<String> resourceItemList) {
        this.resourceItemList = resourceItemList;
    }

    public String getServiceGroupId() {
		return serviceGroupId;
	}

	public void setServiceGroupId(String serviceGroupId) {
		this.serviceGroupId = serviceGroupId;
	}


    public String getReTransmit() {
		return reTransmit;
	}

	public void setReTransmit(String reTransmit) {
		this.reTransmit = reTransmit;
	}

	@Override
    public String toString() {
        return "Param_IDLE_ResourceOrder{" +
                "publicIdType='" + publicIdType + '\'' +
                ", publicIdValue='" + publicIdValue + '\'' +
                ", privateIdType='" + privateIdType + '\'' +
                ", privateIdValue='" + privateIdValue + '\'' +
                ", offeringId='" + offeringId + '\'' +
                ", serviceOrderId='" + serviceOrderId + '\'' +
                ", resourceGroupId='" + resourceGroupId + '\'' +
                ", resourceOrderType='" + resourceOrderType + '\'' +
                ", reasonDesc='" + reasonDesc + '\'' +
                ", serviceGroupId='" + serviceGroupId + '\'' +
                ", serviceOrderType='" + serviceOrderType + '\'' +
                ", channel='" + channel + '\'' +
                ", user='" + user + '\'' +
                ", referenceId='" + referenceId + '\'' +
                ", sourceSystem='" + sourceSystem + '\'' +
                ", userSys='" + userSys + '\'' +
                ", retranSmit='" + reTransmit + '\'' +
                '}';
    }
    
    
    public String toStringOut() {
        return "{" + "\n" +
        		"	\"requestHeader\": {" + "\n" +
                "	  \"publicIdType\": \"" + publicIdType + "\"," + "\n" +
                "	  \"publicIdValue\": \"" + publicIdValue + "\"," + "\n" +
                "	  \"privateIdType\": \"" + privateIdType + "\"," + "\n" +
                "	  \"privateIdValue\": \"" + privateIdValue + "\"," + "\n" +
                "	  \"offeringId\": \"" + offeringId + "\"," + "\n" +
                "	  \"serviceOrderId\": \"" + serviceOrderId + "\"," + "\n" +
                "	  \"resourceGroupId\": \"" + resourceGroupId + "\"," + "\n" +
                "	  \"resourceOrderType\": \"" + resourceOrderType + "\"," + "\n" +
                "	  \"reasonDesc\": \"" + reasonDesc + "\"," + "\n" +
                "	  \"serviceOrderType\": \"" + serviceOrderType + "\"," + "\n" +
                "	  \"channel\": \"" + channel + "\"," + "\n" +
                "	  \"user\": \"" + user + "\"," + "\n" +
                "	  \"referenceId\": \"" + referenceId + "\"," + "\n" +
                "	  \"sourceSystem\": \"" + sourceSystem + "\"," + "\n" +
                "	  \"userSys\": \"" + userSys + '\"' + "\n" +
                "	}";
    }
    
    public void trim() {
    	publicIdType = publicIdType.trim();
        publicIdValue = publicIdValue.trim();
        privateIdType = privateIdType.trim();
        privateIdValue = privateIdValue.trim();
        serviceOrderId = serviceOrderId.trim();
        offeringId = offeringId.trim();
        serviceGroupId = serviceGroupId.trim();
        resourceGroupId = resourceGroupId.trim();
        resourceOrderType = resourceOrderType.trim();
        reasonDesc = reasonDesc.trim();
        serviceOrderType = serviceOrderType.trim();
        channel = channel.trim();
        user = user.trim();
        referenceId = referenceId.trim();
        sourceSystem = sourceSystem.trim();
        userSys = userSys.trim();
        reTransmit = reTransmit.trim();
        customerOrderId = customerOrderId.trim();
        customerOrderType = customerOrderType.trim();
    }
}
