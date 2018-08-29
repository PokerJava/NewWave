package ct.af.resourceModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResourceProperty {
    @SerializedName("resourceName")
    private String resourceName;

    @SerializedName("preNeRouting")
    private String preNeRouting;

    @SerializedName("resourceRuleList")
    private List<String> resourceRuleList;

    @SerializedName("defValue")
    private List<Object> defValue;
    
    @SerializedName("searchKey")
    private List<String> searchKey;
    
    @SerializedName("createDate")
    private String createDate;
    
    @SerializedName("createBy")
    private String createBy;
    
    @SerializedName("lastUpdBy")
    private String lastUpdBy;
    
    @SerializedName("lastUpdDate")
    private String lastUpdDate;
    
    @SerializedName("resourcePropertyDescriptionEN")
    private String resourcePropertyDescriptionEN;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getPreNeRouting() {
        return preNeRouting;
    }

    public void setPreNeRouting(String preNeRouting) {
        this.preNeRouting = preNeRouting;
    }

    public List<String> getResourceRuleList() {
        return resourceRuleList;
    }

    public void setResourceRuleList(List<String> resourceRuleList) {
        this.resourceRuleList = resourceRuleList;
    }

    public List<Object> getDefValue() {
		return defValue;
	}

	public void setDefValue(List<Object> defValue) {
		this.defValue = defValue;
	}

	public List<String> getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(List<String> searchKey) {
        this.searchKey = searchKey;
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

	public String getResourcePropertyDescriptionEN() {
		return resourcePropertyDescriptionEN;
	}

	public void setResourcePropertyDescriptionEN(String resourcePropertyDescriptionEN) {
		this.resourcePropertyDescriptionEN = resourcePropertyDescriptionEN;
	}
    
    public ResourceProperty trim() {
    	this.setResourceName(resourceName.trim());
    	this.setPreNeRouting(preNeRouting.trim());
    	for (int i=0; i<searchKey.size(); i++) {
    		searchKey.set(i, searchKey.get(i).trim());
    	}
    	
		return this;
    }
    
    public String toString() {
    	String str = "{ \"ResourceName\": " + "\"" + resourceName + "\"";
    	str += ", \"PreNeRouting\": " + "\"" + preNeRouting + "\"";
		str += ", \"ResourceRuleList\": [";
		for (int i=0; i<resourceRuleList.size(); i++) {
			str += "\"" + resourceRuleList.get(i) + "\"";
			if (i<resourceRuleList.size()-1) {
				str += ", ";
			}
		};
		str += "], \"KeySearch\": [";
		for (int i=0; i<searchKey.size(); i++) {
			str += "\"" + searchKey.get(i) + "\"";
			if (i<searchKey.size()-1) {
				str += ", ";
			}
		};
		str += "] }";
    	
		return str;
    	
    }
}

