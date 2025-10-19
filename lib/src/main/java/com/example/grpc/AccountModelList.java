package com.example.grpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class AccountModelList {
	protected ArrayList<Account> vAccount;
	private String acFileName;

	public AccountModelList(String fileName) throws IOException {
		BufferedReader objAccountFile = new BufferedReader(new FileReader(fileName));
		this.acFileName = fileName;
		this.vAccount = new ArrayList<>();
		
		while(objAccountFile.ready()) {
			String acInfo = objAccountFile.readLine();
			if (!acInfo.equals("")) {
				this.vAccount.add(addingAccounts(acInfo));
			}
		}
		objAccountFile.close();
		
		
	}
	
	private Account addingAccounts(String acInfo) {
		StringTokenizer stringTokenizer = new StringTokenizer(acInfo);
    	String acId = stringTokenizer.nextToken();
    	String acPw = stringTokenizer.nextToken();
    	String acFname = stringTokenizer.nextToken();
    	String acLname = stringTokenizer.nextToken();
    	String acDepartment = stringTokenizer.nextToken();
    	int acPermiss = Integer.parseInt(stringTokenizer.nextToken());
    	
    	return Account.newBuilder()
        .setAcId(acId)
        .setAcPw(acPw)
        .setFirstName(acFname)
        .setLastName(acLname)
        .setDepartment(acDepartment)
        .setAcPermiss(acPermiss)
        .build();
	}
	
	public Account addingAccounts(AddAccountRequest request) {
		String acId = request.getAccount().getAcId();
    	String acPw = request.getAccount().getAcPw();
    	String acFname =  request.getAccount().getFirstName();
    	String acLname =  request.getAccount().getLastName();
    	String acDepartment = request.getAccount().getDepartment();
    	int acPermiss = request.getAccount().getAcPermiss();


		return Account.newBuilder()
		        .setAcId(acId)
		        .setAcPw(acPw)
		        .setFirstName(acFname)
		        .setLastName(acLname)
		        .setDepartment(acDepartment)
		        .setAcPermiss(acPermiss)
		        .build();
		
	}
	
	public ArrayList<Account> getAllAccountsRecords() {
		return this.vAccount;
	}
	
	public boolean addAccountRecords(AddAccountRequest request) {
		if(this.vAccount.add(addingAccounts(request))) return writeToFile();
		else return false;
	}
	
	private boolean writeToFile() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.acFileName));
			for(int i = 0; i < vAccount.size(); i++) {
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
	
	public String getString(int number) {
        String stringReturn = vAccount.get(number).getAcId() + " " + 
        		vAccount.get(number).getAcPw() + " " + vAccount.get(number).getFirstName() +
        		" " + vAccount.get(number).getLastName() + " " + vAccount.get(number).getDepartment() + " "
        		 + vAccount.get(number).getAcPermiss();
        return stringReturn;
    }
}
