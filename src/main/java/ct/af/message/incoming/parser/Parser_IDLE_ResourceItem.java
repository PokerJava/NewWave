package ct.af.message.incoming.parser;

import com.google.gson.Gson;
import ct.af.instance.*;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.resourceModel.ResourceMaster;
import ct.af.resourceModel.ResourceNeIdRouting;
import ct.af.resourceModel.ResourceRule;
import ct.af.resourceModel.ResourceRuleMaster;
import ct.af.resourceModel.SuppCode;
import ct.af.utils.*;
import ct.af.utils.Validator;
import ec02.af.utils.AFLog;
import java.util.*;
import org.joda.time.DateTime;

public class Parser_IDLE_ResourceItem {
    private boolean checkResourceItemNeedToProcess(AFSubInstance resourceOrderIns, AFSubInstance resourceItemIns) {
        String reTransmit = ((Param_IDLE_ResourceOrder)resourceOrderIns.getSubClientParameter()).getReTransmit();
        HashMap<String, Object> resourceItemParam = resourceItemIns.getSubClientHashMapParameter();
        String resourceIndex = resourceItemIns.getSubInitCmd();
        if (reTransmit.equals("1")) {
            // check resourceItem need to process from errorFlag
            List<SuppCode> suppCodeList = Config.getResourceMasterHashMap().get(resourceIndex).getSuppCodesList();
            String errorFlag = resourceItemParam.get("errorFlag").toString();
            if ((errorFlag.length() > suppCodeList.size() && !errorFlag.substring(0,suppCodeList.size()).contains("0"))
                    || (errorFlag.length() == suppCodeList.size() && !errorFlag.contains("0"))) {
                return false;
            }
        }
        return true;
    }


    public String doParser(String resourceItemData, AFSubInstance resourceOrderIns, AFSubInstance afSubInsChild, int resourceItemNumber) {
        /*
         *  Parse ResourceItem to HashMap
         */
        Gson gson = GsonPool.getGson();
        HashMap<String,Object> param = new HashMap<>();
        param = (HashMap<String, Object>) gson.fromJson(resourceItemData, param.getClass());
        ParamUtils.parseInternalLayer(param);
        GsonPool.pushGson(gson);
//        showMap(param);
        AFLog.d("resourceItem param : "+param.toString()+"\n");

        Param_IDLE_ResourceOrder param_IDLE_ResourceOrder = (Param_IDLE_ResourceOrder)resourceOrderIns.getSubClientParameter();

        String errorMsg = "";

        if (param.containsKey("resourceName") && !param.get("resourceName").toString().equals("")) {
            String resourceName = param.get("resourceName").toString();

            // check resource name and status from resourceInventory
            AFLog.d("[resourceName] : " + resourceName);
            if (!Config.getResourceInventoryHashMap().containsKey(resourceName)) {
                errorMsg = "is not found in SDF_GETRESOURCEINVENTORY.";
            } else {
                // check resource status
                AFLog.d("[resourceState] : " + Config.getResourceInventoryHashMap().get(resourceName).getResourceState());
                if (!Config.getResourceInventoryHashMap().get(resourceName).getResourceState().equals("Active")) {
                    errorMsg = "neState must be Active in SDF_GETRESOURCEINVENTORY.";
                } else {
                    // check resourceName from resourceProperty
                    if (!Config.getResourcePropertyHashMap().containsKey(resourceName)) {
                        errorMsg = "is not found in ResourceProperty.json.";
                    } else {
                        String preNeRouting = Config.getResourcePropertyHashMap().get(resourceName).getPreNeRouting();
                        String identityType = Config.getResourceInventoryHashMap().get(resourceName).getIdentityType() != null ? Config.getResourceInventoryHashMap().get(resourceName).getIdentityType().trim() : "";
//                        AFLog.d("---------------------------------------------------------------------");
                        AFLog.d("preNeRouting : "+preNeRouting);
                        AFLog.d("identityType : "+identityType);
//                        AFLog.d("---------------------------------------------------------------------");

                        if (!preNeRouting.equals("N") && identityType.equals("")) {
                            // resourceInvent must have identityType when resourceName need to preNeRouting
                            errorMsg = "identityType must not be null or empty in SDF_GETRESOURCEINVENTORY.";
                        } else if (preNeRouting.equals("HLR") && !identityType.equals("imsi")) {
                            // case identityType for HLR is not imsi
                            errorMsg = "preNeRouting = HLR in ResourceProperty.json must has identityType = imsi in SDF_GETRESOURCEINVENTORY.";
                        } else if (!preNeRouting.equals("N") && !param.containsKey(identityType)) {
                            // case cannot found identityType from client
                            errorMsg = "required " + identityType + " parameter for preNeRouting.";
                        } else {
                        	/*
                        	 *  check resourceRuleList in resourcePropoty
                        	 */
                        	List<String> resourceRuleList = Config.getResourcePropertyHashMap().get(resourceName).getResourceRuleList();
                        	errorMsg = validateRule(resourceRuleList,resourceName,param, resourceOrderIns,false,"");
                        	if(errorMsg.equals("")){
                        		 // check searchKey in resourceProperty
                                List<String> searchKeyList = Config.getResourcePropertyHashMap().get(resourceName).getSearchKey();
                                if (searchKeyList.size() <= 0) {
                                    errorMsg = "search key is not found in ResourceProperty.json.";
                                } else {
                                    String[] searchKeyArr = new String[searchKeyList.size()];

                                    AFLog.d("========================================= [searchKey] =========================================");
                                    int searchKeyIndex = 0;
                                    for (String searchKey : searchKeyList) {
                                        AFLog.d(searchKey);

                                        // split source$parameterName
                                        String source = searchKey.substring(0,searchKey.indexOf("$"));
                                        String parameterName = searchKey.substring(searchKey.indexOf("$")+1);

                                        if(source.equals("resourceOrderItem")) {
                                            if (param.containsKey(parameterName)) {
                                                searchKeyArr[searchKeyIndex] = param.get(parameterName).toString();
                                            } else {
                                                searchKeyArr[searchKeyIndex] = "";
                                            }
                                        } else if (source.equals("resourceOrder")) {
                                            if (parameterName.equals("resourceName")) {
                                                searchKeyArr[searchKeyIndex] = resourceName;
                                            } else {
                                                HashMap<String, Object> resourceOrderHashmap = resourceOrderIns.getSubClientHashMapParameter();
                                                if (resourceOrderHashmap.containsKey(parameterName)) {
                                                    searchKeyArr[searchKeyIndex] = resourceOrderHashmap.get(parameterName).toString();
                                                } else {
                                                    searchKeyArr[searchKeyIndex] = "";
                                                }
                                            }
                                        } else if (source.equals("neidRouting")) {
                                            String neType = Config.getResourcePropertyHashMap().get(resourceName).getPreNeRouting();
                                            if (!Config.getResourceNeIdRoutingMap().containsKey(neType)) {
                                                searchKeyArr[searchKeyIndex] = "";
                                                errorMsg = "neType = " + neType + " in ResourceProperty is not found in ResourceNeIdRouting.json.";
                                            } else {
                                                String paramValue = param.containsKey(Config.getResourceInventoryHashMap().get(resourceName).getIdentityType()) ? param.get(Config.getResourceInventoryHashMap().get(resourceName).getIdentityType()).toString() : "";
                                                ResourceNeIdRouting neIdRouting = null;
                                                if(paramValue==null || paramValue.equals("")){
                                                    searchKeyArr[searchKeyIndex] = "";
                                                    errorMsg = "neType = " + neType + " is not found identity param : "+Config.getResourceInventoryHashMap().get(resourceName).getIdentityType()+".";
                                                } else if (!(new Validator().isNumeric(paramValue))){
                                                    errorMsg = "identity param : "+Config.getResourceInventoryHashMap().get(resourceName).getIdentityType()+" is not a numeric.";
                                                } else{
                                                    neIdRouting = NETypeMapper.search(neType, Long.parseLong(paramValue));
                                                    if (!neIdRouting.getPartyName().equals("Error")) {
                                                        searchKeyArr[searchKeyIndex] = neIdRouting.getPartyName();
                                                    } else {
                                                        searchKeyArr[searchKeyIndex] = "";
                                                        errorMsg = "neType = " + neType + " is not found identity range in ResourceNeIdRouting.json.";
                                                    }
                                                }
                                            }
                                        } else {
                                            searchKeyArr[searchKeyIndex] = "";
                                        }
                                        searchKeyIndex++;
                                    }

//                                    AFLog.d("=============================================================================================");
                                    AFLog.d("======================================== [searchKeyValue] ========================================");

                                    String outputSearchKey = "";
                                    for (int index = 0; index < searchKeyArr.length; index++) {
                                        AFLog.d(searchKeyArr[index]);

                                        outputSearchKey += searchKeyArr[index];
                                        if (index < searchKeyArr.length - 1) {
                                            outputSearchKey += ",";
                                        }
                                    }
                                    afSubInsChild.setSubInitSearchKey(outputSearchKey);

                                    if (errorMsg.equals("")) {
                                        String resourceIndex = ResourceSearchKeyMapper.search(searchKeyArr,afSubInsChild);
                                        if (resourceIndex == null) {
                                            errorMsg = "searchKey(" + outputSearchKey + ") is not found in ResourceSearchKey.json.";
                                        } else {
                                            AFLog.d("====================================== [resourceIndex] ======================================");
                                            AFLog.d(resourceIndex);
//                                            AFLog.d("=============================================================================================");


                                            if (!Config.getResourceMasterHashMap().containsKey(resourceIndex)) {
                                                errorMsg = resourceIndex + " is not found in ResourceMaster.json.";
                                            } else {
                                                if (param_IDLE_ResourceOrder.getReTransmit().equals("1")
                                                        && param.containsKey("resourceItemStatus")
                                                        && param.get("resourceItemStatus").equals("Success")) {
                                                    // resourceItemStatus Success don't need to check mandatory and optional
                                                    afSubInsChild.setFlagResourceItemStatus("Y");
                                                    AFLog.d("resourceItemStatus == Success");
                                                    afSubInsChild.setSubInitCmd(resourceIndex);
                                                    afSubInsChild.setSubResourceName(resourceName);
                                                    afSubInsChild.setSubClientHashMapParameter(param);
                                                } else {
                                                    afSubInsChild.setSubInitCmd(resourceIndex);
                                                    ResourceMaster resourceMaster = Config.getResourceMasterHashMap().get(resourceIndex);
                                                    List<String> mandatoryList = resourceMaster.getMandatory();
                                                    List<String> validateList = resourceMaster.getValidate();
                                                    Validator validator = new Validator();

                                                    /*
                                                     *   check mandatory
                                                     */
                                                    String mandatoryError = "";
                                                    for (String mandatory : mandatoryList) {
                                                        Object objectValue = param.get(mandatory);
                                                        mandatoryError = validator.mandatoryValidate(mandatory, (String) objectValue, mandatoryError);
                                                    }


                                                    if(param_IDLE_ResourceOrder.getReTransmit().equals("1") && afSubInsChild.getFlagResourceItemStatus().equals("N")) {
                                                        mandatoryError = validator.mandatoryValidate("errorFlag", (String) param.get("errorFlag"), mandatoryError);

                                                        HashMap<String, Object> specialErrHandlingValue = (HashMap<String, Object>) param.get("specialErrHandling");

                                                        if (!param.containsKey("specialErrHandling")) {
                                                            mandatoryError = validator.mandatoryValidate("specialErrHandling", "",mandatoryError);
                                                        } else {
                                                            if(!specialErrHandlingValue.containsKey("suppCode")) {
                                                                mandatoryError = validator.mandatoryValidate("suppCode", "",mandatoryError);
                                                            }
                                                            if(!specialErrHandlingValue.containsKey("taskKeyCondition")) {
                                                                mandatoryError = validator.mandatoryValidate("taskKeyCondition", "",mandatoryError);
                                                            }
                                                            if(!specialErrHandlingValue.containsKey("taskDeveloperMessage")) {
                                                                mandatoryError = validator.mandatoryValidate("taskDeveloperMessage","", mandatoryError);
                                                            }
                                                        }
                                                    }

                                                    errorMsg = ErrorMessageUtil.appendCommaInsteadFullStop(errorMsg, mandatoryError);


                                                    /*
                                                     *   validate pattern
                                                     */
                                                    String validateError = "";
                                                    for (String validate : validateList) {
                                                        String validateParameterName = validate.split(":")[0];
                                                        String validatePattern = validate.split(":")[1];
                                                        Object parameterValue = param.get(validateParameterName);

                                                        if (parameterValue != null && !parameterValue.equals("")) {
                                                            // don't check when missing or empty
                                                            validateError = validator.patternValidate(validateParameterName, parameterValue.toString(), validatePattern, validateError);
                                                        }
                                                    }

                                                    errorMsg = ErrorMessageUtil.appendCommaInsteadFullStop(errorMsg, validateError);


                                                    /*
                                                     *   validate errorFlag
                                                     */
                                                    String errorFlagValidateMessage = "";
                                                    if (param_IDLE_ResourceOrder.getReTransmit().equals("1") && !mandatoryError.contains("errorFlag")) {
                                                        errorFlagValidateMessage = validator.ErrorFlagValidate(param.get("errorFlag").toString(), "[0,1]+", errorFlagValidateMessage);
                                                    }

                                                    errorMsg = ErrorMessageUtil.appendCommaInsteadFullStop(errorMsg, errorFlagValidateMessage);


                                                    if (errorMsg.equals("")) {
                                                        AFLog.d("========================================= [suppcode] =========================================");
                                                        for (SuppCode suppcode : resourceMaster.getSuppCodesList()) {
                                                            AFLog.d(suppcode.getSuppCode());
                                                            List<String> resourceRuleListSuppcode = suppcode.getResourceRuleList();
                                                            errorMsg = validateRule(resourceRuleListSuppcode, resourceName, param, resourceOrderIns, true, suppcode.getSuppCode());
                                                        }
//                                                        AFLog.d("=============================================================================================");

                                                        afSubInsChild.setSubResourceName(resourceName);
                                                        afSubInsChild.setSubClientHashMapParameter(param);
                                                        afSubInsChild.setSubResourceItemNeedToProcess(checkResourceItemNeedToProcess(resourceOrderIns, afSubInsChild));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
          errorMsg = "is missing resourceName.";
        }

        AFLog.d("errorMsg : "+errorMsg);
        if (!errorMsg.equals("")) {
            if (param.containsKey("resourceName") && !param.get("resourceName").toString().equals("")) {
            	if(afSubInsChild.getSubInitCmd()!=null&&!afSubInsChild.getSubInitCmd().isEmpty()&&!afSubInsChild.getSubInitCmd().equals("NONE")) {
            		errorMsg = param.get("resourceName").toString() + "(" + resourceItemNumber + ") " +afSubInsChild.getSubInitCmd()+" "+errorMsg;
            	} else {
            		errorMsg = param.get("resourceName").toString() + "(" + resourceItemNumber + ") " +errorMsg;
            	}
            } else {
                errorMsg = "resourceItem(" + resourceItemNumber + ") " +errorMsg;
            }
        }
        return errorMsg;
    }

    public String validateRule(List<String> resourceRuleList,String resourceName,HashMap<String, Object>param,AFSubInstance resourceOrderIns,boolean isSuppcode,String supcode)
    { 
    	String errorMsg = "";
    	ArrayList<String> resourceRuleListArr = new ArrayList<String>();
    	ArrayList<String> checkRuleInactive = new ArrayList<String>();
    	//check value resourceRuleList 
     	if(resourceRuleList.size() <= 0 || resourceRuleList.isEmpty()) {
              errorMsg = "";
          } else {
        	 while (resourceRuleList.contains("")) {
        		 resourceRuleList.remove("");
			}
         	AFLog.d("===================================== [ResourceRuleList] =====================================");
         	 for(String resourceRule :resourceRuleList){
         		resourceRule = resourceRule.trim().replaceAll("\\s", "");
         		 AFLog.d("resourceRuleList: "+resourceRule.trim().replaceAll("\\s", ""));
         		 ResourceRuleMaster resourceRuleMaster = Config.getResourceRuleHashMap().get(resourceRule);
         		 String resourceRuleState = resourceRuleMaster.getResourceRuleState();
         		 List<ResourceRule> ruleDetailList = resourceRuleMaster.getRuleDetail();
         		 AFLog.d("ruleDetailList size : "+ruleDetailList.size());
         		 ArrayList<String>resultRuleDetail = new ArrayList<String>();
         		 //check resourceRuleState, effectiveDate, expireDate from ResourceRuleMaster
         		 if(!resourceRuleState.equals("Active"))
         		 {
         			 AFLog.d("resourceRuleState is "+resourceRuleState);	
         			 checkRuleInactive.add(resourceRule);
         			 
         		 }else{
         			AFLog.d("resourceRuleState is "+resourceRuleState);	
         			 String resourceDateNow = DateTime.now().toString("yyyyMMddHHmmss");
         			//check effectiveDate and expireDate from ResourceRuleMaster
         			 if(Long.parseLong(resourceDateNow) < Long.parseLong(resourceRuleMaster.getEffectiveDate().replace("+0700","")) 
             				 || Long.parseLong(resourceDateNow) > Long.parseLong(resourceRuleMaster.getExpireDate().replace("+0700",""))){
//             				AFLog.d("Out of effectiveDate or expireDate");
             				checkRuleInactive.add(resourceRule);
         			 }else{
         				 for(ResourceRule ruleDetail:ruleDetailList){
         					 String paramName = "";
         					 String ruleType = ruleDetail.getRuleType();
                             String source = ruleDetail.getParamName().substring(0,ruleDetail.getParamName().indexOf("$"));
                             String parameterName = ruleDetail.getParamName().substring(ruleDetail.getParamName().indexOf("$")+1);
         					 String paramOperand = ruleDetail.getParamOperand();
         					 String paramValue = ruleDetail.getParamValue()==null?"":ruleDetail.getParamValue();;
         					 String paramStart = ruleDetail.getParamStart()==null?"":ruleDetail.getParamStart();
         					 String paramStop = ruleDetail.getParamStop()==null?"":ruleDetail.getParamStop();
         					 int paramNameInt = 0,paramValueInt = 0;
         					 Long paramNameLong=0l,paramStartLong=0l,paramStopLong=0l;
         					 boolean ParamIsNumber = true;
         					 
         					//check source
         					if(source.equals("resourceOrder")){
                                  HashMap<String, Object> resourceOrderHashmap = resourceOrderIns.getSubClientHashMapParameter();
                                  //search parameterName in resourceOrder 
                                  if (resourceOrderHashmap.containsKey(parameterName)) {
                                	  paramName = resourceOrderHashmap.get(parameterName).toString();
                                  } else {
                                	  paramName = "";
                                  }
                                  
    						 }else if(source.equals("resourceOrderItem")){
    							 //search parameterName in resourceOrderItem 
    							 if(param.containsKey(parameterName))
    							 {
    								paramName = param.get(parameterName).toString();
    							 }else{
    								paramName = "";
    							 }
    						 }else if(source.equals("neidRouting")){
    							 //check case neidRouting
    							 if(isSuppcode == false){
    								 String neType = Config.getResourcePropertyHashMap().get(resourceName).getPreNeRouting();
                                     if(!Config.getResourceNeIdRoutingMap().containsKey(neType)){
                                     	paramName = "";
                                        errorMsg = "neType = " + neType + " in ResourceProperty is not found in ResourceNeIdRouting.json.";
                                     }else{
                                         String paramValue_neId = param.containsKey(Config.getResourceInventoryHashMap().get(resourceName).getIdentityType()) ? param.get(Config.getResourceInventoryHashMap().get(resourceName).getIdentityType()).toString() : "";
                                         ResourceNeIdRouting neIdRouting = null;
                                         if(paramValue_neId==null || paramValue_neId.equals("")){
                                         	paramName = "";
                                            errorMsg = "neType = " + neType + " is not found identity param : "+Config.getResourceInventoryHashMap().get(resourceName).getIdentityType()+".";
                                         }else if(!(new Validator().isNumeric(paramValue_neId))){
                                             errorMsg = "identity param : "+Config.getResourceInventoryHashMap().get(resourceName).getIdentityType()+" is not a numeric.";
                                         }else{
                                             neIdRouting = NETypeMapper.search(neType, Long.parseLong(paramValue_neId));
                                             if (!neIdRouting.getPartyName().equals("Error")){
                                             	paramName = neIdRouting.getPartyName();
                                             }else{
                                             	paramName = "";
                                                 errorMsg = "neType = " + neType + " is not found identity range in ResourceNeIdRouting.json.";
                                             }
                                         }
                                     }
    							 }else if(isSuppcode == true){
    								 String neType = Config.getResourceMappingCommandHashMap().get(supcode).getNeType();
                                     if (!Config.getResourceNeIdRoutingMap().containsKey(neType)) {
                                     	paramName = "";
                                         errorMsg = "neType = " + neType + " in ResourceMappingCommand is not found in ResourceNeIdRouting.json.";
                                     } else {
                                         String paramValue_neId = param.containsKey(Config.getResourceInventoryHashMap().get(resourceName).getIdentityType()) ? param.get(Config.getResourceInventoryHashMap().get(resourceName).getIdentityType()).toString() : "";
                                         ResourceNeIdRouting neIdRouting = null;
                                         if(paramValue_neId==null || paramValue_neId.equals("")){
                                         	paramName = "";
                                             errorMsg = "neType = " + neType + " is not found identity param : "+Config.getResourceInventoryHashMap().get(resourceName).getIdentityType()+".";
                                         } else if (!(new Validator().isNumeric(paramValue_neId))){
                                             errorMsg = "identity param : "+Config.getResourceInventoryHashMap().get(resourceName).getIdentityType()+" is not a numeric.";
                                         } else{
                                             neIdRouting = NETypeMapper.search(neType, Long.parseLong(paramValue_neId));
                                             if (!neIdRouting.getPartyName().equals("Error")) {
                                             	paramName = neIdRouting.getPartyName();
                                             } else {
                                             	paramName = "";
                                                 errorMsg = "neType = " + neType + " is not found identity range in ResourceNeIdRouting.json.";
                                             }
                                         }
                                     }
    							 }
    						 }
         					//check ruleType
         					 if(ruleType.equals("checkVal"))
         					 {
         						 try{
             							 paramNameInt = Integer.parseInt(paramName);
                 						 paramValueInt = Integer.parseInt(paramValue); 
             						}catch (NumberFormatException ex) {
             							 ParamIsNumber = false;
             						}
         					 }else if(ruleType.equals("checkRange")){
         						
         						 try{
         							 paramNameLong = Long.parseLong(paramName);
         							 paramStartLong = Long.parseLong(paramStart);
         							 paramStopLong = Long.parseLong(paramStop);
         						}catch (NumberFormatException ex) {
         							 ParamIsNumber = false;
         						}
         					 }
         					//check paramName is number or not
         					 if(ParamIsNumber){
         						 switch (paramOperand) {
									case ">": resultRuleDetail.add((paramNameInt > paramValueInt)? "true":"false");break;
									case "<": resultRuleDetail.add((paramNameInt < paramValueInt)? "true":"false");break;
									case "=": resultRuleDetail.add((paramNameInt == paramValueInt)? "true":"false");break;
									case "!=": resultRuleDetail.add((paramNameInt != paramValueInt)? "true":"false");break;
									case "<=": resultRuleDetail.add((paramNameInt <= paramValueInt)? "true":"false");break;
									case ">=": resultRuleDetail.add((paramNameInt >= paramValueInt)? "true":"false");break;
									case "between": resultRuleDetail.add((paramNameLong >= paramStartLong && paramNameLong <= paramStopLong)? "true":"false");break;
									case "not between": resultRuleDetail.add((paramNameLong >= paramStartLong && paramNameLong <= paramStopLong)? "false":"true");break;
									}
         					 }else{
         						 switch (paramOperand) {
										case "=": resultRuleDetail.add(paramName.equals(paramValue)? "true":"false");break;
										case "!=":  resultRuleDetail.add(!paramName.equals(paramValue)? "true":"false");break;
										default:resultRuleDetail.add("false");
											AFLog.d("No case isn't number");break;
         						 }
         					 }
         					 AFLog.d("ruleType: "+ruleType);
         					 AFLog.d("source: "+source);
         					 AFLog.d("parameterName: "+parameterName);
         					 AFLog.d("paramName: "+paramName);
         					 AFLog.d("paramOperand: "+paramOperand);
         					 AFLog.d("paramValue: "+paramValue);
         					 AFLog.d("paramStart: "+paramStart);
         					 AFLog.d("paramStop: "+paramStop);
         					 AFLog.d("statusRuleDetail : "+resultRuleDetail);
         					 AFLog.d("--------------------------------------------------------------------------------");
         				 }
         				 if(resultRuleDetail.contains("false")){
         					 resourceRuleListArr.add("false");
         					 AFLog.d("add false to resourceRuleListArr");	
         				 }else{
         					 resourceRuleListArr.add("true");
         					 AFLog.d("add true to resourceRuleListArr and End Loop");
         					 break;
         				 }
         			 }
         		 }
//         		 AFLog.d("--------------------------------------------------------------------------------");
         	 }
         	 AFLog.d("resourceRuleListArr : "+resourceRuleListArr);
         	 if(resourceRuleListArr.contains("true")){
         		 AFLog.d("resourceRuleList status : true"); 
         		 errorMsg = "";
         	 }else if(resourceRuleListArr.contains("false")){
         		 AFLog.d("resourceRuleList status : false");
         		 //clear case inactive in list
         		 if(checkRuleInactive.size()>0){
         			 for(int i=0;i<checkRuleInactive.size();i++){
         				resourceRuleList.remove(checkRuleInactive.get(i));
         			 }
         		 }
         		if(isSuppcode)
           		 {
           			errorMsg = resourceName+" "+supcode+" resourceRuleList "+resourceRuleList+" in ResourceMaster.json has conflicts in ResourceRuleMaster.json.";
           		 }else{
           			errorMsg = resourceName+" resourceRuleList "+resourceRuleList+" in ResourceProperty.json has conflicts in ResourceRuleMaster.json.";
           		 }
         		 
         	 }
//         	 AFLog.d("==================================================================================");
          }
   	 return errorMsg;
    }
}




