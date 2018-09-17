package ct.af.extracterd;

import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_Pgw;
import ct.af.message.incoming.parser.Parser_IDLE_Pgw;
import ec02.af.abstracts.AbstractAF;
import ec02.data.interfaces.EquinoxRawData;

public class In_IDLE_Pgw {
	public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns,
            EquinoxRawData eqxRawData) {
//	Param_IDLE_Xxx paramIdleXxx;
//	Parser_IDLE_Xxx parser = new Parser_IDLE_Xxx();
//	paramIdleXxx = parser.doParser(abstractAF, eqxRawData, afInstance, afSubIns);
//	
//	if(!paramIdleXxx.isValid())
//	{
//		afSubIns.setSubResultCode(EResultCode.RE40300.getResultCode());
//	}
//	afSubIns.setSubParam(paramIdleXxx);
//	afSubIns.setState(ERequestState.PENDING.getState());
//	AFLog.d("State : " + afSubIns.getState());
	Param_IDLE_Pgw paramPgw;
	Parser_IDLE_Pgw parser = new Parser_IDLE_Pgw();
	paramPgw = parser.doParser(abstractAF, eqxRawData, afInstance, afSubIns);
	
	return afSubIns;
	
	
	}

}
