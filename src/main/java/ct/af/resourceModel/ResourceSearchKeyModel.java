package ct.af.resourceModel;

public class ResourceSearchKeyModel
{
    private String searchKey;
    private String resourceIndex;

    public String getSearchKey()
    {
        return searchKey;
    }

    public void setSearchKey(String searchKey)
    {
        this.searchKey = searchKey;
    }

    public String getResourceIndex()
    {
        return resourceIndex;
    }

    public void setResourceIndex(String resourceIndex)
    {
        this.resourceIndex = resourceIndex;
    }

    public ResourceSearchKeyModel trim()
    {
        searchKey = searchKey.trim();
        resourceIndex = resourceIndex.trim();
        return this;
    }
}
