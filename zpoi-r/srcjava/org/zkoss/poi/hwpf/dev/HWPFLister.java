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

package org.zkoss.poi.hwpf.dev;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.poi.POIDocument;
import org.zkoss.poi.hwpf.HWPFDocument;
import org.zkoss.poi.hwpf.HWPFDocumentCore;
import org.zkoss.poi.hwpf.HWPFOldDocument;
import org.zkoss.poi.hwpf.OldWordFileFormatException;
import org.zkoss.poi.hwpf.model.CHPX;
import org.zkoss.poi.hwpf.model.FieldsDocumentPart;
import org.zkoss.poi.hwpf.model.FileInformationBlock;
import org.zkoss.poi.hwpf.model.GenericPropertyNode;
import org.zkoss.poi.hwpf.model.ListFormatOverride;
import org.zkoss.poi.hwpf.model.ListLevel;
import org.zkoss.poi.hwpf.model.ListTables;
import org.zkoss.poi.hwpf.model.PAPFormattedDiskPage;
import org.zkoss.poi.hwpf.model.PAPX;
import org.zkoss.poi.hwpf.model.PlexOfCps;
import org.zkoss.poi.hwpf.model.StyleDescription;
import org.zkoss.poi.hwpf.model.StyleSheet;
import org.zkoss.poi.hwpf.model.TextPiece;
import org.zkoss.poi.hwpf.sprm.SprmIterator;
import org.zkoss.poi.hwpf.sprm.SprmOperation;
import org.zkoss.poi.hwpf.usermodel.Bookmark;
import org.zkoss.poi.hwpf.usermodel.Bookmarks;
import org.zkoss.poi.hwpf.usermodel.Field;
import org.zkoss.poi.hwpf.usermodel.OfficeDrawing;
import org.zkoss.poi.hwpf.usermodel.Paragraph;
import org.zkoss.poi.hwpf.usermodel.ParagraphProperties;
import org.zkoss.poi.hwpf.usermodel.Picture;
import org.zkoss.poi.hwpf.usermodel.Range;
import org.zkoss.poi.poifs.common.POIFSConstants;
import org.zkoss.poi.poifs.filesystem.DirectoryEntry;
import org.zkoss.poi.poifs.filesystem.DirectoryNode;
import org.zkoss.poi.poifs.filesystem.Entry;
import org.zkoss.poi.poifs.filesystem.POIFSFileSystem;
import org.zkoss.poi.util.Beta;
import org.zkoss.poi.util.IOUtils;
import org.zkoss.poi.util.LittleEndian;

/**
 * Used by developers to list out key information on a HWPF file. End users will
 * probably never need to use this program.
 * 
 * @author Nick Burch (nick at torchbox dot com)
 * @author Sergey Vladimirov (vlsergey at gmail dot com)
 */
@Beta
public final class HWPFLister
{
    private static HWPFDocumentCore loadDoc( File docFile ) throws IOException
    {
        final FileInputStream istream = new FileInputStream( docFile );
        try
        {
            return loadDoc( istream );
        }
        finally
        {
            IOUtils.closeQuietly( istream );
        }
    }

    private static HWPFDocumentCore loadDoc( InputStream inputStream )
            throws IOException
    {
        final POIFSFileSystem poifsFileSystem = HWPFDocumentCore
                .verifyAndBuildPOIFS( inputStream );
        try
        {
            return new HWPFDocument( poifsFileSystem );
        }
        catch ( OldWordFileFormatException exc )
        {
            return new HWPFOldDocument( poifsFileSystem );
        }
    }

    public static void main( String[] args ) throws Exception
    {
        if ( args.length == 0 )
        {
            System.err.println( "Use:" );
            System.err.println( "\tHWPFLister <filename>\n" + "\t\t[--dop]\n"
                    + "\t\t[--textPieces] [--textPiecesText]\n"
                    + "\t\t[--chpx] [--chpxProperties] [--chpxSprms]\n"
                    + "\t\t[--papx] [--papxProperties] [--papxSprms]\n"
                    + "\t\t[--paragraphs] [--paragraphsText]\n"
                    + "\t\t[--bookmarks]\n" + "\t\t[--escher]\n"
                    + "\t\t[--fields]\n" + "\t\t[--pictures]\n"
                    + "\t\t[--officeDrawings]\n" + "\t\t[--styles]\n"
                    + "\t\t[--writereadback]\n" );
            System.exit( 1 );
        }

        boolean outputDop = false;

        boolean outputTextPieces = false;
        boolean outputTextPiecesText = false;

        boolean outputChpx = false;
        boolean outputChpxProperties = false;
        boolean outputChpxSprms = false;

        boolean outputParagraphs = false;
        boolean outputParagraphsText = false;

        boolean outputPapx = false;
        boolean outputPapxSprms = false;
        boolean outputPapxProperties = false;

        boolean outputBookmarks = false;
        boolean outputEscher = false;
        boolean outputFields = false;
        boolean outputPictures = false;
        boolean outputOfficeDrawings = false;
        boolean outputStyles = false;

        boolean writereadback = false;

        for ( String arg : Arrays.asList( args ).subList( 1, args.length ) )
        {
            if ( "--dop".equals( arg ) )
                outputDop = true;

            if ( "--textPieces".equals( arg ) )
                outputTextPieces = true;
            if ( "--textPiecesText".equals( arg ) )
                outputTextPiecesText = true;

            if ( "--chpx".equals( arg ) )
                outputChpx = true;
            if ( "--chpxProperties".equals( arg ) )
                outputChpxProperties = true;
            if ( "--chpxSprms".equals( arg ) )
                outputChpxSprms = true;

            if ( "--paragraphs".equals( arg ) )
                outputParagraphs = true;
            if ( "--paragraphsText".equals( arg ) )
                outputParagraphsText = true;

            if ( "--papx".equals( arg ) )
                outputPapx = true;
            if ( "--papxProperties".equals( arg ) )
                outputPapxProperties = true;
            if ( "--papxSprms".equals( arg ) )
                outputPapxSprms = true;

            if ( "--bookmarks".equals( arg ) )
                outputBookmarks = true;
            if ( "--escher".equals( arg ) )
                outputEscher = true;
            if ( "--fields".equals( arg ) )
                outputFields = true;
            if ( "--pictures".equals( arg ) )
                outputPictures = true;
            if ( "--officeDrawings".equals( arg ) )
                outputOfficeDrawings = true;
            if ( "--styles".equals( arg ) )
                outputStyles = true;

            if ( "--writereadback".equals( arg ) )
                writereadback = true;
        }

        HWPFDocumentCore doc = loadDoc( new File( args[0] ) );
        if ( writereadback )
            doc = writeOutAndReadBack( doc );

        HWPFDocumentCore original;
        {
            System.setProperty( "org.zkoss.poi.hwpf.preserveBinTables",
                    Boolean.TRUE.toString() );
            System.setProperty( "org.zkoss.poi.hwpf.preserveTextTable",
                    Boolean.TRUE.toString() );

            original = loadDoc( new File( args[0] ) );
            if ( writereadback )
                original = writeOutAndReadBack( original );
        }

        HWPFLister listerOriginal = new HWPFLister( original );
        HWPFLister listerRebuilded = new HWPFLister( doc );

        System.out.println( "== OLE streams ==" );
        listerOriginal.dumpFileSystem();

        System.out.println( "== FIB (original) ==" );
        listerOriginal.dumpFIB();

        if ( outputDop )
        {
            System.out.println( "== Document properties ==" );
            listerOriginal.dumpDop();
        }

        if ( outputTextPieces )
        {
            System.out.println( "== Text pieces (original) ==" );
            listerOriginal.dumpTextPieces( outputTextPiecesText );
        }

        if ( outputChpx )
        {
            System.out.println( "== CHPX (original) ==" );
            listerOriginal.dumpChpx( outputChpxProperties, outputChpxSprms );

            System.out.println( "== CHPX (rebuilded) ==" );
            listerRebuilded.dumpChpx( outputChpxProperties, outputChpxSprms );
        }

        if ( outputPapx )
        {
            System.out.println( "== PAPX (original) ==" );
            listerOriginal.dumpPapx( outputPapxProperties, outputPapxSprms );

            System.out.println( "== PAPX (rebuilded) ==" );
            listerRebuilded.dumpPapx( outputPapxProperties, outputPapxSprms );
        }

        if ( outputParagraphs )
        {
            System.out.println( "== Text paragraphs (original) ==" );
            listerRebuilded.dumpParagraphs( true );

            System.out.println( "== DOM paragraphs (rebuilded) ==" );
            listerRebuilded.dumpParagraphsDom( outputParagraphsText );
        }

        if ( outputBookmarks )
        {
            System.out.println( "== BOOKMARKS (rebuilded) ==" );
            listerRebuilded.dumpBookmarks();
        }

        if ( outputEscher )
        {
            System.out.println( "== ESCHER PROPERTIES (rebuilded) ==" );
            listerRebuilded.dumpEscher();
        }

        if ( outputFields )
        {
            System.out.println( "== FIELDS (rebuilded) ==" );
            listerRebuilded.dumpFields();
        }

        if ( outputOfficeDrawings )
        {
            System.out.println( "== OFFICE DRAWINGS (rebuilded) ==" );
            listerRebuilded.dumpOfficeDrawings();
        }

        if ( outputPictures )
        {
            System.out.println( "== PICTURES (rebuilded) ==" );
            listerRebuilded.dumpPictures();
        }

        if ( outputStyles )
        {
            System.out.println( "== STYLES (rebuilded) ==" );
            listerRebuilded.dumpStyles();
        }
    }

    private static HWPFDocumentCore writeOutAndReadBack(
            HWPFDocumentCore original )
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream( 4096 );
            original.write( baos );
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    baos.toByteArray() );
            return loadDoc( bais );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    private final HWPFDocumentCore _doc;

    private LinkedHashMap<Integer, String> paragraphs;

    public HWPFLister( HWPFDocumentCore doc )
    {
        _doc = doc;

        buildParagraphs();
    }

    private void buildParagraphs()
    {
        paragraphs = new LinkedHashMap<Integer, String>();

        StringBuilder part = new StringBuilder();
        String text = _doc.getDocumentText();
        for ( int charIndex = 0; charIndex < text.length(); charIndex++ )
        {
            char c = text.charAt( charIndex );
            part.append( c );
            if ( c == 13 || c == 7 || c == 12 )
            {
                paragraphs.put( Integer.valueOf( charIndex ), part.toString() );
                part.setLength( 0 );
            }
        }
    }

    private void dumpBookmarks()
    {
        if ( !( _doc instanceof HWPFDocument ) )
        {
            System.out.println( "Word 95 not supported so far" );
            return;
        }

        HWPFDocument document = (HWPFDocument) _doc;
        Bookmarks bookmarks = document.getBookmarks();
        for ( int b = 0; b < bookmarks.getBookmarksCount(); b++ )
        {
            Bookmark bookmark = bookmarks.getBookmark( b );
            System.out.println( "[" + bookmark.getStart() + "; "
                    + bookmark.getEnd() + "): " + bookmark.getName() );
        }
    }

    public void dumpChpx( boolean withProperties, boolean withSprms )
    {
        for ( CHPX chpx : _doc.getCharacterTable().getTextRuns() )
        {
            System.out.println( chpx );

            if ( withProperties )
            {
                System.out.println( chpx.getCharacterProperties(
                        _doc.getStyleSheet(), (short) StyleSheet.NIL_STYLE ) );
            }

            if ( withSprms )
            {
                SprmIterator sprmIt = new SprmIterator( chpx.getGrpprl(), 0 );
                while ( sprmIt.hasNext() )
                {
                    SprmOperation sprm = sprmIt.next();
                    System.out.println( "\t" + sprm.toString() );
                }
            }

            if ( true )
            {
                String text = new Range( chpx.getStart(), chpx.getEnd(),
                        _doc.getOverallRange() )
                {
                    public String toString()
                    {
                        return "CHPX range (" + super.toString() + ")";
                    }
                }.text();
                StringBuilder stringBuilder = new StringBuilder();
                for ( char c : text.toCharArray() )
                {
                    if ( c < 30 )
                        stringBuilder
                                .append( "\\0x" + Integer.toHexString( c ) );
                    else
                        stringBuilder.append( c );
                }
                System.out.println( stringBuilder );
            }
        }
    }

    private void dumpDop()
    {
        if ( !( _doc instanceof HWPFDocument ) )
        {
            System.out.println( "Word 95 not supported so far" );
            return;
        }

        System.out.println( ( (HWPFDocument) _doc ).getDocProperties() );
    }

    private void dumpEscher()
    {
        if ( _doc instanceof HWPFOldDocument )
        {
            System.out.println( "Word 95 not supported so far" );
            return;
        }

        System.out.println( ( (HWPFDocument) _doc ).getEscherRecordHolder() );
    }

    public void dumpFIB()
    {
        FileInformationBlock fib = _doc.getFileInformationBlock();
        System.out.println( fib );

    }

    private void dumpFields()
    {
        if ( !( _doc instanceof HWPFDocument ) )
        {
            System.out.println( "Word 95 not supported so far" );
            return;
        }

        HWPFDocument document = (HWPFDocument) _doc;

        for ( FieldsDocumentPart part : FieldsDocumentPart.values() )
        {
            System.out.println( "=== Document part: " + part + " ===" );
            for ( Field field : document.getFields().getFields( part ) )
            {
                System.out.println( field );
            }
        }
    }

    public void dumpFileSystem() throws Exception
    {
        java.lang.reflect.Field field = POIDocument.class
                .getDeclaredField( "directory" );
        field.setAccessible( true );
        DirectoryNode directoryNode = (DirectoryNode) field.get( _doc );

        System.out.println( dumpFileSystem( directoryNode ) );
    }

    private String dumpFileSystem( DirectoryEntry directory )
    {
        StringBuilder result = new StringBuilder();
        result.append( "+ " );
        result.append( directory.getName() );
        for ( Iterator<Entry> iterator = directory.getEntries(); iterator
                .hasNext(); )
        {
            Entry entry = iterator.next();
            String entryToString = "\n" + dumpFileSystem( entry );
            entryToString = entryToString.replaceAll( "\n", "\n+---" );
            result.append( entryToString );
        }
        result.append( "\n" );
        return result.toString();
    }

    private String dumpFileSystem( Entry entry )
    {
        if ( entry instanceof DirectoryEntry )
            return dumpFileSystem( (DirectoryEntry) entry );

        return entry.getName();
    }

    private void dumpOfficeDrawings()
    {
        if ( !( _doc instanceof HWPFDocument ) )
        {
            System.out.println( "Word 95 not supported so far" );
            return;
        }

        HWPFDocument document = (HWPFDocument) _doc;

        if ( document.getOfficeDrawingsHeaders() != null )
        {
            System.out.println( "=== Document part: HEADER ===" );
            for ( OfficeDrawing officeDrawing : document
                    .getOfficeDrawingsHeaders().getOfficeDrawings() )
            {
                System.out.println( officeDrawing );
            }
        }

        if ( document.getOfficeDrawingsHeaders() != null )
        {
            System.out.println( "=== Document part: MAIN ===" );
            for ( OfficeDrawing officeDrawing : document
                    .getOfficeDrawingsMain().getOfficeDrawings() )
            {
                System.out.println( officeDrawing );
            }
        }
    }

    public void dumpPapx( boolean withProperties, boolean withSprms )
            throws Exception
    {
        if ( _doc instanceof HWPFDocument )
        {
            System.out.println( "binary PAP pages " );

            HWPFDocument doc = (HWPFDocument) _doc;

            java.lang.reflect.Field fMainStream = HWPFDocumentCore.class
                    .getDeclaredField( "_mainStream" );
            fMainStream.setAccessible( true );
            byte[] mainStream = (byte[]) fMainStream.get( _doc );

            PlexOfCps binTable = new PlexOfCps( doc.getTableStream(), doc
                    .getFileInformationBlock().getFcPlcfbtePapx(), doc
                    .getFileInformationBlock().getLcbPlcfbtePapx(), 4 );

            List<PAPX> papxs = new ArrayList<PAPX>();

            int length = binTable.length();
            for ( int x = 0; x < length; x++ )
            {
                GenericPropertyNode node = binTable.getProperty( x );

                int pageNum = LittleEndian.getInt( node.getBytes() );
                int pageOffset = POIFSConstants.SMALLER_BIG_BLOCK_SIZE
                        * pageNum;

                PAPFormattedDiskPage pfkp = new PAPFormattedDiskPage(
                        mainStream, doc.getDataStream(), pageOffset,
                        doc.getTextTable() );

                System.out.println( "* PFKP: " + pfkp );

                for ( PAPX papx : pfkp.getPAPXs() )
                {
                    System.out.println( "** " + papx );
                    papxs.add( papx );
                    if ( papx != null && withSprms )
                    {
                        SprmIterator sprmIt = new SprmIterator(
                                papx.getGrpprl(), 2 );
                        dumpSprms( sprmIt, "*** " );
                    }

                }
            }

            Collections.sort( papxs );
            System.out.println( "* Sorted by END" );
            for ( PAPX papx : papxs )
            {
                System.out.println( "** " + papx );
                if ( papx != null && withSprms )
                {
                    SprmIterator sprmIt = new SprmIterator( papx.getGrpprl(), 2 );
                    dumpSprms( sprmIt, "*** " );
                }
            }
        }

        Method newParagraph = Paragraph.class.getDeclaredMethod(
                "newParagraph", Range.class, PAPX.class );
        newParagraph.setAccessible( true );
        java.lang.reflect.Field _props = Paragraph.class
                .getDeclaredField( "_props" );
        _props.setAccessible( true );

        for ( PAPX papx : _doc.getParagraphTable().getParagraphs() )
        {
            System.out.println( papx );

            if ( withProperties )
            {
                Paragraph paragraph = (Paragraph) newParagraph.invoke( null,
                        _doc.getOverallRange(), papx );
                System.out.println( _props.get( paragraph ) );
            }

            if ( true )
            {
                SprmIterator sprmIt = new SprmIterator( papx.getGrpprl(), 2 );
                dumpSprms( sprmIt, "\t" );
            }
        }
    }

    public void dumpParagraphs( boolean dumpAssotiatedPapx )
    {
        for ( Map.Entry<Integer, String> entry : paragraphs.entrySet() )
        {
            Integer endOfParagraphCharOffset = entry.getKey();
            System.out.println( "[...; " + ( endOfParagraphCharOffset + 1 )
                    + "): " + entry.getValue() );

            if ( dumpAssotiatedPapx )
            {
                boolean hasAssotiatedPapx = false;
                for ( PAPX papx : _doc.getParagraphTable().getParagraphs() )
                {
                    if ( papx.getStart() <= endOfParagraphCharOffset.intValue()
                            && endOfParagraphCharOffset.intValue() < papx
                                    .getEnd() )
                    {
                        hasAssotiatedPapx = true;
                        System.out.println( "* " + papx );

                        SprmIterator sprmIt = new SprmIterator(
                                papx.getGrpprl(), 2 );
                        dumpSprms( sprmIt, "** " );
                    }
                }
                if ( !hasAssotiatedPapx )
                {
                    System.out.println( "* "
                            + "NO PAPX ASSOTIATED WITH PARAGRAPH!" );
                }
            }
        }
    }

    protected void dumpSprms( SprmIterator sprmIt, String linePrefix )
    {
        while ( sprmIt.hasNext() )
        {
            SprmOperation sprm = sprmIt.next();
            System.out.println( linePrefix + sprm.toString() );
        }
    }

    public void dumpParagraphsDom( boolean withText )
    {
        Range range = _doc.getOverallRange();
        for ( int p = 0; p < range.numParagraphs(); p++ )
        {
            Paragraph paragraph = range.getParagraph( p );
            System.out.println( p + ":\t" + paragraph.toString() );

            if ( withText )
                System.out.println( paragraph.text() );
        }
    }

    private void dumpPictures()
    {
        if ( _doc instanceof HWPFOldDocument )
        {
            System.out.println( "Word 95 not supported so far" );
            return;
        }

        List<Picture> allPictures = ( (HWPFDocument) _doc ).getPicturesTable()
                .getAllPictures();
        for ( Picture picture : allPictures )
        {
            System.out.println( picture.toString() );
        }
    }

    private void dumpStyles()
    {
        if ( _doc instanceof HWPFOldDocument )
        {
            System.out.println( "Word 95 not supported so far" );
            return;
        }
        HWPFDocument hwpfDocument = (HWPFDocument) _doc;

        for ( int s = 0; s < hwpfDocument.getStyleSheet().numStyles(); s++ )
        {
            StyleDescription styleDescription = hwpfDocument.getStyleSheet()
                    .getStyleDescription( s );
            if ( styleDescription == null )
                continue;

            System.out.println( "=== Style #" + s + " '"
                    + styleDescription.getName() + "' ===" );
            System.out.println( styleDescription );

            if ( styleDescription.getPAPX() != null )
                dumpSprms( new SprmIterator( styleDescription.getPAPX(), 2 ),
                        "Style's PAP SPRM: " );

            if ( styleDescription.getCHPX() != null )
                dumpSprms( new SprmIterator( styleDescription.getCHPX(), 0 ),
                        "Style's CHP SPRM: " );
        }
    }

    protected void dumpParagraphLevels( ListTables listTables,
            ParagraphProperties paragraph )
    {
        if ( paragraph.getIlfo() != 0 )
        {
            final ListFormatOverride listFormatOverride = listTables
                    .getOverride( paragraph.getIlfo() );

            System.out.println( "PAP's LFO: " + listFormatOverride );

            final ListLevel listLevel = listTables.getLevel(
                    listFormatOverride.getLsid(), paragraph.getIlvl() );

            System.out.println( "PAP's ListLevel: " + listLevel );
            if ( listLevel.getGrpprlPapx() != null )
            {
                System.out.println( "PAP's ListLevel PAPX:" );
                dumpSprms( new SprmIterator( listLevel.getGrpprlPapx(), 0 ),
                        "* " );
            }

            if ( listLevel.getGrpprlPapx() != null )
            {
                System.out.println( "PAP's ListLevel CHPX:" );
                dumpSprms( new SprmIterator( listLevel.getGrpprlChpx(), 0 ),
                        "* " );
            }
        }
    }

    public void dumpTextPieces( boolean withText )
    {
        for ( TextPiece textPiece : _doc.getTextTable().getTextPieces() )
        {
            System.out.println( textPiece );

            if ( withText )
            {
                System.out.println( "\t" + textPiece.getStringBuilder() );
            }
        }
    }
}
