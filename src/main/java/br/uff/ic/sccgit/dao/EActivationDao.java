package br.uff.ic.sccgit.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import br.uff.ic.sccgit.model.EActivation;


public class EActivationDao {
	
	public List<EActivation> getActivations(String workflow, String activityName, String machineAddres) {
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		
		List<EActivation> l = new ArrayList<EActivation>();
		SQLQuery query= session.createSQLQuery(" select "
													+ " eao.taskid, ea.actid, wkfid, ea.tag, em.machineid, folder, subfolder "
												+ " from "
													+ " eworkflow ew, "
													+ " eactivity ea, "
													+ " eactivation eao, "
													+ "	emachine em "
												+ " where "
													+ "	ew.ewkfid = ea.wkfid and "
													+ "	ea.actid = eao.actid and "
													+ "	eao.machineid = em.machineid and "
													+ "	ew.tag = :wfTag and "
													+ "	ea.tag = :actTag and "
													+ "	em.address = :ip");
		
		List<Object[]> rows = query.setString("wfTag", workflow)
									.setString("actTag", activityName)
									.setString("ip", machineAddres)
									.list();
		
		for(Object[] row : rows) {
			EActivation ea = new EActivation();
			ea.setTaskId(Integer.parseInt(row[0].toString()));
			ea.setActId(Integer.parseInt(row[1].toString()));
			ea.setFolder(row[5].toString());
			ea.setSubfolder(row[6].toString());
			l.add(ea);
		}

		session.close();
		sessionFactory.close();
		
		return l;
	}

}
