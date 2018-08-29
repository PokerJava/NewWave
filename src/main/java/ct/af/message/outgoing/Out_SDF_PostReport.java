package ct.af.message.outgoing;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import ct.af.enums.*;
import ct.af.instance.*;
import ct.af.message.incoming.parameter.Param_CLIENT_PostResult;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.utils.*;
import ec02.af.abstracts.AbstractAF;
import ec02.af.data.AFDataFactory;
import ec02.af.utils.AFLog;
import ec02.data.enums.EEquinoxRawData;
import ec02.data.interfaces.EquinoxRawData;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Out_SDF_PostReport {
	public EquinoxRawData messageBuilder(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {

		  //-- Construct EquinoxRawData --//
        EquinoxRawData eqxRawData = AFDataFactory.createEquinoxRawData(); 
        constructMessage(abstractAF, afInstance, afSubIns);
		
		 if (!afSubIns.getSubInternalCode().equals(EResultCode.RE40301.getResultCode())) {
		try {

			DateTime timeStampOut = new DateTime();
			Map<String, String> map = new HashMap<>();
			map.put(EEqxMsg.TYPE.getEqxMsg(), EEventType.REQUEST.getEventType());
			map.put(EEqxMsg.CTYPE.getEqxMsg(), ECType.TEXTPLAIN.getCType());
			map.put(EEqxMsg.PROTOCOL.getEqxMsg(), EProtocol.HTTP.toString());
			map.put(EEqxMsg.DESTINATION.getEqxMsg(), Config.getSdfJourneyInterface());

			String url = Config.getUrlSdfJourney();
			map.put(EEqxMsg.URL.getEqxMsg(), url);
			map.put(EEqxMsg.METHOD.getEqxMsg(), EMethod.POST.toString());
			map.put(EEqxMsg.VAL.getEqxMsg(), afSubIns.getMsJourney());

			String edrCommand = "";
			if (map.get(EEqxMsg.URL.getEqxMsg()) != null) {
				int start = map.get(EEqxMsg.URL.getEqxMsg()).lastIndexOf('/') + 1;
				edrCommand = map.get(EEqxMsg.URL.getEqxMsg()).substring(start);
				int stop = edrCommand.indexOf('.');
				if (stop > 0) {
					edrCommand = edrCommand.substring(0, stop);
				}
			}
			AFLog.d("configURL : " + url);
			AFLog.d("edrCommand : " + edrCommand);

			// -- Invoke --//
			String invoke = new AFUtils().invokeGenerator(abstractAF, afSubIns.getSubInitInvoke(), afSubIns.getSubInitCmd(),
					afSubIns.getSubNextState(), afSubIns.getSubInstanceNo());
			afSubIns.setSubInvoke(invoke);
			AFLog.d("[INVOKE] : " + EEqxMsg.INVOKE.getEqxMsg());
			AFLog.d("[INVOKE] : " + invoke);

			// -- Sub state --//
			afSubIns.setSubStateArray(afSubIns.getSubNextState());
			afSubIns.setSubCountChild(afSubIns.getSubCountChild() + 1);

			// -- Timeout --//
			String timeout = Config.getFormatDateWithMiTz().print(new DateTime().plusSeconds(Config.getSdfJourneyTimeout()));
			afSubIns.setSubTimeout(timeout);
			afSubIns.setSubTimeoutArray(timeout);

			// -- Stat --//
			DateTimeFormatter formatDateWithMiTz = Config.getFormatDateWithMiTz();
			EStats statsOut = EStats.APP_SEND_SDF_GET_W_SDF_POSTREPORT_REQUEST;
			String nodeName = "AISSDF";
			statsOut.setCustomStat(statsOut, nodeName);
			afSubIns.setStatsOut(statsOut);
			afInstance.setMainTimeStampOut(formatDateWithMiTz.print(timeStampOut));

			// -- EDR --//
			SubEDR subEDR = new SubEDR();
			afSubIns.setSubInvokeResp(invoke);
			if(StringUtils.isNotBlank(afSubIns.getStatsOut().getStatName())) {
	        	subEDR.setStatsOut(afSubIns.getStatsOut().getStatName());
	        }
			LogUtils.prepareDataForEDR(abstractAF, afSubIns, invoke, timeStampOut, subEDR);
			AFLog.d("timeStampOut : " + afInstance.getMainTimeStampOut());
			MessageBuilder builder = new MessageBuilder("");
			abstractAF.getEquinoxUtils().sendHTTPRequestMessage(builder, EEquinoxRawData.CTypeHTTP.TEXT_PLAIN, invoke,
					Config.getSdfJourneyInterface(), map);
			
			
		} catch (Exception e) {
			AFLog.e("[Exception] can't build HTTPRequestMessage.");
			AFLog.e(e);
		}
		 }else{
			 LogUtils.writeLogSDF(abstractAF, afSubIns);
			 try {
				 LogUtils.writeLogCDR(abstractAF, afSubIns, null);
			 } catch (Exception e) {
				 AFLog.e("[Exception] writeLogCDR error.");
				 AFLog.e(e);
			}
		 }
		 
		return eqxRawData;
	}
		 
	 public  void   constructMessage(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {
		 
			Param_IDLE_ResourceOrder clientParam = (Param_IDLE_ResourceOrder) afSubIns.getSubClientParameter();
			HashMap<String, PostSubInstance> postInstanceMap = afSubIns.getPostSubInstanceMap();
			Param_CLIENT_PostResult param = afSubIns.getParam_HRZ_PostResult();
			DateTime orderDateConvert = Config.getFormatDateWithMiTz().parseDateTime(afSubIns.getSubInitTimeStampIn());
			DateTime timeStamp = new DateTime();

			String expirationDate = "";
			String ExpirationStatus = "";
			String caId = "";
			String baId = "";
			String saId = "";
			String resourceName = "";
			String mobileNo = "";
			String imsi = "";
			String billingSystem = "";
			int timeOut = Config.getExpirationDate();

			DateTime expirationDateConvert = Config.getFormatDateWithMiTz().parseDateTime(afSubIns.getSubInitTimeStampIn());
			expirationDate = (Config.getJourneyDateFormat().print(expirationDateConvert.plusMinutes(timeOut)));

			SortedSet<String> subResourceItemKeys = new TreeSet<>(postInstanceMap.keySet());
			
			JsonObject postReportJson = new JsonObject();
			postReportJson.addProperty("key1", "resourceOrderId_" + afSubIns.getRequestId());
			postReportJson.addProperty("key2", "actor_" + Config.getActor());
			postReportJson.addProperty("key3", "createdDate_" + Config.getJourneyDateFormat().print(orderDateConvert));
			postReportJson.addProperty("key4", "N/A");
			postReportJson.addProperty("key5", "N/A");
			postReportJson.addProperty("key6", "N/A");
			postReportJson.addProperty("key7", "N/A");
			postReportJson.addProperty("key8", "N/A");
			postReportJson.addProperty("key9", "N/A");
			postReportJson.addProperty("key10","N/A");
			
			postReportJson.addProperty("actor", Config.getActor());
			postReportJson.addProperty("resourceOrderId", afSubIns.getRequestId());
			postReportJson.addProperty("resourceGroupId", clientParam.getResourceGroupId());
			postReportJson.addProperty("resourceOrderType", clientParam.getResourceOrderType());
			postReportJson.addProperty("serviceOrderType", clientParam.getServiceOrderType());

			if (clientParam.getResourceItemList().size() > 0) {
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonObject = jsonParser.parse(clientParam.getResourceItemList().get(0)).getAsJsonObject();
				caId = jsonObject.get("caId") != null ? jsonObject.get("caId").toString() : "";
				baId = jsonObject.get("baId") != null ? jsonObject.get("baId").toString() : "";
				saId = jsonObject.get("saId") != null ? jsonObject.get("saId").toString() : "";
				mobileNo = jsonObject.get("mobileNo") != null ? jsonObject.get("mobileNo").toString() : "";
				imsi = jsonObject.get("imsi") != null ? jsonObject.get("imsi").toString() : "";
				billingSystem = jsonObject.get("billingSystem") != null ? jsonObject.get("billingSystem").toString() : "";
			}

			postReportJson.addProperty("caNo", caId.replaceAll("\"", ""));
			postReportJson.addProperty("baNo", baId.replaceAll("\"", ""));
			postReportJson.addProperty("saNo", saId.replaceAll("\"", ""));
			postReportJson.addProperty("userSys", clientParam.getUserSys());
			postReportJson.addProperty("serviceOrderId", clientParam.getServiceOrderId());
			postReportJson.addProperty("customerOrderId", clientParam.getCustomerOrderId());
			postReportJson.addProperty("customerOrderType", clientParam.getCustomerOrderType());
			postReportJson.addProperty("state", afSubIns.getState());

			String responseStatus = StringUtils.isNotBlank(afSubIns.getSubResultCodeJourney()) ? afSubIns.getSubResultCodeJourney(): afSubIns.getSubResultCode();
			String errorMessage = StringUtils.isNotBlank(afSubIns.getSubResourceOrderValidateMessage()) ? afSubIns.getSubResourceOrderValidateMessage(): afSubIns.getErrorMessage();

			postReportJson.addProperty("responseStatus", EResultCode.valueOf("RE"+responseStatus).getResultStatus());
			postReportJson.addProperty("errorMessage", errorMessage);
			postReportJson.addProperty("developerMessage", (StringUtils.isNotBlank(afSubIns.getRscOrderDevMsg()) ? afSubIns.getRscOrderDevMsg(): ""));

			postReportJson.addProperty("createdDate", Config.getJourneyDateFormat().print(orderDateConvert));
			postReportJson.addProperty("submittedDate", (afSubIns.getSubackReqDate() != null ? afSubIns.getSubackReqDate() : ""));

			if (StringUtils.isNotBlank(afSubIns.getSubcompleteDate())) {
				postReportJson.addProperty("completedDate", (afSubIns.getSubcompleteDate()));
			} else {
				postReportJson.addProperty("completedDate", (afSubIns.getSubackReqDate() != null ? afSubIns.getSubackReqDate() : ""));
			}

			//-- Get resourceName from client --//
			if (JsonUtils.isValidJsonFormat(afSubIns.getReqRawData())) {
				try {
					JsonArray arrayJson = (JsonArray) new JsonParser().parse(afSubIns.getReqRawData()).getAsJsonObject().get("resourceItemList");
					for(Object itemListKeys : arrayJson) {
						JsonObject jsonResourceItem = (JsonObject) itemListKeys;
						if(jsonResourceItem.get("resourceName") != null && !resourceName.contains(jsonResourceItem.get("resourceName").getAsString())) {
							if(StringUtils.isNotBlank(resourceName)) {
								resourceName += ",";
							}
							resourceName += jsonResourceItem.get("resourceName").getAsString();
						}
					}
				} catch (Exception ex) {
					AFLog.e("[Exception] error message : " + errorMessage);
					AFLog.e(ex);
				}
			}
			
			postReportJson.addProperty("resourceName", resourceName.trim());
			postReportJson.addProperty("mobileNo", mobileNo.replaceAll("\"", ""));
			postReportJson.addProperty("imsi", imsi.replaceAll("\"", ""));
			postReportJson.addProperty("publicIdValue", clientParam.getPublicIdValue());
			postReportJson.addProperty("privateIdValue", clientParam.getPrivateIdValue());
			postReportJson.addProperty("billingSystem", billingSystem.replaceAll("\"", ""));
			postReportJson.addProperty("lastModifiedTime", Config.getJourneyDateFormat().print(timeStamp));
			postReportJson.addProperty("priority", "0");
			postReportJson.addProperty("parallelResource", "0");
			postReportJson.addProperty("rollbackFlag", "");
			postReportJson.addProperty("retransmit", clientParam.getReTransmit());
			postReportJson.addProperty("ackReqDate", (afSubIns.getSubackReqDate() != null ? afSubIns.getSubackReqDate() : ""));
			postReportJson.addProperty("activateDate", (afSubIns.getSubackReqDate() != null ? afSubIns.getSubackReqDate() : ""));
			postReportJson.addProperty("sendRespDate", (afSubIns.getSubcompleteDate() != null ? afSubIns.getSubcompleteDate() : ""));
			postReportJson.addProperty("ackRespDate", (afSubIns.getSubackRespDate() != null ? afSubIns.getSubackRespDate() : ""));
			postReportJson.addProperty("orgResourceOrderIds", "0");
			postReportJson.addProperty("expirationDate", expirationDate);

			if (StringUtils.isNotBlank(expirationDate)) {
				DateTime calculateTime = Config.getJourneyDateFormat().parseDateTime(expirationDate);

				if (timeStamp.isAfter(calculateTime)) {
					ExpirationStatus = "Y";
				} else {
					ExpirationStatus = "N";
				}
			}

			postReportJson.addProperty("expirationStatus", ExpirationStatus);
			postReportJson.addProperty("resourceOrderErrorFlag", "");

			if (afSubIns.getSubInitCmd().equals(ECommand.RESOURCEORDER_SYNC.getCommand())) {
				postReportJson.addProperty("apiProtocol", "SynJSON");
			} else {
				postReportJson.addProperty("apiProtocol", "AsynJSON");
			}

			postReportJson.addProperty("pgzServerName", Config.getPgzServerName());

			JsonObject applicationSpecificDataJson = new JsonObject();

			// =================================================================4=================================================================//

			if (JsonUtils.isValidJsonFormat(afSubIns.getReqRawData())) {
				applicationSpecificDataJson.add("pgzRequestDetail",
						new JsonParser().parse(afSubIns.getReqRawData()).getAsJsonObject());
			} else {
				applicationSpecificDataJson.addProperty("unknownMessage", afSubIns.getReqRawData());
			}

			// =================================================================4=================================================================//

			// 10//

			applicationSpecificDataJson.add("pgzResponseDetail",
					(afSubIns.getResRawData() != null ? new JsonParser().parse(afSubIns.getResRawData()).getAsJsonObject()
							: new JsonObject()));

			// =================================================================7=================================================================//

			if (!afSubIns.getSubInitCmd().equals(ECommand.RESOURCEORDER_SYNC.getCommand())) {
				applicationSpecificDataJson.add("ackRequestDetail",
						new JsonParser().parse(afSubIns.getResAckRawData()).getAsJsonObject());
			} else {
				applicationSpecificDataJson.add("ackRequestDetail", new JsonObject());
			}
			// =================================================================7=================================================================//

			// ASYNC 11
			if ((!afSubIns.getSubInitCmd().equals(ECommand.RESOURCEORDER_SYNC.getCommand()))) {
				if (param != null) {
					if (JsonUtils.isValidJsonFormat(afSubIns.getResHrzRawData())) {
						applicationSpecificDataJson.add("ackResponseDetail",
								new JsonParser().parse(afSubIns.getResHrzRawData()).getAsJsonObject());
					} else {
						applicationSpecificDataJson.addProperty("ackResponseDetail",
								JsonUtils.escape(afSubIns.getResHrzRawData()));
					}
				} else {
					applicationSpecificDataJson.add("ackResponseDetail", new JsonObject());
				}
			} else {
				applicationSpecificDataJson.add("ackResponseDetail", new JsonObject());
			}

			if (subResourceItemKeys.size() != 0) {
				JsonArray resourceDetailJsonArray = new JsonArray();

				for (String key : subResourceItemKeys) {
					PostSubInstance postSubInstance = postInstanceMap.get(key);

					int resourceNo = Integer.valueOf(postSubInstance.getSubInstanceNo().substring(0, 2));
					String resourceKey = "";

					for (int i = 0; i < postSubInstance.getResourceSearchKey().size(); i++) {
						if (i == postSubInstance.getResourceSearchKey().size() - 1) {
							resourceKey = resourceKey + postSubInstance.getResourceSearchKey().get(i);
						} else {
							resourceKey = resourceKey + postSubInstance.getResourceSearchKey().get(i) + '$';
						}
					}

					JsonObject postSubInsJson = new JsonObject();
					postSubInsJson.addProperty("resourceNo", String.valueOf(resourceNo));
					postSubInsJson.addProperty("resourceName", postSubInstance.getResourceName());
					postSubInsJson.addProperty("resourceItemId", postSubInstance.getResourceItemId());
					postSubInsJson.addProperty("resourceRollbackThreshold", "0");
					postSubInsJson.addProperty("resourceItemStatus", postSubInstance.getResourceItemStatus());
					postSubInsJson.addProperty("resourceItemErrMessage", postSubInstance.getResourceItemErrMessage().replaceAll("\"", "&quot;"));
					if(!postSubInstance.isDropResourceFlag()){
						postSubInsJson.addProperty("resourceSearchKey", '(' + postSubInstance.getSubInitSearchKey() + "):(" + resourceKey + ')');
	
						
						JsonArray taskDetailJsonArray = new JsonArray();
						ArrayList<TaskDetail> taskDetailLists = postSubInstance.getTaskDetailList();
						for (TaskDetail taskDetailList : taskDetailLists) {
							taskDetailJsonArray.add(new JsonParser().parse(taskDetailList.toStringOut()).getAsJsonObject());
						}
	
						postSubInsJson.add("taskDetailList", taskDetailJsonArray);
					}
					resourceDetailJsonArray.add(postSubInsJson);
				}
				applicationSpecificDataJson.add("resourceDetailList", resourceDetailJsonArray);
			}

			postReportJson.add("applicationSpecificData", applicationSpecificDataJson);

			Gson prettyGson = GsonPool.getPrettyGsons();
			String message = prettyGson.toJson(postReportJson);
			GsonPool.pushPrettyGsons(prettyGson);
			afSubIns.setMsJourney(message);

	 }

}
