package ct.af.instance;

public class If
{
    private Boolean value;
    private Boolean isProcessed;

    public If(Boolean value)
    {
        this.value = value;
        this.isProcessed = false;
    }

    public If(Boolean value, Boolean isProcessed)
    {
        this.value = value;
        this.isProcessed = isProcessed;
    }

    public Boolean getValue()
    {
        return value;
    }

    public void setValue(Boolean value)
    {
        this.value = value;
    }

    public Boolean getProcessed()
    {
        return isProcessed;
    }

    public void setProcessed(Boolean processed)
    {
        isProcessed = processed;
    }
}
