package com.example.grpc;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.LayoutManager;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.example.grpc.StudentServiceGrpc.StudentServiceBlockingStub;

public class CRFrame extends JFrame {
	private CardLayout cdLayout;
	private JPanel cardPanel;
	
	ListPanel listPanel;
	APanel addPanel;
	SUPanel loginPanel;
	
	
	
	public CRFrame(String title) {
		super(title);
		
		this.setSize(1280,720);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		cdLayout = new CardLayout();
		this.cardPanel = new JPanel(cdLayout);
		
		this.listPanel = new ListPanel();
		this.addPanel = new APanel();
		this.loginPanel = new SUPanel();
		
		cardPanel.add(listPanel, "Show List");
//		cardPanel.add(addPanel, "Add Data");
//		cardPanel.add(loginPanel, "login");
		
		
		this.add(cardPanel);
		cdLayout.show(cardPanel, "Show List");
		
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public void init(StudentServiceBlockingStub blockingStub) {
		this.listPanel.init(blockingStub, this);
		this.addPanel.init(blockingStub);
		this.loginPanel.init(blockingStub);
		
		
	}

}
