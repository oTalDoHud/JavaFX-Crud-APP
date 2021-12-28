package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewController implements Initializable{
	


	@FXML
	private MenuItem menuItemVendedor;
	
	@FXML
	private MenuItem menuItemDepartamento;
	
	@FXML
	private MenuItem menuItemSobre;
	
	@FXML
	public void onMenuItemVendedorAction() {
		System.out.println("Vendedor");
	}
	
	@FXML
	public void onMenuItemDepartamentoAction() {
		loadView2("/gui/DepartmentList.fxml");
	}
	
	@FXML
	public void onMenuItemSobreAction() {
		loadView("/gui/Sobre.fxml");
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		
		
	}
	
	private synchronized void loadView (String novaTela) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().
					getResource(novaTela));
			VBox newVBox = loader.load();
			
			Scene mainCene = Main.getMainScene();
			
			VBox mainVBox = (VBox) ((ScrollPane) mainCene.getRoot())
					.getContent();
			
			Node mainMenu = mainVBox.getChildren().get(0);
			
			mainVBox.getChildren().clear();
			
			mainVBox.getChildren().add(mainMenu);
			
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
		} catch (IOException e) {
			Alerts.showAlert("IOException", "Erro ao carregar a página"
					, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private synchronized void loadView2 (String novaTela) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().
					getResource(novaTela));
			VBox newVBox = loader.load();
			
			Scene mainCene = Main.getMainScene();
			
			VBox mainVBox = (VBox) ((ScrollPane) mainCene.getRoot())
					.getContent();
			
			Node mainMenu = mainVBox.getChildren().get(0);
			
			mainVBox.getChildren().clear();
			
			mainVBox.getChildren().add(mainMenu);
			
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			DepartmentListController controller = loader
					.getController();
			
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
			
		} catch (IOException e) {
			Alerts.showAlert("IOException", "Erro ao carregar a página"
					, e.getMessage(), AlertType.ERROR);
		}
	}
}
