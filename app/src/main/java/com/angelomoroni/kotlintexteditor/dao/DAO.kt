package com.angelomoroni.kotlintexteditor.dao

import android.content.Context
import android.os.Environment
import android.util.Log
import com.angelomoroni.kotlintexteditor.models.Note
import com.google.gson.Gson
import rx.Observable
import java.io.DataOutput
import java.io.File
import java.util.*

/**
 * Created by angelomoroni on 10/05/16.
 */

val NOTE_STORAGE :String = "KTextEditorDir"

fun getListNote() : Observable<Note?>{
    return Observable.defer { Observable.from(getListoFromExternalStorage())}
}

fun getListoFromExternalStorage(): Array<Note?>? {

    var listNote :Array<Note?>? = null

    var folder : File = File(Environment.getExternalStorageDirectory().getAbsoluteFile(), NOTE_STORAGE)
    if(folder.mkdirs()||folder.isDirectory()){
        var listFile = folder.listFiles()

        listNote = Array<Note?>(listFile?.size ?: 0,
                { i -> if(listFile[i].isFile)getNote(listFile[i].readText()) else null})
    }
    return listNote;
}

val TAG: String? = "DAO"

fun getNote(readText: String): Note {
    Log.d(TAG,readText)
    return Gson().fromJson(readText,Note::class.java)
}


fun saveNote(n:Note): Observable<Note> {
    return Observable.defer{ Observable.just(saveNoteOnExternalStorage(n))}
}

fun removeNoteDAO(n: Note) : Observable<Boolean>{
    return Observable.defer {
        Observable.just(removeNoteFromExternaStorage(n))
    }
}

fun removeNoteFromExternaStorage(n: Note): Boolean {
    var folder : File = File(Environment.getExternalStorageDirectory().getAbsoluteFile(), NOTE_STORAGE)
    folder.mkdirs()
    var an = folder.absolutePath + File.separator + n.id;
    var file : File = File(an)
    return file.delete()
}

fun saveNotes(l: ArrayList<Note?>) : Observable<Boolean>{
    return Observable.defer {
        for ( n in l){
            saveNoteOnExternalStorage(n as Note);
        };

        Observable.just(true)
    }
}

fun saveNoteOnExternalStorage(n: Note): Note {
    var folder : File = File(Environment.getExternalStorageDirectory().getAbsoluteFile(), NOTE_STORAGE)
    folder.mkdirs()
    var an = folder.absolutePath + File.separator + n.id;
    var file : File = File(an)
    if(!file.exists()){
        file.createNewFile()
    }
    file.writeText(Gson().toJson(n))

    return n
}
