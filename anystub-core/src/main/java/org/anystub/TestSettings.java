package org.anystub;

public class TestSettings {

    /**
     * list of headers to save for all requests
     */
    public final String[] headers;

    /**
     * patterns, if URL match any of the trigger request body saves in stub
     */
    public final String[] bodyTrigger;

    /**
     * pattern, to cut off out of the request body when saves in stub
     * Use it to cut off variable part of the request body: ex. timestamp, random sequences, secrets
     */
    public final String[] requestMask;

    /**
     * http-methods to include request body in request key (case-sensitive)
     */
    public final String[] bodyMethods;

    /**
     *
     */
    public final boolean testFilePrefix;

    /**
     * reserved for server storage-mode
     */
    public final boolean packagePrefix = false;
    /**
     * reserved for server storage-mode
     */
    public final String stubServer = "";

    private TestSettings(String[] headers, String[] bodyTrigger, String[] requestMask, String[] bodyMethods, boolean testFilePrefix) {
        this.headers = headers;
        this.bodyTrigger = bodyTrigger;
        this.requestMask = requestMask;
        this.bodyMethods = bodyMethods;
        this.testFilePrefix = testFilePrefix;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        String[] headers;
        String[] bodyTrigger;

        String[] requestMask;

        String[] bodyMethods;

        public boolean testFilePrefix;

        public Builder setHeaders(String[] headers) {
            this.headers = headers;
            return this;
        }

        public Builder setBodyTrigger(String[] bodyTrigger) {
            this.bodyTrigger = bodyTrigger;
            return this;
        }

        public Builder setRequestMask(String[] requestMask) {
            this.requestMask = requestMask;
            return this;
        }

        public Builder setBodyMethods(String[] bodyMethods) {
            this.bodyMethods = bodyMethods;
            return this;
        }

        public Builder setTestFilePrefix(boolean testFilePrefix) {
            this.testFilePrefix = testFilePrefix;
            return this;
        }

        public TestSettings build() {
            return new TestSettings(
                    headers,
                    bodyTrigger,
                    requestMask,
                    bodyMethods,
                    testFilePrefix);
        }
    }
}
