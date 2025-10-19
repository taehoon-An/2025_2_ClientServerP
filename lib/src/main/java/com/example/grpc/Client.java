package com.example.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;

import io.grpc.Status;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.grpc.StudentServiceGrpc.StudentServiceBlockingStub;

public class Client {
    private final ManagedChannel channel;
    private final StudentServiceGrpc.StudentServiceBlockingStub blockingStub;
    
    private String acToken;
    private String rfToken;
    
    private String acId;
    private int acPermiss;
    private String acName;
    
    private BCryptPasswordEncoder passwordEncoder;
    
    public Client(String host, int port) {
    	this.passwordEncoder = new BCryptPasswordEncoder();
    	JwtAuthInterceptor authInterceptor = new JwtAuthInterceptor(() -> this.acToken);
    	
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .intercept(authInterceptor)
                .build();
        this.blockingStub = StudentServiceGrpc.newBlockingStub(channel); 
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Client client = new Client("localhost", 50051);
        BufferedReader objReader = new BufferedReader(new InputStreamReader(System.in)); 
        boolean isLogin = loginloop(objReader, client);
        
        if(isLogin) {
        	String input = "";
        	while(!input.equals("x")) {
    			input = printMenu(objReader, client);
        	}
        }

    }
    
    public <ReqT, RespT> RespT callGrpc(Function<ReqT, RespT> grpcCall, ReqT request) {
        try {
            return grpcCall.apply(request);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.UNAUTHENTICATED) {
                System.out.println("Access token may be expired. Attempting to refresh...");
                if (refreshAccessToken()) {
                    System.out.println("Token refreshed successfully. Retrying the request...");
                    return grpcCall.apply(request);
                } else {
                    throw new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Session expired. Please log in again."));
                }
            }
            throw e;
        }
    }
    
    private boolean refreshAccessToken() {
        try {
            RefreshTokenRequest request = RefreshTokenRequest.newBuilder().setRefreshToken(this.rfToken).build();
            RefreshTokenResponse response = this.blockingStub.refreshAccessToken(request);
            this.acToken = response.getAccessToken(); 
            return true;
        } catch (StatusRuntimeException e) {
            return false;
        }
    }
    
    private static boolean loginloop(BufferedReader objReader, Client client) throws IOException {
    	while (true) {
            System.out.println("******************** LOGIN **********************");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("x. Exit");
            String choice = objReader.readLine().trim();

            switch (choice) {
                case "1":
                    if (handleLogin(objReader, client)) { 
                        System.out.println("WELCOME.");
                        return true;
                    } else {
                        System.out.println("Fail to login. please re-check your id/pw");
                    }
                    break;
                case "2":
                    handleRegister(objReader, client.blockingStub, client.passwordEncoder); 
                    break;
                case "x":
                	serverClose(client);
                    return false; 
                default:
                    System.out.println("Invaild Choice.");
            }
        }
	}
    
    //login Panel
    private static boolean handleLogin(BufferedReader objReader, Client client) throws IOException {
    	System.out.println("*******Login********");
    	System.out.print("ID : ");
    	String Id = objReader.readLine().trim();
    	System.out.print("Password : ");
    	String Pw = objReader.readLine().trim();
    	
    	
    	LoginRequest LReq = LoginRequest.newBuilder()
    			.setAcId(Id)
    			.setAcPw(Pw)
    			.build();
    	
    	LoginResponse LResponse = client.blockingStub.login(LReq);
    	System.out.println(LResponse.getMessage());
    	
    	if(LResponse.getCheck()) {
    		client.acToken = LResponse.getAccessToken();
    		client.rfToken = LResponse.getRefreshToken();
    		client.acId = LResponse.getAcId();
    		client.acName = LResponse.getAcName();
    		client.acPermiss = LResponse.getAcPermiss();
    	}
    	return LResponse.getCheck();
    }
    
    private static boolean handleRegister(BufferedReader objReader, StudentServiceBlockingStub stub, BCryptPasswordEncoder passwordEncoder) throws IOException {
    	System.out.println("*******Sign Up********");
    	System.out.print("Type your ID : ");
    	String aId = objReader.readLine().trim();
    	System.out.print("Type your Password : ");
    	String aPw = objReader.readLine().trim();
    	System.out.print("First Name : ");
    	String aFname = objReader.readLine().trim();
    	System.out.print("Last Name : ");
    	String aLname = objReader.readLine().trim();
    	System.out.print("Department : ");
    	String aDepartment = objReader.readLine().trim();
    	int aPermiss = checkPermiss(objReader);
    	
    	String hashPw = passwordEncoder.encode(aPw);
    	
    	Account acInfo = Account.newBuilder()
    			.setAcId(aId)
    			.setAcPw(hashPw)
    			.setFirstName(aFname)
    			.setLastName(aLname)
    			.setDepartment(aDepartment)
    			.setAcPermiss(aPermiss)
    			.build();
    	
    	CheckAccountResponse checkResponse = 
    			stub.checkSignUpAccount(CheckAccountRequest.newBuilder().setAccount(acInfo).build());
    	System.out.println(checkResponse.getMessage());
    	
    	if(checkResponse.getCheck()) {
    		AddAccountResponse addResponse = stub.addAccount(AddAccountRequest.newBuilder().setAccount(acInfo).build());
    		System.out.println(addResponse.getMessage());
    		return addResponse.getCheck();
    	}
    	
    	return false;
    }

	private static int checkPermiss(BufferedReader objReader) throws IOException {
		boolean check = false;
    	int aPermiss = 0;
    	System.out.print("Are you admin? (Y/N) : ");
       	String checkPermiss = objReader.readLine().trim();
        if(checkPermiss.equalsIgnoreCase("Y")) {
        	check = true;
        	aPermiss = 1;
        } else if(checkPermiss.equalsIgnoreCase("N")) {
       		check = true;
       		aPermiss = 2;
       	} else {
       		System.out.println("Invaild Choice");
       	}
		return aPermiss;
	}
    
    
    
    // Main Panel
	private static void deleteCourse(Client client, BufferedReader objReader) throws IOException {
		System.out.print("Typing to delete Course Id : ");
		String cId = objReader.readLine().trim();
		
		GetSelCourseIdRequest request = GetSelCourseIdRequest.newBuilder().setCourseId(cId).build();
		
		DeleteCourseResponse res = client.callGrpc(client.blockingStub::deleteCourse, request);
		
		System.out.println(res.getMessage());
	}

	private static void deleteStudent(Client client, BufferedReader objReader) throws IOException {
		System.out.print("Typing to delete Student Id : ");
		String sId = objReader.readLine().trim();
		
		GetSelStudentIdRequest request = GetSelStudentIdRequest.newBuilder().setStudentId(sId).build();
		
		DeleteStudentResponse res = client.callGrpc(client.blockingStub::deleteStudent, request);
		
		System.out.println(res.getMessage());
	}

	private static void addCourse(Client client, BufferedReader objReader) throws IOException {
    	System.out.print("Course ID : ");
    	String cId = objReader.readLine().trim();
    	System.out.print("Professor Name : ");
    	String cPName = objReader.readLine().trim();
    	System.out.print("Course Name : ");
    	String cCname = objReader.readLine().trim();
    	System.out.print("Related Courses(STOP == x) : ");
    	ArrayList<String> cReList = new ArrayList<>();
    	String courseIdPattern = "\\d{5}";
    	while(true) {
    		String tempInput = objReader.readLine().trim();
    		if(tempInput.equalsIgnoreCase("x")) {
    			break;
    		}
    		
    		if(Pattern.matches(courseIdPattern, tempInput)) {
    			cReList.add(tempInput);
    	        System.out.println(" -> Course '" + tempInput + "' added. (Stop = x)");
    		} else {
    	        System.out.println("   [!] Invalid format. Please enter a 5-digit course ID. (Stop = x)");
    	    }
    	}
    	
		
		coursebuild(client, cId, cPName, cCname, cReList);
		
	}

	private static void coursebuild(Client client, String cId, String cPName, String cCname,
			ArrayList<String> cReList) {
		Course crInfo = Course.newBuilder()
	            .setCourseId(cId)
	            .setName(cPName)
	            .setCourseName(cCname)
	            .addAllRelatedCourses(cReList)
	            .build();
		
		client.callGrpc(client.blockingStub::setAllCourses, GetCourseRequest.newBuilder()
				.setCourse(crInfo)
				.build());
	}

	private static void addStudent(Client client, BufferedReader objReader) throws IOException {
    	System.out.print("Student ID : ");
    	String sId = objReader.readLine().trim();
    	System.out.print("First Name : ");
    	String sFname = objReader.readLine().trim();
    	System.out.print("Last Name : ");
    	String sLname = objReader.readLine().trim();
    	System.out.print("Department : ");
    	String sDepart = objReader.readLine().trim();
    	System.out.print("Complete Courses(STOP == x) : ");
    	ArrayList<String> sComList = new ArrayList<>();
    	String courseIdPattern = "\\d{5}";
    	while(true) {
    		String tempInput = objReader.readLine().trim();
    		if(tempInput.equalsIgnoreCase("x")) {
    			break;
    		}
    		
    		if(Pattern.matches(courseIdPattern, tempInput)) {
    			sComList.add(tempInput);
    	        System.out.println(" -> Course '" + tempInput + "' added. (Stop = x)");
    		} else {
    	        System.out.println("   [!] Invalid format. Please enter a 5-digit course ID. (Stop = x)");
    	    }
    	}
    	
		
		studentBuild(client, sId, sFname, sLname, sDepart, sComList);
	}

	private static void studentBuild(Client client, String sId, String sFname, String sLname,
			String sDepart, ArrayList<String> sComList) {
		
		Student stInfo = Student.newBuilder()
	            .setStudentId(sId)
	            .setFirstName(sFname)
	            .setLastName(sLname)
	            .setDepartment(sDepart)
	            .addAllCompletedCourses(sComList)
	            .build();
		
		client.callGrpc(client.blockingStub::setAllStudents, GetStudentRequest.newBuilder()
				.setStudent(stInfo)
				.build());
	}

	private static void getAllCourse(Client client) {
    	GetAllCourseRequest cRequest = GetAllCourseRequest.newBuilder().build();
    	CourseListResponse cResponse = client.callGrpc(client.blockingStub::getAllCourses, cRequest);

    	System.out.println("*** Server Courses List ***\n");
    		
    	cResponse.getCoursesList().forEach(course -> {
                System.out.print("Course code :: " + course.getCourseId() + " | Professor Name :: " + course.getName()
                 + " | CourseName : " + course.getCourseName() + "\n");
                
                if (!course.getRelatedCoursesList().isEmpty()) {
                    System.out.print("Completed Courses : " + course.getRelatedCoursesList() + "\n\n" );
                    } else {
                    System.out.println();
                }
            });
		
	}

	private static void getAllStudent(Client client) {
    	GetAllStudentRequest sRequest = GetAllStudentRequest.newBuilder().build();
    	StudentListResponse sResponse = client.callGrpc(client.blockingStub::getAllStudents, sRequest);

    	System.out.println("*** Server Students List ***\n");
    		
    	sResponse.getStudentsList().forEach(student -> {
                System.out.print("Student code :: " + student.getStudentId() + " | Student Name :: " + student.getFirstName()
                + " " + student.getLastName() + " | Department : " + student.getDepartment() + "\n");
                
                if (!student.getCompletedCoursesList().isEmpty()) {
                    System.out.print("Completed Courses : " + student.getCompletedCoursesList() + "\n\n" );
                    } else {
                    System.out.println();
                }
            });
	}
	
	//student service (student id check, course id check, complete course check)
	private static void studentService(Client client, BufferedReader objReader) throws IOException {
		System.out.println("<< Please select the course you want >> (Stop = x)");
		String courseIdPattern = "\\d{5}";
		ArrayList<String> cList = new ArrayList<>();
    	while(true) {
    		String tempInput = objReader.readLine().trim();
    		if(tempInput.equalsIgnoreCase("x")) {
    			break;
    		}
    		
    		if(Pattern.matches(courseIdPattern, tempInput)) {
    			cList.add(tempInput);
    	        System.out.println(" -> Course '" + tempInput + "' added. (Stop = x)");
    		} else {
    	        System.out.println("   [!] Invalid format. Please enter a 5-digit course ID. (Stop = x)");
    	    }
    	}
    	
    	ApplyCourseReqeust apRequest = ApplyCourseReqeust.newBuilder().setApplyAcId(client.acId)
    		.setApplyAcName(client.acName)
    		.addAllApplyCourses(cList)
    		.build();
    	
    	ApplyCourseResponse apResponse = client.callGrpc(client.blockingStub::applyCourse, apRequest);
		
			
	}

	private static String printMenu(BufferedReader objReader, Client client) throws IOException {
		if(client.acPermiss == 1) {
			System.out.println("******************** MENU **********************");
			System.out.println("1. List Students");
			System.out.println("2. List Courses");
			System.out.println("3. Add Student");
			System.out.println("4. Delete Student");
			System.out.println("5. Add Course");
			System.out.println("6. Delete Course");
			System.out.println("x. Exit");
			
			String sChoice = objReader.readLine().trim();
			
			switch(sChoice) {
			case "1" :
				getAllStudent(client);
				break;
			case "2" :
				getAllCourse(client);
				break;
			case "3" :
				addStudent(client, objReader);
				break;
			case "4" :
				deleteStudent(client, objReader);
				break;
			case "5" :
				addCourse(client, objReader);
				break;
			case "6" :
				deleteCourse(client, objReader);
				break;
			case "x" :
				serverClose(client);
				return "x";
			default :
				System.out.println("Invaild Choice.");
				}
			
		
		}
		
		else if(client.acPermiss == 2) {
			System.out.println("******************** MENU **********************");
			System.out.println("1. List Students");
			System.out.println("2. List Courses");
			System.out.println("x. Exit");
			
			String sChoice = objReader.readLine().trim();
			
			switch(sChoice) {
			case "1" :
				getAllStudent(client);
				break;
			case "2" :
				getAllCourse(client);
				break;
			case "3" :
				studentService(client, objReader);
				break;
			case "x" :
				serverClose(client);
				return "x";
			default :
				System.out.println("Invaild Choice.");
				}
		} else {
			System.out.print("wrong happening. please Quit this program and retry Login.");
		}
		return "";
		
	}

	private static void serverClose(Client client) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
		    System.out.println("Stop application, gRPC Channel is closed..");
		    try {
		        client.shutdown();
		    } catch (InterruptedException e) {
		        e.printStackTrace();
		    }
		}));
	}
}