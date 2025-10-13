package com.example.grpc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PlusStDialog extends JDialog {
	private JTextField studentIdField;
	private JTextField firstNameField;
	private JTextField lastNameField;
	private JTextField departmentField;
	private JTextField coursesField;
	    
	private boolean confirmed = false;
	
	public PlusStDialog(CRFrame parentFrame) {
		super(parentFrame, "add Student Data", true);
		JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initInputPanel(inputPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("확인");
        JButton cancelButton = new JButton("취소");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        actionListener(okButton, cancelButton);
        
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        this.setSize(500,400);
        setLocationRelativeTo(parentFrame);
	}

	private void actionListener(JButton okButton, JButton cancelButton) {
		okButton.addActionListener(e -> {
            confirmed = true; 
            dispose();        
        });

        cancelButton.addActionListener(e -> {
            confirmed = false; 
            dispose();       
        });
	}

	private void initInputPanel(JPanel panel) {
		this.studentIdField = new JTextField(15);
		this.firstNameField = new JTextField(15);
		this.lastNameField = new JTextField(15);
		this.departmentField = new JTextField(15);
		this.coursesField = new JTextField(); 
        
        panel.add(new JLabel("Student ID:"));
        panel.add(studentIdField);

        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);

        panel.add(new JLabel("Department:"));
        panel.add(departmentField);

        panel.add(new JLabel("Complete Courses (쉼표로 구분):"));
        panel.add(coursesField);
	}
	
	public GetStudentRequest getStudent() {
		List<String> completedCoursesList = Arrays.asList(coursesField.getText().split("\\s*,\\s*"));
		
		Student stInfo = Student.newBuilder()
	            .setStudentId(studentIdField.getText())
	            .setFirstName(firstNameField.getText())
	            .setLastName(lastNameField.getText())
	            .setDepartment(departmentField.getText())
	            .addAllCompletedCourses(completedCoursesList)
	            .build();
		
		return GetStudentRequest.newBuilder()
				.setStudent(stInfo)
				.build();
		
	}
	
	public boolean showDialog() {
        setVisible(true); 
        return confirmed; 
	}
}
