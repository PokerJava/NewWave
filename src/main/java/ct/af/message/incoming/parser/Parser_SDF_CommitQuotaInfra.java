package ct.af.message.incoming.parser;

import com.google.gson.Gson;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_SDF_CommitQuotaInfra;
import ct.af.utils.GsonPool;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class Parser_SDF_CommitQuotaInfra {

	public Param_SDF_CommitQuotaInfra doParser(EquinoxRawData eqxRawData, AFInstance afInstance, AFSubInstance afSubIns) {
		Param_SDF_CommitQuotaInfra param = new Param_SDF_CommitQuotaInfra();
		String rawData = eqxRawData.getRawDataAttribute("val");

		Gson gson = GsonPool.getGson();
		try {
			param = gson.fromJson(rawData, Param_SDF_CommitQuotaInfra.class);
		} catch (Exception ex) {
			AFLog.e("[Exception] can't parse in Parser_SDF_CommitQuotaInfra.");
			AFLog.e(ex);
			param.setIsReceived(true);
		}

		GsonPool.pushGson(gson);

		return param;
	}
}
