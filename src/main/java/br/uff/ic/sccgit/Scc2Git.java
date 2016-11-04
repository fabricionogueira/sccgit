package br.uff.ic.sccgit;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

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
import org.xml.sax.SAXException;

import br.uff.ic.sccgit.dao.EActivationDao;
import br.uff.ic.sccgit.model.EActivation;

public class Scc2Git {
    private static Scc2GitInterface git;

    private static void pull(String localRepo) throws SccGitException, IOException, WrongRepositoryStateException, InvalidConfigurationException, DetachedHeadException, InvalidRemoteException, CanceledException, RefNotFoundException, RefNotAdvertisedException, NoHeadException, TransportException, GitAPIException {
        git = new Scc2GitInterface(localRepo);
        git.pull();
    }

    private static void prepareRepository(String remoteRepo, String localRepo) throws InvalidRemoteException, TransportException, GitAPIException, SccGitException, IOException {
        if (!Scc2GitInterface.isValidLocalGitRepository((String)localRepo)) {
            git = new Scc2GitInterface(remoteRepo, localRepo, "master");
        } else {
            Scc2Git.pull(localRepo);
        }
    }

    private static void createBranch(String branchName) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
        git.createBranch(branchName);
    }

    private static void addFiles(String fromDirectory, String toDirectory) throws IOException, NoFilepatternException, GitAPIException {
        File sourceFolder = new File(fromDirectory);
        File[] listOfFiles = sourceFolder.listFiles();
        int i = 0;
        while (i < listOfFiles.length) {
            if (listOfFiles[i].isFile()) {
                Files.copy(listOfFiles[i].toPath(), new File(String.valueOf(toDirectory) + listOfFiles[i].getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                git.addFiles(".");
            }
            ++i;
        }
    }

    private static void commit(String message) throws NoHeadException, NoMessageException, UnmergedPathsException, ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException, GitAPIException {
        git.commit(message);
    }

    private static void push(String user, String password) throws InvalidRemoteException, TransportException, GitAPIException {
        git.push(user, password);
    }

    private static void push(String[] args) throws SccGitException, IOException, InvalidRemoteException, TransportException, GitAPIException {
        String localRepo = "";
        String username = "";
        String password = "";
        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-lr") && !(localRepo = args[i + 1]).endsWith("/")) {
                localRepo = String.valueOf(localRepo) + "/";
            }
            if (args[i].equals("-u")) {
                username = args[i + 1];
            }
            if (args[i].equals("-p")) {
                password = args[i + 1];
            }
            ++i;
        }
        git = new Scc2GitInterface(localRepo);
        Scc2Git.push(username, password);
    }

    private static void addAndCommit(String[] args) throws SccGitException, IOException, NoFilepatternException, GitAPIException, ParserConfigurationException, SAXException {
        String localRepo = "";
        String pathToWfFile = "";
        String activityName = "";
        String message = "";
        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-lr") && !(localRepo = args[i + 1]).endsWith("/")) {
                localRepo = String.valueOf(localRepo) + "/";
            }
            if (args[i].equals("-pwf") && !(pathToWfFile = args[i + 1]).endsWith("/")) {
                pathToWfFile = String.valueOf(pathToWfFile) + "/";
            }
            if (args[i].equals("-an")) {
                activityName = args[i + 1];
            }
            if (args[i].equals("-m")) {
                message = args[i + 1];
            }
            ++i;
        }
        
        InetAddress ip = InetAddress.getLocalHost();
        
        git = new Scc2GitInterface(localRepo);
        String workflowTagName = Scc2GitUtils.getWorkflowTag((String)pathToWfFile);
        EActivationDao eaDao = new EActivationDao();
        List<EActivation> activations = eaDao.getActivations(workflowTagName, activityName, ip.toString());
        for (EActivation eActivation : activations) {
            String sourceDirectory = String.valueOf(eActivation.getFolder()) + eActivation.getSubfolder();
            String toDirectory = String.valueOf(localRepo) + activityName + sourceDirectory;
            File dir = new File(toDirectory);
            dir.mkdirs();
            Scc2Git.addFiles(sourceDirectory, toDirectory);
            Scc2Git.commit(message);
        }
    }

    private static void branchCreate(String[] args) throws InvalidRemoteException, TransportException, GitAPIException, SccGitException, IOException {
        String remoteRepo = "";
        String localRepo = "";
        String branchName = "";
        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-rr")) {
                remoteRepo = args[i + 1];
            }
            if (args[i].equals("-lr") && !(localRepo = args[i + 1]).endsWith("/")) {
                localRepo = String.valueOf(localRepo) + "/";
            }
            if (args[i].equals("-b")) {
                branchName = args[i + 1];
            }
            ++i;
        }
        Scc2Git.prepareRepository(remoteRepo, localRepo);
        Scc2Git.createBranch(branchName);
    }

    private static void fullCommit(String[] args) throws InvalidRemoteException, TransportException, GitAPIException, SccGitException, IOException {
        String remoteRepo = "";
        String localRepo = "";
        String wfDirectory = "";
        String branchName = "";
        String gitUser = "";
        String gitUserPwd = "";
        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-rr")) {
                remoteRepo = args[i + 1];
            }
            if (args[i].equals("-lr") && !(localRepo = args[i + 1]).endsWith("/")) {
                localRepo = String.valueOf(localRepo) + "/";
            }
            if (args[i].equals("-wd") && !(wfDirectory = args[i + 1]).endsWith("/")) {
                wfDirectory = String.valueOf(wfDirectory) + "/";
            }
            if (args[i].equals("-b")) {
                branchName = args[i + 1];
            }
            if (args[i].equals("-u")) {
                gitUser = args[i + 1];
            }
            if (args[i].equals("-p")) {
                gitUserPwd = args[i + 1];
            }
            ++i;
        }
        Scc2Git.prepareRepository(remoteRepo, localRepo);
        Scc2Git.createBranch(branchName);
        Scc2Git.addFiles(wfDirectory, localRepo);
        Scc2Git.commit(String.valueOf(branchName) + " commit.");
        Scc2Git.push(gitUser, gitUserPwd);
    }

    public static void main(String[] args) {
        try {
            List<String> argsL = Arrays.asList(args);
            if (argsL.size() != 14 && argsL.size() != 6 && argsL.size() != 8) {
                System.out.println("Wrong usage! Try one of these: \njava -jar scc2git -rr <git_remote_repository> -lr <git_local_repository> -wd <workflow_directory> -b <branchName> -m <message> -u <git_user> -p <git_user_password>\njava -jar scc2git -rr <git_remote_repository> -lr <git_local_repository> -b <branchName> \njava -jar scc2git -lr <git_local_repository> -pwf <path_to_workflow_file> -an <activity_name> -m <message>\njava -jar scc2git -lr <git_local_repository> -u <username> -p <password>");
            } else if (argsL.size() == 14) {
                Scc2Git.fullCommit(args);
            } else if (argsL.size() == 6 && argsL.contains("-b")) {
                Scc2Git.branchCreate(args);
            } else if (argsL.size() == 8 && argsL.contains("-m")) {
                Scc2Git.addAndCommit(args);
            } else if (argsL.size() == 6 && argsL.contains("-u")) {
                Scc2Git.push(args);
            }
        }
        catch (InvalidRemoteException e) {
            e.printStackTrace();
        }
        catch (TransportException e) {
            e.printStackTrace();
        }
        catch (GitAPIException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (SccGitException e) {
            e.printStackTrace();
        }
        catch (SAXException e) {
            e.printStackTrace();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}