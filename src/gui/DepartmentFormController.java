package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.entities.Department;

public class DepartmentFormController implements Initializable{

	@FXML
	private Department entity;
	
	@FXML
	private javafx.scene.control.TextField txtId;
	
	@FXML
	private javafx.scene.control.TextField txtNome;
	
	@FXML
	private Label labelErroNome;
	
	@FXML
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;
	
	public void setDepartment (Department depar) {
		this.entity = depar;
	}
	@FXML
	public void onBtSalvarAction() {
		System.out.println("Salvar");
	}
	
	@FXML
	public void onBtCancelarAction() {
		System.out.println("Cancelar");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 30);
	}
	
	public void updateFormData() {
		if (this.entity == null) {
			throw new IllegalStateException("Entity está nula");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getName());
	}

}
