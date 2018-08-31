package ct.af.message.incoming.parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.internal.LinkedTreeMap;

public class Param_IDLE_Xxx {
	private Object A;
	private Object B;
	private Object C;
	private Object D;
	
	public Object getA() {
		return A;
	}
	public void setA(Object a) {
		A = a;
	}
	public Object getB() {
		return B;
	}
	public void setB(Object b) {
		B = b;
	}
	public Object getC() {
		return C;
	}
	public void setC(Object c) {
//		if(c instanceof LinkedTreeMap<?, ?>) {
//			LinkedTreeMap map = (LinkedTreeMap<Object, Object>)c;
//			for(Map.Entry<Object, Object> item : map.entrySet()) {
//				
//			}
//		}
//		HashMap<String, Object> resourceMap = new HashMap<String, Object>();
//		if(c instanceof LinkedTreeMap) {
//			// get all key in LinkedTreeMap
//			Set<String> keySet = ((LinkedTreeMap) c).keySet();
//			// set HashMap
//			for (String keyStr : keySet) {
//				resourceMap.put(keyStr, ((LinkedTreeMap) c).get(keyStr));
//			}
//		}
		C = c;
	}
	public Object getD() {
		return D;
	}
	public void setD(Object d) {
		D = d;
	}
	
	public HashMap<String, Object> getHashMap(Object data){
		HashMap<String, Object> resourceMap;
		if(data instanceof LinkedTreeMap) {
			resourceMap = new HashMap<>();
			// get all key in LinkedTreeMap
			Set<String> keySet = ((LinkedTreeMap) data).keySet();
			// set HashMap
			for (String keyStr : keySet) {
				resourceMap.put(keyStr, ((LinkedTreeMap) data).get(keyStr));
			}
		}else if(data instanceof HashMap) {
			resourceMap = new HashMap<>();

		}else if(data instanceof ArrayList) {	
			resourceMap = new HashMap<>();
			ArrayList<HashMap<String, Object>> resourceArry = new ArrayList<>();
			for(int i=((ArrayList) data).size()-1;i>=0;i--) 
			{
//				((ArrayList) data).add(getHashMap(((ArrayList) data).get(i)));
				
//				((ArrayList) data).remove(i);
				resourceArry.add(getHashMap(((ArrayList) data).get(i)));

//				resourceArry.remove(i);
			}
		}else {
			resourceMap = new HashMap<>();
		}
		return resourceMap;
	}
}
