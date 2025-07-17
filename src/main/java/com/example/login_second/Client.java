package com.example.login_second;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Client extends Application {

    private String username;
    private Stage primaryStage;
    private LoginDetails loginDetails;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createLoginPageScene();
    }

    private void createLoginPageScene() {
        primaryStage.setTitle("LuggStorage");

        VBox vbox = new VBox(18);
        vbox.setPadding(new Insets(30, 50, 50, 50));
        vbox.setAlignment(Pos.CENTER);

        Label headingLabel = new Label("LuggStorage");
        headingLabel.getStyleClass().add("label-heading");

        Label subheadingLabel = new Label("Log-In");
        subheadingLabel.getStyleClass().add("label-subheading");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("password-field");

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button-main");
        loginButton.setOnMousePressed(e -> animateButton(loginButton));
        loginButton.setOnMouseReleased(e -> resetButtonAnimation(loginButton));

        HBox signUpBox = createSignUpButton();

        vbox.getChildren().addAll(
                headingLabel,
                subheadingLabel,
                usernameField,
                passwordField,
                loginButton,
                signUpBox
        );

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            loginDetails = new LoginDetails(username, password);

            boolean isValid = sendLoginDetails(loginDetails);

            if (isValid) {
                primaryStage.setScene(createHomePageScene());
                this.username = username;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Provided credentials are invalid");
                alert.show();
            }
        });

        Scene scene = new Scene(vbox, 380, 400);
        scene.getStylesheets().add(getClass().getResource("/com/example/login_second/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createSignUpButton() {
        HBox signUpBox = new HBox(8);
        signUpBox.getStyleClass().add("hbox-signup");

        Label notAUserLabel = new Label("Not a user?");
        notAUserLabel.getStyleClass().add("label-subheading");

        Button signupButton = new Button("Signup");
        signupButton.getStyleClass().add("button-secondary");
        signupButton.setOnMousePressed(e -> animateButton(signupButton));
        signupButton.setOnMouseReleased(e -> resetButtonAnimation(signupButton));

        signupButton.setOnAction(event -> primaryStage.setScene(createSignUpScene()));

        signUpBox.getChildren().addAll(notAUserLabel, signupButton);
        return signUpBox;
    }

    private Scene createSignUpScene() {
        VBox vbox = new VBox(14);
        vbox.setPadding(new Insets(30, 50, 50, 50));
        vbox.setAlignment(Pos.CENTER);

        Label headingLabel = new Label("Register New User");
        headingLabel.getStyleClass().add("label-heading");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("password-field");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.getStyleClass().add("password-field");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        firstNameField.getStyleClass().add("text-field");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        lastNameField.getStyleClass().add("text-field");

        TextField mobileNumberField = new TextField();
        mobileNumberField.setPromptText("Mobile Number");
        mobileNumberField.getStyleClass().add("text-field");

        TextField aadharNumberField = new TextField();
        aadharNumberField.setPromptText("Aadhar Number");
        aadharNumberField.getStyleClass().add("text-field");

        Button signUpButton = new Button("Sign Up");
        signUpButton.getStyleClass().add("button-main");
        signUpButton.setOnMousePressed(e -> animateButton(signUpButton));
        signUpButton.setOnMouseReleased(e -> resetButtonAnimation(signUpButton));

        Button backToLoginButton = new Button("Back to Login");
        backToLoginButton.getStyleClass().add("button-secondary");
        backToLoginButton.setOnMousePressed(e -> animateButton(backToLoginButton));
        backToLoginButton.setOnMouseReleased(e -> resetButtonAnimation(backToLoginButton));

        vbox.getChildren().addAll(
                headingLabel,
                usernameField,
                passwordField,
                confirmPasswordField,
                firstNameField,
                lastNameField,
                mobileNumberField,
                aadharNumberField,
                signUpButton, backToLoginButton
        );

        signUpButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String mobileNumber = mobileNumberField.getText();
            String aadharNumber = aadharNumberField.getText();

            if (!isValidPhoneNumber(mobileNumber)) {
                showAlert("Invalid Phone Number", "Phone number should have 10 digits.");
                return;
            }

            if (!isValidAadharNumber(aadharNumber)) {
                showAlert("Invalid Aadhar Number", "Aadhar number should have 12 digits.");
                return;
            }

            if (isAadharNumberExists(aadharNumber)) {
                showAlert("Aadhar Exists", "Aadhar number already exists. Please enter a different Aadhar number.");
                return;
            }
            if (isAadharNumberNotExistsInAadharTable(aadharNumber)) {
                showAlert("Aadhar Not Found", "Aadhar number not found in master Aadhar table.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showAlert("Password Error", "Passwords do not match");
                return;
            }

            if (usernameExists(username)) {
                showAlert("Username Exists", "Username already exists. Please choose a different username.");
                return;
            }

            storeUserDetails(username, password, firstName, lastName, mobileNumber, aadharNumber);

            createLoginPageScene();
            this.username = username;
        });

        backToLoginButton.setOnAction(event -> createLoginPageScene());

        Scene scene = new Scene(vbox, 420, 600);
        scene.getStylesheets().add(getClass().getResource("/com/example/login_second/style.css").toExternalForm());
        return scene;
    }

    // --- Animation helpers for dynamic button effects ---
    private void animateButton(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        st.setToX(0.95);
        st.setToY(0.95);
        st.play();
    }
    private void resetButtonAnimation(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
        st.setToX(1);
        st.setToY(1);
        st.play();
    }

    private boolean isAadharNumberExists(String aadharNumber) {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login", "root", "aryashetty");
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM user WHERE aadhar = ?")
        ) {
            statement.setString(1, aadharNumber);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isAadharNumberNotExistsInAadharTable(String aadharNumber) {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login", "root", "aryashetty");
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM aadhar WHERE aadharNumber = ?")
        ) {
            statement.setString(1, aadharNumber);
            ResultSet resultSet = statement.executeQuery();
            return !resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void storeUserDetails(String username, String password, String firstName, String lastName, String phone, String aadhar) {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login", "root", "aryashetty");
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO user (username, password, firstName, lastName, phone, aadhar) VALUES (?, ?, ?, ?, ?, ?)"
                )
        ) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, firstName);
            statement.setString(4, lastName);
            statement.setString(5, phone);
            statement.setString(6, aadhar);
            statement.executeUpdate();
            System.out.println("User details stored in the database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{10}");
    }

    private boolean isValidAadharNumber(String aadharNumber) {
        return aadharNumber.matches("\\d{12}");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }

    private boolean usernameExists(String username) {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login", "root", "aryashetty");
                PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM user WHERE username = ?")
        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Scene createHomePageScene() {
        VBox vbox = new VBox(18);
        vbox.setAlignment(Pos.CENTER);

        Label headingLabel = new Label("LuggStorage");
        headingLabel.getStyleClass().add("label-heading");

        Label welcomeLabel = new Label("Welcome to LuggStorage\nA place where you can store all your luggage to roam around\nand enjoy your visit more freely and luxuriously");
        welcomeLabel.setAlignment(Pos.CENTER);
        welcomeLabel.getStyleClass().add("label-subheading");

        Button goToLoginPageButton = new Button("Log Out");
        goToLoginPageButton.getStyleClass().add("button-secondary");
        goToLoginPageButton.setOnMousePressed(e -> animateButton(goToLoginPageButton));
        goToLoginPageButton.setOnMouseReleased(e -> resetButtonAnimation(goToLoginPageButton));

        Button bookingDetailsButton = new Button("New Booking");
        bookingDetailsButton.getStyleClass().add("button-main");
        bookingDetailsButton.setOnMousePressed(e -> animateButton(bookingDetailsButton));
        bookingDetailsButton.setOnMouseReleased(e -> resetButtonAnimation(bookingDetailsButton));
        bookingDetailsButton.setOnAction(event -> navigateToBookingDetailsScene());

        Button viewPreviousBookingsButton = new Button("View Bookings");
        viewPreviousBookingsButton.getStyleClass().add("button-main");
        viewPreviousBookingsButton.setOnMousePressed(e -> animateButton(viewPreviousBookingsButton));
        viewPreviousBookingsButton.setOnMouseReleased(e -> resetButtonAnimation(viewPreviousBookingsButton));
        viewPreviousBookingsButton.setOnAction(event -> primaryStage.setScene(navigateToPreviousBookingsScene()));

        goToLoginPageButton.setOnAction(event -> createLoginPageScene());

        vbox.getChildren().addAll(headingLabel, welcomeLabel, bookingDetailsButton, viewPreviousBookingsButton, goToLoginPageButton);

        Scene scene = new Scene(vbox, 400, 320);
        scene.getStylesheets().add(getClass().getResource("/com/example/login_second/style.css").toExternalForm());
        return scene;
    }

    private Scene navigateToPreviousBookingsScene() {
        VBox vbox = new VBox(10);

        List<BookingDetails> bookings = getPreviousBookings(username);

        TableView<BookingDetails> tableView = createBookingTableView(bookings);
        tableView.getStyleClass().add("table-view");

        Label headingLabel = new Label("Your Bookings");
        headingLabel.getStyleClass().add("label-heading");

        Button backToHomePageButton = new Button("Back to Home");
        backToHomePageButton.getStyleClass().add("button-secondary");
        backToHomePageButton.setOnMousePressed(e -> animateButton(backToHomePageButton));
        backToHomePageButton.setOnMouseReleased(e -> resetButtonAnimation(backToHomePageButton));
        backToHomePageButton.setOnAction(event -> primaryStage.setScene(createHomePageScene()));

        vbox.setPadding(new Insets(20, 50, 50, 50));

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.getChildren().addAll(headingLabel);

        VBox bottomBox = new VBox(10);
        bottomBox.setAlignment(Pos.BOTTOM_CENTER);
        bottomBox.getChildren().addAll(backToHomePageButton);

        vbox.getChildren().addAll(headerBox, tableView, bottomBox);

        Scene scene = new Scene(vbox, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/com/example/login_second/style.css").toExternalForm());
        return scene;
    }

    private TableView<BookingDetails> createBookingTableView(List<BookingDetails> bookings) {
        TableView<BookingDetails> tableView = new TableView<>();

        TableColumn<BookingDetails, Integer> bookingIdCol = new TableColumn<>("Booking ID");
        bookingIdCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));

        TableColumn<BookingDetails, LocalDate> dateCol = new TableColumn<>("Booking Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));

        TableColumn<BookingDetails, Integer> daysCol = new TableColumn<>("Days");
        daysCol.setCellValueFactory(new PropertyValueFactory<>("days"));

        TableColumn<BookingDetails, Integer> bagsCol = new TableColumn<>("Bags");
        bagsCol.setCellValueFactory(new PropertyValueFactory<>("bags"));

        TableColumn<BookingDetails, Integer> totalAmountCol = new TableColumn<>("Total Amount");
        totalAmountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        tableView.getColumns().addAll(bookingIdCol, dateCol, daysCol, bagsCol, totalAmountCol);
        tableView.setItems(FXCollections.observableArrayList(bookings));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return tableView;
    }

    private List<BookingDetails> getPreviousBookings(String username) {
        List<BookingDetails> bookings = new ArrayList<>();

        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login", "root", "aryashetty");
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM booking_details b, user u WHERE u.uid = b.uid AND username = ?"
                )
        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int bookingId = resultSet.getInt("bid");
                LocalDate bookingDate = resultSet.getDate("bookingDate").toLocalDate();
                int days = resultSet.getInt("days");
                int bags = resultSet.getInt("bags");
                int paymentStatus = resultSet.getInt("paymentStatus");
                int totalAmount = resultSet.getInt("totalAmount");
                BookingDetails booking = new BookingDetails(bookingId, bookingDate, days, bags, paymentStatus,totalAmount);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }

    private void navigateToBookingDetailsScene() {
        primaryStage.setTitle("LuggStorage");

        VBox vbox = new VBox(14);
        vbox.setPadding(new Insets(30, 50, 50, 50));
        vbox.setAlignment(Pos.CENTER);

        Label headingLabel = new Label("New Booking");
        headingLabel.getStyleClass().add("label-heading");

        TextField addressField = new TextField();
        addressField.setPromptText("Return Address");
        addressField.getStyleClass().add("text-field");

        DatePicker bookingDateField = new DatePicker();
        bookingDateField.setPromptText("Booking Date");

        TextField daysField = new TextField("1");
        daysField.setPromptText("Number of Days");
        daysField.getStyleClass().add("text-field");

        TextField bagsField = new TextField();
        bagsField.setPromptText("Number of Bags");
        bagsField.getStyleClass().add("text-field");

        Button proceedToPaymentButton = new Button("Proceed to Payment");
        proceedToPaymentButton.getStyleClass().add("button-main");
        proceedToPaymentButton.setOnMousePressed(e -> animateButton(proceedToPaymentButton));
        proceedToPaymentButton.setOnMouseReleased(e -> resetButtonAnimation(proceedToPaymentButton));

        Button backToHomePage = new Button("Back to Home");
        backToHomePage.getStyleClass().add("button-secondary");
        backToHomePage.setOnMousePressed(e -> animateButton(backToHomePage));
        backToHomePage.setOnMouseReleased(e -> resetButtonAnimation(backToHomePage));

        vbox.getChildren().addAll(
                headingLabel,
                bookingDateField,
                daysField,
                bagsField,
                addressField
        );

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(proceedToPaymentButton, backToHomePage);
        buttonBox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(buttonBox);

        proceedToPaymentButton.setOnAction(event -> {
            String address = addressField.getText();
            LocalDate bookingDate = bookingDateField.getValue();
            int days;
            int bags;

            if (bookingDate == null) {
                showAlert("Incomplete Information", "Please select a booking date.");
                return;
            }
            if (bookingDate.isBefore(LocalDate.now())) {
                showAlert("Invalid Booking Date", "Please select a date on or after today's date.");
                return;
            }
            if (address.isEmpty()) {
                showAlert("Incomplete Information", "Please fill in all the fields.");
                return;
            }
            try {
                days = Integer.parseInt(daysField.getText());
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number for Number of Days.");
                return;
            }
            try {
                bags = Integer.parseInt(bagsField.getText());
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid number for Number of Bags.");
                return;
            }
            int totalAmount = bags * days * 200;
            Date sqlBookingDate = Date.valueOf(bookingDate);
            navigateToPaymentConfirmationScene(address, sqlBookingDate.toLocalDate(), days, bags, totalAmount);
        });

        backToHomePage.setOnAction(event -> primaryStage.setScene(createHomePageScene()));

        Scene scene = new Scene(vbox, 380, 420);
        scene.getStylesheets().add(getClass().getResource("/com/example/login_second/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void navigateToPaymentConfirmationScene(String address, LocalDate bookingDate, int days, int bags, int totalAmount) {
        primaryStage.setTitle("LuggStorage");

        VBox vbox = new VBox(14);
        vbox.setPadding(new Insets(30, 50, 50, 50));
        vbox.setAlignment(Pos.CENTER);

        Label bookingDetailsLabel = new Label("Booking Details:");
        bookingDetailsLabel.getStyleClass().add("label-heading");

        Label returnAddressLabel = new Label("Return Address: " + address);
        returnAddressLabel.setWrapText(true);
        returnAddressLabel.setMaxWidth(200);
        Label dateLabel = new Label("Booking Date: " + bookingDate);
        Label daysLabel = new Label("Number of Days: " + days);
        Label bagsLabel = new Label("Number of Bags: " + bags);
        Label totalAmountLabel = new Label("Total Amount: " + totalAmount);

        Button cancelBookingButton = new Button("Cancel Booking");
        cancelBookingButton.getStyleClass().add("button-secondary");
        cancelBookingButton.setOnMousePressed(e -> animateButton(cancelBookingButton));
        cancelBookingButton.setOnMouseReleased(e -> resetButtonAnimation(cancelBookingButton));

        Button confirmPaymentButton = new Button("Confirm Payment");
        confirmPaymentButton.getStyleClass().add("button-main");
        confirmPaymentButton.setOnMousePressed(e -> animateButton(confirmPaymentButton));
        confirmPaymentButton.setOnMouseReleased(e -> resetButtonAnimation(confirmPaymentButton));

        vbox.getChildren().addAll(bookingDetailsLabel, returnAddressLabel, dateLabel, daysLabel, bagsLabel, totalAmountLabel, cancelBookingButton, confirmPaymentButton);

        cancelBookingButton.setOnAction(event -> primaryStage.setScene(createHomePageScene()));

        confirmPaymentButton.setOnAction(event -> {
            storeBookingDetails(address, bookingDate, days, bags, totalAmount);
            confirmPayment();
            showBookingCompletePopup();
            primaryStage.setScene(createHomePageScene());
        });

        Scene scene = new Scene(vbox, 340, 340);
        scene.getStylesheets().add(getClass().getResource("/com/example/login_second/style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void storeBookingDetails(String address, LocalDate bookingDate, int days, int bags, int totalAmount) {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login", "root", "aryashetty");
                PreparedStatement getUserIdStatement = connection.prepareStatement("SELECT uid FROM user WHERE username = ?");
                PreparedStatement storeBookingStatement = connection.prepareStatement(
                        "INSERT INTO booking_details (uid, bid, address, bookingDate, days, bags, totalAmount, paymentStatus) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                )
        ) {
            getUserIdStatement.setString(1, loginDetails.getUsername());
            ResultSet userIdResult = getUserIdStatement.executeQuery();

            if (userIdResult.next()) {
                int userId = userIdResult.getInt("uid");

                int bookingId = generateBookingId();

                storeBookingStatement.setInt(1, userId);
                storeBookingStatement.setInt(2, bookingId);
                storeBookingStatement.setString(3, address);
                storeBookingStatement.setDate(4, Date.valueOf(bookingDate));
                storeBookingStatement.setInt(5, days);
                storeBookingStatement.setInt(6, bags);
                storeBookingStatement.setInt(7, totalAmount);
                storeBookingStatement.setInt(8, 0);

                storeBookingStatement.executeUpdate();

                System.out.println("Booking Details Stored in Database");
            } else {
                System.out.println("User ID not found for the provided username: " + loginDetails.getUsername());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int generateBookingId() {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login", "root", "aryashetty");
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT MAX(bid) FROM booking_details")
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int maxBookingId = resultSet.getInt(1);
                return maxBookingId + 1;
            } else {
                return 1001;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void cancelBooking() {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login", "root", "aryashetty");
                PreparedStatement getLatestBookingStatement = connection.prepareStatement(
                        "SELECT bid FROM booking_details WHERE uid = ? ORDER BY bookingDate DESC LIMIT 1"
                );
                PreparedStatement deleteBookingStatement = connection.prepareStatement("DELETE FROM booking_details WHERE bid = ?")
        ) {
            getLatestBookingStatement.setInt(1, getUserId(loginDetails.getUsername()));
            ResultSet bookingIdResult = getLatestBookingStatement.executeQuery();

            if (bookingIdResult.next()) {
                int bookingId = bookingIdResult.getInt("bid");

                deleteBookingStatement.setInt(1, bookingId);
                deleteBookingStatement.executeUpdate();

                System.out.println("Booking with ID " + bookingId + " has been canceled and removed from the database");
            } else {
                System.out.println("No bookings found for the user with username: " + loginDetails.getUsername());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getUserId(String username) {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login", "root", "aryashetty");
                PreparedStatement getUserIdStatement = connection.prepareStatement("SELECT uid FROM user WHERE username = ?")
        ) {
            getUserIdStatement.setString(1, username);
            ResultSet resultSet = getUserIdStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("uid");
            } else {
                System.out.println("User not found for username: " + username);
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void confirmPayment() {
        try (
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/login", "root", "aryashetty");
                PreparedStatement getLatestBookingStatement = connection.prepareStatement(
                        "SELECT bid FROM booking_details WHERE uid = ? ORDER BY bookingDate DESC LIMIT 1"
                );
                PreparedStatement updatePaymentStatusStatement = connection.prepareStatement(
                        "UPDATE booking_details SET paymentStatus = 1 WHERE bid = ?"
                )
        ) {
            getLatestBookingStatement.setInt(1, getUserId(loginDetails.getUsername()));
            ResultSet bookingIdResult = getLatestBookingStatement.executeQuery();

            if (bookingIdResult.next()) {
                int bookingId = bookingIdResult.getInt("bid");

                updatePaymentStatusStatement.setInt(1, bookingId);
                updatePaymentStatusStatement.executeUpdate();

                System.out.println("Payment confirmed for booking with ID " + bookingId);
            } else {
                System.out.println("No bookings found for the user with username: " + loginDetails.getUsername());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showBookingCompletePopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Booking Complete!");
        alert.show();
    }

    private boolean sendLoginDetails(LoginDetails loginDetails) {
        try (
                Socket socket = new Socket("localhost", 12345);
                ObjectOutputStream outputToServer = new ObjectOutputStream(socket.getOutputStream());
                DataInputStream inputFromServer = new DataInputStream(socket.getInputStream())
        ) {
            outputToServer.writeObject(loginDetails);
            return inputFromServer.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
