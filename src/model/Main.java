package model;
import java.io.*;
import javax.swing.*; 
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import static model.NewJFrame.jTextArea1;

public class Main {
	private Logger logger = Logger.getLogger(getClass().getName());
	public String result;

	Thread	speechThread;
	Thread	resourcesThread;
	public LiveSpeechRecognizer recognizer;

	public Main() {

		logger.log(Level.INFO, "Loading..\n");
		Configuration configuration = new Configuration();

		configuration.setAcousticModelPath("resource:/kz_acoustic/");
		configuration.setDictionaryPath("resource:/kz_lm/kz.dic");

		 configuration.setLanguageModelPath("resource:/kz_lm/kz.lm");

		// Grammar
		configuration.setGrammarPath("resource:/grammars");
		configuration.setGrammarName("grammar");
		configuration.setUseGrammar(true);

		try {
			recognizer = new LiveSpeechRecognizer(configuration);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		}

		recognizer.startRecognition(true);

		startSpeechThread();
		startResourcesThread();
	}


	public void startSpeechThread () {

		if (speechThread != null && speechThread.isAlive())
			return;
		speechThread = new Thread(() -> {
			logger.log(Level.INFO, "You can start to speak...\n");
			try {
				while (true) {
	
					SpeechResult speechResult = recognizer.getResult();
					if (speechResult != null) {

						result = speechResult.getHypothesis();
						System.out.println("You said: [" + result + "]\n");
                                               if (result != "<unk>") {
                                                jTextArea1.setText(jTextArea1.getText() + " " + result);
                                               }
						// logger.log(Level.INFO, "You said: " + result + "\n")
					} else
						logger.log(Level.INFO, "I can't understand what you said.\n");
				}
			} catch (Exception ex) {
				logger.log(Level.WARNING, null, ex);
			}

			logger.log(Level.INFO, "SpeechThread has exited...");
		});

		// Start
		speechThread.start();

	}

	public void startResourcesThread() {

		if (resourcesThread != null && resourcesThread.isAlive())
			return;

		resourcesThread = new Thread(() -> {
			try {

				while (true) {
					if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
						//logger.log(Level.INFO, "Microphone is available.\n")
//                                                statusLabel.setText("kdslk");
					} else {
						// logger.log(Level.INFO, "Microphone is not
						// available.\n")

					}
                                        
					Thread.sleep(350);
				}

			} catch (InterruptedException ex) {
				logger.log(Level.WARNING, null, ex);
				resourcesThread.interrupt();
			}
		});

		resourcesThread.start();
	}

	public void makeDesicion(String result) {

	}

	public static void main(String[] args) {
		// if (args.length == 1 && "SPEECH".equalsIgnoreCase(args[0]))
		new Main();
		// else
		// Logger.getLogger(Main.class.getName()).log(Level.WARNING, "Give me
		// the correct entry string..");

	}
}