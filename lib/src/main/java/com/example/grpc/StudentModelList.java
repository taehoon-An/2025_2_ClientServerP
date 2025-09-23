package com.example.grpc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class StudentModelList {
	protected ArrayList<Student> vStudent;
	
	public StudentModelList(InputStream inputStream) throws FileNotFoundException, IOException {
		BufferedReader objStudentFile = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		this.vStudent = new ArrayList<Student>();
		while (objStudentFile.ready()) {
			String stuInfo = objStudentFile.readLine();
			if (!stuInfo.equals("")) {
				this.vStudent.add(addingStudents(stuInfo));
			}
		}
		objStudentFile.close();
	}
	
	 public static StudentModelList fromFile(String fileName) {
	        try {
	            InputStream inputStream = StudentModelList.class.getClassLoader().getResourceAsStream(fileName);
	            if (inputStream == null) {
	                throw new FileNotFoundException("cant read file to resource folder: " + fileName);
	            }
	            return new StudentModelList(inputStream);
	        } catch (IOException e) {
	            System.err.println(fileName + " create fail: " + e.getMessage());
	           
	        }
	        return null;
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

	public ArrayList<Student> getAllStudentRecords() {
		return this.vStudent;
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
