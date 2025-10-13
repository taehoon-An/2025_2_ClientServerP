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

public class ListPanel extends JPanel {
	JButton sListBt;
	JButton cListBt;
	JButton plusStBt;
	JButton plusCrBt;
	JButton deleteStBt;
	JButton deleteCrBt;
	
	CRFrame parentFrame;
	
	CRTextArea textArea;
	
	StudentServiceBlockingStub blockingStub;

	public ListPanel() {
		LayoutManager bdLayout = new BorderLayout();
		this.setLayout(bdLayout);
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		JPanel PdBtPanel = new JPanel(new FlowLayout());
		defualtSetButtonPanel(buttonPanel, PdBtPanel);
		
		this.textArea = new CRTextArea();

		JScrollPane scPane = new JScrollPane(textArea);
		
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.add(scPane, BorderLayout.CENTER);
		this.add(PdBtPanel, BorderLayout.NORTH);
		
		this.actionListener();
		
	}

	private void defualtSetButtonPanel (JPanel btPanel, JPanel pBtPanel) {
		this.sListBt = new JButton("Print Students List");
		this.cListBt = new JButton("Print Courses List");
		
		this.plusStBt = new JButton("+ Add Student");
		this.plusCrBt = new JButton("+ Add Course");
		this.deleteStBt = new JButton("- Delete Student");
		this.deleteCrBt = new JButton("- Delete Course");
		
		
		btPanel.add(sListBt);
		btPanel.add(cListBt);
		
		pBtPanel.add(plusStBt);
		pBtPanel.add(plusCrBt);
		pBtPanel.add(deleteStBt);
		pBtPanel.add(deleteCrBt);
		
	}
	
	public void init(StudentServiceBlockingStub blockingStub, CRFrame crFrame) {
		this.blockingStub = blockingStub;
		this.parentFrame = crFrame;
	}
	
	public void actionListener() {
		this.sListBt.addActionListener(e -> {
			this.textArea.setText(getStudentsAsString());
		});
		
		this.cListBt.addActionListener(e -> {
			this.textArea.setText(getCoursesAsString());
		});
		
		this.plusStBt.addActionListener(e -> {
			PlusStDialog stDialog = new PlusStDialog(this.parentFrame);
			
			boolean tempOkCheck = stDialog.showDialog();
			
			if(tempOkCheck) {
				this.blockingStub.setAllStudents(stDialog.getStudent());
			}
			this.textArea.setText(getStudentsAsString());
		});
		
		this.plusCrBt.addActionListener(e -> {
			PlusCrDialog crDialog = new PlusCrDialog(this.parentFrame);
			
			boolean tempOkCheck = crDialog.showDialog();
			
			if(tempOkCheck) {
				this.blockingStub.setAllCourses(crDialog.getCourse());
			}
			this.textArea.setText(getCoursesAsString()); 
		});
		
		this.deleteStBt.addActionListener(e -> {
			DeleteStDialog dStDialog = new DeleteStDialog(this.parentFrame);
			
			boolean tempOkCheck = dStDialog.showDialog();
			
			if(tempOkCheck) {
				this.blockingStub.deleteStudent(dStDialog.getStudentId());
			}
			
			this.textArea.setText(getStudentsAsString());
		});
		
		this.deleteCrBt.addActionListener(e -> {
			DeleteCrDialog dCrDialog = new DeleteCrDialog(this.parentFrame);
			
			boolean tempOkCheck = dCrDialog.showDialog();
			
			if(tempOkCheck) {
				this.blockingStub.deleteCourse(dCrDialog.getCourseId());
			}
			
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
