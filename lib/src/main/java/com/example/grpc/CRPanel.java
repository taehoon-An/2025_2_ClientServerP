package com.example.grpc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.LayoutManager;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.example.grpc.StudentServiceGrpc.StudentServiceBlockingStub;

public class CRPanel extends JPanel {
	JButton sListBt;
	JButton cListBt;
	CRTextArea textArea;
	
	StudentServiceBlockingStub blockingStub;

	public CRPanel() {
		LayoutManager bdLayout = new BorderLayout();
		this.setLayout(bdLayout);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		defualtSetButtonPanel(buttonPanel);
		
		this.textArea = new CRTextArea();

		JScrollPane scPane = new JScrollPane(textArea);
		
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.add(scPane, BorderLayout.CENTER);
		
		this.actionListener();
		
	}

	private void defualtSetButtonPanel (JPanel btPanel) {
		this.sListBt = new JButton("Print Students List");
		this.cListBt = new JButton("Print Courses List");
		
		
		btPanel.add(sListBt);
		btPanel.add(cListBt);
	}
	
	public void init(StudentServiceBlockingStub blockingStub) {
		this.blockingStub = blockingStub;
	}
	
	public void actionListener() {
		this.sListBt.addActionListener(e -> {
			this.textArea.setText(getStudentsAsString());
		});
		
		this.cListBt.addActionListener(e -> {
			this.textArea.setText(getCoursesAsString());
		});
	}

	private String getStudentsAsString() {
		GetAllStudentRequest sRequest = GetAllStudentRequest.newBuilder().build();
		StudentListResponse sResponse = blockingStub.getAllStudents(sRequest);

		StringBuilder sb = new StringBuilder();
		sb.append("*** Server Students List ***\n");
		
		sResponse.getStudentsList().forEach(student -> {
            sb.append("Student code :: ").append(student.getStudentId())
              .append(" | Student Name :: ").append(student.getFirstName()).append(" ").append(student.getLastName())
              .append(" | Department : ").append(student.getDepartment()).append("\n");
            
            if (!student.getCompletedCoursesList().isEmpty()) {
                sb.append("Completed Courses : ").append(student.getCompletedCoursesList()).append("\n\n");
            } else {
                sb.append("\n");
            }
        });
		System.out.print(sb.toString());
		return sb.toString();
	}
	
	 public String getCoursesAsString() {
        GetAllCourseRequest cRequest = GetAllCourseRequest.newBuilder().build();
        CourseListResponse cResponse = blockingStub.getAllCourses(cRequest);
        
        StringBuilder sb = new StringBuilder();
        sb.append("*** Server Courses List ***\n");
        
        cResponse.getCoursesList().forEach(course -> {
            sb.append("Course code: ").append(course.getCourseId())
              .append(" | Professor : ").append(course.getName())
              .append(" | Course Name : ").append(course.getCourseName()).append("\n");
            
            if (!course.getRelatedCoursesList().isEmpty()) {
                sb.append("Related Courses : ").append(course.getRelatedCoursesList()).append("\n\n");
            } else {
                sb.append("\n");
            }
        });
        
        System.out.print(sb.toString());
        return sb.toString();
    }

	
}
