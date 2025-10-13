package com.example.grpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CourseModelList {
	protected ArrayList<Course> vCourse;
	
	private String crFileName;
	
	public CourseModelList(String fileName) throws FileNotFoundException, IOException  {
		BufferedReader objStudentFile = new BufferedReader(new FileReader(fileName));
		this.crFileName = fileName;
		this.vCourse = new ArrayList<Course>();
		while (objStudentFile.ready()) {
			String crInfo = objStudentFile.readLine();
			if (!crInfo.equals("")) {
				this.vCourse.add(addingCourses(crInfo));
			}
		}
		objStudentFile.close();
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
	
	public Course addingCourses(GetCourseRequest request) {
		String courseId = request.getCourse().getCourseId();
    	String name = request.getCourse().getName();
    	String courseName =  request.getCourse().getCourseName();
    	ArrayList<String> relatedCoursesList = new ArrayList<String>();
    	for(int i = 0; i < request.getCourse().getRelatedCoursesList().size(); i++) {
    		relatedCoursesList.add(request.getCourse().getRelatedCoursesList().get(i));
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
	
	public boolean addCourseRecords(GetCourseRequest request) {
		if(this.vCourse.add(addingCourses(request))) return writeToFile();
		else return false;
	}
	
	private boolean writeToFile() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.crFileName));
			for(int i = 0; i < vCourse.size(); i++) {
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
	
	public boolean deleteCourseRecords(GetSelCourseIdRequest request) {
		for(int i = 0; i < this.vCourse.size(); i++) {
			if(vCourse.get(i).getCourseId().equals(request.getCourseId())) {
				this.vCourse.remove(i);
				return writeToFile();
			}
		}
		return false;
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
