package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDao dao = DaoFactory.createDepartmentDao();

	public List<Department> findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate (Department depar) {
		if (depar.getId() == null) {
			dao.insert(depar);
		}
		else {
			dao.update(depar);
		}
	}
	
	public void remove (Department depar) {
		dao.deleteById(depar.getId());
	}
}
