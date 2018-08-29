package ct.af.enums;

public enum EResultDescription {
	SUCCESS("success"),
	SUCCESS_WITH_ERR("success with business err"),
	MISSING_INVALID("err missing or invalid parameter"),
	ERROR("err unknown_error"),
	REJECT("err reject"),
	ABORT("err abort"),
	TIMEOUT("err connection timeout"),
	UNKNOWN_ERROR("err unknown_error"),
	;

	private String resultDesc;

	EResultDescription(String resultDesc) {
		this.resultDesc = resultDesc;
	}

	public String getResultDesc() {
		return resultDesc;
	}

}
