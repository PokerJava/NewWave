package ct.af.substate;

import com.google.gson.JsonParseException;
import ct.af.enums.EResultCode;
import ct.af.enums.ESubState;
import ct.af.instance.*;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.resourceModel.*;
import ct.af.utils.Config;
import ct.af.utils.GsonPool;
import ct.af.utils.RequestTemplateProcessor;
import ct.af.utils.Validator;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import java.util.*;
import ct.af.resourceModel.SuppCode;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

public class Do_END_ResourceItem {

	public void doBusinessLogic(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {
		/*
		 * this class for summary resourceItem result
		 *
		 */

		// process postSubInstance when resourceOrder not error
		if (!afSubIns.getSubResourceOrderHasError()) {
			HashMap<String, Object> resourceItemParam = afSubIns.getSubClientHashMapParameter();

			AFSubInstance resourceOrderIns = afInstance.getMainSubInstance(afSubIns.getSubResourceOrderIns());



			PostSubInstance postSubInstance = new PostSubInstance();
			postSubInstance.setResourceName(resourceItemParam.get("resourceName") != null ? (String) resourceItemParam.get("resourceName") : "");
			postSubInstance.setResourceItemId(resourceItemParam.get("resourceItemId") != null? (String) resourceItemParam.get("resourceItemId") : "");
			postSubInstance.setResourceItemNeid(afSubIns.getSubNeId() != null ? afSubIns.getSubNeId() : "");
			postSubInstance.setSubInstanceNo(afSubIns.getSubInstanceNo());
			postSubInstance.setResourceSearchKey(afSubIns.getSubSearchKey());
			postSubInstance.setSubInitSearchKey(afSubIns.getSubInitSearchKey());
			
			//default with 20000.
			postSubInstance.setResultCode(EResultCode.RE20000.getResultCode());
			postSubInstance.setDevelopMessage("");
			postSubInstance.setNew_errorFlag("");

			boolean isSuccess = false;
			boolean isSomeSkipped = false;
			boolean isAllSkipped = true;
			StringBuilder errmsg = new StringBuilder("");
			
			if(afSubIns.getMappingPoolTask()!=null){
				SortedSet<String> keys;
				HashMap<String,PoolTask> lastestTask = new HashMap<>();
				HashMap<String,Integer> maxRetry = new HashMap<>();
				
				//set taskDetail with data from every task
				keys = new TreeSet<>(afSubIns.getMappingPoolTask().keySet());
				for (String key : keys) {					
					AFLog.d("taskDetail of all task from instance, key: "+key);
					AFLog.d(afSubIns.getMappingPoolTask().get(key).toString());
					
					//keep lastest retry task into lastestTask HashMap
					if(key.contains("-")){
						String[] str = key.split("-");
						String k = str[0];
						Integer i = (new Validator()).isNumeric(str[1])?Integer.parseInt(str[1]):0;
						if(i>maxRetry.get(k)){
							maxRetry.replace(key, i);
							lastestTask.replace(k, afSubIns.getMappingPoolTask().get(key));							
						}						
					}else{
						maxRetry.put(key, 0);
						lastestTask.put(key, afSubIns.getMappingPoolTask().get(key));
					}
					
					//if it isn't drop, then set taskDetail
					if(!afSubIns.getDropResourceFlag() && afSubIns.getFlagResourceItemStatus().equals("N")) {
						PoolTask taskPool = afSubIns.getMappingPoolTask().get(key);

						// set resourceActivatedDate
						Param_IDLE_ResourceOrder param =  (Param_IDLE_ResourceOrder)resourceOrderIns.getSubClientParameter();

						DateTime orderDateConvert = Config.getFormatDateWithMiTz().parseDateTime(afSubIns.getSubInitTimeStampIn());
						if(param.getReTransmit().equals("0")) {
							if(key.equals("Task1")&&taskPool.getStatus().equals("Success")) {
								DateTime neComplateDate =  Config.getJourneyDateTask().parseDateTime(afSubIns.getNeCompletionDate().get(key));
								postSubInstance.setResourceActivatedDate(Config.getJourneyDateFormat().print(neComplateDate));
							} else if(key.equals("Task1")&&taskPool.getStatus().equals("Failed")){
								postSubInstance.setResourceActivatedDate(Config.getJourneyDateFormat().print(orderDateConvert));
							} else if(key.contains("Task1")&&taskPool.getStatus().equals("Success")) {
								DateTime neComplateDate =  Config.getJourneyDateTask().parseDateTime(afSubIns.getNeCompletionDate().get(key));
								postSubInstance.setResourceActivatedDate(Config.getJourneyDateFormat().print(neComplateDate));
							} else if(key.contains("Task1")&&taskPool.getStatus().equals("Failed")) {
								postSubInstance.setResourceActivatedDate(Config.getJourneyDateFormat().print(orderDateConvert));
							}
						} else if(param.getReTransmit().equals("1")){
							if(key.equals("Task1")&&taskPool.getStatus().equals("Success")) {
								if(afSubIns.getNeCompletionDate().get(key)!=null) {
									DateTime neComplateDate =  Config.getJourneyDateTask().parseDateTime(afSubIns.getNeCompletionDate().get(key));
									postSubInstance.setResourceActivatedDate(Config.getJourneyDateFormat().print(neComplateDate));
								} else {
									if(afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate")==null||afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate").equals("")) {
										postSubInstance.setResourceActivatedDate(Config.getJourneyDateFormat().print(orderDateConvert));
									} else if(afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate")!=null&&!afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate").equals("")){
										postSubInstance.setResourceActivatedDate(afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate").toString());
									}
								}
							} else if(key.contains("Task1")&&taskPool.getStatus().equals("Success")) {
								DateTime neComplateDate =  Config.getJourneyDateTask().parseDateTime(afSubIns.getNeCompletionDate().get(key));
								postSubInstance.setResourceActivatedDate(Config.getJourneyDateFormat().print(neComplateDate));
							} else if(key.contains("Task1")&&taskPool.getStatus().equals("Failed")) {
								if(afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate")==null||afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate").equals("")) {
									postSubInstance.setResourceActivatedDate(Config.getJourneyDateFormat().print(orderDateConvert));
								} else if(afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate")!=null&&!afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate").equals("")){
									postSubInstance.setResourceActivatedDate(afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate").toString());
								}
							}
						}

						if (!taskPool.getNeId().equals("")) {
							// set task detail when suppcode process to NE
							// set task detail
							String neType = "";
							String timeoutconf = "";

							TaskDetail taskDetail = new TaskDetail();
							taskDetail.setTaskId(key);
							taskDetail.setNeId(taskPool.getNeId());
//    			SuppCode sup = afSubIns.getSuppCodeList().get(Integer.parseInt((key.contains("-")?key.substring(0, key.indexOf("-")).replaceAll("\\D", ""):key.replaceAll("\\D", "")))-1);
//    			String manFlag = sup.getMandatoryFlag();

							ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(taskPool.getSuppCode());
							if (resourceMappingCommand != null) {
								neType = resourceMappingCommand.getNeType() != null ? resourceMappingCommand.getNeType() : "";
								timeoutconf = resourceMappingCommand.getTimeOut() != null ? resourceMappingCommand.getTimeOut() : "";

							}

							if (!neType.equals("")) {
								taskDetail.setNeType(neType);
								List<ResourceNeIdRouting> resourceNeIdRoutings = Config.getResourceNeIdRoutingMap().get(neType);
								for (ResourceNeIdRouting routing : resourceNeIdRoutings) {
									AFLog.d(routing.toString());
									if (routing.getNeId().equalsIgnoreCase(taskPool.getNeId())) {
										taskDetail.setPartyName(routing.getPartyName());
										taskDetail.setProductVersion(routing.getProductVersion());
									}
								}
							}

							int  timeOut = Config.getExpirationDate();

							//NeExpirationDate
							if(StringUtils.isNotBlank(afSubIns.getNeSubmissionDate().get(key))){
								DateTime neSubmissionDateConvert = Config.getJourneyDateTask().parseDateTime(afSubIns.getNeSubmissionDate().get(key));
								taskDetail.setNeExpirationDate(Config.getJourneyDateTask().print(neSubmissionDateConvert.plusSeconds(Integer.parseInt(timeoutconf))));
							}

							//ExpirationDate
							if(StringUtils.isNotBlank(afSubIns.getSubInitTimeStampIn())){
								DateTime expirationDateConvert = Config.getFormatDateWithMiTz().parseDateTime(afSubIns.getSubInitTimeStampIn());
								postSubInstance.setExpirationDate(Config.getJourneyDateFormat().print(expirationDateConvert.plusMinutes(timeOut)));
							}

							String suppCode = taskPool.getSuppCode();
							SuppCode sup = afSubIns.getSuppCodeList().get(Integer.parseInt((key.contains("-")?key.substring(0, key.indexOf("-")).replaceAll("\\D", ""):key.replaceAll("\\D", "")))-1);
							String manFlag = sup.getMandatoryFlag();

							taskDetail.setNeSubmissionDate(afSubIns.getNeSubmissionDate().get(key));
							taskDetail.setNeCompletionDate(afSubIns.getNeCompletionDate().get(key));
							taskDetail.setRequestRefInvoke(afSubIns.getRefInvoke().get(key));
							taskDetail.setResponseRefInvoke(afSubIns.getRefInvoke().get(key));
							if(suppCode != null) {
								taskDetail.setTaskType(suppCode.substring(suppCode.indexOf("_")+1,(suppCode.indexOf("_")+2)));
							}
							taskDetail.setMandatoryFlag(manFlag);
							taskDetail.setSuppCode(taskPool.getSuppCode());
							//taskDetail.setTaskErrorFlag(afSubIns.getTaskTempErrorFlag().get(key));
							taskDetail.setTaskErrorFlag(taskPool.getErrorFlag());
							taskDetail.setErrorCode(taskPool.getErrCode());
							taskDetail.setTaskStatus(taskPool.getStatus());
							taskDetail.setErrorDesc(taskPool.getErrDesc());
							taskDetail.setTaskSmessage(taskPool.getsMessage());
							taskDetail.setParallelTask("0");


							postSubInstance.getTaskDetailList().add(taskDetail);
						}
					} else if (afSubIns.getDropResourceFlag()) {
						if (StringUtils.isNotBlank(afSubIns.getDropResourceParent())) {
							postSubInstance.setDropResourceParent(afSubIns.getDropResourceParent());
						}
					} else if (afSubIns.getFlagResourceItemStatus().equals("Y")) {
						// set resourceActivatedDate for resourceItemStatus Success
						DateTime orderDateConvert = Config.getFormatDateWithMiTz().parseDateTime(afSubIns.getSubInitTimeStampIn());
						if(afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate") == null
								|| afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate").equals("")) {
							postSubInstance.setResourceActivatedDate(Config.getJourneyDateFormat().print(orderDateConvert));
						} else if(afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate")!= null
								&& !afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate").equals("")) {
							postSubInstance.setResourceActivatedDate(afSubIns.getSubClientHashMapParameter().get("resourceActivatedDate").toString());
						}
					}
				}
				
				//concat resourceItemMessage with sMessage of last Task
				keys = new TreeSet<>(lastestTask.keySet());
				for (String key : keys) {										
					isSuccess =true;
					PoolTask taskPool = lastestTask.get(key);
					AFLog.d("Summary from lastest retry, key: "+key);
					AFLog.d(taskPool.toString());
					AFLog.d("Drop resource flag: "+afSubIns.getDropResourceFlag());
					
					String manFlag = "";					
					postSubInstance.setNew_errorFlag(postSubInstance.getNew_errorFlag()+taskPool.getErrorFlag());

					if (!taskPool.getStatus().equalsIgnoreCase(EResultCode.RE20000.getResultStatus())) {

						isSuccess = false;

						if(StringUtils.isNotBlank(postSubInstance.getDevelopMessage())){
							postSubInstance.setDevelopMessage(postSubInstance.getDevelopMessage()+"; "
									+(StringUtils.isBlank(taskPool.getNeId())?"":(taskPool.getNeId()+": "))
									+taskPool.getDeveloperMsg());
						}else{
							if(StringUtils.isNotBlank(taskPool.getDeveloperMsg())) {
								postSubInstance.setDevelopMessage(
										(StringUtils.isBlank(taskPool.getNeId())?"":(taskPool.getNeId()+": "))
												+taskPool.getDeveloperMsg());
							}
						}

						if(!afSubIns.getDropResourceFlag()){
							SuppCode sup = afSubIns.getSuppCodeList().get(Integer.parseInt((key.contains("-")?key.substring(0, key.indexOf("-")).replaceAll("\\D", ""):key.replaceAll("\\D", "")))-1);
							manFlag = sup.getMandatoryFlag();

							//if not success the result of order can be only 50000
							if(manFlag.equalsIgnoreCase("M")){
								postSubInstance.setResultCode(EResultCode.RE50000.getResultCode());
							}
							
							if(!taskPool.getResultCode().equals(EResultCode.RE50023.getResultCode())){
								errmsg.append((StringUtils.isBlank(errmsg.toString())?"":"; ")
										+ taskPool.getsMessage());
								isAllSkipped = false;
							}else{
								isSomeSkipped = true;
							}
						}

					}
					else{
						isAllSkipped = false;
					}
					//AFLog.d("-----");
					AFLog.d("isSuccess: "+isSuccess);
					AFLog.d("isSomeSkipped: "+isSomeSkipped);
					AFLog.d("isAllSkipped: "+isAllSkipped);
					AFLog.d("errmsg: "+errmsg.toString());

					// TODO CHECK if this resource item have spacial handling
					// postSubInstance.getSuppCode().add(taskPool.getSuppCode());
					// postSubInstance.getTaskKeyCondition().add(taskPool.getErrCode());
					// postSubInstance.getTaskDeveloperMessage().add(resultCode.getResultDesc());			

				}
				//END Loop of filter set

				// SUMMARY RESOURCE ITEM
				if (isSuccess) {
					if (!afSubIns.getDropResourceFlag()) {
						postSubInstance.setResourceItemStatus(EResultCode.RE20000.getResultStatus());
						postSubInstance.setResourceItemErrMessage(EResultCode.RE20000.getResultStatus());
						
					} else {
						postSubInstance.setResourceItemStatus(EResultCode.RE20000.getResultStatus());
						postSubInstance.setResourceItemErrMessage(EResultCode.RE20000.getResultStatus() + " (Drop Resource)");
						postSubInstance.setDropResourceFlag(true);
					}
				} else {
					postSubInstance.setResourceItemStatus(EResultCode.RE50000.getResultStatus());
					//AFLog.d("-----");
					AFLog.d("isSuccess: "+isSuccess);
					AFLog.d("isSomeSkipped: "+isSomeSkipped);
					AFLog.d("isAllSkipped: "+isAllSkipped);
					AFLog.d("errmsg: "+errmsg.toString());
					
					if(!isAllSkipped){
						postSubInstance.setResourceItemErrMessage(errmsg.toString());						
					}
					//TODO comment this block for concat resourceItemErrMessage into resultDesc of resourceOrder.
//					else{						
//						postSubInstance.setResultCode(EResultCode.RE50023.getResultCode());
//						postSubInstance.setResourceItemStatus(EResultCode.RE50023.getResultStatus());
//					}
					
					if(isSomeSkipped){
						postSubInstance.setResourceItemErrMessage((
								StringUtils.isBlank(postSubInstance.getResourceItemErrMessage())
								?"":(postSubInstance.getResourceItemErrMessage()+"; "))+
								EResultCode.RE50023.getResultCode()+" "+EResultCode.RE50023.getResultDesc());
					}
					
				}
			}

			// Read Config then push all to HashMap postSubInstance.resourceItemAttribute
			if (!afSubIns.getSubInitCmd().equals("NONE")) {
				String template = Config.getResourceMasterHashMap().get(afSubIns.getSubInitCmd()).getResponseToClient();
				postSubInstance.setResourceIndex(Config.getResourceMasterHashMap().get(afSubIns.getSubInitCmd()).getResourceIndex());
				AFLog.d("response to client template: "+template);

				if (StringUtils.isNotBlank(template)) {

					AFLog.d("Starting processing responseToClient message...");
					String suppcode = afSubIns.getCurrentSuppcode();
					AFLog.d("suppcode : "+ suppcode);

					List<String> dataList = new ArrayList<>();
					String dataVar;
					String endData;
					String respStr = "";
					ArrayList<String> line = new ArrayList<>(Arrays.asList(template.split("\\n")));
                    ArrayList<If> ifStack = new ArrayList<>();
					LinkedHashMap<String, Foreach> foreachHashMap = new LinkedHashMap<>();
					Boolean calculatedTemp = true;

					for (int i = 0; i < line.size(); i++) {
						String dataSoap = new String(line.get(i));

						if(dataSoap.contains("@if"))
						{
							int startPos = dataSoap.indexOf('(');
							int endPos = dataSoap.lastIndexOf(')');
                            if(startPos != -1 || endPos != -1)
                            {
                                String ifVar = dataSoap.substring(startPos + 1, endPos);
                                String[] splittedIfToken = ifVar.split("[\\(\\)]");
                                if (splittedIfToken.length == 2)
                                {
                                    If thisIf = new If(RequestTemplateProcessor.processIfLogic(splittedIfToken[0], splittedIfToken[1], afInstance, afSubIns, foreachHashMap));
                                    ifStack.add(thisIf);
                                    // recalculate temp boolean
                                    calculatedTemp = true;
                                    for (If oneIf : ifStack)
                                    {
                                        calculatedTemp = calculatedTemp && oneIf.getValue();
                                    }

                                    // if calculated all If in ifStack are true then set to processed
                                    if (calculatedTemp)
                                    {
                                        thisIf.setProcessed(true);
                                    }
                                }
                                else
                                {
                                    AFLog.w("[responseToClient ERROR]: Invalid @if token (full line: " + dataSoap + ")");
                                    ifStack.add(new If(false,true));
                                    for (If oneIf : ifStack)
                                    {
                                        calculatedTemp = calculatedTemp && oneIf.getValue();
                                    }
                                }
                            }
                            else
                            {
                                AFLog.w("[responseToClient ERROR]: Invalid @if token (full line: " + dataSoap + ")");
                                ifStack.add(new If(false,true));
                                for (If oneIf : ifStack)
                                {
                                    calculatedTemp = calculatedTemp && oneIf.getValue();
                                }

                            }
                            continue;
						}
						else if(dataSoap.contains("@elseif"))
						{
							int startPos = dataSoap.indexOf('(');
							int endPos = dataSoap.lastIndexOf(')');
                            if(startPos != -1 || endPos != -1)
                            {
                                String ifVar = dataSoap.substring(startPos + 1, endPos);
                                String[] splittedIfToken = ifVar.split("[\\(\\)]");
                                if (splittedIfToken.length == 2)
                                {
                                    If lastIfInStack = ifStack.get(ifStack.size() - 1);
                                    if (!lastIfInStack.getProcessed())
                                    {
                                        lastIfInStack.setValue(RequestTemplateProcessor.processIfLogic(splittedIfToken[0], splittedIfToken[1], afInstance, afSubIns, foreachHashMap));
                                        // recalculate temp boolean
                                        calculatedTemp = true;
                                        for (If oneIf : ifStack)
                                        {
                                            calculatedTemp = calculatedTemp && oneIf.getValue();
                                        }

                                        // if calculated all If in ifStack are true then set to processed
                                        if (calculatedTemp)
                                        {
                                            lastIfInStack.setProcessed(true);
                                        }
                                    }
                                    else
                                    {
                                        lastIfInStack.setValue(false);
                                        // recalculate temp boolean
                                        calculatedTemp = true;
                                        for (If oneIf : ifStack)
                                        {
                                            calculatedTemp = calculatedTemp && oneIf.getValue();
                                        }
                                    }
                                }
                                else
                                {
                                    AFLog.w("[responseToClient ERROR]: Invalid @elseif token (full line: " + dataSoap + ")");
                                    ifStack.add(new If(false,true));
                                    for (If oneIf : ifStack)
                                    {
                                        calculatedTemp = calculatedTemp && oneIf.getValue();
                                    }
                                }
                            }
                            else
                            {
                                AFLog.w("[responseToClient ERROR]: Invalid @elseif token (full line: " + dataSoap + ")");
                                ifStack.add(new If(false,true));
                                for (If oneIf : ifStack)
                                {
                                    calculatedTemp = calculatedTemp && oneIf.getValue();
                                }
                            }
							continue;
						}
						else if(dataSoap.contains("@else"))
						{
                            If lastIfInStack = ifStack.get(ifStack.size() - 1);
                            if(!lastIfInStack.getProcessed())
                            {
                                lastIfInStack.setValue(true);
                                // recalculate temp boolean
                                calculatedTemp = true;
                                for (If oneIf:ifStack)
                                {
                                    calculatedTemp = calculatedTemp && oneIf.getValue();
                                }

                                // if calculated all If in ifStack are true then set to processed
                                if(calculatedTemp)
                                {
                                    lastIfInStack.setProcessed(true);
                                }
                            }
                            else
                            {
                                lastIfInStack.setValue(false);
                                // recalculate temp boolean
                                calculatedTemp = true;
                                for (If oneIf:ifStack)
                                {
                                    calculatedTemp = calculatedTemp && oneIf.getValue();
                                }
                            }
                            continue;
						}
						else if(dataSoap.contains("@endif"))
						{
                            ifStack.remove(ifStack.size() - 1);
                            // recalculate temp boolean
                            calculatedTemp = true;
                            for (If oneIf:ifStack)
                            {
                                calculatedTemp = calculatedTemp && oneIf.getValue();
                            }
                            continue;
						}
						else if(dataSoap.contains("@foreach"))
						{
							int startPos = dataSoap.indexOf('(');
							int endPos = dataSoap.lastIndexOf(')');
							String foreachVar = dataSoap.substring(startPos + 1, endPos);
							String[] splittedForeachToken = foreachVar.split("[\\(\\)]");
							Boolean isForeachEmpty = false;
							if(splittedForeachToken.length == 1)
							{
								String[] splittedDotForeachToken = splittedForeachToken[0].split("\\.");
								if(splittedDotForeachToken.length == 1)
								{
									AFLog.d("[@foreach ERROR]: " + splittedForeachToken[0] + " has only one level.");
									foreachHashMap.put(splittedForeachToken[0], new Foreach(splittedForeachToken[0], i, 0, true, null));
								}
								else if(splittedDotForeachToken.length == 2)
								{
									/* resolve object */
									Object resolvingObj = RequestTemplateProcessor.paramObjectSearch(splittedForeachToken[0], afInstance, afSubIns, foreachHashMap);
									ArrayList<LinkedTreeMap> tempArray = null;
									if(resolvingObj instanceof ArrayList)
									{
										tempArray = (ArrayList) resolvingObj;
										if(tempArray.isEmpty())
										{
											isForeachEmpty = true;
										}
									}
									else
									{
										AFLog.w("[responseToClient @foreach ERROR]: " + splittedDotForeachToken[0] + " is not an array.");
										isForeachEmpty = true;
									}
									foreachHashMap.put(splittedForeachToken[0], new Foreach(splittedForeachToken[0], i, 0, isForeachEmpty, tempArray));
								}
								else if(splittedDotForeachToken.length > 2)
								{
									String combinedForeachKey = splittedDotForeachToken[0];
									for (int foreachKeyIndex = 1; foreachKeyIndex < splittedDotForeachToken.length - 1; foreachKeyIndex++)
									{
										combinedForeachKey += "." + splittedDotForeachToken[foreachKeyIndex];
									}
									AFLog.d("Combined Upper Foreach Key: " + combinedForeachKey);
									if(foreachHashMap.containsKey(combinedForeachKey))
									{
										Foreach foreach = foreachHashMap.get(combinedForeachKey);
										if(foreach.isForeachEmpty())
										{
											AFLog.d("[responseToClient @foreach]: " + combinedForeachKey + " is empty so this foreach \"" + splittedForeachToken[0] + "\" is empty too.");
											foreachHashMap.put(splittedForeachToken[0], new Foreach(splittedForeachToken[0], i, 0, true, null));
										}
										else
										{
											ArrayList upperArrayList = (ArrayList) foreach.getArrayRef();
											Map<String, Object> upperObject = (Map<String, Object>) upperArrayList.get(foreach.getObjectIndex());
											if (upperObject.containsKey(splittedDotForeachToken[splittedDotForeachToken.length - 1]))
											{
												ArrayList tempArray = null;
												if (upperObject.get(splittedDotForeachToken[splittedDotForeachToken.length - 1]) instanceof ArrayList)
												{
													tempArray = (ArrayList) upperObject.get(splittedDotForeachToken[splittedDotForeachToken.length - 1]);
													if (tempArray.isEmpty())
													{
														isForeachEmpty = true;
													}
												}
												else
												{
													AFLog.w("[responseToClient @foreach ERROR]: " + splittedDotForeachToken[0] + " is not an array in an object that has index at " + foreach.getObjectIndex() + ".");
													isForeachEmpty = true;
												}
												foreachHashMap.put(splittedForeachToken[0], new Foreach(splittedForeachToken[0], i, 0, isForeachEmpty, tempArray));
											}
											else
											{
												AFLog.w("[responseToClient @foreach ERROR]: " + splittedDotForeachToken[0] + " not found in an object that has index at " + foreach.getObjectIndex() + ".");
												foreachHashMap.put(splittedForeachToken[0], new Foreach(splittedForeachToken[0], i, 0, true, null));
											}
										}
									}
									else
									{
										/* resolve object */
										Object resolvingObj = RequestTemplateProcessor.paramObjectSearch(splittedForeachToken[0], afInstance, afSubIns, foreachHashMap);
										ArrayList tempArray = null;
										if(resolvingObj != null && resolvingObj instanceof ArrayList)
										{
											tempArray = (ArrayList) resolvingObj;
											if(tempArray.isEmpty())
											{
												isForeachEmpty = true;
											}
										}
										else
										{
											AFLog.w("[responseToClient @foreach ERROR]: " + splittedForeachToken[0] + " is not an array or " + combinedForeachKey + " not found in Foreach stack.  Maybe it's in wrong hierarchy.");
											isForeachEmpty = true;
										}

										foreachHashMap.put(splittedForeachToken[0],new Foreach(splittedForeachToken[0], i, 0, isForeachEmpty, tempArray));
									}
								}
							}
							else
							{
								AFLog.w("[responseToClient @foreach ERROR]: Invalid @foreach token (full line: " + dataSoap + ")");
								foreachHashMap.put(splittedForeachToken[0], new Foreach(splittedForeachToken[0], i, 0, true, null));
							}
							continue;
						}
						else if(dataSoap.contains("@endforeach"))
						{
							if(!foreachHashMap.isEmpty())
							{
								Foreach lastForeachInStack = RequestTemplateProcessor.findLastForeachEntry(foreachHashMap);
								if(!lastForeachInStack.isForeachEmpty() && lastForeachInStack.getObjectIndex() < (lastForeachInStack.getArrayRef().size() - 1))
								{
									i = lastForeachInStack.getStartLinePosition();
									lastForeachInStack.setObjectIndex(lastForeachInStack.getObjectIndex() + 1);
									dataList.set(dataList.size() - 1, dataList.get(dataList.size() - 1) + ",");
								}
								else
								{
									foreachHashMap.remove(lastForeachInStack.getName());
								}
							}
							continue;
						}

						if((calculatedTemp && !RequestTemplateProcessor.findLastForeachEntry(foreachHashMap).isForeachEmpty()) || (calculatedTemp && foreachHashMap.isEmpty()) || (ifStack.isEmpty() && !RequestTemplateProcessor.findLastForeachEntry(foreachHashMap).isForeachEmpty()) || (ifStack.isEmpty() && foreachHashMap.isEmpty()))
						{
							int startPos = dataSoap.indexOf("{@");
							int endPos = dataSoap.lastIndexOf('}');
							if (startPos != -1)
							{
								String startData = dataSoap.substring(0, startPos);
								endData = dataSoap.substring(endPos + "}".length());
								dataVar = dataSoap.substring(startPos, endPos + 1);


								dataVar = RequestTemplateProcessor.processMagicString(dataVar, afInstance, afSubIns, foreachHashMap);

								//dataVar = (String) mapOrderTemplate.get(dataVarN);
								if ("".equals(dataVar) || dataVar == null)
								{
									if ((startData.contains("((")) && (endData.contains("))")))
									{
										dataVar = "";
										dataSoap = startData + dataVar + endData;
										// careData.replace("((", "").replace("))", "");
									} else if ((startData.contains("(")) && (endData.contains(")")) && (endData.startsWith(")\"")))
									{

										dataSoap = "";

									} else if ((startData.contains("(")) && (endData.contains(")")))
									{

										dataSoap = startData + endData;

									} else
									{
										AFLog.w("[responseToClient ERROR]: Mandatory is missing. (key: " + dataSoap + ")");
										dataSoap = startData + endData;
									}
								} else
								{
									dataSoap = startData + dataVar + endData;
								}
								AFLog.d("dataSoap : " + dataSoap);
							}


                            dataSoap = dataSoap.replace("(", "").replace(")", "").replace("&#40;","(").replace("&#41;",")");
							if(!dataSoap.equals(""))
							{
								dataList.add(dataSoap);
							}
						}
					}

					for (int i = 0; i < dataList.size(); i++) {
						if (!(dataList.get(i)).equals("")) {

							respStr = respStr.concat(dataList.get(i) + '\n');

						}
					}

					AFLog.d("OutputMessage: " + respStr);

					Gson gson = GsonPool.getGson();
					try
					{
						JsonObject attr = gson.fromJson(respStr, JsonObject.class);
						postSubInstance.setResourceItemAttribute(attr);
					}
					catch (JsonParseException ex)
					{
						AFLog.e("[Exception] error parsing responseToClient message: " + ex.getMessage());
						AFLog.e(ex);
						postSubInstance.setResourceItemAttribute(null);
					}
					GsonPool.pushGson(gson);
					// TODO change key name

				}
			}

			AFLog.d(postSubInstance.toString());
			resourceOrderIns.getPostSubInstanceMap().put(afSubIns.getSubInstanceNo(), postSubInstance);
		}

		afInstance.setMainIsLock(false);
		afInstance.decrementMainCountProcess();
		afSubIns.setSubNextState(ESubState.END.getState());
	}

	public static String getParamFromHashMapByFullKey(String fullKey, HashMap<String, Object> objectParamHashMap) {
		String value = new String();
		String[] keyList = fullKey.split("\\.");
		String focusKey = keyList[0];
		String otherKey = fullKey.substring(fullKey.indexOf(".") + 1, fullKey.length());
		if (objectParamHashMap.containsKey(focusKey)) {

//			System.out.println("-----------------------------");
//			System.out.println(focusKey);
//			System.out.println(objectParamHashMap.get(focusKey));

			if (objectParamHashMap.get(focusKey) instanceof String) {
				value = (String) objectParamHashMap.get(focusKey);
			} else if (objectParamHashMap.get(focusKey) instanceof HashMap) {
				value = getParamFromHashMapByFullKey(otherKey,
						(HashMap<String, Object>) objectParamHashMap.get(focusKey));
			} else {
				AFLog.d("[ERROR] getParamFromHashMapByFullKey Error Type '" + focusKey + "'");
			}
		} else {
			AFLog.d("[ERROR] getParamFromHashMapByFullKey not found key '" + focusKey + "'");
		}
		return value;
	}
		}
        
        



