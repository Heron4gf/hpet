package it.heron.hpet.utils.heads;

public abstract class HeadFromString extends AbstractHead {

    protected String value;

    /**
     * Initializes the HeadFromString instance with the specified string value.
     *
     * @param value the string value to associate with this head
     */
    public HeadFromString(String value) {
        this.value = value;
    }

}
