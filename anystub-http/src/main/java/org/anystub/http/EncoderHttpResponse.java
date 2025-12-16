package org.anystub.http;

import org.anystub.Encoder;
import org.apache.http.HttpResponse;

public class EncoderHttpResponse implements Encoder<HttpResponse> {
    @Override
    public Iterable<String> encode(HttpResponse httpResponse) {
        return HttpUtil.encode(httpResponse);
    }
}
