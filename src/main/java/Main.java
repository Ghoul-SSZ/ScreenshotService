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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {


    private static ArrayList <String> seed_list = new ArrayList<String>();
    private static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
    private static int num_of_workers=4;

    public static void main(String[] args) throws Exception {

        System.out.println("Redis is running...");

        System.out.println("Welcome to ScreenShot Service.");

        //todo: Solve duplicate weblink case later to optimize execution time. Idea: Multilayer Bloom Filter
        read_seed_list();

        //case: no weblink in the file -> param checked
        //case: small amount of links in the file -> inside if statement. number is set to 4000 but can obviously get larger
        //case: huge amount of links in the file -> answer is multi-treading
        ExecutorService regulator = Executors.newFixedThreadPool(num_of_workers);

        //NOTE: This if statement is the part where we can put into a worker class to multi-thread in the future
        if ( !seed_list.isEmpty()){
            for (String link: seed_list){
                //getImg("https://google.com");  //testing the getImg function as well as the screenshot API

                //Get Image and Store in redis date base
                //Redis will gladly accept anything you throw at it since strings are binary safe. so technically i can store image in the data base
                // but i didn't want to store the img directly in order to prevent the redis db size gets too big
                Jedis jedis = pool.getResource();

                //getImg(link, jedis);
                regulator.submit(new Worker(link,jedis));

            }
            regulator.shutdown();
        }else {
            System.out.println("There are no link in the seed-list.csv file. Time to add some and run the program");
            System.exit(1);
        }

        System.out.println("Operation done, All screenshot taken and saved in img folder");
        Jedis jedis = pool.getResource();

        System.out.println("You can search the screen shot by typing the full url here.(e.g. https://google.com)");
        Scanner sc = new Scanner(System.in);


        while (true) {
            String search_key = sc.next();

            if (search_key.matches("exit") ){
                //Terminate service
                System.out.println("Thank you for using the service!");
                System.exit(1);

            }else {
                String file_location = jedis.get(search_key);

                System.out.println(file_location);

                //String canonicalPath = new File(".").getCanonicalPath();

                //loading img and show it to client
                img_viewer(file_location);


                //next search begins
                System.out.println("You can search the screen shot by typing the full url here.(e.g. https://google.com) If you want to stop the search, type 'exit' ");
            }

        }

    }

    private static void img_viewer(final String img_path){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Image viewer");
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
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