package br.uff.ic.sccgit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

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

public class Scc2Git {

	private static Scc2GitInterface git;

	private static void pull(String localRepo) throws SccGitException, IOException, WrongRepositoryStateException, InvalidConfigurationException, DetachedHeadException, InvalidRemoteException, CanceledException, RefNotFoundException, RefNotAdvertisedException, NoHeadException, TransportException, GitAPIException {
		git = new Scc2GitInterface(localRepo);
		git.pull();
	}

	private static void prepareRepository(String remoteRepo, String localRepo) throws InvalidRemoteException, TransportException, GitAPIException, SccGitException, IOException {
		if (!Scc2GitInterface.isValidLocalGitRepository(localRepo)) {
			git = new Scc2GitInterface(remoteRepo, localRepo, "master");
		} else {
			pull(localRepo);
		}
	}

	private static void createBranch(String branchName) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		git.createBranch(branchName);
	}

	private static void addFiles(String fromDirectory, String toDirectory) throws IOException, NoFilepatternException, GitAPIException {
		File sourceFolder = new File(fromDirectory);
		File[] listOfFiles = sourceFolder.listFiles();
		System.out.println("Copying " + listOfFiles.length + " from " + fromDirectory + " to " + toDirectory);
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				Files.copy(listOfFiles[i].toPath(), new File(toDirectory + listOfFiles[i].getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
				git.addFiles(listOfFiles[i].getName());
			}
		}
	}

	private static void commit(String message) throws NoHeadException, NoMessageException, UnmergedPathsException, ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException, GitAPIException {
		git.commit(message);
	}


	private static void push(String user, String password) throws InvalidRemoteException, TransportException, GitAPIException {
		git.push(user, password);
	}

	private static void push(String[] args) throws SccGitException, IOException, InvalidRemoteException, TransportException, GitAPIException {
		String localRepo = "", username = "", password = "";
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-lr")) {
				localRepo = args[i+1];
				if (!localRepo.endsWith("/")) {
					localRepo += "/";
				}
			}
			if (args[i].equals("-u")) {
				username = args[i+1];
			}
			if (args[i].equals("-p")) {
				password = args[i+1];
			}
		}
		
		git = new Scc2GitInterface(localRepo);
		push(username, password);
	}

	private static void addAndCommit(String[] args) throws SccGitException, IOException, NoFilepatternException, GitAPIException {
		String localRepo = "", wfDirectory = "", message = "";
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-lr")) {
				localRepo = args[i+1];
				if (!localRepo.endsWith("/")) {
					localRepo += "/";
				}
			}
			if (args[i].equals("-wd")) {
				wfDirectory = args[i+1];
				if (!localRepo.endsWith("/")) {
					localRepo += "/";
				}
			}
			if (args[i].equals("-m")) {
				message = args[i+1];
			}
		}
		git = new Scc2GitInterface(localRepo);
		addFiles(wfDirectory, localRepo);
		commit(message);
	}

	private static void branchCreate(String[] args) throws InvalidRemoteException, TransportException, GitAPIException, SccGitException, IOException {
		String remoteRepo = "", localRepo = "", branchName = "";
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-rr")) {
				remoteRepo = args[i+1];
			}
			if (args[i].equals("-lr")) {
				localRepo = args[i+1];
				if (!localRepo.endsWith("/")) {
					localRepo += "/";
				}
			}
			if (args[i].equals("-b")) {
				branchName = args[i+1];
			}
		}
		prepareRepository(remoteRepo, localRepo);
		createBranch(branchName);
	}

	private static void fullCommit(String[] args) throws InvalidRemoteException, TransportException, GitAPIException, SccGitException, IOException {
		String remoteRepo = "", localRepo = "", wfDirectory = "", branchName = "", gitUser = "", gitUserPwd = "";
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-rr")) {
				remoteRepo = args[i+1];
			}
			if (args[i].equals("-lr")) {
				localRepo = args[i+1];
				if (!localRepo.endsWith("/")) {
					localRepo += "/";
				}
			}
			if (args[i].equals("-wd")) {
				wfDirectory = args[i+1];
				if (!wfDirectory.endsWith("/")) {
					wfDirectory += "/";
				}
			}
			if (args[i].equals("-b")) {
				branchName = args[i+1];
			}
			if (args[i].equals("-u")) {
				gitUser = args[i+1];
			}
			if (args[i].equals("-p")) {
				gitUserPwd = args[i+1];
			}
		}

		//1. Clona um repositório remoto, se o local não existir
		//   Se existir, pull
		prepareRepository(remoteRepo, localRepo);
		//2. Criar o branch
		createBranch(branchName);
		//3. Adicionar novos arquivos
		addFiles(wfDirectory, localRepo);
		//4. Comitar os novos arquivos
		commit(branchName + " commit.");
		//5. Fazer push para o repositorio
		push(gitUser, gitUserPwd);

	}

	public static void main(String[] args) {
		try {

			List<String> argsL = Arrays.asList(args);

			if (argsL.size() != 14 && argsL.size() != 6 && argsL.size() != 2) {
				System.out.println("Wrong usage! Try one of these: \n"
						+ "java -jar scc2git -rr <git_remote_repository> -lr <git_local_repository> -wd <workflow_directory> -b <branchName> -m <message> -u <git_user> -p <git_user_password>\n"
						+ "java -jar scc2git -rr <git_remote_repository> -lr <git_local_repository> -b <branchName> \n"
						+ "java -jar scc2git -lr <git_local_repository> -wd <workflow_directory> -m <message>\n"
						+ "java -jar scc2git -lr <git_local_repository> -u <username> -p <password>");
			} else if (argsL.size() == 12) {
				fullCommit(args);
			} else if (argsL.size() == 6 && argsL.contains("-b")) {
				branchCreate(args);
			} else if (argsL.size() == 6 && argsL.contains("-m")) {
				addAndCommit(args);
			} else if (argsL.size() == 6 && argsL.contains("-u")) {
				push(args);
			}
			
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SccGitException e) {
			e.printStackTrace();
		}
	}
}
