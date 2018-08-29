package ct.af.resourceModel;

import com.google.gson.annotations.SerializedName;

public class ResourceInventory {

    @SerializedName("resourceName")
    private String resourceName;

    @SerializedName("inventoryType")
    private String inventoryType;

    @SerializedName("resourceState")
    private String resourceState;

    @SerializedName("quotaPlan")
    private Long quotaPlan;

    @SerializedName("effectiveDate")
    private String effectiveDate;

    @SerializedName("expireDate")
    private String expireDate;

    @SerializedName("instanceFlag")
    private char instanceFlag;

    @SerializedName("identityFlag")
    private String identityFlag;

    @SerializedName("identityType")
    private String identityType;

    @SerializedName("createDate")
    private String createDate;

    @SerializedName("createBy")
    private String createBy;

    @SerializedName("lastUpdBy")
    private String lastUpdBy;

    @SerializedName("lastUpdDate")
    private String lastUpdDate;

    @SerializedName("resourceDescriptionTH")
    private String resourceDescriptionTH;

    @SerializedName("resourceDescriptionEN")
    private String resourceDescriptionEN;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public String getResourceState() {
        return resourceState;
    }

    public void setResourceState(String resourceState) {
        this.resourceState = resourceState;
    }

    public Long getQuotaPlan() {
        return quotaPlan;
    }

    public void setQuotaPlan(Long quotaPlan) {
        this.quotaPlan = quotaPlan;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public char getInstanceFlag() {
        return instanceFlag;
    }

    public void setInstanceFlag(char instanceFlag) {
        this.instanceFlag = instanceFlag;
    }

    public String getIdentityFlag() {
        return identityFlag;
    }

    public void setIdentityFlag(String identityFlag) {
        this.identityFlag = identityFlag;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getLastUpdBy() {
        return lastUpdBy;
    }

    public void setLastUpdBy(String lastUpdBy) {
        this.lastUpdBy = lastUpdBy;
    }

    public String getLastUpdDate() {
        return lastUpdDate;
    }

    public void setLastUpdDate(String lastUpdDate) {
        this.lastUpdDate = lastUpdDate;
    }

    public String getResourceDescriptionTH() {
        return resourceDescriptionTH;
    }

    public void setResourceDescriptionTH(String resourceDescriptionTH) {
        this.resourceDescriptionTH = resourceDescriptionTH;
    }

    public String getResourceDescriptionEN() {
        return resourceDescriptionEN;
    }

    public void setResourceDescriptionEN(String resourceDescriptionEN) {
        this.resourceDescriptionEN = resourceDescriptionEN;
    }

    public ResourceInventory trim() {
        //this.setResourceCode(resourceCode.trim());
        this.setResourceName(resourceName==null?"":resourceName.trim());
        this.setInventoryType(inventoryType==null?"":inventoryType.trim());
        this.setResourceState(resourceState==null?"":resourceState.trim());
        this.setEffectiveDate(effectiveDate==null?"":effectiveDate.trim());
        this.setExpireDate(expireDate==null?"":expireDate.trim());
        this.setIdentityFlag(identityFlag==null?"":identityFlag.trim());
        this.setIdentityType(identityType==null?"":identityType.trim());
        this.setCreateDate(createDate==null?"":createDate.trim());
        this.setCreateBy(createBy==null?"":createBy.trim());
        this.setLastUpdBy(lastUpdBy==null?"":lastUpdBy.trim());
        this.setLastUpdDate(lastUpdDate==null?"":lastUpdDate.trim());
        this.setResourceDescriptionTH(resourceDescriptionTH==null?"":resourceDescriptionTH.trim());
        this.setResourceDescriptionEN(resourceDescriptionEN==null?"":resourceDescriptionEN.trim());

        return this;
    }

    @Override
    public String toString() {
        return " {\"resourceName\":" + "\"" + resourceName + "\"" +
            ", \"inventoryType\":" + "\"" + inventoryType + "\"" +
            ", \"resourceState\":" + "\"" + resourceState + "\"" +
            ", \"quotaPlan\":" + "\"" + quotaPlan + "\"" +
            ", \"effectiveDate\":" + "\"" + effectiveDate + "\"" +
            ", \"expireDate\":" + "\"" + expireDate + "\"" +
            ", \"instanceFlag\":" + "\"" + instanceFlag + "\"" +
            ", \"identityFlag\":" + "\"" + identityFlag + "\"" +
            ", \"identityType\":" + "\"" + identityType + "\"" +
            ", \"createDate\":" + "\"" + createDate + "\"" +
            ", \"createBy\":" + "\"" + createBy + "\"" +
            ", \"lastUpdBy\":" + "\"" + lastUpdBy + "\"" +
            ", \"lastUpdDate\":" + "\"" + lastUpdDate + "\"" +
            ", \"resourceDescriptionTH\":" + "\"" + resourceDescriptionTH + "\"" +
            ", \"resourceDescriptionEN\":" + "\"" + resourceDescriptionEN + "\"" +
            " }";
    }
}
