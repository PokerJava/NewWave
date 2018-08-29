package ct.af.message.outgoing;

import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.utils.AFUtils;
import ct.af.utils.Config;
import ec02.af.abstracts.AbstractAF;
import ec02.af.data.AFDataFactory;
import ec02.data.interfaces.EquinoxRawData;
import org.joda.time.DateTime;


/*
*   this class use for prevent send endResource(line7) and hrz_postresult(line10) at same time
* */

public class Out_SLEEP_SendResult {
  public EquinoxRawData messageBuilder(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns) {
    EquinoxRawData eqxRawData = AFDataFactory.createEquinoxRawData();

    //-- Invoke --//
    String invoke = new AFUtils().invokeGenerator(abstractAF,afSubIns.getSubInitInvoke(),afSubIns.getSubInitCmd(),afSubIns.getSubNextState(),afSubIns.getSubInstanceNo(),abstractAF.getEquinoxProperties().getSession());
    afSubIns.setSubInvoke(invoke);

    //-- Timeout --//
    String timeoutDt = Config.getFormatDateWithMiTz().print(new DateTime().plusSeconds(1));
    afSubIns.setSubTimeout(timeoutDt);
    afSubIns.setSubTimeoutArray(timeoutDt);

    //-- Sub state --//
    afSubIns.setSubStateArray(afSubIns.getSubNextState());
    afSubIns.setSubCountChild(afSubIns.getSubCountChild()+1);

    return eqxRawData;
  }
}
