package ct.af.substate;

import ct.af.enums.EResultCode;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.instance.PoolTask;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.resourceModel.ResourceErrorHandling;
import ct.af.resourceModel.ResourceMappingCommand;
import ct.af.resourceModel.ResourceMaster;
import ct.af.resourceModel.ResourceNeIdRouting;
import ct.af.resourceModel.SuppCode;
import ct.af.utils.Config;
import ct.af.utils.ErrorMessageUtil;
import ct.af.utils.NETypeMapper;
import ct.af.utils.SuccessPatternUtil;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public class Do_IDLE_ResourceOrder {
    public void doBusinessLogic(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {
        AFLog.d("Do_IDLE_ResourceOrder|SubInternalCode: " + afSubIns.getSubInternalCode());

        String errorMsg = "";
        HashMap<String, Integer> neIdQuotaAmountRequire = new HashMap<>();
        String reTransmit = ((Param_IDLE_ResourceOrder)afSubIns.getSubClientParameter()).getReTransmit();

        if (afSubIns.getSubInternalCode().equals(EResultCode.RE20000.getResultCode())) {
            int resourceItemIndex = 1;
            for (String resourceItemInsNo : afSubIns.getSubResourceItemInsNoArray()) {
                String resourceItemErrorMsg = "";
                AFSubInstance resourceItemInstance = afInstance.getMainSubInstance(resourceItemInsNo);

                boolean dropResourceFlag = resourceItemInstance.getDropResourceFlag();
                AFLog.d("resourceItemStatus Flag : "+resourceItemInstance.getFlagResourceItemStatus());
                AFLog.d("dropResourceFlag : "+dropResourceFlag);
                AFLog.d("isSubResourceItemNeedToProcess : "+resourceItemInstance.isSubResourceItemNeedToProcess());


                if (!dropResourceFlag && resourceItemInstance.getFlagResourceItemStatus().equals("N") && resourceItemInstance.isSubResourceItemNeedToProcess()) {
                    // set suppcodeList
                    String resourceIndex = resourceItemInstance.getSubInitCmd();
                    ResourceMaster resourceMaster = Config.getResourceMasterHashMap().get(resourceIndex);
                    List<SuppCode> suppcodeParentList = resourceMaster.getSuppCodesList();
                    resourceItemInstance.setSuppCodeList(suppcodeParentList);
                    HashMap<String, Object> resourceItemParam = resourceItemInstance.getSubClientHashMapParameter();
                    String errorFlag = reTransmit.equals("1")
                                        ? resourceItemParam.get("errorFlag").toString()
                                        : "";

                    if (afSubIns.getSubFirstIndexResourceItemToProcess() == -1) {
                        // set first index resourceItem need to process
                        afSubIns.setSubFirstIndexResourceItemToProcess(resourceItemIndex-1);
                    }

                    AFLog.d("========== start neIdQuotaAmountRequire : " + neIdQuotaAmountRequire + " ==========");
                    int suppcodeIndex = 1;
                    for (SuppCode suppcodeParent : suppcodeParentList) {
                        HashMap<String, Integer> neIdQuotaRequireForEachSuppcode = new HashMap<>();
                        boolean suppcodeNeedProcess = suppcodeIndex - 1 < errorFlag.length()
                                                        ? errorFlag.charAt(suppcodeIndex-1) == '0'
                                                        : true;

                        if (suppcodeNeedProcess) {
                            if (resourceItemInstance.getSubFirstTaskNoToProcess() == -1) {
                                // set first task resourceItem need to process
                                resourceItemInstance.setSubFirstTaskNoToProcess(suppcodeIndex-1);
                            }

                            resourceItemErrorMsg = findNeId(suppcodeParent.getSuppCode(), resourceItemErrorMsg, afSubIns, resourceItemInstance, neIdQuotaRequireForEachSuppcode, null);
                            AFLog.d("========== neIdQuotaRequireForEachSuppcode suppcodeParent : " + neIdQuotaRequireForEachSuppcode + "==========");

                            resourceItemErrorMsg = calculateTimeout(afSubIns, resourceItemInstance, resourceItemErrorMsg, suppcodeParent, neIdQuotaRequireForEachSuppcode);


                            // set default pooltask to fail
                            SuccessPatternUtil successPatternUtil = new SuccessPatternUtil();
                            PoolTask poolTask = successPatternUtil.getTaskResult("Fail", null, resourceItemInstance);
                            poolTask.setSuppCode(resourceItemInstance.getSuppCodeList().get(suppcodeIndex-1).getSuppCode());
                            AFLog.d("set default 50023 to task."+poolTask.toString());
                            resourceItemInstance.getMappingPoolTask().put("Task"+suppcodeIndex, poolTask);
                        } else {
                            // default for don't process suppcode
                            resourceItemInstance.getNeIdList().add("");
                            resourceItemInstance.getSubResourceNeidRoutingArray().add(null);
                        }

                        summaryNeIdQuotaRequire(neIdQuotaAmountRequire, neIdQuotaRequireForEachSuppcode);
                        AFLog.d("========== end neIdQuotaAmountRequire : " + neIdQuotaAmountRequire + " ==========");
                        AFLog.d("NeIdForErrorHandlingSuppcode : "+resourceItemInstance.getSubNeIdForErrorHandlingSuppcode());

                        suppcodeIndex++;
                    }


                    if (!resourceItemErrorMsg.equals("")) {
                        resourceItemErrorMsg = resourceItemInstance.getSubResourceName() + "(" + resourceItemIndex + ") " + resourceItemErrorMsg;
                    }
                } else {
                    if (resourceItemInstance.getFlagResourceItemStatus().equals("Y")) {
                        // set default pooltask to success
                        SuccessPatternUtil successPatternUtil = new SuccessPatternUtil();
                        PoolTask poolTask = successPatternUtil.getTaskResult("FC", null, resourceItemInstance);
                        AFLog.d(poolTask.toString());
                        HashMap<String, Object> objectMapkey = new HashMap<>();
                        objectMapkey.put(afSubIns.getTaskId(), "Force Complete");
                        resourceItemInstance.getMappingPoolTask().put("Task1", poolTask);
                        resourceItemInstance.getMappingResponse().putAll(objectMapkey);
                    } else if (!resourceItemInstance.isSubResourceItemNeedToProcess()) {
                        String resourceIndex = resourceItemInstance.getSubInitCmd();
                        ResourceMaster resourceMaster = Config.getResourceMasterHashMap().get(resourceIndex);
                        List<SuppCode> suppcodeParentList = resourceMaster.getSuppCodesList();
                        resourceItemInstance.setSuppCodeList(suppcodeParentList);

                        // default neid
                        for (int i = 0; i < suppcodeParentList.size(); i++) {
                            resourceItemInstance.getNeIdList().add("");
                        }
                    }
                }

                errorMsg = ErrorMessageUtil.appendWithSemicolon(errorMsg, resourceItemErrorMsg);
                resourceItemIndex++;
            }

            if (!errorMsg.equals("")) {
                afSubIns.setSubResultCode(EResultCode.RE40300.getResultCode());
                afSubIns.setSubInternalCode(EResultCode.RE40300.getResultCode());

                afInstance.setMainResourceOrderInsNo(afSubIns.getSubInstanceNo());
                afSubIns.setSubControlState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SDF_POSTREPORT.getState());
                afSubIns.setSubNextState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SDF_POSTREPORT.getState());
                afSubIns.setErrorMessage(errorMsg);

                for (String subResourceItemInsNo : afSubIns.getSubResourceItemInsNoArray()) {
                    AFSubInstance afSubInsChild = afInstance.getMainSubInstance(subResourceItemInsNo);
                    afSubInsChild.setSubResourceOrderHasError(true);
                }

                afInstance.decrementMainCountWait();
                afInstance.incrementMainCountProcess();
            } else {
                if (afSubIns.getSubFirstIndexResourceItemToProcess() != -1) {
                    // need to process resourceItem
                    AFLog.d("neIdQuotaAmount : "+neIdQuotaAmountRequire);
                    afInstance.setMainResourceOrderInsNo(afSubIns.getSubInstanceNo());
                    afInstance.decrementMainCountWait();
                    afInstance.incrementMainCountProcess();
                    afInstance.setMainIsLock(true);
                    afSubIns.setNeIdQuotaAmountRequire(neIdQuotaAmountRequire);
                    afSubIns.setSubNextState(ESubState.SDF_GETRESOURCEINFRANODE.getState());
                    afSubIns.setSubControlState(ESubState.SDF_GETRESOURCEINFRANODE.getState());
                    AFLog.d("resourceOrderTimeout before : "+afSubIns.getResourceOrderTimeOut());
                    afSubIns.setResourceOrderTimeOut(afSubIns.getResourceOrderTimeOut() < Config.getEXPIRATIONDATE() ? afSubIns.getResourceOrderTimeOut() : Config.getEXPIRATIONDATE());
                    AFLog.d("resourceOrderTimeout after : "+afSubIns.getResourceOrderTimeOut());
                } else {
                    // don't process all resourceItem
                    afInstance.setMainResourceOrderInsNo(afSubIns.getSubInstanceNo());
                    afSubIns.setSubNextState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SLEEP_SENDRESULT.getState());
                    afSubIns.setSubControlState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SLEEP_SENDRESULT.getState());
                    afInstance.setMainIsLock(true);
                    afInstance.incrementMainCountProcess();
                    afInstance.decrementMainCountWait();
                    afSubIns.setResourceOrderTimeOut(Config.getDefaultServerTimeout());
                }
            }

        } else if (afSubIns.getSubInternalCode().equals(EResultCode.RE40301.getResultCode())) {
            afSubIns.setSubControlState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SDF_POSTREPORT.getState()+","+ESubState.END.getState());
            afSubIns.setSubNextState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SDF_POSTREPORT.getState()+","+ESubState.END.getState());
            afSubIns.setErrorMessage(afSubIns.getSubResourceOrderValidateMessage());
            afInstance.decrementMainCountWait();
        } else {
            afInstance.setMainResourceOrderInsNo(afSubIns.getSubInstanceNo());
            afSubIns.setSubControlState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SDF_POSTREPORT.getState());
            afSubIns.setSubNextState(ESubState.END_RESOURCEORDER.getState()+","+ESubState.SDF_POSTREPORT.getState());
            afSubIns.setErrorMessage(afSubIns.getSubResourceOrderValidateMessage());

            for (String subResourceItemInsNo : afSubIns.getSubResourceItemInsNoArray()) {
                AFSubInstance afSubInsChild = afInstance.getMainSubInstance(subResourceItemInsNo);
                afSubInsChild.setSubResourceOrderHasError(true);
            }

            afInstance.decrementMainCountWait();
            afInstance.incrementMainCountProcess();
        }

      

        BigDecimal bd = new BigDecimal(afSubIns.getResourceOrderTimeOut());
        double lastMaxTotaleExceTime =  bd.setScale(0, RoundingMode.UP).doubleValue();
        int resourceOrderTimeOut = (int) lastMaxTotaleExceTime;
        afSubIns.setResourceOrderTimeOut(resourceOrderTimeOut);

        // set resourceOrder expire time
        DateTimeFormatter hrzDateFormat = Config.getHrzDateFormat();
        DateTimeFormatter systemDateFormat = Config.getFormatDateWithMiTz();
        DateTime initialTimeStampIn = systemDateFormat.parseDateTime(afSubIns.getSubInitTimeStampIn());
        DateTime resourceOrderExpirationDate = initialTimeStampIn.plusSeconds(resourceOrderTimeOut);
        afSubIns.setExpirationDateResourceOrder(hrzDateFormat.print(resourceOrderExpirationDate));
        AFLog.d("resourceOrder timestampIncoming : "+afSubIns.getSubInitTimeStampIn());
        AFLog.d("resourceOrder expiredTime : "+afSubIns.getExpirationDateResourceOrder());
    }

    private String findNeId(String suppcode, String resourceItemErrorMsg, AFSubInstance afSubIns, AFSubInstance resourceItemInstance, HashMap<String, Integer> neIdQuotaAmountRequire, ResourceErrorHandling resourceErrorHandling) {
        if (!Config.getResourceMappingCommandHashMap().containsKey(suppcode)) {
            // not found suppcode
            resourceItemErrorMsg = ErrorMessageUtil.appendWithAnd(resourceItemErrorMsg, suppcode + " is not found in ResourceCommandMapping.json.");
        } else {
            ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);

            // check message format
            String[] messageFormatArray = {"xml", "json", "httpget", "cmdline", "cmdlinebase64"};
            boolean isValidMessageFormat = false;
            for (String format : messageFormatArray) {
                if (format.equalsIgnoreCase(resourceMappingCommand.getMessageFormat())) {
                    isValidMessageFormat = true;
                }
            }

            if (!isValidMessageFormat) {
                // invalid messageFormat
                resourceItemErrorMsg = ErrorMessageUtil.appendWithAnd(resourceItemErrorMsg, "messageFormat is invalid in ResourceCommandMapping.json.");
            } else {
                if (!Config.getResourceNeTypePropertyHashMap().containsKey(Config.getResourceMappingCommandHashMap().get(suppcode).getNeType())) {
                    // not found neType from resourceCommandMapping in resourceNeTypeProperty
                    resourceItemErrorMsg = ErrorMessageUtil.appendWithAnd(resourceItemErrorMsg,suppcode + " is not found neType = " + resourceMappingCommand.getNeType() + " in ResourceNeTypeProperty.json.");
                } else {
                    String neType = resourceMappingCommand.getNeType();
                    String identityType = resourceMappingCommand.getIdentityType();
                    String forceNeId = resourceMappingCommand.getForceNeid();
                    String neId = "";
                    String quotaFlag = resourceMappingCommand.getQuotaFlag();


                    ResourceNeIdRouting resourceNeIdRouting = null;
                    AFLog.d("========================================= Look up ResourceMappingCommand =========================================");
                    AFLog.d("suppcode : " + suppcode);
                    AFLog.d("neType : " + neType);
                    AFLog.d("identityType : " + identityType);
                    AFLog.d("forceNeId : " + forceNeId);
                    AFLog.d("quotaFlag : " + quotaFlag);
                    AFLog.d("==================================================================================================================");
                    HashMap<String, Object> resourceItemParam = resourceItemInstance.getSubClientHashMapParameter();

                    if (forceNeId.equals("")) {
                        if (identityType.equals("")) {
                            resourceItemErrorMsg = ErrorMessageUtil.appendWithAnd(resourceItemErrorMsg,suppcode + " forceNeid is empty so identityType must not empty in ResourceCommandMapping.json.");
                        } else {
                            String source = identityType.substring(0,identityType.indexOf("$"));
                            identityType = identityType.substring(identityType.indexOf("$") + 1);
                            if(source.equals("resourceItemList.")) {
                                if (resourceItemParam.containsKey(identityType)) {
                                    if (!Config.getResourceNeIdRoutingMap().containsKey(neType)) {
                                        resourceItemErrorMsg = ErrorMessageUtil.appendWithAnd(resourceItemErrorMsg, suppcode + " neType = " + neType + " in ResourceCommandMapping is not found in ResourceNeIdRouting.json.");
                                    } else {
                                        String paramValue = resourceItemParam.get(identityType).toString();
                                        resourceNeIdRouting = NETypeMapper.search(neType, Long.parseLong(paramValue));

                                        if (!resourceNeIdRouting.getPartyName().equals("Error")) {
                                            neId = resourceNeIdRouting.getNeId();
                                        } else {
                                            resourceItemErrorMsg = ErrorMessageUtil.appendWithAnd(resourceItemErrorMsg,suppcode + " neType = " + neType + " is not found identity range in ResourceNeIdRouting.json.");
                                        }
                                    }
                                    AFLog.d("NEID from NeIdRouting : " + neId);
                                } else {
                                    // not found identityType from client
                                    resourceItemErrorMsg = ErrorMessageUtil.appendWithAnd(resourceItemErrorMsg,suppcode + " forceNeid is empty but " + identityType + " of identityType is invalid in ResourceCommandMapping.json.");
                                }
                            } else {
                                // unknown source
                                resourceItemErrorMsg = ErrorMessageUtil.appendWithAnd(resourceItemErrorMsg,suppcode + " forceNeid is empty but root node of identityType (" + source.substring(0,source.length() - 1) + ") is invalid in ResourceCommandMapping.json.");
                            }
                        }
                    } else if(forceNeId.equals("@forceNeid")) {
                        if (resourceItemParam.containsKey("forceNeid")) {
                            neId = resourceItemParam.get("forceNeid").toString();
                            AFLog.d("NEID from line 1 : " + neId);
                        }
                    } else {
                        neId = forceNeId;
                        AFLog.d("NEID from forceNeId : " + neId);
                    }

                    if (neId != null && !neId.equals("")) {
                        // calculate quota for each errorcode of suppcode
                        if (!neIdQuotaAmountRequire.containsKey(neId)) {
                            if (!quotaFlag.equals("Y")) {
                                neIdQuotaAmountRequire.put(neId, 0);
                            } else {
                                // quotaFlag = Y
                                neIdQuotaAmountRequire.put(neId, 1);
                            }
                        } else {
                            if (quotaFlag.equals("Y")) {
                                if (Integer.parseInt(neIdQuotaAmountRequire.get(neId).toString()) == 0) {
                                    neIdQuotaAmountRequire.put(neId, neIdQuotaAmountRequire.get(neId) + 1);
                                }
                            }
                        }
                        // store neid for errorHandlingSuppcode
                        if (resourceErrorHandling == null) {
                            resourceItemInstance.getNeIdList().add(neId);
                            // for use in out_ne
                            resourceItemInstance.getSubResourceNeidRoutingArray().add(resourceNeIdRouting);
                        } else {
                            HashMap<String, HashMap<String, List<String>>> neIdForErrorHandlingSuppcode = resourceItemInstance.getSubNeIdForErrorHandlingSuppcode();
                            if (!neIdForErrorHandlingSuppcode.containsKey(resourceErrorHandling.getSuppCode())) {
                                List<String> neidList = new ArrayList<>();
                                neidList.add(neId);
                                HashMap<String, List<String>> mapErrorWithNeId = new HashMap<>();
                                mapErrorWithNeId.put(resourceErrorHandling.getSearchKey(), neidList);
                                neIdForErrorHandlingSuppcode.put(resourceErrorHandling.getSuppCode(), mapErrorWithNeId);
                            } else {
                                // HashMap<String, HashMap<String, List<String>>> subNeIdForErrorHandlingSuppcode= new HashMap<>(); // <suppcode, <keysearchError,[neid1,neid2]>>
                                HashMap<String, List<String>> mapErrorCodeWithNeIdList = neIdForErrorHandlingSuppcode.get(resourceErrorHandling.getSuppCode());

                                if (!mapErrorCodeWithNeIdList.containsKey(resourceErrorHandling.getSearchKey())) {
                                    List<String> neidList = new ArrayList<>();
                                    neidList.add(neId);
                                    mapErrorCodeWithNeIdList.put(resourceErrorHandling.getSearchKey(), neidList);
                                } else {
                                    List<String> neIdList = mapErrorCodeWithNeIdList.get(resourceErrorHandling.getSearchKey());
                                    neIdList.add(neId);
                                }
                            }
                        }
                    }
                }
            }
        }
        return resourceItemErrorMsg;
    }

    private void summaryNeIdQuotaRequire(HashMap<String, Integer> neIdQuotaAmountRequire, HashMap<String, Integer> neIdQuotaRequireForEachSuppcode) {
        for (Map.Entry<String, Integer> entry : neIdQuotaRequireForEachSuppcode.entrySet()) {
            if (neIdQuotaAmountRequire.containsKey(entry.getKey())) {
                Integer tempQuota = neIdQuotaAmountRequire.get(entry.getKey());
                neIdQuotaAmountRequire.put(entry.getKey(), tempQuota + entry.getValue());
            } else {
                neIdQuotaAmountRequire.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private String calculateTimeout(AFSubInstance afSubIns, AFSubInstance resourceItemInstance, String resourceItemErrorMsg, SuppCode suppcodeParent, HashMap<String, Integer> neIdQuotaRequireForEachSuppcode) {
        ResourceMappingCommand resourceMappingCommandParentSuppcode;
        List<Float> execTimeForEachErrorcode = new ArrayList<>();

        double gapExecTime = 1 + Config.getGAP_EXECTIME();
        int retrySleep;
        int retryTime;
        int timeout;

        if (!resourceItemErrorMsg.equals("") || afSubIns.getFlagTimeOut()) {
            afSubIns.setFlagTimeOut(true);
            afSubIns.setResourceOrderTimeOut(0);
        }

        if (Config.getResourceErrorhandlingHashMap().containsKey(suppcodeParent.getSuppCode())) {
            // suppcodeParent is in resourceErrorHandling
            // don't check flagTimeout because need to find neid and quota for send error to client
            List<ResourceErrorHandling> resourceErrorHandlingList = Config.getResourceErrorhandlingHashMap().get(suppcodeParent.getSuppCode());

            for (ResourceErrorHandling resourceErrorHandling : resourceErrorHandlingList) {
                AFLog.d("--------------------------------------------- resourceErrorHandling : "+resourceErrorHandling+"---------------------------------------------");
                if (resourceErrorHandling.getErrAction().equals("ErrHandlingSuppCode")) {
                    AFLog.d("****************************************");
                    AFLog.d("errorHanding with suppcode");
                    // errorHandling with ErrHandlingSuppCode

                    int sumExecTimeEachSuppcode = 0;
                    retrySleep = Integer.parseInt(StringUtils.isNotBlank(resourceErrorHandling.getRetrySleep())  ? resourceErrorHandling.getRetrySleep() : "0");

                    if(resourceErrorHandling.getErrHandlingSuppCode().size() !=0){


                        for (String errorHandlingSuppcode : resourceErrorHandling.getErrHandlingSuppCode()) {
                            resourceItemErrorMsg = findNeId(errorHandlingSuppcode, resourceItemErrorMsg, afSubIns, resourceItemInstance, neIdQuotaRequireForEachSuppcode, resourceErrorHandling);
                            AFLog.d("========== neIdQuotaRequireForEachSuppcode suppcodeChild : " + neIdQuotaRequireForEachSuppcode + "==========");

                            if (!afSubIns.getFlagTimeOut() && resourceItemErrorMsg.equals("")) {
                                // calculate timeout when flagTimeout not error and suppcode not error

                                ResourceMappingCommand resourceMappingCommandErrorHandlingSuppcode = Config.getResourceMappingCommandHashMap().get(errorHandlingSuppcode);

                                int errorHandlingSuppcodeTimeout;
                                if (resourceMappingCommandErrorHandlingSuppcode.getTimeOut().equals("0")) {
                                    errorHandlingSuppcodeTimeout = Config.getDefaultServerTimeout();
                                } else {
                                    errorHandlingSuppcodeTimeout = Integer.parseInt(resourceMappingCommandErrorHandlingSuppcode.getTimeOut());
                                }

                                sumExecTimeEachSuppcode += (errorHandlingSuppcodeTimeout + retrySleep);
                            } else {
                                afSubIns.setFlagTimeOut(true);

                                afSubIns.setResourceOrderTimeOut(0);
                            }
                        }
                    }

                    if (!afSubIns.getFlagTimeOut()) {
                        if (!Config.getResourceMappingCommandHashMap().get(suppcodeParent.getSuppCode()).getTimeOut().equals("0")) {
                            timeout = (Integer.parseInt(Config.getResourceMappingCommandHashMap().get(suppcodeParent.getSuppCode()).getTimeOut()));
                        } else {
                            timeout = Config.getDefaultServerTimeout();
                        }
                        if (resourceErrorHandling.getErrHandlingSuppCode().size() != 0) {
                            float totalExecTime = (float) ((timeout + sumExecTimeEachSuppcode)* gapExecTime);
                            execTimeForEachErrorcode.add(totalExecTime);
                        } else {
                            AFLog.d("========== ErrHandlingSuppCodeList size0 ==========");
                            float totalExecTime = (float) ((timeout + retrySleep)* gapExecTime);
                            execTimeForEachErrorcode.add(totalExecTime);
                        }
                    }
                    AFLog.d("****************************************");

                } else if (resourceErrorHandling.getErrAction().equals("Retry") && !afSubIns.getFlagTimeOut()) {
                    // errorHandling with retry && flagTimeout not error
                    // check flagTimeout for don't calculate timeout if flagTimeout error

                    resourceMappingCommandParentSuppcode = Config.getResourceMappingCommandHashMap().get(suppcodeParent.getSuppCode());

                    if (!resourceMappingCommandParentSuppcode.getTimeOut().equals("0")) {
                        timeout = (Integer.parseInt(resourceMappingCommandParentSuppcode.getTimeOut()));
                    } else {
                        timeout = Config.getDefaultServerTimeout();
                    }

                    retrySleep = Integer.parseInt(StringUtils.isNotBlank(resourceErrorHandling.getRetrySleep())  ? resourceErrorHandling.getRetrySleep() : "0");
                    retryTime = Integer.parseInt(StringUtils.isNotBlank(resourceErrorHandling.getRetryTime())  ? resourceErrorHandling.getRetryTime() : "0");


                    AFLog.d("****************************************");
                    AFLog.d("errorHandling with retry && flagTimeout not error");
                    AFLog.d("suppcode "+ suppcodeParent);
                    AFLog.d("[retrySleep] "+ retrySleep);
                    AFLog.d("[retryTime] "+ retryTime);
                    AFLog.d("[timeout] "+ timeout);
                    AFLog.d("****************************************");

                    int allRetryTime = (retryTime * timeout) + (retryTime * retrySleep);
                    float totalExecTimeout = (float) ((timeout + allRetryTime) * gapExecTime);
                    AFLog.d("[totalExecTimeout]"+totalExecTimeout);
                    execTimeForEachErrorcode.add(totalExecTimeout);
                } else if (resourceErrorHandling.getErrAction().equals("FC") && !afSubIns.getFlagTimeOut()) {
                    // ErrorAction FC && flagTimeout not error
                    // check flagTimeout for don't calculate timeout if flagTimeout error

                    resourceMappingCommandParentSuppcode = Config.getResourceMappingCommandHashMap().get(suppcodeParent.getSuppCode());
                    if (!resourceMappingCommandParentSuppcode.getTimeOut().equals("0")) {
                        timeout = (Integer.parseInt(resourceMappingCommandParentSuppcode.getTimeOut()));
                    } else {
                        timeout = Config.getDefaultServerTimeout();
                    }

                    float TimeOutCalculation;
                    AFLog.d("****************************************");
                    AFLog.d("ErrorAction FC && flagTimeout not error");
                    AFLog.d("suppcode "+ suppcodeParent);
                    AFLog.d("timeout "+ timeout);
                    AFLog.d("****************************************");

                    TimeOutCalculation = (float) (timeout * gapExecTime);
                    AFLog.d("[totalExecTimeout]"+TimeOutCalculation);
                    execTimeForEachErrorcode.add(TimeOutCalculation);
                }
            }

            if (!afSubIns.getFlagTimeOut()) {
                // summary max timeOut
                float maxTimeout = Collections.max(execTimeForEachErrorcode);
                AFLog.d("execTimeForEachErrorcode : "+execTimeForEachErrorcode);
                AFLog.d("maxTimeout : "+maxTimeout);

                afSubIns.setResourceOrderTimeOut(afSubIns.getResourceOrderTimeOut() + maxTimeout);
            }
        } else if (!Config.getResourceErrorhandlingHashMap().containsKey(suppcodeParent.getSuppCode()) && !afSubIns.getFlagTimeOut()) {
            // suppcodeParent is not in resourceErrorHandling && flagTimeout not error
            // check flagTimeout for don't calculate timeout if flagTimeout error

            resourceMappingCommandParentSuppcode = Config.getResourceMappingCommandHashMap().get(suppcodeParent.getSuppCode());

            if (!resourceMappingCommandParentSuppcode.getTimeOut().equals("0")) {
                timeout = (Integer.parseInt(resourceMappingCommandParentSuppcode.getTimeOut()));
            } else {
                timeout = Config.getDefaultServerTimeout();
            }

//            AFLog.d("****************************************");
            AFLog.d("suppcodeParent is not in resourceErrorHandling && flagTimeout not error ");
            AFLog.d("suppcode :"+ suppcodeParent);
            AFLog.d("timeout :"+ timeout);
//            AFLog.d("****************************************");

            double timeoutCalculation = timeout * gapExecTime;
            afSubIns.setResourceOrderTimeOut(afSubIns.getResourceOrderTimeOut() + timeoutCalculation);
        }
        return resourceItemErrorMsg;
    }
}