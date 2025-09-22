package com.example.grpc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.grpc.stub.StreamObserver;

public class CourseServiceImpl extends StudentServiceGrpc.StudentServiceImplBase {
	protected static CourseModelList courseList;
	
	public CourseServiceImpl(CourseModelList courseList) {
        this.courseList = courseList;
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
}


