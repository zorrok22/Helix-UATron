package pages.ITSM.Api;

public enum PermissionGroupInfo {

    INCIDENT_MASTER("Incident Master", "20004", "BMC:Incident Mgmt", "Incident Mgmt/Service Desk"),
    INCIDENT_USER("Incident User", "20003", "BMC:Incident Mgmt", "Incident Mgmt/Service Desk"),
    INCIDENT_CONFIG("Incident Config", "20005", "BMC:Incident Mgmt", "Incident Mgmt/Service Desk"),
    PROBLEM_USER("Problem User", "20007", "BMC:Problem Mgmt", "Problem Management"),
    PROBLEM_MASTER("Problem Master", "20008", "BMC:Problem Mgmt", "Problem Management"),
    PROBLEM_CONFIG("Problem Config", "20010", "BMC:Problem Mgmt", "Problem Management"),
    INFRA_CHANGE_CONFIG("Infrastructure Change Config", "20014", "BMC:Change Mgmt", "Change Management");

    private final String groupName;
    private final String groupId;
    private final String tagName;
    private final String productName;

    PermissionGroupInfo(String groupName, String groupId, String tagName, String productName) {
        this.groupName = groupName;
        this.groupId = groupId;
        this.tagName = tagName;
        this.productName = productName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getTagName() {
        return tagName;
    }

    public String getProductName() {
        return productName;
    }

    /** ✅ Lookup method to get enum by group name (case-insensitive) */
    public static PermissionGroupInfo fromGroupName(String groupName) {
        for (PermissionGroupInfo info : values()) {
            if (info.groupName.equalsIgnoreCase(groupName)) {
                return info;
            }
        }
        throw new IllegalArgumentException("Unknown Permission Group: " + groupName);
    }
}
