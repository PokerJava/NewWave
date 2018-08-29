package ct.af.enums;

import ct.af.resourceModel.ResourceMappingCommand;
import ct.af.utils.Config;
import ec02.af.utils.AFLog;

public enum EStats {

	/************************************************************************************
	 * NEW_Format :
	 	List				Format
		Send request		[$Appname_$InstanceNo.srfc.$Node] [send] [$Method] [$command] [success/err] [$resultDescription] [req]
		Send response		[$Appname_$InstanceNo.srfc.$Node] [send] [$Method] [$command] [success/err] [$resultDescription] [req]
		Receive request		[$Appname_$InstanceNo.srfc.$Node] [recv] [$Method] [$command] [success/err] [$resultDescription] [req]
		Receive response	[$Appname_$InstanceNo.srfc.$Node] [recv] [$Method] [$command] [success/err] [$resultDescription] [res]
	 
		************************************************************************************/
	// 0 UNKNOWN
	APP_RECV_UNKNOWN_REQUEST("PGZ.srfc recv UNKNOWN req"),
	
	// 1 TRIGGER
	APP_RECV_TRIGGER_REQUEST("PGZ.srfc.E11 recv TRIGGER success req"),
	
	// LOST MODEL MAX_ACTIVE_REQUEST
	APP_RECV_MAXACTIVE_REQUEST("PGZ.srfc recv MAXACTIVE_REQUEST req"),
	
	
	//**********************************************************CLIENT**********************************************************
	// 4 9 RECV_CLIENT RESOURCE_ORDER
	APP_RECV_CLIENT_POST_RESOURCE_ORDER_REQUEST("$APPNAME.srfc.$USERSYS recv POST RESOURCE_ORDER success req"),
	APP_RECV_CLIENT_POST_RESOURCE_ORDER_RESPONSE_TIMEOUT("$APPNAME.srfc.$USERSYS recv POST RESOURCE_ORDER err connection timeout res"),
	APP_RECV_CLIENT_POST_RESOURCE_ORDER_RESPONSE_REJECT("$APPNAME.srfc.$USERSYS recv POST RESOURCE_ORDER err reject res"),
	APP_RECV_CLIENT_POST_RESOURCE_ORDER_RESPONSE_UNKNOWN_ERROR("$APPNAME.srfc.$USERSYS recv POST RESOURCE_ORDER err unknown_error res"),
	APP_SEND_CLIENT_GET_RESOURCE_ORDER_RESPONSE_SUCCESS("$APPNAME.srfc.$USERSYS send POST RESOURCE_ORDER success res"),
	APP_SEND_CLIENT_GET_RESOURCE_ORDER_RESPONSE_SUCCESS_WITH_ERROR("$APPNAME.srfc.$USERSYS send POST RESOURCE_ORDER success with bussiness err res"),

	//**********************************************************HRZ**********************************************************
	// 10 11 W_CLIENT_POSTRESULT
	APP_SEND_W_CLIENT_POSTRESULT_REQUEST("$APPNAME.srfc.$NODE send POST W_CLIENT_POSTRESULT success req"),
	APP_RECV_W_CLIENT_POSTRESULT_RESPONSE_TIMEOUT("$APPNAME.srfc.$NODE recv POST W_CLIENT_POSTRESULT err connection timeout res"),
	APP_RECV_W_CLIENT_POSTRESULT_RESPONSE_REJECT("$APPNAME.srfc.$NODE recv POST W_CLIENT_POSTRESULT err reject res"),
	APP_RECV_W_CLIENT_POSTRESULT_RESPONSE_UNKNOWN_ERROR("$APPNAME.srfc.$NODE recv POST W_CLIENT_POSTRESULT err unknown_error res"),
	APP_RECV_W_CLIENT_POSTRESULT_RESPONSE_SUCCESS("$APPNAME.srfc.$NODE recv POST W_CLIENT_POSTRESULT success res"),
	APP_RECV_W_CLIENT_POSTRESULT_RESPONSE_SUCCESS_WITH_ERROR("$APPNAME.srfc.$NODE recv POST W_CLIENT_POSTRESULT success with bussiness err res"),
	APP_RECV_W_CLIENT_POSTRESULT_EXE_TIME("$APPNAME.srfc.$NODE recv POST W_CLIENT_POSTRESULT_EXE_TIME success $EXE_TIME res"),

	//**********************************************************BACKEND**********************************************************
	// 2 3 SDF_GET_RESOURCE_INVENTORY

	
	HRZ_IN_CUSTOM_STAT("PGZ.srfc.$NODE &ACTION $METHOD $SUPPCODE $RESULTDESCRIPTION $EVENTTYPE"),
	HRZ_EXE_TIME_CUSTOM_STAT("PGZ.srfc.$NODE &ACTION $METHOD $SUPPCODE $RESULTDESCRIPTION $EVENTTYPE"),
	HRZ_OUT_CUSTOM_STAT("PGZ.srfc.$NODE &ACTION $METHOD $SUPPCODE $RESULTDESCRIPTION $EVENTTYPE"),
		
	// 5 6 NE
	NE_IN_CUSTOM_STAT("PGZ.srfc.$NODE &ACTION $METHOD $SUPPCODE $RESULTDESCRIPTION $EVENTTYPE"),
	NE_EXE_TIME_CUSTOM_STAT("PGZ.srfc.$NODE &ACTION $METHOD $SUPPCODE $RESULTDESCRIPTION $EVENTTYPE"),
	NE_OUT_CUSTOM_STAT("PGZ.srfc.$NODE &ACTION $METHOD $SUPPCODE $RESULTDESCRIPTION $EVENTTYPE"),
	
	// PLEASE USE THIS STATS
	NE_EXE_TIME_CUSTOM_STATS("$APPNAME.srfc.$NODE recv $METHOD &NODE_EXE_TIME success res"),
	
	//**********************************************************SDF**********************************************************
	// 2 3 W_SDF_GETRESOURCEINVENTORY
	APP_SEND_SDF_GET_W_SDF_GETRESOURCEINVENTORY_REQUEST("$APPNAME.srfc.$NODE send GET W_SDF_GETRESOURCEINVENTORY success req"),
	APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_TIMEOUT("$APPNAME.srfc.$NODE recv GET W_SDF_GETRESOURCEINVENTORY err connection timeout res"),
	APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_REJECT("$APPNAME.srfc.$NODE recv GET W_SDF_GETRESOURCEINVENTORY err reject res"),
	APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_UNKNOWN_ERROR("$APPNAME.srfc.$NODE recv GET W_SDF_GETRESOURCEINVENTORY err unknown_error res"),
	APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_SUCCESS("$APPNAME.srfc.$NODE recv GET W_SDF_GETRESOURCEINVENTORY success res"),
	APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_RESPONSE_SUCCESS_WITH_ERROR("$APPNAME.srfc.$NODE recv GET W_SDF_GETRESOURCEINVENTORY success with bussiness err res"),
	APP_RECV_SDF_GET_W_SDF_GETRESOURCEINVENTORY_EXE_TIME("$APPNAME.srfc.$NODE recv GET W_SDF_GETRESOURCEINVENTORY_EXE_TIME success $EXE_TIME res"),
				
	// 5 6 W_SDF_GETRESOURCEINFRANODE
	APP_SEND_SDF_GET_W_SDF_GETRESOURCEINFRANODE_REQUEST("$APPNAME.srfc.$NODE send GET W_SDF_GETRESOURCEINFRANODE success req"),
	APP_RECV_SDF_GET_W_SDF_GETRESOURCEINFRANODE_RESPONSE_TIMEOUT("$APPNAME.srfc.$NODE recv GET W_SDF_GETRESOURCEINFRANODE err connection timeout res"),
	APP_RECV_SDF_GET_W_SDF_GETRESOURCEINFRANODE_RESPONSE_REJECT("$APPNAME.srfc.$NODE recv GET W_SDF_GETRESOURCEINFRANODE err reject res"),
	APP_RECV_SDF_GET_W_SDF_GETRESOURCEINFRANODE_RESPONSE_UNKNOWN_ERROR("$APPNAME.srfc.$NODE recv GET W_SDF_GETRESOURCEINFRANODE err unknown_error res"),
	APP_RECV_SDF_GET_W_SDF_GETRESOURCEINFRANODE_RESPONSE_SUCCESS("$APPNAME.srfc.$NODE recv GET W_SDF_GETRESOURCEINFRANODE success res"),
	APP_RECV_SDF_GET_W_SDF_GETRESOURCEINFRANODE_RESPONSE_SUCCESS_WITH_ERROR("$APPNAME.srfc.$NODE recv GET W_SDF_GETRESOURCEINFRANODE success with bussiness err res"),
	APP_RECV_SDF_GET_W_SDF_GETRESOURCEINFRANODE_EXE_TIME("$APPNAME.srfc.$NODE recv GET W_SDF_GETRESOURCEINFRANODE_EXE_TIME success $EXE_TIME res"),
	
	// W_SDF_POSTREPORT
	APP_SEND_SDF_GET_W_SDF_POSTREPORT_REQUEST("$APPNAME.srfc.$NODE send GET W_SDF_POSTREPORT success req"),
	APP_RECV_SDF_GET_W_SDF_POSTREPORT_RESPONSE_TIMEOUT("$APPNAME.srfc.$NODE recv GET W_SDF_POSTREPORT err connection timeout res"),
	APP_RECV_SDF_GET_W_SDF_POSTREPORT_RESPONSE_REJECT("$APPNAME.srfc.$NODE recv GET W_SDF_POSTREPORT err reject res"),
	APP_RECV_SDF_GET_W_SDF_POSTREPORT_RESPONSE_UNKNOWN_ERROR("$APPNAME.srfc.$NODE recv GET W_SDF_POSTREPORT err unknown_error res"),
	APP_RECV_SDF_GET_W_SDF_POSTREPORT_RESPONSE_SUCCESS("$APPNAME.srfc.$NODE recv GET W_SDF_POSTREPORT success res"),
	APP_RECV_SDF_GET_W_SDF_POSTREPORT_RESPONSE_SUCCESS_WITH_ERROR("$APPNAME.srfc.$NODE recv GET W_SDF_POSTREPORT success with bussiness err res"),
	APP_RECV_SDF_GET_W_SDF_POSTREPORT_EXE_TIME("$APPNAME.srfc.$NODE recv GET W_SDF_POSTREPORT_EXE_TIME success $EXE_TIME res"),

	
	// W_SDF_RESERVEQUOTA
	APP_SEND_SDF_GET_W_SDF_RESERVEQUOTA_REQUEST("$APPNAME.srfc.$NODE send GET W_SDF_RESERVEQUOTA success req"),
	APP_RECV_SDF_GET_W_SDF_RESERVEQUOTA_RESPONSE_TIMEOUT("$APPNAME.srfc.$NODE recv GET W_SDF_RESERVEQUOTA err connection timeout res"),
	APP_RECV_SDF_GET_W_SDF_RESERVEQUOTA_RESPONSE_REJECT("$APPNAME.srfc.$NODE recv GET W_SDF_RESERVEQUOTA err reject res"),
	APP_RECV_SDF_GET_W_SDF_RESERVEQUOTA_RESPONSE_UNKNOWN_ERROR("$APPNAME.srfc.$NODE recv GET W_SDF_RESERVEQUOTA err unknown_error res"),
	APP_RECV_SDF_GET_W_SDF_RESERVEQUOTA_RESPONSE_SUCCESS("$APPNAME.srfc.$NODE recv GET W_SDF_RESERVEQUOTA success res"),
	APP_RECV_SDF_GET_W_SDF_RESERVEQUOTA_RESPONSE_SUCCESS_WITH_ERROR("$APPNAME.srfc.$NODE recv GET W_SDF_RESERVEQUOTA success with bussiness err res"),
	APP_RECV_SDF_GET_W_SDF_RESERVEQUOTA_EXE_TIME("$APPNAME.srfc.$NODE recv GET W_SDF_RESERVEQUOTA_EXE_TIME success $EXE_TIME res"),
	
	// W_SDF_RELEASEQUOTA
	APP_SEND_SDF_GET_W_SDF_RELEASEQUOTA_REQUEST("$APPNAME.srfc.$NODE send GET W_SDF_RELEASEQUOTA success req"),
	APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_TIMEOUT("$APPNAME.srfc.$NODE recv GET W_SDF_RELEASEQUOTA err connection timeout res"),
	APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_REJECT("$APPNAME.srfc.$NODE recv GET W_SDF_RELEASEQUOTA err reject res"),
	APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_UNKNOWN_ERROR("$APPNAME.srfc.$NODE recv GET W_SDF_RELEASEQUOTA err unknown_error res"),
	APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_SUCCESS("$APPNAME.srfc.$NODE recv GET W_SDF_RELEASEQUOTA success res"),
	APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_RESPONSE_SUCCESS_WITH_ERROR("$APPNAME.srfc.$NODE recv GET W_SDF_RELEASEQUOTA success with bussiness err res"),
	APP_RECV_SDF_GET_W_SDF_RELEASEQUOTA_EXE_TIME("$APPNAME.srfc.$NODE recv GET W_SDF_RELEASEQUOTA_EXE_TIME success $EXE_TIME res"),
	
	
	//W_SDF_COMMITQUOTALNFRA
	APP_SEND_SDF_PUT_W_SDF_COMMITQUOTALNFRA_REQUEST("$APPNAME.srfc.$NODE send PUT W_SDF_COMMITQUOTALNFRA success req"),
	APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_RESPONSE_TIMEOUT("$APPNAME.srfc.$NODE recv PUT W_SDF_COMMITQUOTALNFRA err connection timeout res"),
	APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_REJECT("$APPNAME.srfc.$NODE recv PUT W_SDF_COMMITQUOTALNFRA err reject res"),
	APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_UNKNOWN_ERROR("$APPNAME.srfc.$NODE recv PUT W_SDF_COMMITQUOTALNFRA err unknown_error res"),
	APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_SUCCESS("$APPNAME.srfc.$NODE recv PUT W_SDF_COMMITQUOTALNFRA success res"),
	APP_RECV_SDF_PUT_W_SDF_COMMITQUOTALNFRA_SUCCESS_WITH_ERROR("$APPNAME.srfc.$NODE recv PUT W_SDF_COMMITQUOTALNFRA success with bussiness err res"),
	APP_RECV_SDF_PUT_COMMITQUOTALNFRA_EXE_TIME("$APPNAME.srfc.$NODE recv PUT W_SDF_COMMITQUOTALNFRA success $EXE_TIME res"),
	;

	private String statName;

	EStats(String statName) {
		this.setStatName(statName);
	}

	public String getStatName() {
		return statName;
	}

	private void setStatName(String statName) {
		this.statName = statName;
	}

	public static EStats fromString(String text) {
		if (text != null) {
			for (EStats statName : EStats.values()) {
				if (text.equalsIgnoreCase(statName.statName)) {
					return statName;
				}
			}
		}
		return null;
	}
	
	public void setDynamicCustomStat(String af, String node, String send_recv, String method, String command, String success_err, String resultDescription, String req_res){
		String text;
		command=command.replaceAll(" ", "_");
		text=af+"."+node+" "+send_recv+" "+method+" "+command+" "+success_err+" "+resultDescription+" "+req_res;
		AFLog.d("dynamic custom stat : "+text);
		setStatName(text);
	}
	
	public void setClientCustomStat(EStats clientStat, String userSys){
		String appname = Config.getAPPNAME();
		String clientCustomStat = clientStat.statName.replace("$USERSYS", userSys).replace("$APPNAME", appname);
		AFLog.d("[STAT DEBUG] CLIENT CUSTOM STAT : "+clientCustomStat);
		setStatName(clientCustomStat);
	}
	public void setNeCustomStat(String neId,ResourceMappingCommand resourceMappingCommand, EResultDescription resultDescription, EEventType eventType){
		//APPNAME.srfc.$NEID.$INSTANCE &ACTION $METHOD $SUPCODE $RESULTDESCRIPTION $EVENTTYPE		
		//PGZ.srfc.HLR recv POST $suppcode success req
		//PGZ.srfc.HLR recv POST ServiceProvisioning success req

		String appname = Config.getAPPNAME();
		String node = resourceMappingCommand.getNeType();
		String action = "";
        String method = resourceMappingCommand.getMethod();
        String type = "";
        if(eventType.getEventType().equalsIgnoreCase("REQUEST")){
        	action = "send";
        	type = eventType.getEventTypeShort();
		} else if(eventType.getEventType().equalsIgnoreCase("RESPONSE")){
			action = "recv";
        	type = eventType.getEventTypeShort();
		}
        		
		String neCustomStat = appname+".srfc."+neId+" "+action+" "+method+" "+node+" "+resultDescription.getResultDesc()+" "+type;
		AFLog.d("[STAT DEBUG] NE CUSTOM STAT : "+neCustomStat);
		setStatName(neCustomStat);
	}
	
	public void setNeExeTimeCustomStat(String neId,ResourceMappingCommand resourceMappingCommand, EResultDescription resultDescription, EEventType eventType, long resTime){
		//PGZ.srfc.$NODE &ACTION $METHOD $COMMAND $RESULTDESCRIPTION $EVENTTYPE	
		//APPNAME.srfc.$NEID &ACTION $METHOD $NETYPE_EXE_TIME $RESULTDESCRIPTION $EVENTTYPE $RESTIME	
		//PGZ.srfc.HLR recv POST $suppcode success req
		//PGZ.srfc.HLR recv POST ServiceProvisioning success req
		String appname = Config.getAPPNAME();
		String node = resourceMappingCommand.getNeType();
		String action = "";
        String method = resourceMappingCommand.getMethod();
        String type = "";
        String timeLength = getTimeLength(resTime);
        if(eventType.getEventType().equalsIgnoreCase("REQUEST")){
        	action = "send";
        	type = eventType.getEventTypeShort();
		} else if(eventType.getEventType().equalsIgnoreCase("RESPONSE")){
			action = "recv";
        	type = eventType.getEventTypeShort();
		}
        		
		String neCustomStat = appname+".srfc."+neId+" "+action+" "+method+" "+node+"_EXE_TIME"+" "+resultDescription.getResultDesc()+" "+timeLength+" "+type;
		AFLog.d("[STAT DEBUG] NE CUSTOM STAT : "+neCustomStat);
		setStatName(neCustomStat);
	}

	

	public void setExeTimeCustomStat(EStats clientStat,String node,long resTime){
		String appname = Config.getAPPNAME();

	    String timeLength = getTimeLength(resTime);
		String customStat =clientStat.statName.replace("$APPNAME", appname).replace("$NODE", node).replace("$EXE_TIME", timeLength);
		AFLog.d("[STAT DEBUG] CUSTOM STAT IN ExeTime: "+customStat);
		setStatName(customStat);
	}
	
	public void setCustomStat(EStats clientStat, String node){
		String appname = Config.getAPPNAME();
		
		String customStat =clientStat.statName.replace("$APPNAME", appname).replace("$NODE", node);
		AFLog.d("[STAT DEBUG] CUSTOM STAT : "+customStat);
		setStatName(customStat);
	}
	private String getTimeLength(long resTime){
		String timeLength = "";
		if (resTime <= 500) {
			timeLength = "0-500_MS";
        }
        //501 - 1000 ms.
        else if (resTime <= 1000) {
        	timeLength = "501-1000_MS";
        }
        //1001 - 2000 ms.
        else if (resTime <= 2000) {
        	timeLength = "1001-2000_MS";
        }
        //2001 - 3000 ms.
        else if (resTime <= 3000) {
        	timeLength = "2001-3000_MS";
        }
        //3001 - 4000 ms.
        else if (resTime <= 4000) {
        	timeLength = "3001-4000_MS";
        }
        //4001 - 5000 ms.
        else if (resTime <= 5000) {
        	timeLength = "4001-5000_MS";
        }
        //> 5000 ms.
        else {
        	timeLength = "More_5000_MS";
        }
		return timeLength;
	}
}