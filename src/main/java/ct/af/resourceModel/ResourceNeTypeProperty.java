package ct.af.resourceModel;

import com.google.gson.annotations.SerializedName;

public class ResourceNeTypeProperty {
	@SerializedName("neType")
	String neType;

	@SerializedName("cType")
	String cType;

	@SerializedName("protocol")
	String protocol;
	
	@SerializedName("descriptionEN")
	String descriptionEN;

	@SerializedName("createDate")
	String createDate;

	@SerializedName("createBy")
	String createBy;

	@SerializedName("lastUpdBy")
	String lastUpdBy;

	@SerializedName("lastUpdDate")
	String lastUpdDate;

	public String getNeType() {
		return neType;
	}

	public void setNeType(String neType) {
		this.neType = neType;
	}

	public String getCType() {
		return cType;
	}

	public void setCType(String cType) {
		this.cType = cType;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getDescriptionEN() {
		return descriptionEN;
	}

	public void setDescriptionEN(String descriptionEN) {
		this.descriptionEN = descriptionEN;
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
	
	public ResourceNeTypeProperty trim() {
		this.setNeType(neType.trim());
		this.setCType(cType.trim());
		this.setProtocol(protocol.trim());
		this.setDescriptionEN(descriptionEN.trim());
		this.setCreateDate(createDate.trim());
		this.setCreateBy(createBy.trim());
		this.setLastUpdBy(lastUpdBy.trim());
		this.setLastUpdDate(lastUpdDate.trim());
		
		return this;
	}
	
	public String toString() {
		String str = "{ \"NeType\": " + "\"" + neType + "\"";
		str += ", \"CType\": " + "\"" + cType + "\"";
		str += ", \"Protocol\": " + "\"" + protocol + "\"";
		str += ", \"DescriptionEN\": " + "\"" + descriptionEN + "\"";
		str += ", \"CreateDate\": " + "\"" + createDate + "\"";
		str += ", \"CreateBy\": " + "\"" + createBy + "\"";
		str += ", \"LastUpdBy\": " + "\"" + lastUpdBy + "\"";
		str += ", \"LastUpdDate\": " + "\"" + lastUpdDate + "\"";
		str += "}";
		return str;
	}
}
