package br.uff.ic.sccgit.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EActivation {

	@Id
	Integer taskId;
	
	Integer actId;
	
	String folder;
	
	String subfolder;

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getActId() {
		return actId;
	}

	public void setActId(Integer actId) {
		this.actId = actId;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getSubfolder() {
		return subfolder;
	}

	public void setSubfolder(String subfolder) {
		this.subfolder = subfolder;
	}
	
}
