package br.uff.ic.sccgit.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import br.uff.ic.sccgit.model.EActivation;


public class EActivationDao {
	
	public List<EActivation> getLastActivationsFromWorkflow(String workflow) {
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		List<EActivation> l = new ArrayList<EActivation>();
		SQLQuery query= session.createSQLQuery("select taskid, actid, folder, subfolder from eactivation where actid = (\n" + 
				"select \n" + 
				"	max(eao.actid)\n" + 
				"from \n" + 
				"	eworkflow e,\n" + 
				"	eactivity ea,\n" + 
				"	eactivation eao\n" + 
				"where\n" + 
				"	e.ewkfid = ea.wkfid and\n" + 
				"	ea.actid = eao.actid and\n" + 
				"	e.tag =  :tag and\n" + 
				"	folder is not null and folder !=  '')");
		
		List<Object[]> rows = query.setString("tag", workflow).list();
		
		for(Object[] row : rows) {
			EActivation ea = new EActivation();
			ea.setTaskId(Integer.parseInt(row[0].toString()));
			ea.setActId(Integer.parseInt(row[1].toString()));
			ea.setFolder(row[2].toString());
			ea.setSubfolder(row[3].toString());
			l.add(ea);
		}

		session.close();
		sessionFactory.close();
		
		return l;
	}

}
