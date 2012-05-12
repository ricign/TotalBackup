package mobi.durian.tbackup;

import android.provider.Browser;
import android.provider.Contacts;

public class TBackupColumsFactory {
    public static Columns contacts() {
        String[] names = { Contacts.Phones.DISPLAY_NAME, Contacts.Phones.NUMBER };
        Class<?> types[] = { String.class, String.class };
        return new Columns(names, types, Contacts.Phones.DISPLAY_NAME, Contacts.Phones.NUMBER);
    }
    
    public static Columns bookmarks() {
        String[] names = { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL };
        Class<?> types[] = { String.class, String.class };
        return new Columns(names, types, Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL);
    }
}
