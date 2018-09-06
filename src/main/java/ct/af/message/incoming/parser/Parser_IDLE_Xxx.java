package ct.af.message.incoming.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;

import ct.af.enums.EResultCode;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_Xxx;
import ct.af.utils.GsonPool;
import ec02.af.abstracts.AbstractAF;
import ec02.data.interfaces.EquinoxRawData;

public class Parser_IDLE_Xxx {
	public Param_IDLE_Xxx doParser(AbstractAF abstractAF, EquinoxRawData eqxRawData, AFInstance afInstance,
			AFSubInstance afSubIns) {
		Param_IDLE_Xxx param = new Param_IDLE_Xxx();
		Gson gson = GsonPool.getGson();

		/*
		 * { "A":"resourceA", "B":["resourceB1","resourceB2"], "C":{"resourceC1":"c1",
		 * "resourceC2":"c2"}, "D":[{"resourceD1":"d1", "resourceD2":"d2"},
		 * {"resourceD3":"d3", "resourceD4":"d4"}] }
		 * 
		 */

		String rawCType = "text/plain";
		String rawPlainMessage = "{ \"A\" : \"resourceA\","
				+ "					\"B\" : [\"resourceB1\",\"resourceB2\"],"
				+ "					\"C\" : {\"resourceC1\" : \"c1\","
				+ "							\"resourceC2\" : \"c2\"},"
				+ "					\"D\" : [{\"resourceD1\" : \"d1\","
				+ "							\"resourceD2\" : \"d2\"},"
				+ "							{\"resourceD3\" : \"d3\","
				+ "							\"resourceD4\" : \"d4\"}]" + "					}";

		String xmlMessage1 = "<ERDData value="
				+ "/api/v1/aaf/publicId.json?company=CT&name=nutchapol.thathaii@gmail.co.th&invoke=999999&mobile=0909767978"
				+ "/>]]>";

		String xmlValue = "<ERDData value=\"" + "{" + "\"A\"" + ":" + "\"564093493534958340\"" + "," + "\"B\"" + ":"
				+ "\"1775\"" + ","

				+ "\"C\"" + ":" + "\"fb\"" + ","

				+ "\"D\"" + ":" + "\"30010\"" + "}"

				+ "/>";

		String xmlMessage2 = "<root>" + "<A>CT</A>" + "<B>" + "<element>dog</element>" + "<element>cat</element>"
				+ "</B>" + "<C>" + "<jojo>1234</jojo>" + "<momo>4321</momo>" + "</C>" + "<D>" + "<element>"
				+ "<resourceA>A1</resourceA>" + "<resourceB>" + "<jojo>1234</jojo>" + " <momo>4321</momo>"
				+ "</resourceB>" + "</element>" + "<element>" + " <resourceB>B1" + "<jojo>"
				+ "<A><B>1234</B></A></jojo>" + "<momo>3242</momo>" + "</resourceB>" + " <resourceA>B2</resourceA>"
				+ "</element>" + "</D>" + "</root>";

		String rawDiameterMessage = "<![CDATA[" + "<Session-Id value=\"session_gx_0\"/>"
				+ "<Origin-Host value=\"pcrf_p3\"/>" + "<Origin-Realm value=\"pcrf_p3.sand.ais.co.th\"/>"
				+ "<Destination-Realm value=\"dgw_gx.sand.ais.co.th\"/>" + "<Destination-Host value=\"dgw_gx\"/>"
				+ "<CC-Request-Type value=\"1\"/>" + "<CC-Request-Number value=\"0\"/>"
				+ "<Result-Code value=\"2001\"/>" + "<Auth-Application-Id value=\"16777238\"/>"
				+ "<CC-Session-Failover value=\"1\"/>" + "<Service-Information>" + "    <PS-Information>"
				+ "        <PS-Furnish-Charging-Information>" + "            <PS-Free-Format-Data value=\"0X22\"/>"
				+ "            <PS-Append-Free-Format-Data value=\"1\"/>" + "        </PS-Furnish-Charging-Information>"
				+ "    </PS-Information>" + "</Service-Information>]]>";

		String msg = "<ERDHeader>\n" + "      <Header name=\"Server\" value=\"Apache-Coyote/1.1\" />\n"
				+ "      <Header name=\"X-Powered-By\" value=\"Servlet 2.4; JBoss-4.0.5.GA (build: CVSTag=Branch_4_0 date=200610162339)/Tomcat-5.5\" />\n"
				+ "      <Header name=\"X-UA-Compatible\" value=\"IE=EmulateIE7\" />\n"
				+ "      <Header name=\"Set-Cookie\" value=\"JSESSIONID=657179F49E5885A4A090D04B5F67FD2D.server1; Path=/\" />\n"
				+ "      <Header name=\"Content-Type\" value=\"text/xml;charset=UTF-8\" />\n"
				+ "      <Header name=\"Transfer-Encoding\" value=\"chunked\" />\n"
				+ "      <Header name=\"Date\" value=\"Wed, 07 Jun 2017 10:24:08 GMT\" />\n" + "    </ERDHeader>\n"
				+ "    <ERDData value=\"\" />";

		if (rawCType.equals("text/plain")) {
			try {
				JsonParser jsonParser = new JsonParser();
				HashMap<String, Object> hashMapParam = new HashMap<>();
				JsonObject resourceObject = jsonParser.parse(rawPlainMessage).getAsJsonObject();
				gson = GsonPool.getGson();
				param = gson.fromJson(resourceObject, Param_IDLE_Xxx.class);
				GsonPool.pushGson(gson);

				if (param.getA() instanceof ArrayList || param.getA() instanceof LinkedTreeMap) {
					param.setA(param.getHashMap(param.getA()));
				}
				if (param.getB() instanceof ArrayList || param.getB() instanceof LinkedTreeMap) {
					param.setB(param.getHashMap(param.getB()));
				}
				if (param.getC() instanceof ArrayList || param.getC() instanceof LinkedTreeMap) {
					param.setC(param.getHashMap(param.getC()));
				}
				if (param.getD() instanceof ArrayList || param.getD() instanceof LinkedTreeMap) {
					param.setD(param.getHashMap(param.getD()));
				}

				// if (param.getD() instanceof ArrayList) {
				// SortedSet<String> key = new TreeSet<>();
				// ArrayList<Object> test = (ArrayList<Object>) param.getD();
				// HashMap<String, Object> dataHash = new HashMap<>();
				// for (int i = 0; i < test.size(); i++) {
				// dataHash = (HashMap<String, Object>) test.get(i);
				// key.add(dataHash.get("resourceA").toString());
				// }
				// for (String str : key) {
				// System.out.println(str);
				// }
				// }
				hashMapParam = gson.fromJson(resourceObject, HashMap.class);
				if (hashMapParam.get("A") instanceof ArrayList || hashMapParam.get("A") instanceof LinkedTreeMap) {
					hashMapParam.put("A", param.getHashMap(hashMapParam.get("A")));
				}
				if (hashMapParam.get("B") instanceof ArrayList || hashMapParam.get("B") instanceof LinkedTreeMap) {
					hashMapParam.put("B", param.getHashMap(hashMapParam.get("B")));
				}
				if (hashMapParam.get("C") instanceof ArrayList || hashMapParam.get("C") instanceof LinkedTreeMap) {
					hashMapParam.put("C", param.getHashMap(hashMapParam.get("C")));
				}
				if (hashMapParam.get("D") instanceof ArrayList || hashMapParam.get("D") instanceof LinkedTreeMap) {
					hashMapParam.put("D", param.getHashMap(hashMapParam.get("D")));
				}
				afSubIns.setSubClientHashMapParameter(hashMapParam);
				afSubIns.setSubCurrentState(ESubState.IDLE_XXX.getState());
				afSubIns.setSubControlState(ESubState.IDLE_XXX.getState());
				afSubIns.setSubNextState(ESubState.Unknown.toString());
				afSubIns.setSubResultCode(EResultCode.RE20000.getResultCode());
				afSubIns.setSubInternalCode(EResultCode.RE20000.getResultCode());
				afInstance.incrementMainCountWait();
				afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);
				param.setValid(true);
			} catch (Exception e) {
				param.setValid(false);
			}
			return param;
		}

		else if (rawCType.equals("text/xml")) {
			if (validateFormatXML(xmlMessage2)) {
				String xmlFormat = getXmlType(xmlMessage2);
				if (xmlFormat.equals("xmlUrl")) {
					HashMap<String, String> map = param.getXMLMsgToHashmap(xmlMessage1);
					String erdData = map.get("ERDData");

					param.setName(param.getParameterValueFromUrl(erdData, "name"));
					param.setCompany(param.getParameterValueFromUrl(erdData, "company"));
					param.setMobileNo(param.getParameterValueFromUrl(erdData, "mobile"));
					param.setInvoke(param.getParameterValueFromUrl(erdData, "invoke"));

				} else if (xmlFormat.equals("xmlValue")) {
					String data = xmlValue.substring(xmlValue.indexOf("{"), xmlValue.lastIndexOf("}") + 1);
					JsonParser jsonParser = new JsonParser();

					JsonObject resourceOrderJsonObject = jsonParser.parse(data).getAsJsonObject();
					param = gson.fromJson(resourceOrderJsonObject, Param_IDLE_Xxx.class);
					GsonPool.pushGson(gson);

					if (param.getA() instanceof ArrayList || param.getA() instanceof LinkedTreeMap) {
						param.setA(param.getHashMap(param.getA()));
					}
					if (param.getB() instanceof ArrayList || param.getB() instanceof LinkedTreeMap) {
						param.setB(param.getHashMap(param.getB()));
					}
					if (param.getC() instanceof ArrayList || param.getC() instanceof LinkedTreeMap) {
						param.setC(param.getHashMap(param.getC()));
					}
					if (param.getD() instanceof ArrayList || param.getD() instanceof LinkedTreeMap) {
						param.setD(param.getHashMap(param.getD()));
					}

				} else if (xmlFormat.equals("xml")) {
					String XmlHeader = getHeaderXML(xmlMessage2);
					HashMap<String, Object> resourceHashMap = (HashMap<String, Object>) xmlToHash(xmlMessage2);
					Object jsonFormat = gson.toJson(resourceHashMap.get(XmlHeader));
					GsonPool.pushGson(gson);

					if (validateParam((HashMap<String, Object>) resourceHashMap.get("root"))) {
						param = gson.fromJson((String) jsonFormat, Param_IDLE_Xxx.class);
						GsonPool.pushGson(gson);
						if (param.getA() instanceof ArrayList || param.getA() instanceof LinkedTreeMap) {
							param.setA(param.getHashMap(param.getA()));
						}
						if (param.getB() instanceof ArrayList || param.getB() instanceof LinkedTreeMap) {
							param.setB(param.getHashMap(param.getB()));
						}
						if (param.getC() instanceof ArrayList || param.getC() instanceof LinkedTreeMap) {
							param.setC(param.getHashMap(param.getC()));
						}
						if (param.getD() instanceof ArrayList || param.getD() instanceof LinkedTreeMap) {
							param.setD(param.getHashMap(param.getD()));
						}

						System.out.println(param.getA());
						System.out.println(param.getB());
						System.out.println(param.getC());
						System.out.println(param.getD());

					}
				}
			}
		} else if (rawCType.equals("Diameter")) {
			HashMap<String, Object> dataHash = new HashMap<>();
			dataHash = (HashMap<String, Object>) diameterToHash(rawDiameterMessage);
		} else if (rawCType.equals("Ldap")) {

		} else {
			return param;
		}

		return param;
	}

	public Object diameterToHash(Object message) {
		Object finalData = new Object();
		HashMap<String, Object> dataHash = new HashMap<>();
		if (message instanceof String) {
			finalData = new Object();
			String dataStr = (String) message;
			dataStr = dataStr.replace("<![CDATA[", "");
			dataStr = dataStr.replace("]]>", "");

			String[] segment = dataStr.split("/><");
			for (String dataLoop : segment) {
				String key;
				String data;

				String tempMsg = dataLoop;
				int startPos;
				int endPos;
				if (dataLoop.contains("<")) {
					key = dataLoop.substring(dataLoop.indexOf("<") + 1, dataLoop.indexOf("value")).trim();
				} else {
					key = dataLoop.substring(0, dataLoop.indexOf("value")).trim();
				}

				startPos = dataLoop.indexOf("value=\"") + 7;
				data = dataLoop.substring(startPos, dataLoop.length() - 1);
				// endPos = data.indexOf("\"");
				// data = data.substring(0, endPos);

				dataHash.put(key, data);
				// finalData = xmlToHash(dataHash);
			}
		}

		return dataHash;
	}

	public Object xmlToHash(Object xmlMessage2) {
		Object finalData;

		// TODO Validate T3 from Config
		if (xmlMessage2 instanceof String) {
			finalData = new Object();
			String strMessage = xmlMessage2.toString();
			if (strMessage.contains("<") || strMessage.contains(">")) {
				String key;
				String data;
				HashMap<String, Object> dataHash = new HashMap<>();
				String tempMsg = strMessage;
				int startPos;
				int endPos;

				key = strMessage.substring(strMessage.indexOf("<") + 1, strMessage.indexOf(">"));
				startPos = strMessage.indexOf(">") + 1;
				endPos = strMessage.lastIndexOf("<");
				data = strMessage.substring(startPos, endPos);

				dataHash.put(key, data);
				finalData = xmlToHash(dataHash);

			}
		} else if (xmlMessage2 instanceof HashMap) {
			finalData = xmlMessage2;
			HashMap<String, Object> dataHash = (HashMap<String, Object>) xmlMessage2;
			for (String key : dataHash.keySet()) {
				HashMap<String, Object> dataHash1 = new HashMap<>();
				if (dataHash.get(key) instanceof String) {
					String strMessage = dataHash.get(key).toString();
					if (strMessage.contains("<") || strMessage.contains(">")) {
						if (strMessage.contains("<element>")) {
							strMessage = strMessage.replaceAll("</element><element>", ",");
							strMessage = strMessage.replaceAll("<element>", "[");
							strMessage = strMessage.replaceAll("</element>", "]");
						}
						String key1 = "";
						String data = "";
						String tempMsg;
						int startPos;
						int endPos;
						int loop = countText(strMessage, "<") / 2;
						tempMsg = strMessage;
						while (!tempMsg.isEmpty()) {
							if (tempMsg.contains("<") || tempMsg.contains(">")) {

								key1 = tempMsg.substring(tempMsg.indexOf("<") + 1, tempMsg.indexOf(">"));

								startPos = tempMsg.indexOf("<" + key1 + ">") + key1.length() + 2;
								endPos = tempMsg.indexOf("</" + key1 + ">");
								data = tempMsg.substring(startPos, endPos);
								tempMsg = tempMsg.substring(endPos + key1.length() + 3);
								dataHash1.put(key1, data);
							} else if (tempMsg.contains("]")) {
								tempMsg = "";
							} else {
								dataHash.put(key, dataHash1);
								return xmlMessage2;
							}
						}
						dataHash.put(key, dataHash1);
						xmlToHash(dataHash.get(key));
					}
				} else if (dataHash.get(key) instanceof HashMap) {
					finalData = xmlToHash(dataHash.get(key));
				}
			}
		} else {
			finalData = xmlMessage2;
		}

		return finalData;
	}

	public Object hashToJson(Object data, JsonObject jsonData) {
		if (data instanceof HashMap) {
			HashMap<String, Object> dataHash = (HashMap<String, Object>) data;
			for (String key : dataHash.keySet()) {
				if (dataHash.get(key) instanceof String) {
					jsonData.addProperty(key, (String) dataHash.get(key));
				} else {
					hashToJson(dataHash.get(key), jsonData);
				}
			}
		} else {
			jsonData = new JsonObject();
		}
		return jsonData;
	}

	public int countText(String text, String textForCount) {
		int count = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.substring(i, i + 1).equals(textForCount)) {
				++count;
			}
		}

		return count;
	}

	public boolean validateParam(HashMap<String, Object> dataHash) {
		boolean isValidate = false;
		if (dataHash.containsKey("A") && dataHash.containsKey("B") && dataHash.containsKey("C")
				&& dataHash.containsKey("D")) {
			isValidate = true;
		}

		return isValidate;
	}

	public boolean validateFormatXML(String xml) {
		boolean isValid = false;
		int tagFirst = countText(xml, "<");
		int tagSecond = countText(xml, ">");
		int tagEnd = 0;

		if (tagFirst == tagSecond) {
			isValid = true;
		}
		return isValid;
	}

	public String getHeaderXML(String xml) {
		int headStartPosition;
		int headEndPosition;
		String headFirst;
		String headSecond;
		String header = "";

		headStartPosition = xml.indexOf("<") + 1;
		headEndPosition = xml.indexOf(">");
		headFirst = xml.substring(headStartPosition, headEndPosition);

		headStartPosition = xml.lastIndexOf("</") + 2;
		headEndPosition = xml.lastIndexOf(">");
		headSecond = xml.substring(headStartPosition, headEndPosition);

		if (headFirst.equals(headSecond)) {
			header = headFirst;
		}

		return header;
	}

	public String getXmlType(String xml) {
		int startPosition = xml.indexOf("<") + 1;
		int endPosition = xml.indexOf(">");
		String type = "";
		String head = xml.substring(startPosition, endPosition);

		if (head.contains("value")) {
			type = "xmlValue";
		} else if (countText(xml, "/") > countText(xml, "<")) {
			type = "xmlUrl";
		} else {
			type = "xml";
		}
		return type;
	}

	public boolean validateParam(Param_IDLE_Xxx param) {
		boolean isValid = false;
		/* Validate format json */

		return isValid;
	}

	public boolean validateJsonFormat(String rawMessage, JsonParser jsonParser) {
		boolean isValid = false;
		try {
			JsonObject resourceOrderJsonObject = jsonParser.parse(rawMessage).getAsJsonObject();
		} catch (Exception e) {
			isValid = false;
		}
		return isValid;
	}

}
