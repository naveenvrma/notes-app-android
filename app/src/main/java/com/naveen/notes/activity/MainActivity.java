package com.naveen.notes.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.naveen.notes.R;
import com.naveen.notes.adapter.NoteListAdapter;
import com.naveen.notes.db.DBController;
import com.naveen.notes.listeners.INoteItemListener;
import com.naveen.notes.model.NoteModel;
import com.naveen.notes.ui.SearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends BaseActivity implements INoteItemListener {

    private TextView title;
    private ImageView searchBtn, addBtn;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private NoteListAdapter noteListAdapter;

    private String mCategory = "";
    private Spinner categorySpinner;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private ArrayList<String> strCategoryList = new ArrayList<String>(Arrays.asList("Choose Category", "Sports", "Music", "Education"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setSpinnerArrayAdapter();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRecycler();
    }

    private void initListener() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra("note_edit", false);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setVisibility(View.VISIBLE);
                showSearchView();
            }
        });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCategory = position == 0  ? "" : parent.getItemAtPosition(position).toString();
                try {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                if (position == 0){
                    initRecycler();
                }else {
                    initRecyclerWithCategory(mCategory);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSpinnerArrayAdapter() {
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strCategoryList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerArrayAdapter);
        categorySpinner.setSelection(0);
    }

    private void initView() {
        title = findViewById(R.id.main_title);
        searchBtn = findViewById(R.id.main_search_btn);
        addBtn = findViewById(R.id.main_add_btn);
        searchView = findViewById(R.id.main_search_view);
        recyclerView = findViewById(R.id.main_recycler_view);
        categorySpinner = findViewById(R.id.category_spinner);
    }

    private void showSearchView() {
        searchView.with(this)
                .hideSearch(new SearchView.OnHideSearchListener() {
                    @Override
                    public void hideSearch() {
                        searchView.setVisibility(View.INVISIBLE);
                    }
                })
                .removeMinToSearch()
                .removeSearchDelay()
                .build();
        searchView.show();
        searchView.setOnSearchListener(new SearchView.OnSearchListener() {
            @Override
            public void changedSearch(CharSequence text) {
                if (noteListAdapter != null){
                    noteListAdapter.searchItems(text);
                }
            }
        });
    }

    private void initRecycler() {
        ArrayList<NoteModel> _list = new ArrayList<>();
        ArrayList<NoteModel> _temp = DBController.shared().getAllNote();
        Collections.reverse(_temp);
        _list.addAll(_temp);
        noteListAdapter = new NoteListAdapter(this, _list, this);
        recyclerView.setAdapter(noteListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }
    private void initRecyclerWithCategory(String category) {
        ArrayList<NoteModel> _list = new ArrayList<>();
        ArrayList<NoteModel> _temp = DBController.shared().getAllNoteWithCategory(category);
        Collections.reverse(_temp);
        _list.addAll(_temp);
        noteListAdapter = new NoteListAdapter(this, _list, this);
        recyclerView.setAdapter(noteListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void OnClickEditNoteItem(NoteModel noteModel, int pos) {
        Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
        intent.putExtra("note_model", noteModel.getTime());
        intent.putExtra("note_edit", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void OnClickDeleteNoteItem(NoteModel noteModel, int pos) {
        DBController.shared().deleteNote(noteModel);
        if (noteListAdapter != null){
            noteListAdapter.deleteNoteItem(noteModel, pos);
        }
    }

    @Override
    public void OnClickDetailNoteItem(NoteModel noteModel, int pos) {
        Intent intent = new Intent(MainActivity.this, NoteDetailActivity.class);
        intent.putExtra("note_model", noteModel.getTime());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
