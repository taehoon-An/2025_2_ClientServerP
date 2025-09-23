package com.example.grpc;

import java.util.ArrayList;

import io.grpc.stub.StreamObserver;


public class DataServiceImpl extends StudentServiceGrpc.StudentServiceImplBase{
	protected static StudentModelList studentList;
	protected static CourseModelList courseList;
	
	public DataServiceImpl() {
		
		studentList = StudentModelList.fromFile("Students.txt");
		courseList = CourseModelList.fromFile("Courses.txt");
		
		System.out.println("data is ready.");
		System.out.println(studentList.getString(0) + "  \n" + courseList.getString(0));
		
	}
	
	@Override
	public void getAllCourses(GetAllCourseRequest request, StreamObserver<CourseListResponse> responseObserver) {
		ArrayList<Course> allCourseModels = courseList.getAllCourseRecords();
		
		CourseListResponse response = CourseListResponse.newBuilder()
				.addAllCourses(allCourseModels)
				.build();
		
		responseObserver.onNext(response);
        responseObserver.onCompleted();
	}
	
	@Override
	public void getAllStudents(GetAllStudentRequest request, StreamObserver<StudentListResponse> responseObserver) {
		ArrayList<Student> allStudentModels = studentList.getAllStudentRecords();
		
		StudentListResponse response = StudentListResponse.newBuilder()
				.addAllStudents(allStudentModels)
				.build();
		
		responseObserver.onNext(response);
        responseObserver.onCompleted();
	}
}
