/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.zkoss.poi.xslf.extractor;

import java.io.IOException;

import org.zkoss.poi.POIXMLTextExtractor;
import org.zkoss.poi.openxml4j.exceptions.OpenXML4JException;
import org.zkoss.poi.openxml4j.opc.OPCPackage;
import org.zkoss.poi.xslf.XSLFSlideShow;
import org.zkoss.poi.xslf.usermodel.DrawingParagraph;
import org.zkoss.poi.xslf.usermodel.DrawingTextBody;
import org.zkoss.poi.xslf.usermodel.DrawingTextPlaceholder;
import org.zkoss.poi.xslf.usermodel.XMLSlideShow;
import org.zkoss.poi.xslf.usermodel.XSLFCommentAuthors;
import org.zkoss.poi.xslf.usermodel.XSLFComments;
import org.zkoss.poi.xslf.usermodel.XSLFCommonSlideData;
import org.zkoss.poi.xslf.usermodel.XSLFNotes;
import org.zkoss.poi.xslf.usermodel.XSLFRelation;
import org.zkoss.poi.xslf.usermodel.XSLFSlide;
import org.zkoss.poi.xslf.usermodel.XSLFSlideLayout;
import org.zkoss.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.presentationml.x2006.main.CTComment;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthor;
import org.zkoss.poi.POIXMLTextExtractor;
import org.zkoss.poi.openxml4j.exceptions.OpenXML4JException;
import org.zkoss.poi.openxml4j.opc.OPCPackage;
import org.zkoss.poi.xslf.XSLFSlideShow;
import org.zkoss.poi.xslf.usermodel.DrawingParagraph;
import org.zkoss.poi.xslf.usermodel.XMLSlideShow;
import org.zkoss.poi.xslf.usermodel.XSLFCommonSlideData;
import org.zkoss.poi.xslf.usermodel.XSLFRelation;
import org.zkoss.poi.xslf.usermodel.XSLFSlide;

public class XSLFPowerPointExtractor extends POIXMLTextExtractor {
   public static final XSLFRelation[] SUPPORTED_TYPES = new XSLFRelation[] {
      XSLFRelation.MAIN, XSLFRelation.MACRO, XSLFRelation.MACRO_TEMPLATE,
      XSLFRelation.PRESENTATIONML, XSLFRelation.PRESENTATIONML_TEMPLATE,
      XSLFRelation.PRESENTATION_MACRO
   };
   
	private XMLSlideShow slideshow;
	private boolean slidesByDefault = true;
	private boolean notesByDefault = false;
   private boolean masterByDefault = false;
	
	public XSLFPowerPointExtractor(XMLSlideShow slideshow) {
		super(slideshow);
		this.slideshow = slideshow;
	}
	public XSLFPowerPointExtractor(XSLFSlideShow slideshow) throws XmlException, IOException {
		this(new XMLSlideShow(slideshow.getPackage()));
	}
	public XSLFPowerPointExtractor(OPCPackage container) throws XmlException, OpenXML4JException, IOException {
		this(new XSLFSlideShow(container));
	}

	public static void main(String[] args) throws Exception {
		if(args.length < 1) {
			System.err.println("Use:");
			System.err.println("  XSLFPowerPointExtractor <filename.pptx>");
			System.exit(1);
		}
		POIXMLTextExtractor extractor = 
			new XSLFPowerPointExtractor(
					new XSLFSlideShow(args[0]));
		System.out.println(extractor.getText());
	}

	/**
	 * Should a call to getText() return slide text?
	 * Default is yes
	 */
	public void setSlidesByDefault(boolean slidesByDefault) {
		this.slidesByDefault = slidesByDefault;
	}
	/**
	 * Should a call to getText() return notes text?
	 * Default is no
	 */
	public void setNotesByDefault(boolean notesByDefault) {
		this.notesByDefault = notesByDefault;
	}
	
   /**
    * Should a call to getText() return text from master? Default is no
    */
   public void setMasterByDefault(boolean masterByDefault) {
       this.masterByDefault = masterByDefault;
   }
	
	/**
	 * Gets the slide text, but not the notes text
	 */
	public String getText() {
		return getText(slidesByDefault, notesByDefault);
	}
	
   /**
    * Gets the requested text from the file
    * @param slideText Should we retrieve text from slides?
    * @param notesText Should we retrieve text from notes?
    */
   public String getText(boolean slideText, boolean notesText) {
      return getText(slideText, notesText, masterByDefault);
   }
   
   /**
    * Gets the requested text from the file
    * @param slideText Should we retrieve text from slides?
    * @param notesText Should we retrieve text from notes?
    * @param masterText Should we retrieve text from master slides?
    */
   public String getText(boolean slideText, boolean notesText, boolean masterText) {
      StringBuffer text = new StringBuffer();

      XSLFSlide[] slides = slideshow.getSlides();
      XSLFCommentAuthors commentAuthors = slideshow.getCommentAuthors();

      for (XSLFSlide slide : slides) {
         try {
            XSLFNotes notes = slide.getNotes();
            XSLFComments comments = slide.getComments();
            XSLFSlideLayout layout = slide.getSlideLayout();
            XSLFSlideMaster master = layout.getSlideMaster();

            // TODO Do the slide's name
            // (Stored in docProps/app.xml)

            // Do the slide's text if requested
            if (slideText) {
               extractText(slide.getCommonSlideData(), false, text);
               
               // If requested, get text from the master and it's layout 
               if(masterText) {
                  if(layout != null) {
                     extractText(layout.getCommonSlideData(), true, text);
                  }
                  if(master != null) {
                     extractText(master.getCommonSlideData(), true, text);
                  }
               }

               // If the slide has comments, do those too
               if (comments != null) {
                  for (CTComment comment : comments.getCTCommentsList().getCmList()) {
                     // Do the author if we can
                     if (commentAuthors != null) {
                        CTCommentAuthor author = commentAuthors.getAuthorById(comment.getAuthorId());
                        if(author != null) {
                           text.append(author.getName() + ": ");
                        }
                     }
                     
                     // Then the comment text, with a new line afterwards
                     text.append(comment.getText());
                     text.append("\n");
                  }
               }
            }

            // Do the notes if requested
            if (notesText && notes != null) {
               extractText(notes.getCommonSlideData(), false, text);
            }
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      return text.toString();
   }
	
	private void extractText(XSLFCommonSlideData data, boolean skipPlaceholders, StringBuffer text) {
	   for(DrawingTextBody textBody : data.getDrawingText()) {
	      if(skipPlaceholders && textBody instanceof DrawingTextPlaceholder) {
	         DrawingTextPlaceholder ph = (DrawingTextPlaceholder)textBody;
	         if(! ph.isPlaceholderCustom()) {
	            // Skip non-customised placeholder text
	            continue;
	         }
	      }
	      
	      for (DrawingParagraph p : textBody.getParagraphs()) {
            text.append(p.getText());
            text.append("\n");
	      }
	   }
	}
}
