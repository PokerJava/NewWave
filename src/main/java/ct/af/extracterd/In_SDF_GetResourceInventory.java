package ct.af.extracterd;

import org.joda.time.Duration;
import ct.af.enums.EResultCode;
import ct.af.enums.ERet;
import ct.af.enums.EStats;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_SDF_GetResourceInventory;
import ct.af.message.incoming.parser.Parser_SDF_GetResourceInventory;
import ct.af.resourceModel.ResourceInventory;
import ct.af.utils.Config;
import ct.af.utils.LogUtils;
import ct.af.utils.ResourceInventoryFromFile;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class In_SDF_GetResourceInventory {

	public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns,
			EquinoxRawData eqxRawData) {
//		AFLog.d("In_SDF_GetResourceInventory");
		String rawDataRet = eqxRawData.getRet();
		String rawDataECode = eqxRawData.getRawDataAttribute("ecode");
		String rawDataVal = eqxRawData.getRawDataAttribute("val");

		EResultCode resultCode;
		EResultCode internalCode;
		String insNo = afSubIns.getSubInitOrig().split("\\.")[afSubIns.getSubInitOrig().split("\\.").length - 1];
		String nodeName = "AISSDF_" + insNo;
		EStats statsIn;

		boolean isValid = false;
		Param_SDF_GetResourceInventory param;
		if (rawDataVal != null && !rawDataVal.equals("") && rawDataECode.equals("200")
				&& rawDataRet.equals(ERet.RET0.getRet())) {

			Parser_SDF_GetResourceInventory parser = new Parser_SDF_GetResourceInventory();
			param = parser.doParser(eqxRawData, afInstance, afSubIns);
			isValid = param.getIsValid();
			afSubIns.setSubResultCodeEDR(param.getResultCode());
			afSubIns.setSubResultDescEDR(param.getResultDescription());

			if (isValid) {

//				AFLog.d("getResultCode: \"" + param.getResultCode() + "\"");
				if (param.getResultCode().equals(EResultCode.RE20000.getResultCode())) {
					boolean allDataIsValid = false;
					if (param.getResultData() != null && param.getResultData().size() > 0) {
						allDataIsValid = true;
//						AFLog.d("========================= resourceInventory =========================");
						for (ResourceInventory inventory : param.getResultData()) {
							if (inventory == null) {
								AFLog.d("Found null value in inventory!!");
								continue;
							} else {
//								AFLog.d("Validating...");
								if (inventory.getResourceName() == null || inventory.getResourceName().equals("")) {
									allDataIsValid = false;
									AFLog.d("missing resourceName");
								} else if (inventory.getResourceState() == null
										|| inventory.getResourceState().equals("")) {
									allDataIsValid = false;
									AFLog.d("missing resourceState");
								} else if (inventory.getQuotaPlan() == null || inventory.getQuotaPlan().equals("")) {
									allDataIsValid = false;
									AFLog.d("missing quotaPlan");
								} else if (inventory.getIdentityType() == null
										|| inventory.getIdentityType().equals("")) {
									allDataIsValid = false;
									AFLog.d("missing identityType");
								} 
//								else {
//									AFLog.d("OK");
//								}
								Config.getResourceInventoryHashMap().put(String.valueOf(inventory.getResourceName()),
										inventory);
//								AFLog.d(inventory.toString());
							}
						}
					}
//					AFLog.d("========================================================================");
					AFLog.d("resourceInventory : " + Config.getResourceInventoryHashMap().size() + " records");
					if (allDataIsValid) {
						resultCode = EResultCode.RE20000;
						internalCode = EResultCode.RE20000;
						statsIn = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_SUCCESS;
						EStats statexe = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_EXE_TIME;
						long resTime = new Duration(
								Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampOut()),
								Config.getFormatDateWithMiTz().parseDateTime(afInstance.getMainTimeStampIncoming()))
										.getMillis();
						statexe.setExeTimeCustomStat(statexe, nodeName, resTime);
						afSubIns.setStatsExeTime(statexe);
//						new ResourceInventoryFromFile()
//								.writeBackUpResourceInven((eqxRawData.getRawDataAttribute("val")));

					} else {
						resultCode = EResultCode.RE40300;
						internalCode = EResultCode.RE40300;
						statsIn = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_SUCCESS_WITH_ERROR;

					}

				} else if (param.getResultCode() == null || param.getResultDescription() == null) {
					resultCode = EResultCode.RE40300;
					internalCode = EResultCode.RE40300;
					statsIn = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_SUCCESS_WITH_ERROR;

				} else if (param.getResultCode().equals("") || param.getResultDescription().equals("")) {
					resultCode = EResultCode.RE40300;
					internalCode = EResultCode.RE40300;
					statsIn = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_SUCCESS_WITH_ERROR;

				} else if (param.getResultCode().equals(EResultCode.RE40401.getResultCode())) {
					resultCode = EResultCode.RE40401;
					internalCode = EResultCode.RE40401;
					statsIn = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_SUCCESS_WITH_ERROR;

				} else {
					AFLog.d("getResultCode not equals RE20000 or RE40401");
					resultCode = EResultCode.RE50000;
					internalCode = EResultCode.RE50000;
					statsIn = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_SUCCESS_WITH_ERROR;

				}

			} else {

				resultCode = EResultCode.RE40300;
				internalCode = EResultCode.RE40300;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_SUCCESS_WITH_ERROR;

			}
			afSubIns.setResultDesc(resultCode.getResultDesc());

		} else {

			if (rawDataRet.equals(ERet.RET4.getRet())) {
				resultCode = EResultCode.RE00004;
				internalCode = EResultCode.RE50000;
				resultCode.getResultCode();
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_TIMEOUT;
				afSubIns.setSubResultCodeEDR(resultCode.getResultCode());
				afSubIns.setSubResultDescEDR(resultCode.getResultDesc());

			} else if (rawDataRet.equals(ERet.RET2.getRet())) {
				resultCode = EResultCode.RE00002;
				internalCode = EResultCode.RE50000;
				resultCode.getResultCode();
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_REJECT;
				afSubIns.setSubResultCodeEDR(resultCode.getResultCode());
				afSubIns.setSubResultDescEDR(resultCode.getResultDesc());

			} else if (rawDataECode.equals("403")) {
				resultCode = EResultCode.RE40300;
				internalCode = EResultCode.RE50000;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_UNKNOWN_ERROR;
				afSubIns.setSubResultCodeEDR(resultCode.getResultCode());
				afSubIns.setSubResultDescEDR(resultCode.getResultDesc());
			}

			else if (rawDataECode.equals("503")) {
				resultCode = EResultCode.RE50000;
				internalCode = EResultCode.RE50000;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_UNKNOWN_ERROR;
				afSubIns.setSubResultCodeEDR(resultCode.getResultCode());
				afSubIns.setSubResultDescEDR(resultCode.getResultDesc());
			} else if (!rawDataECode.equals("200")) {
				resultCode = EResultCode.RE50000;
				internalCode = EResultCode.RE50000;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_UNKNOWN_ERROR;
				afSubIns.setSubResultCodeEDR(resultCode.getResultCode());
				afSubIns.setSubResultDescEDR(resultCode.getResultDesc());
			} else {
				resultCode = EResultCode.RE50000;
				internalCode = EResultCode.RE50000;
				statsIn = EStats.APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_UNKNOWN_ERROR;
				afSubIns.setSubResultCodeEDR(resultCode.getResultCode());
				afSubIns.setSubResultDescEDR(resultCode.getResultDesc());
			}

		}

		afSubIns.setSubCountChild(afSubIns.getSubCountChild() - 1);

		afSubIns.setSubIsValid(isValid);
		afSubIns.setSubResultCode(resultCode.getResultCode());
		afSubIns.setSubInternalCode(internalCode.getResultCode());
		afSubIns.setSubNextState(ESubState.Unknown.toString());
		afSubIns.setSubCurrentState(ESubState.SDF_GETRESOURCEINVENTORY.toString());
		afSubIns.setSubControlState(ESubState.SDF_GETRESOURCEINVENTORY.toString());

		// -- Stats --//
		statsIn.setCustomStat(statsIn, nodeName);
		afSubIns.setStatsIn(statsIn);

		try {
			LogUtils.writeLogEDR(abstractAF, afSubIns, afInstance, eqxRawData);
		} catch (Exception e1) {
			AFLog.e("[Exception] writeLogEDR error.");
			AFLog.e(e1);
		}

		// -- Max Retry --//
		int maxRetry = Config.getSdfMaxretry();

		if (!afSubIns.getSubInternalCode().equals(EResultCode.RE20000.getResultCode())
				&& afSubIns.getSubCountRetryNe() < maxRetry) {
			afSubIns.incrementSubCountRetryNe();
			afSubIns.setSubNextState(afSubIns.getSubCurrentState());
		} else {
			afSubIns.setSubCountRetryNe(0);
		}

		return afSubIns;

	}
}
