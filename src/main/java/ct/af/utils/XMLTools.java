package ct.af.utils;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import ec02.af.utils.AFLog;

public class XMLTools {

	 private static String stringReplace(String rawDataMsg)
	    {
	        rawDataMsg = rawDataMsg.replace("&lt;?xml version=&apos;1.0&apos; encoding=&apos;UTF-8&apos;?&gt;", "");
	        rawDataMsg = rawDataMsg.replace("&lt;?xml version=&apos;1.0&apos; encoding=&apos;UTF-8&apos;?&gt;", "");
	        rawDataMsg = rawDataMsg.replace("&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;", "");
	        rawDataMsg = rawDataMsg.replace("&lt;?xml version=&quot;1.0&quot; encoding=&quot;tis-620&quot; ?&gt;", "");

	        // Insert CDATA
	        rawDataMsg = rawDataMsg.replace("&lt;ResultDesc xmlns=&quot;http://www.huawei.com/bme/cbsinterface/common&quot;&gt;",
	                "&lt;ResultDesc xmlns=&quot;http://www.huawei.com/bme/cbsinterface/common&quot;&gt;&lt;![CDATA[");
	        rawDataMsg = rawDataMsg.replace("&lt;/ResultDesc&gt;","]]&gt;&lt;/ResultDesc&gt;");

	        rawDataMsg = rawDataMsg.replace("&lt;cbs:ResultDesc&gt;",
	                "&lt;cbs:ResultDesc&gt;&lt;![CDATA[");
	        rawDataMsg = rawDataMsg.replace("&lt;/cbs:ResultDesc&gt;","]]&gt;&lt;/cbs:ResultDesc&gt;");

	        return rawDataMsg;
	    }
	
//	   public static Object getParseObject(String rawDataMsg, Class aClass)
//	    {
//	        rawDataMsg = stringReplace(rawDataMsg);
//
//	        Object parsedObject = null;
//	        Serializer serializer = ParserPool.getPersister();
//	        try
//	        {
//	            ERDContainer container = serializer.read(ERDContainer.class,"<xml>" + rawDataMsg + "</xml>", false);
//
//	            parsedObject = serializer.read(aClass, "<xml>" + container.getData().getValue() + "</xml>", false);
//	        }
//	        catch (Exception e) {
//	            if(!isTest)
//	            {
//	                AFLog.e("[Exception] Error invalid ERDContainer");
//	                AFLog.e(e.getMessage());
//	                AFLog.d(e);
//	            }
//	            else
//	            {
//	                e.printStackTrace();
//	            }
//
//	            try
//	            {
//	                parsedObject = aClass.newInstance();
//	            }
//	            catch (Exception e2)
//	            {
//	                AFLog.e("FATAL ERROR: Cannot instantiate blank object when there's an error on parsing.");
//	                AFLog.e(e2);
//	            }
//	        }
//	        ParserPool.pushPersister((Persister) serializer);
//	        return parsedObject;
//	    }
}
