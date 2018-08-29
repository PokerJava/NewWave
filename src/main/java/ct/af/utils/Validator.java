package ct.af.utils;

import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    public boolean isString(String str,int lng) {

        if (lng == 0) {
            return str.matches(".*");
        } else {
            return str.matches("^\\w{" + lng + "}");
        }
    }

    public boolean isDigit(String str,int lng) {
        if (lng == 0) {
            return str.matches("\\d*");
        } else {
            return str.matches("^\\d{" + lng + "}");
        }
    }


    public boolean isDateFormat(String strDate) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            sf.parse(strDate);
        } catch (ParseException e) {
            return false;
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean isDateFormatWithTimezone(String strDate) {
        try {
            if(strDate.length() > 19 || strDate.length() < 19) {
                return false;
            } else {
                Config.getFormatDateWithMiTz().parseDateTime(strDate);
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean isNumeric(String strNum)  {
        return strNum.matches("-?\\d*\\.?\\d+");
    }



    /*
    Parameter Validate
    variableName: name of the parameter to log when error occurs
    parameter: variable to check
    vNumber - if true, validate isNumber
    errorMsg: return errorMsg when error occurs
*/
    public String mandatoryValidate(String variableName, String parameter, boolean vNumber, String errorMsg)
    {
        //Validator validator = new Validator();
        if (parameter != null && !parameter.equals(""))
        {
            if (vNumber && !isNumeric(parameter))
            {
                errorMsg += variableName;
                errorMsg += " is not a number. ";
            }
        }
        else
        {
            errorMsg += "is missing ";
            errorMsg += variableName;
            errorMsg += ". ";

        }
        return errorMsg;
    }

    public String mandatoryValidate(String parameterName, String parameterValue, String errorMsg)
    {
        if (parameterValue == null || parameterValue.trim().equals(""))
        {
            if (!errorMsg.contains("is missing")) {
                errorMsg += "is missing ";
                errorMsg += parameterName + ".";
            } else if (!errorMsg.contains("and")) {
                errorMsg = errorMsg.replaceAll("\\.", " and ");
                errorMsg += parameterName + ".";
            } else {
                errorMsg = errorMsg.replaceAll(" and ", " ");
                errorMsg = errorMsg.replaceAll("\\.", "");
                errorMsg += " and " + parameterName + ".";
            }
        }
        return errorMsg;
    }

    public String patternValidate(String parameterName, String parameterValue, String format, String errorMsg) {
        boolean hasError = false;
        if (format.contains("|")) {
            List<String> valueList = Arrays.asList(format.split("\\|"));
            boolean isMatch = false;
            for (String value : valueList) {
                if (parameterValue.equals(value)) {
                    isMatch = true;
                }
            }

            if (!isMatch) {
                hasError = true;
            }
        } else {
            // Create a Pattern object
            Pattern pattern = Pattern.compile(format);
            // Now create matcher object.
            Matcher matcher = pattern.matcher(parameterValue);

            if (!matcher.matches()) {
                hasError = true;
            }
        }


        if (hasError) {
            if (errorMsg.equals("")) {
                errorMsg += "is invalid ";
                errorMsg += parameterName + ".";
            } else if (!errorMsg.contains(" and ")) {
                errorMsg = errorMsg.replaceAll("\\.", " and ");
                errorMsg += parameterName + ".";
            } else {
                errorMsg = errorMsg.replaceAll(" and ", " ");
                errorMsg = errorMsg.replaceAll("\\.", "");
                errorMsg += " and " + parameterName + ".";
            }
        }

        return errorMsg;
    }

    public String ErrorFlagValidate(String parameterValue, String format, String errorMsg) {
        // Create a Pattern object
        Pattern pattern = Pattern.compile(format);
        // Now create matcher object.
        Matcher matcher = pattern.matcher(parameterValue);

        if (!matcher.matches()) {
            errorMsg += "errorFlag must be 0 or 1.";
        }
        return errorMsg;
    }


    public String dateTimeValidate(String variableName, String strDate, DateTimeFormatter dateTimeFormatter, boolean isRequired, String errorMsg)
    {
        boolean passed = false;
        if(strDate != null && !strDate.equals(""))
        {
            try
            {
                dateTimeFormatter.parseDateTime(strDate);
                passed = true;
            }
            catch (Exception e)
            {
                passed = false;
            }

            if (!passed)
            {
                errorMsg += variableName;
                errorMsg += " is invalid DateTime format. ";
            }
        }
        else if(isRequired)
        {
            errorMsg += "is missing ";
            errorMsg += variableName;
            errorMsg += ". ";
        }
        return errorMsg;
    }


}
