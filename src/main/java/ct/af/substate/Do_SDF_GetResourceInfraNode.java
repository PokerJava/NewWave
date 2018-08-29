package ct.af.substate;

import ct.af.enums.ECommand;
import ct.af.enums.EResultCode;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.resourceModel.ResourceInfraNode;
import ct.af.resourceModel.SuppCode;
import ct.af.utils.ErrorMessageUtil;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import java.util.HashMap;
import java.util.Map;


public class Do_SDF_GetResourceInfraNode {
  public void doBusinessLogic(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance resourceOrderIns) {
    boolean needReserveQuota = false;
    String errorMsg = resourceOrderIns.getErrorMessage() != null ? resourceOrderIns.getErrorMessage() : "";
    AFLog.d("resourceInfraNode size : " + resourceOrderIns.getResourceInfraNodeHashMap().size());

    for (Map.Entry neIdQuota : resourceOrderIns.getNeIdQuotaAmountRequire().entrySet()) {
      String neIdName = neIdQuota.getKey().toString();
      int quotaRequire = (int) neIdQuota.getValue();

      // check need to reserve quota
      if (quotaRequire > 0) {
        needReserveQuota = true;
      }

      if (resourceOrderIns.getResourceInfraNodeHashMap().containsKey(neIdName)) {
        // check quota and status when can get information
        ResourceInfraNode resourceInfraNode = resourceOrderIns.getResourceInfraNodeHashMap().get(neIdName);
        if (resourceInfraNode.getNeState() != null && resourceInfraNode.getQuotaAvailability() != null && !resourceInfraNode.getNeState().equals("") && !resourceInfraNode.getQuotaAvailability().equals("")) {
          if (resourceInfraNode.getNeState().equals("Active") && resourceInfraNode.getQuotaAvailability() <  quotaRequire && quotaRequire > 0) {
            errorMsg = ErrorMessageUtil.appendWithSemicolon(errorMsg, neIdName + " quota unavailable for " + quotaRequire + ".");
          } else if (!resourceInfraNode.getNeState().equals("Active")) {
            errorMsg = ErrorMessageUtil.appendWithSemicolon(errorMsg, neIdName + " neState must be Active in SDF_GETINFRANODE.");
          }
        } else {
          errorMsg = ErrorMessageUtil.appendWithSemicolon(errorMsg, neIdName + " information is insufficient from SDF_GETINFRANODE.");
        }
      }
    }


    if (errorMsg.equals("")) {

      resourceOrderIns.setSubResultCode(EResultCode.RE20000.getResultCode());
      resourceOrderIns.setSubInternalCode(EResultCode.RE20000.getResultCode());

      if (needReserveQuota) {
        resourceOrderIns.setSubControlState(ESubState.SDF_RESERVEQUOTA.getState());
        resourceOrderIns.setSubNextState(ESubState.SDF_RESERVEQUOTA.getState());
      } else {

        AFSubInstance resourceItem1 = afInstance.getMainSubInstance(resourceOrderIns.getSubResourceItemInsNoArray().get(resourceOrderIns.getSubFirstIndexResourceItemToProcess()));
        if(resourceItem1!=null) {
          SuppCode suppCode = resourceItem1.getSuppCodeList().get(resourceItem1.getSubFirstTaskNoToProcess());
          resourceItem1.setCurrentSuppcode(suppCode.getSuppCode());
          String neIdName = resourceItem1.getNeIdList().get(resourceItem1.getSubTaskNo());
          HashMap<String, ResourceInfraNode> resourceInfraNodeHashMap = resourceOrderIns.getResourceInfraNodeHashMap();
          resourceItem1.setResourceInfraNodeHashMap(resourceInfraNodeHashMap);
          resourceItem1.setSubNeId(neIdName);


          new Do_Internal_NE().validateAndProcess(abstractAF, afInstance, resourceItem1);
        }
        if (resourceItem1!=null&&!resourceItem1.isProcessOutputMessageComplete()){
          AFLog.d("do_business SDF_GETRESOURCEINFRANODE internalcode != 20000");
          resourceOrderIns.setErrorMessage(resourceItem1.getErrorMessage());
          resourceOrderIns.setSubResultCode(EResultCode.RE50000.getResultCode());
          resourceOrderIns.setSubInternalCode(EResultCode.RE50000.getResultCode());
          resourceOrderIns.setSubControlState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SDF_POSTREPORT.getState());
          resourceOrderIns.setSubNextState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SDF_POSTREPORT.getState());
          afInstance.setMainIsFinishFlow(true);
          resourceOrderIns.setResourceOrderTimeOut(0);
        } else {
          // not error
          if(resourceOrderIns.getSubInitCmd().equals(ECommand.RESOURCEORDER_SYNC.getCommand())) {
            resourceOrderIns.setSubControlState(ESubState.WAIT_REPORT.getState());
            resourceOrderIns.setSubNextState(ESubState.WAIT_REPORT.getState());
            afInstance.decrementMainCountProcess();
            afInstance.incrementMainCountWait();
            afInstance.setMainIsLock(false);
          } else {
            resourceOrderIns.setSubControlState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.WAIT_REPORT.getState());
            resourceOrderIns.setSubNextState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.WAIT_REPORT.getState());
            afInstance.decrementMainCountProcess();
            afInstance.incrementMainCountWait();
            afInstance.setMainIsLock(false);
          }
        }
      }
    } else {
      AFLog.d("do_business SDF_GETRESOURCEINFRANODE internalcode != 20000");
      resourceOrderIns.setErrorMessage(errorMsg);
      resourceOrderIns.setSubResultCode(EResultCode.RE50000.getResultCode());
      resourceOrderIns.setSubInternalCode(EResultCode.RE50000.getResultCode());
      resourceOrderIns.setSubControlState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SDF_POSTREPORT.getState());
      resourceOrderIns.setSubNextState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SDF_POSTREPORT.getState());
      afInstance.setMainIsFinishFlow(true);
      resourceOrderIns.setResourceOrderTimeOut(0);
      
    }
  }
}
