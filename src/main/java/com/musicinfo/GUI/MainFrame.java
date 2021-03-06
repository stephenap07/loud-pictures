/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.musicinfo.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFileChooser;
import java.io.*;

import com.musicinfo.util.ImageWav;
import java.io.IOException;

public class MainFrame extends JFrame implements ActionListener {
   JButton openButton;
   JFileChooser chooser;
   File file;
   
   public MainFrame() {
      setLayout(new BorderLayout());
      chooser = new JFileChooser();
      openButton = new JButton("Open an image file");
      openButton.addActionListener(this);
      JPanel buttonPanel = new JPanel();
      buttonPanel.add(openButton);
      add(buttonPanel, BorderLayout.CENTER);
   }
   
   public void actionPerformed(ActionEvent e) {
      if(e.getSource() == openButton) {
         int returnVal = chooser.showOpenDialog(MainFrame.this);
         if(returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
			ImageWav img = new ImageWav(file);
			img.writeToWav(new File("./picture.wav"));
		 }
      }
   }
}
