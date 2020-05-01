package controllers.secondstyle;

import animations.MoveRoot;
import animations.TransitionResizeHeight;
import animations.TransitionResizeWidth;
import controllers.base.Controller;
import controllers.base.ControllerLogged;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicReference;

import static application.RestaurantApplication.*;

public class LoggedMenu extends ControllerLogged implements Controller {
    @FXML AnchorPane menuRoot, menu, menuButtons, menuButtonsContainer, profileView, menuContent;
    @FXML Button menuButton, notificationButton;
    @FXML Pane profileImageContainer, profileImageClip;
    @FXML HBox notificationMenuIcon;

    private HBox notificationBox;
    private AnchorPane currentMenuView;
    private AtomicReference<Button> currentContentButton = new AtomicReference<>();
    private AtomicReference<Button> currentMenuButton = new AtomicReference<>();

    private LoggedSecond contentController = (LoggedSecond) stageManager.secondLoggedController;
    private Stage secondLoggedMenuStage = stageManager.secondLoggedMenuStage;

    private Timeline reverseStageHeight, reverseStageWidth, reverseDelay;
    private TransitionResizeWidth reverseMenu, expandMenu;
    private TransitionResizeHeight reverseMenuContent, expandMenuContent;

    @FXML
    public void initialize(){
        notificationBox = (HBox)notificationIcon;
        setNotificationBox(notificationBox);
        setNotificationBox(notificationMenuIcon);
        bindNotificationIconSize();
        setClips();

        notificationMenuIcon.opacityProperty().bind(notificationBox.opacityProperty());
        notificationsList.setItems(notificationManager.notifications);

        setUserGraphicIndicator();
        setNotificationsListeners();
        setNotificationsFactories();
        setUserFields();
        setMenuTransitions();

        MoveRoot.moveStage(menuButton, secondLoggedMenuStage);

        editIndicator.maxHeightProperty().bind(editButton.heightProperty().subtract(15));
    }

    private void setClips() {
        Rectangle profileViewClip = new Rectangle();
        profileViewClip.widthProperty().bind(profileView.widthProperty());
        profileViewClip.heightProperty().bind(profileView.heightProperty());
        profileView.setClip(profileViewClip);

        Circle clip = new Circle(30.8, 30.8, 30.8);
        profileImageClip.setClip(clip);

        Rectangle notificationClip = new Rectangle();
        notificationClip.setArcHeight(33);
        notificationClip.setArcWidth(33);
        notificationClip.heightProperty().bind(notificationsList.heightProperty());
        notificationClip.widthProperty().bind(notificationsList.widthProperty());
        notificationsList.setClip(notificationClip);
    }

    private void setMenuTransitions() {
        expandMenu = new TransitionResizeWidth(Duration.millis(700), menu, menu.getMaxWidth());
        reverseMenu = new TransitionResizeWidth(Duration.millis(700), menu, 38.5);
        reverseMenuContent = new TransitionResizeHeight(Duration.millis(800), menuContent, 0);
        expandMenuContent = new TransitionResizeHeight(Duration.millis(800), menuContent, menuContent.getMaxHeight());

        reverseDelay = new Timeline();
        reverseStageHeight = new Timeline(new KeyFrame(Duration.millis(800), event -> secondLoggedMenuStage.setHeight(menuRoot.getPrefHeight())));
        reverseStageWidth = new Timeline(new KeyFrame(Duration.millis(800), event -> secondLoggedMenuStage.setWidth(75)));
    }

    @Override
    public void resetStage() {
        if(notificationsList.getItems().size() > 0) notificationsList.scrollTo(0);

        secondLoggedMenuStage.setHeight(menuRoot.getPrefHeight());

        menuContent.setDisable(true);
        notificationsView.setOpacity(0);
        notificationsView.setDisable(true);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(600), profileImageContainer);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.play();
    }

    @Override
    public void setStage() throws Exception {
        secondLoggedMenuStage.setWidth(menuRoot.getPrefWidth());
        secondLoggedMenuStage.setHeight(menuRoot.getPrefHeight());

        secondLoggedMenuStage.setX((primaryScreenBounds.getWidth() - secondLoggedMenuStage.getWidth()) / 2);
        secondLoggedMenuStage.setY(contentController.contentRoot.getLayoutY() - 75);

        menuRoot.setLayoutX((menuRoot.getPrefWidth() - menu.getPrefWidth()) / 2);
    }

    @FXML
    public void onNotificationClick(){
        notificationsList.getItems().remove(notificationsList.getFocusModel().getFocusedItem());
    }

    private void expandMenuContent(){
        reverseStageHeight.stop();
        secondLoggedMenuStage.setHeight(menuRoot.getMaxHeight());

        reverseMenuContent.stop();

        expandMenuContent = new TransitionResizeHeight(Duration.millis(800), menuContent, menuContent.getMaxHeight());
        expandMenuContent.play();
    }
    private void reverseMenuContent(){
        reverseStageHeight.play();
        expandMenuContent.stop();

        reverseMenuContent = new TransitionResizeHeight(Duration.millis(800), menuContent, 0);
        reverseMenuContent.play();
    }

    @FXML public void expandMenu(){
        if(secondLoggedMenuStage.getHeight() != menuRoot.getPrefWidth()) {
            notificationMenuIcon.setVisible(false);
            if (menuButtonsContainer.getChildren().size() == 1) {
                menuButtonsContainer.getChildren().add(0, menuButtons);
            }
            reverseStageWidth.stop();
            secondLoggedMenuStage.setWidth(menuRoot.getPrefWidth());
            reverseDelay.stop();
            reverseMenu.stop();

            expandMenu = new TransitionResizeWidth(Duration.millis(700), menu, menu.getMaxWidth());
            expandMenu.play();
        }
    }
    @FXML
    public void reverseMenu(){
        if(currentMenuView == null || reverseMenuContent.getCurrentRate() != 0 ) {
            Duration delay = reverseMenuContent.getCurrentRate() == 1 ?
                    reverseMenuContent.getCycleDuration().subtract(reverseMenuContent.getCurrentTime()) : Duration.millis(1);

            reverseDelay = new Timeline(new KeyFrame(delay, event -> {
                notificationMenuIcon.setVisible(true);

                expandMenu.stop();
                reverseStageWidth.play();


                reverseMenu = new TransitionResizeWidth(Duration.millis(700), menu, 38.5);
                reverseMenu.play();
                menuButtonsContainer.getChildren().remove(menuButtons);
            }));
            reverseDelay.play();
        }
    }

    @FXML
    public void showProfile(){
        if(menuContent.isDisabled()) {
            expandMenuContent();
            currentMenuView = profileView;

            menuContent.setDisable(false);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(600), profileImageContainer);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setDelay(Duration.millis(300));
            fadeIn.play();

        }else if(!currentMenuView.equals(profileView) && menuContent.getPrefHeight() == menuContent.getMaxHeight()){
            currentMenuView.setOpacity(0);
            currentMenuView.setDisable(true);
            currentMenuView = profileView;

            profileImageContainer.setOpacity(1);
        }else if(menuContent.getPrefHeight() == menuContent.getMaxHeight()){
            reverseMenuContent();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(600), profileImageContainer);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.play();
            Timeline removeView = new Timeline(new KeyFrame(Duration.millis(800), event -> {
                menuContent.setDisable(true);
                currentMenuView = null;
            }));
            removeView.play();
        }
    }
    @FXML
    public void showNotifications(){
        if(menuContent.isDisabled()) {
            expandMenuContent();

            currentMenuView = notificationsView;
            currentMenuView.setOpacity(1);
            currentMenuView.setDisable(false);
            menuContent.setDisable(false);

            isNewNotificationChecked.set(true);
        }else if(!currentMenuView.equals(notificationsView) && menuContent.getPrefHeight() == menuContent.getMaxHeight()){
            profileImageContainer.setOpacity(0);

            currentMenuView = notificationsView;
            currentMenuView.setDisable(false);
            currentMenuView.setOpacity(1);

            isNewNotificationChecked.set(true);
        }else if(menuContent.getPrefHeight() == menuContent.getMaxHeight()){
            reverseMenuContent();

            Timeline removeView = new Timeline(new KeyFrame(Duration.millis(800), event -> {
                menuContent.setDisable(true);
                currentMenuView.setDisable(true);
                currentMenuView.setOpacity(0);
                currentMenuView = null;
            }));
            removeView.play();
        }
    }
    @FXML
    public void profileButtonHoverOver(MouseEvent event){
        AnchorPane shadowContainer = (AnchorPane) ((Button)event.getSource()).getParent();
        shadowContainer.getStyleClass().add("profile-button-hovered");
    }
    @FXML
    public void profileButtonHoverOut(MouseEvent event){
        AnchorPane shadowContainer = (AnchorPane) ((Button)event.getSource()).getParent();
        shadowContainer.getStyleClass().remove("profile-button-hovered");
    }
    @FXML
    public void menuButtonAction(MouseEvent event){
        Button button = (Button) event.getSource();

        switch (button.getId()){
            case "profileButton":
                showProfile();
                if(menuContent.getPrefHeight() == 0 || menuContent.getPrefHeight() == menuContent.getMaxHeight())
                    changeButtonStyle(button, currentMenuButton);
                break;
            case "notificationButton":
                if(menuContent.getPrefHeight() == 0 || menuContent.getPrefHeight() == menuContent.getMaxHeight())
                    changeButtonStyle(button, currentMenuButton);
                showNotifications();
                break;
            case "orderButton":
                changeButtonStyle(button, currentContentButton);
                showOrderView();
                break;
            case "chatButton":
                showChatView();
                changeButtonStyle(button, currentContentButton);
                break;
            case "createButton":
                showCreateView();
                changeButtonStyle(button, currentContentButton);
                break;
        }
    }
    private void changeButtonStyle(Button newButton, AtomicReference<Button> currentButtonOptional){
        Button currentButton = currentButtonOptional.get();
        if(currentButton != null){
            currentButton.getStyleClass().add("shadow");
        }
        if(newButton != currentButton){
            newButton.getStyleClass().remove("shadow");
            currentButtonOptional.set(newButton);
        }else{
            currentButtonOptional.set(null);
        }
    }
    @FXML
    public void menuButtonFocus(MouseEvent event){
        Button button = (Button) event.getSource();
        AnchorPane.setTopAnchor(button, -5.5);
        AnchorPane.setBottomAnchor(button, -4.0);
    }
    @FXML
    public void menuButtonUnFocus(MouseEvent event){
        Button button = (Button) event.getSource();
        AnchorPane.setTopAnchor(button, -1.0);
        AnchorPane.setBottomAnchor(button, 0.0);
    }


    private void setNotificationBox(HBox icon) {
        SVGPath usb3 = new SVGPath();
        usb3.setContent("m434.753906 360.8125c-32.257812-27.265625-50.753906-67.117188-50.753906-109.335938v-59.476562c0-75.070312-55.765625-137.214844-128-147.625v-23.042969c0-11.796875-9.558594-21.332031-21.332031-21.332031-11.777344 0-21.335938 9.535156-21.335938 21.332031v23.042969c-72.253906 10.410156-128 72.554688-128 147.625v59.476562c0 42.21875-18.496093 82.070313-50.941406 109.503907-8.300781 7.105469-13.058594 17.429687-13.058594 28.351562 0 20.589844 16.746094 37.335938 37.335938 37.335938h352c20.585937 0 37.332031-16.746094 37.332031-37.335938 0-10.921875-4.757812-21.246093-13.246094-28.519531zm0 0");

        SVGPath usb4 = new SVGPath();
        usb4.setContent("m234.667969 512c38.632812 0 70.953125-27.542969 78.378906-64h-156.757813c7.421876 36.457031 39.742188 64 78.378907 64zm0 0");
        Shape s = Shape.union(usb3,usb4);

        Region region = new Region();
        region.setShape(s);
        icon.getChildren().add(region);

        RotateTransition tt = new RotateTransition(Duration.millis(200), icon);
        tt.setByAngle(16);
        RotateTransition tl = new RotateTransition(Duration.millis(200), icon);
        tl.setByAngle(-32);
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));

        SequentialTransition sequentialTransition = new SequentialTransition(pauseTransition, tt, tl);
        sequentialTransition.setCycleCount(Timeline.INDEFINITE);
        sequentialTransition.setAutoReverse(true);
        sequentialTransition.play();

    }
    void bindNotificationIconSize(){
        Region region = (Region)notificationBox.getChildren().get(0);
        region.minWidthProperty().bind(notificationButton.widthProperty().subtract(68));
        region.prefWidthProperty().bind(notificationButton.widthProperty().subtract(68));
        region.minHeightProperty().bind(region.widthProperty());
        region.prefHeightProperty().bind(region.widthProperty());

        notificationBox.prefWidthProperty().bind(notificationButton.widthProperty().subtract(57));
        notificationBox.prefHeightProperty().bind(notificationButton.widthProperty().subtract(57));
    }
    @FXML
    public void showChatView(){
        contentController.displayView(contentController.chatView);
    }
    @FXML
    public void showOrderView(){
        contentController.displayView(contentController.orderView);
    }
    @FXML
    public void showCreateView(){
        contentController.displayView(contentController.createView);
    }
}