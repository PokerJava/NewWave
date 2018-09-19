package ct.af.message.outgoing;

import java.util.HashMap;
import java.util.Map;

import ct.af.enums.ECType;
import ct.af.enums.EEqxMsg;
import ct.af.enums.EEventType;
import ct.af.enums.EMethod;
import ct.af.enums.EProtocol;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.utils.AFUtils;
import ct.af.utils.Config;
import ec02.af.abstracts.AbstractAF;
import ec02.af.data.AFDataFactory;
import ec02.af.utils.AFLog;
import ec02.data.enums.EEquinoxRawData;
import ec02.data.interfaces.EquinoxRawData;

public class Out_SDF_GetLocation {
	public EquinoxRawData messageBuilder(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {
		EquinoxRawData eqxRawData = AFDataFactory.createEquinoxRawData();
		
		String message = "";
		Map<String, String> map = new HashMap<>();
        map.put(EEqxMsg.TYPE.getEqxMsg(), EEventType.REQUEST.getEventType());
        map.put(EEqxMsg.CTYPE.getEqxMsg(), ECType.TEXTPLAIN.getCType());
        map.put(EEqxMsg.PROTOCOL.getEqxMsg(), EProtocol.HTTP.toString());
        map.put(EEqxMsg.DESTINATION.getEqxMsg(), Config.getSdfInventoryInterface());
        
        String url = Config.getUrlSdfInventory();
        map.put(EEqxMsg.URL.getEqxMsg(), url);
        map.put(EEqxMsg.METHOD.getEqxMsg(), EMethod.GET.toString());
        map.put(EEqxMsg.VAL.getEqxMsg(), message.toString());

        String invoke = new AFUtils().invokeGenerator(abstractAF,afSubIns.getSubInitInvoke(),afSubIns.getSubInitCmd(),afSubIns.getSubNextState(),afSubIns.getSubInstanceNo());
        afSubIns.setSubInvoke(invoke);
        AFLog.d("[INVOKE] : "+EEqxMsg.INVOKE.getEqxMsg());
        AFLog.d("[INVOKE] : "+invoke);
        
        MessageBuilder builder = new MessageBuilder("");
        try{
     	   abstractAF.getEquinoxUtils().sendHTTPRequestMessage(builder, EEquinoxRawData.CTypeHTTP.TEXT_PLAIN, invoke, Config.getSdfInventoryInterface(), map);
        }
        catch (Exception e)
        {
     	   AFLog.e("[Exception] can't build HTTPRequestMessage.");
     	   AFLog.e(e);
 	   }
       
		return eqxRawData;
	}
}
