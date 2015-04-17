package com.musicinfo.app;

import com.musicinfo.GUI.FFT;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class App
{
	public static void main(String[] a) {
		new App();
		JPanel panel = new FFT();
		JButton okButton = new JButton("OK");
		panel.add(okButton);
		JButton cancelButton = new JButton("Cancel");
		panel.add(cancelButton);
		JFrame frame = new JFrame("Oval Sample");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
