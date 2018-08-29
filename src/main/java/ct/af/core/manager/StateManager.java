package ct.af.core.manager;

import ct.af.enums.EState;
import ec02.af.abstracts.AbstractAFStateManager;
import ec02.af.utils.AFLog;

public class StateManager extends AbstractAFStateManager {
	

	public StateManager(String state) {
		this.afState = null;

		switch (state) {
		case EState.IDLE:
			this.afState = new Idle();
			AFLog.d("[IDLE]");
			break;
		case EState.W_SDF_GETRESOURCEINVENTORY:
			this.afState = new W_SDF_GetResourceInventory();
			AFLog.d("[W_SDF_GetResourceInventory]");
			break;
		case EState.W_SDF_GETRESOURCEINFRANODE:
			this.afState = new W_SDF_GetResourceInfraNode();
			AFLog.d("[W_SDF_GETRESOURCEINFRANODE]");
			break;
		case EState.W_SDF_RESERVEQUOTA:
			this.afState = new W_SDF_ReserveQuota();
			AFLog.d("[W_SDF_RESERVEQUOTA]");
			break;
		case EState.W_SDF_COMMITQUOTAINFRA:
			this.afState = new W_SDF_CommitQuotaInfra();
			AFLog.d("[W_SDF_CommitQuotaInfra]");
			break;
		case EState.W_SDF_RELEASEQUOTA:
			this.afState = new W_SDF_ReleaseQuota();
			AFLog.d("[W_SDF_ReleaseQuota]");
			break;
		case EState.W_CLIENT_POSTRESULT:
			this.afState = new W_CLIENT_PostResult();
			AFLog.d("[W_CLIENT_POSTRESULT]");
			break;
		case EState.W_SDF_POSTREPORT:
			this.afState = new W_SDF_PostReport();
			AFLog.d("[W_SDF_POSTREPORT]");
			break;
		default :
			this.afState = new IncomingManager();
			AFLog.d("[ANY]");
			break;
		}
		
	}


}
