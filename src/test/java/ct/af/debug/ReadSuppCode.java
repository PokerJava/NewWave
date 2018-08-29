package ct.af.debug;

import java.util.List;

import com.google.gson.Gson;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import ct.af.instance.AFInstance;
import ct.af.resourceModel.SuppCode;
import ct.af.utils.Config;
import ct.af.utils.GsonPool;
import ct.af.utils.Zip;
import ec02.af.abstracts.AbstractAF;
import ec02.af.data.AFDataFactory;
import ec02.af.exception.ActionProcessException;
import ec02.af.utils.AFLog;
import ec02.data.enums.EStdLogDelimiter;
import ec02.data.interfaces.ECDialogue;
import ec02.data.interfaces.EquinoxPropertiesAF;
import ec02.data.interfaces.EquinoxRawData;
import ec02.data.interfaces.InstanceData;
import ec02.data.interfaces.StdCDRData;
import ec02.data.interfaces.StdEDRFactory;

public class ReadSuppCode extends AbstractAF {
    @Override
    public ECDialogue actionProcess(EquinoxPropertiesAF equinoxPropertiesAF, List<EquinoxRawData> list, InstanceData instanceData) throws ActionProcessException {
        AFLog.d("Lib Version : " + Config.getLibVersion());
        AFLog.d("[Start Process]");
        /* extract instance */
        AFInstance afInstance = null;

        String instanceString = instanceData.toString();

        if (instanceString.isEmpty()) {
            afInstance = new AFInstance();
        } else {
            AFLog.d("[Extract Instance]");
            afInstance = decodeInstance(instanceString);

            if (afInstance == null) {
                afInstance = new AFInstance();
            }
        }

        instanceData.setObject(afInstance);
        /* main */
        AFLog.d("[CURRENT STATE] : " + equinoxPropertiesAF.getState());

        if (Config.isHasReloadConfig()) {
            Config.loadConfig(this);
        }
        
        List<SuppCode> suppCodeList = Config.getResourceMasterHashMap().get("PromotionManagement_2_001").getSuppCodesList();
        System.out.println(suppCodeList);
		
        //String eqxState = new IncomingManager().start((AbstractAF) this, ec02Ins, list);

        EquinoxPropertiesAF newEqxProp = AFDataFactory.createEquinoxProperties();
        //newEqxProp.setState(eqxState);

        //AFLog.d("[NEXT STATE] : " + eqxState);

        newEqxProp.setTimeout(afInstance.getMainTimeout());

        AFLog.d("[TIMEOUT] : " + afInstance.getMainTimeout());

        /* compose instance */
        afInstance = (AFInstance) instanceData.getObject();
        String encodeString = "";

        try {
            encodeString = encodeInstance(afInstance);
        }
        catch (Exception e) {
            AFLog.e("[Exception] composeInstance error");
            AFLog.e(e);
        }

        this.getEquinoxUtils().setInstanceMessage(encodeString);

        return AFDataFactory.createECDialogue(newEqxProp);
    }

    @Override
    public boolean verifyAFConfiguration(String s) {
        AFLog.d("Starting Verify Config");
        boolean x = Config.verifyConfig(this);
        AFLog.d("Finish Verify Config");
        return x;
    }

    @Override
    public StdCDRData initializedCallDetailRecord() {
        return null;
    }

    @Override
    public StdEDRFactory initializedEventDetailRecord() {
        StdEDRFactory ef = AFDataFactory.createEDRFactory();

        ef.setDelimiter(EStdLogDelimiter.PIPE);
        ef.setApplicationName("PGZ");
        ef.setComponentName("PGZ");

        return ef;
    }

    private String encodeInstance(AFInstance instance) {
        String encodeString = "";
        try {
            Gson gson = GsonPool.getGson();
            String str = gson.toJson(instance);
            GsonPool.pushGson(gson);

            byte[] bytes = str.getBytes();
            byte[] zipBytes = Zip.compressBytes(bytes);
            encodeString = Base64.encode(zipBytes);
        }
        catch (Exception e) {
            AFLog.e("[Exception] encodeInstance error");
            AFLog.e(e);
        }

        return encodeString;
    }

    private AFInstance decodeInstance(String instance) {
        AFInstance afInstance = null;
        try {
            byte[] simpleString = Base64.decode(instance);
            byte[] unZipString = Zip.extractBytes(simpleString);
            Gson gson = GsonPool.getGson();
            afInstance = gson.fromJson(new String(unZipString), AFInstance.class);
            GsonPool.pushGson(gson);
        } catch (Exception e) {
            AFLog.e("[Exception] decodeInstance error");
            AFLog.e(e);
        }
        return afInstance;
    }
}
