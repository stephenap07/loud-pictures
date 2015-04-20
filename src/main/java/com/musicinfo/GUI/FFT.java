package com.musicinfo.GUI;

import java.applet.Applet; 
import java.awt.*; 
import java.awt.image.*; 
import java.io.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import javax.imageio.*;
import java.net.URL;
import org.jtransforms.fft.DoubleFFT_1D;
import javax.swing.JPanel;
import javax.swing.JApplet;
import javax.swing.JInternalFrame;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class FFT extends JPanel
{
	private BufferedImage img;
	private BufferedImage newImage;
	double[] input;
	double[] fft;

	int sampleRate;
	int bitsSample;

	File out;
	AudioFormat format;

	private static int[] getPixels(BufferedImage image) {

		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		final boolean hasAlphaChannel = image.getAlphaRaster() != null;

		int[] result = new int[height*width];
		if (hasAlphaChannel) {
			final int pixelLength = 4;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
				argb += ((int) pixels[pixel + 1] & 0xff); // blue
				argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
				result[row * width + col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		} else {
			final int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb += -16777216; // 255 alpha
				argb += ((int) pixels[pixel] & 0xff); // blue
				argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
				result[row * width + col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		}

		return result;
	}

	private static BufferedImage createFlipped(BufferedImage image)
    {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
        return createTransformed(image, at);
    }

    private static BufferedImage createTransformed(BufferedImage image, AffineTransform at)
    {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

	private void writeWav() throws IOException
	{
		// Make image symmetric
		BufferedImage flippedImage = createFlipped(img);
		int[] pixelImg = getPixels(img);
		int[] pixelFlippedImg = getPixels(flippedImage);

		int width = img.getWidth();
		int height = img.getHeight();

		double[] sampleBuffer = new double[height * 2 * width];

		// Loop through the columns
		for (int x = 0; x < width; ++x) {
			double[] fft = new double[height * 2];
			// Insert column of pixels of the flipped image and the regular image
			for (int y = 0; y < height; ++y) {
				fft[y] = (double)pixelFlippedImg[y * width];
			}
			for (int y = 0; y < height; ++y) {
				fft[y + height] = (double)pixelImg[y * width];
			}

			DoubleFFT_1D fftDo = new DoubleFFT_1D(img.getHeight());
			fftDo.realInverse(fft, true);
			System.arraycopy(fft, 0, sampleBuffer, x * fft.length, fft.length);
		}

		byte[] byteBuffer = new byte[sampleBuffer.length * 8];
		for (int i = 0, j = 0; i < byteBuffer.length; i += 8, ++j) {
			byte[] bytes = new byte[8];
			ByteBuffer.wrap(bytes).putDouble(sampleBuffer[j]);
			System.arraycopy(bytes, 0, byteBuffer, i, bytes.length);
		}

		final boolean bigEndian = true;
		final boolean signed = true;
		final int bits = 16;
		final int channels = 1;
		AudioFormat format;

		System.out.println("Writing to picture.wav");
		format = new AudioFormat((float)sampleRate, bits, channels, signed, bigEndian);
		ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
		AudioInputStream audioInputStream;
		audioInputStream = new AudioInputStream(bais, format, sampleBuffer.length);
		AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new FileOutputStream("picture.wav"));
		audioInputStream.close();
	}

	public void init() {
		new FFT();
	}

	public void stop() {} 

	public void paint(Graphics g) {
		g.drawString("FFT Image", 10, 20);
		g.drawImage(img, 10, 30 , null);
	}

	public FFT() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException ex) {
                } catch (InstantiationException ex) {
                } catch (IllegalAccessException ex) {
                } catch (UnsupportedLookAndFeelException ex) {
                }

                CapturePane capturePane = new CapturePane();
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(capturePane);
                frame.setSize(200, 200);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                PrintStream ps = System.out;
                System.setOut(new PrintStream(new StreamCapturer("STDOUT", capturePane, ps)));

				// Same properties of a CD
				sampleRate = 16000;
				bitsSample = 16;

				try {
					img = ImageIO.read(new File("stormtrooper.bmp"));
					writeWav();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}

            }            
        });
    }

	public class CapturePane extends JPanel implements Consumer {
        private JTextArea output;

        public CapturePane() {
            setLayout(new BorderLayout());
            output = new JTextArea();
            add(new JScrollPane(output));
        }

        @Override
        public void appendText(final String text) {
            if (EventQueue.isDispatchThread()) {
                output.append(text);
                output.setCaretPosition(output.getText().length());
            } else {

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        appendText(text);
                    }
                });

            }
        }        
    }

    public interface Consumer {        
        public void appendText(String text);        
    }

    public class StreamCapturer extends OutputStream {

        private StringBuilder buffer;
        private String prefix;
        private Consumer consumer;
        private PrintStream old;

        public StreamCapturer(String prefix, Consumer consumer, PrintStream old) {
            this.prefix = prefix;
            buffer = new StringBuilder(128);
            buffer.append("[").append(prefix).append("] ");
            this.old = old;
            this.consumer = consumer;
        }

        @Override
        public void write(int b) throws IOException {
            char c = (char) b;
            String value = Character.toString(c);
            buffer.append(value);
            if (value.equals("\n")) {
                consumer.appendText(buffer.toString());
                buffer.delete(0, buffer.length());
                buffer.append("[").append(prefix).append("] ");
            }
            old.print(c);
        }        
    }  
}
