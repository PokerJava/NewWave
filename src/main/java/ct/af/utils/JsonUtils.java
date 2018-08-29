package ct.af.utils;

import com.google.gson.JsonParser;

public class JsonUtils {

    public static boolean isValidJsonFormat(String jsonData) {
        JsonParser jsonParser = new JsonParser();
        try {
            jsonParser.parse(jsonData);
            return true;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }


    public static String escape(String s) {
        if (s == null) {
            return null;
        } else {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < s.length(); i++) {
                char ch = s.charAt(i);
                switch (ch) {
                    case '"':
                        sb.append("\\\"");
                        break;
                    case '\\':
                        sb.append("\\\\");
                        break;
                    case '\b':
                        sb.append("\\b");
                        break;
                    case '\f':
                        sb.append("\\f");
                        break;
                    case '\n':
                        sb.append("\\n");
                        break;
                    case '\r':
                        sb.append("\\r");
                        break;
                    case '\t':
                        sb.append("\\t");
                        break;
                    case '/':
                        sb.append("\\/");
                        break;

                    default:
                        if (((ch >= 0) && (ch <= '\037')) || ((ch >= '') && (ch <= '?')) || ((ch >= '?') && (ch <= '?'))) {
                            String ss = Integer.toHexString(ch);
                            sb.append("\\u");
                            for (int k = 0; k < 4 - ss.length(); k++) {
                                sb.append('0');
                            }
                            sb.append(ss.toUpperCase());
                        }
                        else {
                            sb.append(ch);
                        }
                        break;
                }
            }
            return sb.toString();
        }
    }
    
}
