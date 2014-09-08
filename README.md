# Lightweight Java Game Font library

[The Lightweight Java Game Font library](http://lwjgfont.chocolapod.net/) (LWJGFont) is a solution for use any true type fonts on [LWJGL](http://www.lwjgl.org/).
LWJGFont makes images which any characters of the true type font was drawn on,
and packages into jar which contains these images and classes to use the characters with LWJGL.

## How to use

### Generate jar file to use any font on LWJGL

First of all, pre-compile your font file to use as texture on LWJGL.

    $ mvn clean package
    $ java -jar target/lwjgfont-1.0.jar -x
    $ java -jar target/lwjgfont-1.0.jar sample/migu-1p-regular.ttf:35

So myfont-1.0-SNAPSHOT.jar and myfont-1.0-SNAPSHOT.pom.xml will be generated on current directory.
This jar file contains subclass of net.chocolapod.lwjgfont.LWJGFont to use the specified font: migu-1p-regular.ttf (its size is 35.)

### Install generated jar to local repository

    $ mvn install:install-file -Dfile=myfont-1.0-SNAPSHOT.jar -DpomFile=myfont-1.0-SNAPSHOT.pom.xml \
       -DgroupId=net.chocolapod.lwjgfont -DartifactId=myfont -Dversion=1.0-SNAPSHOT -Dpackaging=jar

### Draw any string on your LWJGL game project.

Add the dependency of the generated jar into pom.xml.

    <dependency>
        <groupId>net.chocolapod.lwjgfont</groupId>
        <artifactId>myfont</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

Now, draw a string on Java.

    //  Load the font with LWJGFont.
    LWJGFont    font = new Migu1pRegularH35Font();

    //  Draw a string on the specified location.
    font.drawString("いろはにほへと ちりぬるを", 100, 50, 0);

All generated classes by LWJGFont are subclass of net.chocolapod.lwjgfont.LWJGFont.
net.chocolapod.lwjgfont.LWJGFont has many utility methods to render strings on LWJGL.

These classes are included in net.chocolapod.lwjgfont package.

## License

This software is released under the MIT License,
see LICENSE.txt or http://lwjgfont.chocolapod.net/LICENSE.txt

## I am deeply grateful to

* [The Lightweight Java Game Library (LWJGL)](http://lwjgl.org/) : The greatest Java game library.
* [もさもさフォント](http://lovalotta.pya.jp/mosamosa/) created by @Longsword : The cool font which is used in LWJGFont unit test.

