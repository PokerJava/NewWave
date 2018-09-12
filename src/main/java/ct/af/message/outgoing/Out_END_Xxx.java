package ct.af.message.outgoing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ct.af.enums.ECType;
import ct.af.enums.EEqxMsg;
import ct.af.enums.EEventType;
import ct.af.enums.EMethod;
import ct.af.enums.EProtocol;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_Xxx;
import ec02.af.abstracts.AbstractAF;
import ec02.af.data.AFDataFactory;
import ec02.af.utils.AFLog;
import ec02.data.enums.EEquinoxRawData;
import ec02.data.interfaces.EquinoxRawData;

public class Out_END_Xxx {
	public EquinoxRawData messageBuilder(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {
		EquinoxRawData eqxRawData = AFDataFactory.createEquinoxRawData();
		Object message = new Object();
		Param_IDLE_Xxx paramXxx = afSubIns.getSubParam();
		StringBuilder strbuilder = new StringBuilder();
		
		if (afSubIns.getType().equals("text/plain")) {
			/* JsonFormat */
			JsonObject messageJson = new JsonObject();
			messageJson.addProperty("A", paramXxx.getA().toString());

			System.out.println(paramXxx.getB());
			JsonArray jsonAry = new JsonArray();
			ArrayList<Object> arryParam = (ArrayList<Object>) paramXxx.getB();
			for (int i = 0; i < arryParam.size(); i++) {
				jsonAry.add(arryParam.get(i).toString());
			}
			messageJson.add("B", jsonAry);

			HashMap<String, Object> hashParam = (HashMap<String, Object>) paramXxx.getC();
			SortedSet<String> SortKey = new TreeSet<>();
			/* Sort Key */
			for (String key : hashParam.keySet()) {
				SortKey.add(key);
			}
			/* create Object json */
			JsonObject subMessageJson = new JsonObject();
			for (String key : SortKey) {
				subMessageJson.addProperty(key, hashParam.get(key).toString());
			}
			messageJson.add("C", subMessageJson);

			jsonAry = new JsonArray();

			ArrayList<HashMap<String, Object>> subArryParam = (ArrayList<HashMap<String, Object>>) paramXxx.getD();
			for (int i = 0; i < subArryParam.size(); i++) {
				SortedSet<String> SortSubKey = new TreeSet<>();
				/* Sort Key */
				for (String key : subArryParam.get(i).keySet()) {
					SortSubKey.add(key);
				}
				subMessageJson = new JsonObject();
				for (String key : SortSubKey) {
					subMessageJson.addProperty(key, subArryParam.get(i).get(key).toString());
				}
				jsonAry.add(subMessageJson);
			}
			messageJson.add("D", jsonAry);
			message = messageJson;
			System.out.println(message);
		}
		else{	
			strbuilder.append("&lt;A&gt;"+paramXxx.getA().toString()+"&lt;/A&gt;\n"+"&lt;B&gt;\n");
	
			ArrayList<Object> arryParam = (ArrayList<Object>) paramXxx.getB();
			 
			for (int i = 0; i < arryParam.size(); i++) {
					strbuilder.append("&lt;element&gt;"+arryParam.get(i).toString()+"&lt;/element&gt;\n");
			}
			strbuilder.append("&lt;/B&gt;\n&lt;C&gt;\n");
			
			HashMap<String, Object> hashParam = (HashMap<String, Object>) paramXxx.getC();
			SortedSet<String> SortKey = new TreeSet<>();
			/* Sort Key */
			for (String key : hashParam.keySet()) {
				SortKey.add(key);
			}
				for(String key:SortKey){
					strbuilder.append("&lt;"+key+"&gt;"+hashParam.get(key)+"&lt;/"+key+"&gt;\n");
				}
			strbuilder.append("&lt;/C&gt;\n&lt;D&gt;\n");
			
			ArrayList<HashMap<String, Object>> subArryParam = (ArrayList<HashMap<String, Object>>) paramXxx.getD();
			for (int i = 0; i < subArryParam.size(); i++) {
				SortedSet<String> SortSubKey = new TreeSet<>();
				/* Sort Key */
				for (String key : subArryParam.get(i).keySet()) {
					SortSubKey.add(key);
				}
				strbuilder.append("&lt;element&gt;\n");
				for (String key : SortSubKey) {
					strbuilder.append("&lt;"+ key+ "&gt;" + subArryParam.get(i).get(key).toString()+"&lt;/"+ key+ "&gt;\n");
							
				}
				strbuilder.append("&lt;/element&gt;\n");
			}
			
			
			
			
	
			
//			"<ERDData>"
////			+ 		"<A>resourceA</A>"
////			+ 		"<B>"
////			+ 		  "<element>resourceB1</element>"
////			+ 		  "<element>resourceB2</element>"
////			+      "</B>"
////			+       "<C>"
////			+ 		  "<resourceC1>c1</resourceC1>"
////			+ 		  "<resourceC2>c2</resourceC2>"
////			+      "</C>"
//			+ 		"<D>"
//			+ 		  "<element>"
//			+ 			"<resourceD1>d1</resourceD1>"
//			+ 			"<resourceD1>d2</resourceD1>"
//			+ 		 "</element>"
//			+ 		  "<element>"
//			+		    "<resourceD1>d3</resourceD1>"
//			+		    "<resourceD2>d4</resourceD2>"
//			+ 		 "</element>"
//			+	   "</D>"
//			+ "</ERDData>";
		}

		Map<String, String> mapResponse = new HashMap<>();
		mapResponse.put(EEqxMsg.TYPE.getEqxMsg(), EEventType.RESPONSE.getEventType());
		mapResponse.put(EEqxMsg.CTYPE.getEqxMsg(), ECType.TEXTPLAIN.getCType());
		mapResponse.put(EEqxMsg.PROTOCOL.getEqxMsg(), EProtocol.HTTP.toString());
		mapResponse.put(EEqxMsg.METHOD.getEqxMsg(), EMethod.POST.toString());
		mapResponse.put(EEqxMsg.VAL.getEqxMsg(), message.toString());
		mapResponse.put(EEqxMsg.URL.getEqxMsg(), afSubIns.getSubInitURL());

		String[] splitOrig = afSubIns.getSubInitOrig().split("\\.");
		MessageBuilder builder = new MessageBuilder("");
		try {
			abstractAF.getEquinoxUtils().sendHTTPResponseMessage(builder, EEquinoxRawData.CTypeHTTP.TEXT_PLAIN,
					afSubIns.getSubInitInvoke(), splitOrig[2] + "." + splitOrig[3], mapResponse);
		} catch (Exception e) {
			AFLog.e("[Exception] can't build HTTPResponseMessage.");
			AFLog.e(e);
		}

		return eqxRawData;
	}

}
