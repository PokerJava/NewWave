package ct.af.message.incoming.parser;

import com.google.gson.Gson;

import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_Xml;
import ct.af.message.incoming.parameter.Param_IDLE_Xxx;
import ct.af.utils.GsonPool;
import ct.af.utils.XMLTools;
import ec02.af.abstracts.AbstractAF;
import ec02.core.utils.XML;
import ec02.data.interfaces.EquinoxRawData;


public class Parser_IDLE_Xml {
	public Param_IDLE_Xml doParser(AbstractAF abstractAF, EquinoxRawData eqxRawData, AFInstance afInstance, AFSubInstance afSubIns) {
		
		StringBuilder msg = new StringBuilder();  
		
		msg.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soapenv:Body>"
				+ "<bcs:ChangeSubIdentityResultMsg xmlns:bcs=\"http://www.huawei.com/bme/cbsinterface/bcservices\" xmlns:cbs=\"http://www.huawei.com/bme/cbsinterface/cbscommon\">"
				+ "<ResultHeader>"
				+ "<cbs:Version>1</cbs:Version>"
				+ "<cbs:ResultCode>0</cbs:ResultCode>"
				+ "<cbs:ResultDesc>Operation successfully.</cbs:ResultDesc>"
				+ "</ResultHeader>"
				+ "</bcs:ChangeSubIdentityResultMsg>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>");
		
		System.out.println(msg);
		
		//Param_IDLE_Xml	param = (Param_IDLE_Xml) XMLTools.getParseObject(msg, Param_IDLE_Xml.class);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return null;
		
	}
}
