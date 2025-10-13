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

public class PlusCrDialog extends JDialog {
	private JTextField courseIdField;
	private JTextField professorNameField;
	private JTextField courseNameField;
	private JTextField relatedCoursesField;
	    
	private boolean confirmed = false;
	
	public PlusCrDialog(CRFrame parentFrame) {
		super(parentFrame, "add Course Data", true);
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
		this.courseIdField = new JTextField(15);
		this.professorNameField = new JTextField(15);
		this.courseNameField = new JTextField(15);
		this.relatedCoursesField = new JTextField(15);
        
        panel.add(new JLabel("Course ID:"));
        panel.add(courseIdField);

        panel.add(new JLabel("Professor Name:"));
        panel.add(professorNameField);
        
        panel.add(new JLabel("Course Name:"));
        panel.add(courseNameField);

        panel.add(new JLabel("Related Courses (쉼표로 구분):"));
        panel.add(relatedCoursesField);
	}
	
	public GetCourseRequest getCourse() {
		List<String> relatedCoursesList = Arrays.asList(relatedCoursesField.getText().split("\\s*,\\s*"));
		
		Course crInfo = Course.newBuilder()
	            .setCourseId(courseIdField.getText())
	            .setName(professorNameField.getText())
	            .setCourseName(courseNameField.getText())
	            .addAllRelatedCourses(relatedCoursesList)
	            .build();
		
		return GetCourseRequest.newBuilder()
				.setCourse(crInfo)
				.build();
		
	}
	
	public boolean showDialog() {
        setVisible(true); 
        return confirmed; 
	}
}
