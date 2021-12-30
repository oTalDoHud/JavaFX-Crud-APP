package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable{

	private SellerService service; 
	
	private Seller entity;
	
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
	
	public void setSeller (Seller depar) {
		this.entity = depar;
	}
	
	public void setSellerService (SellerService depar) {
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
		} catch (ValidationException e) {
			setErrorMsg(e.getErros());
		}
	}
	
	private void notifyMudaram() {
		for (DataChangeListener x: mudaram) {
			x.dadosMudaram();
		}
	}

	private Seller getFormData() {
		Seller obj = new Seller();
		
		ValidationException ex = new 
				ValidationException("Erro");
		
		obj.setId(Utils.tryPaseToInt(txtId.getText()));
		
		if (txtNome.getText() == null ||
				txtNome.getText().trim().equals("")) {
		
			ex.addErro("Nome", "Nome está vazio");
		}
		obj.setName(txtNome.getText());
		
		if(ex.getErros().size() > 0) {
			throw ex;
		}
		
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

	private void setErrorMsg(Map<String, String> erro) {
		Set<String> campos = erro.keySet();
		
		if(campos.contains("Nome")) {
			labelErroNome.setText(erro.get("Nome"));
		}
	}
}
