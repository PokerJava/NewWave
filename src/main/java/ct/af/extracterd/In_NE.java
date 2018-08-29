package ct.af.extracterd;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import ct.af.instance.AFInstance;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ct.af.enums.EEventType;
import ct.af.enums.EResultCode;
import ct.af.enums.EResultDescription;
import ct.af.enums.ERet;
import ct.af.enums.EStats;
import ct.af.enums.ESubState;
import ct.af.instance.AFSubInstance;
import ct.af.instance.PoolTask;
import ct.af.resourceModel.ResourceErrorHandling;
import ct.af.resourceModel.ResourceMappingCommand;
import ct.af.utils.Config;
import ct.af.utils.GsonPool;
import ct.af.utils.LogUtils;
import ct.af.utils.SuccessPatternUtil;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class In_NE {
	public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns,
			EquinoxRawData eqxRawData) {

		AFLog.d("In_Ne");
		String rawDataRet = eqxRawData.getRet();
		String rawDataVal = eqxRawData.getRawDataAttribute("val");
		String rawDataMessage = eqxRawData.getRawDataMessage();
		String rawDataInvoke = eqxRawData.getInvoke();
		String suppcode = afSubIns.getCurrentSuppcode();
		EResultCode resultCode = EResultCode.RE20000;
		EResultCode internalCode = EResultCode.RE20000;
		String taskId = rawDataInvoke.split("\\.")[5];
		Boolean issuccess = false;
		ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);
		
		PoolTask poolTask = new PoolTask();
		poolTask.setSuppCode(afSubIns.getCurrentSuppcode());
		poolTask.setNeId(afSubIns.getSubNeId());
		
		DateTimeFormatter subEffectiveTime = Config.getJourneyDateTask();
        DateTime timeStamp = new DateTime();
        afSubIns.getNeCompletionDate().put(taskId, subEffectiveTime.print(timeStamp));
        EStats statsIn = EStats.NE_IN_CUSTOM_STAT;
        long resTime = new Duration(Config.getJourneyDateTask().parseDateTime(afSubIns.getNeSubmissionDate().get(taskId)),
        		Config.getJourneyDateTask().parseDateTime(afSubIns.getNeCompletionDate().get(taskId))).getMillis();
		
        // for test resend

		if (!rawDataRet.equals("0")) {
			resultCode = EResultCode.RE50000;
			internalCode = EResultCode.RE50000;
		}
		
		if (StringUtils.isNotBlank(rawDataMessage) && ERet.RET0.getRet().equals(rawDataRet)) {
			rawDataMessage = unescape(rawDataMessage);
			if (resourceMappingCommand.getMessageFormat().equalsIgnoreCase("xml")
					|| resourceMappingCommand.getMessageFormat().equalsIgnoreCase("httpget")) {
				if (resourceMappingCommand.getSuccessPattern().equalsIgnoreCase("md")){
					issuccess = mapMdXmlResponse(afSubIns, rawDataMessage, rawDataInvoke);
				} else {
					issuccess = mapXmlResponse(afSubIns, rawDataMessage, rawDataInvoke);
				}
			} else if (resourceMappingCommand.getMessageFormat().equalsIgnoreCase("cmdLine")) {
				issuccess = commandline(afSubIns, rawDataMessage, rawDataInvoke);
			} else if (resourceMappingCommand.getMessageFormat().equalsIgnoreCase("cmdlinebase64")) {
				issuccess = commandlinebase64(afSubIns, rawDataVal, rawDataInvoke);
			} else if (resourceMappingCommand.getMessageFormat().equalsIgnoreCase("JSON")) {
				issuccess = jsonmessage(afSubIns, rawDataMessage, rawDataInvoke);
			}

			if (!issuccess) {
				resultCode = EResultCode.RE50000;
				internalCode = EResultCode.RE50000;
				statsIn.setNeCustomStat(afSubIns.getSubNeId(), resourceMappingCommand, EResultDescription.SUCCESS_WITH_ERR, EEventType.RESPONSE);
				afSubIns.setStatsIn(statsIn);
				
			}else {
				statsIn.setNeCustomStat(afSubIns.getSubNeId(), resourceMappingCommand, EResultDescription.SUCCESS, EEventType.RESPONSE);
				afSubIns.setStatsIn(statsIn);
				EStats statsExeTime = EStats.NE_EXE_TIME_CUSTOM_STAT;
				statsExeTime.setNeExeTimeCustomStat(afSubIns.getSubNeId(),resourceMappingCommand, EResultDescription.SUCCESS, EEventType.RESPONSE, resTime);
				afSubIns.setStatsExeTime(statsExeTime);
			}
		} else {
			AFLog.d("[ERROR] Error on "+taskId);
			String errorMessage = "";
			
			afSubIns.setSubNextState(ESubState.Unknown.toString());
			afSubIns.setSubCurrentState(suppcode);
	        afSubIns.setSubControlState(suppcode);
			if (rawDataRet.equals(ERet.RET4.getRet())) {
				errorMessage = afSubIns.getSubNeId() + ": TIMEOUT";
				resultCode = EResultCode.RE00004;
				statsIn.setNeCustomStat(afSubIns.getSubNeId(), resourceMappingCommand, EResultDescription.TIMEOUT, EEventType.RESPONSE);
				if(!afSubIns.isErrorHandling()) {
					errorHandlingForEqxRet(afSubIns,"ret4");
				}
			} else if (rawDataRet.equals(ERet.RET2.getRet())) {
				errorMessage = afSubIns.getSubNeId() + ": REJECT";
				resultCode = EResultCode.RE00002;
				statsIn.setNeCustomStat(afSubIns.getSubNeId(), resourceMappingCommand, EResultDescription.	REJECT, EEventType.RESPONSE);
				if(!afSubIns.isErrorHandling()) {
					errorHandlingForEqxRet(afSubIns,"ret2");
				}
			} else {
				errorMessage = afSubIns.getSubNeId() + ": OTHER ERROR";
				resultCode = EResultCode.RE50000;
				afSubIns.setResponseMessage(rawDataMessage);
				statsIn.setNeCustomStat(afSubIns.getSubNeId(), resourceMappingCommand, EResultDescription.ERROR, EEventType.RESPONSE);
			}
			AFLog.d("[ERROR] " + errorMessage);
			internalCode = EResultCode.RE50000;
			
			poolTask.setsMessage(errorMessage);
			poolTask.setStatus(resultCode.getResultStatus());
			poolTask.setErrCode(resultCode.getResultCode());
			poolTask.setResultCode(resultCode.getResultCode());
			afSubIns.setErrorMessage(errorMessage);
			afSubIns.getMappingPoolTask().put(taskId, poolTask);
			afSubIns.setStatsIn(statsIn);
			
		}

		afSubIns.setTaskId(taskId);
		
        AFLog.d("[ResultCode] "+resultCode.getResultCode());
        afSubIns.setSubCountChild(afSubIns.getSubCountChild()-1);
        afSubIns.setSubResultCode(resultCode.getResultCode());
        afSubIns.setSubInternalCode(internalCode.getResultCode()); // 20000 //

		String netype = resourceMappingCommand.getNeType();
		PoolTask taskPool = afSubIns.getMappingPoolTask().get(taskId);
		
		if(taskPool.getStatus().equalsIgnoreCase("success")&&!afSubIns.isErrorHandlingSuppcodeFlg()) {
			afSubIns.setSubNextState(ESubState.Unknown.toString());
			afSubIns.setSubCurrentState(suppcode);
	        afSubIns.setSubControlState(suppcode);
		}
		
		if (afSubIns.isErrorHandling() && !taskPool.getStatus().equalsIgnoreCase(EResultCode.RE20000.getResultStatus()) || afSubIns.isErrorHandlingSuppcodeFlg() ) {
			AFLog.d("In ErrorHandling");
			
			if (netype.equals("MD")) {
				String errorFlag = "";
				Object taskResponse = afSubIns.getMappingResponse().get(taskId);
				AFLog.d("TaskId : "+ taskId);
				try {
					errorFlag = ((HashMap<String, String>) ((HashMap<String, Object>) taskResponse).get("ResponseParameters")).get("ERROR_FLAG_1");
					AFLog.d("ErrorFlag TaskResp : " + errorFlag);
					AFLog.d("ResponseParameters : "+((HashMap<String, String>) ((HashMap<String, Object>) taskResponse).get("ResponseParameters")).toString());
				} catch (Exception e) {
					AFLog.d("Can't Get ErrorFlag");
				}

				if (errorFlag.isEmpty() || errorFlag == null) {// errorFlagMD == ""|| null; set newErrorFlag = 0;
					taskPool.setErrorFlag("0");
				} else {
					taskPool.setErrorFlag(errorFlag);
				}

			}else if (taskPool.getStatus() != null && taskPool.getStatus().equalsIgnoreCase(EResultCode.RE20000.getResultStatus())) {
				taskPool.setErrorFlag("1");
			
			} else {
				taskPool.setErrorFlag("0");
			}
			
		}else {

			if (netype.equals("MD")) {
				String errorFlag = "";
				Object taskResponse = afSubIns.getMappingResponse().get(taskId);
				AFLog.d("TaskId : "+ taskId);
				try {
					errorFlag = ((HashMap<String, String>) ((HashMap<String, Object>) taskResponse).get("ResponseParameters")).get("ERROR_FLAG_1");
					AFLog.d("ErrorFlag TaskResp : " + errorFlag);
					AFLog.d("ResponseParameters : "+((HashMap<String, String>) ((HashMap<String, Object>) taskResponse).get("ResponseParameters")).toString());
				} catch (Exception e) {
					AFLog.e("[Exception] can't get ERROR_FLAG_1 from MD.");
					AFLog.e(e);
				}

				if (errorFlag.isEmpty() || errorFlag == null) {// errorFlagMD == ""|| null; set newErrorFlag = 0;
					afSubIns.setNew_ErrorFlag("0");
					afSubIns.getTaskTempErrorFlag().put(taskId, "0");
					taskPool.setErrorFlag("0");
				} else {
					afSubIns.setNew_ErrorFlag(errorFlag);
					afSubIns.getTaskTempErrorFlag().put(taskId, errorFlag);
					taskPool.setErrorFlag(errorFlag);
					
				}

			} else if (taskPool.getStatus() != null && taskPool.getStatus().equalsIgnoreCase(EResultCode.RE20000.getResultStatus())) {

					afSubIns.setNew_ErrorFlag(afSubIns.getNew_ErrorFlag() + "1");
					taskPool.setErrorFlag("1");
				
			} else {
					afSubIns.setNew_ErrorFlag(afSubIns.getNew_ErrorFlag() + "0");
					taskPool.setErrorFlag("0");
			}
			AFLog.d("ErrorFlag : " + afSubIns.getNew_ErrorFlag());

		}


        try {
        	LogUtils.writeLogEDR(abstractAF, afSubIns, afInstance, eqxRawData);
        } catch (Exception e1) {
            AFLog.e("[Exception] writeLogEDR error.");
            AFLog.e(e1);
        }
        
      //-- TransectionLog --//
        try {
            LogUtils.writeTransactionLog(abstractAF, afSubIns, afInstance, eqxRawData);
        } catch (Exception e2) {
            AFLog.e("[Exception] writeTransactionLog error.");
            AFLog.e(e2);
        }
        
        if(afSubIns.isErrorHandling()&&afSubIns.isErrorHandlingSuppcodeFlg()) {
        	afSubIns.setCurrentSuppcode(afSubIns.getCurrentSuppcodeErrorhandling());
        	afSubIns.setSubNeId(afSubIns.getSubNeIdErrorhandling());
        }
        return afSubIns;
    }
    
	public Boolean mapMdXmlResponse(AFSubInstance afSubIns,String rawDataMessage,String rawDataInvoke) {
	    
    	String configLineData="";
		int startPos ;
		int endPos ;
		String dataVar = "";

        String suppcode = afSubIns.getCurrentSuppcode();
		ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);

		String keyInvoke = rawDataInvoke.split("\\.")[5];
		ArrayList<String> resconfig = resourceMappingCommand.getResponseMessage();

		String resMessage = rawDataMessage;

		String[] resMessageLine ;
		if(resMessage.contains("\n")){
			resMessageLine = resMessage.split("\n");
		}else{
			resMessageLine = resMessage.split("><");
			for(int i=0; i<resMessageLine.length;i++){
				if(i==0){
					resMessageLine[i]=(resMessageLine[i]+">");
				}else if (i==resMessageLine.length-1){
					resMessageLine[i]=("<"+resMessageLine[i]);
				}else {
					resMessageLine[i]=("<"+resMessageLine[i]+">");
				}
			}
		}

		String[] configMessageLine = new String[resconfig.size()];
		configMessageLine = resconfig.toArray(configMessageLine);
		
		String datasub="";
		int startForeachLoop = 0;
		int responseCountRecord = 0;
		boolean responseHeaderFlag = false;
		boolean responseParametersFlag = false;
		
		HashMap<String, Object> responseMessage =new HashMap<String,Object>();
		HashMap<String, Object> responseObjectParamHashMap = new HashMap<String, Object>();
	    
		for(int i = startForeachLoop; i < configMessageLine.length; i++) {
			configLineData=configMessageLine[i].trim();
			if (configLineData.replace("<", "").replace(">", "").equals("ResponseHeader")) {
				responseHeaderFlag = true;
				responseParametersFlag = false;
				
			} else if (configLineData.replace("<", "").replace(">", "").equals("ResponseParameters")) {
				responseHeaderFlag = false;
				responseParametersFlag = true;
			}
			
			if (responseHeaderFlag) {
				startPos = configLineData.indexOf("{@");
				endPos = configLineData.lastIndexOf("}");
				
		
				if (startPos != -1) {

					String startData = configLineData.substring(0, startPos);
		            dataVar = configLineData.substring(startPos, endPos + 1);
		            
		            int startAtIndex = dataVar.indexOf("{@");
		            int endAtIndex = dataVar.indexOf("}");
		
		            dataVar = dataVar.substring(startAtIndex, endAtIndex);
		            String[] splitdatavar = dataVar.split(":");
			
			        String startData2 = startData.replace("<", "").replace(">", "");
			        
			        int responseCount = responseCountRecord;
					for(int k = responseCount ; k < resMessageLine.length; k++)  {
						String startTag = resMessageLine[k].substring(0, resMessageLine[k].indexOf(">")+1).trim();
						if(startData2.equals(startTag.replace("<", "").replace(">", "").trim())){
			        		int startTagIndex = resMessageLine[k].trim().indexOf(">");
			                int endTagIndex = resMessageLine[k].trim().lastIndexOf("</");
			                datasub = resMessageLine[k].trim().substring(startTagIndex+1, endTagIndex); 
			                responseCountRecord=k+1;
			                break;
			        	} else if (startData2.equals(startTag.replace("<", "").replace("/>", "").trim())){
			                responseCountRecord=k+1;
			                break;
			        	}
			        }                
			        if(!("".equals(datasub))) {
			        	responseObjectParamHashMap.put(splitdatavar[1], datasub);
			        	responseMessage.put(splitdatavar[1], datasub);
			        }	                
			        datasub="";
			            
		            
				} 
			} else if (responseParametersFlag) {
				
				startPos = configLineData.indexOf("{@");
				endPos = configLineData.lastIndexOf("}");
				
		
				if (startPos != -1) {
		            dataVar = configLineData.substring(startPos, endPos + 1);
		            
		            int startAtIndex = dataVar.indexOf("{@");
		            int endAtIndex = dataVar.indexOf("}");
		
		            dataVar = dataVar.substring(startAtIndex, endAtIndex);
		            String[] splitdatavar = dataVar.split(":");
						
					int nameStartIndex = configLineData.indexOf("name=\"")+6;
					int nameEndIndex = configLineData.substring(nameStartIndex,configLineData.length()).indexOf("\"")+nameStartIndex;
					String startDataParam = configLineData.substring(nameStartIndex, nameEndIndex);
					int responseParametersStartLine = 0;
					int responseParametersEndLine = 0;
					for(int count = 0 ; count < resMessageLine.length; count++)  {
						if (resMessageLine[count].trim().equals("<ResponseParameters>")){
							responseParametersStartLine = count;
						} else if (resMessageLine[count].trim().equals("</ResponseParameters>")){
							responseParametersEndLine = count;
							break;
						}
					}
						
					for(int count = responseParametersStartLine ; count < responseParametersEndLine; count++)  {
						if (resMessageLine[count].trim().startsWith("<Parameter")) {
							int nameStartIndexResp = resMessageLine[count].indexOf("name=\"")+6;
							int nameEndIndexResp = resMessageLine[count].substring(nameStartIndexResp,resMessageLine[count].length()).indexOf("\"")+nameStartIndexResp;
							String startDataParamResp = resMessageLine[count].substring(nameStartIndexResp, nameEndIndexResp);
							if(startDataParam.equals(startDataParamResp)){
								int valueStartIndexResp = resMessageLine[count].indexOf("value=\"")+7;
								int valueEndIndexResp = resMessageLine[count].substring(valueStartIndexResp,resMessageLine[count].length()-1).indexOf("\"")+valueStartIndexResp;
								datasub = resMessageLine[count].substring(valueStartIndexResp, valueEndIndexResp);
				                responseCountRecord=count+1;
				                break;
				        	}
						}
			        }                
			        if(!("".equals(datasub))) {
			        	responseObjectParamHashMap.put(splitdatavar[1], datasub);
			        	responseMessage.put(splitdatavar[1], datasub);
			        }	                
			        datasub="";				
				}
			}
		}
		
		HashMap<String, Object> objectParamHashMap = getObjectParamHashMap(responseObjectParamHashMap);
		
	    
	    String neID = afSubIns.getSubNeId();
	    String patternID = resourceMappingCommand.getSuccessPattern();
	    AFLog.d("[NeId]"+ neID);
	    AFLog.d("[PatternId]"+ patternID);
	    HashMap<String, Object> objectMapkey = new HashMap<>();
	    if(keyInvoke.contains("-")) {
	    	objectMapkey.put(keyInvoke.substring(0, keyInvoke.indexOf("-")), objectParamHashMap);
	    	objectMapkey.put(keyInvoke, objectParamHashMap);
	    }else {
	    	objectMapkey.put(keyInvoke, objectParamHashMap);
	    }
	    AFLog.d("[ResponseObjectHashMap]"+ objectMapkey);
	    
	    SuccessPatternUtil successPatternUtil = new SuccessPatternUtil();	    
	    PoolTask poolTask = successPatternUtil.getTaskResult(patternID, objectParamHashMap, afSubIns);
	    AFLog.d(poolTask.toString());
	    
	    errorHandling(afSubIns, poolTask);
	    
	    afSubIns.getMappingPoolTask().put(keyInvoke, poolTask);
	    afSubIns.getMappingResponse().putAll(objectMapkey);
	    if(poolTask.getStatus()!=null && poolTask.getStatus().equals(EResultCode.RE20000.getResultStatus())){
	    	return true;
	    }else{
	    	return false;
	    }
    }
    public Boolean mapXmlResponse(AFSubInstance afSubIns,String rawDataMessage,String rawDataInvoke) {
    
    	String configLineData="";
		int startPos ;
		int endPos ;
		String dataVar = "";

        String suppcode = afSubIns.getCurrentSuppcode();
		ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);
		
		String keyInvoke = rawDataInvoke.split("\\.")[5];
		ArrayList<String> resconfig = resourceMappingCommand.getResponseMessage();

		String resMessage = rawDataMessage;


		String[] resMessageLine ;
		if(resMessage.contains("\n")){
			resMessageLine = resMessage.split("\n");
		}else{
			resMessageLine = resMessage.split("><");
			for(int i=0; i<resMessageLine.length;i++){
				if(i==0){
					resMessageLine[i]=(resMessageLine[i]+">");
				}else if (i==resMessageLine.length-1){
					resMessageLine[i]=("<"+resMessageLine[i]);
				}else {
					resMessageLine[i]=("<"+resMessageLine[i]+">");
				}
			}
		}	
		
		String[] configMessageLine = new String[resconfig.size()];
		configMessageLine = resconfig.toArray(configMessageLine);

		String datasub="";
		int startForeachLoop = 0;
		int responseCountRecord = 0;
		boolean loopConfigFlag = false;
		String startLoopTag = null;
		String loopKeyName = null;
		
		HashMap<String, Object> responseMessage =new HashMap<String,Object>();
		HashMap<String, Object> responseObjectParamHashMap = new HashMap<String, Object>();
	    
	    HashMap<String, HashMap<String, Object>> arrayListStructure = new HashMap<>();
	    
		for(int i = startForeachLoop; i < configMessageLine.length; i++) {
			configLineData=configMessageLine[i].trim();
			if(configLineData.contains("@foreach")){
				loopConfigFlag = true;
				if(configLineData.contains("(")&&configLineData.contains(")")){
					HashMap<String, Object> subArrayListStructure = new HashMap<>();
					String singleParentLoopKeyName = new String();
					if(loopKeyName!=null){
						if(arrayListStructure.containsKey(loopKeyName)){
							Boolean parentCompleteLoopFlag = (Boolean) arrayListStructure.get(loopKeyName).get("completeLoopFlag");
							if(parentCompleteLoopFlag!= null && !parentCompleteLoopFlag){
								singleParentLoopKeyName = loopKeyName.split("\\.")[loopKeyName.split("\\.").length-1];
								subArrayListStructure.put("parentLoopKeyName", (Object) loopKeyName);
							}
						}
					}
					
					startLoopTag = configMessageLine[i+1].replace("<", "").replace(">", "").trim();
					String responseTag = "";
					int respCount = responseCountRecord;
					for(int responseCount = respCount ; responseCount < resMessageLine.length; responseCount++) {
						if(startLoopTag.equals(resMessageLine[responseCount].replace("<", "").replace(">", "").trim())){

							responseCountRecord = responseCount;
							responseTag = resMessageLine[responseCountRecord].replace("<", "").replace(">", "").trim();
							break;
						} else if (loopKeyName!=null && resMessageLine[responseCount].replace("<", "").replace(">", "").trim().contains(singleParentLoopKeyName)) {
							break;
						}
					}
					
					if(startLoopTag.equals(responseTag)){
						subArrayListStructure.put("startLoopTag", (Object) startLoopTag);
						subArrayListStructure.put("startLoopLine", (Object) i);
						subArrayListStructure.put("arrayListObjectParamHashMap", (Object) new HashMap<String, Object>());
						subArrayListStructure.put("loopRespFlag", (Object) false);
						subArrayListStructure.put("responseCountRecord", (Object) responseCountRecord);
						subArrayListStructure.put("completeLoopFlag", (Object) false);
						subArrayListStructure.put("resMsgArrayList", (Object) new ArrayList<>());
						subArrayListStructure.put("emptyLoopFlap", (Object) false);
						
						loopKeyName = configLineData.substring(configLineData.indexOf("(")+1, configLineData.indexOf(")"));
						arrayListStructure.put(loopKeyName, subArrayListStructure);
						responseCountRecord = responseCountRecord+1;
						continue;
					} else {
						String emptyLoopTag = configMessageLine[i+1].replace("<", "").replace(">", "").trim();
						for(int count = i; count<configMessageLine.length;count++){
							if(configMessageLine[count].replace("</", "").replace(">", "").trim().equals(emptyLoopTag)){
								i = count+1;
								break;
							}
						}
						continue;
					}
				} else {
					AFLog.e("[Error] Config '@foreach' missing loopKeyName.");
				}
				continue;
			} else if(loopConfigFlag && configLineData.contains("@endforeach")){
				
				if(arrayListStructure.containsKey(loopKeyName)){

					HashMap<String, Object> arrayListObjectParamHashMap = (HashMap<String, Object>) arrayListStructure.get(loopKeyName).get("arrayListObjectParamHashMap");
					arrayListObjectParamHashMap = getObjectParamHashMap(arrayListObjectParamHashMap);
					ArrayList<HashMap<String, Object>> resMsgArrayList = (ArrayList<HashMap<String, Object>>) arrayListStructure.get(loopKeyName).get("resMsgArrayList");
					resMsgArrayList.add(arrayListObjectParamHashMap);
					arrayListStructure.get(loopKeyName).put("resMsgArrayList", resMsgArrayList);

		        	if(responseCountRecord<resMessageLine.length-2){

		        		String currentStartLoopTag = (String) arrayListStructure.get(loopKeyName).get("startLoopTag");
		        		int respCount = responseCountRecord;
						for(int responseCount = respCount ; responseCount < resMessageLine.length; responseCount++) {
							if(currentStartLoopTag.equals(resMessageLine[responseCount].replace("</", "").replace(">", "").trim())){
								responseCountRecord = responseCount;
								break;
							}
						}
						
		            	String currentEndTag = resMessageLine[responseCountRecord].replace("</", "").replace(">", "").trim();
		            	String nextStartTag = resMessageLine[responseCountRecord+1].replace("<", "").replace(">", "").trim();
		            	
		            	if(currentStartLoopTag.equals(currentEndTag) && currentStartLoopTag.equals(nextStartTag)){
		            		arrayListStructure.get(loopKeyName).put("arrayListObjectParamHashMap", (Object) new HashMap<String, Object>());
		            		i = ((int) arrayListStructure.get(loopKeyName).get("startLoopLine"))+1;
		            		responseCountRecord = responseCountRecord+2;
		            		continue;
		            	} else if(currentStartLoopTag.equals(currentEndTag) && !currentStartLoopTag.equals(nextStartTag)){
		            		String parentLoopKeyName = (String) arrayListStructure.get(loopKeyName).get("parentLoopKeyName");
		            		if(parentLoopKeyName != null){
		            			HashMap<String, Object> hashMap = (HashMap<String, Object>) arrayListStructure.get(parentLoopKeyName).get("arrayListObjectParamHashMap");
		            			hashMap.put(loopKeyName.replace(parentLoopKeyName+".", ""), resMsgArrayList);
								HashMap<String, Object> newHashMap = getObjectParamHashMap(hashMap);

								arrayListStructure.get(parentLoopKeyName).put("arrayListObjectParamHashMap", (Object) newHashMap);
								responseCountRecord = responseCountRecord+1;
		            		} else {
		            			responseObjectParamHashMap.put(loopKeyName, resMsgArrayList);
								responseCountRecord = responseCountRecord+1;
								
								String parentLoopKeyName2 = (String) arrayListStructure.get(loopKeyName).get("parentLoopKeyName");
								if(arrayListStructure.containsKey(parentLoopKeyName2)){
									loopKeyName = parentLoopKeyName2;
								} else {
									loopKeyName = null;
									loopConfigFlag = false;
								}
								continue;
		            		}
		            	} else {
		            		String parentLoopKeyName = (String) arrayListStructure.get(loopKeyName).get("parentLoopKeyName");

		            		if(parentLoopKeyName != null){
		            			HashMap<String, Object> hashMap = (HashMap<String, Object>) arrayListStructure.get(parentLoopKeyName).get("arrayListObjectParamHashMap");
		            			hashMap.put(loopKeyName.replace(parentLoopKeyName+".", ""), resMsgArrayList);
								HashMap<String, Object> newHashMap = getObjectParamHashMap(hashMap);

								arrayListStructure.get(parentLoopKeyName).put("arrayListObjectParamHashMap", (Object) newHashMap);
								responseCountRecord = responseCountRecord+1;							
		            		} else {
								responseCountRecord = responseCountRecord+1;
		            		}
		            	}
		        	}
					
		        	String parentLoopKeyName = (String) arrayListStructure.get(loopKeyName).get("parentLoopKeyName");
					if(arrayListStructure.containsKey(parentLoopKeyName)){
						Boolean parentCompleteLoopFlag = (Boolean) arrayListStructure.get(parentLoopKeyName).get("completeLoopFlag");

						if(parentCompleteLoopFlag!= null && !parentCompleteLoopFlag){
	            			HashMap<String, Object> hashMap = (HashMap<String, Object>) arrayListStructure.get(parentLoopKeyName).get("arrayListObjectParamHashMap");
	            			hashMap.put(loopKeyName.replace(parentLoopKeyName+".", ""), resMsgArrayList);
							HashMap<String, Object> newHashMap = getObjectParamHashMap(hashMap);

							arrayListStructure.get(parentLoopKeyName).put("arrayListObjectParamHashMap", (Object) newHashMap);
							arrayListStructure.get(loopKeyName).put("completeLoopFlag", (Object) true);
							
							arrayListStructure.remove(loopKeyName);
							loopKeyName = parentLoopKeyName;
						
							startForeachLoop = (int) arrayListStructure.get(parentLoopKeyName).get("startLoopLine");
							loopConfigFlag = true;
						}
					} else {
						responseObjectParamHashMap.put(loopKeyName, arrayListStructure.get(loopKeyName).get("arrayListObjectParamHashMap"));
						arrayListStructure.get(loopKeyName).put("completeLoopFlag", (Object) true);
						arrayListStructure.remove(loopKeyName);

						loopKeyName = null;
						loopConfigFlag = false;
					}
				} 
				continue;
			}
			
			startPos = configLineData.indexOf("{@");
			endPos = configLineData.lastIndexOf("}");
	
			if (startPos != -1) {
				
				String startData = configLineData.substring(0, startPos);
	            dataVar = configLineData.substring(startPos, endPos + 1);
	            
	            int startAtIndex = dataVar.indexOf("{@");
	            int endAtIndex = dataVar.indexOf("}");
	
	            dataVar = dataVar.substring(startAtIndex, endAtIndex);
	            String[] splitdatavar = dataVar.split(":");
	            
	            if(loopConfigFlag && i >= startForeachLoop && !StringUtils.isBlank(loopKeyName)){

	            	int test22 = responseCountRecord;
	            	boolean foundThisVar = false;
	        		for(int responseCount = test22 ; responseCount < resMessageLine.length; responseCount++) {
	        			if(StringUtils.isBlank(loopKeyName)){
	        				loopConfigFlag = false;
	        				continue;
	        			}
	        			startLoopTag = (String) arrayListStructure.get(loopKeyName).get("startLoopTag");
	        			if(startLoopTag.equals(resMessageLine[responseCount].replace("<", "").replace(">", "").trim())) {
	        				arrayListStructure.get(loopKeyName).put("loopRespFlag", (Object) true);
	        				responseCountRecord = responseCount+1;
	        				break;
	        			} else if(startLoopTag.equals(resMessageLine[responseCount].replace("</", "").replace(">", "").trim())) {
	        				if(foundThisVar) {
		        				arrayListStructure.get(loopKeyName).put("loopRespFlag", (Object) false);
		        				responseCountRecord = responseCount+1;
	        				} 
	        				break;
        				
	        			} else if (!startLoopTag.equals(resMessageLine[responseCount].replace("<", "").replace(">", "").trim()) && 
	        					!resMessageLine[responseCount].contains("</")){
	        				for(int findChildCount = i; findChildCount < configMessageLine.length && foundThisVar ; findChildCount++){
	        					if(configMessageLine[findChildCount].contains("@foreach") && configMessageLine[findChildCount+1].trim().equals(resMessageLine[responseCount].trim())){
	        						i = findChildCount-1;
	        						break;
	        					}
	        				}
	        				if (!resMessageLine[responseCount].contains("</") && startLoopTag.equals(resMessageLine[responseCount-1].replace("<", "").replace(">", "").trim())) {	        					
	        					continue;	
		        			} else {
		        				break;
		        			}
	        			} else if (resMessageLine[responseCount].contains(startLoopTag) && startLoopTag.contains("/>")) {
	        				responseCountRecord = responseCount+1;
	        				continue;
	        			} 
        			
        				configLineData = configMessageLine[i].trim();

						if (startPos != -1) {
							
							startData = configLineData.substring(0, startPos);
							String tagName = startData.replace("<", "").replace(">", "");

			                dataVar = configLineData.substring(startPos, endPos + 1);
		
			                startAtIndex = dataVar.indexOf("{@");
			                endAtIndex = dataVar.indexOf("}");
			                String[] resMessageCut = resMessageLine[responseCount].trim().replace("<","").split(">");
	    					if(resMessageCut[0].equals(tagName)) {
				                dataVar = dataVar.substring(startAtIndex, endAtIndex);
				                splitdatavar = dataVar.split(":");
				                String keyConfig = splitdatavar[1];
				                
			            		int startTagIndex = resMessageLine[responseCount].trim().indexOf(">");
			                    int endTagIndex = resMessageLine[responseCount].trim().lastIndexOf("</");
			            		
			                    String valueResponse = resMessageLine[responseCount].trim().substring(startTagIndex+1, endTagIndex);
		                    
			                    if(!("".equals(valueResponse))) {
			                    	HashMap<String, Object>arrayListObjectParamHashMap = (HashMap<String, Object>) arrayListStructure.get(loopKeyName).get("arrayListObjectParamHashMap");
			                    	arrayListObjectParamHashMap.put(keyConfig.replace(loopKeyName+".", ""), (Object) valueResponse);
			                    	arrayListStructure.get(loopKeyName).put("arrayListObjectParamHashMap", (Object) arrayListObjectParamHashMap);
			                    	arrayListStructure.get(loopKeyName).put("responseCountRecord", (Object) responseCount);
			                    	responseCountRecord = responseCount+1;
			                        break;
	                        	}
	    					} 
    					}
		            } 
	            } else {
	            	loopConfigFlag = false;
	                String startData2 = startData.replace("<", "").replace(">", "");
	                
	                int respCountRecord = responseCountRecord;
	        		for(int responseCount = respCountRecord ; responseCount < resMessageLine.length; responseCount++)  {
	        			String startTag = resMessageLine[responseCount].substring(0, resMessageLine[responseCount].indexOf(">")+1).trim();
	        			
	        			if(startData2.equals(startTag.replace("<", "").replace(">", "").trim())){
	                		int startTagIndex = resMessageLine[responseCount].trim().indexOf(">");
	                        int endTagIndex = resMessageLine[responseCount].trim().lastIndexOf("</");
	                        datasub = resMessageLine[responseCount].trim().substring(startTagIndex+1, endTagIndex); 
	                        responseCountRecord=responseCount+1;
	                        break;
	                	} else if (startData2.equals(startTag.replace("<", "").replace("/>", "").trim())){
	                        responseCountRecord=responseCount+1;
	                        break;
	                	}
	                }                
	                if(!("".equals(datasub))) {
	                	responseObjectParamHashMap.put(splitdatavar[1], datasub);
	                	responseMessage.put(splitdatavar[1], datasub);
	                }	                
	                datasub="";
	            }
			} 
		}
		
		HashMap<String, Object> objectParamHashMap = getObjectParamHashMap(responseObjectParamHashMap);
		
	    String neID = afSubIns.getSubNeId();
	    String patternID = resourceMappingCommand.getSuccessPattern();
	    AFLog.d("[NeId]"+ neID);
	    AFLog.d("[PatternId]"+ patternID);
	    HashMap<String, Object> objectMapkey = new HashMap<>();
	    if(keyInvoke.contains("-")) {
	    	objectMapkey.put(keyInvoke.substring(0, keyInvoke.indexOf("-")), objectParamHashMap);
	    }else {
	    	objectMapkey.put(keyInvoke, objectParamHashMap);
	    }
	    
	    AFLog.d("[ResponseObjectHashMap]"+ objectMapkey);
	    
	    SuccessPatternUtil successPatternUtil = new SuccessPatternUtil();	    
	    PoolTask poolTask = successPatternUtil.getTaskResult(patternID, objectParamHashMap, afSubIns);
	    AFLog.d(poolTask.toString());
	    
	    errorHandling(afSubIns, poolTask);
	    
	    afSubIns.getMappingPoolTask().put(keyInvoke, poolTask);
	    afSubIns.getMappingResponse().putAll(objectMapkey);
	    	    
	    if(poolTask.getStatus()!=null && poolTask.getStatus().equals(EResultCode.RE20000.getResultStatus())){
	    	return true;
	    }else{
	    	return false;
	    }
    }
    
    public Boolean commandline(AFSubInstance afSubIns,String rawDataVal,String rawDataInvoke) {
    	
    	String suppcode = afSubIns.getCurrentSuppcode();
		ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);
		int lastDot = rawDataInvoke.lastIndexOf(".");
		String keyInvoke = rawDataInvoke.substring(lastDot+1);
    	String patternID = resourceMappingCommand.getSuccessPattern();
    			
    	HashMap<String, Object> objectParamHashMap = new HashMap<>();
    	rawDataVal = rawDataVal.replaceAll("\r", "");
		String [] spitresne = rawDataVal.split("\r|\n");
		boolean currentResponse =false;
		boolean tablePattern =false;
		int tableRowCount = 0;
		int totalCount = 0;
		int startTableLine = 0;
		
		for(int i=0 ;i<spitresne.length;i++) {
			
			if(spitresne[i].contains("RETCODE")) {
				String retCode = spitresne[i].split("=")[1].trim().split(" ")[0];
				String retDesc = spitresne[i].substring(spitresne[i].indexOf(retCode)+retCode.length()).trim();
				objectParamHashMap.put("RETCODE",retCode);
				objectParamHashMap.put("RETDESC",retDesc);
				
				currentResponse=true;
				
			}else if(spitresne[i].contains("Total")){
				totalCount = Integer.parseInt(spitresne[i].replace("Total count =", "").trim());
				currentResponse=false;
			}else if(spitresne[i].contains("There ")){
				currentResponse=false;
			}else if(currentResponse) {
			
				if(spitresne[i].contains("=")) {
					String [] subobjectspit = spitresne[i].split("=");
					if(subobjectspit.length==2) {
						objectParamHashMap.put(subobjectspit[0].trim(), subobjectspit[1].trim());
					}
				}
				
				if(spitresne[i].trim().length()>0 && !spitresne[i].contains("=") && spitresne[i].split("\\s+").length > 1 && tableRowCount == 0){
					tablePattern = true;
					startTableLine = i;
					tableRowCount++;	
				} else if(tablePattern && spitresne[i].trim().length()>0){
					tableRowCount++;
				}
			}
		}
		
		if(tableRowCount-1 == totalCount){
			tablePattern = true;
		} else {
			tablePattern = false;
		}
		
		if(tablePattern){
			
			tableRowCount = 0;
			String[] tableHeadList = null;
			ArrayList<Integer> tableHeadIndex = new ArrayList<>();
			ArrayList<HashMap> tableHashMapList = new ArrayList<>();
			
			for(int i=startTableLine ;i<spitresne.length;i++) {
				if(spitresne[i].trim().length()>0 && tableRowCount < totalCount+1){
					if(tableRowCount==0){
						tableHeadList = spitresne[i].split("\\s+");
						for(String tableHead : tableHeadList){
							tableHeadIndex.add(spitresne[i].indexOf(tableHead));
						}
						tableRowCount++;
					} else {
						HashMap<String, String> tableData = new HashMap<>();
						for(int count = 0 ; count < tableHeadIndex.size() ; count++) {
							String value = "";
							if(count < tableHeadIndex.size()-1){
								int startIndex = tableHeadIndex.get(count);;
								int endIndex = tableHeadIndex.get(count+1)-1;
								if(endIndex > spitresne[i].length()){
									endIndex = spitresne[i].length();
								}
								value = spitresne[i].substring(startIndex, endIndex).trim();
								
							} else {
								int startIndex = tableHeadIndex.get(count);;
								int endIndex = spitresne[i].length();
								if(startIndex > spitresne[i].length()){
									startIndex = spitresne[i].length();
								}
								value = spitresne[i].substring(startIndex, endIndex).trim();
							}
							
							if(value!=null && !value.equals("")){
								tableData.put(tableHeadList[count], value);
							}
								
						}
						tableHashMapList.add(tableData);
						tableRowCount++;
					}
				}
			}
			objectParamHashMap.put("TABLE", tableHashMapList);
		}
    	
		SuccessPatternUtil successPatternUtil = new SuccessPatternUtil();
	    PoolTask poolTask = successPatternUtil.getTaskResult(patternID, objectParamHashMap,afSubIns);
	    AFLog.d(objectParamHashMap.toString());
	    AFLog.d(poolTask.toString());

	    errorHandling(afSubIns, poolTask);
	    
	    HashMap<String, Object> objectMapkey = new HashMap<>();
	    if(keyInvoke.contains("-")) {
	    	objectMapkey.put(keyInvoke.substring(0, keyInvoke.indexOf("-")), objectParamHashMap);
	    }else {
	    	objectMapkey.put(keyInvoke, objectParamHashMap);
	    }
	    afSubIns.getMappingPoolTask().put(keyInvoke, poolTask);
	    afSubIns.getMappingResponse().putAll(objectMapkey);
	    
	    if(poolTask.getStatus()!=null && poolTask.getStatus().equals(EResultCode.RE20000.getResultStatus())){
	    	return true;
	    }else{
	    	return false;
	    }
    }
    
    public Boolean commandlinebase64(AFSubInstance afSubIns,String rawDataVal,String rawDataInvoke) {
    	String suppcode = afSubIns.getCurrentSuppcode();
		ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);
		int lastDot = rawDataInvoke.lastIndexOf(".");
		String keyInvoke = rawDataInvoke.substring(lastDot+1);
    	String patternID = resourceMappingCommand.getSuccessPattern();
    	
    	byte[] respcommandline = Base64.getDecoder().decode(rawDataVal);
		String responsecommandline = new String(respcommandline);
		
		AFLog.d(responsecommandline);
		
		String responsecommand = "";
		HashMap<String, Object> objectParamHashMap= new HashMap<>();
		String[]responsecommandlinespit =responsecommandline.split("\n");
		for(int i =0; i<responsecommandlinespit.length;i++) {
			if(responsecommandlinespit[i].contains("RETN")) {
				responsecommand = responsecommandlinespit[i];
			}
		}
		
		String a = responsecommand.substring(responsecommand.indexOf(":","ACK:".length())+1);
		String [] subObject = a.split(",");
		
		for(int i =0;i<subObject.length;i++) {
			if(subObject[i].contains("=")) {
			String [] subobjectspit = subObject[i].split("=");
				if(subobjectspit.length==2) {
					objectParamHashMap.put(subobjectspit[0].trim(), subobjectspit[1].trim());
				}
			}
		}
		
		SuccessPatternUtil successPatternUtil = new SuccessPatternUtil();
	    PoolTask poolTask = successPatternUtil.getTaskResult(patternID, objectParamHashMap, afSubIns);
	    AFLog.d(poolTask.toString());
	    
	    errorHandling(afSubIns, poolTask);
	    
	    HashMap<String, Object> objectMapkey = new HashMap<>();	    
	    if(keyInvoke.contains("-")) {
	    	objectMapkey.put(keyInvoke.substring(0, keyInvoke.indexOf("-")), objectParamHashMap);
	    }else {
	    	objectMapkey.put(keyInvoke, objectParamHashMap);
	    }
	    afSubIns.getMappingPoolTask().put(keyInvoke, poolTask);
	    afSubIns.getMappingResponse().putAll(objectMapkey);

	    if(poolTask.getStatus()!=null && poolTask.getStatus().equals(EResultCode.RE20000.getResultStatus())){
	    	return true;
	    }else{
	    	return false;
	    }
    }
    
    public boolean jsonmessage(AFSubInstance afSubIns,String rawDataMessage,String rawDataInvoke) { 
    	String suppcode = afSubIns.getCurrentSuppcode();
		ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);
		String keyInvoke = rawDataInvoke.split("\\.")[5];
		
		String resMessage = rawDataMessage;
				
        Type jsonHashMapType = new TypeToken<HashMap<String, Object>>() {}.getType(); 
        Gson gson = GsonPool.getGson(); 
        HashMap<String, Object> jsonHashMap = gson.fromJson(resMessage, jsonHashMapType); 
        GsonPool.pushGson(gson);
		
		String neID = afSubIns.getSubNeId();
	    String patternID = resourceMappingCommand.getSuccessPattern();
	    AFLog.d("[NeId]"+ neID);
	    AFLog.d("[PatternId]"+ patternID);
	    HashMap<String, Object> objectMapkey = new HashMap<>();
	    if(keyInvoke.contains("-")) {
	    	objectMapkey.put(keyInvoke.substring(0, keyInvoke.indexOf("-")), jsonHashMap);
	    }else {
	    	objectMapkey.put(keyInvoke, jsonHashMap);
	    }
	    
	    AFLog.d("[ResponseObjectHashMap]"+ objectMapkey);
	    
	    SuccessPatternUtil successPatternUtil = new SuccessPatternUtil();
	    PoolTask poolTask = successPatternUtil.getTaskResult(patternID, jsonHashMap, afSubIns);
	    AFLog.d(poolTask.toString());
	    
	    errorHandling(afSubIns, poolTask);
	    
	    afSubIns.getMappingPoolTask().put(keyInvoke, poolTask);
	    afSubIns.getMappingResponse().putAll(objectMapkey);
	    
	    if(poolTask.getStatus()!=null && poolTask.getStatus().equals(EResultCode.RE20000.getResultStatus())){
	    	return true;
	    }else{
	    	return false;
	    }
    }
    
    
    public static HashMap<String, Object> getObjectParamHashMap(HashMap<String, Object> responseParam){
		HashMap<String, Object> objectParamHashMap = new HashMap<String, Object>();
		for(String fullKey : responseParam.keySet() ){
			String[] keyList = fullKey.split("\\.");
			String focusKey = keyList[0];
			String otherKey = fullKey.substring(fullKey.indexOf(".")+1, fullKey.length());
			
			if (fullKey.equals(otherKey)){
				objectParamHashMap.put(fullKey, responseParam.get(fullKey));
			} else if(objectParamHashMap.containsKey(focusKey)){
				objectParamHashMap.put(focusKey, mapParamToHashMap(otherKey, responseParam.get(fullKey), (HashMap<String, Object>) objectParamHashMap.get(focusKey)));
			} else {
				objectParamHashMap.put(focusKey, mapParamToHashMap(otherKey, responseParam.get(fullKey), new HashMap<String, Object>()));
			}	
		}
		return objectParamHashMap;
	}
	
	public static Object mapParamToHashMap(String fullKey, Object value, HashMap<String, Object> hashMap){
		String[] keyList = fullKey.split("\\.");
		String focusKey = keyList[0];
		String otherKey = fullKey.substring(fullKey.indexOf(".")+1, fullKey.length());
		
		if(keyList.length > 1){
			if(hashMap.get(focusKey) != null){
				hashMap.put(focusKey, mapParamToHashMap(otherKey, value, (HashMap<String, Object>)hashMap.get(focusKey)));
			} else {
				hashMap.put(focusKey, mapParamToHashMap(otherKey, value, new HashMap<String, Object>()));
			}	
		} else {
			hashMap.put(focusKey, value);			
		}
		return hashMap;
	}
	private static String unescape(String text) {
		StringBuilder result = new StringBuilder(text.length());
		int i = 0;
		int n = text.length();
		while (i < n) {
			char charAt = text.charAt(i);
			if (charAt != '&') {
				result.append(charAt);
				i++;
			} else if (text.startsWith("&amp;", i)) {
				result.append('&');
				i += 5;
			} else if (text.startsWith("&apos;", i)) {
				result.append('\'');
				i += 6;
			} else if (text.startsWith("&quot;", i)) {
				result.append('"');
				i += 6;
			} else if (text.startsWith("&lt;", i)) {
				result.append('<');
				i += 4;
			} else if (text.startsWith("&gt;", i)) {
				result.append('>');
				i += 4;
			} else {
				i++;
			}
		}
		return result.toString();
	}
	
	private void errorHandling(AFSubInstance afSubIns,PoolTask poolTask) {
		
		SuccessPatternUtil successPatternUtil = new SuccessPatternUtil();
		String suppcode = afSubIns.getCurrentSuppcode();
		List<ResourceErrorHandling> resourceErrorHandling = Config.getResourceErrorhandlingHashMap().get(suppcode);
		boolean errcodeisdef=false;
		boolean errdesisdef=false;
	    if(poolTask.getStatus().equals("Failed")&&!afSubIns.isErrorHandling()) {
	    	if(resourceErrorHandling!=null) {
	    		int retry = 0;
			    for(ResourceErrorHandling resourceErrorHandlingloop : resourceErrorHandling) {
			    	if(resourceErrorHandlingloop.getErrCode().equals(poolTask.getErrCode())) {
			    		if((resourceErrorHandlingloop.getErrDescription().equals(poolTask.getErrDesc()))
			    				||(resourceErrorHandlingloop.getErrDescription().equals("null")
			    						&&(poolTask.getErrDesc()==null||poolTask.getErrDesc().trim().isEmpty()))) {
			    			if(resourceErrorHandlingloop.getErrAction().equals("FC")) {
			    				AFLog.d("ErrorHandling result [FC] ...");
			    				poolTask = successPatternUtil.getTaskResult("ForceCode", poolTask);
			    				AFLog.d(poolTask.toString());
			    				errdesisdef = false;
			    				errcodeisdef= false;
			    				break;
			    			} else if(resourceErrorHandlingloop.getErrAction().equals("Retry")){
			    				AFLog.d("ErrorHandling Start retry ...");
			    				retry = Integer.parseInt(resourceErrorHandlingloop.getRetryTime());
			    				afSubIns.setMaxRetryNe(retry);
			    				if(resourceErrorHandlingloop.getRetrySleep()!=null&&!resourceErrorHandlingloop.getRetrySleep().trim().isEmpty()) {
			    					afSubIns.setTimeSleepRetry(resourceErrorHandlingloop.getRetrySleep());
			    				} else {
			    					afSubIns.setTimeSleepRetry("0");
			    				}
			    				afSubIns.setErrorHandling(true);
			    				errdesisdef = false;
			    				errcodeisdef= false;
			    				break;
			    			} else if(resourceErrorHandlingloop.getErrAction().equals("ErrHandlingSuppCode")) {
			    				AFLog.d("ErrorHandling Start ErrorHandlingSuppCode ...");
			    				afSubIns.setSuppCodeListErrorhandling(resourceErrorHandlingloop.getErrHandlingSuppCode());
			    				if(resourceErrorHandlingloop.getRetrySleep()!=null&&!resourceErrorHandlingloop.getRetrySleep().trim().isEmpty()) {
			    					afSubIns.setTimeSleepRetry(resourceErrorHandlingloop.getRetrySleep());
			    				} else {
			    					afSubIns.setTimeSleepRetry("0");
			    				}
			    				afSubIns.setSubNeIdListErrorhandling(afSubIns.getSubNeIdForErrorHandlingSuppcode().get(suppcode).get(resourceErrorHandlingloop.getSearchKey()));
			    				afSubIns.setErrorHandling(true);
			    				afSubIns.setErrorHandlingSuppcodeFlg(true);
			    				errdesisdef = false;
			    				errcodeisdef= false;
			    				break;
			    			}
			    		} else if(resourceErrorHandlingloop.getErrDescription().equals("def")){
			    			errdesisdef=true;
			    		}
			    	}else if(resourceErrorHandlingloop.getErrCode().equals("def")){
			    		errcodeisdef=true;
			    		errdesisdef = false;
			    	}
				}
			    
			    if(errdesisdef&&!errcodeisdef) {
			    	for(ResourceErrorHandling resourceErrorHandlingloop : resourceErrorHandling) {
				    	if(resourceErrorHandlingloop.getErrCode().equals(poolTask.getErrCode())) {
				    		if(resourceErrorHandlingloop.getErrDescription().equals("def")) {
				    			if(resourceErrorHandlingloop.getErrAction().equals("FC")) {
				    				AFLog.d("ErrorHandling result [FC] ...");
				    				poolTask = successPatternUtil.getTaskResult("ForceCode", poolTask);
				    				AFLog.d(poolTask.toString());
				    				errdesisdef = false;
				    				errcodeisdef= false;
				    				break;
				    			} else if(resourceErrorHandlingloop.getErrAction().equals("Retry")){
				    				AFLog.d("ErrorHandling Start retry ...");
				    				retry = Integer.parseInt(resourceErrorHandlingloop.getRetryTime());
				    				afSubIns.setMaxRetryNe(retry);
				    				if(resourceErrorHandlingloop.getRetrySleep()!=null&&!resourceErrorHandlingloop.getRetrySleep().trim().isEmpty()) {
				    					afSubIns.setTimeSleepRetry(resourceErrorHandlingloop.getRetrySleep());
				    				} else {
				    					afSubIns.setTimeSleepRetry("0");
				    				}
				    				afSubIns.setErrorHandling(true);
				    				errdesisdef = false;
				    				errcodeisdef= false;
				    				break;
				    			} else if(resourceErrorHandlingloop.getErrAction().equals("ErrHandlingSuppCode")) {
				    				AFLog.d("ErrorHandling Start ErrorHandlingSuppCode ...");
				    				afSubIns.setSuppCodeListErrorhandling(resourceErrorHandlingloop.getErrHandlingSuppCode());
				    				if(resourceErrorHandlingloop.getRetrySleep()!=null&&!resourceErrorHandlingloop.getRetrySleep().trim().isEmpty()) {
				    					afSubIns.setTimeSleepRetry(resourceErrorHandlingloop.getRetrySleep());
				    				} else {
				    					afSubIns.setTimeSleepRetry("0");
				    				}
				    				afSubIns.setSubNeIdListErrorhandling(afSubIns.getSubNeIdForErrorHandlingSuppcode().get(suppcode).get(resourceErrorHandlingloop.getSearchKey()));
				    				afSubIns.setErrorHandling(true);
				    				afSubIns.setErrorHandlingSuppcodeFlg(true);
				    				errdesisdef = false;
				    				errcodeisdef= false;
				    				break;
				    			}
				    		}
				    	}
					}
			    }
			    
			    if(errcodeisdef) {
			    	for(ResourceErrorHandling resourceErrorHandlingloop : resourceErrorHandling) {
				    	if(resourceErrorHandlingloop.getErrCode().equals("def")) {
				    		if((resourceErrorHandlingloop.getErrDescription().equals(poolTask.getErrDesc()))
				    				||(resourceErrorHandlingloop.getErrDescription().equals("null")
				    						&&(poolTask.getErrDesc()==null||poolTask.getErrDesc().trim().isEmpty()))) {
				    			if(resourceErrorHandlingloop.getErrAction().equals("FC")) {
				    				AFLog.d("ErrorHandling result [FC] ...");
				    				poolTask = successPatternUtil.getTaskResult("ForceCode", poolTask);
				    				AFLog.d(poolTask.toString());
				    				errdesisdef = false;
				    				errcodeisdef= false;
				    				break;
				    			} else if(resourceErrorHandlingloop.getErrAction().equals("Retry")){
				    				AFLog.d("ErrorHandling Start retry ...");
				    				retry = Integer.parseInt(resourceErrorHandlingloop.getRetryTime());
				    				afSubIns.setMaxRetryNe(retry);
				    				if(resourceErrorHandlingloop.getRetrySleep()!=null&&!resourceErrorHandlingloop.getRetrySleep().trim().isEmpty()) {
				    					afSubIns.setTimeSleepRetry(resourceErrorHandlingloop.getRetrySleep());
				    				} else {
				    					afSubIns.setTimeSleepRetry("0");
				    				}
				    				afSubIns.setErrorHandling(true);
				    				errdesisdef = false;
				    				errcodeisdef= false;
				    				break;
				    			} else if(resourceErrorHandlingloop.getErrAction().equals("ErrHandlingSuppCode")) {
				    				AFLog.d("ErrorHandling Start ErrorHandlingSuppCode ...");
				    				afSubIns.setSuppCodeListErrorhandling(resourceErrorHandlingloop.getErrHandlingSuppCode());
				    				if(resourceErrorHandlingloop.getRetrySleep()!=null&&!resourceErrorHandlingloop.getRetrySleep().trim().isEmpty()) {
				    					afSubIns.setTimeSleepRetry(resourceErrorHandlingloop.getRetrySleep());
				    				} else {
				    					afSubIns.setTimeSleepRetry("0");
				    				}
				    				afSubIns.setSubNeIdListErrorhandling(afSubIns.getSubNeIdForErrorHandlingSuppcode().get(suppcode).get(resourceErrorHandlingloop.getSearchKey()));
				    				afSubIns.setErrorHandling(true);
				    				afSubIns.setErrorHandlingSuppcodeFlg(true);
				    				errdesisdef = false;
				    				errcodeisdef= false;
				    				break;
				    			}
				    		} else if(resourceErrorHandlingloop.getErrDescription().equals("def")){
				    			errdesisdef=true;
				    		}
				    	}
					}
			    }
			    
			    if(errdesisdef&&errcodeisdef) {
			    	for(ResourceErrorHandling resourceErrorHandlingloop : resourceErrorHandling) {
				    	if(resourceErrorHandlingloop.getErrCode().equals("def")) {
				    		if(resourceErrorHandlingloop.getErrDescription().equals("def")) {
				    			if(resourceErrorHandlingloop.getErrAction().equals("FC")) {
				    				AFLog.d("ErrorHandling result [FC] ...");
				    				poolTask = successPatternUtil.getTaskResult("ForceCode", poolTask);
				    				AFLog.d(poolTask.toString());
				    				break;
				    			} else if(resourceErrorHandlingloop.getErrAction().equals("Retry")){
				    				AFLog.d("ErrorHandling Start retry ...");
				    				retry = Integer.parseInt(resourceErrorHandlingloop.getRetryTime());
				    				afSubIns.setMaxRetryNe(retry);
				    				if(resourceErrorHandlingloop.getRetrySleep()!=null&&!resourceErrorHandlingloop.getRetrySleep().trim().isEmpty()) {
				    					afSubIns.setTimeSleepRetry(resourceErrorHandlingloop.getRetrySleep());
				    				} else {
				    					afSubIns.setTimeSleepRetry("0");
				    				}
				    				afSubIns.setErrorHandling(true);
				    				break;
				    			} else if(resourceErrorHandlingloop.getErrAction().equals("ErrHandlingSuppCode")) {
				    				AFLog.d("ErrorHandling Start ErrorHandlingSuppCode ...");
				    				afSubIns.setSuppCodeListErrorhandling(resourceErrorHandlingloop.getErrHandlingSuppCode());
				    				if(resourceErrorHandlingloop.getRetrySleep()!=null&&!resourceErrorHandlingloop.getRetrySleep().trim().isEmpty()) {
				    					afSubIns.setTimeSleepRetry(resourceErrorHandlingloop.getRetrySleep());
				    				} else {
				    					afSubIns.setTimeSleepRetry("0");
				    				}
				    				afSubIns.setSubNeIdListErrorhandling(afSubIns.getSubNeIdForErrorHandlingSuppcode().get(suppcode).get(resourceErrorHandlingloop.getSearchKey()));
				    				afSubIns.setErrorHandling(true);
				    				afSubIns.setErrorHandlingSuppcodeFlg(true);
				    				break;
				    			}
				    		}
				    	}
					}
			    }
	    	}
	    }
	    
	    if(afSubIns.isErrorHandling()&&afSubIns.isErrorHandlingSuppcodeFlg()&&afSubIns.getSubTaskErrorHandling()==0) {
	    	afSubIns.setSubNextState(ESubState.SleepForErrorHandling.getState());
	    	if(afSubIns.getSubTaskErrorHandling()<afSubIns.getSuppCodeListErrorhandling().size()) {
	    		afSubIns.setCurrentSuppcodeErrorhandling(afSubIns.getSuppCodeListErrorhandling().get(afSubIns.getSubTaskErrorHandling()));
		    	afSubIns.setSubNextOfNextState(afSubIns.getCurrentSuppcodeErrorhandling());
		    	afSubIns.setSubNeIdErrorhandling(afSubIns.getSubNeIdListErrorhandling().get(afSubIns.getSubTaskErrorHandling()));
	    	}
	    	afSubIns.setSubCurrentState(ESubState.SleepForErrorHandling.getState());
	        afSubIns.setSubControlState(ESubState.SleepForErrorHandling.getState());
	        afSubIns.incrementSubTaskErrorHandling();
	    } else if(afSubIns.isErrorHandling()
	    		&&afSubIns.isErrorHandlingSuppcodeFlg()
	    		&&poolTask.getStatus().equals(EResultCode.RE20000.getResultStatus())
	    		&&afSubIns.getSubTaskErrorHandling()<afSubIns.getSuppCodeListErrorhandling().size()) {
	    	afSubIns.setSubNextState(ESubState.SleepForErrorHandling.getState());
	    	if(afSubIns.getSubTaskErrorHandling()<afSubIns.getSuppCodeListErrorhandling().size()) {
	    		afSubIns.setCurrentSuppcodeErrorhandling(afSubIns.getSuppCodeListErrorhandling().get(afSubIns.getSubTaskErrorHandling()));
		    	afSubIns.setSubNextOfNextState(afSubIns.getCurrentSuppcodeErrorhandling());
		    	afSubIns.setSubNeIdErrorhandling(afSubIns.getSubNeIdListErrorhandling().get(afSubIns.getSubTaskErrorHandling()));
	    	}
	    	afSubIns.setSubCurrentState(ESubState.SleepForErrorHandling.getState());
	        afSubIns.setSubControlState(ESubState.SleepForErrorHandling.getState());
	        afSubIns.incrementSubTaskErrorHandling();
	    } else if(poolTask.getStatus().equals(EResultCode.RE20000.getResultStatus())) {
	    	afSubIns.setSubCountRetryNe(0);
	    	afSubIns.setMaxRetryNe(0);
	    	afSubIns.setErrorHandling(false);
	    	afSubIns.setSubNextState(ESubState.Unknown.toString());
	    	afSubIns.setSubCurrentState(suppcode);
	        afSubIns.setSubControlState(suppcode);
	        afSubIns.setSubTaskErrorHandling(0);
	        afSubIns.setErrorHandlingSuppcodeFlg(false);
	        afSubIns.setSuppCodeListErrorhandling(new ArrayList<>());
	    } else if(afSubIns.isErrorHandling()&&afSubIns.getSubCountRetryNe() < afSubIns.getMaxRetryNe()) {
	    	afSubIns.incrementSubCountRetryNe();
	    	afSubIns.setSubNextState(ESubState.SleepForErrorHandling.getState());
	    	afSubIns.setSubNextOfNextState(suppcode);
	    	afSubIns.setSubCurrentState(ESubState.SleepForErrorHandling.getState());
	        afSubIns.setSubControlState(ESubState.SleepForErrorHandling.getState());
	        afSubIns.incrementSubTaskErrorHandling();
	    } else {
	    	afSubIns.setSubCountRetryNe(0);
	    	afSubIns.setMaxRetryNe(0);
	    	afSubIns.setErrorHandling(false);
	    	afSubIns.setSubNextState(ESubState.Unknown.toString());
	    	afSubIns.setSubCurrentState(suppcode);
	        afSubIns.setSubControlState(suppcode);
	        afSubIns.setSubTaskErrorHandling(0);
	        afSubIns.setErrorHandlingSuppcodeFlg(false);
	        afSubIns.setSuppCodeListErrorhandling(new ArrayList<>());
	    }
	}
	private void errorHandlingForEqxRet(AFSubInstance afSubIns,String ret) {
		String suppcode = afSubIns.getCurrentSuppcode();
		List<ResourceErrorHandling> resourceErrorHandling = Config.getResourceErrorhandlingHashMap().get(suppcode);
		if(resourceErrorHandling!=null) {
    		int retry = 0;
		    for(ResourceErrorHandling resourceErrorHandlingloop : resourceErrorHandling) {
		    	if(ret.equals("ret4")){
		    		if(resourceErrorHandlingloop.getErrCode().equals("eqxret4")) {
		    			if(resourceErrorHandlingloop.getErrAction().equals("Retry")){
		    				AFLog.d("ErrorHandling Start retry ...");
		    				retry = Integer.parseInt(resourceErrorHandlingloop.getRetryTime());
		    				afSubIns.setMaxRetryNe(retry);
		    				if(resourceErrorHandlingloop.getRetrySleep()!=null&&!resourceErrorHandlingloop.getRetrySleep().trim().isEmpty()) {
		    					afSubIns.setTimeSleepRetry(resourceErrorHandlingloop.getRetrySleep());
		    				}else {
		    					afSubIns.setTimeSleepRetry("0");
		    				}
		    				afSubIns.setErrorHandling(true);
		    			}
		    		}
		    	}else if(ret.equals("ret2")) {
		    		if(resourceErrorHandlingloop.getErrCode().equals("eqxret2")) {
		    			if(resourceErrorHandlingloop.getErrAction().equals("Retry")){
		    				AFLog.d("ErrorHandling Start retry ...");
		    				retry = Integer.parseInt(resourceErrorHandlingloop.getRetryTime());
		    				afSubIns.setMaxRetryNe(retry);
		    				if(resourceErrorHandlingloop.getRetrySleep()!=null&&!resourceErrorHandlingloop.getRetrySleep().trim().isEmpty()) {
		    					afSubIns.setTimeSleepRetry(resourceErrorHandlingloop.getRetrySleep());
		    				}else {
		    					afSubIns.setTimeSleepRetry("0");
		    				}
		    				afSubIns.setErrorHandling(true);
		    			}
		    		}
		    	}
			}
		    
		    if(afSubIns.isErrorHandling()&&afSubIns.getSubCountRetryNe() < afSubIns.getMaxRetryNe()) {
		    	afSubIns.incrementSubCountRetryNe();
		    	afSubIns.setSubNextState(ESubState.SleepForErrorHandling.getState());
		    	afSubIns.setSubNextOfNextState(suppcode);
		    	afSubIns.setSubCurrentState(ESubState.SleepForErrorHandling.getState());
		        afSubIns.setSubControlState(ESubState.SleepForErrorHandling.getState());
		        afSubIns.incrementSubTaskErrorHandling();
		    } else {
		    	afSubIns.setSubCountRetryNe(0);
		    	afSubIns.setMaxRetryNe(0);
		    	afSubIns.setErrorHandling(false);
		    	afSubIns.setSubNextState(ESubState.Unknown.toString());
		    	afSubIns.setSubCurrentState(suppcode);
		        afSubIns.setSubControlState(suppcode);
		        afSubIns.setSubTaskErrorHandling(0);
		        afSubIns.setErrorHandlingSuppcodeFlg(false);
		        afSubIns.setSuppCodeListErrorhandling(new ArrayList<>());
		    }
    	}
	}
}
