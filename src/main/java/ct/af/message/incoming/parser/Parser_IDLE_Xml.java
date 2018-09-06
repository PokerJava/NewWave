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
	public Param_IDLE_Xml doParser(AbstractAF abstractAF, EquinoxRawData eqxRawData, AFInstance afInstance,
			AFSubInstance afSubIns) {

		StringBuilder msg = new StringBuilder();

		msg.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" + "<soapenv:Body>"
				+ "<bcs:ChangeSubIdentityResultMsg xmlns:bcs=\"http://www.huawei.com/bme/cbsinterface/bcservices\" xmlns:cbs=\"http://www.huawei.com/bme/cbsinterface/cbscommon\">"
				+ "<ResultHeader>" + "<cbs:Version>1</cbs:Version>" + "<cbs:ResultCode>0</cbs:ResultCode>"
				+ "<cbs:ResultDesc>Operation successfully.</cbs:ResultDesc>" + "</ResultHeader>"
				+ "</bcs:ChangeSubIdentityResultMsg>" + "</soapenv:Body>" + "</soapenv:Envelope>");

		String poke = "<ERDHeader>" + "<Header name=\"x-ssb-origin\" value=\"application\"/>"
				+ "<Header name=\"x-ssb-service-origin\" value=\"eService-Mobile\"/>"
				+ "<Header name=\"x-ssb-transaction-id\" value=\"2016081517300001234\"/>"
				+ "<Header name=\"x-ssb-order-channel\" value=\"MOBILE_APP\"/>"
				+ "<Header name=\"x-ssb-version\" value=\"application\"/>" + "<Header name=\"x-app\" value=\"\"/>"
				+ "<Header name=\"x-session\" value=\"\"/" + "</ERDHeader>"
				+ "<ERDData value=\"&#10;{&#10;         &#10;   &quot;sessionId&quot;"
				+ ":&quot;564093493534958340&quot;,&#10; &#10;   &quot;accessNum&quot;:&quot"
				+ ";1775&quot;,&#10;        &#10;   &quot;appName&quot;:&quot;fb&quot;,&#10;         "
				+ "&#10;   &quot;callBackUrl&quot;:&quot;10.240.104.215:8443&quot;,&#10;            &#10; "
				+ "  &quot;submissionTime&quot;:&quot;20150731091000&quot;,&#10;            &#10;   &quot"
				+ ";callBackUrl&quot;:&quot;10.240.104.215:8443&quot;,&#10;            &#10;   &quot;submission"
				+ "Time&quot;:&quot;150903111111&quot;,&#10;           &#10;   &quot;partnerId&quot;:&quot;3001"
				+ "0&quot;&#10;}&#10;&#10;\"/>]]>";

		System.out.println(msg);

		Param_IDLE_Xml param = (Param_IDLE_Xml) XMLTools.getParseObject(poke, Param_IDLE_Xml.class);

		System.out.println(param);

		return null;

	}
}
