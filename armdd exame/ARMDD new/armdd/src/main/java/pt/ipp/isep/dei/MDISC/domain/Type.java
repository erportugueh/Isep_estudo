package pt.ipp.isep.dei.MDISC.domain;

public enum Type {
    PERSON("person"),
    ORGANIZATION("organization"),
    POSITION("position"),
    ASSET("asset");

    private final String Type;

    Type(String type){
        this.Type = type;
    }

    public String getType(){
        return Type;
    }
}
