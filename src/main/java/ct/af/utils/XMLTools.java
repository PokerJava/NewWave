package ct.af.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import com.google.gson.Gson;
import ct.af.message.incoming.parameter.Param_IDLE_Xxx;
import ct.af.utils.XMLTools.ERDData;
import ct.af.utils.XMLTools.ERDHeader;
import ec02.af.utils.AFLog;

public class XMLTools {
	private static boolean isTest = false;

	static class ERDData {
		HashMap<String, Object> hashMapAll = new HashMap<>();

		@Element
		private String A;
		
		@ElementList
		List<String> B;
		
		@Element
		ElementsC  C;
		
		@ElementList
		List<Elements> D;
		
		public HashMap<String, Object> getAll(){
		hashMapAll.put("A", A);
		hashMapAll.put("B", B);
		hashMapAll.put("C", C);
		
		hashMapAll.put("D", D);
		return hashMapAll;
	}
	}

	static class ERDHeader {
		
//		@Attribute
//		private String name;
//
//		@Attribute
//		private String value;

		@ElementList
		List<String> element;

	}

	static class ERDContainer {
		@Element(name = "ERDData", required = false)
		ERDData data;// = new ERDData();

//		@ElementList(name = "ERDData", required = false)
//		List<ERDHeader> header;
		
//		public ERDData getData() {
//			return data;
//		}
//
//		public void setData(ERDData data) {
//			this.data = data;
//		}


//		public  List<ERDHeader> getHeader() {
//			return header;
//		}
//
//		public void setHeader(List<ERDHeader> header) {
//			this.header = header;
//		}	
	}
	
	
	
	static class ElementsC {
		 @Element(required = false)
		 private String resourceC1;
		 @Element(required = false)
		 private String resourceC2;

		 
	}
	
	static class Elements {
		 @Element(required = false)
		    private String resourceD1;
		 @Element(required = false)
		 private String resourceD2;


		 
		 
	}
	
	public static Object getParseObject(String rawDataMsg, Class aClass) {

		Object parsedObject = null;
		Serializer serializer = ParserPool.getPersister();
		try {
			ERDContainer container = serializer.read(ERDContainer.class, "<xml>" + rawDataMsg + "</xml>" , true);
			parsedObject = serializer.read(ERDContainer.class, "<xml>" + rawDataMsg + "</xml>" , false);
//			parsedObject = serializer.read(aClass, "<xml>" + container.getHeader().get(0).getValue() + "</xml>", false);

			HashMap<String, Object> paramHash = new HashMap<>();
			paramHash = container.data.getAll();
			Gson gson = GsonPool.getGson();
			parsedObject = gson.toJson(paramHash);
			GsonPool.pushGson(gson);
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
