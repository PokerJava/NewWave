package ct.af.extracterd;

import java.util.ArrayList;
import java.util.TreeMap;

import ct.af.enums.EResultCode;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_Pgw;
import ct.af.utils.DiameterUtils;
import ct.af.utils.ValidateUtils;
import ec02.af.abstracts.AbstractAF;
import ec02.data.interfaces.EquinoxRawData;

public class In_IDLE_Dgw {
	public Param_IDLE_Pgw doParser(AbstractAF abstractAF, EquinoxRawData eqxRawData, AFInstance afInstance,
			AFSubInstance afSubIns) {
		Param_IDLE_Pgw paramPgw = new Param_IDLE_Pgw();
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
							parserToObjectCCRI(paramPgw, param);
							/* Validate CCR-I */
							validateAllParamCCRI(paramPgw, validateutils);
							afSubIns.setCcRequestType("1");
							break;
						/* CCR-U */
						case "2" :
							/* Parser CCR-U */
							parserToObjectCCRU(paramPgw, param);
							/* Validate CCR-U */
							validateAllParamCCRU(paramPgw, validateutils);
							afSubIns.setCcRequestType("2");
							break;
						/* CCR-T */
						case "3" :
							/* Parser CCR-T */
							parserToObjectCCRT(paramPgw, param);
							/* Validate CCR-T */
							validateAllParamCCRT(paramPgw, validateutils);
							afSubIns.setCcRequestType("3");
							break;
						default :
							paramPgw.setValid(false);
							afSubIns.setSubCurrentState(ESubState.IDLE_PGW.getState());
							afSubIns.setSubControlState(ESubState.IDLE_PGW.getState());
							afSubIns.setSubNextState(ESubState.Unknown.toString());
							afSubIns.setSubResultCode(EResultCode.RE40300.getResultCode());
							afSubIns.setSubInternalCode(EResultCode.RE40300.getResultCode());
							afInstance.incrementMainCountWait();
							afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);
							return paramPgw;
					}	
					afSubIns.setSubCurrentState(ESubState.IDLE_PGW.getState());
					afSubIns.setSubControlState(ESubState.IDLE_PGW.getState());
					afSubIns.setSubNextState(ESubState.Unknown.toString());
					afSubIns.setSubResultCode(EResultCode.RE20000.getResultCode());
					afSubIns.setSubInternalCode(EResultCode.RE20000.getResultCode());
					afInstance.incrementMainCountWait();
					afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);
				}
				else
				{
					paramPgw.setValid(false);
					afSubIns.setSubCurrentState(ESubState.IDLE_PGW.getState());
					afSubIns.setSubControlState(ESubState.IDLE_PGW.getState());
					afSubIns.setSubNextState(ESubState.Unknown.toString());
					afSubIns.setSubResultCode(EResultCode.RE40300.getResultCode());
					afSubIns.setSubInternalCode(EResultCode.RE40300.getResultCode());
					afInstance.incrementMainCountWait();
					afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);
				}
			}catch(Exception e) {
				paramPgw.setValid(false);
				afSubIns.setSubCurrentState(ESubState.IDLE_PGW.getState());
				afSubIns.setSubControlState(ESubState.IDLE_PGW.getState());
				afSubIns.setSubNextState(ESubState.Unknown.toString());
				afSubIns.setSubResultCode(EResultCode.RE40300.getResultCode());
				afSubIns.setSubInternalCode(EResultCode.RE40300.getResultCode());
				afInstance.incrementMainCountWait();
				afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);
			}
		}

		return paramPgw;
	}
	public void parserToObjectCCRI(Param_IDLE_Pgw paramPgw, TreeMap<String, Object> param)
	{
		paramPgw.setSessionId(param.get("Session-Id").toString());
		paramPgw.setAuthApplicationId(param.get("Auth-Application-Id").toString());
		paramPgw.setOriginHost(param.get("Origin-Host").toString());
		paramPgw.setOriginRealm(param.get("Origin-Realm").toString());
		paramPgw.setDestinationRealm(param.get("Destination-Realm").toString());
		paramPgw.setCcRequestType(param.get("CC-Request-Type").toString());
		paramPgw.setCcRequestNumber(param.get("CC-Request-Number").toString());
		paramPgw.setDestinationHost(param.get("Destination-Host").toString());
		paramPgw.setOriginStateId(param.get("Origin-State-Id").toString());
		paramPgw.setSubScriptionId((ArrayList<TreeMap<String, String>>) param.get("Subscription-Id"));
		paramPgw.setSupportedFeatures((TreeMap<String, String>) param.get("Supported-Features"));
		paramPgw.setNetworkRequestSupport(param.get("Network-Request-Support").toString());
		paramPgw.setBearerIdentifier(param.get("Bearer-Identifier").toString());
		paramPgw.setBearerOperation(param.get("Bearer-Operation").toString());
		paramPgw.setFramedIpAddress(param.get("Framed-IP-Address").toString());
		paramPgw.setIpCanType(param.get("IP-CAN-Type").toString());
		paramPgw.setRatType(param.get("RAT-Type").toString());
		paramPgw.setUserEquipmentInfo((TreeMap<String, String>) param.get("User-Equipment-Info"));
		paramPgw.setQosInformation((TreeMap<String, Object>)param.get("QoS-Information"));
		paramPgw.setTgppSgsnMccMnc(param.get("TGPP-SGSN-MCC-MNC").toString());
		paramPgw.setTgppSgsnAddress(param.get("TGPP-SGSN-Address").toString());
		paramPgw.setTgppUserLocationInfo(param.get("TGPP-User-Location-Info").toString());
		paramPgw.setTgppMsTimeZone(param.get("TGPP-MS-TimeZone").toString());
		paramPgw.setCalledStationId(param.get("Called-Station-Id").toString());
		paramPgw.setBearerUsage(param.get("Bearer-Usage").toString());
		paramPgw.setOffline(param.get("Offline").toString());
		paramPgw.setAccessNetworkChargingAddress(param.get("Access-Network-Charging-Address").toString());
		paramPgw.setChargingRuleReport((TreeMap<String, Object>) param.get("Charging-Rule-Report"));
		paramPgw.setOnline(param.get("Online").toString());
	}
	public void parserToObjectCCRU(Param_IDLE_Pgw paramPgw, TreeMap<String, Object> param)
	{
		paramPgw.setSessionId(param.get("Session-Id").toString());
		paramPgw.setAuthApplicationId(param.get("Auth-Application-Id").toString());
		paramPgw.setOriginHost(param.get("Origin-Host").toString());
		paramPgw.setOriginRealm(param.get("Origin-Realm").toString());
		paramPgw.setDestinationRealm(param.get("Destination-Realm").toString());
		paramPgw.setCcRequestType(param.get("CC-Request-Type").toString());
		paramPgw.setCcRequestNumber(param.get("CC-Request-Number").toString());
		paramPgw.setDestinationHost(param.get("Destination-Host").toString());
		paramPgw.setOriginStateId(param.get("Origin-State-Id").toString());
		paramPgw.setSubScriptionId((ArrayList<TreeMap<String, String>>) param.get("Subscription-Id"));
		paramPgw.setSupportedFeatures((TreeMap<String, String>) param.get("Supported-Features"));
		paramPgw.setNetworkRequestSupport(param.get("Network-Request-Support").toString());
		paramPgw.setBearerIdentifier(param.get("Bearer-Identifier").toString());
		paramPgw.setBearerOperation(param.get("Bearer-Operation").toString());
		paramPgw.setFramedIpAddress(param.get("Framed-IP-Address").toString());
		paramPgw.setIpCanType(param.get("IP-CAN-Type").toString());
		paramPgw.setRatType(param.get("RAT-Type").toString());
		paramPgw.setUserEquipmentInfo((TreeMap<String, String>) param.get("User-Equipment-Info"));
		paramPgw.setQosInformation((TreeMap<String, Object>)param.get("QoS-Information"));
		paramPgw.setTgppSgsnMccMnc(param.get("TGPP-SGSN-MCC-MNC").toString());
		paramPgw.setTgppSgsnAddress(param.get("TGPP-SGSN-Address").toString());
		paramPgw.setTgppUserLocationInfo(param.get("TGPP-User-Location-Info").toString());
		paramPgw.setTgppMsTimeZone(param.get("TGPP-MS-TimeZone").toString());
		paramPgw.setCalledStationId(param.get("Called-Station-Id").toString());
		paramPgw.setBearerUsage(param.get("Bearer-Usage").toString());
		paramPgw.setOffline(param.get("Offline").toString());
		paramPgw.setAccessNetworkChargingAddress(param.get("Access-Network-Charging-Address").toString());
		paramPgw.setChargingRuleReport((TreeMap<String, Object>) param.get("Charging-Rule-Report"));
		paramPgw.setOnline(param.get("Online").toString());
		paramPgw.setEventTrigger((ArrayList<String>) param.get("Event-Trigger"));
	}
	public void parserToObjectCCRT(Param_IDLE_Pgw paramPgw, TreeMap<String, Object> param)
	{
		paramPgw.setSessionId(param.get("Session-Id").toString());
		paramPgw.setAuthApplicationId(param.get("Auth-Application-Id").toString());
		paramPgw.setOriginHost(param.get("Origin-Host").toString());
		paramPgw.setOriginRealm(param.get("Origin-Realm").toString());
		paramPgw.setDestinationRealm(param.get("Destination-Realm").toString());
		paramPgw.setCcRequestType(param.get("CC-Request-Type").toString());
		paramPgw.setCcRequestNumber(param.get("CC-Request-Number").toString());
		paramPgw.setDestinationHost(param.get("Destination-Host").toString());
		paramPgw.setOriginStateId(param.get("Origin-State-Id").toString());
		paramPgw.setSubScriptionId((ArrayList<TreeMap<String, String>>) param.get("Subscription-Id"));
		paramPgw.setSupportedFeatures((TreeMap<String, String>) param.get("Supported-Features"));
		paramPgw.setNetworkRequestSupport(param.get("Network-Request-Support").toString());
		paramPgw.setBearerIdentifier(param.get("Bearer-Identifier").toString());
		paramPgw.setBearerOperation(param.get("Bearer-Operation").toString());
		paramPgw.setFramedIpAddress(param.get("Framed-IP-Address").toString());
		paramPgw.setIpCanType(param.get("IP-CAN-Type").toString());
		paramPgw.setRatType(param.get("RAT-Type").toString());
		paramPgw.setUserEquipmentInfo((TreeMap<String, String>) param.get("User-Equipment-Info"));
		paramPgw.setQosInformation((TreeMap<String, Object>)param.get("QoS-Information"));
		paramPgw.setTgppSgsnMccMnc(param.get("TGPP-SGSN-MCC-MNC").toString());
		paramPgw.setTgppSgsnAddress(param.get("TGPP-SGSN-Address").toString());
		paramPgw.setTgppUserLocationInfo(param.get("TGPP-User-Location-Info").toString());
		paramPgw.setTgppMsTimeZone(param.get("TGPP-MS-TimeZone").toString());
		paramPgw.setCalledStationId(param.get("Called-Station-Id").toString());
		paramPgw.setBearerUsage(param.get("Bearer-Usage").toString());
		paramPgw.setOffline(param.get("Offline").toString());
		paramPgw.setAccessNetworkChargingAddress(param.get("Access-Network-Charging-Address").toString());
		paramPgw.setChargingRuleReport((TreeMap<String, Object>) param.get("Charging-Rule-Report"));
		paramPgw.setOnline(param.get("Online").toString());
		paramPgw.setTerminationCause(param.get("Termination-Cause").toString());
	}
	public void validateAllParamCCRI(Param_IDLE_Pgw paramPgw, ValidateUtils validateutils)
	{
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getSessionId()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getAuthApplicationId()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getOriginHost()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getOriginRealm()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getDestinationRealm()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getCcRequestNumber()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getDestinationHost()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getOriginStateId()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getSubScriptionId().toString()))
		{
			paramPgw.setValid(false);
		}
		else
		{
			for(int i=0;i<paramPgw.getSubScriptionId().size();i++)
			{
				if(!validateutils.isNotNullAndNotEmpty(paramPgw.getSubScriptionId().get(i).get("Subscription-Id-Type")))
				{
					paramPgw.setValid(false);
				}
				if(!validateutils.isNotNullAndNotEmpty(paramPgw.getSubScriptionId().get(i).get("Subscription-Id-Data")))
				{
					paramPgw.setValid(false);
				}
			}
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getIpCanType()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getRatType()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getUserEquipmentInfo().toString()))
		{
			paramPgw.setValid(false);
		}
		else
		{
			if(!validateutils.isNotNullAndNotEmpty(paramPgw.getUserEquipmentInfo().get("User-Equipment-Info-Type")))
			{
				paramPgw.setValid(false);
			}
			if(!validateutils.isNotNullAndNotEmpty(paramPgw.getUserEquipmentInfo().get("User-Equipment-Info-Value")))
			{
				paramPgw.setValid(false);
			}
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getTgppSgsnAddress()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getCalledStationId()))
		{
			paramPgw.setValid(false);
		}
	}
	public void validateAllParamCCRU(Param_IDLE_Pgw paramPgw, ValidateUtils validateutils)
	{
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getSessionId()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getAuthApplicationId()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getOriginHost()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getOriginRealm()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getDestinationRealm()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getCcRequestNumber()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getDestinationHost()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getOriginStateId()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getSubScriptionId().toString()))
		{
			paramPgw.setValid(false);
		}
		else
		{
			for(int i=0;i<paramPgw.getSubScriptionId().size();i++)
			{
				if(!validateutils.isNotNullAndNotEmpty(paramPgw.getSubScriptionId().get(i).get("Subscription-Id-Type")))
				{
					paramPgw.setValid(false);
				}
				if(!validateutils.isNotNullAndNotEmpty(paramPgw.getSubScriptionId().get(i).get("Subscription-Id-Data")))
				{
					paramPgw.setValid(false);
				}
			}
		}
		
	}
	public void validateAllParamCCRT(Param_IDLE_Pgw paramPgw, ValidateUtils validateutils)
	{
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getSessionId()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getAuthApplicationId()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getOriginHost()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getOriginRealm()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getDestinationRealm()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getCcRequestNumber()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getDestinationHost()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getOriginStateId()))
		{
			paramPgw.setValid(false);
		}
		if(!validateutils.isNotNullAndNotEmpty(paramPgw.getSubScriptionId().toString()))
		{
			paramPgw.setValid(false);
		}
		else
		{
			for(int i=0;i<paramPgw.getSubScriptionId().size();i++)
			{
				if(!validateutils.isNotNullAndNotEmpty(paramPgw.getSubScriptionId().get(i).get("Subscription-Id-Type")))
				{
					paramPgw.setValid(false);
				}
				if(!validateutils.isNotNullAndNotEmpty(paramPgw.getSubScriptionId().get(i).get("Subscription-Id-Data")))
				{
					paramPgw.setValid(false);
				}
			}
		}
	}
}
