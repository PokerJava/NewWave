package ct.af.resourceModel;

import java.util.HashMap;

public class ResourceSearchKeyNode
{
    public ResourceSearchKeyNode(String nodeName, boolean isChild)
    {
        this.name = nodeName;
        this.isChild = isChild;
    }

    public ResourceSearchKeyNode(String nodeName, boolean isChild, String nodeStringValue)
    {
        this.name = nodeName;
        this.isChild = isChild;
        this.stringValue = nodeStringValue;
    }

    public ResourceSearchKeyNode(String nodeName, boolean isChild, HashMap<String, ResourceSearchKeyNode> nodes)
    {
        this.name = nodeName;
        this.isChild = isChild;
        this.nodesValue = nodes;
    }

    private String name;
    private boolean isChild;
    private String stringValue = null;
    private HashMap<String, ResourceSearchKeyNode> nodesValue = null;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isChild()
    {
        return isChild;
    }

    public void setChild(boolean child)
    {
        isChild = child;
    }

    public String getStringValue()
    {
        return stringValue;
    }

    public void setStringValue(String stringValue)
    {
        this.stringValue = stringValue;
    }

    public HashMap<String, ResourceSearchKeyNode> getNodesValue()
    {
        return nodesValue;
    }

    public void setNodesValue(HashMap<String, ResourceSearchKeyNode> nodesValue)
    {
        this.nodesValue = nodesValue;
    }
}
