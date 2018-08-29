package ct.af.instance;

import java.util.List;

public class Foreach
{
    private String name;
    private int startLinePosition;
    private int objectIndex;
    private boolean isForeachEmpty;
    private List arrayRef;

    public Foreach(String name, int startLinePosition, int objectIndex, boolean isForeachEmpty, List arrayRef)
    {
        this.name = name;
        this.startLinePosition = startLinePosition;
        this.objectIndex = objectIndex;
        this.isForeachEmpty = isForeachEmpty;
        this.arrayRef = arrayRef;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getStartLinePosition()
    {
        return startLinePosition;
    }

    public void setStartLinePosition(int startLinePosition)
    {
        this.startLinePosition = startLinePosition;
    }

    public int getObjectIndex()
    {
        return objectIndex;
    }

    public void setObjectIndex(int objectIndex)
    {
        this.objectIndex = objectIndex;
    }

    public boolean isForeachEmpty()
    {
        return isForeachEmpty;
    }

    public void setForeachEmpty(boolean foreachEmpty)
    {
        isForeachEmpty = foreachEmpty;
    }

    public List getArrayRef()
    {
        return arrayRef;
    }

    public void setArrayRef(List arrayRef)
    {
        this.arrayRef = arrayRef;
    }
}
