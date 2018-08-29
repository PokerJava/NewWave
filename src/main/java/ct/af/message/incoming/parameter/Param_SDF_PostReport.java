package ct.af.message.incoming.parameter;

import java.util.HashMap;

public class Param_SDF_PostReport {

	private String resultCode;
	private String resultDescription;
	private HashMap<String,Object> resultData = new HashMap<>();
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

	public HashMap<String, Object> getResultData() {
		return resultData;
	}


	@Override
	public String toString() {
		return "Param_SDF_PostReport{" + "resultCode='" + resultCode + '\'' + ", resultDescription='" + resultDescription + '\''
				+ ", resultData=" + resultData.toString() + ", isValid=" + getIsValid() + '}';
	}

}
