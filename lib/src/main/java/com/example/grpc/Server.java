package com.example.grpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import org.w3c.dom.NameList;

import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Jwts;

public class Server extends StudentServiceGrpc.StudentServiceImplBase {
	private io.grpc.Server server;
	
	 private static final Logger logger = Logger.getLogger(Server.class.getName());
	 
	public Server() {
	}
	
	private void start(int port) throws IOException {
		SecretKey masterKey = Jwts.SIG.HS256.key().build();
		DataServiceImpl service = new DataServiceImpl(masterKey);
		 AuthInterceptor authInterceptor = new AuthInterceptor(masterKey);
		
		
		server = ServerBuilder.forPort(port)
                .addService(ServerInterceptors.intercept(service, authInterceptor))
                .build()
                .start();
		
		logger.info("gRPC server start / port :" + port);
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.warning("detected server stop sign, server is stopping..");
            Server.this.stop();
            logger.info("stopping server completed.");
        }));
	}
	
	 private void blockUntilShutdown() throws InterruptedException {
	        if (server != null) {
	            server.awaitTermination();
	        }
	    }
	 
	 public static void main(String[] args) throws IOException, InterruptedException {
	        final Server server = new Server();
	        System.out.println("server is ready");
	        server.start(50051);
	        server.blockUntilShutdown();
	    }
	 
	 private void stop() {
	        if (server != null) {
	            server.shutdown();
	        }
	    }
	 

}


