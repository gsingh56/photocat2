/**
 * Copyright 2011, Trustees of Indiana University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *   
 *   Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *   
 *   Neither the name of Indiana University nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. 
 */
package edu.indiana.dlib.catalog.dataimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import edu.indiana.dlib.catalog.dataimport.filemaker.FilemakerXML;
import edu.indiana.dlib.catalog.dataimport.operations.RecordImportOperation;
import edu.indiana.dlib.catalog.dataimport.spreadsheet.SpreadsheetRecords;

/**
 * Encapsulates the data needed for the various steps
 * of the process of importing data.  This keeps track of the
 * state of an import process.
 * 
 * State 1: no import process started
 * State 2: valid file uploaded
 * State 2e: invalid file uploaded
 * State 3: valid mapping selected, importation started
 * State 4: importation stalled or completed (interaction)
 * 
 *   TODO: there are some thread-safety concerns, especially surrounding the file
 */
public class DataImportProcess {

    private File fileToImport;
    
    private String originalFilename;
    
    private Records records;
    
    private RecordImportOperation importOperation;
    
    public File getFile() {
        return fileToImport;
    }
    
    //create a new Records object by using parseFactory 
    //based on the type of the file
    public void setFile(File file, String originalName) throws Exception {
        
        	ParseFactory factory = new ParseFactory();
        	records = factory.getRecords(file,originalName);
        	
        	if(records == null) {
        		throw new Exception("Problem in parsing");
        	}
     
    }
    
    public void setRecordImportOperation(RecordImportOperation op) {
        importOperation = op;
    }
    
    public RecordImportOperation getRecordImportOperation() {
        return importOperation;
    }
    
    public Records getRecords() {
        return records;
    }
    
    public String getOriginalFilename() {
        return originalFilename;
    }
    
    public static void writeStreamToFile(InputStream is, File file) throws IOException {
        OutputStream output = new FileOutputStream(file);  
        ReadableByteChannel inputChannel = Channels.newChannel(is);  
        WritableByteChannel outputChannel = Channels.newChannel(output);  
        ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);  
        while (inputChannel.read(buffer) != -1) {  
            buffer.flip();  
            outputChannel.write(buffer);  
            buffer.compact();  
        }  
        buffer.flip();  
        while (buffer.hasRemaining()) {  
            outputChannel.write(buffer);  
        }  
       inputChannel.close();  
       outputChannel.close();
    }
}

 