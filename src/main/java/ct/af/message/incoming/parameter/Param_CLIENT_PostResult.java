package ct.af.message.incoming.parameter;

public class Param_CLIENT_PostResult extends AbsClientParam  {
	private String resourceOrderId;
    private String resourceGroupId;
    private String resultCode;
    private String resultDesc;
    private String developerMessage;



	public String getResourceOrderId()
    {
        return resourceOrderId;
    }

    public void setResourceOrderId(String resourceOrderId)
    {
        this.resourceOrderId = resourceOrderId;
    }

	public String getResourceGroupId()
    {
        return resourceGroupId;
    }

    public void setResourceGroupId(String resourceGroupId)
    {
        this.resourceGroupId = resourceGroupId;
    }

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultDesc() {
		return resultDesc;
	}

	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}

}
