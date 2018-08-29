package ct.af.message.outgoing;

import ct.af.enums.*;
import ct.af.instance.*;
import ct.af.resourceModel.ResourceMappingCommand;
import ct.af.resourceModel.ResourceNeTypeProperty;
import ct.af.utils.*;
import ct.af.utils.AFUtils;
import ct.af.utils.Config;
import ec02.af.abstracts.AbstractAF;
import ec02.af.data.AFDataFactory;
import ec02.af.utils.AFLog;
import ec02.data.enums.EEquinoxRawData;
import ec02.data.interfaces.EquinoxRawData;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public class Out_NE {

	public EquinoxRawData messageBuilder(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {
		String suppcode = afSubIns.getCurrentSuppcode();

        ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);
        ResourceNeTypeProperty resourceNeTypeProperty = Config.getResourceNeTypePropertyHashMap().get(resourceMappingCommand.getNeType());

		String cType = resourceNeTypeProperty.getCType();
		String protocol = resourceNeTypeProperty.getProtocol();
		String timeoutconf = resourceMappingCommand.getTimeOut();
		String url = resourceMappingCommand.getUrl();
		String soapAction = resourceMappingCommand.getSOAPAction();
		String neId = afSubIns.getSubNeId();
		String mainIns = (String) afInstance.getMainSubInstance().keySet().toArray()[0];
		
		afInstance.getMainSubInstance().get(mainIns).setState(ERequestState.INPROGRESS.getState());
		AFLog.d("state : " + afInstance.getMainSubInstance().get(mainIns).getState());

		url = url + afSubIns.getRequestURLMessage();

        // -- Construct EquinoxRawData --//
        EquinoxRawData eqxRawData = AFDataFactory.createEquinoxRawData();
        DateTime timeStampOut = new DateTime();
        Map<String, String> map = new HashMap<>();
        map.put(EEqxMsg.TYPE.getEqxMsg(), EEventType.REQUEST.getEventType());
        map.put(EEqxMsg.CTYPE.getEqxMsg(), cType);
        map.put(EEqxMsg.PROTOCOL.getEqxMsg(), protocol);
        map.put(EEqxMsg.URL.getEqxMsg(), url);
        map.put(EEqxMsg.METHOD.getEqxMsg(), EMethod.POST.toString());
        map.put(EEqxMsg.SOAPACTION.getEqxMsg(), soapAction);
        map.put(EEqxMsg.VAL.getEqxMsg(), "");
        MessageBuilder builder = new MessageBuilder("");

        if(cType.equalsIgnoreCase("tcp")) {
            map.put(EEqxMsg.VAL.getEqxMsg(), afSubIns.getRequestMessage());
        }else if(cType.equalsIgnoreCase("text/xml")||cType.equalsIgnoreCase("text/plain")){
            builder = new MessageBuilder(afSubIns.getRequestMessage());
        }

        String invoke="";
		// -- Invoke --//
        if(afSubIns.isErrorHandling()) {
			invoke = new AFUtils().invokeGenerator(abstractAF,afSubIns.getSubInitInvoke(),afSubIns.getSubInitCmd(),afSubIns.getSubNextState(),afSubIns.getSubInstanceNo(),"Task"+(afSubIns.getSubTaskNo()+1)+"-"+afSubIns.getSubTaskErrorHandling());
			afSubIns.setSubInvoke(invoke);
			
			afSubIns.getRefInvoke().put("Task"+(afSubIns.getSubTaskNo()+1)+"-"+afSubIns.getSubTaskErrorHandling(), invoke);
        }else {
        	invoke = new AFUtils().invokeGenerator(abstractAF,afSubIns.getSubInitInvoke(),afSubIns.getSubInitCmd(),afSubIns.getSubNextState(),afSubIns.getSubInstanceNo(),"Task"+(afSubIns.getSubTaskNo()+1));
			afSubIns.setSubInvoke(invoke);
			
			afSubIns.getRefInvoke().put("Task"+(afSubIns.getSubTaskNo()+1), invoke);
        }
       
		 		 

		// -- Sub state --//

		afSubIns.setSubStateArray(afSubIns.getSubNextState());
		afSubIns.setSubCountChild(afSubIns.getSubCountChild() + 1);

		// -- Timeout --//
		String timeoutDt = Config.getFormatDateWithMiTz().print(new DateTime().plusSeconds(Integer.parseInt(timeoutconf)));
		afSubIns.setSubTimeout(timeoutDt);
		afSubIns.setSubTimeoutArray(timeoutDt);

		//-- Stats --//
        EStats statsOut = EStats.NE_OUT_CUSTOM_STAT;
        statsOut.setNeCustomStat(afSubIns.getSubNeId(), resourceMappingCommand, EResultDescription.SUCCESS, EEventType.REQUEST);
        afSubIns.setStatsOut(statsOut);

		//-- EDR --//
		SubEDR subEDR = new SubEDR();
        subEDR.setNextState(afSubIns.getSubNextState());
        if(StringUtils.isNotBlank(afSubIns.getStatsOut().getStatName())) {
        	subEDR.setStatsOut(afSubIns.getStatsOut().getStatName());
        }
		LogUtils.prepareDataForEDR(abstractAF, afSubIns, invoke,timeStampOut, subEDR);
		AFLog.d("timeStampOut : " + afInstance.getMainTimeStampOut());

        DateTimeFormatter subEffectiveTime = Config.getJourneyDateTask();

        DateTime timeStamp = new DateTime();
        
        if(afSubIns.isErrorHandling()) {
        	afSubIns.getNeSubmissionDate().put("Task"+(afSubIns.getSubTaskNo()+1)+"-"+afSubIns.getSubTaskErrorHandling(), subEffectiveTime.print(timeStamp));
        }else {
        	afSubIns.getNeSubmissionDate().put("Task"+(afSubIns.getSubTaskNo()+1), subEffectiveTime.print(timeStamp));
        }
		
		
		AFLog.d(resourceMappingCommand.toString());
		try {
			
				if (protocol.equals("HTTP")) {
					if(cType.equalsIgnoreCase("text/xml")){
					abstractAF.getEquinoxUtils().sendHTTPRequestMessage(builder, EEquinoxRawData.CTypeHTTP.TEXT_XML,
						invoke, neId, map);
					}else if(cType.equalsIgnoreCase("text/plain")){
						abstractAF.getEquinoxUtils().sendHTTPRequestMessage(builder, EEquinoxRawData.CTypeHTTP.TEXT_PLAIN,
								invoke, neId, map);
					}
				} else if (protocol.equalsIgnoreCase("SOCKET")) {
					abstractAF.getEquinoxUtils().sendSOCKETRequestMessage(builder, EEquinoxRawData.CTypeSOCKET.TCP, invoke,
						neId, map);
				}
			
		} catch (Exception e) {
			if (StringUtils.isNotBlank(protocol)&&protocol.equalsIgnoreCase("SOCKET")){
				AFLog.e("[Exception] can't build SOCKETRequestMessage.");
			}else{
				AFLog.e("[Exception] can't build HTTPRequestMessage.");
			}
			AFLog.e(e);
		}

		return eqxRawData;
	}

//	private static String unescape(String text) {
//		StringBuilder result = new StringBuilder(text.length());
//		int i = 0;
//		int n = text.length();
//		while (i < n) {
//			char charAt = text.charAt(i);
//			if (charAt != '&') {
//				result.append(charAt);
//				i++;
//			} else if (text.startsWith("&amp;", i)) {
//				result.append('&');
//				i += 5;
//			} else if (text.startsWith("&apos;", i)) {
//				result.append('\'');
//				i += 6;
//			} else if (text.startsWith("&quot;", i)) {
//				result.append('"');
//				i += 6;
//			} else if (text.startsWith("&lt;", i)) {
//				result.append('<');
//				i += 4;
//			} else if (text.startsWith("&gt;", i)) {
//				result.append('>');
//				i += 4;
//			} else {
//				i++;
//			}
//		}
//		return result.toString();
//	}

}
