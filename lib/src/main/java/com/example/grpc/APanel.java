package com.example.grpc;

import javax.swing.JPanel;

import com.example.grpc.StudentServiceGrpc.StudentServiceBlockingStub;

public class APanel extends JPanel {
	
	private StudentServiceBlockingStub blockingStub;

	public void init(StudentServiceBlockingStub blockingStub) {
		this.blockingStub = blockingStub;
	}
}
