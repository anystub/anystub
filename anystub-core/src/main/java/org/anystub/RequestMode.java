package org.anystub;

/**
 * Specifies behaviour of a stub
 */

public enum RequestMode {
    /**
     * Use case: general using of cache.
     * request is sent to real system if it is not found in cache
     */
    rmNew,

    /**
     * Use case: strict check.
     * sending requests to real system is forbidden.
     * cache is loaded immediately. if request is not found in cache then exception is thrown
     */
    rmNone,

    /**
     * Use case: very strict check.
     * if stub file is new it allows to send all requests to a real system and record it
     * <p>
     * if stub-file exists
     * sending requests to real system is forbidden
     * cache is loaded immediately.
     * use each response from cache once
     */
    rmTrack,

    /**
     * Use case: logging from upstream.
     * all requests are sent to real system. all responses recorded to cache (duplicates may be created in stub-file)
     * old stub-file overridden
     */
    rmAll,

    /**
     * Use case: check anystub works properly
     * all requests are sent to upstream, results return with no mutations
     */
    rmPassThrough,

    /**
     * Stub behaves similar to rmNew - if request is missing in stub it sends to the real system.
     * But if real system fails to respond stub will generate a fake object. fake objects saves in stub
     * (!) Not supported by all request functions. rmFake works if request function has returning type in arguments
     * Use case: real system is not available, but API-spec is available
     */
    rmFake

}