package ct.af.message.incoming.parameter;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;

public class Param_IDLE_Xml {

		@Path("Envelope/Body/ChangeSubIdentityResultMsg/ResultHeader/")
	    @Element(name = "Version" ,required = false)
	    private String version;

	    @Path("Envelope/Body/ChangeSubIdentityResultMsg/ResultHeader/")
	    @Element(name = "ResultCode")
	    private String resultCode;

	    @Path("Envelope/Body/ChangeSubIdentityResultMsg/ResultHeader/")
	    @Element(name = "ResultDesc" ,required = false)
	    private String resultDesc;

	    public String getVersion() {
	        return version;
	    }

	    public String getResultCode() {
	        return resultCode;
	    }

	    public String getResultDesc() {
	        return resultDesc;
	    }
}
