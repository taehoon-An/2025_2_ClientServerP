package com.example.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

public class Client {
    private final ManagedChannel channel;
    private final StudentServiceGrpc.StudentServiceBlockingStub blockingStub;
    
    private CRFrame mainFrame;

    public Client(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = StudentServiceGrpc.newBlockingStub(channel); 
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Client client = new Client("localhost", 50051);
        client.mainFrame = new CRFrame("Course Registration");
        client.mainFrame.init(client.blockingStub);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("애플리케이션 종료... gRPC 채널을 닫습니다.");
            try {
                // 프로그램이 끝날 때만 shutdown()이 호출됩니다.
                client.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }
}