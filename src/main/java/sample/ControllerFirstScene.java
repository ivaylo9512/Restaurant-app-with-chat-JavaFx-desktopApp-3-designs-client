package sample;

import com.google.gson.Gson;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class ControllerFirstScene implements Initializable {
    @FXML public Text loginText, registerText;
    @FXML public Button login, register;
    @FXML
    public TextField username, password;
    @FXML public Pane loginPane, menuBar, mainPane;
    @FXML public Text errorField;
    private ResizeWidthTransition resizeTextField, resizeLogin, resizeRegister;
    private TranslateTransition move, moveRegister;
    private FadeTransition fadeInLogin, fadeInRegister;
    private Boolean usernameHovered = false, registerHovered = false;
    private CloseableHttpClient httpClient = FirstScene.httpClient;
//    @FXML
//    public void login(){
//        Map<String, Object> jsonValues = new HashMap<>();
//        jsonValues.put("username", username.getText());
//        jsonValues.put("password", password.getText());
//        JSONObject json = new JSONObject(jsonValues);
//
//        StringEntity entity = new StringEntity(json.toString(), "UTF8");
//        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//
//        HttpPost httpPost = new HttpPost("http://localhost:8080/api/users/login");
//        httpPost.setEntity(entity);
//        try(CloseableHttpResponse response2 = httpClient.execute(httpPost)) {
//            int responseStatus = response2.getStatusLine().getStatusCode();
//            HttpEntity entity2 = response2.getEntity();
//            String content = EntityUtils.toString(entity2);
//            if(responseStatus != 200){
//                Gson gson = new Gson();
//                System.out.println(content + "-----------------");
//                String errorMessage = gson.fromJson(content,HttpResponse.class).getMessage();
//                updateFieldsOnError(errorMessage);
//                EntityUtils.consume(entity2);
//                throw new HttpException("Invalid response code: " + responseStatus + ". With an error message: " + errorMessage);
//            }
//            Preferences userPreference = Preferences.userRoot();
//            userPreference.put("user", content);
//
//            EntityUtils.consume(entity2);
//            changeStage();
//        } catch (IOException | HttpException e) {
//            e.printStackTrace();
//        }
//
//    }
    private void changeStage(){
        FadeTransition fade = new FadeTransition(Duration.millis(500), loginPane);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.play();
        ResizeHeightTransition resizeRegister = new ResizeHeightTransition(Duration.millis(750), menuBar, 80);
        resizeRegister.setDelay(Duration.millis(100));
        resizeRegister.play();
        Timeline displayNewStage = new Timeline(new KeyFrame(Duration.millis(800), ae -> {
            try {
                Stage window = (Stage)loginPane.getScene().getWindow();
                SecondScene.displayLoggedScene(window.getX(), window.getY());
                Timeline closeOldStage = new Timeline(new KeyFrame(Duration.millis(50), e -> window.close()));
                closeOldStage.play();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        displayNewStage.play();

    }
//    private void updateFieldsOnError(String errorMessage){
//        animateFieldsOnError();
//        username.getStyleClass().add("login-error");
//        password.getStyleClass().add("login-error");
//        errorField.setText(errorMessage);
//    }
//    private void animateFieldsOnError(){
//        if(!usernameHovered) {
//            TranslateTransition shakeUsername = new TranslateTransition(Duration.millis(50), username);
//            shakeUsername.setToX(6);
//            shakeUsername.setCycleCount(2);
//            shakeUsername.setAutoReverse(true);
//            shakeUsername.play();
//        }
//        if(!registerHovered) {
//            TranslateTransition shakeRegister = new TranslateTransition(Duration.millis(50), password);
//            shakeRegister.setToX(6);
//            shakeRegister.setCycleCount(2);
//            shakeRegister.setAutoReverse(true);
//            shakeRegister.play();
//        }
//    }
//    @FXML
//    public void expandButtons() {
//        resizeLogin = new ResizeWidthTransition(Duration.millis(450), login, 120);
//        resizeLogin.play();
//
//        resizeRegister = new ResizeWidthTransition(Duration.millis(450), register, 120);
//        resizeRegister.play();
//
//        moveRegister = new TranslateTransition(Duration.millis(449), register);
//        moveRegister.setToX(-79.5);
//        moveRegister.play();
//
//        fadeInLogin = new FadeTransition(Duration.millis(500), loginText);
//        fadeInLogin.setFromValue(0);
//        fadeInLogin.setToValue(1);
//        fadeInLogin.setDelay(Duration.millis(200));
//        fadeInLogin.play();
//
//        fadeInRegister = new FadeTransition(Duration.millis(500), registerText);
//        fadeInRegister.setFromValue(0);
//        fadeInRegister.setToValue(1);
//        fadeInRegister.setDelay(Duration.millis(200));
//        fadeInRegister.play();
//    }
//    @FXML
//    public void reverseButtons(){
//        fadeInLogin.stop();
//        fadeInRegister.stop();
//        loginText.setOpacity(0);
//        registerText.setOpacity(0);
//
//        moveRegister.stop();
//        resizeLogin.stop();
//        resizeRegister.stop();
//
//        login.setMinWidth(40);
//        register.setMinWidth(40);
//        register.setTranslateX(0);
//    }
//    @FXML
//    public void expandTextField(MouseEvent event){
//        TextField textField = (TextField) event.getSource();
//        textField.setPromptText("");
//        if(textField.equals(username)){
//            usernameHovered = true;
//        }else{
//            registerHovered = true;
//        }
//
//        resizeTextField = new ResizeWidthTransition(Duration.millis(450), textField, 250);
//        resizeTextField.play();
//
//        move = new TranslateTransition(Duration.millis(202.5), textField);
//        move.setDelay(Duration.millis(235.5));//223
//        move.setToX(-50);
//        move.play();
//
//        FadeTransition fadeIn = new FadeTransition(Duration.millis(800),textField);
//        fadeIn.setFromValue(0.15);
//        fadeIn.setToValue(1);
//        fadeIn.play();
//    }
//    @FXML
//    public void reverseTextField(MouseEvent event){
//        TextField textField = (TextField) event.getTarget();
//        String text = textField.getId();
//        text = text.substring(0, 1).toUpperCase() + text.substring(1);
//        textField.setPromptText(text);
//
//        usernameHovered = false;
//        registerHovered = false;
//
//        move.stop();
//        resizeTextField.stop();
//
//        textField.setMinWidth(150);
//        textField.setTranslateX(0);
//    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        changeStage();
//        password.setOnKeyPressed(event -> {
//            if(event.getCode().equals(KeyCode.ENTER)){
//                login();
//            }
//        });
//        username.setOnKeyPressed(event -> {
//            if(event.getCode().equals(KeyCode.ENTER)){
//                login();
//            }
//        });
//        password.focusedProperty().addListener((obs, oldVal, newVal) ->{
//            if (newVal){
//                focusedTextField = password;
//            }else{
//                focusedTextField = null;
//                password.setPromptText("Password");
//                password.setMinWidth(150);
//                password.setTranslateX(0);
//            }
//        });
    }
}
