import redis.clients.jedis.Jedis;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    private static String ACCESS_KEY = "1d46c99540d44ac2a49a4b8b8bfec739";
    private static String APIFLASH_ENDPOINT = "https://api.apiflash.com/v1/urltoimage";
    private static ArrayList <String> seed_list = new ArrayList<String>();
    public static void main(String[] args) throws Exception {

        Jedis jedis = new Jedis("localhost");
        jedis.set("foo", "bar");
        String value = jedis.get("foo");

        System.out.println(value);
        System.out.println("Redis is running...");

        System.out.println("Welcome to ScreenShot Service.");

        //todo: Solve duplicate weblink case later to optimize execution time. Idea: Multilayer Bloom Filter
        read_seed_list();

        //case: no weblink in the file
        //case: only 1 link in the file
        //case: small amount of links in the file
        //case: huge amount of links in the file
        if (seed_list.size()<4000){
            for (String link: seed_list){
                //getImg("https://google.com");
                getImg(link);

                //Store in a DB or HashMap or Arraylist
            }
        }

        System.out.println("Operation done, All screenshot taken and saved in img folder");
        System.out.println("You can search the screen shot by typing the full url here.(e.g. https://google.com)");

    }

    private static void getImg(String weblink) throws Exception{
        URL url = new URL(String.format("%s?access_key=%s&url=%s", APIFLASH_ENDPOINT, ACCESS_KEY, weblink));
        InputStream inputStream = url.openStream();

        String parsedWeblink = weblink.replaceAll("https://","");
        String fileName = String.format("imgs/%s.jpeg", parsedWeblink.replaceAll("/","_"));

        System.out.println(fileName);
        OutputStream outputStream = new FileOutputStream(fileName);

        byte[] b = new byte[2048];
        int length;

        while ((length = inputStream.read(b)) != -1) {
            outputStream.write(b, 0, length);
        }

        inputStream.close();
        outputStream.close();
    }

    //Convert the text file into an Arraylist for further processing
    private static void read_seed_list(){
        String filename = "seed-list.csv";
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            int i = 0;
            while((line = br.readLine()) != null){
                seed_list.add(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}