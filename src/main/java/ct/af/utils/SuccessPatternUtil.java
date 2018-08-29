package ct.af.utils;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import ct.af.enums.EResultCode;
import ct.af.instance.AFSubInstance;
import ct.af.instance.PoolTask;
import ec02.af.utils.AFLog;

public class SuccessPatternUtil {

	public PoolTask getTaskResult(String patternID, HashMap<String, Object> incomingMap, AFSubInstance afSubIns) {
		PoolTask pool = new PoolTask();
		String neId = StringUtils.isBlank(afSubIns.getSubNeId())?"":(afSubIns.getSubNeId());
		pool.setStatus(EResultCode.RE50000.getResultStatus());
		pool.setNeId(neId);
		pool.setSuppCode(afSubIns.getCurrentSuppcode());
		pool.setResultCode(EResultCode.RE50000.getResultCode());
		pool.setErrDesc(EResultCode.RE50000.getResultDesc());
		pool.setDeveloperMsg("");
		
		try {
			switch (patternID) {

			case "FC":
				pool.setErrDesc("Operation is successful");
				pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": ")) 
						+ "Task successfully executed.");
				pool.setResultCode(EResultCode.RE20000.getResultCode());
				pool.setStatus(EResultCode.RE20000.getResultStatus());
				pool.setErrCode(EResultCode.RE20000.getResultCode());
				pool.setErrorFlag("1");
				break;
				
			case "Fail":
				pool.setErrDesc(EResultCode.RE50023.getResultStatus() );
				pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": "))
						+ EResultCode.RE50023.getResultCode() + " "
						+ EResultCode.RE50023.getResultDesc());
				pool.setResultCode(EResultCode.RE50023.getResultCode());
				pool.setStatus(EResultCode.RE50023.getResultStatus());
				pool.setErrCode(EResultCode.RE50023.getResultCode());
				break;

			case "HLR_HUAWEI":
				pool.setErrCode((String) incomingMap.get("RETCODE"));
				pool.setErrDesc((String) incomingMap.get("RETDESC"));
				if (incomingMap.get("RETCODE").equals("0")) {
					pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": ")) 
							+ "Task successfully executed.");
					pool.setResultCode(EResultCode.RE20000.getResultCode());
					pool.setStatus(EResultCode.RE20000.getResultStatus());
				} else {
					pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": "))
							+ (String) incomingMap.get("RETCODE") + " "
							+ (String) incomingMap.get("RETDESC"));
				}
				break;

			case "HLR_ZTE":
				pool.setErrCode((String) incomingMap.get("RETN"));
				pool.setErrDesc((String) incomingMap.get("DESC"));
				if (incomingMap.get("RETN").equals("000000")) {
					pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": "))
							+ "Task successfully executed.");
					pool.setResultCode(EResultCode.RE20000.getResultCode());
					pool.setStatus(EResultCode.RE20000.getResultStatus());
				} else {
					pool.setsMessage(
							(StringUtils.isBlank(neId)?"":(neId+ ": "))
							+ (String) incomingMap.get("RETN") 
							+ " " + (String) incomingMap.get("DESC"));
				}
				break;

			case "CBS":				
				if(incomingMap.containsKey("ResultHeader")){
					pool.setErrCode((String) ((HashMap<String, Object>) incomingMap.get("ResultHeader")).get("ResultCode"));
					pool.setErrDesc((String) ((HashMap<String, Object>) incomingMap.get("ResultHeader")).get("ResultDesc"));
					if (((HashMap<String, Object>) incomingMap.get("ResultHeader")).get("ResultCode").equals("0")) {
						pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": ")) 
								+ "Task successfully executed.");
						pool.setResultCode(EResultCode.RE20000.getResultCode());
						pool.setStatus(EResultCode.RE20000.getResultStatus());
					} else {
						pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": ")) 
								+ (String) ((HashMap<String, Object>) incomingMap.get("ResultHeader")).get("ResultCode")
								+ " "
								+ (String) ((HashMap<String, Object>) incomingMap.get("ResultHeader")).get("ResultDesc"));
					}
				}else if(incomingMap.containsKey("faultcode")){
					pool.setErrCode((String) (incomingMap.get("faultcode")));
					pool.setErrDesc((String) (incomingMap.get("faultstring")));
					pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": ")) 
							+ (String) (incomingMap.get("faultcode"))
							+ " "
							+ (String) (incomingMap.get("faultstring")));
				}else{
					set50019(neId,pool, afSubIns.getCurrentSuppcode(), patternID);
					AFLog.d("incoming: "+incomingMap.toString());
				}
				break;

			case "USMP":
				pool.setErrCode((String) ((HashMap<String, Object>) incomingMap.get("OperationStatus")).get("Code"));
				pool.setErrDesc(
						(String) ((HashMap<String, Object>) incomingMap.get("OperationStatus")).get("Description"));
				if (((HashMap<String, Object>) incomingMap.get("OperationStatus")).get("IsSuccess").equals("true")
						&& ((HashMap<String, Object>) incomingMap.get("OperationStatus")).get("Code")
								.equals("VSMP-00000000")) {
					pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": ")) + "Task successfully executed.");
					pool.setResultCode(EResultCode.RE20000.getResultCode());
					pool.setStatus(EResultCode.RE20000.getResultStatus());
				} else {
					pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": "))
							+ (String) ((HashMap<String, Object>) incomingMap.get("OperationStatus")).get("Code") + " "
							+ (String) ((HashMap<String, Object>) incomingMap.get("OperationStatus"))
									.get("Description"));
				}
				break;

			case "NOTIFY_ME":
				pool.setErrCode((String) incomingMap.get("errorcode"));
				pool.setErrDesc((String) incomingMap.get("errdesc"));
				if (incomingMap.get("errorcode").equals("0")) {
					pool.setStatus(EResultCode.RE20000.getResultStatus());
					pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": ")) 
							+ "Task successfully executed.");
					pool.setResultCode(EResultCode.RE20000.getResultCode());
				} else {
					pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": "))
							+ (String) incomingMap.get("errorcode") + " "
							+ (String) incomingMap.get("errdesc"));
				}
				break;

			case "MD":
				pool.setErrCode((String) ((HashMap<String, Object>) incomingMap.get("ResponseHeader")).get("Status"));
				pool.setErrDesc(
						(String) ((HashMap<String, Object>) incomingMap.get("ResponseHeader")).get("StatusMessage"));
				if ((incomingMap.get("ResponseParameters") != null)
						&& ((HashMap<String, Object>) incomingMap.get("ResponseParameters"))
								.get("SMESSAGE_1") != null) {
					pool.setErrDesc(pool.getErrDesc() + ", "
							+ (String) ((HashMap<String, Object>) incomingMap.get("ResponseParameters"))
									.get("SMESSAGE_1"));
				}

				if (((HashMap<String, Object>) incomingMap.get("ResponseHeader")).get("Status").equals("9")) 
				{
					pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": "))
							+ "Task successfully executed.");
					pool.setResultCode(EResultCode.RE20000.getResultCode());
					pool.setStatus(EResultCode.RE20000.getResultStatus());
				} else {
					pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": ")) 
							+ (String) ((HashMap<String, Object>) incomingMap.get("ResponseHeader")).get("Status") + " "
							+ (String) ((HashMap<String, Object>) incomingMap.get("ResponseHeader")).get("StatusMessage"));
					if ((incomingMap.get("ResponseParameters") != null)
							&& ((HashMap<String, Object>) incomingMap.get("ResponseParameters")).get("SMESSAGE_1") != null) 
					{
						pool.setsMessage(pool.getsMessage() + ", "
								+ (String) ((HashMap<String, Object>) incomingMap.get("ResponseParameters")).get("SMESSAGE_1"));
					}
				}
				break;

			case "SDF":
				pool.setErrCode((String) incomingMap.get("resultCode"));
				pool.setErrDesc((String) incomingMap.get("resultDescription"));
				if (incomingMap.get("resultCode").equals(EResultCode.RE20000.getResultCode())) {
					pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": "))
							+ "Task successfully executed.");
					pool.setResultCode(EResultCode.RE20000.getResultCode());
					pool.setStatus(EResultCode.RE20000.getResultStatus());
				} else {
					pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": "))
							+ (String) incomingMap.get("resultCode") + " "
							+ (String) incomingMap.get("resultDescription"));
				}
				break;
				
			case "DROP_RESOURCE":
				pool.setErrCode("20000");
				pool.setErrDesc("Drop Resource Index Success");
				pool.setsMessage("Drop Resource Index Success");
				pool.setResultCode(EResultCode.RE20000.getResultCode());
				pool.setStatus(EResultCode.RE20000.getResultStatus());
				pool.setErrorFlag("1");
				break;
			default :
				set50019(neId,pool,afSubIns.getCurrentSuppcode(),patternID);
				AFLog.d("incoming: "+incomingMap.toString());
				break;
			}			
			pool.setErrCode(JsonUtils.escape(pool.getErrCode()));
			pool.setErrDesc(JsonUtils.escape(pool.getErrDesc()));
			pool.setsMessage(JsonUtils.escape(pool.getsMessage()));
			pool.setResultCode(JsonUtils.escape(pool.getResultCode()));
			pool.setDeveloperMsg(JsonUtils.escape(pool.getDeveloperMsg()));
			
		} catch (Exception e) {
			set50019(neId,pool,afSubIns.getCurrentSuppcode(),patternID);			
			AFLog.d("incoming: "+incomingMap.toString());
		}

		return pool;
	}
	
	public PoolTask getTaskResult(String patternID,PoolTask pool) {
		switch (patternID) {

		case "ForceCode":
			pool.setResultCode(EResultCode.RE20000.getResultCode());
			pool.setStatus(EResultCode.RE20000.getResultStatus());
			pool.setErrorFlag("1");
			break;
		}	
		
		return pool;
	}
	
	private void set50019 (String neId, PoolTask pool, String suppcode, String patternID){
		pool.setStatus(EResultCode.RE50019.getResultStatus());
		pool.setErrCode(EResultCode.RE50019.getResultCode());		
		pool.setResultCode(EResultCode.RE50019.getResultCode());
		pool.setErrDesc(EResultCode.RE50019.getResultDesc());
		pool.setsMessage((StringUtils.isBlank(neId)?"":(neId+ ": "))
				+ EResultCode.RE50019.getResultCode() + " "
				+ EResultCode.RE50019.getResultDesc());
		pool.setDeveloperMsg((StringUtils.isBlank(suppcode)?"Result from NE":suppcode) 
				+ " is not match with response message in configuration file."
				+ " Please contact PGZ to check message templates, "+pool.getSuppCode()+".txt");
	}
}
