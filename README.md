# ScreenshotService

This is a small java program that takes screenshot of websites based on user input. The user can later on query and search for a screenshot he/she wants bye typing the url. 

## Requirements 
Maven, Redis

## Installation
First install [maven](https://maven.apache.org/install.html) if you don't already have it installed. 

This service uses [Redis](https://redis.io/) as Database 

Use the following command to install Redis.

```bash
wget http://download.redis.io/redis-stable.tar.gz
tar xvzf redis-stable.tar.gz
cd redis-stable
make
```

The simplest way to start the Redis server is just executing the redis-server binary without any argument.
```bash
$ redis-server
[28550] 01 Aug 19:29:28 # Warning: no config file specified, using the default config. In order to specify a config file use 'redis-server /path/to/redis.conf'
[28550] 01 Aug 19:29:28 * Server started, Redis version 2.2.12
[28550] 01 Aug 19:29:28 * The server is now ready to accept connections on port 6379
... more logs ...
```
Check if the server is running simply run the redis-cli 
```bash
$ redis-cli ping
PONG
```

Once the server is up and running you can then proceed to clone and run the project

```bash
git clone https://github.com/Ghoul-SSZ/ScreenshotService.git
```

Now you can start to use the Screenshot Service

## How to use
To tell the java program what websites you want to take a screenshot of. Locate and edit the seed-list.csv file.(note. one url per line). If you wish to change the file location. It is located in a private method in Main.java  

```java
private static void read_seed_list(){
        String filename = "seed-list.csv";
        ...
}

```
### How this solution should be scaled to handle up to 1 000 000 screenshots per day as an enterprise infrastructure component.
The program is structured into a main Class and a Worker Class. 

Main Class only handle the csv file and put all the web-links in an Arraylist, and then assign the tasks to the Workers using a Java multi-tread pool(one worker is given one web-link at a time to work with), and finally inform user that all screenshot have be taken and allow the user to do query and serve the image to the user. 

The Worker Class will check whether we have done the screenshot already by checking our Redis database(Here we can try to use a Multi-Layered Bloom Filter instead, which will tell us the answer in O(1) time instead of having to ask the Redis database). If we haven't done a screenshot, then the worker will get a screenshot from the specific web-link. It will then store the image in a folder(This could be improved later on) in hard-disk,  and store the image file path as a key value pair( key=web-link , value = file path ) into the Redis Database. This is done to prevent the Database size from getting too large.  

The amount of worker active can be configured based on the hardware specifications, theoretically the more core it has, the more worker you can set to be active at the same time. Thus make this program more scale-able. (Up to a limit of course :) , but 1 000 000 per day is no problem at all )

## Limitation and Ideas for further improvement
1. Add Multi-Layer Bloom Filter(MLBF). Redis is fast, but MLBF is definitely faster.   
2. Add functionality to allow user to add more web-links to get screenshot without having to quit the program and modify the csv file. 
    

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
