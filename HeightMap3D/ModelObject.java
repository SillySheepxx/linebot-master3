package com.example.wa007.practice;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//






import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;

public abstract class ModelObject implements ModelInterface, Serializable {
    public static int X_AXIS = 0;
    public static int Y_AXIS = 1;
    public static int Z_AXIS = 2;
    public String header;
    public long vertexcount;
    public long facecount;
    public float[] size = new float[3];
    public float[] center = new float[3];
    public float volume;
    public double area;
    public String filename = "";
    public String path = "";
    public String filetype = "";
    public String units = "";
    public boolean modified = false;
    public String mimeType;

    public ModelObject(String header, long vertexcount, String mimeType) {
        this.header = header;
        this.mimeType = mimeType;
        this.vertexcount = vertexcount;
    }

    public void init(boolean checking) {
        this.calcSizes();
        if (checking) {
            this.checkModel();
        }

    }

    public float maxSize() {
        float s = 0.0F;

        for(int i = 0; i < this.size.length; ++i) {
            if (this.size[i] > s) {
                s = this.size[i];
            }
        }

        return s;
    }

    public float minSize() {
        float s = 3.4028235E38F;

        for(int i = 0; i < this.size.length; ++i) {
            if (this.size[i] < s) {
                s = this.size[i];
            }
        }

        return s;
    }

    public String getFileType() {
        return this.filetype;
    }

    protected void calcSizes() {
        float[] points = this.getPointArray();
    }

    public void center() {
        float[] points = this.getPointArray();

        for(int i = 0; i < points.length; ++i) {
            if (i % 3 == 0) {
                points[i] += this.center[0] * -1.0F;
            } else if (i % 3 == 1) {
                points[i] += this.center[1] * -1.0F;
            } else {
                points[i] += this.center[2] * -1.0F;
            }
        }

        this.calcSizes();
        this.modified = true;
    }

    public void moveModel(int AXIS, float value) {
        float[] points = this.getPointArray();

        for(int i = AXIS; i < points.length; i += 3) {
            points[i] += value;
        }

        this.calcSizes();
        this.modified = true;
    }

    public void scaleModel(float scale) {
        float[] points = this.getPointArray();

        for(int i = 0; i < points.length; ++i) {
            points[i] *= scale;
        }

        this.calcSizes();
        this.modified = true;
    }

    public float[] rotateNormalPoint(float x, float y, float z, int AXIS, double radius) {
        float[] erg = new float[]{0.0F, 0.0F, 0.0F};
        double[] p0 = new double[]{0.0D, 0.0D, 0.0D};
        ++p0[AXIS];
        getRotatedPoint(erg, new float[]{x, y, z}, radius * 3.141592653589793D / 180.0D, p0);
        return erg;
    }

    public void rotate(int AXIS, double radius) {
        float[] erg = new float[3];
        float[] points = this.getPointArray();
        float[] normal = this.getNormalsArray();
        double[] p0 = new double[]{0.0D, 0.0D, 0.0D};
        ++p0[AXIS];
        double[] p1 = new double[]{(double)this.center[0], (double)this.center[1], (double)this.center[2]};
        double[] p2 = new double[]{(double)this.center[0], (double)this.center[1], (double)this.center[2]};
        ++p2[AXIS];

        for(int i = 0; i < points.length; i += 3) {
            getRotatedPoint(erg, new float[]{points[i], points[i + 1], points[i + 2]}, radius * 3.141592653589793D / 180.0D, p1, p2);
            points[i] = erg[0];
            points[i + 1] = erg[1];
            points[i + 2] = erg[2];
            if (normal != null) {
                getRotatedPoint(erg, new float[]{normal[i], normal[i + 1], normal[i + 2]}, radius * 3.141592653589793D / 180.0D, p0);
                normal[i] = erg[0];
                normal[i + 1] = erg[1];
                normal[i + 2] = erg[2];
            }
        }

        this.calcSizes();
        this.modified = true;
    }

    private void checkModel() {
        float[] points = this.getPointArray();
        RedBlackTree set = new RedBlackTree();

        for(int i = 0; i < points.length; i += 3) {
            if (set.find(points[i] + points[i + 1] * 10.0F + points[i + 2] * 100.0F) == null) {
                boolean found = false;

                for(int j = i; j < points.length; j += 3) {
                    if (i != j && points[i] == points[j] && points[i + 1] == points[j + 1] && points[i + 2] == points[j + 2]) {
                        found = true;
                        set.insert(points[i] + points[i + 1] * 10.0F + points[i + 2] * 100.0F);
                        break;
                    }
                }

                if (!found) {
                    throw new IllegalArgumentException();
                }
            }
        }

    }

    protected static void getRotatedPoint(float[] q, float[] p, double theta, double[] r) {
        q[0] = 0.0F;
        q[1] = 0.0F;
        q[2] = 0.0F;
        r[0] /= abs(r);
        r[1] /= abs(r);
        r[2] /= abs(r);
        double costheta = Math.cos(theta);
        double sintheta = Math.sin(theta);
        q[0] = (float)((double)q[0] + (costheta + (1.0D - costheta) * r[0] * r[0]) * (double)p[0]);
        q[0] = (float)((double)q[0] + ((1.0D - costheta) * r[0] * r[1] - r[2] * sintheta) * (double)p[1]);
        q[0] = (float)((double)q[0] + ((1.0D - costheta) * r[0] * r[2] + r[1] * sintheta) * (double)p[2]);
        q[1] = (float)((double)q[1] + ((1.0D - costheta) * r[0] * r[1] + r[2] * sintheta) * (double)p[0]);
        q[1] = (float)((double)q[1] + (costheta + (1.0D - costheta) * r[1] * r[1]) * (double)p[1]);
        q[1] = (float)((double)q[1] + ((1.0D - costheta) * r[1] * r[2] - r[0] * sintheta) * (double)p[2]);
        q[2] = (float)((double)q[2] + ((1.0D - costheta) * r[0] * r[2] - r[1] * sintheta) * (double)p[0]);
        q[2] = (float)((double)q[2] + ((1.0D - costheta) * r[1] * r[2] + r[0] * sintheta) * (double)p[1]);
        q[2] = (float)((double)q[2] + (costheta + (1.0D - costheta) * r[2] * r[2]) * (double)p[2]);
    }

    protected static void getRotatedPoint(float[] q, float[] p, double theta, double[] p1, double[] p2) {
        double[] r = new double[3];
        q[0] = 0.0F;
        q[1] = 0.0F;
        q[2] = 0.0F;
        r[0] = p2[0] - p1[0];
        r[1] = p2[1] - p1[1];
        r[2] = p2[2] - p1[2];
        p[0] = (float)((double)p[0] - p1[0]);
        p[1] = (float)((double)p[1] - p1[1]);
        p[2] = (float)((double)p[2] - p1[2]);
        r[0] /= abs(r);
        r[1] /= abs(r);
        r[2] /= abs(r);
        double costheta = Math.cos(theta);
        double sintheta = Math.sin(theta);
        q[0] = (float)((double)q[0] + (costheta + (1.0D - costheta) * r[0] * r[0]) * (double)p[0]);
        q[0] = (float)((double)q[0] + ((1.0D - costheta) * r[0] * r[1] - r[2] * sintheta) * (double)p[1]);
        q[0] = (float)((double)q[0] + ((1.0D - costheta) * r[0] * r[2] + r[1] * sintheta) * (double)p[2]);
        q[1] = (float)((double)q[1] + ((1.0D - costheta) * r[0] * r[1] + r[2] * sintheta) * (double)p[0]);
        q[1] = (float)((double)q[1] + (costheta + (1.0D - costheta) * r[1] * r[1]) * (double)p[1]);
        q[1] = (float)((double)q[1] + ((1.0D - costheta) * r[1] * r[2] - r[0] * sintheta) * (double)p[2]);
        q[2] = (float)((double)q[2] + ((1.0D - costheta) * r[0] * r[2] - r[1] * sintheta) * (double)p[0]);
        q[2] = (float)((double)q[2] + ((1.0D - costheta) * r[1] * r[2] + r[0] * sintheta) * (double)p[1]);
        q[2] = (float)((double)q[2] + (costheta + (1.0D - costheta) * r[2] * r[2]) * (double)p[2]);
        q[0] = (float)((double)q[0] + p1[0]);
        q[1] = (float)((double)q[1] + p1[1]);
        q[2] = (float)((double)q[2] + p1[2]);
    }

    private static double abs(double[] a) {
        return Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
    }

    protected static int unsignedByteToInt(byte b) {
        return b & 255;
    }

    protected void writeFloat(RandomAccessFile out, float val) throws IOException {
        byte[] b = ByteBuffer.allocate(4).putFloat(val).array();

        for(int i = b.length - 1; i >= 0; --i) {
            out.write(b[i]);
        }

    }

    protected static float readFloat(RandomAccessFile in, byte[] b) throws IOException {
        in.read(b);
        return Float.intBitsToFloat((b[3] & 255) << 24 | (b[2] & 255) << 16 | (b[1] & 255) << 8 | b[0] & 255);
    }

    protected static float readFloat(InputStream in, byte[] b) throws IOException {
        in.read(b);
        return Float.intBitsToFloat((b[3] & 255) << 24 | (b[2] & 255) << 16 | (b[1] & 255) << 8 | b[0] & 255);
    }

    protected static String getHexString(byte[] b) throws Exception {
        String result = "";

        for(int i = b.length - 1; i >= 0; --i) {
            result = result + Integer.toString((b[i] & 255) + 256, 16).substring(1);
        }

        return result;
    }

    protected static long unsignedIntToLong(byte[] b) {
        long l = 0L;
        l |= (long)(b[3] & 255);
        l <<= 8;
        l |= (long)(b[2] & 255);
        l <<= 8;
        l |= (long)(b[1] & 255);
        l <<= 8;
        l |= (long)(b[0] & 255);
        return l;
    }

    protected static int unsignedShortToInt(byte[] b) {
        int i = 0;
        i = i | b[0] & 255;
        i <<= 8;
        i |= b[1] & 255;
        return i;
    }

    protected static int unsignedShortToIntLittleEndian(byte[] b) {
        int i = 0;
       i = i | b[1] & 255;
        i <<= 8;
        i |= b[0] & 255;
        return i;
    }

   /* public static native float parseFloat(String var0);

    public static native int parseInteger(String var0);

    private static native float calculateVolume(int var0, float[] var1);

    private static native float calculateSizes(int var0, float[] var1, float[] var2, float[] var3);

    protected static native void calculateNormals(int var0, float[] var1);

    protected static native int[] triangulate(int var0, int[] var1, float[] var2);

    */
}
