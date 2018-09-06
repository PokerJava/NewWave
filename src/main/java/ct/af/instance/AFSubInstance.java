package ct.af.instance;

import ct.af.enums.EStats;
import ct.af.enums.ESubState;
import ct.af.message.incoming.parameter.AbsClientParam;
import ct.af.message.incoming.parameter.Param_CLIENT_PostResult;
import ct.af.message.incoming.parameter.Param_IDLE_Xxx;
import ct.af.resourceModel.ResourceInfraNode;
import ct.af.resourceModel.ResourceNeIdRouting;
import ct.af.resourceModel.SuppCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;

public class AFSubInstance{

	
	private Param_IDLE_Xxx subParam;
	private HashMap<String, Object> subHashMapParam = new HashMap<>();
	//Default Parameter
	private String subInstanceNo;
	private String subInitTimeStampIn;
	private String subackReqDate;
	private String subackRespDate;
	private String subAckReqCDR;
	private String subCompleteDateCDR;
	private String subResultCodeEDR;
	private String subResultDescEDR;

	private DateTime timeStampEnd;

	private String subcompleteDate;
	private String subInitOrig = "NONE";
	private String subInitInvoke = "NONE";
	private String subInitURL = "NONE";
	private String subInitEvent = "NONE";
	private String subInitCmd = "NONE";
	private String subInitMethod = "UNKNOWN";
	private String urlRespResult = "UNKNOWN";
	private String subInvokeResp = "NONE";

	private int subCountRetryNe = 0;
	private int subCountRetrySdf = 0;

	private String subCurrentState = ESubState.Unknown.toString();
	private String subControlState;
	private String subNextState = ESubState.Unknown.toString();
	private String subNextOfNextState = ESubState.Unknown.toString();
	private String subState;
	private ArrayList<String> subInvoke = new ArrayList<>();
	private ArrayList<String> subTimeoutArray = new ArrayList<>();
	private ArrayList<String> subStateArray = new ArrayList<>();
	private HashMap<String,SubEDR> subEDRMap = new HashMap<>();
	private HashMap<String,SubCDR> subCDRMap = new HashMap<>();
	private Map<String, SubEDR> mapSubEDR = new HashMap<String, SubEDR>();

	private int subCountChild = 0;

	private boolean subIsValid;

	private String subTimeout;

	private String subResultCode;
	private String subInternalCode;

	private EStats statsIn = null;
	private EStats statsOut = null;
	private EStats statsExeTime =null;
	//End Default Parameter

	private String reqRawData = "";
	private String requestId = "";
	private AbsClientParam subClientParameter;
	private AbsClientParam subResponseParam;
	private Param_CLIENT_PostResult param_HRZ_PostResult;

	private String subResourceOrderValidateMessage = "";
	private HashMap<String, Object> subClientHashMapParameter = new HashMap<>();
	private List<SuppCode> suppCodeList = new ArrayList<>();
	private String currentSuppcode;
	private String requestMessage;
	private String requestURLMessage = "";
	private String subResourceOrderIns;
	private String subNeId;
	private String subEDRResultCode;
	private String expirationDateResourceOrder;
	private String state; // requestStatus
	private String respStatus;
	private String user;
	private String receivereceivedDateMills;
	private HashMap<String, ResourceInfraNode> resourceInfraNodeHashMap = new HashMap<>();
	private HashMap<String, String> mapNeidWithInvoke = new HashMap<>(); // <invoke, neId>
	private List<String> subResourceItemInsNoArray = new ArrayList<>();
	private boolean subResourceOrderHasError;
	private String  errorMessage;
	private boolean isNeTask = false;
	private HashMap<String, Integer> neIdQuotaAmountRequire;
	private HashMap<String, PostSubInstance> postSubInstanceMap = new HashMap<>();
	private String subackRespRawData;

	private String resourceItemStatus;
	private String command;
	private String serviceOrderId;
	private String serviceOrderType;

	private String subResourceName;
	private String ret;
	private String ecode;
	private String invoke;
	private double timeIn;
	private double timeOut;
	private String resultDesc;

	private String taskId;
	private List<ResourceNeIdRouting> subResourceNeidRoutingArray = new ArrayList<>();
	private HashMap<String, HashMap<String, List<String>>> subNeIdForErrorHandlingSuppcode= new HashMap<>(); // <suppcode, <keysearchError,[neid1,neid2]>>
	
	private String old_errorFlag;

	private HashMap<String, Object> mappingResponse = new HashMap<>();

	private HashMap<String, PoolTask> mappingPoolTask = new HashMap<>();

	private String retranSmit;
	private String errorFlagOferrorHandling = "";
	private String new_ErrorFlag = "";
	private ArrayList<String> subCodeArr = new ArrayList<>();
	private ArrayList<String> taskCondition = new ArrayList<>();
	private ArrayList<String> taskRespArr = new ArrayList<>();
	private HashMap<String, String> refInvoke = new HashMap<>();
	private HashMap<String, String> neSubmissionDate = new HashMap<>();
	private HashMap<String, String> neCompletionDate = new HashMap<>();
	private ArrayList<String> subSearchKey = new ArrayList<>();
	private String subInitSearchKey;

	private String responseMessage;
	private String resAckRawData;
	private String resHrzRawData;
	private String rscOrderDevMsg;

	private boolean processOutputMessageComplete = true;

	private double resourceOrderTimeOut ;
	private Boolean flagTimeOut = false;

	private int subResourceItemNo;
	private String msJourney;

	private boolean errorHandling = false;
	private int maxRetryNe = 0;
	private List<String> suppCodeListErrorhandling = new ArrayList<>();
	private List<String> subNeIdListErrorhandling = new ArrayList<>();
	
	private boolean errorHandlingSuppcodeFlg = false;

	private String timeSleepRetry;
	private String resAckResultCode=null;
	private String recoveryConf;
	private int subTaskErrorHandling =0;
	private boolean isSubResourceOrderExpired = false;
	private HashMap<String,String> taskTempErrorFlag = new HashMap<>();
	
	private boolean dropResourceFlag = false;
	private boolean dropAllResourceFlag = false;
	private String dropResourceParent;
	
	private String flagResourceItemStatus = "N";
	private String errorFlagForErrorhandlingSub = "";
	private String currentSuppcodeErrorhandling;
	private String subNeIdErrorhandling;
	private int subFirstIndexResourceItemToProcess = -1;
	private int subFirstTaskNoToProcess = -1;
	private boolean subResourceItemNeedToProcess = true;

	

	public HashMap<String, Object> getSubHashMapParam() {
		return subHashMapParam;
	}

	public void setSubHashMapParam(HashMap<String, Object> subHashMapParam) {
		this.subHashMapParam = subHashMapParam;
	}

	public Param_IDLE_Xxx getSubParam() {
		return subParam;
	}

	public void setSubParam(Param_IDLE_Xxx subParam) {
		this.subParam = subParam;
	}

	public String getSubResultCodeEDR() {
		return subResultCodeEDR;
	}

	public void setSubResultCodeEDR(String subResultCodeEDR) {
		this.subResultCodeEDR = subResultCodeEDR;
	}

	public String getSubResultDescEDR() {
		return subResultDescEDR;
	}

	public void setSubResultDescEDR(String subResultDescEDR) {
		this.subResultDescEDR = subResultDescEDR;
	}

	public String getCurrentSuppcodeErrorhandling() {
		return currentSuppcodeErrorhandling;
	}

	public void setCurrentSuppcodeErrorhandling(String currentSuppcodeErrorhandling) {
		this.currentSuppcodeErrorhandling = currentSuppcodeErrorhandling;
	}

	public String getSubNeIdErrorhandling() {
		return subNeIdErrorhandling;
	}

	public void setSubNeIdErrorhandling(String subNeIdErrorhandling) {
		this.subNeIdErrorhandling = subNeIdErrorhandling;
	}

	public String getErrorFlagForErrorhandlingSub() {
		return errorFlagForErrorhandlingSub;
	}

	public void setErrorFlagForErrorhandlingSub(String errorFlagForErrorhandlingSub) {
		this.errorFlagForErrorhandlingSub = errorFlagForErrorhandlingSub;
	}


	public String getFlagResourceItemStatus() {
		return flagResourceItemStatus;
	}

	public void setFlagResourceItemStatus(String flagResourceItemStatus) {
		this.flagResourceItemStatus = flagResourceItemStatus;
	}

	public List<String> getSubNeIdListErrorhandling() {
		return subNeIdListErrorhandling;
	}

	public void setSubNeIdListErrorhandling(List<String> subNeIdListErrorhandling) {
		this.subNeIdListErrorhandling = subNeIdListErrorhandling;

	}

	public HashMap<String, String> getTaskTempErrorFlag() {
		return taskTempErrorFlag;
	}

	public void setTaskTempErrorFlag(HashMap<String, String> taskTempErrorFlag) {
		this.taskTempErrorFlag = taskTempErrorFlag;
	}

	public int getSubTaskErrorHandling() {
		return subTaskErrorHandling;
	}
	
	public void incrementSubTaskErrorHandling() {
		subTaskErrorHandling += 1;
	}

	public void setSubTaskErrorHandling(int subTaskErrorHandling) {
		this.subTaskErrorHandling = subTaskErrorHandling;
	}

	public boolean isErrorHandlingSuppcodeFlg() {
		return errorHandlingSuppcodeFlg;
	}

	public void setErrorHandlingSuppcodeFlg(boolean errorHandlingSuppcodeFlg) {
		this.errorHandlingSuppcodeFlg = errorHandlingSuppcodeFlg;
	}

	public List<String> getSuppCodeListErrorhandling() {
		return suppCodeListErrorhandling;
	}

	public void setSuppCodeListErrorhandling(List<String> suppCodeListErrorhandling) {
		this.suppCodeListErrorhandling = suppCodeListErrorhandling;
	}

	public String getTimeSleepRetry() {
		return timeSleepRetry;
	}

	public void setTimeSleepRetry(String timeSleepRetry) {
		this.timeSleepRetry = timeSleepRetry;
	}

	public String getRecoveryConf() {
		return recoveryConf;
	}

	public void setRecoveryConf(String recoveryConf) {
		this.recoveryConf = recoveryConf;
	}
	public String getResAckResultCode() {
		return resAckResultCode;
	}

	public void setResAckResultCode(String resAckResultCode) {
		this.resAckResultCode = resAckResultCode;

	}

	public String getMsJourney() {
		return msJourney;
	}

	public void setMsJourney(String msJourney) {
		this.msJourney = msJourney;
	}

	public int getSubResourceItemNo() {
		return subResourceItemNo;
	}

	public void setSubResourceItemNo(int subResourceItemNo) {
		this.subResourceItemNo = subResourceItemNo;
	}

	public Boolean getFlagTimeOut() {
		return flagTimeOut;
	}

	public void setFlagTimeOut(Boolean flagTimeOut) {
		this.flagTimeOut = flagTimeOut;
	}

	public double getResourceOrderTimeOut() {
		return resourceOrderTimeOut;
	}

	public void setResourceOrderTimeOut(double resourceOrderTimeOut) {
		this.resourceOrderTimeOut = resourceOrderTimeOut;
	}

	public String getSubInvokeResp() {
		return subInvokeResp;
	}

	public void setSubInvokeResp(String subInvokeResp) {
		this.subInvokeResp = subInvokeResp;
	}

	public String getSubAckReqCDR() {
		return subAckReqCDR;
	}

	public void setSubAckReqCDR(String subAckReqCDR) {
		this.subAckReqCDR = subAckReqCDR;
	}

	public String getSubCompleteDateCDR() {
		return subCompleteDateCDR;
	}

	public void setSubCompleteDateCDR(String subAckRespCDR) {
		this.subCompleteDateCDR = subAckRespCDR;
	}

	public DateTime getTimeStampEnd() {
		return timeStampEnd;
	}

	public void setTimeStampEnd(DateTime timeStampEnd) {
		this.timeStampEnd = timeStampEnd;
	}

	public String getUrlRespResult() {
		return urlRespResult;
	}

	public void setUrlRespResult(String urlRespResult) {
		this.urlRespResult = urlRespResult;
	}
	//ResultCode from Line10
	private String subResultCodeJourney;


	public String getResHrzRawData() {
		return resHrzRawData;
	}

	public void setResHrzRawData(String resHrzRawData) {
		this.resHrzRawData = resHrzRawData;
	}

	public String getRscOrderDevMsg() {
		return rscOrderDevMsg;
	}

	public void setRscOrderDevMsg(String rscOrderDevMsg) {
		this.rscOrderDevMsg = rscOrderDevMsg;
	}

	public String getResAckRawData() {
		return resAckRawData;
	}

	public void setResAckRawData(String resAckRawData) {
		this.resAckRawData = resAckRawData;
	}

	public String getReqRawData() {
		return reqRawData;
	}

	public void setReqRawData(String reqRawData) {
		this.reqRawData = reqRawData;
	}

	public EStats getStatsExeTime() {
		return statsExeTime;
	}

	public void setStatsExeTime(EStats statsExeTime) {
		this.statsExeTime = statsExeTime;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getSubInitSearchKey() {
		return subInitSearchKey;
	}

	public void setSubInitSearchKey(String subInitSearchKey) {
		this.subInitSearchKey = subInitSearchKey;
	}

	public String getResultDesc() {
		return resultDesc;
	}

	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}

	public double getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(double timeIn) {
		this.timeIn = timeIn;
	}

	public double getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(double timeOut) {
		this.timeOut = timeOut;
	}

	public String getInvoke() {
		return invoke;
	}

	public void setInvoke(String invoke) {
		this.invoke = invoke;
	}

	public String getEcode() {
		return ecode;
	}

	public void setEcode(String ecode) {
		this.ecode = ecode;
	}

	public String getRet() {
		return ret;
	}

	public void setRet(String ret) {
		this.ret = ret;
	}

	public String getSubResourceName() {
		return subResourceName;
	}

	public void setSubResourceName(String subResourceName) {
		this.subResourceName = subResourceName;
	}

	public HashMap<String, String> getNeCompletionDate() {
		return neCompletionDate;
	}

	public void setNeCompletionDate(HashMap<String, String> neCompletionDate) {
		this.neCompletionDate = neCompletionDate;
	}

	public HashMap<String, String> getNeSubmissionDate() {
		return neSubmissionDate;
	}

	public void setNeSubmissionDate(HashMap<String, String> neSubmissionDate) {
		this.neSubmissionDate = neSubmissionDate;
	}

	public HashMap<String, String> getRefInvoke() {
		return refInvoke;
	}

	public void setRefInvoke(HashMap<String, String> refInvoke) {
		this.refInvoke = refInvoke;
	}

	public List<ResourceNeIdRouting> getSubResourceNeidRoutingArray() {
		return subResourceNeidRoutingArray;
	}

	public void setSubResourceNeidRoutingArray(List<ResourceNeIdRouting> subResourceNeidRoutingArray) {
		this.subResourceNeidRoutingArray = subResourceNeidRoutingArray;
	}

	public ArrayList<String> getSubCodeArr() {
		return subCodeArr;
	}

	public void setSubCodeArr(ArrayList<String> subCodeArr) {
		this.subCodeArr = subCodeArr;
	}

	public ArrayList<String> getTaskCondition() {
		return taskCondition;
	}

	public void setTaskCondition(ArrayList<String> taskCondition) {
		this.taskCondition = taskCondition;
	}

	public ArrayList<String> getTaskRespArr() {
		return taskRespArr;
	}

	public void setTaskRespArr(ArrayList<String> taskRespArr) {
		this.taskRespArr = taskRespArr;
	}

	public String getNew_ErrorFlag() {
		return new_ErrorFlag;
	}

	public String getErrorFlagOferrorHandling() {
		return errorFlagOferrorHandling;
	}

	public void setErrorFlagOferrorHandling(String errorFlagOferrorHandling) {
		this.errorFlagOferrorHandling = errorFlagOferrorHandling;
	}

	public void setNew_ErrorFlag(String new_ErrorFlag) {
		this.new_ErrorFlag = new_ErrorFlag;
	}

	public String getOld_errorFlag() {
		return old_errorFlag;
	}

	public void setOld_errorFlag(String old_errorFlag) {
		this.old_errorFlag = old_errorFlag;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}


	public String getServiceOrderType() {
		return serviceOrderType;
	}

	public void setServiceOrderType(String serviceOrderType) {
		this.serviceOrderType = serviceOrderType;
	}

	public String getServiceOrderId() {
		return serviceOrderId;
	}

	public void setServiceOrderId(String serviceOrderId) {
		this.serviceOrderId = serviceOrderId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	private HashMap<String, String> numbering;



	public HashMap<String, String> getNumbering() {
		return numbering;
	}

	public void setNumbering(HashMap<String, String> numbering) {
		this.numbering = numbering;
	}

	public String getResourceItemStatus() {
		return resourceItemStatus;
	}

	public void setResourceItemStatus(String resourceItemStatus) {
		this.resourceItemStatus = resourceItemStatus;
	}

	public String getSubackRespDate() {
		return subackRespDate;
	}

	public void setSubackRespDate(String subackRespDate) {
		this.subackRespDate = subackRespDate;
	}

	public String getSubackReqDate() {
		return subackReqDate;
	}

	public void setSubackReqDate(String subackReqDate) {
		this.subackReqDate = subackReqDate;
	}

	public String getSubcompleteDate() {
		return subcompleteDate;
	}

	public void setSubcompleteDate(String subcompleteDate) {
		this.subcompleteDate = subcompleteDate;
	}


	public Param_CLIENT_PostResult getParam_HRZ_PostResult() {
		return param_HRZ_PostResult;
	}

	public void setParam_HRZ_PostResult(Param_CLIENT_PostResult param_HRZ_PostResult) {
		this.param_HRZ_PostResult = param_HRZ_PostResult;
	}

	public boolean isNeTask() {
		return isNeTask;
	}

	public void setNeTask(boolean neTask) {
		isNeTask = neTask;
	}

	public String getRetranSmit() {
		return retranSmit;
	}

	public void setRetranSmit(String retranSmit) {
		this.retranSmit = retranSmit;
	}

	public HashMap<String, PostSubInstance> getPostSubInstanceMap() {
		return postSubInstanceMap;
	}

	public void setPostSubInstanceMap(HashMap<String, PostSubInstance> postSubInstanceMap) {
		this.postSubInstanceMap = postSubInstanceMap;
	}

	private List<String> neIdList = new ArrayList<>();

	public List<String> getNeIdList() {
		return neIdList;
	}

	public void setNeIdList(List<String> neIdList) {
		this.neIdList = neIdList;
	}

	public HashMap<String, Integer> getNeIdQuotaAmountRequire() {
		return neIdQuotaAmountRequire;
	}

	public void setNeIdQuotaAmountRequire(HashMap<String, Integer> neIdQuotaAmountRequire) {
		this.neIdQuotaAmountRequire = neIdQuotaAmountRequire;
	}


	public HashMap<String, ResourceInfraNode> getResourceInfraNodeHashMap() {
		return resourceInfraNodeHashMap;
	}


	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean getSubResourceOrderHasError() {
		return subResourceOrderHasError;
	}

	public void setSubResourceOrderHasError(boolean subResourceOrderHasError) {
		this.subResourceOrderHasError = subResourceOrderHasError;
	}

	public void addSubResourceItemNoArray(String subResourceItemInsNo) {
		this.subResourceItemInsNoArray.add(subResourceItemInsNo);
	}

	public List<String> getSubResourceItemInsNoArray() {
		return subResourceItemInsNoArray;
	}

	public void setResourceInfraNodeHashMap(HashMap<String, ResourceInfraNode> resourceInfraNodeHashMap) {
		this.resourceInfraNodeHashMap = resourceInfraNodeHashMap;
	}

	public String getSubNeId() {
		return subNeId;
	}

	public void setSubNeId(String subNeId) {
		this.subNeId = subNeId;
	}

	public String getSubResourceOrderIns() {
		return subResourceOrderIns;
	}

	public void setSubResourceOrderIns(String subResourceOrderIns) {
		this.subResourceOrderIns = subResourceOrderIns;
	}

	public String getCurrentSuppcode() {
		return currentSuppcode;
	}

	public void setCurrentSuppcode(String currentSuppcode) {
		this.currentSuppcode = currentSuppcode;
	}

	private int subTaskNo = 0;
	private String resultMessage;

	public List<SuppCode> getSuppCodeList() {
		return suppCodeList;
	}

	public void setSuppCodeList(List<SuppCode> suppCodeList) {
		this.suppCodeList = suppCodeList;
	}

	public int getSubTaskNo() {
		return subTaskNo;
	}

	public void incrementSubTaskNo() {
		subTaskNo += 1;
	}

	public HashMap<String, Object> getSubClientHashMapParameter() {
		return subClientHashMapParameter;
	}

	public void setSubClientHashMapParameter(HashMap<String, Object> subClientHashMapParameter) {
		this.subClientHashMapParameter = subClientHashMapParameter;
	}

	public String getSubResourceOrderValidateMessage() {
		return subResourceOrderValidateMessage;
	}

	public void setSubResourceOrderValidateMessage(String subResourceOrderValidateMessage) {
		this.subResourceOrderValidateMessage = subResourceOrderValidateMessage;
	}

	public String getSubInstanceNo() {
		return subInstanceNo;
	}

	public void setSubInstanceNo(String subInstanceNo) {
		this.subInstanceNo = subInstanceNo;
	}

	public String getSubInitTimeStampIn() {
		return subInitTimeStampIn;
	}

	public void setSubInitTimeStampIn(String subInitTimeStampIn) {
		this.subInitTimeStampIn = subInitTimeStampIn;
	}

	public String getSubInitOrig() {
		return subInitOrig;
	}

	public void setSubInitOrig(String subInitOrig) {
		this.subInitOrig = subInitOrig;
	}

	public String getSubInitInvoke() {
		return subInitInvoke;
	}

	public void setSubInitInvoke(String subInitInvoke) {
		this.subInitInvoke = subInitInvoke;
	}

	public String getSubInitURL() {
		return subInitURL;
	}

	public void setSubInitURL(String subInitURL) {
		this.subInitURL = subInitURL;
	}

	public String getSubInitEvent() {
		return subInitEvent;
	}

	public void setSubInitEvent(String subInitEvent) {
		this.subInitEvent = subInitEvent;
	}

	public String getSubInitCmd() {
		return subInitCmd;
	}

	public void setSubInitCmd(String subInitCmd) {
		this.subInitCmd = subInitCmd;
	}

	public String getSubInitMethod() {
		return subInitMethod;
	}

	public void setSubInitMethod(String subInitMethod) {
		this.subInitMethod = subInitMethod;
	}

	public String getSubCurrentState() {
		return subCurrentState;
	}

	public void setSubCurrentState(String subCurrentState) {
		this.subCurrentState = subCurrentState;
	}

	public String getSubControlState() {
		return subControlState;
	}

	public void setSubControlState(String subControlState) {
		this.subControlState = subControlState;
	}

	public String getSubNextState() {
		return subNextState;
	}

	public void setSubNextState(String subNextState) {
		this.subNextState = subNextState;
	}

	public String getSubNextOfNextState() {
		return subNextOfNextState;
	}

	public void setSubNextOfNextState(String subNextOfNextState) {
		this.subNextOfNextState = subNextOfNextState;
	}

	public String getSubState() {
		return subState;
	}

	public void setSubState(String subState) {
		this.subState = subState;
	}

	public ArrayList<String> getSubInvoke() {
		return subInvoke;
	}

	public void setSubInvoke(String invoke) {
		this.subInvoke.add(invoke);
	}

	public ArrayList<String> getSubTimeoutArray() {
		return subTimeoutArray;
	}

	public void setSubTimeoutArray(String subTimeoutArray) {
		this.subTimeoutArray.add(subTimeoutArray);
	}

	public ArrayList<String> getSubStateArray() {
		return subStateArray;
	}

	public void setSubStateArray(String subStateArray) {
		this.subStateArray.add(subStateArray);
	}

	public int getSubCountChild() {
		return subCountChild;
	}

	public void setSubCountChild(int subCountChild) {
		this.subCountChild = subCountChild;
	}

	public boolean isSubIsValid() {
		return subIsValid;
	}

	public void setSubIsValid(boolean subIsValid) {
		this.subIsValid = subIsValid;
	}

	public String getSubTimeout() {
		return subTimeout;
	}

	public void setSubTimeout(String subTimeout) {
		this.subTimeout = subTimeout;
	}

	public String getSubResultCode() {
		return subResultCode;
	}

	public void setSubResultCode(String subResultCode) {
		this.subResultCode = subResultCode;
	}

	public String getSubInternalCode() {
		return subInternalCode;
	}

	public void setSubInternalCode(String subInternalCode) {
		this.subInternalCode = subInternalCode;
	}

	public EStats getStatsIn() {
		return statsIn;
	}

	public void setStatsIn(EStats statsIn) {
		this.statsIn = statsIn;
	}

	public EStats getStatsOut() {
		return statsOut;
	}

	public void setStatsOut(EStats statsOut) {
		this.statsOut = statsOut;
	}

	public AbsClientParam getSubClientParameter() {
		return subClientParameter;
	}

	public void setSubClientParameter(AbsClientParam subClientParameter) {
		this.subClientParameter = subClientParameter;
	}

	public AbsClientParam getSubResponseParam() {
		return subResponseParam;
	}

	public void setSubResponseParam(AbsClientParam subResponseParam) {
		this.subResponseParam = subResponseParam;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestMessage() {
		return requestMessage;
	}

	public void setRequestMessage(String requestMessage) {
		this.requestMessage = requestMessage;
	}

	public HashMap<String, SubEDR> getSubEDRMap() {
		return subEDRMap;
	}

	public void setSubEDRMap(HashMap<String, SubEDR> subEDRMap) {
		this.subEDRMap = subEDRMap;
	}

	public HashMap<String, SubCDR> getSubCDRMap() {
		return subCDRMap;
	}

	public void setSubCDRMap(HashMap<String, SubCDR> subCDRMap) {
		this.subCDRMap = subCDRMap;
	}

	public String getSubEDRResultCode() {
		return subEDRResultCode;
	}

	public void setSubEDRResultCode(String subEDRResultCode) {
		this.subEDRResultCode = subEDRResultCode;
	}

	public String getExpirationDateResourceOrder() {
		return expirationDateResourceOrder;
	}

	public void setExpirationDateResourceOrder(String expirationDateResourceOrder) {
		this.expirationDateResourceOrder = expirationDateResourceOrder;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRespStatus() {
		return respStatus;
	}

	public void setRespStatus(String respStatus) {
		this.respStatus = respStatus;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getReceivereceivedDateMills() {
		return receivereceivedDateMills;
	}

	public void setReceivereceivedDateMills(String receivereceivedDateMills) {
		this.receivereceivedDateMills = receivereceivedDateMills;
	}

	public Map<String, SubEDR> getMapSubEDR() {
		return mapSubEDR;
	}

	public void setMapSubEDR(Map<String, SubEDR> mapSubEDR) {
		this.mapSubEDR = mapSubEDR;
	}

	public void setResRawData(String message) {
		this.resultMessage = message;

	}

	public String getResRawData() {
		return resultMessage;

	}

	public int getSubCountRetryNe() {
		return subCountRetryNe;
	}

	public void setSubCountRetryNe(int subCountRetryNe) {
		this.subCountRetryNe = subCountRetryNe;
	}

	public int getSubCountRetrySdf() {
		return subCountRetrySdf;
	}

	public void setSubCountRetrySdf(int subCountRetrySdf) {
		this.subCountRetrySdf = subCountRetrySdf;
	}

	public void incrementSubCountRetryNe()
	{
		this.subCountRetryNe++;
	}


	public HashMap<String, Object> getMappingResponse() {
		return mappingResponse;
	}

	public void setMappingResponse(HashMap<String, Object> mappingResponse) {
		this.mappingResponse = mappingResponse;
	}

	public HashMap<String, PoolTask> getMappingPoolTask() {
		return mappingPoolTask;
	}

	public void setMappingPoolTask(HashMap<String, PoolTask> mappingPoolTask) {
		this.mappingPoolTask = mappingPoolTask;
	}

	public void decrementSubCountRetryNe()
	{
		if(this.subCountRetryNe > 0)
		{
			this.subCountRetryNe--;
		}
		else
		{
			this.subCountRetryNe = 0;
		}
	}

	public void incrementSubCountRetrySdf()
	{
		this.subCountRetrySdf++;
	}
	public void decrementSubCountRetrySdf()
	{
		if(this.subCountRetrySdf > 0)
		{
			this.subCountRetrySdf--;
		}
		else
		{
			this.subCountRetrySdf = 0;
		}
	}

	public ArrayList<String> getSubSearchKey()
	{
		return subSearchKey;
	}

	public void setSubSearchKey(ArrayList<String> subSearchKey)
	{
		this.subSearchKey = subSearchKey;
	}

	public String getSubackRespRawData() {
		return subackRespRawData;
	}

	public void setSubackRespRawData(String subAckRespRawData) {
		this.subackRespRawData = subAckRespRawData;
	}

	public String getSubResultCodeJourney() {
		return subResultCodeJourney;
	}

	public void setSubResultCodeJourney(String subResultCodeJourney) {
		this.subResultCodeJourney = subResultCodeJourney;
	}

	public boolean isProcessOutputMessageComplete()
	{
		return processOutputMessageComplete;
	}

	public void setProcessOutputMessageComplete(boolean processOutputMessageComplete)
	{
		this.processOutputMessageComplete = processOutputMessageComplete;
	}

	public HashMap<String, String> getMapNeidWithInvoke() {
		return mapNeidWithInvoke;
	}

	public void setMapNeidWithInvoke(HashMap<String, String> mapNeidWithInvoke) {
		this.mapNeidWithInvoke = mapNeidWithInvoke;
	}

	public boolean isErrorHandling() {
		return errorHandling;
	}

	public void setErrorHandling(boolean errorHandling) {
		this.errorHandling = errorHandling;
	}

	public int getMaxRetryNe() {
		return maxRetryNe;
	}

	public void setMaxRetryNe(int maxRetryNe) {
		this.maxRetryNe = maxRetryNe;
	}

	public HashMap<String, HashMap<String, List<String>>> getSubNeIdForErrorHandlingSuppcode() {
		return subNeIdForErrorHandlingSuppcode;
	}

	public void setSubNeIdForErrorHandlingSuppcode(HashMap<String, HashMap<String, List<String>>> subNeIdForErrorHandlingSuppcode) {
		this.subNeIdForErrorHandlingSuppcode = subNeIdForErrorHandlingSuppcode;
	}

	public boolean getDropResourceFlag() {
		return dropResourceFlag;
	}

	public void setDropResourceFlag(boolean dropResourceFlag) {
		this.dropResourceFlag = dropResourceFlag;
	}

	public boolean getDropAllResourceFlag() {
		return dropAllResourceFlag;
	}

	public void setDropAllResourceFlag(boolean dropAllResourceFlag) {
		this.dropAllResourceFlag = dropAllResourceFlag;
	}
	
	public String getDropResourceParent() {
		return dropResourceParent;
	}

	public void setDropResourceParent(String dropResourceParent) {
		this.dropResourceParent = dropResourceParent;
	}

	public boolean isSubResourceOrderExpired() {
		return isSubResourceOrderExpired;
	}

	public void setSubResourceOrderExpired(boolean subResourceOrderExpired) {
		isSubResourceOrderExpired = subResourceOrderExpired;
	}

	public int getSubFirstIndexResourceItemToProcess() {
		return subFirstIndexResourceItemToProcess;
	}

	public void setSubFirstIndexResourceItemToProcess(int subFirstIndexResourceItemToProcess) {
		this.subFirstIndexResourceItemToProcess = subFirstIndexResourceItemToProcess;
	}

    public String getRequestURLMessage()
    {
        return requestURLMessage;
    }

    public void setRequestURLMessage(String requestURLMessage)
    {
        this.requestURLMessage = requestURLMessage;
    }

	public boolean isSubResourceItemNeedToProcess() {
		return subResourceItemNeedToProcess;
	}

	public void setSubResourceItemNeedToProcess(boolean subResourceItemNeedToProcess) {
		this.subResourceItemNeedToProcess = subResourceItemNeedToProcess;
	}

	public int getSubFirstTaskNoToProcess() {
		return subFirstTaskNoToProcess;
	}

	public void setSubFirstTaskNoToProcess(int subFirstTaskNoToProcess) {
		this.subFirstTaskNoToProcess = subFirstTaskNoToProcess;
	}
}
