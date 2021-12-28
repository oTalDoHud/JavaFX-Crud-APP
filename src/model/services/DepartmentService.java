package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartmentService {

	public List<Department> findAll(){
		List<Department> list = new ArrayList<>();
		
		list.add(new Department(1, "Livros"));
		list.add(new Department(2, "Computadores"));
		list.add(new Department(3, "Eletronicos"));
		list.add(new Department(4, "Eletrodomesticos"));
		
		return list;
	}
}
