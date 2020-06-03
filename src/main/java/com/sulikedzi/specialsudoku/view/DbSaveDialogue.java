package com.sulikedzi.specialsudoku.view;

import com.sulikedzi.specialsudoku.model.dao.JdbcSudokuBoardDao;
import com.sulikedzi.specialsudoku.model.exceptions.SudokuException;
import com.sulikedzi.specialsudoku.model.logs.FileAndConsoleLoggerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class DbSaveDialogue implements Initializable {

    private final Logger logger = FileAndConsoleLoggerFactory.getConfiguredLogger(DbSaveDialogue.class.getName());

    public TextField saveNameField;
    public Button saveDbButton;

    private static final String BUNDLE_NAME = "interfaceLanguage";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME);
        switch (MainView.currentLanguage) {
            case "eng": bundle = ResourceBundle.getBundle(BUNDLE_NAME); break;
            case "pl": bundle = ResourceBundle.getBundle(BUNDLE_NAME, new Locale("pl")); break;
            case "pt": bundle = ResourceBundle.getBundle(BUNDLE_NAME, new Locale("pt")); break;
        }

        saveNameField.setPromptText(bundle.getString("choose_save"));
        saveDbButton.setText(bundle.getString("saveButton"));
    }

    public void saveToDb(ActionEvent actionEvent) throws SudokuException {
        String name = saveNameField.getText();
        if (MainView.getSudokuBoard() != null) {

            JdbcSudokuBoardDao dao = new JdbcSudokuBoardDao(name);
            dao.write(MainView.getSudokuBoard());

            Stage stage = (Stage) saveDbButton.getScene().getWindow();
            stage.close();
        }
    }
}
