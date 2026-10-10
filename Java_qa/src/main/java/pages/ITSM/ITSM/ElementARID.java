package pages.ITSM.ITSM;

public enum ElementARID {

    Add_Approver_Btn("301904200"),
    Add_Remove_Licenses_Page_Title("15418"),
    CTM_PEOPLE_Title("300367500"),
    IS_Application_Licenses_Table("T304449631"),
    Add_Remove_Licenses_Table("T15417");

    private final String text;

    ElementARID(String text) {
        this.text = text;
    }

    public String toString() {
        return this.text;
    }
}