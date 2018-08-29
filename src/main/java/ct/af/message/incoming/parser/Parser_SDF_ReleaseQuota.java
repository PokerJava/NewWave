package ct.af.message.incoming.parser;

import com.google.gson.Gson;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_SDF_ReleaseQuota;
import ct.af.utils.GsonPool;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class Parser_SDF_ReleaseQuota {

	public Param_SDF_ReleaseQuota doParser(EquinoxRawData eqxRawData, AFInstance afInstance, AFSubInstance afSubIns) {
		Param_SDF_ReleaseQuota param = new Param_SDF_ReleaseQuota();
		String rawData = eqxRawData.getRawDataAttribute("val");
		Gson gson = GsonPool.getGson();
		try {
			param = gson.fromJson(rawData, Param_SDF_ReleaseQuota.class);
		} catch (Exception ex) {
			AFLog.e("[Exception] can't parse in Parser_SDF_ReleaseQuota.");
            AFLog.e(ex);
			param.setIsReceived(true);
		}

		GsonPool.pushGson(gson);

		return param;
	}
}
