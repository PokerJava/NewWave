package ct.af.substate;

import ct.af.enums.EResultCode;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ec02.af.abstracts.AbstractAF;

public class Do_IDLE_Xxx {
	public void doBusinessLogic(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {
		
		if(afSubIns.getSubInternalCode().equals(EResultCode.RE20000.getResultCode()))
		{
			afSubIns.setSubNextState(ESubState.END_XXX.getState());
            afSubIns.setSubControlState(ESubState.END_XXX.getState());
		}
	}
}
