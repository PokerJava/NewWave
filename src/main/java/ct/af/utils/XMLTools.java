package ct.af.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

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
		TreeMap<String, String> cTree = new TreeMap<String, String>();
		cTree.put("resourceC1", C.getResourceC1());
		cTree.put("resourceC2", C.getResourceC2());
		hashMapAll.put("C", cTree);
		ArrayList<TreeMap<String, String>> dArray = new ArrayList<>();
		TreeMap<String, String> dTree0 = new TreeMap<String, String>();
		dTree0.put("resourceD1", D.get(0).getResourceD1());
		dTree0.put("resourceD2", D.get(0).getResourceD2());
		TreeMap<String, String> dTree1 = new TreeMap<String, String>();
		dTree1.put("resourceD1", D.get(1).getResourceD1());
		dTree1.put("resourceD2", D.get(1).getResourceD2());
		dArray.add(dTree0);
		dArray.add(dTree1);
		hashMapAll.put("D", dArray);
		return hashMapAll;
	}

		public List<Elements> getD() {
			return D;
		}

		public void setD(List<Elements> d) {
			D = d;
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
		 
		public String getResourceC1() {
			return resourceC1;
		}
		public void setResourceC1(String resourceC1) {
			this.resourceC1 = resourceC1;
		}
		public String getResourceC2() {
			return resourceC2;
		}
		public void setResourceC2(String resourceC2) {
			this.resourceC2 = resourceC2;
		}

		 
		 
	}
	
	static class Elements {
		 @Element(required = false)
		    private String resourceD1;
		 @Element(required = false)
		 private String resourceD2;
		 
		public String getResourceD1() {
			return resourceD1;
		}
		public void setResourceD1(String resourceD1) {
			this.resourceD1 = resourceD1;
		}
		public String getResourceD2() {
			return resourceD2;
		}
		public void setResourceD2(String resourceD2) {
			this.resourceD2 = resourceD2;
		}

		 
		 
		 
	}
	
	public static Object getParseObject(String rawDataMsg, Class aClass) {

		Object parsedObject = null;
		HashMap<String, Object> paramHash = new HashMap<>();
		Serializer serializer = ParserPool.getPersister();
		try {
			ERDContainer container = serializer.read(ERDContainer.class, "<xml>" + rawDataMsg + "</xml>" , true);
			parsedObject = serializer.read(ERDContainer.class, "<xml>" + rawDataMsg + "</xml>" , false);
//			parsedObject = serializer.read(aClass, "<xml>" + container.getHeader().get(0).getValue() + "</xml>", false);

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
		return paramHash;
	}

}
