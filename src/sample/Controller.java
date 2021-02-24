package sample;

import com.sun.media.jfxmediaimpl.platform.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Controller {
    @FXML
    ImageView imgview,BWimgview;
    @FXML
    TextField txtfield;
    @FXML
    Button loadButton,removeButton,greyButton;
    @FXML
    MenuItem quitButton,FilePicker;
    @FXML
    Slider redSlider,greenSlider,blueSlider;
    PixelReader pixelReader;
    WritableImage wImage;
    PixelWriter pixelWriter;
    Image image,BWimage;

    private static final boolean DEBUG=true;

    public void chooseFile(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        FilePicker.setOnAction(e -> {
            setExtFilters(fileChooser);
            File file = fileChooser.showOpenDialog(new Stage());
            imgview.setImage(new Image(file.toURI().toString()));
            image=imgview.getImage();
            imageReader();
        });
    }

    private void setExtFilters(FileChooser chooser) {
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }

    public void loadImage(ActionEvent actionEvent) {
        if((txtfield.getText()==null && txtfield.getText().equals(""))) {
            txtfield.setText("Insert image url here!");
        } else {
            loadButton.setOnAction(e -> {
                //InputStream stream = new FileInputStream("/home/keith/Documents/Semester04/DataStructuresAlgorithms2/Worksheet01/testImage.png");
                // image = new Image(getClass().getResourceAsStream("testImage.png"));
                image = new Image(txtfield.getText());
                imgview.setImage(image);

                imageReader();
            });
        }
    }
    private void imageReader(){
        // Obtain PixelReader
        pixelReader = image.getPixelReader();
        // Create WritableImage
        wImage = new WritableImage(
                (int) image.getWidth(),
                (int) image.getHeight());
        pixelWriter = wImage.getPixelWriter();
    }

    public void setRedColor(ActionEvent actionEvent) {
        redSlider.setOnMouseDragExited(e -> {
            // Determine the color of each pixel in a specified row
            for (int readY = 0; readY < image.getHeight(); readY++) {
                for (int readX = 0; readX < image.getWidth(); readX++) {
                    Color color = pixelReader.getColor(readX, readY);
                    System.out.println("\nPixel color at coordinates ("
                            + readX + "," + readY + ") "
                            + color.toString());

                    // Now write a Red color to the PixelWriter.
                    pixelWriter.setColor(readX, readY, color.deriveColor(redSlider.getValue(),color.getGreen(),color.getBlue(),color.getOpacity()));
                    imgview.setImage(wImage);
                    System.out.println(redSlider.getValue());
                }
            }
        });
    }

//    opacityLevel.valueProperty().addListener(new ChangeListener<Number>() {
//        public void changed(ObservableValue<? extends Number> ov,
//                Number old_val, Number new_val) {
//            cappuccino.setOpacity(new_val.doubleValue());
//            opacityValue.setText(String.format("%.2f", new_val));
//        }
//    });

    public void greyScale(ActionEvent actionEvent){
        // Determine the color of each pixel in a specified row
        for(int readY=0;readY<image.getHeight();readY++) {
            for(int readX=0;readX<image.getWidth();readX++) {
                Color color=pixelReader.getColor(readX,readY);
                System.out.println("\nPixel color at coordinates ("
                        + readX + "," + readY + ") "
                        + color.toString());
                System.out.println("R = " + color.getRed());
                System.out.println("G = " + color.getGreen());
                System.out.println("B = " + color.getBlue());
                System.out.println("Opacity = " + color.getOpacity());
                System.out.println("Saturation = " + color.getSaturation());

                // Now write a greyscale color to the PixelWriter.
                pixelWriter.setColor(readX,readY,color.grayscale());
                BWimgview.setImage(wImage);
            }
        }
        System.out.println("Image Width: "+image.getWidth());
        System.out.println("Image Height: "+image.getHeight());
        System.out.println("Pixel Format: "+pixelReader.getPixelFormat());
    }

    public void removeImage(ActionEvent actionEvent) {
        removeButton.setOnAction(e -> {
            txtfield.clear();
            imgview.setImage(null);
        });
    }

    public void quitProgram(ActionEvent actionEvent) {
        quitButton.setOnAction(e -> {
            System.exit(0);
        });
    }

}
