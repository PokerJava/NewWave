package ct.af.utils;

import com.google.gson.internal.LinkedTreeMap;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.instance.Foreach;
import ct.af.resourceModel.*;
import ec02.af.utils.AFLog;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;

public class RequestTemplateProcessor
{
    public static Object paramObjectSearch(String input, AFInstance afInstance, AFSubInstance afSubIns, Map<String, Foreach> foreachMap)
    {
        // resourceCommandMapping
        // resourceInfraNode
        // resourceInventory
        // resourceMaster
        // resourceProperty
        // resourceRuleMaster
        // resourceOrder
        // resourceOrderItem
        HashMap<String, Object> resourceOrderItemMap = afSubIns.getSubClientHashMapParameter();
        String[] splittedInput = input.split("\\.");
        for(int i = 0 ; i<splittedInput.length ; i++) {
        	splittedInput[i] = splittedInput[i].trim();
        }
       
        Map<String, Object> tempMap;
        if(splittedInput.length > 1)
        {
            switch (splittedInput[0])
            {
                case "resourceOrder":
                    AFSubInstance orderIns = afInstance.getMainSubInstance(afSubIns.getSubResourceOrderIns());
                    tempMap = orderIns.getSubClientHashMapParameter();
                    StringBuilder bld = new StringBuilder("resourceOrder.");
                    for(int i=1; i < splittedInput.length; i++)
                    {
                        bld.append(splittedInput[i]);
                        if(i == splittedInput.length - 1)
                        {
                            if(tempMap.containsKey(splittedInput[i]))
                            {
                                return tempMap.get(splittedInput[i]);
                            }
                            else
                            {
                                AFLog.d("[paramObjectSearch ERROR]: Can't get param from resourceOrder: " + bld.toString());
                                afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                return null;
                            }
                        }
                        else
                        {
                            if(tempMap.get(splittedInput[i]) instanceof LinkedTreeMap)
                            {
                                // get deeper item
                                tempMap = (LinkedTreeMap) tempMap.get(splittedInput[i]);
                                bld.append(".");
                            }
                            else if(tempMap.get(splittedInput[i]) instanceof HashMap)
                            {
                                // get deeper item
                                tempMap = (HashMap) tempMap.get(splittedInput[i]);
                                bld.append(".");
                            }
                            else
                            {
                                AFLog.d("[paramObjectSearch ERROR]: " + bld.toString() + " is not an object in resourceOrder.");
                                afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                return null;
                            }
                        }
                    }
                    break;
                case "resourceOrderItem":
                    tempMap = resourceOrderItemMap;
                    bld = new StringBuilder("resourceOrderItem.");
                    for(int i=1; i < splittedInput.length; i++)
                    {
                        bld.append(splittedInput[i]);
                        if(i == splittedInput.length - 1)
                        {
                            if(tempMap.containsKey(splittedInput[i]))
                            {
                                return tempMap.get(splittedInput[i]);
                            }
                            else
                            {
                                AFLog.d("[paramObjectSearch ERROR]: Can't get param from resourceOrderItem: " + bld.toString());
                                afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                return null;
                            }
                        }
                        else
                        {
                            // resourceOrderItem.array1.hashMap.realVar
                            if(foreachMap.containsKey(bld.toString()))
                            {
                                Foreach foreach = foreachMap.get(bld.toString());
                                Object currentObjectFromArray = foreach.getArrayRef().get(foreach.getObjectIndex());
                                if(currentObjectFromArray instanceof Map)
                                {
                                    Map hashmap = (Map) currentObjectFromArray;
                                    if (hashmap.get(splittedInput[i]) instanceof LinkedTreeMap)
                                    {
                                        // get deeper item
                                        tempMap = (Map) hashmap.get(splittedInput[i]);
                                        bld.append(".");
                                    }
                                    else if (hashmap.get(splittedInput[i]) instanceof HashMap)
                                    {
                                        // get deeper item
                                        tempMap = (Map) hashmap.get(splittedInput[i]);
                                        bld.append(".");
                                    }
                                    else
                                    {
                                        tempMap = hashmap;
                                        bld.append(".");
                                    }
                                }
                                else
                                {
                                    AFLog.d("[paramObjectSearch ERROR]: " + bld.toString() + " is not an object in resourceOrderItem.");
                                    afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                    return null;
                                }
                            }
                            else if(tempMap.get(splittedInput[i]) instanceof LinkedTreeMap)
                            {
                                // get deeper item
                                tempMap = (Map) tempMap.get(splittedInput[i]);
                                bld.append(".");
                            }
                            else if(tempMap.get(splittedInput[i]) instanceof HashMap)
                            {
                                // get deeper item
                                tempMap = (Map) tempMap.get(splittedInput[i]);
                                bld.append(".");
                            }
                            else
                            {
                                AFLog.d("[paramObjectSearch ERROR]: " + bld.toString() + " is not an object in resourceOrderItem.");
                                afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                return null;
                            }
                        }
                    }
                    break;

                case "resourceCommandMapping":
                    String suppcode = afSubIns.getCurrentSuppcode();
                    AFLog.d("suppcode : "+ suppcode);

                    ResourceMappingCommand resMappingCmd = Config.getResourceMappingCommandHashMap().get(suppcode);
                    if(splittedInput.length == 2)
                    {
                        switch (splittedInput[1])
                        {
                            case "suppCode":
                                return resMappingCmd.getSuppcode();

                            case "suppCodeType":
                                return resMappingCmd.getSuppCodeType();

                            case "timeOut":
                                return resMappingCmd.getTimeOut();

                            case "neType":
                                return resMappingCmd.getNeType();

                            case "identityType":
                                return resMappingCmd.getIdentityType();

                            case "quotaFlag":
                                return resMappingCmd.getQuotaFlag();

                            case "url":
                                return resMappingCmd.getUrl();

                            case "SOAPAction":
                                return resMappingCmd.getSOAPAction();

                            case "requestMsgType":
                                return resMappingCmd.getRequestMsgType();

                            case "responseMsgType":
                                return resMappingCmd.getResponseMsgType();

                            case "description":
                                return resMappingCmd.getDescription();

                            case "method":
                                return resMappingCmd.getMethod();

                            case "forceNeid":
                                return resMappingCmd.getForceNeid();

                            case "messageFormat":
                                return resMappingCmd.getMessageFormat();

                            case "createDate":
                                return resMappingCmd.getCreateDate();

                            case "createBy":
                                return resMappingCmd.getCreateBy();

                            case "lastUpdBy":
                                return resMappingCmd.getLastUpdBy();

                            case "lastUpdDate":
                                return resMappingCmd.getLastUpdDate();

                            default:
                                AFLog.d("[paramObjectSearch ERROR]: " + input + " not found in resourceCommandMapping.");
                                afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                return null;
                        }
                    }
                    else
                    {
                        AFLog.d("[paramObjectSearch ERROR]: @param can get only 2 levels of variable hierarchy.");
                        afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                        return null;
                    }

                case "resourceInfraNode":
                    ResourceInfraNode resInfraNode = afSubIns.getResourceInfraNodeHashMap().get(afSubIns.getSubNeId());
                    AFLog.d("[b] : "+resInfraNode);
                    if(splittedInput.length == 2)
                    {
                        switch (splittedInput[1])
                        {
                            case "neId":
                                return resInfraNode.getNeId();

                            case "neState":
                                return resInfraNode.getNeState();

                            case "quotaPlan":
                                return resInfraNode.getQuotaPlan();

                            case "quotaAvailability":
                                return resInfraNode.getQuotaAvailability();

                            case "alertRatio":
                                return resInfraNode.getAlertRatio();

                            case "pfUsername":
                                return resInfraNode.getUserName();

                            case "pfPassword":
                                return resInfraNode.getPassword();

                            case "hlrSN":
                                return resInfraNode.getHlrSN();

                            default:
                                AFLog.d("[paramObjectSearch ERROR]: " + input + " not found in resourceInfraNode.");
                                afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                return null;
                        }
                    }
                    else
                    {
                        AFLog.d("[paramObjectSearch ERROR]: @param can get only 2 levels of variable hierarchy.");
                        afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                        return null;
                    }

                case "resourceInventory":
                    if(resourceOrderItemMap.containsKey("resourceName"))
                    {
                        ResourceInventory resInventory = Config.getResourceInventoryHashMap().get((String) resourceOrderItemMap.get("resourceName"));
                        if (splittedInput.length == 2)
                        {
                            switch (splittedInput[1])
                            {
                                case "resourceName":
                                    return resInventory.getResourceName();

                                case "inventoryType":
                                    return resInventory.getInventoryType();

                                case "resourceState":
                                    return resInventory.getResourceState();

                                case "quotaPlan":
                                    return resInventory.getQuotaPlan();

                                case "effectiveDate":
                                    return resInventory.getEffectiveDate();

                                case "expireDate":
                                    return resInventory.getExpireDate();

                                case "instanceFlag":
                                    return resInventory.getInstanceFlag();

                                case "identityFlag":
                                    return resInventory.getIdentityFlag();

                                case "identityType":
                                    return resInventory.getIdentityType();

                                case "createDate":
                                    return resInventory.getCreateDate();

                                case "createBy":
                                    return resInventory.getCreateBy();

                                case "lastUpdBy":
                                    return resInventory.getLastUpdBy();

                                case "lastUpdDate":
                                    return resInventory.getLastUpdDate();

                                case "resourceDescriptionTH":
                                    return resInventory.getResourceDescriptionTH();

                                case "resourceDescriptionEN":
                                    return resInventory.getResourceDescriptionEN();

                                default:
                                    AFLog.d("[paramObjectSearch ERROR]: " + input + " not found in resourceInventory.");
                                    afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                    return null;
                            }
                        }
                        else
                        {
                            AFLog.d("[paramObjectSearch ERROR]: @param can get only 2 levels of variable hierarchy.");
                            afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                            return null;
                        }
                    }
                    else
                    {
                        AFLog.d("[paramObjectSearch ERROR]: resourceOrderItem doesn't contain resourceName.");
                        afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                        return null;
                    }

                case "resourceMaster":
                    String resourceIndex = afSubIns.getSubInitCmd();
                    ResourceMaster resourceMaster = Config.getResourceMasterHashMap().get(resourceIndex);
                    if(splittedInput.length == 2)
                    {
                        switch (splittedInput[1])
                        {
                            case "resourceIndex":
                                return resourceMaster.getResourceIndex();

                            case "mandatoryParam":
                                return resourceMaster.getMandatory();

                            case "validateParam":
                                return resourceMaster.getValidate();

                            case "suppCodesList":
                                return resourceMaster.getSuppCodesList();

                            case "rollbackFlag":
                                return resourceMaster.getRollbackFlag();

                            case "resourceRollbackThreshold":
                                return resourceMaster.getResourceRollbackThreshold();

                            case "responseToClient":
                                return resourceMaster.getResponseToClient();

                            case "effectiveDate":
                                return resourceMaster.getEffectiveDate();

                            case "expireDate":
                                return resourceMaster.getExpireDate();

                            case "createDate":
                                return resourceMaster.getCreateDate();

                            case "createBy":
                                return resourceMaster.getCreateBy();

                            case "lastUpdBy":
                                return resourceMaster.getLastUpdBy();

                            case "lastUpdDate":
                                return resourceMaster.getLastUpdDate();

                            case "description":
                                return resourceMaster.getDescription();

                            default:
                                AFLog.d("[paramObjectSearch ERROR]: " + input + " not found in resourceMaster.");
                                afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                return null;
                        }
                    }
                    else
                    {
                        AFLog.d("[paramObjectSearch ERROR]: @param can get only 2 levels of variable hierarchy.");
                        afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                        return null;
                    }

                case "resourceNeTypeProperty":
                    if(splittedInput.length == 2)
                    {
                        ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(afSubIns.getCurrentSuppcode());
                        ResourceNeTypeProperty resourceNeTypeProperty = Config.getResourceNeTypePropertyHashMap().get(resourceMappingCommand.getNeType());

                        switch (splittedInput[1])
                        {
                            case "neType":
                                return resourceNeTypeProperty.getNeType();

                            case "cType":
                                return resourceNeTypeProperty.getCType();

                            case "protocol":
                                return resourceNeTypeProperty.getProtocol();

                            case "descriptionEN":
                                return resourceNeTypeProperty.getDescriptionEN();

                            case "createDate":
                                return resourceNeTypeProperty.getCreateDate();

                            case "createBy":
                                return resourceNeTypeProperty.getCreateBy();

                            case "lastUpdBy":
                                return resourceNeTypeProperty.getLastUpdBy();

                            case "lastUpdDate":
                                return resourceNeTypeProperty.getLastUpdDate();

                            default:
                                AFLog.d("[paramObjectSearch ERROR]: " + input + " not found in resourceNeTypeProperty.");
                                afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                return null;
                        }
                    }
                    else
                    {
                        AFLog.d("[paramObjectSearch ERROR]: @param can get only 2 levels of variable hierarchy.");
                        afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                        return null;
                    }


                case "resourceProperty":
                    if(resourceOrderItemMap.containsKey("resourceName"))
                    {
                        ResourceProperty resourceProperty = Config.getResourcePropertyHashMap().get((String) resourceOrderItemMap.get("resourceName"));
                        if (splittedInput.length == 2)
                        {

                            switch (splittedInput[1])
                            {
                                case "resourceName":
                                    return resourceProperty.getResourceName();

                                case "preNeRouting":
                                    return resourceProperty.getPreNeRouting();

                                case "resourceRuleList":
                                    return resourceProperty.getResourceRuleList();

                                case "defValue":
                                    return resourceProperty.getDefValue();

                                case "searchKey":
                                    return resourceProperty.getSearchKey();

                                case "createDate":
                                    return resourceProperty.getCreateDate();

                                case "createBy":
                                    return resourceProperty.getCreateBy();

                                case "lastUpdBy":
                                    return resourceProperty.getLastUpdBy();

                                case "lastUpdDate":
                                    return resourceProperty.getLastUpdDate();

                                case "resourcePropertyDescriptionEN":
                                    return resourceProperty.getResourcePropertyDescriptionEN();

                                default:
                                    AFLog.d("[paramObjectSearch ERROR]: " + input + " not found in resourceProperty.");
                                    afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                    return null;
                            }

                        }
                        else
                        {
                            AFLog.d("[paramObjectSearch ERROR]: @param can get only 2 levels of variable hierarchy.");
                            afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                            return null;
                        }
                    }
                    else
                    {
                        AFLog.d("[paramObjectSearch ERROR]: resourceOrderItem doesn't contain resourceName.");
                        afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                        return null;
                    }

                case "resourceNeIdRouting":

                    ResourceNeIdRouting resourceNeIdRouting = afSubIns.getSubResourceNeidRoutingArray().get(afSubIns.getSubTaskNo());
                    if(splittedInput.length == 2)
                    {
                        switch (splittedInput[1])
                        {
                            case "neType":
                                return resourceNeIdRouting.getNeType();

                            case "start":
                                return resourceNeIdRouting.getStart();

                            case "end":
                                return resourceNeIdRouting.getEnd();

                            case "neId":
                                return resourceNeIdRouting.getNeId();

                            case "partyName":
                                return resourceNeIdRouting.getPartyName();

                            case "productVersion":
                                return resourceNeIdRouting.getProductVersion();

                            default:
                                AFLog.d("[paramObjectSearch ERROR]: " + input + " not found in resourceNeIdRouting.");
                                afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                return null;
                        }
                    }
                    else
                    {
                        AFLog.d("[paramObjectSearch ERROR]: @param can get only 2 levels of variable hierarchy.");
                        afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                        return null;
                    }

                case "task":
                    if(splittedInput.length > 2)
                    {
                        // splittedInput[0] == task
                        // splittedInput[1] == taskNo
                        // splittedInput[2] == varName
                        HashMap<String, Object> mappingResponse = afSubIns.getMappingResponse();
                        bld = new StringBuilder("task.");
                        if(mappingResponse.containsKey("Task" + splittedInput[1]))
                        {
                            tempMap = (Map<String, Object>) mappingResponse.get("Task" + splittedInput[1]);
                            bld.append(splittedInput[1]);
                            bld.append(".");
                            for(int i=2; i < splittedInput.length; i++)
                            {
                                bld.append(splittedInput[i]);
                                if(i == splittedInput.length - 1)
                                {
                                    if(tempMap.containsKey(splittedInput[i]))
                                    {
                                        return tempMap.get(splittedInput[i]);
                                    }
                                    else
                                    {
                                        AFLog.d("[paramObjectSearch ERROR]: Can't get param Task " + splittedInput[1] + ": " + bld.toString());
                                        afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                        return null;
                                    }
                                }
                                else
                                {
                                    // task.1.ChangeSubOfferingResult.ModifyOffering.OfferingKey.OfferingID
                                    if(foreachMap.containsKey(bld.toString()))
                                    {
                                        Foreach foreach = foreachMap.get(bld.toString());
                                        Object currentObjectFromArray = foreach.getArrayRef().get(foreach.getObjectIndex());
                                        if(currentObjectFromArray instanceof HashMap)
                                        {
                                            HashMap hashmap = (HashMap) currentObjectFromArray;
                                            if (hashmap.get(splittedInput[i]) instanceof LinkedTreeMap)
                                            {
                                                // get deeper item
                                                tempMap = (LinkedTreeMap) hashmap.get(splittedInput[i]);
                                                bld.append(".");
                                            } else if (hashmap.get(splittedInput[i]) instanceof HashMap)
                                            {
                                                // get deeper item
                                                tempMap = (HashMap) hashmap.get(splittedInput[i]);
                                                bld.append(".");
                                            }
                                        }
                                        else
                                        {
                                            AFLog.d("[paramObjectSearch ERROR]: " + bld.toString() + " is not an object in resourceOrderItem.");
                                            afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                            return null;
                                        }
                                    }
                                    else if(tempMap.get(splittedInput[i]) instanceof LinkedTreeMap)
                                    {
                                        // get deeper item
                                        tempMap = (LinkedTreeMap) tempMap.get(splittedInput[i]);
                                        bld.append(".");
                                    }
                                    else if(tempMap.get(splittedInput[i]) instanceof HashMap)
                                    {
                                        // get deeper item
                                        tempMap = (HashMap) tempMap.get(splittedInput[i]);
                                        bld.append(".");
                                    }
                                    else
                                    {
                                        AFLog.d("[paramObjectSearch ERROR]: " + bld.toString() + " is not an object in Task " + splittedInput[1] + ".");
                                        afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                                        return null;
                                    }
                                }
                            }
                        }
                        else
                        {
                            AFLog.d("[paramObjectSearch ERROR]: Task" + splittedInput[1] + " not found.");
                            afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                            return null;
                        }
                    }
                    else
                    {
                        AFLog.d("[paramObjectSearch ERROR]: Not enough param for Task.");
                        afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" missing mandatory "+input+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                        return null;
                    }

                default:
                    AFLog.e("[paramObjectSearch ERROR]: Table named " + splittedInput[0] + " not found.");
                    afSubIns.setProcessOutputMessageComplete(false);
                    afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" the "+input+" has invalid root node in "+afSubIns.getCurrentSuppcode()+".txt.");
                    return null;
            }
        }
        else
        {
            AFLog.e("[paramObjectSearch ERROR]: The input has only one level. Can't search object.");
            afSubIns.setProcessOutputMessageComplete(false);
            afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" the "+input+" is missing mandatory parameter name in "+afSubIns.getCurrentSuppcode()+".txt.");
            return null;
        }

        return null;
    }

    public static String processMagicString(String input, AFInstance afInstance, AFSubInstance afChildSubIns, Map<String, Foreach> foreachHashmap)
    {
        try
        {
            ArrayList<String> commandStack = nestedCommandSplit(input);
            ArrayList<String> replaceStack = new ArrayList<>();
            for (int stackIndex = 0; stackIndex < commandStack.size(); stackIndex++)
            {
                String magicString = commandStack.get(stackIndex);
                int startPos = magicString.indexOf("{@");
                int endPos = magicString.indexOf('}');

                /* if magic command string found */
                if (startPos != -1)
                {
                    String dataVar = magicString.substring(startPos + "{@".length(), endPos);
                    String replaceVar = "";
                    if(!(dataVar.contains(":"))) {
                        AFLog.e("[ProcessMagicString ERROR]: Config fail ':' missing");
                        afChildSubIns.setProcessOutputMessageComplete(false);
                        afChildSubIns.setErrorMessage(afChildSubIns.getSubResourceName()+"("+afChildSubIns.getSubResourceItemNo()+")"+" "+dataVar+" command is invalid in "+afChildSubIns.getCurrentSuppcode()+".txt.");
                        break;
                    }

                    String splittedDataVar0 = dataVar.substring(0,dataVar.indexOf(':'));
                    String splittedDataVar1 = dataVar.substring(dataVar.indexOf(':')+1);
                    splittedDataVar0 = splittedDataVar0.trim();
                    splittedDataVar1 = splittedDataVar1.trim();

                    switch (splittedDataVar0)
                    {
                        case "param":
                            Object param = RequestTemplateProcessor.paramObjectSearch(splittedDataVar1, afInstance, afChildSubIns, foreachHashmap);
                            if(param == null)
                            {
                                replaceVar = "";
                            }
                            else if(param instanceof String)
                            {
                                replaceVar = param.toString();
                            }
                            else
                            {
                                replaceVar = null;
                            }
                            break;
                        case "overwrite_def":
                            replaceVar = Overwrite.overwriteDef(splittedDataVar1, afChildSubIns);
                            break;
                        case "overwrite_extra":
                            replaceVar = Overwrite.overwriteExtra(splittedDataVar1, afChildSubIns);
                            break;
                        case "param_foreach":
                            String[] splittedDotForeachToken = splittedDataVar1.split("\\.");
                            if(splittedDotForeachToken.length > 1)
                            {
                                StringBuilder combinedForeachKey = new StringBuilder(splittedDotForeachToken[0]);
                                String finalForeachKey = null;
                                for (int foreachStackIndex = 1; foreachStackIndex < splittedDotForeachToken.length - 1; foreachStackIndex++)
                                {
                                    combinedForeachKey.append(".");
                                    combinedForeachKey.append(splittedDotForeachToken[foreachStackIndex]);
                                    if(foreachHashmap.containsKey(combinedForeachKey.toString()))
                                    {
                                        finalForeachKey = combinedForeachKey.toString();
                                    }
                                }

                                if (foreachHashmap.containsKey(splittedDataVar1))
                                {
                                    // if arrayList is a list of String
                                    Foreach lastForeach = foreachHashmap.get(splittedDataVar1);
                                    ArrayList array = (ArrayList) lastForeach.getArrayRef();
                                    replaceVar = (String) array.get(lastForeach.getObjectIndex());
                                }
                                else if (foreachHashmap.containsKey(combinedForeachKey.toString()))
                                {
                                    Foreach lastForeach = foreachHashmap.get(combinedForeachKey.toString());
                                    ArrayList array = (ArrayList) lastForeach.getArrayRef();
                                    // if ArrayList is a list of object

                                    Map<String, String> map = (Map<String, String>) array.get(lastForeach.getObjectIndex());
                                    String remainingKey = splittedDataVar1.replace(combinedForeachKey.toString() + ".","");
                                    if (map.containsKey(remainingKey))
                                    {
                                        replaceVar = map.get(remainingKey);
                                    }
                                    else
                                    {
                                        AFLog.d("[@param_foreach ERROR]: foreach_param not found: " + splittedDataVar1);
                                        return null;
                                    }
                                }
                                else if (foreachHashmap.containsKey(finalForeachKey))
                                {
                                    // get string from object that inside an object in arraylist
                                    Foreach lastForeach = foreachHashmap.get(finalForeachKey);
                                    ArrayList array = (ArrayList) lastForeach.getArrayRef();
                                    Map map = (Map) array.get(lastForeach.getObjectIndex());
                                    String[] remainingKeys = splittedDataVar1.replace(finalForeachKey + ".","").split("\\.");

                                    //get deeper object
                                    for(int mapKeyIndex = 0; mapKeyIndex < remainingKeys.length; mapKeyIndex++)
                                    {
                                        if(map.containsKey(remainingKeys[mapKeyIndex]) && map.get(remainingKeys[mapKeyIndex]) instanceof HashMap)
                                        {
                                            map = (HashMap) map.get(remainingKeys[mapKeyIndex]);
                                        }
                                        else if(map.containsKey(remainingKeys[mapKeyIndex]) && map.get(remainingKeys[mapKeyIndex]) instanceof LinkedTreeMap)
                                        {
                                            map = (LinkedTreeMap) map.get(remainingKeys[mapKeyIndex]);
                                        }
                                        else if(map.containsKey(remainingKeys[mapKeyIndex]) && mapKeyIndex == remainingKeys.length - 1 && map.get(remainingKeys[mapKeyIndex]) instanceof String)
                                        {
                                            replaceVar = (String) map.get(remainingKeys[mapKeyIndex]);
                                        }
                                        else
                                        {
                                            AFLog.d("[@param_foreach ERROR]: foreach_param not found: " + splittedDataVar1);
                                            return null;
                                        }
                                    }
                                }
                                else
                                {
                                    AFLog.d("[@param_foreach ERROR]: Foreach hierarchy " + combinedForeachKey.toString() + " not found.");
                                    return null;
                                }
                            }
                            else
                            {
                                AFLog.d("[@param_foreach ERROR]: Foreach hierarchy must be more than 1 level.");
                                return null;
                            }
                            break;
                        case "index_foreach":
                            if(foreachHashmap.containsKey(splittedDataVar1))
                            {
                                return Integer.toString(foreachHashmap.get(splittedDataVar1).getObjectIndex());
                            }
                            else
                            {
                                AFLog.d("[@index_foreach ERROR]: Foreach not found: " + splittedDataVar1);
                                return null;
                            }

                        default:
                            AFLog.e("[ProcessMagicString ERROR]: Command not found: " + splittedDataVar0);
                            afChildSubIns.setProcessOutputMessageComplete(false);
                            afChildSubIns.setErrorMessage(afChildSubIns.getSubResourceName()+"("+afChildSubIns.getSubResourceItemNo()+")"+": Invalid feature "+splittedDataVar0+" in "+afChildSubIns.getCurrentSuppcode()+".txt.");
                            return null;
                    }

                    replaceStack.add(replaceVar);

                    // replace this token for all higher indexes
                    for (int commandIndex = stackIndex + 1; commandIndex < commandStack.size(); commandIndex++)
                    {
                        if (commandStack.get(commandIndex).contains(commandStack.get(stackIndex)))
                        {
                            commandStack.set(commandIndex, commandStack.get(commandIndex).replace(commandStack.get(stackIndex), replaceVar));
                        }
                    }

                }
            }

            for (int commandStackIndex = 0; commandStackIndex < commandStack.size(); commandStackIndex++)
            {
                input = input.replace(commandStack.get(commandStackIndex), replaceStack.get(commandStackIndex));
            }
            return input;
        }
        catch (Exception ex)
        {
            AFLog.e("[Exception] ProcessMagicString: " + ex.getMessage());
            AFLog.e(ex);
            return null;
        }
    }

    public static Foreach findLastForeachEntry(Map<String, Foreach> linkedMap) {
        List<Map.Entry<String, Foreach>> entryList = new ArrayList<>(linkedMap.entrySet());
        if(entryList.isEmpty())
        {
            return new Foreach("", 0,0,true,null);
        }
        else
        {
            return entryList.get(entryList.size() - 1).getValue();
        }
    }

    private static ArrayList<String> nestedCommandSplit(String s){
        // The splits.
        ArrayList<String> split = new ArrayList<>();
        // The stack.
        ArrayList<Integer> stack = new ArrayList<>();
        // Walk the string.
        for (int i = 0; i < s.length(); i++)
        {
            // Get the char there.
            char ch = s.charAt(i);
            char ch2 = '\0';
            if(i < s.length() - 1)
            {
                ch2 = s.charAt(i + 1);
            }


            // Is it an open brace?
            //int o = open.indexOf(ch);
            // Is it a close brace?
            //int c = close.indexOf(ch);

            // Is it an open brace?
            if (ch == '{' && ch2 == '@')
            {
                // Its an open! Push it.
                stack.add(i);
            }
            // Is it a close brace?
            else if (ch == '}' && !stack.isEmpty())
            {
                // Pop (if matches).
                int tosPos = stack.size() - 1;
                Integer tos = stack.get(tosPos);
                // Does the brace match?
                //if (tos.brace == c)
                //{
                // Matches!
                split.add(s.substring(tos, i + 1));
                // Done with that one.
                stack.remove(tosPos);
                //}
            }
        }

        if(!stack.isEmpty())
        {
            AFLog.d("[ProcessMagicString ERROR]: '}' not balance.");
        }

        return split;
    }

    public static Boolean processIfLogic(String functionName, String functionParam, AFInstance afInstance, AFSubInstance afSubIns, Map<String, Foreach> foreachStack)
    {
        switch (functionName)
        {
            case "existParam":
                return existParam(functionParam,afInstance,afSubIns, foreachStack);
            case "existSomeChildParams":
                return existSomeChildParams(functionParam,afInstance,afSubIns, foreachStack);
            case "checkStrEqual":
            	return checkStrEqual(functionParam,afInstance,afSubIns, foreachStack);
            case "checkStrAndEqual":
            	return checkStrAndEqual(functionParam,afInstance,afSubIns, foreachStack);
            case "checkStrOrEqual":
            	return checkStrOrEqual(functionParam,afInstance,afSubIns, foreachStack);
            case "equalArrayIndex":
                return equalArrayIndex(functionParam,foreachStack);
            case "checkForeachIndexEqual":
                return checkForeachIndexEqual(functionParam,foreachStack);
            default:
                AFLog.e("[processIfLogic ERROR]: function named \"" + functionName + "\" in @if is undefined.");
                afSubIns.setProcessOutputMessageComplete(false);
                afSubIns.setErrorMessage(afSubIns.getSubResourceName()+"("+afSubIns.getSubResourceItemNo()+")"+" cannot find function "+functionName+" in "+afSubIns.getCurrentSuppcode()+".txt.");
                return false;
        }
    }

    private static Boolean existParam(String varName, AFInstance afInstance, AFSubInstance afSubInstance, Map<String, Foreach> foreachStack)
    {
    	return paramObjectSearch(varName, afInstance, afSubInstance, foreachStack) != null && !paramObjectSearch(varName, afInstance, afSubInstance, foreachStack).equals("") ? true : false;
    }

    private static Boolean existSomeChildParams(String varsName, AFInstance afInstance, AFSubInstance afSubInstance, Map<String, Foreach> foreachStack)
    {
        String[] varsNameSplitted = varsName.split(",");
        Boolean temp = false;
        for (String varName: varsNameSplitted)
        {
        	temp = temp || (paramObjectSearch(varName, afInstance, afSubInstance, foreachStack) != null && !paramObjectSearch(varName, afInstance, afSubInstance, foreachStack).equals("")) ? true : false;
        }
        return temp;
    }


    private static Boolean checkStrEqual(String varName, AFInstance afInstance, AFSubInstance afSubInstance, Map<String, Foreach> foreachStack)
    {
    	Boolean temp = false;
    	String errorMessage = "";
    	//-- if flag of process message is true, method will process. --//
    	if(afSubInstance.isProcessOutputMessageComplete()) {
    		if(varName.matches("\\<.+\\>|\\<.+|.+\\>")){
				errorMessage = "checkStrEqual cannot use \"<\" or \">\" in " + afSubInstance.getCurrentSuppcode() + ".txt.";
    			AFLog.e("[Error] "+errorMessage);
				afSubInstance.setErrorMessage(afSubInstance.getSubResourceName()+"("+afSubInstance.getSubResourceItemNo()+") "+errorMessage);
				afSubInstance.setProcessOutputMessageComplete(false);
				return temp;
	    	} else {
	        	String [] splitStringConfig = varName.split(",", 2);
	            temp = checkParamWithConfig(splitStringConfig, afInstance, afSubInstance, foreachStack);
	    	}
    		return temp;
    	} else {
    		return false;
    	}
    }

    private static Boolean checkStrAndEqual(String varName, AFInstance afInstance, AFSubInstance afSubInstance, Map<String, Foreach> foreachStack)
    {
    	Boolean temp = true;
    	String errorMessage = "";
    	//-- if flag of process message is true, method will process. --//
    	if(afSubInstance.isProcessOutputMessageComplete()) {
    		//-- if config have "<" at start and ">" at end of config, method will process. --//
    		if(varName.matches("\\<.*\\>")) {
    			varName = varName.replaceAll("^\\<|\\>$", "");
    			String [] splitAnd = varName.split("\\>\\&\\&\\<");
	        	for (int i = 0; i < splitAnd.length ; i++){
	        		if(splitAnd[i].matches(".*\\>.*\\<.*")){ //-- if there're another delimiter, method will not process. --//
	        			errorMessage = "checkStrAndEqual must use \"&&\" operator in " + afSubInstance.getCurrentSuppcode() + ".txt.";
	        			AFLog.e("[Error] "+errorMessage);
	        			afSubInstance.setErrorMessage(afSubInstance.getSubResourceName()+"("+afSubInstance.getSubResourceItemNo()+") "+errorMessage);
	        			afSubInstance.setProcessOutputMessageComplete(false);
	        			return false;
	        		} else {
	        			String [] splitStringConfig = splitAnd[i].split(",", 2);
	    	    		temp = temp && checkParamWithConfig(splitStringConfig, afInstance, afSubInstance, foreachStack);
	        		}
	        	}
	        	return temp;
    		} else {
    			errorMessage = "checkStrAndEqual unbalances \"<\" or \">\" in " + afSubInstance.getCurrentSuppcode() + ".txt.";
    			AFLog.e("[Error] "+errorMessage);
    			afSubInstance.setErrorMessage(afSubInstance.getSubResourceName()+"("+afSubInstance.getSubResourceItemNo()+") "+errorMessage);
    			afSubInstance.setProcessOutputMessageComplete(false);
    			return false;
    		}
    	} else {
    		return false;
    	}
    }

    private static Boolean checkStrOrEqual(String varName, AFInstance afInstance, AFSubInstance afSubInstance, Map<String, Foreach> foreachStack)
    {
    	Boolean temp = false;
    	String errorMessage = "";
    	//-- if flag of process message is true, method will process. --//
    	if(afSubInstance.isProcessOutputMessageComplete()) {
    		//-- if config have "<" at start and ">" at end of config, method will process. --//
    		if(varName.matches("\\<.*\\>")) {
    			varName = varName.replaceAll("^\\<|\\>$", "");
    			String [] splitOr = varName.split("\\>\\|\\|\\<");
	        	for (int i = 0; i < splitOr.length ; i++){
	        		if(splitOr[i].matches(".*\\>.*\\<.*")){ //-- if there're another delimiter, method will not process. --//
	        			errorMessage = "checkStrOrEqual must use \"||\" operator in " + afSubInstance.getCurrentSuppcode() + ".txt.";
	        			AFLog.e("[Error] "+errorMessage);
	        			afSubInstance.setErrorMessage(afSubInstance.getSubResourceName()+"("+afSubInstance.getSubResourceItemNo()+") "+errorMessage);
	        			afSubInstance.setProcessOutputMessageComplete(false);
	        			return false;
	        		} else {
	        			String [] splitStringConfig = splitOr[i].split(",", 2);
	    	    		temp = temp || checkParamWithConfig(splitStringConfig, afInstance, afSubInstance, foreachStack);
	        		}
	        	}
	        	return temp;
    		} else {
    			errorMessage = "checkStrOrEqual unbalances \"<\" or \">\" in " + afSubInstance.getCurrentSuppcode() + ".txt.";
    			AFLog.e("[Error] "+errorMessage);
    			afSubInstance.setErrorMessage(afSubInstance.getSubResourceName()+"("+afSubInstance.getSubResourceItemNo()+") "+errorMessage);
    			afSubInstance.setProcessOutputMessageComplete(false);
    			return false;
    		}
    	} else {
    		return false;
    	}
    }

    public static Boolean checkParamWithConfig(String[] strSplit, AFInstance afInstance, AFSubInstance afSubInstance, Map<String, Foreach> foreachStack){
    	Boolean methodResult;
    	String parameter = strSplit[0];
    	//-- if flag of process message is true, method will process. --//
    	if(afSubInstance.isProcessOutputMessageComplete()) {
	    	strSplit[0] = (String) paramObjectSearch(strSplit[0], afInstance, afSubInstance, foreachStack);
			//-- If config has value for check. --//
	    	if(strSplit.length > 1){
	    		//-- case match with config. --//
	    		if(strSplit[0] != null && strSplit[0].equals(strSplit[1])){
	    			methodResult = true;
	    			AFLog.d("Value of parameter \"" + parameter + "\" is match with configs \"" + strSplit[1] + "\" in " + afSubInstance.getCurrentSuppcode() + ".txt.");
	    	    } //-- case match with config for empty string. --//
	    		else if (strSplit[0] != null && strSplit[0].equals("") && strSplit[1].equals("\"\"")){
	    			methodResult = true;
	    			AFLog.d("Value of parameter \"" + parameter + "\" is \"\" so it is match with configs for empty string in " + afSubInstance.getCurrentSuppcode() + ".txt.");
	    		} //-- case match with config for no value. --//
	    		else if (strSplit[0] == null && strSplit[1].equals("NOVALUE")){
	    			methodResult = true;
	    			AFLog.d("Value of parameter \"" + parameter + "\" is null so it is match with configs \"" + strSplit[1] + "\" in " + afSubInstance.getCurrentSuppcode() + ".txt.");
	    		} else {
	    			methodResult = false;
		    	}
	        //-- If config has no value for check --//
	    	} else {
	    		//-- case value of parameter is empty string. --//
	    		if(strSplit[0] != null && strSplit[0].equals("")){
	    			methodResult = true;
	    			AFLog.d("Value of parameter \"" + parameter + "\" is empty string so it is match with configs def for empty string in " + afSubInstance.getCurrentSuppcode() + ".txt.");
	    		} else {
	    			methodResult = false;
	    		}
	    	}
	    	return methodResult;
    	} else {
    		return false;
    	}
    }

    // equalArrayIndex
    // Check if 2 specified Foreachs has the same index number.
    // Parameters: 2
    // Input: foreachName, foreachName
    private static Boolean equalArrayIndex(String foreachNames, Map<String, Foreach> foreachStack)
    {
        String[] foreachSplitted = foreachNames.split(",");
        if(foreachSplitted.length == 2)
        {
            if(foreachStack.containsKey(foreachSplitted[0]) && foreachStack.containsKey(foreachSplitted[1]))
            {
                return foreachStack.get(foreachSplitted[0]).getObjectIndex() == foreachStack.get(foreachSplitted[1]).getObjectIndex();
            }
            else
            {
                if(!foreachStack.containsKey(foreachSplitted[0]))
                {
                    AFLog.d("[equalArrayIndex ERROR]: Foreach \"" + foreachSplitted[0] + "\" not found.");
                }
                else
                {
                    AFLog.d("[equalArrayIndex ERROR]: Foreach \"" + foreachSplitted[1] + "\" not found.");
                }
                return false;
            }
        }
        else
        {
            AFLog.d("[equalArrayIndex ERROR]: Invalid function parameters.");
            return false;
        }
    }

    // checkForeachIndexEqual
    // Check if specified Foreach has current index at specified number
    // Parameters: 2
    // Input: foreachName, foreachIndex
    private static Boolean checkForeachIndexEqual(String foreachNames, Map<String, Foreach> foreachStack)
    {
        Validator validator = new Validator();
        String[] foreachSplitted = foreachNames.split(",");
        if(foreachSplitted.length == 2)
        {
            if(foreachStack.containsKey(foreachSplitted[0]) && validator.isNumeric(foreachSplitted[1]))
            {
                return foreachStack.get(foreachSplitted[0]).getObjectIndex() == Integer.parseInt(foreachSplitted[1]);
            }
            else if(!foreachStack.containsKey(foreachSplitted[0]))
            {
                AFLog.d("[checkForeachIndexEqual ERROR]: Foreach \"" + foreachSplitted[0] + "\" not found.");
                return false;
            }
            else if(!validator.isNumeric(foreachSplitted[1]))
            {
                AFLog.d("[checkForeachIndexEqual ERROR]: " + foreachSplitted[1] + " is not a number.");
                return false;
            }
            else
            {
                AFLog.d("[checkForeachIndexEqual ERROR]: Unknown Error.");
                return false;
            }
        }
        else
        {
            AFLog.d("[checkForeachIndexEqual ERROR]: Invalid function parameters.");
            return false;
        }
    }
}
