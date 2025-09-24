package com.example.grpc;

import java.awt.BorderLayout;
import java.awt.LayoutManager;

import javax.swing.JFrame;

import com.example.grpc.StudentServiceGrpc.StudentServiceBlockingStub;

public class CRFrame extends JFrame {
	CRPanel mainPanel;
	
	public CRFrame(String title) {
		super(title);
		
		this.setSize(1280,720);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.mainPanel = new CRPanel();
		
		this.add(mainPanel);
		
		this.setVisible(true);
	}

	public void init(StudentServiceBlockingStub blockingStub) {
		this.mainPanel.init(blockingStub);
		
	}

}
