package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {
	
	private SellerDao dao = DaoFactory.createSellerDao();

	public List<Seller> findAll(){
		return dao.findAll();
	}
	
	public void saveOrUpdate (Seller depar) {
		if (depar.getId() == null) {
			dao.insert(depar);
		}
		else {
			dao.update(depar);
		}
	}
	
	public void remove (Seller depar) {
		dao.deleteById(depar.getId());
	}
}
