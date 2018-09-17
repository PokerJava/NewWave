package ct.af.message.incoming.parser;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_Pgw;
import ct.af.utils.DiameterUtils;
import ct.af.utils.ValidateUtils;
import ec02.af.abstracts.AbstractAF;
import ec02.data.interfaces.EquinoxRawData;

public class Parser_IDLE_Pgw {
	public Param_IDLE_Pgw doParser(AbstractAF abstractAF, EquinoxRawData eqxRawData, AFInstance afInstance,
			AFSubInstance afSubIns) {
		Param_IDLE_Pgw paramPgw = new Param_IDLE_Pgw();
		boolean isToolParser = false;
		String requestMessageCcrI = "<Session-Id value=\"71:1002064297:GatewayService-4-13-1.SUK8N.awn.com;1393540200;2147818684\" />\r\n" + 
				"<Auth-Application-Id value=\"16777238\" />\r\n" + 
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
				"<Online value=\"1\" />  \r\n" + 
				"" + 
				"";
		if(isToolParser) {
			
		}else {
			DiameterUtils diameterUtils = new DiameterUtils();
			TreeMap<String, Object> param = (TreeMap<String, Object>) diameterUtils.parser(requestMessageCcrI);
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
			
			if(paramPgw.getCcRequestType()!=null&& !paramPgw.getCcRequestType().isEmpty()&&new ValidateUtils().validRegex(paramPgw.getCcRequestType(), "[0-9]")) {
				switch(paramPgw.getCcRequestType()) {
					/* CCR-I */
					case "1" :
						if(paramPgw.getSessionId()==null&& paramPgw.getSessionId().isEmpty())
						{
							paramPgw.setValid(false);
						}
						if(paramPgw.getAuthApplicationId()==null&& paramPgw.getAuthApplicationId().isEmpty())
						{
							paramPgw.setValid(false);
						}
						if(paramPgw.getOriginHost()==null&& paramPgw.getOriginHost().isEmpty())
						{
							paramPgw.setValid(false);
						}
						if(paramPgw.getOriginRealm()==null&& paramPgw.getOriginRealm().isEmpty())
						{
							paramPgw.setValid(false);
						}
						if(paramPgw.getDestinationRealm()==null&& paramPgw.getDestinationRealm().isEmpty())
						{
							paramPgw.setValid(false);
						}
						if(paramPgw.getCcRequestNumber()==null&& paramPgw.getCcRequestNumber().isEmpty())
						{
							paramPgw.setValid(false);
						}
						if(paramPgw.getDestinationHost()==null&& paramPgw.getCcRequestNumber().isEmpty())
						{
							paramPgw.setValid(false);
						}
						if(paramPgw.getOriginStateId()==null&& paramPgw.getOriginStateId().isEmpty())
						{
							paramPgw.setValid(false);
						}
						if(paramPgw.getSubScriptionId()==null&& paramPgw.getSubScriptionId().isEmpty())
						{
							paramPgw.setValid(false);
						}
						else
						{
							for(int i=0;i<paramPgw.getSubScriptionId().size();i++)
							{
								if(paramPgw.getSubScriptionId().get(i).get("Subscription-Id-Type")==null&&paramPgw.getSubScriptionId().get(i).get("Subscription-Id-Type").isEmpty())
								{
									paramPgw.setValid(false);
								}
								if(paramPgw.getSubScriptionId().get(i).get("Subscription-Id-Data")==null&&paramPgw.getSubScriptionId().get(i).get("Subscription-Id-Data").isEmpty())
								{
									paramPgw.setValid(false);
								}
							}
						}
						if(paramPgw.getIpCanType()==null&& paramPgw.getIpCanType().isEmpty())
						{
							paramPgw.setValid(false);
						}
						if(paramPgw.getRatType()==null&& paramPgw.getRatType().isEmpty())
						{
							paramPgw.setValid(false);
						}
						if(paramPgw.getUserEquipmentInfo()==null&& paramPgw.getUserEquipmentInfo().isEmpty())
						{
							paramPgw.setValid(false);
						}
						else
						{
							if(paramPgw.getUserEquipmentInfo().get("User-Equipment-Info-Type")==null&& paramPgw.getUserEquipmentInfo().get("User-Equipment-Info-Type").isEmpty())
							{
								paramPgw.setValid(false);
							}
							if(paramPgw.getUserEquipmentInfo().get("User-Equpment-Info-Value")==null&& paramPgw.getUserEquipmentInfo().get("User-Equpment-Info-Value").isEmpty())
							{
								paramPgw.setValid(false);
							}
						}
						if(paramPgw.getTgppSgsnAddress()==null&& paramPgw.getTgppSgsnAddress().isEmpty())
						{
							paramPgw.setValid(false);
						}
						if(paramPgw.getCalledStationId()==null&& paramPgw.getCalledStationId().isEmpty())
						{
							paramPgw.setValid(false);
						}
						break;
					/* CCR-U */
					case "2" :
						break;
					/* CCR-T */
					case "3" :
						break;
				}
			}
			/* Validate */
			/* Mandatory */
			
		}
		return paramPgw;
	}
}
