package it.heron.hpet.database;

import it.heron.hpet.main.PetPlugin;
import it.heron.hpet.pettypes.NoPetType;
import it.heron.hpet.pettypes.PetType;

import java.util.UUID;


public abstract class AbstractDatabase implements Database {

    protected PetPlugin PLUGIN;

    public AbstractDatabase(PetPlugin PLUGIN) {
        this.PLUGIN = PLUGIN;
    };

}

