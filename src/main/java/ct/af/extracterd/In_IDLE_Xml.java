package ct.af.extracterd;

import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.message.incoming.parameter.Param_IDLE_Xml;
import ct.af.message.incoming.parser.Parser_IDLE_Xml;
import ec02.af.abstracts.AbstractAF;
import ec02.data.interfaces.EquinoxRawData;

public class In_IDLE_Xml {

	public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns,
			EquinoxRawData eqxRawData) {

		Param_IDLE_Xml paramIdleXml;

		Parser_IDLE_Xml parser = new Parser_IDLE_Xml();
		paramIdleXml = parser.doParser(abstractAF, eqxRawData, afInstance, afSubIns);
		return afSubIns;

	}
}
