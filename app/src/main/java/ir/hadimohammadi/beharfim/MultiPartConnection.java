package ir.hadimohammadi.beharfim;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MultiPartConnection {


    private final String url;
    private HttpURLConnection connection;
    private OutputStream outputStream;
    private PrintWriter writer;
    public int statusCode;

    private final int BUFFER_SIZE = 1024;
    private final int TIME_OUT = 10 * 60 * 1000;
    private final String LINE_FEED = "\r\n";

    private final FileUploadListener listener;
    private final HashMap<String, File> formFiles = new HashMap<>();
    private final HashMap<String, String> formData = new HashMap<>();
    private String boundary;
    private long lastProgressUpdateTime = 0;


    public interface FileUploadListener {

        void onUpdateProgress(int percentage, int i);
        void onResponseListener(String response);
        void onErrorListener(String Error);

    }

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     */
    public MultiPartConnection(String requestURL, FileUploadListener listener) {
        this.url = requestURL;
        this.listener = listener;
    }


    /**
     * Adds a form field to the request
     *
     * @param fieldName name attribute in <input type="text" name="..." />
     * @param value     field value
     */
    public void addData(String fieldName, String value) {
        formData.put(fieldName, value);
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     */

    public void addFile(String fieldName, File uploadFile) {
        formFiles.put(fieldName, uploadFile);
    }


    private void getData() throws IOException {


        for (Map.Entry<String, String> entry : this.formData.entrySet()) {

            writer.append("--").append(boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"").append(LINE_FEED);
            writer.append("Content-Type: text/plain; charset=").append("UTF-8").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(entry.getValue()).append(LINE_FEED);
            writer.flush();

        }
    }

    private void getFiles() throws IOException {

        int i = 0;
        for (Map.Entry<String, File> entry : this.formFiles.entrySet()) {
            Log.d("f",entry.getValue().getName());
            String fieldName = entry.getKey();
            File file = entry.getValue();
            writer.append("--").append(boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"").append(fieldName)
                    .append("\"; filename=\"").append(file.getName()).append("\"").append(LINE_FEED);
            writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(file.getName())).append(LINE_FEED);
            writer.append("charset=" + "UTF-8").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            outputStream.flush();
            byte[] buffer = new byte[BUFFER_SIZE];
            final FileInputStream inputStream = new FileInputStream(file);
            long totalRead = 0;
            long totalSize = file.length();

            int read ;
            while ((read = inputStream.read(buffer)) > 0) {
                totalRead += read;
                int percentage = (int) ((totalRead / (float) totalSize) * 100);
                //if(totalRead == totalSize) percentage = 100;

                outputStream.write(buffer, 0, read);

                if (listener != null)
                    this.listener.onUpdateProgress(percentage,i);

              /*
                long now = System.currentTimeMillis();
              if (lastProgressUpdateTime == 0 || lastProgressUpdateTime < now - 100) {
                    lastProgressUpdateTime = now;
                }*/
            }
            outputStream.flush();
            writer.append(LINE_FEED);
            writer.flush();
            i++;
        }

    }

    private void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*  public void addFilePart(String fieldName, File uploadFile) {

        try {
            String fileName = uploadFile.getName();
            writer.append("--").append(boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"").append(fieldName)
                    .append("\"; filename=\"").append(fileName).append("\"").append(LINE_FEED);
            writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
            writer.append("charset=" + "UTF-8").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            outputStream.flush();
            byte[] buffer = new byte[BUFFER_SIZE];
            final FileInputStream inputStream = new FileInputStream(uploadFile);
            long totalRead = 0;
            long totalSize = uploadFile.length();

            int read;
            while ((read = inputStream.read(buffer)) > 0) {
                totalRead += read;
                int percentage = (int) ((totalRead / (float) totalSize) * 100);
                outputStream.write(buffer, 0, read);
                long now = System.currentTimeMillis();
                if (lastProgressUpdateTime == 0 || lastProgressUpdateTime < now - 100) {
                    lastProgressUpdateTime = now;
                    Log.d("", totalRead + " " + " " + percentage);
                    if (listener != null)
                        this.listener.onUpdateProgress(percentage, totalRead);
                }
            }
            outputStream.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
        writer.append(LINE_FEED);
        writer.flush();
    }
*/

    public void addHeaderField(String name, String value) {
        writer.append(name).append(": ").append(value).append(LINE_FEED);
        writer.flush();
    }

    private void prepare(){

        String responses = "";
        try {
            StringBuilder sb = new StringBuilder();
            this.boundary = String.valueOf(System.currentTimeMillis());
            URL url = new URL(this.url);
            if (url.getProtocol().equalsIgnoreCase("https")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                connection = https;
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }

            connection.setConnectTimeout(TIME_OUT);
            connection.setReadTimeout(TIME_OUT);
            connection.setUseCaches(false);
            connection.setDoOutput(true); // indicates POST method
            connection.setDoInput(true);
            connection.setChunkedStreamingMode(BUFFER_SIZE);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Connection", "Keep-Alive");
            outputStream = connection.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);

            getData();
            getFiles();


            //writer.append(LINE_FEED).flush();
            writer.append(LINE_FEED);
            writer.append("--").append(boundary).append("--").append(LINE_FEED);
            writer.flush();
            writer.close();

            statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                responses = "httpRequestError:" + statusCode;
                this.listener.onErrorListener(responses);
            } else {

                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                if (line != null) {
                    sb.append(line);
                }
                responses = sb.toString();

            }

        } catch (IOException e) {
            e.printStackTrace();
             /*
             sb = new StringBuilder();
            sb.append("" + Utility.convertStreamToString(httpConn.getErrorStream()) + "\n");
            responses = sb.toString();
            Log.v(Constants.TAG, "Error response: " + responses);
            */
        } finally {
            Log.d("multiPartConnection", "disconnected");
            if (connection != null) connection.disconnect();
            this.listener.onResponseListener(responses);
        }
    }

    public void Execute() {
        new Thread(this::prepare).start();
    }

}

