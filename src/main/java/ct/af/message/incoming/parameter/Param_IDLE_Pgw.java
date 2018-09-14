package ct.af.message.incoming.parameter;

import java.util.ArrayList;
import java.util.TreeMap;

public class Param_IDLE_Pgw {
	/* Parameter CCR-I */
	String sessionId;
	String authApplicationId;
	String originHost;
	String originRealm;
	String ccRequestType;
	String ccRequestNumber;
	String destinationHost;
	String originStateId;
	String networkRequestSupport;
	String bearerIdentifier;
	String bearerOperation;
	String framedIpAddress;
	String ipCanType;
	String ratType;
	String tgppSgsnMccMnc;
	String tgppSgsnAddress;
	String tgppUserLocationInfo;
	String tgppMsTimeZone;
	String calledStationId;
	String bearerUsage;
	String offline;
	String accessNetworkChargingAddress;
	String online;
	TreeMap<String, String> supportedFeatures = new TreeMap<>();	
	TreeMap<String, String> userEquipmentInfo = new TreeMap<>();
	TreeMap<String, Object> qosInformation = new TreeMap<>();
	TreeMap<String, Object> chargingRuleReport = new TreeMap<>();
	ArrayList<TreeMap<String, String>> subScriptionId = new ArrayList<TreeMap<String, String>>();
	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getAuthApplicationId() {
		return authApplicationId;
	}
	public void setAuthApplicationId(String authApplicationId) {
		this.authApplicationId = authApplicationId;
	}
	public String getOriginHost() {
		return originHost;
	}
	public void setOriginHost(String originHost) {
		this.originHost = originHost;
	}
	public String getOriginRealm() {
		return originRealm;
	}
	public void setOriginRealm(String originRealm) {
		this.originRealm = originRealm;
	}
	public String getCcRequestType() {
		return ccRequestType;
	}
	public void setCcRequestType(String ccRequestType) {
		this.ccRequestType = ccRequestType;
	}
	public String getCcRequestNumber() {
		return ccRequestNumber;
	}
	public void setCcRequestNumber(String ccRequestNumber) {
		this.ccRequestNumber = ccRequestNumber;
	}
	public String getDestinationHost() {
		return destinationHost;
	}
	public void setDestinationHost(String destinationHost) {
		this.destinationHost = destinationHost;
	}
	public String getOriginStateId() {
		return originStateId;
	}
	public void setOriginStateId(String originStateId) {
		this.originStateId = originStateId;
	}
	public String getNetworkRequestSupport() {
		return networkRequestSupport;
	}
	public void setNetworkRequestSupport(String networkRequestSupport) {
		this.networkRequestSupport = networkRequestSupport;
	}
	public String getBearerIdentifier() {
		return bearerIdentifier;
	}
	public void setBearerIdentifier(String bearerIdentifier) {
		this.bearerIdentifier = bearerIdentifier;
	}
	public String getBearerOperation() {
		return bearerOperation;
	}
	public void setBearerOperation(String bearerOperation) {
		this.bearerOperation = bearerOperation;
	}
	public String getFramedIpAddress() {
		return framedIpAddress;
	}
	public void setFramedIpAddress(String framedIpAddress) {
		this.framedIpAddress = framedIpAddress;
	}
	public String getIpCanType() {
		return ipCanType;
	}
	public void setIpCanType(String ipCanType) {
		this.ipCanType = ipCanType;
	}
	public String getRatType() {
		return ratType;
	}
	public void setRatType(String ratType) {
		this.ratType = ratType;
	}
	public String getTgppSgsnMccMnc() {
		return tgppSgsnMccMnc;
	}
	public void setTgppSgsnMccMnc(String tgppSgsnMccMnc) {
		this.tgppSgsnMccMnc = tgppSgsnMccMnc;
	}
	public String getTgppSgsnAddress() {
		return tgppSgsnAddress;
	}
	public void setTgppSgsnAddress(String tgppSgsnAddress) {
		this.tgppSgsnAddress = tgppSgsnAddress;
	}
	public String getTgppUserLocationInfo() {
		return tgppUserLocationInfo;
	}
	public void setTgppUserLocationInfo(String tgppUserLocationInfo) {
		this.tgppUserLocationInfo = tgppUserLocationInfo;
	}
	public String getTgppMsTimeZone() {
		return tgppMsTimeZone;
	}
	public void setTgppMsTimeZone(String tgppMsTimeZone) {
		this.tgppMsTimeZone = tgppMsTimeZone;
	}
	public String getCalledStationId() {
		return calledStationId;
	}
	public void setCalledStationId(String calledStationId) {
		this.calledStationId = calledStationId;
	}
	public String getBearerUsage() {
		return bearerUsage;
	}
	public void setBearerUsage(String bearerUsage) {
		this.bearerUsage = bearerUsage;
	}
	public String getOffline() {
		return offline;
	}
	public void setOffline(String offline) {
		this.offline = offline;
	}
	public String getAccessNetworkChargingAddress() {
		return accessNetworkChargingAddress;
	}
	public void setAccessNetworkChargingAddress(String accessNetworkChargingAddress) {
		this.accessNetworkChargingAddress = accessNetworkChargingAddress;
	}
	public String getOnline() {
		return online;
	}
	public void setOnline(String online) {
		this.online = online;
	}
	public TreeMap<String, String> getSupportedFeatures() {
		return supportedFeatures;
	}
	public void setSupportedFeatures(TreeMap<String, String> supportedFeatures) {
		this.supportedFeatures = supportedFeatures;
	}
	public TreeMap<String, String> getUserEquipmentInfo() {
		return userEquipmentInfo;
	}
	public void setUserEquipmentInfo(TreeMap<String, String> userEquipmentInfo) {
		this.userEquipmentInfo = userEquipmentInfo;
	}
	public TreeMap<String, Object> getQosInformation() {
		return qosInformation;
	}
	public void setQosInformation(TreeMap<String, Object> qosInformation) {
		this.qosInformation = qosInformation;
	}
	public TreeMap<String, Object> getChargingRuleReport() {
		return chargingRuleReport;
	}
	public void setChargingRuleReport(TreeMap<String, Object> chargingRuleReport) {
		this.chargingRuleReport = chargingRuleReport;
	}
	public ArrayList<TreeMap<String, String>> getSubScriptionId() {
		return subScriptionId;
	}
	public void setSubScriptionId(ArrayList<TreeMap<String, String>> subScriptionId) {
		this.subScriptionId = subScriptionId;
	}
	
	
}
