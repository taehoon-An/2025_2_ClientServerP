
package com.example.grpc;

import io.grpc.*;
import java.util.function.Supplier;

//ºñ¼­ °´Ã¼
public class JwtAuthInterceptor implements ClientInterceptor {

    private final Supplier<String> tokenSupplier;

    public JwtAuthInterceptor(Supplier<String> tokenSupplier) {
        this.tokenSupplier = tokenSupplier;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                String token = tokenSupplier.get();
                if (token != null && !token.isEmpty()) {
                    headers.put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), "Bearer " + token);
                }
                super.start(responseListener, headers);
            }
        };
    }
}