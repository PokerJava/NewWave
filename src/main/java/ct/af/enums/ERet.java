package ct.af.enums;

public enum ERet {

  RET0("0","Normal"),
  RET1("1","Error"),
  RET2("2","Reject"),
  RET3("3","Abort"),
  RET4("4","Timeout"),
  ;

  private String ret;
  private String retDesc;

  ERet(String ret, String retDesc) {
    this.ret = ret;
    this.retDesc = retDesc;
  }

  public String getRet() {
    return ret;
  }

  public String getRetDesc() {
    return retDesc;
  }

  public static ERet fromString(String text) {
    if (text != null) {
      for (ERet eRet : ERet.values()) {
        if (text.equalsIgnoreCase(eRet.ret)) {
          return eRet;
        }
      }
    }
    return null;
  }

}
