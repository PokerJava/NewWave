package ct.af.enums;

public enum EConfig {
//  SDF_LOCATION_INTERFACE("ResourceName_SDF_LOCATION"),
	
  PGZ_SERVER_NAME("PgzServerName"),
  UserSys_List("UserSysList"),
  SDF_INFRANODE_INTERFACE("Resource-Name-SDF_INFRANODE"),
  SDF_INVENTORY_INTERFACE("Resource-Name-SDF_INVENTORY"),
  SDF_JOURNEY_INTERFACE("Resource-Name-SDF_JOURNEY"),
  SDF_RESERVEQUOTA_INTERFACE("Resource-Name-SDF_RESERVEQUOTA"),
  SDF_RELEASEQUOTA_INTERFACE("Resource-Name-SDF_RELEASEQUOTA"),
  SDF_COMMITQUOTA_INTERFACE("Resource-Name-SDF_COMMITQUOTA"),
  Mongo_INTERFACE("Resource-Name-Mongo"),
  MD_INTERFACE("Resource-Name-MD"),
  URL_SDF_INVENTORY("URL-SDF_INVENTORY"),
  URL_SDF_INFRANODE("URL-SDF_INFRANODE"),
  URL_SDF_JOURNEY("URL-SDF_JOURNEY"),
  URL_SDF_RESERVEQUOTA("URL-SDF_RESERVEQUOTA"),
  URL_SDF_RELEASEQUOTA("URL-SDF_RELEASEQUOTA"),
  URL_SDF_COMMITQUOTA("URL-SDF_COMMITQUOTA"),
  URL_Mongo("URL-Mongo"),
  SDF_INVENTORY_TIMEOUT("Tm-SDF_INVENTORY"),
  SDF_INFRANODE_TIMEOUT("Tm-SDF_INFRANODE"),
  SDF_JOURNEY_TIMEOUT("Tm-SDF_JOURNEY"),
  SDF_RESERVEQUOTA_TIMEOUT("Tm-SDF_RESERVEQUOTA"),
  SDF_RELEASEQUOTA_TIMEOUT("Tm-SDF_RELEASEQUOTA"),
  SDF_COMMITQUOTA_TIMEOUT("Tm-SDF_COMMITQUOTA"),
  MONGO_TIMEOUT("Tm-Mongo"),
  DEFAULT_SERVER_TIMEOUT("Tm-Default"),
  MD_TIMEOUT("Tm-MD"),
  EXPIRATIONDATE("Tm-ExpirationDate"),
  PROVISIONING_URL("Provisioning-URL"),
  Expiration_Date("ExpirationDate"),
  Gap_exectime("Tm-Gapexectime"),
  Actor("Actor"),
  Protocol("Protocol"),
  EDR_LOG("EDR_LOG"),
  CDR_LOG("CDR_LOG"),
  TRANSACTION_LOG("Transaction_LOG"),
  INCOMING_UNKNOWN_LOG("IncomingUnknown_LOG"),
  RECOVERY_FILE("Resource-Name-Recovery_File"),
  PATH_ES22_RECOVERYLOG_DUPLICATE("Path-ES22-Recovery_Duplicate"),
  PATH_ES22_RECOVERYLOG_TIMEOUT("Path-ES22-Recovery_Timeout"),
  PATH_ES22_RECOVERYLOG_ERROR("Path-ES22-Recovery_error"),
  PATH_BACKUPRESOURCEINVENTORY("ResourceInventoryBackupPath"),
  PATH_FILEBACKUPRESOURCEINVENTORY("ResourceInventoryBackupFile"),
  REQUEST_MAXACTIVE("MaxActive_Request"),
  RESOURCEINVENTORYFORMFILEMODE("ResourceInventoryFromFileMode");





  private String name;

  EConfig(String name) {

    this.name = name;
  }

  public String getName() {
    return name;
  }

}
