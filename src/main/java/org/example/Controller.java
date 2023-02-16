package org.example;

import java.io.File;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class Controller {
//    https://stackoverflow.com/questions/5585779/how-do-i-convert-a-string-to-an-int-in-java
    public TextField minSize;
    public TextField maxSize; // Integer.parseInt(maxSize.getText);
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
    //TODO: add a slider to control this value
    private double minBrightness = 0.4;

    @FXML
    private void initialize()
    {
//        originalImage =  new Image("file:/home/keith/workspace/college/Semester%204%20Repeat/Assignment01/src/main/resources/org/example/TestImage16x16.png");
        originalImage =  new Image("file:/home/keith/workspace/college/Semester%204%20Repeat/Assignment01/src/main/resources/org/example/TestImage128x64.png");
        originalImageView.setImage(originalImage);

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        // Saving the image to a 1D array
        int[] imageArray;
        imageArray = new int[width*height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Sets every pixel to its own disjoint set on startup
                imageArray[(y*width) + x] = (y*width) + x;
            }
        }

        Image blackWhiteImage = toBlackAndWhite(originalImage, imageArray);
        grayscaleImageView.setImage(blackWhiteImage);

        imageArrayView(imageArray);
        disjointSet(imageArray);
        imageArrayView(imageArray);
        grayscaleImageView.setImage(colorBWImage(blackWhiteImage,imageArray));
//        for(int id=0;id<imageArray.length;id++)
//            System.out.println("The root of element "+id+" is "+find(imageArray,id)+" (element value: "+imageArray[id]+")");
//        for(int id=0;id<imageArray.length;id++)
//            System.out.println("The root of "+id+" is "+ find(imageArray,id));

//        for (int i = 0; i < imageArray.length; i++) {
//
//        }
//
//        Group imageGroup = new Group();
//
//        // need to know outer limits
//        Circle circle = new Circle();
//        circle.setCenterX(100.0f);
//        circle.setCenterY(100.0f);
//        circle.setRadius(50.0f);
//
//        imageGroup.getChildren().add(originalImageView);
//        imageGroup.getChildren().add(circle);
    }

    // Takes the sliders value and adjusts the original image
    @FXML
    public void onUpdateImageColors(MouseEvent e) {
        ColorAdjust colorAdjust = new ColorAdjust();

        colorAdjust.setContrast(0);
        colorAdjust.setHue(hueSlider.getValue());
        minBrightness = brightnessSlider.getValue();
        colorAdjust.setBrightness(brightnessSlider.getValue());
        colorAdjust.setSaturation(saturationSlider.getValue());
        originalImageView.setEffect(colorAdjust);
    }

    @FXML
    public void onGrayscaleButtonClick(KeyEvent keyEvent) {
        Image grayscaleImage = toGrayScale(originalImage);
//        Image grayscaleImage = toBlackAndWhite(originalImage);
        grayscaleImageView.setImage(grayscaleImage);
    }

    // Index = (row number * size of row) + column number
    // (y) row = index/rowsize
    // (x) column = index%rowsize
    private void disjointSet(int[] imageArray) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        PixelReader pixelReader;
        pixelReader = Objects.requireNonNullElseGet(grayscaleImage, () -> originalImage).getPixelReader();
//        if (grayscaleImage != null) pixelReader = grayscaleImage.getPixelReader();
//        else pixelReader = originalImage.getPixelReader();
        WritableImage bwImage = new WritableImage(width, height);

        for (int i = 0; i < imageArray.length; i++) {
            int currentIndex = abs(((i / width)* width) + (i % width));
            int topLeftIndex = abs(((((i - width) / width) * width) + ((i - 1) % width)));
            int topIndex = abs(((i - width) / width) * width+ (i % width));
            int topRightIndex = abs(((i - width) / width) * width + ((i + 1) % width));
            int leftIndex = abs((((i / width) * width) + ((i - 1) % width)));

            Color pixelColor = pixelReader.getColor(i%width,i/width);
            double pixelBrightness = pixelColor.getBrightness();

            // sets black pixels to have a root of -1
            if (pixelBrightness <= minBrightness) {
                imageArray[currentIndex] = -1;
            }
            //top left adjacent index
            else if (imageArray[topLeftIndex] > -1) {
//                System.out.println("top left index used");
//                imageArray[currentIndex] = find(imageArray, imageArray[topLeftIndex]);
                union(imageArray,topLeftIndex,currentIndex);
            }
            //top adjacent index
            else if (imageArray[topIndex] > -1) {
//                System.out.println("top index used");
//                imageArray[currentIndex] = find(imageArray, imageArray[topIndex]);
                union(imageArray,topIndex,currentIndex);
            }
            //top right adjacent index
            else if (imageArray[topRightIndex] > -1) {
//                System.out.println("top right index used");
//                imageArray[currentIndex] = find(imageArray, imageArray[topRightIndex]);
                union(imageArray,topRightIndex,currentIndex);
            }
            //left adjacent index
            else if (imageArray[leftIndex] > -1) {
//                System.out.println("left index used");
//                imageArray[currentIndex] = find(imageArray, imageArray[leftIndex]);
                union(imageArray,leftIndex,currentIndex);
            }
        }

//        originalImageView.setImage((bwImage));
    }

//    int size = 88;
//    int height = 99;
//    int root = 1;
//    int value = (root << 31) + (height << 16) + size;
    private boolean isRoot(int value) {
        if (((value >> 32) & 0xff) >= 1) return true;
        else return false;
    }

    // Index = (row number * size of row) + column number
    // (y) row = index/rowsize
    // (x) column = index%rowsize
    private int getImagePixelColor (int i, int w, int h, PixelReader pixelReader) {
        return pixelReader.getArgb(i%w, i/w);
    }

    private void imageArrayView(int[] imageArray) {
        // Viewing the 1D array
        System.out.println();
        System.out.print("index: "  + "\t|\t");
        for (int i = 0; i < imageArray.length; i++)
            System.out.print(i + "\t|\t");
        System.out.println();
        System.out.print("root: " + "\t|\t");
        for (int i : imageArray)
            System.out.print(i + "\t|\t");
        System.out.println();
    }

    // Takes in an image and returns it in black/white
    private Image toBlackAndWhite(Image sourceImage, int[] imageArray) {
        PixelReader pixelReader = sourceImage.getPixelReader();

        int width = (int) sourceImage.getWidth();
        int height = (int) sourceImage.getHeight();

        WritableImage grayImage = new WritableImage(width, height);

        for (int i = 0; i < imageArray.length; i++) {
            int x = i%width;
            int y = i/width;

            Color pixelColor = pixelReader.getColor(x, y);
            if (pixelColor.getBrightness() >= minBrightness) {
                grayImage.getPixelWriter().setArgb(x, y, -1);
            }
            else {
                grayImage.getPixelWriter().setArgb(x, y, -16777216);
            }
//            }
        }
        return grayImage;
    }

    // Index = (row number * size of row) + column number
    // row = index/rowsize
    // column = index%rowsize
    private Image colorBWImage(Image sourceImage, int[] a) {
        PixelReader pixelReader = sourceImage.getPixelReader();

        int width = (int) sourceImage.getWidth();
        int height = (int) sourceImage.getHeight();

        WritableImage coloredBWImage = new WritableImage(width, height);

        for (int i = 0; i < a.length; i++) {
            int x = i%width;
            int y = i/width;
            // if root choose random color
            // else find root and use its color

            if (a[i] == i) {
                int pixel = getImagePixelColor(i, width, height, pixelReader);
                int alpha = ((pixel >> 24) & 0xff);

                Random rand = new Random();
                int red = rand.nextInt(256);
                int green = rand.nextInt(256);
                int blue = rand.nextInt(256);
                int color = (alpha << 24) + (red << 16) + (green << 8) + blue;
//                System.out.println("Root : " + find(a,i));
//                System.out.println("Color: " + color);

                coloredBWImage.getPixelWriter().setArgb(x, y, color);
            }
            else if (a[i] != -1) {
                int pixel = getImagePixelColor(find(a,i), width, height, coloredBWImage.getPixelReader());
//                System.out.println("Root : " + find(a,i));
//                System.out.println("Color: " + pixel);
                coloredBWImage.getPixelWriter().setArgb(x, y, pixel);
            }
            else {
                int pixel = getImagePixelColor(i,width,height,pixelReader);
//                System.out.println("Coloring black pixels: " + pixel);
                coloredBWImage.getPixelWriter().setArgb(x, y, pixel);
            }
        }
        return coloredBWImage;
    }

    //Iterative version of find with path compression
    public static int find(int[] a, int id) {
        while(id!=-1 && a[id]!=id) {
            a[id]=a[a[id]]; //Compress path
            id=a[id];
        }
        return id;
    }
    //Quick union of disjoint sets containing elements p and q
    public static void union(int[] a, int p, int q) {
        a[find(a,q)]=find(a,p); //The root of q is made reference the root of p
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
//                System.out.println(blue);

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