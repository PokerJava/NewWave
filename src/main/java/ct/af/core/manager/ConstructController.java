package ct.af.core.manager;

import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.outgoing.*;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;

public class ConstructController {
    public void enterConstructERD(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubInstance){

        String subNextState = afSubInstance.getSubNextState();

        AFLog.d("[EnterConstructERD] Next State : " + afSubInstance.getSubNextState());
        if (subNextState.equals(ESubState.END_RESOURCEORDER.getState())) {
            new Out_END_Resource().messageBuilder(abstractAF, afInstance, afSubInstance);
        } else if(subNextState.equals(ESubState.END_XXX.getState())) {
        	new Out_END_Xxx().messageBuilder(abstractAF, afInstance, afSubInstance);
        } else if(subNextState.equals(ESubState.SDF_GETLOCATION.getState())) {
        	new Out_SDF_GetLocation().messageBuilder(abstractAF, afInstance, afSubInstance);
        } else if (subNextState.equals(ESubState.SDF_GETRESOURCEINVENTORY.getState())) {
            new Out_SDF_GetResourceInventory().messageBuilder(abstractAF, afInstance, afSubInstance);
        } else if (subNextState.equals(ESubState.SDF_GETRESOURCEINFRANODE.getState())) {
            new Out_SDF_GetResourceInfraNode().messageBuilder(abstractAF, afInstance, afSubInstance);
        } else if (subNextState.equals(ESubState.SDF_RESERVEQUOTA.getState())) {
            new Out_SDF_ReserveQuota().messageBuilder(abstractAF, afInstance, afSubInstance);
        } else if (subNextState.equals(ESubState.SDF_COMMITQUOTAINFRA.getState())) {
            new Out_SDF_CommitQuotaInfra().messageBuilder(abstractAF, afInstance, afSubInstance);
        } else if (subNextState.equals(ESubState.SDF_RELEASEQUOTA.getState())) {
            new Out_SDF_ReleaseQuota().messageBuilder(abstractAF, afInstance, afSubInstance);
        } else if (subNextState.equals(ESubState.CLIENT_POSTRESULT.getState())) {
            new Out_CLIENT_PostResult().messageBuilder(abstractAF, afInstance, afSubInstance);
        } else if (subNextState.equals(ESubState.SDF_POSTREPORT.getState())) {
            new Out_SDF_PostReport().messageBuilder(abstractAF, afInstance, afSubInstance);
        } else if (subNextState.equals(ESubState.SleepForErrorHandling.getState())) {
            new Out_SLEEP_ForErrorHandling().messageBuilder(abstractAF, afInstance, afSubInstance);
        } else if (subNextState.equals(ESubState.SLEEP_SENDRESULT.getState())) {
            new Out_SLEEP_SendResult().messageBuilder(abstractAF, afInstance, afSubInstance);
        } else  {
            if (afSubInstance.isNeTask()) {
                new Out_NE().messageBuilder(abstractAF, afInstance, afSubInstance);
            } else {
                // unknown
            }
        }
        
        if(afSubInstance.getStatsOut() != null) {
			abstractAF.getEquinoxUtils().incrementStats(afSubInstance.getStatsOut().getStatName());
		}

		afSubInstance.setStatsOut(null);

    }
    public void constructGateway(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubInstance) {
        AFLog.d("Invoke size : "+afSubInstance.getSubInvoke().size());

        if(afSubInstance.getSubInvoke().size() == 0) {
            AFLog.d("NextState : " + afSubInstance.getSubNextState());

            if (!afSubInstance.getSubNextState().equals(ESubState.WAIT_REPORT.getState()) &&
                !afSubInstance.getSubNextState().equals(ESubState.WAIT.getState()) &&
                !afSubInstance.getSubNextState().equals(ESubState.END.getState())) {
                if (afSubInstance.getSubNextState().contains(",")) {
                    String[] nextState = afSubInstance.getSubNextState().split(",");

                    for (String state : nextState) {
                        if (!state.equals(ESubState.WAIT.getState()) &&
                            !state.equals(ESubState.WAIT_REPORT.getState()) &&
                            !state.equals(ESubState.END.getState())) {
                            afSubInstance.setSubNextState(state);
                            enterConstructERD(abstractAF, afInstance, afSubInstance);
                        } else {
                            afSubInstance.setSubControlState(state);
                            afSubInstance.setSubNextState(state);
                            AFLog.d("CASE : byPassSubstate with nextState == WAIT");
                        }
                    }
                } else {
                    enterConstructERD(abstractAF, afInstance, afSubInstance);
                }
            } else {
                AFLog.d("CASE : byPassSubstate with nextState == WAIT");
            }
        } else {
            AFLog.d("CASE : byPassSubstate with invoke.size != 0");
        }
    }
}
