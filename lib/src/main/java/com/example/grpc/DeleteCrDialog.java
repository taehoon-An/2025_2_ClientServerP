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

public class DeleteCrDialog extends JDialog {
	private JTextField deleteCourseIdField;
	    
	private boolean confirmed = false;
	
	public DeleteCrDialog(CRFrame parentFrame) {
		super(parentFrame, "delete Course Data", true);
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
        
        this.setSize(400,150);
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
		this.deleteCourseIdField = new JTextField(15);
        
        panel.add(new JLabel("Delete Course ID:"));
        panel.add(deleteCourseIdField);
	}
	
	public GetSelCourseIdRequest getCourseId() {
		return GetSelCourseIdRequest.newBuilder()
				.setCourseId(this.deleteCourseIdField.getText())
				.build();	
	}
	
	public boolean showDialog() {
        setVisible(true); 
        return confirmed; 
	}
}
