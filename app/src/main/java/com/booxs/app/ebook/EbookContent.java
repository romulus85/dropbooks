package com.booxs.app.ebook;

import com.booxs.app.EbookFragment;

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
     * An array of ebook items.
     */
    public List<EbookItem> ITEMS = new ArrayList<EbookItem>();

    /**
     * A map of ebook items, by ID.
     */
    public Map<String, EbookItem> ITEM_MAP = new HashMap<String, EbookItem>();
    private EbookFragment.orderByEnum order;

    public void addItem(EbookItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static Comparator<EbookItem> orderDateComparator = new Comparator<EbookItem>() {
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

    private static Comparator<EbookItem> orderNameComparator = new Comparator<EbookContent.EbookItem>() {
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

    public void setOrder(EbookFragment.orderByEnum order) {
        if (order != this.order) {
            if (order == EbookFragment.orderByEnum.ORDER_BY_DATE)
                Collections.sort(ITEMS, orderDateComparator);
            else
                Collections.sort(ITEMS, orderNameComparator);
        }
        this.order = order;

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
