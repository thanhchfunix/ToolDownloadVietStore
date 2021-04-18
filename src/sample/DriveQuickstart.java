package sample;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriveQuickstart {

    private static final String APPLICATION_NAME = "Google Drive API Java Quickstart";

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // Directory to store user credentials for this application.
    private static final java.io.File CREDENTIALS_FOLDER //
            = new java.io.File(System.getProperty("user.home"), "credentials");

    private static final String CLIENT_SECRET_FILE_NAME = "client_secret.json";

    private static final java.io.File dirDownload = new java.io.File("download");
    //
    // Global instance of the scopes required by this quickstart. If modifying these
    // scopes, delete your previously saved credentials/ folder.
    //
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private static Drive service = null;

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        java.io.File clientSecretFilePath = new java.io.File(CREDENTIALS_FOLDER, CLIENT_SECRET_FILE_NAME);

        if (!clientSecretFilePath.exists()) {
            throw new FileNotFoundException("Please copy " + CLIENT_SECRET_FILE_NAME //
                    + " to folder: " + CREDENTIALS_FOLDER.getAbsolutePath());
        }

        // Load client secrets.

        InputStream in = new FileInputStream(clientSecretFilePath);
        //System.out.println(in);
        Reader reader = new InputStreamReader(in);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(CREDENTIALS_FOLDER))
                .setAccessType("offline").build();
        //System.out.println(3);
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static Drive getDrive() throws IOException, GeneralSecurityException {

//        System.out.println("CREDENTIALS_FOLDER: " + CREDENTIALS_FOLDER.getAbsolutePath());

        // 1: Create CREDENTIALS_FOLDER
        if (!CREDENTIALS_FOLDER.exists()) {
            CREDENTIALS_FOLDER.mkdirs();

            System.out.println("Created Folder: " + CREDENTIALS_FOLDER.getAbsolutePath());
            System.out.println("Copy file " + CLIENT_SECRET_FILE_NAME + " into folder above.. and rerun this class!!");
            return null;
        }



        // 5: Create Google Drive Service.
        if (service == null) {
            // 2: Build a new authorized API client service.
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            // 3: Read client_secret.json file & create Credential object.
            Credential credential = getCredentials(HTTP_TRANSPORT);
            service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential) //
                    .setApplicationName(APPLICATION_NAME).build();
        }


        // Print the names and IDs for up to 10 files.
//        FileList result = service.files().list().setPageSize(10).setFields("nextPageToken, files(id, name)").execute();
//        List<File> files = result.getFiles();
//        if (files == null || files.isEmpty()) {
//            System.out.println("No files found.");
//        } else {
//            System.out.println("Files:");
//            for (File file : files) {
//                System.out.printf("%s (%s)\n", file.getName(), file.getId());
//            }
//        }
        return service;
    }

    public static void downloadLink(String diriveId, String folderOrderName) throws GeneralSecurityException, IOException {
        System.out.println(folderOrderName);
        java.io.File fileSave = new java.io.File(dirDownload, folderOrderName + ".jpg");
        Drive driveService = getDrive();
        String fileId = diriveId;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileOutputStream os = null;
        try {
            driveService.files().get(fileId)
                    .executeMediaAndDownloadTo(byteArrayOutputStream);
            os = new FileOutputStream(fileSave);
            byteArrayOutputStream.writeTo(os);
//            byteArrayOutputStream.w
            //System.out.println(byteArrayOutputStream);
            if (os != null) {
                os.close();
            }
        } catch (HttpResponseException e) {
            java.io.File dirDesign = new java.io.File(dirDownload, folderOrderName);
            System.out.println(diriveId + " !! " + folderOrderName);
            if (!dirDesign.exists()) {
                dirDesign.mkdirs();
            }
            List<File> files = getFileInParrentFolders(diriveId);
            java.io.File fComponent = null;
            for (File f : files) {
                try {
                    driveService = getDrive();
                    fComponent = new java.io.File(dirDesign, f.getName());
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    System.out.println("Download component: " + f.getId());

                    driveService.files().get(f.getId())
                            .executeMediaAndDownloadTo(byteArrayOutputStream);
                    os = new FileOutputStream(fComponent);
                    byteArrayOutputStream.writeTo(os);

//                    f = driveService.files().get(f.getId()).execute();
//                    if (f.getMimeType().equals("image/jpg") || f.getMimeType().equals("image/png")) {
//                        driveService.files().get(f.getId())
//                                .executeMediaAndDownloadTo(byteArrayOutputStream);
//                        os = new FileOutputStream(fComponent);
//                        byteArrayOutputStream.writeTo(os);
//                    }
                    if (os != null) {
                        os.close();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
            System.out.println("1977_" + dirDesign.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (os != null) {
                os.close();
            }

        }

    }

    public static void downloadCache(String diriveId, java.io.File cache, String folderOrderName) throws GeneralSecurityException, IOException {
        System.out.println(folderOrderName);
        java.io.File fileSave = new java.io.File(cache, folderOrderName + ".jpg");
        Drive driveService = getDrive();
        String fileId = diriveId;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileOutputStream os = null;
        try {
            driveService.files().get(fileId)
                    .executeMediaAndDownloadTo(byteArrayOutputStream);
            os = new FileOutputStream(fileSave);
            byteArrayOutputStream.writeTo(os);
//            byteArrayOutputStream.w
            //System.out.println(byteArrayOutputStream);
            if (os != null) {
                os.close();
            }
        } catch (HttpResponseException e) {
            java.io.File dirDesign = new java.io.File(dirDownload, folderOrderName);
            System.out.println(diriveId + " !! " + folderOrderName);
            if (!dirDesign.exists()) {
                dirDesign.mkdirs();
            }
            List<File> files = getFileInParrentFolders(diriveId);
            java.io.File fComponent = null;
            for (File f : files) {
                try {
                    driveService = getDrive();
                    fComponent = new java.io.File(dirDesign, f.getName());
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    System.out.println("Download component: " + f.getId());
                    driveService.files().get(f.getId())
                            .executeMediaAndDownloadTo(byteArrayOutputStream);
                    os = new FileOutputStream(fComponent);
                    byteArrayOutputStream.writeTo(os);
                    if (os != null) {
                        os.close();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
            System.out.println("1977_" + dirDesign.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (os != null) {
                os.close();
            }

        }

    }

    // com.google.api.services.drive.model.File
    public static final List<File> getFileInParrentFolders(String googleFolderIdParent) throws IOException {

        Drive driveService = null;
        try {
            driveService = getDrive();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        String pageToken = null;
        List<File> list = new ArrayList<File>();

        String query = null;
        if (googleFolderIdParent == null) {
            query = " mimeType = 'application/vnd.google-apps.folder' " //
                    + " and 'root' in parents";
        } else {
            query = " mimeType != 'application/vnd.google-apps.folder' " //
                    + " and '" + googleFolderIdParent + "' in parents";
        }

        do {
            FileList result = driveService.files().list().setQ(query).setSpaces("drive") //
                    // Fields will be assigned values: id, name, createdTime
                    .setFields("nextPageToken, files(id, name, createdTime)")//
                    .setPageToken(pageToken).execute();
            for (File file : result.getFiles()) {
                list.add(file);
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        //
        System.out.println(query);
        return list;
    }

    // com.google.api.services.drive.model.File
    public static final List<File> getGoogleRootFolders() throws IOException {
        return getFileInParrentFolders(null);
    }

//    public static void main(String[] args) {
//        try {
//            getDrive();
//            downloadLink("1vDTzb886U_2lwXY8r9RdVtsmy-NWE55H", "mockup");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        }
//    }
}