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

public class CourseModelList {
	protected ArrayList<Course> vCourse;
	
	public CourseModelList(InputStream inputStream) throws FileNotFoundException, IOException {
		BufferedReader objStudentFile = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		this.vCourse = new ArrayList<Course>();
		while (objStudentFile.ready()) {
			String crInfo = objStudentFile.readLine();
			if (!crInfo.equals("")) {
				this.vCourse.add(addingCourses(crInfo));
			}
		}
		objStudentFile.close();
	}
	
	 public static CourseModelList fromFile(String fileName) {
	        try {
	            InputStream inputStream = CourseModelList.class.getClassLoader().getResourceAsStream(fileName);
	            if (inputStream == null) {
	                throw new FileNotFoundException("cant read file to resource folder: " + fileName);
	            }
	            return new CourseModelList(inputStream);
	        } catch (IOException e) {
	            System.err.println(fileName + " create fail: " + e.getMessage());
	           
	        }
	        return null;
	    }
	 
	public Course addingCourses(String crInfo) {
		 	StringTokenizer stringTokenizer = new StringTokenizer(crInfo);
	    	String courseId = stringTokenizer.nextToken();
	    	String name = stringTokenizer.nextToken();
	    	String courseName = stringTokenizer.nextToken();
	    	ArrayList<String> relatedCoursesList = new ArrayList<String>();
	    	while (stringTokenizer.hasMoreTokens()) {
	    		relatedCoursesList.add(stringTokenizer.nextToken());
	    	}
	    	
	    	return Course.newBuilder()
            .setCourseId(courseId)
            .setName(name)
            .setCourseName(courseName)
            .addAllRelatedCourses(relatedCoursesList)
            .build();
	}

	public ArrayList<Course> getAllCourseRecords() {
		return this.vCourse;
	}

	public boolean isRegisteredStudent(String sSID) {
		for (int i = 0; i < this.vCourse.size(); i++) {
			Course objCourse = (Course) this.vCourse.get(i);
			if (objCourse.getCourseId().equals(sSID)) {
				return true;
			}
		}
		return false;
	}
	
	public String getString(int number) {
        String stringReturn = vCourse.get(number).getCourseId() + " " + 
        		vCourse.get(number).getName() + " " + vCourse.get(number).getCourseName();
        for (int i = 0; i < vCourse.get(number).getRelatedCoursesList().size(); i++) {
            stringReturn = stringReturn + " " + vCourse.get(number).getRelatedCoursesList().get(i).toString();
        }
        return stringReturn;
    }
}
