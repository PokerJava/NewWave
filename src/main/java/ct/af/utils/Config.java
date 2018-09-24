package ct.af.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import ct.af.enums.EConfig;
import ct.af.resourceModel.*;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;


public class Config {

//	private static String SDF_LOCATION_INTERFACE;
	
    private static boolean hasReloadConfig = true;

    private static String LIB_VERSION;
    private static String PGZ_SERVER_NAME;
    private static String UserSysList;
    private static String Protocol;
    private static List<String> UrlList = new ArrayList<>();
    private static int  DEFAULT_SERVER_TIMEOUT;
    

    private static HashMap<String, ResourceMaster> resourceMasterHashMap = new HashMap<>();
    private static HashMap<String, ResourceInventory> resourceInventoryHashMap = new HashMap<>();
    private static HashMap<String, ResourceRuleMaster> resourceRuleHashMap = new HashMap<>();
    private static HashMap<String, ResourceNeTypeProperty> resourceNeTypePropertyHashMap = new HashMap<>();
    private static HashMap<String, ResourceMappingCommand> resourceMappingCommandHashMap = new HashMap<>();
    private static HashMap<String, ResourceProperty> resourcePropertyHashMap = new HashMap<>();
    private static HashMap<String, List<ResourceErrorHandling>> resourceErrorhandlingHashMap = new HashMap<>();
    private static HashMap<String, DropResourceOrderType> dropResourceOrderTypeHashMap = new HashMap<>();
    private static HashMap<String, HashMap<String, String>> overwriteDefHashMap = new HashMap<>();
    private static HashMap<String, HashMap<String, Object>> overwriteParamHashMap = new HashMap<>();
    private static HashMap<String, String> serverInterfaceHashMap = new HashMap<>();
    private static HashMap<String, String> urlServersToClientHashMap = new HashMap<>();
    private static HashMap<String, Integer> serversTimeoutHashMap = new HashMap<>();


    private static final DateTimeFormatter formatDateWithMiTz = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss.SSSZ"); //System logic
    private static final DateTimeFormatter journeyDateFormat = DateTimeFormat.forPattern("yyyyMMddHHmmssZ");
    private static final DateTimeFormatter journeyDateTask =  DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss:SSSZ");
    private static final DateTimeFormatter hrzDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZ");
    private static final DateTimeFormatter requestIdDate = DateTimeFormat.forPattern("yyyyMMddHHmmss");

                                                                        
    private static String SDF_INFRANODE_INTERFACE;
    private static String SDF_INVENTORY_INTERFACE;
    private static String SDF_JOURNEY_INTERFACE;
    private static String MONGO_INTERFACE;
    private static String SDF_RESERVEQUOTA_INTERFACE;
    private static String SDF_RELEASEQUOTA_INTERFACE;
    private static String SDF_COMMITQUOTA_INTERFACE;
    private static String MD_INTERFACE;
    private static String RECOVERY_FILE;


    private static String URL_SDF_INVENTORY;
    private static String URL_SDF_INFRANODE;
    private static String URL_SDF_JOURNEY;
    private static String URL_SDF_RESERVEQUOTA;
    private static String URL_SDF_RELEASEQUOTA;
    private static String URL_SDF_COMMITQUOTA;
    private static String URL_Mongo;
    private static String Actor;
    

    
    private static int SDF_INVENTORY_TIMEOUT;
    private static int SDF_INFRANODE_TIMEOUT;
    private static int SDF_JOURNEY_TIMEOUT;
    private static int SDF_RESERVEQUOTA_TIMEOUT;
    private static int SDF_RELEASEQUOTA_TIMEOUT;
    private static int SDF_COMMITQUOTA_TIMEOUT;
    private static int MONGO_TIMEOUT;
    private static int MD_TIMEOUT;
    private static int EXPIRATION_DATE;
    private static double GAP_EXECTIME;


    private static String PROVISIONING_URL;
    private static int EXPIRATIONDATE;
    private static int SDF_MAXRETRY;
    private static int REQUEST_MAXACTIVE;
    private static int CountMaxActive = 0;
    private static String APPNAME;
    private static String EDR_LOG;
	private static String CDR_LOG;
	private static String TRANSACTION_LOG;
	private static String INCOMING_UNKNOWN_LOG;
	private static String PATH_ES22_RECOVERYLOG_DUPLICATE;
	private static String PATH_ES22_RECOVERYLOG_TIMEOUT;
	private static String PATH_ES22_RECOVERYLOG_ERROR;
	private static String PATH_BACKUPRESOURCEINVENTORY;
	private static String PATH_FILEBACKUPRESOURCEINVENTORY;

    private static HashMap<String, ResourceSearchKeyNode> resourceIndexMap = new HashMap<>();
    private static HashMap<String, List<ResourceNeIdRouting>> resourceNeIdRoutingMap = new HashMap<>();
    private static boolean isRandomInvoke = true;
    private static boolean enableTimeoutManagerFlag = true;
    private static boolean ResourceInventoryFromFileMode;

    public static boolean verifyConfig(AbstractAF abstractAF) {

        try {

            for (EConfig conf : EConfig.values()) {
                String confName = conf.getName().trim();
                if (!abstractAF.getEquinoxUtils().getHmWarmConfig().containsKey(confName)) {
                    AFLog.e("Configuration named : " + confName + " [ NOT FOUND ], please check EC02 config !");
                    AFLog.e("Verify Config Failed!!");
                    return false;
                }
            }

            hasReloadConfig = true;

//            AFLog.d("Start Loading Config");
//            AFLog.d("==============================================================");
            Config.loadConfig(abstractAF);
            initialRandomInvoke(abstractAF);
            initialTimeoutManagerFlag(abstractAF);
            try{
            	List<String> valueUserSysList = Arrays.asList(UserSysList.split("\\|"));
            	for(String usersys:valueUserSysList){
            		String valueServers_Interface =getWarmConfig(abstractAF,"Resource-Name-"+usersys.trim()).trim();
            		
            		serverInterfaceHashMap.put(usersys, valueServers_Interface);  
            		String urlServersToClient="";
            		try{
            			    urlServersToClient =getWarmConfig(abstractAF,"URL-"+usersys.trim()).trim();
            			    urlServersToClientHashMap.put(usersys, urlServersToClient);
            		}catch (Exception e) {
            			    
            			 AFLog.w("Warning! "+"No url for "+usersys.trim());
					}
            		
            		
            		int serversTimeout;
            		try{
        			   
            			serversTimeout =  Integer.parseInt(getWarmConfig(abstractAF, "Tm-"+usersys.trim()));          			
            			serversTimeoutHashMap.put(usersys, serversTimeout);
        		}catch (Exception e) {
        			
        			 AFLog.w("Warning! "+"No time out for "+usersys.trim());
				}
        		
            		
            		
            	}
            }catch (Exception e) {
                AFLog.e("Verify Config Failed!! "+"UserSys not match ServersInterface");
                return false;
			}
            try{
            	UrlList = Arrays.asList(PROVISIONING_URL.split("\\|"));
            
            }catch (Exception e) {
                AFLog.e("Verify Config Failed!! "+"UserSys not match ServersInterface");
                return false;
			}
            

            
//            if (!initializeResourceInventoryHashMap(abstractAF)) {
//            	AFLog.d("Load Resource Inventory Failed");
//                return false;
//            }
            
            if (!initializeResourcePropertyHashMap(abstractAF)) {
            	AFLog.d("Load Resource Property Failed");
                return false;
            }        
            
            if (!initializeResourceSearchKeyHashmap(abstractAF)) {
            	AFLog.d("Load Resource SearchKey Failed");
                return false;
            }            
          
            if (!initializeResourceMasterHashMap(abstractAF)) {
            	AFLog.d("Load Resource Master Failed");
                return false;
            }

            if (!initializeResourceNeIdRouting(abstractAF)) {
                AFLog.d("Load Resource NeId Routing Failed");
                return false;
            }
            
            if (!initializeOverwriteDefHashMap(abstractAF)) {
            	AFLog.d("Load Overwrite Def Failed");
                return false;
            }


            if (!initializeResourceRuleHashMap(abstractAF)) {
            	AFLog.d("Load Resource Rule Failed");
                return false;
            }

            
            if (!initializeResourceNeTypeProperty(abstractAF)){
            	AFLog.d("Load Resource NeType Property Failed");
                return false;
            }
            
            if (!initializeResourceMappingCommandHashMap(abstractAF)) {
            	AFLog.d("Load Resource Mapping Failed");
                return false;
            } 
            
            if (!initializeResourceErrorHandlingHashMap(abstractAF)) {
            	AFLog.d("Load Resource Error Handling Failed");
                return false;
            }
            
            if (!initializeDropResourceOrderTypeHashMap(abstractAF)) {
            	AFLog.d("Load Drop Resource OrderType Failed");
                return false;
            }

            if(Integer.parseInt(getWarmConfig(abstractAF, "MaxActive_Request")) < 1){
            	AFLog.d("Verify Config Failed! MaxActive_Request is less than 0");
            	return false;
            }
            
            if (!initialTimeoutManagerFlag(abstractAF)) {
                AFLog.d("Fail Config  EnableTimeoutManagerFlag");
                return false;
			}
            if(!initalResourceInventoryFormFileName(abstractAF)&&Config.ResourceInventoryFromFileMode) 
            {
            	AFLog.d("ResourceInventory File not found!");
            	return false;
            }
            if(!initalResourceInventoryFormFileMode(abstractAF))
            {
            	AFLog.d("ResourceInventory Mode error not match :" + Config.getWarmConfig(abstractAF, EConfig.RESOURCEINVENTORYFORMFILEMODE.getName()) );
            	return false;
            }
            
            
            AFLog.d("Verify Config Success!!");
            AFLog.d("Lib Version : " + LIB_VERSION);

            return true;
        } catch (Exception e) {
            AFLog.e("Verify Config Failed!! "+e);
            return false;
        }
    }

	public static String getLibversion(AbstractAF abstractAF) {
        Map<String, Map<String, String>> coldConfigList = abstractAF.getEquinoxUtils().getHmColdConfig();
        Map<String, String> libAttribute = coldConfigList.get("Library");
        return libAttribute.get("name");
    }

    public static String getWarmConfig(AbstractAF abstractAF, String name) {
        return abstractAF.getEquinoxUtils().getHmWarmConfig().get(name).get(0);
    }

    public static int getSizeMultiWarmConfig(AbstractAF abstractAF, String name) {
        return abstractAF.getEquinoxUtils().getHmWarmConfig().get(name).size();
    }

    public static List<String> getMultiWarmConfig(AbstractAF abstractAF, String name) {
        return abstractAF.getEquinoxUtils().getHmWarmConfig().get(name);
    }

    public static void loadConfig(AbstractAF abstractAF) {
        LIB_VERSION = getLibversion(abstractAF);
        PGZ_SERVER_NAME = getWarmConfig(abstractAF, EConfig.PGZ_SERVER_NAME.getName());
        UserSysList =getWarmConfig(abstractAF, EConfig.UserSys_List.getName());
        DEFAULT_SERVER_TIMEOUT =Integer.parseInt(getWarmConfig(abstractAF, EConfig.DEFAULT_SERVER_TIMEOUT.getName()));
        SDF_INVENTORY_INTERFACE = getWarmConfig(abstractAF, EConfig.SDF_INVENTORY_INTERFACE.getName());
        SDF_INFRANODE_INTERFACE = getWarmConfig(abstractAF, EConfig.SDF_INFRANODE_INTERFACE.getName());
        SDF_RESERVEQUOTA_INTERFACE = getWarmConfig(abstractAF, EConfig.SDF_RESERVEQUOTA_INTERFACE.getName());
        SDF_RELEASEQUOTA_INTERFACE = getWarmConfig(abstractAF, EConfig.SDF_RELEASEQUOTA_INTERFACE.getName());
        SDF_COMMITQUOTA_INTERFACE = getWarmConfig(abstractAF, EConfig.SDF_COMMITQUOTA_INTERFACE.getName());
        SDF_JOURNEY_INTERFACE = getWarmConfig(abstractAF, EConfig.SDF_JOURNEY_INTERFACE.getName());
        MONGO_INTERFACE = getWarmConfig(abstractAF, EConfig.Mongo_INTERFACE.getName());
        MD_INTERFACE = getWarmConfig(abstractAF, EConfig.MD_INTERFACE.getName());


        URL_SDF_INVENTORY = getWarmConfig(abstractAF, EConfig.URL_SDF_INVENTORY.getName());
        URL_SDF_INFRANODE = getWarmConfig(abstractAF, EConfig.URL_SDF_INFRANODE.getName());
        URL_SDF_RESERVEQUOTA = getWarmConfig(abstractAF, EConfig.URL_SDF_RESERVEQUOTA.getName());
        URL_SDF_RELEASEQUOTA = getWarmConfig(abstractAF, EConfig.URL_SDF_RELEASEQUOTA.getName());
        URL_SDF_COMMITQUOTA = getWarmConfig(abstractAF, EConfig.URL_SDF_COMMITQUOTA.getName());
        URL_SDF_JOURNEY = getWarmConfig(abstractAF, EConfig.URL_SDF_JOURNEY.getName());
        URL_Mongo = getWarmConfig(abstractAF, EConfig.URL_Mongo.getName());
        RECOVERY_FILE = getWarmConfig(abstractAF, EConfig.RECOVERY_FILE.getName());
        PATH_ES22_RECOVERYLOG_DUPLICATE = getWarmConfig(abstractAF, EConfig.PATH_ES22_RECOVERYLOG_DUPLICATE.getName());
        PATH_ES22_RECOVERYLOG_TIMEOUT = getWarmConfig(abstractAF, EConfig.PATH_ES22_RECOVERYLOG_TIMEOUT.getName());
        PATH_ES22_RECOVERYLOG_ERROR = getWarmConfig(abstractAF, EConfig.PATH_ES22_RECOVERYLOG_ERROR.getName());
        PATH_BACKUPRESOURCEINVENTORY = getWarmConfig(abstractAF, EConfig.PATH_BACKUPRESOURCEINVENTORY.getName());
        PATH_FILEBACKUPRESOURCEINVENTORY = PATH_BACKUPRESOURCEINVENTORY + getWarmConfig(abstractAF, EConfig.PATH_FILEBACKUPRESOURCEINVENTORY.getName());
        
        
        SDF_INVENTORY_TIMEOUT = Integer.parseInt(getWarmConfig(abstractAF, EConfig.SDF_INVENTORY_TIMEOUT.getName()));
        SDF_INFRANODE_TIMEOUT = Integer.parseInt(getWarmConfig(abstractAF, EConfig.SDF_INFRANODE_TIMEOUT.getName()));
        SDF_RESERVEQUOTA_TIMEOUT = Integer.parseInt(getWarmConfig(abstractAF, EConfig.SDF_RESERVEQUOTA_TIMEOUT.getName()));
        SDF_RELEASEQUOTA_TIMEOUT = Integer.parseInt(getWarmConfig(abstractAF, EConfig.SDF_RELEASEQUOTA_TIMEOUT.getName()));
        SDF_COMMITQUOTA_TIMEOUT = Integer.parseInt(getWarmConfig(abstractAF, EConfig.SDF_COMMITQUOTA_TIMEOUT.getName()));
        SDF_JOURNEY_TIMEOUT = Integer.parseInt(getWarmConfig(abstractAF, EConfig.SDF_JOURNEY_TIMEOUT.getName()));
        MONGO_TIMEOUT = Integer.parseInt(getWarmConfig(abstractAF, EConfig.MONGO_TIMEOUT.getName()));
        MD_TIMEOUT = Integer.parseInt(getWarmConfig(abstractAF, EConfig.MD_TIMEOUT.getName()));
        EXPIRATION_DATE = Integer.parseInt(getWarmConfig(abstractAF, EConfig.Expiration_Date.getName()));
        Actor = getWarmConfig(abstractAF, EConfig.Actor.getName());
        GAP_EXECTIME = Double.parseDouble(getWarmConfig(abstractAF, EConfig.Gap_exectime.getName()));

        PROVISIONING_URL = getWarmConfig(abstractAF, EConfig.PROVISIONING_URL.getName());
        EXPIRATIONDATE = Integer.parseInt(getWarmConfig(abstractAF, EConfig.EXPIRATIONDATE.getName()));
        EDR_LOG = getWarmConfig(abstractAF, EConfig.EDR_LOG.getName());
        CDR_LOG = getWarmConfig(abstractAF, EConfig.CDR_LOG.getName());
        TRANSACTION_LOG = getWarmConfig(abstractAF, EConfig.TRANSACTION_LOG.getName());
        INCOMING_UNKNOWN_LOG = getWarmConfig(abstractAF, EConfig.INCOMING_UNKNOWN_LOG.getName());
        REQUEST_MAXACTIVE = Integer.parseInt(getWarmConfig(abstractAF, EConfig.REQUEST_MAXACTIVE.getName()));
        ResourceInventoryFromFileMode = ResourceInventoryMode(changeFormatResourceInvnetoryFormFileMode(getWarmConfig(abstractAF, EConfig.RESOURCEINVENTORYFORMFILEMODE.getName())));

        
        if(abstractAF.getEquinoxUtils().getHmWarmConfig().containsKey("MaxRetry-SDF"))
        {
            SDF_MAXRETRY = Integer.parseInt(getWarmConfig(abstractAF, "MaxRetry-SDF"));
        }
        
        if(abstractAF.getEquinoxUtils().getHmColdConfig().containsKey("Application")) {
        	APPNAME = abstractAF.getEquinoxUtils().getHmColdConfig().get("Application").get("group");
        }
 
        Protocol = getWarmConfig(abstractAF, EConfig.Protocol.getName());

        hasReloadConfig = false;
    }



    public static boolean initializeResourceMasterHashMap(AbstractAF abstractAF) {
//        AFLog.d("Loading Resource Master...");
        Gson gson = GsonPool.getGson();
        JsonObject jsonObject;
        String keyName = "ResourceMaster";
        boolean isVerifyPass = true;
        
        try {
            String filePath = getWarmConfig(abstractAF, keyName);            
            jsonObject = (JsonObject) new JsonParser().parse(new FileReader(filePath));
            JsonArray arr = (JsonArray) jsonObject.get("resultData");
            
            for (int j = 0; j< arr.size() ; j++) {
            	boolean isValid = true;
        		ResourceMaster master = gson.fromJson(arr.get(j).toString(), ResourceMaster.class);
        
        		if(master.getResourceIndex()!=null&&!master.getResourceIndex().trim().isEmpty()) {
	        		if(master.getMandatory()==null) {
	        			isValid=false;
	        			AFLog.e(master.getResourceIndex()+" mandatoryParam is missing or invalid in "+keyName+".json");
	        		}
	        		if(master.getValidate()==null) {
	        			isValid=false;
	        			AFLog.e(master.getResourceIndex()+" validateParam is missing or invalid in "+keyName+".json");
	        		}
	        		
	        		List<SuppCode> suppCodeList = master.getSuppCodesList();
	        		if(suppCodeList!=null&&!suppCodeList.isEmpty()) {
	        			for(SuppCode currentSuppcode : suppCodeList) {
	        				if(currentSuppcode.getSuppCode()!=null&&!currentSuppcode.getSuppCode().trim().isEmpty()) {
	        					if(currentSuppcode.getMandatoryFlag()!=null&&!currentSuppcode.getMandatoryFlag().trim().isEmpty()) {
	        						if(!currentSuppcode.getMandatoryFlag().equals("M")&&!currentSuppcode.getMandatoryFlag().equals("O")) {
	        							isValid=false;
	        							AFLog.e(master.getResourceIndex()+" mandatoryFlag must be \"M\" or \"O\" in "+keyName+".json");
	        						}
	        					} else {
	        						isValid=false;
	        						AFLog.e(master.getResourceIndex()+" is not found mandatoryFlag in "+keyName+".json");
	        					}
	        				} else {
	        					isValid=false;
								AFLog.e(master.getResourceIndex()+" suppCode is not found in "+keyName+".json");
	        				}
	        			}
	        		} else {
	        			isValid=false;
						AFLog.e(master.getResourceIndex()+" suppCode is not found in "+keyName+".json");
	        		}
	        		
	        		if(master.getResponseToClientType()!=null&&!master.getResponseToClientType().isEmpty()) {
	        			if(master.getResponseToClientType().equals("Special")) {
	        				if(master.getResponseToClient()==null||master.getResponseToClient().trim().isEmpty()) {
	        					isValid=false;
	        					AFLog.e(master.getResourceIndex()+" responseToClientType is Special then responseToClient must not be missing or empty in "+keyName+".json");
	        				}
	        			} else if(!master.getResponseToClientType().equals("Normal")){
	        				isValid=false;
	    					AFLog.e(master.getResourceIndex()+" responseToClientType must be Normal or Special in "+keyName+".json");
	        			}
	        		} else {
	        			master.setResponseToClientType("Normal");
	        		}
	        		
	        		if(isValid) {
	        			try {
	    					master = master.trim();
	    				} catch (Exception e) {
	    					
	    				}
	        			resourceMasterHashMap.put(String.valueOf(master.getResourceIndex()), master);
	        		} else {
	        			isVerifyPass = false;
	        		}
        		} else {
        			isVerifyPass = false;
        			AFLog.e("ResourceIndex is not found in "+keyName+".json");
        		}
            }
        } catch (FileNotFoundException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file not found.");
        } catch (NullPointerException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " not found.");
        } catch (JsonParseException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file is invalid format. (" + ex.getMessage() + ").");
        } catch (Exception ex) {
            isVerifyPass = false;
            AFLog.e("[Exception] can't load "+keyName+".");
            AFLog.e(ex);
        }	
        
//        try { 
//        AFLog.d("========================= show value =========================");
//        AFLog.d("resourceMaster : "+resourceMasterHashMap.size()+" records");
//        for (Map.Entry resourceMaster : resourceMasterHashMap.entrySet()) {
//        	AFLog.d(resourceMaster.toString());
//        }                  
//        AFLog.d("==============================================================");
//        } catch (Exception ex) {
//            AFLog.e(ex);
//        }
        
        
        return isVerifyPass;
    }
    
    /**
     * 
     * This step will move to extract result from SDF
     * 
     */
    public static boolean initializeResourceInventoryHashMap(AbstractAF abstractAF) {
//    	AFLog.d("Loading Resource Inventory...");
        Gson gson = GsonPool.getGson();
        JsonObject jsonObject;
        String keyName = "ResourceInventory";
        boolean isVerifyPass = true;
        // load config of resource rule to JVM
        try {
            String filePath = getWarmConfig(abstractAF, keyName);  
            jsonObject = (JsonObject) new JsonParser().parse(new FileReader(filePath));
            JsonArray arr = (JsonArray) jsonObject.get("resultData");
        	for(int j = 0; j< arr.size() ; j++){
        		ResourceInventory invent = gson.fromJson(arr.get(j).toString(), ResourceInventory.class).trim();
        		resourceInventoryHashMap.put(String.valueOf(invent.getResourceName()), invent);
        	}

        } catch (FileNotFoundException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file not found.");
        } catch (NullPointerException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " not found.");
        } catch (JsonParseException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file is invalid format. (" + ex.getMessage() + ")");
        } catch (Exception ex) {
            isVerifyPass = false;
            AFLog.e("[Exception] can't load "+keyName+".");
            AFLog.e(ex);
        }
        
//        AFLog.d("========================= show value =========================");
//        AFLog.d("resourceInventory : "+resourceInventoryHashMap.size()+" records");
//        for (Map.Entry resourceInventory : resourceInventoryHashMap.entrySet()) {
//        	AFLog.d(resourceInventory.toString());
//        }                  
//        AFLog.d("==============================================================");

        
        
        return isVerifyPass;
    }

    
    public static boolean initializeResourceRuleHashMap(AbstractAF abstractAF) {
//        AFLog.d("Loading Resource Rule...");
        Gson gson = GsonPool.getGson();
        JsonObject jsonObject = null;
        String keyName = "ResourceRuleMaster";
        boolean isVerifyPass = true;
        ArrayList<String> ResourceRuleIdList = new ArrayList<String>();
        String error = "";
        // load configuration of resource rule to JVM
        try {
            String filePath = getWarmConfig(abstractAF, keyName);            
            jsonObject = (JsonObject) new JsonParser().parse(new FileReader(filePath));
            Type listType = new TypeToken<ArrayList<ResourceRuleMaster>>(){}.getType();
            List<ResourceRuleMaster> resourceRuleMasterList = gson.fromJson(jsonObject.get("resultData").toString(), listType);
         	GsonPool.pushGson(gson);
//            JsonArray arr = (JsonArray) jsonObject.get("resultData");
//            for(int j = 0; j< arr.size() ; j++){
         	for(ResourceRuleMaster rule:resourceRuleMasterList){
//        		ResourceRuleMaster rule = gson.fromJson(arr.get(j).toString(), ResourceRuleMaster.class).trim();
         		String resourceRuleId = rule.getResourceRuleId();
         		if(StringUtils.isBlank(rule.getResourceRuleId())){
         			error = "error: resourceRuleId is missing or invalid parameter in ResourceRuleMaster.json.";
         			break;
         		}else if(StringUtils.isBlank(rule.getResourceRuleState())){
         			error = "error: resourceRuleId "+resourceRuleId+" resourceRuleState is missing or empty parameter in ResourceRuleMaster.json.";
         			break;
         		}else if(StringUtils.isBlank(rule.getEffectiveDate())){
         			error = "error: resourceRuleId "+resourceRuleId+" effectiveDate is missing or empty parameter in ResourceRuleMaster.json.";
         			break;
         		}else if(StringUtils.isBlank(rule.getExpireDate())){
         			error = "error: resourceRuleId "+resourceRuleId+" expireDate is missing or empty parameter in ResourceRuleMaster.json.";
         			break;
         		}else if(rule.getRuleDetail() == null || rule.getRuleDetail().isEmpty()){
         			error = "error: resourceRuleId "+resourceRuleId+" ruleDetail is missing or empty parameter in ResourceRuleMaster.json.";
         			break;
         		}
        		else{
        			//check ResourceRuleId format 0-9 a-z A-Z
        			resourceRuleId = resourceRuleId.trim().replaceAll("\\s", "");
        			rule.setResourceRuleId(resourceRuleId);
        			if(!resourceRuleId.matches("[0-9a-zA-Z]+")){
        				isVerifyPass = false;
    					error = "error: ResourceRuleId \""+resourceRuleId+"\" is wrong format [0-9][a-z][A-Z].";
    					break;
        			}
        			//check duplication Rule
        			if(ResourceRuleIdList.size() > 0 ){
        				if(ResourceRuleIdList.contains(resourceRuleId)){
        					isVerifyPass = false;
        					error = "error: resourceRuleId "+resourceRuleId+" is duplicated in ResourceRuleMaster.json.";
        				}
        			}
        			ResourceRuleIdList.add(resourceRuleId);
        				if(!rule.getResourceRuleState().equals("Active") && !rule.getResourceRuleState().equals("Inactive")){
        				error = "error: resourceRuleState must be \"Active\" or \"Inactive\" in ResourceRuleMaster.json.";
	        			}else if(rule.getResourceRuleState().equals("Active")){
	        						//Check format effectivedDate and expireDate must length = 14(substring +0700) 
			        				if(rule.getEffectiveDate().indexOf("+0700") == -1 || rule.getEffectiveDate().replace("+0700", "").length() != 14 || rule.getExpireDate().indexOf("+0700") == -1 || rule.getExpireDate().replace("+0700", "").length() != 14){
			        					error = "error: EffectiveDate or ExpireDate is wrong format(yyyyMMddHHmmssZ)";
			        				//Check effectiveDate more than expireDate
			        				}else if(Long.parseLong(rule.getEffectiveDate().replace("+0700", "")) >= Long.parseLong(rule.getExpireDate().replace("+0700", ""))){
			        					error = "error: resourceRuleId"+resourceRuleId+" effectiveDate cannot more than expireDate in ResourceRuleMaster.json.";
			        				}else{
			        					String[] checkValOper = {">","<","<=",">=","=","!="};
			        					String[] checkRangeOper = {"between","not between"};
			        					ArrayList<String>ListcheckValOper = new ArrayList<String>();
			        					ArrayList<String>ListcheckRangeOper = new ArrayList<String>();
			        					//add operand to arraylist for check
			        					for(int i=0;i<checkValOper.length;i++){
			        						ListcheckValOper.add(checkValOper[i]);
			        					}
			        					for(int i=0;i<checkRangeOper.length;i++){
			        						ListcheckRangeOper.add(checkRangeOper[i]);
			        					}
			        					List<ResourceRule> ruleDetailList = rule.getRuleDetail();
			        					//check size rulDetail
			        					if(ruleDetailList.size() < 0){
			        						error = "error: ruleDetailList is no parameter";
			        					}else{
			        							int indexRuleDetail = 1;
				        						for(ResourceRule ruleDetail:ruleDetailList){
					        	        			 //check ruleType
				        							if(StringUtils.isBlank(ruleDetail.getRuleType())){
				        								error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") ruleType is missing or empty parameter in ResourceRuleMaster.json."; 
				        							}else if(!ruleDetail.getRuleType().equals("checkVal") && !ruleDetail.getRuleType().equals("checkRange")){
					        	        				 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") ruleType is invalid in ResourceRuleMaster.json."; 
					        	        			 }else if(ruleDetail.getRuleType().toString().equals("checkVal")){
					        	        				 //check blank in ruleDetail
					        	        				 if(StringUtils.isBlank(ruleDetail.getParamName())){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") paramName is missing or empty parameter in ResourceRuleMaster.json.";
					        	        				 }else if(StringUtils.isBlank(ruleDetail.getParamOperand())){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") paramOperand is missing or empty parameter in ResourceRuleMaster.json.";
					        	        				 }else if(StringUtils.isBlank(ruleDetail.getParamValue())){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") ruleType is checkVal and therefore it must be configured with paramValue in ResourceRuleMaster.json.";
					        	        					 //check $ in paramName
					        	        				 }else if(ruleDetail.getParamName().indexOf("$") < 0){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") paramName"+ruleDetail.getParamName()+"has invalid root Node in ResourceRuleMaster.json.";
					        	        				//check $ more than 1
					        	        				 }else if(ruleDetail.getParamName().indexOf("$") != ruleDetail.getParamName().lastIndexOf("$")){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") paramName"+ruleDetail.getParamName()+"has more than 1 root Node in ResourceRuleMaster.json.";
					        	        				//check index of $ 
					        	        				 }else if(!ruleDetail.getParamName().substring(0,ruleDetail.getParamName().indexOf("$")).equals("resourceOrder")
					        	        						 &&!ruleDetail.getParamName().substring(0,ruleDetail.getParamName().indexOf("$")).equals("resourceOrderItem")
					        	        						 &&!ruleDetail.getParamName().substring(0,ruleDetail.getParamName().indexOf("$")).equals("neidRouting")){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") paramName "+ruleDetail.getParamName()+" has invalid root Node in ResourceRuleMaster.json.";
					        	        				//check paramName after index of $
					        	        				 }else if(ruleDetail.getParamName().substring(ruleDetail.getParamName().indexOf("$")+1).equals("")){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") has invalid paramName in ResourceRuleMaster.json.";
					        	        				//check operand of checkVal can use
					        	        				 }else if(!ListcheckValOper.contains(ruleDetail.getParamOperand())){
					        	        					 error = "error: resourceRuleId"+resourceRuleId+" ruleDetail("+indexRuleDetail+") ruleType is checkVal and therefore paramOperand must be \"=\", \"!=\", \"<\", \">\", \"<=\" or \">=\" in ResourceRuleMaster.json.";
					        	        				//check case paramValue is string cant use operand >,<,>=,<=
					        	        				 }else if(!StringUtils.isNumeric(ruleDetail.getParamValue())){
					        	        					 if(ruleDetail.getParamOperand().equals(">") || ruleDetail.getParamOperand().equals("<") || ruleDetail.getParamOperand().equals(">=") || ruleDetail.getParamOperand().equals("<=")){
					        	        						 error = "resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") paramOperand = \""+ruleDetail.getParamOperand()+"\" paramValue must be numeric in ResourceRuleMaster.json.";
					        	        						  }
					        	        				 }
					        	        			 }else if(ruleDetail.getRuleType().equals("checkRange")){
					        	        				//check blank in ruleDetail
					        	        				  if(StringUtils.isBlank(ruleDetail.getParamName())){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") paramName is missing or empty parameter in ResourceRuleMaster.json.";
					        	        				 }else if(StringUtils.isBlank(ruleDetail.getParamOperand())){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") paramOperand is missing or empty parameter in ResourceRuleMaster.json.";
					        	        				 }else if(StringUtils.isBlank(ruleDetail.getParamStart()) || StringUtils.isBlank(ruleDetail.getParamStop())){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") ruleType is checkRange and therefore it must be configured with paramStart and paramStop in ResourceRuleMaster.json.";
					        	        				 //check $ in paramName
					        	        				 }else if(ruleDetail.getParamName().indexOf("$") < 0){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") paramName"+ruleDetail.getParamName()+"has invalid root Node in ResourceRuleMaster.json.";
					        	        				//check $ more than 1
					        	        				 }else if(ruleDetail.getParamName().indexOf("$") != ruleDetail.getParamName().lastIndexOf("$")){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") paramName"+ruleDetail.getParamName()+"has more than 1 root Node in ResourceRuleMaster.json.";
					        	        				//check index of $ 
					        	        				 }else if(!ruleDetail.getParamName().substring(0,ruleDetail.getParamName().indexOf("$")).equals("resourceOrder")
					        	        						 &&!ruleDetail.getParamName().substring(0,ruleDetail.getParamName().indexOf("$")).equals("resourceOrderItem")
					        	        						 &&!ruleDetail.getParamName().substring(0,ruleDetail.getParamName().indexOf("$")).equals("neidRouting")){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") paramName "+ruleDetail.getParamName()+" has invalid root Node in ResourceRuleMaster.json.";
					        	        				//check paramName after index of $
					        	        				 }else if(ruleDetail.getParamName().substring(ruleDetail.getParamName().indexOf("$")+1).equals("")){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") has invalid paramName in ResourceRuleMaster.json.";
					        	        				//check operand of checkRange can use
					        	        				 }else if(!ListcheckRangeOper.contains(ruleDetail.getParamOperand())){
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") ruleType is checkRange and therefore paramOperand must be \"between\" or \"not between\" in ResourceRuleMaster.json.";
					        	        				//check numeric of paramStart paramStop
					        	        				 }else if(StringUtils.isNumeric(ruleDetail.getParamStart()) && StringUtils.isNumeric(ruleDetail.getParamStop())){
					        	        					 //check paramStart >= paramStop
					        	        					 if(Long.parseLong(ruleDetail.getParamStart()) > Long.parseLong(ruleDetail.getParamStop())){
					        	        						 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") paramOperand = \""+ruleDetail.getParamOperand()+"\" paramStart or paramStop must be numeric in ResourceRuleMaster.json.";
					        	        					 }
					        	        				 }else{
					        	        					 error = "error: resourceRuleId "+resourceRuleId+" ruleDetail("+indexRuleDetail+") paramOperand = \""+ruleDetail.getParamOperand()+"\" paramStart or paramStop must be numeric in ResourceRuleMaster.json.";
					        	        				 }
					        	        			 }
					        	        			 indexRuleDetail++;
					        	        		 }
			        					}
			        				}
	        			}
        			resourceRuleHashMap.put(String.valueOf(rule.getResourceRuleId()), rule);
        		}
        	}
            if(!error.equals("")){
    			AFLog.d(error);
    			isVerifyPass = false;
    		}
        } catch (FileNotFoundException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file not found.");
        } catch (NullPointerException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " not found.");
        } catch (JsonParseException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file is invalid format. (" + ex.getMessage() + ")");
        } catch (Exception ex) {
            isVerifyPass = false;
            AFLog.e("[Exception] can't load "+keyName+".");
            AFLog.e(ex);;
        }
        
//        AFLog.d("========================= show value =========================");
//        AFLog.d("resourceRuleMaster : "+resourceRuleHashMap.size()+" records");
//        for (Map.Entry resourceRule : resourceRuleHashMap.entrySet()) {
//        	AFLog.d(resourceRule.toString());
//        }                  
//        AFLog.d("==============================================================");
        
        //Check RuleList in ResourceProperty
        if(error.equals(""))
        {
        	Gson gson2 = GsonPool.getGson();
            keyName = "ResourceProperty";
            try {
                String filePath = getWarmConfig(abstractAF, keyName);
                jsonObject = (JsonObject) new JsonParser().parse(new FileReader(filePath));
                Type listType = new TypeToken<ArrayList<ResourceProperty>>(){}.getType();
                List<ResourceProperty> resourcePropertiesList = gson2.fromJson(jsonObject.get("resultData").toString(), listType);
            	GsonPool.pushGson(gson2);
            	ArrayList<String>checkRuleProperty = new ArrayList<String>();
            	String errorRule = ""; 
                for(ResourceProperty resourceProperties : resourcePropertiesList){
                	//check rulelist in resourceProperty
                	if(resourceProperties.getResourceRuleList().size() > 0 ){
                		for(String Rule : resourceProperties.getResourceRuleList()){
                			Rule = Rule.trim().replaceAll("\\s", "");
                			if(ResourceRuleIdList.contains(Rule)|| Rule.equals("")){
                				checkRuleProperty.add("true");
                    		}
                			else{
                				checkRuleProperty.add("false");
                				errorRule = "error: "+keyName+" resourceRuleList "+resourceProperties.getResourceRuleList() +"(only invalid resourceRuleId, in this case is \""+Rule+"\") in ResourceProperty.json is configure resourceRuleId that doesn't exist in ResourceRuleMaster.json. ";
                				
                			}
                		}
                		
                		if(checkRuleProperty.contains("false")){
                			isVerifyPass = false;
                			AFLog.e(errorRule);
                		}
                	}
                }
            } catch (FileNotFoundException ex) {
                isVerifyPass = false;
                AFLog.e(keyName + " config file not found.");
            } catch (NullPointerException ex) {
                isVerifyPass = false;
                AFLog.e(keyName + " not found.");
            } catch (JsonParseException ex) {
                isVerifyPass = false;
                AFLog.e(keyName + " config file is invalid format. (" + ex.getMessage() + ")");
            } catch (Exception ex) {
                isVerifyPass = false;
                AFLog.e("[Exception] can't load "+keyName+".");
                AFLog.e(ex);
            }
            
            Gson gson3 = GsonPool.getGson();
            keyName = "ResourceMaster";
            
            try {
                String filePath = getWarmConfig(abstractAF, keyName);            
                jsonObject = (JsonObject) new JsonParser().parse(new FileReader(filePath));
                JsonArray arr = (JsonArray) jsonObject.get("resultData");
                ArrayList<String>checkRuleProperty = new ArrayList<String>();
                String errorRule = ""; 
                for (int j = 0; j< arr.size() ; j++) {
            		ResourceMaster master = gson3.fromJson(arr.get(j).toString(), ResourceMaster.class).trim();
            		List<SuppCode> suppCodeList = master.getSuppCodesList();
            		if(suppCodeList!=null&&!suppCodeList.isEmpty()) {
            			for(SuppCode currentSuppcode : suppCodeList) {
                        	if(currentSuppcode.getResourceRuleList().size() > 0 ){
                        		for(String Rule : currentSuppcode.getResourceRuleList()){
                        			Rule = Rule.trim().replaceAll("\\s", "");
                        			if(ResourceRuleIdList.contains(Rule)||Rule.equals("")){
                        				checkRuleProperty.add("true");
                            		}
                        			else{
                        				checkRuleProperty.add("false");
                        				errorRule = "error: "+keyName+" resourceRuleList "+currentSuppcode.getResourceRuleList() +"(only invalid resourceRuleId, in this case is \""+Rule+"\") in ResourceMaster.json is configure resourceRuleId that doesn't exist in ResourceRuleMaster.json.";
                        			}
                        		}
                        		
                        		if(checkRuleProperty.contains("false")){
                        			isVerifyPass = false;
                        			AFLog.e(errorRule);
                        		}
                        	}
            			}
            		}
                }
            } catch (FileNotFoundException ex) {
                isVerifyPass = false;
                AFLog.e(keyName + " config file not found.");
            } catch (NullPointerException ex) {
                isVerifyPass = false;
                AFLog.e(keyName + " not found.");
            } catch (JsonParseException ex) {
                isVerifyPass = false;
                AFLog.e(keyName + " config file is invalid format. (" + ex.getMessage() + ")");
            } catch (Exception ex) {
                isVerifyPass = false;
                AFLog.e("[Exception] can't load "+keyName+".");
                AFLog.e(ex);
            }
        }
        return isVerifyPass;
    }
    

    
    
    public static boolean initializeResourceNeTypeProperty(AbstractAF abstractAF) {
//    	AFLog.d("Loading Resource NeType Property...");
        Gson gson = GsonPool.getGson();
        JsonObject jsonObject;
        String keyName = "ResourceNeTypeProperty";
        
        boolean isVerifyPass = true;
        try {
        	String filePath = getWarmConfig(abstractAF, keyName);            
            jsonObject = (JsonObject) new JsonParser().parse(new FileReader(filePath));
            JsonArray arr = (JsonArray) jsonObject.get("resultData");
            
            String[] confProtocolArray = Protocol.split("\\|");
            boolean isValidProtocol = false;
            
            for(int j = 0; j < arr.size() ; j++) {
            	
            	ResourceNeTypeProperty neType = gson.fromJson(arr.get(j).toString(), ResourceNeTypeProperty.class).trim();
            	for (String format : confProtocolArray) {
            		format = format.trim();
                    if (format.equalsIgnoreCase(neType.getProtocol().trim())) {
                        isValidProtocol = true;
                    }
                }
            	if(isValidProtocol) {
//	        		System.out.println("neTypeProperty ==> " + neType.getNeType() + " : " + neType.toString());
	        		if(neType.getProtocol().equalsIgnoreCase("HTTP")&&(!neType.getCType().equalsIgnoreCase("text/xml")&&!neType.getCType().equalsIgnoreCase("text/plain"))) {
	        			 isVerifyPass = false;
	        	         AFLog.e(keyName + " Load Failed : neType = "+neType.getNeType()+" and Protocol = HTTP : cType must be text/xml or text/plain.");
	        		}else if (neType.getProtocol().equalsIgnoreCase("SOCKET")&&!neType.getCType().equalsIgnoreCase("tcp")) {
	        			isVerifyPass = false;
	       	         	AFLog.e(keyName + " Load Failed : neType = "+neType.getNeType()+" and Protocol = SOCKET : cType must be tcp.");
	        		}
	        		resourceNeTypePropertyHashMap.put(String.valueOf(neType.getNeType()), neType);
	        		isValidProtocol = false;
            	}else {
            		isVerifyPass = false;
       	         	AFLog.e(keyName + " Load Failed : Protocol of neType = "+ neType.getNeType()+" is invalid.");
            	}
            }
        	
        	//AFLog.d("OK!");
        } catch (FileNotFoundException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file not found.");
        } catch (NullPointerException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " not found.");
        } catch (JsonParseException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file is invalid format. (" + ex.getMessage() + ")");
        } catch (Exception ex) {
            isVerifyPass = false;
            AFLog.e("[Exception] can't load "+keyName+".");
            AFLog.e(ex);
        }
        
//        AFLog.d("========================= show value =========================");
//        AFLog.d("resourceNeTypeProperty : "+resourceNeTypePropertyHashMap.size()+" records");
//        for (Map.Entry resourceNeTypeProperty : resourceNeTypePropertyHashMap.entrySet()) {
//        	AFLog.d(resourceNeTypeProperty.toString());
//        }                  
//        AFLog.d("==============================================================");
        
		return isVerifyPass ;
    }

    public static boolean initializeResourceMappingCommandHashMap(AbstractAF abstractAF) {
//        AFLog.d("Loading Resource Mapping Command...");
        Gson gson = GsonPool.getGson();
        JsonObject jsonObject;
        String keyName = "ResourceMappingCommand";
        String messageTemplatePathConfigName = "MessageTemplatePath";

        boolean isVerifyPass = true;        
        try {
            String filePath = getWarmConfig(abstractAF, keyName);
            String messageTemplatePath = getWarmConfig(abstractAF, messageTemplatePathConfigName);
            //AFLog.d("[]path : "+filePath);
            jsonObject = (JsonObject) new JsonParser().parse(new FileReader(filePath));
            //AFLog.d(jsonObject.toString());
            //AFLog.d("OK!");
            Type listType = new TypeToken<ArrayList<ResourceMappingCommand>>(){}.getType();
            List<ResourceMappingCommand> resourceMappingCommandList = gson.fromJson(jsonObject.get("resultData").toString(), listType);
            GsonPool.pushGson(gson);

            for(ResourceMappingCommand resourceMappingCommand:resourceMappingCommandList){
            	if(resourceMappingCommand.getSuppcode()!=null&&!resourceMappingCommand.getSuppcode().trim().isEmpty()) {
	            	if(resourceMappingCommand.getNeType()!=null&&!resourceMappingCommand.getNeType().trim().isEmpty()) {
	            		if(resourceMappingCommand.getSuccessPattern()!=null&&!resourceMappingCommand.getSuccessPattern().trim().isEmpty()) {
	            			if(resourceMappingCommand.getMessageFormat()!=null&&!resourceMappingCommand.getMessageFormat().trim().isEmpty()) {
	            				if(resourceMappingCommand.getTimeOut()!=null&&!resourceMappingCommand.getTimeOut().trim().isEmpty()) {
	            					if(resourceMappingCommand.getMethod()==null||resourceMappingCommand.getMethod().trim().isEmpty()) {
	            						resourceMappingCommand.setMethod("POST");
	            					}
	            					
					            	resourceMappingCommandHashMap.put(resourceMappingCommand.getSuppcode(), resourceMappingCommand.trim());
					
					                // read request and response message
//					                AFLog.d("Reading message templates file: " + messageTemplatePath + "/" + resourceMappingCommand.getSuppcode() + ".txt");
					                try(BufferedReader br = new BufferedReader(new FileReader(messageTemplatePath + "/" + resourceMappingCommand.getSuppcode() + ".txt"))) {
					
					                    boolean currentlyRequest = false;
					                    boolean currentlyResponse = false;
					                    boolean currentlyRequestURL = false;
					
					                    int ifBracket = 0;
					                    int foreachBracket = 0;
					                    int lineNo = 1;
					                    int requiredRequestField = 0;
				                        int requiredResponseField = 0;
				                        int requiredRequestURLField = 0;
					                    for(String line; (line = br.readLine()) != null; )
					                    {
					                        if(line.trim().equals("@request") && (!currentlyRequest && !currentlyResponse))
					                        {
					                            currentlyRequest = true;
					                            lineNo++;
				                                requiredRequestField++;
					                            continue;
					                        }
					                        else if(line.trim().equals("@endrequest"))
					                        {
					                            currentlyRequest = false;
					                            lineNo++;
				                                requiredRequestField++;
					                            continue;
					                        }
					                        else if(line.trim().equals("@response") && (!currentlyRequest && !currentlyResponse))
					                        {
					                            currentlyResponse = true;
					                            lineNo++;
				                                requiredResponseField++;
					                            continue;
					                        }
					                        else if(line.trim().equals("@endresponse"))
					                        {
					                            currentlyRequest = false;
					                            lineNo++;
				                                requiredResponseField++;
					                            continue;
					                        }
	                                        else if(line.trim().equals("@requesturl") && (!currentlyRequest && !currentlyResponse && !currentlyRequestURL))
	                                        {
	                                            currentlyRequestURL = true;
	                                            lineNo++;
	                                            requiredRequestURLField++;
	                                            continue;
	                                        }
	                                        else if(line.trim().equals("@endrequesturl"))
	                                        {
	                                            currentlyRequestURL = false;
	                                            lineNo++;
	                                            requiredRequestURLField++;
	                                            continue;
	                                        }
					                        else if(line.contains("@if"))
					                        {
					                            if(balancedParenthensies(line))
					                            {
					                                ifBracket++;
					                                if(currentlyRequest)
					                                {
					                                    resourceMappingCommand.getRequestMessage().add(line.trim());
					                                }
					                                else if(currentlyResponse)
					                                {
					                                    resourceMappingCommand.getResponseMessage().add(line);
					                                }
	                                                else if(currentlyRequestURL)
	                                                {
	                                                    resourceMappingCommand.getRequestURLMessage().add(line.trim());
	                                                }
					                            }
					                            else
					                            {
					                                AFLog.e("[Error] unbalanced parenthesises at line: " + lineNo + " in " + resourceMappingCommand.getSuppcode() + ".txt");
					                                return false;
					                            }
					                        }
					                        else if(line.trim().equals("@endif"))
					                        {
					                            ifBracket--;
					                            if(currentlyRequest)
					                            {
					                                resourceMappingCommand.getRequestMessage().add(line.trim());
					                            }
					                            else if(currentlyResponse)
					                            {
					                                resourceMappingCommand.getResponseMessage().add(line);
					                            }
	                                            else if(currentlyRequestURL)
	                                            {
	                                                resourceMappingCommand.getRequestURLMessage().add(line.trim());
	                                            }
					                        }
					                        else if(line.contains("@foreach"))
					                        {
					                            foreachBracket++;
					                            if(currentlyRequest)
					                            {
					                                resourceMappingCommand.getRequestMessage().add(line.trim());
					                            }
					                            else if(currentlyResponse)
					                            {
					                                resourceMappingCommand.getResponseMessage().add(line);
					                            }
	                                            else if(currentlyRequestURL)
	                                            {
	                                                resourceMappingCommand.getRequestURLMessage().add(line.trim());
	                                            }
					                        }
					                        else if(line.trim().equals("@endforeach"))
					                        {
					                            foreachBracket--;
					                            if(currentlyRequest)
					                            {
					                                resourceMappingCommand.getRequestMessage().add(line.trim());
					                            }
					                            else if(currentlyResponse)
					                            {
					                                resourceMappingCommand.getResponseMessage().add(line);
					                            }
	                                            else if(currentlyRequestURL)
	                                            {
	                                                resourceMappingCommand.getRequestURLMessage().add(line.trim());
	                                            }
					                        }
					                        else if(line.trim().equals("@elseif") && ifBracket == 0)
					                        {
					                            AFLog.e("[Error] unexpected @elseif in " + resourceMappingCommand.getSuppcode() + ".txt");
					                            return false;
					                        }
	                                        else if(line.trim().equals("@else") && ifBracket == 0)
	                                        {
	                                            AFLog.e("[Error] unexpected @else in " + resourceMappingCommand.getSuppcode() + ".txt");
	                                            return false;
	                                        }
					                        else if(currentlyRequest)
					                        {
					                            if(resourceMappingCommand.getMessageFormat().equals("xml"))
					                            {
					                                String[] splittedTags = line.split("><");
					
					                                for (int i = 0; i < splittedTags.length; i++)
					                                {
					                                    if (i == 0)
					                                    {
					                                        splittedTags[i] = (splittedTags[i] + ">");
					
					                                    }
					                                    else if (i == splittedTags.length - 1)
					                                    {
					                                        splittedTags[i] = ("<" + splittedTags[i]);
					
					                                    }
					                                    else
					                                    {
					                                        splittedTags[i] = ("<" + splittedTags[i] + ">");
					
					                                    }
					                                }
					
					                                if (splittedTags.length > 1)
					                                {
					                                    for (String tag : splittedTags)
					                                    {
					                                        resourceMappingCommand.getRequestMessage().add(tag.trim());
					                                    }
					                                }
					                                else
					                                {
					                                    resourceMappingCommand.getRequestMessage().add(line.trim());
					                                }
					                            }
					                            else
					                            {
					                                resourceMappingCommand.getRequestMessage().add(line.trim());
					                            }
					                        }
					                        else if(currentlyResponse)
					                        {
					                        	if(resourceMappingCommand.getMessageFormat().equals("xml")||resourceMappingCommand.getMessageFormat().equals("httpget"))
					                            {
					                                String[] splittedTags = line.split("><");
					
					                                for (int i = 0; i < splittedTags.length; i++)
					                                {
					                                    if (i == 0)
					                                    {
					                                        splittedTags[i] = (splittedTags[i] + ">");
					
					                                    }
					                                    else if (i == splittedTags.length - 1)
					                                    {
					                                        splittedTags[i] = ("<" + splittedTags[i]);
					
					                                    }
					                                    else
					                                    {
					                                        splittedTags[i] = ("<" + splittedTags[i] + ">");
					
					                                    }
					                                }
					
					                                if (splittedTags.length > 1)
					                                {
					                                    for (String tag : splittedTags)
					                                    {
					                                        resourceMappingCommand.getResponseMessage().add(tag);
					                                    }
					                                }
					                                else
					                                {
					                                    resourceMappingCommand.getResponseMessage().add(line);
					                                }
					                            }
					                        }
					                        else if(currentlyRequestURL)
	                                        {
	                                            resourceMappingCommand.getRequestURLMessage().add(line.trim());
	                                        }
					                        lineNo++;
					                    }
					                    if(ifBracket != 0)
					                    {
					                        AFLog.e("[Error] unbalanced @if, @endif in " + resourceMappingCommand.getSuppcode() + ".txt");
					                        return false;
					                    }
					
					                    if(foreachBracket != 0)
					                    {
					                        AFLog.e("[Error] unbalanced @foreach, @endforeach in " + resourceMappingCommand.getSuppcode() + ".txt");
					                        return false;
					                    }
				
					                    if(requiredRequestField != 2)
				                        {
				                            AFLog.e("[Error] unbalanced @request, @endrequest in " + resourceMappingCommand.getSuppcode() + ".txt");
				                            return false;
				                        }
				
				                        if(requiredResponseField != 2)
				                        {
				                            AFLog.e("[Error] unbalanced @response, @endresponse in " + resourceMappingCommand.getSuppcode() + ".txt");
				                            return false;
				                        }
	
				                        if(requiredRequestURLField > 0 && requiredRequestURLField != 2)
	                                    {
	                                        AFLog.e("[Error] unbalanced @requesturl, @endrequesturl in " + resourceMappingCommand.getSuppcode() + ".txt");
	                                        return false;
	                                    }
//					                    AFLog.d(messageTemplatePath + "/" + resourceMappingCommand.getSuppcode() + ".txt read completed.");
					                }
					                catch (FileNotFoundException ex)
					                {
					                    isVerifyPass = false;
					                    AFLog.e(messageTemplatePath + "/" + resourceMappingCommand.getSuppcode() + ".txt template file not found.");
					                }
	            				}else {
	            					isVerifyPass = false;
	            	                AFLog.e(resourceMappingCommand.getSuppcode()+" timeOut must not missing or empty in "+keyName+".json");
	            				}
	            			}else {
	            				isVerifyPass = false;
	        	                AFLog.e(resourceMappingCommand.getSuppcode()+" messageFormat must not missing or empty in "+keyName+".json");
	            			}
	            		}else {
	            			isVerifyPass = false;
	    	                AFLog.e(resourceMappingCommand.getSuppcode()+" SuccessPattern must not missing or empty in "+keyName+".json");
	            		}
	        		} else {
		            	isVerifyPass = false;
		                AFLog.e(resourceMappingCommand.getSuppcode()+" neType must not missing or empty in "+keyName+".json");
		            }
	            } else {
	            	isVerifyPass = false;
	                AFLog.e("suppcode must not missing or empty in "+keyName+".json");
	            }
            }
        } catch (FileNotFoundException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file not found.");
        } catch (NullPointerException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " not found.");
            AFLog.e(ex);
        } catch (JsonParseException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file is invalid format. (" + ex.getMessage() + ")");
        } catch (Exception ex) {
            isVerifyPass = false;
            AFLog.e("[Exception] can't load "+keyName+".");
            AFLog.e(ex);
        }	     

//        AFLog.d("========================= show value =========================");
//        AFLog.d("resourceMappingCommand : "+resourceMappingCommandHashMap.size()+" records");
//        for (Map.Entry resourceMappingCommand : resourceMappingCommandHashMap.entrySet()) {
//        	AFLog.d(resourceMappingCommand.toString());
//        }                  
//        AFLog.d("==============================================================");
        return isVerifyPass;
    }

    public static boolean initializeResourceErrorHandlingHashMap(AbstractAF abstractAF) {
//        AFLog.d("Loading Resource ErrorHandling...");
        Gson gson = GsonPool.getGson();
        JsonObject jsonObject;
        String keyName = "ResourceErrorHandling";
        boolean isVerifyPass = true;
        List<String> resourceAddedList = new ArrayList<>();
        
        try {
            String filePath = getWarmConfig(abstractAF, keyName);
            jsonObject = (JsonObject) new JsonParser().parse(new FileReader(filePath));
            Type listType = new TypeToken<ArrayList<ResourceErrorHandling>>(){}.getType();
            List<ResourceErrorHandling> resourceErrorHandlingList = gson.fromJson(jsonObject.get("resultData").toString(), listType);
        	GsonPool.pushGson(gson);

        	int modelIndex = 0;
            for(ResourceErrorHandling resourceErrorHandlings : resourceErrorHandlingList) {
                if(resourceErrorHandlings.getSearchKey() == null || resourceErrorHandlings.getSearchKey().equals(""))
                {
                    AFLog.e("[ResourceErrorHandling] searchKey is missing in ResourceErrorHandling.json. (Index: " + modelIndex + ")");
                    isVerifyPass = false;
                    break;
                }
                else
                {
                    String searchKey = resourceErrorHandlings.getSearchKey();
                    if(resourceErrorHandlings.getSuppCode() == null || resourceErrorHandlings.getSuppCode().equals(""))
                    {
                        AFLog.e("[ResourceErrorHandling] suppCode is missing at " + searchKey + " in ResourceErrorHandling.json. (Index: " + modelIndex + ")");
                        isVerifyPass = false;
                        break;
                    }
                    else if(resourceErrorHandlings.getErrCode() == null || resourceErrorHandlings.getErrCode().equals(""))
                    {
                        AFLog.e("[ResourceErrorHandling] errCode is missing at " + searchKey + " in ResourceErrorHandling.json. (Index: " + modelIndex + ")");
                        isVerifyPass = false;
                        break;
                    }
                    else if(resourceErrorHandlings.getErrDescription() == null || resourceErrorHandlings.getErrDescription().equals(""))
                    {
                        AFLog.e("[ResourceErrorHandling] errDescription is missing at " + searchKey + " in ResourceErrorHandling.json. (Index: " + modelIndex + ")");
                        isVerifyPass = false;
                        break;
                    }
                    else if(resourceErrorHandlings.getErrAction() == null || resourceErrorHandlings.getErrAction().equals(""))
                    {
                        AFLog.e("[ResourceErrorHandling] errAction is missing at " + searchKey + " in ResourceErrorHandling.json. (Index: " + modelIndex + ")");
                        isVerifyPass = false;
                        break;
                    }
                    else
                    {
                        String errAction = resourceErrorHandlings.getErrAction();
                        Validator validator = new Validator();
                        final List<String> errActionValidation = Arrays.asList("Retry","FC","ErrHandlingSuppCode","Special");
                        if(!errActionValidation.contains(errAction))
                        {
                            AFLog.e("[ResourceErrorHandling] errAction is invalid at " + searchKey + " in ResourceErrorHandling.json. (Index: " + modelIndex + ")");
                            isVerifyPass = false;
                            break;
                        }

                        if(errAction.equals("Retry"))
                        {
                            if(resourceErrorHandlings.getRetryTime() == null || resourceErrorHandlings.getRetryTime().equals(""))
                            {
                                AFLog.e("[ResourceErrorHandling] retryTime is missing at " + searchKey + " in ResourceErrorHandling.json. (Index: " + modelIndex + ")");
                                isVerifyPass = false;
                                break;
                            }
                            else if(!validator.isNumeric(resourceErrorHandlings.getRetryTime()) || resourceErrorHandlings.getRetryTime().contains("-"))
                            {
                                AFLog.e("[ResourceErrorHandling] retryTime should be positive number at " + searchKey + " in ResourceErrorHandling.json. (Index: " + modelIndex + ")");
                                isVerifyPass = false;
                                break;
                            }

                            if(resourceErrorHandlings.getRetrySleep() != null && !resourceErrorHandlings.getRetrySleep().equals("") && !validator.isNumeric(resourceErrorHandlings.getRetrySleep()) || resourceErrorHandlings.getRetrySleep().contains("-"))
                            {
                                AFLog.e("[ResourceErrorHandling] retrySleep should be positive number at " + searchKey + " in ResourceErrorHandling.json. (Index: " + modelIndex + ")");
                                isVerifyPass = false;
                                break;
                            }

                        }

                        if(errAction.equals("ErrHandlingSuppCode"))
                        {
                            if(resourceErrorHandlings.getErrHandlingSuppCode() == null || resourceErrorHandlings.getErrHandlingSuppCode().size() == 0 || resourceErrorHandlings.getErrHandlingSuppCode().contains(""))
                            {
                                AFLog.e("[ResourceErrorHandling] errHandlingSuppCode is missing at " + searchKey + " in ResourceErrorHandling.json. (Index: " + modelIndex + ")");
                                isVerifyPass = false;
                                break;
                            }

                            if(resourceErrorHandlings.getRetrySleep() != null && !resourceErrorHandlings.getRetrySleep().equals("") && !validator.isNumeric(resourceErrorHandlings.getRetrySleep()) || resourceErrorHandlings.getRetrySleep().contains("-"))
                            {
                                AFLog.e("[ResourceErrorHandling] retrySleep should be positive number at " + searchKey + " in ResourceErrorHandling.json. (Index: " + modelIndex + ")");
                                isVerifyPass = false;
                                break;
                            }
                        }
                    }
                }


                if (!resourceErrorhandlingHashMap.containsKey(resourceErrorHandlings.getSuppCode())) {
                    resourceErrorhandlingHashMap.put(resourceErrorHandlings.getSuppCode(), new ArrayList<>());
                }

                if(resourceAddedList.contains(resourceErrorHandlings.getSearchKey()))
                {
                    AFLog.e("[ResourceErrorHandling] Duplicated searchKey: " + resourceErrorHandlings.getSearchKey());
                    isVerifyPass = false;
                    break;
                }
                else
                {
                    resourceErrorhandlingHashMap.get(resourceErrorHandlings.getSuppCode()).add(resourceErrorHandlings);
                    resourceAddedList.add(resourceErrorHandlings.getSearchKey());
                    modelIndex++;
                }
            }

            
       
        } catch (FileNotFoundException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file not found.");
        } catch (NullPointerException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " not found.");
        } catch (JsonParseException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file is invalid format. (" + ex.getMessage() + ")");
        } catch (Exception ex) {
            isVerifyPass = false;
            AFLog.e("[Exception] can't load "+keyName+".");
            AFLog.e(ex);
        }
        
//        AFLog.d("========================= show value =========================");
//        AFLog.d("resourceProperty : "+resourceErrorhandlingHashMap.size()+" records");
//        for (Map.Entry resourceErrorHandlings : resourceErrorhandlingHashMap.entrySet()) {
//        	AFLog.d(resourceErrorHandlings.toString());
//        }                  
//        AFLog.d("==============================================================");
 
        return isVerifyPass;
    }
    
    public static boolean initializeResourcePropertyHashMap(AbstractAF abstractAF) {
//        AFLog.d("Loading Resource Property...");
        Gson gson = GsonPool.getGson();
        JsonObject jsonObject;
        String keyName = "ResourceProperty";
        boolean isVerifyPass = true;
        
        try {
            String filePath = getWarmConfig(abstractAF, keyName);
            jsonObject = (JsonObject) new JsonParser().parse(new FileReader(filePath));
            Type listType = new TypeToken<ArrayList<ResourceProperty>>(){}.getType();
            List<ResourceProperty> resourcePropertiesList = gson.fromJson(jsonObject.get("resultData").toString(), listType);
        	GsonPool.pushGson(gson);

            for(ResourceProperty resourceProperties : resourcePropertiesList){
            	if(resourceProperties.getResourceName()!=null
            			&&!resourceProperties.getResourceName().trim().isEmpty()
            			&&resourceProperties.getPreNeRouting()!=null
            			&&!resourceProperties.getPreNeRouting().trim().isEmpty()
            			&&resourceProperties.getSearchKey()!=null
            			&&!resourceProperties.getSearchKey().isEmpty()
            			&&(resourceProperties.getSearchKey().size()>=1)) {
            		if(resourceProperties.getSearchKey().size()==1&&!resourceProperties.getSearchKey().get(0).trim().isEmpty()) {
            			resourcePropertyHashMap.put(resourceProperties.getResourceName(), resourceProperties.trim());
            		}else if (resourceProperties.getSearchKey().size()>1) {
            			resourcePropertyHashMap.put(resourceProperties.getResourceName(), resourceProperties.trim());
            		}else {
            			isVerifyPass = false;
                        AFLog.e("Invalid configuration in "+keyName+".json");
            		}
            	}else {
            		 isVerifyPass = false;
                     AFLog.e("Invalid configuration in "+keyName+".json");
            	}
            }
            
        } catch (FileNotFoundException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file not found.");
        } catch (NullPointerException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " not found.");
        } catch (JsonParseException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file is invalid format. (" + ex.getMessage() + ")");
        } catch (Exception ex) {
            isVerifyPass = false;
            AFLog.e("[Exception] can't load "+keyName+".");
            AFLog.e(ex);
        }
        
//        AFLog.d("========================= show value =========================");
//        AFLog.d("resourceProperty : "+resourcePropertyHashMap.size()+" records");
//        for (Map.Entry resourceProperties : resourcePropertyHashMap.entrySet()) {
//        	AFLog.d(resourceProperties.toString());
//        }                  
//        AFLog.d("==============================================================");
 
        return isVerifyPass;
    }


    public static boolean initializeResourceSearchKeyHashmap(AbstractAF abstractAF)
    {
//        AFLog.d("Loading ResourceSearchKey...");
    	String keyName = "ResourceSearchKey";
        boolean isVerifyPass = true;
        try
        {
        	String filePath = getWarmConfig(abstractAF, keyName);            
            JsonObject o = new JsonParser().parse(new FileReader(filePath)).getAsJsonObject();
            Gson gson = GsonPool.getGson();

            List<ResourceSearchKeyModel> resourceSearchKeyList;
            Type resourceSearchKeyListType = new TypeToken<List<ResourceSearchKeyModel>>() {}.getType();
            resourceSearchKeyList = gson.fromJson(o.get("resultData").toString(), resourceSearchKeyListType);

//            AFLog.d("========================= show value =========================");
//            AFLog.d("resourceSearchKey : "+resourceSearchKeyList.size()+" records");


            GsonPool.pushGson(gson);
            // loop every key
            int modelIndex = 0;
            for (ResourceSearchKeyModel model : resourceSearchKeyList)
            {
                if(model.getSearchKey() == null)
                {
                    AFLog.e("[ResourceSearchKey] searchKey is invalid for some entries in ResourceSearchKey.json. (Index: " + modelIndex + ")");
                    isVerifyPass = false;
                    break;
                }

                if(model.getResourceIndex() == null)
                {
                    AFLog.e("[ResourceSearchKey] resourceIndex is invalid for some entries in ResourceSearchKey.json. (Index: " + modelIndex + ")");
                    isVerifyPass = false;
                    break;
                }
                model = model.trim();
                String resourceKey = model.getSearchKey();
                String resourceIndex = model.getResourceIndex();

                //AFLog.d(model.getSearchKey() + "=" + model.getResourceIndex());

                ResourceSearchKeyNode deepNode = null;

                String[] splitedKey = resourceKey.split("\\$");
                if(splitedKey.length > 1)
                // keySearch must have more than 1 key.
                {
                    String concatKeyName = "";
                    for (int keyIndex = 0; keyIndex < splitedKey.length; keyIndex++)
                    {
                        if(splitedKey[keyIndex].equals("Def"))
                        {
                            AFLog.e("[ResourceSearchKey] Def found in " + model.getSearchKey());
                            isVerifyPass = false;
                            break;
                        }

                        if (keyIndex == 0)
                        // if this is root node (first section of keySearch)
                        {
                            if (resourceIndexMap.containsKey(splitedKey[0]))
                            {
                                // continue;
                                // root node already existed
                            }
                            else
                            {
                                concatKeyName += splitedKey[0];
                                resourceIndexMap.put(splitedKey[0], new ResourceSearchKeyNode(concatKeyName, false, new HashMap<>()));
                                // create root node

                            }
                        }
                        else
                        {
                            if(keyIndex == 1)
                            {
                                deepNode = resourceIndexMap.get(splitedKey[0]);
                            }
                            // else if(keyIndex > 1)
                            else
                            {
                            	//check null because SonarQ concern "A "NullPointerException" could be thrown; "deepNode" is nullable here"
                            	if(deepNode!=null && deepNode.getNodesValue()!=null)
                            		deepNode = deepNode.getNodesValue().get(splitedKey[keyIndex - 1]);
                            }

                            if (keyIndex == splitedKey.length - 1)
                            // if this is leaf node (last section of keySearch)
                            {
                                HashMap<String, ResourceSearchKeyNode> deepNodeChildren = new HashMap<>();
                                if(deepNode!=null && deepNode.getNodesValue()!=null){
                                	deepNodeChildren = deepNode.getNodesValue();
                                }
                                
                                if(deepNodeChildren.containsKey(splitedKey[keyIndex]))
                                {
                                    AFLog.e("[ResourceSearchKey] ERROR: Duplicated config at " + resourceKey);
                                    isVerifyPass = false;
                                    break;
                                }
                                else
                                {
                                    concatKeyName += "$" + splitedKey[keyIndex];
                                    deepNodeChildren.put(splitedKey[keyIndex], new ResourceSearchKeyNode(concatKeyName, true, resourceIndex));
                                }
                            }
                            else
                            // if this is between node
                            {
                                //deepNode = resourceIndexMap.get(splitedKey[keyIndex]);
                                // get deeper node
                                HashMap<String, ResourceSearchKeyNode> deepNodeChildren = new HashMap<>();
                                if(deepNode!=null && deepNode.getNodesValue()!=null){
                                	deepNodeChildren = deepNode.getNodesValue();
                                }
                                if(deepNodeChildren!=null)
                                {
	                                if(deepNodeChildren.containsKey(splitedKey[keyIndex]))
	                                {
	                                    // continue;
	                                }
	                                else
	                                {
	                                    concatKeyName += "$" + splitedKey[keyIndex];
	                                    deepNodeChildren.put(splitedKey[keyIndex], new ResourceSearchKeyNode(concatKeyName, false, new HashMap<>()));
	                                }
                                }
                            }
                        }
                    }
                }
                else
                {
                    AFLog.e("[ResourceSearchKey] ERROR: Malformed config at " + resourceKey);
                    isVerifyPass = false;
                    break;
                }
                modelIndex++;
            }

        } catch (FileNotFoundException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file not found.");
        } catch (NullPointerException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " not found.");
        } catch (JsonParseException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file is invalid format. (" + ex.getMessage() + ")");
        } catch (Exception ex) {
            isVerifyPass = false;
            AFLog.e("[Exception] can't load "+keyName+".");
            AFLog.e(ex);
        }
//        AFLog.d("==============================================================");
        return isVerifyPass;
    }

    public static boolean initializeResourceNeIdRouting(AbstractAF abstractAF)
    {
//        AFLog.d("Loading ResourceNeIdRouting...");
        String keyName = "ResourceNeIdRouting";

        try
        {
            String filePath = getWarmConfig(abstractAF, keyName);
            JsonObject o = new JsonParser().parse(new FileReader(filePath)).getAsJsonObject();
            Gson gson = GsonPool.getGson();

            List<ResourceNeIdRouting> resourceNeIdRoutings;
            Type resourceNeIdRoutingListType = new TypeToken<List<ResourceNeIdRouting>>() {}.getType();
            resourceNeIdRoutings = gson.fromJson(o.get("resultData").toString(), resourceNeIdRoutingListType);
//
//            AFLog.d("========================= show value =========================");
//            AFLog.d("resourceNeIdRoutings : "+resourceNeIdRoutings.size()+" records");

            for (ResourceNeIdRouting routing: resourceNeIdRoutings)
            {
//                AFLog.d(routing.toString());
                if(!resourceNeIdRoutingMap.containsKey(routing.getNeType()))
                {
                    resourceNeIdRoutingMap.put(routing.getNeType(), new ArrayList<>());
                }
                if(Long.parseLong(routing.getStart()) <= Long.parseLong(routing.getEnd()))
                {
                    resourceNeIdRoutingMap.get(routing.getNeType()).add(routing);
                }
                else
                {
                    AFLog.e(keyName + " has an entry that start value is more than end value at [" + routing.toString() + "].");
                    return false;
                }
            }
//            AFLog.d("==============================================================");
            return true;
        }
        catch (Exception ex)
        {
//            AFLog.d("==============================================================");
        	AFLog.e("[Exception] can't load "+keyName+".");
            AFLog.e(ex);
            return false;
        }
    }
    
    public static boolean initializeOverwriteDefHashMap(AbstractAF abstractAF) {
//        AFLog.d("Loading Overwrite Def...");
        Gson gson = GsonPool.getGson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject;
        String keyName = "OverwriteParam";
        boolean isVerifyPass = true;

        try
        {
//            String filePath = getWarmConfig(abstractAF, keyName);
//            jsonObject = (JsonObject) parser.parse(new FileReader(filePath));
//            System.out.println(jsonObject.get("resultData").toString());
//            Type listType = new TypeToken<HashMap<String, HashMap<String, String>>>()
//            {
//            }.getType();
//            overwriteDefHashMap = gson.fromJson(jsonObject.get("resultData").getAsJsonArray().get(0).toString(), listType);
//            GsonPool.pushGson(gson);
            
        	String filePath = getWarmConfig(abstractAF, keyName);
        	jsonObject = (JsonObject) parser.parse(new FileReader(filePath));
			List<OverwriteParam> overwriteParamList;
			Type overwriteParamListType = new TypeToken<List<OverwriteParam>>() {}.getType();
			overwriteParamList = gson.fromJson(jsonObject.get("resultData").toString(), overwriteParamListType);
			GsonPool.pushGson(gson);

            for(OverwriteParam overwriteParam : overwriteParamList){
            	Type overwritePatternType = new TypeToken<HashMap<String, String>>()
                {
                }.getType();
                HashMap<String, String> overwritePattern = gson.fromJson(gson.toJson(overwriteParam.getOverwritePattern()).toString(), overwritePatternType);
                GsonPool.pushGson(gson);

                overwriteDefHashMap.put(overwriteParam.getOverwriteParam(), overwritePattern);
            }
        } catch (FileNotFoundException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file not found.");
        } catch (NullPointerException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " not found.");
        }
         catch (Exception ex) {
            isVerifyPass = false;
            AFLog.e("[Exception] can't load "+keyName+".");
            AFLog.e(ex);
        }	
        
//        try { 
//        AFLog.d("========================= show value =========================");
//        AFLog.d("overwriteDef : "+overwriteDefHashMap.size()+" records");
//        for (Map.Entry resourceMaster : overwriteDefHashMap.entrySet()) {
//        	AFLog.d(resourceMaster.toString());
//        }                  
//        AFLog.d("==============================================================");
//        } catch (Exception ex) {
//            AFLog.e(ex);
//        }
        
        
        return isVerifyPass;
    }
    
    public static boolean initializeDropResourceOrderTypeHashMap(AbstractAF abstractAF) {
//    	AFLog.d("Loading Drop Resource OrderType...");
        Gson gson = GsonPool.getGson();
        JsonObject jsonObject;
        String keyName = "DropResourceOrderType";
        boolean isVerifyPass = true;
        // load config of resource rule to JVM
        try {
            String filePath = getWarmConfig(abstractAF, keyName);            
            jsonObject = (JsonObject) new JsonParser().parse(new FileReader(filePath));
            JsonArray arr = (JsonArray) jsonObject.get("resultData");
        	for(int j = 0; j< arr.size() ; j++){
        		DropResourceOrderType resourceHLRTemplate = gson.fromJson(arr.get(j).toString(), DropResourceOrderType.class);
        		
				if(resourceHLRTemplate.getResourceOrderType() == null || resourceHLRTemplate.getResourceOrderType().equals("")) {
					isVerifyPass = false;
					AFLog.e("resourceOrderType is missing or invalid parameter in DropResourceOrderType.json.");
				} 
				
				if(StringUtils.isNotBlank(resourceHLRTemplate.getResourceParent())) {
					if(resourceHLRTemplate.getResourceParent().contains(",")){
						String[] resourceParentList = resourceHLRTemplate.getResourceParent().split(",");
						for( String resourceParent : resourceParentList ){
							if(StringUtils.isBlank(resourceParent)){
								isVerifyPass = false;
								AFLog.e("resourceParent ( "+ resourceHLRTemplate.getResourceParent() +" ) is invalid parameter in DropResourceOrderType.json.");
								break;
							}
						}
					}
				} 
				
				if (resourceHLRTemplate.getDropResource() == null || resourceHLRTemplate.getDropResource().size() == 0 || resourceHLRTemplate.getDropResource().get(0).toString().equals("{}")) {
					isVerifyPass = false;
					AFLog.e("dropResource is missing or invalid parameter in DropResourceOrderType.json.");
				} 
				
				if (isVerifyPass) {
					dropResourceOrderTypeHashMap.put(String.valueOf(resourceHLRTemplate.getResourceOrderType()), resourceHLRTemplate);
				}
        					
        	}                   
        } catch (FileNotFoundException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file not found.");
        } catch (NullPointerException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " not found.");
        } catch (JsonParseException ex) {
            isVerifyPass = false;
            AFLog.e(keyName + " config file is invalid format. (" + ex.getMessage() + ")");
        } catch (Exception ex) {
            isVerifyPass = false;
            AFLog.e(ex);
        }
        
//        AFLog.d("========================= show value =========================");
//        AFLog.d("resourceHLRTemplate : "+dropResourceOrderTypeHashMap.size()+" records");
//        for (Map.Entry dropResourceOrderType : dropResourceOrderTypeHashMap.entrySet()) {
//        	AFLog.d(dropResourceOrderType.getKey().toString()+"="+gson.toJson(dropResourceOrderType.getValue()).toString());
//        }                  
//        AFLog.d("==============================================================");

        
        
        return isVerifyPass;
    }


    public static List<SuppCode> getSuppCodeList(String resourceIndex){
    	List<SuppCode> suppCodeList = null;
		try {
			if(resourceMasterHashMap.containsKey(resourceIndex)){
				suppCodeList = resourceMasterHashMap.get(resourceIndex).getSuppCodesList();
				List<String> suppCodeName = new ArrayList<>();
				for(SuppCode suppCode : suppCodeList){
					suppCodeName.add(suppCode.getSuppCode());
				}
//				System.out.println("Suppcodes List : " + suppCodeName);
				AFLog.d("Suppcodes List : " + suppCodeName);
			} else {
//				System.out.println("Resource Index '" + resourceIndex + "' not found!!");
				AFLog.e("Resource Index '" + resourceIndex + "' not found!!");
			}
		} catch (Exception e) {
	        AFLog.e("[Exception] getSuppCodeList error");
	        AFLog.e(e);
	    }
		return suppCodeList;
   }

    public static boolean balancedParenthensies(String s) {
        Stack<Character> stack  = new Stack<Character>();
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(c == '[' || c == '(' || c == '{' ) {
                stack.push(c);
            } else if(c == ']') {
                if(stack.isEmpty() || stack.pop() != '[') {
                    return false;
                }
            } else if(c == ')') {
                if(stack.isEmpty() || stack.pop() != '(') {
                    return false;
                }
            } else if(c == '}') {
                if(stack.isEmpty() || stack.pop() != '{') {
                    return false;
                }
            }

        }
        return stack.isEmpty();
    }

    public static void initialRandomInvoke(AbstractAF abstractAF) {
        try {
            String isRandomInvokeValue = getWarmConfig(abstractAF, "RandomInvoke");
            if (isRandomInvokeValue.equals("TRUE")) {
                isRandomInvoke = true;
            } else {
                isRandomInvoke = false;
            }
        } catch (Exception e) {
            AFLog.d("no config random invoke");
        }
    }
    
    public static boolean initialTimeoutManagerFlag(AbstractAF abstractAF) {
    	  boolean isVerifyPass = true;
        try {
            String TimeoutManagerFlag = getWarmConfig(abstractAF, "EnableTimeoutManagerFlag");
            if (TimeoutManagerFlag.equalsIgnoreCase("true")) {
                enableTimeoutManagerFlag = true;
            } else if (TimeoutManagerFlag.equalsIgnoreCase("false")) {
            	  enableTimeoutManagerFlag = false;
			}else{
				throw new Exception();
			}
        } catch (Exception e) {
        	isVerifyPass = false;
        }
		return isVerifyPass;
    }
    

    public static boolean initialRuleParam(AbstractAF abstractAF) {
    	boolean isVerifyPass = true;
    	
    	return isVerifyPass;
    }

    public static String changeFormatResourceInvnetoryFormFileMode(String mode) {
    	return mode.toLowerCase();
    }
    public static Boolean ResourceInventoryMode(String mode) {
    	boolean isMode = false;
    	if(mode.equals("sdf")) {
    		isMode = false;
    	}else if(mode.equals("file")) {
    		isMode = true;
    	}
    	return isMode;
    }

    public static boolean initalResourceInventoryFormFileName(AbstractAF abstractAF) {
    	ResourceInventoryFromFile resourceInventFile = new ResourceInventoryFromFile();
    	boolean isVerifyPass = false;

    		 String PATH_FILEBACKUPRESOURCEINVENTORY = getWarmConfig(abstractAF, EConfig.PATH_FILEBACKUPRESOURCEINVENTORY.getName());
    		 ArrayList<String> list = resourceInventFile.listFilesByFolder();
    	     for(int i =0;i<list.size();i++) {
    	    	 if(PATH_FILEBACKUPRESOURCEINVENTORY.equals(list.get(i))) {
    	    		 isVerifyPass = true;
    	    	 }
    	     }


    	return isVerifyPass;
    }
    public static boolean initalResourceInventoryFormFileMode(AbstractAF abstractAF) {
    	boolean isVerifyPass = false;
    	String mode = Config.getWarmConfig(abstractAF, EConfig.RESOURCEINVENTORYFORMFILEMODE.getName());
    	mode = mode.toLowerCase();
    	if(mode.equals("sdf")) {
    		isVerifyPass = true;
    	}else if(mode.equals("file")) {
    		isVerifyPass = true;
    	}else {
    		isVerifyPass = false;
    	}

    	return isVerifyPass;
    }
    // method get set


  public static boolean isHasReloadConfig() {
    return hasReloadConfig;
  }


public static String getPATH_FILEBACKUPRESOURCEINVENTORY() {
	return PATH_FILEBACKUPRESOURCEINVENTORY;
}

public static void setPATH_FILEBACKUPRESOURCEINVENTORY(String pATH_FILEBACKUPRESOURCEINVENTORY) {
	PATH_FILEBACKUPRESOURCEINVENTORY = pATH_FILEBACKUPRESOURCEINVENTORY;
}

public static Boolean getEnableResourceInventoryFromFileMode() {
	return ResourceInventoryFromFileMode;
}

public static void setEnableResourceInventoryFromFileMode(Boolean enableResourceInventoryFromFileMode) {
	Config.ResourceInventoryFromFileMode = enableResourceInventoryFromFileMode;
}

public static String getPATH_BACKUPRESOURCEINVENTORY() {
	return PATH_BACKUPRESOURCEINVENTORY;
}

public static void setPATH_BACKUPRESOURCEINVENTORY(String pATH_BACKUPRESOURCEINVENTORY) {
	PATH_BACKUPRESOURCEINVENTORY = pATH_BACKUPRESOURCEINVENTORY;
}

public static void setHasReloadConfig(boolean hasReloadConfig) {
    Config.hasReloadConfig = hasReloadConfig;
  }

  public static String getLibVersion() {
    return LIB_VERSION;
  }

  public static void setLibVersion(String libVersion) {
    LIB_VERSION = libVersion;
  }

  public static String getPgzServerName() {
    return PGZ_SERVER_NAME;
  }

  public static void setPgzServerName(String pgzServerName) {
    PGZ_SERVER_NAME = pgzServerName;
  }

  public static String getUserSysList() {
    return UserSysList;
  }

  public static void setUserSysList(String userSysList) {
    UserSysList = userSysList;
  }

  public static String getProtocol() {
    return Protocol;
  }

  public static void setProtocol(String protocol) {
    Protocol = protocol;
  }

  public static List<String> getUrlList() {
    return UrlList;
  }

  public static void setUrlList(List<String> urlList) {
    UrlList = urlList;
  }

  public static int getDefaultServerTimeout() {
    return DEFAULT_SERVER_TIMEOUT;
  }

  public static void setDefaultServerTimeout(int defaultServerTimeout) {
    DEFAULT_SERVER_TIMEOUT = defaultServerTimeout;
  }

  public static DateTimeFormatter getRequestIdDate() {
    return requestIdDate;
  }

  public static DateTimeFormatter getHrzDateFormat() {
    return hrzDateFormat;
  }

  public static DateTimeFormatter getFormatDateWithMiTz() {
    return formatDateWithMiTz;
  }

  public static DateTimeFormatter getJourneyDateFormat() {
    return journeyDateFormat;
  }

  public static DateTimeFormatter getJourneyDateTask() {
    return journeyDateTask;
  }

  public static HashMap<String, ResourceMaster> getResourceMasterHashMap() {
    return resourceMasterHashMap;
  }

  public static void setResourceMasterHashMap(
      HashMap<String, ResourceMaster> resourceMasterHashMap) {
    Config.resourceMasterHashMap = resourceMasterHashMap;
  }

  public static HashMap<String, ResourceInventory> getResourceInventoryHashMap() {
    return resourceInventoryHashMap;
  }

  public static void setResourceInventoryHashMap(HashMap<String, ResourceInventory> resourceInventoryHashMap) {
    Config.resourceInventoryHashMap = resourceInventoryHashMap;
  }

  public static HashMap<String, ResourceRuleMaster> getResourceRuleHashMap() {
    return resourceRuleHashMap;
  }

  public static void setResourceRuleHashMap(
      HashMap<String, ResourceRuleMaster> resourceRuleHashMap) {
    Config.resourceRuleHashMap = resourceRuleHashMap;
  }

  public static HashMap<String, ResourceNeTypeProperty> getResourceNeTypePropertyHashMap() {
    return resourceNeTypePropertyHashMap;
  }

  public static void setResourceNeTypePropertyHashMap(
      HashMap<String, ResourceNeTypeProperty> resourceNeTypePropertyHashMap) {
    Config.resourceNeTypePropertyHashMap = resourceNeTypePropertyHashMap;
  }

  public static HashMap<String, ResourceMappingCommand> getResourceMappingCommandHashMap() {
    return resourceMappingCommandHashMap;
  }

  public static void setResourceMappingCommandHashMap(
      HashMap<String, ResourceMappingCommand> resourceMappingCommandHashMap) {
    Config.resourceMappingCommandHashMap = resourceMappingCommandHashMap;
  }

  public static HashMap<String, ResourceProperty> getResourcePropertyHashMap() {
    return resourcePropertyHashMap;
  }

  public static void setResourcePropertyHashMap(
      HashMap<String, ResourceProperty> resourcePropertyHashMap) {
    Config.resourcePropertyHashMap = resourcePropertyHashMap;
  }

  public static HashMap<String, DropResourceOrderType> getResourceHLRTemplateHashMap() {
	return dropResourceOrderTypeHashMap;
}

public static void setResourceHLRTemplateHashMap(HashMap<String, DropResourceOrderType> resourceHLRTemplateHashMap) {
	Config.dropResourceOrderTypeHashMap = resourceHLRTemplateHashMap;
}

public static HashMap<String, HashMap<String, String>> getOverwriteDefHashMap() {
    return overwriteDefHashMap;
  }

  public static void setOverwriteDefHashMap(
      HashMap<String, HashMap<String, String>> overwriteDefHashMap) {
    Config.overwriteDefHashMap = overwriteDefHashMap;
  }

  public static HashMap<String, HashMap<String, Object>> getOverwriteParamHashMap() {
    return overwriteParamHashMap;
  }

  public static void setOverwriteParamHashMap(
      HashMap<String, HashMap<String, Object>> overwriteParamHashMap) {
    Config.overwriteParamHashMap = overwriteParamHashMap;
  }

  public static HashMap<String, String> getServerInterfaceHashMap() {
    return serverInterfaceHashMap;
  }

  public static void setServerInterfaceHashMap(
      HashMap<String, String> serverInterfaceHashMap) {
    Config.serverInterfaceHashMap = serverInterfaceHashMap;
  }

  public static HashMap<String, String> getUrlServersToClientHashMap() {
    return urlServersToClientHashMap;
  }

  public static void setUrlServersToClientHashMap(
      HashMap<String, String> urlServersToClientHashMap) {
    Config.urlServersToClientHashMap = urlServersToClientHashMap;
  }

  public static HashMap<String, Integer> getServersTimeoutHashMap() {
    return serversTimeoutHashMap;
  }

  public static void setServersTimeoutHashMap(
      HashMap<String, Integer> serversTimeoutHashMap) {
    Config.serversTimeoutHashMap = serversTimeoutHashMap;
  }

  public static String getSdfInfranodeInterface() {
    return SDF_INFRANODE_INTERFACE;
  }

  public static void setSdfInfranodeInterface(String sdfInfranodeInterface) {
    SDF_INFRANODE_INTERFACE = sdfInfranodeInterface;
  }

  public static String getSdfInventoryInterface() {
    return SDF_INVENTORY_INTERFACE;
  }

  public static void setSdfInventoryInterface(String sdfInventoryInterface) {
    SDF_INVENTORY_INTERFACE = sdfInventoryInterface;
  }

  public static String getSdfJourneyInterface() {
    return SDF_JOURNEY_INTERFACE;
  }

  public static void setSdfJourneyInterface(String sdfJourneyInterface) {
    SDF_JOURNEY_INTERFACE = sdfJourneyInterface;
  }

  public static String getMongoInterface() {
    return MONGO_INTERFACE;
  }

  public static void setMongoInterface(String mongoInterface) {
    MONGO_INTERFACE = mongoInterface;
  }

  public static String getSdfReservequotaInterface() {
    return SDF_RESERVEQUOTA_INTERFACE;
  }

  public static void setSdfReservequotaInterface(String sdfReservequotaInterface) {
    SDF_RESERVEQUOTA_INTERFACE = sdfReservequotaInterface;
  }

  public static String getSdfReleasequotaInterface() {
    return SDF_RELEASEQUOTA_INTERFACE;
  }

  public static void setSdfReleasequotaInterface(String sdfReleasequotaInterface) {
    SDF_RELEASEQUOTA_INTERFACE = sdfReleasequotaInterface;
  }

  public static String getSdfCommitquotaInterface() {
    return SDF_COMMITQUOTA_INTERFACE;
  }

  public static void setSdfCommitquotaInterface(String sdfCommitquotaInterface) {
    SDF_COMMITQUOTA_INTERFACE = sdfCommitquotaInterface;
  }

  public static String getMdInterface() {
    return MD_INTERFACE;
  }

  public static void setMdInterface(String mdInterface) {
    MD_INTERFACE = mdInterface;
  }

  public static String getUrlSdfInventory() {
    return URL_SDF_INVENTORY;
  }

  public static void setUrlSdfInventory(String urlSdfInventory) {
    URL_SDF_INVENTORY = urlSdfInventory;
  }

  public static String getUrlSdfInfranode() {
    return URL_SDF_INFRANODE;
  }

  public static void setUrlSdfInfranode(String urlSdfInfranode) {
    URL_SDF_INFRANODE = urlSdfInfranode;
  }

  public static String getUrlSdfJourney() {
    return URL_SDF_JOURNEY;
  }

  public static void setUrlSdfJourney(String urlSdfJourney) {
    URL_SDF_JOURNEY = urlSdfJourney;
  }

  public static String getUrlSdfReservequota() {
    return URL_SDF_RESERVEQUOTA;
  }

  public static void setUrlSdfReservequota(String urlSdfReservequota) {
    URL_SDF_RESERVEQUOTA = urlSdfReservequota;
  }

  public static String getUrlSdfReleasequota() {
    return URL_SDF_RELEASEQUOTA;
  }

  public static void setUrlSdfReleasequota(String urlSdfReleasequota) {
    URL_SDF_RELEASEQUOTA = urlSdfReleasequota;
  }

  public static String getUrlSdfCommitquota() {
    return URL_SDF_COMMITQUOTA;
  }

  public static void setUrlSdfCommitquota(String urlSdfCommitquota) {
    URL_SDF_COMMITQUOTA = urlSdfCommitquota;
  }

  public static String getURL_Mongo() {
    return URL_Mongo;
  }

  public static void setURL_Mongo(String URL_Mongo) {
    Config.URL_Mongo = URL_Mongo;
  }

  public static String getActor() {
    return Actor;
  }

  public static void setActor(String actor) {
    Actor = actor;
  }

  public static int getSdfInventoryTimeout() {
    return SDF_INVENTORY_TIMEOUT;
  }

  public static void setSdfInventoryTimeout(int sdfInventoryTimeout) {
    SDF_INVENTORY_TIMEOUT = sdfInventoryTimeout;
  }

  public static int getSdfInfranodeTimeout() {
    return SDF_INFRANODE_TIMEOUT;
  }

  public static void setSdfInfranodeTimeout(int sdfInfranodeTimeout) {
    SDF_INFRANODE_TIMEOUT = sdfInfranodeTimeout;
  }

  public static int getSdfJourneyTimeout() {
    return SDF_JOURNEY_TIMEOUT;
  }

  public static void setSdfJourneyTimeout(int sdfJourneyTimeout) {
    SDF_JOURNEY_TIMEOUT = sdfJourneyTimeout;
  }

  public static int getSdfReservequotaTimeout() {
    return SDF_RESERVEQUOTA_TIMEOUT;
  }

  public static void setSdfReservequotaTimeout(int sdfReservequotaTimeout) {
    SDF_RESERVEQUOTA_TIMEOUT = sdfReservequotaTimeout;
  }

  public static int getSdfReleasequotaTimeout() {
    return SDF_RELEASEQUOTA_TIMEOUT;
  }

  public static void setSdfReleasequotaTimeout(int sdfReleasequotaTimeout) {
    SDF_RELEASEQUOTA_TIMEOUT = sdfReleasequotaTimeout;
  }

  public static int getSdfCommitquotaTimeout() {
    return SDF_COMMITQUOTA_TIMEOUT;
  }

  public static void setSdfCommitquotaTimeout(int sdfCommitquotaTimeout) {
    SDF_COMMITQUOTA_TIMEOUT = sdfCommitquotaTimeout;
  }

  public static int getMongoTimeout() {
    return MONGO_TIMEOUT;
  }

  public static void setMongoTimeout(int mongoTimeout) {
    MONGO_TIMEOUT = mongoTimeout;
  }

  public static int getMdTimeout() {
    return MD_TIMEOUT;
  }

  public static void setMdTimeout(int mdTimeout) {
    MD_TIMEOUT = mdTimeout;
  }

  public static int getExpirationDate() {
    return EXPIRATION_DATE;
  }

  public static void setExpirationDate(int expirationDate) {
    EXPIRATION_DATE = expirationDate;
  }

  public static int getSdfMaxretry() {
    return SDF_MAXRETRY;
  }

  public static void setSdfMaxretry(int sdfMaxretry) {
    SDF_MAXRETRY = sdfMaxretry;
  }

  public static int getEXPIRATIONDATE() {
    return EXPIRATIONDATE;
  }

  public static void setEXPIRATIONDATE(int EXPIRATIONDATE) {
    Config.EXPIRATIONDATE = EXPIRATIONDATE;
  }

  public static String getAPPNAME() {
    return APPNAME;
  }

  public static void setAPPNAME(String APPNAME) {
    Config.APPNAME = APPNAME;
  }

  public static String getEdrLog() {
    return EDR_LOG;
  }

  public static void setEdrLog(String edrLog) {
    EDR_LOG = edrLog;
  }

  public static String getCdrLog() {
    return CDR_LOG;
  }

  public static void setCdrLog(String cdrLog) {
    CDR_LOG = cdrLog;
  }

  public static String getTransactionLog() {
    return TRANSACTION_LOG;
  }

  public static void setTransactionLog(String transactionLog) {
    TRANSACTION_LOG = transactionLog;
  }

  public static String getIncomingUnknownLog() {
	return INCOMING_UNKNOWN_LOG;
  }

  public static void setIncomingUnknownLog(String incomingUnknownLog) {
	INCOMING_UNKNOWN_LOG = incomingUnknownLog;
  }

  public static HashMap<String, ResourceSearchKeyNode> getResourceIndexMap() {
    return resourceIndexMap;
  }

  public static void setResourceIndexMap(
      HashMap<String, ResourceSearchKeyNode> resourceIndexMap) {
    Config.resourceIndexMap = resourceIndexMap;
  }

  public static HashMap<String, List<ResourceNeIdRouting>> getResourceNeIdRoutingMap() {
    return resourceNeIdRoutingMap;
  }

  public static void setResourceNeIdRoutingMap(
      HashMap<String, List<ResourceNeIdRouting>> resourceNeIdRoutingMap) {
    Config.resourceNeIdRoutingMap = resourceNeIdRoutingMap;
  }

  public static boolean isRandomInvoke() {
    return isRandomInvoke;
  }

  public static void setIsRandomInvoke(boolean isRandomInvoke) {
    Config.isRandomInvoke = isRandomInvoke;
  }

  public static HashMap<String, List<ResourceErrorHandling>> getResourceErrorhandlingHashMap() {
	return resourceErrorhandlingHashMap;
  }

  public static void setResourceErrorhandlingHashMap(
		HashMap<String, List<ResourceErrorHandling>> resourceErrorhandlingHashMap) {
	Config.resourceErrorhandlingHashMap = resourceErrorhandlingHashMap;
  }

  public static double getGAP_EXECTIME() {
	return GAP_EXECTIME;
  }
	
  public static void setGAP_EXECTIME(double gAP_EXECTIME) {
	GAP_EXECTIME = gAP_EXECTIME;
  }

public static String getPATH_ES22_RECOVERYLOG_DUPLICATE() {
	return PATH_ES22_RECOVERYLOG_DUPLICATE;
}

public static void setPATH_ES22_RECOVERYLOG_DUPLICATE(String pATH_ES22_RECOVERYLOG_DUPLICATE) {
	PATH_ES22_RECOVERYLOG_DUPLICATE = pATH_ES22_RECOVERYLOG_DUPLICATE;
}

public static String getPATH_ES22_RECOVERYLOG_TIMEOUT() {
	return PATH_ES22_RECOVERYLOG_TIMEOUT;
}

public static void setPATH_ES22_RECOVERYLOG_TIMEOUT(String pATH_ES22_RECOVERYLOG_TIMEOUT) {
	PATH_ES22_RECOVERYLOG_TIMEOUT = pATH_ES22_RECOVERYLOG_TIMEOUT;
}

public static String getPATH_ES22_RECOVERYLOG_ERROR() {
	return PATH_ES22_RECOVERYLOG_ERROR;
}

public static void setPATH_ES22_RECOVERYLOG_ERROR(String pATH_ES22_RECOVERYLOG_ERROR) {
	PATH_ES22_RECOVERYLOG_ERROR = pATH_ES22_RECOVERYLOG_ERROR;
}

public static String getRECOVERY_FILE() {
	return RECOVERY_FILE;
}

public static void setRECOVERY_FILE(String rECOVERY_FILE) {
	RECOVERY_FILE = rECOVERY_FILE;
}

public static int getREQUEST_MAXACTIVE() {
	return REQUEST_MAXACTIVE;
}



	public static synchronized int getCountMaxActive() {
	return CountMaxActive;
}

	public static synchronized void incrementMaxActive() {
		CountMaxActive++;
	}

	public static synchronized void decrementMaxActive() {
		if (CountMaxActive > 0) {
			CountMaxActive--;
		} else {
			CountMaxActive = 0;
		}
	}

	public static boolean isEnableTimeoutManagerFlag() {
		return enableTimeoutManagerFlag;
	}
}
