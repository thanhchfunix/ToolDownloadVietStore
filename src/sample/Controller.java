package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

public class Controller implements Initializable {

    @FXML
    public TextArea taLink, taSize, taCode;

    @FXML
    public TextField tfKeyWord;

    @FXML
    public ProgressBar pgTask;

    private Set<String> setCache = new HashSet<>();

    private String[] links, sizes, codes;

    double count = 0;
    double max = 0;

    private File cache;

    private File dirDownload = new File("download");

    @FXML
    public void onDownload(ActionEvent actionEvent) {

        Task<Void> task = new Task() {
            @Override
            protected Object call() throws Exception {
                String sLink = taLink.getText();
                String sSize = taSize.getText();
                String sCode = taCode.getText();

                links = sLink.split("\n");
                sizes = sSize.split("\n");
                codes = sCode.split("\n");
                max = links.length;
                count = 0;
                try {
                    if (links.length != codes.length) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setContentText("Dữ liệu nhập vào k trùng khớp giữa các cột");
                        alert.showAndWait();
                    } else {


                        for (int i = 0; i < links.length; i++) {
                            String link = links[i].replace("https://drive.google.com/open?id=", "")
                                    .replace("https://drive.google.com/file/d/", "")
                                    .replace("/view?usp=sharing", "")
                                    .replace("https://drive.google.com/drive/folders/", "")
                                    .replace("?usp=sharing", "")
                                    .replace("https://drive.google.com/drive/u/0/folders/", "")
                                    .replace("https://drive.google.com/drive/u/1/folders/", "")
                                    .replace("https://drive.google.com/drive/u/2/folders/", "")
                                    .replace("https://drive.google.com/drive/u/3/folders/", "")
                                    .replace("https://drive.google.com/drive/u/4/folders/", "")
                                    .replace("https://drive.google.com/drive/u/5/folders/", "")
                                    .replace("https://drive.google.com/drive/u/6/folders/", "")
                                    .replace("https://drive.google.com/drive/u/7/folders/", "")
                                    .replace("https://drive.google.com/drive/u/8/folders/", "").trim();
                            System.out.println(link);
                            String code = codes[i].trim();
                            File[] files = cache.listFiles();
                            boolean found = false;
                            if (sSize.length() > 0) {
                                String size = sizes[i].trim();
                                File fileFound = null;
                                for (int j = 0; j < files.length; j++) {
                                    File file = files[j];
                                    System.out.println(file.getName());
                                    if (file.getName().contains(link)) {
                                        System.err.println("found");
                                        fileFound = file;
                                        found = true;
                                        break;
                                    }
                                }
                                if (found) {
                                    File dirCode = new File(dirDownload, size + "_" + code);
                                    dirCode.mkdirs();
                                    File fCode = new File(dirCode, fileFound.getName());
                                    Files.copy(fileFound.toPath(), fCode.toPath());
                                } else {
                                    DriveQuickstart.downloadLink(link,size + "_" + code);
                                }

                            } else {
                                //String size = sizes[i].trim();
                                File fileFound = null;
                                for (int j = 0; j < files.length; j++) {
                                    File file = files[j];
                                    if (file.getName().contains(link)) {
                                        System.err.println("found");
                                        fileFound = file;
                                        found = true;
                                        break;
                                    }
                                }
                                if (found) {
                                    File dirCode = new File(dirDownload, code);
                                    dirCode.mkdirs();
                                    File fCode = new File(dirCode, fileFound.getName());
                                    if (fCode != null) {
                                        Files.copy(fileFound.toPath(), fCode.toPath());
                                    }

                                } else {
                                    DriveQuickstart.downloadLink(link, code);
                                }

                            }


                            //downloadWithJar(size + "_" + code + "_" + link);

                            count += 1;
                            Platform.runLater(() -> {
                                pgTask.setProgress(count/max);
                                if (count == max) {
                                    pgTask.setProgress(1);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    public void onSort(ActionEvent actionEvent) {
    }

    @FXML
    public void onAdd(ActionEvent actionEvent) {
        Task<Void> task = new Task() {
            @Override
            protected Object call() throws Exception {
                String sLink = taLink.getText();
                String sSize = taSize.getText();
                String sCode = taCode.getText();

                links = sLink.split("\n");

                max = links.length;
                count = 0;
                try {

                        for (int i = 0; i < links.length; i++) {
                            String link = links[i].replace("https://drive.google.com/open?id=", "")
                                    .replace("https://drive.google.com/file/d/", "")
                                    .replace("/view?usp=sharing", "")
                                    .replace("https://drive.google.com/drive/folders/", "")
                                    .replace("?usp=sharing", "")
                                    .replace("https://drive.google.com/drive/u/0/folders/", "")
                                    .replace("https://drive.google.com/drive/u/1/folders/", "")
                                    .replace("https://drive.google.com/drive/u/2/folders/", "")
                                    .replace("https://drive.google.com/drive/u/3/folders/", "")
                                    .replace("https://drive.google.com/drive/u/4/folders/", "")
                                    .replace("https://drive.google.com/drive/u/5/folders/", "")
                                    .replace("https://drive.google.com/drive/u/6/folders/", "")
                                    .replace("https://drive.google.com/drive/u/7/folders/", "")
                                    .replace("https://drive.google.com/drive/u/8/folders/", "").trim();

                            setCache.add(link);
                        }

                        Iterator<String> iterator = setCache.iterator();

                        while (iterator.hasNext()) {
                            String id = iterator.next();
                            File[] files = cache.listFiles();
                            boolean found = false;
                            for (int i = 0; i < files.length; i++) {
                                if (files[i].getName().contains(id)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                DriveQuickstart.downloadCache(id, cache, id);
                            }

                        }
                    count += 1;
                    Platform.runLater(() -> {
                        pgTask.setProgress(count/max);
                        if (count == max) {
                            pgTask.setProgress(1);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
        new Thread(task).start();
    }

    @FXML
    public void onBlock(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cache = new File("cache");
        if (!cache.exists()) {
            cache.mkdirs();
        }
        if (!dirDownload.exists()) {
            dirDownload.mkdirs();
        }
    }
}
