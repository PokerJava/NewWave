package ct.af.extracterd;

import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class In_SLEEP_ForErrorHandling {
	public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns,
	            EquinoxRawData eqxRawData) {
	//Param_IDLE_Trigger paramIdleTrigger;
	//
	//Parser_IDLE_Trigger parser = new Parser_IDLE_Trigger();
	//paramIdleTrigger = parser.doParser(eqxRawData, ec02Instance, afSubIns);
	//afSubIns.setSubClientParameter(paramIdleTrigger);
	AFLog.i("Sleep");
	
	afSubIns.setSubCurrentState(ESubState.SleepForErrorHandling.getState());
	afSubIns.setSubControlState(ESubState.SleepForErrorHandling.getState());
	afSubIns.setSubNextState(ESubState.Unknown.getState());
	
    afSubIns.setSubCountChild(afSubIns.getSubCountChild()-1);

	return afSubIns;
	}
}
