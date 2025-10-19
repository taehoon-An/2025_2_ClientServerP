package com.example.grpc;

import io.grpc.Context;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;

//ÆÄ½Ì(ÅäÅ« °ËÁõ) °´Ã¼
public class AuthInterceptor implements ServerInterceptor {
    private final SecretKey scKey;

    public AuthInterceptor(SecretKey masterKey) {
		this.scKey = masterKey;
	}

	@Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        String fullMethodName = call.getMethodDescriptor().getFullMethodName();

        if (isPublicEndpoint(fullMethodName)) {
            return next.startCall(call, headers);
        }

        String authHeader = headers.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.err.println("Request to protected endpoint without token: " + fullMethodName);
            call.close(Status.UNAUTHENTICATED.withDescription("Authorization token is missing or invalid"), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }

        try {
            String token = authHeader.substring(7);

            Claims claims = Jwts.parser()
                                .verifyWith(this.scKey)
                                .build()
                                .parseSignedClaims(token)
                                .getPayload();

            System.out.println("Authenticated request for user: " + claims.getSubject());
            return next.startCall(call, headers);

        } catch (Exception e) {

            System.err.println("Token validation failed: " + e.getMessage());
            call.close(Status.UNAUTHENTICATED.withDescription("Token is expired or invalid"), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }
    }

    private boolean isPublicEndpoint(String fullMethodName) {
        return fullMethodName.equals("grpc.sample.StudentService/Login") ||
                fullMethodName.equals("grpc.sample.StudentService/CheckSignUpAccount") ||
                fullMethodName.equals("grpc.sample.StudentService/AddAccount") ||
                fullMethodName.equals("grpc.sample.StudentService/RefreshAccessToken");
    }
}