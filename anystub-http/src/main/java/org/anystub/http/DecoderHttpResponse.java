package org.anystub.http;

import org.anystub.Decoder;
import org.apache.http.HttpResponse;

public class DecoderHttpResponse implements Decoder<HttpResponse> {
    @Override
    public HttpResponse decode(Iterable<String> iterable) {
        return HttpUtil.decode(iterable);
    }

}
