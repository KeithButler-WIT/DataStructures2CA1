package org.example;

import java.io.File;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.ArrayList;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import static java.lang.Math.*;

public class Controller {
    public TextField minSizeText;
    public TextField maxSizeText; // Integer.parseInt(maxSize.getText);
    public Pane imageViewBox;
    public Label totalNumSets;
    public Button blackWhiteButton;
    private int minSize;
    private int maxSize;
    private Image originalImage;
    private Image blackWhiteImage;
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
    private ImageView blackWhiteImageView;
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
    //TODO: add slider to adjust this
    private double minBrightness = 0.3;

    @FXML
    private void initialize() {
        originalImage = new Image("file:/home/keith/workspace/college/Semester%204%20Repeat/Assignment01/src/main/resources/org/example/TestImage16x16.png");
//        originalImage =  new Image("file:/home/keith/workspace/college/Semester%204%20Repeat/Assignment01/src/main/resources/org/example/TestImage128x64.png");
        originalImageView.setImage(originalImage);

        run();
    }

    private void run() {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        // Saving the image to a 1D array
        long[] imageArray = new long[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width) + x;
                // Sets every pixel to its own disjoint set on startup
                setRoot(imageArray, index, index);
                // Initialize all sets to a size of 1
                setSize(imageArray, index, 1);

//                System.out.println((imageArray[index] >> 32) & 0xffffffffL);
//                System.out.println(getSize(imageArray[index]));
//                imageArray[(y*width) + x] = (y*width) + x;
            }
        }

//        imageArrayView(imageArray);
        Image blackWhiteImage = toBlackAndWhite(originalImage, imageArray);
        blackWhiteImageView.setImage(blackWhiteImage);

//        imageArrayView(imageArray);
        disjointSet(imageArray);
//        imageArrayView(imageArray);
        blackWhiteImageView.setImage(colorBWImage(blackWhiteImage, imageArray));

        ArrayList<Integer> knownRoots = new ArrayList<>();
        int numSets = countSets(imageArray, knownRoots);
        drawCircles(numSets, imageArray, knownRoots);
    }

    // Index = (row number * size of row) + column number
    // (y) row = index/rowsize
    // (x) column = index%rowsize
    private void disjointSet(long[] imageArray) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

//        PixelReader pixelReader = Objects.requireNonNullElseGet(grayscaleImage, () -> originalImage).getPixelReader();

        // Initialise all white pixels to be their own root
        for (int i = 0; i < imageArray.length; i++) {
            // skip to next pixel if node is set to ignored
            if (isIgnored(imageArray[i])) {
                continue;
            }

            int currentIndex = abs(((i / width) * width) + (i % width));
            int topLeftIndex = abs(((((i - width) / width) * width) + ((i - 1) % width)));
            int topIndex = abs(((i - width) / width) * width + (i % width));
            int topRightIndex = abs(((i - width) / width) * width + ((i + 1) % width));
            int leftIndex = abs((((i / width) * width) + ((i - 1) % width)));

            PixelReader pixelReader = originalImage.getPixelReader();
            double pixelBrightness = pixelReader
                    .getColor(i % width, i / width)
                    .getBrightness();

//            System.out.println(getSize(imageArray[currentIndex]));

            //top left adjacent index
            if (!isIgnored(imageArray[topLeftIndex])) {
                setSize(imageArray, topLeftIndex, getSize(find(imageArray, topLeftIndex))+1);
                setSize(imageArray, currentIndex, getSize(find(imageArray, currentIndex))+1);
                setRoot(imageArray, currentIndex, topLeftIndex);
            }
            //top adjacent index
            if (!isIgnored(imageArray[topIndex])) {
                setSize(imageArray, topIndex, getSize(find(imageArray, topIndex))+1);
                setSize(imageArray, currentIndex, getSize(find(imageArray, currentIndex))+1);
                setRoot(imageArray, currentIndex, topIndex);
            }
            //top right adjacent index
            if (!isIgnored(imageArray[topRightIndex])) {
                setSize(imageArray, topRightIndex, getSize(find(imageArray, topRightIndex))+1);
                setSize(imageArray, currentIndex, getSize(find(imageArray, currentIndex))+1);
                setRoot(imageArray, currentIndex, topRightIndex);
            }
            //left adjacent index
            if (!isIgnored(imageArray[leftIndex])) {
                setSize(imageArray, leftIndex, getSize(find(imageArray, leftIndex))+1);
                setSize(imageArray, currentIndex, getSize(find(imageArray, currentIndex))+1);
                setRoot(imageArray, currentIndex, leftIndex);
            }

            System.out.println(getSize(imageArray[currentIndex]));
        }

        // Union
        for (int i = 0; i < imageArray.length; i++) {
            // skip to next pixel if node is set to ignored
            if (isIgnored(imageArray[i])) {
                continue;
            }

            int currentIndex = abs(((i / width) * width) + (i % width));
            int topIndex = abs(((i - width) / width) * width + (i % width));
            int bottomIndex = abs(((i + width) / width) * width + (i % width));
            int leftIndex = abs((((i / width) * width) + ((i - 1) % width)));
            int rightIndex = abs((((i / width) * width) + ((i + 1) % width)));

            //top adjacent index
            if (topIndex / width > 0 && !isIgnored(imageArray[topIndex])) {
                union(imageArray, topIndex, currentIndex);
            }
            //left adjacent index
            else if (leftIndex % width > 0 && !isIgnored(imageArray[leftIndex])) {
                union(imageArray, leftIndex, currentIndex);
            }
            //right adjacent index
            else if (rightIndex % width < width && !isIgnored(imageArray[rightIndex])) {
                union(imageArray, rightIndex, currentIndex);
            }
            //bottom adjacent index
            else if (bottomIndex / width < height && !isIgnored(imageArray[bottomIndex])) {
                union(imageArray, bottomIndex, currentIndex);
            }
        }
    }

    public int countSets(long[] imageArray, ArrayList<Integer> knownRoots) {
        int numSets = 0;
//         Counts the total number of sets in the image
        for (int i = 0; i < imageArray.length; i++) {
            if (isIgnored(imageArray[i])) {
                continue;
            }

            if (getSize(imageArray[i]) > 1) {
                System.out.println(getSize(imageArray[i]));
            }
            int currentRoot = find(imageArray, i);
            if (!knownRoots.contains(currentRoot)) {
                knownRoots.add(currentRoot);
                numSets++;
            }
        }
//        System.out.println(numSets);
        totalNumSets.setText(String.valueOf(numSets));
        return numSets;
    }

    private void drawCircles(int numSets, long[] imageArray, ArrayList<Integer> knownRoots) {
        // Removes old circles before adding new ones
        for(int i=0; i<imageViewBox.getChildren().size(); i++)
            if(imageViewBox.getChildren().get(i) instanceof Circle)
                imageViewBox.getChildren().remove(i);

        // Index = (row number * size of row) + column number
        // (y) row = index/rowsize
        // (x) column = index%rowsize

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

//        if (getSize(imageArray[i]) > 1) {
//            System.out.println(getSize(imageArray[i]));
//        }
        // loop through numSets and imageArray
        for (int i = 0; i < numSets; i++) {
            // need to know outer limits
            int minX = width;
            int minY = height;
            int maxX = 0;
            int maxY = 0;

            double totalRed = 0.0;
            double totalGreen = 0.0;
            double totalBlue = 0.0;

            int currentCircle = knownRoots.get(i);
//            System.out.println(getSize(imageArray[currentCircle]));
            for (int j = 0; j < imageArray.length; j++) {
                if (isIgnored(imageArray[j])) {
                    continue;
                }

                int currentRoot = find(imageArray, j);
                if (currentRoot != currentCircle) {
                    continue;
                }

                int x = j % width;
                int y = j / width;

                // Set minX
                if (x < minX) {
                    minX = x;
//                    System.out.println(minX);
                }
                // Set maxX
                else if (x > maxX) {
                    maxX = x;
//                    System.out.println("maxX: " + maxX);
                }
                // Set minY
                if (y < minY) {
                    minY = y;
//                    System.out.println(minY);
                }
                // Set maxY
                else if (y > maxY) {
                    maxY = y;
//                    System.out.println(maxY);
                }

                PixelReader pixelReader = originalImage.getPixelReader();

                int pixel = pixelReader.getArgb(x, y);

                totalRed += ((pixel >> 16) & 0xff);
                totalGreen += ((pixel >> 8) & 0xff);
                totalBlue += (pixel & 0xff);
            }

            totalRed = totalRed / getSize(imageArray[currentCircle]);
            totalGreen = totalGreen / getSize(imageArray[currentCircle]);
            totalBlue = totalBlue / getSize(imageArray[currentCircle]);

            double paneWidth = imageViewBox.getBoundsInLocal().getWidth();
            double paneHeight = imageViewBox.getBoundsInLocal().getHeight();
            Circle circle = new Circle();
            // Pythagoras Theorem
            circle.setCenterX(((minX + maxX) / 2f) * (paneWidth / width));
            circle.setCenterY(((minY + maxY) / 2f) * (paneHeight / height));
            // Length formula using the outer bounds of the circle
            double length = sqrt(((maxX - minX) ^ 2) + ((maxY - minY) ^ 2));
//            circle.setRadius((length) * (paneHeight / height));
            circle.setRadius(((maxY-minY)/2f) * (paneWidth/width));
            circle.setFill(Color.color(0,0,0,0));
            circle.setStroke(Color.BLUE);

//            System.out.println("-------TOP LEFT POSITION--------");
//            System.out.println("\t\t( " + minX + " , " + minY + " )");
//            System.out.println("-----BOTTOM RIGHT POSITION------");
//            System.out.println("\t\t( " + maxX + " , " + maxY + " )");
//            System.out.println("--------------------------------");
//            System.out.println("Circle center: ( " + circle.getCenterX() + " , " + circle.getCenterY() + " )");
//            System.out.println("Radius: " + circle.getRadius());

            //https://stackoverflow.com/questions/48173943/javafx-how-to-implement-a-custom-tooltip
            Tooltip.install(circle, new Tooltip("Contents:\n"
                    +"Estimated Size (pixel units): "+getSize(imageArray[currentCircle])+"\n"
                    +"Estimated Sulphur: "+totalRed+"\n"
                    +"Estimated Hydrogen: "+totalGreen+"\n"
                    +"Estimated Oxygen: "+totalBlue
            ));

            // Removes Circles when right-clicked
            circle.setOnMouseClicked(e  -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    imageViewBox.getChildren().remove(circle);
                }
            });

            imageViewBox.getChildren().add(circle);
        }
    }

    private void imageArrayView(long[] imageArray) {
        // Viewing the 1D array
        System.out.println();
        System.out.print("index: " + "\t|\t");
        for (int i = 0; i < imageArray.length; i++)
            System.out.print(i + "\t|\t");
        System.out.println();
        System.out.print("root: " + "\t|\t");
        for (long i : imageArray)
            System.out.print(getRoot(i) + "\t|\t");
        System.out.println();
    }

    // Takes in an image and returns it in black/white
    private Image toBlackAndWhite(Image sourceImage, long[] imageArray) {
        PixelReader pixelReader = sourceImage.getPixelReader();

        int width = (int) sourceImage.getWidth();
        int height = (int) sourceImage.getHeight();

        WritableImage blackWhiteImage = new WritableImage(width, height);

        for (int i = 0; i < imageArray.length; i++) {
            int x = i % width;
            int y = i / width;

            Color pixelColor = pixelReader.getColor(x, y);
            if (pixelColor.getBrightness() >= minBrightness) {
                blackWhiteImage.getPixelWriter().setColor(x, y, Color.WHITE);
                setRoot(imageArray, i, i);
            } else {
                blackWhiteImage.getPixelWriter().setColor(x, y, Color.BLACK);
                setRoot(imageArray, i, 0);
            }
        }
        return blackWhiteImage;
    }

    // Index = (row number * size of row) + column number
    // row = index/rowsize
    // column = index%rowsize
    private Image colorBWImage(Image sourceImage, long[] a) {
        minSize = Integer.parseInt(minSizeText.getText());
        maxSize = Integer.parseInt(maxSizeText.getText());

        PixelReader pixelReader = sourceImage.getPixelReader();

        int width = (int) sourceImage.getWidth();
        int height = (int) sourceImage.getHeight();

        WritableImage coloredBWImage = new WritableImage(width, height);

        for (int i = 0; i < a.length; i++) {
            int x = i % width;
            int y = i / width;

            if (isIgnored(a[i])) { // skip black pixels
                int pixel = getImagePixelColor(i, width, pixelReader);
                coloredBWImage.getPixelWriter().setArgb(x, y, pixel);
//                System.out.println("Root : " + find(a,i));
//                System.out.println("Color: " + pixel);
            }
            // Randomly colors the root
            else if (getRoot(a[i]) == i) {
                if (getSize(a[i]) < minSize && getSize(a[i]) > maxSize) {
                    setRoot(a,i,0);
                    continue;
                }

                int pixel = getImagePixelColor(i, width, pixelReader);
                int alpha = ((pixel >> 24) & 0xff);

                Random rand = new Random();
                int red = rand.nextInt(256);
                int green = rand.nextInt(256);
                int blue = rand.nextInt(256);
                int color = (alpha << 24) + (red << 16) + (green << 8) + blue;

                coloredBWImage.getPixelWriter().setArgb(x, y, color);
            }
        }

        for (int i = 0; i < a.length; i++) {
            int x = i % width;
            int y = i / width;
            // if root choose random color
            // else find root and use its color

            // Gets the root color and set the current color to it
            if (!isIgnored(a[i])) {
                int pixel = getImagePixelColor(find(a, i), width, coloredBWImage.getPixelReader());
                coloredBWImage.getPixelWriter().setArgb(x, y, pixel);
//                System.out.println("Root : " + find(a,i));
//                System.out.println("Color: " + pixel);
            }
        }
        return coloredBWImage;
    }

    //Iterative version of find with path compression and bitshifting
    public static int find(long[] a, int id) {
        while (!isIgnored(a[id]) && getRoot(a[id]) != id) {
            // TODO: Remove type casting
            setRoot(a, id, (int) getRoot(a[(int) getRoot(a[id])]));
            id = (int) getRoot(a[id]);
        }
        return id;
    }

    //Quick union of disjoint sets containing elements p and q
    //TODO: Change to union-by-size
    public static void union(long[] a, int p, int q) {
        int rootp = find(a,p);
        int rootq = find(a,q);
        setSize(a,rootp,getSize(a[rootp]) + getSize(a[rootq]));
        setSize(a,rootq,getSize(a[rootp]) + getSize(a[rootq]));
        setRoot(a, rootp, rootq); //The root of q is made reference the root of p
//        a[find(a,q)]=find(a,p);
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
    public void onBlackWhiteButtonClick(MouseEvent mouseEvent) {
        run();
    }

    @FXML
    protected void onMenuOpenImageClick() {
        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(originalImageView.getScene().getWindow());
        if (file != null) {
            openFile(file);
//            run();
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

    private void openFile(File file) {
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
        System.out.println(blackWhiteImageView.getId());
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
    }

    public void onMenuOpenRGBChannelWindow(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("rgbChannel.fxml"));
        Scene rgbChannelScene = new Scene(fxmlLoader.load(), 320, 240);
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

                int red = (int) pixelColor.getRed();
                int green = (int) pixelColor.getGreen();
                int blue = (int) pixelColor.getBlue();

                if (red > green && red > blue && color == Color.RED) {
                    colorImage.getPixelWriter().setArgb(x, y, red);
                } else if (green > red && green > blue && color == Color.GREEN) {
                    colorImage.getPixelWriter().setArgb(x, y, green);
                } else if (blue > red && blue > green && color == Color.BLUE) {
                    colorImage.getPixelWriter().setArgb(x, y, blue);
                } else {
                    colorImage.getPixelWriter().setArgb(x, y, 0);
                }
            }
        }
        return colorImage;
    }

    private int getImagePixelColor(int i, int w, PixelReader pixelReader) {
        return pixelReader.getArgb(i % w, i / w);
    }

    // First 32 bits is the root
    // Last 32 bits is the size

    //    https://stackoverflow.com/questions/1080150/getting-the-bottom-16-bits-of-a-java-int-as-a-signed-16-bit-value
    // Checks if the first 32 bits is 0
    public static boolean isIgnored(long value) {
        // checks the last bit if 1 is root otherwise not root
//        return getUsed(value) == 0;
        return getRoot(value) == 0;
    }

    // Returns the last 32 bits
    public static long getSize(long value) {
        return ((value >> 32) & 0xffffffffL);
    }

    // Sets the last 32 bits to the sets size
    public static void setSize(long[] a, int index, long value) {
        long root = (a[index] & 0xffffffffL);
        a[index] = (value << 32) + root;
    }

    // Returns the first 32 bits
    public static long getRoot(long value) {
        return (value & 0xffffffffL);
    }

    // Sets the first 32 bits to the roots index
    public static void setRoot(long[] a, int index, long value) {
        long size = ((a[index] >> 32) & 0xffffffffL);
        a[index] = (size << 32) + value;
    }

}