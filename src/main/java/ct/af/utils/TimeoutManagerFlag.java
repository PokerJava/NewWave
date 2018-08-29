package ct.af.utils;

import ec02.af.utils.AFLog;

public class TimeoutManagerFlag {
	
	public boolean incrementMaxActive(Boolean Flag) {

		if (Config.getCountMaxActive() != Config.getREQUEST_MAXACTIVE() && Flag) {
			Config.incrementMaxActive();
			AFLog.d("IncrementMaxActive: "+ Config.getCountMaxActive());
			return true;
		}else if(!Flag){
			return true;
		}
		else{
			return false;
		}
			
	}

	public void decrementMaxActive(Boolean Flag) {

		if (Flag) {
			Config.decrementMaxActive();
			AFLog.d("DecrementMaxActive: "+ Config.getCountMaxActive());
		} 
	}

}
