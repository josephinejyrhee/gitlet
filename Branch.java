public class Branch {
    private int commitId;
    private String branchName;
    private int splitPointId;

    public Branch(String name, int commitId) {
        this.commitId = commitId;
        branchName = name;
        splitPointId = commitId;
    }
    
    public Branch(String name) {
        this(name, 0);
    }

    public int getSplitPointId() {
        return splitPointId;
    }

    public void setCommitId(int id) {
        commitId = id;
    }

    public int getCommitId() {
        return commitId;
    }

    public String getName() {
        return branchName;
    }
}