package ct.af.message.outgoing;

import ct.af.enums.*;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.instance.SubEDR;
import ct.af.utils.AFUtils;
import ct.af.utils.Config;
import ct.af.utils.LogUtils;
import ec02.af.abstracts.AbstractAF;
import ec02.af.data.AFDataFactory;
import ec02.af.utils.AFLog;
import ec02.data.enums.EEquinoxRawData;
import ec02.data.interfaces.EquinoxRawData;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;

public class Out_SDF_ReleaseQuota {
    public EquinoxRawData messageBuilder(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {
        EquinoxRawData eqxRawData = AFDataFactory.createEquinoxRawData();
        
        String message = "";
        DateTime timeStampOut = new DateTime();

        Map<String, String> map = new HashMap<>();
        map.put(EEqxMsg.TYPE.getEqxMsg(), EEventType.REQUEST.getEventType());
        map.put(EEqxMsg.CTYPE.getEqxMsg(), ECType.TEXTPLAIN.getCType());
        map.put(EEqxMsg.PROTOCOL.getEqxMsg(), EProtocol.HTTP.toString());
        map.put(EEqxMsg.DESTINATION.getEqxMsg(), Config.getSdfReleasequotaInterface());
        
        String url = Config.getUrlSdfReleasequota();
        map.put(EEqxMsg.URL.getEqxMsg(), url);
        map.put(EEqxMsg.METHOD.getEqxMsg(), EMethod.GET.toString());
        map.put(EEqxMsg.VAL.getEqxMsg(), message.toString());


        String edrCommand = "";                
        if(map.get(EEqxMsg.URL.getEqxMsg())!=null)
        {
        	int start = map.get(EEqxMsg.URL.getEqxMsg()).lastIndexOf('/') + 1;
        	edrCommand = map.get(EEqxMsg.URL.getEqxMsg()).substring(start);
        	int stop =  edrCommand.indexOf('.');
        	if(stop>0){
        		edrCommand = edrCommand.substring(0,stop);
        	}
        }
        AFLog.d("configURL : "+url);
        AFLog.d("edrCommand : "+edrCommand);

        //-- Invoke --//
        String invoke = new AFUtils().invokeGenerator(abstractAF,afSubIns.getSubInitInvoke(),afSubIns.getSubInitCmd(),afSubIns.getSubNextState(),afSubIns.getSubInstanceNo(),"$TaskNo");
        afSubIns.setSubInvoke(invoke);
        AFLog.d("[INVOKE] : "+EEqxMsg.INVOKE.getEqxMsg());
        AFLog.d("[INVOKE] : "+invoke);

        //-- Sub state --//
        afSubIns.setSubStateArray(afSubIns.getSubNextState());
        afSubIns.setSubCountChild(afSubIns.getSubCountChild()+1);

        //-- Timeout --//
        String timeout = Config.getFormatDateWithMiTz().print(new DateTime().plusSeconds(Config.getSdfReleasequotaTimeout()));
        afSubIns.setSubTimeout(timeout);
        afSubIns.setSubTimeoutArray(timeout);
        
        //-- Stat --//
        DateTimeFormatter formatDateWithMiTz = Config.getFormatDateWithMiTz();
        EStats statsOut = EStats.APP_SEND_SDF_GET_W_SDF_RELEASEQUOTA_REQUEST;
        String nodeName = "AISSDF";
        statsOut.setCustomStat(statsOut,nodeName);		
        afSubIns.setStatsOut(statsOut);	
		afInstance.setMainTimeStampOut(formatDateWithMiTz.print(timeStampOut));

        //-- EDR --//
        SubEDR subEDR = new SubEDR();
        subEDR.setNextState(afSubIns.getSubNextState());
        if(StringUtils.isNotBlank(afSubIns.getStatsOut().getStatName())) {
        	subEDR.setStatsOut(afSubIns.getStatsOut().getStatName());
        }
        LogUtils.prepareDataForEDR(abstractAF, afSubIns, invoke,timeStampOut, subEDR);
        AFLog.d("timeStampOut : " + afInstance.getMainTimeStampOut());
        MessageBuilder builder = new MessageBuilder("");
        try
        {
            abstractAF.getEquinoxUtils().sendHTTPRequestMessage(builder, EEquinoxRawData.CTypeHTTP.TEXT_PLAIN, invoke, Config.getSdfReleasequotaInterface(), map);
        }
        catch (Exception e)
        {
        	AFLog.e("[Exception] can't build HTTPRequestMessage.");
			AFLog.e(e);
        }

        return eqxRawData;
    }
}
