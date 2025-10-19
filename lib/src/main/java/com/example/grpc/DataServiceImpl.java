package com.example.grpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;




public class DataServiceImpl extends StudentServiceGrpc.StudentServiceImplBase{
	private static final Logger logger = Logger.getLogger(DataServiceImpl.class.getName());

	protected static StudentModelList studentList;
	protected static CourseModelList courseList;
	protected static AccountModelList accountList;
	
	protected static BCryptPasswordEncoder passwordEncoder;
	private final SecretKey scKey;
	
	public DataServiceImpl(SecretKey masterKey) {
		this.scKey = masterKey;
		passwordEncoder = new BCryptPasswordEncoder();
		
		try {
			studentList = new StudentModelList("C:/Users/TehoonAhn/eclipse-workspace/gRPCServer/Students.txt");
			courseList = new CourseModelList("C:/Users/TehoonAhn/eclipse-workspace/gRPCServer/Courses.txt");
			accountList = new AccountModelList("C:/Users/TehoonAhn/eclipse-workspace/gRPCServer/Account.txt");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("data is ready.");
		System.out.println(studentList.getString(0) + "  \n" + courseList.getString(0));
		
	}
	
	@Override
	public void getAllCourses(GetAllCourseRequest request, StreamObserver<CourseListResponse> responseObserver) {
		logger.info("logging Get Course receive ");
		ArrayList<Course> allCourseModels = courseList.getAllCourseRecords();
		
		CourseListResponse response = CourseListResponse.newBuilder()
				.addAllCourses(allCourseModels)
				.build();
		
		logger.info("Get Courses Success : " + response.getCoursesList().hashCode());
		
		responseObserver.onNext(response);
        responseObserver.onCompleted();
	}
	
	@Override
	public void getAllStudents(GetAllStudentRequest request, StreamObserver<StudentListResponse> responseObserver) {
		logger.info("logging Get Student receive ");
		ArrayList<Student> allStudentModels = studentList.getAllStudentRecords();
		
		StudentListResponse response = StudentListResponse.newBuilder()
				.addAllStudents(allStudentModels)
				.build();
		
		logger.info("Get Students Success : " + response.getStudentsList().hashCode());
		
		responseObserver.onNext(response);
        responseObserver.onCompleted();
	}
	
	@Override
	public void setAllStudents(GetStudentRequest request, StreamObserver<StudentResponse> responseObserver) {
		logger.info("logging add Student receive ");
		boolean success = studentList.addStudentRecords(request);

        if (success) {
            StudentResponse response = StudentResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            logger.info("add Students success");
        } else {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Failed to add or write student record to file.")
                    .asRuntimeException()
            );
            logger.warning("Failed to add or write student record to file.");
        }

	}

	@Override
	public void setAllCourses(GetCourseRequest request, StreamObserver<CourseResponse> responseObserver) {
		logger.info("logging add Course receive ");
		boolean success = courseList.addCourseRecords(request);

        if (success) {
            CourseResponse response = CourseResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            logger.info("add Courses success");
        } else {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Failed to add or write course record to file.")
                    .asRuntimeException()
            );
            logger.warning("Failed to add or write course record to file.");
        }
	}
	
	@Override
	public void deleteStudent(GetSelStudentIdRequest request, StreamObserver<DeleteStudentResponse> responseObserver) {
		logger.info("logging student delete receive : " + request.getStudentId());
		
		boolean success = studentList.deleteStudentRecords(request);
		DeleteStudentResponse.Builder response = DeleteStudentResponse.newBuilder();

        if (!success) {
        	response.setMessage(request.getStudentId() + "<< is not existed Id. please retry typying delete Id.").build();
        	logger.severe(response.getMessage());
        } else {
        	response.setMessage("Delete Student task success");
        	logger.info(response.getMessage());
        }
        
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
	}

	@Override
	public void deleteCourse(GetSelCourseIdRequest request, StreamObserver<DeleteCourseResponse> responseObserver) {
		logger.info("logging course delete receive : " + request.getCourseId());
		
		boolean success = courseList.deleteCourseRecords(request);
		DeleteCourseResponse.Builder response = DeleteCourseResponse.newBuilder();

        if (!success) {
           response.setMessage(request.getCourseId() + "<< is not existed Id. please retry typying delete Id."); 
           logger.severe(response.getMessage());
        } else {
        	response.setMessage("Delete Course task success");
        	logger.info(response.getMessage());
        }
        
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
	}
	
	@Override
	public void checkSignUpAccount(CheckAccountRequest request, StreamObserver<CheckAccountResponse> responseObserver) {
		logger.info("logging Check Sign Up receive : " + request.getAccount());
	
		ArrayList<Account> allAccountModels = this.accountList.getAllAccountsRecords();
		
		CheckAccountResponse.Builder response = CheckAccountResponse.newBuilder();
		if(request.getAccount().getAcPermiss() == 0) {
			response.setCheck(false)
			.setMessage("Permiss is not Access. try to regist");
			logger.severe(response.getMessage());
		} else {
			for(int i = 0; i < allAccountModels.size(); i++) {
				if(allAccountModels.get(i).getAcId().equals(request.getAccount().getAcId())) {
					response.setCheck(false)
							.setMessage(request.getAccount().getAcId() + "<< Id is already existed. Try again to Sign up.");
					logger.severe(response.getMessage());
				} 
			}
			
			if(response.getMessage().isEmpty()) {
				response.setCheck(true)
						.setMessage("Sign Up is Completed. " + request.getAccount());
				logger.info(response.getMessage());
			}
		}
		
		responseObserver.onNext(response.build());
        responseObserver.onCompleted();
		
	}
	
	@Override
	public void addAccount(AddAccountRequest request, StreamObserver<AddAccountResponse> responseObserver) {
		logger.info("logging add Account receive ");
		
		boolean success = this.accountList.addAccountRecords(request);

        if (success) {
        	if(request.getAccount().getAcPermiss() == 2) {
        		 Student newStudent = Student.newBuilder()
                         .setStudentId(request.getAccount().getAcId())
                         .setFirstName(request.getAccount().getFirstName())
                         .setLastName(request.getAccount().getLastName())
                         .setDepartment(request.getAccount().getDepartment())
                         .build();
        		 
        		 this.studentList.addStudentRecords(GetStudentRequest.newBuilder().setStudent(newStudent).build());
        		 
        	}
        	AddAccountResponse response = AddAccountResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            logger.info("add Account success");
        } else {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Failed to add or write student record to file.")
                    .asRuntimeException()
            );
            logger.warning("Failed to add or write student record to file.");
        }
	}

	@Override
	public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
		logger.info("logging Login receive ");

		ArrayList<Account> allAccountModels = this.accountList.getAllAccountsRecords();
		LoginResponse.Builder LResponse = LoginResponse.newBuilder();
		
		boolean tempPwCheck = false;
		boolean tempIdCheck = false;
		
		for(int i = 0; i < allAccountModels.size(); i++) {
			String tempId = allAccountModels.get(i).getAcId();
			String tempPw = allAccountModels.get(i).getAcPw();
			
			if(tempId.equals(request.getAcId())) {
				System.out.println("id OK");
				tempIdCheck = true;
				if(this.passwordEncoder.matches(request.getAcPw(), tempPw)) {
					System.out.println("pw OK");	
					tempPwCheck = true;
					
					String acToken = createAccessToken(request.getAcId());
					String rfToken = createRefreshToken(request.getAcId());
			        
			        LResponse.setAccessToken(acToken)
			        		.setRefreshToken(rfToken)
			        		.setAcId(tempId)
			        		.setAcName(allAccountModels.get(i).getLastName())
			        		.setAcPermiss(allAccountModels.get(i).getAcPermiss())
			        		.setCheck(true)
			        		.setMessage("Login Success");
		            
			        logger.info("Login success | Access Token : " + acToken + " / Refresh Token : " + rfToken);
			        break;
				} 
			}
		}
		
		if(tempIdCheck && !tempPwCheck) {
			LResponse.setMessage("Login Fail : Password does not match");
			logger.severe(LResponse.getMessage());
		} else if(!tempIdCheck && !tempPwCheck) {
			LResponse.setMessage("Login Fail : ID does not match or not exist");
			logger.severe(LResponse.getMessage());
		}
		
		
		responseObserver.onNext(LResponse.build());
        responseObserver.onCompleted();
        

	}
	
	@Override
	public void refreshAccessToken(RefreshTokenRequest request, StreamObserver<RefreshTokenResponse> responseObserver) {
	    String refreshToken = request.getRefreshToken();
	    logger.info("Refresh token request received");

	    try {
	        Claims claims = Jwts.parser()
	                            .verifyWith(this.scKey)
	                            .build()
	                            .parseSignedClaims(refreshToken)
	                            .getPayload();

	        String userId = claims.getSubject();

	        String newAccessToken = createAccessToken(userId);

	        RefreshTokenResponse response = RefreshTokenResponse.newBuilder()
	                .setAccessToken(newAccessToken)
	                .build();
	        
	        responseObserver.onNext(response);
	        responseObserver.onCompleted();
	        logger.info("Access token refreshed for user: " + userId);

	    } catch (Exception e) {
	        logger.warning("Invalid refresh token: " + e.getMessage());
	        responseObserver.onError(
	            Status.UNAUTHENTICATED
	                  .withDescription("Invalid Refresh Token. Please log in again.")
	                  .asRuntimeException()
	        );
	    }
	}
	
	@Override
	public void applyCourse(ApplyCourseReqeust request, StreamObserver<ApplyCourseResponse> responseObserver) {
	    logger.info("Apply Course request received for student: " + request.getApplyAcId());
	    ApplyCourseResponse.Builder response = ApplyCourseResponse.newBuilder();

	    ArrayList<Student> studentRecords = this.studentList.getAllStudentRecords();
	    int targetStudentIndex = -1;
	    Student targetStudent = null;

	    for (int i = 0; i < studentRecords.size(); i++) {
	        if (studentRecords.get(i).getStudentId().equals(request.getApplyAcId())) {
	            targetStudent = studentRecords.get(i);
	            targetStudentIndex = i;
	            break;
	        }
	    }

	    if (targetStudent != null) {
	        Set<String> updatedCourses = new HashSet<>(targetStudent.getCompletedCoursesList());
	        updatedCourses.addAll(request.getApplyCoursesList());

	        Student updatedStudent = Student.newBuilder(targetStudent)
	                .clearCompletedCourses()
	                .addAllCompletedCourses(updatedCourses)
	                .build();

	        this.studentList.getAllStudentRecords().set(targetStudentIndex, updatedStudent);
	        this.studentList.writeToFile(); 

	        logger.info("Successfully applied courses for student: " + request.getApplyAcId());
	        response.setIsApplied(true).setMessage("Courses successfully applied.");

	    } else {
	        logger.warning("Apply course failed. Student not found: " + request.getApplyAcId());
	        response.setIsApplied(false).setMessage("Student with ID " + request.getApplyAcId() + " not found.");
	    }

	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	}
	
	private String createAccessToken(String userId) {
		long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expMillisHour = nowMillis + TimeUnit.MINUTES.toMillis(15);
        Date expH = new Date(expMillisHour);
        
		String acToken = Jwts.builder()
        		.subject(userId)
        		.expiration(expH)
        		.signWith(scKey)
        		.compact();
        
		return acToken;
	}

	private String createRefreshToken(String userId) {
		long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expMillisDay = nowMillis + TimeUnit.DAYS.toMillis(1);
        Date expD = new Date(expMillisDay);
        
        String rfToken = Jwts.builder()
        		.subject(userId)
        		.expiration(expD)
        		.signWith(scKey)
        		.compact();
        
        return rfToken;
	}
	
}
