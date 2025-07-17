package com.example.login_second;

import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server waiting for client on port 12345...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress().getHostAddress());

                // Handle client in a separate thread
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (
                ObjectInputStream inputFromClient = new ObjectInputStream(socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream())
        ) {
            // Read login details from client
            LoginDetails loginDetails = (LoginDetails) inputFromClient.readObject();

            // Validate login details (you should replace this with your database validation logic)
            boolean isValid = validateLoginDetails(loginDetails);

            // Send validation result to the client
            outputToClient.writeBoolean(isValid);
            outputToClient.flush();
            socket.close();
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean validateLoginDetails(LoginDetails loginDetails) throws SQLException {
        // Implement your database validation logic here
        String url = "jdbc:mysql://localhost:3306/login";
        String user = "root";
        String password = "aryashetty";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT * FROM user WHERE username=? AND password=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, loginDetails.getUsername());
                preparedStatement.setString(2, loginDetails.getPassword());

                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next(); // If there is a match, the details are valid
            }
        }
    }
}
