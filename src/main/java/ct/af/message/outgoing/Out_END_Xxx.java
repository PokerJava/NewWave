package ct.af.message.outgoing;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ct.af.enums.ECType;
import ct.af.enums.ECommand;
import ct.af.enums.EEqxMsg;
import ct.af.enums.EEventType;
import ct.af.enums.EMethod;
import ct.af.enums.EProtocol;
import ct.af.enums.ERequestState;
import ct.af.enums.EResultCode;
import ct.af.enums.EStats;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.utils.Config;
import ct.af.utils.GsonPool;
import ct.af.utils.LogUtils;
import ec02.af.abstracts.AbstractAF;
import ec02.af.data.AFDataFactory;
import ec02.af.utils.AFLog;
import ec02.data.enums.EEquinoxRawData;
import ec02.data.interfaces.EquinoxRawData;

public class Out_END_Xxx {
	public EquinoxRawData messageBuilder(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {
	    EquinoxRawData eqxRawData = AFDataFactory.createEquinoxRawData();
	    Param_IDLE_ResourceOrder param = (Param_IDLE_ResourceOrder) afSubIns.getSubClientParameter();

	    String state = afSubIns.getState();

	    JsonObject ackRespJson = new JsonObject();
	    ackRespJson.addProperty("resourceOrderId", afSubIns.getRequestId());
	    if(StringUtils.isNotBlank(param.getResourceGroupId())){
	      ackRespJson.addProperty("resourceGroupId", param.getResourceGroupId());
	    }
	    if(StringUtils.isNotBlank(param.getCustomerOrderId())){
	      ackRespJson.addProperty("customerOrderId", param.getCustomerOrderId());
	    }
	    ackRespJson.addProperty("resultCode", afSubIns.getSubResultCode());
	    afSubIns.setSubResultCodeEDR(afSubIns.getSubResultCode());

	    ackRespJson.addProperty("resourceOrderTimeOut", String.valueOf((int)afSubIns.getResourceOrderTimeOut()));

	    EResultCode resultCode = EResultCode.valueOf("RE"+afSubIns.getSubResultCode());
	    if (resultCode.equals(EResultCode.RE20000)) {
	      ackRespJson.addProperty("resultDesc", EResultCode.RE20000.getResultStatus());
	      afSubIns.setSubResultDescEDR(EResultCode.RE20000.getResultStatus());
	    } else {
	      // fail
	      ackRespJson.addProperty("resultDesc", resultCode.getResultStatus()
	          +": " + afSubIns.getErrorMessage());
	      state = ERequestState.COMPLETED.getState();
	      afSubIns.setSubResultDescEDR(resultCode.getResultStatus()
	              +": " + afSubIns.getErrorMessage());
	    }

	    ackRespJson.addProperty("developerMessage", "");

	    String response = "";

	    if (afSubIns.getSubInitCmd().equals(ECommand.RESOURCEORDER_SYNC.getCommand())) {
	      new Out_CLIENT_PostResult().hrzMessage(abstractAF, afInstance, afSubIns);
	      response = afSubIns.getResRawData();
	      AFLog.d("Message : " + response);
	      state = ERequestState.COMPLETED.getState();
	    } else {
	      Gson prettyGson = GsonPool.getPrettyGsons();
	      response = prettyGson.toJson(ackRespJson);
	      GsonPool.pushPrettyGsons(prettyGson);
	    }

	    afSubIns.setResAckRawData(response);
	    afSubIns.setResAckResultCode(afSubIns.getSubResultCode());
	    afSubIns.setState(state);
	    AFLog.d("State : " + afSubIns.getState());


	    Map<String, String> map = new HashMap<>();
	    map.put(EEqxMsg.TYPE.getEqxMsg(), EEventType.RESPONSE.getEventType());
	    map.put(EEqxMsg.CTYPE.getEqxMsg(), ECType.TEXTPLAIN.getCType());
	    map.put(EEqxMsg.PROTOCOL.getEqxMsg(), EProtocol.HTTP.toString());
	    map.put(EEqxMsg.METHOD.getEqxMsg(), EMethod.POST.toString());
	    map.put(EEqxMsg.VAL.getEqxMsg(), response);
	    map.put(EEqxMsg.URL.getEqxMsg(), afSubIns.getSubInitURL());


	    DateTimeFormatter subEffectiveTime = Config.getJourneyDateFormat();
	    DateTime timeStamp = new DateTime();
	    afSubIns.setSubackReqDate(subEffectiveTime.print(timeStamp));
	    afSubIns.setSubAckReqCDR(Config.getFormatDateWithMiTz().print(timeStamp));



	    String[] splitOrig = afSubIns.getSubInitOrig().split("\\.");

	    MessageBuilder builder = new MessageBuilder("");
	    try {
	      abstractAF.getEquinoxUtils().sendHTTPResponseMessage(builder, EEquinoxRawData.CTypeHTTP.TEXT_PLAIN, afSubIns.getSubInitInvoke(), splitOrig[2] + "." + splitOrig[3], map);
	    } catch (Exception e) {
	    	AFLog.e("[Exception] can't build HTTPResponseMessage.");
	    	AFLog.e(e);
	    }

	    // TODO : ask for set respStatus?
	    afSubIns.setRespStatus("1");

	    String userSys = param.getUserSys().trim();
	    if (Config.getServerInterfaceHashMap().containsKey(userSys)) {
	      userSys = Config.getServerInterfaceHashMap().get(userSys);
	    }

	    EStats statsOut = EStats.APP_SEND_CLIENT_GET_RESOURCE_ORDER_RESPONSE_SUCCESS;
	    if(userSys!=null&&!userSys.isEmpty()) {
	      if (afSubIns.getSubResultCode().equals(EResultCode.RE20000.getResultCode())) {
	        statsOut.setClientCustomStat(EStats.APP_SEND_CLIENT_GET_RESOURCE_ORDER_RESPONSE_SUCCESS, userSys);
	      } else {
	        statsOut.setClientCustomStat(EStats.APP_SEND_CLIENT_GET_RESOURCE_ORDER_RESPONSE_SUCCESS_WITH_ERROR, userSys);
	      }
	    }else {
	      if (afSubIns.getSubResultCode().equals(EResultCode.RE20000.getResultCode())) {
	        statsOut.setClientCustomStat(EStats.APP_SEND_CLIENT_GET_RESOURCE_ORDER_RESPONSE_SUCCESS, "Client");
	      } else {
	        statsOut.setClientCustomStat(EStats.APP_SEND_CLIENT_GET_RESOURCE_ORDER_RESPONSE_SUCCESS_WITH_ERROR, "Client");
	      }
	    }
	    afSubIns.setStatsOut(statsOut);
	    /* EDR */
	    JsonObject ackRespJsonEDR = new JsonObject();
	    ackRespJsonEDR.addProperty("ackRespJson", ackRespJson.toString());
	    afSubIns.setResponseMessage(ackRespJsonEDR.toString());
	    AFLog.d("timeStampOut : " + afInstance.getMainTimeStampOut());
	    try {
	      //LogUtils.writeLogCDR(abstractAF, afSubIns, map);
	      LogUtils.writeLogEDR(abstractAF, afSubIns, afInstance, eqxRawData);
	    } catch (Exception ex) {
	    	AFLog.e("[Exception] writeLogEDR error.");
	    	AFLog.e(ex);
	    }

	    return eqxRawData;
	  }


}
