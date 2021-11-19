package com.naveen.notes.listeners;

import com.naveen.notes.model.NoteModel;

public interface INoteItemListener {
    void OnClickEditNoteItem(NoteModel noteModel, int pos);
    void OnClickDeleteNoteItem(NoteModel noteModel, int pos);
    void OnClickDetailNoteItem(NoteModel noteModel, int pos);
}
