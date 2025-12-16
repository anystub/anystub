package org.anystub.mgmt;

import org.anystub.Base;

import java.util.function.Consumer;

public interface BaseManager {

    /**
     * returns stub with specific path
     *
     * @param filename stub file
     * @return
     */
    Base getBase(String filename);

    /**
     * returns stub with specific path and specified Constrains
     *
     * @param filename stub-file
     * @param initializer post constructor, invokes only when new base created
     * @return
     */
    Base getBase(String filename, Consumer<Base> initializer);

    /**
     * returns default stub
     *
     * @return
     */
    Base getBase();

    /**
     * returns stub depends on context
     *
     * @return
     */
    Base getStub();
    Base getStub(String suffix);
}
