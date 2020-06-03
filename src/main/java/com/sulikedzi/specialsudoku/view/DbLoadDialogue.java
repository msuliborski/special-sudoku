package com.sulikedzi.specialsudoku.view;

import com.sulikedzi.specialsudoku.model.dao.JdbcSudokuBoardDao;
import com.sulikedzi.specialsudoku.model.logs.FileAndConsoleLoggerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import com.sulikedzi.specialsudoku.model.dao.SudokuBoardDaoFactory;
import com.sulikedzi.specialsudoku.model.exceptions.DaoException;
import com.sulikedzi.specialsudoku.model.sudoku.SudokuBoard;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbLoadDialogue implements Initializable {

    private final Logger logger = FileAndConsoleLoggerFactory.getConfiguredLogger(DbSaveDialogue.class.getName());

    public ComboBox dbComboBoxList;
    public Button loadDbButton;

    private static final String BUNDLE_NAME = "interfaceLanguage";

    @SuppressWarnings("Duplicates")
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME);;
        switch (MainView.currentLanguage) {
            case "eng": bundle = ResourceBundle.getBundle(BUNDLE_NAME); break;
            case "pl": bundle = ResourceBundle.getBundle(BUNDLE_NAME, new Locale("pl")); break;
            case "pt": bundle = ResourceBundle.getBundle(BUNDLE_NAME, new Locale("pt")); break;
        }

        loadDbButton.setText(bundle.getString("loadButton"));

        try {
            JdbcSudokuBoardDao dao = new JdbcSudokuBoardDao("sudoku");
            List<String[]> allBoards = dao.getAllBoardsAsStrings();
            for(int i = 0; i < allBoards.size(); i++) {
                String boardName = allBoards.get(i)[0];
                String date = allBoards.get(i)[1];
                System.out.println(date + " " +boardName);
                dbComboBoxList.getItems().addAll(boardName + ' ' + date);
            }
        } catch(DaoException e) {
            logger.log(Level.SEVERE, DaoException.MISSING_FILE, e);
        }
        dbComboBoxList.setPromptText(MainView.getInstance().getCurrentBundle().getString("choose_load"));
        loadDbButton.setText(MainView.getInstance().getCurrentBundle().getString("loadButton"));
    }

    public void loadFromDb(ActionEvent actionEvent) {
        SudokuBoardDaoFactory sudokuBoardDaoFactory = new SudokuBoardDaoFactory();
        if (dbComboBoxList.getValue() != null) {
            String boardName = (String) dbComboBoxList.getValue();
            boardName = boardName.substring(0, boardName.length() - 20);
            try (JdbcSudokuBoardDao dao = (JdbcSudokuBoardDao) sudokuBoardDaoFactory.getDatabaseDao(boardName)) {
                SudokuBoard tempBoard = dao.read();
                MainView.getInstance().setSudokuBoard(tempBoard);
                MainView.getInstance().reinitializeBoard();
            } catch(DaoException e) {
                logger.log(Level.SEVERE, DaoException.NO_SUCH_RECORD, e);
            }

            Stage stage = (Stage) loadDbButton.getScene().getWindow();
            stage.close();
        }
    }
}
