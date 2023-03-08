## Geocoder Demo Documentation


NOTE: This demo depends on an Oracle Spatial installation with cartography from one of the supported providers, like Here or TomTom (free sample available here https://www.tomtom.com/map-data-for-oracle-spatial/).

Execute with:

``` 
 ./gradlew run --args='-oracle.map-server.url=http://your.oracle.spatial.server.com'
``` 


To create a Docker image, when using podman:

``` 
❯ podman info |grep sock
    path: /run/user/54321/podman/podman.sock
❯ podman system service -t 5000
``` 

Create a docker image with:

``` 
export DOCKER_HOST=unix:///run/user/54321/podman/podman.sock
./gradlew dockerBuildNative
``` 

To run this docker image and pass the arguments:

``` 
docker run --name geocoder-demo -p8080:8080 -e oracle.map-server.url=http://your.oracle.spatial.server.com localhost/demo:latest
``` 

To use graalvm, set the enviroment variables correctly before running gradle:

```
export JAVA_HOME=/usr/lib64/graalvm/graalvm22-ee-java11 
export PATH=$JAVA_HOME/bin:$PATH
``` 
