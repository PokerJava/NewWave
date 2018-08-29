package ct.af.message.incoming.parameter;

import java.util.ArrayList;
import java.util.List;

import ct.af.resourceModel.ResourceInventory;

public class Param_SDF_GetResourceInventory {

	private String resultCode;
	private String resultDescription;
	private List<ResourceInventory> resultData = new ArrayList<>();
	private boolean isvalid = true;

	public boolean getIsValid() {
		return isvalid;
	}

	public void setIsValid(boolean isvalid) {
		this.isvalid = isvalid;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultDescription() {
		return resultDescription;
	}

	public void setResultDescription(String resultDesc) {
		this.resultDescription = resultDesc;
	}

	public List<ResourceInventory> getResultData() {
		return resultData;
	}

	public void setResultData(List<ResourceInventory> resultData) {
		this.resultData = resultData;
	}

	@Override
	public String toString() {
		return "Param_SDF_GetResourceInventory{" + "resultCode='" + resultCode + '\'' + ", resultDescription='" + resultDescription + '\''
				+ ", resultData=" + resultData.toString() + ", isValid=" + getIsValid() + '}';
	}

}
