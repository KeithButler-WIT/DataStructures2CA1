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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import static java.lang.Math.abs;

public class Controller {
    public TextField minSizeText;
    public TextField maxSizeText; // Integer.parseInt(maxSize.getText);
    public AnchorPane imageViewBox;
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
        int[] imageArray = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y*width) + x;
                // Sets every pixel to its own disjoint set on startup
                setRoot(imageArray, index, index);
                // Initialize all sets to a size of 1
                setSize(imageArray, index, 1);
                // Initialize all sets to not be ignored - overwrite later
                setUsed(imageArray, index,1);
//                imageArray[(y*width) + x] = (y*width) + x;
            }
        }

        imageArrayView(imageArray);
        Image blackWhiteImage = toBlackAndWhite(originalImage, imageArray);
        grayscaleImageView.setImage(blackWhiteImage);

        imageArrayView(imageArray);
        disjointSet(imageArray);
        imageArrayView(imageArray);
        grayscaleImageView.setImage(colorBWImage(blackWhiteImage, imageArray));

//        int numSets = countSets(imageArray);
//        drawCircles(numSets, imageArray, knownRoots);
    }

    // Index = (row number * size of row) + column number
    // (y) row = index/rowsize
    // (x) column = index%rowsize
    //TODO: use bitshift to allow the id to hold size/height
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

            int currentIndex = abs(((i / width)* width) + (i % width));
            int topLeftIndex = abs(((((i - width) / width) * width) + ((i - 1) % width)));
            int topIndex = abs(((i - width) / width) * width + (i % width));
            int topRightIndex = abs(((i - width) / width) * width + ((i + 1) % width));
            int leftIndex = abs((((i / width) * width) + ((i - 1) % width)));

            PixelReader pixelReader = originalImage.getPixelReader();
            double pixelBrightness = pixelReader
                    .getColor(i%width,i/width)
                    .getBrightness();

            //top left adjacent index
            if (!isIgnored(imageArray[topLeftIndex])) {
//                imageArray[currentIndex] = topLeftIndex;
//                System.out.println("Top Left Index: " + topLeftIndex);
                setRoot(imageArray,currentIndex,topLeftIndex);
            }
            //top adjacent index
            if (!isIgnored(imageArray[topIndex])) {
//            else if (imageArray[topIndex] > 0) {
//                imageArray[currentIndex] = topIndex;
                setRoot(imageArray,currentIndex,topIndex);
            }
            //top right adjacent index
            if (!isIgnored(imageArray[topRightIndex])) {
//            else if (imageArray[topRightIndex] > 0) {
//                imageArray[currentIndex] = topRightIndex;
                setRoot(imageArray,currentIndex,topRightIndex);
            }
            //left adjacent index
            if (!isIgnored(imageArray[leftIndex])) {
//            else if (imageArray[leftIndex] > 0) {
//                imageArray[currentIndex] = leftIndex;
                setRoot(imageArray,currentIndex,leftIndex);
            }
        }

        // Union
        for (int i = 0; i < imageArray.length; i++) {
            // skip to next pixel if node is set to ignored
            if (isIgnored(imageArray[i])) {
                continue;
            }

            int currentIndex = abs(((i / width)* width) + (i % width));
            int topIndex = abs(((i - width) / width) * width + (i % width));
            int bottomIndex = abs(((i + width) / width) * width + (i % width));
            int leftIndex = abs((((i / width) * width) + ((i - 1) % width)));
            int rightIndex = abs((((i / width) * width) + ((i + 1) % width)));

            //top adjacent index
            if (topIndex/width > 0 && !isIgnored(imageArray[topIndex])) {
                union(imageArray,topIndex,currentIndex);
//                union(imageArray,currentIndex,topIndex);
//                System.out.println("top union");
            }
            //left adjacent index
//            else if (getRoot(imageArray[leftIndex]) > 0) {
            else if (leftIndex%width > 0 && !isIgnored(imageArray[leftIndex])) {
                union(imageArray,leftIndex,currentIndex);
//                union(imageArray,currentIndex,leftIndex);
//                System.out.println("left union");
            }
            //right adjacent index
//            else if (getRoot(imageArray[rightIndex]) > 0) {
            else if (rightIndex%width < width && !isIgnored(imageArray[rightIndex])) {
                union(imageArray,rightIndex,currentIndex);
//                union(imageArray,currentIndex,rightIndex);
//                System.out.println("right union");
            }
            //bottom adjacent index
//            else if (getRoot(imageArray[bottomIndex]) == -1) {
            else if (bottomIndex/width < height && !isIgnored(imageArray[bottomIndex])) {
                union(imageArray,bottomIndex,currentIndex);
//                union(imageArray,currentIndex,bottomIndex);
//                System.out.println("bottom union");
            }
        }
    }

    private int countSets(int[] imageArray) {
        int numSets = 0;
        ArrayList<Integer> knownRoots = new ArrayList<>();
//         Counts the total number of sets in the image
        for (int i = 0; i < imageArray.length; i++) {
//            TODO: Count total number of sets
            if (isIgnored(imageArray[i])) {
                continue;
            }
//            if (getSize(i) == 1) {
//                continue;
//            }

            int currentRoot = find(imageArray,i);
            if (!knownRoots.contains(currentRoot)) {
                knownRoots.add(currentRoot);
                numSets++;
            }
        }
        System.out.println(numSets);
        return numSets;
    }

    //TODO: Finish drawCircles
    private void drawCircles(int numSets, int[] imageArray, ArrayList<Integer> knownRoots) {
        // Index = (row number * size of row) + column number
        // (y) row = index/rowsize
        // (x) column = index%rowsize

        //TODO: draw circles around each disjoint set

        int width = (int) originalImage.getWidth();

        // loop through numSets and imageArray
        for (int i = 0; i < numSets; i++) {
            // need to know outer limits
            int topLeft = 0;
            int topRight = 0;
            int bottomLeft = 0;
            int bottomRight = 0;

            int currentCircle = knownRoots.get(i);
            for (int j = 0; j < imageArray.length; j++) {
//                if (imageArray[j] == -1) {
                if (isIgnored(imageArray[j])) {
                    continue;
                }

                int currentRoot = find(imageArray,j);
                if (currentRoot != currentCircle) {
                    continue;
                }

//                int x = j%width;
//                int y = j/width;

                int currentIndex = abs(((i / width)* width) + (i % width));
                int topIndex = abs(((i - width) / width) * width + (i % width));
                int bottomIndex = abs(((i + width) / width) * width + (i % width));
                int leftIndex = abs((((i / width) * width) + ((i - 1) % width)));
                int rightIndex = abs((((i / width) * width) + ((i + 1) % width)));

                //top left index
                if (imageArray[topIndex] <= 0 && imageArray[leftIndex] <= 0) {
                    topLeft = j;
                }
                //top right index
                else if (imageArray[topIndex] <= 0 && imageArray[rightIndex] <= 0) {
                    topRight = j;
                }
                //bottom left index
                else if (imageArray[leftIndex] <= 0 && imageArray[bottomIndex] <= 0) {
                    bottomLeft = j;
                }
                //bottom right index
                else if (imageArray[rightIndex] <= 0 && imageArray[bottomIndex] <= 0) {
                    bottomRight = j;
                }
            }
            Circle circle = new Circle();
//            circle.setCenterX((bottomRight%width) - (topLeft%width));
//            circle.setCenterY((bottomRight/width) - (topLeft/width));
//            int center = (((topLeft%width) + (bottomRight%width))/2) * (((topLeft/width) + (bottomRight/width))/2);
//            int length = (((topLeft%width) + (bottomRight%width))^2) + (((topLeft/width) + (bottomRight/width))^2);
//            circle.setCenterX(((topLeft%width) + (bottomRight%width))/2);
//            circle.setCenterY(((topLeft/width) + (bottomRight/width))/2);
//            circle.setRadius(bottomLeft - topLeft);
            circle.setCenterX(100);
            circle.setCenterY(100);
            circle.setRadius(100);
            circle.setFill(null);
            circle.setStroke(Color.BLUE);
            imageViewBox.getChildren().add(circle);
        }
    }

    //TODO: labelCircles

    private void imageArrayView(int[] imageArray) {
        // Viewing the 1D array
        System.out.println();
        System.out.print("index: "  + "\t|\t");
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
            int x = i%width;
            int y = i/width;

            Color pixelColor = pixelReader.getColor(x, y);
            if (pixelColor.getBrightness() >= minBrightness) {
//                grayImage.getPixelWriter().setArgb(x, y, -1);
                grayImage.getPixelWriter().setColor(x,y,Color.WHITE);
                setUsed(imageArray, i, 1);
            }
            else {
//                grayImage.getPixelWriter().setArgb(x, y, -16777216);
                grayImage.getPixelWriter().setColor(x, y, Color.BLACK);
                setUsed(imageArray, i, 0);
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
            int x = i%width;
            int y = i/width;

            if (isIgnored(a[i])){
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
            int x = i%width;
            int y = i/width;
            // if root choose random color
            // else find root and use its color

            // Gets the root color and set the current color to it
            if (!isIgnored(a[i])) {
                int pixel = getImagePixelColor(find(a,i), width, coloredBWImage.getPixelReader());
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
        while(!isIgnored(a[id]) && getRoot(a[id])!=id) {
            setRoot(a,id,getRoot(a[getRoot(a[id])]));
            id=getRoot(a[id]);
        }
        return id;
    }
    //Quick union of disjoint sets containing elements p and q
    //TODO: Change to union-by-size
    public static void union(int[] a, int p, int q) {
//        int root = find(a,q);
//        setRoot(a,find(a,q),find(a,p));
        setRoot(a,find(a,q),find(a,p));
//        setSize(a,root,getSize(root) + getSize(a[find(a,p)]));
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

    private int getImagePixelColor (int i, int w, PixelReader pixelReader) {
        return pixelReader.getArgb(i%w, i/w);
    }


    // First 16 bits is the root
    // Next 15 bits is the size
    // last 1 bit is if the set is ignored

//    https://stackoverflow.com/questions/1080150/getting-the-bottom-16-bits-of-a-java-int-as-a-signed-16-bit-value
    //TODO: 0xff only gets 8 bits
    private static boolean isIgnored(int value) {
        // checks the last bit if 1 is root otherwise not root
        return getUsed(value) == 0;
    }
    private static int getUsed(int value) {
        // checks the last bit if 1 is root otherwise not root
        return ((value >> 31) & 0xff);
//        return (value & 0x01);
    }
    private static void setUsed(int[] a, int index, int value) {
        int root = (a[index] & 0xffff);
        int size = ((a[index] >> 16) & 0xff);
//        int size = (((a[index] >> 16) >> 31) & 0xff);
//        int used = ((value >> 31) & 0xff);
        a[index] = (value << 31) + (size << 16) + root;
    }
    private static int getSize(int value) {
        return ((value >> 16) & 0xff);
    }
    private static void setSize(int[] a, int index, int value) {
        int root = (a[index] & 0xffff);
//        int size = ((value >> 16) & 0xff);
        int used = ((a[index] >> 31) & 0xff);
        a[index] = (used << 31) + (value << 16) + root;
    }
    private static int getRoot(int value) {
        return (value & 0xffff);
    }
    private static void setRoot(int[] a, int index, int value) {
//        int root = (value & 0xff);
        int size = ((a[index] >> 16) & 0xff);
        int used = ((a[index] >> 31) & 0xff);
        a[index] = (used << 31) + (size << 16) + value;
    }

}