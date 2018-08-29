package ct.af.extracterd;

import org.joda.time.Duration;
import org.joda.time.DateTime;
import ct.af.enums.EResultCode;
import ct.af.enums.ERet;
import ct.af.enums.EStats;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_SDF_PostReport;
import ct.af.message.incoming.parser.Parser_SDF_PostReport;
import ct.af.utils.Config;
import ct.af.utils.LogUtils;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class In_SDF_PostReport {
	public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns,
			EquinoxRawData eqxRawData) {
		String rawDataRet = eqxRawData.getRet();
		String rawDataECode = eqxRawData.getRawDataAttribute("ecode");
		String rawDataVal = eqxRawData.getRawDataAttribute("val");

		EResultCode resultCode;
		EResultCode internalCode;
		
		String insNo = afSubIns.getSubInitOrig().split("\\.")[afSubIns.getSubInitOrig().split("\\.").length - 1];
		String nodeName = "AISSDF_" + insNo;
		EStats statsIn;
		//Set src
		String recoveryConf = "";
		
		boolean isValid = false;
		
		Param_SDF_PostReport param;
				
		if (rawDataVal != null && !rawDataVal.equals("") && rawDataECode.equals("200")
				&& rawDataRet.equals(ERet.RET0.getRet())) {
			Parser_SDF_PostReport parser = new Parser_SDF_PostReport();
			param = parser.doParser(eqxRawData, afInstance, afSubIns);	
			afSubIns.setSubResultCodeEDR(param.getResultCode());
			afSubIns.setSubResultDescEDR(param.getResultDescription());
			isValid = param.getIsValid();
			if (isValid) {

				AFLog.d("getResultCode: \"" + param.getResultCode() + "\"");
				if (param.getResultCode().equals(EResultCode.RE20000.getResultCode())) {		
					resultCode = EResultCode.RE20000;
					internalCode = EResultCode.RE20000;
					
					long resTime = new Duration(Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampOut()),
							Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampIncoming())).getMillis();
					statsIn = EStats.APP_RECV_SDF_GET_W_SDF_POSTREPORT_RESPONSE_SUCCESS;
					EStats statexe = EStats.APP_RECV_SDF_GET_W_SDF_POSTREPORT_EXE_TIME;
					statexe.setExeTimeCustomStat(statexe, nodeName, resTime);
					afSubIns.setStatsExeTime(statexe);
				} else {
					resultCode = EResultCode.RE50000;
					internalCode = EResultCode.RE50000;
					statsIn = EStats.APP_RECV_SDF_GET_W_SDF_POSTREPORT_RESPONSE_SUCCESS_WITH_ERROR;
					recoveryConf = Config.getPATH_ES22_RECOVERYLOG_ERROR();
				}
			} else if (param.getResultCode()==null || param.getResultDescription()==null) {
				resultCode = EResultCode.RE40300;
				internalCode = EResultCode.RE40300;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_POSTREPORT_RESPONSE_SUCCESS_WITH_ERROR;
				recoveryConf = Config.getPATH_ES22_RECOVERYLOG_ERROR();
			} else if (param.getResultCode().equals("") || param.getResultDescription().equals("")) {
				resultCode = EResultCode.RE40300;
				internalCode = EResultCode.RE40300;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_POSTREPORT_RESPONSE_SUCCESS_WITH_ERROR;
				recoveryConf = Config.getPATH_ES22_RECOVERYLOG_ERROR();
			} else if (param.getResultCode().equals(EResultCode.RE40401.getResultCode())) {
				resultCode = EResultCode.RE40401;
				internalCode = EResultCode.RE40401;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_POSTREPORT_RESPONSE_SUCCESS_WITH_ERROR;
				recoveryConf = Config.getPATH_ES22_RECOVERYLOG_ERROR();
			} else {
				AFLog.d("getResultCode not equals RE20000 or RE40401");
				resultCode = EResultCode.RE50000;
				internalCode = EResultCode.RE50000;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_POSTREPORT_RESPONSE_SUCCESS_WITH_ERROR;
				recoveryConf = Config.getPATH_ES22_RECOVERYLOG_ERROR();
			}
		} else {
			
			if (rawDataRet.equals(ERet.RET4.getRet())) {
				resultCode = EResultCode.RE00004;
				internalCode = EResultCode.RE50000;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_POSTREPORT_RESPONSE_TIMEOUT;
				recoveryConf = Config.getPATH_ES22_RECOVERYLOG_TIMEOUT();
				afSubIns.setSubResultCodeEDR(resultCode.getResultCode());
				afSubIns.setSubResultDescEDR(resultCode.getResultDesc());
			} else if (rawDataRet.equals(ERet.RET2.getRet())) {
				resultCode = EResultCode.RE00002;
				internalCode = EResultCode.RE50000;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_POSTREPORT_RESPONSE_REJECT;
				recoveryConf = Config.getPATH_ES22_RECOVERYLOG_TIMEOUT();
				afSubIns.setSubResultCodeEDR(resultCode.getResultCode());
				afSubIns.setSubResultDescEDR(resultCode.getResultDesc());
			} else {
				resultCode = EResultCode.RE50000;
				internalCode = EResultCode.RE50000;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_POSTREPORT_RESPONSE_UNKNOWN_ERROR;
				recoveryConf = Config.getPATH_ES22_RECOVERYLOG_TIMEOUT();
				afSubIns.setSubResultCodeEDR(resultCode.getResultCode());
				afSubIns.setSubResultDescEDR(resultCode.getResultDesc());
			}
		}
		
		// -- RecoveryLog --//
		if (!resultCode.getResultCode().equals(EResultCode.RE20000.getResultCode())) {
			 afSubIns.setRecoveryConf(recoveryConf);
			 LogUtils.writeLogSDF(abstractAF, afSubIns);
		}
		
	
		afSubIns.setSubCountChild(afSubIns.getSubCountChild() - 1);
		afSubIns.setSubResultCode(resultCode.getResultCode());
		afSubIns.setSubIsValid(isValid);
		afSubIns.setSubInternalCode(internalCode.getResultCode());
		afSubIns.setSubNextState(ESubState.Unknown.toString());
		afSubIns.setSubCurrentState(ESubState.SDF_POSTREPORT.getState());
		afSubIns.setSubControlState(ESubState.SDF_POSTREPORT.getState());
		
		// -- Stats --//
		statsIn.setCustomStat(statsIn, nodeName);
		afSubIns.setStatsIn(statsIn);
		// -- EDR --//
		DateTime timeStampEnd = new DateTime();
		afSubIns.setTimeStampEnd(timeStampEnd);
		
		try {
			LogUtils.writeLogEDR(abstractAF, afSubIns, afInstance, eqxRawData);
			LogUtils.writeLogCDR(abstractAF, afSubIns, null);
		} catch (Exception e1) {
			AFLog.e("[Exception] writeLogEDR error.");
			AFLog.e(e1);
		}

		return afSubIns;
	}
}
