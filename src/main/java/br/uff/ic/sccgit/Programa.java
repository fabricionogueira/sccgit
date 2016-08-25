package br.uff.ic.sccgit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

public class Programa {

	public static void main(String[] args) {

		Log log = LogFactory.getLog(Programa.class);
		
		String branchName = "branch01";
		String remoteRepo = "https://github.com/fabricionogueira/sciphy";

		try {
			//0. Iniciar o repositorio
			String path = "C:/repositorios/sciphy/";
			Scc2GitInterface git;// = new Scc2Git(remoteRepo, path, "fabricionogueira", "r3v3nlorD");
			
			//1. Clonar um repositorio
			if (!Scc2GitInterface.isValidLocalGitRepository(path)) {
				//git = new Scc2Git("https://github.com/fabricionogueira/repositorio_criado", path, "master");
				git = new Scc2GitInterface(remoteRepo, path, "master");
			} else {
				git = new Scc2GitInterface(path);
				git.pull();
			}
			//2. Criar o branch
			git.createBranch(branchName);
			//3. Adicionar novos arquivos
			String srcFilesDiretory = "C:\\repositorios\\arquivos";
			File pastaArquivos = new File(srcFilesDiretory);
			File[] listOfFiles = pastaArquivos.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					System.out.println("File " + listOfFiles[i].getName());
					Files.copy(listOfFiles[i].toPath(), new File(path + "\\" + listOfFiles[i].getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
					log.info("Adding file " + listOfFiles[i].getName());
					git.addFiles(listOfFiles[i].getName());
					log.info("File added.");
				}
			}
			//4. Comitar os novos arquivos
			git.commit("Commit to branch " + branchName);
			//5. Fazer push para o repositorio
			git.push("fabricionogueira", "r3v3nlorD");
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (SccGitException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
