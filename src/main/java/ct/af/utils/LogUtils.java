package ct.af.utils;

import ct.af.enums.ECommand;
import ct.af.enums.EResultCode;
import ct.af.enums.ERet;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.instance.IncomingUnknownLog;
import ct.af.instance.RecoveryLog;
import ct.af.instance.SubCDR;
import ct.af.instance.SubEDR;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.resourceModel.ResourceMappingCommand;
import ct.af.instance.TransactionLog;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.enums.EEquinoxRawData.CTypeFILECONTROL;
import ec02.data.enums.EEquinoxRawData.TypeFILECONTROL;
import ec02.data.interfaces.EquinoxRawData;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LogUtils {

	public static void writeLogSDF(AbstractAF abstractAF, AFSubInstance afSubIns) {
		try {

			AFLog.d("write log Recovery");
			String resourceOrderId = afSubIns.getRequestId();
			DateTimeFormatter sdf = Config.getFormatDateWithMiTz();
			DateTime date = new DateTime();
			String fileName = "SDF_" + sdf.print(date) + "_" + resourceOrderId + ".dat";
			Map<String, String> additinalAttribute = new HashMap<String, String>();

			RecoveryLog recoveryLog = new RecoveryLog();
			recoveryLog.setDataResponse(afSubIns.getMsJourney());
			// Set src
			if (afSubIns.getSubInternalCode().equals(EResultCode.RE40301.getResultCode())) {
				recoveryLog.setSrc(Config.getPATH_ES22_RECOVERYLOG_DUPLICATE() + fileName);
			} else {
				recoveryLog.setSrc(afSubIns.getRecoveryConf() + fileName);
			}

			String invoke = new AFUtils().invokeGenerator(abstractAF, afSubIns.getSubInitInvoke(),
					afSubIns.getSubInitCmd(), afSubIns.getSubNextState(), afSubIns.getSubInstanceNo());

			abstractAF.getEquinoxUtils().invokeOfflineData(recoveryLog, TypeFILECONTROL.NOREPLY,
					CTypeFILECONTROL.UNKNOWN, invoke, Config.getRECOVERY_FILE(), additinalAttribute);
//			AFLog.d("*****************************************************************************************************************************************************");
			AFLog.d("write log ES22 : " + afSubIns.getMsJourney().replaceAll("\n", "").replaceAll("\r", ""));
//			AFLog.d("*****************************************************************************************************************************************************");
		} catch (Exception e) {
			AFLog.e("[Exception] error writeLogRecovery");
			AFLog.e(e);
		}
	}

	public static void prepareDataForEDR(AbstractAF abstractAF, AFSubInstance afSubIns, String invoke,
			DateTime timeStampOut, SubEDR subEDR) {
		subEDR.setTimeStampOut(Config.getFormatDateWithMiTz().print(timeStampOut));
		afSubIns.getMapSubEDR().put(invoke, subEDR);
	}

	public static void prepareDataForCDR(AFSubInstance afSubIns) {
		AFLog.d("Timestamp CDRLog");
		SubCDR subCDR = new SubCDR();
		DateTimeFormatter formatDateWithMiTz = Config.getFormatDateWithMiTz();
		DateTime timeStamp = new DateTime();
		String receivedDateMills = formatDateWithMiTz.print(timeStamp);
		afSubIns.setReceivereceivedDateMills(receivedDateMills);
		DateTime timeStampOut = new DateTime();
		subCDR.setTimeStampIn(Config.getFormatDateWithMiTz().print(timeStampOut));
		afSubIns.getSubCDRMap().put(afSubIns.getSubInitInvoke(), subCDR);
	}

	public static void writeLogEDR(AbstractAF abstractAF, AFSubInstance afSubIns, AFInstance afInstance,
			EquinoxRawData eqxRawData) throws Exception {
//		AFLog.d("***************************************************************");
//		AFLog.d("Start write EDR");
		String invoke = eqxRawData.getInvoke();// afSubIns.invokeArray
		String suppcode = afSubIns.getCurrentSuppcode();
		AFSubInstance resourceOrder = afInstance.getMainSubInstance(afSubIns.getSubResourceOrderIns());
		SubEDR subEDR = new SubEDR();

		if ((afSubIns.getSubInitURL() != null && !afSubIns.getSubInitURL().isEmpty())
				&& afSubIns.getSubControlState().contains(ESubState.END_RESOURCEORDER.getState())
				|| (afSubIns.getSubControlState().equals(ESubState.CLIENT_POSTRESULT.getState())
						&& afSubIns.getSubInitURL().contains("/sync"))) {
			invoke = afSubIns.getSubInitInvoke();
		}
		subEDR.setInvokeIdResp(afSubIns.getInvoke());
		if (afSubIns.getMapSubEDR().containsKey(invoke)) {
			/* get timeStampOut, statsOut */
			AFLog.d("MapSubEDR() is contains : invoke : " + invoke);
			subEDR = afSubIns.getMapSubEDR().get(invoke);
		} else {
			AFLog.d("MapSubEDR() is not contains : invoke : " + invoke);
		}

		subEDR = subEDR.setStat(subEDR, afSubIns);
		if (afSubIns.isNeTask()) {

			/* NeId */
			Param_IDLE_ResourceOrder subResourceOrder = (Param_IDLE_ResourceOrder) resourceOrder
					.getSubClientParameter();

			if (subResourceOrder.getCustomerOrderId() != null && subResourceOrder.getCustomerOrderId() != "") {
				subEDR.setCustomerOrderId(subResourceOrder.getCustomerOrderId());
			} else {
				AFLog.d("CustomerOrderId can't get from resourceOrderInstance");
			}
			if (subResourceOrder.getCustomerOrderType() != null && subResourceOrder.getCustomerOrderType() != "") {
				subEDR.setCustomerOrderType(subResourceOrder.getCustomerOrderType());
			} else {
				AFLog.d("CustomerOrderType can't get from resourceOrderInstance");
			}
			if (subResourceOrder.getResourceGroupId() != null && subResourceOrder.getResourceGroupId() != "") {
				subEDR.setResourceGroupId(subResourceOrder.getResourceGroupId());
			} else {
				AFLog.d("ResourceGroupId can't get from resourceOrderInstance");
			}
			if (subResourceOrder.getResourceOrderType() != null && subResourceOrder.getResourceOrderType() != "") {
				subEDR.setResourceOrderType(subResourceOrder.getResourceOrderType());
			} else {
				AFLog.d("ResourceOrderType can't get from resourceOrderInstance");
			}
			if (subResourceOrder.getServiceOrderId() != null && subResourceOrder.getServiceOrderId() != "") {
				subEDR.setServiceOrderId(subResourceOrder.getServiceOrderId());
			} else {
				AFLog.d("ServiceOrderId can't get from resourceOrderInstance");
			}
			if (subResourceOrder.getServiceOrderType() != null && subResourceOrder.getServiceOrderType() != "") {
				subEDR.setServiceOrderType(subResourceOrder.getServiceOrderType());
			} else {
				AFLog.d("ServiceOrderType can't get from resourceOrderInstance");
			}
			if (afSubIns.getSubNeId() != null && afSubIns.getSubNeId() != "") {
				subEDR.setNeId(afSubIns.getSubNeId());
			} else {
				AFLog.d("NeId can't get from afSubInstance");
			}
			if (suppcode != null && !suppcode.isEmpty()) {
				subEDR.setMethod(Config.getResourceMappingCommandHashMap().get(suppcode).getMethod());
			} else {
				AFLog.d("Method can't get from Config by resourceMappingCommand");
			}
			if (afSubIns.getMappingPoolTask().get(afSubIns.getTaskId()).getErrCode() != null
					&& !afSubIns.getMappingPoolTask().get(afSubIns.getTaskId()).getErrCode().isEmpty()) {
				subEDR.setServiceResult(afSubIns.getMappingPoolTask().get(afSubIns.getTaskId()).getResultCode());
			} else {
				AFLog.d("ServiceResult can't get from MappingPoolTask");
			}
			if (afSubIns.getMappingPoolTask().get(afSubIns.getTaskId()).getStatus() != null
					&& !afSubIns.getMappingPoolTask().get(afSubIns.getTaskId()).getStatus().isEmpty()) {
				subEDR.setStatus(afSubIns.getMappingPoolTask().get(afSubIns.getTaskId()).getStatus());
			} else {
				AFLog.d("Status can't get from MappingPoolTask");
			}
			if (afSubIns.getMappingPoolTask().get(afSubIns.getTaskId()).getErrDesc() != null

					&& !afSubIns.getMappingPoolTask().get(afSubIns.getTaskId()).getErrDesc().isEmpty()
					&& eqxRawData.getRet().equals("0")) {
				subEDR.setErrorDesc("\"" + afSubIns.getMappingPoolTask().get(afSubIns.getTaskId()).getErrDesc() + "\"");
			} else {
				subEDR.setErrorDesc("\"Success\"");
				if (eqxRawData.getRet().equals("2")) {
					subEDR.setErrorDesc("\"Reject\"");
				} else if (eqxRawData.getRet().equals("3")) {
					subEDR.setErrorDesc("\"Abort\"");
				} else if (eqxRawData.getRet().equals("4")) {
					subEDR.setErrorDesc("\"Timeout\"");
				}

				AFLog.d("ErrorDescription can't get from MappingPoolTask");
			}

			/* if NeTask SubClientParameter = null */
			if (!afSubIns.getSubClientHashMapParameter().isEmpty()) {
				if (afSubIns.getSubClientHashMapParameter().containsKey("imsi")) {
					subEDR.setImsi(afSubIns.getSubClientHashMapParameter().get("imsi").toString() != null
							? afSubIns.getSubClientHashMapParameter().get("imsi").toString()
							: "");
				}
				if (afSubIns.getSubClientHashMapParameter().containsKey("mobileNo")) {
					subEDR.setMobileNo(afSubIns.getSubClientHashMapParameter().get("mobileNo").toString() != null
							? afSubIns.getSubClientHashMapParameter().get("mobileNo").toString()
							: "");
				}
				if (afSubIns.getSubClientHashMapParameter().containsKey("resourceName")) {
					subEDR.setResourceName(
							afSubIns.getSubClientHashMapParameter().get("resourceName").toString() != null
									? afSubIns.getSubClientHashMapParameter().get("resourceName").toString()
									: "");
				}
			}

			subEDR.setTaskId(afSubIns.getSubInstanceNo().substring(0, 2) + "." + afSubIns.getTaskId());
			if (Config.getResourceMappingCommandHashMap() != null && StringUtils.isNotBlank(suppcode)
					&& Config.getResourceMappingCommandHashMap().get(suppcode) != null) {
				subEDR.setDestinationServiceName(Config.getResourceMappingCommandHashMap().get(suppcode).getNeType());
			} else {
				AFLog.d("NeType can't get from Config by resourceMappingCommandHashMap");
			}
		} else {
			if (eqxRawData.getRet().equals(ERet.RET0.getRet()) || eqxRawData.getRet().isEmpty()) {
				if (EResultCode.valueOf("RE" + afSubIns.getSubResultCodeEDR()).getResultStatus() != null) {
					subEDR.setStatus(EResultCode.valueOf("RE" + afSubIns.getSubResultCodeEDR()).getResultStatus());
				}
				if (!afSubIns.getSubControlState().equals(ESubState.CLIENT_POSTRESULT.getState())) {
					if (afSubIns.getSubResultCodeEDR() != null) {
						subEDR.setServiceResult(afSubIns.getSubResultCodeEDR());
					} else {
						AFLog.d("ResultCode no data from afSubIns");
					}
				} else {
					subEDR.setServiceResult(afSubIns.getResAckResultCode());
				}
			} else {
				subEDR.setServiceResult("0000" + eqxRawData.getRet());
				subEDR.setStatus(EResultCode.RE50000.getResultStatus());
			}
			if (afSubIns.getSubControlState().contains(ESubState.END_RESOURCEORDER.getState())
					|| afSubIns.getSubControlState().equals(ESubState.SDF_POSTREPORT.getState())
					|| afSubIns.getSubControlState().equals(ESubState.CLIENT_POSTRESULT.getState())) {
				subEDR.setMethod("POST");
			} else {
				subEDR.setMethod("GET");
			}
			int indexOfLastInvoke = invoke.lastIndexOf(".") + 1;
			subEDR.setTaskId(invoke.substring(indexOfLastInvoke));
		}
		/* Destination by service_Name */
		if (invoke.contains("SDF")) {
			subEDR.setDestinationServiceName("SDF");
		}
		/* getResourceItemList */
		if (afSubIns.getSubClientParameter() != null) {
			Param_IDLE_ResourceOrder subParam = (Param_IDLE_ResourceOrder) afSubIns.getSubClientParameter();
			if (subParam.getCustomerOrderId() != null) {
				subEDR.setCustomerOrderId(subParam.getCustomerOrderId());
			} else {
				AFLog.d("CustomerOrderId can't get from subClientParameter");
			}
			if (subParam.getCustomerOrderType() != null) {
				subEDR.setCustomerOrderType(subParam.getCustomerOrderType());
			}
			if (subParam.getResourceOrderType() != null) {
				subEDR.setResourceOrderType(subParam.getResourceOrderType());
			}
			if (afSubIns.getSubControlState().equals(ESubState.CLIENT_POSTRESULT.getState())) {
				if (subParam.getUserSys() != null) {
					subEDR.setDestinationServiceName(Config.getServerInterfaceHashMap().get(subParam.getUserSys()));
				} else {
					AFLog.d("UserSys can't get from subClientParamater");
				}
			} else if (afSubIns.getSubControlState().contains(ESubState.END_RESOURCEORDER.getState())) {
				if (subParam.getUserSys() != null) {
					subEDR.setDestinationServiceName(Config.getServerInterfaceHashMap().get(subParam.getUserSys()));
				}
			}
			// get resource in ResourceItemList
			if (subParam.getResourceItemList() != null && subParam.getResourceItemList().size() > 0) {
				if (subParam.getResourceItemList().size() > 0) {
					for (int i = 0; i < subParam.getResourceItemList().size(); i++) {

						JsonParser jsonParser = new JsonParser();
						JsonObject jsonResourceItem = (JsonObject) jsonParser
								.parse(subParam.getResourceItemList().get(i)).getAsJsonObject();
						Gson gson = GsonPool.getGson();
						HashMap<String, Object> hashmapResourceItem = new HashMap<>();
						hashmapResourceItem = (HashMap<String, Object>) gson.fromJson(jsonResourceItem,
								hashmapResourceItem.getClass());
						subEDR = setImsiMobileNoEDR(hashmapResourceItem, subEDR);
							if (hashmapResourceItem.containsKey("resourceName")) {
								if (afSubIns.isNeTask()) {
									subEDR.setResourceName(hashmapResourceItem.get("resourceName").toString());
								}
							}
						}
					}
				
			} else {
				AFLog.d("ResourceItemList can't get from subClientParameter");
			}
		} else {
			AFLog.d("SubClientParameter and SubClientHashMapParameter no data");
		}
		/* Incoming 2,5,10,12 */

		if (afSubIns.getSubControlState().equals(ESubState.SDF_GETRESOURCEINVENTORY.getState())) {
			subEDR.setNeId(Config.getSdfInventoryInterface());
		} else if (afSubIns.getSubControlState().equals(ESubState.SDF_GETRESOURCEINFRANODE.getState())) {
			subEDR.setNeId(Config.getSdfInfranodeInterface());
		} else if (afSubIns.getSubControlState().contains(ESubState.END_RESOURCEORDER.getState())) {
			Param_IDLE_ResourceOrder subParam = (Param_IDLE_ResourceOrder) afSubIns.getSubClientParameter();
			subEDR.setNeId(Config.getServerInterfaceHashMap().get(subParam.getUserSys()));
			subEDR.setTimeStampOut(afSubIns.getSubInitTimeStampIn());
		} else if (afSubIns.getSubControlState().equals(ESubState.CLIENT_POSTRESULT.getState())) {
			Param_IDLE_ResourceOrder subParam = (Param_IDLE_ResourceOrder) afSubIns.getSubClientParameter();
			subEDR.setNeId(Config.getServerInterfaceHashMap().get(subParam.getUserSys()));
		} else if (afSubIns.getSubControlState().equals(ESubState.SDF_POSTREPORT.getState())) {
			subEDR.setNeId(Config.getSdfJourneyInterface());
		}

		if (invoke.contains("SDF")) {
			if (afSubIns.getSubResultDescEDR() != null && !afSubIns.getSubResultDescEDR().isEmpty()
					&& eqxRawData.getRet().equals(ERet.RET0.getRet())) {
				subEDR.setErrorDesc("\"" + afSubIns.getSubResultDescEDR() + "\"");
			} else {
				subEDR.setErrorDesc(errorDescFormEqx(eqxRawData.getRet()));
			}
		} else {
			if (afSubIns.getParam_HRZ_PostResult() != null) {
				if (afSubIns.getParam_HRZ_PostResult().getResultDesc() != null
						&& !afSubIns.getParam_HRZ_PostResult().getResultDesc().equals("")
						&& eqxRawData.getRet().equals("0")) {
					subEDR.setErrorDesc("\"" + afSubIns.getParam_HRZ_PostResult().getResultDesc() + "\"");
				} else {
					subEDR.setErrorDesc(errorDescFormEqx(eqxRawData.getRet()));
					AFLog.d("ResultDesc not data from HRZ_PostResult");

				}
			}
		}
		if (afSubIns.getSubControlState().equals(ESubState.SDF_GETRESOURCEINFRANODE.getState())
				|| afSubIns.getSubControlState().equals(ESubState.CLIENT_POSTRESULT.getState())
				|| afSubIns.getSubControlState().equals(ESubState.SDF_POSTREPORT.getState())
				|| afSubIns.getSubControlState().contains(ESubState.END_RESOURCEORDER.getState())) {
			if (!afSubIns.getSubClientHashMapParameter().isEmpty()) {
				if (afSubIns.getSubClientHashMapParameter().containsKey("resourceGroupId")) {
					if (!afSubIns.getSubClientHashMapParameter().get("resourceGroupId").equals("")) {
						subEDR.setResourceGroupId(
								afSubIns.getSubClientHashMapParameter().get("resourceGroupId").toString());
					} else {
						AFLog.d("ResourceGroupId can't get from subClientHashMapParameter");
					}
				}
				if (afSubIns.getSubClientHashMapParameter().containsKey("resourceOrderId")) {
					if (!afSubIns.getSubClientHashMapParameter().get("resourceOrderId").equals("")) {
						subEDR.setResourceOrderId(
								afSubIns.getSubClientHashMapParameter().get("resourceOrderId").toString());
					} else {
						AFLog.d("ResourceOrderId can't get from subClientHashMapParameter");
					}
				}
				if (afSubIns.getSubClientHashMapParameter().containsKey("resourceOrderType")) {
					if (!afSubIns.getSubClientHashMapParameter().get("resourceOrderType").equals("")) {
						subEDR.setResourceOrderType(
								afSubIns.getSubClientHashMapParameter().get("resourceOrderType").toString());
					} else {
						AFLog.d("ResourceOrderType can't get from subClientHashMapParameter");
					}
				}
				if (afSubIns.getSubClientHashMapParameter().containsKey("serviceOrderId")) {
					if (!afSubIns.getSubClientHashMapParameter().get("serviceOrderId").equals("")) {
						subEDR.setServiceOrderId(
								afSubIns.getSubClientHashMapParameter().get("serviceOrderId").toString());
					} else {
						AFLog.d("ServiceOrderid can't get from subClientHashMapParameter");
					}
				}
				if (afSubIns.getSubClientHashMapParameter().containsKey("customerOrderId")) {
					if (!afSubIns.getSubClientHashMapParameter().get("customerOrderId").equals("")) {
						subEDR.setCustomerOrderId(
								afSubIns.getSubClientHashMapParameter().get("customerOrderId").toString());
					} else {
						AFLog.d("customerOrderId can't get from subClientHashMapParameter");
					}
				}
				if (afSubIns.getSubClientHashMapParameter().containsKey("customerOrderType")) {
					if (!afSubIns.getSubClientHashMapParameter().get("customerOrderType").equals("")) {
						subEDR.setCustomerOrderType(
								afSubIns.getSubClientHashMapParameter().get("customerOrderType").toString());
					} else {
						AFLog.d("customerOrderType can't get from subClientHashMapParameter");
					}
				}
				/* infraNode not have serviceOrderType */
				if (afSubIns.getSubClientHashMapParameter().containsKey("serviceOrderType")) {
					if (!afSubIns.getSubClientHashMapParameter().get("serviceOrderType").equals("")) {
						subEDR.setServiceOrderType(
								afSubIns.getSubClientHashMapParameter().get("serviceOrderType").toString());
					} else {
						AFLog.d("ServiceOrderType is not data from subClientHashMapParameter");
					}
				} else {
					if (afSubIns.getSubClientParameter() != null) {
						int indexOfServiceOrderType = Integer
								.valueOf(afSubIns.getSubClientParameter().toString().indexOf("serviceOrderType"));
						String serviceOrderType = afSubIns.getSubClientParameter().toString()
								.substring(indexOfServiceOrderType).split("'")[1];
						subEDR.setServiceOrderType(serviceOrderType);
					}
				}
			} else {
				AFLog.d("SubClientHashMapParameter doesn't have data");
			}
		}

		if (!eqxRawData.getRet().equals(ERet.RET0.getRet()) && afSubIns.getRet() != null
				&& !afSubIns.getRet().equals(ERet.RET0.getRet())) {
			subEDR.setTimeStampIn(afSubIns.getSubTimeout());
		} else {
			subEDR.setTimeStampIn(afInstance.getMainTimeStampIncoming());
		}
		if (invoke.contains("Task")) {
			if (suppcode != null && !suppcode.equals("")) {
				subEDR.setRecordName(suppcode);
			}
			if (resourceOrder.getRequestId() != null && !resourceOrder.getRequestId().equals("")) {
				subEDR.setResourceOrderId(resourceOrder.getRequestId());
			}
			if (abstractAF.getEquinoxProperties().getSession() != null
					&& !abstractAF.getEquinoxProperties().getSession().equals("")) {
				subEDR.setSessionId(abstractAF.getEquinoxProperties().getSession());
			}
			if (eqxRawData.getRet() != null && !eqxRawData.getRet().equals("")) {
				subEDR.setReceivedEvent(eqxRawData.getRet().toString());
			}
			if (eqxRawData.getInvoke() != null && !eqxRawData.getInvoke().equals("")) {
				subEDR.setInvokeId(eqxRawData.getInvoke().toString());
			}
		} else {
			if (afSubIns.getSubCurrentState() != null && !afSubIns.getSubCurrentState().equals("")) {
				if (!afSubIns.getSubControlState().contains(ESubState.END_RESOURCEORDER.getState())) {
					subEDR.setRecordName(afSubIns.getSubCurrentState());
				} else {
					JsonParser jsonParser = new JsonParser();
					JsonObject jsonResourceItem = (JsonObject) jsonParser.parse(afSubIns.getResponseMessage())
							.getAsJsonObject();
					Gson gson = GsonPool.getGson();
					HashMap<String, Object> hashmapResponse = new HashMap<>();
					hashmapResponse = (HashMap<String, Object>) gson.fromJson(jsonResourceItem,
							hashmapResponse.getClass());
					hashmapResponse = (HashMap<String, Object>) gson
							.fromJson(hashmapResponse.get("ackRespJson").toString(), hashmapResponse.getClass());
					if (hashmapResponse != null && !hashmapResponse.isEmpty()) {
						if (hashmapResponse.get("resultDesc") != null && !hashmapResponse.isEmpty()
								&& afSubIns.getRet().equals(ERet.RET0.getRet())) {
							subEDR.setErrorDesc("\"" + hashmapResponse.get("resultDesc").toString() + "\"");
						} else {
							subEDR.setErrorDesc("\"Success\"");
							if (afSubIns.getRet().equals("2")) {
								subEDR.setErrorDesc("\"" + ERet.RET2.getRetDesc() + "\"");
							} else if (afSubIns.getRet().equals("3")) {
								subEDR.setErrorDesc("\"" + ERet.RET3.getRetDesc() + "\"");
							} else if (afSubIns.getRet().equals("4")) {
								subEDR.setErrorDesc("\"" + ERet.RET4.getRetDesc() + "\"");
							}
						}
						subEDR.setTaskId(afSubIns.getSubInstanceNo());
						subEDR.setRecordName(ESubState.END_RESOURCEORDER.getState());
						subEDR.setInvokeId(afSubIns.getSubInitInvoke());
						subEDR.setInvokeIdResp(afSubIns.getSubInvokeResp());
					}
				}
			}
			if (afSubIns.getRequestId() != null && !afSubIns.getRequestId().equals("")) {
				subEDR.setResourceOrderId(afSubIns.getRequestId());
			}
			if (abstractAF.getEquinoxProperties().getSession() != null
					&& !abstractAF.getEquinoxProperties().getSession().equals("")) {
				subEDR.setSessionId(abstractAF.getEquinoxProperties().getSession());
			}

			if (eqxRawData.getInvoke() != null && !eqxRawData.getInvoke().equals("")) {
				subEDR.setInvokeId(eqxRawData.getInvoke().toString());
			}
			if (eqxRawData.getRet() != null && !eqxRawData.getRet().equals("")) {
				subEDR.setReceivedEvent(eqxRawData.getRet().toString());
			} else {
				subEDR.setReceivedEvent(afSubIns.getRet());
			}
		}
		/* Response time EDR timeResponse-timeRequest */
		Calendar req = Calendar.getInstance();
		Calendar resp = Calendar.getInstance();
		if (!afSubIns.getSubControlState().contains(ESubState.END_RESOURCEORDER.getState())) {
			AFLog.d("TimeOut : " + subEDR.getTimeStampOut());
			req.setTime(Config.getFormatDateWithMiTz().parseDateTime(subEDR.getTimeStampOut()).toDate());
		} else {
			AFLog.d("TimeInit : " + afSubIns.getSubInitTimeStampIn());
			req.setTime(Config.getFormatDateWithMiTz().parseDateTime(afSubIns.getSubInitTimeStampIn()).toDate());
		}
		resp.setTime(Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampIncoming()).toDate());
		AFLog.d("TimeStampInconing : " + afInstance.getMainTimeStampIncoming());
		long currentReq = req.getTimeInMillis();
		long currentResp = resp.getTimeInMillis();
		long responseTime = 0;
		responseTime = currentResp - currentReq;
		AFLog.d("Response Time : " + responseTime);
		subEDR.setResponseTime(String.valueOf(responseTime));
		// -----------------------------------------------------------------------//
		/* invokeParrent index[0] of invoke to first"." */
		subEDR.setInvokeParrent(afSubIns.getSubInitInvoke());
		subEDR = clearNullEdr(subEDR);
		AFLog.d("EDR Stamp Request : " + req.getTimeInMillis());
		AFLog.d("EDR Stamp Response : " + resp.getTimeInMillis());
		AFLog.d("EDR Response Time : " + String.valueOf(responseTime) + "ms");
		String logEDR = buildEDRLog(abstractAF, afInstance, subEDR);
		String edrConf = Config.getEdrLog();
//		AFLog.d("****************************************************************");
		writeLog(abstractAF, edrConf, logEDR);
		AFLog.d("write EDR success");
//		AFLog.d("****************************************************************");
	}

	private static String buildEDRLog(AbstractAF abstractAF, AFInstance afInstance, SubEDR subEDR) throws Exception {
//		AFLog.d("Start build Log EDR");
		String delimitor = ",";
		String leftSquareBracket = "[";
		String rightSquareBracket = "]";
		String appName = abstractAF.getEquinoxProperties().getApplicationName();
		String component = "SRFC";
		String timeStampIn = subEDR.getTimeStampIn();
		String recordName = subEDR.getRecordName();
		String sessionId = subEDR.getSessionId();
		String destinationServiceName = subEDR.getDestinationServiceName();
		String invokeId = subEDR.getInvokeId();
		String receivedEvent = subEDR.getReceivedEvent().toString();
		String serviceResult = subEDR.getServiceResult();
		String method = subEDR.getMethod();
		String command = subEDR.getCommand();
		String counter = "0";
		String invokeParrent = subEDR.getInvokeParrent();
		String responseTime = subEDR.getResponseTime();
		String serviceOrderId = subEDR.getServiceOrderId();
		String serviceOrderType = subEDR.getServiceOrderType();
		String resourceGroupId = subEDR.getResourceGroupId();
		String resourceOrderId = subEDR.getResourceOrderId();
		String resourceName = subEDR.getResourceName();
		String mobileNo = subEDR.getMobileNo();
		String imsi = subEDR.getImsi();
		String taskId = subEDR.getTaskId();
		String neId = subEDR.getNeId();
		String status = subEDR.getStatus();
		String errorDesc = subEDR.getErrorDesc();
		String subscriberIdKey = "";
		String customerOrderId = subEDR.getCustomerOrderId();
		String customerOrderType = subEDR.getCustomerOrderType();
		/* wait feature Async */
		String invokeResp = subEDR.getInvokeId();
		String statsIn = subEDR.getStatsIn();
		String statsOut = subEDR.getStatsOut();
		String statsExeTime = subEDR.getStatsExeTime();
		String resourceOrderType = subEDR.getResourceOrderType();
		StringBuilder log = new StringBuilder();

		log.append(appName + delimitor);
		log.append(component + delimitor);
		log.append(timeStampIn + delimitor);
		log.append(recordName + delimitor);
		log.append(sessionId + delimitor);
		log.append(destinationServiceName + delimitor);
		log.append(invokeParrent + delimitor);
		log.append(receivedEvent + delimitor);
		log.append(serviceResult + delimitor);
		log.append(responseTime + delimitor);
		log.append(method + delimitor);
		log.append(command + delimitor);
		log.append(counter + delimitor);
		log.append(invokeId + delimitor + leftSquareBracket);
		log.append(invokeResp + delimitor);
		log.append(customerOrderId + delimitor);
		log.append(customerOrderType + delimitor);
		log.append(serviceOrderId + delimitor);
		log.append(serviceOrderType + delimitor);
		log.append(resourceGroupId + delimitor);
		log.append(resourceOrderId + delimitor);
		log.append(resourceOrderType + delimitor);
		log.append(resourceName + delimitor);
		log.append(mobileNo + delimitor);
		log.append(imsi + delimitor);
		log.append(subscriberIdKey + delimitor);
		log.append(taskId + delimitor);
		log.append(neId + delimitor);
		log.append(status + delimitor);
		log.append(errorDesc + delimitor);
		log.append(statsOut + delimitor);
		log.append(statsIn + delimitor);
		log.append(statsExeTime + rightSquareBracket);
		AFLog.d("End build log EDR");
		return log.toString();
	}

	public static void writeLogCDR(AbstractAF abstractAF, AFSubInstance afSubIns, Map<String, String> map)
			throws Exception {
//		AFLog.d("***************************************************************");
//		AFLog.d("Start write CDR");
		SubCDR subCDR = new SubCDR();
		DateTime StampIn = new DateTime();
		String timeStampInCDR = Config.getFormatDateWithMiTz().print(StampIn);
		Param_IDLE_ResourceOrder param = (Param_IDLE_ResourceOrder) afSubIns.getSubClientParameter();

		if (param != null) {
			if (param.getCustomerOrderId() != null && !param.getCustomerOrderId().isEmpty()) {
				subCDR.setCustomerOrderId(param.getCustomerOrderId());
			}
			if (param.getCustomerOrderType() != null && !param.getCustomerOrderType().isEmpty()) {
				subCDR.setCustomerOrderType(param.getCustomerOrderType());
			}
			if (param.getResourceOrderType() != null && !param.getResourceOrderType().isEmpty()) {
				subCDR.setResourceOrderType(param.getResourceOrderType());
			}
		}
		if (afSubIns.getSubClientParameter() != null) {
			for (int i = 0; i < param.getResourceItemList().size(); i++) {
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonResourceItem = (JsonObject) jsonParser.parse(param.getResourceItemList().get(i))
						.getAsJsonObject();
				Gson gson = GsonPool.getGson();
				HashMap<String, Object> hashmapResourceItem = new HashMap<>();
				hashmapResourceItem = (HashMap<String, Object>) gson.fromJson(jsonResourceItem,
						hashmapResourceItem.getClass());
				subCDR = setImsiMobileNoCDR(hashmapResourceItem, subCDR);
				
			}
		} else {
			AFLog.w("*************** Client message request = null ************************");
		}
		subCDR.setSessionId(
				abstractAF.getEquinoxProperties().getSession() != null ? abstractAF.getEquinoxProperties().getSession()
						: "");
		subCDR.setInvokeParrent(afSubIns.getSubInitInvoke() != null ? afSubIns.getSubInitInvoke() : "");
		subCDR.setInvokeResp(afSubIns.getSubInvokeResp() != null ? afSubIns.getSubInvokeResp() : "");
		subCDR.setRet(afSubIns.getRet() != null ? afSubIns.getRet() : "");
		subCDR.setMethod(afSubIns.getSubInitMethod() != null ? afSubIns.getSubInitMethod() : "");
		subCDR.setOriginserviceName(
				Config.getServerInterfaceHashMap().get(afSubIns.getSubClientHashMapParameter().get("userSys")) != null
						? Config.getServerInterfaceHashMap().get(afSubIns.getSubClientHashMapParameter().get("userSys"))
						: "");
		if (!param.getResourceGroupId().isEmpty() && param.getResourceGroupId() != null) {
			subCDR.setResourceGroupId(param.getResourceGroupId());
		}
		if (afSubIns.getRequestId() != null && !afSubIns.getRequestId().isEmpty()) {
			subCDR.setResourceOrderId(afSubIns.getRequestId());
		}
		if (!param.getServiceOrderId().isEmpty() && param.getServiceOrderId() != null) {
			subCDR.setServiceOrderId(param.getServiceOrderId());
		}
		if (!param.getServiceOrderType().isEmpty() && param.getServiceOrderType() != null) {
			subCDR.setServiceOrderType(param.getServiceOrderType());
		}
		subCDR.setRecordType("F");

		if (!afSubIns.getUrlRespResult().equals(ECommand.UNKNOWN.getCommand())) {
			subCDR.setRecordName(afSubIns.getUrlRespResult());
		} else {
			subCDR.setRecordName(afSubIns.getSubInitURL());
		}

		if (subCDR.getRecordName().contains("/sync")) {
			/* Sync yes flow, no flow [10] */
			if (afSubIns.getSubResultCodeJourney() != null) {
				subCDR.setResultCode(afSubIns.getSubResultCodeJourney());

				if (afSubIns.getSubResultCodeJourney().equals(EResultCode.RE20000.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE20000.getResultCode());
				} else if (afSubIns.getSubResultCodeJourney().equals(EResultCode.RE20001.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE20001.getResultCode());
				} else if (afSubIns.getSubResultCodeJourney().equals(EResultCode.RE00001.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE00001.getResultCode());
				} else if (afSubIns.getSubResultCodeJourney().equals(EResultCode.RE00002.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE00002.getResultCode());
				} else if (afSubIns.getSubResultCodeJourney().equals(EResultCode.RE00003.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE00003.getResultCode());
				} else if (afSubIns.getSubResultCodeJourney().equals(EResultCode.RE00004.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE00004.getResultCode());
				} else if (afSubIns.getSubResultCodeJourney().equals(EResultCode.RE40300.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE40300.getResultCode());
				} else if (afSubIns.getSubResultCodeJourney().equals(EResultCode.RE40400.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE40400.getResultCode());
				} else if (afSubIns.getSubResultCodeJourney().equals(EResultCode.RE40401.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE40401.getResultCode());
				} else if (afSubIns.getSubResultCodeJourney().equals(EResultCode.RE50000.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE50000.getResultCode());
				} else if (afSubIns.getSubResultCodeJourney().equals(EResultCode.RE50019.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE50019.getResultCode());
				}
			}
		} else {
			if (afSubIns.getParam_HRZ_PostResult() != null) {
				/* Async yes flow resultCode[10] */
				subCDR.setResultCode(afSubIns.getParam_HRZ_PostResult().getResultCode());
				if (afSubIns.getParam_HRZ_PostResult().getResultCode().equals(EResultCode.RE20000.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE20000.getResultCode());
				} else if (afSubIns.getParam_HRZ_PostResult().getResultCode()
						.equals(EResultCode.RE20001.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE20001.getResultCode());
				} else if (afSubIns.getParam_HRZ_PostResult().getResultCode()
						.equals(EResultCode.RE00001.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE00001.getResultCode());
				} else if (afSubIns.getParam_HRZ_PostResult().getResultCode()
						.equals(EResultCode.RE00002.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE00002.getResultCode());
				} else if (afSubIns.getParam_HRZ_PostResult().getResultCode()
						.equals(EResultCode.RE00003.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE00003.getResultCode());
				} else if (afSubIns.getParam_HRZ_PostResult().getResultCode()
						.equals(EResultCode.RE00004.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE00004.getResultCode());
				} else if (afSubIns.getParam_HRZ_PostResult().getResultCode()
						.equals(EResultCode.RE40300.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE40300.getResultCode());
				} else if (afSubIns.getParam_HRZ_PostResult().getResultCode()
						.equals(EResultCode.RE40400.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE40400.getResultCode());
				} else if (afSubIns.getParam_HRZ_PostResult().getResultCode()
						.equals(EResultCode.RE40401.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE40401.getResultCode());
				} else if (afSubIns.getParam_HRZ_PostResult().getResultCode()
						.equals(EResultCode.RE50000.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE50000.getResultCode());
				} else if (afSubIns.getParam_HRZ_PostResult().getResultCode()
						.equals(EResultCode.RE50019.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE50019.getResultCode());
				}
			} else {
				/* Async no flow resultCode[7] */
				if (afSubIns.getResAckResultCode() != null) {
					subCDR.setResultCode(afSubIns.getResAckResultCode());
				}
				if (afSubIns.getSubResultCode().equals(EResultCode.RE20000.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE20000.getResultCode());
				} else if (afSubIns.getSubResultCode().equals(EResultCode.RE20001.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE20001.getResultCode());
				} else if (afSubIns.getSubResultCode().equals(EResultCode.RE00001.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE00001.getResultCode());
				} else if (afSubIns.getSubResultCode().equals(EResultCode.RE00002.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE00002.getResultCode());
				} else if (afSubIns.getSubResultCode().equals(EResultCode.RE00003.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE00003.getResultCode());
				} else if (afSubIns.getSubResultCode().equals(EResultCode.RE00004.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE00004.getResultCode());
				} else if (afSubIns.getSubResultCode().equals(EResultCode.RE40300.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE40300.getResultCode());
				} else if (afSubIns.getSubResultCode().equals(EResultCode.RE40400.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE40400.getResultCode());
				} else if (afSubIns.getSubResultCode().equals(EResultCode.RE40401.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE40401.getResultCode());
				} else if (afSubIns.getSubResultCode().equals(EResultCode.RE50000.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE50000.getResultCode());
				} else if (afSubIns.getSubResultCode().equals(EResultCode.RE50019.getResultCode())) {
					subCDR.setResponseStatus(EResultCode.RE50019.getResultCode());
				}
			}

			if (afSubIns.getState() != null && !afSubIns.getState().isEmpty()) {
				subCDR.setRequestStatus(afSubIns.getState());
			}
		}
		Calendar stampInit = Calendar.getInstance();
		Calendar stampResult = Calendar.getInstance();
		Calendar stampReport = Calendar.getInstance();
		DateTime timeStampInitCDR = Config.getFormatDateWithMiTz().parseDateTime(afSubIns.getSubInitTimeStampIn());
		stampInit.setTime(timeStampInitCDR.toDate());
		long currentInit = stampInit.getTimeInMillis();
		AFLog.d("CDR TimeStamp WriteCDR[now] :" + timeStampInCDR);
		AFLog.d("CDR TimeStamp Initial [4] :" + afSubIns.getSubInitTimeStampIn());
		AFLog.d("CDR TimeStamp Client_PostResult [10] :" + afSubIns.getSubCompleteDateCDR());
		AFLog.d("CDR TimeStamp END_APP [13] :" + timeStampInCDR);
		stampReport.setTime(StampIn.toDate());
		long currentReport = stampReport.getTimeInMillis();
		long initMinusReport = currentReport - currentInit;
		stampReport.setTimeInMillis(initMinusReport);
		subCDR.setRespTime(String.valueOf(stampReport.getTimeInMillis()));
		if (afSubIns.getSubCompleteDateCDR() != null && !afSubIns.getSubCompleteDateCDR().equals("")) {
			stampResult
					.setTime(Config.getFormatDateWithMiTz().parseDateTime(afSubIns.getSubCompleteDateCDR()).toDate());
		} else {
			stampResult.setTime(Config.getFormatDateWithMiTz().parseDateTime(afSubIns.getSubAckReqCDR()).toDate());
		}
		long currentResult = stampResult.getTimeInMillis();
		long initMinusResult = currentResult - currentInit;
		stampResult.setTimeInMillis(initMinusResult);
		subCDR.setRealTime(String.valueOf(stampResult.getTimeInMillis()));

		subCDR.setTimeStampIn(timeStampInCDR);
		if (!afSubIns.getSubResourceOrderValidateMessage().isEmpty()) {
			afSubIns.setSubResourceOrderValidateMessage("Failed: " + afSubIns.getSubResourceOrderValidateMessage());
		}
		subCDR.setErrMessage(StringUtils.isNotBlank(afSubIns.getSubResourceOrderValidateMessage())
				? afSubIns.getSubResourceOrderValidateMessage()
				: afSubIns.getErrorMessage());
		subCDR.setErrMessage("\"" + subCDR.getErrMessage() + "\"");

		String logCDR = buildCDRLog(abstractAF, subCDR);
		String cdrConf = Config.getCdrLog();
		writeLog(abstractAF, cdrConf, logCDR);
		AFLog.d("write CDR success");
	}

	private static String buildCDRLog(AbstractAF abstractAF, SubCDR subCDR) throws Exception {
		String rightSquareBracket = "[";
		String leftSquareBracket = "]";
		String delimitor = ",";
		String appName = abstractAF.getEquinoxProperties().getApplicationName();
		String component = "SRFC";
		String recordType = subCDR.getRecordType();
		String timeStampIn = subCDR.getTimeStampIn();// CurrentLogdate
		String recordName = subCDR.getRecordName();
		String session = subCDR.getSessionId();
		String originatingServiceName = subCDR.getOriginserviceName();
		String invokeParrent = subCDR.getInvokeParrent();
		String counter = "0";// retryCount
		String ret = subCDR.getRet();
		String method = subCDR.getMethod();
		String command = "";
		String invokeReq = subCDR.getInvokeParrent();
		String invokeResp = subCDR.getInvokeResp();
		String serviceOrderId = subCDR.getServiceOrderId();
		String serviceOrderType = subCDR.getServiceOrderType();
		String requestStatus = subCDR.getRequestStatus();
		String responseStatus = subCDR.getResponseStatus();
		String realTime = subCDR.getRealTime();
		String mobileNo = subCDR.getMobileNo();
		String imsi = subCDR.getImsi();
		String errMessage = subCDR.getErrMessage();
		String waitTime = "0";
		String resultCode = subCDR.getResultCode();
		String respTime = subCDR.getRespTime();
		String resourceGroupId = subCDR.getResourceGroupId();
		String resourceOrderId = subCDR.getResourceOrderId();
		String subscriberIdKey = "";
		String customerOrderId = subCDR.getCustomerOrderId();
		String customerOrderType = subCDR.getCustomerOrderType();
		String resourceOrderType = subCDR.getResourceOrderType();
		StringBuilder log = new StringBuilder();

		log.append(appName + delimitor);
		log.append(component + delimitor);
		log.append(timeStampIn + delimitor);
		log.append(recordName + delimitor);
		log.append(session + delimitor);
		log.append(originatingServiceName + delimitor);
		log.append(invokeParrent + delimitor);
		log.append(ret + delimitor);
		log.append(resultCode + delimitor);
		log.append(respTime + delimitor);
		log.append(method + delimitor);
		log.append(command + delimitor);
		log.append(counter + delimitor);
		log.append(invokeReq + delimitor + rightSquareBracket);
		log.append(invokeResp + delimitor);
		log.append(recordType + delimitor);
		log.append(customerOrderId + delimitor);
		log.append(customerOrderType + delimitor);
		log.append(serviceOrderId + delimitor);
		log.append(serviceOrderType + delimitor);
		log.append(resourceGroupId + delimitor);
		log.append(resourceOrderId + delimitor);
		log.append(resourceOrderType + delimitor);
		log.append(requestStatus + delimitor);
		log.append(responseStatus + delimitor);
		log.append(realTime + delimitor);
		log.append(mobileNo + delimitor);
		log.append(imsi + delimitor);
		log.append(subscriberIdKey + delimitor);
		log.append(errMessage + delimitor);
		log.append(waitTime + leftSquareBracket);
		return log.toString();
	}

	public static void writeTransactionLog(AbstractAF abstractAF, AFSubInstance afSubIns, AFInstance afInstance,
			EquinoxRawData eqxRawData) throws Exception {

		AFLog.d("Start write Transaction Log");

		AFSubInstance resourceOrderIns = afInstance.getMainSubInstance(afSubIns.getSubResourceOrderIns());
		// Param_IDLE_ResourceOrder param_IDLE_ResourceOrder =
		// (Param_IDLE_ResourceOrder)resourceOrderIns.getSubClientParameter();
		String suppcode = afSubIns.getCurrentSuppcode();
		ResourceMappingCommand resourceMappingCommand = Config.getResourceMappingCommandHashMap().get(suppcode);
		String RespRawData = eqxRawData.getRawDataMessage();
		String ReqRawData = afSubIns.getRequestMessage();
		AFLog.d("Encode Base64 " + resourceMappingCommand.getMessageFormat());

		if (resourceMappingCommand.getMessageFormat().equalsIgnoreCase("cmdlinebase64")) {
			byte[] respcommandline = Base64.getDecoder().decode(RespRawData);
			RespRawData = new String(respcommandline);

			byte[] reqcommandline = Base64.getDecoder().decode(ReqRawData);
			ReqRawData = new String(reqcommandline);
			AFLog.d("Encode Base64");
		}

		RespRawData = unescape(RespRawData);

		TransactionLog transactionLog = new TransactionLog();
		transactionLog.setActor("Pegazus");
		transactionLog.setInvokeId(eqxRawData.getInvoke());
		transactionLog.setResourceName(afSubIns.getSubClientHashMapParameter().get("resourceName").toString());
		transactionLog.setResourceOrderId(resourceOrderIns.getRequestId());
		transactionLog.setNeId(afSubIns.getSubNeId());
		transactionLog.setTaskId(transactionLog.getInvokeId().split("\\.")[5]);
		transactionLog.setSuppcode(afSubIns.getCurrentSuppcode());
		transactionLog.setReqRawData(ReqRawData);
		transactionLog.setRespRawData(RespRawData);

		String logTransection = buildTransectionLog(abstractAF, afInstance, transactionLog);
		String TransactionConf = Config.getTransactionLog();
//		AFLog.d("****************************************************************");
		AFLog.d("Log Transaction");

		writeLog(abstractAF, TransactionConf, logTransection);
		AFLog.d("write TransactionLog success");
	}

	private static String buildTransectionLog(AbstractAF abstractAF, AFInstance afInstance,
			TransactionLog transectionLog) throws Exception {

		String delimitor = "``";
		String actor = transectionLog.getActor();
		String invokeId = transectionLog.getInvokeId();
		String resourceName = transectionLog.getResourceName();
		String resourceOrderId = transectionLog.getResourceOrderId();
		String neId = transectionLog.getNeId();
		String taskId = transectionLog.getTaskId();
		String suppcode = transectionLog.getSuppcode();
		String reqRawData = transectionLog.getReqRawData();
		String respRawData = transectionLog.getRespRawData();

		StringBuilder log = new StringBuilder();

		log.append(actor + delimitor);
		log.append(invokeId + delimitor);
		log.append(invokeId + delimitor);
		log.append(resourceOrderId + delimitor);
		log.append(resourceName + delimitor);
		log.append(taskId + delimitor);
		log.append(neId + delimitor);
		log.append(suppcode + delimitor);
		log.append(reqRawData + delimitor);
		log.append(respRawData);

		return log.toString();
	}

	public static void writeIncomingUnknownLog(AbstractAF abstractAF, AFSubInstance afSubIns, AFInstance afInstance,
			EquinoxRawData eqxRawData) throws Exception {
		AFLog.d("Start write Incoming Unknown Message Log");

		String RawData = eqxRawData.getRawDataMessage();

		RawData = unescape(RawData);
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(RawData);
			RawData = new String(decodedBytes);
			AFLog.d("Message is a base64");
		} catch (Exception e) {
			AFLog.d("Message is not a base64");
		}

		IncomingUnknownLog incomingUnknownLog = new IncomingUnknownLog();
		incomingUnknownLog.setIncomingTime(afInstance.getMainTimeStampIncoming());
		incomingUnknownLog.setOrig(eqxRawData.getOrig());
		incomingUnknownLog.setInvokeId(eqxRawData.getInvoke());
		incomingUnknownLog.setMessage(RawData);

		String logIncomingUnknown = buildIncomingUnknownLog(abstractAF, afInstance, incomingUnknownLog);
		String IncomingUnknownConf = Config.getIncomingUnknownLog();

//		AFLog.d("****************************************************************");
		AFLog.d("Log Incoming Unknown Message");
		writeLog(abstractAF, IncomingUnknownConf, logIncomingUnknown);
		AFLog.d("write IncomingUnknownLog success");
	}

	private static String buildIncomingUnknownLog(AbstractAF abstractAF, AFInstance afInstance,
			IncomingUnknownLog incomingUnknownLog) throws Exception {

		String delimitor = "|";
		String time = incomingUnknownLog.getIncomingTime();
		String orig = incomingUnknownLog.getOrig();
		String invokeId = incomingUnknownLog.getInvokeId();
		String message = incomingUnknownLog.getMessage().replaceAll("\r\n|\n|\r|\t", "");

		StringBuilder log = new StringBuilder();

		log.append(time + delimitor);
		log.append(orig + delimitor);
		log.append(invokeId + delimitor);
		log.append(message);

		// JsonObject jsonLog = new JsonObject();

		// jsonLog.addProperty("Incoming time stamp", time);
		// jsonLog.addProperty("Orig", orig);
		// jsonLog.addProperty("InvokeId", invokeId);
		// jsonLog.addProperty("Message", message);

		// System.out.println("Start write Incoming Unknown Message Log: " + log);
		// System.out.println("Start write Incoming Unknown Message JSON: " + jsonLog);
		return log.toString();

	}

	private static void writeLog(AbstractAF abstractAF, String logName, String data) {
//		AFLog.d("*****************************************************************************************************************************************************");
		AFLog.d("write log E06 : " + data.replaceAll("\n", "").replaceAll("\r", ""));
//		AFLog.d("*****************************************************************************************************************************************************");
		abstractAF.getEquinoxUtils().writeLog(logName, data.replaceAll("\n", "").replaceAll("\r", ""));
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

	private static String errorDescFormEqx(String ret) {
		if (ret.equals(ERet.RET1.getRet())) {
			return "\"" + ERet.RET1.getRetDesc() + "\"";
		} else if (ret.equals(ERet.RET2.getRet())) {
			return "\"" + ERet.RET2.getRetDesc() + "\"";
		} else if (ret.equals(ERet.RET3.getRet())) {
			return "\"" + ERet.RET3.getRetDesc() + "\"";
		} else if (ret.equals(ERet.RET4.getRet())) {
			return "\"" + ERet.RET4.getRetDesc() + "\"";
		} else {
			return "\"" + "Success" + "\"";
		}
	}

	private static SubEDR clearNullEdr(SubEDR subEdr) {

		if (subEdr.getRecordName() == null) {
			subEdr.setRecordName("");
		}
		if (subEdr.getDestinationServiceName() == null) {
			subEdr.setDestinationServiceName("");
		}
		if (subEdr.getReceivedEvent() == null) {
			subEdr.setReceivedEvent("");
		}
		if (subEdr.getServiceResult() == null) {
			subEdr.setServiceResult("");
		}
		if (subEdr.getServiceOrderId() == null) {
			subEdr.setServiceOrderId("");
		}
		if (subEdr.getServiceOrderType() == null) {
			subEdr.setServiceOrderType("");
		}
		if (subEdr.getResourceGroupId() == null) {
			subEdr.setResourceGroupId("");
		}
		if (subEdr.getResourceOrderId() == null) {
			subEdr.setResourceOrderId("");
		}
		if (subEdr.getResourceName() == null) {
			subEdr.setResourceName("");
		}
		if (subEdr.getMobileNo() == null) {
			subEdr.setMobileNo("");
		}
		if (subEdr.getImsi() == null) {
			subEdr.setImsi("");
		}
		if (subEdr.getNeId() == null) {
			subEdr.setNeId("");
		}
		if (subEdr.getStatus() == null) {
			subEdr.setStatus("");
		}
		if (subEdr.getErrorDesc() == null) {
			subEdr.setErrorDesc("");
		}
		if (subEdr.getCustomerOrderId() == null) {
			subEdr.setCustomerOrderId("");
		}
		if (subEdr.getCustomerOrderType() == null) {
			subEdr.setCustomerOrderType("");
		}
		if (subEdr.getResourceOrderType() == null) {
			subEdr.setResourceOrderType("");
		}
		return subEdr;
	}
	public static SubEDR setImsiMobileNoEDR(HashMap<String, Object> resourceMap, SubEDR subEDR) {
		if (resourceMap.containsKey("mobileNo")) {
			if (resourceMap.get("mobileNo") != null && !resourceMap.get("mobileNo").toString().isEmpty()) {
				subEDR.setMobileNo(resourceMap.get("mobileNo").toString());
			}
		}
		if (resourceMap.containsKey("imsi")) {
			if (resourceMap.get("imsi") != null && !resourceMap.get("imsi").toString().isEmpty()) {
				subEDR.setImsi(resourceMap.get("imsi").toString());
			}
		}

		return subEDR;
	}

	public static SubCDR setImsiMobileNoCDR(HashMap<String, Object> resourceMap, SubCDR subCDR) {
		if (resourceMap.containsKey("mobileNo")) {
			if (resourceMap.get("mobileNo") != null && !resourceMap.get("mobileNo").toString().isEmpty()) {
				subCDR.setMobileNo(resourceMap.get("mobileNo").toString());
			}
		}
		if (resourceMap.containsKey("imsi")) {
			if (resourceMap.get("imsi") != null && !resourceMap.get("imsi").toString().isEmpty()) {
				subCDR.setImsi(resourceMap.get("imsi").toString());
			}
		}

		return subCDR;
	}

}
