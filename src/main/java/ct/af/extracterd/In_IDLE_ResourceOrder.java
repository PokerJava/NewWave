package ct.af.extracterd;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import ct.af.enums.ERequestState;
import ct.af.enums.EStats;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.instance.SubEDR;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.message.incoming.parser.Parser_IDLE_ResourceOrder;
import ct.af.utils.Config;
import ct.af.utils.LogUtils;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class In_IDLE_ResourceOrder {
    public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns,
                                        EquinoxRawData eqxRawData) {
        Param_IDLE_ResourceOrder paramIdleResourceOrder;

        Parser_IDLE_ResourceOrder parser = new Parser_IDLE_ResourceOrder();
        paramIdleResourceOrder = parser.doParser(abstractAF, eqxRawData, afInstance, afSubIns);
        afSubIns.setSubClientParameter(paramIdleResourceOrder);
        afSubIns.setState(ERequestState.PENDING.getState());
        AFLog.d("State : " + afSubIns.getState());
 //aaa
        //-- Stat --//
        EStats statsIn = EStats.APP_RECV_CLIENT_POST_RESOURCE_ORDER_REQUEST;
        String userSys = paramIdleResourceOrder.getUserSys().trim();
        if(Config.getServerInterfaceHashMap().containsKey(userSys)){
        	userSys = Config.getServerInterfaceHashMap().get(userSys);
        }
        if(userSys!=null&&!userSys.isEmpty()) {
        	statsIn.setClientCustomStat(EStats.APP_RECV_CLIENT_POST_RESOURCE_ORDER_REQUEST, userSys);
        }else{
        	statsIn.setClientCustomStat(EStats.APP_RECV_CLIENT_POST_RESOURCE_ORDER_REQUEST, "Client");
        }	
        afSubIns.setStatsIn(statsIn);
       
        //StatUtils.incrementStats(abstractAF, StatUtils.PEGAZUS.pgz_client_recv_GET_resourceorder_success_req);
        
        /* EDR */
        SubEDR subEDR = new SubEDR();
        DateTime timeStampOut = new DateTime();
        subEDR.setNextState(afSubIns.getSubNextState());
        
        if(StringUtils.isNotBlank(afSubIns.getStatsIn().getStatName())) {
        	subEDR.setStatsIn(afSubIns.getStatsIn().getStatName());
        }
        LogUtils.prepareDataForEDR(abstractAF, afSubIns, eqxRawData.getInvoke(),timeStampOut, subEDR);
        
        return afSubIns;
    }
}
