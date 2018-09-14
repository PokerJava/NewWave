package ct.af.message.incoming.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

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
				

		String msg =   "<ERDHeader>"
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
		
		String msg2 = "<ERDHeader>"
				+ "<Header name=\"x-ssb-service-origin\" value=\"eService-Mobile\" x=\"2\" y=\"2\">"
				+ 		"<text>"
				+ 		"<test>555</test>"
				+ 		"<test>111</test>"
				+ "</text>"
				+ "</Header>"
				+ "</ERDHeader>";

		String msg3 = 	"<ERDData>"
				+ 		"<A>resourceA</A>"
				+ 		"<B>"
				+ 		  "<element>resourceB1</element>"
				+ 		  "<element>resourceB2</element>"
				+      "</B>"
				+       "<C>"
				+ 		  "<resourceC1>c1</resourceC1>"
				+ 		  "<resourceC2>c2</resourceC2>"
				+      "</C>"
				+ 		"<D>"
				+ 		  "<element>"
				+ 			"<resourceD1>d1</resourceD1>"
				+ 			"<resourceD2>d2</resourceD2>"
				+ 		 "</element>"
				+ 		  "<element>"
				+		    "<resourceD1>d3</resourceD1>"
				+		    "<resourceD2>d4</resourceD2>"
				+ 		 "</element>"
				+	   "</D>"
				+ "</ERDData>";
		
		String rawXMLMessage = "<ERDData>"
				+ "				<A>resourceA</A>"
				+ "				<B>"
				+                "<element>b1</element>"
				+                "<element>b2</element>"
				+ "            </B>"
				+ "				<C>"
				+                "<resourceC1>c1</resourceC1>"
				+                "<resourceC2></resourceC2>"
				+              "</C>"
//				+ "				<D>"
//				+                "<element>"
//				+                  "<resourceD1>d1</resourceD1>"
//				+                  "<resourceD2>2222</resourceD2>"
//				+                "</element>"
////				+ "				<element>"
////				+                  "<resourceD1>d3</resourceD1>"
////				+                  "<resourceD2>1111</resourceD2>"
////				+              "</element>"
//				+			"</D>"
				+ "				</ERDData>";
		
		
		
		Gson gson = GsonPool.getGson();
		JsonParser jsonParser = new JsonParser();
		HashMap<String, Object> param =(HashMap<String, Object>) (XMLTools.getParseObject(msg3, Param_IDLE_Xxx.class));

		ArrayList<TreeMap<String, String>> d = (ArrayList<TreeMap<String, String>>)param.get("D");
		
		for(int i=0;i<d.size();i++){
			
			System.out.println(d.get(i).get("resourceD1"));
		}
//		System.out.println(d.get(0).get("resourceD1"));
//		System.out.println(d.get(1).get("resourceD1"));
//		System.out.println(param);
//		System.out.println("X ="+param.getTest());

		return null;

	}
}
