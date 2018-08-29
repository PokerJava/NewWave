package ct.af.message.incoming.parser;

import com.google.gson.Gson;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_SDF_GetResourceInfraNode;
import ct.af.utils.GsonPool;
import ct.af.utils.JsonUtils;
import ec02.data.interfaces.EquinoxRawData;

public class Parser_SDF_GetResourceInfraNode {

    public Param_SDF_GetResourceInfraNode doParser(EquinoxRawData eqxRawData, AFInstance afInstance, AFSubInstance resourceOrderIns) {
        String rawData = eqxRawData.getRawDataAttribute("val");

        Param_SDF_GetResourceInfraNode param = new Param_SDF_GetResourceInfraNode();
        Gson gson = GsonPool.getGson();

        if (JsonUtils.isValidJsonFormat(rawData)) {
            param = gson.fromJson(rawData, Param_SDF_GetResourceInfraNode.class);
            param.setIsValid(true);
        }
        GsonPool.pushGson(gson);

        return param;
    }
}
