package ct.af.message.incoming.parser;

import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_Pgw;
import ct.af.message.incoming.parameter.Param_IDLE_Xxx;
import ec02.af.abstracts.AbstractAF;
import ec02.data.interfaces.EquinoxRawData;

public class Parser_IDLE_Pgw {
	public Param_IDLE_Pgw doParser(AbstractAF abstractAF, EquinoxRawData eqxRawData, AFInstance afInstance,
			AFSubInstance afSubIns) {
		Param_IDLE_Pgw paramPgw = new Param_IDLE_Pgw();
		
		return paramPgw;
	}
}
