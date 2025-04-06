package it.heron.hpet.database;

import it.heron.hpet.main.PetPlugin;


public abstract class AbstractDatabase implements Database {

    protected PetPlugin PLUGIN;

    public AbstractDatabase(PetPlugin PLUGIN) {
        this.PLUGIN = PLUGIN;
    };

}

