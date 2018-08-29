package ct.af.message.incoming.parser;

import ct.af.instance.AFInstance;
import com.google.gson.Gson;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_SDF_GetResourceInventory;
import ct.af.utils.GsonPool;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class Parser_SDF_GetResourceInventory {

	public Param_SDF_GetResourceInventory doParser(EquinoxRawData eqxRawData, AFInstance afInstance,
		AFSubInstance afSubIns) {
		
		Param_SDF_GetResourceInventory param = new Param_SDF_GetResourceInventory();
		String rawData = eqxRawData.getRawDataAttribute("val");
		 Gson gson = GsonPool.getGson();

        try {
            param = gson.fromJson(rawData, Param_SDF_GetResourceInventory.class);
            
        } catch (Exception ex) {
            param.setIsValid(false);
            AFLog.e("[Exception] can't parse in Parser_SDF_GetResourceInventory.");
            AFLog.e(ex);
        }

        GsonPool.pushGson(gson);       
		
		return param;
	}
}
