package org.anystub;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * keeps cache of running requests
 * if the same request comes in non-blocking mode it will be reverted to
 * key in the map is composite - stub-filename+all+fields
 */
public class  RequestCache<T> {



    private ConcurrentMap<QueryKey, Mono<T>> m = new ConcurrentHashMap<>();

    public Mono<T> track(Base base, List<String> key, Mono<T> candidate) {
        if (!base.seekInCache()) {
            return candidate;
        }
        return m.computeIfAbsent(new QueryKey(base.getFilePath(), key), tk -> candidate);
    }

}
