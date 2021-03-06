package com.it4i.imagej;
/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */



import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This example illustrates how to create an ImageJ {@link Command} plugin.
 * <p>
 * The code here is a simple Gaussian blur using ImageJ Ops.
 * </p>
 * <p>
 * You should replace the parameter fields with your own inputs and outputs,
 * and replace the {@link run} method implementation with your own logic.
 * </p>
 */
@Plugin(type = Command.class, menuPath = "Plugins>Calculate average")
public class ImageMean<T extends RealType<T>> implements Command {
    //
    // Feel free to add more parameters here...
    //

    @Parameter
    private Dataset currentData;

    @Parameter
    private UIService uiService;

    @Parameter
    private OpService opService;

    @Override
    public void run() {
        final Img<T> image = (Img<T>)currentData.getImgPlus();

        System.out.println("numDimensions = " + image.numDimensions());
            
        RandomAccess<T> ra = image.randomAccess();
        
        long dimensions[] = new long[image.numDimensions()];
        image.dimensions(dimensions);
        
        for (int i = 0; i < image.numDimensions(); i++) {
        	System.out.println(i + " = " + dimensions[i]);
        }
        
        double sum = 0;
        for (int c = 0; c < dimensions[2]; c++) {
        	for (int x = 0; x < dimensions[0]; x++) {
        		for (int y = 0; y < dimensions[1]; y++) {
        			ra.setPosition(x, 0);
        			ra.setPosition(y, 1);
        			ra.setPosition(c, 2);
        			T val = ra.get();
        			sum += val.getRealDouble();
        		}
        	}
        }
        
        System.out.println("average = " + sum / (dimensions[0] * dimensions[1] * dimensions[2]));
        
        // create a cursor for the image (the order does not matter)
        Cursor< T > cursor = image.cursor();
        
        T type;
        double val = 0;
        int count = 0;
        
        while ( cursor.hasNext() )
        {
        	type = cursor.next();
        	val += type.getRealDouble();
        	++count;
        }
        
        System.out.println("average = " + val / count + " count = " + count);
        
        uiService.show("The average is " + (val / count) + " (whatever it means)");        

    }

    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();

       // Date d = new Date();
        //ij.ui().showDialog("Hello, it is " + d.toLocaleString());
        
        
        // ask the user for a file to open
        final File file = ij.ui().chooseFile(null, "open");

        if (file != null) {
            // load the dataset
            final Dataset dataset = ij.scifio().datasetIO().open(file.getPath());

            // show the image
            ij.ui().show(dataset);

            // invoke the plugin
            ij.command().run(ImageMean.class, true);
        }
        
    }

}
