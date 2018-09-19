package ct.af.message.incoming.parser;

import java.util.ArrayList;
import java.util.TreeMap;

import ct.af.enums.EResultCode;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_Dgw;
import ct.af.message.incoming.parameter.Param_IDLE_Pgw;
import ct.af.utils.DiameterUtils;
import ct.af.utils.ValidateUtils;
import ec02.af.abstracts.AbstractAF;
import ec02.data.interfaces.EquinoxRawData;

public class Parser_IDLE_Dgw {
	public Param_IDLE_Dgw doParser(AbstractAF abstractAF, EquinoxRawData eqxRawData, AFInstance afInstance,
			AFSubInstance afSubIns) {
		Param_IDLE_Dgw paramDgw = new Param_IDLE_Dgw();
		boolean isToolParser = false;
		String requestMessageCcrI = //"<ERDData>"+
				"<Session-Id value=\"71:1002064297:GatewayService-4-13-1.SUK8N.awn.com;1393540200;2147818684\" />\r\n"+
				"<Auth-Application-Id value=\"16777238\" />\r\n"+
				"<Origin-Host value=\"GatewayService-4-13-1.SUK8N.awn.com\" />\r\n" + 
				"<Origin-Realm value=\"awn.com\" />\r\n" + 
				"<Destination-Realm value=\"toro.awn.co.th\" />\r\n" + 
				"<CC-Request-Type value=\"1\" />\r\n" + 
				"<CC-Request-Number value=\"0\" />\r\n" + 
				"<Destination-Host value=\"PCRF3g231\" />\r\n" + 
				"<Origin-State-Id value=\"7\" />\r\n" + 
				"<Subscription-Id>\r\n" + 
				"	<Subscription-Id-Type value=\"1\" />\r\n" + 
				"	<Subscription-Id-Data value=\"520032100481461\" />\r\n" + 
				"</Subscription-Id>\r\n" + 
				"<Subscription-Id>\r\n" + 
				"	<Subscription-Id-Type value=\"0\" />\r\n" + 
				"	<Subscription-Id-Data value=\"66931427952\" />\r\n" + 
				"</Subscription-Id>\r\n" + 
				"<Supported-Features>\r\n" + 
				"	<Vendor-Id value=\"10415\" />\r\n" + 
				"	<Feature-List-ID value=\"1\" />\r\n" + 
				"</Supported-Features>\r\n" + 
				"<Network-Request-Support value=\"0\" />\r\n" + 
				"<Bearer-Identifier value=\"0x33363138363931393831\" />\r\n" + 
				"<Bearer-Operation value=\"1\" />\r\n" + 
				"<Framed-IP-Address value=\"0x0a73d033\" />\r\n" + 
				"<IP-CAN-Type value=\"0\" />\r\n" + 
				"<RAT-Type value=\"1000\" />\r\n" + 
				"<User-Equipment-Info>\r\n" + 
				"	<User-Equipment-Info-Type value=\"0\" />\r\n" + 
				"	<User-Equipment-Info-Value value=\"0x30313330353630303836393633313132\" />\r\n" + 
				"</User-Equipment-Info>\r\n" + 
				"<QoS-Information>\r\n" + 
				"	<QoS-Class-Identifier value=\"8\" />\r\n" + 
				"	<Max-Requested-Bandwidth-UL value=\"8640000\" />\r\n" + 
				"	<Max-Requested-Bandwidth-DL value=\"42000000\" />\r\n" + 
				"	<Bearer-Identifier value=\"0x33363138363931393831\" />\r\n" + 
				"	<Allocation-Retention-Priority>\r\n" + 
				"		<Priority-Level value=\"3\" />\r\n" + 
				"	</Allocation-Retention-Priority>\r\n" + 
				"</QoS-Information>\r\n" + 
				"<TGPP-SGSN-MCC-MNC value=\"52003\" />\r\n" + 
				"<TGPP-SGSN-Address value=\"119.31.127.142\" />\r\n" + 
				"<TGPP-User-Location-Info value=\"0x0125f03004c99e7e\" />\r\n" + 
				"<TGPP-MS-TimeZone value=\"0x8200\" />\r\n" + 
				"<Called-Station-Id value=\"internet\" />\r\n" + 
				"<Bearer-Usage value=\"0\" />\r\n" + 
				"<Offline value=\"1\" />\r\n" + 
				"<Access-Network-Charging-Address value=\"110.49.195.7\" />\r\n" + 
				"<Charging-Rule-Report>\r\n"
				+ "<Charging-Rule-Base-Name value=\"rbn-default\" />\r\n" + 
				"	<Charging-Rule-Base-Name value=\"rbn-deny-all\" />\r\n" + 
				"	<Bearer-Identifier value=\"0x33363138363931393831\" />\r\n" + 
				"</Charging-Rule-Report>\r\n" + 
				"<Online value=\"1\" />  \r\n";

//				"</ERDData>";
		if(isToolParser) {
			DiameterUtils diameterUtils = new DiameterUtils();
			TreeMap<String, Object> param = (TreeMap<String, Object>) (diameterUtils.getparserObject(requestMessageCcrI));
			
		}else {
			DiameterUtils diameterUtils = new DiameterUtils();
			ValidateUtils validateutils = new ValidateUtils();
			TreeMap<String, Object> param;
			try {
				param = (TreeMap<String, Object>) diameterUtils.parser(requestMessageCcrI);
	
				if(validateutils.isNotNullAndNotEmpty(param.get("CC-Request-Type").toString())&&validateutils.validRegex(param.get("CC-Request-Type").toString(), "[0-9]"))
				{
					switch(param.get("CC-Request-Type").toString()) {
						/* CCR-I */
						case "1" :
							/* Parser CCR-I */
							parserToObjectCCRI(paramDgw, param);
							/* Validate CCR-I */
							validateAllParamCCRI(paramDgw, validateutils);
							afSubIns.setCcRequestType("1");
							break;
						/* CCR-U */
						case "2" :
							/* Parser CCR-U */
							parserToObjectCCRU(paramDgw, param);
							/* Validate CCR-U */
							validateAllParamCCRU(paramDgw, validateutils);
							afSubIns.setCcRequestType("2");
							break;
						/* CCR-T */
						case "3" :
							/* Parser CCR-T */
							parserToObjectCCRT(paramDgw, param);
							/* Validate CCR-T */
							validateAllParamCCRT(paramDgw, validateutils);
							afSubIns.setCcRequestType("3");
							break;
						default :
							paramDgw.setValid(false);
							afSubIns.setSubCurrentState(ESubState.IDLE_DGW.getState());
							afSubIns.setSubControlState(ESubState.IDLE_DGW.getState());
							afSubIns.setSubNextState(ESubState.Unknown.toString());
							afSubIns.setSubResultCode(EResultCode.RE40300.getResultCode());
							afSubIns.setSubInternalCode(EResultCode.RE40300.getResultCode());
							afInstance.incrementMainCountWait();
							afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);
							return paramDgw;
					}	
					afSubIns.setSubCurrentState(ESubState.IDLE_DGW.getState());
					afSubIns.setSubControlState(ESubState.IDLE_DGW.getState());
					afSubIns.setSubNextState(ESubState.Unknown.toString());
					afSubIns.setSubResultCode(EResultCode.RE20000.getResultCode());
					afSubIns.setSubInternalCode(EResultCode.RE20000.getResultCode());
					afInstance.incrementMainCountWait();
					afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);
				}
				else
				{
					paramDgw.setValid(false);
					afSubIns.setSubCurrentState(ESubState.IDLE_DGW.getState());
					afSubIns.setSubControlState(ESubState.IDLE_DGW.getState());
					afSubIns.setSubNextState(ESubState.Unknown.toString());
					afSubIns.setSubResultCode(EResultCode.RE40300.getResultCode());
					afSubIns.setSubInternalCode(EResultCode.RE40300.getResultCode());
					afInstance.incrementMainCountWait();
					afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);
				}
			}catch(Exception e) {
				paramDgw.setValid(false);
				afSubIns.setSubCurrentState(ESubState.IDLE_DGW.getState());
				afSubIns.setSubControlState(ESubState.IDLE_DGW.getState());
				afSubIns.setSubNextState(ESubState.Unknown.toString());
				afSubIns.setSubResultCode(EResultCode.RE40300.getResultCode());
				afSubIns.setSubInternalCode(EResultCode.RE40300.getResultCode());
				afInstance.incrementMainCountWait();
				afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);
			}
		}

		return paramDgw;
	}
	public void parserToObjectCCRI(Param_IDLE_Dgw paramDgw, TreeMap<String, Object> param)
	{
		paramDgw.setSessionId(param.get("Session-Id").toString());
		paramDgw.setAuthApplicationId(param.get("Auth-Application-Id").toString());
		paramDgw.setOriginHost(param.get("Origin-Host").toString());
		paramDgw.setOriginRealm(param.get("Origin-Realm").toString());
		paramDgw.setDestinationRealm(param.get("Destination-Realm").toString());
		paramDgw.setCcRequestType(param.get("CC-Request-Type").toString());
		paramDgw.setCcRequestNumber(param.get("CC-Request-Number").toString());
		paramDgw.setDestinationHost(param.get("Destination-Host").toString());
		paramDgw.setOriginStateId(param.get("Origin-State-Id").toString());
		paramDgw.setSubScriptionId((ArrayList<TreeMap<String, String>>) param.get("Subscription-Id"));
		paramDgw.setSupportedFeatures((TreeMap<String, String>) param.get("Supported-Features"));
		paramDgw.setNetworkRequestSupport(param.get("Network-Request-Support").toString());
		paramDgw.setBearerIdentifier(param.get("Bearer-Identifier").toString());
		paramDgw.setBearerOperation(param.get("Bearer-Operation").toString());
		paramDgw.setFramedIpAddress(param.get("Framed-IP-Address").toString());
		paramDgw.setIpCanType(param.get("IP-CAN-Type").toString());
		paramDgw.setRatType(param.get("RAT-Type").toString());
		paramDgw.setUserEquipmentInfo((TreeMap<String, String>) param.get("User-Equipment-Info"));
		paramDgw.setQosInformation((TreeMap<String, Object>)param.get("QoS-Information"));
		paramDgw.setTgppSgsnMccMnc(param.get("TGPP-SGSN-MCC-MNC").toString());
		paramDgw.setTgppSgsnAddress(param.get("TGPP-SGSN-Address").toString());
		paramDgw.setTgppUserLocationInfo(param.get("TGPP-User-Location-Info").toString());
		paramDgw.setTgppMsTimeZone(param.get("TGPP-MS-TimeZone").toString());
		paramDgw.setCalledStationId(param.get("Called-Station-Id").toString());
		paramDgw.setBearerUsage(param.get("Bearer-Usage").toString());
		paramDgw.setOffline(param.get("Offline").toString());
		paramDgw.setAccessNetworkChargingAddress(param.get("Access-Network-Charging-Address").toString());
		paramDgw.setChargingRuleReport((TreeMap<String, Object>) param.get("Charging-Rule-Report"));
		paramDgw.setOnline(param.get("Online").toString());
	}
	public void parserToObjectCCRU(Param_IDLE_Dgw paramDgw, TreeMap<String, Object> param)
	{
		paramDgw.setSessionId(param.get("Session-Id").toString());
		paramDgw.setAuthApplicationId(param.get("Auth-Application-Id").toString());
		paramDgw.setOriginHost(param.get("Origin-Host").toString());
		paramDgw.setOriginRealm(param.get("Origin-Realm").toString());
		paramDgw.setDestinationRealm(param.get("Destination-Realm").toString());
		paramDgw.setCcRequestType(param.get("CC-Request-Type").toString());
		paramDgw.setCcRequestNumber(param.get("CC-Request-Number").toString());
		paramDgw.setDestinationHost(param.get("Destination-Host").toString());
		paramDgw.setOriginStateId(param.get("Origin-State-Id").toString());
		paramDgw.setSubScriptionId((ArrayList<TreeMap<String, String>>) param.get("Subscription-Id"));
		paramDgw.setSupportedFeatures((TreeMap<String, String>) param.get("Supported-Features"));
		paramDgw.setNetworkRequestSupport(param.get("Network-Request-Support").toString());
		paramDgw.setBearerIdentifier(param.get("Bearer-Identifier").toString());
		paramDgw.setBearerOperation(param.get("Bearer-Operation").toString());
		paramDgw.setFramedIpAddress(param.get("Framed-IP-Address").toString());
		paramDgw.setIpCanType(param.get("IP-CAN-Type").toString());
		paramDgw.setRatType(param.get("RAT-Type").toString());
		paramDgw.setUserEquipmentInfo((TreeMap<String, String>) param.get("User-Equipment-Info"));
		paramDgw.setQosInformation((TreeMap<String, Object>)param.get("QoS-Information"));
		paramDgw.setTgppSgsnMccMnc(param.get("TGPP-SGSN-MCC-MNC").toString());
		paramDgw.setTgppSgsnAddress(param.get("TGPP-SGSN-Address").toString());
		paramDgw.setTgppUserLocationInfo(param.get("TGPP-User-Location-Info").toString());
		paramDgw.setTgppMsTimeZone(param.get("TGPP-MS-TimeZone").toString());
		paramDgw.setCalledStationId(param.get("Called-Station-Id").toString());
		paramDgw.setBearerUsage(param.get("Bearer-Usage").toString());
		paramDgw.setOffline(param.get("Offline").toString());
		paramDgw.setAccessNetworkChargingAddress(param.get("Access-Network-Charging-Address").toString());
		paramDgw.setChargingRuleReport((TreeMap<String, Object>) param.get("Charging-Rule-Report"));
		paramDgw.setOnline(param.get("Online").toString());
		paramDgw.setEventTrigger((ArrayList<String>) param.get("Event-Trigger"));
	}
	public void parserToObjectCCRT(Param_IDLE_Dgw paramDgw, TreeMap<String, Object> param)
	{
		paramDgw.setSessionId(param.get("Session-Id").toString());
		paramDgw.setAuthApplicationId(param.get("Auth-Application-Id").toString());
		paramDgw.setOriginHost(param.get("Origin-Host").toString());
		paramDgw.setOriginRealm(param.get("Origin-Realm").toString());
		paramDgw.setDestinationRealm(param.get("Destination-Realm").toString());
		paramDgw.setCcRequestType(param.get("CC-Request-Type").toString());
		paramDgw.setCcRequestNumber(param.get("CC-Request-Number").toString());
		paramDgw.setDestinationHost(param.get("Destination-Host").toString());
		paramDgw.setOriginStateId(param.get("Origin-State-Id").toString());
		paramDgw.setSubScriptionId((ArrayList<TreeMap<String, String>>) param.get("Subscription-Id"));
		paramDgw.setSupportedFeatures((TreeMap<String, String>) param.get("Supported-Features"));
		paramDgw.setNetworkRequestSupport(param.get("Network-Request-Support").toString());
		paramDgw.setBearerIdentifier(param.get("Bearer-Identifier").toString());
		paramDgw.setBearerOperation(param.get("Bearer-Operation").toString());
		paramDgw.setFramedIpAddress(param.get("Framed-IP-Address").toString());
		paramDgw.setIpCanType(param.get("IP-CAN-Type").toString());
		paramDgw.setRatType(param.get("RAT-Type").toString());
		paramDgw.setUserEquipmentInfo((TreeMap<String, String>) param.get("User-Equipment-Info"));
		paramDgw.setQosInformation((TreeMap<String, Object>)param.get("QoS-Information"));
		paramDgw.setTgppSgsnMccMnc(param.get("TGPP-SGSN-MCC-MNC").toString());
		paramDgw.setTgppSgsnAddress(param.get("TGPP-SGSN-Address").toString());
		paramDgw.setTgppUserLocationInfo(param.get("TGPP-User-Location-Info").toString());
		paramDgw.setTgppMsTimeZone(param.get("TGPP-MS-TimeZone").toString());
		paramDgw.setCalledStationId(param.get("Called-Station-Id").toString());
		paramDgw.setBearerUsage(param.get("Bearer-Usage").toString());
		paramDgw.setOffline(param.get("Offline").toString());
		paramDgw.setAccessNetworkChargingAddress(param.get("Access-Network-Charging-Address").toString());
		paramDgw.setChargingRuleReport((TreeMap<String, Object>) param.get("Charging-Rule-Report"));
		paramDgw.setOnline(param.get("Online").toString());
		paramDgw.setTerminationCause(param.get("Termination-Cause").toString());
	}
	public void validateAllParamCCRI(Param_IDLE_Dgw paramDgw, ValidateUtils validateutils)
	{
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getSessionId()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getAuthApplicationId()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getOriginHost()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getOriginRealm()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getDestinationRealm()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getCcRequestNumber()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getDestinationHost()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getOriginStateId()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getSubScriptionId().toString()))
		{
			paramDgw.setValid(false);
		}
		else
		{
			for(int i=0;i<paramDgw.getSubScriptionId().size();i++)
			{
				if(!validateutils.isNotNullAndNotEmpty(paramDgw.getSubScriptionId().get(i).get("Subscription-Id-Type")))
				{
					paramDgw.setValid(false);
				}
				if(!validateutils.isNotNullAndNotEmpty(paramDgw.getSubScriptionId().get(i).get("Subscription-Id-Data")))
				{
					paramDgw.setValid(false);
				}
			}
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getIpCanType()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getRatType()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getUserEquipmentInfo().toString()))
		{
			paramDgw.setValid(false);
		}
		else
		{
			if(!validateutils.isNotNullAndNotEmpty(paramDgw.getUserEquipmentInfo().get("User-Equipment-Info-Type")))
			{
				paramDgw.setValid(false);
			}
			if(!validateutils.isNotNullAndNotEmpty(paramDgw.getUserEquipmentInfo().get("User-Equipment-Info-Value")))
			{
				paramDgw.setValid(false);
			}
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getTgppSgsnAddress()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getCalledStationId()))
		{
			paramDgw.setValid(false);
		}
	}
	public void validateAllParamCCRU(Param_IDLE_Dgw paramDgw, ValidateUtils validateutils)
	{
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getSessionId()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getAuthApplicationId()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getOriginHost()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getOriginRealm()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getDestinationRealm()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getCcRequestNumber()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getDestinationHost()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getOriginStateId()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getSubScriptionId().toString()))
		{
			paramDgw.setValid(false);
		}
		else
		{
			for(int i=0;i<paramDgw.getSubScriptionId().size();i++)
			{
				if(!validateutils.isNotNullAndNotEmpty(paramDgw.getSubScriptionId().get(i).get("Subscription-Id-Type")))
				{
					paramDgw.setValid(false);
				}
				if(!validateutils.isNotNullAndNotEmpty(paramDgw.getSubScriptionId().get(i).get("Subscription-Id-Data")))
				{
					paramDgw.setValid(false);
				}
			}
		}
		
	}
	public void validateAllParamCCRT(Param_IDLE_Dgw paramDgw, ValidateUtils validateutils)
	{
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getSessionId()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getAuthApplicationId()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getOriginHost()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getOriginRealm()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getDestinationRealm()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getCcRequestNumber()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getDestinationHost()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getOriginStateId()))
		{
			paramDgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramDgw.getSubScriptionId().toString()))
		{
			paramDgw.setValid(false);
		}
		else
		{
			for(int i=0;i<paramDgw.getSubScriptionId().size();i++)
			{
				if(!validateutils.isNotNullAndNotEmpty(paramDgw.getSubScriptionId().get(i).get("Subscription-Id-Type")))
				{
					paramDgw.setValid(false);
				}
				if(!validateutils.isNotNullAndNotEmpty(paramDgw.getSubScriptionId().get(i).get("Subscription-Id-Data")))
				{
					paramDgw.setValid(false);
				}
			}
		}
	}
}
