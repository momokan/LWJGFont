# Lightweight Java Game Font library

The Lightweight Java Game Font library (LWJGFont) is a solution for use any true type fonts on LWJGL.
LWJGFont makes images which any characters of the true type font was drawn on,
and packages into jar which contains these images and classes to use the characters with LWJGL.

## How to use

### Generate jar file to use any font on LWJGL

$ mvn clean package  
$ java -jar target/lwjgfont-0.1.jar -x  
$ java -jar target/lwjgfont-0.1.jar sample/migu-1p-regular.ttf:35  

### Install generated jar to local repository

$ mvn install:install-file -Dfile=myfont-1.0-SNAPSHOT.jar -DgroupId=lwjgfont -DartifactId=myfont \  
  -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true


