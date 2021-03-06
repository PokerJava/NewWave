package ct.af.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtils {
	public boolean validRegex(String text, String regX)
	{
		Pattern pattern = Pattern.compile(regX);
		Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}
	public boolean isNotNullAndNotEmpty(String data)
	{
		boolean isValid = false;
		if(data!=null&&!data.isEmpty())
		{
			isValid = true;
		}
		return isValid;
	}
}
