package com.example.grpc;

import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.NameList;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class Server extends StudentServiceGrpc.StudentServiceImplBase {
	private io.grpc.Server server;
	 private final StudentModelList studentList;
	 private final CourseModelList courseList;
	
	 public Server() throws IOException {
	        this.studentList = StudentModelList.fromFile("Students.txt");
	        this.courseList = CourseModelList.fromFile("Courses.txt");
	        
	        System.out.println("read file success : " + this.studentList.getString(0));
	    }
	
	private void start(int port) throws IOException {
		server = ServerBuilder.forPort(port)
                .addService(new StudentServiceImpl(studentList)) 
                .addService(new CourseServiceImpl(courseList))
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


