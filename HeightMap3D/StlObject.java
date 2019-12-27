//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.wa007.practice;



import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class StlObject extends ModelObject implements Serializable {
    public static int FILE_BINARY = 3;
    public static int FILE_ASCII = 4;
    public String stlType;
    public float[] points;
    public float[] normal;

    public StlObject(String header, long vertexcount) {
        super(header, vertexcount, "application/vnd.ms-pki.stl");
    }

    public String getFileType() {
        return this.filetype + " (" + this.stlType + ")";
    }

    public static ModelObject parseObjectFromFile(File file, boolean checking) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String firstLine = br.readLine();
        br.close();
        if (firstLine.startsWith("solid ")) {
            StlObject obj = null;

            try {
                obj = parseASCII(file, checking);
            } catch (Exception var6) {
                return parseBinary(file, checking);
            }

            return obj == null ? parseBinary(file, checking) : obj;
        } else {
            return parseBinary(file, checking);
        }
    }

    private static StlObject parseASCII(File file, boolean checking) throws Exception {
        ArrayList<Float> points = new ArrayList();
        ArrayList<Float> normals = new ArrayList();
        String header = "";
        String[] t = new String[3];
        BufferedReader br = new BufferedReader(new FileReader(file), 2097152);
        String line = "";
        boolean firstLine = true;

        while((line = br.readLine()) != null) {
            line = StringUtils.strip(line);
            t = StringUtils.split(line, " ");
            if (firstLine) {
                firstLine = false;
                if (!StringUtils.equalsIgnoreCase(t[0], "solid")) {
                    return null;
                }

                header = t[1].trim();
            } else if (StringUtils.equalsIgnoreCase(t[0], "vertex")) {
                points.add(Float.parseFloat(t[1]));
                points.add(Float.parseFloat(t[2]));
                points.add(Float.parseFloat(t[3]));
            } else if (StringUtils.equalsIgnoreCase(t[0], "facet")) {
                float x = Float.parseFloat(t[2]);
                float y = Float.parseFloat(t[3]);
                float z = Float.parseFloat(t[4]);
                normals.add(x);
                normals.add(y);
                normals.add(z);
                normals.add(x);
                normals.add(y);
                normals.add(z);
                normals.add(x);
                normals.add(y);
                normals.add(z);
            } else if (StringUtils.equalsIgnoreCase(t[0], "endsolid")) {
                StlObject obj = new StlObject(header, (long)(points.size() / 3));
                obj.facecount = (long)(points.size() / 9);
                obj.points = toArray(points);
                obj.normal = toArray(normals);
                obj.filename = file.getName();
                obj.path = file.getAbsolutePath();
                obj.init(checking);
                obj.stlType = "ASCII";
                obj.filetype = "STL";
                br.close();
                return obj;
            }
        }

        br.close();
        return null;
    }

    private static float[] toArray(ArrayList<Float> array) {
        float[] farray = new float[array.size()];

        for(int i = 0; i < array.size(); ++i) {
            farray[i] = (Float)array.get(i);
        }

        return farray;
    }

    private static StlObject parseBinary(File file, boolean checking) throws Exception {
        String header = "";
        RandomAccessFile in = new RandomAccessFile(file, "r");
        byte[] b = new byte[80];
        in.read(b);

        for(int i = 0; i < b.length; ++i) {
            header = header + (char)unsignedByteToInt(b[i]);
        }

        header = header.trim();
        b = new byte[4];
        in.read(b);
        long vertexcount = (long)((b[3] & 255) << 24 | (b[2] & 255) << 16 | (b[1] & 255) << 8 | b[0] & 255);
        long facecount = vertexcount / 3L;
        float[] points = new float[(int)vertexcount * 9];
        float[] normal = new float[(int)vertexcount * 9];

        for(int i = 0; i < points.length; i += 9) {
            b = new byte[4];
            normal[i] = readFloat(in, b);
            normal[i + 1] = readFloat(in, b);
            normal[i + 2] = readFloat(in, b);
            normal[i + 3] = normal[i];
            normal[i + 4] = normal[i + 1];
            normal[i + 5] = normal[i + 2];
            normal[i + 6] = normal[i];
            normal[i + 7] = normal[i + 1];
            normal[i + 8] = normal[i + 2];
            points[i] = readFloat(in, b);
            points[i + 1] = readFloat(in, b);
            points[i + 2] = readFloat(in, b);
            points[i + 3] = readFloat(in, b);
            points[i + 4] = readFloat(in, b);
            points[i + 5] = readFloat(in, b);
            points[i + 6] = readFloat(in, b);
            points[i + 7] = readFloat(in, b);
            points[i + 8] = readFloat(in, b);
            b = new byte[2];
            in.read(b);
        }

        StlObject object = new StlObject(header, vertexcount);
        object.points = points;
        object.normal = normal;
        object.filename = file.getName();
        object.path = file.getAbsolutePath();
        object.facecount = facecount;
        object.init(checking);
        object.stlType = "Binary";
        object.filetype = "STL";
        in.close();
        return object;
    }

    public void writeToAscii() throws Exception {
        (new File(this.path)).delete();
        BufferedWriter out = null;
        out = new BufferedWriter(new FileWriter(new File(this.path)));
        out.append("solid " + this.filename.substring(0, this.filename.lastIndexOf(".")) + "\r\n");

        for(int i = 0; i < this.points.length; i += 9) {
            out.append(" facet normal " + this.normal[i] + " " + this.normal[i + 1] + " " + this.normal[i + 2] + "\r\n");
            out.append("  outer loop\r\n");
            out.append("   vertex " + this.points[i] + " " + this.points[i + 1] + " " + this.points[i + 2] + "\r\n");
            out.append("   vertex " + this.points[i + 3] + " " + this.points[i + 4] + " " + this.points[i + 5] + "\r\n");
            out.append("   vertex " + this.points[i + 6] + " " + this.points[i + 7] + " " + this.points[i + 8] + "\r\n");
            out.append("  endloop\r\n");
            out.append(" endfacet\r\n");
        }

        out.append("endsolid " + this.filename.substring(0, this.filename.lastIndexOf(".") - 1) + "\r\n");
        out.close();
        this.filetype = "ASCII";
    }

    public void writeToBinary() throws Exception {
        Date startTime = new Date();
        (new File(this.path)).delete();
        RandomAccessFile out = new RandomAccessFile(this.path,"rw");
        double percent = 0.0D;
        byte[] b = new byte[80];

        int i;
        for(i = 0; i < this.header.length(); ++i) {
            b[i] = (byte)this.header.charAt(i);
        }

        out.write(b);
        b = ByteBuffer.allocate(4).putInt(this.points.length / 9).array();

        for(i = b.length - 1; i >= 0; --i) {
            out.write(b[i]);
        }

        for(i = 0; i < this.points.length; i += 9) {
            if (this.normal != null) {
                this.writeFloat(out, this.normal[i]);
                this.writeFloat(out, this.normal[i + 1]);
                this.writeFloat(out, this.normal[i + 2]);
            } else {
                this.writeFloat(out, 0.0F);
                this.writeFloat(out, 0.0F);
                this.writeFloat(out, 0.0F);
            }

            this.writeFloat(out, this.points[i]);
            this.writeFloat(out, this.points[i + 1]);
            this.writeFloat(out, this.points[i + 2]);
            this.writeFloat(out, this.points[i + 3]);
            this.writeFloat(out, this.points[i + 4]);
            this.writeFloat(out, this.points[i + 5]);
            this.writeFloat(out, this.points[i + 6]);
            this.writeFloat(out, this.points[i + 7]);
            this.writeFloat(out, this.points[i + 8]);
            out.write(new byte[2]);
            double p = (double)Math.round((double)i * 1.0D / (double)this.points.length * 100.0D);
            if (p >= percent + 2.0D) {
                double dur = (double)((new Date()).getTime() - startTime.getTime()) / p;
                double t = dur * (100.0D - p) / 1000.0D;
                percent = p;
                System.out.print(p + "% - ");
                if (t > 60.0D) {
                    Log.d("JakeMObile",Math.round(t / 60.0D) + " mins");
                } else {
                    Log.d("JakeMObile",Math.round(t) + " seconds");
                }
            }
        }

        this.filetype = "Binary";
        out.close();
    }

    public float[] getNormalsArray() {
        return this.normal;
    }

    public float[] getPointArray() {
        return this.points;
    }

    public void save(int type) throws Exception {
        if (type == FILE_ASCII) {
            this.writeToAscii();
        } else {
            this.writeToBinary();
        }

        this.modified = false;
    }
}
