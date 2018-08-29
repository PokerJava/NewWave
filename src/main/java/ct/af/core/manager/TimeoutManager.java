package ct.af.core.manager;

import ct.af.enums.EEqxMsg;
import ct.af.enums.EEventType;
import ct.af.enums.EState;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.utils.AFUtils;
import ct.af.utils.Config;
import ct.af.utils.TimeoutManagerFlag;
import ec02.af.abstracts.AbstractAF;
import ec02.af.data.AFDataFactory;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import java.util.*;

public class TimeoutManager {
    public void recvTimeoutManager(AbstractAF abstractAF, AFInstance afInstance, DateTime timeStampIn) {
        HashMap<String, AFSubInstance> subInstanceHashMap = afInstance.getMainSubInstance();
        SortedSet<String> subInstancekeys = new TreeSet<>(subInstanceHashMap.keySet());

        for (String key : subInstancekeys) {

            AFLog.d("Sub Instance No. : "+key);
            AFSubInstance afSubInstance = subInstanceHashMap.get(key);

            if(!afSubInstance.getSubNextState().equals(ESubState.WAIT.toString())
                    && !afSubInstance.getSubNextState().equals(ESubState.WAIT_REPORT.toString())
                    && !afSubInstance.getSubNextState().equals(ESubState.END.toString())) {

                DateTime timeoutSub = Config.getFormatDateWithMiTz().parseDateTime(afSubInstance.getSubTimeout());

                AFLog.d("CASE : NextState NotIn WAIT && END");

                AFLog.d("currentTime"+Config.getFormatDateWithMiTz().print(timeStampIn));
                AFLog.d("timeoutSub"+Config.getFormatDateWithMiTz().print(timeoutSub));

                if(!timeoutSub.isAfter(timeStampIn)) {
                    int subInvokeSize = afSubInstance.getSubInvoke().size() - 1;
                    for (int invokeIndex = subInvokeSize; invokeIndex >= 0; invokeIndex--) {
                        AFLog.d("CASE : Timeout");

                        String subNextState = afSubInstance.getSubNextState();
                        AFLog.d("subNextState : "+subNextState);

                        AFLog.d("CASE : Timeout.Backend");
                        EquinoxRawData eqxRawData = AFDataFactory.createEquinoxRawData();
                        eqxRawData.setRet("4");

                        Map<String, String> map = new HashMap<>();
                        map.put(EEqxMsg.TYPE.getEqxMsg(), EEventType.RESPONSE.getEventType());
                        map.put(EEqxMsg.ORIG.getEqxMsg(), afSubInstance.getSubInitOrig());

                        map.put(EEqxMsg.METHOD.getEqxMsg(), "");
                        map.put(EEqxMsg.URL.getEqxMsg(), afSubInstance.getSubInitURL());
                        map.put(EEqxMsg.VAL.getEqxMsg(), "");

                        eqxRawData.setRawDataAttributes(map);

                        if(afSubInstance.getSubInvoke().get(invokeIndex).equals("unknown_invoke")) {
                            eqxRawData.setInvoke(new AFUtils().invokeGenerator(abstractAF,afSubInstance.getSubInitInvoke(),afSubInstance.getSubInitCmd(),afSubInstance.getSubNextState(),afSubInstance.getSubInstanceNo(),abstractAF.getEquinoxProperties().getSession()));
                            afSubInstance.getSubInvoke().remove(0);
                            afSubInstance.setSubInvoke(eqxRawData.getInvoke());
                        } else {
                            eqxRawData.setInvoke(afSubInstance.getSubInvoke().get(invokeIndex));
                        }



	                    ExtractController extractController = new ExtractController();
	                    afSubInstance = extractController.checkStateByInvoke(abstractAF, afInstance, eqxRawData);
	
	                    if(afSubInstance.getStatsIn() != null)
	                    {
	                        abstractAF.getEquinoxUtils().incrementStats(afSubInstance.getStatsIn().getStatName());
	                        afSubInstance.setStatsIn(null);
	                        if(afSubInstance.getStatsExeTime() != null) {
	                    		abstractAF.getEquinoxUtils().incrementStats(afSubInstance.getStatsExeTime().getStatName());
	                    		afSubInstance.setStatsExeTime(null);
	                    	}
	                    }
                    }
                }
            }
        }
    }

    public String setEqxTimeout(AbstractAF abstractAF, AFInstance afInstance,DateTime timeStampIn) {
        DateTime preTimeout = timeStampIn.plusYears(1);

        HashMap<String, AFSubInstance> subInstanceHashMap = afInstance.getMainSubInstance();
        SortedSet<String> subInskeys = new TreeSet<>(subInstanceHashMap.keySet());
        String state = "";


        for (String keyIns : subInskeys) {

            AFSubInstance afSubInstance = subInstanceHashMap.get(keyIns);

            afSubInstance.setSubState("");
            AFLog.d("Substate: " + afSubInstance.getSubStateArray());


            for (String stateArr : afSubInstance.getSubStateArray())
            {
                if (afSubInstance.getSubState().isEmpty()) {
                    afSubInstance.setSubState(stateArr);
                }
                else
                {
                    if (!afSubInstance.getSubState().contains(stateArr)) {
                        afSubInstance.setSubState(afSubInstance.getSubState() + " and W_" + stateArr);
                    }
                }
            }

            ArrayList<String> timeoutArray = afSubInstance.getSubTimeoutArray();
            AFLog.d("Substate: " + afSubInstance.getSubState());

            DateTime subTimeout;
            DateTime arrayTimeout;

            if(afSubInstance.getSubCountChild() > 0) {
                subTimeout = Config.getFormatDateWithMiTz().parseDateTime(afSubInstance.getSubTimeout());
                AFLog.d("timeout array: " + afSubInstance.getSubTimeoutArray());
                for (String timeArr : timeoutArray)
                {
                    arrayTimeout = Config.getFormatDateWithMiTz().parseDateTime(timeArr);

                    if (arrayTimeout.isBefore(subTimeout)){
                        subTimeout = arrayTimeout;
                        afSubInstance.setSubTimeout(Config.getFormatDateWithMiTz().print(subTimeout));
                    }
                }

                AFLog.d(">0 subIns key : "+keyIns+" : Timeout = "+subTimeout );

                if(subTimeout != null) {

                    if(subTimeout.isBefore(preTimeout)) {

                        preTimeout = subTimeout;
                        state = "W_" +  afSubInstance.getSubState();
                        AFLog.d("state: "+state);
                    }

                }
            }
        }


        AFLog.d("preTimeout after loop sub ins = "+preTimeout);

        AFLog.d("timeStampIn : "+timeStampIn);

        Seconds diffTimeout = Seconds.secondsBetween(timeStampIn, preTimeout);

        AFLog.d("preTimeout - timeStampIn = diffTimeout : "+diffTimeout.getSeconds());

        if(diffTimeout.getSeconds() < 1) {

            AFLog.d("diffTimeout < 1");
            afInstance.setMainTimeout("1");

        } else {

            AFLog.d("diffTimeout > 1");

            if(diffTimeout.getSeconds() > 30000000) {

                afInstance.setMainTimeout("30000000");
            }
            else
            {
                afInstance.setMainTimeout(String.valueOf(diffTimeout.getSeconds()));
            }
        }

        AFLog.d("set timeout = "+afInstance.getMainTimeout());


        AFLog.d("COUNT WAIT : "+afInstance.getMainCountWait());
        AFLog.d("COUNT PROCESS  : "+afInstance.getMainCountProcess());

        
        if(afInstance.getMainCountWait() == 0
                && afInstance.getMainCountProcess() == 0) {

     	new TimeoutManagerFlag().decrementMaxActive(Config.isEnableTimeoutManagerFlag());
        	
     		AFLog.d("COUNT MAX_ACTIVE: "+Config.getCountMaxActive());
            state = EState.IDLE.toString();
            afInstance.setMainTimeout("0");
            
        }
        
        return state;
    }
}
