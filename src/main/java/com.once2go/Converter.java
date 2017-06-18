package com.once2go;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by once2go on 04.07.16.
 */
public class Converter {

    public static LinkedList<String> convert(String filePath, String fileName) {
        LinkedList<String> builder = new LinkedList<>();
        Scanner scan = null;
        try {
            String absPath = filePath + "/" + fileName;
            scan = new Scanner(new File(absPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (scan == null) return null;
        addHeader(builder, fileName);
        while (scan.hasNextLine()) {
            String line = scan.nextLine().trim();
            if (!line.isEmpty()) {
                builder.add("file.writeline([[" + line + "]])\r\n");
            }
        }
        addFooter(builder);
        return builder;
    }

    private static LinkedList<String> addHeader(LinkedList<String> builder, String filename) {
        builder.add("file.open(\"" + filename + "\",\"w+\")\r\n");
        return builder;
    }

    private static LinkedList<String> addFooter(LinkedList<String> builder) {
        builder.add("file.flush()\r\n");
        builder.add("file.close()\r\n");
        builder.add("\r\n");
        return builder;
    }
}
