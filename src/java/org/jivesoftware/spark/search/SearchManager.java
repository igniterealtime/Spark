/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package org.jivesoftware.spark.search;

import org.jivesoftware.spark.util.SwingWorker;
import org.jivesoftware.sparkimpl.search.users.UserSearchService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Users of the SearchManager can add their own <code>Searchable</code> objects to the Spark
 * search service. This allows for a pluggable search architecture by simply plugging into the
 * find area of the bottom of Spark.
 */
public class SearchManager {
    private List<Searchable> searchServices = new ArrayList<>();
    private SearchService ui;
    private Collection<String> _searchservicesHosts;

    private static SearchManager singleton;
    private static final Object LOCK = new Object();

    /**
     * Returns the singleton instance of <CODE>SearchManager</CODE>,
     * creating it if necessary.
     * <p/>
     *
     * @return the singleton instance of <Code>SearchManager</CODE>
     */
    public static SearchManager getInstance() {
        // Synchronize on LOCK to ensure that we don't end up creating
        // two singletons.
        synchronized (LOCK) {
            if (null == singleton) {
                SearchManager controller = new SearchManager();
                singleton = controller;
                return controller;
            }
        }
        return singleton;
    }

    private SearchManager() {
        ui = new SearchService();

        // By default, the user search is first.
        SwingWorker worker = new SwingWorker() {
            UserSearchService searchWizard;
            public Object construct() {
                searchWizard = new UserSearchService();
                return searchWizard;
            }

            public void finished() {
                if (searchWizard.getSearchServices() != null) {
                    ui.setActiveSearchService(searchWizard);
                    addSearchService(searchWizard);
                   _searchservicesHosts =  searchWizard.getSearchServices();
                }
            }
        };

        worker.start();


    }

    /**
     * Add your own <code>Searchable</code> service.The UI will take
     * immediate effect to indicate that this search service is now available as
     * an option.
     *
     * @param searchable the search service.
     */
    public void addSearchService(Searchable searchable) {
        searchServices.add(searchable);
        checkSearchService();
    }

    /**
     * Remove the <code>Searchable</code> service. The UI will take
     * immediate effect to indicate that this search service is no longer
     * an option.
     *
     * @param searchable the searchable object to remove.
     */
    public void removeSearchService(Searchable searchable) {
        searchServices.remove(searchable);
        checkSearchService();
    }

    /**
     * Returns all registered search services.
     *
     * @return the collection of search services.
     */
    public Collection<Searchable> getSearchServices() {
        return searchServices;
    }
    /**
     * Returns all registered search services.
     *
     * @return the collection of search services.
     */
    public Collection<String> getSearchServicesAsString()
    {
	return _searchservicesHosts;
    }

    private void checkSearchService() {
        Collection<Searchable> searchables = getSearchServices();
        if (searchables.size() <= 1) {
            ui.getFindField().enableDropdown(false);
        }
        else {
            ui.getFindField().enableDropdown(true);
        }
    }

    public SearchService getSearchServiceUI() {
        return ui;
    }


}
