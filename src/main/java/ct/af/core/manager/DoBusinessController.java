package ct.af.core.manager;

import ct.af.enums.EConfig;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_SDF_GetResourceInventory;
import ct.af.substate.*;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.joda.time.Seconds;
import ct.af.utils.Config;
import ct.af.utils.ResourceInventoryFromFile;


public class DoBusinessController {
    public void doBusinessLogic(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {

        if (afSubIns.getSubControlState().equals(ESubState.IDLE_TRIGGER.getState())) {
            //AFLog.d("in dobusiness IDLE_TRIGGER ");
            AFLog.i("--> Random service trigger is incomming from \"E11\"");
            if(!Config.getEnableResourceInventoryFromFileMode())
            {
	            afSubIns.setSubControlState(ESubState.SDF_GETRESOURCEINVENTORY.getState());
	            afSubIns.setSubNextState(ESubState.SDF_GETRESOURCEINVENTORY.getState());
	            afInstance.decrementMainCountWait();
	            afInstance.incrementMainCountProcess();
            }
            else
            {
            	ResourceInventoryFromFile resourceInventoryFromFile = new ResourceInventoryFromFile();
            	//AFLog.d("======================= Start Load ResourceInventory =====================");
    			String rawData = "";
    			try 
    			{
    				BufferedReader br;
    				FileReader fr;
    				AFLog.d("ResourceInventory Mode : File");
    				fr = new FileReader(Config.getPATH_FILEBACKUPRESOURCEINVENTORY());
    				String manualFile = Config.getWarmConfig(abstractAF, EConfig.PATH_FILEBACKUPRESOURCEINVENTORY.getName());
    				AFLog.d("Manual Select file :" +  manualFile);
    				AFLog.d("Path : "+ Config.getPATH_BACKUPRESOURCEINVENTORY());
    				br = new BufferedReader(fr);
    				//AFLog.d("======================= END Load ResourceInventory =====================");
    				try 
    				{
    					rawData = br.readLine();
    					br.close();
    				} 
    				catch (IOException e) 
    				{
    					AFLog.e("[Exception] can't load ResourceInventory from file.");
    					AFLog.e(e);
    				}
    			} 
    			catch (Exception e) 
    			{
    				AFLog.e("[Exception] can't load ResourceInventory from file.");
					AFLog.e(e);
    			}
    			Param_SDF_GetResourceInventory paramFile = resourceInventoryFromFile.doParserFile(rawData);
    			resourceInventoryFromFile.setResourceInventFormFile(paramFile);
            	
            	afSubIns.setSubControlState(ESubState.END.getState());
	            afSubIns.setSubNextState(ESubState.END.getState());
	            afInstance.decrementMainCountWait();
            }
            if (!afSubIns.getSubNextState().equals(ESubState.Unknown.getState())) {
                return;
            }
        }

        if (afSubIns.getSubControlState().equals(ESubState.SDF_GETRESOURCEINVENTORY.getState())) {
//            AFLog.d("in dobusiness SDF_GETRESOURCEINVENTORY ");


            if (!afSubIns.getSubNextState().equals(ESubState.Unknown.getState())) {
                return;
            }

            afSubIns.setSubControlState(ESubState.END.getState());
            afSubIns.setSubNextState(ESubState.END.getState());
            afInstance.decrementMainCountProcess();

            if (!afSubIns.getSubNextState().equals(ESubState.Unknown.getState())) {
                return;
            }
        }

        if(afSubIns.getSubControlState().equals(ESubState.IDLE_XXX.getState()))
        {
        	Do_IDLE_Xxx doIdleXxx = new Do_IDLE_Xxx();
        	//TODO
        }
        if(afSubIns.getSubControlState().equals(ESubState.IDLE_RESOURCEORDER.getState())) {
            Do_IDLE_ResourceOrder doIdleResource = new Do_IDLE_ResourceOrder();
            doIdleResource.doBusinessLogic(abstractAF, afInstance, afSubIns);

            if(!afSubIns.getSubNextState().equals(ESubState.Unknown.getState()))
            {
                return;
            }
        }


        if(afSubIns.getSubControlState().equals(ESubState.SDF_GETRESOURCEINFRANODE.getState())) {
            //AFLog.d("in do_business SDF_GETRESOURCEINFRANODE ");

            new Do_SDF_GetResourceInfraNode().doBusinessLogic(abstractAF, afInstance, afSubIns);

            if(!afSubIns.getSubNextState().equals(ESubState.Unknown.getState()))
            {
                return;
            }
        }


        if (afSubIns.getSubControlState().equals(ESubState.SDF_RESERVEQUOTA.getState())) {
            //AFLog.d("in do_business SDF_RESERVEQUOTA ");

            new Do_SDF_ReserveQuota().doBusinessLogic(abstractAF, afInstance, afSubIns);

            if(!afSubIns.getSubNextState().equals(ESubState.Unknown.getState()))
            {
                return;
            }
        }

        if (afSubIns.getSubControlState().equals(ESubState.SLEEP_SENDRESULT.getState())) {
            // set for resourceItem summary own result

            afSubIns.setSubControlState(ESubState.WAIT_REPORT.getState());
            afSubIns.setSubNextState(ESubState.WAIT_REPORT.getState());
            afInstance.incrementMainCountWait();
            afInstance.decrementMainCountProcess();
            afInstance.setMainIsLock(false);
            return;
        }

        if(afSubIns.getSubControlState().contains(ESubState.WAIT_REPORT.toString()))
        {
            afSubIns.setSubControlState(ESubState.WAIT_REPORT.toString());
            afSubIns.setSubNextState(ESubState.WAIT_REPORT.toString());
            //AFLog.d("WAIT MainCountProcess: " + afInstance.getMainCountProcess());

            AFLog.d("TimeStampIn    : " + afInstance.getMainTimeStampIncoming());
            AFLog.d("ExpirationDate : " + afSubIns.getExpirationDateResourceOrder());
            Seconds diffExpired = Seconds.secondsBetween(Config.getHrzDateFormat().parseDateTime(afSubIns.getExpirationDateResourceOrder()), Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampIncoming()));
            AFLog.d("[WAIT_REPORT] diffExpired: " + diffExpired.getSeconds());

            if(diffExpired.getSeconds() > 0) {
                afInstance.setMainIsFinishFlow(true);
                afSubIns.setSubResourceOrderExpired(true);
            }

            if(!afSubIns.getSubNextState().equals(ESubState.Unknown.toString())) {
                return;
            }
        }

        /*
        if (afSubIns.getSubControlState().equals(ESubState.PROCESSING_RESOURCEITEM.getState())&& afSubIns.getSubResultCode().equals("20000")) {
//            AFLog.d("in do_business PROCESSING_RESOURCEITEM ");
            
            afSubIns.setSubControlState(ESubState.SDF_COMMITQUOTAINFRA.getState());
            afSubIns.setSubNextState(ESubState.SDF_COMMITQUOTAINFRA.getState());

            if (!afSubIns.getSubNextState().equals(ESubState.Unknown.getState())) {
                return;
            }
        }

        if (afSubIns.getSubControlState().equals(ESubState.PROCESSING_RESOURCEITEM.getState())&& !afSubIns.getSubResultCode().equals("20000")) {
//            AFLog.d("in do_business PROCESSING_RESOURCEITEM ");

            afSubIns.setSubControlState(ESubState.SDF_RELEASEQUOTA.getState());
            afSubIns.setSubNextState(ESubState.SDF_RELEASEQUOTA.getState());

            if (!afSubIns.getSubNextState().equals(ESubState.Unknown.getState())) {
                return;
            }
        }

        /
        if(afSubIns.getSubControlState().equals(ESubState.SDF_COMMITQUOTAINFRA.getState())) {
//            AFLog.d("in do_business SDF_COMMITQUOTAINFRA ");
            afSubIns.setSubControlState(ESubState.END.getState());
            afSubIns.setSubNextState(ESubState.Unknown.getState());

            if (!afSubIns.getSubNextState().equals(ESubState.Unknown.getState())) {
                return;
            }
        }

        if(afSubIns.getSubControlState().equals(ESubState.SDF_RELEASEQUOTA.getState())) {
//            AFLog.d("in do_business SDF_RELEASEQUOTA ");

            afSubIns.setSubControlState(ESubState.END.getState());
            afSubIns.setSubNextState(ESubState.Unknown.getState());


            if (!afSubIns.getSubNextState().equals(ESubState.Unknown.getState())) {
                return;
            }
        }
        */

        if(afSubIns.getSubControlState().equals(ESubState.SleepForErrorHandling.getState())) {

            if (!afSubIns.getSubNextState().equals(ESubState.Unknown.getState())) {
                return;
            }
            afSubIns.setSubNextState(afSubIns.getSubNextOfNextState());
            afSubIns.setSubControlState(afSubIns.getSubNextOfNextState());
        }

        if (!afSubIns.getSubControlState().equals(ESubState.IDLE_TRIGGER.getState()) &&
            !afSubIns.getSubControlState().contains("RESOURCEORDER") &&
            !afSubIns.getSubControlState().contains("SDF") &&
            !afSubIns.getSubControlState().equals(ESubState.CLIENT_POSTRESULT.getState()) &&
            !afSubIns.getSubControlState().equals(ESubState.WAIT_REPORT.getState()) &&
            !afSubIns.getSubControlState().equals(ESubState.END.getState()) &&
            !afSubIns.getSubControlState().equals(ESubState.IDLE_Purge.getState())) {
        	
        	if (afSubIns.getSubNextState().equals(ESubState.Unknown.getState())||afSubIns.getSubNextState().equals(ESubState.WAIT.getState())||afSubIns.getSubNextState().equals(ESubState.IDLE_RESOURCEITEM.getState())) {
            // for state idle_resourceItemm, wait and  NE task
            Do_Internal_ResourceItem doInternalResourceItem = new Do_Internal_ResourceItem();
            doInternalResourceItem.doBusinessLogic(abstractAF, afInstance, afSubIns);
        	}
            // Add Do_Internal_NE
            if(afSubIns.isNeTask() && !afSubIns.getSubControlState().equals(ESubState.END.getState()))
            {
                new Do_Internal_NE().validateAndProcess(abstractAF, afInstance, afSubIns);
                if(!afSubIns.isProcessOutputMessageComplete())
                {
                    afInstance.setMainIsFinishFlow(true);
                    afSubIns.setSubControlState(ESubState.END.getState());
                    afSubIns.setSubNextState(ESubState.Unknown.toString());
                }
            }

            if (!afSubIns.getSubNextState().equals(ESubState.Unknown.getState())) {
                return;
            }
        }

        if (afSubIns.getSubControlState().equals(ESubState.END.getState())) {
            //AFLog.d("in do_business END_ResourceItem ");
            new Do_END_ResourceItem().doBusinessLogic(abstractAF, afInstance, afSubIns);

            if (!afSubIns.getSubNextState().equals(ESubState.Unknown.getState())) {
                return;
            }
        }
                

        if(afSubIns.getSubControlState().equals(ESubState.CLIENT_POSTRESULT.getState())) {
            //AFLog.d("in do_business HRZ_POSTRESULT ");

            afSubIns.setSubControlState(ESubState.SDF_POSTREPORT.getState());
            afSubIns.setSubNextState(ESubState.SDF_POSTREPORT.getState());

            if(!afSubIns.getSubNextState().equals(ESubState.Unknown.getState()))
            {
                return;
            }
        }

        if(afSubIns.getSubControlState().equals(ESubState.SDF_POSTREPORT.getState())) {
            //AFLog.d("in do_business SDF_POSTREPORT ");
            afSubIns.setSubControlState(ESubState.END.getState());
            afSubIns.setSubNextState(ESubState.END.getState());
            afInstance.decrementMainCountProcess();

            if(!afSubIns.getSubNextState().equals(ESubState.Unknown.getState()))
            {
                return;
            }
        }
        
        if (afSubIns.getSubControlState().equals(ESubState.END_RESOURCEORDER.getState())) {
            //AFLog.d("in do_business END_RESOURCEORDER ");
            afSubIns.setSubControlState(ESubState.END.getState());
            afSubIns.setSubNextState(ESubState.END.getState());
            
            if(!afSubIns.getSubNextState().equals(ESubState.Unknown.getState()))
            {
                return;
            }
        }
        if (afSubIns.getSubControlState().equals(ESubState.END_XXX.getState())) {
            afSubIns.setSubControlState(ESubState.END.getState());
            afSubIns.setSubNextState(ESubState.END.getState());
            
            if(!afSubIns.getSubNextState().equals(ESubState.Unknown.getState()))
            {
                return;
            }
        }

        if (afSubIns.getSubControlState().equals(ESubState.IDLE_Purge.toString())) {
            afSubIns.setSubControlState(ESubState.END.toString());
            afSubIns.setSubNextState(ESubState.END.toString());

            if(!afSubIns.getSubNextState().equals(ESubState.Unknown.toString())) {
                return;
            }
        } else {
            AFLog.e("[Error] state not found: " + afSubIns.getSubControlState()+".");
        }
    }
}
