package main.Client.View;

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
import javafx.stage.Stage;
import main.Server.Controller.UserController;
import main.Server.Model.User;
import main.util.PasswordUtils;
import main.util.Session;

import java.util.Objects;

public class PersonalProfile extends StackPane{
    private StackPane contentPane;

    private Stage chatStage;
    private String currentName = "Nguyen Anh";
    private Image currentAvatar = new Image("./images/chatPage/profile.png");;
    private String currentEmail;

    String email = Session.getInstance().getEmail();  // <<< LẤY EMAIL ĐÃ LƯU
    UserController userController = new UserController();
    // Lấy User từ DB
    User user = userController.getUserProfile(email);  // giả sử gọi DAO bên trong Controller




    String bio = "Love coding, traveling, and coffee";
    String skill = "Java, JavaFX, SQL";
    String gender = "Male";
    String lastName = "Nguyen";
    String firstName = "Quynh Anh";
    String website = "https://quynhanh.dev";
    String location = "Ho Chi Minh, Vietnam";


    public PersonalProfile(StackPane contentPane, String currentEmail) {
        this.contentPane = contentPane;
        this.currentEmail = currentEmail;

        initializeUI();
    }

    private VBox rightVBox;
    private Button activeMenuButton = null;

    private void initializeUI() {
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


        // LEFT VBOX (Avatar + Fullname + Username + Menu Items)
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
        if (user == null) {
            System.out.println("User not found!");
            return;
        }
        if (user != null) {
            // Fullname
            fullnameLabel = new Label(user.getFullName()); // hoặc fullName nếu có
            fullnameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

            // Username
            usernameLabel = new Label("_" + user.getUsername() + "_");
            usernameLabel.setFont(Font.font("Arial", 14));
            usernameLabel.setTextFill(Color.GRAY);
            usernameLabel.setPadding(new Insets(0, 0, 20, 0));

        } else {
            fullnameLabel = new Label("Unknown");
            usernameLabel = new Label("@unknown");
        }

        // Tạo label hoặc cập nhật label
//        Label emailLabel = new Label();
//        emailLabel.setFont(Font.font("Arial", 14));
//        emailLabel.setTextFill(Color.GRAY);
//        emailLabel.setText(email);  // <- đặt email vào label
//        emailLabel.setPadding(new Insets(0, 0, 20, 0));


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

//        scene.setFill(Color.web("#F8F7FF"));

//        stage.setScene(scene);
//        stage.setTitle("Personal Profile");
//        stage.setMaximized(true);


//        stage.show();

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
    private HBox createTag(String title, String subtitle, String imagePath) {
        // ===== ICON =====
        ImageView icon = new ImageView(new Image(imagePath));
        icon.setFitWidth(48);
        icon.setFitHeight(48);
        icon.setPreserveRatio(true);

        // ===== TITLE =====
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #000000; -fx-font-size: 17;");

        // ===== SUBTITLE =====
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-text-fill: #555; -fx-font-size: 11;");

        // ===== TEXT BOX =====
        VBox textBox = new VBox(3);
        textBox.getChildren().addAll(titleLabel, subtitleLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);

        // ===== CONTENT (ICON + TEXT) =====
        HBox contentBox = new HBox(25);  // khoảng cách giữa icon và text
        contentBox.getChildren().addAll(icon, textBox);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        // ===== TAG CONTAINER =====
        HBox box = new HBox(contentBox);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(8, 12, 8, 25));

        box.setStyle("""
            -fx-background-color: transparent;
            -fx-border-color: #BBBBBB;
            -fx-border-width: 0.8;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
        """);

        box.setPrefHeight(90);
        box.setMinHeight(90);
        box.setMaxWidth(Double.MAX_VALUE);

        return box;
    }

    private VBox createSection(String title, String contentText) {
        VBox section = new VBox();
        section.setSpacing(10);
        section.setPadding(new Insets(15));
        section.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 15;
            -fx-border-radius: 15;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.5, 0, 2);
        """);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));

        Label contentLabel = new Label(contentText);
        contentLabel.setWrapText(true);

        section.getChildren().addAll(titleLabel, contentLabel);

        return section;
    }
    private VBox createProfileInfoSection() {
        VBox section = new VBox();
        section.setSpacing(10);
        section.setPadding(new Insets(15));
        section.setStyle("""
        -fx-background-color: white;
        -fx-background-radius: 15;
        -fx-border-radius: 15;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.5, 0, 2);
    """);

        Label titleLabel = new Label("Profile Information");
        titleLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));

        // Info labels
        Label bioLabel = new Label("Bio: " + bio);
        Label skillLabel = new Label("Skill: " + skill);
        Label genderLabel = new Label("Gender: " + gender);
        Label lastNameLabel = new Label("Last Name: " + lastName);
        Label firstNameLabel = new Label("First Name: " + firstName);
        Label emailLabel = new Label("Email: " + email);
        Label websiteLabel = new Label("Website: " + website);
        Label locationLabel = new Label("Location: " + location);

        // Nếu muốn mỗi field xuống dòng riêng
        VBox infoBox = new VBox(5);
        infoBox.getChildren().addAll(bioLabel, skillLabel, genderLabel, lastNameLabel,
                firstNameLabel, emailLabel, websiteLabel, locationLabel);

        section.getChildren().addAll(titleLabel, infoBox);
        return section;
    }


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


        // Lấy email của user đang đăng nhập
        String email = Session.getInstance().getEmail();
        UserController userController = new UserController();
        User user = userController.getUserProfile(email);  // get từ DAO

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
        if (user != null && user.getGender() != null) {  // giả sử bạn lưu gender trong role hoặc thêm gender
            String gender = user.getGender(); // nếu bạn thêm trường gender thì lấy user.getGender()
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

        String fullName = user.getFullName() != null ? user.getFullName() : "";
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
        emailField.setText(user != null && user.getEmail() != null ? user.getEmail() : "");
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
        if (user != null && user.getAddress() != null && !user.getAddress().isEmpty()) {
            addressField.setText(user.getAddress());
        } else {
            addressField.setPromptText("Address");
        }
        VBox addressBox = labeledField("Address", addressField);

        // ===========================
        // PHONE + DATE OF BIRTH
        // ===========================
        TextField phoneField = createRoundedTextField();
        phoneField.setText(user != null && user.getPhone() != null ? user.getPhone() : "");
        phoneField.setPromptText("Phone Number");

        DatePicker dobPicker = new DatePicker();
        dobPicker.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        dobPicker.setValue(user.getDob() != null ? user.getDob() : null);

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
        if (user != null && user.getAddress() != null && !user.getAddress().isEmpty()) {
            locationField.setText(user.getAddress());
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
            if (user != null) {
                // Gender
                String gender = user.getGender();
                if (gender != null) {
                    maleRadio.setSelected(gender.equalsIgnoreCase("Male"));
                    femaleRadio.setSelected(gender.equalsIgnoreCase("Female"));
                } else {
                    genderGroup.selectToggle(null);  // bỏ chọn nếu null
                }
                // Full Name -> First + Last
                String fullNamee = user.getFullName() != null ? user.getFullName() : "";
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
                emailField.setText(user.getEmail() != null ? user.getEmail() : "");
                // Phone
                phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
                // Address
                addressField.setText(user.getAddress() != null ? user.getAddress() : "");
                // Location (nếu có field riêng)
                locationField.setText(user.getAddress() != null ? user.getAddress() : "");
                // Date of Birth
                dobPicker.setValue(user.getDob());
            }
        });
        saveBtn.setOnAction(e -> {
            if (user != null) {
                // Update dữ liệu từ form
                String updatedFullName = firstNameField.getText().trim() + " " + lastNameField.getText().trim();
                user.setFullName(updatedFullName);

                String updatedGender = maleRadio.isSelected() ? "Male" : femaleRadio.isSelected() ? "Female" : null;
                user.setGender(updatedGender);

                user.setPhone(phoneField.getText().trim());
                user.setAddress(addressField.getText().trim());
                user.setDob(dobPicker.getValue());

                // Nếu có location riêng, dùng locationField.getText()

                // Update vào database
                UserController userControllerUpdate = new UserController();
                boolean success = userControllerUpdate.updateUserProfile(user);  // bạn cần viết hàm này trong UserController -> DAO

                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("User profile updated successfully!");
                    alert.show();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Update failed. Please try again.");
                    alert.show();
                }
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

        PasswordField currentPasswordField = createRoundedPasswordField();
        PasswordField newPasswordField = createRoundedPasswordField();
        PasswordField confirmPasswordField = createRoundedPasswordField();

        // Labels hiển thị thông báo
        Label currentPasswordMsg = new Label();
        Label confirmPasswordMsg = new Label();

        currentPasswordMsg.setFont(Font.font("System", 9));
        confirmPasswordMsg.setFont(Font.font("System", 9));

        currentPasswordField.textProperty().addListener((obs, oldText, newText) -> {
            String email = Session.getInstance().getEmail();
            UserController userController = new UserController();
            User user = userController.getUserProfile(email);

            if (user != null) {
                String salt = "hienanh";
                String hashedInput = PasswordUtils.hashPassword(newText + salt);

                if (hashedInput.equals(user.getPassword())) {
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

//        VBox form = new VBox(24);
//        form.getChildren().addAll(
//                labeledField("Current Password", createRoundedPasswordField()),
//                labeledField("New Password", createRoundedPasswordField()),
//                labeledField("Confirm Password", createRoundedPasswordField())
//        );
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
//        saveBtn.setOnAction(e -> {
//            String currentPass = currentPasswordField.getText();
//            String newPass = newPasswordField.getText();
//            String confirmPass = confirmPasswordField.getText();
//
//            String email = Session.getInstance().getEmail();
//            UserController userController = new UserController();
//            User user = userController.getUserProfile(email);
//
//            if (user == null) return;
//
//            // Check current password
//            String salt = "hienanh";
//            String hashedCurrent = PasswordUtils.hashPassword(currentPass + salt);
//            if (!hashedCurrent.equals(user.getPassword())) {
//                currentPasswordMsg.setText("Password is incorrect");
//                currentPasswordMsg.setTextFill(Color.RED);
//                return;
//            }
//
//            // Check confirm password
//            if (!newPass.equals(confirmPass)) {
//                confirmPasswordMsg.setText("Passwords do not match");
//                confirmPasswordMsg.setTextFill(Color.RED);
//                return;
//            }
//
//
//            // Update password
//
//            String salt = "hienanh";
//            String hashedPassword = PasswordUtils.hashPassword(newPass + salt);
//            user.setPassword(hashedPassword);
//
//            UserController userController = new UserController();
//            userController.updateUserPassword(user);
//
//            currentPasswordMsg.setText("Password updated successfully");
//            currentPasswordMsg.setTextFill(Color.GREEN);
//        });
        saveBtn.setOnAction(e -> {
            String currentPass = currentPasswordField.getText();
            String newPass = newPasswordField.getText();
            String confirmPass = confirmPasswordField.getText();

            String email = Session.getInstance().getEmail();
            UserController userController = new UserController();
            User user = userController.getUserProfile(email);

            if (user == null) return;

            String salt = "hienanh"; // dùng 1 lần duy nhất

            // Check current password
            String hashedCurrent = PasswordUtils.hashPassword(currentPass + salt);
            if (!hashedCurrent.equals(user.getPassword())) {
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
            user.setPassword(hashedPassword);
            userController.updateUserPassword(user);

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

//    public static void main(String[] args) {
//        javafx.application.Application.launch(TestProfilePage.class, args);
//    }

}


