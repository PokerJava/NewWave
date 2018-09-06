package ct.af.message.outgoing;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ct.af.enums.*;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.message.incoming.parameter.Param_IDLE_Xxx;
import ct.af.utils.Config;
import ct.af.utils.GsonPool;
import ct.af.utils.LogUtils;
import ec02.af.abstracts.AbstractAF;
import ec02.af.data.AFDataFactory;
import ec02.af.utils.AFLog;
import ec02.data.enums.EEquinoxRawData;
import ec02.data.interfaces.EquinoxRawData;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public class Out_END_Resource {
  public EquinoxRawData messageBuilder(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {
    EquinoxRawData eqxRawData = AFDataFactory.createEquinoxRawData();
    Param_IDLE_Xxx param = afSubIns.getSubParam();

    return eqxRawData;
  }


}
