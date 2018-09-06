package ct.af.extracterd;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import ct.af.enums.ERequestState;
import ct.af.enums.EResultCode;
import ct.af.enums.EStats;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.instance.SubEDR;
import ct.af.message.incoming.parameter.Param_IDLE_ResourceOrder;
import ct.af.message.incoming.parameter.Param_IDLE_Xxx;
import ct.af.message.incoming.parser.Parser_IDLE_ResourceOrder;
import ct.af.message.incoming.parser.Parser_IDLE_Xxx;
import ct.af.utils.Config;
import ct.af.utils.LogUtils;
import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.EquinoxRawData;

public class In_IDLE_Xxx {
	public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns,
            EquinoxRawData eqxRawData) {
	Param_IDLE_Xxx paramIdleXxx;
	
	Parser_IDLE_Xxx parser = new Parser_IDLE_Xxx();
	paramIdleXxx = parser.doParser(abstractAF, eqxRawData, afInstance, afSubIns);
	
	if(!paramIdleXxx.isValid())
	{
		afSubIns.setSubResultCode(EResultCode.RE40300.getResultCode());
	}
	afSubIns.setSubParam(paramIdleXxx);
	afSubIns.setState(ERequestState.PENDING.getState());
	AFLog.d("State : " + afSubIns.getState());

	return afSubIns;
	}
}
