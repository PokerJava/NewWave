package ct.af.message.incoming.parser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

		String poke = "<ERDHeader>" + "<Header name=\"x-ssb-origin\" value=\"application\"/>"
				+ "<Header name=\"x-ssb-service-origin\" value=\"eService-Mobile\"/>"
				+ "<Header name=\"x-ssb-transaction-id\" value=\"2016081517300001234\"/>"
				+ "<Header name=\"x-ssb-order-channel\" value=\"MOBILE_APP\"/>"
				+ "<Header name=\"x-ssb-version\" value=\"application\"/>" + "<Header name=\"x-app\" value=\"\"/>"
				+ "<Header name=\"x-session\" value=\"\"" + "</ERDHeader>"
				+ "<ERDData value=\"&#10;{&#10;         &#10;   &quot;sessionId&quot;"
				+ ":&quot;564093493534958340&quot;,&#10; &#10;   &quot;accessNum&quot;:&quot"
				+ ";1775&quot;,&#10;        &#10;   &quot;appName&quot;:&quot;fb&quot;,&#10;         "
				+ "&#10;   &quot;callBackUrl&quot;:&quot;10.240.104.215:8443&quot;,&#10;            &#10; "
				+ "  &quot;submissionTime&quot;:&quot;20150731091000&quot;,&#10;            &#10;   &quot"
				+ ";callBackUrl&quot;:&quot;10.240.104.215:8443&quot;,&#10;            &#10;   &quot;submission"
				+ "Time&quot;:&quot;150903111111&quot;,&#10;           &#10;   &quot;partnerId&quot;:&quot;3001"
				+ "0&quot;&#10;}&#10;&#10;\"/>";
		
		
		String poke2 =   "<ERDHeader>"
				+ "<Header y=\"2\" name=\"x-ssb-origin\" value=\"application\" x=\"1\">"
				+ "<text>Example message</text>"
				+ "</Header>"
				+ "<Header name=\"x-ssb-service-origin\" value=\"eService-Mobile\" x=\"1\" y=\"2\">"
				+ "<text>Example message</text>"
				+ "</Header>"
				+ "<Header name=\"x-ssb-transaction-id\" value=\"2016081517300001234\" x=\"1\" y=\"2\">"
				+ "<text>Example message</text>"
				+ "</Header>"
				+ "</ERDHeader>";
		
		String poke3 = "<ERDHeader>"
				+ "<Header name=\"x-ssb-origin\" value=\"application\" x=\"1\" y=\"2\">"
				+ 		   "<text><test>555</test>"
				+ "					<test>111</test></text>"
//				+ ""
				+ "</Header>"
				+ "</ERDHeader>";
		Gson gson = GsonPool.getGson();
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = jsonParser.parse(XMLTools.getParseObject(poke3, Param_IDLE_Xml.class).toString()).getAsJsonObject();
		System.out.println(XMLTools.getParseObject(poke3, Param_IDLE_Xml.class).toString());
		Param_IDLE_Xml param = gson.fromJson(jsonObject, Param_IDLE_Xml.class);

		System.out.println(param);

		return null;

	}
}
