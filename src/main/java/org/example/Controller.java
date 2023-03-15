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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class Controller {
    public TextField minSizeText;
    public TextField maxSizeText; // Integer.parseInt(maxSize.getText);
    public Pane imageViewBox;
    public Label totalNumSets;
    private int minSize;
    private int maxSize;
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
    //TODO: this
    private double minBrightness = 0.3;

    @FXML
    private void initialize() {
//        originalImage = new Image("file:/home/keith/workspace/college/Semester%204%20Repeat/Assignment01/src/main/resources/org/example/TestImage16x16.png");
//        originalImage =  new Image("file:/home/keith/workspace/college/Semester%204%20Repeat/Assignment01/src/main/resources/org/example/TestImage128x64.png");
//        originalImageView.setImage(originalImage);
//
//        run();
    }

    private void run() {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        // Saving the image to a 1D array
        int[] imageArray = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width) + x;
                // Sets every pixel to its own disjoint set on startup
                setRoot(imageArray, index, index);
                // Initialize all sets to a size of 1
                setSize(imageArray, index, 1);
                // Initialize all sets to not be ignored - overwrite later
//                setUsed(imageArray, index,1);
//                imageArray[(y*width) + x] = (y*width) + x;
            }
        }

        imageArrayView(imageArray);
        Image blackWhiteImage = toBlackAndWhite(originalImage, imageArray);
        grayscaleImageView.setImage(blackWhiteImage);

//        imageArrayView(imageArray);
        disjointSet(imageArray);
//        imageArrayView(imageArray);
        grayscaleImageView.setImage(colorBWImage(blackWhiteImage, imageArray));

        ArrayList<Integer> knownRoots = new ArrayList<>();
        int numSets = countSets(imageArray, knownRoots);
        drawCircles(numSets, imageArray, knownRoots);
    }

    // Index = (row number * size of row) + column number
    // (y) row = index/rowsize
    // (x) column = index%rowsize
    private void disjointSet(int[] imageArray) {
        minSize = Integer.parseInt(minSizeText.getText());
        maxSize = Integer.parseInt(maxSizeText.getText());

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

            //top left adjacent index
            if (!isIgnored(imageArray[topLeftIndex])) {
                setRoot(imageArray, currentIndex, topLeftIndex);
//                setSize(imageArray,find(imageArray,topLeftIndex), getSize(find(imageArray, topLeftIndex))+1);
//                setSize(imageArray,topLeftIndex, getSize(find(imageArray, topLeftIndex))+1);
            }
            //top adjacent index
            if (!isIgnored(imageArray[topIndex])) {
                setRoot(imageArray, currentIndex, topIndex);
//                setSize(imageArray,find(imageArray,topIndex), getSize(find(imageArray, topIndex))+1);
//                setSize(imageArray,topIndex, getSize(find(imageArray, topIndex))+1);
            }
            //top right adjacent index
            if (!isIgnored(imageArray[topRightIndex])) {
                setRoot(imageArray, currentIndex, topRightIndex);
//                setSize(imageArray,find(imageArray, topRightIndex), getSize(find(imageArray, topRightIndex))+1);
//                setSize(imageArray, topRightIndex, getSize(find(imageArray, topRightIndex))+1);
            }
            //left adjacent index
            if (!isIgnored(imageArray[leftIndex])) {
                setRoot(imageArray, currentIndex, leftIndex);
//                setSize(imageArray,find(imageArray,leftIndex), getSize(find(imageArray, leftIndex))+1);
//                setSize(imageArray, leftIndex, getSize(find(imageArray, leftIndex))+1);
            }
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

    private int countSets(int[] imageArray, ArrayList<Integer> knownRoots) {
        int numSets = 0;
//         Counts the total number of sets in the image
        for (int i = 0; i < imageArray.length; i++) {
            if (isIgnored(imageArray[i])) {
                continue;
            }
//            if (getSize(i) == 1) {
//                continue;
//            }

            int currentRoot = find(imageArray, i);
            if (!knownRoots.contains(currentRoot)) {
                knownRoots.add(currentRoot);
                numSets++;
            }
        }
        System.out.println(numSets);
        totalNumSets.setText(String.valueOf(numSets));
        return numSets;
    }

    private void drawCircles(int numSets, int[] imageArray, ArrayList<Integer> knownRoots) {
        // Index = (row number * size of row) + column number
        // (y) row = index/rowsize
        // (x) column = index%rowsize

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

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
                // Set minY
                if (y < minY) {
                    minY = y;
//                    System.out.println(minY);
                }
                // Set maxX
                if (x > maxX) {
                    maxX = x;
//                    System.out.println("maxX: " + maxX);
                }
                // Set maxY
                if (y > maxY) {
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
//            System.out.println("Pane Width:  " + paneWidth);
//            System.out.println("Pane Height: " + paneHeight);
//            double paneWidth = originalImageView.getLayoutBounds().getWidth();
//            double paneHeight = originalImageView.getLayoutBounds().getHeight();
            Circle circle = new Circle();
            // Pythagoras Theorem
            circle.setCenterX(((minX + maxX) / 2f) * (paneWidth / width));
            circle.setCenterY(((minY + maxY) / 2f) * (paneHeight / height));
            // Length formula using the outer bounds of the circle
            double length = sqrt(((maxX - minX) ^ 2) + ((maxY - minY) ^ 2));
            circle.setRadius((length) * (paneHeight / height));
            circle.setFill(null);
            circle.setStroke(Color.BLUE);
            imageViewBox.getChildren().add(circle);

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
                    +"Estimated Oxygen: "+totalBlue));
        }
    }

    private void imageArrayView(int[] imageArray) {
        // Viewing the 1D array
        System.out.println();
        System.out.print("index: " + "\t|\t");
        for (int i = 0; i < imageArray.length; i++)
            System.out.print(i + "\t|\t");
        System.out.println();
        System.out.print("root: " + "\t|\t");
        for (int i : imageArray)
            System.out.print(getRoot(i) + "\t|\t");
        System.out.println();
    }

    // Takes in an image and returns it in black/white
    private Image toBlackAndWhite(Image sourceImage, int[] imageArray) {
        PixelReader pixelReader = sourceImage.getPixelReader();

        int width = (int) sourceImage.getWidth();
        int height = (int) sourceImage.getHeight();

        WritableImage grayImage = new WritableImage(width, height);

        for (int i = 0; i < imageArray.length; i++) {
            int x = i % width;
            int y = i / width;

            Color pixelColor = pixelReader.getColor(x, y);
            if (pixelColor.getBrightness() >= minBrightness) {
//                grayImage.getPixelWriter().setArgb(x, y, -1);
                grayImage.getPixelWriter().setColor(x, y, Color.WHITE);
                setRoot(imageArray, i, i);
            } else {
//                grayImage.getPixelWriter().setArgb(x, y, -16777216);
                grayImage.getPixelWriter().setColor(x, y, Color.BLACK);
                setRoot(imageArray, i, 0);
            }
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
            int x = i % width;
            int y = i / width;

            if (isIgnored(a[i])) {
                int pixel = getImagePixelColor(i, width, pixelReader);
                coloredBWImage.getPixelWriter().setArgb(x, y, pixel);
//                System.out.println("Root : " + find(a,i));
//                System.out.println("Color: " + pixel);
            }
            // Randomly colors the root
            else if (getRoot(a[i]) == i) {
                int pixel = getImagePixelColor(i, width, pixelReader);
                int alpha = ((pixel >> 24) & 0xff);

                Random rand = new Random();
                int red = rand.nextInt(256);
                int green = rand.nextInt(256);
                int blue = rand.nextInt(256);
                int color = (alpha << 24) + (red << 16) + (green << 8) + blue;
//                System.out.println("Root : " + find(a,i));
//                System.out.println("Size: " + getSize(i));
//                System.out.println("Use: " + getUsed(i));
//                System.out.println("Color: " + color);

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

    //Iterative version of find with path compression
//    public static int find(int[] a, int id) {
//        while(id!=-1 && a[id]!=id) {
//            a[id]=a[a[id]]; //Compress path
//            id=a[id];
//        }
//        return id;
//    }
    // Find with bitshifting
    public static int find(int[] a, int id) {
        while (!isIgnored(a[id]) && getRoot(a[id]) != id) {
            setRoot(a, id, getRoot(a[getRoot(a[id])]));
            id = getRoot(a[id]);
        }
        return id;
    }

    //Quick union of disjoint sets containing elements p and q
    //TODO: Change to union-by-size
    public static void union(int[] a, int p, int q) {
        int root1 = find(a,q);
        int root2 = find(a,p);
        setSize(a,root1,getSize(root1) + getSize(a[root2]));
        setRoot(a, root1, root2);
//        a[find(a,q)]=find(a,p); //The root of q is made reference the root of p
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
        run();
    }

    @FXML
    protected void onMenuOpenImageClick() {
        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(originalImageView.getScene().getWindow());
        if (file != null) {
            openFile(file);
            run();
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

    private static int getX(int value, int width) {
        return value % width;
    }

    private static int getY(int value, int width) {
        return value / width;
    }

    // First 16 bits is the root
    // Next 15 bits is the size
    // last 1 bit is if the set is ignored

    //    https://stackoverflow.com/questions/1080150/getting-the-bottom-16-bits-of-a-java-int-as-a-signed-16-bit-value
    // Checks if the first 16 bits is 0
    private static boolean isIgnored(int value) {
        // checks the last bit if 1 is root otherwise not root
//        return getUsed(value) == 0;
        return getRoot(value) == 0;
    }

    // Returns the last 16 bits
    private static int getSize(int value) {
        return ((value >> 16) & 0xffff);
    }

    // Sets the first 16 bits to the sets size
    private static void setSize(int[] a, int index, int value) {
        int root = (a[index] & 0xffff);
        a[index] = (value << 16) + root;
    }

    // Returns the first 16 bits
    private static int getRoot(int value) {
        return (value & 0xffff);
    }

    // Sets the first 16 bits to the roots index
    private static void setRoot(int[] a, int index, int value) {
        int size = ((a[index] >> 16) & 0xffff);
        a[index] = (size << 16) + value;
    }

}