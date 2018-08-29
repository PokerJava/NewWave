package ct.af.extracterd;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Duration;
import ct.af.enums.EResultCode;
import ct.af.enums.ERet;
import ct.af.enums.EStats;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.resourceModel.ResourceInfraNode;
import ct.af.utils.Config;
import ct.af.utils.ErrorMessageUtil;
import ct.af.utils.LogUtils;
import ct.af.message.incoming.parameter.Param_SDF_GetResourceInfraNode;
import ct.af.message.incoming.parser.Parser_SDF_GetResourceInfraNode;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class In_SDF_GetResourceInfraNode {
    public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance resourceOrderIns, EquinoxRawData eqxRawData) {
        String rawDataRet = eqxRawData.getRet();
        String rawDataVal = eqxRawData.getRawDataAttribute("val");
        String rawDataInvoke = eqxRawData.getInvoke();
        String neId = resourceOrderIns.getMapNeidWithInvoke().get(rawDataInvoke);

        EResultCode resultCode = EResultCode.RE20000;
        EResultCode internalCode = EResultCode.RE20000;

        String insNo = resourceOrderIns.getSubInitOrig().split("\\.")[resourceOrderIns.getSubInitOrig().split("\\.").length-1];
        String nodeName = "AISSDF_"+insNo;


        EStats statsIn;

        boolean isValid = false;

        Param_SDF_GetResourceInfraNode param;
        if(rawDataVal != null && !rawDataVal.equals("") && rawDataRet.equals(ERet.RET0.getRet())) {
            Parser_SDF_GetResourceInfraNode parser = new Parser_SDF_GetResourceInfraNode();
            param = parser.doParser(eqxRawData, afInstance, resourceOrderIns);
            isValid = param.getIsValid();
            resourceOrderIns.setSubResultCodeEDR(param.getResultCode());
            resourceOrderIns.setSubResultDescEDR(param.getResultDescription());

            if(isValid) {
                if (param.getResultCode() != null && param.getResultCode().equals(EResultCode.RE20000.getResultCode())) {
                    if (param.getCadResourceInfraInventory() != null && param.getCadResourceInfraInventory().size() > 0) {
                        for (ResourceInfraNode resourceInfraNode : param.getCadResourceInfraInventory()) {
                            resourceOrderIns.getResourceInfraNodeHashMap().put(neId, resourceInfraNode);
                        }
                        statsIn =EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINFRANODE_RESPONSE_SUCCESS;
                        long resTime = new Duration(Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampOut()), Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampIncoming())).getMillis();
                        EStats statexe = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINFRANODE_EXE_TIME;
                        statexe.setExeTimeCustomStat(statexe,nodeName,resTime);
                        resourceOrderIns.setStatsExeTime(statexe);

                    } else {
                        resourceOrderIns.setErrorMessage(ErrorMessageUtil.appendWithSemicolon(resourceOrderIns.getErrorMessage(), neId + " cannot get information from SDF_GETRESOURCEINFRANODE."));
                        resultCode = EResultCode.RE40300;
                        internalCode = EResultCode.RE50000;
                        statsIn =EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINFRANODE_RESPONSE_SUCCESS_WITH_ERROR;
                    }
                } else {
                    String sdfResultCode = StringUtils.isNotBlank(param.getResultCode()) ? param.getResultCode().trim() : "";
                    String sdfResultDesc  = StringUtils.isNotBlank(param.getResultDescription()) ? param.getResultDescription().trim() : "";
                    String sdfDiagMsg = StringUtils.isNotBlank(param.getDiagnosticMessage()) ? param.getDiagnosticMessage().trim() : "";

                    if (StringUtils.isBlank(sdfResultCode) && StringUtils.isBlank(sdfResultDesc) && StringUtils.isBlank(sdfDiagMsg)) {
                        resourceOrderIns.setErrorMessage(ErrorMessageUtil.appendWithSemicolon(resourceOrderIns.getErrorMessage(), neId + " no resultCode resultDescription and diagnosticMessage from SDF_GETRESOURCEINFRANODE."));
                    } else if (StringUtils.isNotBlank(sdfResultCode) && StringUtils.isBlank(sdfResultDesc) && StringUtils.isBlank(sdfDiagMsg)) {
                        resourceOrderIns.setErrorMessage(ErrorMessageUtil.appendWithSemicolon(resourceOrderIns.getErrorMessage(), neId + " " + sdfResultCode + " from SDF_GETRESOURCEINFRANODE."));
                    } else {
                        resourceOrderIns.setErrorMessage(ErrorMessageUtil.appendWithSemicolon(resourceOrderIns.getErrorMessage(), neId + " " + sdfResultCode + ", " + sdfResultDesc + "_" + sdfDiagMsg +  " from SDF_GETRESOURCEINFRANODE."));
                    }
                    resourceOrderIns.setResultDesc(sdfResultDesc);
                    resultCode = EResultCode.RE50000;
                    internalCode = EResultCode.RE50000;
                    statsIn =EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINFRANODE_RESPONSE_SUCCESS_WITH_ERROR;
                }
            } else {
                resourceOrderIns.setErrorMessage(ErrorMessageUtil.appendWithSemicolon(resourceOrderIns.getErrorMessage(), neId + " cannot parse incoming message from SDF_GETRESOURCEINFRANODE."));
                resultCode = EResultCode.RE40300;
                internalCode = EResultCode.RE50000;
                statsIn =EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINFRANODE_RESPONSE_SUCCESS_WITH_ERROR;
            }
            resourceOrderIns.setSubEDRResultCode((param.getResultCode()!=null&&param.getResultCode()!="")?param.getResultCode():resultCode.getResultCode());
        } else {
            if(rawDataRet.equals(ERet.RET4.getRet())) {
                resourceOrderIns.setErrorMessage(ErrorMessageUtil.appendWithSemicolon(resourceOrderIns.getErrorMessage(), neId + " cannot get information due to SDF_GETRESOURCEINFRANODE timeout."));
                resultCode = EResultCode.RE00004;
                internalCode = EResultCode.RE50000;
                statsIn =EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINFRANODE_RESPONSE_TIMEOUT;
                resourceOrderIns.setSubResultCodeEDR(resultCode.getResultCode());
                resourceOrderIns.setSubResultDescEDR(resultCode.getResultDesc());
            } else if(rawDataRet.equals(ERet.RET2.getRet())) {
                resourceOrderIns.setErrorMessage(ErrorMessageUtil.appendWithSemicolon(resourceOrderIns.getErrorMessage(), neId + " cannot get information due to SDF_GETRESOURCEINFRANODE reject."));
                resultCode = EResultCode.RE00002;
                internalCode = EResultCode.RE50000;
                statsIn =EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINFRANODE_RESPONSE_REJECT;
                resourceOrderIns.setSubResultCodeEDR(resultCode.getResultCode());
                resourceOrderIns.setSubResultDescEDR(resultCode.getResultDesc());
            } else {
                resourceOrderIns.setErrorMessage(ErrorMessageUtil.appendWithSemicolon(resourceOrderIns.getErrorMessage(), neId + " other error from SDF_GETRESOURCEINFRANODE."));
                resultCode = EResultCode.RE50000;
                internalCode = EResultCode.RE50000;
                statsIn =EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINFRANODE_RESPONSE_UNKNOWN_ERROR;
                resourceOrderIns.setSubResultCodeEDR(resultCode.getResultCode());
                resourceOrderIns.setSubResultDescEDR(resultCode.getResultDesc());
            }
        }

        // clear map for reuse in reserveQuota
        resourceOrderIns.getMapNeidWithInvoke().remove(neId);

        resourceOrderIns.setSubCountChild(resourceOrderIns.getSubCountChild() - 1);

        resourceOrderIns.setSubIsValid(isValid);
        resourceOrderIns.setSubResultCode(resultCode.getResultCode());
        resourceOrderIns.setSubInternalCode(internalCode.getResultCode());

        resourceOrderIns.setSubNextState(ESubState.Unknown.toString());
        resourceOrderIns.setSubCurrentState(ESubState.SDF_GETRESOURCEINFRANODE.toString());
        resourceOrderIns.setSubControlState(ESubState.SDF_GETRESOURCEINFRANODE.toString());
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
