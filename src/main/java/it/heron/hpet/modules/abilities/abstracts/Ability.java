package it.heron.hpet.modules.abilities.abstracts;

import it.heron.hpet.modules.pets.userpets.abstracts.UserPet;

public interface Ability {

    /**
 * Executes the ability on the specified UserPet.
 *
 * @param userPet the UserPet instance on which the ability is performed
 */
void execute(UserPet userPet);

}
