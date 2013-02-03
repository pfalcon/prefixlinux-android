package utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
//import java.lang.RuntimeException;
import java.io.IOException;
import java.io.FileNotFoundException;

import android.content.res.AssetManager;
import android.util.Log;

public class UnixShell {

    static final int BUF_SIZE = 4096;

    private AssetManager assetMan;

    public UnixShell(AssetManager am) {
        this.assetMan = am;
    }

    public static int spawn(String... cmd)
        throws IOException, InterruptedException
    {
        Process proc = Runtime.getRuntime().exec(cmd);
        int rc = proc.waitFor();
        proc.destroy();
        return rc;
    }

    public static void mkdir(File dir) {
        dir.mkdir();
    }

    public static void mkdir(String dir) {
        mkdir(new File(dir));
    }

    public static void mkdir_p(String dir) {
        new File(dir).mkdirs();
    }

    public static void chmod(File file, int mode) throws IOException {
        int rc;
        try {
            rc = spawn("chmod", Integer.toOctalString(mode), file.getAbsolutePath());
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
        if (rc != 0)
            throw new IOException("chmod failed");
        // TODO: elaborate
        // TODO; this stuff is API 9

        // This works as expected for dirs too
/*        if ((mode & 0100) != 0)
            file.setExecutable(true, false);

        if ((mode & 0004) != 0)
            file.setReadable(true, false);

        if ((mode & 0002) != 0)
            file.setWritable(true, false);
*/
    }

    public static void chmod(String file, int mode) throws IOException {
        chmod(new File(file), mode);
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte buf[] = new byte[BUF_SIZE];
        int sz;
        while ((sz = in.read(buf, 0, BUF_SIZE)) != -1) {
            out.write(buf, 0, sz);
        }
    }

    public static void copy(InputStream in, File out, boolean append) throws IOException {
        OutputStream os = new FileOutputStream(out, append);
        try {
            copy(in, os);
        } finally {
            os.close();
        }
    }

    public static void copy(InputStream in, File out) throws IOException {
        copy(in, out, false);
    }

    public static void copy(InputStream in, String out, boolean append) throws IOException {
        copy(in, new File(out), append);
    }

    public static void copy(InputStream in, String out) throws IOException {
        copy(in, out, false);
    }

    public static void appendToFile(String file, String content) throws IOException {
        InputStream is = new ByteArrayInputStream(content.getBytes());
        copy(is, file, true);
    }


    final static int dirPerm = 0755;
//    final static int dirPerm = 0777; // For debugging
    final static int filePerm = 0644;
    final static int exePerm = 0755;

    public void untarAssets(String assetsRoot, File targetDir)
        throws IOException
    {

        String[] files = assetMan.list(assetsRoot);
        Log.d("shops", "untarAssets: list(" + assetsRoot + ") len: " + Integer.toString(files.length));
        for (String fname: files) {
            Log.d("shops", "list(): " + fname);
        }

        for (String fname: files) {
            String targetFname = fname;
            boolean isExec = false;
            if (targetFname.endsWith("._x")) {
                targetFname = targetFname.replaceFirst("\\._x$", "");
                isExec = true;
            }
            File targetFile = new File(targetDir, targetFname);

            InputStream is = null;
            try {
                is = assetMan.open(assetsRoot + "/" + fname);
            } catch (FileNotFoundException exc) {
                // targetFname is a directory
                mkdir(targetFile);
                chmod(targetFile, dirPerm);
                // Recurse into it
                untarAssets(assetsRoot + "/" + fname, targetFile);
                continue;
            }

            copy(is, targetFile);
            is.close();
            if (isExec)
                chmod(targetFile, exePerm);
            else
                chmod(targetFile, filePerm);
        }
    }

    public void untarAssets(String assetsRoot, String targetDir)
        throws IOException
    {
        untarAssets(assetsRoot, new File(targetDir));
    }
}
