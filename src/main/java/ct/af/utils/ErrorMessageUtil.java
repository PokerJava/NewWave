package ct.af.utils;

import org.apache.commons.lang3.StringUtils;

public class ErrorMessageUtil
{
    public static String appendWithAnd(String original, String stringToAppend)
    {
        String retString;
        if (original == null || original.equals("")) {
            retString = stringToAppend;
        } else if (!original.contains(" and ")) {
            retString = original.substring(0, original.length() - 1);
            retString += " and " + stringToAppend;
        } else {
            retString = original.replaceAll(" and ", ", ");
            retString = retString.substring(0, retString.length() - 1);
            retString += " and " + stringToAppend;
        }
        return retString;
    }

    public static String appendWithSemicolon(String original, String stringToAppend) {
        String retString;
        if (original == null || original.trim().equals("")) {
            if (stringToAppend == null || stringToAppend.trim().equals("")) {
                retString = "";
            } else {
                retString = stringToAppend;
            }
        } else if (stringToAppend != null && !stringToAppend.trim().equals("")) {
            retString = original + "; " + stringToAppend;
        }
        else
        {
            retString = original;
        }
        return retString;
    }

    public static String appendCommaInsteadFullStop(String original, String stringToAppend) {
        // replace . (full stop) with , (comma) and connect errorMessage
        String retString = "";

        if (StringUtils.isNotBlank(original) && StringUtils.isNotBlank(stringToAppend)) {
            retString = original.replaceAll("\\.", ", ");
            retString += stringToAppend;
        } else if (StringUtils.isBlank(original)) {
            retString = stringToAppend;
        } else if (StringUtils.isBlank(stringToAppend)) {
            retString = original;
        }

        return retString;
    }
}
