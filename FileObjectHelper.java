import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.*;
import java.io.IOException;
import java.util.Date;

public final class FileObjectHelper {
    public static int numFilesInDir(String dirPath) {
        return new File(dirPath).list().length;
    }

    
    public static void pullFile(FileObject obj) {
        FileObjectHelper.copyToWorkingDirectory(obj.getId(), obj.getFileName());
    }


    public static void cacheNewFile(String fileName, int id) {
        File toCache = new File(fileName);
        if (!toCache.exists()) {
            System.out.println("File does not exist.");
        }else {
            try {
                Files.copy(Paths.get(fileName), 
                        Paths.get("./.gitlet/obj/" + id ));
            } catch (IOException e) {
            	
            }
        }
    }

    public static void cacheConflictedFile(String fileName, int id) {
        File toCache = new File(fileName);
        File dest = new File("./.gitlet/obj/" + fileName + ".conflicted");
        if (!toCache.exists()) {
            System.out.println("File " + fileName + " does not exist.");
        } else {
            try {
                Files.copy(Paths.get("./.gitlet/obj/" + id ),
                        Paths.get("./.gitlet/obj/" + fileName + ".conflicted"));
            } catch (IOException e) {

            }
        }
    }

    public static void copyToWorkingDirectory(int id, String fileName) {
        File toCopy = new File("./.gitlet/obj/" + id);
        File dest = new File(fileName);
        if (!toCopy.exists()) {
            System.out.println("File " + fileName + " does not exist.");
        } else {
            try {
                Files.copy(Paths.get("./.gitlet/obj/" + id), 
                        Paths.get(fileName), REPLACE_EXISTING);
            } catch (IOException e) {
                
            }
        }
    }


    public static Date getLastModifiedDate(String fileName) {
        File src = new File(fileName);
        return new Date(src.lastModified());
    }
}