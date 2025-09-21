package com.example.grpc;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.NameList;

import com.example.grpc.NameServiceGrpc.NameServiceImplBase;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class Server {
	private io.grpc.Server server;
	
	private void start(int port) throws IOException {
		server = ServerBuilder.forPort(port)
                .addService(new NameServiceImpl()) // ���ο� ����(�丮��) ���
                .build()
                .start();
	}
	
	 private void blockUntilShutdown() throws InterruptedException {
	        if (server != null) {
	            server.awaitTermination();
	        }
	    }
	 
	 static class NameServiceImpl extends NameServiceImplBase {
	        private final ArrayList<String> nameList = new ArrayList<>();

	        @Override
	        public void addName(NameRequest req, StreamObserver<NameResponse> responseObserver) {
	            String name = req.getName();
	            nameList.add(name); // RMI�� ������ ����
	            System.out.println(name + " add Complite");

	            // Ŭ���̾�Ʈ���� ������ ����
	            NameResponse reply = NameResponse.newBuilder().setMessage(name + "add Complite").build();
	            responseObserver.onNext(reply);
	            responseObserver.onCompleted();
	        }
	        
	        @Override
		     public void readName(NameRequest req, StreamObserver<NameResponse> responseObserver) {
		         String name = null;
		         for(int i = 0; i < nameList.size(); i++) {
		        	 if(req.getName().equals(nameList.get(i))) {
		        		 name = nameList.get(i);
		        	 }
		         }
		        	 
		         if(name == null) {
		        	 NameResponse reply = NameResponse.newBuilder().setMessage("Cant found this name : " + req.getName()).build();
			         responseObserver.onNext(reply);
			         responseObserver.onCompleted();
		         } else {
		        	 // Ŭ���̾�Ʈ���� ������ ����
			         NameResponse reply = NameResponse.newBuilder().setMessage("read Complite : " + name).build();
			         responseObserver.onNext(reply);
			         responseObserver.onCompleted();
		         }
		
		         
		     }
	    }
		 
		
	 
	 public static void main(String[] args) throws IOException, InterruptedException {
	        final Server server = new Server();
	        System.out.println("server is ready");
	        server.start(50051);
	        server.blockUntilShutdown();
	    }
	 

}


