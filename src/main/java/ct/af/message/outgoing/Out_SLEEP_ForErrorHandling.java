package ct.af.message.outgoing;

import org.joda.time.DateTime;

import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.utils.AFUtils;
import ct.af.utils.Config;
import ec02.af.abstracts.AbstractAF;
import ec02.af.data.AFDataFactory;
import ec02.data.interfaces.EquinoxRawData;

public class Out_SLEEP_ForErrorHandling {
	public EquinoxRawData messageBuilder(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns)
    {
    	
        EquinoxRawData eqxRawData = AFDataFactory.createEquinoxRawData();

        //-- Invoke --//
        String invoke = new AFUtils().invokeGenerator(abstractAF,afSubIns.getSubInitInvoke(),afSubIns.getSubInitCmd(),afSubIns.getSubNextState(),afSubIns.getSubInstanceNo(),abstractAF.getEquinoxProperties().getSession());
        //eqxRawData.setInvoke(invoke);
        afSubIns.setSubInvoke(invoke);

        //-- Timeout --//
        String timeoutDt = Config.getFormatDateWithMiTz().print(new DateTime().plusSeconds(Integer.parseInt(afSubIns.getTimeSleepRetry())));
		afSubIns.setSubTimeout(timeoutDt);
		afSubIns.setSubTimeoutArray(timeoutDt);

		//-- Sub state --//
        afSubIns.setSubStateArray(afSubIns.getSubNextState());
        afSubIns.setSubCountChild(afSubIns.getSubCountChild()+1);
       
        return eqxRawData;
    }
}
