package com.musicinfo.GUI;

import java.awt.*; 
import java.awt.image.*; 
import java.io.File;
import java.io.IOException;

import javax.imageio.*;
import org.jtransforms.fft.DoubleFFT_1D;
import java.awt.Graphics2D;
import javax.sound.sampled.*;

import javax.swing.JPanel;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import com.musicinfo.util.*;

public class FFT extends JPanel
{
	private BufferedImage img;
	private BufferedImage newImage;

	int bitsSample;

	File file;

	private static final double TAU = Math.PI * 2;
	private static final int MAX_COLOR = 0xFF;

	private static double getPower(int rgb, double maxPower) {
		double step = Math.log1p(maxPower) / 4;
		double cf = MAX_COLOR / step;
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;
		double k = 0.0;
		if (r > 0 && g > 0 && b > 0)
			k = g / cf + step * 3;
		else if (r > 0)
			k = r / cf + step * 2;
		else if (g > 0)
			k = g / cf + step;
		else
			k = b / cf;
		return Math.expm1(k);
	}

	public void writeWav2(File f, int sampleRate, double scale) throws IOException {
		double overlap = 0.5;
		int cols = img.getWidth();
		int bins = img.getHeight();
		int sw = (int)(bins * 2 * overlap);
		double maxPower = bins * bins / 4.0;
		double[] samples = new double[sw * cols];
		double[] col = new double[bins * 2];

		DoubleFFT_1D fft = new DoubleFFT_1D(col.length);
		int index = 0;
		for (int c = 0; c < cols; c++) {
			for (int r = 0; r < bins; r++) {
				int rgb = img.getRGB(c, bins - r - 1);
				double power = getPower(rgb, maxPower);
				double amplitude = Math.sqrt(power);
				double phase = Math.random() * TAU - Math.PI;
				col[2 * r] = amplitude;
				col[2 * r + 1] = amplitude;
			}
			fft.realInverse(col, true);
			int start = 0;
			int end = start + sw;
			for (int s = start; s < end; s++) {
				samples[index++] = (col[s] * 2) / scale;
			}
		}

		SampledMemoryData data = new SampledMemoryData(samples);
		AudioInputStream stream = data.toStream(sampleRate);		
		AudioSystem.write(stream, AudioFileFormat.Type.WAVE, f);
	}

	public void init() throws IOException {
		img = ImageIO.read(file);
		int sampleRate = 441000;
		writeWav2(new File("picture.wav"), sampleRate, 100);
	}

	public void stop() {} 

	public void paint(Graphics g) {
		g.drawString("FFT Image", 10, 20);
		g.drawImage(img, 10, 30 , null);
	}

	public FFT(File imageFile) {
		file = imageFile;
    }
}
