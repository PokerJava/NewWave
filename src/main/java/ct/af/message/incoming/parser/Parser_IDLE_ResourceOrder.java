package ct.af.message.incoming.parser;

import com.google.gson.*;
import ct.af.enums.ECommand;
import ct.af.enums.EResultCode;
import ct.af.enums.EState;
import ct.af.enums.EStats;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.instance.PoolTask;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.resourceModel.DropResourceOrderType;
import ct.af.utils.*;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;
import java.util.*;

import org.apache.commons.lang3.StringUtils;


public class Parser_IDLE_ResourceOrder {
    private  HashMap<String, Object> parseParameterToHashmap(JsonObject resourceOrderJsonObject) {
        /*
         *   parse to hashmap
         */
        JsonObject requestHeaderObject = (JsonObject)resourceOrderJsonObject.get("requestHeader");
        Gson gson = GsonPool.getGson();
        HashMap<String,Object> hashmapParam = new HashMap<>();
        hashmapParam = (HashMap<String, Object>) gson.fromJson(requestHeaderObject, hashmapParam.getClass());
        ParamUtils.parseInternalLayer(hashmapParam);
        GsonPool.pushGson(gson);
        AFLog.d("hashmapParam : "+hashmapParam);
        return  hashmapParam;
    }

    private String validateAndSetResourceOrder(JsonObject resourceOrderJsonObject, AFSubInstance resourceOrderIns)
    {
        /*
        *   parse resourceOrder to param
        */
        JsonObject requestHeaderObject = (JsonObject)resourceOrderJsonObject.get("requestHeader");

        Gson gson = GsonPool.getGson();
        Param_IDLE_ResourceOrder param =  gson.fromJson(requestHeaderObject, Param_IDLE_ResourceOrder.class);
        param.trim();

        Validator validator = new Validator();

        String errorMsg = "";
        errorMsg = validator.mandatoryValidate("resourceGroupId", param.getResourceGroupId(), errorMsg);
        errorMsg = validator.mandatoryValidate("userSys", param.getUserSys(), errorMsg);
        errorMsg = validator.mandatoryValidate("reTransmit", param.getReTransmit(), errorMsg);

        if (resourceOrderJsonObject.get("resourceItemList") == null) {
            errorMsg = validator.mandatoryValidate("resourceItemList", "", errorMsg);
        } else {
            JsonElement jsonElement = resourceOrderJsonObject.get("resourceItemList");

            if (jsonElement.getAsJsonArray().size() == 0) {
                errorMsg = ErrorMessageUtil.appendWithSemicolon(errorMsg, "is empty resourceItemList.");
            } else {
                for (Object resourceItemJsonObject : jsonElement.getAsJsonArray()) {
                    param.getResourceItemList().add(resourceItemJsonObject.toString());
                }
            }
        }


        GsonPool.pushGson(gson);

        if (!param.getReTransmit().equals("") && !param.getReTransmit().equals("0") && !param.getReTransmit().equals("1")) {
            errorMsg = ErrorMessageUtil.appendWithSemicolon(errorMsg, "reTransmit must be 0 or 1.");
        }

        String validateErrorMsg = "";
        if (!param.getUserSys().equals("")) {
            validateErrorMsg = validator.patternValidate("userSys", param.getUserSys(), Config.getUserSysList(), validateErrorMsg);
            if (!validateErrorMsg.equals("")) {
                errorMsg = ErrorMessageUtil.appendWithSemicolon(errorMsg, validateErrorMsg);
            }
        }

        resourceOrderIns.setSubClientParameter(param);

        if (!errorMsg.equals("")) {
            errorMsg = "resourceOrder " + errorMsg;
        }

        AFLog.d("resourceInventory size : "+Config.getResourceInventoryHashMap().size());
        if (Config.getResourceInventoryHashMap().size() == 0) {
            if (!errorMsg.equals("")) {
                errorMsg = ErrorMessageUtil.appendWithSemicolon( "cannot do any resourceOrder since cannot query Inventory.", errorMsg);
            } else {
                errorMsg = "cannot do any resourceOrder since cannot query Inventory.";
            }
        }

        return errorMsg;
    }


    public Param_IDLE_ResourceOrder doParser(AbstractAF abstractAF, EquinoxRawData eqxRawData, AFInstance afInstance, AFSubInstance afSubIns) {
        Param_IDLE_ResourceOrder param = new Param_IDLE_ResourceOrder();

        String rawData = eqxRawData.getRawDataAttribute("val");
        String errorMsg = "";
        afSubIns.setRequestId(new AFUtils().requestIdGenerator());
        afSubIns.setReqRawData(rawData);
        afSubIns.setServiceOrderId(param.getServiceOrderId());
        afSubIns.setServiceOrderType(param.getServiceOrderType());

        EResultCode resultCode = EResultCode.RE20000;
        EResultCode internalCode = EResultCode.RE20000;
        AFLog.d("equinox state : "+abstractAF.getEquinoxProperties().getState());

        if (!abstractAF.getEquinoxProperties().getState().equals(EState.IDLE)) {
            resultCode = EResultCode.RE40301;
            internalCode = EResultCode.RE40301;

            afSubIns.setSubResultCode(resultCode.getResultCode());
            afSubIns.setSubInternalCode(internalCode.getResultCode());
            afSubIns.setSubResourceOrderValidateMessage("request is still processing.");
            afSubIns.setSubCurrentState(ESubState.IDLE_RESOURCEORDER.getState());
            afSubIns.setSubControlState(ESubState.IDLE_RESOURCEORDER.getState());
            afSubIns.setSubNextState(ESubState.Unknown.toString());

            afInstance.incrementMainCountWait();
            afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);
		} else {
			
			if(new TimeoutManagerFlag().incrementMaxActive(Config.isEnableTimeoutManagerFlag())){
				// Check current proccesing request already reach limit ?

				String url = eqxRawData.getRawDataAttribute("url");

				if (!Config.getUrlList().contains(url)) {
					AFLog.d("url : " + url);
					AFLog.d("UrlList : " + Config.getUrlList().toString());

					resultCode = EResultCode.RE40300;
					internalCode = EResultCode.RE40300;

					afSubIns.setSubResultCode(resultCode.getResultCode());
					afSubIns.setSubInternalCode(internalCode.getResultCode());
					afSubIns.setSubResourceOrderValidateMessage("unknown url.");
					afSubIns.setSubCurrentState(ESubState.IDLE_RESOURCEORDER.getState());
					afSubIns.setSubControlState(ESubState.IDLE_RESOURCEORDER.getState());
					afSubIns.setSubNextState(ESubState.Unknown.toString());

					afInstance.incrementMainCountWait();
					afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);
				} else {
					try {
						AFLog.d("***** Parse ResourceOrder Level *****");
						JsonParser jsonParser = new JsonParser();
						JsonObject resourceOrderJsonObject = jsonParser.parse(rawData).getAsJsonObject();

						String resourceOrderValidateMessage = validateAndSetResourceOrder(resourceOrderJsonObject,
								afSubIns);
						param = (Param_IDLE_ResourceOrder) afSubIns.getSubClientParameter();

						afSubIns.setServiceOrderId(param.getServiceOrderId());
						afSubIns.setServiceOrderType(param.getServiceOrderType());

						afSubIns.setSubClientHashMapParameter(parseParameterToHashmap(resourceOrderJsonObject));

						if (!resourceOrderValidateMessage.equals("")) {
							resultCode = EResultCode.RE40300;
							internalCode = EResultCode.RE40300;
						}

						afSubIns.setSubCurrentState(ESubState.IDLE_RESOURCEORDER.getState());
						afSubIns.setSubControlState(ESubState.IDLE_RESOURCEORDER.getState());
						afSubIns.setSubNextState(ESubState.Unknown.toString());
						afSubIns.setSubResourceOrderValidateMessage(resourceOrderValidateMessage);

						afSubIns.setSubResultCode(resultCode.getResultCode());
						afSubIns.setSubInternalCode(internalCode.getResultCode());
						afInstance.incrementMainCountWait();
						afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);

						LogUtils.prepareDataForCDR(afSubIns);

					} catch (Exception ex) {
						AFLog.e("[Exception] invalid JSON request format.");
						AFLog.e(ex);
						
						resultCode = EResultCode.RE40300;
						internalCode = EResultCode.RE40300;

						afSubIns.setSubResultCode(resultCode.getResultCode());
						afSubIns.setSubInternalCode(internalCode.getResultCode());
						afSubIns.setSubResourceOrderValidateMessage("invalid JSON request format.");
						afSubIns.setSubCurrentState(ESubState.IDLE_RESOURCEORDER.getState());
						afSubIns.setSubControlState(ESubState.IDLE_RESOURCEORDER.getState());
						afSubIns.setSubNextState(ESubState.Unknown.toString());

						afInstance.incrementMainCountWait();
						afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);

						return param;
					}

						if (afSubIns.getSubResultCode().equals(EResultCode.RE20000.getResultCode())) {int resourceItemNumber = 1;
	                    AFLog.d("***** Parse ResourceItem Level *****");
	                    
	                    //Select DropResourceTndex
	                    //String resourceOrderType = "Prepaid-Sim2Fly";
	                    String resourceOrderType = param.getResourceOrderType();
	                    //String resourceOrderType = param.getResourceOrderType();
	                	DropResourceOrderType resourceHLRTemplate = Config.getResourceHLRTemplateHashMap().get(resourceOrderType);
	                	boolean pendingDropResourceParentFlag = false;
	                	if (resourceHLRTemplate != null) {
	                		try{
	                			AFLog.d("========================================= [DROP RESOURCE] =========================================");
	    	            		HashMap<String,HashMap<String,String>> dropResourceHashMap = new HashMap<>();
	    	            		for (Object dropResource : resourceHLRTemplate.getDropResource()){
	    	            			Gson gson = GsonPool.getGson();
	    	            			HashMap<String,String> subDropResource = new HashMap<>();
	    	            			String dropResourceString = gson.toJson(dropResource).toString();
	    	            			subDropResource = (HashMap<String,String>) gson.fromJson(dropResourceString, subDropResource.getClass());
	    	            			dropResourceHashMap.put(subDropResource.get("resourceName"), subDropResource);
	    	            		}
	    	            		
	    	            		AFLog.d("[DROP RESOURCE] Processing DropResourceOrderType : " + resourceOrderType + " = " + dropResourceHashMap.toString());
	    	            		
	    	            		List<HashMap<String,Object>> resourceItemList = new ArrayList<>();
	    	            		for (String resourceItemData : param.getResourceItemList()) {
	    	            			Gson gson = GsonPool.getGson();
	    	            	        HashMap<String,Object> resourceItem = new HashMap<>();
	    	            	        resourceItem = (HashMap<String, Object>) gson.fromJson(resourceItemData, resourceItem.getClass());
	    	            	        
	    		            		if (resourceItem.containsKey("resourceName") && !resourceItem.get("resourceName").toString().equals("")) {
	    		                        resourceItemList.add(resourceItem);
	    		            		} 
	    	            		}
	    	            		
	    	            		List<String> dropResourceList = new ArrayList<>();
	    	            		List<String> notDropResourceList = new ArrayList<>();	
	    	            		for (String resourceItemData : param.getResourceItemList()) {
	    	            			//AFLog.d("[HLR TEMPLATE] ResourceName " + resourceItemHashMap.get("resourceName"));
	    	            			Gson gson = GsonPool.getGson();
	    	            	        HashMap<String,Object> resourceItemHashMap = new HashMap<>();
	    	            	        resourceItemHashMap = (HashMap<String, Object>) gson.fromJson(resourceItemData, resourceItemHashMap.getClass());
	    	            			String resourceName = resourceItemHashMap.get("resourceName").toString();
	    	            			
	    	            			if(dropResourceHashMap.containsKey(resourceName)){
	    	            				HashMap<String,String> subDropResourceHashMap = (HashMap<String,String>) dropResourceHashMap.get(resourceName);
	    	            				boolean isMatch = true;
	    	            				String errorMessage = "";
	    	            				for (Map.Entry subDropResource : subDropResourceHashMap.entrySet()) {
	    	            					String dropKey = (String) subDropResource.getKey();
	    	            					String dropValue = (String) subDropResource.getValue();
	    	            					if(resourceItemHashMap.containsKey(dropKey)){
	    	            						resourceItemHashMap.get(dropKey);
	    	            						Validator validator = new Validator();
	    	            						String parameterName = dropKey; 
	    	            						String parameterValue = resourceItemHashMap.get(dropKey).toString(); 
	    	            						String format = dropValue; 
	    	            						String validateError = validator.patternValidate(parameterName, parameterValue, format, errorMessage);
	    	            						if(!validateError.equals("")){
	    	            							isMatch = false;
	    	            						}
	    	            					} else {
	    	            						isMatch = false;
	    	            					}
	    	            				}
	    	            				if(isMatch){
	            							AFLog.d("[DROP RESOURCE] MATCH resourceName : " + resourceName);
	            							dropResourceHashMap.remove(resourceName);
	            							GsonPool.getGson();
	            							dropResourceList.add(resourceItemData);
	            							GsonPool.pushGson(gson);
	            						} else {
	            							AFLog.d("[DROP RESOURCE] NOT MATCH resourceName : " + resourceName);
	            							GsonPool.getGson();
	            							notDropResourceList.add(resourceItemData);
	            							GsonPool.pushGson(gson);
	            						}
	    	            			}  else {
	        							AFLog.d("[DROP RESOURCE] NOT IN DROP resourceName : " + resourceName);
	        							GsonPool.getGson();
	        							notDropResourceList.add(resourceItemData);
	        							GsonPool.pushGson(gson);
	        						}
	    	                    } 
	    	            		
	    	            		 
	    	            		if (dropResourceHashMap.size() == 0) {
	    	            			AFLog.d("[DROP RESOURCE SUCESS] DropResourceOrderType SUCCESS : " + dropResourceList);
	    	            			AFLog.d("[DROP RESOURCE SUCESS] Continue Process ResourceItem : " + notDropResourceList);
	    	            			
	    	            			if (dropResourceList.size() > 0) {
	    	                            for (String resourceItemData : dropResourceList) {
	    	
	    	                                AFLog.d("====================================== Start DropResourceItem  " + resourceItemNumber + " ===========================================");
	    	
	    	                                AFSubInstance afSubInsChild = new AFSubInstance();
	    	                                String command = ECommand.RESOURCEITEM.getCommand();
	    	                                
	    	                                Gson gson = GsonPool.getGson();
	    	                                HashMap<String,Object> resourceItemParam = new HashMap<>();
	    	                                resourceItemParam = (HashMap<String, Object>) gson.fromJson(resourceItemData, resourceItemParam.getClass());
	    	                                String resourceName = resourceItemParam.get("resourceName").toString();
	    	                                
	    	                                
	    	                                afSubInsChild.setSubInstanceNo(new AFUtils().subInsNoGenerator(afInstance, command.toString()));
	    	                                afSubInsChild.setSubInitTimeStampIn(afInstance.getMainTimeStampIncoming());
	    	                                afSubInsChild.setSubInitInvoke(afSubIns.getSubInitInvoke());
	    	                                afSubInsChild.setSubResourceOrderIns(afSubIns.getSubInstanceNo());
	    	                                
	    	                                //afSubInsChild.setSubInitCmd(resourceIndex);
	    	    	            			afSubInsChild.setSubResourceName(resourceName);
	    	                                afSubInsChild.setSubClientHashMapParameter(resourceItemParam);
	    	                                afSubInsChild.setDropResourceFlag(true);
	    	                                
	    	                                if (StringUtils.isNotBlank(resourceHLRTemplate.getResourceParent())) {
	    	                                	afSubInsChild.setDropResourceParent(resourceHLRTemplate.getResourceParent());
		    	                                pendingDropResourceParentFlag = true;
	    	        						}
	    	                                
	    	                                afSubIns.addSubResourceItemNoArray(afSubInsChild.getSubInstanceNo());
	
	                                        afSubInsChild.setSubInternalCode(EResultCode.RE20000.getResultCode());
	                                        afSubInsChild.setSubResultCode(EResultCode.RE20000.getResultCode());
	
	    	                                AFLog.d("====================================== End DropResourceItem " + resourceItemNumber + " ===========================================");
	    	
	    	                                resourceItemNumber++;
	    	                                
	    	                                //************************
	    	                                SuccessPatternUtil successPatternUtil = new SuccessPatternUtil();
	    	                        	    PoolTask poolTask = successPatternUtil.getTaskResult("DROP_RESOURCE", null,afSubInsChild);
	    	                        	    AFLog.d(poolTask.toString());
	    	                        	    	    
	    	                        	    HashMap<String, Object> objectMapkey = new HashMap<>();	    
	    	                        	    objectMapkey.put(afSubInsChild.getSubInstanceNo(), null);
	    	                        	    afSubInsChild.getMappingPoolTask().put(afSubInsChild.getSubInstanceNo(), poolTask);
	    	                        	    afSubInsChild.getMappingResponse().putAll(objectMapkey);
	    	                        	    afSubInsChild.setNew_ErrorFlag("1");
	    	                                //************************
	    	                                afSubInsChild.setSubCurrentState(ESubState.IDLE_RESOURCEITEM.getState());
	    	                                afSubInsChild.setSubControlState(ESubState.IDLE_RESOURCEITEM.getState());
	    	                                afSubInsChild.setSubNextState(ESubState.Unknown.toString());
	    	                                
	    	                                afInstance.incrementMainCountWait();
	    	                                afInstance.putMainSubInstance(afSubInsChild.getSubInstanceNo(), afSubInsChild);   
	    	                            }
	                                }
	    	            			
	                                if (notDropResourceList.size() > 0) {
	
	    	                            for (String resourceItemData : notDropResourceList) {
	    	
	    	                                AFLog.d("====================================== Start ResourceItem  " + resourceItemNumber + " ===========================================");
	    	
	    	                                AFSubInstance afSubInsChild = new AFSubInstance();
	    	                                String command = ECommand.RESOURCEITEM.getCommand();
	    	
	    	                                afSubInsChild.setSubInstanceNo(new AFUtils().subInsNoGenerator(afInstance, command.toString()));
	    	                                afSubInsChild.setSubInitTimeStampIn(afInstance.getMainTimeStampIncoming());
	    	                                afSubInsChild.setSubInitInvoke(afSubIns.getSubInitInvoke());
	    	                                afSubInsChild.setSubResourceOrderIns(afSubIns.getSubInstanceNo());
	    	                                afSubIns.addSubResourceItemNoArray(afSubInsChild.getSubInstanceNo());
	    	                                
	    	                                errorMsg = new Parser_IDLE_ResourceItem().doParser(resourceItemData, afSubIns, afSubInsChild, resourceItemNumber);
	    	                                
	    	                                if (errorMsg.equals("")) {
	    	                                    afSubInsChild.setSubInternalCode(EResultCode.RE20000.getResultCode());
	    	                                    afSubInsChild.setSubResultCode(EResultCode.RE20000.getResultCode());
	    	                                    
	    	                                    if (StringUtils.isNotBlank(resourceHLRTemplate.getResourceParent())) {
	    	                                    	String[] dropResourceParentList = resourceHLRTemplate.getResourceParent().split(",");
	    	                    					for ( String dropResourceParent : dropResourceParentList){
	    	                    						if (afSubInsChild.getSubInitCmd().equals(dropResourceParent)){
	    	                    							AFLog.d("[DROP RESOURCE] ResourceOrderType "+ resourceOrderType +" found parent ( "+ afSubInsChild.getSubInitCmd()+" ).");
	    	                    							pendingDropResourceParentFlag = false;
	    	                    						}
	    	                    					}
		    	        						}
	    	                                } else {
	    	
	    	                                    if (afSubIns.getSubResourceOrderValidateMessage().equals("")) {
	    	                                        afSubIns.setSubResourceOrderValidateMessage(errorMsg);
	    	                                    } else {
	    	                                        afSubIns.setSubResourceOrderValidateMessage(afSubIns.getSubResourceOrderValidateMessage() + "; " + errorMsg);
	    	                                    }
	    	
	    	                                    afSubInsChild.setSubInternalCode(EResultCode.RE40300.getResultCode());
	    	                                    afSubInsChild.setSubResultCode(EResultCode.RE40300.getResultCode());
	    	                                    afSubIns.setSubResultCode(EResultCode.RE40300.getResultCode());
	    	                                    afSubIns.setSubInternalCode(EResultCode.RE40300.getResultCode());
	    	                                }
	    	                                AFLog.d("====================================== End ResourceItem " + resourceItemNumber + " ===========================================");
	    	                                resourceItemNumber++;
	    	                                afSubInsChild.setSubCurrentState(ESubState.IDLE_RESOURCEITEM.getState());
	    	                                afSubInsChild.setSubControlState(ESubState.IDLE_RESOURCEITEM.getState());
	    	                                afSubInsChild.setSubNextState(ESubState.Unknown.toString());
	    	
	    	                                afInstance.incrementMainCountWait();
	    	                                afInstance.putMainSubInstance(afSubInsChild.getSubInstanceNo(), afSubInsChild);
	    	                            }
	                                } else if (notDropResourceList.size() == 0) {
	                                	afSubIns.setDropAllResourceFlag(true);
	                                }
	
	    	            		}
	    	            		
	    	            		
	    	            		if(dropResourceHashMap.size() != 0 || pendingDropResourceParentFlag){
	    	            			if (dropResourceHashMap.size() != 0) {
	    	            				AFLog.d("[DROP RESOURCE ERROR] resourceOrderType " + resourceOrderType + " dropResource " + dropResourceHashMap.keySet() + " is invalid in DropResourceOrderType.json.");
	    	            				errorMsg = "resourceOrderType " + resourceOrderType + " dropResource " + dropResourceHashMap.keySet() + " is invalid in DropResourceOrderType.json.";
	    	            			} else {
	    	            				AFLog.d("[DROP RESOURCE ERROR] resourceOrderType "+ resourceOrderType +" is required parent resourceIndex "+ resourceHLRTemplate.getResourceParent()+'.');
		                                errorMsg = "resourceOrderType "+ resourceOrderType +" is required parent resourceIndex "+ resourceHLRTemplate.getResourceParent()+".";
//		                                System.out.println("errorMsg "+errorMsg);
	    	            			}
	    	            				    	            			AFSubInstance afSubInsChild = new AFSubInstance();
	                                String command = ECommand.RESOURCEITEM.getCommand();
	
	                                afSubInsChild.setSubInstanceNo(new AFUtils().subInsNoGenerator(afInstance, command.toString()));
	                                afSubInsChild.setSubInitTimeStampIn(afInstance.getMainTimeStampIncoming());
	                                afSubInsChild.setSubInitInvoke(afSubIns.getSubInitInvoke());
	                                afSubInsChild.setSubResourceOrderIns(afSubIns.getSubInstanceNo());
	                                afSubIns.addSubResourceItemNoArray(afSubInsChild.getSubInstanceNo());
	                                

	    	            		    	
	                                if (afSubIns.getSubResourceOrderValidateMessage().equals("")) {
	                                    afSubIns.setSubResourceOrderValidateMessage(errorMsg);
	                                } else {
	                                    afSubIns.setSubResourceOrderValidateMessage(afSubIns.getSubResourceOrderValidateMessage() + "; " + errorMsg);
	                                }
	
	                                afSubInsChild.setSubInternalCode(EResultCode.RE40300.getResultCode());
	                                afSubInsChild.setSubResultCode(EResultCode.RE40300.getResultCode());
	                                afSubIns.setSubResultCode(EResultCode.RE40300.getResultCode());
	                                afSubIns.setSubInternalCode(EResultCode.RE40300.getResultCode());
	                                
	                                afSubInsChild.setSubCurrentState(ESubState.IDLE_RESOURCEITEM.getState());
	                                afSubInsChild.setSubControlState(ESubState.IDLE_RESOURCEITEM.getState());
	                                afSubInsChild.setSubNextState(ESubState.Unknown.toString());
	
	                                afInstance.incrementMainCountWait();
	                                afInstance.putMainSubInstance(afSubInsChild.getSubInstanceNo(), afSubInsChild);
	    	            		}
	                		} catch (Exception e1) {
	                			AFLog.e("[Exception] HLR TEMPLATE error : " + e1.getMessage()+".");
	                			AFLog.e(e1);
	                			
	                			resultCode = EResultCode.RE50000;
	    						internalCode = EResultCode.RE50000;

	    						afSubIns.setSubResultCode(resultCode.getResultCode());
	    						afSubIns.setSubInternalCode(internalCode.getResultCode());
	    						afSubIns.setSubResourceOrderValidateMessage("Drop Resource Error.");
	    						afSubIns.setSubCurrentState(ESubState.IDLE_RESOURCEORDER.getState());
	    						afSubIns.setSubControlState(ESubState.IDLE_RESOURCEORDER.getState());
	    						afSubIns.setSubNextState(ESubState.Unknown.toString());

	    						afInstance.incrementMainCountWait();
	    						afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);

	    						return param;
	                		}
	                		
	                		
	                	} else {
	                		
	                		for (String resourceItemData : param.getResourceItemList()) {
	
	                            AFLog.d("====================================== Start ResourceItem  " + resourceItemNumber + " ===========================================");
	
	                            AFSubInstance afSubInsChild = new AFSubInstance();
	                            String command = ECommand.RESOURCEITEM.getCommand();
	
	                            afSubInsChild.setSubInstanceNo(new AFUtils().subInsNoGenerator(afInstance, command.toString()));
	                            afSubInsChild.setSubInitTimeStampIn(afInstance.getMainTimeStampIncoming());
	                            afSubInsChild.setSubInitInvoke(afSubIns.getSubInitInvoke());
	                            afSubInsChild.setSubResourceOrderIns(afSubIns.getSubInstanceNo());
	                            afSubIns.addSubResourceItemNoArray(afSubInsChild.getSubInstanceNo());
	
	                            errorMsg = new Parser_IDLE_ResourceItem().doParser(resourceItemData, afSubIns, afSubInsChild, resourceItemNumber);
	
	                            if (errorMsg.equals("")) {
	                                afSubInsChild.setSubInternalCode(EResultCode.RE20000.getResultCode());
	                                afSubInsChild.setSubResultCode(EResultCode.RE20000.getResultCode());
	                            } else {
	
	                                if (afSubIns.getSubResourceOrderValidateMessage().equals("")) {
	                                    afSubIns.setSubResourceOrderValidateMessage(errorMsg);
	                                } else {
	                                    afSubIns.setSubResourceOrderValidateMessage(afSubIns.getSubResourceOrderValidateMessage() + "; " + errorMsg);
	                                }
	
	                                afSubInsChild.setSubInternalCode(EResultCode.RE40300.getResultCode());
	                                afSubInsChild.setSubResultCode(EResultCode.RE40300.getResultCode());
	                                afSubIns.setSubResultCode(EResultCode.RE40300.getResultCode());
	                                afSubIns.setSubInternalCode(EResultCode.RE40300.getResultCode());
	                            }
	                            AFLog.d("====================================== End ResourceItem " + resourceItemNumber + " ===========================================");
	
	                            resourceItemNumber++;
	                            afSubInsChild.setSubCurrentState(ESubState.IDLE_RESOURCEITEM.getState());
	                            afSubInsChild.setSubControlState(ESubState.IDLE_RESOURCEITEM.getState());
	                            afSubInsChild.setSubNextState(ESubState.Unknown.toString());
	
	                            afInstance.incrementMainCountWait();
	                            afInstance.putMainSubInstance(afSubInsChild.getSubInstanceNo(), afSubInsChild);
	                        }
	                	}
	                }
				}
			} else {
			   
	        	afSubIns.setSubResultCode(EResultCode.RE50020.getResultCode());
	        	afSubIns.setSubResourceOrderValidateMessage(EResultCode.RE50020.getResultDesc());
				afSubIns.setSubInternalCode(EResultCode.RE50020.getResultCode());
				afSubIns.setSubCurrentState(ESubState.IDLE_RESOURCEORDER.getState());
				afSubIns.setSubControlState(ESubState.IDLE_RESOURCEORDER.getState());
				afSubIns.setSubNextState(ESubState.Unknown.toString());
				afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);
				StatUtils.incrementStats(abstractAF, EStats.APP_RECV_MAXACTIVE_REQUEST.getStatName());
			}
		}

        return param;
    }
}
