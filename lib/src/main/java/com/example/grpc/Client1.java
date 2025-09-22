package com.example.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client1 {
    private final ManagedChannel channel;
    private final StudentServiceGrpc.StudentServiceBlockingStub blockingStub;

    public Client1(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = StudentServiceGrpc.newBlockingStub(channel); 
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        Client1 client = new Client1("localhost", 50051);
        try {
        	Scanner ip = new Scanner(System.in);
        	System.out.printf("adding your name : ");
        } finally {
            client.shutdown();
        }
    }
}