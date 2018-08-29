package ct.af.enums;

public enum ERequestState {
	
	OPEN("Open"),
	PENDING("Pending"),
	INTEST("In Test"),
	INPROGRESS("In Progress"),
	COMPLETED("Completed"),
	NOTCOMPLETED("Not Completed")
	;
	
	private String state;
	
	ERequestState(String state) {
		this.state = state;
	}

	public String getState() {
	    return state;
	}
	
	public static ERequestState fromString(String text) {
		if(text != null){
			for(ERequestState eState : ERequestState.values()){
				if (text.equalsIgnoreCase(eState.state)) {
					return eState;
				}
			}
		}
		return null;
	}

}
