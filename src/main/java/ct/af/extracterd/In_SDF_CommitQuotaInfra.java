package ct.af.extracterd;

import org.joda.time.Duration;
import ct.af.enums.*;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_SDF_CommitQuotaInfra;
import ct.af.message.incoming.parser.Parser_SDF_CommitQuotaInfra;
import ct.af.utils.Config;
import ct.af.utils.LogUtils;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class In_SDF_CommitQuotaInfra {
    public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns,
                                        EquinoxRawData eqxRawData) {
        AFLog.d("In_SDF_CommitQuotaInfra");
        String rawDataRet = eqxRawData.getRet();
        String rawDataECode = eqxRawData.getRawDataAttribute("ecode");
        String rawDataval = eqxRawData.getRawDataAttribute("val");

        EResultCode resultCode;
        EResultCode internalCode;
        EStats statsIn;
        
        String insNo = afSubIns.getSubInitOrig().split("\\.")[afSubIns.getSubInitOrig().split("\\.").length-1];
		String nodeName = "AISSDF_"+insNo;

        boolean isValid = false;
        Param_SDF_CommitQuotaInfra param;

        if(rawDataval != null && !rawDataval.equals("")
                && rawDataECode.equals("200")
                && rawDataRet.equals(ERet.RET0.getRet())) {

            Parser_SDF_CommitQuotaInfra parser = new Parser_SDF_CommitQuotaInfra();
            param = parser.doParser(eqxRawData, afInstance, afSubIns);

            isValid = param.getIsValid();

            if(isValid) {

                AFLog.d("getResultCode: \"" + param.getResultCode() + "\"");
                if(param.getResultCode().equals(EResultCode.RE20000.getResultCode()))
                {
                	resultCode = EResultCode.RE20000;
                    internalCode = EResultCode.RE20000;
                    statsIn = EStats.APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_SUCCESS;
                    
                    long resTime = new Duration(Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampOut()), Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampIncoming())).getMillis();
        			EStats statexe = EStats.APP_RECV_SDF_PUT_COMMITQUOTALNFRA_EXE_TIME;
        			statexe.setExeTimeCustomStat(statexe, nodeName,  resTime);
        			afSubIns.setStatsExeTime(statexe);	
                }
                else if(param.getResultCode()==null || param.getResultDesc()==null)
                {
                    resultCode = EResultCode.RE40300;
                    internalCode = EResultCode.RE40300;
                    statsIn = EStats.APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_SUCCESS_WITH_ERROR;		
                }
                else if(param.getResultCode().equals("") || param.getResultDesc().equals(""))
                {
                    resultCode = EResultCode.RE40300;
                    internalCode = EResultCode.RE40300;
                    statsIn = EStats.APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_SUCCESS_WITH_ERROR;		
                }
                else if(param.getResultCode().equals(EResultCode.RE40401.getResultCode()))
                {
                    resultCode = EResultCode.RE40401;
                    internalCode = EResultCode.RE40401;
                    statsIn = EStats.APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_SUCCESS_WITH_ERROR;		
                }
                else
                {
                    AFLog.d("getResultCode not equals RE20000 or RE40401");
                    resultCode = EResultCode.RE50000;
                    internalCode = EResultCode.RE50000;
                    statsIn = EStats.APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_SUCCESS_WITH_ERROR;
                }
                afSubIns.setSubEDRResultCode((param.getResultCode()!=null&&param.getResultCode()!="")?param.getResultCode():resultCode.getResultCode());

            } else {
                resultCode = EResultCode.RE40300;
                internalCode = EResultCode.RE40300;
                statsIn = EStats.APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_SUCCESS_WITH_ERROR;
                if(param.getResultCode()==null || param.getResultCode().equals("")){
                    AFLog.d("getResultCode: \"" + "\" is null");
                	resultCode.getResultCode();
                }else{
                    AFLog.d("getResultCode: \"" + param.getResultCode() + "\" is invalid");
                	param.getResultCode();
                }                
            }     

        } else {
            if(rawDataRet.equals(ERet.RET4.getRet())) {
                resultCode = EResultCode.RE00004;
                internalCode = EResultCode.RE50000;
                resultCode.getResultCode();
                statsIn = EStats.APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_RESPONSE_TIMEOUT;                
            } else if(rawDataRet.equals(ERet.RET2.getRet())) {
                resultCode = EResultCode.RE00002;
                internalCode = EResultCode.RE50000;
                resultCode.getResultCode();
                statsIn = EStats.APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_REJECT;
            } else if(rawDataECode.equals("503")) {
                resultCode = EResultCode.RE50000;
                internalCode = EResultCode.RE50000;
                statsIn = EStats.APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_UNKNOWN_ERROR;	
            } else if(!rawDataECode.equals("200")) {
                resultCode = EResultCode.RE50000;
                internalCode = EResultCode.RE50000;
                statsIn = EStats.APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_UNKNOWN_ERROR;		
            } else {
                resultCode = EResultCode.RE50000;
                internalCode = EResultCode.RE50000;
                statsIn = EStats.APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_UNKNOWN_ERROR;	
            }
        }

        afSubIns.setSubCountChild(afSubIns.getSubCountChild()-1);
        afSubIns.setSubIsValid(isValid);
        afSubIns.setSubResultCode(resultCode.getResultCode());
        afSubIns.setSubInternalCode(internalCode.getResultCode());
        afSubIns.setSubNextState(ESubState.Unknown.toString());
        afSubIns.setSubCurrentState(ESubState.SDF_COMMITQUOTAINFRA.toString());
        afSubIns.setSubControlState(ESubState.SDF_COMMITQUOTAINFRA.toString());

        //-- Stats --//	
		statsIn.setCustomStat(statsIn, nodeName);
		afSubIns.setStatsIn(statsIn);	


        //-- EDR --//        
        try {
            LogUtils.writeLogEDR(abstractAF, afSubIns, afInstance, eqxRawData);
        }catch (Exception e1) {
            AFLog.e("[Exception] writeLogEDR error.");
            AFLog.e(e1);
        }               
        
        return afSubIns;
    }
}
