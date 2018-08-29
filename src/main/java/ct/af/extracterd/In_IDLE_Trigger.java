package ct.af.extracterd;

import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class In_IDLE_Trigger {
    public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns,
                                        EquinoxRawData eqxRawData) {
//        Param_IDLE_Trigger paramIdleTrigger;
//
//        Parser_IDLE_Trigger parser = new Parser_IDLE_Trigger();
//        paramIdleTrigger = parser.doParser(eqxRawData, ec02Instance, afSubIns);
//        afSubIns.setSubClientParameter(paramIdleTrigger);
    	AFLog.i("Random service trigger is incomming from E11");
    	
    	afSubIns.setSubCurrentState(ESubState.IDLE_TRIGGER.getState());
    	afSubIns.setSubControlState(ESubState.IDLE_TRIGGER.getState());
        afInstance.incrementMainCountWait();
        afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);

        //-- Stat --//      
        //StatUtils.incrementStats(abstractAF, StatUtils.PEGAZUS.pgz_idle_trigger);

        return afSubIns;
    }
}
