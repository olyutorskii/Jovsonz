/*
 * JSON types
 *
 * License : The MIT License
 * Copyright(c) 2010 olyutorskii
 */

package jp.sourceforge.jovsonz;

import java.util.Objects;

/**
 * JSON types.
 */
public enum JsTypes {

    /** NUMBER. */
    NUMBER(  JsNumber .class ),
    /** STRING. */
    STRING(  JsString .class ),
    /** BOOLEAN. */
    BOOLEAN( JsBoolean.class ),
    /** ARRAY. */
    ARRAY(   JsArray  .class ),
    /** OBJECT. */
    OBJECT(  JsObject .class ),
    /** NULL. */
    NULL(    JsNull   .class ),
    ;


    private static final JsTypes[] VALUES = values();


    private final Class<? extends JsValue> klass;
    private final boolean isJsComposition;


    /**
     * Constructor.
     *
     * @param klass matched {@link java.lang.Class}
     */
    private JsTypes(Class<? extends JsValue> klass) {
        this.klass = klass;
        this.isJsComposition = JsComposition.class.isAssignableFrom(this.klass);
        return;
    }


    /**
     * Returns associated enum by {@link java.lang.Class}.
     *
     * @param carg instance of {@code java.lang.Class}
     * @return enum value. null if not associated argument.
     * @throws NullPointerException argument is null
     */
    public static JsTypes getJsTypes(Class<?> carg) {
        Objects.requireNonNull(carg);

        for (JsTypes types : VALUES) {
            if (types.klass == carg) return types;
        }

        return null;
    }

    /**
     * Returns associated {@link java.lang.Class} instance.
     *
     * @return instance of {@code java.lang.Class}
     */
    public Class<? extends JsValue> getJsClass() {
        return this.klass;
    }

    /**
     * Determine if this JSON type can have children.
     *
     * <p>Only OBJECT and ARRAY can have children.
     *
     * @return true if it is possible to have children
     */
    public boolean isComposition() {
        return this.isJsComposition;
    }

}
