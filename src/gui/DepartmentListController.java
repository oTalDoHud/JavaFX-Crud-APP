package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {

	private DepartmentService service;

	@FXML
	private TableView<Department> tableViewDepartments;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnNome;

	@FXML
	private TableColumn<Department, Department> tableColumnEdit;

	@FXML
	TableColumn<Department, Department> tableColumnRemove;

	@FXML
	private Button btNovo;

	@FXML
	private ObservableList<Department> obsList;

	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage stage = Utils.palcoAtual(event);
		Department depar = new Department();
		criarDialogo(stage, "/gui/DepartmentForm.fxml", depar);
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL irl, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("name"));

		Stage stage = (Stage) Main.getMainScene().getWindow();

		tableViewDepartments.prefHeightProperty().bind(stage.heightProperty());

	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Servi�o est� nulo");
		}

		List<Department> list = service.findAll();

		obsList = FXCollections.observableArrayList(list);

		tableViewDepartments.setItems(obsList);
		initEditButtons();
		initRemoveButtons();

	}

	private void criarDialogo(Stage stage, String novaTela, Department depar) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(novaTela));

			Pane pane = loader.load();

			DepartmentFormController controller = loader.getController();
			controller.setDepartment(depar);
			controller.setDepartmentService(new DepartmentService());
			controller.entrandoNaLista(this);
			controller.updateFormData();

			Stage dialogo = new Stage();
			dialogo.setTitle("Dados do departamento");
			dialogo.setScene(new Scene(pane));
			dialogo.setResizable(false);
			dialogo.initOwner(stage);
			dialogo.initModality(Modality.WINDOW_MODAL);
			dialogo.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IOException", "Erro ao carregar a tela"
					, e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void dadosMudaram() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> criarDialogo(Utils.palcoAtual(event), "/gui/DepartmentForm.fxml", obj));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemove.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Department depar) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirma��o", "Tem certeza que quer deletar?");
		
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Servi�o est� nulo");
			}
			try {
				service.remove(depar);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao remove objeto", null,
						e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
