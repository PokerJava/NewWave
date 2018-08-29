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
import java.util.Set;

public class Out_SDF_GetResourceInfraNode {
    public EquinoxRawData messageBuilder(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance resourceOrderIns) {

        DateTime timeStampOut = new DateTime();

        //-- Construct EquinoxRawData --//
        EquinoxRawData eqxRawData = AFDataFactory.createEquinoxRawData();

        Set<String> neIdList = resourceOrderIns.getNeIdQuotaAmountRequire().keySet();
        for (String neid : neIdList) {
            Map<String, String> map = new HashMap<>();
            map.put(EEqxMsg.TYPE.getEqxMsg(), EEventType.REQUEST.getEventType());
            map.put(EEqxMsg.CTYPE.getEqxMsg(), ECType.TEXTPLAIN.getCType());
            map.put(EEqxMsg.PROTOCOL.getEqxMsg(), EProtocol.HTTP.toString());
            map.put(EEqxMsg.DESTINATION.getEqxMsg(), Config.getSdfInfranodeInterface());
            map.put(EEqxMsg.METHOD.getEqxMsg(), EMethod.GET.toString());

            String url = Config.getUrlSdfInfranode() + "/" + neid + ".json";
            map.put(EEqxMsg.URL.getEqxMsg(), url);
            map.put(EEqxMsg.VAL.getEqxMsg(), "");


            //-- Invoke --//
            String invoke = new AFUtils().invokeGenerator(abstractAF,resourceOrderIns.getSubInitInvoke(),resourceOrderIns.getSubInitCmd(),resourceOrderIns.getSubNextState(),resourceOrderIns.getSubInstanceNo());
            resourceOrderIns.setSubInvoke(invoke);

            // set neId map with invoke for handle timeout, reject
            resourceOrderIns.getMapNeidWithInvoke().put(invoke, neid);

            //-- Sub state --//
            resourceOrderIns.setSubStateArray(resourceOrderIns.getSubNextState());
            resourceOrderIns.setSubCountChild(resourceOrderIns.getSubCountChild()+1);

            //-- Timeout --//
            String timeout = Config.getFormatDateWithMiTz().print(new DateTime().plusSeconds(Config.getSdfInfranodeTimeout()));
            resourceOrderIns.setSubTimeout(timeout);
            resourceOrderIns.setSubTimeoutArray(timeout);

            //-- Stat --//
            DateTimeFormatter formatDateWithMiTz = Config.getFormatDateWithMiTz();
            EStats statsOut = EStats.APP_SEND_SDF_GET_W_SDF_GETRESOURCEINFRANODE_REQUEST;
            String nodeName = "AISSDF";
            statsOut.setCustomStat(statsOut,nodeName);		
            resourceOrderIns.setStatsOut(statsOut);	
    		afInstance.setMainTimeStampOut(formatDateWithMiTz.print(timeStampOut));

            //-- EDR --//
            SubEDR subEDR = new SubEDR();
            subEDR.setServiceOrderId(resourceOrderIns.getServiceOrderId());
            subEDR.setServiceOrderType(resourceOrderIns.getServiceOrderType());
            subEDR.setNextState(resourceOrderIns.getSubNextState());
            subEDR.setResourceOrderId(resourceOrderIns.getRequestId());
            if(StringUtils.isNotBlank(resourceOrderIns.getStatsOut().getStatName())) {
            	subEDR.setStatsOut(resourceOrderIns.getStatsOut().getStatName());
            }
            LogUtils.prepareDataForEDR(abstractAF, resourceOrderIns, invoke,timeStampOut, subEDR);
            AFLog.d("timeStampOut : " + afInstance.getMainTimeStampOut());
            MessageBuilder builder = new MessageBuilder("");
            try
            {
                abstractAF.getEquinoxUtils().sendHTTPRequestMessage(builder, EEquinoxRawData.CTypeHTTP.TEXT_PLAIN, invoke, Config.getSdfInfranodeInterface(), map);
            }
            catch (Exception e)
            {
            	AFLog.e("[Exception] can't build HTTPRequestMessage.");
                AFLog.e(e);
            }
        }

        return eqxRawData;
    }
}
