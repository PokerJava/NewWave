package ct.af.enums;

public enum EEqxMsg {

	ORIG("orig"),
	INVOKE("invoke"),
	PROTOCOL("name"),
	TYPE("type"),
	CTYPE("ctype"),
	METHOD("method"),
	URL("url"),
	VAL("val"),
	FILTER("filter"),
	SOAPACTION("SOAPAction"),
	DESTINATION("to"),
	TIMEOUT("timeout")
	;

	private String name;
	EEqxMsg(String name) {
		this.name = name;
	}

	public String getEqxMsg() {
		return name;
	}
}
