package ct.af.message.outgoing;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ct.af.enums.*;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.instance.PostSubInstance;
import ct.af.instance.SubEDR;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.utils.*;
import ct.af.utils.GsonTools.JsonObjectExtensionConflictException;
import ec02.af.abstracts.AbstractAF;
import ec02.af.data.AFDataFactory;
import ec02.af.utils.AFLog;
import ec02.data.enums.EEquinoxRawData;
import ec02.data.interfaces.EquinoxRawData;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public class Out_CLIENT_PostResult {

    public void hrzMessage(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns)
    {

    	//loop for each resourceItem
        HashMap<String, PostSubInstance> postInstanceMap = afSubIns.getPostSubInstanceMap();
        SortedSet<String> subResourceItemKeys = new TreeSet<>(postInstanceMap.keySet());
        
        StringBuilder rscOrderStatus = new StringBuilder("");
        StringBuilder rscOrderErrMsg = new StringBuilder("");
        StringBuilder rscOrderDevMsg = new StringBuilder("");
		JsonArray resourceItemArray = new JsonArray();
	    EResultCode resultCode = null;
	    for (String key : subResourceItemKeys){
	    	JsonObject resourceItemJson = new JsonObject();
	    	PostSubInstance postSubInstance = postInstanceMap.get(key);
            AFLog.d("======================================== "+key+" ========================================");
            AFLog.d(postSubInstance.toString());

            if(StringUtils.isNotBlank(postSubInstance.getResourceItemId())){
                resourceItemJson.addProperty("resourceItemId", postSubInstance.getResourceItemId());
            }
			resourceItemJson.addProperty("resourceName", postSubInstance.getResourceName());
			resourceItemJson.addProperty("resourceItemStatus", postSubInstance.getResourceItemStatus());
			resourceItemJson.addProperty("resourceItemErrMessage", postSubInstance.getResourceItemErrMessage());
 
	    	//Check status of each ResourceItem
	    	if(!postSubInstance.getResourceItemStatus().equals(EResultCode.RE20000.getResultStatus())){
	    		rscOrderStatus.append(Integer.toString(0));
	    	}
	    	
	    	//Get ResourceItemErrMessage
	    	if(!postSubInstance.getResourceItemStatus().contains(EResultCode.RE20000.getResultStatus()) 
	    			&& StringUtils.isNotBlank(postSubInstance.getResourceItemErrMessage())) {
    			if(StringUtils.isNotBlank(rscOrderErrMsg)){
	    			rscOrderErrMsg.append("; ");
	    		}
    			rscOrderErrMsg.append(postSubInstance.getResourceName()
    					+(postSubInstance.getSubInstanceNo().contains("RESOURCEITEM")?
    							("("+Integer.parseInt(postSubInstance.getSubInstanceNo().replaceAll("\\D", ""))+") ")
    							:"")
    					+postSubInstance.getResourceItemStatus()+": "+postSubInstance.getResourceItemErrMessage());
	    	}
	    	
	    	if(StringUtils.isBlank(rscOrderDevMsg)){
	    		rscOrderDevMsg.append(postSubInstance.getDevelopMessage());
	    	} else {
	    		rscOrderDevMsg.append(";"+postSubInstance.getDevelopMessage());
	    	}
	    	
			resourceItemJson.addProperty("errorFlag", postSubInstance.getNew_errorFlag());
			AFLog.d("Out_CLIENT + errorFlag :"+ postSubInstance.getNew_errorFlag());
			DateTime orderDateConvert = Config.getFormatDateWithMiTz().parseDateTime(afSubIns.getSubInitTimeStampIn());
			
			String resourceActivatedDate = "";
			if(StringUtils.isNotBlank(postSubInstance.getResourceActivatedDate())){
				resourceActivatedDate = postSubInstance.getResourceActivatedDate();
				
			} else if (StringUtils.isNotBlank(postSubInstance.getDropResourceParent())) {
				
				// Find resourceIndex in all postSubInstance is in resourceParent in DropResourceConfig
				// To process clone parent detail to child
				for (String keyCheck : subResourceItemKeys){
					PostSubInstance postSubInstanceForCheck = postInstanceMap.get(keyCheck);
					String resourceIndexCheck = postSubInstanceForCheck.getResourceIndex();
					String[] dropResourceParentList = postSubInstance.getDropResourceParent().split(",");
					
					for ( String dropResourceParent : dropResourceParentList){
						
						if (resourceIndexCheck.equals(dropResourceParent)){
							// Parent dead then child dead too
							if (!postSubInstanceForCheck.getResultCode().equals(EResultCode.RE20000.getResultCode())) {
								AFLog.d("Clone Status and ErrMessage from parent : "+keyCheck+" ("+resourceIndexCheck+")");
								postSubInstance.setResourceItemStatus(postSubInstanceForCheck.getResourceItemStatus());
								postSubInstance.setResourceItemErrMessage(postSubInstanceForCheck.getResourceItemErrMessage());
								resourceItemJson.addProperty("resourceItemStatus", postSubInstance.getResourceItemStatus());
								resourceItemJson.addProperty("resourceItemErrMessage", postSubInstance.getResourceItemErrMessage());	
								
							}
							
							// Clone resourceActivatedDate from parent to child
							if (postSubInstanceForCheck.getResourceActivatedDate() != null){
								AFLog.d("Clone resourceActivatedDate from parent : "+keyCheck+" ("+resourceIndexCheck+")");
								resourceActivatedDate = postSubInstanceForCheck.getResourceActivatedDate();
								postSubInstance.setResourceActivatedDate(resourceActivatedDate);
							}
						}
					}
				}
			} 

			resourceItemJson.addProperty("resourceActivatedDate", 
					(StringUtils.isNotBlank(resourceActivatedDate)
					?resourceActivatedDate:Config.getJourneyDateFormat().print(orderDateConvert)));
			try {
				//if resourceMaster has responseToClient 
				if(postSubInstance.getResourceItemAttribute()!=null){					
					String resourceIndex = postSubInstance.getResourceIndex();
					//if resourceMaster of this resource index have "responseToClientType": "Special"
					if(StringUtils.isNotBlank(resourceIndex)&&Config.getResourceMasterHashMap().get(resourceIndex)!=null){
						String responseToClientType = Config.getResourceMasterHashMap().get(resourceIndex).getResponseToClientType();
						if(StringUtils.isNotBlank(responseToClientType)&&responseToClientType.equalsIgnoreCase("Special")) {
							postSubInstance.setResourceItemAttribute(SpecialResptoClient.toSpecialRespToClient(postSubInstance.getResourceItemAttribute(),postSubInstance.getResourceIndex()));
						}
					}
					
					//finally merge json as below
					if(postSubInstance.getResourceItemAttribute()!=null){
						GsonTools.extendJsonObject(resourceItemJson,GsonTools.ConflictStrategy.PREFER_FIRST_OBJ,postSubInstance.getResourceItemAttribute());
					}
				}
			} catch (JsonObjectExtensionConflictException e) {
				AFLog.e("[Exception] can't merge response to client: " + e.getMessage()+".");
				AFLog.e(e);
			} catch (NullPointerException ex){
				AFLog.e("[Exception] can't merge response to client: " + ex.getMessage()+".");
				AFLog.e(ex);
			}

			JsonObject specialErrHandlingJson = new JsonObject();
	    	//looping to get suppcode
	    	JsonArray suppcodeArray = new JsonArray();
	    	for(int i = 0; i < postSubInstance.getSuppCode().size(); i++){
	    		suppcodeArray.add(postSubInstance.getSuppCode().get(i));
	    	}
	    	specialErrHandlingJson.add("suppCode", suppcodeArray);

	    	//looping to get taskKeyCondition
	    	JsonArray taskKeyConditionArray = new JsonArray();
	    	for(int i = 0; i < postSubInstance.getTaskKeyCondition().size(); i++){
	    		taskKeyConditionArray.add(postSubInstance.getTaskKeyCondition().get(i));
	    	}
	    	specialErrHandlingJson.add("taskKeyCondition", taskKeyConditionArray);
	    	
	    	//looping to get taskDeveloperMessage
	    	JsonArray taskDeveloperMessageArray = new JsonArray();
	    	for(int i = 0; i < postSubInstance.getTaskDeveloperMessage().size(); i++){
	    		taskDeveloperMessageArray.add(postSubInstance.getTaskDeveloperMessage().get(i));
	    	}
	    	specialErrHandlingJson.add("taskDeveloperMessage", taskDeveloperMessageArray);
	    	
			resourceItemJson.add("specialErrHandling", specialErrHandlingJson);
	    	resourceItemArray.add(resourceItemJson);
	    }
	    
	    //ResponseHeader
	    String resultDesc = "";
	    if(subResourceItemKeys!=null && !subResourceItemKeys.isEmpty()){ // has result on resourceItemList
	    //TODO if 0 is optional will summary as success with condition
	    	if(afSubIns.isSubResourceOrderExpired()) { // Case resourceOrder Expired.
	    		resultCode = EResultCode.RE50000;
		    	resultDesc = resultCode.getResultStatus() + ": " + EResultCode.RE50024.getResultDesc() + (StringUtils.isNotBlank(rscOrderErrMsg)?"; "+rscOrderErrMsg:"");
	    	} else if (rscOrderStatus.toString().contains("0")){ // Case fail.
	    		resultCode = EResultCode.RE50000;
		    	resultDesc = resultCode.getResultStatus() + ": " + rscOrderErrMsg;
		    } else { // Case success.
		    	resultCode = EResultCode.RE20000;
		    	resultDesc = resultCode.getResultStatus();
		    }
	    } else {
	    	resultCode = EResultCode.valueOf("RE"+afSubIns.getSubResultCode());
	    	if(resultCode.getResultCode().equals(EResultCode.RE20000.getResultCode())){
		    	resultDesc = resultCode.getResultStatus();
	    	} else {
	    		resultDesc = resultCode.getResultStatus()+": " + afSubIns.getErrorMessage();
	    	}
	    }
	    
		JsonObject hrzRespJson = new JsonObject();
		JsonObject responHeaderJson = new JsonObject();
    	
		//responseHeader parameter base on requestHeader parameter from client resourceOrder
    	Gson gson = GsonPool.getGson();
    	String ReqRawData = afSubIns.getReqRawData().trim();
    	JsonParser parser = new JsonParser();
    	
    	//Parse JSON of resourceOrder && add to responseHeader
    	try{
        	JsonObject jsonObj = (JsonObject) parser.parse(ReqRawData);
        	HashMap<?, ?> hashMap = gson.fromJson(jsonObj.get("requestHeader") , HashMap.class);
        	GsonPool.pushGson(gson);
        	for(Entry<?, ?> entry : hashMap.entrySet()){
        		if(StringUtils.isNotBlank((String) entry.getValue())){
            		responHeaderJson.addProperty((String) entry.getKey() , (String) entry.getValue());
        		}
        	}
    	} catch (Exception ex) {
    		AFLog.d("[ParseException] Can't parse resourceOrder");
    	}
		
		responHeaderJson.addProperty("resourceOrderId", afSubIns.getRequestId());
		responHeaderJson.addProperty("resultCode", resultCode.getResultCode());
		responHeaderJson.addProperty("resultDesc", resultDesc.trim());
		responHeaderJson.addProperty("developerMessage", rscOrderDevMsg.toString());

		hrzRespJson.add("responseHeader", responHeaderJson);
		hrzRespJson.add("resourceItemList", resourceItemArray);


        DateTime timeStamp = new DateTime();
        afSubIns.setSubcompleteDate(Config.getJourneyDateFormat().print(timeStamp));    
        afSubIns.setSubCompleteDateCDR(Config.getFormatDateWithMiTz().print(timeStamp));

		Gson prettyGson = GsonPool.getPrettyGsons();
        String message = prettyGson.toJson(hrzRespJson);
        GsonPool.pushPrettyGsons(prettyGson);

        afSubIns.setRscOrderDevMsg(rscOrderDevMsg.toString());
        afSubIns.setSubResultCodeJourney(resultCode.getResultCode());
        afSubIns.setErrorMessage(resultDesc);
        afSubIns.setResRawData(message);
    }


    public EquinoxRawData messageBuilder(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {

        Param_IDLE_ResourceOrder clientParam = (Param_IDLE_ResourceOrder) afSubIns.getSubClientParameter();
        DateTimeFormatter subEffectiveTime = Config.getFormatDateWithMiTz();
        DateTime timeStampOut = new DateTime();

        //String user = ((Param_IDLE_ResourceOrder) afSubIns.getSubClientParameter()).getUser();
        
        //-- Construct EquinoxRawData --//
        EquinoxRawData eqxRawData = AFDataFactory.createEquinoxRawData();
        hrzMessage(abstractAF, afInstance, afSubIns);

        Map<String, String> map = new HashMap<>();
        int totimeout=Config.getDefaultServerTimeout();
        //String url = "/Resource/V1/PLCMngt/ResourceManagement/ResourceInventory.json";
        String url="";
        map.put(EEqxMsg.TYPE.getEqxMsg(), EEventType.REQUEST.getEventType());
        map.put(EEqxMsg.CTYPE.getEqxMsg(), ECType.TEXTPLAIN.getCType());
        map.put(EEqxMsg.PROTOCOL.getEqxMsg(), EProtocol.HTTP.toString());
        //TODO change to real user
//        map.put(EEqxMsg.DESTINATION.getEqxMsg(), Config.HRZ_INTERFACE);
//        map.put(EEqxMsg.URL.getEqxMsg(), Config.BWO_URL_USER_LIST.get(user));
        map.put(EEqxMsg.VAL.getEqxMsg(), afSubIns.getResRawData());
       // map.put(EEqxMsg.URL.getEqxMsg(), afSubIns.getSubInitURL());
        map.put(EEqxMsg.METHOD.getEqxMsg(), EMethod.POST.toString());
        
        if(afSubIns.getSubInitCmd().equals(ECommand.RESOURCEORDER_SYNC.getCommand())||
        	!Config.getUrlServersToClientHashMap().containsKey(clientParam.getUserSys())
        	||Config.getUrlServersToClientHashMap().get(clientParam.getUserSys())==null||Config.getUrlServersToClientHashMap().get(clientParam.getUserSys()).isEmpty()){
          	url =afSubIns.getSubInitURL();
          
        }else{
         	url = Config.getUrlServersToClientHashMap().get(clientParam.getUserSys().trim());
	      	 AFLog.d("RESOURCEORDER_ASYNC "+afSubIns.getSubInitCmd());
	      	 if(Config.getServersTimeoutHashMap().containsKey(clientParam.getUserSys())
	      		&&Config.getServersTimeoutHashMap().get(clientParam.getUserSys())!=null){
	      		totimeout=Config.getServersTimeoutHashMap().get(clientParam.getUserSys());
	      	 }
	      	 else   {
	      		totimeout=Config.getDefaultServerTimeout();
			}
     
        }

        map.put(EEqxMsg.URL.getEqxMsg(), url);
        

        String edrCommand = "";     
        
        if(map.get(EEqxMsg.URL.getEqxMsg())!=null)
        {
        	int start = map.get(EEqxMsg.URL.getEqxMsg()).lastIndexOf('/') + 1;
        	edrCommand = map.get(EEqxMsg.URL.getEqxMsg()).substring(start);
        	int stop =  edrCommand.indexOf('.');
        	if(stop>0){
        		edrCommand = edrCommand.substring(0,stop);
        	}
        }             
        AFLog.d("configURL : "+url);
        AFLog.d("edrCommand : "+edrCommand);

        //-- Invoke --//
        String invoke = new AFUtils().invokeGenerator(abstractAF,afSubIns.getSubInitInvoke(),afSubIns.getSubInitCmd(),afSubIns.getSubNextState(),afSubIns.getSubInstanceNo());
        afSubIns.setSubInvoke(invoke);
        afSubIns.setSubInvokeResp(invoke);
        AFLog.d("[INVOKE] : "+EEqxMsg.INVOKE.getEqxMsg());
        AFLog.d("[INVOKE] : "+invoke);

        //-- Sub state --//
        afSubIns.setSubStateArray(afSubIns.getSubNextState());
        afSubIns.setSubCountChild(afSubIns.getSubCountChild()+1);

        //-- Timeout --//
        String timeout = Config.getFormatDateWithMiTz().print(new DateTime().plusSeconds(totimeout));
        afSubIns.setSubTimeout(timeout);
        afSubIns.setSubTimeoutArray(timeout);
        
        
        MessageBuilder builder = new MessageBuilder("");
        try
        {
        	String serversInterface;
        	if(clientParam.getUserSys()!=null) {
        		serversInterface = Config.getServerInterfaceHashMap().get(clientParam.getUserSys().trim());
        	} else {
        		serversInterface = "";
        	}
        	
    		abstractAF.getEquinoxUtils().sendHTTPRequestMessage(builder, EEquinoxRawData.CTypeHTTP.TEXT_PLAIN, invoke, serversInterface, map);
    		//-- Stat --//
            EStats statsOut = EStats.APP_SEND_W_CLIENT_POSTRESULT_REQUEST;
            statsOut.setCustomStat(EStats.APP_SEND_W_CLIENT_POSTRESULT_REQUEST,serversInterface);
            afSubIns.setStatsOut(statsOut);
            afInstance.setMainTimeStampOut(subEffectiveTime.print(timeStampOut));
        }
        catch (Exception e)
        {
        	AFLog.e("[Exception] can't build HTTPRequestMessage.");
            AFLog.e(e);
        }

        //-- EDR --//
        SubEDR subEDR = new SubEDR();
        if(!afSubIns.getSubInitURL().contains("/sync")) {
        subEDR.setNextState(afSubIns.getSubNextState());
        if(StringUtils.isNotBlank(afSubIns.getStatsOut().getStatName())) {
        	subEDR.setStatsOut(afSubIns.getStatsOut().getStatName());
        }
        LogUtils.prepareDataForEDR(abstractAF, afSubIns, invoke,timeStampOut, subEDR);
        }else {
        	try {
        	LogUtils.writeLogEDR(abstractAF, afSubIns, afInstance, eqxRawData);
        	}catch(Exception e) {
        		AFLog.e("[Exception] writeLogEDR error.");
        		AFLog.e(e);
        	}
        }
        AFLog.d("timeStampOut : " + afInstance.getMainTimeStampOut());
        AFLog.d("state : " + abstractAF.getEquinoxProperties().getState());
        
        return eqxRawData;
    }
}
