package ct.af.extracterd;

import org.joda.time.Duration;
import ct.af.enums.EResultCode;
import ct.af.enums.ERet;
import ct.af.enums.EStats;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_SDF_ReleaseQuota;
import ct.af.message.incoming.parser.Parser_SDF_ReleaseQuota;
import ct.af.utils.Config;
import ct.af.utils.LogUtils;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class In_SDF_ReleaseQuota {
	public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns,
			EquinoxRawData eqxRawData) {
		AFLog.d("In_SDF_ReleaseQuota");
		String rawDataRet = eqxRawData.getRet();
		String rawDataECode = eqxRawData.getRawDataAttribute("ecode");
		String rawDataval = eqxRawData.getRawDataAttribute("val");

		EResultCode resultCode;
		EResultCode internalCode;

		String insNo = afSubIns.getSubInitOrig().split("\\.")[afSubIns.getSubInitOrig().split("\\.").length - 1];
		String nodeName = "AISSDF_" + insNo;
		EStats statsIn; 

		boolean isValid = false;

		Param_SDF_ReleaseQuota param;

		if (rawDataval != null && !rawDataval.equals("") && rawDataECode.equals("200")
				&& rawDataRet.equals(ERet.RET0.getRet())) {

			Parser_SDF_ReleaseQuota parser = new Parser_SDF_ReleaseQuota();
			param = parser.doParser(eqxRawData, afInstance, afSubIns);
			isValid = param.getIsValid();

			if (isValid) {

				AFLog.d("getResultCode: \"" + param.getResultCode() + "\"");
				if (param.getResultCode().equals(EResultCode.RE20000.getResultCode())) {
					resultCode = EResultCode.RE20000;
					internalCode = EResultCode.RE20000;
					statsIn = EStats.APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_SUCCESS;

					long resTime = new Duration(
							Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampOut()),
							Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampIncoming())).getMillis();
					EStats statexe = EStats.APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_EXE_TIME;
					statexe.setExeTimeCustomStat(statexe, nodeName, resTime);
					afSubIns.setStatsExeTime(statexe);

				} else if (param.getResultCode().equals("") || param.getResultDesc().equals("")) {
					// afSubIns.setErrorMessage(" SDF_GETREPORT MISSING OR
					// INVALID DATA");
					// statIn =
					// EStats.APP_RECEIVE_SDF_GETREPORT_RESPONSE_MISSING;

					resultCode = EResultCode.RE40300;
					internalCode = EResultCode.RE40300;
					statsIn = EStats.APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_SUCCESS_WITH_ERROR;
				} else if (param.getResultCode().equals(EResultCode.RE40401.getResultCode())) {
					// afSubIns.setErrorMessage(" SDF_GETREPORT DATA NOT
					// FOUND");
					resultCode = EResultCode.RE40401;
					internalCode = EResultCode.RE40401;
					statsIn = EStats.APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_SUCCESS_WITH_ERROR;
				} else {
					// afSubIns.setErrorMessage(" SDF_GETREPORT " +
					// param.getResultDesc());
					AFLog.d("getResultCode not equals RE20000 or RE40401");
					resultCode = EResultCode.RE50000;
					internalCode = EResultCode.RE50000;
					statsIn = EStats.APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_SUCCESS_WITH_ERROR;
				}
				afSubIns.setSubEDRResultCode((param.getResultCode() != null && param.getResultCode() != "")
						? param.getResultCode() : resultCode.getResultCode());
				// edrErrDesc =
				// (param.getResultDesc()!=null&&param.getResultDesc()!=
				// "")?param.getResultDesc():resultCode.geteDesc();

			} else {
				// afSubIns.setErrorMessage(" SDF_GETREPORT MISSING OR INVALID
				// DATA");
				// statIn = EStats.APP_RECEIVE_SDF_GETREPORT_RESPONSE_MISSING;

				resultCode = EResultCode.RE40300;
				internalCode = EResultCode.RE40300;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_SUCCESS_WITH_ERROR;

				if (param.getResultCode() == null || param.getResultCode().equals("")) {
					AFLog.d("getResultCode: \"" + "\" is null");
					// edrErrDesc = resultCode.geteDesc();
				} else {
					AFLog.d("getResultCode: \"" + param.getResultCode() + "\" is invalid");
					// edrErrDesc = param.getResultDesc();
				}

			}

		} else {

			if (rawDataRet.equals(ERet.RET4.getRet())) {
				// afSubIns.setErrorMessage(" SDF_GETREPORT TIMEOUT");
				// statIn = EStats.APP_RECEIVE_SDF_GETREPORT_RESPONSE_TIMEOUT;
				// abstractAF.getEquinoxUtils().incrementStats(EStats.APP_RECEIVE_SDF_RESPONSE_TIMEOUT.getStatName());
				resultCode = EResultCode.RE00004;
				internalCode = EResultCode.RE50000;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_TIMEOUT;
			} else if (rawDataRet.equals(ERet.RET2.getRet())) {
				// afSubIns.setErrorMessage(" SDF_GETREPORT REJECTED");
				// statIn = EStats.APP_RECEIVE_SDF_GETREPORT_RESPONSE_REJECT;
				// abstractAF.getEquinoxUtils().incrementStats(EStats.APP_RECEIVE_SDF_RESPONSE_REJECT.getStatName());
				resultCode = EResultCode.RE00002;
				internalCode = EResultCode.RE50000;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_REJECT;
			} else if (rawDataECode.equals("503")) {
				// afSubIns.setErrorMessage(" SDF_GETREPORT ERROR");
				// statIn = EStats.APP_RECEIVE_SDF_GETREPORT_RESPONSE_ERROR;
				// abstractAF.getEquinoxUtils().incrementStats(EStats.APP_RECEIVE_SDF_RESPONSE_ERROR.getStatName());
				resultCode = EResultCode.RE50000;
				internalCode = EResultCode.RE50000;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_UNKNOWN_ERROR;

			} else if (!rawDataECode.equals("200")) {
				// afSubIns.setErrorMessage(" SDF_GETREPORT SYSTEM ERROR");
				// statIn = EStats.APP_RECEIVE_SDF_GETREPORT_RESPONSE_ERROR;
				// abstractAF.getEquinoxUtils().incrementStats(EStats.APP_RECEIVE_SDF_RESPONSE_ERROR.getStatName());
				resultCode = EResultCode.RE50000;
				internalCode = EResultCode.RE50000;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_UNKNOWN_ERROR;
			} else {
				// afSubIns.setErrorMessage(" SDF_GETREPORT OTHER ERROR");
				// statIn = EStats.APP_RECEIVE_SDF_GETREPORT_RESPONSE_ERROR;
				// abstractAF.getEquinoxUtils().incrementStats(EStats.APP_RECEIVE_SDF_RESPONSE_ERROR.getStatName());
				resultCode = EResultCode.RE50000;
				internalCode = EResultCode.RE50000;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_UNKNOWN_ERROR;
			}
			// edrErrDesc = resultCode.geteDesc();

		}

		afSubIns.setSubCountChild(afSubIns.getSubCountChild() - 1);
		// ec02Instance.getAFInstance().setMainSDFReportParam(param);

		afSubIns.setSubIsValid(isValid);
		afSubIns.setSubResultCode(resultCode.getResultCode());
		afSubIns.setSubInternalCode(internalCode.getResultCode());

		afSubIns.setSubNextState(ESubState.Unknown.toString());
		afSubIns.setSubCurrentState(ESubState.SDF_RELEASEQUOTA.toString());
		afSubIns.setSubControlState(ESubState.SDF_RELEASEQUOTA.toString());

		// afSubIns.setSubFunctionGroup(EFunctionGroup.IncomingFromClient.toString());

		// -- Stats --//
		statsIn.setCustomStat(statsIn, nodeName);
		afSubIns.setStatsIn(statsIn);

		// -- EDR --//
		try {
			LogUtils.writeLogEDR(abstractAF, afSubIns, afInstance, eqxRawData);
		} catch (Exception e1) {
			AFLog.e("[Exception] writeLogEDR error.");
			AFLog.e(e1);
		}

		return afSubIns;
	}
}
