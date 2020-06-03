package com.sulikedzi.specialsudoku.view;

import com.sulikedzi.specialsudoku.model.dao.SudokuBoardDaoFactory;
import com.sulikedzi.specialsudoku.model.exceptions.DaoException;
import com.sulikedzi.specialsudoku.model.logs.FileAndConsoleLoggerFactory;
import com.sulikedzi.specialsudoku.model.sudoku.SudokuBoard;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainView implements Initializable {

    private final Logger logger = FileAndConsoleLoggerFactory.getConfiguredLogger(DbSaveDialogue.class.getName());

    private static SudokuBoard sudokuBoard;
    private Stage stage;

    Synthesizer synthesizer;
    boolean voiceAssistanceOn = true;

    public GridPane grid;

    public Menu file;
    public Text newGameSeparator;
    public MenuItem startEasy;
    public MenuItem startMedium;
    public MenuItem startHard;
    public Text savingSeparator;
    public MenuItem quickSave;
    public MenuItem quickLoad;
    public MenuItem dbsave;
    public MenuItem dbload;
    public MenuItem dbdelete;

    public Menu options;
    public MenuItem language;
    public MenuItem polish;
    public MenuItem english;
    public MenuItem portuguese;
    public MenuItem colorScheme;
    public MenuItem colorSchemeN;
    public MenuItem colorSchemeDP;
    public MenuItem colorSchemeT;
    public MenuItem colorSchemeM;
    public MenuItem voiceAssistance;
    public MenuItem assistanceOn;
    public MenuItem assistanceOff;

    public Text verifyText;

    public Button row1;
    public Button row2;
    public Button row3;
    public Button row4;
    public Button row5;
    public Button row6;
    public Button row7;
    public Button row8;
    public Button row9;

    public Button column1;
    public Button column2;
    public Button column3;
    public Button column4;
    public Button column5;
    public Button column6;
    public Button column7;
    public Button column8;
    public Button column9;

    public Button box1;
    public Button box2;
    public Button box3;
    public Button box4;
    public Button box5;
    public Button box6;
    public Button box7;
    public Button box8;
    public Button box9;

    public Button stopVoice;

    private static final String BUNDLE_NAME = "interfaceLanguage";
    private List<List<TextField>> boardTextFields = new ArrayList<>();
    private SimpleIntegerProperty[][] boardIntegerProperties = new SimpleIntegerProperty[9][9];


    static String currentLanguage = "eng";

    private ResourceBundle bundle;

    private String currentCorrectColor, currentWrongColor, currentNeutralColor;

    private static MainView instance;

    public MainView() {
        if (instance == null) {
            instance = this;
        } else {
            throw new RuntimeException("MainView already created!");
        }
    }

    public static MainView getInstance() {
        return instance;
    }

    public static SudokuBoard getSudokuBoard() {
        return sudokuBoard;
    }

    public void setSudokuBoard(SudokuBoard sb) {
        sudokuBoard = sb;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public ResourceBundle getCurrentBundle() {
        return bundle;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us" + ".cmu_us_kal.KevinVoiceDirectory");

            Central.registerEngineCentral("com.sun.speech.freetts" + ".jsapi.FreeTTSEngineCentral");

            synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
            synthesizer.allocate();
            // Deallocate the Synthesizer: synthesizer.deallocate();
        } catch (EngineException e) {
            e.printStackTrace();
        }

        bundle = ResourceBundle.getBundle(BUNDLE_NAME);
        currentCorrectColor = "#40B117";
        currentWrongColor = "#D22614";
        currentNeutralColor = "#3F5EE2";

        for (int i = 0; i < 9; i++)
            boardTextFields.add(new ArrayList<>());

        sudokuBoard = new SudokuBoard();

        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 9; y++) {
                TextField emptyTextField = new TextField();
                emptyTextField.setText("");
                emptyTextField.setEditable(true);
                emptyTextField.setAlignment(Pos.CENTER);
                emptyTextField.setPrefHeight(100);
                emptyTextField.setPrefWidth(100);
                emptyTextField.setFont(Font.font("Verdana", 36));
                emptyTextField.setTextFormatter(new TextFormatter<String>((TextFormatter.Change change) -> {
                    if (change.getText().matches("[0-9]*") && !(change.getControlNewText().length() > 1)) {
                        return change;
                    } else {
                        return null;
                    }
                }));

                int finalX = x;
                int finalY = y;
                boardIntegerProperties[x][y] = new SimpleIntegerProperty();
                Bindings.bindBidirectional(emptyTextField.textProperty(), boardIntegerProperties[x][y], new NumberStringConverter());
                emptyTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.equals("")) {
                        sudokuBoard.setFieldValue(finalY, finalX, 0);
                    } else {
                        sudokuBoard.setFieldValue(finalY, finalX, Integer.parseInt(newValue));
                    }
                    verify();
                });

                boardIntegerProperties[x][y].addListener((observable, oldValue, newValue) -> {
                    if (newValue.intValue() == 0) {
                        emptyTextField.setText("");
                    } else {
                        emptyTextField.setText(String.valueOf(newValue));
                    }
                    if (sudokuBoard.isFieldDefault(finalY, finalX)) {
                        emptyTextField.setStyle("-fx-text-fill: grey;");
                        emptyTextField.setEditable(false);
                    } else {
                        emptyTextField.setStyle("-fx-text-fill: black;");
                        emptyTextField.setEditable(true);
                    }
                    verify();
                });

                boardTextFields.get(x).add(emptyTextField);
                grid.add(emptyTextField, x, y);
            }
        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 9; y++) {
                boardTextFields.get(x).get(y).setText("");
            }
    }

    public void reinitializeBoard() {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                boardIntegerProperties[y][x].set(sudokuBoard.getFieldValue(x, y));
            }
        }
    }

    public void startGameEasy() {
        readString("Start game: easy");
        startGame(1);
        verify();
    }

    public void startGameMedium() {
        readString("Start game: medium");
        startGame(2);
        verify();
    }

    public void startGameHard() {
        readString("Start game: hard");
        startGame(3);
        verify();
    }

    public void startGame(int difficulty) {
        sudokuBoard = new SudokuBoard(difficulty);
        logger.log(Level.INFO, sudokuBoard.toString());
        reinitializeBoard();
        verifyText.setText(bundle.getString("verifyButton"));
        verifyText.setFill(Color.web(currentNeutralColor));
    }

    public void verify() {
        if (sudokuBoard != null) {
            if (sudokuBoard.verify() && !sudokuBoard.areThereZeros()) {
                verifyText.setText(bundle.getString("correct"));
                verifyText.setFill(Color.web(currentCorrectColor));
            } else if (sudokuBoard.verify()) {
                verifyText.setText(bundle.getString("noLogicErrors"));
                verifyText.setFill(Color.web(currentNeutralColor));
            } else {

                verifyText.setText(bundle.getString("wrong"));
                verifyText.setFill(Color.web(currentWrongColor));
            }
        } else {
            verifyText.setText(bundle.getString("newGame"));
            verifyText.setFill(Color.web(currentWrongColor));
        }
    }

    public void saveGame() throws DaoException {
        readString("Quick save");
        SudokuBoardDaoFactory factory = new SudokuBoardDaoFactory();
        factory.getFileDao("sudoku").write(sudokuBoard);
    }

    public void loadGame() throws DaoException {
        readString("Quick load");
        SudokuBoardDaoFactory factory = new SudokuBoardDaoFactory();
        sudokuBoard = (SudokuBoard) factory.getFileDao("sudoku").read();
        reinitializeBoard();
    }

    public void setPl() {
        readString("polish");
        currentLanguage = "pl";
        updateNames();
        verify();
    }

    public void setEng() {
        readString("english");
        currentLanguage = "eng";
        updateNames();
        verify();
    }
    public void setPt() {
        readString("portuguese");
        currentLanguage = "pt";
        updateNames();
        verify();
    }

    public void updateNames() {
        switch (currentLanguage) {
            case "eng": bundle = ResourceBundle.getBundle(BUNDLE_NAME); break;
            case "pl": bundle = ResourceBundle.getBundle(BUNDLE_NAME, new Locale("pl")); break;
            case "pt": bundle = ResourceBundle.getBundle(BUNDLE_NAME, new Locale("pt")); break;
        }

        file.setText(bundle.getString("file"));
        newGameSeparator.setText(bundle.getString("newGameSeparator"));
        startEasy.setText(bundle.getString("startEasy"));
        startMedium.setText(bundle.getString("startMedium"));
        startHard.setText(bundle.getString("startHard"));
        savingSeparator.setText(bundle.getString("savingSeparator"));
        quickSave.setText(bundle.getString("quickSave"));
        quickLoad.setText(bundle.getString("quickLoad"));

        dbsave.setText(bundle.getString("saveDialogue"));
        dbload.setText(bundle.getString("loadDialogue"));
        dbdelete.setText(bundle.getString("deleteDialogue"));

        options.setText(bundle.getString("options"));

        language.setText(bundle.getString("language"));
        polish.setText(bundle.getString("polish"));
        english.setText(bundle.getString("english"));
        portuguese.setText(bundle.getString("portuguese"));
        colorScheme.setText(bundle.getString("colorScheme"));
        colorSchemeN.setText(bundle.getString("colorSchemeN"));
        colorSchemeDP.setText(bundle.getString("colorSchemeDP"));
        colorSchemeT.setText(bundle.getString("colorSchemeT"));
        colorSchemeM.setText(bundle.getString("colorSchemeM"));
        voiceAssistance.setText(bundle.getString("voiceAssistance"));
        assistanceOn.setText(bundle.getString("assistanceOn"));
        assistanceOff.setText(bundle.getString("assistanceOff"));

        verifyText.setText(bundle.getString("verifyButton"));
    }

    public void openSaveWindow() throws IOException {
        readString("Save as...");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/sulikedzi/specialsudoku/DbSaveDialogue.fxml"));

        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle(bundle.getString("saveDialogue"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void openLoadWindow() throws IOException {
        readString("Load from...");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/sulikedzi/specialsudoku/DbLoadDialogue.fxml"));

        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle(bundle.getString("loadDialogue"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void openDeleteWindow() throws IOException {
        readString("Delete...");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/sulikedzi/specialsudoku/DbDeleteDialogue.fxml"));

        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle(bundle.getString("deleteDialogue"));
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    void setColorSchemeN(){
        readString("Color scheme: Normal");
        currentCorrectColor = "#40B117";
        currentWrongColor = "#D22614";
        currentNeutralColor = "#3F5EE2";
        verify();
    }

    @FXML
    void setColorSchemeDP(){
        readString("Color scheme: Deuteranopia, slash, Protanopia");
        currentCorrectColor = "#B5B31B";
        currentWrongColor = "#404CE4";
        currentNeutralColor = "#000000";
        verify();
    }

    @FXML
    void setColorSchemeT(){
        readString("Color scheme: Tritanopia");
        currentCorrectColor = "#43ADFF";
        currentWrongColor = "#F55CE1";
        currentNeutralColor = "#000000";
        verify();
    }

    @FXML
    void setColorSchemeM(){
        readString("Color scheme: Monochromacy");
        currentCorrectColor = "#8E8E8E";
        currentWrongColor = "#4E4E4E";
        currentNeutralColor = "#000000";
        verify();

    }

    void readString(String stringToRead){
        if (voiceAssistanceOn) {
            try {
                synthesizer.resume();
                synthesizer.speakPlainText(stringToRead, null);
                synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    String getStringFromBoard(int what, int value) {
        StringBuilder stringBuilder = new StringBuilder();
        switch (what) {
            case 1:
                for (int i = 0; i < 9; i++) {
                    stringBuilder.append(boardIntegerProperties[value][i].getValue() + " ");
                }
                break;
            case 2: ;
                for (int i = 0; i < 9; i++) {
                    stringBuilder.append(boardIntegerProperties[i][value].getValue() + " ");
                }
                break;
            case 3:
                for (int i = (int) (Math.floor(value / 3) * 3); i < (int) (Math.floor(value / 3) * 3) + 3; i++) {
                    for (int j = (value % 3) * 3; j < (value % 3) * 3 + 3; j++) {
                        stringBuilder.append(boardIntegerProperties[j][i].getValue() + " ");
                    }
                }
                break;
        }
        String stringToRead = stringBuilder.toString();
        stringToRead = stringToRead.substring(0, 5) + ", " + stringToRead.substring(6, 11) + ", " + stringToRead.substring(12, 17);
        stringToRead = stringToRead.replace("0", "blank");
        return stringToRead;
    }

    @FXML
    void readBox(ActionEvent event) {
        readString(getStringFromBoard(3, Integer.parseInt((String) ((Node) event.getSource()).getUserData()) - 1));
    }
    @FXML
    void readRow(ActionEvent event) {
        readString(getStringFromBoard(2, Integer.parseInt((String) ((Node) event.getSource()).getUserData()) - 1));
    }

    @FXML
    void readColumn(ActionEvent event){
        readString(getStringFromBoard(1, Integer.parseInt((String) ((Node) event.getSource()).getUserData()) - 1));
    }

    @FXML
    void turnAssistanceOn(){
        voiceAssistanceOn = true;
        readString("Voice assistance: ON");
    }
    @FXML
    void turnAssistanceOff(){
        readString("Voice assistance: OFF");
        voiceAssistanceOn = false;
    }

}
