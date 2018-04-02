package com.bitcast.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;

import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.List;

/**
 * Created by KangWei on 2018/3/23.
 * 2018/3/23 19:13
 * Coin
 * com.baidu.coin
 */

public class SearchActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener, PopupMenu.OnMenuItemClickListener {
    private MaterialSearchBar searchBar;
    private List lastSearches;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search);
        searchBar = findViewById(R.id.searchBar);
        searchBar.setHint("Custom hint");
        searchBar.setSpeechMode(true);
        //enable searchbar callbacks
        searchBar.setOnSearchActionListener(this);
        //restore last queries from disk
        lastSearches = loadSearchSuggestionFromDisk();
        searchBar.setLastSuggestions(lastSearches);
        //Inflate menu and setup OnMenuItemClickListener
        //searchBar.getMenu().setOnMenuItemClickListener(this);
    }

    private List loadSearchSuggestionFromDisk() {
        return searchBar.getLastSuggestions();
    }

    /**
     * Invoked when SearchBar opened or closed
     *
     * @param enabled state
     */
    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    /**
     * Invoked when search confirmed and "search" button is clicked on the soft keyboard
     *
     * @param text search input
     */
    @Override
    public void onSearchConfirmed(CharSequence text) {
        startSearch(text.toString(), true, null, true);
    }

    /**
     * Invoked when "speech" or "navigation" buttons clicked.
     *
     * @param buttonCode {@link #BUTTON_NAVIGATION}, {@link #BUTTON_SPEECH} or {@link #BUTTON_BACK} will be passed
     */
    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
                break;
            case MaterialSearchBar.BUTTON_BACK:
                break;
        }
    }

    /**
     * This method will be invoked when a menu item is clicked if the item
     * itself did not already handle the event.
     *
     * @param item the menu item that was clicked
     * @return {@code true} if the event was handled, {@code false}
     * otherwise
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //save last queries to disk
        saveSearchSuggestionToDisk(searchBar.getLastSuggestions());
    }

    private void saveSearchSuggestionToDisk(List lastSuggestions) {
        searchBar.setLastSuggestions(lastSuggestions);
    }

}
