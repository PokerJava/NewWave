package ct.af.substate;

import ct.af.enums.EResultCode;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ec02.af.abstracts.AbstractAF;

public class Do_IDLE_Dgw {
	public void doBusinessLogic(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {
		
		if(afSubIns.getSubInternalCode().equals(EResultCode.RE20000.getResultCode()))
		{			
			 afSubIns.setSubNextState(ESubState.SDF_GETLOCATION.getState());
             afSubIns.setSubControlState(ESubState.SDF_GETLOCATION.getState());
             afInstance.decrementMainCountWait();
             afInstance.incrementMainCountProcess();
		}else if (afSubIns.getSubInternalCode().equals(EResultCode.RE40301.getResultCode())) {
            afSubIns.setSubControlState(ESubState.END_DGW.getState()+","+ESubState.END.getState());
            afSubIns.setSubNextState(ESubState.END_DGW.getState()+","+ESubState.END.getState());

            afInstance.decrementMainCountWait();
        } else {
            afInstance.setMainResourceOrderInsNo(afSubIns.getSubInstanceNo());
            afSubIns.setSubControlState(ESubState.END_DGW.getState());
            afSubIns.setSubNextState(ESubState.END_DGW.getState());



            afInstance.decrementMainCountWait();
            afInstance.incrementMainCountProcess();
        }
	}
}
