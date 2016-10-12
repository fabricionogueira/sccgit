package br.uff.ic.sccgit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

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
	
	public static String getWorkflowTag(String workflowDefinitionFile) throws ParserConfigurationException, SAXException, IOException {
		File inputFile = new File(workflowDefinitionFile);
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();
		return ((Element) doc.getElementsByTagName("executionWorkflow").item(0)).getAttribute("tag");
	}

}
