package org.anystub;

import java.lang.annotation.Annotation;

public class AnyStubIdData implements AnyStubId {

    private final String filename;
    private final RequestMode requestMode;
    private final String[] paramMasks;
    private final String config;

    public AnyStubIdData(String filename,
                         RequestMode requestMode,
                         String[] paramMasks,

                         String config) {
        this.filename = filename;
        this.requestMode = requestMode;
        this.paramMasks = paramMasks;
        this.config = config;
    }

    @Override
    public String filename() {
        return filename;
    }

    @Override
    public RequestMode requestMode() {
        return requestMode;
    }

    @Override
    public String[] requestMasks() {
        return paramMasks;
    }

    @Override
    public String config() {
        return config;
    }

    public Class<? extends Annotation> annotationType() {
        return AnyStubIdData.class;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
          private String filename;
          private RequestMode requestMode;
          private String[] paramMasks;
          private String config;

        public Builder setFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder setRequestMode(RequestMode requestMode) {
            this.requestMode = requestMode;
            return this;
        }

        public Builder setParamMasks(String[] paramMasks) {
            this.paramMasks = paramMasks;
            return this;
        }

        public Builder setConfig(String config) {
            this.config = config;
            return this;
        }

        public AnyStubIdData build() {
            return new AnyStubIdData(
                    filename,
                    requestMode,
                    paramMasks,
                    config);
        }
    }
}
