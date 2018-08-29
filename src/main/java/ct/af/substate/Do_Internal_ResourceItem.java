package ct.af.substate;

import ct.af.enums.EResultCode;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.instance.PoolTask;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.resourceModel.*;
import ct.af.utils.Config;
import ct.af.utils.SuccessPatternUtil;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import java.util.HashMap;

public class Do_Internal_ResourceItem {
    public void miniEFlow(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {
        if (!afInstance.getMainIsLock() ||
                (afInstance.getMainIsLock() &&
                        !afSubIns.getSubControlState().equals(ESubState.IDLE_RESOURCEITEM.getState()) &&
                        !afSubIns.getSubControlState().equals(ESubState.WAIT.getState()))) {
            if (afSubIns.getSubInternalCode().equals(EResultCode.RE20000.getResultCode()) &&
                    afSubIns.getSubTaskNo() < afSubIns.getSuppCodeList().size()) {
                // do suppcode
                SuppCode suppCode = afSubIns.getSuppCodeList().get(afSubIns.getSubTaskNo());
                afSubIns.setCurrentSuppcode(suppCode.getSuppCode());

                String neIdName = afSubIns.getNeIdList().get(afSubIns.getSubTaskNo());
                AFSubInstance resourceOrderIns = afInstance.getMainSubInstance(afSubIns.getSubResourceOrderIns());
                HashMap<String, ResourceInfraNode> resourceInfraNodeHashMap = resourceOrderIns.getResourceInfraNodeHashMap();
                afSubIns.setResourceInfraNodeHashMap(resourceInfraNodeHashMap);
                afSubIns.setSubNeId(neIdName);

                afSubIns.setSubControlState(suppCode.getSuppCode());
                afSubIns.setSubNextState(suppCode.getSuppCode());
                AFLog.d(afSubIns.getSubInitCmd() + " task " + (afSubIns.getSubTaskNo() + 1));

                if (afSubIns.getSubTaskNo() == 0) {
                    afSubIns.setNeTask(true);
                    afInstance.incrementMainCountProcess();
                    afInstance.decrementMainCountWait();
                    afInstance.setMainIsLock(true);
                }

            } else if (afSubIns.getSubInternalCode().equals(EResultCode.RE20000.getResultCode()) &&
                    afSubIns.getSubTaskNo() == afSubIns.getSuppCodeList().size()) {
                // end all supppcode
                afSubIns.setSubNextState(ESubState.Unknown.getState());
                afSubIns.setSubControlState(ESubState.END.getState());
            } else {
                // fail
                afSubIns.setSubNextState(ESubState.Unknown.getState());
                afSubIns.setSubControlState(ESubState.END.getState());
                afInstance.setMainIsFinishFlow(true);
            }
        } else {
            afSubIns.setSubNextState(ESubState.WAIT.getState());
            afSubIns.setSubControlState(ESubState.WAIT.getState());
        }
    }
    

    public void doBusinessLogic(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {
    	
    	HashMap<String, Object> paramChild = afSubIns.getSubClientHashMapParameter();
        if (afSubIns.getSubResourceOrderHasError() || afInstance.getMainIsFinishFlow()|| (!afInstance.getMainIsLock() && afSubIns.getFlagResourceItemStatus().equals("Y"))|| (afSubIns.getDropResourceFlag() && !afInstance.getMainIsLock())) {
        	if (afSubIns.getSubControlState().equals(ESubState.WAIT.toString()) ||
                afSubIns.getSubControlState().equals(ESubState.IDLE_RESOURCEITEM.toString())) {
        		afInstance.decrementMainCountWait();
                afInstance.incrementMainCountProcess();
            }

            afSubIns.setSubControlState(ESubState.END.getState());
            afSubIns.setSubNextState(ESubState.Unknown.toString());
        } else {

            boolean isLoop = false;
            do {
            
            	
            	AFLog.d("before task : "+afSubIns.getSubTaskNo());
            	AFLog.d("before control : "+afSubIns.getSubControlState());
            	AFLog.d("before next : "+afSubIns.getSubNextState());
            	if (afInstance.getMainIsLock() &&
                        !afSubIns.getSubControlState().equals(ESubState.IDLE_RESOURCEITEM.getState()) &&
                        !afSubIns.getSubControlState().equals(ESubState.WAIT.getState())) {
                    // increment taskNo after NE response
                    afSubIns.incrementSubTaskNo();
               }
            	
                miniEFlow(abstractAF, afInstance, afSubIns);
                AFLog.d("after task : "+afSubIns.getSubTaskNo());
            	AFLog.d("after control : "+afSubIns.getSubControlState());
            	AFLog.d("after next : "+afSubIns.getSubNextState());
            	String netype = "";
            	
//            	if(!afSubIns.getFlagResourceItemStatus().equals("Y")) {
            	
            	AFLog.d("Do_internal + errorFlag: "+ afSubIns.getNew_ErrorFlag());
            	if(!afSubIns.getSubControlState().contains("IDLE") &&
	                !afSubIns.getSubControlState().contains("END") && 
	                !afSubIns.getSubControlState().equals(ESubState.WAIT.getState())) {
            		
            		try {
                		SuppCode suppCode = afSubIns.getSuppCodeList().get(afSubIns.getSubTaskNo());
                    	ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppCode.getSuppCode());
                    	netype = resourceMappingCommand.getNeType();
                	}catch (Exception e) {
    					//System.out.println("************* Exception NeType : " + netype +" ************* ");
    					AFLog.d("************* Exception NeType : " + netype +" ************* ");
    				}
            		
            		AFSubInstance resourceOrderIns = afInstance.getMainSubInstance(afSubIns.getSubResourceOrderIns());
                    Param_IDLE_ResourceOrder param_IDLE_ResourceOrder = (Param_IDLE_ResourceOrder)resourceOrderIns.getSubClientParameter();
                    
                    
                    
                    
                    // TODO:BB 
                    
	                    if(netype.equals("MD")) {
	                    	//afSubIns.setOld_errorFlag(paramChild.get("errorFlag").toString());
	                		isLoop = false;
	                		
	                	}else if(param_IDLE_ResourceOrder.getReTransmit().equals("1")&&!netype.isEmpty()) {
	                		
	                		String oldErrorFlag = paramChild.get("errorFlag").toString();
	                        int suppcodeNum = afSubIns.getSuppCodeList().size();
	                        int errorFlagNum = paramChild.get("errorFlag").toString().length();
	                        if(errorFlagNum>suppcodeNum) {
	                        	oldErrorFlag = oldErrorFlag.substring(0,suppcodeNum);
	                        }
	            			
	            			if(afSubIns.getSubCurrentState().equals(ESubState.IDLE_RESOURCEITEM.getState())||afSubIns.getSubCurrentState().equals(ESubState.WAIT.getState())) {
	            				afSubIns.setOld_errorFlag(oldErrorFlag);
	            				
	            				AFLog.d("re_item errorFlag : "+afSubIns.getNew_ErrorFlag());
	            			}
	            			
	    	            	if(afSubIns.getOld_errorFlag().startsWith("0")) {
	    	            		afSubIns.setOld_errorFlag(afSubIns.getOld_errorFlag().substring(1));
	    	            		isLoop = false;
	    	            		
	    	            	}else if(afSubIns.getOld_errorFlag().startsWith("1")) {
	    	            		afSubIns.setSubInternalCode(EResultCode.RE20000.getResultCode());
	    	            		afSubIns.setOld_errorFlag(afSubIns.getOld_errorFlag().substring(1));
	    	            		afSubIns.setNew_ErrorFlag(afSubIns.getNew_ErrorFlag()+"1");
	    	            		afSubIns.setSubCurrentState(afSubIns.getSubNextState());
	    	            		
	    	            		SuccessPatternUtil successPatternUtil = new SuccessPatternUtil();	    
	    	            	    PoolTask testTask = successPatternUtil.getTaskResult("FC", null, afSubIns);
	    	            	    AFLog.d(testTask.toString());
	    	            	        	            	    
	    	            	    HashMap<String, Object> objectMapkey = new HashMap<>();	    
	    	            	    objectMapkey.put(afSubIns.getTaskId(), "Force Complete");
	    	            	    afSubIns.getMappingPoolTask().put("Task"+(afSubIns.getSubTaskNo()+1), testTask);
	    	            	    afSubIns.getMappingResponse().putAll(objectMapkey);
	    	            	    
	    	            		AFLog.d("re_item errorFlag : "+afSubIns.getNew_ErrorFlag());      	            		
	                			isLoop = true;
	                			
	                		}else if(afSubIns.getOld_errorFlag().startsWith("")) {
	                			isLoop = false;
	                		}
	                    }
            	}else if(afSubIns.getSubControlState().contains("END")){
            		isLoop = false;
            	}
//            	}else {
//            		isLoop = false;
//            	}
            }while(isLoop);
        }
    }
}
            
