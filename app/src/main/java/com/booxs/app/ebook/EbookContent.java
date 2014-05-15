package com.booxs.app.ebook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample title for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class EbookContent {

    /**
     * An array of sample items.
     */
    public List<EbookItem> ITEMS = new ArrayList<EbookItem>();

    /**
     * A map of sample items, by ID.
     */
    public Map<String, EbookItem> ITEM_MAP = new HashMap<String, EbookItem>();

//    static {
//        // Add 4 sample items.
//        addItem(new EbookItem("1", "Item 1"));
//        addItem(new EbookItem("2", "Item 2"));
//        addItem(new EbookItem("3", "Item 3"));
//        addItem(new EbookItem("4", "Item 4"));
//    }

    public void addItem(EbookItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static Comparator<EbookItem> orderDateComparator = new Comparator<EbookItem>() {
        @Override
        public int compare(EbookContent.EbookItem ebookItem, EbookContent.EbookItem ebookItem2) {
            if (ebookItem.creation_time > ebookItem2.creation_time)
                return -1;
            else if (ebookItem.creation_time < ebookItem2.creation_time)
                return 1;
            else
                return 0;
        }
    };

    public static Comparator<EbookItem> orderNameComparator = new Comparator<EbookContent.EbookItem>() {
        @Override
        public int compare(EbookContent.EbookItem ebookItem, EbookContent.EbookItem ebookItem2) {
            return ebookItem.file_name.compareToIgnoreCase(ebookItem2.file_name);
        }
    };

    public List<EbookItem> getAllItems() {
        return Collections.unmodifiableList(ITEMS);
    }

    public EbookItem get(int position) {
        return ITEMS.get(position);
    }

    /**
     * A ebook item to store everything related to a book
     */
    public static class EbookItem {
        public String id;
        public String title;
        public String file_name;
        public long creation_time;

        public EbookItem(String id, String file_name) {
            this.id = id;
            this.file_name = file_name;
        }

        @Override
        public String toString() {
            return file_name;
        }
    }
}
