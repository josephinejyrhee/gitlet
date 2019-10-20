import java.util.Date;
import java.io.Serializable;

public class FileObject implements Serializable {
    private Date lastCommit;
    private boolean staged;
    private boolean markedForRemove;
    private String fileName;
    private int id;

    public FileObject(String fileName, int id) {
        this.fileName = fileName;
        this.id = id;
        staged = false;
        markedForRemove = false;
        lastCommit = null;
    }
    public boolean hasBeenCommitted() {
        return lastCommit != null;
    }
    
    public boolean isStaged() {
        return staged;
    }
    
    public void stage() {
        staged = true;
    }

    public void unstage() {
        staged = false;
    }

    public void unmarkForRemoval() {
        markedForRemove = false;
    }

    public void markForRemoval() {
        markedForRemove = true;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemove;
    }

    public void update() {
        setCommitDate(new Date());
    }

    public Date lastCommitDate() {
        return lastCommit;
    }

    public void setCommitDate(Date date) {
        lastCommit = date;
    }

    public String getFileName() {
        return fileName;
    }

    public int getId() {
        return id;
    }
}