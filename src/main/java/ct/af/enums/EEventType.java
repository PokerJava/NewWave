package ct.af.enums;

public enum EEventType {

  REQUEST("request","req"),
  RESPONSE("response","res")
  ;

  private String eventType;
  private String eventTypeShort;

  EEventType(String eventType, String eventTypeShort) {
    this.eventType = eventType;
    this.eventTypeShort = eventTypeShort;
  }

  public String getEventType() {
    return eventType;
  }

  public String getEventTypeShort() {
    return eventTypeShort;
  }

}