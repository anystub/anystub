package org.anystub.mgmt;

import org.anystub.AnyStubFileLocator;
import org.anystub.AnyStubId;
import org.anystub.Base;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class BaseManagerImpl implements BaseManager {
    private static final BaseManager baseManager = new BaseManagerImpl();
    private static final ConcurrentHashMap<String, Base> list = new ConcurrentHashMap<>();
    public static final String DEFAULT_STUB_PATH = new File("src/test/resources/anystub/stub.yml").getPath();
    public static final String DEFAULT_PATH = new File("src/test/resources/anystub").getPath();

    public static BaseManager instance() {
        return baseManager;
    }

    protected BaseManagerImpl() {

    }

    /**
     * returns default stub
     *
     * @return
     */
    public Base getBase() {
        return getBase(DEFAULT_STUB_PATH);
    }

    /**
     * returns stub with specific path and rmNew Constrains
     *
     * @param filename
     * @return
     */
    public Base getBase(String filename) {
        return getBase(filename, base -> {});
    }

    /**
     * returns stub for given filePath,
     * creates new one if non created before, for every newly created executes initializer
     *
     * @param filename stub-file
     * @param initializer post constructor, invokes only when new base created
     * @return
     */
    public Base getBase(String filename, Consumer<Base> initializer){

        String fullPath = filename == null || filename.isEmpty() ?
                DEFAULT_STUB_PATH :
                getFilePath(filename);

        return list.computeIfAbsent(fullPath, p -> {
                Base base = new Base(fullPath);
                initializer.accept(base);
                return base;
        });
    }

    /**
     * returns path for default stub
     *
     * @return
     */
    public static String getFilePath(String filename) {
        File file = new File(filename);
        if (file.getParentFile() == null || file.getParent().isEmpty()) {
            return new File(DEFAULT_PATH,  file.getPath()).getPath();
        }
        return file.getPath();
    }


    /**
     * returns stub for current test
     *
     * @return stub for current test
     */
    public Base getStub() {
        AnyStubId s = AnyStubFileLocator.discoverFile();
        if (s != null) {
            return getBase(s.filename(), base -> base.constrain(s.requestMode()));
        }

        return getBase();
    }

    @Override
    public Base getStub(String suffix) {
        AnyStubId s = AnyStubFileLocator.discoverFile(suffix);
        if (s != null) {
            return getBase(s.filename(), base -> base.constrain(s.requestMode()));
        }

        return getBase();
    }

}
