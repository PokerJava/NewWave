package ct.af.extracterd;

import ct.af.enums.ECommand;
import ct.af.enums.EResultCode;
import ct.af.enums.EStats;
import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.utils.AFUtils;
import ct.af.utils.Config;
import ec02.af.abstracts.AbstractAF;
import ec02.data.interfaces.EquinoxRawData;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public class In_IDLE_Purge {
    public AFSubInstance extractRawData(AbstractAF abstractAF, AFInstance afInstance, AFSubInstance afSubIns, EquinoxRawData eqxRawData) {

        DateTimeFormatter formatDateWithMiTz = Config.getFormatDateWithMiTz();

        DateTime timeStampIn = formatDateWithMiTz.parseDateTime(afInstance.getMainTimeStampIncoming());

        Boolean isValid = false;
        EResultCode resultCode = EResultCode.RE40400;
        EResultCode internalCode = EResultCode.RE40400;
        EStats statIn = EStats.APP_RECV_UNKNOWN_REQUEST;

        afSubIns = new AFSubInstance();
        afSubIns.setSubInitTimeStampIn(formatDateWithMiTz.print(timeStampIn));

        afSubIns.setSubInstanceNo(new AFUtils().subInsNoGenerator(afInstance, ECommand.UNKNOWN.toString()));
        afSubIns.setSubInitOrig(eqxRawData.getOrig());
        afSubIns.setSubInitInvoke(eqxRawData.getInvoke());
        afSubIns.setSubInitURL(eqxRawData.getRawDataAttribute("url"));
        afSubIns.setSubInitEvent(statIn.getStatName());
        afSubIns.setSubInitCmd(ECommand.UNKNOWN.getCommand());
        afSubIns.setSubInitMethod(eqxRawData.getRawDataAttribute("method"));
        afSubIns.setSubTimeout(formatDateWithMiTz.print(timeStampIn.plusYears(1)));

        afSubIns.setSubCurrentState(ESubState.IDLE_Purge.toString());
        afSubIns.setSubNextState(ESubState.END.toString());
        afSubIns.setSubControlState(ESubState.IDLE_Purge.toString());

        afSubIns.setSubResultCode(resultCode.getResultCode());
        afSubIns.setSubInternalCode(internalCode.getResultCode());
        afSubIns.setSubIsValid(isValid);
        //-- Stats --//
        afSubIns.setStatsIn(statIn);

        afInstance.putMainSubInstance(afSubIns.getSubInstanceNo(), afSubIns);
        return afSubIns;
    }
}