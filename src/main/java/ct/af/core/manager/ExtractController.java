package ct.af.core.manager;

import ct.af.enums.ECommand;
import ct.af.enums.EEventType;
import ct.af.enums.ERet;
import ct.af.enums.ESubState;
import ct.af.extracterd.*;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.utils.AFUtils;
import ct.af.utils.Config;
import ct.af.utils.LogUtils;
import ct.af.utils.TimeoutManagerFlag;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ExtractController {
    public AFSubInstance enterToParser(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns, EquinoxRawData eqxRawData, String currentState) {
        AFLog.d("********** ExtractController ==> CurrentState : " + currentState + " **********");

        if (currentState.equals(ESubState.IDLE_RESOURCEORDER.getState())) {
        	return new In_IDLE_ResourceOrder().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else if(currentState.equals(ESubState.IDLE_XXX.getState())) {
        	return new In_IDLE_Xxx().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else if(currentState.equals(ESubState.IDLE_XML.getState())){
        	return new In_IDLE_Xml().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else if (currentState.equals(ESubState.IDLE_TRIGGER.getState())){
        	return new In_IDLE_Trigger().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else if (currentState.equals(ESubState.SDF_GETRESOURCEINVENTORY.getState())){
        	return new In_SDF_GetResourceInventory().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else if (currentState.equals(ESubState.SDF_GETRESOURCEINFRANODE.getState())) {
            return new In_SDF_GetResourceInfraNode().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else if (currentState.equals(ESubState.SDF_RESERVEQUOTA.getState())) {
            return new In_SDF_ReserveQuota().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else if (currentState.equals(ESubState.SDF_COMMITQUOTAINFRA.getState())){
            return new In_SDF_CommitQuotaInfra().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else if (currentState.equals(ESubState.SDF_RELEASEQUOTA.getState())){
            return new In_SDF_ReleaseQuota().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else if (currentState.equals(ESubState.PROCESSING_RESOURCEITEM.getState())) {
            return new In_NE().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else if (currentState.equals(ESubState.CLIENT_POSTRESULT.getState())) {
           return new In_CLIENT_PostResult().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else if (currentState.equals(ESubState.SDF_POSTREPORT.getState())) {
            return new In_SDF_PostReport().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else if (currentState.equals(ESubState.IDLE_Purge.toString())) {
            return new In_IDLE_Purge().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else if (currentState.equals(ESubState.SleepForErrorHandling.getState())) {
            return new In_SLEEP_ForErrorHandling().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else if (currentState.equals(ESubState.SLEEP_SENDRESULT.getState())) {
          return new In_SLEEP_SendResult().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
        } else {
            if (afSubIns.isNeTask()) {
                return new In_NE().extractRawData(abstractAF, afInstance, afSubIns, eqxRawData);
            } else {
                // unknown
            }
        }
        return afSubIns;
    }

    public AFSubInstance checkStateByInvoke(AbstractAF abstractAF, AFInstance afInstance, EquinoxRawData eqxRawData) {
        String rawDataInvoke = eqxRawData.getInvoke();
        String rawDataType = eqxRawData.getType();
        String rawDataRet = eqxRawData.getRet();
        String rawDataECode = eqxRawData.getRawDataAttribute("ecode");
        String rawDataOrig = eqxRawData.getOrig();

        AFLog.d("Rawdata invoke : "+rawDataInvoke);
        AFLog.d("Rawdata type : "+rawDataType);
        AFLog.d("Rawdata ret : "+rawDataRet);
        AFLog.d("Rawdata ecode : "+rawDataECode);
        AFLog.d("Rawdata orig : "+rawDataOrig);

        AFSubInstance afSubIns = null;
        String currentState;
        


        ECommand command = ECommand.UNKNOWN;

        
	        if (rawDataType.equals(EEventType.REQUEST.getEventType()) && rawDataRet.equals(ERet.RET0.getRet())) {
	            if (rawDataOrig.contains("ES05")) {
                currentState = ESubState.IDLE_XXX.getState();
//              currentState = ESubState.IDLE_XML.getState();
//	            	currentState = ESubState.IDLE_RESOURCEORDER.getState();
	            	command = ECommand.RESOURCEORDER_ASYNC;
	                
//	                String[] urlSplit =  eqxRawData.getRawDataAttribute("url").split("/");
//	                for(String url :urlSplit){
//	                	if(url.contains("synchronous")) {
//	                	
//	                		command = ECommand.RESOURCEORDER_SYNC;
//	                		break;
//	                	}else {
//	            
//	                		command = ECommand.RESOURCEORDER_ASYNC;
//	                	}
//	                }
	                afSubIns = new AFSubInstance();
	                afSubIns.setSubInstanceNo(new AFUtils().subInsNoGenerator(afInstance, command.getCommand()));
	                afSubIns.setSubInitTimeStampIn(afInstance.getMainTimeStampIncoming());
	                afSubIns.setSubInitOrig(eqxRawData.getOrig());
	                afSubIns.setSubInitInvoke(eqxRawData.getInvoke());
	                afSubIns.setSubInitURL(eqxRawData.getRawDataAttribute("url"));
	                afSubIns.setSubInitMethod(eqxRawData.getRawDataAttribute("method"));
	                afSubIns.setSubInitCmd(command.getCommand());
	                afSubIns.setRet(rawDataRet);
	                afSubIns.setEcode(rawDataECode);
	
	
	                afSubIns = enterToParser(abstractAF, afInstance, afSubIns, eqxRawData, currentState);
	            }
	            else if (rawDataOrig.contains("E11")){	 
	            		new TimeoutManagerFlag().incrementMaxActive(Config.isEnableTimeoutManagerFlag());
		            	currentState = ESubState.IDLE_TRIGGER.getState();
		            	command = ECommand.TRIGGER;
		            	afSubIns = new AFSubInstance();
		                afSubIns.setSubInstanceNo(new AFUtils().subInsNoGenerator(afInstance, command.getCommand()));
		                afSubIns.setSubInitTimeStampIn(afInstance.getMainTimeStampIncoming());
		                afSubIns.setSubInitOrig(eqxRawData.getOrig());
		                afSubIns.setSubInitInvoke(eqxRawData.getInvoke().split("\\.")[3]);
		                afSubIns.setSubInitURL(eqxRawData.getRawDataAttribute("url"));
		                afSubIns.setSubInitMethod(eqxRawData.getRawDataAttribute("method"));
		                afSubIns.setSubInitCmd(command.getCommand());
		                afSubIns.setSubControlState(currentState);
		                afSubIns.setSubNextState(ESubState.Unknown.getState());
		                afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);
	            }
	        } else {
            // resp and req not ret0

            boolean isPurge = false;
            boolean isDelayResp = false;

            String[] retError = new String[]{"1","2","3"};

            if((rawDataInvoke.contains(";")) || (Arrays.asList(retError).contains(rawDataRet) && (rawDataType.equals(EEventType.RESPONSE.getEventType())))) {
                AFLog.d("isPurge");
                isPurge = true;
            } else {
                String respSubInsNo = new AFUtils().getSubInsNoFromInvoke(rawDataInvoke);
                HashMap<String, AFSubInstance> subIns = afInstance.getMainSubInstance();
                boolean subInsFound = subIns.containsKey(respSubInsNo);

                if (subInsFound) {
                    ArrayList<String> subInsInvoke = subIns.get(respSubInsNo).getSubInvoke();
                    int subInsInvokeSize = 0;
                    if(subInsInvoke!=null){
                    	subInsInvokeSize=subInsInvoke.size();
                    	AFLog.d("respSubInsNo : "+respSubInsNo);
                        AFLog.d("subInsInvoke : "+subInsInvoke);
                        
                        if(subInsInvokeSize > 0) {

                            if(!subInsInvoke.contains(rawDataInvoke)) {
                                AFLog.d("****** subInsInvoke  : "+subInsInvoke);
                                AFLog.d("****** rawDataInvoke : "+rawDataInvoke);

                                AFLog.d("isDelay Response - respSubInsNo not in list subInsInvoke");
                                isDelayResp = true;
                            }
                        } else {
                            AFLog.d("isDelay Response - InvokeSize <= 0");
                            isDelayResp = true;
                        }
                    }                                                 
                } else {
                    AFLog.d("isDelay Response - subIns not Found");
                    isDelayResp = true;
                }
            }

            AFLog.d("isPurge : "+isPurge);
            AFLog.d("isDelayResp : "+isDelayResp);

            if(isPurge || isDelayResp) {

                afSubIns = enterToParser(abstractAF, afInstance, afSubIns, eqxRawData, ESubState.IDLE_Purge.toString());
                
                try{
            		LogUtils.writeIncomingUnknownLog(abstractAF, afSubIns, afInstance, eqxRawData);
            	} catch(Exception e) {
            		AFLog.d("Cannot write IncomingUnknownLog!!");
            	}

            } else {
                // Resp. from Server
                try
                {
                    afSubIns = afInstance.getMainSubInstance().get(new AFUtils().getSubInsNoFromInvoke(rawDataInvoke));
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    afSubIns = enterToParser(abstractAF, afInstance, afSubIns, eqxRawData, ESubState.IDLE_Purge.toString());
                }
                AFLog.d("Delete invoke in sub instance");

                ArrayList<String> subInvoke = afSubIns.getSubInvoke();
                ArrayList<String> subTimeoutArray = afSubIns.getSubTimeoutArray();
                ArrayList<String> subStateArray = afSubIns.getSubStateArray();

                AFLog.d("Before Delete : "+subInvoke.toString());

                int indexOfInvoke = subInvoke.indexOf(rawDataInvoke);
                AFLog.d("*********** SubInvoke : " + subInvoke);
                AFLog.d("*********** IndexOfInvoke = " + indexOfInvoke);

                subInvoke.remove(indexOfInvoke);
                subStateArray.remove(indexOfInvoke);
                subTimeoutArray.remove(indexOfInvoke);

                AFLog.d("After Delete : "+subInvoke.toString());

                currentState = new AFUtils().getNextStateFromInvoke(rawDataInvoke);

                afSubIns = enterToParser(abstractAF, afInstance, afSubIns, eqxRawData, currentState);

            }
        }

        return afSubIns;

    }
}
