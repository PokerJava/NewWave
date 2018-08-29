package ct.af.message.incoming.parameter;

import ct.af.resourceModel.ResourceInfraNode;
import java.util.List;

public class Param_SDF_GetResourceInfraNode extends AbsClientParam {
    private String resultCode;
    private String resultDescription;
    private String diagnosticMessage;
    private List<ResourceInfraNode> cadResourceInfraInventory;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getDiagnosticMessage() {
        return diagnosticMessage;
    }

    public void setDiagnosticMessage(String diagnosticMessage) {
        this.diagnosticMessage = diagnosticMessage;
    }

    public List<ResourceInfraNode> getCadResourceInfraInventory() {
        return cadResourceInfraInventory;
    }

    public void setCadResourceInfraInventory(List<ResourceInfraNode> cadResourceInfraInventory) {
        this.cadResourceInfraInventory = cadResourceInfraInventory;
    }
}
