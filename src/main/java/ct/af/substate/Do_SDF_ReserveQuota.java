package ct.af.substate;

import ct.af.enums.EResultCode;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ec02.af.abstracts.AbstractAF;

public class Do_SDF_ReserveQuota {
    public void doBusinessLogic(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance resourceOrderIns) {
        if (resourceOrderIns.getErrorMessage() == null || resourceOrderIns.getErrorMessage().equals("")) {
            resourceOrderIns.setSubResultCode(EResultCode.RE20000.getResultCode());
            resourceOrderIns.setSubInternalCode(EResultCode.RE20000.getResultCode());
            resourceOrderIns.setSubControlState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.WAIT_REPORT.getState());
            resourceOrderIns.setSubNextState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.WAIT_REPORT.getState());
            afInstance.decrementMainCountProcess();
            afInstance.incrementMainCountWait();
            afInstance.setMainIsLock(false);
        } else {
            resourceOrderIns.setSubResultCode(EResultCode.RE50000.getResultCode());
            resourceOrderIns.setSubInternalCode(EResultCode.RE50000.getResultCode());
            resourceOrderIns.setSubControlState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SDF_POSTREPORT.getState());
            resourceOrderIns.setSubNextState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SDF_POSTREPORT.getState());
            afInstance.setMainIsFinishFlow(true);
        }
    }
}
