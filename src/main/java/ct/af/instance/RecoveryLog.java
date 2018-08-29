package ct.af.instance;

import ec02.data.interfaces.IMessageBuilder;
import ec02.exception.BuilderParserException;

public class RecoveryLog implements IMessageBuilder{
	
		private String dataResponse;
		private String id = "response1";
		private String command = "write";
		private String state_type = "successreturn";
		private String param = "O_CREAT|O_APPEND";
		private String src;
		
		
		public String getDataResponse() {
			return dataResponse;
		}


		public void setDataResponse(String dataResponse) {
			this.dataResponse = dataResponse;
		}


		public String getId() {
			return id;
		}


		public void setId(String id) {
			this.id = id;
		}


		public String getCommand() {
			return command;
		}


		public void setCommand(String command) {
			this.command = command;
		}


		public String getState_type() {
			return state_type;
		}


		public void setState_type(String state_type) {
			this.state_type = state_type;
		}


		public String getParam() {
			return param;
		}


		public void setParam(String param) {
			this.param = param;
		}


		public String getSrc() {
			return src;
		}


		public void setSrc(String src) {
			this.src = src;
		}


		@Override
		public String buildMessage() throws BuilderParserException {
			String rtn = "<state id=\""+id+"\" command=\""+command+"\" state_type=\""+state_type+"\" param=\""+param+"\" src=\"" + src + "\" ><data decode=\"no\" >" + dataResponse + "\r\n</data></state>";
			return rtn;
		}
		
	}

