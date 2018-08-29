package ct.af.enums;

public enum ECommand {
    TRIGGER("TRIGGER"),
    RESOURCEORDER_ASYNC("ResourceOrder"),
    RESOURCEITEM("ResourceItem"),
    UNKNOWN("UNKNOWN"),
    RESOURCEORDER_SYNC("SYNC_ResourceOrder")
    ;

    private String command;

    ECommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
