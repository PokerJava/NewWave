package ct.af.message.incoming.parameter;

import java.util.ArrayList;
import java.util.HashMap;

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
	    
	    @Path("ERDHeader/Header/")
	    @Element(name = "x")
	    
	    public Object x;
	    public Object test;
	    public Object name;
	    public Object y;
	    public Object value;
//	    ArrayList<Object> header = new ArrayList<>();
	    public String getVersion() {
	        return version;
	    }

	    public String getResultCode() {
	        return resultCode;
	    }

	    public String getResultDesc() {
	        return resultDesc;
	    }

		public Object getX() {
			return x;
		}

		public void setX(Object x) {
			this.x = x;
		}

		public Object getTest() {
			return test;
		}

		public void setTest(Object test) {
			this.test = test;
		}

		public Object getName() {
			return name;
		}

		public void setName(Object name) {
			this.name = name;
		}

		public Object getY() {
			return y;
		}

		public void setY(Object y) {
			this.y = y;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
	    
	    
	    
}
