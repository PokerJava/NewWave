package ct.af.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
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
//		@Element(name="D")
//		private D dVal;
		

//		@Attribute
//		private String value = "";
//		
		@Element
		private String A;
		
		@ElementList
		List<String> B;
		
		@ElementList
		List<String>  C;
		
		@ElementList
		List<String> D;
		
		
//		@Path("D/element[0]")
//		@Element
//		private String resourceD1;
//		
//		@ElementList(name = "B")
//		List<String> element;

//		public String getValue() {
//			return value;
//		}
//
//		public void setValue(String value) {
//			this.value = value;
//		}
		public HashMap<String, Object> getAll(){
		hashMapAll.put("A", A);
		hashMapAll.put("B", B);
		hashMapAll.put("C", C);
		
		for(int i=0;i<D.size();i++)
		{
			if(D.get(i)==null)
			{
				D.remove(i);
				i--;
			}
		}
		
		hashMapAll.put("D", D);
		return hashMapAll;
	}
	}

	static class ERDHeader {
//		HashMap<String, Object> hashMapAll = new HashMap<>();
//		@Attribute
//		private String name;
//
//		@Attribute
//		private String value;
//		
//		@Attribute
//		private String x;
//		
//		@Attribute
//		private String y;

		

		
//		public String getName() {
//			return name;
//		}
//		
//
//		public void setName(String name) {
//			this.name = name;
//		}
//
//		public String getValue() {
//			return value;
//		}
//
//		public void setValue(String value) {
//			this.value = value;
//		}
//		public HashMap<String, Object> getAll(){
//			hashMapAll.put("name", name);
//			hashMapAll.put("value", value);
//			hashMapAll.put("x", x);
//			hashMapAll.put("y", y);
//			hashMapAll.put("test", test);
//			return hashMapAll;
//		}
	}

	static class ERDContainer {
		@Element(name = "ERDData", required = false)
		ERDData data;// = new ERDData();

//		@ElementList(name = "ERDData", required = false)
//		List<ERDHeader> header;
//		
		public ERDData getData() {
			return data;
		}

		public void setData(ERDData data) {
			this.data = data;
		}

//
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
	
	static class D {
	    @ElementList(entry = "element", inline = true,required = false)
	    private List<Elements> element = new ArrayList<>();

		public List<Elements> getElement() {
			return element;
		}

		public void setElementList(List<Elements> element) {
			this.element = element;
		}
	    
	
	}
	
	static class Elements {
		 @ElementList(entry = "resourceD1", inline = true,required = false)
		    private List<Element> resourceD1 = new ArrayList<>();

		public List<Element> getResourceD1() {
			return resourceD1;
		}

		public void setResourceD1List(List<Element> resourceD1) {
			this.resourceD1 = resourceD1;
		}
		 
		 
	}
	
	static class ResourceD1 {
		@Element(name = "resourceD1")
		private String val;

		public String getVal() {
			return val;
		}

		public void setVal(String val) {
			this.val = val;
		}
		
		
		
	}
	

	public static Object getParseObject(String rawDataMsg, Class aClass) {

		Object parsedObject = null;
		Serializer serializer = ParserPool.getPersister();
		try {
			ERDContainer container = serializer.read(ERDContainer.class, "<xml>" + rawDataMsg + "</xml>" , false);
			parsedObject = serializer.read(ERDContainer.class, "<xml>" + rawDataMsg + "</xml>" , false);
//			parsedObject = serializer.read(aClass, "<xml>" + container.getHeader().get(0).getValue() + "</xml>", false);

			HashMap<String, Object> paramHash = new HashMap<>();
			//paramHash = (HashMap<String, Object>) container.getHeader().get(0).getAll();
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
