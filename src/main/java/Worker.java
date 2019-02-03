import redis.clients.jedis.Jedis;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class Worker implements Runnable {

    private String link;
    private Jedis jedis;
    private static String ACCESS_KEY = "1d46c99540d44ac2a49a4b8b8bfec739";
    private static String APIFLASH_ENDPOINT = "https://api.apiflash.com/v1/urltoimage";

    public Worker( String link, Jedis jedis){
        this.link=link;
        this.jedis=jedis;
    }

    private static String filePath(String link){
        String parsedWeblink = link.replaceAll("https://","");
        String fileName = String.format("imgs/%s.jpeg", parsedWeblink.replaceAll("/","_"));
        return fileName;
    }

    public void run() {
        try {
            // if key doesn't not exist, we don't have the img in our database.  time to get it
            // otherwise we don't need to do any action.
            if (!jedis.exists(link)) {

                URL url = new URL(String.format("%s?access_key=%s&url=%s", APIFLASH_ENDPOINT, ACCESS_KEY, link));
                InputStream inputStream = url.openStream();

                String fileName = filePath(link);

                System.out.println("image found in" + fileName);
                OutputStream outputStream = new FileOutputStream(fileName);

                byte[] b = new byte[2048];
                int length;

                while ((length = inputStream.read(b)) != -1) {
                    outputStream.write(b, 0, length);
                }

                inputStream.close();
                outputStream.close();

                jedis.set(link, filePath(link));

            }
        }catch (Exception err){
            err.printStackTrace();
        }

    }
}
