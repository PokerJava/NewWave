package ct.af.enums;

public enum  EResultCode {

	ECODE200("200", "SUCCESS"),
	ECODE201("201", "CREATED"),
	ECODE400("400", "BAD REQUEST"),
	ECODE401("401", "UNAUTHORIZED"),
	ECODE403("403", "FORBIDDEN"),
	ECODE404("404", "NOT FOUND"),
	ECODE500("500", "INTERNAL SERVER ERROR"),
	ECODE501("501", "NOT IMPLEMENTED"),
	ECODE502("502", "BAD GATEWAY"),
	ECODE503("503", "SERVER UNVAILABLE"),
	ECODE504("504", "GATEWAY TIMEOUT"),
	
    RE00004("00004", "Timeout."),
    RE00003("00003", "Abort."),
    RE00002("00002", "Reject."),
    RE00001("00001", "Error."),
    RE20000("20000", "Success."),
    RE20001("20001", "Success without access network publicId."),
    RE20002("20002", "Success with condition."),
    RE20010("20010", "Success on both publicId."),
    RE20011("20011", "Success only on access network publicId."),
    RE20012("20012", "Success only on PublicId."),
    RE20013("20013", "Success without reservation but reuse both PublicId and access network publicId which have the same uId."),
    RE20014("20014", "Success without reservation but reuse both PublicId and access network publicId which have the different uId."),
    RE20100("20100", "Created success."),
    RE20101("20101", "Created success without SAP."),
    RE20300("20300", "Bypass authen."),
    RE40080("40080", "Unknown state."),
    RE40081("40081", "Unknown invoke."),
    RE40082("40082", "Invalid data on E01."),
    RE40100("40100", "Access is denied due to invalid credentials."),
    RE40101("40101", "Access denied."),
    RE40102("40102", "SessionId expired."),
    RE40103("40103", "Access token expired."),
    RE40104("40104", "Invalid Header."),
    RE40105("40105", "Network not allow."),
    RE40110("40110", "User or password�has�expired."),
    RE40111("40111", "Invalid user or password."),
    RE40112("40112", "Invalid user or password more than 3 times."),
    RE40113("40113", "Exceeded data allowances."),
    RE40114("40114", "Order still in processing."),
    RE40300("40300", "Missing or invalid parameter."),
    RE40301("40301", "Data exists."),
    RE40302("40302", "Unauthorized to access production."),
    RE40303("40303", "Unauthorized to access pre production."),
    RE40310("40310", "Inactive User."),
    RE40311("40311", "Msisdn is not matched."),
    RE40312("40312", "FbbId is invalid."),
    RE40313("40313", "Permission denied."),
    RE40314("40314", "Data process abort."),
    RE40350("40350", "Inactive Partner."),
    RE40351("40351", "Registration account attempt was unsuccessful."),
    RE40352("40352", "Registration package attempt was unsuccessful."),
    RE40400("40400", "Unknown URL."),
    RE40401("40401", "Data not found."),
    RE41700("41700", "Expectation failed."),
    RE42410("42410", "Operation failed."),
    RE42411("42411", "Operation failed and rollback unsuccessfully."),
    RE42901("42901", "Too many request."),
//    RE50000("50000", "System error."),
    RE50000("50000", "Failed."),
    RE50001("50001", "DB error."),
    RE50002("50002", "Connection timeout."),    
    RE50003("50003", "Connection error."),
    RE50011("50011", "Application is shutting down on the web server."),
    RE50012("50012", "Application is busy restarting on the web server."),
    RE50013("50013", "Web server is too busy."),
    RE50019("50019", "configuration data is invalid."),
    RE50020("50020", "current processing request already reach limit, please send request later."),
    RE50023("50023", "could not be processed."),
    RE50024("50024", "resourceOrder is expired."),
    RE50060("50060", "Unknown error."),
    RE50080("50080", "Data not found on E01."),
    RE50100("50100", "Invalid url structure."),
    RE50200("50200", "Bad gateway error."),
    RE50300("50300", "Server busy."),
    RE50301("50301", "Server unavailable."),
    RE50400("50400", "Gateway Timeout error.");
	
    private String resultCode;
    private String resultDesc;

    EResultCode(String resultCode, String resultDesc) {
        this.resultCode = resultCode;
        this.resultDesc = resultDesc;
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }
    
    public String getResultStatus() {
    	if(this.equals(RE20000))
    		return "Success";
    	else
    		return "Failed";
    }

    public static EResultCode fromString(String text) {
        if (text != null) {
            for (EResultCode resultCode : EResultCode.values()) {
                if (text.equalsIgnoreCase(resultCode.resultCode)) {
                    return resultCode;
                }
            }
        }
        return null;
    }
}
