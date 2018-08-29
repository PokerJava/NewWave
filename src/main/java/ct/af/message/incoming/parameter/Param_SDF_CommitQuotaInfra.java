package ct.af.message.incoming.parameter;



public class Param_SDF_CommitQuotaInfra extends AbsClientParam {

	private String resultCode;
	private String resultDesc;


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

	    public boolean getIsValid() {

			return true;
		}

	@Override
	public String toString() {
		return "Param_SDF_CommitQuotaInfra{" +
				"resultCode='" + resultCode + '\'' +
				", resultDesc='" + resultDesc + '\'' +
				'}';
	}
}
