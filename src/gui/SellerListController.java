package gui;

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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	private SellerService service;

	@FXML
	private TableView<Seller> tableViewSellers;

	@FXML
	private TableColumn<Seller, Integer> tableColumnId;

	@FXML
	private TableColumn<Seller, String> tableColumnNome;

	@FXML
	private TableColumn<Seller, Seller> tableColumnEdit;

	@FXML
	TableColumn<Seller, Seller> tableColumnRemove;

	@FXML
	private Button btNovo;

	@FXML
	private ObservableList<Seller> obsList;

	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage stage = Utils.palcoAtual(event);
		Seller depar = new Seller();
		criarDialogo(stage, "/gui/SellerForm.fxml", depar);
	}

	public void setSellerService(SellerService service) {
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

		tableViewSellers.prefHeightProperty().bind(stage.heightProperty());

	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Serviço está nulo");
		}

		List<Seller> list = service.findAll();

		obsList = FXCollections.observableArrayList(list);

		tableViewSellers.setItems(obsList);
		initEditButtons();
		initRemoveButtons();

	}

	private void criarDialogo(Stage stage, String novaTela, Seller depar) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(novaTela));
//
//			Pane pane = loader.load();
//
//			SellerFormController controller = loader.getController();
//			controller.setSeller(depar);
//			controller.setSellerService(new SellerService());
//			controller.entrandoNaLista(this);
//			controller.updateFormData();
//
//			Stage dialogo = new Stage();
//			dialogo.setTitle("Dados do departamento");
//			dialogo.setScene(new Scene(pane));
//			dialogo.setResizable(false);
//			dialogo.initOwner(stage);
//			dialogo.initModality(Modality.WINDOW_MODAL);
//			dialogo.showAndWait();
//		} catch (IOException e) {
//			Alerts.showAlert("IOException", "Erro ao carregar a tela", e.getMessage(), AlertType.ERROR);
//		}
	}

	@Override
	public void dadosMudaram() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> criarDialogo(Utils.palcoAtual(event), "/gui/SellerForm.fxml", obj));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemove.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
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

	private void removeEntity(Seller depar) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza que quer deletar?");
		
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Serviço está nulo");
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
