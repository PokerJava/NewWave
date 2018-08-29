package ct.af.extracterd;

import ct.af.instance.AFInstance;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import ct.af.enums.ERequestState;
import ct.af.enums.EResultCode;
import ct.af.enums.ERet;
import ct.af.enums.EStats;
import ct.af.enums.ESubState;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_CLIENT_PostResult;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.message.incoming.parser.Parser_CLIENT_PostResult;
import ct.af.utils.Config;
import ct.af.utils.LogUtils;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class In_CLIENT_PostResult {
    public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns,
                                        EquinoxRawData eqxRawData) {
//    	DateTimeFormatter subEffectiveTime = DateTimeFormat.forPattern("yyyyMMddHHmmssZ");
//    	DateTimeFormatter subEffectiveTime2 = Config.formatDateWithMiTz;
    	Param_IDLE_ResourceOrder clientParam = (Param_IDLE_ResourceOrder) afSubIns.getSubClientParameter();
//    	DateTime timeStamp = new DateTime();
    	String state;
    	String rawDataRet = eqxRawData.getRet();
        String rawDataECode = eqxRawData.getRawDataAttribute("ecode");
        String rawDataVal = eqxRawData.getRawDataAttribute("val");
        String serversInterface = Config.getServerInterfaceHashMap().get(clientParam.getUserSys().trim());
        Param_CLIENT_PostResult paramPostResult;
        
        EResultCode resultCode;
        EResultCode internalCode;
        EStats statsIn = EStats.APP_RECV_W_CLIENT_POSTRESULT_RESPONSE_SUCCESS;
        boolean isValid = false;
//        afInstance.setMainTimeStampIncoming(subEffectiveTime2.print(timeStamp));
        long resTime = new Duration(Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampOut()), Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampIncoming())).getMillis();
        
        if(StringUtils.isNotBlank(rawDataVal) && rawDataECode.equals("200") && rawDataRet.equals(ERet.RET0.getRet())) {
        	
        	Parser_CLIENT_PostResult parser = new Parser_CLIENT_PostResult();
            paramPostResult = parser.doParser(eqxRawData, afInstance, afSubIns);
            state = afSubIns.getState();
            
            if(StringUtils.isNotBlank(paramPostResult.getResultCode()) && paramPostResult.getResultCode().equals(EResultCode.RE20000.getResultCode())){
        	
                afSubIns.setSubackRespRawData(eqxRawData.getRawDataMessage().replace("&quot;","\"").trim());
                DateTime subArckRespDate = Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampIncoming());
                afSubIns.setSubackRespDate(Config.getJourneyDateFormat().print(subArckRespDate));
                  
                isValid = paramPostResult.getIsValid();
                
                if(isValid){
                    resultCode = EResultCode.RE20000;
                    internalCode = EResultCode.RE20000;
                    statsIn.setCustomStat(EStats.APP_RECV_W_CLIENT_POSTRESULT_RESPONSE_SUCCESS,serversInterface);
                    afSubIns.setStatsIn(statsIn);
                    EStats statsExeTime = EStats.APP_RECV_W_CLIENT_POSTRESULT_EXE_TIME;
    				statsExeTime.setExeTimeCustomStat(EStats.APP_RECV_W_CLIENT_POSTRESULT_EXE_TIME,serversInterface,resTime);
    				afSubIns.setStatsExeTime(statsExeTime);
                } else {
                    resultCode = EResultCode.RE40300;
                    internalCode = EResultCode.RE50000;
                    statsIn.setCustomStat(EStats.APP_RECV_W_CLIENT_POSTRESULT_RESPONSE_UNKNOWN_ERROR,serversInterface);
                    afSubIns.setStatsIn(statsIn);
                }
        		
        	} else {
                resultCode = EResultCode.RE50000;
                internalCode = EResultCode.RE50000;
                statsIn.setCustomStat(EStats.APP_RECV_W_CLIENT_POSTRESULT_RESPONSE_UNKNOWN_ERROR,serversInterface);
                afSubIns.setStatsIn(statsIn);
        	}
        	
        } else {
        	if(rawDataRet.equals(ERet.RET4.getRet())) {
        		resultCode = EResultCode.RE00004;
                internalCode = EResultCode.RE50000;
                statsIn.setCustomStat(EStats.APP_RECV_W_CLIENT_POSTRESULT_RESPONSE_TIMEOUT,serversInterface);
                
        	} else if(rawDataRet.equals(ERet.RET2.getRet())) {
                resultCode = EResultCode.RE00002;
                internalCode = EResultCode.RE50000;
                statsIn.setCustomStat(EStats.APP_RECV_W_CLIENT_POSTRESULT_RESPONSE_REJECT,serversInterface);
                
        	} else {
                resultCode = EResultCode.RE50000;
                internalCode = EResultCode.RE50000;
                statsIn.setCustomStat(EStats.APP_RECV_W_CLIENT_POSTRESULT_RESPONSE_UNKNOWN_ERROR,serversInterface);
                
        	}
        	paramPostResult = null;
        	afSubIns.setStatsIn(statsIn);
        	state = ERequestState.NOTCOMPLETED.getState();
        }

        afSubIns.setSubCountChild(afSubIns.getSubCountChild()-1);

        afSubIns.setSubIsValid(isValid);
        afSubIns.setSubResultCode(resultCode.getResultCode());
        afSubIns.setSubInternalCode(internalCode.getResultCode());
        afSubIns.setResHrzRawData(rawDataVal);
        afSubIns.setState(state);
        AFLog.d("State : " + afSubIns.getState());

        afSubIns.setSubNextState(ESubState.Unknown.toString());
        afSubIns.setSubCurrentState(ESubState.CLIENT_POSTRESULT.toString());
        afSubIns.setSubControlState(ESubState.CLIENT_POSTRESULT.toString());
        afSubIns.setParam_HRZ_PostResult(paramPostResult);
        afSubIns.setUrlRespResult(eqxRawData.getRawDataAttributes().get("url"));
        
        try {
        	afSubIns.setSubResultCode(resultCode.getResultCode());
        	LogUtils.writeLogEDR(abstractAF, afSubIns, afInstance, eqxRawData);
        }catch (Exception e) {
            AFLog.e("[Exception] writeLogEDR error.");
            AFLog.e(e);
        }

        return afSubIns;
    }
}
