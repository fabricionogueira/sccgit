package br.uff.ic.sccgit;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitInterface {

	private Git git;

	/**
	 * <p>
	 * Constructor of GitInterface that creates an empty Git repository in the <b>pathToGitDirectory</b> given. 
	 * </p>
	 * @param pathToGitDirectory
	 * @throws IllegalStateException
	 * @throws GitAPIException
	 * @throws IOException 
	 */
	public GitInterface(File pathToGitDirectory) throws IllegalStateException, GitAPIException, IOException {
		if (!pathToGitDirectory.isDirectory()) {
			git = Git.init().setDirectory( pathToGitDirectory ).call();
			StoredConfig config = git.getRepository().getConfig();
			config.setString("remote", "origin", "url", "https://github.com/fabricionogueira/repositorio_criado");
			config.save();
			git.pull().call();
		} else {
		}
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

	/**
	 * Return the local repository location.
	 * @return
	 */
	public Repository getRepository() {
		return this.git.getRepository();
	}

	public void addFiles(String path) throws NoFilepatternException, GitAPIException {
		DirCache index = git.add().addFilepattern( path ).call();
	}

	public void commit(String message) throws NoHeadException, NoMessageException, UnmergedPathsException, ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException, GitAPIException {
		git.commit().setMessage( message ).call();
	}

	public void push(String name, String password) throws InvalidRemoteException, TransportException, GitAPIException{
		CredentialsProvider cp = new UsernamePasswordCredentialsProvider(name, password);
		PushCommand pc = git.push();
		pc.setCredentialsProvider(cp)
		.setForce(true)
		.setPushAll();
		pc.call();
	}

	public void push(String remote, String name, String password) throws InvalidRemoteException, TransportException, GitAPIException{
		CredentialsProvider cp = new UsernamePasswordCredentialsProvider(name, password);
		PushCommand pc = git.push().setRemote(remote);
		pc.setCredentialsProvider(cp)
		.setForce(true)
		.setPushAll();
		pc.call();
	}

}
