package main.Client.View;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

import main.Client.Controller.LoginController;
import main.Server.Controller.UserController;
import main.util.Session;


public class LogIn extends Application {
    double fieldWidth = 320;
    double fieldHeight = 50;
    private TextField email;
    private PasswordField password;
    private Stage currentStage;
    private LoginController controller = new LoginController(this);

    @Override
    public void start(Stage stage) {
        this.currentStage = stage;
        // Left part
        Label lable1 = new Label("Welcome to HaloMeet");
        lable1.setFont(Font.font("Poppins", FontWeight.BOLD, 40));
        lable1.setTextFill(Color.BLACK);
        lable1.setPadding(new Insets(20, 0, 0, 30));

        Label lable2 = new Label("Your Space for Real-Time Collaboration");
        lable2.setFont(Font.font("Poppins", FontWeight.NORMAL, 22));
        lable2.setTextFill(Color.BLACK);
        lable2.setPadding(new Insets(0, 0, 50, 30));


        Image login_image = new Image(getClass().getResource("/images/login.jfif").toExternalForm());
        ImageView login_imageView = new ImageView(login_image);
        login_imageView.setFitWidth(600);
        login_imageView.setFitHeight(700);
        login_imageView.setPreserveRatio(true);

        VBox leftPane = new VBox(5, lable1, lable2, login_imageView);
        leftPane.setAlignment(Pos.TOP_LEFT);
        leftPane.setPadding(new Insets(30, 0, 0, 50));

        // Right part
        Image logo = new Image(getClass().getResource("/images/logo2.png").toExternalForm());
        ImageView logo_imageView = new ImageView(logo);
        logo_imageView.setFitWidth(50);
        logo_imageView.setFitHeight(70);
        logo_imageView.setPreserveRatio(true);
        Label label3 = new Label("HaloMeet");
        label3.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        label3.setTextFill(Color.BLACK);
        label3.setPadding(new Insets(-5, 0, 0, 0));

        HBox app = new HBox(10);
        app.getChildren().addAll(logo_imageView, label3);
        app.setAlignment(Pos.TOP_CENTER);
        app.setPadding(new Insets(50, 0, 30, 0));

        // Title
        Label label4 = new Label("USER LOGIN");
        label4.setFont(Font.font("Poppins", FontWeight.BOLD, 25));
        label4.setTextFill(Color.BLACK);

        Label label5 = new Label("Welcome back — let’s connect and create");
        label5.setFont(Font.font("Poppins", FontWeight.NORMAL, 18));
        label5.setTextFill(Color.BLACK);
        label5.setPadding(new Insets(0, 0, 25, 0));

        // Form
//        Label emailLabel = new Label("Email");
//        emailLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 15));
        email = new TextField();
        email.setPromptText("Enter your email");
        email.setFont(Font.font("Poppins", FontWeight.NORMAL, 15));
        String textFieldStyle = """
                -fx-text-fill: black;
                -fx-prompt-text-fill: gray;
                -fx-background-radius: 20;
                -fx-border-radius: 20;
                -fx-border-color: gray; 
                -fx-border-width: 1.5;
                """;
       String textFieldFocus = """
               -fx-text-fill: black;
               -fx-prompt-text-fill: gray;
               -fx-background-radius: 20;
               -fx-border-radius: 20;
               -fx-border-color: #b333e9; 
               -fx-border-width: 2;
               """;
       email.setStyle(textFieldStyle);
       email.setMaxWidth(fieldWidth);
       email.setPrefHeight(fieldHeight);
       // Change style when focusing
        email.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(t1) {
                email.setStyle(textFieldFocus);
            } else {
                email.setStyle(textFieldStyle);
            }
        });
        VBox.setMargin(email, new Insets(0, 0, 5, 0));

//        Label passwordLabel = new Label("Password");
//        passwordLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 15));
        password = new PasswordField();
        password.setPromptText("Enter your password");
        password.setFont(Font.font("Poppins", FontWeight.NORMAL, 15));
        password.setStyle(textFieldStyle);
        password.focusedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (t1) {
                password.setStyle(textFieldFocus);
            } else {
                password.setStyle(textFieldStyle);
            }
        }));
        VBox.setMargin(password, new Insets(0, 0, 5, 0));
        password.setMaxWidth(fieldWidth);
        password.setPrefHeight(fieldHeight);

        Button signInBtn = new Button("Sign in");
        signInBtn.setFont(Font.font("Poppins", FontWeight.BOLD, 15));
        String signInBtnStyle = """
                -fx-background-color: #8900f2;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 30;
                -fx-border-radius: 30;
                -fx-border-color: transparent;
                -fx-cursor: hand;
                """;
        String signInBtnHover = """
                -fx-background-color: #6a00f4; 
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 30;
                -fx-border-radius: 30;
                -fx-border-color: transparent;
                -fx-cursor: hand;
                """;
        signInBtn.setStyle(signInBtnStyle);
        signInBtn.setOnMouseEntered(e -> {
            signInBtn.setStyle(signInBtnHover);
        });
        signInBtn.setOnMouseExited(e -> {
            signInBtn.setStyle(signInBtnStyle);
        });
        signInBtn.setMaxWidth(fieldWidth);
        signInBtn.setPrefHeight(fieldHeight);

        signInBtn.setOnAction(e -> controller.onClickLogin());

        // Keep the same size for email, password and button when window change size
        signInBtn.prefWidthProperty().bind(email.widthProperty());
        password.prefWidthProperty().bind(email.widthProperty());

        Label signupLabel = new Label("Don’t have an account?");
        signupLabel.setFont(Font.font("null", FontWeight.NORMAL, 13));
        Hyperlink signupLink = new Hyperlink("Sign Up");
        signupLink.setFont(Font.font("null", FontWeight.NORMAL, 13));
        signupLink.setTextFill(Color.web("#7b2cbf"));
        HBox signupBox = new HBox(5, signupLabel, signupLink);
        signupBox.setAlignment(Pos.CENTER);

        signupLink.setOnAction(e -> {
            LogUp logUpPage = new LogUp();
            Stage stage1 = new Stage();
            try {
                logUpPage.start(stage1);
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox form = new VBox(10, label4,label5, email, password, signInBtn, signupBox);
        form.setPadding(new Insets(40));
        form.setAlignment(Pos.BOTTOM_CENTER);

        VBox rightPane = new VBox(10, app, form);
        rightPane.setPadding(new Insets(20, 40, 30, 40));

        HBox root = new HBox(leftPane, rightPane);
        root.setSpacing(10);
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        root.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));

        // Khi cửa sổ thay đổi kích thước, mỗi pane chiếm 50%
        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            double half = newVal.doubleValue() / 2;
            leftPane.setPrefWidth(half);
            rightPane.setPrefWidth(half);
        });

        Scene scene = new Scene(root);
        stage.setTitle("Log in");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public String getEmail() {
        return email.getText().trim();
    }

    public String getPassword() {
        return password.getText().trim();
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public void openDashboard() {
        Dashboard dashboard = new Dashboard();
        Stage dashboardStage = new Stage();
        try {
            dashboard.start(dashboardStage);
            currentStage.close(); // stage là biến Stage hiện tại
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


//    public static void main(String[] args) {
//        launch(args);
//    }
}
