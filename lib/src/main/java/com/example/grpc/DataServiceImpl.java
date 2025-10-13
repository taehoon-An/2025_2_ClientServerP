package com.example.grpc;

import java.io.IOException;
import java.util.ArrayList;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;


public class DataServiceImpl extends StudentServiceGrpc.StudentServiceImplBase{

	protected static StudentModelList studentList;
	protected static CourseModelList courseList;
	
	public DataServiceImpl() {
		
		try {
			studentList = new StudentModelList("C:/Users/TehoonAhn/eclipse-workspace/gRPCServer/Students.txt");
			courseList = new CourseModelList("C:/Users/TehoonAhn/eclipse-workspace/gRPCServer/Courses.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	@Override
	public void setAllStudents(GetStudentRequest request, StreamObserver<StudentResponse> responseObserver) {
		boolean success = studentList.addStudentRecords(request);

        if (success) {
            StudentResponse response = StudentResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Failed to add or write student record to file.")
                    .asRuntimeException()
            );
        }

	}

	@Override
	public void setAllCourses(GetCourseRequest request, StreamObserver<CourseResponse> responseObserver) {
		boolean success = courseList.addCourseRecords(request);

        if (success) {
            CourseResponse response = CourseResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Failed to add or write course record to file.")
                    .asRuntimeException()
            );
        }
	}
	
	@Override
	public void deleteStudent(GetSelStudentIdRequest request, StreamObserver<DeleteStudentResponse> responseObserver) {
		boolean success = studentList.deleteStudentRecords(request);

        if (success) {
            DeleteStudentResponse response = DeleteStudentResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Failed to Delete or write Student record to file.")
                    .asRuntimeException()
            );
        }
	}

	@Override
	public void deleteCourse(GetSelCourseIdRequest request, StreamObserver<DeleteCourseResponse> responseObserver) {
		boolean success = courseList.deleteCourseRecords(request);

        if (success) {
            DeleteCourseResponse response = DeleteCourseResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Failed to Delete or write Course record to file.")
                    .asRuntimeException()
            );
        }
	}

	
}
