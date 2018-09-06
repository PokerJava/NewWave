package ct.af.utils;

import org.simpleframework.xml.core.Persister;

import java.util.ArrayList;


public final class ParserPool {
    public static final ArrayList<Persister> xmlPersisters = new ArrayList<>();

    public static synchronized Persister getPersister()
    {
        if (!xmlPersisters.isEmpty())
        {
            return xmlPersisters.remove(0);
        }
        else
        {
            return new Persister();
        }
    }

    public static synchronized void pushPersister(Persister parser)
    {
        xmlPersisters.add(parser);
    }
}
