package ct.af.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ct.af.utils.GsonTools.JsonObjectExtensionConflictException;
import ec02.af.utils.AFLog;

public class SpecialResptoClient {
 
	//public static JsonObject specialRespToClient(JsonObject nomalRespToClient,String resourceIndex)
	public static JsonObject toSpecialRespToClient(JsonObject nomalRespToClient,String resourceIndex){
		JsonObject specialRespToClient ;
		switch (resourceIndex) {
			case "queryFreeUnitByPack_4H_001":
				specialRespToClient = specialRespToClient_queryFreeUnitByPack_4H_001(nomalRespToClient);
				break;
	
			default:
				specialRespToClient = nomalRespToClient;
				AFLog.e("[Error] can't found special response to client for resource index = "+resourceIndex);
				break;
		}
		
		return specialRespToClient;
		
	}
	
	
	public static JsonObject specialRespToClient_queryFreeUnitByPack_4H_001(JsonObject nomalRespToClient){
		//StringBuilder specialRespToClient = new StringBuilder("");
		JsonObject specialRespToClient = new JsonObject();
		JsonArray freeUnitItemList = (JsonArray) nomalRespToClient.get("freeUnitItemList");
		ArrayList<HashMap<String, Object>> flatMap = getFlatMapFromJsonArray(freeUnitItemList);
//		System.out.println("---flatMap---");
//		System.out.println(flatMap.toString());
//		System.out.println("------------------------");
		
		HashMap<String, List<HashMap<String, Object>>> groupByProductMap = new HashMap<String, List<HashMap<String, Object>>>();
		for(int i =0;i<flatMap.size();i++) {
			String firstId = (flatMap.get(i).get("productId")==null)?"":flatMap.get(i).get("productId").toString();
			String firstSequence = (flatMap.get(i).get("productSequenceId")==null)?"":flatMap.get(i).get("productSequenceId").toString();
			List<HashMap<String, Object>> productDetail;
			if(groupByProductMap.containsKey(firstId+firstSequence)) {
				productDetail = groupByProductMap.get(firstId+firstSequence);
				productDetail.add(flatMap.get(i));
			}else {
				productDetail = new ArrayList<>();
				productDetail.add(flatMap.get(i));
				groupByProductMap.put(firstId+firstSequence, productDetail);
			}
		}

		
		JsonArray mainProduct = new JsonArray();
		JsonArray onTopProduct = new JsonArray();
		JsonArray ortherFreeUnit = new JsonArray();
		
		for(Entry<String, List<HashMap<String, Object>>> key : groupByProductMap.entrySet()) {

				String productId = key.getValue().get(0).get("productId")==null?"":key.getValue().get(0).get("productId").toString();
				if(productId.length() == 8) {
					groupByProductProcessor(mainProduct,key.getValue(),true);
				}else if(productId.length() == 6){
					groupByProductProcessor(onTopProduct,key.getValue(),false);
				}else {
					groupByProductProcessor(ortherFreeUnit,key.getValue(),false);
				}
		}

		specialRespToClient.add("responseMainProductList", mainProduct);
		specialRespToClient.add("responseOntopAndServiceList", onTopProduct);
		specialRespToClient.add("responseOtherList", ortherFreeUnit);
		
		AFLog.d(specialRespToClient.toString());
		return specialRespToClient;
		
	}

	public static ArrayList<HashMap<String, Object>> getFlatMapFromJsonArray(JsonArray hirachyJsonArr) {
		ArrayList<HashMap<String, Object>> flatMap = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < hirachyJsonArr.size(); i++) {
			JsonObject hirachy = (JsonObject) hirachyJsonArr.get(i);
			JsonObject attr = new JsonObject();
			ArrayList<JsonObject> arrObj = new ArrayList<>();
			for (Map.Entry<String, JsonElement> entry : hirachy.entrySet()) {
				if (entry.getValue() instanceof JsonArray) {
					JsonArray arr = (JsonArray) entry.getValue();
					for (int j = 0; j < arr.size(); j++) {
						JsonObject obj = (JsonObject) arr.get(j);
						arrObj.add(obj);						
					}
				} else {
					attr.add(entry.getKey(), entry.getValue());
				}
			}
			JsonObject flat = null;
			for(int j=0; j<arrObj.size(); j++){
				HashMap<String, Object> hashmap = new HashMap<String, Object>();
				flat = arrObj.get(j);
				try {
					GsonTools.extendJsonObject(flat, GsonTools.ConflictStrategy.PREFER_FIRST_OBJ, attr);
				} catch (JsonObjectExtensionConflictException e) {
					AFLog.e("[Exception] can't merge JsonObject.");
					AFLog.e(e);
				}
				Gson gson = GsonPool.getGson();
				hashmap = (HashMap<String, Object>) gson.fromJson(flat,hashmap.getClass());
				GsonPool.pushGson(gson);
				flatMap.add(hashmap);
			}
			
			
		}
		
		AFLog.d(flatMap.toString());
		return flatMap;
	}
	
	public static void groupByProductProcessor (JsonArray jsonArr, List<HashMap<String, Object>> list, Boolean isMain) {
		
		// ProduuctID productSequenceId productName notificationName
		
		HashMap<String, Object> firstOfGroup =  list.get(0);
		JsonObject jsonProduct = new JsonObject();
		String[] productAttr = {"productId","productSequenceId","productName","notificationName"}; 
		String[] freeUnitAttr = {"freeUnitId","freeUnitName","measureUnit","measureUnitName"};
		
		for(int i=0;i<productAttr.length;i++){
			if(firstOfGroup.get(productAttr[i])!=null
					&&StringUtils.isNotBlank(firstOfGroup.get(productAttr[i]).toString())){
				if(isMain&&i==0){
					jsonProduct.addProperty(productAttr[i], firstOfGroup.get(productAttr[i]).toString().substring(0,6));
				}else{
					jsonProduct.addProperty(productAttr[i], firstOfGroup.get(productAttr[i]).toString());
				}
			}
		}

		HashMap<String, List<HashMap<String, Object>>> groupByFreeUnitMap = new HashMap<String, List<HashMap<String, Object>>>();
		
		//grouping by freeUnitId, if it didn't exist -> use unknown for key
		for(int i =0;i<list.size();i++) {
			String freeUnitId = "unknown";
			if(list.get(i).get(freeUnitAttr[0])!=null&&StringUtils.isNotBlank(list.get(i).get(freeUnitAttr[0]).toString())){
				freeUnitId = list.get(i).get(freeUnitAttr[0]).toString();
			}
			List<HashMap<String, Object>> freeUnitDetail ;
			if(groupByFreeUnitMap.containsKey(freeUnitId)) {
				freeUnitDetail = groupByFreeUnitMap.get(freeUnitId);
				freeUnitDetail.add(list.get(i));
				
			}else {
				freeUnitDetail = new ArrayList<>();
				freeUnitDetail.add(list.get(i));
				groupByFreeUnitMap.put(freeUnitId, freeUnitDetail);
			}
		}
		
		//freeUnitItemList
		JsonArray freeUnitArr = new JsonArray();		
		for(List<HashMap<String, Object>> freeUnitMap : groupByFreeUnitMap.values()) {
			Boolean isUnlimited = false;
			JsonObject jsonFreeUnitList = new JsonObject();
			HashMap<String, Object> firstFreeUnitOfGroup =  freeUnitMap.get(0);
			
			for(int i=0;i<freeUnitAttr.length;i++){
				if(firstFreeUnitOfGroup.get(freeUnitAttr[i])!=null
						&&StringUtils.isNotBlank(firstFreeUnitOfGroup.get(freeUnitAttr[i]).toString())){
					if(i==0){
						if(firstFreeUnitOfGroup.get(freeUnitAttr[i]).toString().contains("_U")){
							isUnlimited = true;							
						}
						if((!"unknown".equalsIgnoreCase(firstFreeUnitOfGroup.get(freeUnitAttr[i]).toString()))
								&&firstFreeUnitOfGroup.get(freeUnitAttr[i])!=null
								&&StringUtils.isNotBlank(firstFreeUnitOfGroup.get(freeUnitAttr[i]).toString())){
						jsonFreeUnitList.addProperty(freeUnitAttr[i], firstFreeUnitOfGroup.get(freeUnitAttr[i]).toString().replaceAll("_U",""));
						}
					}else if(firstFreeUnitOfGroup.get(freeUnitAttr[i])!=null
							&&StringUtils.isNotBlank(firstFreeUnitOfGroup.get(freeUnitAttr[i]).toString())){					
						jsonFreeUnitList.addProperty(freeUnitAttr[i], firstFreeUnitOfGroup.get(freeUnitAttr[i]).toString());
					}
				}
			}
			
			//freeUnitItemDetailList
			JsonArray freeUnitdetailArr = new JsonArray();
			for(int i=0;i<freeUnitMap.size();i++) {
				JsonObject jsonFreeUnitDetail = new JsonObject();
				for (Map.Entry<String, Object> entry : freeUnitMap.get(i).entrySet()) {
					if((!Arrays.asList(productAttr).contains(entry.getKey())&&!Arrays.asList(freeUnitAttr).contains(entry.getKey()))
							&&(entry.getValue()!=null&&StringUtils.isNotBlank(entry.getValue().toString()))){
						if(isUnlimited && entry.getKey().toString().equalsIgnoreCase("initialAmount")){
							jsonFreeUnitDetail.addProperty(entry.getKey().toString(), "Unlimited");
						}else{
							jsonFreeUnitDetail.addProperty(entry.getKey().toString(), entry.getValue().toString());
						}
					}					
				}
				freeUnitdetailArr.add(jsonFreeUnitDetail);	
			}
			
			jsonFreeUnitList.add("freeUnitItemDetailList", freeUnitdetailArr);
			freeUnitArr.add(jsonFreeUnitList);
		}
		jsonProduct.add("freeUnitItemList", freeUnitArr);
		
		AFLog.d(jsonProduct.toString());
		jsonArr.add(jsonProduct);
	}
}
