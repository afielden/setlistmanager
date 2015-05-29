package com.bigdrum.metronomemate.backup;

import android.util.Log;

import com.bigdrum.metronomemate.database.DataService;
import com.bigdrum.metronomemate.gig.Gig;
import com.bigdrum.metronomemate.ui.setlistmanagement.Model;
import com.bigdrum.metronomemate.venue.Venue;

import static com.bigdrum.metronomemate.database.Constants.*;

import java.io.IOException;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;


/**
 * Created by andrew on 29/05/15.
 *
 * A class to provide the facility to backup and restore the application database
 *
 */
public class DatabaseBackup {

    Document document;
    DataService dbService;
    String appVersion;


    /**
     *
     * @param dbService
     */
    public DatabaseBackup(DataService dbService, String appVersion) {
        this.dbService = dbService;
        this.appVersion = appVersion;
    }


    /**
     *
     *
     */
   public void backupDatabase() {

       Element root = new Element("root");

       dumpAppVersion(root);
       dumpSetlists(root);
       dumpSongTable(root);
       dumpSongSetTable(root);
       dumpVenuTable(root);
       dumpGigTable(root);

       document = new Document(root);

       serializeDocument();
   }


    private void serializeDocument() {
        try {
            Serializer serializer = new Serializer(System.out, "ISO-8859-1");
            serializer.setIndent(4);
            serializer.setMaxLength(64);
            serializer.write(document);
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
    }


    /**
     *
     * @param root
     */
    private void dumpAppVersion(Element root) {

        Element appVersionInfo = new Element("App_Version");
        Element version = new Element("version");
        version.appendChild(appVersion);

        appVersionInfo.appendChild(version);
        root.appendChild(appVersionInfo);
    }


    /**
     *
     */
    private void dumpSetlists(Element root) {

        List<Model> setlists = dbService.getAllSetlists();

        Element setlistGroup = new Element("Setlist_Table");
        Element element = null;

        for (Model setlist : setlists) {
            Element setlistEl = new Element("setlist");

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

        Element songGroup = new Element("Song_Table");
        Element element = null;

        for (Model song : songs) {
            Element songEl = new Element("song");

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

        Element songSetGroup = new Element("Song_Set_Table");
        Element element = null;

        for (Model songSet : songSets) {
            Element parent = new Element("song_set");

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

        Element venueGroup = new Element("Venue_Table");
        Element element = null;

        for (Venue venue : venues) {

            Element parent = new Element("venue");

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

        Element gigGroup = new Element("Gig_Table");
        Element element = null;

        for (Gig gig : gigs) {

            Element parent = new Element("gig");

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
}
