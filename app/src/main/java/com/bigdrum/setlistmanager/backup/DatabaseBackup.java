package com.bigdrum.setlistmanager.backup;

import android.content.Context;
import android.widget.Toast;

import com.bigdrum.setlistmanager.R;
import com.bigdrum.setlistmanager.database.DataService;
import com.bigdrum.setlistmanager.gig.Gig;
import com.bigdrum.setlistmanager.ui.setlistmanagement.Model;
import com.bigdrum.setlistmanager.venue.Venue;

import static com.bigdrum.setlistmanager.database.Constants.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.Serializer;


/**
 * Created by andrew on 29/05/15.
 *
 * A class to provide the facility to backup and restore the application database
 *
 */
public class DatabaseBackup {

    public static final String SETLIST_TABLE = "Setlist_Table";
    public static final String APP_VERSION = "App_Version";
    public static final String VERSION = "version";
    public static final String SETLIST = "setlist";
    public static final String SONG_TABLE = "Song_Table";
    public static final String SONG = "song";
    public static final String SONG_SET_TABLE = "Song_Set_Table";
    public static final String SONGSET = "songset";
    public static final String VENUE_TABLE = "Venue_Table";
    public static final String VENUE = "venue";
    public static final String GIG_TABLE = "Gig_Table";
    public static final String GIG = "gig";
    private Document document;
    private DataService dbService;
    private String appVersion;
    private Context context;


    /**
     *
     * @param dbService
     */
    public DatabaseBackup(DataService dbService, String appVersion, Context context) {
        this.dbService = dbService;
        this.appVersion = appVersion;
        this.context = context;
    }


    /**
     *
     *
     */
   public boolean backupDatabase() {

       Element root = new Element("root");

       dumpAppVersion(root);
       dumpSetlists(root);
       dumpSongTable(root);
       dumpSongSetTable(root);
       dumpVenuTable(root);
       dumpGigTable(root);

       document = new Document(root);

       saveDocument(getOutputStream());

       Toast.makeText(context, R.string.backup_success, Toast.LENGTH_LONG).show();

       return true;
   }


    /**
     *
     * @return
     */
    public boolean restoreDatabase() {

        FileInputStream inputStream = getInputStream();

        Builder parser = new Builder();
        try {
            document = parser.build(inputStream);
            saveDocument(System.out);
        }
        catch (ParsingException ex) {
            Toast.makeText(context, R.string.restore_xml_read_fail, Toast.LENGTH_LONG).show();
        }
        catch (IOException ex) {
            Toast.makeText(context, R.string.restore_xml_read_fail, Toast.LENGTH_LONG).show();
        }

        Toast.makeText(context, R.string.restore_success, Toast.LENGTH_LONG).show();

        return true;
    }


    /**
     *
     * @return
     */
    private FileInputStream getInputStream() {

        try {
            return context.openFileInput(DB_BACKUP_FILE);
        }
        catch (FileNotFoundException e) {
            Toast.makeText(context, R.string.error_restore_no_file, Toast.LENGTH_LONG).show();
        }

        return null;
    }


    /**
     *
     */
    private void saveDocument(OutputStream outputStream) {
        try {
//            FileOutputStream outputStream = getOutputStream();

            if (outputStream != null) {
                Serializer serializer = new Serializer(outputStream, "ISO-8859-1");
                serializer.setIndent(4);
                serializer.setMaxLength(64);
                serializer.write(document);
            }
        }
        catch (IOException ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }


    /**
     *
     */
    private FileOutputStream getOutputStream() {

        FileOutputStream outputStream = null;

        try {
            outputStream = context.openFileOutput(DB_BACKUP_FILE, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream;
    }


    /**
     *
     * @param root
     */
    private void dumpAppVersion(Element root) {

        Element appVersionInfo = new Element(APP_VERSION);
        Element version = new Element(VERSION);
        version.appendChild(appVersion);

        appVersionInfo.appendChild(version);
        root.appendChild(appVersionInfo);
    }


    /**
     *
     */
    private void dumpSetlists(Element root) {

        List<Model> setlists = dbService.getAllSetlists();

        Element setlistGroup = new Element(SETLIST_TABLE);
        Element element = null;

        for (Model setlist : setlists) {
            Element setlistEl = new Element(SETLIST);

            element = new Element(SETLISTPRIMARYKEY);
            element.appendChild(String.valueOf(setlist.getId()));
            setlistEl.appendChild(element);

            element = new Element(SETLISTNAME);
            element.appendChild(setlist.getName());
            setlistEl.appendChild(element);

            element = new Element(SETLISTPOS);
            element.appendChild(String.valueOf(setlist.getPosition()));
            setlistEl.appendChild(element);

            setlistGroup.appendChild(setlistEl);
        }

        root.appendChild(setlistGroup);
    }


    /**
     *
     * @param root
     */
    private void dumpSongTable(Element root) {

        List<Model> songs = dbService.getAllSongs();

        Element songGroup = new Element(SONG_TABLE);
        Element element = null;

        for (Model song : songs) {
            Element songEl = new Element(SONG);

            element = new Element(SONG_PRIMARYKEY);
            element.appendChild(String.valueOf(song.getId()));
            songEl.appendChild(element);

            element = new Element(SONG_NAME);
            element.appendChild(song.getName());
            songEl.appendChild(element);

            element = new Element(SONG_ARTIST);
            element.appendChild(song.getArtist());
            songEl.appendChild(element);

            element = new Element(SONG_TEMPO);
            element.appendChild(String.valueOf(song.getTempo()));
            songEl.appendChild(element);

            element = new Element(SONG_TIMESIG);
            element.appendChild(String.valueOf(song.getTimeSignature()));
            songEl.appendChild(element);

            element = new Element(SONG_KEY);
            element.appendChild(String.valueOf(song.getKey()));
            songEl.appendChild(element);

            element = new Element(SONG_SETLIST_COUNT);
            element.appendChild(String.valueOf(song.getSetlistCount()));
            songEl.appendChild(element);

            element = new Element(SONG_DURATION);
            element.appendChild(String.valueOf(song.getDuration()));
            songEl.appendChild(element);

            songGroup.appendChild(songEl);
        }

        root.appendChild(songGroup);
    }


    /**
     *
     * @param root
     */
    private void dumpSongSetTable(Element root) {

        List<Model> songSets = dbService.getAllSongSetRecords();

        Element songSetGroup = new Element(SONG_SET_TABLE);
        Element element = null;

        for (Model songSet : songSets) {
            Element parent = new Element(SONGSET);

            element = new Element(SONG_SET_PRIMARY_KEY);
            element.appendChild(String.valueOf(songSet.getId()));
            parent.appendChild(element);

            element = new Element(SONG_SET_SETLIST_ID);
            element.appendChild(String.valueOf(songSet.getSetlistId()));
            parent.appendChild(element);

            element = new Element(SONG_SET_SONG_ID);
            element.appendChild(String.valueOf(songSet.getSongId()));
            parent.appendChild(element);

            element = new Element(SONG_SET_SONG_POS);
            element.appendChild(String.valueOf(songSet.getPosition()));
            parent.appendChild(element);

            songSetGroup.appendChild(parent);
        }

        root.appendChild(songSetGroup);
    }



    /**
     *
     */
    private void dumpVenuTable(Element root) {

        List<Venue> venues = dbService.readAllVenues();

        Element venueGroup = new Element(VENUE_TABLE);
        Element element = null;

        for (Venue venue : venues) {

            Element parent = new Element(VENUE);

            element = new Element(VENUE_PRIMARYKEY);
            element.appendChild(String.valueOf(venue.getId()));
            parent.appendChild(element);

            element = new Element(VENUE_NAME);
            element.appendChild(venue.getName());
            parent.appendChild(element);

            element = new Element(VENUE_CONTACT_NAME);
            element.appendChild(venue.getContactName());
            parent.appendChild(element);

            element = new Element(VENUE_PHONE);
            element.appendChild(venue.getPhone());
            parent.appendChild(element);

            element = new Element(VENUE_STREET);
            element.appendChild(venue.getStreet());
            parent.appendChild(element);

            element = new Element(VENUE_TOWN);
            element.appendChild(venue.getTown());
            parent.appendChild(element);

            element = new Element(VENUE_POSTCODE);
            element.appendChild(venue.getPostcode());
            parent.appendChild(element);

            element = new Element(VENUE_COUNTRY);
            element.appendChild(venue.getCountry());
            parent.appendChild(element);

            element = new Element(VENUE_EMAIL);
            element.appendChild(venue.getEmail());
            parent.appendChild(element);

            element = new Element(VENUE_LAST_GIG_DATE);
            element.appendChild(String.valueOf(venue.getLastGigDate()));
            parent.appendChild(element);

            venueGroup.appendChild(parent);
        }

        root.appendChild(venueGroup);
    }


    /**
     *
     * @param root
     */
    private void dumpGigTable(Element root) {

        List<Gig> gigs = dbService.findAllGigsOrderById();

        Element gigGroup = new Element(GIG_TABLE);
        Element element = null;

        for (Gig gig : gigs) {

            Element parent = new Element(GIG);

            element = new Element(GIG_PRIMARYKEY);
            element.appendChild(String.valueOf(gig.getId()));
            parent.appendChild(element);

            element = new Element(GIG_NAME);
            element.appendChild(gig.getName());
            parent.appendChild(element);

            element = new Element(GIG_VENUE_ID);
            element.appendChild(String.valueOf(gig.getVenueId()));
            parent.appendChild(element);

            element = new Element(GIG_SETLIST_ID);
            element.appendChild(String.valueOf(gig.getSetlistId()));
            parent.appendChild(element);

            element = new Element(GIG_DATE_TIME);
            element.appendChild(gig.getDateTime());
            parent.appendChild(element);

//            element = new Element(GIG_NOTES);
//            element.appendChild(gig.getNotes());
//            parent.appendChild(element);

            gigGroup.appendChild(parent);
        }

        root.appendChild(gigGroup);
    }


    /*********************************************************************
     *
     * Restore database methods
     *
     *********************************************************************/

    /**
     *
     */
    private void restoreSetlistTable() {

        Element setlistTable = document.getRootElement().getFirstChildElement(SETLIST_TABLE);

        Elements setlists = setlistTable.getChildElements(SETLIST);

        for (int index = 0; index < setlists.size(); index++) {

            Element setlist = setlists.get(index);
            String setlistName = setlist.getFirstChildElement(SETLISTNAME).getValue();
            Integer position = Integer.valueOf(setlist.getFirstChildElement(SETLISTPOS).getValue());

            dbService.addSetlistAtPosition(setlistName, position);
        }
    }


    /**
     *
     */
    private void restoreSongTable() {

        Element songTable = document.getRootElement().getFirstChildElement(SONG_TABLE);

        Elements songs = songTable.getChildElements(SONG);
        Model songDb = null;

        for (int index = 0; index < songs.size(); index++) {

            Element song = songs.get(index);
            songDb = new Model(-1, song.getFirstChildElement(SONG_NAME).getValue(), song.getFirstChildElement(SONG_ARTIST).getValue(),
                    Double.valueOf(song.getFirstChildElement(SONG_TEMPO).getValue()), Integer.valueOf(song.getFirstChildElement(SONG_TIMESIG).getValue()),
                    Integer.valueOf(song.getFirstChildElement(SONG_KEY).getValue()), Integer.valueOf(song.getFirstChildElement(SONG_SETLIST_COUNT).getValue()),
                    Double.valueOf(song.getFirstChildElement(SONG_DURATION).getValue()));

            dbService.addSong(songDb);
        }
    }


    /**
     *
     */
    private void restoreSongSetTable() {

    }
}
