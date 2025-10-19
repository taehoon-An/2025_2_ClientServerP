package com.example.grpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class StudentModelList {
	protected ArrayList<Student> vStudent;
	
	private String stFileName;
	
	public StudentModelList(String fileName) throws FileNotFoundException, IOException {
		BufferedReader objStudentFile = new BufferedReader(new FileReader(fileName));
		this.stFileName = fileName;
		this.vStudent = new ArrayList<Student>();
		while (objStudentFile.ready()) {
			String stuInfo = objStudentFile.readLine();
			if (!stuInfo.equals("")) {
				this.vStudent.add(addingStudents(stuInfo));
			}
		}
		objStudentFile.close();
	}

	 
	public Student addingStudents(String stuInfo) {
		 	StringTokenizer stringTokenizer = new StringTokenizer(stuInfo);
	    	String studentId = stringTokenizer.nextToken();
	    	String firstName = stringTokenizer.nextToken();
	    	String lastName = stringTokenizer.nextToken();
	    	String department = stringTokenizer.nextToken();
	    	ArrayList<String> completedCoursesList = new ArrayList<String>();
	    	while (stringTokenizer.hasMoreTokens()) {
	    		completedCoursesList.add(stringTokenizer.nextToken());
	    	}
	    	
	    	return Student.newBuilder()
            .setStudentId(studentId)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setDepartment(department)
            .addAllCompletedCourses(completedCoursesList)
            .build();
	}
	
	public Student addingStudents(GetStudentRequest request) {
		String studentId = request.getStudent().getStudentId();
    	String firstName = request.getStudent().getFirstName();
    	String lastName =  request.getStudent().getLastName();
    	String department = request.getStudent().getDepartment();
    	ArrayList<String> completedCoursesList = new ArrayList<String>();
    	for(int i = 0; i < request.getStudent().getCompletedCoursesList().size(); i++) {
    		completedCoursesList.add(request.getStudent().getCompletedCoursesList().get(i));
    	}
		
		return Student.newBuilder()
            .setStudentId(studentId)
            .setFirstName(firstName)
            .setLastName(lastName)
            .setDepartment(department)
            .addAllCompletedCourses(completedCoursesList)
            .build();
		
	}

	public ArrayList<Student> getAllStudentRecords() {
		return this.vStudent;
	}
	
	public boolean addStudentRecords(GetStudentRequest request)  {
		if(this.vStudent.add(addingStudents(request))) return writeToFile();
		else return false;
	}
	
	public boolean writeToFile() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.stFileName));
			for(int i = 0; i < vStudent.size(); i++) {
				writer.write(this.getString(i));
				writer.newLine();
				writer.newLine();
			}
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteStudentRecords(GetSelStudentIdRequest request) {
		for(int i = 0; i < this.vStudent.size(); i++) {
			if(vStudent.get(i).getStudentId().equals(request.getStudentId())) {
				this.vStudent.remove(i);
				return writeToFile();
			}
		}
		return false;
	}

	public boolean isRegisteredStudent(String sSID) {
		for (int i = 0; i < this.vStudent.size(); i++) {
			Student objStudent = (Student) this.vStudent.get(i);
			if (objStudent.getStudentId().equals(sSID)) {
				return true;
			}
		}
		return false;
	}
	
	public String getString(int number) {
        String stringReturn = vStudent.get(number).getStudentId() + " " + 
        		vStudent.get(number).getFirstName() + " " + vStudent.get(number).getLastName() 
        		+ " " + vStudent.get(number).getDepartment();
        for (int i = 0; i < vStudent.get(number).getCompletedCoursesList().size(); i++) {
            stringReturn = stringReturn + " " + vStudent.get(number).getCompletedCoursesList().get(i).toString();
        }
        return stringReturn;
    }
}
