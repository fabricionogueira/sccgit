package br.uff.ic.sccgit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class Scc2GitInterface {

	private Git git;

	/**
	 * <p>
	 * Constructor of Scc2Git that clones a specified <b>branch</b> in the repository from <b>uri</b> to a local Git repository given in <b>pathToGitDirectory</b>. 
	 * </p>
	 * @param uri
	 * @param pathToGitDirectory
	 * @param branch
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 */
	public Scc2GitInterface(String uri, String pathToGitDirectory, String branch) throws InvalidRemoteException, TransportException, GitAPIException {
		git = Git.cloneRepository()
				.setBranch(branch)
				.setURI( uri )
				.setDirectory( new File(pathToGitDirectory) )
				.call();
	}
	
	/**
	 * <p>
	 * Constructor of Scc2Git that sets an already defined local git repository.
	 * </p>
	 * @param pathToGitDirectory
	 * @throws SccGitException
	 * @throws IOException
	 */
	public Scc2GitInterface(String pathToGitDirectory) throws SccGitException, IOException {
		if (!isValidLocalGitRepository(pathToGitDirectory)) {
			throw new SccGitException("The given path (" + pathToGitDirectory +") is not a git repository.");
		}
		git = new Git(new FileRepository(Paths.get(pathToGitDirectory, ".git").toFile()));
	}
	
	/**
	 * <p>
	 * Constructor of Scc2Git that initializes an empty local repository and links it to a remote one.
	 * </p>
	 * @param uri
	 * @param pathToGitDirectory
	 * @param username
	 * @param password
	 * @throws IllegalStateException
	 * @throws GitAPIException
	 * @throws IOException
	 */
	public Scc2GitInterface(String uri, String pathToGitDirectory, String username, String password) throws IllegalStateException, GitAPIException, IOException {
		if (!new File(pathToGitDirectory).exists()) {
			new File(pathToGitDirectory).mkdirs();
		}
		String readmeFile = "README.md";
		git = Git.init().setDirectory(new File(pathToGitDirectory)).call();
		Scc2GitUtils.createFile(pathToGitDirectory, readmeFile, uri.split("/")[uri.split("/").length - 1]);
		addFiles(readmeFile);
		commit("Initializing repository - First commit");
		StoredConfig sc = git.getRepository().getConfig();
		sc.setString("remote", "origin", "url", uri);
		sc.setString("remote", "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*");
		sc.save();
		push(username, password);
	}
	
	/**
	 * <p>
	 * Checks if a given path is a valid local Git repository.
	 * </p>
	 * @param pathToGitDirectory
	 * @return
	 */
	public static boolean isValidLocalGitRepository(String pathToGitDirectory) {
		boolean result;
		try {
			result = new FileRepository(Paths.get(pathToGitDirectory, ".git").toFile()).getObjectDatabase().exists();
		} catch (IOException e) {
			result = false;
		}
		return result;
	}

	/**
	 * Return the local repository location.
	 * @return
	 */
	public Repository getRepository() {
		return this.git.getRepository();
	}
	
	/**
	 * Creates a local branch.
	 * @param branchName
	 * @return
	 * @throws RefAlreadyExistsException
	 * @throws RefNotFoundException
	 * @throws InvalidRefNameException
	 * @throws CheckoutConflictException
	 * @throws GitAPIException
	 */
	public Ref createBranch(String branchName) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		//TODO - verificar o que significa o setStartPoint
		return this.git.checkout()
				.setCreateBranch(true)
				.setName(branchName)
				.call();
	}
	
//	public void addFilesFromDirectory(String sourceDirectory) {
//		File directory = new File(sourceDirectory);
//		File[] listOfFiles = directory.listFiles();
//		for (int i = 0; i < listOfFiles.length; i++) {
//			if (listOfFiles[i].isFile()) {
//				Files.copy(listOfFiles[i].toPath(), new File(git.getRepository().getWorkTree().getAbsolutePath() + "\\" + listOfFiles[i].getName()).toPath());
//				git.addFiles(listOfFiles[i].getName());
//			}
//		}
//	}
	
	/**
	 * Add files to the working directory.
	 * @param path
	 * @throws NoFilepatternException
	 * @throws GitAPIException
	 */
	public void addFiles(String path) throws NoFilepatternException, GitAPIException {
		git.add().addFilepattern( path ).call();
	}

	/**
	 * Commits all modifications with the specified message. 
	 * @param message
	 * @throws NoHeadException
	 * @throws NoMessageException
	 * @throws UnmergedPathsException
	 * @throws ConcurrentRefUpdateException
	 * @throws WrongRepositoryStateException
	 * @throws AbortedByHookException
	 * @throws GitAPIException
	 */
	public void commit(String message) throws NoHeadException, NoMessageException, UnmergedPathsException, ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException, GitAPIException {
		git.commit().setMessage( message ).call();
	}

	/**
	 * Pushes the modifications to the remote repository.
	 * @param branchName
	 * @param name
	 * @param password
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 */
	public void push(String name, String password) throws InvalidRemoteException, TransportException, GitAPIException{
		CredentialsProvider cp = new UsernamePasswordCredentialsProvider(name, password);
		PushCommand pc = git.push();
		pc.setCredentialsProvider(cp)
		  .setForce(true)
		  .setPushAll();
		
		pc.call();
	}
	
	public void pull() throws WrongRepositoryStateException, InvalidConfigurationException, DetachedHeadException, InvalidRemoteException, CanceledException, RefNotFoundException, RefNotAdvertisedException, NoHeadException, TransportException, GitAPIException {
		git.pull().call();
	}
}
