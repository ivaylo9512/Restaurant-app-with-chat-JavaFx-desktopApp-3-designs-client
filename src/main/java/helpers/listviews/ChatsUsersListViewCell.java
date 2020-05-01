package helpers.listviews;

import controllers.base.ControllerLogged;
import controllers.firststyle.LoggedFirst;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import models.ChatValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.io.IOException;

import static application.RestaurantApplication.stageManager;

public class ChatsUsersListViewCell extends ListCell<ChatValue> {
    @FXML
    private ImageView imageView;
    @FXML
    private Pane imageContainer, shadow;
    @FXML
    private HBox container;

    private Circle clip = new Circle(25, 25, 25);
    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(ChatValue chat, boolean empty) {
        super.updateItem(chat, empty);
        if(empty || chat == null){
            setGraphic(null);
            setText(null);
        }else{
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/FXML/cells/chat-user-cell.fxml"));
                mLLoader.setController(this);
                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            imageView.setImage(chat.getSecondUserPicture());
            imageContainer.setClip(clip);

            setText(null);
            setGraphic(container);
        }
    }

    {
        setOnMouseClicked(event -> {
            if(stageManager.currentController instanceof LoggedFirst && stageManager.firstLoggedStage.isShowing()){
                ((ControllerLogged)stageManager.firstLoggedController).setMainChat(getItem());
            }
        });
    }
}