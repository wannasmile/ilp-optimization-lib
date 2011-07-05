package com.dborisenko.math.optimization.problems.generators.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Denis
 */
public class CrossReferencesUtils {

    public static boolean verify(String input,
            NamedCollectionVO<String> ownerList,
            List<NamedCollectionVO<String>> notOwnerLists) {

        if (hasReferences(ownerList.name, ownerList)) {
            return false;
        }

        for (int i = 0; i < notOwnerLists.size(); i++) {
             boolean notOwnerHasReferences = hasReferences(ownerList.name, notOwnerLists.get(i));
             if (!notOwnerHasReferences) {
                 continue;
             }
             if (notOwnerHasReferences && hasReferences(notOwnerLists.get(i).name, ownerList)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasReferences(String value, NamedCollectionVO<String> collection) {
        Iterator<String> iterator = collection.collection.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            if (str.indexOf(value) != -1) {
                return true;
            }
        }
        return false;
    }

    public static List<NamedCollectionVO<String>> orderByIndependence(List<NamedCollectionVO<String>> list) {
        List<NamedCollectionVO<String>> result = new ArrayList<NamedCollectionVO<String>>(list);

        final Dictionary<String, OrderHelper> orderDic = new Hashtable<String, OrderHelper>();
        
        for (int i = 0; i < list.size(); i++) {
            NamedCollectionVO<String> src = list.get(i);
            int refCounter = 0;
            for (int j = 0; j < list.size(); j++) {
                if (i == j) continue;

                NamedCollectionVO<String> dest = list.get(j);
                if (hasReferences(dest.name, src)) {
                    refCounter++;
                }
            }
            orderDic.put(src.name, new OrderHelper(src, refCounter));
        }

        Collections.sort(result, new Comparator<NamedCollectionVO<String>>() {

            public int compare(NamedCollectionVO<String> o1, NamedCollectionVO<String> o2) {
                return orderDic.get(o1.name).refCounter - orderDic.get(o2.name).refCounter;
            }
        });

        return result;
    }

    private static class OrderHelper {
        protected final NamedCollectionVO<String> collection;
        protected final int refCounter;

        protected OrderHelper(NamedCollectionVO<String> collection, int refCounter) {
            this.collection = collection;
            this.refCounter = refCounter;
        }
    }
}
