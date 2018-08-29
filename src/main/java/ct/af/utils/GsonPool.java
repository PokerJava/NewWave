package ct.af.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ct.af.message.incoming.parameter.AbsClientParam;
import java.util.ArrayList;
import java.util.List;

public class GsonPool {
    private static final List<Gson> gsons = new ArrayList<>();
    private static final List<Gson> prettyGsons = new ArrayList<>();


    public static synchronized Gson getGson()
    {
        if (!gsons.isEmpty())
        {
            return gsons.remove(0);
        }
        else
        {
            return new GsonBuilder().registerTypeAdapter(AbsClientParam.class, new GsonInterfaceAdapter<AbsClientParam>()).create();
        }
    }

    public static synchronized void pushGson(Gson gson)
    {
        gsons.add(gson);
    }

    public static synchronized Gson getPrettyGsons() {
        if (!prettyGsons.isEmpty()) {
            return prettyGsons.remove(0);
        } else {
            return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        }
    }

    public static synchronized void pushPrettyGsons(Gson prettyGson) {
        prettyGsons.add(prettyGson);
    }
}
