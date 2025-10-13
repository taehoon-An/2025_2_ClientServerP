package com.example.grpc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.example.grpc.StudentServiceGrpc.StudentServiceBlockingStub;

public class SUPanel extends JPanel {
	
	private JButton signUpBt;
	private JButton signInBt;
	
	private JTextField idTextField;
	private JPasswordField passwordField;
	
	private StudentServiceBlockingStub blockingStub;
	
	public SUPanel() {
		GridLayout GridLayout = new GridLayout(3, 2, 5, 5);

		this.setLayout(GridLayout);
		this.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));
		
		JLabel idLabel = new JLabel("ID :");
        idLabel.setHorizontalAlignment(SwingConstants.CENTER); // 라벨 텍스트 오른쪽 정렬
        this.idTextField = new JTextField();

        JLabel passwordLabel = new JLabel("PASSWORD :");
        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.passwordField = new JPasswordField();
        
        this.signUpBt = new JButton("Sign Up");
        this.signInBt = new JButton("Login");
		
        this.add(idLabel);
        this.add(idTextField);
        this.add(passwordLabel);
        this.add(passwordField);
        this.add(signUpBt);
        this.add(signInBt);
        
        this.signUpBt.addActionListener(e -> {
			
		});
	}

	public void init(StudentServiceBlockingStub blockingStub) {
		this.blockingStub = blockingStub;
	}
}
