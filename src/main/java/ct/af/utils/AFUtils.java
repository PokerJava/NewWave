package ct.af.utils;

import ct.af.instance.AFInstance;
import ec02.af.abstracts.AbstractAF;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import java.util.Random;

public class AFUtils {
    public String subInsNoGenerator(AFInstance afInstance, String command){
        int newSubSessionRunNo = afInstance.getMainSubSessionRunningNo();
        afInstance.countSubSessionRunningNo();
        return String.format("%02d",newSubSessionRunNo)+command.toUpperCase();
    }

    public String requestIdGenerator(){
        int newRequestId = RequestIdUtils.getAndCountRequestIdRunningNo();
        String hostName = Config.getPgzServerName();
        DateTimeFormatter requestIdDate = Config.getRequestIdDate();
        DateTime timeStampOut = new DateTime();

        return hostName+"-"+requestIdDate.print(timeStampOut)+String.format("%04d", newRequestId);
    }

    public String invokeGenerator(AbstractAF abstractAF, String initInvoke , String initCommand , String nextState, String subInstanceNo) {
    	return invokeGenerator(abstractAF, initInvoke, initCommand, nextState, subInstanceNo, "");    	
    }
    
    
    public String invokeGenerator(AbstractAF abstractAF, String initInvoke , String initCommand , String nextState, String subInstanceNo, String taskNo) {
		
//		long randomNumber = (long)(9000 * random.nextDouble());
//	    int randomKey = (int)(randomNumber + 1000);
	   

		String invoke = "";
		
        if (Config.isRandomInvoke()) {
            // generate invoke on server
            Random random = new Random();
            String randomKey = String.format("%05d", random.nextInt(100000));
            //invoke = initInvoke+"."+abstractAF.getEquinoxProperties().getApplicationName()+"."+initCommand+".W_"+nextState+"."+subInstanceNo+"."+randomKey+"@"+msisdn;
            invoke = initInvoke+"."+randomKey+"."+initCommand+".W_"+nextState+"."+subInstanceNo;
            
        } else {
            // don't generate invoke for dev's debug
            //invoke = initInvoke+"."+abstractAF.getEquinoxProperties().getApplicationName()+"."+initCommand+".W_"+nextState+"."+subInstanceNo+"."+"00000"+"."+taskNo;
        	invoke = initInvoke+"."+"00000"+"."+initCommand+".W_"+nextState+"."+subInstanceNo;
        }
        
        if(taskNo!=null && !taskNo.equals("") && !taskNo.equals("0")){
        	invoke += "."+taskNo;
        }
        return invoke;
    }

    public String getNextStateFromInvoke(String invoke){
        return invoke.split("\\.")[3].substring(2);
    }

    public String getSubInsNoFromInvoke(String invoke){
        return invoke.split("\\.")[4];
    }    

}
