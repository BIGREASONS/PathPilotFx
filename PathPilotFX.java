import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import org.json.JSONObject;

public class PathPilotFX extends Application {

    private StringProperty email = new SimpleStringProperty("");
    private StringProperty firstName = new SimpleStringProperty("");
    private StringProperty lastName = new SimpleStringProperty("");
    private StringProperty careerGoal = new SimpleStringProperty("Software Developer");
    private StringProperty skillLevel = new SimpleStringProperty("Beginner");
    private StringProperty timeCommitment = new SimpleStringProperty("1-2 hours/day");
    private IntegerProperty score = new SimpleIntegerProperty(0);
    private IntegerProperty streak = new SimpleIntegerProperty(0);
    private StringProperty currentTier = new SimpleStringProperty("Bronze");

    private BorderPane root;
    private VBox content;
    private Label scoreLabel, streakLabel, tierLabel;

    private static final String DATA_FILE = "pathpilot_data.properties";
    private Properties userData = new Properties();

    private ObservableList<Challenge> challenges = FXCollections.observableArrayList();
    private ObservableList<Milestone> milestones = FXCollections.observableArrayList();

    private OllamaService ollamaService = new OllamaService();

    private void initializeData() {
        if (challenges.isEmpty()) {
            challenges.addAll(
                new Challenge("Two Sum", "Find two numbers that add up to a target.", "Easy", 10, "https://leetcode.com/problems/two-sum/"),
                new Challenge("Reverse String", "Reverse a given string in-place.", "Easy", 15, "https://leetcode.com/problems/reverse-string/"),
                new Challenge("Valid Parentheses", "Check if a string of parentheses is valid.", "Medium", 20, "https://leetcode.com/problems/valid-parentheses/"),
                new Challenge("Binary Search", "Implement binary search algorithm.", "Medium", 25, "https://leetcode.com/problems/binary-search/"),
                new Challenge("Median of Two Sorted Arrays", "Find the median of two sorted arrays.", "Hard", 50, "https://leetcode.com/problems/median-of-two-sorted-arrays/")
            );
        }
    }

    @Override
    public void start(Stage stage) {
        loadUserData();
        initializeData();

        root = new BorderPane();
        root.setStyle("-fx-background: linear-gradient(to bottom right, #1A1F3D, #403B4A);");

        HBox topBar = createTopNavigation();
        root.setTop(topBar);

        content = new VBox(20);
        content.setPadding(new Insets(30));
        
        // Make the main content scrollable
        ScrollPane mainScroll = new ScrollPane(content);
        mainScroll.setFitToWidth(true);
        mainScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        mainScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setCenter(mainScroll);

        showSetupTab();
        updateTier();

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("🎯 PathPilot FX - Career Companion");
        stage.setResizable(true);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setOnCloseRequest(e -> saveUserData());
        stage.show();
    }

    public static class OllamaService {
        private final String apiUrl = "http://localhost:11434/api/chat";
        private final String model = "llama2";
        private final int timeout = 120000; // 120 seconds timeout

        public String askAI(String prompt) {
            try {
                String payload = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}], \"stream\": false}";
                HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload.getBytes());
                }
                
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    return "Error: HTTP " + responseCode + " - " + conn.getResponseMessage();
                }

                try (InputStream is = conn.getInputStream()) {
                    return new String(is.readAllBytes());
                }
            } catch (java.net.SocketTimeoutException e) {
                return "Error: Request timed out after " + (timeout/1000) + " seconds";
            } catch (Exception e) {
                e.printStackTrace();
                return "Error communicating with Ollama: " + e.getMessage();
            }
        }
    }

    private void generateRoadmap() {
        milestones.clear();
        
        // Show loading indicator
        showNotification("Generating detailed roadmap with AI... Please wait", false);

        String prompt = "Create a VERY DETAILED 12-week learning roadmap for a user aiming to become a "
                + careerGoal.get() + ". The user is at " + skillLevel.get() 
                + " level and can commit " + timeCommitment.get() + ". "
                + "Provide comprehensive weekly milestones with: "
                + "1. Week number and title"
                + "2. Detailed learning objectives and topics"
                + "3. Specific skills to acquire each week"
                + "4. Recommended projects or exercises"
                + "5. Estimated time commitment breakdown"
                + "6. Learning resources and URLs"
                + "7. Success metrics for each week"
                + "Make it extremely detailed, practical, and structured week-by-week. "
                + "Include programming languages, frameworks, tools, and real-world applications.";

        // Run AI call in background thread to prevent UI freezing
        new Thread(() -> {
            try {
                String response = ollamaService.askAI(prompt);
                
                // Parse response on JavaFX thread
                Platform.runLater(() -> {
                    String aiContent = parseAIResponse(response);
                    
                    milestones.add(new Milestone(
                        "📅 12-Week " + careerGoal.get() + " Learning Roadmap",
                        aiContent,
                        100,
                        false,
                        Arrays.asList(
                            "https://leetcode.com/",
                            "https://www.geeksforgeeks.org/", 
                            "https://www.coursera.org/",
                            "https://www.udemy.com/",
                            "https://github.com/",
                            "https://stackoverflow.com/",
                            "https://developer.mozilla.org/",
                            "https://www.freecodecamp.org/",
                            "https://www.codecademy.com/",
                            "https://www.pluralsight.com/",
                            "https://www.kaggle.com/",
                            "https://www.datacamp.com/"
                        )
                    ));

                    showNotification("Detailed roadmap generated successfully!", false);
                    showRoadmapTab();
                    addScore(50, "Detailed roadmap generated!");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showNotification("AI service error: " + e.getMessage(), true);
                });
            }
        }).start();
    }

    private String parseAIResponse(String jsonResponse) {
        try {
            JSONObject json = new JSONObject(jsonResponse);
            if (json.has("message")) {
                return json.getJSONObject("message").getString("content").trim();
            }
            return jsonResponse.trim();
        } catch (Exception e) {
            System.out.println("Error parsing AI response: " + e.getMessage());
            return jsonResponse.length() > 500 ?
                   jsonResponse.substring(0, 500) + "..." :
                   jsonResponse;
        }
    }

    private HBox createTopNavigation() {
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(20));
        topBar.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#667eea")),
                        new Stop(1, Color.web("#764ba2"))),
                CornerRadii.EMPTY, Insets.EMPTY)));
        topBar.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.3)));

        Label logo = new Label("🎯 PathPilot FX");
        logo.setFont(Font.font("Poppins", FontWeight.BOLD, 24));
        logo.setTextFill(Color.WHITE);

        VBox userInfo = new VBox(2);
        Label userName = new Label();
        userName.textProperty().bind(firstName.concat(" ").concat(lastName));
        userName.setFont(Font.font("Poppins", FontWeight.SEMI_BOLD, 14));
        userName.setTextFill(Color.web("#f0f0f0"));

        Label userGoal = new Label();
        userGoal.textProperty().bind(careerGoal.concat(" • ").concat(skillLevel));
        userGoal.setFont(Font.font("Poppins", 12));
        userGoal.setTextFill(Color.web("#d6d6d6"));

        userInfo.getChildren().addAll(userName, userGoal);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        scoreLabel = createAnimatedLabel(score.asString("⭐ %d pts"), "#b1d4e0");
        streakLabel = createAnimatedLabel(streak.asString("🔥 %d day streak"), "#f78c6b");
        tierLabel = createAnimatedLabel(currentTier.concat(" Tier"), "#8effa3");

        HBox stats = new HBox(25, scoreLabel, streakLabel, tierLabel);
        stats.setAlignment(Pos.CENTER_RIGHT);

        Button setupBtn = styledNavButton("📋 Setup", this::showSetupTab);
        Button roadmapBtn = styledNavButton("🛣️ Roadmap", this::showRoadmapTab);
        Button challengesBtn = styledNavButton("⚡ Challenges", this::showChallengesTab);
        Button progressBtn = styledNavButton("📈 Progress", this::showProgressTab);
        Button resourcesBtn = styledNavButton("📚 Resources", this::showResourcesTab);

        HBox navButtons = new HBox(15, setupBtn, roadmapBtn, challengesBtn, progressBtn, resourcesBtn);

        topBar.getChildren().addAll(logo, userInfo, spacer, stats, navButtons);
        return topBar;
    }

    private Label createAnimatedLabel(StringExpression textBinding, String colorHex) {
        Label label = new Label();
        label.textProperty().bind(textBinding);
        label.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        label.setTextFill(Color.web(colorHex));

        Glow glow = new Glow(0.4);
        label.setEffect(glow);

        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.4)),
                new KeyFrame(Duration.seconds(1.2), new KeyValue(glow.levelProperty(), 0.8))
        );
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        return label;
    }

    private Button styledNavButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Poppins", FontWeight.SEMI_BOLD, 14));
        btn.setTextFill(Color.WHITE);
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 8 18;");
        btn.setOnAction(e -> action.run());

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(160), btn);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(160), btn);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-padding: 8 18;");
            scaleUp.playFromStart();
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: transparent; -fx-padding: 8 18;");
            scaleDown.playFromStart();
        });

        return btn;
    }

    private void animateContentTransition(Pane newContent) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), content);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> {
            content.getChildren().setAll(newContent);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), content);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    private void showRoadmapTab() {
        VBox newContent = new VBox(20);
        newContent.setPadding(new Insets(20));
        newContent.setStyle("-fx-background-color: transparent;");

        Label heading = new Label("🚀 Your Learning Roadmap");
        heading.setFont(Font.font("Poppins", FontWeight.BOLD, 26));
        heading.setTextFill(Color.web("#d1d3e0"));
        newContent.getChildren().add(heading);

        if (milestones.isEmpty()) {
            Label noContent = new Label("No roadmap generated yet. Go to Setup tab to generate your personalized roadmap!");
            noContent.setFont(Font.font("Poppins", 16));
            noContent.setTextFill(Color.web("#9aa0ba"));
            newContent.getChildren().add(noContent);
        } else {
            VBox milestonesContainer = new VBox(20);
            
            for (Milestone milestone : milestones) {
                VBox card = createMilestoneCard(milestone);
                milestonesContainer.getChildren().add(card);
            }

            // Create scroll pane for the roadmap
            ScrollPane scrollPane = new ScrollPane(milestonesContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            scrollPane.setPadding(new Insets(10));
            
            // Set preferred height to allow scrolling
            scrollPane.setPrefHeight(600);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            
            newContent.getChildren().add(scrollPane);
        }

        animateContentTransition(newContent);
    }

    private VBox createMilestoneCard(Milestone milestone) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle(
                "-fx-background-color: #2b2f50;" +
                "-fx-background-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 6);"
        );

        Label title = new Label(milestone.getTitle());
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#d1d3e0"));

        // Create a scrollable text area for the description
        TextArea descriptionArea = new TextArea(milestone.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setStyle(
            "-fx-background-color: #3a3f63;" +
            "-fx-text-fill: #9aa0ba;" +
            "-fx-font-family: 'Poppins';" +
            "-fx-font-size: 14px;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 15;" +
            "-fx-border-color: #4a4f74;" +
            "-fx-border-width: 1;"
        );
        descriptionArea.setPrefRowCount(15);
        descriptionArea.setPrefHeight(400);

        Label points = new Label("🎯 Points: " + milestone.getPoints());
        points.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        points.setTextFill(Color.web("#3baf7f"));

        // Create resource links
        VBox resourcesBox = new VBox(8);
        resourcesBox.setPadding(new Insets(15, 0, 0, 0));
        Label resourcesLabel = new Label("📚 Learning Resources:");
        resourcesLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        resourcesLabel.setTextFill(Color.web("#d1d3e0"));
        resourcesBox.getChildren().add(resourcesLabel);
        
        for (String url : milestone.getResources()) {
            Hyperlink link = new Hyperlink("🔗 " + url);
            link.setFont(Font.font("Poppins", 14));
            link.setTextFill(Color.web("#667eea"));
            link.setOnAction(e -> getHostServices().showDocument(url));
            resourcesBox.getChildren().add(link);
        }

        card.getChildren().addAll(title, descriptionArea, points, resourcesBox);
        
        FadeTransition fade = new FadeTransition(Duration.seconds(1), card);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        return card;
    }

    private void showSetupTab() {
        content.getChildren().clear();
        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: rgba(255,255,255,0.95);" +
                "-fx-background-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);");

        Label title = new Label("🚀 Create Your Profile");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2d3748"));

        GridPane personalGrid = new GridPane();
        personalGrid.setHgap(15);
        personalGrid.setVgap(15);

        // Improved UI for input fields
        TextField firstNameField = createStyledTextField("Enter your first name");
        firstNameField.setText(firstName.get());
        firstName.bindBidirectional(firstNameField.textProperty());

        TextField lastNameField = createStyledTextField("Enter your last name");
        lastNameField.setText(lastName.get());
        lastName.bindBidirectional(lastNameField.textProperty());

        TextField emailField = createStyledTextField("your.email@example.com");
        emailField.setText(email.get());
        email.bindBidirectional(emailField.textProperty());

        personalGrid.add(createFormLabel("First Name:"), 0, 0);
        personalGrid.add(firstNameField, 1, 0);
        personalGrid.add(createFormLabel("Last Name:"), 0, 1);
        personalGrid.add(lastNameField, 1, 1);
        personalGrid.add(createFormLabel("Email (optional):"), 0, 2);
        personalGrid.add(emailField, 1, 2);

        ComboBox<String> goalBox = createStyledComboBox();
        goalBox.getItems().addAll(
                "Software Developer", "Data Scientist", "ML Engineer",
                "Cloud Engineer", "DevOps Engineer", "Product Manager",
                "UI/UX Designer", "Cybersecurity Analyst", "Full Stack Developer",
                "Frontend Developer", "Backend Developer", "Mobile Developer"
        );
        goalBox.setValue(careerGoal.get());
        careerGoal.bind(goalBox.valueProperty());

        ComboBox<String> levelBox = createStyledComboBox();
        levelBox.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");
        levelBox.setValue(skillLevel.get());
        skillLevel.bind(levelBox.valueProperty());

        ComboBox<String> timeBox = createStyledComboBox();
        timeBox.getItems().addAll(
                "1-2 hours/day", "2-4 hours/day", "4-6 hours/day", "6-8 hours/day", "8+ hours/day"
        );
        timeBox.setValue(timeCommitment.get());
        timeCommitment.bind(timeBox.valueProperty());

        VBox careerSection = new VBox(15);
        careerSection.getChildren().addAll(
                createFormLabel("Career Goal:"), goalBox,
                createFormLabel("Current Skill Level:"), levelBox,
                createFormLabel("Daily Time Commitment:"), timeBox
        );

        Button generateBtn = new Button("🎯 Generate Detailed Roadmap (12 Weeks)");
        generateBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 12 20; -fx-background-radius: 10;");
        generateBtn.setOnAction(e -> {
            if (validateProfile()) {
                generateRoadmap();
            }
        });

        Button saveBtn = new Button("💾 Save Profile");
        saveBtn.setStyle("-fx-background-color: #48bb78; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 20; -fx-background-radius: 10;");
        saveBtn.setOnAction(e -> {
            saveUserData();
            showNotification("Profile saved successfully!", false);
        });

        HBox buttonBox = new HBox(10, generateBtn, saveBtn);
        buttonBox.setAlignment(Pos.CENTER);

        card.getChildren().addAll(title, personalGrid, new Separator(), careerSection, buttonBox);
        content.getChildren().add(card);
    }

    private TextField createStyledTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle(
            "-fx-background-color: #f7fafc;" +
            "-fx-border-color: #cbd5e0;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;" +
            "-fx-font-family: 'Poppins';" +
            "-fx-font-size: 14px;"
        );
        textField.setPrefWidth(300);
        return textField;
    }

    private ComboBox<String> createStyledComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setStyle(
            "-fx-background-color: #f7fafc;" +
            "-fx-border-color: #cbd5e0;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 8;" +
            "-fx-font-family: 'Poppins';" +
            "-fx-font-size: 14px;"
        );
        comboBox.setPrefWidth(300);
        return comboBox;
    }

    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Poppins", FontWeight.SEMI_BOLD, 14));
        label.setTextFill(Color.web("#2d3748"));
        return label;
    }

    private void showChallengesTab() {
        content.getChildren().clear();
        content.setStyle("-fx-background-color: transparent;");
        
        Label heading = new Label("⚡ Interactive Challenges");
        heading.setFont(Font.font("Poppins", FontWeight.BOLD, 26));
        heading.setTextFill(Color.web("#d1d3e0"));
        content.getChildren().add(heading);

        VBox challengesContainer = new VBox(15);
        
        for (Challenge challenge : challenges) {
            VBox box = new VBox(10);
            box.setPadding(new Insets(15));
            box.setStyle("-fx-background-color: #2b2f50; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
            
            Label title = new Label(challenge.getTitle());
            title.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
            title.setTextFill(Color.web("#d1d3e0"));
            
            Label desc = new Label(challenge.getDescription());
            desc.setFont(Font.font("Poppins", 14));
            desc.setTextFill(Color.web("#9aa0ba"));
            desc.setWrapText(true);
            
            HBox detailsBox = new HBox(20);
            detailsBox.setAlignment(Pos.CENTER_LEFT);
            
            Label difficulty = new Label("Difficulty: " + challenge.getDifficulty());
            difficulty.setFont(Font.font("Poppins", FontWeight.MEDIUM, 14));
            difficulty.setTextFill(Color.web("#" + (challenge.getDifficulty().equals("Easy") ? "48bb78" : challenge.getDifficulty().equals("Medium") ? "ed8936" : "e53e3e")));
            
            Label points = new Label("Points: " + challenge.getPoints());
            points.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
            points.setTextFill(Color.web("#667eea"));
            
            detailsBox.getChildren().addAll(difficulty, points);
            
            Hyperlink url = new Hyperlink("🔗 Solve Challenge");
            url.setFont(Font.font("Poppins", 12));
            url.setTextFill(Color.web("#667eea"));
            url.setOnAction(e -> getHostServices().showDocument(challenge.getUrl()));
            
            box.getChildren().addAll(title, desc, detailsBox, url);
            challengesContainer.getChildren().add(box);
        }
        
        ScrollPane scrollPane = new ScrollPane(challengesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        content.getChildren().add(scrollPane);
    }

    private void showProgressTab() {
        content.getChildren().clear();
        content.setStyle("-fx-background-color: transparent;");
        
        Label heading = new Label("📈 Your Progress");
        heading.setFont(Font.font("Poppins", FontWeight.BOLD, 26));
        heading.setTextFill(Color.web("#d1d3e0"));
        content.getChildren().add(heading);

        VBox progressBox = new VBox(20);
        progressBox.setPadding(new Insets(20));
        progressBox.setStyle("-fx-background-color: #2b2f50; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 6);");

        Label scoreLabel = new Label("⭐ Total Score: " + score.get());
        scoreLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        scoreLabel.setTextFill(Color.web("#b1d4e0"));

        Label streakLabel = new Label("🔥 Current Streak: " + streak.get() + " days");
        streakLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        streakLabel.setTextFill(Color.web("#f78c6b"));

        Label tierLabel = new Label("🏆 Current Tier: " + currentTier.get());
        tierLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        tierLabel.setTextFill(Color.web("#8effa3"));

        progressBox.getChildren().addAll(scoreLabel, streakLabel, tierLabel);
        content.getChildren().add(progressBox);
    }

    private void showResourcesTab() {
        content.getChildren().clear();
        content.setStyle("-fx-background-color: transparent;");
        
        Label heading = new Label("📚 Learning Resources");
        heading.setFont(Font.font("Poppins", FontWeight.BOLD, 26));
        heading.setTextFill(Color.web("#d1d3e0"));
        content.getChildren().add(heading);

        List<String> resources = Arrays.asList(
            "https://leetcode.com/", "https://www.geeksforgeeks.org/", "https://www.coursera.org/",
            "https://www.udemy.com/", "https://github.com/", "https://stackoverflow.com/",
            "https://developer.mozilla.org/", "https://www.freecodecamp.org/", "https://www.codecademy.com/",
            "https://www.pluralsight.com/", "https://www.kaggle.com/", "https://www.datacamp.com/",
            "https://www.udacity.com/", "https://www.edx.org/", "https://www.hackerrank.com/",
            "https://www.codewars.com/", "https://www.topcoder.com/", "https://www.hackerearth.com/"
        );

        VBox resourcesBox = new VBox(10);
        resourcesBox.setPadding(new Insets(20));
        resourcesBox.setStyle("-fx-background-color: #2b2f50; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 6);");

        for (String resource : resources) {
            Hyperlink link = new Hyperlink("🔗 " + resource);
            link.setFont(Font.font("Poppins", 14));
            link.setTextFill(Color.web("#667eea"));
            link.setOnAction(e -> getHostServices().showDocument(resource));
            resourcesBox.getChildren().add(link);
        }

        content.getChildren().add(resourcesBox);
    }

    private void loadUserData() {
        try (InputStream input = Files.newInputStream(Path.of(DATA_FILE))) {
            userData.load(input);
            firstName.set(userData.getProperty("firstName", ""));
            lastName.set(userData.getProperty("lastName", ""));
            email.set(userData.getProperty("email", ""));
            careerGoal.set(userData.getProperty("careerGoal", "Software Developer"));
            skillLevel.set(userData.getProperty("skillLevel", "Beginner"));
            timeCommitment.set(userData.getProperty("timeCommitment", "1-2 hours/day"));
            score.set(Integer.parseInt(userData.getProperty("score", "0")));
            streak.set(Integer.parseInt(userData.getProperty("streak", "0")));
            currentTier.set(userData.getProperty("currentTier", "Bronze"));
        } catch (Exception e) {
            // Ignore - first time running
        }
    }

    private void saveUserData() {
        try (OutputStream output = Files.newOutputStream(Path.of(DATA_FILE))) {
            userData.setProperty("firstName", firstName.get());
            userData.setProperty("lastName", lastName.get());
            userData.setProperty("email", email.get());
            userData.setProperty("careerGoal", careerGoal.get());
            userData.setProperty("skillLevel", skillLevel.get());
            userData.setProperty("timeCommitment", timeCommitment.get());
            userData.setProperty("score", Integer.toString(score.get()));
            userData.setProperty("streak", Integer.toString(streak.get()));
            userData.setProperty("currentTier", currentTier.get());
            userData.store(output, "PathPilotFX User Data");
        } catch (Exception e) {
            showNotification("Failed to save user data: " + e.getMessage(), true);
        }
    }

    private boolean validateProfile() {
        if (firstName.get().isBlank() || lastName.get().isBlank()) {
            showNotification("First and last name cannot be empty.", true);
            return false;
        }
        return true;
    }

    private void showNotification(String msg, boolean isError) {
        Alert.AlertType type = isError ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION;
        Alert alert = new Alert(type);
        alert.setTitle(isError ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void addScore(int points, String reason) {
        score.set(score.get() + points);
        showNotification(reason + " You earned " + points + " points.", false);
        updateTier();
    }

    private void updateTier() {
        int currentScore = score.get();
        if (currentScore >= 500) currentTier.set("Diamond");
        else if (currentScore >= 300) currentTier.set("Platinum");
        else if (currentScore >= 150) currentTier.set("Gold");
        else if (currentScore >= 50) currentTier.set("Silver");
        else currentTier.set("Bronze");
    }

    // Data classes
    public static class Challenge {
        private final String title;
        private final String description;
        private final String difficulty;
        private final int points;
        private final String url;

        public Challenge(String title, String description, String difficulty, int points, String url) {
            this.title = title;
            this.description = description;
            this.difficulty = difficulty;
            this.points = points;
            this.url = url;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getDifficulty() { return difficulty; }
        public int getPoints() { return points; }
        public String getUrl() { return url; }
    }

    public static class Milestone {
        private final String title;
        private final String description;
        private final int points;
        private boolean completed;
        private final List<String> resources;

        public Milestone(String title, String description, int points, boolean completed, List<String> resources) {
            this.title = title;
            this.description = description;
            this.points = points;
            this.completed = completed;
            this.resources = resources;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public int getPoints() { return points; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
        public List<String> getResources() { return resources; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}