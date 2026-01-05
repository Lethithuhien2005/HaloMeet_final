package main.Client.View;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Client.Controller.PersonalController;
import main.Server.Controller.AvatarService;
import main.Server.DAO.UserDAO;
import main.Server.Model.User;
import main.util.PasswordUtils;
import main.util.Session;

import java.io.*;
import java.net.Socket;
import java.util.Objects;


import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import shared.DTO.UserDTO;


public class PersonalProfile extends StackPane{
    private StackPane contentPane;

    private Stage chatStage;
    private String currentName = "Nguyen Anh";
    private Image currentAvatar = new Image("./images/chatPage/profile.png");;
    private String currentEmail;
    private PersonalController personalController = new PersonalController(this);
    private UserDTO currentUser;

    String email = Session.getInstance().getEmail();  // <<< LẤY EMAIL ĐÃ LƯU
    private String userIdHex;



    String bio = "Love coding, traveling, and coffee";
    String skill = "Java, JavaFX, SQL";
    String gender = "Male";
    String lastName = "Nguyen";
    String firstName = "Quynh Anh";
    String website = "https://quynhanh.dev";
    String location = "Ho Chi Minh, Vietnam";
    public Label currentPasswordMsg;
    public PasswordField currentPasswordField;
    public PasswordField newPasswordField;
    public Label confirmPasswordMsg;
    public PasswordField confirmPasswordField;


    public PersonalProfile(StackPane contentPane, String currentEmail) {
        this.contentPane = contentPane;
        this.currentEmail = currentEmail;
        personalController.getUserProfile(currentEmail);
    }

    private VBox rightVBox;
    private Button activeMenuButton = null;

    private void initializeUI() {
        userIdHex = Session.getInstance().getUserIdHex();


        // ====== ROOT ======
        HBox root = new HBox();
        root.setPadding(new Insets(20));
        root.setSpacing(20);
        root.setStyle("-fx-background-color: #F8F7FF;");
        root.setAlignment(Pos.CENTER);



        ImageView backIcon = new ImageView(new Image("images/profiles/back_non_backgr.png"));
        backIcon.setFitWidth(23);
        backIcon.setFitHeight(23);
        backIcon.setPreserveRatio(true);



        double size = 160;
        // ImageView
        ImageView avatar = new ImageView(currentAvatar);
        // Lấy kích thước gốc ảnh
        double imgWidth = avatar.getImage().getWidth();
        double imgHeight = avatar.getImage().getHeight();
        double minSide = Math.min(imgWidth, imgHeight);
        // Crop trung tâm ảnh thành vuông
        avatar.setViewport(new Rectangle2D(
                (imgWidth - minSide) / 2,
                (imgHeight - minSide) / 2,
                minSide,
                minSide
        ));
        avatar.setFitWidth(size);
        avatar.setFitHeight(size);
        avatar.setPreserveRatio(false); // để vừa khít size

        // Clip hình tròn
        Circle clip = new Circle(size / 2, size / 2, size / 2);
        avatar.setClip(clip);


//        avatar.setOnMouseClicked(e -> {
//            FileChooser fc = new FileChooser();
//            File file = fc.showOpenDialog(null);
//            System.out.println("Sending file for userIdHex: " + userIdHex + ", path: " + file.getAbsolutePath());
//
//            if(file != null && userIdHex != null){
//                try(Socket socket = new Socket("localhost", 12345);
//                    OutputStream out = socket.getOutputStream();
//                    InputStream in = socket.getInputStream();
//                    PrintWriter writer = new PrintWriter(out, true);
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
//
//                    // gửi request UPLOAD_AVATAR + userId
//                    writer.println("UPLOAD_AVATAR");
//                    writer.println(userIdHex);
//
//                    // gửi độ dài file trước
//                    writer.println(file.length());
//                    // gửi extension để server giữ đúng định dạng
////                    writer.println(file.getName().substring(file.getName().lastIndexOf(".")));
//                    writer.println(file.getName());
//
//                    writer.flush();
//
//                    // gửi dữ liệu file
////                    Files.copy(file.toPath(), out);
////                    out.flush();
//                    // gửi file: đọc từng chunk 4KB
//                    try (FileInputStream fis = new FileInputStream(file)) {
//                        byte[] buffer = new byte[4096];
//                        int read;
//                        while((read = fis.read(buffer)) != -1) {
//                            out.write(buffer, 0, read);
//                        }
//                    }
//                    out.flush();
//
//                    // nhận phản hồi
//                    String resp = reader.readLine();
//                    if("UPLOAD_SUCCESS".equals(resp)) {
//                        // cập nhật UI sau khi upload
////                        avatar.setImage(new Image(file.toURI().toString()));
//
//                        Image newImg = new Image(file.toURI().toString());
//                        avatar.setImage(newImg);
//
//// re-crop
//                        double w = newImg.getWidth();
//                        double h = newImg.getHeight();
//                        double side = Math.min(w, h);
//
//                        avatar.setViewport(new Rectangle2D(
//                                (w - side) / 2,
//                                (h - side) / 2,
//                                side,
//                                side
//                        ));
//
//
//                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                        alert.setTitle("Upload Avatar");
//                        alert.setHeaderText(null);
//                        alert.setContentText("Upload file thành công!");
//                        alert.showAndWait();
//                    }
//                    else {
//                        Alert alert = new Alert(Alert.AlertType.ERROR);
//                        alert.setTitle("Upload Avatar");
//                        alert.setHeaderText(null);
//                        alert.setContentText("Upload thất bạicho@");
//                        alert.showAndWait();
//                    }
//
//                } catch(IOException ex){
//                    ex.printStackTrace();
//                    Alert alert = new Alert(Alert.AlertType.ERROR);
//                    alert.setTitle("Upload Avatar");
//                    alert.setHeaderText(null);
//                    alert.setContentText("Có lỗi xảy ra khi upload file.");
//                    alert.showAndWait();
//                }
//            }
//        });


//        try(Socket socket = new Socket("localhost", 12345);
//            InputStream in = socket.getInputStream();
//            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
//
//            writer.println("GET_AVATAR");
//            writer.println(userIdHex);
//
//            // đọc độ dài file
//            String lenStr = reader.readLine();
//            if(lenStr == null) throw new IOException("Không nhận được chiều dài file từ server");
//            long length = Long.parseLong(lenStr);
//
//            // đọc file theo buffer nhỏ (4KB) thay vì đọc hết vào mảng
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            byte[] buf = new byte[4096];
//            long remaining = length;
//            int read;
//            while(remaining > 0 && (read = in.read(buf, 0, (int)Math.min(buf.length, remaining))) != -1){
//                baos.write(buf, 0, read);
//                remaining -= read;
//            }
//
//            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//            avatar.setImage(new Image(bais));
//
//        } catch(IOException ex){
//            ex.printStackTrace();
//        }


        VBox leftVBox = new VBox();
        leftVBox.setAlignment(Pos.TOP_CENTER);
        leftVBox.setSpacing(20);
        leftVBox.setPadding(new Insets(40, 20, 0, 20));
//        leftVBox.setStyle("-fx-background-color: #FFFFFF;");
        leftVBox.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 15;
            -fx-background-insets: 0;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 15, 0.1, 0, 4);
        """);

        leftVBox.setPrefWidth(300);   // chiều rộng cố định để tạo layout đẹp

        // Avatar (đã có từ code trước)
        leftVBox.getChildren().add(avatar);

        Label fullnameLabel;
        Label usernameLabel;
        // Kiểm tra null để tránh lỗi
        if (currentUser == null) {
            System.out.println("User not found!");
            return;
        }
        if (currentUser != null) {
            // Fullname
            fullnameLabel = new Label(currentUser.getFullName()); // hoặc fullName nếu có
            fullnameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

            // Username
            usernameLabel = new Label("_" + currentUser.getUsername() + "_");
            usernameLabel.setFont(Font.font("Arial", 14));
            usernameLabel.setTextFill(Color.GRAY);
            usernameLabel.setPadding(new Insets(0, 0, 20, 0));

        } else {
            fullnameLabel = new Label("Unknown");
            usernameLabel = new Label("@unknown");
        }


        // Menu buttons
        Button btnPersonalInfo = createMenuButton("Personal Information");
        Button btnLoginPassword = createMenuButton("Login & Password");
        Button btnLogout = createMenuButton("Log Out");

        leftVBox.getChildren().addAll(fullnameLabel, usernameLabel,
                btnPersonalInfo, btnLoginPassword);


        rightVBox = new VBox();
        rightVBox.setSpacing(20);
        rightVBox.setPadding(new Insets(30));
//        rightVBox.setStyle("-fx-background-color: #F8F7FF;");
        rightVBox.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 15;
            -fx-border-radius: 15;
            -fx-border-color: rgba(0,0,0,0.05);
            -fx-border-width: 1;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 15, 0.1, 0, 4);
        """);

        rightVBox.setPrefWidth(700);

        HBox.setHgrow(rightVBox, Priority.ALWAYS);
        rightVBox.setMaxWidth(Double.MAX_VALUE);

        leftVBox.setMinHeight(450);
        rightVBox.setMinHeight(450);

        btnPersonalInfo.setOnAction(e -> {
            loadPersonalInfo();
            highlightMenu(btnPersonalInfo);
        });

        btnLoginPassword.setOnAction(e -> {
            loadLoginPassword();
            highlightMenu(btnLoginPassword);
        });

        btnLogout.setOnAction(e -> {
            loadLogout();
            highlightMenu(btnLogout);
        });


        loadPersonalInfo();
        highlightMenu(btnPersonalInfo);  // tô tím nút Personal Information

        root.getChildren().addAll(leftVBox, rightVBox);

        VBox wrapper = new VBox(root);
        wrapper.setAlignment(Pos.CENTER);        // căn giữa theo chiều dọc
        wrapper.setFillWidth(true);              // cho phép root chiếm hết chiều ngang
        wrapper.setStyle("-fx-background-color: #F8F7FF;");


        ScrollPane scrollPane = new ScrollPane(wrapper);
        scrollPane.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            wrapper.setMinHeight(newVal.getHeight());
        });

        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("""
            -fx-background-color: transparent;
            -fx-background-insets: 0;
            -fx-padding: 0;
        """);

        String scrollMoreStyle = """
            .scroll-pane {
                -fx-background-color: transparent;
            }
        
            .scroll-pane .viewport {
                -fx-background-color: transparent;
            }
        
            .scroll-pane .scroll-bar:vertical {
                -fx-background-color: transparent;
                -fx-pref-width: 8px;
            }
        
            .scroll-pane .scroll-bar:vertical .thumb {
                -fx-background-color: rgba(138,43,226,1.0); /* tím đậm, alpha = 1 */
                -fx-background-insets: 2;
                -fx-background-radius: 4;
            }
        
            .scroll-pane .scroll-bar:vertical .track {
                -fx-background-color: transparent;
            }
        
            .scroll-pane .scroll-bar:horizontal {
                -fx-background-color: transparent;
                -fx-pref-height: 6px;
            }
        
            .scroll-pane .scroll-bar:horizontal .thumb {
                -fx-background-color: rgba(138,43,226,1.0); /* tím đậm, alpha = 1 */
                -fx-background-radius: 3;
            }
        
            .scroll-pane .scroll-bar:horizontal .track {
                -fx-background-color: transparent;
            }            
        """;

        scrollPane.getStylesheets().add("data:text/css," + scrollMoreStyle.replace("\n", ""));

        scrollPane.getStyleClass().add("scroll-rounded");

        String styleBorderScroll = """
            .scroll-rounded .viewport {
                -fx-background-color: transparent;
            }
        """;

        scrollPane.getStylesheets().add("data:text/css," + styleBorderScroll.replace("\n", ""));


//        Scene scene = new Scene(scrollPane);

        scrollPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/custom_radio.css")).toExternalForm()
        );
        scrollPane.getStylesheets().add(getClass().getResource("/css/datepicker.css").toExternalForm());


        this.getChildren().add(scrollPane);
    }

    // ====== HELPER FUNCTION: tạo section ======
    private HBox createTwoColumn(String leftText, Node rightContent, VBox content) {
        HBox sec = new HBox(20);
        sec.setAlignment(Pos.TOP_LEFT);

        // ⭐ STYLE
        sec.setPadding(new Insets(15));


        // Cột trái
        Label left = new Label(leftText);
        left.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        left.setMinWidth(250);            // độ rộng cố định
        left.setAlignment(Pos.TOP_LEFT);

        // Cột phải (nới rộng hết phần còn lại)
        HBox.setHgrow(rightContent, Priority.ALWAYS);

        // Bind width section bằng width của content
        sec.maxWidthProperty().bind(content.widthProperty());
        sec.prefWidthProperty().bind(content.widthProperty());

        sec.getChildren().addAll(left, rightContent);
        return sec;
    }
    private VBox createReadMoreText(String fullText, int limitChars) {
        VBox box = new VBox();
        box.setSpacing(5);

        Label contentLabel = new Label();
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(Double.MAX_VALUE);

        Label toggle = new Label("Read more");
        toggle.setTextFill(Color.web("#9B59B6"));
        toggle.setStyle("-fx-cursor: hand; -fx-font-weight: bold;");

        // Nếu text ngắn thì trả lại label luôn
        if (fullText.length() <= limitChars) {
            contentLabel.setText(fullText);
            box.getChildren().add(contentLabel);
            return box;
        }

        // Rút gọn ban đầu
        String shortText = fullText.substring(0, limitChars) + "...";
        contentLabel.setText(shortText);

        toggle.setOnMouseClicked(e -> {
            if (toggle.getText().equals("Read more")) {
                contentLabel.setText(fullText);
                toggle.setText("Show less");
            } else {
                contentLabel.setText(shortText);
                toggle.setText("Read more");
            }
        });

        box.getChildren().addAll(contentLabel, toggle);
        return box;
    }
//    private HBox createTag(String title, String subtitle, String imagePath) {
//        // ===== ICON =====
//        ImageView icon = new ImageView(new Image(imagePath));
//        icon.setFitWidth(48);
//        icon.setFitHeight(48);
//        icon.setPreserveRatio(true);
//
//        // ===== TITLE =====
//        Label titleLabel = new Label(title);
//        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #000000; -fx-font-size: 17;");
//
//        // ===== SUBTITLE =====
//        Label subtitleLabel = new Label(subtitle);
//        subtitleLabel.setStyle("-fx-text-fill: #555; -fx-font-size: 11;");
//
//        // ===== TEXT BOX =====
//        VBox textBox = new VBox(3);
//        textBox.getChildren().addAll(titleLabel, subtitleLabel);
//        textBox.setAlignment(Pos.CENTER_LEFT);
//
//        // ===== CONTENT (ICON + TEXT) =====
//        HBox contentBox = new HBox(25);  // khoảng cách giữa icon và text
//        contentBox.getChildren().addAll(icon, textBox);
//        contentBox.setAlignment(Pos.CENTER_LEFT);
//
//        // ===== TAG CONTAINER =====
//        HBox box = new HBox(contentBox);
//        box.setAlignment(Pos.CENTER_LEFT);
//        box.setPadding(new Insets(8, 12, 8, 25));
//
//        box.setStyle("""
//            -fx-background-color: transparent;
//            -fx-border-color: #BBBBBB;
//            -fx-border-width: 0.8;
//            -fx-border-radius: 8;
//            -fx-background-radius: 8;
//        """);
//
//        box.setPrefHeight(90);
//        box.setMinHeight(90);
//        box.setMaxWidth(Double.MAX_VALUE);
//
//        return box;
//    }
//
//    private VBox createSection(String title, String contentText) {
//        VBox section = new VBox();
//        section.setSpacing(10);
//        section.setPadding(new Insets(15));
//        section.setStyle("""
//            -fx-background-color: white;
//            -fx-background-radius: 15;
//            -fx-border-radius: 15;
//            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.5, 0, 2);
//        """);
//
//        Label titleLabel = new Label(title);
//        titleLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
//
//        Label contentLabel = new Label(contentText);
//        contentLabel.setWrapText(true);
//
//        section.getChildren().addAll(titleLabel, contentLabel);
//
//        return section;
//    }
//    private VBox createProfileInfoSection() {
//        VBox section = new VBox();
//        section.setSpacing(10);
//        section.setPadding(new Insets(15));
//        section.setStyle("""
//        -fx-background-color: white;
//        -fx-background-radius: 15;
//        -fx-border-radius: 15;
//        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.5, 0, 2);
//    """);
//
//        Label titleLabel = new Label("Profile Information");
//        titleLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
//
//        // Info labels
//        Label bioLabel = new Label("Bio: " + bio);
//        Label skillLabel = new Label("Skill: " + skill);
//        Label genderLabel = new Label("Gender: " + gender);
//        Label lastNameLabel = new Label("Last Name: " + lastName);
//        Label firstNameLabel = new Label("First Name: " + firstName);
//        Label emailLabel = new Label("Email: " + email);
//        Label websiteLabel = new Label("Website: " + website);
//        Label locationLabel = new Label("Location: " + location);
//
//        // Nếu muốn mỗi field xuống dòng riêng
//        VBox infoBox = new VBox(5);
//        infoBox.getChildren().addAll(bioLabel, skillLabel, genderLabel, lastNameLabel,
//                firstNameLabel, emailLabel, websiteLabel, locationLabel);
//
//        section.getChildren().addAll(titleLabel, infoBox);
//        return section;
//    }


    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(220);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setFont(Font.font(15));

        // Style default
        btn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #4A4A4A;
            -fx-padding: 13 18;
            -fx-cursor: hand;
        """);

        btn.setOnMouseEntered(e -> {
            if (btn != activeMenuButton) {
                btn.setStyle("""
                    -fx-background-color: #EFE9FF;
                    -fx-text-fill: black;
                    -fx-padding: 13 18;
                """);
            }
        });

        btn.setOnMouseExited(e -> {
            if (btn != activeMenuButton) {
                btn.setStyle("""
                    -fx-background-color: transparent;
                    -fx-text-fill: #4A4A4A;
                    -fx-padding: 13 18;
                """);
            }
        });

        return btn;
    }
    private void highlightMenu(Button btn) {

        // reset nút cũ
        if (activeMenuButton != null) {
            activeMenuButton.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #4A4A4A;
            -fx-padding: 13 18;
        """);
        }

        // tô tím nút mới
        btn.setStyle("""
        -fx-background-color: #A569FF;
        -fx-text-fill: white;
        -fx-padding: 13 18;
        -fx-background-radius: 6;
    """);

        activeMenuButton = btn;
    }
    private void loadPersonalInfo() {
        rightVBox.getChildren().clear();

        personalController.getUserProfile(currentEmail);

        Label title = new Label("Personal Information");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 23));
        title.setPadding(new Insets(0, 0, 20, 0)); // top, right, bottom, left


        VBox form = new VBox(20);

        // ===========================
        //  GENDER (RadioButton tròn)
        // ===========================
        // ToggleGroup
        ToggleGroup genderGroup = new ToggleGroup();

        // Radio buttons
        RadioButton maleRadio = new RadioButton("Male");
        RadioButton femaleRadio = new RadioButton("Female");
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio.setToggleGroup(genderGroup);
        // Style radio button (xoá màu xanh, chỉ màu tím)
        maleRadio.getStyleClass().add("custom-radio");
        femaleRadio.getStyleClass().add("custom-radio");

        // HBox chứa Male – Female
        HBox genderOptions = new HBox(20, maleRadio, femaleRadio);
        genderOptions.setAlignment(Pos.CENTER_LEFT);

        Label genderLabel = new Label("Gender");
        genderLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        HBox genderBox = new HBox(40, genderLabel, genderOptions);
        genderBox.setAlignment(Pos.CENTER_LEFT);
        genderBox.setSpacing(60);

        // Đặt gender nếu có dữ liệu
        if (currentUser != null && currentUser.getGender() != null) {  // giả sử bạn lưu gender trong role hoặc thêm gender
            String gender = currentUser.getGender(); // nếu bạn thêm trường gender thì lấy user.getGender()
            if (gender.equalsIgnoreCase("Male")) maleRadio.setSelected(true);
            else if (gender.equalsIgnoreCase("Female")) femaleRadio.setSelected(true);
        }

        // ===========================
        // FIRST NAME + LAST NAME
        // ===========================
        TextField firstNameField = createRoundedTextField();
        TextField lastNameField = createRoundedTextField();

        firstNameField.setPromptText("First Name");
        lastNameField.setPromptText("Last Name");

        // Label riêng
        Label firstNameLabel = new Label("First Name");
        Label lastNameLabel = new Label("Last Name");

        firstNameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        lastNameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));

        String fullName = currentUser.getFullName() != null ? currentUser.getFullName() : "";
        String firstName = "";
        String lastName = "";

        if (!fullName.isEmpty()) {
            fullName = fullName.trim();
            int lastSpace = fullName.lastIndexOf(" ");
            if (lastSpace != -1) {
                firstName = fullName.substring(0, lastSpace);
                lastName = fullName.substring(lastSpace + 1);
            } else {
                firstName = fullName;
                lastName = "";
            }
        }

        // Set vào TextField
        firstNameField.setText(firstName);
        lastNameField.setText(lastName);

        // Mỗi field đặt trong một VBox riêng
        VBox firstNameBox = new VBox(5, firstNameLabel, firstNameField);
        VBox lastNameBox = new VBox(5, lastNameLabel, lastNameField);

        // Cho phép giãn đều 2 phía
        HBox.setHgrow(firstNameBox, Priority.ALWAYS);
        HBox.setHgrow(lastNameBox, Priority.ALWAYS);

        firstNameBox.setMaxWidth(Double.MAX_VALUE);
        lastNameBox.setMaxWidth(Double.MAX_VALUE);

        HBox nameBox = new HBox(20, firstNameBox, lastNameBox);
        nameBox.setPrefWidth(Double.MAX_VALUE);


        // ===========================
        // EMAIL
        // ===========================
        TextField emailField = createRoundedTextField();
        emailField.setText(currentUser != null && currentUser.getEmail() != null ? currentUser.getEmail() : "");
        // Read only
        emailField.setEditable(false);
        // Style nhạt hơn (màu chữ xám)
        emailField.setStyle("""
            -fx-opacity: 0.7;          /* nhạt hơn */
            -fx-background-color: #F0F0F0; /* nền nhạt */
        """);
        VBox emailBox = labeledField("Email", emailField);

        // ===========================
        // ADDRESS
        // ===========================
        TextField addressField = createRoundedTextField();
        if (currentUser != null && currentUser.getAddress() != null && !currentUser.getAddress().isEmpty()) {
            addressField.setText(currentUser.getAddress());
        } else {
            addressField.setPromptText("Address");
        }
        VBox addressBox = labeledField("Address", addressField);

        // ===========================
        // PHONE + DATE OF BIRTH
        // ===========================
        TextField phoneField = createRoundedTextField();
        phoneField.setText(currentUser != null && currentUser.getPhone() != null ? currentUser.getPhone() : "");
        phoneField.setPromptText("Phone Number");

        DatePicker dobPicker = new DatePicker();
        dobPicker.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        dobPicker.setValue(currentUser.getDob() != null ? currentUser.getDob() : null);

        StackPane dobWrapper = new StackPane(dobPicker);
        dobWrapper.setStyle("""
            -fx-background-color: #F4F4F4;
            -fx-background-radius: 10;
            -fx-border-radius: 10;
        """);

        HBox.setHgrow(dobWrapper, Priority.ALWAYS);
        dobPicker.setMaxWidth(Double.MAX_VALUE);
        dobPicker.getEditor().setMaxWidth(Double.MAX_VALUE);

        // Editor bên trong cũng phải remove border
        dobPicker.getEditor().setStyle("""
            -fx-background-color: transparent;
            -fx-border-color: transparent;
            -fx-padding: 10;
        """);


        phoneField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(phoneField, Priority.ALWAYS);

        // Label riêng
        Label phoneLabel = new Label("Phone Number");
        Label dobLabel = new Label("Date of Birth");

        phoneLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        dobLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));

        // VBox từng field
        VBox phoneBox = new VBox(5, phoneLabel, phoneField);
        VBox dobBox = new VBox(5, dobLabel, dobWrapper);

        HBox.setHgrow(phoneBox, Priority.ALWAYS);
        HBox.setHgrow(dobBox, Priority.ALWAYS);

        // HBox chứa 2 field
        HBox phoneDobBox = new HBox(20, phoneBox, dobBox);
        phoneDobBox.setPrefWidth(Double.MAX_VALUE);



        // ===========================
        // LOCATION
        // ===========================
        TextField locationField = createRoundedTextField();
        if (currentUser != null && currentUser.getAddress() != null && !currentUser.getAddress().isEmpty()) {
            locationField.setText(currentUser.getAddress());
        } else {
            locationField.setPromptText("Location");
        }
        VBox locationBox = labeledField("Location", locationField);

        // ===========================
        // BUTTONS
        // ===========================
        HBox buttons = new HBox(15);
        buttons.setPadding(new Insets(30, 0, 0, 0)); // PADDING TOP 20px

        Button discardBtn = new Button("DISCARD CHANGES");
        discardBtn.setStyle("""
            -fx-background-color: white;         /* Nền trắng */
            -fx-text-fill: #663399;              /* Màu chữ xám */
            -fx-border-color: #663399;           /* Viền xám */
            -fx-border-width: 1;                 /* Độ dày viền */
            -fx-background-radius: 10;           /* Bo góc cho nền */
            -fx-border-radius: 10;               /* Bo góc cho viền */
            -fx-padding: 10 20;                  /* Khoảng cách bên trong */
            -fx-font-size: 15px;      
        """);
        discardBtn.setMaxWidth(Double.MAX_VALUE);
        discardBtn.setPrefHeight(50);              // chiều cao cố định, ví dụ 40px

        Button saveBtn = new Button(" SAVE CHANGES   ");
        saveBtn.setStyle("""
            -fx-background-color: #9966CC;
            -fx-padding: 10 20;
            -fx-text-fill: white;
            -fx-font-size: 15px;      
            -fx-background-radius: 10;
        """);
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setPrefHeight(50);

        discardBtn.setOnAction(e -> {
            if (currentUser != null) {
                // Gender
                String gender = currentUser.getGender();
                if (gender != null) {
                    maleRadio.setSelected(gender.equalsIgnoreCase("Male"));
                    femaleRadio.setSelected(gender.equalsIgnoreCase("Female"));
                } else {
                    genderGroup.selectToggle(null);  // bỏ chọn nếu null
                }
                // Full Name -> First + Last
                String fullNamee = currentUser.getFullName() != null ? currentUser.getFullName() : "";
                if (!fullNamee.isEmpty()) {
                    fullNamee = fullNamee.trim();
                    int lastSpace = fullNamee.lastIndexOf(" ");
                    firstNameField.setText(lastSpace != -1 ? fullNamee.substring(0, lastSpace) : fullNamee);
                    lastNameField.setText(lastSpace != -1 ? fullNamee.substring(lastSpace + 1) : "");
                } else {
                    firstNameField.setText("");
                    lastNameField.setText("");
                }
                // Email
                emailField.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
                // Phone
                phoneField.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
                // Address
                addressField.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
                // Location (nếu có field riêng)
                locationField.setText(currentUser.getAddress() != null ? currentUser.getAddress() : "");
                // Date of Birth
                dobPicker.setValue(currentUser.getDob());
            }
        });
        saveBtn.setOnAction(e -> {
            if (currentUser != null) {
                // Update dữ liệu từ form
                String updatedFullName = firstNameField.getText().trim() + " " + lastNameField.getText().trim();
                currentUser.setFullName(updatedFullName);

                String updatedGender = maleRadio.isSelected() ? "Male" : femaleRadio.isSelected() ? "Female" : null;
                currentUser.setGender(updatedGender);

                currentUser.setPhone(phoneField.getText().trim());
                currentUser.setAddress(addressField.getText().trim());
                currentUser.setDob(dobPicker.getValue());

                // Nếu có location riêng, dùng locationField.getText()

                // Update vào database
               personalController.updateUserProfile(currentUser);
            }
        });


        // -------------------------------
        // HOVER EFFECTS (Scale + Brightness)
        // -------------------------------
        addHoverEffect(discardBtn, true);  // discard
        addHoverEffect(saveBtn, false);    // save

        // VBox containers
        VBox leftBox = new VBox(discardBtn);
        VBox rightBox = new VBox(saveBtn);

        HBox.setHgrow(leftBox, Priority.ALWAYS);
        HBox.setHgrow(rightBox, Priority.ALWAYS);

        leftBox.setAlignment(Pos.CENTER_LEFT);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        buttons.getChildren().addAll(leftBox, rightBox);


        // ===========================
        // ADD ALL TO FORM
        // ===========================
        form.getChildren().addAll(
                genderBox,
                nameBox,
                emailBox,
                addressBox,
                phoneDobBox,
                locationBox
        );

        rightVBox.getChildren().addAll(title, form, buttons);
    }
    private TextField createRoundedTextField() {
        TextField tf = new TextField();
        tf.setStyle("""
        -fx-background-color: #F4F4F4;
        -fx-background-radius: 10;
        -fx-border-radius: 10;
        -fx-padding: 10;
        -fx-border-color: transparent;
    """);
        return tf;
    }
    private VBox labeledField(String label, Node field) {
        Label lb = new Label(label);
        lb.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        VBox box = new VBox(5, lb, field);
        return box;
    }
    private void addHoverEffect(Button btn, boolean isDiscard) {
        btn.setOnMouseEntered(e -> {
            if (isDiscard) {
                btn.setStyle("""
                -fx-background-color: white;
                -fx-text-fill: #330066;       /* chữ xám đậm hơn khi hover */
                -fx-border-color: #330066;
                -fx-border-width: 1;
                -fx-font-size: 15px;      
                -fx-background-radius: 10;
                -fx-border-radius: 10;
                -fx-padding: 10 20;
            """);
            } else {
                btn.setStyle("""
                -fx-background-radius: 10;
                -fx-padding: 10 20;
                -fx-text-fill: white;
                -fx-font-size: 15px;      
                -fx-background-color: #663399;   /* sáng hơn khi hover */
            """);
            }
            btn.setScaleX(0.95);  // Scale +5%
            btn.setScaleY(1);
            btn.setCursor(Cursor.HAND);
        });

        btn.setOnMouseExited(e -> {
            if (isDiscard) {
                btn.setStyle("""
                -fx-background-color: white;
                -fx-text-fill: #663399;
                -fx-border-color: #663399;
                -fx-border-width: 1;
                -fx-background-radius: 10;
                -fx-font-size: 15px;      
                -fx-border-radius: 10;
                -fx-padding: 10 20;
            """);
            } else {
                btn.setStyle("""
                -fx-background-color: #9966CC;
                -fx-padding: 10 20;
                -fx-text-fill: white;
                -fx-font-size: 15px;      
                -fx-background-radius: 10;
            """);
            }
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
    }

    private PasswordField createRoundedPasswordField() {
        PasswordField pf = new PasswordField();
        pf.setStyle("""
        -fx-background-color: #F4F4F4;
        -fx-background-radius: 10;
        -fx-border-radius: 10;
        -fx-padding: 10;
        -fx-border-color: transparent;
    """);
        return pf;
    }
    private void loadLoginPassword() {
        rightVBox.getChildren().clear();

        Label title = new Label("Login & Password");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setPadding(new Insets(0, 0, 30, 0));

        currentPasswordField = createRoundedPasswordField();
        newPasswordField = createRoundedPasswordField();
        confirmPasswordField = createRoundedPasswordField();

        // Labels hiển thị thông báo
        currentPasswordMsg = new Label();
        confirmPasswordMsg = new Label();

        currentPasswordMsg.setFont(Font.font("System", 9));
        confirmPasswordMsg.setFont(Font.font("System", 9));

        currentPasswordField.textProperty().addListener((obs, oldText, newText) -> {
            if (currentUser != null) {
                String salt = "hienanh";
                String hashedInput = PasswordUtils.hashPassword(newText + salt);

                if (hashedInput.equals(currentUser.getPassword())) {
                    currentPasswordMsg.setText("Current password is correct");
                    currentPasswordMsg.setTextFill(Color.GREEN);
                } else {
                    currentPasswordMsg.setText("Password is incorrect");
                    currentPasswordMsg.setTextFill(Color.RED);
                }
            }
        });

        // Check Confirm New Pass
        confirmPasswordField.textProperty().addListener((obs, oldText, newText) -> {
            String newPass = newPasswordField.getText();
            if (newPass.equals(newText)) {
                confirmPasswordMsg.setText("Passwords match");
                confirmPasswordMsg.setTextFill(Color.GREEN);
            } else {
                confirmPasswordMsg.setText("Passwords do not match");
                confirmPasswordMsg.setTextFill(Color.RED);
            }
        });

        VBox form = new VBox(10);
        form.getChildren().addAll(
                labeledField("Current Password", currentPasswordField),
                currentPasswordMsg,
                labeledField("New Password", newPasswordField),
                labeledField("Confirm Password", confirmPasswordField),
                confirmPasswordMsg
        );


        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle("-fx-background-color: #9966CC; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-size: 15px; -fx-font-size: 15px;  ");

        // Áp dụng hover effect
        addHoverEffect(saveBtn, false);

        // Bọc nút trong HBox để căn giữa
        HBox btnBox = new HBox(saveBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(30, 0, 0, 0)); // khoảng cách top

        // Set On Action for SAVE BTN

        saveBtn.setOnAction(e -> {
            String currentPass = currentPasswordField.getText();
            String newPass = newPasswordField.getText();
            String confirmPass = confirmPasswordField.getText();

            if (currentUser == null) return;

            String salt = "hienanh"; // dùng 1 lần duy nhất

            // Check current password
            String hashedCurrent = PasswordUtils.hashPassword(currentPass + salt);
            if (!hashedCurrent.equals(currentUser.getPassword())) {
                currentPasswordMsg.setText("Password is incorrect");
                currentPasswordMsg.setTextFill(Color.RED);
                return;
            }

            // Check confirm password
            if (!newPass.equals(confirmPass)) {
                confirmPasswordMsg.setText("Passwords do not match");
                confirmPasswordMsg.setTextFill(Color.RED);
                return;
            }

            // Update password
            String hashedPassword = PasswordUtils.hashPassword(newPass + salt);
            currentUser.setPassword(hashedPassword);
            personalController.updateUserPassword(currentUser);

            currentPasswordMsg.setText("Password updated successfully");
            currentPasswordMsg.setTextFill(Color.GREEN);

            // Hiển thị Alert thành công
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null); // bỏ tiêu đề phụ
            alert.setContentText("Password updated successfully!");
            alert.showAndWait();

            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
            currentPasswordMsg.setText("");
            confirmPasswordMsg.setText("");
        });


        rightVBox.getChildren().addAll(title, form, btnBox);
    }


    private void loadLogout() {
        rightVBox.getChildren().clear();

        Label title = new Label("Are you sure you want to log out?");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Button logoutBtn = new Button("Log Out");
        logoutBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 10 20;");

        rightVBox.getChildren().addAll(title, logoutBtn);
    }

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public void setCurrentUser(UserDTO userDTO) {
        this.currentUser = userDTO;
        Platform.runLater(() -> {
            System.out.println("CurrentUser in initializeUI: " + currentUser);
            initializeUI();
        });
    }

//    public static void main(String[] args) {
//        javafx.application.Application.launch(TestProfilePage.class, args);
//    }

//    private String uploadAvatarToServer(File file, String email) throws Exception {
//        String boundary = Long.toHexString(System.currentTimeMillis());
//        URL url = new URL("http://localhost:8080/api/uploadAvatar"); // endpoint server
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setDoOutput(true);
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
//
//        try (OutputStream output = connection.getOutputStream();
//             PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)) {
//
//            // Thêm field email
//            writer.append("--").append(boundary).append("\r\n");
//            writer.append("Content-Disposition: form-data; name=\"email\"\r\n\r\n");
//            writer.append(email).append("\r\n");
//
//            // Thêm file
//            writer.append("--").append(boundary).append("\r\n");
//            writer.append("Content-Disposition: form-data; name=\"avatar\"; filename=\"")
//                    .append(file.getName()).append("\"\r\n");
//            writer.append("Content-Type: ").append(Files.probeContentType(file.toPath())).append("\r\n\r\n");
//            writer.flush();
//
//            Files.copy(file.toPath(), output);
//            output.flush();
//            writer.append("\r\n");
//            writer.append("--").append(boundary).append("--\r\n");
//            writer.flush();
//        }
//
//        // Nhận response từ server: trả về URL
//        int responseCode = connection.getResponseCode();
//        if (responseCode == HttpURLConnection.HTTP_OK) {
//            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
//                String line;
//                StringBuilder response = new StringBuilder();
//                while ((line = in.readLine()) != null) {
//                    response.append(line);
//                }
//                // Giả sử server trả về URL của avatar
//                return response.toString();
//            }
//        } else {
//            throw new IOException("Server returned code: " + responseCode);
//        }
//    }

//    @WebServlet("/api/uploadAvatar")
//    @MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024)
//    public class AvatarUploadServlet extends HttpServlet {
//        protected void doPost(HttpServletRequest request, HttpServletResponse response)
//                throws ServletException, IOException {
//            String email = request.getParameter("email");
//            Part filePart = request.getPart("avatar"); // tên trường "avatar"
//            String fileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();
//            String uploadPath = getServletContext().getRealPath("/avatars");
//            File uploads = new File(uploadPath);
//            if (!uploads.exists()) uploads.mkdirs();
//
//            File file = new File(uploads, fileName);
//            try (InputStream input = filePart.getInputStream()) {
//                Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
//            }
//
//            // Trả về URL để client load
//            String avatarUrl = request.getContextPath() + "/avatars/" + fileName;
//            response.setContentType("text/plain");
//            response.getWriter().write(avatarUrl);
//
//            // Bạn có thể luôn update DB ở đây nếu muốn
//        }
//    }



}


