package ct.af.extracterd;

import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ec02.af.abstracts.AbstractAF;
import ec02.data.interfaces.EquinoxRawData;

/*
*   this class use for handle state sleep for prevent send endResource(line7) and hrz_postresult(line10) at same time
* */

public class In_SLEEP_SendResult {
  public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns, EquinoxRawData eqxRawData) {
    afSubIns.setSubCurrentState(ESubState.SLEEP_SENDRESULT.getState());
    afSubIns.setSubControlState(ESubState.SLEEP_SENDRESULT.getState());
    afSubIns.setSubNextState(ESubState.Unknown.getState());

    afSubIns.setSubCountChild(afSubIns.getSubCountChild()-1);

    return afSubIns;
  }
}
