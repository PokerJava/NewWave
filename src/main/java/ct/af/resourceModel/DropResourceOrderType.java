package ct.af.resourceModel;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DropResourceOrderType {
	@SerializedName("resourceOrderType")
    private String resourceOrderType;
	
	@SerializedName("resourceParent")
    private String resourceParent;
	
	@SerializedName("dropResource")
    private List<Object> dropResource;

	public String getResourceOrderType() {
		return resourceOrderType;
	}

	public void setResourceOrderType(String resourceOrderType) {
		this.resourceOrderType = resourceOrderType;
	}

	public String getResourceParent() {
		return resourceParent;
	}

	public void setResourceParent(String resourceParent) {
		this.resourceParent = resourceParent;
	}

	public List<Object> getDropResource() {
		return dropResource;
	}

	public void setDropResource(List<Object> dropResource) {
		this.dropResource = dropResource;
	}

}
