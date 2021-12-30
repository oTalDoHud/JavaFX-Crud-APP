package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable{

	private DepartmentService service; 
	
	private Department entity;
	
	private List<DataChangeListener> mudaram = new ArrayList<>();
	
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
	
	public void setDepartmentService (DepartmentService depar) {
		this.service = depar;
	}
	
	public void entrandoNaLista(DataChangeListener listener) {
		mudaram.add(listener);
	}
	
	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entidade estava nula!");
		}
		if (service == null) {
			throw new IllegalStateException("Serviço estava nulo!");
		}
		
		try {
			this.entity = getFormData();
			service.saveOrUpdate(entity);
			notifyMudaram();
			Utils.palcoAtual(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar objeto", null,
					e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyMudaram() {
		for (DataChangeListener x: mudaram) {
			x.dadosMudaram();
		}
	}

	private Department getFormData() {
		Department obj = new Department();
		obj.setId(Utils.tryPaseToInt(txtId.getText()));
		obj.setName(txtNome.getText());
		
		return obj;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.palcoAtual(event).close();
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
