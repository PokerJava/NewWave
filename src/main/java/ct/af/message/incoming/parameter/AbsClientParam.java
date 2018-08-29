package ct.af.message.incoming.parameter;


public class AbsClientParam {
    boolean isReceived = false;
    boolean isNotMissing = false;
    boolean isValid = false;

    public boolean getIsReceived() {
        return isReceived;
    }
    public void setIsReceived(boolean isReceived) {
        this.isReceived = isReceived;
    }
    public boolean getIsNotMissing() {
        return isNotMissing;
    }
    public void setIsNotMissing(boolean isNotMissing) {
        this.isNotMissing = isNotMissing;
    }
    public boolean getIsValid() {
        return isValid;
    }
    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }
}
