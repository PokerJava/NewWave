package ct.af.utils;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import ct.af.utils.XMLTools.ERDData;
import ct.af.utils.XMLTools.ERDHeader;
import ec02.af.utils.AFLog;

public class XMLTools {
	private static boolean isTest = false;

	static class ERDData {
		@Attribute
		private String value = "";

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	static class ERDHeader {
		@Attribute
		private String name;

		@Attribute
		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	static class ERDContainer {
		@Element(name = "ERDData", required = false)
		ERDData data = new ERDData();

		@ElementList(name = "ERDHeader", required = false)
		List<ERDHeader> header;

		public ERDData getData() {
			return data;
		}

		public void setData(ERDData data) {
			this.data = data;
		}

		public List<ERDHeader> getHeader() {
			return header;
		}

		public void setHeader(List<ERDHeader> header) {
			this.header = header;
		}
	}

	private static String stringReplace(String rawDataMsg) {
		rawDataMsg = rawDataMsg.replace("&lt;?xml version=&apos;1.0&apos; encoding=&apos;UTF-8&apos;?&gt;", "");
		rawDataMsg = rawDataMsg.replace("&lt;?xml version=&apos;1.0&apos; encoding=&apos;UTF-8&apos;?&gt;", "");
		rawDataMsg = rawDataMsg.replace("&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;", "");
		rawDataMsg = rawDataMsg.replace("&lt;?xml version=&quot;1.0&quot; encoding=&quot;tis-620&quot; ?&gt;", "");

		// Insert CDATA
		rawDataMsg = rawDataMsg.replace(
				"&lt;ResultDesc xmlns=&quot;http://www.huawei.com/bme/cbsinterface/common&quot;&gt;",
				"&lt;ResultDesc xmlns=&quot;http://www.huawei.com/bme/cbsinterface/common&quot;&gt;&lt;![CDATA[");
		rawDataMsg = rawDataMsg.replace("&lt;/ResultDesc&gt;", "]]&gt;&lt;/ResultDesc&gt;");

		rawDataMsg = rawDataMsg.replace("&lt;cbs:ResultDesc&gt;", "&lt;cbs:ResultDesc&gt;&lt;![CDATA[");
		rawDataMsg = rawDataMsg.replace("&lt;/cbs:ResultDesc&gt;", "]]&gt;&lt;/cbs:ResultDesc&gt;");

		return rawDataMsg;
	}

	public static Object getParseObject(String rawDataMsg, Class aClass) {
		rawDataMsg = stringReplace(rawDataMsg);

		Object parsedObject = null;
		Serializer serializer = ParserPool.getPersister();
		try {
			ERDContainer container = serializer.read(ERDContainer.class, "<xml>" + rawDataMsg + "</xml>", false);

			parsedObject = serializer.read(aClass, "<xml>" + container.getData().getValue() + "</xml>", false);
		} catch (Exception e) {
			if (!isTest) {
				AFLog.e("[Exception] Error invalid ERDContainer");
				AFLog.e(e.getMessage());
				AFLog.d(e);
			} else {
				e.printStackTrace();
			}

			try {
				parsedObject = aClass.newInstance();
			} catch (Exception e2) {
				AFLog.e("FATAL ERROR: Cannot instantiate blank object when there's an error on parsing.");
				AFLog.e(e2);
			}
		}
		ParserPool.pushPersister((Persister) serializer);
		return parsedObject;
	}

}
