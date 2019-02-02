# ScreenshotService

This is a small java program that takes screenshot of websites based on user input. 

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
To tell the java program what websites you want to take a screenshot for. Locate and edit the seed-list.csv file.(note. one url per line). If you wish to change the file location. It is located in a private method in Main.java  

```java
private static void read_seed_list(){
        String filename = "seed-list.csv";
        ...
}

```



## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
