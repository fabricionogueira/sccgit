package br.uff.ic.sccgit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;

public class Scc2GitUtils {

	/**
	 * <p>
	 * Creates a file with the specified <b>fileName</b> and <b>content</b> in the specified <b>pathToGitDirectory</b>.
	 * </p>
	 * @param pathToGitDirectory
	 * @param fileName
	 * @param fileContent
	 * @return
	 * @throws NoHeadException
	 * @throws NoMessageException
	 * @throws UnmergedPathsException
	 * @throws ConcurrentRefUpdateException
	 * @throws WrongRepositoryStateException
	 * @throws AbortedByHookException
	 * @throws GitAPIException
	 * @throws IOException
	 */
	public static File createFile(String pathToGitDirectory, String fileName, String fileContent) throws NoHeadException, NoMessageException, UnmergedPathsException, ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException, GitAPIException, IOException {
		if (!pathToGitDirectory.endsWith("/")) {
			pathToGitDirectory += "/";
		}
		BufferedWriter output = null;
		File file = new File(pathToGitDirectory + fileName);
		output = new BufferedWriter(new FileWriter(file));
		output.write(fileContent);
		output.close();
		return file;
	}

}
