package ct.af.extracterd;

import java.util.ArrayList;
import java.util.TreeMap;

import ct.af.enums.EResultCode;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_Dgw;
import ct.af.message.incoming.parameter.Param_IDLE_Pgw;
import ct.af.message.incoming.parser.Parser_IDLE_Dgw;
import ct.af.message.incoming.parser.Parser_IDLE_Pgw;
import ct.af.utils.DiameterUtils;
import ct.af.utils.ValidateUtils;
import ec02.af.abstracts.AbstractAF;
import ec02.data.interfaces.EquinoxRawData;

public class In_IDLE_Dgw {
	public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns,
            EquinoxRawData eqxRawData) {

	Param_IDLE_Dgw paramDgw;
	Parser_IDLE_Dgw parser = new Parser_IDLE_Dgw();
	paramDgw = parser.doParser(abstractAF, eqxRawData, afInstance, afSubIns);
	
	return afSubIns;
	
	
	}
}
