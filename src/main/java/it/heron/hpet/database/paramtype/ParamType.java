package it.heron.hpet.database.paramtype;

public enum ParamType {
    OBJECT,BOOLEAN,INT,DOUBLE,STRING;

    public static ParamType whatParamTypeIsThis(String parameter) {
        if(!parameter.contains(":")) return ParamType.STRING;
        String type = parameter.split(":")[0];
        return ParamType.valueOf(type);
    }
}
