package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private SellerService service;

	private Seller entity;

	private DepartmentService departmentService;

	private List<DataChangeListener> mudaram = new ArrayList<>();

	@FXML
	private javafx.scene.control.TextField txtId;

	@FXML
	private javafx.scene.control.TextField txtNome;

	@FXML
	private javafx.scene.control.TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private javafx.scene.control.TextField txtBaseSalary;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErroNome;

	@FXML
	private Label labelErroEmail;

	@FXML
	private Label labelErroBirthDate;

	@FXML
	private Label labelErroBaseSalary;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	@FXML
	private ObservableList<Department> obsList;

	public void setSeller(Seller depar) {
		this.entity = depar;
	}

	public void setServices(SellerService depar, DepartmentService service) {
		this.service = depar;
		this.departmentService = service;
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
			throw new IllegalStateException("Servi�o estava nulo!");
		}

		try {
			this.entity = getFormData();
			service.saveOrUpdate(entity);
			notifyMudaram();
			Utils.palcoAtual(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMsg(e.getErros());
		}
	}

	private void notifyMudaram() {
		for (DataChangeListener x : mudaram) {
			x.dadosMudaram();
		}
	}

	private Seller getFormData() {
		Seller obj = new Seller();

		ValidationException ex = new ValidationException("Erro");

		obj.setId(Utils.tryPaseToInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {

			ex.addErro("Nome", "Nome est� vazio");
		}
		obj.setName(txtNome.getText());
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {

			ex.addErro("Email", "Email est� vazio");
		}
		obj.setEmail(txtEmail.getText());
		
		if (dpBirthDate.getValue() == null) {
			ex.addErro("BirthDate", "Anivers�rio est� vazio");
		}
		else {
			Instant instante = Instant.from(dpBirthDate.getValue().
					atStartOfDay(ZoneId.systemDefault()));
			
			obj.setBirthDate(Date.from(instante));
			
		}

		if (txtBaseSalary.getText() == null 
				|| txtBaseSalary.getText().trim().equals("")){

			ex.addErro("BaseSalary", "Sal�rio base est� vazio");
		}
		
		obj.setBaseSalary(Utils.tryPaseToDouble(txtBaseSalary.getText()));
		
		obj.setDepartment(comboBoxDepartment.getValue());
		
		if (ex.getErros().size() > 0) {
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
		Constraints.setTextFieldMaxLength(txtNome, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 70);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	public void updateFormData() {
		if (this.entity == null) {
			throw new IllegalStateException("Entity est� nula");
		}

		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}

		if (entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		} 
		else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}
	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("Departmente est� nulo");
		}

		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private void setErrorMsg(Map<String, String> erro) {
		Set<String> campos = erro.keySet();

		if (campos.contains("Nome")) {
			labelErroNome.setText(erro.get("Nome"));
		}
		else {
			labelErroNome.setText("");
		}
		
		if (campos.contains("Email")) {
			labelErroEmail.setText(erro.get("Email"));
		}
		else {
			labelErroEmail.setText("");
		}
		
		if (campos.contains("BaseSalary")) {
			labelErroBaseSalary.setText(erro.get("BaseSalary"));
		}
		else {
			labelErroBaseSalary.setText("");
		}
		
		if (campos.contains("BirthDate")) {
			labelErroBirthDate.setText(erro.get("BirthDate"));
		}
		else {
			labelErroBirthDate.setText("");
		}
		
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
}
