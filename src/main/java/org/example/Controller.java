package org.example;

import java.io.File;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller {
    private Image originalImage;
    private Image grayscaleImage;
    @FXML
    private Slider hueSlider;
    @FXML
    private Slider saturationSlider;
    @FXML
    private Slider brightnessSlider;
    @FXML
    private MenuItem openRGBChannelMenuItem;
    @FXML
    private ImageView originalImageView;
    @FXML
    private ImageView grayscaleImageView;
    @FXML
    private ImageView redImageView;
    @FXML
    private ImageView greenImageView;
    @FXML
    private ImageView blueImageView;
    @FXML
    private MenuItem menuOpenImage;
    @FXML
    private Label imageSizeLabel;
    @FXML
    private Label imageNameLabel;

    @FXML
    private void initialize()
    {
        originalImage =  new Image("file:TestImage.png", 0, 100, false, false);
        originalImageView.setImage(originalImage);
    }

    // Takes the sliders value and adjusts the original image
    @FXML
    public void onUpdateImageColors(MouseEvent e) {
        ColorAdjust colorAdjust = new ColorAdjust();

        colorAdjust.setContrast(0);
        colorAdjust.setHue(hueSlider.getValue());
        colorAdjust.setBrightness(brightnessSlider.getValue());
        colorAdjust.setSaturation(saturationSlider.getValue());
        originalImageView.setEffect(colorAdjust);
    }

    @FXML
    public void onGrayscaleButtonClick(KeyEvent keyEvent) {
        Image grayscaleImage = toGrayScale(originalImage);
        grayscaleImageView.setImage(grayscaleImage);
    }

    // Takes in an image and returns it in grayscale
    private Image toGrayScale(Image sourceImage) {
        PixelReader pixelReader = sourceImage.getPixelReader();

        int width = (int) sourceImage.getWidth();
        int height = (int) sourceImage.getHeight();

        WritableImage grayImage = new WritableImage(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixelReader.getArgb(x, y);

                int alpha = ((pixel >> 24) & 0xff);
                int red = ((pixel >> 16) & 0xff);
                int green = ((pixel >> 8) & 0xff);
                int blue = (pixel & 0xff);

                int grayLevel = (int) (0.2162 * red + 0.7152 * green + 0.0722 * blue);
                int gray = (alpha << 24) + (grayLevel << 16) + (grayLevel << 8) + grayLevel;

                grayImage.getPixelWriter().setArgb(x, y, gray);
            }
        }
        return grayImage;
    }

    @FXML
    protected void onMenuOpenImageClick() {
        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(originalImageView.getScene().getWindow());
        if (file != null) {
            openFile(file);
        }
    }

    private void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Open Image File");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }

    private void openFile(File file){
//        try {
            originalImage = new Image(file.toURI().toString());
            imageDetails(originalImage);
            originalImageView.setImage(originalImage);
//        } catch (IOException ex) {
//            Logger.getLogger(HelloController.class.getName()).log(
//                    Level.SEVERE, null, ex
//            );
//        }
    }

    @FXML
    public void onSaveClick(ActionEvent actionEvent) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            System.out.println(grayscaleImageView.getId());
            File file = fileChooser.showOpenDialog(originalImageView.getScene().getWindow());
            if (file != null) {
//                try {
//                    ImageIO.write(SwingFXUtils.fromFXImage(grayscaleImageView.getImage(),
//                            null), "png", file);
//                } catch (IOException ex) {
//                    System.out.println(ex.getMessage());
//                }
            }
    }

    @FXML
    public void onQuitClick(ActionEvent actionEvent) {
        System.exit(1);
    }

    // Displays image details into the appropriate labels
    private void imageDetails(Image image) {
        imageSizeLabel.setText(image.getWidth() + "x" + image.getHeight());
        imageNameLabel.setText(image.getUrl());
//        originalImageView.setOpacity();
    }

    public void onMenuOpenRGBChannelWindow(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("rgbChannel.fxml"));
        Scene rgbChannelScene  = new Scene(fxmlLoader.load(), 320, 240);
//        TabPane tabPane = new TabPane(new Tab("Red"), new Tab("Blue"), new Tab("Green"));
//        Scene rgbChannelScene = new Scene(tabPane, 50, 50);
        Stage rgbChannelStage = new Stage();
        rgbChannelStage.setTitle("RGB Channel");
        rgbChannelStage.setScene(rgbChannelScene);
        rgbChannelStage.show();

//        if (originalImage != null) {
            redImageView.setImage(toDominantColor(originalImage, Color.RED));
            greenImageView.setImage(toDominantColor(originalImage, Color.GREEN));
            blueImageView.setImage(toDominantColor(originalImage, Color.BLUE));
//        }
    }

    private Image toDominantColor(Image sourceImage, Color color) {
        PixelReader pixelReader = sourceImage.getPixelReader();

        int width = (int) sourceImage.getWidth();
        int height = (int) sourceImage.getHeight();

        WritableImage colorImage = new WritableImage(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = pixelReader.getColor(x, y);

                int red =  (int) pixelColor.getRed();
                int green = (int) pixelColor.getGreen();
                int blue = (int) pixelColor.getBlue();

                if (red > green && red > blue && color == Color.RED) {
                    colorImage.getPixelWriter().setArgb(x, y, red);
                } else if ( green > red && green > blue && color == Color.GREEN) {
                    colorImage.getPixelWriter().setArgb(x, y, green);
                } else if (blue > red && blue > green && color == Color.BLUE) {
                    colorImage.getPixelWriter().setArgb(x, y, blue);
                }
                else {
                    colorImage.getPixelWriter().setArgb(x, y, 0);
                }
            }
        }
        return colorImage;
    }

}