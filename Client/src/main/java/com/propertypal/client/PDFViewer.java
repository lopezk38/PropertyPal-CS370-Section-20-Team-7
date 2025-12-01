package com.propertypal.client;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PDFViewer
{
    public static void openPDF(String filepath) throws Exception
    {
        File file = new File(filepath);
        if (!file.exists())
            throw new Exception("File not found: " + filepath);

        PDDocument document = PDDocument.load(file);
        PDFRenderer renderer = new PDFRenderer(document);

        VBox pagesBox = new VBox(20);
        pagesBox.setStyle("-fx-padding: 20; -fx-background-color: white;");

        for (int i = 0; i < document.getNumberOfPages(); i++)
        {
            BufferedImage awtImage = renderer.renderImageWithDPI(i, 130);
            javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(awtImage, null);
            ImageView imageView = new ImageView(fxImage);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(800);

            pagesBox.getChildren().add(imageView);
        }

        document.close();

        ScrollPane scroll = new ScrollPane(pagesBox);
        scroll.setFitToWidth(true);

        Stage viewer = new Stage();
        viewer.setTitle("PDF Viewer");
        viewer.setScene(new Scene(scroll, 900, 1000));
        viewer.show();
    }
}