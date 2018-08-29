package ct.af.resourceModel;

public class ResourceNeIdRouting
{
    private String neType;
    private String start;
    private String end;
    private String neId;
    private String partyName;
    private String productVersion;

    public ResourceNeIdRouting(String neType, String partyName)
    {
        this.neType = neType;
        this.partyName = partyName;
    }

    public ResourceNeIdRouting()
    {}

    public String getNeType()
    {
        return neType;
    }

    public void setNeType(String neType)
    {
        this.neType = neType;
    }

    public String getStart()
    {
        return start;
    }

    public void setStart(String start)
    {
        this.start = start;
    }

    public String getEnd()
    {
        return end;
    }

    public void setEnd(String end)
    {
        this.end = end;
    }

    public String getNeId()
    {
        return neId;
    }

    public void setNeId(String neId)
    {
        this.neId = neId;
    }

    public String getPartyName()
    {
        return partyName;
    }

    public void setPartyName(String partyName)
    {
        this.partyName = partyName;
    }

    public String getProductVersion()
    {
        return productVersion;
    }

    public void setProductVersion(String productVersion)
    {
        this.productVersion = productVersion;
    }

    @Override
    public String toString()
    {
        return "ResourceNeIdRouting{" + "neType='" + neType + '\'' + ", start='" + start + '\'' + ", end='" + end + '\'' + ", neId='" + neId + '\'' + ", partyName='" + partyName + '\'' + ", productVersion='" + productVersion + '\'' + '}';
    }
}
