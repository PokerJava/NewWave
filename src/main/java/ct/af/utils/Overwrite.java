package ct.af.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ct.af.instance.AFSubInstance;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.DefaultEditorKit.PasteAction;

import org.joda.time.DateTime;

import ec02.af.utils.AFLog;

public class Overwrite {
	//private static final String ClientDateTimeFormat = "yyyy-MM-dd HH:mm:ss+0700";
	private static final String ClientDateTimeFormat = "yyyyMMddHHmmssZ";
	private static final String NeDateTimeFormat = "yyyyMMddHHmmss";
	
	private static final String CONVERT_DATETIME_TO_NE = "convert_ClientTimeToyyyyMMddHHmmss";
	private static final String CONVERT_DATETIME_TO_CLIENT = "CONVERT_DATETIME_TO_CLIENT";
	private static final String CUTPREFIX_ZERO = "cutPrefix0";
	private static final String CUTPREFIX_66 = "cutPrefix66";
	private static final String CUTPREFIX_C_ = "cutPrefixC_";
	private static final String replacePrefix0with66 = "replacePrefix0with66";
	private static final String replacePrefix66with0 = "replacePrefix66with0";
	private static final String CutPrefix = "cutPrefix";
	private static final String MinusValue = "minusValue";
	private static final String CutPrefixLength = "cutPrefixLength";
	private static final String CalculateValidity = "calculateValidity";
	
	public static String overwriteDef(String unsplitingOverwrite,AFSubInstance afChildSubIns){
		String[] overwriteDefVar = unsplitingOverwrite.split("[\\(\\)]");
		String functionName = "";
		String input = new String();
		if (overwriteDefVar.length == 2){
			functionName = overwriteDefVar[0].trim();
			input = overwriteDefVar[1].trim();
        } else if (overwriteDefVar.length == 1) {
        	functionName = overwriteDefVar[0].trim();
        } else {
        	AFLog.e("[Error] OverwriteDef invalid format '" + unsplitingOverwrite +"'");
        	afChildSubIns.setProcessOutputMessageComplete(false);
        	afChildSubIns.setErrorMessage(afChildSubIns.getSubResourceName()+"("+afChildSubIns.getSubResourceItemNo()+")"+" overwrite_def invalid format "+unsplitingOverwrite+" in "+afChildSubIns.getCurrentSuppcode()+".txt");
        	return null;
        }
		return overwriteDef(functionName, input,afChildSubIns);
	}
	
	public static String overwriteExtra(String unsplitingOverwrite,AFSubInstance afChildSubIns){
		String[] overwriteExtraVar = unsplitingOverwrite.split("[\\(\\)]");
		String functionName = "";
		String input = new String();

		
		if (overwriteExtraVar.length == 2){
			functionName = overwriteExtraVar[0].trim();
			input = overwriteExtraVar[1].trim();
        } else if (overwriteExtraVar.length == 1) {
        	functionName = overwriteExtraVar[0].trim();
        } else {
        	AFLog.e("[Error] OverwriteExtra invalid format '" + unsplitingOverwrite +"'");
        	afChildSubIns.setProcessOutputMessageComplete(false);
        	afChildSubIns.setErrorMessage(afChildSubIns.getSubResourceName()+"("+afChildSubIns.getSubResourceItemNo()+")"+" overwrite_extra invalid format "+unsplitingOverwrite+" in "+afChildSubIns.getCurrentSuppcode()+".txt");
        	return null;
        }
		return overwriteExtra(functionName, input,afChildSubIns);
	}

	public static String overwriteDef(String functionName, String input,AFSubInstance afChildSubIns){
		if(Config.getOverwriteDefHashMap().containsKey(functionName))
		{	
			HashMap<String, String> overwriteDefChild = Config.getOverwriteDefHashMap().get(functionName);
			if(overwriteDefChild.containsKey(input)) {
				AFLog.d("OverwriteDef " + functionName + " (\"" + input + "\" : \"" + overwriteDefChild.get(input) + "\") success");
				return overwriteDefChild.get(input);
			} else if("".equals(input)||input==null) {
				if(overwriteDefChild.containsKey("NOVALUE")) {
					input = "NOVALUE";
					AFLog.d("OverwriteDef " + functionName + " missing input");
					AFLog.d("OverwriteDef " + functionName + " (\"" + input + "\" : \"" + overwriteDefChild.get(input) + "\") success");
					return overwriteDefChild.get(input);
				} else {
					AFLog.d("[Error] OverwriteDef " + functionName + " missing input, and OverwriteParam config has no \"NOVALUE\"");
					afChildSubIns.setErrorMessage(afChildSubIns.getSubResourceName()+"("+afChildSubIns.getSubResourceItemNo()+")"+" functionName "+functionName+" doesn't define default value (NOVALUE) in overwriteParam.json");
					return input;
				}	
			} else{
				AFLog.w("OverwriteDef key '" + input + "' not found!, return '" + input + "' as value");
				return input;
			}
		} else {
			//AFLog.e("[Error] OverwriteDef function '" + functionName + "' not found!");
			//afChildSubIns.setProcessOutputMessageComplete(false);
			//afChildSubIns.setErrorMessage(afChildSubIns.getSubResourceName()+"("+afChildSubIns.getSubResourceItemNo()+")"+" Cannot found overwriteParam "+functionName+" in overwriteParam.json");
			//return null;
			//(Change requirement) if functionName not found, use raw format.
			AFLog.w("OverwriteDef function '" + functionName + "' not found!, return '" + input + "' as value");
			return input;
		}
	}
	
	public static String overwriteExtra(String functionName, String input,AFSubInstance afChildSubIns){
		String outputValue;
		switch (functionName) {
		case CONVERT_DATETIME_TO_NE :  
			outputValue = convertDateTimetToNe(input);
			break;
		case CONVERT_DATETIME_TO_CLIENT :
			outputValue = convertDateTimeToClient(input);
			break;
		case replacePrefix0with66 :
			outputValue = replacePrefix0with66(input);
			break;
		case replacePrefix66with0 :
			outputValue = replacePrefix66with0(input);
			break;
		case CUTPREFIX_ZERO :
			outputValue = prefixZero(input);
			break;
		case CutPrefixLength :
			outputValue = cutPrefixLength(input);
			break;
		case CalculateValidity :
			outputValue = calculateValidity(input);
			break;
		case CutPrefix :
			outputValue = cutPrefix(input);
			break;
		case MinusValue :
			outputValue = minusValue(input);
			break;
		case CUTPREFIX_66 :
			outputValue = prefix66(input);
			break;
		case CUTPREFIX_C_ :
			outputValue = prefixC_(input);
			break;
		default: 
			outputValue = input;
			AFLog.w("OverwriteExtra function '" + functionName + "' not found!, return '" + input + "' as value");
			//afChildSubIns.setProcessOutputMessageComplete(false);
			//afChildSubIns.setErrorMessage(afChildSubIns.getSubResourceName()+"("+afChildSubIns.getSubResourceItemNo()+")"+" Cannot found function \""+functionName+"\" in "+afChildSubIns.getCurrentSuppcode()+".txt");
			break;
		}
		return outputValue;
	}

	
	
	public static String convertDateTimetToNe(String inputDateTimeString){
		return convertDateTime(inputDateTimeString, ClientDateTimeFormat, NeDateTimeFormat);
	}
	
	public static String convertDateTimeToClient(String inputDateTimeString){
		return convertDateTime(inputDateTimeString, NeDateTimeFormat, ClientDateTimeFormat);
	}
	
	

	public static String convertDateTime(String inputDateTimeString, String inputFormat, String outputFormat){
		String outputDateTime = new String();
		try{
			Date date = new SimpleDateFormat(inputFormat).parse(inputDateTimeString);
			outputDateTime = new SimpleDateFormat(outputFormat).format(date);
			AFLog.d("Convert dateTime from '" + inputDateTimeString + "' (" + inputFormat + ") to '" + outputDateTime + "' (" + outputFormat + ")");
		} catch (ParseException ex) {
			AFLog.d("Cannot convert dateTime from '" + inputDateTimeString + "' (" + inputFormat + ") to (" + outputFormat + ")");
			outputDateTime = inputDateTimeString;
		}
		return outputDateTime;
	}
	
	public static String prefixZero(String input){
		String outputMsisdn = "";
		String msisdn = input.trim();
		
		if(null!=input&&!"".equals(input)&&(input.length()>=9&&input.length()<=10) && input.matches("^\\d+$")){
			
			if(msisdn.startsWith("0")){
				outputMsisdn = msisdn.substring(1, msisdn.length());
				AFLog.d("OverwriteExtra function ('"+ CUTPREFIX_ZERO +"') from ('" + input +"') to ('"+ outputMsisdn +"')");
			}else {
				AFLog.d("@OVERWRITE_EXTRA : " + CUTPREFIX_ZERO + "($MSISDN) : $MSISDN(" + msisdn + ") is not start with '0'");
				outputMsisdn = msisdn;
			}

		} else {
			AFLog.d("Invalid format @OVERWRITE_EXTRA : " + CUTPREFIX_ZERO + "($MSISDN) ");
			outputMsisdn = msisdn;
		}
		return outputMsisdn;
	}
	
	public static String minusValue(String input){
		String outputValue = "";
		String[] splitInput = input.trim().split(",");
		int[] lengthSplit = new int[splitInput.length]; 
		
		if(null!=input) {
			for (int i = 0; i < lengthSplit.length; i++){
				lengthSplit[i] = Integer.parseInt(splitInput[i]);
			}
			
			int val = lengthSplit[0];
			for (int i = 1; i < lengthSplit.length; i++) {
				val = val-lengthSplit[i];
			}
			outputValue = Integer.toString(val);
			AFLog.d("OverwriteExtra function ('"+ MinusValue +"') from ('" + input +"') to ('"+ outputValue +"')");
		}else {
			AFLog.d("Invalid format @OVERWRITE_EXTRA : " + MinusValue);
			outputValue = input;
		}
		return outputValue;
	}
	
	public static String cutPrefix(String input){
		String outputValue = "";
		String[] valueSplit = input.trim().split(",");
		
		String value = valueSplit[0];
		String cutValue = valueSplit[1];
		
		if(value!=null&&value.contains(cutValue)) {
			outputValue = value.replaceFirst(cutValue,"");
			AFLog.d("OverwriteExtra function ('"+ CutPrefix +"') from ('" + input +"') to ('"+ outputValue +"')");
			System.out.println("OverwriteExtra function ('"+ CutPrefix +"') from ('" + input +"') to ('"+ outputValue +"')");
		}else {
			AFLog.d("Invalid format @OVERWRITE_EXTRA : " + CutPrefix +" : ("+ input + ") is not start with '"+cutValue+"'" );
			System.out.println("Invalid format @OVERWRITE_EXTRA : " + CutPrefix +" : ("+ input + ") is not start with '"+cutValue+"'" );
			outputValue = input;
		}
		
		return outputValue;
	}
	
	public static String cutPrefixLength(String input){
		String outputCalendar = "";
		String valsub = null;
		String[] valueSplit = input.trim().split(",");
		
		// Create a Pattern object
        Pattern pattern = Pattern.compile("[0-9]+");
        // Now create matcher object.
        Matcher matcher = pattern.matcher(valueSplit[1]);
        
		if(matcher.matches()) {
			valsub = input.substring(0,Integer.parseInt(valueSplit[1]));
			outputCalendar = valsub;
			AFLog.d("OverwriteExtra function ('"+ CutPrefix +"') from ('" + input +"') to ('"+ outputCalendar +"')");
			System.out.println("OverwriteExtra function ('"+ CutPrefix +"') from ('" + input +"') to ('"+ outputCalendar +"')");
			System.out.println("Invalid format @OVERWRITE_EXTRA : " + CutPrefixLength +" : ("+ valueSplit[1] + ") is not Number in '"+input+"'" );
		}else {
			AFLog.d("Invalid format @OVERWRITE_EXTRA : " + CutPrefixLength +" : ("+ valueSplit[1] + ") is not Number in '"+input+"'" );
			outputCalendar = input;
		}
		
		outputCalendar = calculateValidity(valsub);
		return outputCalendar;
	}
	
	public static String calculateValidity(String input){
		String outputcalculate = "";
		int daysBetween = 0;
		
		DateFormat FormatDatetime = new SimpleDateFormat("yyyyMMdd");
		Date startDate = null;
		Date endDate = null;
		String timeStamp = FormatDatetime.format(Calendar.getInstance().getTime());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			endDate = sdf.parse(input);
			startDate = sdf.parse(timeStamp);
		} catch (ParseException e) {
			AFLog.d("Invalid format @OVERWRITE_EXTRA : " + CalculateValidity +" : ("+ timeStamp +" Or "+input + ") is not Cast format Date " );
		}
		
		Calendar calStartDate = null;
		Calendar calEndDate =null;
		
		try {
			calStartDate = Calendar.getInstance();
			calStartDate.setTime(startDate);
			calEndDate = Calendar.getInstance();
			calEndDate.setTime(endDate);
		}catch (Exception e) {
			AFLog.d("Invalid format @OVERWRITE_EXTRA : " + CalculateValidity +" : ("+ timeStamp +" Or "+input + ") is not Cast format Calendar " );
		}
		
		
		Calendar date = (Calendar) calStartDate.clone(); 
		  while (date.before(calEndDate)) {
		    date.add(Calendar.DAY_OF_MONTH, 1);
		    daysBetween++;
		  }
		  while (calEndDate.before(date)) {
			calEndDate.add(Calendar.DAY_OF_MONTH, 1);
			daysBetween--;
		  }
		
		  outputcalculate = Integer.toString(daysBetween);
	
		return outputcalculate;
	}
		
	
	public static String prefix66(String input){
		String outputMsisdn = "";
		input = input.trim();
//		if(null!=input&&!"".equals(input)&&(input.length()>=9&&input.length()<=10) && input.matches("^\\d+$")){
			
			if(input.startsWith("66")){
				outputMsisdn = input.replaceFirst("66", "");
				AFLog.d("OverwriteExtra function ('"+ CUTPREFIX_66 +"') from ('" + input +"') to ('"+ outputMsisdn +"')");
			}else {
				AFLog.d("@OVERWRITE_EXTRA : " + CUTPREFIX_66 + "($MSISDN) : $MSISDN(" + input + ") is not start with '66'");
				outputMsisdn = input;
			}
			
			

//		} else {
//			AFLog.d("Invalid format @OVERWRITE_EXTRA : " + CUTPREFIX_ZERO + "($MSISDN) ");
//			outputMsisdn = msisdn;
//		}
		return outputMsisdn;
	}
	
	public static String prefixC_(String input) {
		String outputFreeUnitId  = "";
		input = input.trim();
		if(input.startsWith("C_")){
			outputFreeUnitId = input.replaceFirst("C_", "");
			AFLog.d("OverwriteExtra function ('"+ CUTPREFIX_C_ +"') from ('" + input +"') to ('"+ outputFreeUnitId +"')");
		}else {
			AFLog.d("@OVERWRITE_EXTRA : " + CUTPREFIX_C_ + "<bbs:FreeUnitType>" + input + "</bbs:FreeUnitType> is not start with 'C_'");
			outputFreeUnitId = input;
		}
		return outputFreeUnitId;
	}

	
	public static String replacePrefix0with66(String input){
		String outputMsisdn = "";
		if(input.startsWith("0")) {
			outputMsisdn = input.replaceFirst("0", "66");
			AFLog.d("OverwriteExtra function ('"+ replacePrefix0with66 +"') from ('" + input +"') to ('"+ outputMsisdn +"')");

		}else{
			AFLog.d("Cannot OverwriteExtra function from ('" + input +"')");
			outputMsisdn = input;
		}
		return outputMsisdn;
	}
	
	public static String replacePrefix66with0(String input){
		String outputMsisdn = "";
		if(input.startsWith("66")) {
			outputMsisdn = input.replaceFirst("66", "0");
			AFLog.d("OverwriteExtra function ('"+ replacePrefix66with0 +"') from ('" + input +"') to ('"+ outputMsisdn +"')");

		}else{
			AFLog.d("Cannot OverwriteExtra function from ('" + input +"')");
			outputMsisdn = input;
		}
		return outputMsisdn;
	}

}
