package ct.af.instance;

import java.util.HashMap;

public class AFInstance {
    /* DEFAULT ELEMENTS */
    private int mainSubSessionRunningNo = 0;
    private String mainTimeStampIncoming;
    private String mainTimeStampOut;
    private int mainCountWait = 0;
    private int mainCountProcess = 0;
    private String mainTimeout = "";
    /* END DEFAULT ELEMENTS */

    private boolean mainHasError = false;
    private boolean mainIsLock = false;
    private boolean mainIsFinishFlow = false;
    private String mainResourceOrderInsNo = "";
    private HashMap<String, AFSubInstance> mainSubInstance = new HashMap<>();

    public String getMainTimeout()
    {
        return mainTimeout;
    }

    public void setMainTimeout(String mainTimeout)
    {
        this.mainTimeout = mainTimeout;
    }

    public String getMainResourceOrderInsNo() {
        return mainResourceOrderInsNo;
    }

    public void setMainResourceOrderInsNo(String mainResourceOrderInsNo) {
        this.mainResourceOrderInsNo = mainResourceOrderInsNo;
    }

    public int getMainSubSessionRunningNo() {
        return mainSubSessionRunningNo;
    }

    public void countSubSessionRunningNo() {
        if (this.mainSubSessionRunningNo < 50000) {
            this.mainSubSessionRunningNo++;
        } else {
            this.mainSubSessionRunningNo = 0;
        }
    }

    public String getMainTimeStampIncoming() {
        return mainTimeStampIncoming;
    }

    public void setMainTimeStampIncoming(String mainTimeStampIncoming) {
        this.mainTimeStampIncoming = mainTimeStampIncoming;
    }

    public int getMainCountWait() {
        return mainCountWait;
    }

    public void incrementMainCountWait(){
        this.mainCountWait++;
    }

    public void decrementMainCountWait() {
        if (this.mainCountWait > 0) {
            this.mainCountWait--;
        } else {
            this.mainCountWait = 0;
        }
    }

    public int getMainCountProcess() {
        return mainCountProcess;
    }

    public void incrementMainCountProcess(){
        this.mainCountProcess++;
    }

    public void decrementMainCountProcess() {
        if(this.mainCountProcess > 0) {
            this.mainCountProcess--;
        } else {
            this.mainCountProcess = 0;
        }

    }

    public String getMainTimeStampOut() {
        return mainTimeStampOut;
    }

    public void setMainTimeStampOut(String mainTimeStampOut) {
        this.mainTimeStampOut = mainTimeStampOut;
    }

    public boolean getMainHasError() {
        return mainHasError;
    }

    public void setMainHasError(boolean mainHasError) {
        this.mainHasError = mainHasError;
    }

    public boolean getMainIsLock() {
        return mainIsLock;
    }

    public void setMainIsLock(boolean mainIsLock) {
        this.mainIsLock = mainIsLock;
    }

    public boolean getMainIsFinishFlow() {
        return mainIsFinishFlow;
    }

    public void setMainIsFinishFlow(boolean mainIsFinishFlow) {
        this.mainIsFinishFlow = mainIsFinishFlow;
    }


    //HashMap Method//
    public void putMainSubInstance(String key,AFSubInstance value) {
        this.mainSubInstance.put(key, value);
    }

    public HashMap<String, AFSubInstance> getMainSubInstance() {
        return this.mainSubInstance;
    }

    public AFSubInstance getMainSubInstance(String key) {
        return this.mainSubInstance.get(key);
    }

    public int getMainSubInstanceSize() {
        return this.mainSubInstance.size();
    }
    //End HashMap Method//


}
