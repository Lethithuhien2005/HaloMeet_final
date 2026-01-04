package main.Client.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class ProfilePage extends StackPane{
    private StackPane contentPane;

    private Stage chatStage;
    private String currentName;
    private Image currentAvatar;

    String bio = "Love coding, traveling, and coffee";
    String skill = "Java, JavaFX, SQL";
    String gender = "Male";
    String lastName = "Nguyen";
    String firstName = "Quynh Anh";
    String email = "quynhanh@example.com";
    String website = "https://quynhanh.dev";
    String location = "Ho Chi Minh, Vietnam";

//    public ProfilePage(Stage chatStage) {
//        this.chatStage = chatStage;
//    }

//    public ProfilePage() {
//        // constructor mặc định nếu cần
//    }
//    private Stage stage;
//    public ProfilePage(Stage stage, StackPane contentPane) {
//        this.stage = stage;
//        this.contentPane = contentPane;
//    }

//    public ProfilePage(Stage chatStage, String currentName, Image currentAvatar) {
//        this.chatStage = chatStage;
//        this.currentName = currentName;
//        this.currentAvatar = currentAvatar;
//    }
    public ProfilePage(StackPane contentPane, String currentName, Image currentAvatar) {
        this.contentPane = contentPane;
        this.currentName = currentName;
        this.currentAvatar = currentAvatar;

        initializeUI();
    }

//    public void start(Stage stage) {

//    public ProfilePage(StackPane contentPane) {
//        this.contentPane = contentPane;

    private void initializeUI() {
        // ====== ROOT ======
        VBox root = new VBox();
        root.setPadding(new Insets(20));
        root.setSpacing(20);
        root.setStyle("-fx-background-color: #F8F7FF;");


        StackPane headerStack = new StackPane();
        headerStack.setPrefHeight(270);
        headerStack.setMaxWidth(Double.MAX_VALUE); // cho VBox mở rộng theo padding

        headerStack.setClip(null); // bỏ clip mặc định

        // 1️⃣ Background
        ImageView avatarBack = new ImageView(new Image("./images/profiles/background.png"));
        avatarBack.setPreserveRatio(false);
        avatarBack.setFitHeight(250);
        avatarBack.setFitWidth(1135);
        StackPane.setAlignment(avatarBack, Pos.TOP_CENTER);
        StackPane.setMargin(avatarBack, new Insets(0, 20, 0, 20)); // 20px 2 bên

        ImageView backIcon = new ImageView(new Image("images/profiles/back_non_backgr.png"));
        backIcon.setFitWidth(23);
        backIcon.setFitHeight(23);
        backIcon.setPreserveRatio(true);

        // BUTTON back
        Button backBtn = new Button();
        backBtn.setGraphic(backIcon);   // dùng icon thay cho text
        backBtn.setStyle("""
             -fx-background-color: transparent;   
             -fx-padding: 0;                      
             -fx-cursor: hand;
        """);

//        backBtn.setOnAction(e -> {
//            stage.close();
//            chatStage.show();
//        });
        backBtn.setOnAction(e -> {
            // Khi bấm back, load lại ChatPage vào contentPane
            if (contentPane != null) {
                contentPane.getChildren().clear();
                contentPane.getChildren().add(new ChatPage(contentPane));
            }
        });

        StackPane.setAlignment(backBtn, Pos.TOP_LEFT);
        StackPane.setMargin(backBtn, new Insets(5,0,0,30));


        // 3️⃣ User Info (cạnh avatar, trên header)
        VBox userInfo = new VBox();
        userInfo.setSpacing(5);
        Label nameLabel = new Label(currentName != null ? currentName : "Unknown User");
        nameLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        Label usernameLabel = new Label("@quynhanh");
        usernameLabel.setTextFill(Color.GRAY);
        userInfo.getChildren().addAll(nameLabel, usernameLabel);
        // Chưa căn vị trí chính xác, sẽ căn cùng avatar

        headerStack.getChildren().addAll(avatarBack, backBtn);

        // 4️⃣ Avatar “nổi” ra ngoài header
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

        // StackPane chứa
        StackPane avatarPane = new StackPane(avatar);
        avatarPane.setPrefSize(size, size);


        StackPane.setAlignment(avatar, Pos.TOP_LEFT);
        avatar.setTranslateY(200); // dịch xuống vừa đủ để "nổi" ra ngoài background
        StackPane.setMargin(avatar, new Insets(0,0,0,110));


        StackPane.setAlignment(userInfo, Pos.TOP_LEFT);
        userInfo.setTranslateY(260); // căn ngay cạnh avatar
        StackPane.setMargin(userInfo, new Insets(0,0,0,295)); // avatar width + padding

        // ===== Message Button =====
        Button messageBtn = new Button("Message");
        messageBtn.setStyle("""
            -fx-background-color: #ffffff;
            -fx-text-fill: black;
            -fx-background-radius: 5;
            -fx-border-color: #BBBBBB;   
            -fx-border-radius: 5;
            -fx-border-width: 0.5;
            -fx-font-weight: bold;
            -fx-padding: 5 20 5 20;
            -fx-cursor: hand;
        """);


        StackPane.setAlignment(messageBtn, Pos.TOP_RIGHT);
        messageBtn.setTranslateY(290); // căn dưới, nhưng không quá

        StackPane.setMargin(messageBtn, new Insets(0, 30, 20, 0)); // top,right,bottom,left

        headerStack.getChildren().addAll(avatar, userInfo, messageBtn);
        headerStack.setMaxWidth(800);
        VBox.setMargin(headerStack, new Insets(0, 10, 90, 10));

        root.setPadding(new Insets(0, 0, 0, 0));


        // ============ LINE ==================
        HBox lineBox = new HBox();
        lineBox.setAlignment(Pos.CENTER);
        lineBox.prefWidthProperty().bind(root.widthProperty()); // hoặc bind vào scene/scroll viewport

        Rectangle line = new Rectangle();
        line.setHeight(1);
        line.setFill(Color.rgb(0,0,0,0.12));
        line.widthProperty().bind(lineBox.widthProperty().subtract(80)); // ngắn 40px mỗi bên

        lineBox.getChildren().add(line);
        VBox.setMargin(lineBox, new Insets(0, 0, 0, 0));


        // ================================ CONTENT ================================
        VBox content = new VBox();
        content.setSpacing(15);
        content.setFillWidth(true);

        VBox aboutTextBox = createReadMoreText(
                "Passionate software developer with a strong interest in scalable systems, cloud computing, and modern UI/UX design. "
                        + "I enjoy tackling complex technical challenges and transforming innovative ideas into clean, maintainable, and efficient code. "
                        + "With a mindset for continuous improvement, I actively explore new frameworks, architectures, and emerging technologies "
                        + "to stay ahead in the rapidly evolving tech industry. I thrive in collaborative environments, sharing knowledge, mentoring peers, "
                        + "and contributing to products that create real-world impact. "
                        + "Beyond development, I am fascinated by entrepreneurship, product strategy, and leveraging technology to solve societal problems. "
                        + "Always curious, always learning, and always striving to deliver solutions that balance performance, usability, and innovation. "
                        + "In my free time, I experiment with AI, cloud automation, and open-source projects to sharpen my skills and remain adaptable. "
                        + "I also enjoy analyzing trends in tech and business, understanding how emerging technologies like blockchain, edge computing, "
                        + "and IoT can create opportunities for innovative products. "
                        + "Over the years, I have participated in hackathons, startup initiatives, and collaborative projects that taught me the value of "
                        + "resilience, creativity, and strategic thinking. I am motivated by challenges that push me to grow both technically and professionally. "
                        + "My goal is to create software and products that not only solve problems but also inspire and empower users around the world. "
                        + "Whether it is designing elegant front-end experiences, architecting robust back-end systems, or integrating cutting-edge tools, "
                        + "I aim to make technology accessible, reliable, and impactful. "
                        + "Passionate about lifelong learning, I constantly read, experiment, and collaborate to stay at the forefront of technological advancements. "
                        + "Ultimately, I aspire to combine technical expertise, entrepreneurial thinking, and creative problem-solving to deliver products "
                        + "that leave a lasting mark in the digital world.",
                350 // giới hạn ký tự trước khi rút gọn
        );

        HBox infoBox = new HBox(40); // khoảng cách giữa các cột
        infoBox.setAlignment(Pos.TOP_LEFT);
        infoBox.setStyle(
                "-fx-background-color: #f3e8ff;" +     // màu tím nhạt
                        "-fx-background-radius: 10;" +         // bo góc
                        "-fx-padding: 15;"                      // padding bên trong
        );
        infoBox.setSpacing(0);                // không cần spacing
        infoBox.setFillHeight(true);

        String titleColor  = "-fx-text-fill: #9B59B6; ";
        String valueColor  = "-fx-text-fill: #9B59B6; -fx-font-weight: bold;";

        // ===== Location =====
        VBox locBox = new VBox(3);
        Label locTitle = new Label("Location");
        locTitle.setStyle(titleColor);
        Label locValue = new Label("Da Nang, Viet Nam");
        locValue.setStyle(valueColor);
        locBox.getChildren().addAll(locTitle, locValue);
        // ===== Website =====
        VBox webBox = new VBox(3);
        Label webTitle = new Label("Website");
        webTitle.setStyle(titleColor);
        Label webValue = new Label("fb.com");
        webValue.setStyle(valueColor);
        webBox.getChildren().addAll(webTitle, webValue);
        // ===== Email =====
        VBox emailBox = new VBox(3);
        Label emailTitle = new Label("Email");
        emailTitle.setStyle(titleColor);
        Label emailValue = new Label("abc@gmail.com");
        emailValue.setStyle(valueColor);
        emailBox.getChildren().addAll(emailTitle, emailValue);

        // Cho 3 VBox giãn FULL chiều ngang
        HBox.setHgrow(locBox, Priority.ALWAYS);
        HBox.setHgrow(webBox, Priority.ALWAYS);
        HBox.setHgrow(emailBox, Priority.ALWAYS);

        locBox.setMaxWidth(Double.MAX_VALUE);
        webBox.setMaxWidth(Double.MAX_VALUE);
        emailBox.setMaxWidth(Double.MAX_VALUE);

        // Căn trái mỗi box bên trong
        locBox.setAlignment(Pos.TOP_LEFT);
        webBox.setAlignment(Pos.TOP_LEFT);
        emailBox.setAlignment(Pos.TOP_LEFT);

        infoBox.getChildren().addAll(locBox, webBox, emailBox);
        VBox rightColumn = new VBox(20);
        rightColumn.getChildren().addAll(aboutTextBox, infoBox);

        HBox sec1 = createTwoColumn("About me", rightColumn, content);


        GridPane expTags = new GridPane();
        expTags.setHgap(15);  // khoảng cách ngang giữa tag
        expTags.setVgap(15);  // khoảng cách dọc
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50); // 50% chiều rộng
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        expTags.getColumnConstraints().addAll(col1, col2);
        // ===== Add tags =====
        expTags.add(createTag("Java Developer", "ABC Corp (2022 - now)", "./images/profiles/dev.png"), 0, 0);
        expTags.add(createTag("Internship", "XYZ Studio (2021)", "./images/profiles/intern.png"), 1, 0);
        expTags.add(createTag("Backend Development", "API, Services", "images/profiles/backend.png"), 0, 1);
        expTags.add(createTag("System Integration", "Microservices", "./images/profiles/system.png"), 1, 1);

        HBox sec2 = createTwoColumn("Experience", expTags, content);





        VBox.setMargin(content, new Insets(5, 35, 40, 35));
//        content.getChildren().addAll(sec1, sec2, profileSection, aboutSection, friendsSection, postsSection);
        content.getChildren().addAll(sec1, sec2);


        root.getChildren().addAll(headerStack, lineBox, content);
        root.setStyle("-fx-background-color: white;");
        root.setAlignment(null);



        ScrollPane scrollPane = new ScrollPane(root);
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
//
//        stage.setScene(scene);
//        stage.setTitle("Profile Page");
//        stage.setMaximized(true);
//
//
//        stage.show();

        // Thêm vào StackPane hiện tại
        if (contentPane != null) {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(scrollPane);
        } else {
            this.getChildren().add(scrollPane);
        }
//        this.getChildren().add(scrollPane);
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


//    public static void main(String[] args) {
//        javafx.application.Application.launch(TestProfilePage.class, args);
//    }

}


