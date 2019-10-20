import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public final class CommitHelper {
    public static void commitObject(FileObject obj) {
        FileObjectHelper.cacheNewFile(obj.getFileName(), obj.getId());
    }


    public static void revertToCommit(Commit c) {
        for (FileObject go : c.getAllButRemoved()) {
            FileObjectHelper.pullFile(go);
        }
    }


    public static Collection<Integer> getCommitIds() {
        HashSet<Integer> result = new HashSet<Integer>();
        File file = new File("./.gitlet/commits/");
        File[] files = file.listFiles();
        for (File f : files) {
            result.add(Integer.parseInt(f.getName()));
        }
        return result;
    }


    public static boolean commitExists(int commitId) {
        return Files.exists(Paths.get("./.gitlet/commits/" + commitId));
    }


    public static Commit getCurrentCommit() {
        return loadCommit(getIdOfCurrentCommit());
    }


    public static void storeCommit(Commit obj) {
        if (obj == null) {
            return;
        }
        try {
            File destFile = new File(".gitlet/commits/" + obj.getId());
            FileOutputStream fileOut = new FileOutputStream(destFile);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(obj);
        } catch (IOException e) {
            //insert error
        }
    }


    public static Commit loadCommit(int id) {
        Commit result = null;
        String fileName = ".gitlet/commits/" + id;
        File src = new File(fileName);
        if (src.exists()) {
            try {
                FileInputStream fileIn = new FileInputStream(src);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                result = (Commit) objectIn.readObject();
            } catch (IOException e) {
            	//insert error
            } catch (ClassNotFoundException e) {
                //insert error
            }
        }
        return result;
    }


    public static void cacheCurrentCommit(int id) {
        try {
            File file = new File("./.gitlet/CURR");
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.print(id);
            out.close();
        } catch (IOException e) {
            //insert error
        }
    }


    public static void cacheCurrentCommit() {
        cacheCurrentCommit(getIdOfCurrentCommit());
    }


    public static int getIdOfCurrentCommit() {
        int commitId = -1;
        try {
            File file = new File("./.gitlet/CURR");
            Scanner in = new Scanner(file);
            commitId = in.nextInt();
        } catch (IOException e) {
            //System.out.println("Error reading CURR file.");
        }
        return commitId;
    }
}