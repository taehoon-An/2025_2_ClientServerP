package com.example.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client2 {
    private final ManagedChannel channel;
    private final NameServiceGrpc.NameServiceBlockingStub blockingStub;

    public Client2(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = NameServiceGrpc.newBlockingStub(channel); 
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void readName(String name) {
        System.out.println(name + " read to server..");
        NameRequest request = NameRequest.newBuilder().setName(name).build();
        NameResponse response = blockingStub.readName(request); 
        System.out.println("response to server " + response.getMessage());
    }

    public static void main(String[] args) throws InterruptedException {
        Client2 client = new Client2("localhost", 50051);
        try {
        	Scanner ip = new Scanner(System.in);
        	System.out.printf("Reading your name : ");
            client.readName(ip.next());
        } finally {
            client.shutdown();
        }
    }
}