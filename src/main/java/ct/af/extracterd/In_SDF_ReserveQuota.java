package ct.af.extracterd;

import org.joda.time.Duration;
import ct.af.enums.EResultCode;
import ct.af.enums.ERet;
import ct.af.enums.EStats;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.utils.Config;
import ct.af.message.incoming.parameter.Param_SDF_ReserveQuota;
import ct.af.message.incoming.parser.Parser_SDF_ReserveQuota;
import ct.af.utils.ErrorMessageUtil;
import ct.af.utils.LogUtils;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class In_SDF_ReserveQuota {
    public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance resourceOrderIns, EquinoxRawData eqxRawData) {
        String rawDataRet = eqxRawData.getRet();
        String rawDataVal = eqxRawData.getRawDataAttribute("val");
        String rawDataInvoke = eqxRawData.getInvoke();
        String neId = resourceOrderIns.getMapNeidWithInvoke().get(rawDataInvoke);

        String insNo = resourceOrderIns.getSubInitOrig().split("\\.")[resourceOrderIns.getSubInitOrig().split("\\.").length-1];
        String nodeName = "AISSDF_"+insNo;
        EStats statsIn;

        EResultCode resultCode = EResultCode.RE20000;
        EResultCode internalCode = EResultCode.RE20000;
        boolean isValid = false;


        Param_SDF_ReserveQuota param;
        if(rawDataVal != null && !rawDataVal.equals("") && rawDataRet.equals(ERet.RET0.getRet())) {
            Parser_SDF_ReserveQuota parser = new Parser_SDF_ReserveQuota();
            param = parser.doParser(eqxRawData, afInstance, resourceOrderIns);
            isValid = param.getIsValid();

            if(isValid) {
                String sdfResultCode = param.getResultCode() != null ? param.getResultCode() : "";
                String sdfResultDesc  = param.getResultDescription() != null ? param.getResultDescription() : "";
                String sdfDiagMsg = param.getDiagnosticMessage() != null ? param.getDiagnosticMessage() : "";

                if (sdfResultCode.equals(EResultCode.RE20000.getResultCode())) {
                    if (!sdfResultDesc.equalsIgnoreCase(EResultCode.RE20000.getResultStatus())) {
                        resourceOrderIns.setErrorMessage(ErrorMessageUtil.appendWithSemicolon(resourceOrderIns.getErrorMessage(), neId + " " + sdfResultCode + ", " + sdfResultDesc + "_" + sdfDiagMsg +  " from SDF_RESERVEQUOTA."));
                        resultCode = EResultCode.RE50000;
                        internalCode = EResultCode.RE50000;
                        statsIn = EStats.APP_RECV_SDF_GET_W_SDF_RESERVEQUOTA_RESPONSE_SUCCESS_WITH_ERROR;
                    } else {
                        statsIn = EStats.APP_RECV_SDF_GET_W_SDF_RESERVEQUOTA_RESPONSE_SUCCESS;
                        long resTime = new Duration(Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampOut()), Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampIncoming())).getMillis();
                        EStats statexe = EStats.APP_RECV_SDF_GET_W_SDF_RESERVEQUOTA_EXE_TIME;
                        statexe.setExeTimeCustomStat(statexe,nodeName, resTime);
                        resourceOrderIns.setStatsExeTime(statexe);
                    }
                } else {
                    resourceOrderIns.setErrorMessage(ErrorMessageUtil.appendWithSemicolon(resourceOrderIns.getErrorMessage(), neId + " " + sdfResultCode + ", " + sdfResultDesc + "_" + sdfDiagMsg +  " from SDF_RESERVEQUOTA."));
                    resultCode = EResultCode.RE50000;
                    internalCode = EResultCode.RE50000;

                    statsIn =EStats.APP_RECV_SDF_GET_W_SDF_RESERVEQUOTA_RESPONSE_SUCCESS_WITH_ERROR;
                }
            } else {
                resourceOrderIns.setErrorMessage(ErrorMessageUtil.appendWithSemicolon(resourceOrderIns.getErrorMessage(), neId + " cannot parse incoming message from SDF_RESERVEQUOTA."));
                resultCode = EResultCode.RE40300;
                internalCode = EResultCode.RE50000;
                statsIn =EStats.APP_RECV_SDF_GET_W_SDF_RESERVEQUOTA_RESPONSE_SUCCESS_WITH_ERROR;
            }
            resourceOrderIns.setSubEDRResultCode((param.getResultCode()!=null&&param.getResultCode()!="")?param.getResultCode():resultCode.getResultCode());
        } else {
            if(rawDataRet.equals(ERet.RET4.getRet())) {
                resourceOrderIns.setErrorMessage(ErrorMessageUtil.appendWithSemicolon(resourceOrderIns.getErrorMessage(), neId + " cannot reserve quota due to SDF_RESERVEQUOTA timeout."));
                resultCode = EResultCode.RE00004;
                internalCode = EResultCode.RE50000;
                statsIn=EStats.APP_RECV_SDF_GET_W_SDF_RESERVEQUOTA_RESPONSE_TIMEOUT;
            } else if(rawDataRet.equals(ERet.RET2.getRet())) {
                resourceOrderIns.setErrorMessage(ErrorMessageUtil.appendWithSemicolon(resourceOrderIns.getErrorMessage(), neId + " cannot reserve quota due to SDF_RESERVEQUOTA reject."));
                resultCode = EResultCode.RE00002;
                internalCode = EResultCode.RE50000;
                statsIn=EStats.APP_RECV_SDF_GET_W_SDF_RESERVEQUOTA_RESPONSE_REJECT;
            } else {
                resourceOrderIns.setErrorMessage(ErrorMessageUtil.appendWithSemicolon(resourceOrderIns.getErrorMessage(), neId + " other error from SDF_RESERVEQUOTA."));
                resultCode = EResultCode.RE50000;
                internalCode = EResultCode.RE50000;
                statsIn=EStats.APP_RECV_SDF_GET_W_SDF_RESERVEQUOTA_RESPONSE_UNKNOWN_ERROR;
            }
        }

        // clear map
        resourceOrderIns.getMapNeidWithInvoke().remove(neId);

        resourceOrderIns.setSubCountChild(resourceOrderIns.getSubCountChild() - 1);

        resourceOrderIns.setSubResultCode(resultCode.getResultCode());
        resourceOrderIns.setSubInternalCode(internalCode.getResultCode());

        resourceOrderIns.setSubNextState(ESubState.Unknown.toString());
        resourceOrderIns.setSubCurrentState(ESubState.SDF_RESERVEQUOTA.toString());
        resourceOrderIns.setSubControlState(ESubState.SDF_RESERVEQUOTA.toString());
        statsIn.setCustomStat(statsIn,nodeName);
        resourceOrderIns.setStatsIn(statsIn);

        //-- EDR --//
        try {
            LogUtils.writeLogEDR(abstractAF, resourceOrderIns, afInstance, eqxRawData);
        } catch (Exception e1) {
            AFLog.e("[Exception] writeLogEDR error.");
            AFLog.e(e1);
        }

        return resourceOrderIns;
    }
}
