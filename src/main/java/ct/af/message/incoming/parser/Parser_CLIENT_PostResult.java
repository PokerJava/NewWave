package ct.af.message.incoming.parser;

import com.google.gson.Gson;

import ct.af.enums.ERequestState;
import ct.af.enums.EResultCode;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_CLIENT_PostResult;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.utils.GsonPool;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;
import org.apache.commons.lang3.StringUtils;

public class Parser_CLIENT_PostResult {

	public Param_CLIENT_PostResult doParser(EquinoxRawData eqxRawData, AFInstance afInstance, AFSubInstance afSubIns) {
		// TODO Auto-generated method stub
		Param_CLIENT_PostResult param = new Param_CLIENT_PostResult();
        String rawData = eqxRawData.getRawDataAttribute("val").trim();
        Param_IDLE_ResourceOrder param_idle_resourceorder = (Param_IDLE_ResourceOrder) afSubIns.getSubClientParameter();

        String idle_resourceGroupId = param_idle_resourceorder.getResourceGroupId();
        
        String resourceOrderId;
        String resourceGroupId;
        String resultCode;
        String resultDesc;
        String developerMessage;
        String state = afSubIns.getState();

        boolean isValid = true;
        Gson gson = GsonPool.getGson();
        
        try
        {
        	param = gson.fromJson(rawData, Param_CLIENT_PostResult.class);
        	resourceOrderId = param.getResourceOrderId().trim();
            resourceGroupId = param.getResourceGroupId().trim();
            resultCode = param.getResultCode().trim();
            resultDesc = param.getResultDesc().trim();
            developerMessage = param.getDeveloperMessage().trim();

            param.setIsReceived(true);
            state = ERequestState.COMPLETED.getState();

        }
        catch (Exception ex)
        {
        	AFLog.e("[Exception] can't parse in Parser_CLIENT_PostResult.");
        	AFLog.e(ex);
        	param.setIsValid(false);
            afSubIns.setState(ERequestState.NOTCOMPLETED.getState());
            return param;
        }

        GsonPool.pushGson(gson);
        
        if(StringUtils.isBlank(resourceOrderId) || StringUtils.isBlank(resourceGroupId) || StringUtils.isBlank(resultCode) || StringUtils.isBlank(resultDesc) ||
        		!resourceGroupId.equals(idle_resourceGroupId) || !resourceOrderId.equals(afSubIns.getRequestId()) || !resultCode.equals(EResultCode.RE20000.getResultCode())){
        	isValid = false;
            state = ERequestState.NOTCOMPLETED.getState();
        }

        param.setIsValid(isValid);
        afSubIns.setState(state);
        AFLog.d("[Class: In] resourceOrderId : "+ resourceOrderId);
        AFLog.d("[Class: In] resourceGroupId : "+ resourceGroupId);
        AFLog.d("[Class: In] resultCode : "+ resultCode);
        AFLog.d("[Class: In] resultDesc : "+ resultDesc);
        AFLog.d("[Class: In] developerMessage : "+ developerMessage);
        AFLog.d("[Class: In] isValid : "+ isValid);
        AFLog.d("[Class: In] state : "+ state);
        
		return param;
	}

}
