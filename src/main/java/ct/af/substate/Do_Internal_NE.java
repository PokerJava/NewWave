package ct.af.substate;

import com.google.gson.internal.LinkedTreeMap;
import ct.af.enums.EResultCode;
import ct.af.instance.*;
import ct.af.resourceModel.ResourceMappingCommand;
import ct.af.utils.Config;
import ct.af.utils.RequestTemplateProcessor;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import java.util.*;
import org.apache.commons.lang3.StringUtils;

public class Do_Internal_NE
{
    public void validateAndProcess(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns)
    {
        String suppcode = afSubIns.getCurrentSuppcode();
        ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);

        afSubIns.setSubResourceItemNo(Integer.parseInt(afSubIns.getSubInstanceNo().replaceAll("\\D", "")));
        
        if(resourceMappingCommand.getMessageFormat().equalsIgnoreCase("xml"))
        {
            xmlMessage(afInstance, afSubIns);
        }
        else if(resourceMappingCommand.getMessageFormat().equalsIgnoreCase("json"))
        {
            jsonMessage(afInstance, afSubIns);
        }
        else if(resourceMappingCommand.getMessageFormat().equalsIgnoreCase("cmdline"))
        {
            commandLineMessage(afInstance, afSubIns,false);
        }
        else if(resourceMappingCommand.getMessageFormat().equalsIgnoreCase("cmdlinebase64"))
        {
            commandLineMessage(afInstance, afSubIns,true);
        }
        else if(resourceMappingCommand.getMessageFormat().equalsIgnoreCase("httpget"))
        {
            httpGetMessage(afInstance, afSubIns);
        }
    }
    private List<String> processMessage(ArrayList<String> line, AFInstance afInstance, AFSubInstance afSubIns)
    {
        AFLog.d("Starting processing template message...");

        PoolTask poolTask = new PoolTask();
        poolTask.setSuppCode(afSubIns.getCurrentSuppcode());
		poolTask.setNeId(afSubIns.getSubNeId());
		
        String suppcode = afSubIns.getCurrentSuppcode();
        AFLog.d("suppcode : "+ suppcode);

        ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);

        List<String> dataList = new ArrayList<>();
        String dataVar;
        String endData;

        ArrayList<If> ifStack = new ArrayList<>();
        LinkedHashMap<String, Foreach> foreachHashMap = new LinkedHashMap<>();
        Boolean calculatedTemp = true;

        String errmsg="";
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
                        AFLog.w("[ERROR]: Invalid @if token (full line: " + dataSoap + ")");
                        afSubIns.setProcessOutputMessageComplete(false);
                        break;
                    }
                    continue;
                }
                else
                {
                    AFLog.w("[ERROR]: Invalid @if token (full line: " + dataSoap + ")");
                    afSubIns.setProcessOutputMessageComplete(false);
                    break;
                }
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
                        } else
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
                        AFLog.w("[ERROR]: Invalid @elseif token (full line: " + dataSoap + ")");
                        afSubIns.setProcessOutputMessageComplete(false);
                        break;
                    }
                    continue;
                }
                else
                {
                    AFLog.w("[ERROR]: Invalid @elseif token (full line: " + dataSoap + ")");
                    afSubIns.setProcessOutputMessageComplete(false);
                    break;
                }
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
                            AFLog.w("[@foreach ERROR]: " + splittedDotForeachToken[0] + " is not an array.");
                            isForeachEmpty = true;
                        }
                        foreachHashMap.put(splittedForeachToken[0], new Foreach(splittedForeachToken[0], i, 0, isForeachEmpty, tempArray));
                    }
                    else if(splittedDotForeachToken.length > 2)
                    {
                        StringBuilder combinedForeachKey = new StringBuilder(splittedDotForeachToken[0]);
                        for (int foreachKeyIndex = 1; foreachKeyIndex < splittedDotForeachToken.length - 1; foreachKeyIndex++)
                        {
                            combinedForeachKey.append(".");
                            combinedForeachKey.append(splittedDotForeachToken[foreachKeyIndex]);
                        }
                        AFLog.d("Combined Upper Foreach Key: " + combinedForeachKey.toString());
                        if(foreachHashMap.containsKey(combinedForeachKey.toString()))
                        {
                            Foreach foreach = foreachHashMap.get(combinedForeachKey.toString());
                            if(foreach.isForeachEmpty())
                            {
                                AFLog.d("[@foreach]: " + combinedForeachKey.toString() + " is empty so this foreach \"" + splittedForeachToken[0] + "\" is empty too.");
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
                                        AFLog.w("[@foreach ERROR]: " + splittedDotForeachToken[0] + " is not an array in an object that has index at " + foreach.getObjectIndex() + ".");
                                        isForeachEmpty = true;
                                    }
                                    foreachHashMap.put(splittedForeachToken[0], new Foreach(splittedForeachToken[0], i, 0, isForeachEmpty, tempArray));
                                }
                                else
                                {
                                    AFLog.w("[@foreach ERROR]: " + splittedDotForeachToken[0] + " not found in an object that has index at " + foreach.getObjectIndex() + ".");
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
                                AFLog.w("[@foreach ERROR]: " + splittedForeachToken[0] + " is not an array or " + combinedForeachKey.toString() + " not found in Foreach stack.  Maybe it's in wrong hierarchy.");
                                isForeachEmpty = true;
                            }

                            foreachHashMap.put(splittedForeachToken[0],new Foreach(splittedForeachToken[0], i, 0, isForeachEmpty, tempArray));
                        }
                    }
                }
                else
                {
                    AFLog.w("[@foreach ERROR]: Invalid @foreach token (full line: " + dataSoap + ")");
                    afSubIns.setProcessOutputMessageComplete(false);
                }
                continue;
            }
            else if(dataSoap.contains("@endforeach"))
            {
                if(!foreachHashMap.isEmpty())
                {
                    Foreach lastForeachInStack = RequestTemplateProcessor.findLastForeachEntry(foreachHashMap);
                    if(!lastForeachInStack.isForeachEmpty() && lastForeachInStack.getObjectIndex() < lastForeachInStack.getArrayRef().size() - 1)
                    {
                        i = lastForeachInStack.getStartLinePosition();
                        lastForeachInStack.setObjectIndex(lastForeachInStack.getObjectIndex() + 1);
                        if(resourceMappingCommand.getMessageFormat().equalsIgnoreCase("json"))
                        {
                            dataList.set(dataList.size() - 1, dataList.get(dataList.size() - 1) + ",");
                        }
                    }
                    else
                    {
                        foreachHashMap.remove(lastForeachInStack.getName());
                    }
                }
                continue;
            }

            /* About this if logic

            A ifStack.isEmpty() (ifStack ว่าง)
            B calculatedTemp (ค่าที่ได้จากการคำนวน)
            C foreachHashMap.isEmpty() (1 = ไม่มี foreach ในระบบ, 0 = มี foreach ทำงานในระบบ)
            D RequestTemplateProcessor.findLastForeachEntry(foreachHashMap).isForeachEmpty() (1 = foreach สุดท้ายว่าง, 0 = foreach สุดท้ายไม่ว่าง)

            Truth Table
            A B C D
            0 0 0 0 = 0
            0 0 0 1 = 0
            0 0 1 0 = 0
            0 0 1 1 = 0
            0 1 0 0 = 1
            0 1 0 1 = 0
            0 1 1 0 = 1
            0 1 1 1 = 1
            1 0 0 0 = 1
            1 0 0 1 = 0
            1 0 1 0 = 1
            1 0 1 1 = 1
            1 1 0 0 = 1
            1 1 0 1 = 0
            1 1 1 0 = 1
            1 1 1 1 = 1

            convert to K-Map will get this if
             */
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
                    if(afSubIns.isProcessOutputMessageComplete()) {
	                    if ("".equals(dataVar) || dataVar == null)
	                    {
	                        if ((startData.contains("((")) && (endData.contains("))")))
	                        {
	                            dataVar = "";
	                            dataSoap = startData + dataVar + endData;
	                            // careData.replace("((", "").replace("))", "");
	                        } else if ((startData.contains("(")) && (endData.contains(")")) && ((endData.startsWith(")\""))||(endData.startsWith(")<"))))
	                        {
	
	                            dataSoap = "";
	
	                        } else if ((startData.contains("(")) && (endData.contains(")")))
	                        {
	
	                            dataSoap = startData + endData;
	
	                        } else
	                        {
	                        	errmsg=" (key: " + dataSoap + ").";
	                            AFLog.w("[ERROR]: Mandatory is missing (key: " + dataSoap + ").");
	                            afSubIns.setProcessOutputMessageComplete(false);
	                            break;
	                        }
	                    } else
	                    {
	                        dataSoap = startData + dataVar + endData;
	                    }
                    }else {
                    	break;
                    }
                }


                dataSoap = dataSoap.replace("(", "").replace(")", "").replace("&#40;","(").replace("&#41;",")");
                if(!dataSoap.equals(""))
                {
                    dataList.add(dataSoap);
                }
            }
        }
        //
        
        poolTask.setsMessage((StringUtils.isBlank(afSubIns.getErrorMessage())?"":(afSubIns.getErrorMessage()+"; "))
        		+EResultCode.RE40300.getResultCode()+" "+EResultCode.RE40300.getResultDesc()+errmsg);
		poolTask.setStatus(EResultCode.RE40300.getResultStatus());
		poolTask.setResultCode(EResultCode.RE40300.getResultCode());
		if(afSubIns.isErrorHandling()) {
			afSubIns.getMappingPoolTask().put("Task"+(afSubIns.getSubTaskNo()+1)+"-"+afSubIns.getSubTaskErrorHandling(), poolTask);
		}else {
			afSubIns.getMappingPoolTask().put("Task"+(afSubIns.getSubTaskNo()+1), poolTask);
		}
        
		return dataList;
    }

    private void jsonMessage(AFInstance afInstance, AFSubInstance afSubIns)
    {
        String suppcode = afSubIns.getCurrentSuppcode();
        ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);
        List<String> dataList = processMessage(resourceMappingCommand.getRequestMessage(), afInstance,afSubIns);
        String respStr = "";
        for (int i = 0; i < dataList.size(); i++) {
            if (!(dataList.get(i)).equals("")) {

                respStr = respStr.concat(dataList.get(i) + '\n');

            }
        }
        AFLog.d("OutputMessage: " + respStr);

        afSubIns.setRequestMessage(respStr);

        if(resourceMappingCommand.getRequestURLMessage().size() != 0)
        {
            buildURLMessage(resourceMappingCommand.getRequestURLMessage(), afInstance, afSubIns);
        }
    }

    private void commandLineMessage(AFInstance afInstance,AFSubInstance afSubIns, boolean encodeInBase64)
    {
        String suppcode = afSubIns.getCurrentSuppcode();
        ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);
        List<String> dataList = processMessage(resourceMappingCommand.getRequestMessage(), afInstance,afSubIns);
        String respStr = "";
        for (int i = 0; i < dataList.size(); i++) {
            if (!(dataList.get(i)).equals("")) {

                respStr = respStr.concat(dataList.get(i));

            }
        }

        respStr += "\r\n";
        AFLog.d("OutputMessage: " + respStr);

        if(encodeInBase64)
        {
            byte[] bytes = respStr.getBytes();
            respStr = Base64.getEncoder().encodeToString(bytes);
        }


        afSubIns.setRequestMessage(respStr);
    }

    private void xmlMessage(AFInstance afInstance, AFSubInstance afSubIns) {

        String suppcode = afSubIns.getCurrentSuppcode();
        ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);
        List<String> dataList = processMessage(resourceMappingCommand.getRequestMessage(), afInstance,afSubIns);
        String respStr = "";
        String dataTag = "";

        for (int i = 0; i < dataList.size(); i++) {
            String tempTag = (dataList.get(i)).replace("<", "").replace(">", "").replace("/", "");

            if (!dataTag.equals("") && tempTag.equals(dataTag) && (i > 0 && !dataList.get(i - 1).contains("/")))
            {
                dataList.set(i, "");
                dataList.set(i - 1, "");
            }
            dataTag = tempTag;
        }

        for (int i = 0; i < dataList.size(); i++) {
            if (!(dataList.get(i)).equals("")) {

                respStr = respStr.concat(dataList.get(i));
            }
        }

//		respStr = mainSoap.concat(respStr).concat(endSoap);
//
//		reqMessage = respStr;

        AFLog.d("OutputMessage: " + respStr);

        afSubIns.setRequestMessage(respStr);

        if(resourceMappingCommand.getRequestURLMessage().size() != 0)
        {
            buildURLMessage(resourceMappingCommand.getRequestURLMessage(), afInstance, afSubIns);
        }
    }

    private void httpGetMessage(AFInstance afInstance, AFSubInstance afSubIns)
    {
        String suppcode = afSubIns.getCurrentSuppcode();
        ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);
        List<String> dataList = processMessage(resourceMappingCommand.getRequestMessage(), afInstance,afSubIns);
        String respStr = "";

        for (int i = 0; i < dataList.size(); i++) {
            if (!(dataList.get(i)).equals("")) {
                respStr = respStr.concat(dataList.get(i));
            }
        }
        AFLog.d("OutputMessage: " + respStr);

        afSubIns.setRequestMessage(respStr);

        if(resourceMappingCommand.getRequestURLMessage().size() != 0)
        {
            buildURLMessage(resourceMappingCommand.getRequestURLMessage(), afInstance, afSubIns);
        }
    }

    private void buildURLMessage(ArrayList<String> line, AFInstance afInstance, AFSubInstance afSubIns)
    {
        List<String> dataList = processMessage(line, afInstance,afSubIns);
        StringBuilder urlBuilder = new StringBuilder();

        for (int i = 0; i < dataList.size(); i++) {
            if (!(dataList.get(i)).equals("")) {
                urlBuilder.append(dataList.get(i));
            }
        }
        AFLog.d("URLMessage: " + urlBuilder.toString());

        afSubIns.setRequestURLMessage(urlBuilder.toString());
    }
}
