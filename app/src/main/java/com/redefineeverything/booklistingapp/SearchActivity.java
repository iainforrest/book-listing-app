package com.redefineeverything.booklistingapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.setProperty("http.keepAlive", "false");

        listView = (ListView) findViewById(R.id.list_view);


        setContentView(R.layout.activity_search);

        EditText searchQuearyView = (EditText) findViewById(R.id.search_query);
        //change the label of the return key on some keyboards
        searchQuearyView.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        //set a listener for when the user hits the return or search key on their keypad.
        searchQuearyView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                runsearchQuery();
                return true;
            }
        });




        // TODO hide topic text when list loaded (fade first then move)
        //TODO figure out how to stream images
        //TODO remove hard coded book image from BookAdapter
        //TODO add streamed images to the list

        // TODO set intent to open details screen



    }

    public void searchButtonClicked(View v){
        runsearchQuery();
    }

    /**
     * Moved this into it's own function so that I could call it from either the user hitting return
     * on the keyboard or from tapping the search icon.
     */
    public void runsearchQuery(){
        EditText searchQueryEditText = (EditText) findViewById(R.id.search_query);

        String searchQuery = searchQueryEditText.getText().toString();

        if (searchQuery.equals("")){
            Toast.makeText(SearchActivity.this, "Please Enter A Topic To Search For", Toast.LENGTH_SHORT).show();
            return;
        }

        new BookSearchAsyncTask(this).execute(searchQuery);


        TextView questionView = (TextView) findViewById(R.id.question);
        if (questionView.getVisibility() == View.VISIBLE) {
            swicthToListView(questionView);
        }

        hideKeyboard();
    }


    /**
     * Switch to list view is performed at the first search. It removes the Question,
     * moves the search bar to the top of the screen and makes the Listview visible
     * @param questionView
     */
    public void swicthToListView (final TextView questionView){
        final LinearLayout topPanelLayout = (LinearLayout) findViewById(R.id.topPanel);
        //create an animations
        questionView.animate()
                //fade to invisible
                .alpha(0.0f)
                //set duration
                .setDuration(100)
                //create a listener that activates when the animation is finished
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //once invisible, set to GONE to remove padding
                        questionView.setVisibility(View.GONE);
                        //change top panel parameters so that it wraps content and
                        //makes space for the listview.
                        topPanelLayout.setLayoutParams(new LinearLayout.LayoutParams
                                (LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT));

                    }
                });

        //make the listview visible
        listView = (ListView) findViewById(R.id.list_view);
        listView.setVisibility(View.VISIBLE);


    }

    /**
     * Because we have set some custom featureson the keyboard we have to manually hid it from
     * view when a search is activitated. The easiest way to do this was to swicth the focus to
     * the listview and then also hide the keyboard.
     * COde found on stackoverflow
     */
    public void hideKeyboard(){
        listView.requestFocus();
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(listView.getWindowToken(), 0);
    }



}
