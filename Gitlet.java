import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

public class Gitlet {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please enter a command.");
			System.exit(0);
		}

		String command = args[0];
		if (command.equals("init")) {
			init();

		} else if (command.equals("add")) {
			if (args.length != 2) {
				return;
			} else {
				if (Files.exists(Paths.get(args[1]))) {
					addFile(args[1]);
				} else {
					System.out.println("File does not exist.");
				}
			}

		} else if (command.equals("commit")) {
			if (args.length != 2) {
				System.out.println("Please enter a commit message.");
			} else {
				Commit curr = CommitHelper.getCurrentCommit();
				curr.push(args[1]);
			}

		} else if (command.equals("rm")) {
			if (args.length != 2) {
				return;
			} else {
				Commit curr = CommitHelper.getCurrentCommit();
				curr.markForRemoval(args[1]);
				CommitHelper.storeCommit(curr);
			}

		} else if (command.equals("log")) {
			log();

		} else if (command.equals("global-log")) {
			global_log();
		} else if (command.equals("find")){
			
		} else if (command.equals("checkout")) {
			if (args.length == 3) {
				int commitId = Integer.parseInt(args[1]);
				String fileName = args[2];
				checkoutByFileAndCommit(commitId, fileName);
			} else if (args.length == 2) {
				if (BranchHelper.branchExists(args[1])) {
					String branchName = args[1];
					checkoutByBranch(branchName);
				} else if (Files.exists(Paths.get(args[1]))) {
					String fileName = args[1];
					checkoutByFile(fileName);
				} else {
					System.out.println(" File does not exist in the most recent commit, or no such branch exists.");
				}
			}
		} else if (command.equals("branch")) {
			if (args.length != 2) {
				System.out.println("You must specify a branch name.");
			} else if (BranchHelper.branchExists(args[1])) {
				System.out.println("A branch with that name already exists.");
			} else {
				int commitId = BranchHelper.getIdOfHeadCommit();
				Branch nb = new Branch(args[1], commitId);
				BranchHelper.cacheBranch(nb);
				BranchHelper.cacheSplitPoint(nb);
			}

		} else if (command.equals("status")) {
			status();

		} else if (command.equals("rm-branch")) {
			if (!(BranchHelper.branchExists(args[1]))) {
				System.out.println("A branch with that name does not exist.");
			} else {
				BranchHelper.deleteBranch(args[1]);
			}
		} else if (command.equals("reset")) {
			int commitId = Integer.parseInt(args[1]);
			if (CommitHelper.commitExists(commitId)) {
				Commit c = CommitHelper.loadCommit(commitId);
				CommitHelper.revertToCommit(c);
			} else {
				System.out.println("No commit with that id exists.");
			}

		} else {
			System.out.println("No command with that name exists.");
		}
	}

	private static void checkoutByFile(String fileName) {
		Commit head = CommitHelper.loadCommit(BranchHelper.getIdOfHeadCommit());
		FileObjectHelper.pullFile(head.getObject(fileName));
	}
	private static void checkoutByFileAndCommit(int commitId, String fileName) {
		Commit c = CommitHelper.loadCommit(commitId);
		FileObjectHelper.pullFile(c.getObject(fileName));
	}

	private static void checkoutByBranch(String branchName) {
		Commit current = CommitHelper.getCurrentCommit();
		CommitHelper.revertToCommit(BranchHelper.getHeadOfBranch(branchName));
		BranchHelper.setCurrentBranch(branchName);
		Commit curr = CommitHelper.getCurrentCommit();
		curr.setParentId(BranchHelper.getIdOfHeadCommit());
		CommitHelper.storeCommit(curr);
	}


	private static void status() {
		System.out.println("=== Branches ===");
		String currBranchName = BranchHelper.getCurrentBranch();
		for (String name : BranchHelper.getBranchNames()) {
			if (name.equals(currBranchName)) {
				System.out.println("*" + name);
			} else {
				System.out.println(name);
			}
		}
		System.out.println();
		System.out.println("=== Staged Files ===");
		Commit currCommit = CommitHelper.getCurrentCommit();
		for (FileObject go : currCommit.getStagedFiles()) {
			System.out.println(go.getFileName());
		}
		System.out.println();
		System.out.println("=== Files Marked for Removal ===");
		for (FileObject go : currCommit.getRemovedFiles()) {
			System.out.println(go.getFileName());
		}
		System.out.println();
	}

	private static void global_log() {
		int currCommitId = CommitHelper.getIdOfCurrentCommit();
		for (int id : CommitHelper.getCommitIds()) {
			if (id != currCommitId) {
				Commit curr = CommitHelper.loadCommit(id);
				System.out.println(curr.getLogInfo());
			}
		}
	}

	private static void log() {
		Commit head = BranchHelper.getHeadOfBranch(BranchHelper.getCurrentBranch());
		while (!(head.getMessage().equals("initial commit"))) {
			System.out.print(head.getLogInfo());
			head = CommitHelper.loadCommit(head.getParentId());
		}
		System.out.print(head.getLogInfo());
	}

	private static void addFile(String name) {
		Commit curr = CommitHelper.getCurrentCommit();
		curr.stageFile(name);
		CommitHelper.storeCommit(curr);
		
	}

	private static void init() {
		if (Files.exists(Paths.get("./.gitlet/"))) {
			System.out.println("A gitlet version control system already exists in the current directory.");
			return;
		}
		createDirectories();
		Commit defaultCom = new Commit(0);
		Branch master = new Branch("master", defaultCom.getId());
		BranchHelper.cacheBranch(master);
		BranchHelper.setCurrentBranch("master");
		BranchHelper.cacheSplitPoint(master);
		defaultCom.push("initial commit");
	}

	private static void createDirectories() {
		try {
			Files.createDirectory(Paths.get("./.gitlet/"));
			Files.createDirectory(Paths.get("./.gitlet/obj"));
			Files.createDirectory(Paths.get("./.gitlet/branches"));
			Files.createDirectory(Paths.get("./.gitlet/commits"));
			Files.createDirectory(Paths.get("./.gitlet/splits"));
		} catch (IOException e) {
			return;
		}
	}
}