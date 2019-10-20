import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.Collection;
import java.util.HashSet;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;

public final class BranchHelper {
    public static Collection<String> getModifiedFiles(String branchName) {
        int stopId = getSplitPointId(branchName);
        Commit branchHead = getHeadOfBranch(branchName);
        HashSet<String> result = new HashSet<String>();
        while (!(branchHead.getMessage().equals("initial commit")) && branchHead.getId() != stopId) {
            for (FileObject go : branchHead.getStagedFiles()) {
                result.add(go.getFileName());
            }
            branchHead = CommitHelper.loadCommit(branchHead.getParentId());
        }
        return result;
    }
    

    public static void setCurrentBranch(String b) {
        try {
            File file = new File("./.gitlet/HEAD");
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.print(b);
            out.close();
        } catch (IOException e) {
            //insert error
        }
    }

    public static void deleteBranch(String b) {
        if (b.equals(getCurrentBranch())) {
            System.out.println("Cannot delete the current branch.");
            return;
        }
        try {
            Files.delete(Paths.get("./.gitlet/branches/" + b));
        } catch (NoSuchFileException e) {
            System.out.println("A branch with that name does not exist.");
        } catch (IOException e) {
           //insert error
        }
    }
    
    public static Collection<String> getBranchNames() {
        HashSet<String> results = new HashSet<String>();
        File file = new File("./.gitlet/branches/");
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                results.add(f.getName());
            }	
        }
        return results;
    }

    public static int getIdOfHeadCommit() {
        String currBranch = getCurrentBranch();
        Commit head = getHeadOfBranch(currBranch);
        return head.getId();
    }

    public static String getCurrentBranch() {
        String name = null;
        try {
            File file = new File("./.gitlet/HEAD");
            Scanner in = new Scanner(file);
            name = in.next();
            in.close();
        } catch (IllegalStateException e) {
            //insert error
        } catch (FileNotFoundException e) {
        }
        return name;
    }

    public static boolean branchExists(String name) {
        return Files.exists(Paths.get("./.gitlet/branches/" + name)); 
    }


    public static void cacheBranch(Branch b) {
        try {
            File file = new File("./.gitlet/branches/" + b.getName());
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.print(b.getCommitId());
            out.close();
        } catch (IOException e) {
        }
    }


    public static void cacheSplitPoint(Branch b) {
        try {
            File file = new File("./.gitlet/splits/" + b.getName());
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.print(b.getSplitPointId());
            out.close();
        } catch (IOException e) {
        }
    }

    public static int getSplitPointId(String branchName) {
        int result = -1;
        File file = new File("./.gitlet/splits/" + branchName);
        try {
            Scanner scanner = new Scanner(file);
            result = scanner.nextInt();
        } catch (FileNotFoundException e) {
        }
        return result;
    }


    public static Commit getHeadOfBranch(String name) {
        Commit result = null;
        File file = new File("./.gitlet/branches/" + name);
        try {
            Scanner scanner = new Scanner(file);
            int commitId = scanner.nextInt();
            result = CommitHelper.loadCommit(commitId);
        } catch (FileNotFoundException e) {
        }
        return result;
    }	
}