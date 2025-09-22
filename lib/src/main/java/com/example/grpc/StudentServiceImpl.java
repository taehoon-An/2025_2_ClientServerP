package com.example.grpc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.grpc.stub.StreamObserver;

public class StudentServiceImpl extends StudentServiceGrpc.StudentServiceImplBase {
	protected static StudentModelList studentList;
	
	public StudentServiceImpl(StudentModelList studentList) {
        this.studentList = studentList;
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
