package br.uff.ic.sccgit;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;

public class GitInterface {
	
	private Git git;
	
	/**
	 * <p>
	 * Constructor of GitInterface that creates an empty Git repository in the <b>pathToGitDirectory</b> given. 
	 * </p>
	 * @param pathToGitDirectory
	 * @throws IllegalStateException
	 * @throws GitAPIException
	 */
	public GitInterface(File pathToGitDirectory) throws IllegalStateException, GitAPIException {
		git = Git.init().setDirectory( pathToGitDirectory ).call();
	}
	
	/**
	 * <p>
	 * Constructor of GitInterface that clones the repository from <b>uri</b> to a local Git repository given in <b>pathToGitDirectory</b>. 
	 * </p>
	 * @param uri
	 * @param pathToGitDirectory
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 */
	public GitInterface(String uri, File pathToGitDirectory) throws InvalidRemoteException, TransportException, GitAPIException {
		git = Git.cloneRepository()
				  .setURI( uri )
				  .setDirectory( pathToGitDirectory )
				  .call();
	}
	
	public Repository getRepository() {
		return this.git.getRepository();
	}
	

}
