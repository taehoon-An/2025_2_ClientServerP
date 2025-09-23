package com.example.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client {
    private final ManagedChannel channel;
    private final StudentServiceGrpc.StudentServiceBlockingStub blockingStub;

    public Client(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = StudentServiceGrpc.newBlockingStub(channel); 
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Client client = new Client("localhost", 50051);
        try {
        	String sChoice = "";
        	while(sChoice != "0") {
        		BufferedReader objReader = new BufferedReader(new InputStreamReader(System.in)); 
            	client.printMenu();
    			
    			sChoice = objReader.readLine().trim();
    			
    			if(sChoice.equals("1")) {
    				client.printStudentsList(client.blockingStub);
    			} else if(sChoice.equals("2")) {
    				client.printCoursesList(client.blockingStub);
    			} else if(sChoice.equals("0")) {
    				break;
    			} else {
    				System.out.println("This is wrong Input, Please selecting correct Input.");
    			}
        	}
        	
        } finally {
            client.shutdown();
        }
    }

	private void printMenu() {
		System.out.println("******************** MENU **********************");
		System.out.println("1. Print List Students");
		System.out.println("2. Print List Courses");
		System.out.println("0. Exit");
	}

	private void printCoursesList(StudentServiceGrpc.StudentServiceBlockingStub blockingStub) {
		GetAllCourseRequest cRequest = GetAllCourseRequest.newBuilder().build();
		CourseListResponse cResponse = blockingStub.getAllCourses(cRequest);
		System.out.println("*** Server Courses List ***");
		cResponse.getCoursesList().forEach(course -> {
			System.out.print("Course code: " + course.getCourseId() + " | Professor : " + course.getName()+ 
			" | Course Name : " + course.getCourseName() + "\n");
			if(course.getRelatedCoursesList().size() > 0) {
				System.out.print("Related Courses : " + course.getRelatedCoursesList() + "\n\n");
			} else {
				System.out.println();
			}
		});
	}
	
	private void printStudentsList(StudentServiceGrpc.StudentServiceBlockingStub blockingStub) {
		GetAllStudentRequest sRequest = GetAllStudentRequest.newBuilder().build();
		StudentListResponse sResponse = blockingStub.getAllStudents(sRequest);
		System.out.println("*** Server Students List ***");
		sResponse.getStudentsList().forEach(course -> {
			System.out.print("Student code :: " + course.getStudentId() + " | Student Name :: " + course.getFirstName() + " "
					+ course.getLastName() + 
			" | Department : " + course.getDepartment() + "\n");
			if(course.getCompletedCoursesList().size() > 0) {
				System.out.print("Completed Courses : " + course.getCompletedCoursesList() + "\n\n");
			} else {
				System.out.println();
			}
		});
	}
}