package ct.af.enums;

public enum ECType {

  QXML("qxml"),
  TEXTPLAIN("text/plain"),
  TEXTXML("text/xml"),
  TEXTHTML("text/html"),
  APPJSON("application/json"),
  TCP("tcp")
  ;

  private String name;
  ECType(String name) {
    this.name = name;
  }

  public String getCType() {
    return name;
  }
}
