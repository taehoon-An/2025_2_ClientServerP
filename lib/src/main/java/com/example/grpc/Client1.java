package com.example.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client1 {
    private final ManagedChannel channel;
    private final NameServiceGrpc.NameServiceBlockingStub blockingStub;

    public Client1(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = NameServiceGrpc.newBlockingStub(channel); 
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void addName(String name) {
        System.out.println(name + " add to server..");
        NameRequest request = NameRequest.newBuilder().setName(name).build();
        NameResponse response = blockingStub.addName(request); 
        System.out.println("response to server \n" + response.getMessage());
    }

    public static void main(String[] args) throws InterruptedException {
        Client1 client = new Client1("localhost", 50051);
        try {
        	Scanner ip = new Scanner(System.in);
        	System.out.printf("adding your name : ");
            client.addName(ip.next());
        } finally {
            client.shutdown();
        }
    }
}