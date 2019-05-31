package org.anystub.mgmt;

import org.anystub.AnyStubFileLocator;
import org.anystub.AnyStubId;
import org.anystub.Base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BaseManagerImpl implements BaseManager {
    private static BaseManagerImpl baseManager = new BaseManagerImpl();
    private final List<Base> list = new ArrayList<>();
    private Consumer<Base> defaultInitializer = null;

    public static BaseManagerImpl instance() {
        return baseManager;
    }

    private BaseManagerImpl() {

    }


    public Base getBase() {
        return getBase(getFilePath());
    }

    public Base getBase(String filename) {

        String fullPath = ((filename == null) || filename.isEmpty()) ?
                getFilePath() :
                getFilePath(filename);

        return get(fullPath).orElseGet(new Supplier<Base>() {
            @Override
            public Base get() {
                Base base = new Base(fullPath, true);
                if (defaultInitializer != null) {
                    defaultInitializer.accept(base);
                }
                list.add(base);
                return base;
            }
        });
    }

    public void register(Base base) {
        if (get(base.getFilePath()).isPresent()) {
            throw new StubFileAlreadyCreatedException("Stub is already used. Consider to use BaseManager.getBase(name)", base.getFilePath());
        }
        list.add(base);

    }

    private Optional<Base> get(String fullFileName) {
        return list
                .stream()
                .filter(base -> base.getFilePath().equals(fullFileName))
                .findFirst();
    }

    public static String getFilePath() {
        return new File("src/test/resources/anystub/stub.yml").getPath();
    }

    public static String getFilePath(String filename) {
        File file = new File(filename);
        if (file.getParentFile() == null || file.getParent().isEmpty()) {
            return new File("src/test/resources/anystub").getPath() + File.separator + file.getPath();
        }
        return file.getPath();
    }

    public static String getFilePath(String path, String filename) {
        return new File(path).getPath() + File.separator + new File(filename).getPath();
    }

    public static Base getStub(String filename) {
        return instance()
                .getBase(filename);
    }

    /**
     * returns stub for current test
     * @return stub for current test
     */
    public static Base getStub() {
        AnyStubId s = AnyStubFileLocator.discoverFile();
        if (s != null) {
            return BaseManagerImpl
                    .instance()
                    .getBase(s.filename())
                    .constrain(s.requestMode());
        }

        return BaseManagerImpl
                .instance()
                .getBase();
    }


    public static void setDefaultInitializer(Consumer<Base> defaultInitializer) {
        BaseManagerImpl.instance().defaultInitializer = defaultInitializer;
    }
}
