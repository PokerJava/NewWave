package ct.af.substate;

import ct.af.core.manager.ConstructController;
import ct.af.enums.ECommand;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ec02.af.abstracts.AbstractAF;

public class Do_Internal_FinalResult {
    public void doBusinessLogic(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns)
    {
    	if(afSubIns.getSubInitCmd().equals(ECommand.RESOURCEORDER_SYNC.getCommand())) {
    		afSubIns.setSubControlState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SDF_POSTREPORT.getState());
    		afSubIns.setSubNextState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SDF_POSTREPORT.getState());
    		afInstance.incrementMainCountProcess();
    		afInstance.decrementMainCountWait();
    	}else {
    		afSubIns.setSubControlState(ESubState.CLIENT_POSTRESULT.getState());
    		afSubIns.setSubNextState(ESubState.CLIENT_POSTRESULT.getState());
    		afInstance.incrementMainCountProcess();
    		afInstance.decrementMainCountWait();
    	}
        new ConstructController().constructGateway(abstractAF, afInstance, afSubIns);
    }
}
