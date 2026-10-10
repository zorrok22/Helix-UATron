package pages.ITSM.ITSM;

public interface ElementPath {

    //General Element ID xpath
    String elementWithID = "(//*[@id='%s'])";
    String elementWithARID = "(//*[@arid='%s'])";
    String linkWithID = "(//a[@id='%s'])";
    String linkWithARID = "(//a[@arid='%s'])";

}