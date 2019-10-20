import java.io.Serializable;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Date;

public class Commit implements Serializable {
    private int parentId;
    private HashMap<String, FileObject> objects;
    private Date commitDate;
    private String message;
    private int id;
    private int numStagedFiles;

    public Commit(int p) {
        parentId = p;
        objects = new HashMap<String, FileObject>();
        commitDate = null;
        message = null;
        id = FileObjectHelper.numFilesInDir(".gitlet/commits");
        Commit parent = CommitHelper.loadCommit(parentId);
        inheritFiles(parent);
        numStagedFiles = 0;
    }
    
    public void inheritFiles(Commit parent) {
        if (parent != null) {
            for (String s : parent.objects.keySet()) {
                FileObject curr = parent.objects.get(s);
                if (!curr.isMarkedForRemoval()) {
                    FileObject newCurr = new FileObject(curr.getFileName(), curr.getId());
                    objects.put(s, newCurr);
                }
            }
        }
    }

    public void push(String message) {
        for (FileObject go : getStagedFiles()) {
            CommitHelper.commitObject(go);
            //System.out.println("Pushed file: " + go.getFileName());
            go.update();
            objects.put(go.getFileName(), go);
        }
        setCommitDate(new Date());
        setMessage(message);
        CommitHelper.storeCommit(this);
        Commit newCommit = new Commit(getId());
        String b = BranchHelper.getCurrentBranch();
        CommitHelper.storeCommit(newCommit);
        CommitHelper.cacheCurrentCommit(newCommit.getId());
        BranchHelper.cacheBranch(new Branch(b, getId())); 
    }

    public void stageFile(String fileName) {
        // If this fileName is already found in this commit.
        if (objects.containsKey(fileName)) {
            FileObject lastCommit = objects.get(fileName);
            Date lastMod = FileObjectHelper.getLastModifiedDate(fileName);
            Date commitDate = lastCommit.lastCommitDate();
            if (commitDate != null && lastMod.before(commitDate)) {
            
                return;
            }
        }
        int nextId = FileObjectHelper.numFilesInDir(".gitlet/obj") + numStagedFiles;
        FileObject newObj = new FileObject(fileName, nextId);
        newObj.stage();
        objects.put(fileName, newObj);
        numStagedFiles = numStagedFiles + 1;
        System.out.println("Staged file: " + "[" + fileName + "]");
    }
    public void markForRemoval(String fileName) {
        if (objects.containsKey(fileName)) {
            FileObject toRemove = objects.get(fileName);
            toRemove.unstage();
            toRemove.markForRemoval();
            objects.put(fileName, toRemove);
            System.out.println("Marked for removal: " + fileName);
        }
        else {

        }
    }
    public Collection<FileObject> getStagedFiles() {
        HashSet<FileObject> result = new HashSet<FileObject>();
        for (String name : objects.keySet()) {
            FileObject curr = objects.get(name);
            if (curr.isStaged()) {
                result.add(curr);
            }
        }
        return result;
    }

    public Collection<FileObject> getAllButRemoved() {
        HashSet<FileObject> result = new HashSet<FileObject>();
        for (String name : objects.keySet()) {
            FileObject curr = objects.get(name);
            if (!(curr.isMarkedForRemoval())) {
                result.add(curr); 
            }
        }
        return result;
    }
    public Collection<FileObject> getRemovedFiles() {
        HashSet<FileObject> result = new HashSet<FileObject>();
        for (String name : objects.keySet()) {
            FileObject curr = objects.get(name);
            if (curr.isMarkedForRemoval()) {
                result.add(curr);
            }
        }
        return result;
    }
    
    

    public FileObject getObject(String fileName) {
        return objects.get(fileName);
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public String getMessage() {
        return message;
    }

    public void setCommitDate(Date d) {
        commitDate = d; 
    }

    public void setMessage(String msg) {
        message = msg;
    }

    public String getLogInfo() {
        return "====\n" + "Commit " + id + "\n" + commitDate + "\n" + message + "\n";
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int id) {
        parentId = id;
    }
}