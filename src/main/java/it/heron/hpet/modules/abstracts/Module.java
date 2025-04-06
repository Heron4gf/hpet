package it.heron.hpet.modules.abstracts;

public interface Module {

    String name();
    void load();
    void unload();
    boolean isLoaded();

}
