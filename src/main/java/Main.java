import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static String ACCESS_KEY = "1d46c99540d44ac2a49a4b8b8bfec739";
    private static String APIFLASH_ENDPOINT = "https://api.apiflash.com/v1/urltoimage";
    private static ArrayList <String> seed_list = new ArrayList<String>();
    private static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

    public static void main(String[] args) throws Exception {

        //testing Jedis
        //Jedis jedis = new Jedis("localhost");
        //jedis.set("foo", "bar");
        //String value = jedis.get("foo");

        //System.out.println(value);
        System.out.println("Redis is running...");

        System.out.println("Welcome to ScreenShot Service.");

        //todo: Solve duplicate weblink case later to optimize execution time. Idea: Multilayer Bloom Filter
        read_seed_list();

        //case: no weblink in the file -> param checked
        //case: small amount of links in the file -> inside if statement. number is set to 4000 but can obviously get larger
        //case: huge amount of links in the file ->

        //NOTE: This if statement is the part where we can put into a worker class to multi-thread in the future
        if (seed_list.size()<4000 && !seed_list.isEmpty()){
            for (String link: seed_list){
                //getImg("https://google.com");  //testing the getImg function as well as the screenshot API

                //Get Image and Store in a DB or HashMap or Arraylist
                //Redis will gladly accept anything you throw at it since strings are binary safe.
                // but i didn't want to store the img directly in order to prevent the redis db size gets too big
                Jedis jedis = pool.getResource();
                getImg(link, jedis);

            }
        }

        System.out.println("Operation done, All screenshot taken and saved in img folder");
        Jedis jedis = pool.getResource();

        System.out.println("You can search the screen shot by typing the full url here.(e.g. https://google.com)");
        Scanner sc = new Scanner(System.in);

        while (true) {
            String search_key = sc.next();
            String file_location = jedis.get(search_key);

            System.out.println(file_location);

            String canonicalPath = new File(".").getCanonicalPath();
            //loading img to client

            img_viewer(file_location);


            //next search begins
            System.out.println("You can search the screen shot by typing the full url here.(e.g. https://google.com)");
        }

    }

    private static void img_viewer(final String img_path){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Image viewer");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                BufferedImage img = null;

                try {
                    img = ImageIO.read(new File(img_path));
                }catch (IOException e){
                    e.printStackTrace();
                    System.exit(1);
                }

                ImageIcon imgIcon = new ImageIcon(img);
                JLabel lbl = new JLabel();
                lbl.setIcon(imgIcon);
                frame.getContentPane().add(lbl,BorderLayout.CENTER);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    private static String filePath(String link){
        String parsedWeblink = link.replaceAll("https://","");
        String fileName = String.format("imgs/%s.jpeg", parsedWeblink.replaceAll("/","_"));
        return fileName;
    }

    private static void getImg(String weblink, Jedis jedis) throws Exception{

        // if key doesn't not exist, we don't have the img in our database.  time to get it
        // otherwise we don't need to do any action.
        if (!jedis.exists(weblink)) {

            URL url = new URL(String.format("%s?access_key=%s&url=%s", APIFLASH_ENDPOINT, ACCESS_KEY, weblink));
            InputStream inputStream = url.openStream();

            String fileName = filePath(weblink);

            System.out.println(fileName);
            OutputStream outputStream = new FileOutputStream(fileName);

            byte[] b = new byte[2048];
            int length;

            while ((length = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, length);
            }

            inputStream.close();
            outputStream.close();

            jedis.set(weblink, filePath(weblink));

        }
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