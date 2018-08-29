package ct.af.core.manager;

import ct.af.enums.ESubState;
import ct.af.instance.AFInstance;
import ct.af.instance.AFSubInstance;
import ct.af.substate.Do_Internal_FinalResult;
import ct.af.utils.Config;
import ec02.af.abstracts.AbstractAF;
import ec02.af.data.AFDataFactory;
import ec02.af.interfaces.IAFState;
import ec02.af.utils.AFLog;
import ec02.data.interfaces.ECDialogue;
import ec02.data.interfaces.EquinoxPropertiesAF;
import ec02.data.interfaces.EquinoxRawData;
import ec02.data.interfaces.InstanceData;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class W_SDF_CommitQuotaInfra implements IAFState {
    @Override
    public ECDialogue doAction (AbstractAF abstractAF, InstanceData ec02Ins, List<EquinoxRawData> equinoxRawDatas) {

//        AFLog.d("==================================================================================");
//        AFLog.d("     Start and Set mainTimeStampIncoming : W_SDF_CommitQuotaInfra");
//        AFLog.d("==================================================================================");

        AFInstance afInstance = (AFInstance) ec02Ins.getObject();
        DateTimeFormatter formatDateWithMiTz = Config.getFormatDateWithMiTz();

        DateTime timeStampIn = new DateTime();
        afInstance.setMainTimeStampIncoming(formatDateWithMiTz.print(timeStampIn));

        AFLog.d("incomingTimeStamp : "+formatDateWithMiTz.print(timeStampIn));


//        AFLog.d("==================================================================================");
        AFLog.d("     For Loop EquinoxRawData Size : "+equinoxRawDatas.size());
//        AFLog.d("==================================================================================");

        for(EquinoxRawData eqxRawData : equinoxRawDatas) {

            AFLog.d("##### eqxRawData @ "+eqxRawData.getInvoke()+" #####");
            ExtractController extractController = new ExtractController();
            AFSubInstance afSubIns = extractController.checkStateByInvoke(abstractAF, afInstance, eqxRawData);

            if(afSubIns.getStatsIn() != null) {
                abstractAF.getEquinoxUtils().incrementStats(afSubIns.getStatsIn().getStatName());
                afSubIns.setStatsIn(null);
                if(afSubIns.getStatsExeTime() != null) {
            		abstractAF.getEquinoxUtils().incrementStats(afSubIns.getStatsExeTime().getStatName());
            		 afSubIns.setStatsExeTime(null);
            	}
            }

        }


//        AFLog.d("==================================================================================");
        AFLog.d("     For Loop SubInstance Size : "+afInstance.getMainSubInstanceSize());
        for(String insNo:afInstance.getMainSubInstance().keySet())
        {
            AFLog.d("Key: " + insNo);
        }
//        AFLog.d("==================================================================================");

        HashMap<String, AFSubInstance> subInstanceHashMap = afInstance.getMainSubInstance();
        SortedSet<String> subInstancekeys = new TreeSet<>(subInstanceHashMap.keySet());

        for (String key : subInstancekeys) {

            AFLog.d("##### Sub Instance Key : "+key+" #####");
            AFLog.d("MainCountWait    : " + afInstance.getMainCountWait());
            AFLog.d("MainCountProcess : " + afInstance.getMainCountProcess());


            AFSubInstance afSubInstance = subInstanceHashMap.get(key);

            AFLog.d("invoke size : "+afSubInstance.getSubInvoke().size());

            if(afSubInstance.getSubInvoke().size() == 0 || afSubInstance.getSubCountChild() > 0) {

                if(afSubInstance.getSubInvoke().size() == 0 ) {
                    new DoBusinessController().doBusinessLogic(abstractAF,afInstance,afSubInstance);

                    new ConstructController().constructGateway(abstractAF, afInstance, afSubInstance);

                }
            }

            AFLog.d("MainCountWait    : " + afInstance.getMainCountWait());
            AFLog.d("MainCountProcess : " + afInstance.getMainCountProcess());
            AFLog.d("##### End Sub Instance Key : "+key+" #####");

        }

        AFLog.d("mainCountWait: " + Integer.toString(afInstance.getMainCountWait()));
        AFLog.d("mainCountProcess: " + Integer.toString(afInstance.getMainCountProcess()));


//        AFLog.d("==================================================================================");
//        AFLog.d("     Clear END Instances");
//        AFLog.d("==================================================================================");

        for (String key : subInstancekeys) {

            AFSubInstance afSubIns = subInstanceHashMap.get(key);
            if(afSubIns.getSubNextState().equals(ESubState.END.toString())
                    && afSubIns.getSubCountChild() == 0) {
                subInstanceHashMap.remove(key);

            }
        }

        if(afInstance.getMainCountProcess() == 0 &&
                afInstance.getMainCountWait() == 1 &&
                !afInstance.getMainResourceOrderInsNo().equals(""))
        {
            AFSubInstance afSubIns = afInstance.getMainSubInstance(afInstance.getMainResourceOrderInsNo());
            Do_Internal_FinalResult doFinalResult = new Do_Internal_FinalResult();
            doFinalResult.doBusinessLogic(abstractAF, afInstance, afSubIns);
        }


//        AFLog.d("==================================================================================");
//        AFLog.d("     Set EqxTimeout and EqxState");
//        AFLog.d("==================================================================================");
        String eqxState = new TimeoutManager().setEqxTimeout(abstractAF, afInstance, timeStampIn);


        EquinoxPropertiesAF newEqxProp = AFDataFactory.createEquinoxProperties();
        newEqxProp.setState(eqxState);

        AFLog.d("[NEXT STATE] : " + eqxState);

        newEqxProp.setTimeout(afInstance.getMainTimeout());

        AFLog.d("[TIMEOUT] : " + afInstance.getMainTimeout());

        return AFDataFactory.createECDialogue(newEqxProp);
    }


    @Override
    public ECDialogue doAged(AbstractAF arg0, InstanceData arg1, List<EquinoxRawData> arg2) {
        return null;
    }

    @Override
    public ECDialogue doShutdown(AbstractAF arg0, InstanceData arg1, List<EquinoxRawData> arg2) {
        return null;
    }

    @Override
    public ECDialogue doTimeout(AbstractAF abstractAF, InstanceData ec02Ins, List<EquinoxRawData> equinoxRawDatas) {
//        AFLog.d("==================================================================================");
//        AFLog.d("     Start and Set mainTimeStampIncoming : W_SDF_CommitQuotaInfra");
//        AFLog.d("==================================================================================");

        AFInstance afInstance = (AFInstance) ec02Ins.getObject();
        DateTimeFormatter formatDateWithMiTz = Config.getFormatDateWithMiTz();

        DateTime timeStampIn = new DateTime();
        afInstance.setMainTimeStampIncoming(formatDateWithMiTz.print(timeStampIn));

        AFLog.d("incomingTimeStamp : "+formatDateWithMiTz.print(timeStampIn));


        boolean isRetTimeout = abstractAF.getEquinoxProperties().isTimeout();
        AFLog.d("isTimeout (ret=4) : "+isRetTimeout);
        AFLog.d("Call TimeoutManager.recvTimeoutManager()");
        TimeoutManager timeoutManager = new TimeoutManager();
        timeoutManager.recvTimeoutManager(abstractAF, afInstance, timeStampIn);


//        AFLog.d("==================================================================================");
        AFLog.d("     For Loop EquinoxRawData Size : "+equinoxRawDatas.size());
//        AFLog.d("==================================================================================");

        for(EquinoxRawData eqxRawData : equinoxRawDatas) {

            AFLog.d("##### eqxRawData @ "+eqxRawData.getInvoke()+" #####");
            ExtractController extractController = new ExtractController();
            AFSubInstance afSubIns = extractController.checkStateByInvoke(abstractAF, afInstance, eqxRawData);

            if(afSubIns.getStatsIn() != null) {
                abstractAF.getEquinoxUtils().incrementStats(afSubIns.getStatsIn().getStatName());
                afSubIns.setStatsIn(null);
            }

        }


//        AFLog.d("==================================================================================");
        AFLog.d("     For Loop SubInstance Size : "+afInstance.getMainSubInstanceSize());
        for(String insNo:afInstance.getMainSubInstance().keySet())
        {
            AFLog.d("Key: " + insNo);
        }
//        AFLog.d("==================================================================================");

        HashMap<String, AFSubInstance> subInstanceHashMap = afInstance.getMainSubInstance();
        SortedSet<String> subInstancekeys = new TreeSet<>(subInstanceHashMap.keySet());

        for (String key : subInstancekeys) {

            AFLog.d("##### Sub Instance Key : "+key+" #####");
            AFLog.d("MainCountWait    : " + afInstance.getMainCountWait());
            AFLog.d("MainCountProcess : " + afInstance.getMainCountProcess());


            AFSubInstance afSubInstance = subInstanceHashMap.get(key);

            AFLog.d("invoke size : "+afSubInstance.getSubInvoke().size());

            if(afSubInstance.getSubInvoke().size() == 0 || afSubInstance.getSubCountChild() > 0) {

                if(afSubInstance.getSubInvoke().size() == 0 ) {
                    new DoBusinessController().doBusinessLogic(abstractAF,afInstance,afSubInstance);

                    new ConstructController().constructGateway(abstractAF, afInstance, afSubInstance);

                }
            }

            AFLog.d("MainCountWait    : " + afInstance.getMainCountWait());
            AFLog.d("MainCountProcess : " + afInstance.getMainCountProcess());
            AFLog.d("##### End Sub Instance Key : "+key+" #####");

        }

        AFLog.d("mainCountWait: " + Integer.toString(afInstance.getMainCountWait()));
        AFLog.d("mainCountProcess: " + Integer.toString(afInstance.getMainCountProcess()));


//        AFLog.d("==================================================================================");
//        AFLog.d("     Clear END Instances");
//        AFLog.d("==================================================================================");

        for (String key : subInstancekeys) {

            AFSubInstance afSubIns = subInstanceHashMap.get(key);
            if(afSubIns.getSubNextState().equals(ESubState.END.toString())
                    && afSubIns.getSubCountChild() == 0) {
                subInstanceHashMap.remove(key);

            }
        }

        if(afInstance.getMainCountProcess() == 0 &&
                afInstance.getMainCountWait() == 1 &&
                !afInstance.getMainResourceOrderInsNo().equals(""))
        {
            AFSubInstance afSubIns = afInstance.getMainSubInstance(afInstance.getMainResourceOrderInsNo());
            Do_Internal_FinalResult doFinalResult = new Do_Internal_FinalResult();
            doFinalResult.doBusinessLogic(abstractAF, afInstance, afSubIns);
        }


//        AFLog.d("==================================================================================");
//        AFLog.d("     Set EqxTimeout and EqxState");
//        AFLog.d("==================================================================================");
        String eqxState = new TimeoutManager().setEqxTimeout(abstractAF, afInstance, timeStampIn);


        EquinoxPropertiesAF newEqxProp = AFDataFactory.createEquinoxProperties();
        newEqxProp.setState(eqxState);

        AFLog.d("[NEXT STATE] : " + eqxState);

        newEqxProp.setTimeout(afInstance.getMainTimeout());

        AFLog.d("[TIMEOUT] : " + afInstance.getMainTimeout());

        return AFDataFactory.createECDialogue(newEqxProp);
    }
}
