package com.example.grpc;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.NameList;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class Server extends StudentServiceGrpc.StudentServiceImplBase {
	private io.grpc.Server server;
	
	public Server() {
	}
	
	private void start(int port) throws IOException {
		server = ServerBuilder.forPort(port)
                .addService(new DataServiceImpl())
                .build()
                .start();
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
	 

}


