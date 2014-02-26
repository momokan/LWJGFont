# Lightweight Java Game Font library

The Lightweight Java Game Font library (LWJGFont) is a solution for use any true type fonts on LWJGL.
LWJGFont makes images which any characters of the true type font was drawn on,
and packages into jar which contains these images and classes to use the characters with LWJGL.

## How to use

### Generate jar file to use any font on LWJGL

$ mvn clean package  
$ java -jar target/lwjgfont-1.0.jar -x  
$ java -Dfile.encoding=UTF8 -jar target/lwjgfont-1.0.jar sample/migu-1p-regular.ttf:35  

so myfont-1.0-SNAPSHOT.jar will be generated on current directory.

### Install generated jar to local repository

$ mvn install:install-file -Dfile=myfont-1.0-SNAPSHOT.jar -DgroupId=net.chocolapod.lwjgfont \ 
   -DartifactId=myfont -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true

## License

This software is released under the MIT License, 
see LICENSE.txt or http://lwjgfont.chocolapod.net/LICENSE.txt

## I am deeply grateful to

* [The Lightweight Java Game Library (LWJGL)](http://lwjgl.org/) : The greatest Java game library.
* [もさもさフォント](http://lovalotta.pya.jp/mosamosa/) created by @Longsword : The cool font which is used in LWJGFont unit test.

